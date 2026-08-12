package com.startechnology.start_core.integration.jei;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.common.data.GTRecipeCategories;
import com.gregtechceu.gtceu.integration.jei.orevein.GTBedrockFluidInfoCategory;
import com.gregtechceu.gtceu.integration.jei.orevein.GTBedrockOreInfoCategory;
import com.gregtechceu.gtceu.integration.jei.recipe.GTRecipeJEICategory;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.machine.bacteria.StarTBacteriaMachines;
import com.startechnology.start_core.machine.drills.StarTDrillingRigs;
import com.startechnology.start_core.machine.hellforge.StarTHellForgeMachines;
import com.startechnology.start_core.machine.solar.StarTSolarMachines;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.resources.ResourceLocation;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@JeiPlugin
public class StarTJeiPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return StarTCore.resourceLocation("jei_plugin");
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        if (!GTCEu.Mods.isEMILoaded()) {
            CBMicroblockRecipes.registerCategoryExtension(registration);
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (!GTCEu.Mods.isEMILoaded()) {
            CBMicroblockRecipes.registerRecipes(registration);
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        if (GTCEu.Mods.isREILoaded() || GTCEu.Mods.isEMILoaded()) return;

        registration.addRecipeCatalyst(StarTBacteriaMachines.BACTERIAL_BREEDING_VAT.asStack(),
                GTRecipeJEICategory.TYPES.apply(StarTRecipeTypes.BACTERIAL_BREEDING_VAT_RECIPES.getCategory()));

        registration.addRecipeCatalyst(StarTBacteriaMachines.BACTERIAL_HYDROCARBON_HARVESTER.asStack(),
                GTRecipeJEICategory.TYPES
                        .apply(StarTRecipeTypes.BACTERIAL_HYDROCARBON_HARVESTER_RECIPES.getCategory()));

        registration.addRecipeCatalyst(StarTBacteriaMachines.BACTERIAL_RUNIC_MUTATOR.asStack(),
                GTRecipeJEICategory.TYPES.apply(StarTRecipeTypes.BACTERIAL_RUNIC_MUTATOR_RECIPES.getCategory()));

        registration.addRecipeCatalyst(StarTHellForgeMachines.HELL_FORGE.asStack(),
                GTRecipeJEICategory.TYPES.apply(StarTRecipeTypes.HELL_FORGE_RECIPES.getCategory()),
                GTRecipeJEICategory.TYPES.apply(GTRecipeCategories.get("hellforge_heating")));

        registration.addRecipeCatalyst(StarTHellForgeMachines.BOOSTED_HELL_FORGE.asStack(),
                GTRecipeJEICategory.TYPES.apply(StarTRecipeTypes.HELL_FORGE_RECIPES.getCategory()),
                GTRecipeJEICategory.TYPES.apply(GTRecipeCategories.get("hellforge_heating")));

        for (MultiblockMachineDefinition multiBlockDefinition : StarTDrillingRigs.FLUID_DRILLING_RIGS) {
            if (multiBlockDefinition != null) {
                registration.addRecipeCatalyst(multiBlockDefinition.asStack(), GTBedrockFluidInfoCategory.RECIPE_TYPE);
                registration.addRecipeCatalyst(multiBlockDefinition.asStack(), GTBedrockOreInfoCategory.RECIPE_TYPE);
            }
        }

        registration.addRecipeCatalyst(StarTSolarMachines.SOLAR_PANEL_EV.asStack(),
                GTRecipeJEICategory.TYPES.apply(StarTRecipeTypes.SOLAR_PANEL_REPLACEMENT.getCategory()));

        registration.addRecipeCatalyst(StarTSolarMachines.SOLAR_PANEL_IV.asStack(),
                GTRecipeJEICategory.TYPES.apply(StarTRecipeTypes.SOLAR_PANEL_REPLACEMENT.getCategory()));

        registration.addRecipeCatalyst(StarTSolarMachines.SOLAR_PANEL_LUV.asStack(),
                GTRecipeJEICategory.TYPES.apply(StarTRecipeTypes.SOLAR_PANEL_REPLACEMENT.getCategory()));

        registration.addRecipeCatalyst(StarTSolarMachines.SOLAR_ARRAY_UV.asStack(),
                GTRecipeJEICategory.TYPES.apply(StarTRecipeTypes.SOLAR_PANEL_REPLACEMENT.getCategory()));

        registration.addRecipeCatalyst(StarTSolarMachines.SOLAR_ARRAY_UHV.asStack(),
                GTRecipeJEICategory.TYPES.apply(StarTRecipeTypes.SOLAR_PANEL_REPLACEMENT.getCategory()));
            
    }
}
