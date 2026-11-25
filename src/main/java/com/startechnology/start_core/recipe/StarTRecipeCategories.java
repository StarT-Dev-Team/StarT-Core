package com.startechnology.start_core.recipe;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.texture.UVSpriteTexture;
import com.gregtechceu.gtceu.common.data.GTRecipeCategories;

public class StarTRecipeCategories {

    public static final void init() {
        GTRecipeCategories.register("hellforge_heating", StarTRecipeTypes.HELL_FORGE_RECIPES)
                .setIcon(UVSpriteTexture.of(GuiTextures.PROGRESS_BAR_BOILER_FUEL.get(true), 0, 0.5f, 1, 0.5f));
    }
}
