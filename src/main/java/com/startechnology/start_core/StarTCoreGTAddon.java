package com.startechnology.start_core;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.startechnology.start_core.block.StarTBlocks;
import com.startechnology.start_core.item.StarTDreamLinkCovers;
import com.startechnology.start_core.item.StarTItems;
import com.startechnology.start_core.recipe.StarTRecipes;

import net.minecraft.data.recipes.FinishedRecipe;

@GTAddon
public class StarTCoreGTAddon implements IGTAddon {
    @Override
    public GTRegistrate getRegistrate() {
        return StarTCore.START_REGISTRATE;
    }

    @Override
    public void initializeAddon() {
        StarTBlocks.init();
        StarTItems.init();
    }

    @Override
    public String addonModId() {
        return StarTCore.MOD_ID;
    }

    @Override
    public void registerTagPrefixes() {
        //CustomTagPrefixes.init();
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        StarTRecipes.init(provider);
    }

    @Override
    public void registerCovers() {
        StarTDreamLinkCovers.init();
    }
    
    // If you have custom ingredient types, uncomment this & change to match your capability.
    // KubeJS WILL REMOVE YOUR RECIPES IF THESE ARE NOT REGISTERED.
    /*
    public static final ContentJS<Double> PRESSURE_IN = new ContentJS<>(NumberComponent.ANY_DOUBLE, GregitasRecipeCapabilities.PRESSURE, false);
    public static final ContentJS<Double> PRESSURE_OUT = new ContentJS<>(NumberComponent.ANY_DOUBLE, GregitasRecipeCapabilities.PRESSURE, true);

    @Override
    public void registerRecipeKeys(KJSRecipeKeyEvent event) {
        event.registerKey(CustomRecipeCapabilities.PRESSURE, Pair.of(PRESSURE_IN, PRESSURE_OUT));
    }
    */
}
