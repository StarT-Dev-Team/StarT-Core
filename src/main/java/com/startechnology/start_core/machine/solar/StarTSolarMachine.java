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
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.startechnology.start_core.machine.solar.cell.StarTSolarCell;
import com.startechnology.start_core.machine.solar.cell.StarTSolarCellBlockEntity;
import com.startechnology.start_core.machine.solar.cell.StarTSolarCellType;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StarTSolarMachine extends WorkableElectricMultiblockMachine {
    @Getter
    private final int tier;
    @Getter
    private final List<SolarCellInstance> cells;
    @Getter
    private int euT = 0;
    @Getter
    private double avgTemp = 0;
    @Getter
    private int avgDura = 0;
    @Getter
    private int cellAmount = 0;
    @Getter
    private int brokenCells = 0;
    @Getter
    private boolean isCooled = false;
    @Getter
    @Persisted
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
        double totalTemp = 0;
        int totalDura = 0;

        euT = 0;
        cellAmount = 0;
        brokenCells = 0;

        cells.clear();

        if (!level.isClientSide) {
            LongOpenHashSet solarCells = getMultiblockState().getMatchContext().get("cellPositions");

            if (solarCells != null && !solarCells.isEmpty()) {
                for (long packedPos : solarCells) {
                    BlockPos blockPos = BlockPos.of(packedPos);
                    BlockState blockState = level.getBlockState(blockPos);

                    if (blockState.getBlock() instanceof StarTSolarCell solarCell) {
                        BlockEntity blockEntity = level.getBlockEntity(blockPos);

                        if (!(blockEntity instanceof StarTSolarCellBlockEntity solarCellBlockEntity)) continue;

                        cellAmount++;

                        totalTemp += solarCellBlockEntity.getTemperature();
                        totalDura += solarCellBlockEntity.getDurability();

                        cells.add(new SolarCellInstance(blockPos, solarCell.getSolarCellType()));

                        if (!solarCellBlockEntity.isBroken() && level.canSeeSky(blockPos)) {
                            euT += GTValues.V[solarCell.getSolarCellType().getTier()] / 3;
                        } else if (solarCellBlockEntity.isBroken()) {
                            brokenCells++;
                        }
                    }
                }
            }

            euT = (int) (euT * getOutputModifier());

            int activeCells = cellAmount - brokenCells;

            avgTemp = totalTemp / activeCells;
            avgDura = totalDura / activeCells;
        }
    }

    public void doLogic() {
        var level = getLevel();
        boolean isDay = isDay();

        int newEuT = 0;
        double totalTemp = 0;
        int totalDura = 0;
        int newBrokenCells = 0;

        if (tier >= GTValues.UV && tier <= GTValues.UHV) {
            ++runningTimer;

            if (runningTimer == 10) {
                GTRecipe boostingRecipe = getBoostingRecipe();

                runningTimer = 0;
                isCooled = RecipeHelper.matchRecipe(this, boostingRecipe).isSuccess() && RecipeHelper.handleRecipeIO(this, boostingRecipe, IO.IN, recipeLogic.getChanceCaches()).isSuccess();
            }
        }

        var heatDiff = isDay ? getHeatGain() : getHeatLoose();

        for (SolarCellInstance solarCell : cells) {
            BlockPos blockPos = solarCell.blockPos();
            StarTSolarCellType solarCellType = solarCell.cellType();
            BlockEntity blockEntity = level.getBlockEntity(blockPos);

            if (!(blockEntity instanceof StarTSolarCellBlockEntity solarCellBlockEntity)) continue;

            if (solarCellBlockEntity.isBroken()) {
                GTRecipe solarCellRecipe = getSolarPanelRecipe(solarCellType);

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

                    newBrokenCells++;

                    continue;
                }

                int durabilityDiff = StarTSolarCell.calculateDurabilityDamage((currentTemp - 273) / (maxTemp - 273));
                int newDurability = solarCellBlockEntity.getDurability() - durabilityDiff;

                if (newDurability <= 0) {
                    solarCellBlockEntity.setBroken(true);

                    newBrokenCells++;

                    continue;
                }

                solarCellBlockEntity.setTemperature(currentTemp);
                solarCellBlockEntity.setDurability(newDurability);

                totalTemp += currentTemp;
                totalDura += newDurability;

                if (level.canSeeSky(blockPos)) {
                    newEuT += (int) (GTValues.V[solarCellType.getTier()] / 3);
                }
            } else {
                double currentTemp = Math.max(solarCellBlockEntity.getTemperature() - heatDiff, solarCellType.getMinTemperature());

                solarCellBlockEntity.setTemperature(currentTemp);

                totalTemp += currentTemp;
            }
        }

        int activeCells = cellAmount - brokenCells;

        euT = (int) (newEuT * getOutputModifier());
        brokenCells = newBrokenCells;
        avgTemp = totalTemp / activeCells;
        avgDura = totalDura / activeCells;
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

    public boolean isDay() {
        return getLevel().isDay();
    }

    public GTRecipe getBoostingRecipe() {
        var amount = tier == GTValues.UV ? 1000 : 2500;

        return GTRecipeBuilder.ofRaw().inputFluids(GTMaterials.get("tungsten_disulfide").getFluid(amount)).buildRawRecipe();
    }

    public GTRecipe getSolarPanelRecipe(StarTSolarCellType type) {
        return GTRecipeBuilder.ofRaw().inputItems(type.getSerializedName(), 1).buildRawRecipe();
    }

    public boolean regressWhenWaiting() {
        return false;
    }

    public boolean canVoidRecipeOutputs(RecipeCapability<?> capability) {
        return false;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);

        if (isFormed) {
            if (isActive()) {
                textList.add(3, Component.translatable("gtceu.multiblock.turbine.energy_per_tick_maxed", FormattingUtil.formatNumbers(euT)));
            }

            textList.add(Component.translatable("solar.start_core.solar_machine.cell_tooltip", cellAmount, brokenCells));
            textList.add(Component.translatable("solar.start_core.solar_machine.avg_temp_tooltip", FormattingUtil.formatNumbers(avgTemp)));
            textList.add(Component.translatable("solar.start_core.solar_machine.avg_dura_tooltip", avgDura));
        }
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

    public record SolarCellInstance(BlockPos blockPos, StarTSolarCellType cellType) {
    }
}
