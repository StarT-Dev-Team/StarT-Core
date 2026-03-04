package com.startechnology.start_core.machine.solar;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.startechnology.start_core.machine.solar.cell.StarTSolarCell;
import com.startechnology.start_core.machine.solar.cell.StarTSolarCellBlockEntity;
import com.startechnology.start_core.machine.solar.cell.StarTSolarCellType;
import it.unimi.dsi.fastutil.longs.LongSets;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private boolean isWorking;
    @Getter
    private int euT;

    public StarTSolarMachine(IMachineBlockEntity holder, int tier) {
        super(holder);

        this.tier = tier;
        this.cells = new ArrayList<>();
        this.isWorking = false;
        this.euT = 0;
    }

    @Override
    protected RecipeLogic createRecipeLogic(Object... args) {
        return new StarTSolarMachineRecipeLogic(this);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        for (var cellPosition : getMultiblockState().getMatchContext().getOrDefault("cellPositions", LongSets.emptySet())) {
            if (getLevel().getBlockState(BlockPos.of(cellPosition)).getBlock() instanceof StarTSolarCell solarCell) {
                if (!solarCell.getSolarCellBlockEntity().isBroken()) {
                    euT += (int) (GTValues.V[solarCell.getSolarCellType().getTier()] / 3);

                    cells.add(solarCell);
                }
            }
        }
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        boolean isWorking = super.beforeWorking(recipe);

        if (isWorking) {
            this.isWorking = true;
        }

        return isWorking;
    }

    @Override
    public void afterWorking() {
        super.afterWorking();

        this.isWorking = false;
    }

    public void doLogic() {
        AtomicInteger newEuT = new AtomicInteger();

        this.cells.forEach(solarCell -> {
            StarTSolarCellBlockEntity solarCellBlockEntity = solarCell.getSolarCellBlockEntity();

            if (!solarCellBlockEntity.isBroken()) {
                StarTSolarCellType solarCellType = solarCell.getSolarCellType();

                int maxTemp = solarCellType.getMaxTemperature();
                double currentTemp = solarCellBlockEntity.getTemperature() + (solarCellType.getTemperatureScale() * solarCellType.getHeatRaise());

                if (currentTemp >= maxTemp) {
                    solarCell.getSolarCellBlockEntity().setBroken(true);

                    return;
                }

                double tempPercent = (currentTemp - 273) / (maxTemp - 273);
                int durabilityDiff = solarCell.calculateDurabilityDamage(tempPercent);
                int durability = Math.max(0, solarCellBlockEntity.getDurability() - durabilityDiff);

                if (durability == 0) {
                    solarCell.getSolarCellBlockEntity().setBroken(true);

                    return;
                }

                solarCell.getSolarCellBlockEntity().setTemperature(currentTemp);
                solarCell.getSolarCellBlockEntity().setDurability(durability);

                newEuT.addAndGet(GTValues.VHA[solarCellType.getTier()]);
            }
        });

    public void doNightLogic() {}
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
@Getter
        public static int nightProgress = 0;

        public StarTSolarMachineRecipeLogic(StarTSolarMachine metaTileEntity) {
            super(metaTileEntity);
        }

        @NotNull
        @Override
        public StarTSolarMachine getMachine() {
            return (StarTSolarMachine) super.getMachine();
        }

        private boolean produceEnergy() {
            EnergyContainerList energyContainer = getMachine().energyContainer;

            if (energyContainer == null) {
                return false;
            }

            long resultEnergy = energyContainer.getEnergyStored() + getMachine().euT;
            if (resultEnergy >= 0L && resultEnergy <= energyContainer.getEnergyCapacity()) {
                energyContainer.changeEnergy(getMachine().euT);
                return true;
            }
            return false;
        }

        @Override
        public void serverTick() {
            if (!getMachine().isFormed() || !isWorkingEnabled()) {
                setStatus(Status.IDLE);
            }  else if (getMachine().isDay() && produceEnergy()) {
                setStatus(Status.WORKING);

                isActive = true;
                progress = (progress + 1) % BASE_UPDATE_INTERVAL;

                if (progress == 0) {
                    getMachine().doLogic();
                }
            } else {
                setStatus(Status.WAITING);

                isActive = false;
                progress = Math.max(progress - 2, 1);

                nightProgress = (nightProgress + 1) % BASE_UPDATE_INTERVAL;

                if (nightProgress == 0) {
                    getMachine().doNightLogic();
                }
            }
        }

        @Override
        public int getMaxProgress() {
            return BASE_UPDATE_INTERVAL;
        }

        @Override
        public boolean isActive() {
            return getMachine().isFormed() && this.isActive;
        }
    }
}
