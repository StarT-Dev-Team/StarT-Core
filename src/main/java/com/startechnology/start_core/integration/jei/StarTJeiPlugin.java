package com.startechnology.start_core.integration.jei;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.common.data.GTRecipeCategories;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.integration.jei.recipe.GTRecipeJEICategory;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.machine.bacteria.StarTBacteriaMachines;
import com.startechnology.start_core.machine.fusion.StarTFusionMachines;
import com.startechnology.start_core.machine.hellforge.StarTHellForgeMachines;
import com.startechnology.start_core.recipe.StarTRecipeCategories;
import com.startechnology.start_core.recipe.StarTRecipeTypes;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class StarTJeiPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return StarTCore.resourceLocation("jei_plugin");
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        if (GTCEu.Mods.isREILoaded() || GTCEu.Mods.isEMILoaded()) return;
        registration.addRecipeCatalyst(StarTBacteriaMachines.BACTERIAL_BREEDING_VAT.asStack(), 
            GTRecipeJEICategory.TYPES.apply(StarTRecipeTypes.BACTERIAL_BREEDING_VAT_RECIPES.getCategory())
        );

        registration.addRecipeCatalyst(StarTBacteriaMachines.BACTERIAL_HYDROCARBON_HARVESTER.asStack(), 
                GTRecipeJEICategory.TYPES.apply(StarTRecipeTypes.BACTERIAL_HYDROCARBON_HARVESTER_RECIPES.getCategory())
            );

        registration.addRecipeCatalyst(StarTBacteriaMachines.BACTERIAL_RUNIC_MUTATOR.asStack(), 
            GTRecipeJEICategory.TYPES.apply(StarTRecipeTypes.BACTERIAL_RUNIC_MUTATOR_RECIPES.getCategory())
        );

        registration.addRecipeCatalyst(StarTHellForgeMachines.HELL_FORGE.asStack(), 
            GTRecipeJEICategory.TYPES.apply(StarTRecipeTypes.HELL_FORGE_RECIPES.getCategory()),
            GTRecipeJEICategory.TYPES.apply(GTRecipeCategories.get("hellforge_heating"))
        );

        Arrays.asList(StarTFusionMachines.AUXILIARY_BOOSTED_FUSION_REACTOR).forEach(
            fusion_reactor -> {
                registration.addRecipeCatalyst(fusion_reactor.asStack(), 
                GTRecipeJEICategory.TYPES.apply(GTRecipeTypes.FUSION_RECIPES.getCategory()));
            }
        );
    }
    
}
