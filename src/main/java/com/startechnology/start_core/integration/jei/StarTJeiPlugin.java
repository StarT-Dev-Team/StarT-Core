package com.startechnology.start_core.integration.jei;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.common.data.GTRecipeCategories;
import com.gregtechceu.gtceu.integration.jei.orevein.GTBedrockFluidInfoCategory;
import com.gregtechceu.gtceu.integration.jei.orevein.GTBedrockOreInfoCategory;
import com.gregtechceu.gtceu.integration.jei.recipe.GTRecipeJEICategory;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.gcrop.StarTGCropGenome;
import com.startechnology.start_core.api.gcrop.StarTGCropTrait;
import com.startechnology.start_core.item.gcrops.StarTGCropItems;
import com.startechnology.start_core.machine.bacteria.StarTBacteriaMachines;
import com.startechnology.start_core.machine.gcrop.*;
import com.startechnology.start_core.machine.drills.StarTDrillingRigs;
import com.startechnology.start_core.machine.hellforge.StarTHellForgeMachines;
import com.startechnology.start_core.machine.solar.StarTSolarMachines;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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
            registerTraitDescriptions(registration);
        }
    }

    private void registerTraitDescriptions(IRecipeRegistration registration) {
        for (StarTGCropTrait trait : StarTGCropTrait.TRAITS.values()) {
            if (trait == null) continue;
            String traitId = trait.id().toLowerCase();
            String traitType = trait.genomeType().name().toLowerCase();
            int traitTier = trait.tier();

            Component name = Component.translatable(String.format("behaviour.start_core.trait.%s.name", traitId));
            Component symbol = Component.literal(StarTGCropGenome.getPrettyTrait(
                    Component.translatable(String.format("behaviour.start_core.trait.%s.symbol", traitId)).getString(),
                    traitTier));
            Component type = Component.translatable(String.format("behaviour.start_core.trait.type.%s", traitType));

            Component headerLine = Component.translatable("behaviour.start_core.trait.info.header", name, symbol);
            Component typeLine = Component.translatable("behaviour.start_core.trait.info.type", traitTier, type);

            if (StarTGCropTrait.traitHasDescription.contains(traitId)) {
                Component effectLine = Component.translatable("behaviour.start_core.trait.info.effects");
                Component description = Component
                        .translatable(String.format("behaviour.start_core.trait.%s.description", traitId));
                addDescription(registration, StarTGCropItems.DNA_STRAND.asItem(), headerLine, typeLine, effectLine,
                        description);
            } else {
                addDescription(registration, StarTGCropItems.DNA_STRAND.asItem(), headerLine, typeLine);
            }
        }
    }

    private void addDescription(IRecipeRegistration registration, Item item, Component... descriptionComponents) {
        registration.addIngredientInfo(new ItemStack(item), VanillaTypes.ITEM_STACK, descriptionComponents);
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

        registration.addRecipeCatalyst(GCropMachines.GCROP_MUTATION_STATION.asStack(),
                GTRecipeJEICategory.TYPES.apply(StarTRecipeTypes.GCROP_MUTATOR_RECIPES.getCategory()));
      
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
