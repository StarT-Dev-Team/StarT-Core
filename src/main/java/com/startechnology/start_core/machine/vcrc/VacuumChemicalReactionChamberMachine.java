package com.startechnology.start_core.machine.vcrc;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.startechnology.start_core.machine.redstone.IStarTRedstoneIndicatorMachine;
import com.startechnology.start_core.machine.redstone.StarTRedstoneIndicatorMap;
import com.startechnology.start_core.machine.redstone.StarTRedstoneIndicatorRecord;
import com.startechnology.start_core.machine.vacuum_pump.IVacuumPump;
import com.startechnology.start_core.machine.vacuum_pump.VacuumPumpPartMachine;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VacuumChemicalReactionChamberMachine extends WorkableElectricMultiblockMachine implements IStarTRedstoneIndicatorMachine {

    @Persisted
    @Getter
    protected float vacuumAmount;
    @Persisted
    @Getter
    protected Status vacuumStatus;

    @Getter
    private IVacuumPump pump = new IVacuumPump.Empty();

    private @Nullable TickableSubscription vacuumSubscription;

    public VacuumChemicalReactionChamberMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        this.vacuumAmount = 0;
        this.vacuumStatus = Status.IDLE;
    }

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        super.addDisplayText(textList);

        textList.add(Component.empty());
        textList.add(Component.translatable("ui.start_core.vcrc.info"));
        textList.add(Component.translatable("ui.start_core.vcrc.vacuum_status", formatVacuumStatus(vacuumStatus)).withStyle(ChatFormatting.GRAY));
        textList.add(Component.translatable("ui.start_core.vcrc.vacuum_amount", formatVacuumAmount(vacuumAmount)).withStyle(ChatFormatting.GRAY));
        if (isFormed) {
            textList.add(Component.translatable("ui.start_core.vcrc.pump_type.cap", VacuumPumpPartMachine.formatVacuumPumpRate(pump.getPumpCap()))
                    .withStyle(ChatFormatting.GRAY));
            textList.add(Component.translatable("ui.start_core.vcrc.pump_type.rate", VacuumPumpPartMachine.formatVacuumPumpRate(pump.getPumpRate()))
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    public static @NotNull ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof VacuumChemicalReactionChamberMachine vcrc)) {
            return RecipeModifier.nullWrongType(VacuumChemicalReactionChamberMachine.class, machine);
        }

        var requiredVacuum = recipe.data.getInt("vacuum_level");
        if (requiredVacuum == 0) return ModifierFunction.IDENTITY;

        if (vcrc.pump.getPumpCap() < requiredVacuum) return ModifierFunction.NULL;

        var diff = requiredVacuum - vcrc.vacuumAmount;
        if (diff <= 0) return ModifierFunction.IDENTITY;

        var timeToVacuum = diff / (vcrc.pump.getPumpRate() * 0.05f);
        return ModifierFunction.builder().durationModifier(ContentModifier.addition(timeToVacuum)).build();
    }

    @Override
    public boolean onWorking() {
        if (!super.onWorking()) {
            return false;
        }

        if (vacuumAmount < pump.getPumpCap()) {
            vacuumStatus = Status.PUMPING_DOWN;
            setVacuumAmount(vacuumAmount + pump.getPumpRate() * 0.05f);
        } else {
            vacuumStatus = vacuumAmount >= 100.f - Mth.EPSILON ? Status.FULL_VACUUM : Status.PARTIAL_VACUUM;
        }

        return true;
    }

    @Override
    public void afterWorking() {
        super.afterWorking();

        if (!isRemote()) {
            vacuumStatus = Status.PRESSURE_LOSS;
            if (recipeLogic.getLastRecipe() != null) {
                setVacuumAmount(vacuumAmount * 0.5f);
            }
            // check next tick
            subscribeServerTick(vacuumSubscription, this::updateVacuum);
        }
    }

    @Override
    public void onWaiting() {
        super.onWaiting();
        if (!isRemote()) {
            // check next tick
            subscribeServerTick(vacuumSubscription, this::updateVacuum);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!isRemote()) {
            updateVacuumSubscription();
        }
    }

    private boolean shouldUpdateVacuum() {
        return (getRecipeLogic().isIdle() || !isWorkingEnabled() || (getRecipeLogic().isWaiting() && getRecipeLogic().getProgress() == 0)) && vacuumAmount > 0.0f;
    }

    private void updateVacuumSubscription() {
        if (!shouldUpdateVacuum()) {
            if (vacuumSubscription != null) {
                vacuumSubscription.unsubscribe();
                vacuumSubscription = null;
            }
        } else {
            vacuumSubscription = subscribeServerTick(vacuumSubscription, this::updateVacuum);
        }
    }

    private void updateVacuum() {
        if (shouldUpdateVacuum()) {
            vacuumStatus = Status.PRESSURE_LOSS;
            setVacuumAmount(vacuumAmount - (1 * 0.05f)); // 1 per second
        } else if (vacuumAmount <= Mth.EPSILON) {
            vacuumStatus = Status.IDLE;
        }
        updateVacuumSubscription();
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        this.pump = getParts().stream()
                .filter(IVacuumPump.class::isInstance).map(IVacuumPump.class::cast)
                .findFirst().orElseGet(IVacuumPump.Empty::new);
    }

    public void onStructureInvalid() {
        super.onStructureInvalid();
        vacuumAmount = 0;
        pump = new IVacuumPump.Empty();
    }

    private int redstoneOutputVacPercentToPumpCapacity() {
        return (int) Math.floor(Math.min((vacuumAmount / pump.getPumpCap()) * 15f, 15f));
    }

    private void setVacuumAmount(float vacuumAmount) {
        this.vacuumAmount = Mth.clamp(vacuumAmount, 0f, (float) pump.getPumpCap());

        this.setIndicatorValue("variadic.start_core.indicator.vcrc.vac_to_capacity", redstoneOutputVacPercentToPumpCapacity());
    }

    @Override
    public StarTRedstoneIndicatorMap getIndicatorMap() {
        return indicatorMap;
    }

    @Override
    public List<StarTRedstoneIndicatorRecord> getInitialIndicators() {
        return List.of(
                new StarTRedstoneIndicatorRecord(
                        "variadic.start_core.indicator.vcrc.vac_to_capacity",
                        Component.translatable("variadic.start_core.indicator.vcrc.vac_to_capacity"),
                        Component.translatable("variadic.start_core.description.vcrc.vac_to_capacity", FormattingUtil.DECIMAL_FORMAT_0F.format(pump.getPumpCap())),
                        0,
                        0)
        );
    }

    public static Component formatVacuumStatus(Status status) {
        return Component.translatable(status.langKey).withStyle(status.color);
    }

    public static Component formatVacuumAmount(float vacuumAmount) {
        var status = vacuumAmount >= (100.0f - Mth.EPSILON) ? Status.FULL_VACUUM : vacuumAmount >= (80.0f - Mth.EPSILON) ? Status.PARTIAL_VACUUM : Status.PRESSURE_LOSS;
        return Component.literal(FormattingUtil.DECIMAL_FORMAT_0F.format(vacuumAmount) + "%").withStyle(status.color);
    }

    public static Component formatVacuumPumpCap(int cap) {
        return VacuumPumpPartMachine.formatVacuumPumpCap(cap);
    }

    public static Component formatVacuumPumpRate(int rate) {
        return Component.literal(rate + "%");
    }

    public enum Status {
        PUMPING_DOWN("ui.start_core.vcrc.vacuum_status.pumping_down", ChatFormatting.YELLOW),
        PARTIAL_VACUUM("ui.start_core.vcrc.vacuum_status.partial_vacuum", ChatFormatting.GREEN),
        FULL_VACUUM("ui.start_core.vcrc.vacuum_status.full_vacuum", ChatFormatting.DARK_GREEN),
        PRESSURE_LOSS("ui.start_core.vcrc.vacuum_status.pressure_loss", ChatFormatting.DARK_RED),
        IDLE("ui.start_core.vcrc.vacuum_status.idle", ChatFormatting.GOLD);

        @Getter
        private final String langKey;

        @Getter
        private final ChatFormatting color;

        Status(String langKey, ChatFormatting color) {
            this.langKey = langKey;
            this.color = color;
        }

        public static Status of(int num) {
            return Status.values()[num];
        }

        public Component format() {
            return VacuumChemicalReactionChamberMachine.formatVacuumStatus(this);
        }
    }
}



