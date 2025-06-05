package com.startechnology.start_core.recipe;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.recipe.category.GTRecipeCategory;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.data.GTRecipeCategories;
import com.tterrag.registrate.util.entry.ItemEntry;

public class StarTRecipeCategories {
    public static final void init() {
        GTRecipeCategories.register("hellforge_heating", StarTRecipeTypes.HELL_FORGE_RECIPES)
            .setIcon(GuiTextures.PROGRESS_BAR_BOILER_FUEL.get(true).getSubTexture(0, 0.5, 1, 0.5));
    }
}
