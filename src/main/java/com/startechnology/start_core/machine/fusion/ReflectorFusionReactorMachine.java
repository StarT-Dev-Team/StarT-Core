package com.startechnology.start_core.machine.fusion;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.block.IFusionCasingType;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.common.block.FusionCasingBlock;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.FusionReactorMachine;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.startechnology.start_core.api.reflector.FusionReflectorType;
import com.startechnology.start_core.block.fusion.StarTFusionBlocks;
import lombok.Getter;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.common.data.GTBlocks.*;

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
        var maxVoltage = Math.min(reactor.getOverclockMaxVoltage(), GTValues.V[reactor.getTier() + reflectorDiff]);

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
        var type = getMultiblockState().getMatchContext().get("ReflectorType");
        if (type instanceof FusionReflectorType reflectorType) {
            this.reflectorType = reflectorType;
        }
    }

    public static @NotNull Block getCasingState(int tier) {
        return switch (tier) {
            case LuV -> FUSION_CASING.get();
            case ZPM -> FUSION_CASING_MK2.get();
            case UV -> FUSION_CASING_MK3.get();
            default -> StarTFusionBlocks.FUSION_CASING_MK4.get();
        };
    }

    public static @NotNull Block getCoilState(int tier) {
        return switch (tier) {
            case LuV -> SUPERCONDUCTING_COIL.get();
            case ZPM, UV -> FUSION_COIL.get();
            default -> StarTFusionBlocks.ADVANCED_FUSION_COIL.get();
        };
    }

    public static IFusionCasingType getCasingType(int tier) {
        return switch (tier) {
            case ZPM -> FusionCasingBlock.CasingType.FUSION_CASING_MK2;
            case UV -> FusionCasingBlock.CasingType.FUSION_CASING_MK3;
            case UEV -> StarTFusionCasings.FUSION_CASING_MK4;
            default -> FusionCasingBlock.CasingType.FUSION_CASING;
        };
    }

}
