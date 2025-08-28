package com.startechnology.start_core.machine.fusion;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.block.IFusionCasingType;
import com.gregtechceu.gtceu.api.block.IMachineBlock;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.common.block.FusionCasingBlock;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.machines.GCYMMachines;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.FusionReactorMachine;
import com.startechnology.start_core.block.fusion.StarTFusionBlocks;
import com.startechnology.start_core.machine.parallel.StarTParallelHatches;

import net.minecraft.world.level.block.Block;

public class AuxiliaryBoostedFusionReactor extends FusionReactorMachine {
    public AuxiliaryBoostedFusionReactor(IMachineBlockEntity holder, int tier) {
        super(holder, tier);
    }


    public static Block getCasingState(int tier) {
        return switch (tier) {
            case GTValues.UHV -> StarTFusionBlocks.AUXILIARY_BOOSTED_FUSION_CASING_MK1.get();
            case GTValues.UIV -> StarTFusionBlocks.AUXILIARY_BOOSTED_FUSION_CASING_MK2.get();
            default -> FusionReactorMachine.getCasingState(tier);
        };
    }

    public static int getParallelCount(int tier) {
        return switch (tier) {
            case GTValues.UHV -> 4;
            case GTValues.UIV -> 16;
            default -> 4;
        };
    }

    public static MachineDefinition getParallelHatch(int tier) {
        return switch (tier) {
            case GTValues.UHV -> StarTParallelHatches.ABSOLUTE_PARALLEL_HATCH[GTValues.UHV];
            case GTValues.UIV -> StarTParallelHatches.ABSOLUTE_PARALLEL_HATCH[GTValues.UIV];
            default -> StarTParallelHatches.ABSOLUTE_PARALLEL_HATCH[GTValues.UHV];
        };
    }

    public static Block getAuxiliaryCoilState(int tier) {
        return switch (tier) {
            case GTValues.UHV -> StarTFusionBlocks.AUXILIARY_FUSION_COIL_MK1.get();
            case GTValues.UIV -> StarTFusionBlocks.AUXILIARY_FUSION_COIL_MK2.get();
            default -> StarTFusionBlocks.AUXILIARY_FUSION_COIL_MK1.get();
        };
    }

    public static IFusionCasingType getCasingType(int tier) {
        return switch (tier) {
            case GTValues.UHV -> StarTFusionCasings.AUXILIARY_BOOSTED_FUSION_CASING_MK1;
            case GTValues.UIV -> StarTFusionCasings.AUXILIARY_BOOSTED_FUSION_CASING_MK2;
            default -> FusionReactorMachine.getCasingType(tier);
        };
    }
}
