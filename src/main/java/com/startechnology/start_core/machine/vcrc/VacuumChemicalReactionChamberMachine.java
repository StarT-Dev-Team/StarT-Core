package com.startechnology.start_core.machine.vcrc;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.startechnology.start_core.machine.redstone.IRedstoneIndicatorMachine;
import com.startechnology.start_core.machine.redstone.RedstoneIndicatorRecord;
import com.startechnology.start_core.machine.vacuum_pump.IVacuumPump;
import com.startechnology.start_core.machine.vacuum_pump.VacuumPumpPartMachine;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.List;

public class VacuumChemicalReactionChamberMachine extends WorkableElectricMultiblockMachine
                                                  implements IRedstoneIndicatorMachine {

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
        textList.add(Component.translatable("ui.start_core.vcrc.vacuum_status", formatVacuumStatus(vacuumStatus))
                .withStyle(ChatFormatting.GRAY));
        textList.add(Component.translatable("ui.start_core.vcrc.vacuum_amount", formatVacuumAmount(vacuumAmount))
                .withStyle(ChatFormatting.GRAY));
        if (isFormed) {
            textList.add(Component
                    .translatable("ui.start_core.vcrc.pump_type.cap",
                            VacuumPumpPartMachine.formatVacuumPumpCap(pump.getPumpCap()))
                    .withStyle(ChatFormatting.GRAY));
            textList.add(Component
                    .translatable("ui.start_core.vcrc.pump_type.rate",
                            VacuumPumpPartMachine.formatVacuumPumpRate(pump.getPumpRate()))
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
            vacuumStatus = getVacuumStatusFromAmount(vacuumAmount);
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
            vacuumSubscription = subscribeServerTick(vacuumSubscription, this::updateVacuum);
        }
    }

    @Override
    public void onWaiting() {
        super.onWaiting();
        if (!isRemote()) {
            // check next tick
            vacuumSubscription = subscribeServerTick(vacuumSubscription, this::updateVacuum);
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
        return (getRecipeLogic().isIdle() || !isWorkingEnabled() ||
                (getRecipeLogic().isWaiting() && getRecipeLogic().getProgress() == 0)) && vacuumAmount > Mth.EPSILON;
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

        this.setIndicatorValue("variadic.start_core.indicator.vcrc.vac_to_capacity",
                redstoneOutputVacPercentToPumpCapacity());
    }

    @Override
    public List<RedstoneIndicatorRecord> getInitialIndicators() {
        return List.of(
                new RedstoneIndicatorRecord(
                        "variadic.start_core.indicator.vcrc.vac_to_capacity",
                        Component.translatable("variadic.start_core.indicator.vcrc.vac_to_capacity"),
                        Component.translatable("variadic.start_core.description.vcrc.vac_to_capacity",
                                VacuumPumpPartMachine.formatVacuumPumpCap(pump.getPumpCap()).getString()),
                        redstoneOutputVacPercentToPumpCapacity(),
                        0));
    }

    public static Component formatVacuumStatus(Status status) {
        return Component.translatable(status.langKey).withStyle(status.color);
    }

    public static Status getVacuumStatusFromAmount(float v) {
        if (v > 90.0f + Mth.EPSILON) {
            return Status.ULTRA_HIGH_VACUUM;
        } else if (v > 85.0f + Mth.EPSILON) {
            return Status.HIGH_VACUUM;
        } else if (v > 80.0f + Mth.EPSILON) {
            return Status.MEDIUM_VACUUM;
        } else {
            return Status.NO_VACUUM;
        }
    }

    public static Component formatVacuumAmount(float vacuumAmount) {
        var status = getVacuumStatusFromAmount(vacuumAmount);
        return Component.literal(formatVacuumAmountString(vacuumAmount)).withStyle(status.color);
    }

    public static double getPressureExponent(double v) {
        if (v <= 0) return 5.0;
        if (v <= 80.0) {
            return 5.0 - (v / 80.0) * 2.0;
        } else if (v <= 85.0) {
            return 3.0 - ((v - 80.0) / 5.0) * 2.0;
        } else if (v <= 90.0) {
            return 1.0 - ((v - 85.0) / 5.0) * 4.0;
        } else if (v <= 95.0) {
            return -3.0 - ((v - 90.0) / 5.0) * 2.0;
        } else if (v <= 100.0) {
            return -5.0 - ((v - 95.0) / 5.0) * 2.0;
        } else {
            return -7.0;
        }
    }

    public static String toSuperscript(String str) {
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            switch (c) {
                case '0' -> sb.append('⁰');
                case '1' -> sb.append('¹');
                case '2' -> sb.append('²');
                case '3' -> sb.append('³');
                case '4' -> sb.append('⁴');
                case '5' -> sb.append('⁵');
                case '6' -> sb.append('⁶');
                case '7' -> sb.append('⁷');
                case '8' -> sb.append('⁸');
                case '9' -> sb.append('⁹');
                case '-' -> sb.append('⁻');
                case '.' -> sb.append('·');
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String formatVacuumAmountString(float vacuumAmount) {
        double exp = getPressureExponent(vacuumAmount);
        long roundedExp = Math.round(exp);
        String expStr;
        if (Math.abs(exp - roundedExp) < 1e-4) {
            expStr = String.format(java.util.Locale.ROOT, "%d", roundedExp);
        } else {
            expStr = String.format(java.util.Locale.ROOT, "%.1f", exp);
        }
        return "10" + toSuperscript(expStr) + " Pa";
    }

    public enum Status {

        PUMPING_DOWN("ui.start_core.vcrc.vacuum_status.pumping_down", ChatFormatting.YELLOW),
        NO_VACUUM("ui.start_core.vcrc.vacuum_status.no_vacuum", ChatFormatting.RED),
        MEDIUM_VACUUM("ui.start_core.vcrc.vacuum_status.medium_vacuum", ChatFormatting.GOLD),
        HIGH_VACUUM("ui.start_core.vcrc.vacuum_status.high_vacuum", ChatFormatting.GREEN),
        ULTRA_HIGH_VACUUM("ui.start_core.vcrc.vacuum_status.ultra_high_vacuum", ChatFormatting.DARK_GREEN),
        PRESSURE_LOSS("ui.start_core.vcrc.vacuum_status.pressure_loss", ChatFormatting.DARK_RED),
        IDLE("ui.start_core.vcrc.vacuum_status.idle", ChatFormatting.GRAY);

        @Getter
        private final String langKey;

        @Getter
        private final ChatFormatting color;

        Status(String langKey, ChatFormatting color) {
            this.langKey = langKey;
            this.color = color;
        }

        public static Status of(int num) {
            if (num < 0 || num >= values().length) return IDLE;
            return values()[num];
        }

        public Component format() {
            return VacuumChemicalReactionChamberMachine.formatVacuumStatus(this);
        }
    }
}
