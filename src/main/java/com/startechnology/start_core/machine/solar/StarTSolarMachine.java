package com.startechnology.start_core.machine.solar;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.startechnology.start_core.machine.solar.cell.StarTSolarCell;
import com.startechnology.start_core.machine.solar.cell.StarTSolarCellBlockEntity;
import com.startechnology.start_core.machine.solar.cell.StarTSolarCellType;
import it.unimi.dsi.fastutil.longs.LongSets;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StarTSolarMachine extends WorkableElectricMultiblockMachine {
    @Getter
    private final int tier;
    private final List<StarTSolarCell> cells;
    @Getter
    private int euT = 0;
    @Setter
    @Getter
    private boolean isCooled = false;
    @Getter
    private int runningTimer = 0;

    public StarTSolarMachine(IMachineBlockEntity holder, int tier) {
        super(holder);

        this.tier = tier;
        this.cells = new ArrayList<>();
    }

    @Override
    protected RecipeLogic createRecipeLogic(Object... args) {
        return new StarTSolarMachineRecipeLogic(this);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        var level = getLevel();

        for (var cellPosition : getMultiblockState().getMatchContext().getOrDefault("cellPositions", LongSets.emptySet())) {
            if (level.getBlockState(BlockPos.of(cellPosition)).getBlock() instanceof StarTSolarCell solarCell) {
                if (!solarCell.getSolarCellBlockEntity().isBroken() && level.canSeeSky(BlockPos.of(cellPosition))) {
                    euT += (int) (GTValues.V[solarCell.getSolarCellType().getTier()] / 3);

                    cells.add(solarCell);
                }
            }
        }

        euT = (int) (euT * getOutputModifier());
    }

    public void doLogic() {
        var isDay = isDay();
        var level = getLevel();

        AtomicInteger newEuT = new AtomicInteger();

        if (tier >= GTValues.UV && tier <= GTValues.UHV) {
            ++runningTimer;

            if (runningTimer == 10) {
                GTRecipe boostingRecipe = getBoostingRecipe();

                runningTimer = 0;
                isCooled = RecipeHelper.matchRecipe(this, boostingRecipe).isSuccess() && RecipeHelper.handleRecipeIO(this, boostingRecipe, IO.IN, recipeLogic.getChanceCaches()).isSuccess();
            }
        }

        var heatDiff = isDay ? getHeatGain() : getHeatLoose();

        for (var solarCell : cells) {
            StarTSolarCellBlockEntity solarCellBlockEntity = solarCell.getSolarCellBlockEntity();
            StarTSolarCellType solarCellType = solarCell.getSolarCellType();

            if (solarCellBlockEntity.isBroken()) {
                GTRecipe solarCellRecipe = getSolarPanelRecipe(solarCell);

                if (RecipeHelper.matchRecipe(this, solarCellRecipe).isSuccess() && RecipeHelper.handleRecipeIO(this, solarCellRecipe, IO.IN, recipeLogic.getChanceCaches()).isSuccess()) {
                    solarCellBlockEntity.setBroken(false);
                    solarCellBlockEntity.setDurability(solarCellType.getMaxDurability());
                    solarCellBlockEntity.setTemperature(300);
                }
            }

            if (isDay) {
                int maxTemp = solarCellType.getMaxTemperature();
                double currentTemp = solarCellBlockEntity.getTemperature() + (solarCellType.getTemperatureScale() * heatDiff);

                if (currentTemp > maxTemp) {
                    solarCellBlockEntity.setBroken(true);

                    continue;
                }

                double tempPercent = (currentTemp - 273) / (maxTemp - 273);
                int durabilityDiff = solarCell.calculateDurabilityDamage(tempPercent);
                int durability = solarCellBlockEntity.getDurability() - durabilityDiff;

                if (durability <= 0) {
                    solarCellBlockEntity.setBroken(true);

                    continue;
                }

                solarCellBlockEntity.setTemperature(currentTemp);
                solarCellBlockEntity.setDurability(durability);

                if (level.canSeeSky(solarCell.getSolarCellBlockEntity().getBlockPos())) {
                    newEuT.addAndGet((int) GTValues.V[solarCellType.getTier()] / 3);
                }
            } else {
                solarCellBlockEntity.setTemperature(solarCellBlockEntity.getTemperature() - heatDiff);
            }
        }

        euT = newEuT.get() > 0 ? (int) (newEuT.get() * getOutputModifier()) : 0;
    }

    public double getOutputModifier() {
        return switch (tier) {
            case GTValues.IV -> 1.1;
            case GTValues.LuV -> 1.2;
            case GTValues.UV -> 1.5;
            case GTValues.UHV -> 1.75;
            default -> 1.0;
        };
    }

    public double getHeatGain() {
        if (tier >= GTValues.EV && tier <= GTValues.LuV) return 0.2;
        else {
            if (isCooled) return 0.25;
            else return 0.3;
        }
    }

    public double getHeatLoose() {
        if (isCooled) return 0.15;
        else return 0.1;
    }

    public GTRecipe getBoostingRecipe() {
        var amount = tier == GTValues.UV ? 1000 : 2500;

        return GTRecipeBuilder.ofRaw().inputFluids(GTMaterials.get("tungsten_disulfide").getFluid(amount)).buildRawRecipe();
    }

    public GTRecipe getSolarPanelRecipe(StarTSolarCell solarCell) {
        return GTRecipeBuilder.ofRaw().inputItems(solarCell, 1).buildRawRecipe();
    }

    public boolean regressWhenWaiting() {
        return false;
    }

    public boolean canVoidRecipeOutputs(RecipeCapability<?> capability) {
        return false;
    }

    public boolean isDay() {
        return getLevel().isDay();
    }

    public static class StarTSolarMachineRecipeLogic extends RecipeLogic {
        private static final int BASE_UPDATE_INTERVAL = 6 * 20;

        public StarTSolarMachineRecipeLogic(StarTSolarMachine metaTileEntity) {
            super(metaTileEntity);
        }

        @NotNull
        @Override
        public StarTSolarMachine getMachine() {
            return (StarTSolarMachine) super.getMachine();
        }

        private void produceEnergy() {
            EnergyContainerList energyContainer = getMachine().energyContainer;

            if (energyContainer == null) return;

            long resultEnergy = energyContainer.getEnergyStored() + getMachine().euT;

            if (resultEnergy >= 0L && resultEnergy <= energyContainer.getEnergyCapacity()) {
                energyContainer.changeEnergy(getMachine().euT);
            }
        }

        @Override
        public void serverTick() {
            var machine = getMachine();

            if (!machine.isFormed || !isWorkingEnabled()) {
                setStatus(Status.IDLE);
            } else {
                isActive = true;
                progress = (progress + 1) % BASE_UPDATE_INTERVAL;

                if (machine.isDay()) produceEnergy();

                if (progress == 0) {
                    machine.doLogic();
                }
            }
        }

        @Override
        public int getMaxProgress() {
            return BASE_UPDATE_INTERVAL;
        }

        @Override
        public boolean isActive() {
            return getMachine().isFormed() && isActive;
        }
    }
}
