package com.startechnology.start_core.machine.solar;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.startechnology.start_core.machine.solar.cell.StarTSolarCell;
import com.startechnology.start_core.machine.solar.cell.StarTSolarCellBlockEntity;
import com.startechnology.start_core.machine.solar.cell.StarTSolarCellType;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.Mth;
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
    private final List<StarTSolarCell> panels;
    private boolean isWorking;
    @Getter
    private int euT;

    public StarTSolarMachine(IMachineBlockEntity holder, int tier) {
        super(holder);

        this.tier = tier;
        this.panels = new ArrayList<>();
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

        this.getParts().forEach(part -> {
            if (part instanceof StarTSolarCell solarCell) {
                if (!solarCell.getSolarCellBlockEntity().isBroken()) {
                    euT += GTValues.VHA[solarCell.getSolarCellType().getTier()];
                }

                panels.add(solarCell);
            }
        });
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

        this.panels.forEach(solarCell -> {
            StarTSolarCellBlockEntity solarCellBlockEntity = solarCell.getSolarCellBlockEntity();

            if (!solarCellBlockEntity.isBroken()) {
                StarTSolarCellType solarCellType = solarCell.getSolarCellType();

                int tier = solarCellType.getTier();
                int maxTemp = solarCellType.getMaxTemperature();
                int currentTemp = Math.min(maxTemp, solarCellBlockEntity.getTemperature() + tier);

                if (currentTemp >= maxTemp) {
                    solarCell.getSolarCellBlockEntity().setBroken(true);

                    return;
                }

                double tempPercent = (double) currentTemp / maxTemp;
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

        euT = newEuT.get();
    }

    public boolean regressWhenWaiting() {
        return false;
    }

    public boolean canVoidRecipeOutputs(RecipeCapability<?> capability) {
        return capability != EURecipeCapability.CAP;
    }

    public static class StarTSolarMachineRecipeLogic extends RecipeLogic {
        private static final int BASE_UPDATE_INTERVAL = 5 * 20;

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
                energyContainer.changeEnergy(resultEnergy);
                return true;
            }
            return false;
        }

        @Override
        public void serverTick() {
            if (!getMachine().isFormed() || !isWorkingEnabled()) {
                setStatus(Status.IDLE);
            } else if (produceEnergy()) {
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
