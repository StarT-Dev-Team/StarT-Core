package com.startechnology.start_core.machine.heat_chamber;

import com.gregtechceu.gtceu.api.capability.IParallelHatch;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ParallelHatchPartMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.startechnology.start_core.machine.redstone.IRedstoneIndicatorMachine;
import com.startechnology.start_core.machine.redstone.RedstoneIndicatorRecord;
import com.startechnology.start_core.mixin.ParallelHatchPartMachineAccessor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.List;

public class HeatChamberMachine extends WorkableElectricMultiblockMachine implements IRedstoneIndicatorMachine {

    public static final int DEFAULT_BASE_TEMPERATURE = 298;
    public static final int COOLING_RATE = 1;
    public static final String REDSTONE_INDICATOR_KEY = "variadic.start_core.indicator.heat_chamber.temp_to_target";

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            HeatChamberMachine.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @DescSynced
    @Getter
    protected int temperature;

    @Persisted
    @DescSynced
    @Getter
    protected int targetTemperature;

    @Persisted
    @DescSynced
    @Getter
    protected int heatingRate;

    @Persisted
    @DescSynced
    @Getter
    protected Status heatStatus;

    private @Nullable TickableSubscription coolingSubscription;

    public HeatChamberMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        this.temperature = DEFAULT_BASE_TEMPERATURE;
        this.targetTemperature = DEFAULT_BASE_TEMPERATURE;
        this.heatingRate = 0;
        this.heatStatus = Status.IDLE;
    }

    @Override
    protected @NotNull RecipeLogic createRecipeLogic(Object... args) {
        return new HeatChamberRecipeLogic(this);
    }

    @Override
    public @NotNull HeatChamberRecipeLogic getRecipeLogic() {
        return (HeatChamberRecipeLogic) super.getRecipeLogic();
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public boolean keepSubscribing() {
        return true;
    }

    public static @NotNull ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof HeatChamberMachine heatChamber)) {
            return RecipeModifier.nullWrongType(HeatChamberMachine.class, machine);
        }
        if (!heatChamber.isFormed()) {
            return ModifierFunction.NULL;
        }

        var hatchOpt = heatChamber.getParallelHatch();
        if (hatchOpt.isPresent()) {
            IParallelHatch hatch = hatchOpt.get();
            int parallels = ParallelLogic.getParallelAmountWithoutEU(heatChamber, recipe, hatch.getCurrentParallel());
            if (parallels < hatch.getMinimumParallel()) {
                return ModifierFunction
                        .cancel(Component.translatable("gtceu.recipe_modifier.cant_perform_at_min_parallel"));
            }
        }

        return ModifierFunction.IDENTITY;
    }

    public int getMaxHatchParallels() {
        var hatchOpt = this.getParallelHatch();
        if (hatchOpt.isEmpty()) return 1;

        IParallelHatch hatch = hatchOpt.get();

        if (hatch instanceof ParallelHatchPartMachine partMachine) {
            return Math.max(1, ((ParallelHatchPartMachineAccessor) partMachine).start_core$getMaxParallel());
        }

        return Math.max(1, hatch.getCurrentParallel());
    }

    public int getAchievableParallels(GTRecipe rawRecipe, GTRecipe modifiedRecipe) {
        var hatchOpt = getParallelHatch();
        if (hatchOpt.isEmpty()) return 1;

        IParallelHatch hatch = hatchOpt.get();

        int parallels = (modifiedRecipe.parallels) > 1 ? modifiedRecipe.parallels :
                ParallelLogic.getParallelAmountWithoutEU(this, rawRecipe, hatch.getCurrentParallel());

        if (parallels < hatch.getMinimumParallel()) return 0;

        return parallels;
    }

    public int calculateHeatingRate(GTRecipe rawRecipe, GTRecipe modifiedRecipe) {
        int achievableParallels = getAchievableParallels(rawRecipe, modifiedRecipe);
        if (achievableParallels <= 0) return 0;

        int maxParallels = getMaxHatchParallels();
        double ratio = (double) achievableParallels / (double) maxParallels;

        if (ratio < 0.25) return 2;
        if (ratio < 0.50) return 5;
        if (ratio < 0.75) return 10;
        return 25;
    }

    public void onStartHeatingRecipe(int targetTemp, int rate) {
        this.targetTemperature = Math.max(DEFAULT_BASE_TEMPERATURE, targetTemp);
        this.heatingRate = rate;
        this.heatStatus = Status.HEATING;
        updateRedstoneIndicator();
        updateCoolingSubscription();
    }

    public void onStartWorkingRecipe(int minTemp) {
        this.targetTemperature = Math.max(DEFAULT_BASE_TEMPERATURE, minTemp);
        this.heatingRate = 0;
        this.heatStatus = Status.WORKING;
        updateRedstoneIndicator();
        updateCoolingSubscription();
    }

    @Override
    public boolean onWorking() {
        if (!super.onWorking()) {
            return false;
        }

        GTRecipe currentRecipe = getRecipeLogic().getLastRecipe();
        if (currentRecipe != null && HeatChamberRecipeLogic.isHeatingRecipe(currentRecipe)) {
            int rate = currentRecipe.data.getInt("heating_rate");
            int target = currentRecipe.data.getInt("target_temp");
            this.heatStatus = Status.HEATING;
            this.heatingRate = rate;
            this.targetTemperature = Math.max(DEFAULT_BASE_TEMPERATURE, target);

            if ((getRecipeLogic().getProgress() + 1) % 20 == 0) {
                setTemperature(Math.min(this.temperature + rate, this.targetTemperature));
            }
        } else {
            this.heatStatus = Status.WORKING;
            this.heatingRate = 0;
            if (currentRecipe != null) {
                this.targetTemperature = Math.max(DEFAULT_BASE_TEMPERATURE,
                        HeatChamberRecipeLogic.getMinTemp(currentRecipe));
            }
            updateRedstoneIndicator();
        }

        return true;
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
        if (!isRemote()) {
            updateCoolingSubscription();
        }
    }

    @Override
    public void onWaiting() {
        super.onWaiting();
        if (!isRemote()) {
            updateCoolingSubscription();
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!isRemote()) {
            updateCoolingSubscription();
        }
    }

    @Override
    public void onUnload() {
        super.onUnload();
        if (coolingSubscription != null) {
            coolingSubscription.unsubscribe();
            coolingSubscription = null;
        }
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        if (!isRemote()) {
            updateRedstoneIndicator();
            updateCoolingSubscription();
        }
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        this.temperature = DEFAULT_BASE_TEMPERATURE;
        this.targetTemperature = DEFAULT_BASE_TEMPERATURE;
        this.heatingRate = 0;
        this.heatStatus = Status.IDLE;
        if (!isRemote()) {
            updateRedstoneIndicator();
            updateCoolingSubscription();
        }
    }

    private boolean shouldCoolDown() {
        return isFormed() && (!getRecipeLogic().isWorking() || !isWorkingEnabled()) &&
                this.temperature > DEFAULT_BASE_TEMPERATURE;
    }

    public void updateCoolingSubscription() {
        if (isRemote()) return;
        if (shouldCoolDown()) {
            this.heatStatus = Status.COOLING;
            this.heatingRate = -COOLING_RATE;
            this.coolingSubscription = subscribeServerTick(this.coolingSubscription, this::tickCooling);
        } else {
            if (!getRecipeLogic().isWorking()) {
                this.heatStatus = Status.IDLE;
                this.heatingRate = 0;
                this.targetTemperature = DEFAULT_BASE_TEMPERATURE;
                updateRedstoneIndicator();
            }
            if (this.coolingSubscription != null) {
                this.coolingSubscription.unsubscribe();
                this.coolingSubscription = null;
            }
        }
    }

    private void tickCooling() {
        if (shouldCoolDown()) {
            this.heatStatus = Status.COOLING;
            this.heatingRate = -COOLING_RATE;
            if (getOffsetTimer() % 20 == 0) {
                setTemperature(Math.max(DEFAULT_BASE_TEMPERATURE, this.temperature - COOLING_RATE));
            }
        }
        updateCoolingSubscription();
    }

    public void setTemperature(int newTemperature) {
        this.temperature = Math.max(DEFAULT_BASE_TEMPERATURE, newTemperature);
        updateRedstoneIndicator();
    }

    public int getRedstoneSignalForTarget() {
        if (this.targetTemperature <= DEFAULT_BASE_TEMPERATURE) {
            return (this.temperature > DEFAULT_BASE_TEMPERATURE) ? 15 : 0;
        }
        if (this.temperature >= this.targetTemperature) return 15;

        double progress = (double) (this.temperature - DEFAULT_BASE_TEMPERATURE) /
                (this.targetTemperature - DEFAULT_BASE_TEMPERATURE);

        return Mth.clamp((int) Math.floor(progress * 15.0), 0, 15);
    }

    private void updateRedstoneIndicator() {
        this.setIndicatorValue(REDSTONE_INDICATOR_KEY, getRedstoneSignalForTarget());
    }

    @Override
    public List<RedstoneIndicatorRecord> getInitialIndicators() {
        return List.of(
                new RedstoneIndicatorRecord(REDSTONE_INDICATOR_KEY, Component.translatable(REDSTONE_INDICATOR_KEY),
                        Component.translatable("variadic.start_core.description.heat_chamber.temp_to_target"),
                        this.getRedstoneSignalForTarget(), 0));
    }

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        super.addDisplayText(textList);

        textList.add(Component.translatable("ui.start_core.heat_chamber.status", formatHeatStatus(this.heatStatus))
                .withStyle(ChatFormatting.GRAY));
        textList.add(Component.translatable("ui.start_core.heat_chamber.temperature",
                formatTemperature(this.temperature, this.heatStatus)).withStyle(ChatFormatting.GRAY));

        if (this.targetTemperature > DEFAULT_BASE_TEMPERATURE) {
            textList.add(Component.translatable("ui.start_core.heat_chamber.target_temperature",
                    Component.literal(FormattingUtil.formatNumbers(this.targetTemperature) + " K")
                            .withStyle(ChatFormatting.GOLD))
                    .withStyle(ChatFormatting.GRAY));
        }

        textList.add(Component.translatable("ui.start_core.heat_chamber.rate", formatHeatingRate(this.heatingRate))
                .withStyle(ChatFormatting.GRAY));
    }

    public static Component formatHeatStatus(Status status) {
        return Component.translatable(status.langKey).withStyle(status.color);
    }

    public static Component formatTemperature(int temperature, Status status) {
        return Component.literal(FormattingUtil.formatNumbers(temperature) + " K").withStyle(status.color);
    }

    public static Component formatHeatingRate(int rate) {
        if (rate > 0) {
            return Component.literal("+" + FormattingUtil.formatNumbers(rate) + " K/s")
                    .withStyle(ChatFormatting.YELLOW);
        } else if (rate < 0) {
            return Component.literal(FormattingUtil.formatNumbers(rate) + " K/s")
                    .withStyle(ChatFormatting.AQUA);
        }
        return Component.literal("0 K/s").withStyle(ChatFormatting.GRAY);
    }

    public enum Status {

        IDLE("ui.start_core.heat_chamber.status.idle", ChatFormatting.GOLD),
        HEATING("ui.start_core.heat_chamber.status.heating", ChatFormatting.YELLOW),
        WORKING("ui.start_core.heat_chamber.status.working", ChatFormatting.GREEN),
        COOLING("ui.start_core.heat_chamber.status.cooling", ChatFormatting.AQUA);

        @Getter
        private final String langKey;

        @Getter
        private final ChatFormatting color;

        Status(String langKey, ChatFormatting color) {
            this.langKey = langKey;
            this.color = color;
        }

        public static Status of(int ordinal) {
            Status[] values = Status.values();
            if (ordinal < 0 || ordinal >= values.length) {
                return IDLE;
            }
            return values[ordinal];
        }
    }
}
