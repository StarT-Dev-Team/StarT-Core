package com.startechnology.start_core.machine.fusion;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.block.IFusionCasingType;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.common.block.FusionCasingBlock;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.FusionReactorMachine;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.startechnology.start_core.api.reflector.FusionReflectorType;
import com.startechnology.start_core.block.fusion.StarTFusionBlocks;
import com.startechnology.start_core.machine.StarTMachineUtils;
import com.startechnology.start_core.machine.parallel.StarTParallelHatches;
import lombok.Getter;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ReflectorFusionReactorMachine extends FusionReactorMachine {

    private int tier;

    @Getter
    private FusionReflectorType reflectorType = null;

    public ReflectorFusionReactorMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier);
        this.tier = tier;
    }

    public long getOverclockMaxVoltage() {
        if (inputEnergyContainers == null) return 0;
        long highestVoltage = inputEnergyContainers.getHighestInputVoltage();
        if (inputEnergyContainers.getNumHighestInputContainers() > 1) {
            int tier = GTUtil.getTierByVoltage(highestVoltage);
            return GTValues.V[Math.min(tier + 1, GTValues.MAX)];
        } else {
            return highestVoltage;
        }
    }

    public static @NotNull ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof ReflectorFusionReactorMachine reactor)) {
            return RecipeModifier.nullWrongType(FusionReactorMachine.class, machine);
        }

        // reflector logic

        var reflectorType = reactor.getReflectorType();
        if (reflectorType == null) return ModifierFunction.NULL;

        var recipeReflectorTier = 0;
        if (recipe.data.contains("reflector_tier")) {
            recipeReflectorTier = recipe.data.getInt("reflector_tier");
        } else return ModifierFunction.NULL;

        if (recipeReflectorTier > reflectorType.getTier()) return ModifierFunction.NULL;

        var reflectorDiff = reflectorType.getTier() - recipeReflectorTier;
        var maxVoltage = GTValues.VEX[reactor.getTier() + reflectorDiff];

        // normal fusion logic

        if (RecipeHelper.getRecipeEUtTier(recipe) > reactor.getTier() ||
            !recipe.data.contains("eu_to_start") ||
            recipe.data.getLong("eu_to_start") > reactor.energyContainer.getEnergyCapacity()) {
            return ModifierFunction.NULL;
        }

        long heatDiff = recipe.data.getLong("eu_to_start") - reactor.heat;

        // if the stored heat is >= required energy, recipe is okay to run
        if (heatDiff <= 0) {
            return FUSION_OC.getModifier(machine, recipe, maxVoltage, false);
        }
        // if the remaining energy needed is more than stored, do not run
        if (reactor.energyContainer.getEnergyStored() < heatDiff) return ModifierFunction.NULL;

        // remove the energy needed
        reactor.energyContainer.removeEnergy(heatDiff);
        // increase the stored heat
        reactor.heat += heatDiff;
        reactor.updatePreHeatSubscription();

        return FUSION_OC.getModifier(machine, recipe, maxVoltage, false);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        var typeO = getMultiblockState().getMatchContext().get("ReflectorType");
        if (typeO instanceof FusionReflectorType type) {
            reflectorType = type;
        }
    }

    public static boolean isAuxReactor(int tier) {
        return switch (tier) {
            case GTValues.LuV, GTValues.ZPM, GTValues.UV, GTValues.UEV -> false;
            default -> true;
        };
    }

    public static @NotNull Block getCoilState(int tier) {
        return switch (tier) {
            case GTValues.LuV -> GTBlocks.SUPERCONDUCTING_COIL.get();
            case GTValues.ZPM, GTValues.UV, GTValues.UHV -> GTBlocks.FUSION_COIL.get();
            default -> StarTFusionBlocks.ADVANCED_FUSION_COIL.get();
        };
    }

    public static MachineDefinition getParallelHatch(int tier) {
        return switch (tier) {
            case GTValues.UHV -> StarTParallelHatches.ABSOLUTE_PARALLEL_HATCH[GTValues.UHV];
            case GTValues.UIV -> StarTParallelHatches.ABSOLUTE_PARALLEL_HATCH[GTValues.UIV];
            default -> null;
        };
    }

    public static @NotNull Block getFusionGlass(int tier) {
        return switch (tier) {
            case GTValues.LuV, GTValues.ZPM -> GTBlocks.FUSION_GLASS.get();
            case GTValues.UV, GTValues.UHV -> StarTMachineUtils.getKjsBlock("reinforced_fusion_glass");
            default -> StarTMachineUtils.getKjsBlock("draco_resilient_fusion_glass");
        };
    }

    public static Block getAuxiliaryCoilState(int tier) {
        return switch (tier) {
            case GTValues.UHV -> StarTFusionBlocks.AUXILIARY_FUSION_COIL_MK1.get();
            case GTValues.UIV -> StarTFusionBlocks.AUXILIARY_FUSION_COIL_MK2.get();
            default -> null;
        };
    }

    public static @NotNull Block getCasingState(int tier) {
        return switch (tier) {
            case GTValues.ZPM -> GTBlocks.FUSION_CASING_MK2.get();
            case GTValues.UV -> GTBlocks.FUSION_CASING_MK3.get();
            case GTValues.UHV -> StarTFusionBlocks.AUXILIARY_BOOSTED_FUSION_CASING_MK1.get();
            case GTValues.UEV -> StarTFusionBlocks.FUSION_CASING_MK4.get();
            case GTValues.UIV -> StarTFusionBlocks.AUXILIARY_BOOSTED_FUSION_CASING_MK2.get();
            default -> GTBlocks.FUSION_CASING.get();
        };
    }

    public static @NotNull IFusionCasingType getCasingType(int tier) {
        return switch (tier) {
            case GTValues.ZPM -> FusionCasingBlock.CasingType.FUSION_CASING_MK2;
            case GTValues.UV -> FusionCasingBlock.CasingType.FUSION_CASING_MK3;
            case GTValues.UHV -> StarTFusionCasings.AUXILIARY_BOOSTED_FUSION_CASING_MK1;
            case GTValues.UEV -> StarTFusionCasings.FUSION_CASING_MK4;
            case GTValues.UIV -> StarTFusionCasings.AUXILIARY_BOOSTED_FUSION_CASING_MK2;
            default -> FusionCasingBlock.CasingType.FUSION_CASING;
        };
    }

    public static String getControllerName(int tier) {
        return switch (tier) {
            case GTValues.LuV -> "Fusion Reactor MK I";
            case GTValues.ZPM -> "Fusion Reactor MK II";
            case GTValues.UV -> "Fusion Reactor MK III";
            case GTValues.UHV -> "Auxiliary Boosted Fusion Reactor MK I";
            case GTValues.UEV -> "Fusion Reactor MK IV";
            case GTValues.UIV -> "Auxiliary Boosted Fusion Reactor MK II";
            default -> "Fusion Reactor";
        };
    }

}
