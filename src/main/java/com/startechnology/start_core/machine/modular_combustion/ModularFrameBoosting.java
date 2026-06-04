package com.startechnology.start_core.machine.modular_combustion;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.startechnology.start_core.machine.modular.StarTModularConduitHatchPartMachine;
import com.startechnology.start_core.machine.modular.StarTModularControllerMachine;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ModularFrameBoosting extends StarTModularControllerMachine {

    @Persisted
    @DescSynced
    private long netInLastSec = 0L;

    @Persisted
    @DescSynced
    private long netOutLastSec = 0L;

    @Persisted
    @DescSynced
    private long inputPerSec = 0L;

    @Persisted
    @DescSynced
    private long outputPerSec = 0L;

    private static final Material DISTILLED_WATER = GTMaterials.get("distilled_water");
    private static final Material DEIONIZED_WATER = GTMaterials.get("deionized_water");
    private Material activeCoolant = null;
    private EnergyContainerList frameNodeContainers = new EnergyContainerList(new ArrayList<>());
    private List<StarTModularConduitHatchPartMachine> trackedConduits = new ArrayList<>();
    private EnergyContainerList frameOutputHatches = new EnergyContainerList(new ArrayList<>());

    public ModularFrameBoosting(IMachineBlockEntity holder, ResourceLocation... supportedMultiblockIds) {
        super(holder, supportedMultiblockIds);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        trackedConduits = new ArrayList<>();
        List<IEnergyContainer> nodeList = new ArrayList<>();
        List<IEnergyContainer> outputHatchList = new ArrayList<>();

        for (IMultiPart part : getParts()) {
            if (part instanceof StarTModularConduitHatchPartMachine conduit && !conduit.isTerminal()) {
                trackedConduits.add(conduit);
                nodeList.add(conduit.getEnergyContainer());
            }

            for (var handlerList : part.getRecipeHandlers()) {
                if (!handlerList.getHandlerIO().support(IO.OUT)) continue;

                for (var capability : handlerList.getCapability(EURecipeCapability.CAP)) {
                    if (capability instanceof IEnergyContainer container) {
                        outputHatchList.add(container);
                    }
                }
            }
        }

        this.frameNodeContainers = new EnergyContainerList(nodeList);
        this.outputConduits = new EnergyContainerList(new ArrayList<>());

        this.inputHatches = new EnergyContainerList(new ArrayList<>());

        this.frameOutputHatches = new EnergyContainerList(outputHatchList);
    }

    @Override
    public void onStructureInvalid() {
        trackedConduits = new ArrayList<>();
        this.frameNodeContainers = new EnergyContainerList(new ArrayList<>());
        this.frameOutputHatches = new EnergyContainerList(new ArrayList<>());
        this.netInLastSec = 0L;
        this.netOutLastSec = 0L;
        this.inputPerSec = 0L;
        this.outputPerSec = 0L;
        this.activeCoolant = null;
        super.onStructureInvalid();
    }

    private int countLinkedModules() {
        int count = 0;
        for (StarTModularConduitHatchPartMachine hatch : trackedConduits) {
            if (hatch != null && hatch.isCurrentlyLinked()) count++;
        }
        return count;
    }

    @Override
    protected void transferEnergy() {
        if (getLevel().isClientSide) return;
        if (!this.readyToUpdate) return;

        if (getOffsetTimer() % 60L == 0L) {
            this.inputPerSec   = this.netInLastSec;
            this.outputPerSec  = this.netOutLastSec;
            this.netInLastSec  = 0L;
            this.netOutLastSec = 0L;
            transferModuleInterfacesTick();
        }
    }

    private boolean hasDistilledWater() {
        return RecipeHelper.matchRecipe(this,
                GTRecipeBuilder.ofRaw().inputFluids(DISTILLED_WATER.getFluid(500000)).buildRawRecipe()
        ).isSuccess();
    }

    private boolean hasDeionizedWater() {
        return RecipeHelper.matchRecipe(this,
                GTRecipeBuilder.ofRaw().inputFluids(DEIONIZED_WATER.getFluid(500000)).buildRawRecipe()
        ).isSuccess();
    }

    private double getFrameBoost() {
        if (hasDeionizedWater()) return 1.4;
        if (hasDistilledWater()) return 1.2;
        return 0.9;
    }

    private void consumeCoolant() {
        int linked = countLinkedModules();

        if (linked <= 0) {
            activeCoolant = null;
            return;
        }

        if (hasDeionizedWater()) {
            activeCoolant = DEIONIZED_WATER;
        } else if (hasDistilledWater()) {
            activeCoolant = DISTILLED_WATER;
        } else {
            activeCoolant = null;
            return;
        }

        GTRecipe consumeRecipe = GTRecipeBuilder.ofRaw()
                .inputFluids(activeCoolant.getFluid(linked))
                .buildRawRecipe();

        RecipeHelper.handleRecipeIO(this, consumeRecipe, IO.IN, null);
    }

    @Override
    protected boolean transferModuleInterfacesTick() {
        if (getLevel().isClientSide || !this.readyToUpdate || !isWorkingEnabled()) return false;
        long energyInNodes = frameNodeContainers.getEnergyStored();
        if (energyInNodes <= 0) return false;

        if (hasDeionizedWater()) {
            activeCoolant = DEIONIZED_WATER;
        } else if (hasDistilledWater()) {
            activeCoolant = DISTILLED_WATER;
        } else {
            activeCoolant = null;
        }

        double boost = getFrameBoost();
        long boostedEnergy = (long)(energyInNodes * boost);
        long transferred = frameOutputHatches.changeEnergy(boostedEnergy);

        if (transferred > 0) {
            long rawConsumed = Math.min(
                    energyInNodes,
                    (long) Math.ceil((double) transferred / boost)
            );
            frameNodeContainers.removeEnergy(rawConsumed);

            this.netInLastSec  += rawConsumed;
            this.netOutLastSec += transferred;

            if (getOffsetTimer() % 144L == 0L) {
                consumeCoolant();
            }

            return true;
        }
        return false;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);

        if (isFormed()) {
            int linked = countLinkedModules();
            double boost = getFrameBoost();

            long avgEuIn  = this.inputPerSec  / 60L;
            long avgEuOut = this.outputPerSec / 60L;

            textList.add(Component.translatable("start_core.multiblock.frame.avg_eu_in",
                    FormattingUtil.formatNumbers(avgEuIn)));

            textList.add(Component.translatable("start_core.multiblock.frame.frame_boost",
                    (int) Math.round((boost - 1.0) * 100)));

            textList.add(Component.translatable("start_core.multiblock.frame.boosted_eu_out",
                    FormattingUtil.formatNumbers(avgEuOut)));

            textList.add(Component.translatable("start_core.multiblock.frame.linked_modules",
                    linked));

            if (activeCoolant == DEIONIZED_WATER) {
                textList.add(Component.translatable("start_core.multiblock.frame.cooled_deionized"));
            } else if (activeCoolant == DISTILLED_WATER) {
                textList.add(Component.translatable("start_core.multiblock.frame.cooled_distilled"));
            } else {
                textList.add(Component.translatable("start_core.multiblock.frame.not_cooled"));
            }

            if (activeCoolant != null && linked > 0) {
                textList.add(Component.translatable("start_core.multiblock.frame.coolant_consumption",
                        FormattingUtil.formatNumbers(linked * 500)));
            }
        }
    }
}