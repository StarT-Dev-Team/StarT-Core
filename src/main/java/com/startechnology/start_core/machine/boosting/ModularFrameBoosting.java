package com.startechnology.start_core.machine.boosting;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.startechnology.start_core.machine.modular.StarTModularConduitHatchPartMachine;
import com.startechnology.start_core.machine.modular.StarTModularControllerMachine;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ModularFrameBoosting extends StarTModularControllerMachine {

    private long netInLastSec = 0L;
    private long netOutLastSec = 0L;
    private long inputPerSec = 0L;
    private long outputPerSec = 0L;
    private static final Material DISTILLED_WATER = GTMaterials.get("distilled_water");
    private static final Material DEIONIZED_WATER = GTMaterials.get("deionized_water");
    private Material activeCoolant = null;

    private List<StarTModularConduitHatchPartMachine> trackedConduits = new ArrayList<>();

    public ModularFrameBoosting(IMachineBlockEntity holder, ResourceLocation... supportedMultiblockIds) {
        super(holder, supportedMultiblockIds);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        trackedConduits = new ArrayList<>();
        for (IMultiPart part : getParts()) {
            if (part instanceof StarTModularConduitHatchPartMachine conduit && !conduit.isTerminal()) {
                trackedConduits.add(conduit);
            }
        }
    }

    @Override
    public void onStructureInvalid() {
        trackedConduits = new ArrayList<>();
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
        super.transferEnergy();
        if (getLevel().isClientSide) return;

        if (getOffsetTimer() % 60L == 0L) {
            this.inputPerSec = this.netInLastSec;
            this.outputPerSec = this.netOutLastSec;
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

        long energyStored = inputHatches.getEnergyStored();
        if (energyStored <= 0) return false;

        if (hasDeionizedWater()) {
            activeCoolant = DEIONIZED_WATER;
        } else if (hasDistilledWater()) {
            activeCoolant = DISTILLED_WATER;
        } else {
            activeCoolant = null;
        }

        double boost = getFrameBoost();
        long boostedEnergy = (long)(energyStored * boost);
        long totalEnergyTransferred = outputConduits.changeEnergy(boostedEnergy);

        if (totalEnergyTransferred > 0) {
            inputHatches.removeEnergy(totalEnergyTransferred);
            this.netOutLastSec = totalEnergyTransferred;

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

            long avgEuIn = (long)(this.outputPerSec / 60d / boost);
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