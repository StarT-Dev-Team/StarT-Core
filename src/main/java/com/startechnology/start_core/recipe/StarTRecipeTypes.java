package com.startechnology.start_core.recipe;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.block.ICoilType;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.recipe.GTRecipeSerializer;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.api.sound.SoundEntry;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
import com.startechnology.start_core.machine.hellforge.StarTHellForgeMachine;
import com.startechnology.start_core.recipe.logic.BacteriaVatLogic;
import com.startechnology.start_core.recipe.logic.BacterialDormantAwakeningLogic;
import com.startechnology.start_core.recipe.logic.BacterialHydrocarbonHarvesterLogic;
import com.startechnology.start_core.recipe.logic.BacterialRunicMutatorLogic;
import com.startechnology.start_core.recipe.logic.HellForgeHeatingLogic;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeType;


public class StarTRecipeTypes {

    public final static GTRecipeType BLAST_FURNACE_RECIPES = GTRecipeTypes.register("electric_vanilla_blast_furnace", GTRecipeTypes.ELECTRIC, RecipeType.BLASTING)
        .setMaxIOSize(1, 1, 0, 0).setEUIO(IO.IN)
        .prepareBuilder(recipeBuilder -> recipeBuilder.EUt(4))
        .setSlotOverlay(false, false, GuiTextures.FURNACE_OVERLAY_1)
        .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
        .setSteamProgressBar(GuiTextures.PROGRESS_BAR_ARROW_STEAM, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
        .setSound(GTSoundEntries.FURNACE);

    public final static GTRecipeType SMOKER_RECIPES = GTRecipeTypes.register("electric_smoker", GTRecipeTypes.ELECTRIC, RecipeType.SMOKING)
        .setMaxIOSize(1, 1, 0, 0).setEUIO(IO.IN)
        .prepareBuilder(recipeBuilder -> recipeBuilder.EUt(4))
        .setSlotOverlay(false, false, GuiTextures.FURNACE_OVERLAY_1)
        .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
        .setSteamProgressBar(GuiTextures.PROGRESS_BAR_ARROW_STEAM, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
        .setSound(GTSoundEntries.FURNACE);

    public static final GTRecipeType BACTERIAL_BREEDING_VAT_RECIPES = GTRecipeTypes.register("bacterial_breeding_vat", GTRecipeTypes.MULTIBLOCK)
        .setMaxIOSize(1, 2, 2, 0)
        .setEUIO(IO.IN)
        .addCustomRecipeLogic(new BacteriaVatLogic())
        .setProgressBar(GuiTextures.PROGRESS_BAR_FUSION, ProgressTexture.FillDirection.LEFT_TO_RIGHT);
    
    public static final GTRecipeType BACTERIAL_RUNIC_MUTATOR_RECIPES = GTRecipeTypes.register("bacterial_runic_mutator", GTRecipeTypes.MULTIBLOCK)
        .setMaxIOSize(2, 1, 2, 0)
        .setEUIO(IO.IN)
        .addCustomRecipeLogic(new BacterialRunicMutatorLogic())
        .addCustomRecipeLogic(new BacterialDormantAwakeningLogic())
        .setProgressBar(GuiTextures.PROGRESS_BAR_BATH, ProgressTexture.FillDirection.LEFT_TO_RIGHT);

    public static final GTRecipeType BACTERIAL_HYDROCARBON_HARVESTER_RECIPES = GTRecipeTypes.register("bacterial_hydrocarbon_harvester", GTRecipeTypes.MULTIBLOCK)
        .setMaxIOSize(2, 0, 2, 3)
        .setEUIO(IO.IN)
        .addCustomRecipeLogic(new BacterialHydrocarbonHarvesterLogic())
        .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW_MULTIPLE, ProgressTexture.FillDirection.LEFT_TO_RIGHT);

    public static GTRecipeType registerStarTPrioritiseCustomLogic(String name, String group, RecipeType<?>... proxyRecipes) {
        var recipeType = new StarTPrioritiseCustomLogicRecipeType(GTCEu.id(name), group, proxyRecipes);
        GTRegistries.register(BuiltInRegistries.RECIPE_TYPE, recipeType.registryName, recipeType);
        GTRegistries.register(BuiltInRegistries.RECIPE_SERIALIZER, recipeType.registryName, new GTRecipeSerializer());
        GTRegistries.RECIPE_TYPES.register(recipeType.registryName, recipeType);
        return recipeType;
    }

    public static final GTRecipeType HELL_FORGE_RECIPES = registerStarTPrioritiseCustomLogic("hellforge", GTRecipeTypes.MULTIBLOCK)
        .setMaxIOSize(0, 0, 8, 1)
        .setEUIO(IO.IN)
        .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW_MULTIPLE, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
        .addCustomRecipeLogic(new HellForgeHeatingLogic())
        .addDataInfo(data -> {
            int temp = data.getInt("ebf_temp");

            if (temp > 0) {
                return LocalizationUtils.format("start_core.recipe.temperature", FormattingUtil.formatNumbers(temp));
            }

            return "";
        })
        .addDataInfo(data -> {
            int temp = data.getInt("ebf_temp");
            Material requiredFluid = StarTHellForgeMachine.getHellforgeHeatingLiquid(temp);

            if (temp > 0) {
                return Component.translatable("start_core.recipe.heating_fluid", requiredFluid.getLocalizedName().getString()).getString();
            }

            return "";
        }).setUiBuilder((recipe, widgetGroup) -> {
        })
        .setSound(GTSoundEntries.FURNACE);

    public static final GTRecipeType ABYSSAL_HARVESTER_RECIPES = GTRecipeTypes.register("abyssal_harvester", GTRecipeTypes.MULTIBLOCK)
        .setMaxIOSize(1, 1, 0, 1)
        .setEUIO(IO.IN)
        .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW_MULTIPLE, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
        .addDataInfo(data -> {
            int min_entropy = data.getInt("min_entropy");

            if (min_entropy > 0) {
                return LocalizationUtils.format("start_core.recipe.min_entropy", min_entropy);
            }

            return "";
        })
        .addDataInfo(data -> {
            int max_entropy = data.getInt("max_entropy");

            if (max_entropy > 0) {
                return LocalizationUtils.format("start_core.recipe.max_entropy", max_entropy);
            }

            return "";
        })
        .setSound(GTSoundEntries.FURNACE);

    public static final void init() {
        
    }
}
