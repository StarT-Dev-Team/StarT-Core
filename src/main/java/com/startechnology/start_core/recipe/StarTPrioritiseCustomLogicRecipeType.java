package com.startechnology.start_core.recipe;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;

import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.lookup.RecipeIterator;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;

public class StarTPrioritiseCustomLogicRecipeType extends GTRecipeType {

    public StarTPrioritiseCustomLogicRecipeType(ResourceLocation registryName, String group,
            RecipeType<?>[] proxyRecipes) {
        super(registryName, group, proxyRecipes);
    }

    @Override
    public Iterator<GTRecipe> searchRecipe(IRecipeCapabilityHolder holder, Predicate<GTRecipe> canHandle) {
        if (!holder.hasCapabilityProxies()) return Collections.emptyIterator();

        for (ICustomRecipeLogic logic : this.getCustomRecipeLogicRunners()) {
            GTRecipe recipe = logic.createCustomRecipe(holder);
            if (recipe != null && canHandle.test(recipe)) return Collections.singleton(recipe).iterator();
        }

        RecipeIterator iterator = getLookup().getRecipeIterator(holder, canHandle);
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
