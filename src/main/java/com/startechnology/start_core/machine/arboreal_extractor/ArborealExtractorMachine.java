package com.startechnology.start_core.machine.arboreal_extractor;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.common.machine.multiblock.primitive.PrimitiveFancyUIWorkableMachine;
import com.startechnology.start_core.block.arboreal_extractor.TreeType;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ArborealExtractorMachine extends PrimitiveFancyUIWorkableMachine {

    private TreeType treeType;

    public ArborealExtractorMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        var leavesType = getMultiblockState().getMatchContext().get(StarTArborealPredicates.CONTEXT_KEY_TREE_TYPE);
        if (leavesType instanceof TreeType type) {
            this.treeType = type;
        }
    }

    public @NotNull String getTreeType() {
        return this.treeType.getName();
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        this.treeType = null;
    }

    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (!(machine instanceof ArborealExtractorMachine arborealExtractorMachine)) {
            return RecipeModifier.nullWrongType(ArborealExtractorMachine.class, machine);
        }

        var availableType = arborealExtractorMachine.getTreeType();
        var recipeType = recipe.data.getString("treeVariant");

        if (!recipeType.equals(availableType)) {
            return ModifierFunction.cancel(Component.translatable("start_core.recipe_modifier.wrong_tree_type"), false);
        }

        return ModifierFunction.IDENTITY;
    }
}
