package com.startechnology.start_core.recipe;

import java.util.Collections;
import java.util.Iterator;

import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;

public class StarTPrioritiseCustomLogicRecipeType extends GTRecipeType {

    public StarTPrioritiseCustomLogicRecipeType(ResourceLocation registryName, String group,
            RecipeType<?>[] proxyRecipes) {
        super(registryName, group, proxyRecipes);
    }

    @Override
    public Iterator<GTRecipe> searchRecipe(IRecipeCapabilityHolder holder) {
        if (!holder.hasProxies()) return null;

        // Prioritise custom recipe logic, then the default recipe iterator.
        for (ICustomRecipeLogic logic : this.getCustomRecipeLogicRunners()) {
            GTRecipe recipe = logic.createCustomRecipe(holder);
            if (recipe != null) return Collections.singleton(recipe).iterator();
        }
        
        var iterator = getLookup().getRecipeIterator(holder, recipe -> !recipe.isFuel && recipe.matchRecipe(holder).isSuccess() && recipe.matchTickRecipe(holder).isSuccess());
        boolean any = false;
        while (iterator.hasNext()) {
            GTRecipe recipe = iterator.next();
            if (recipe == null) continue;
            any = true;
            break;
        }
        if (any) {
            iterator.reset();
            return iterator;
        }
        return Collections.emptyIterator();
    }
    
}
