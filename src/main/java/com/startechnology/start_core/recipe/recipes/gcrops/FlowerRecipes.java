package com.startechnology.start_core.recipe.recipes.gcrops;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.startechnology.start_core.item.components.StarTFruitBehaviour;
import com.startechnology.start_core.utils.StarTItemUtils;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ModList;

import java.util.function.Consumer;

import static com.startechnology.start_core.item.gcrops.StarTGCropItems.*;
import static com.startechnology.start_core.StarTCore.LOGGER;

public class FlowerRecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        gcropFruitGrowingRecipes(provider);
    }

    public static void gcropFruitGrowingRecipes(Consumer<FinishedRecipe> provider) {
        GTRecipeType CROP_GREENHOUSE_RECIPE_TYPE = GTRecipeTypes.get("crop_greenhouse");
        GTRecipeType HYDROPONIC_GARDEN_RECIPE_TYPE = GTRecipeTypes.get("hydroponic_garden");

        if (CROP_GREENHOUSE_RECIPE_TYPE == null) {
            LOGGER.error("""
                    Invalid KubeJS Scripts detected, You are missing elements from the Star Technology modpack!
                    Star Technology Core may not work as expected without the full modpack.
                    Failed to load recipe type "crop_greenhouse\"""");
            return;
        }

        if (HYDROPONIC_GARDEN_RECIPE_TYPE == null) {
            LOGGER.error("""
                    Invalid KubeJS Scripts detected, You are missing elements from the Star Technology modpack!
                    Star Technology Core may not work as expected without the full modpack.
                    Failed to load recipe type "hydroponic_garden\"""");
            return;
        }

        for (var flower : GCROP_FLOWERS) {
            StarTFruitBehaviour flowerBehaviour = StarTFruitBehaviour.getFruitBehaviour(flower.asStack());
            if (flowerBehaviour == null) return;

            int flowerTier = flowerBehaviour.getCropTier();
            String flowerName = flower.getId().getPath();
            Material flowerMaterial = flowerBehaviour.getCropMaterial();

            var fruit = GCROP_FRUITMAP.get(flowerMaterial);

            CROP_GREENHOUSE_RECIPE_TYPE.recipeBuilder(flowerName + "_blooming_no_fertilizer")
                    .inputItems(flower.asStack())
                    .inputFluids(GTMaterials.Water.getFluid(100))
                    .outputItems(fruit.asStack())
                    .EUtVHA(GTValues.MV + flowerTier)
                    .duration(600)
                    .circuitMeta(0)
                    .save(provider);

            CROP_GREENHOUSE_RECIPE_TYPE.recipeBuilder(flowerName + "_blooming_bone_meal")
                    .inputItems(flower.asStack())
                    .chancedInput(new ItemStack(Items.BONE_MEAL, 1), 7500, -500)
                    .inputFluids(GTMaterials.Water.getFluid(100))
                    .outputItems(fruit.asStack())
                    .EUtVHA(GTValues.MV + flowerTier)
                    .duration(600)
                    .circuitMeta(1)
                    .save(provider);

            if (ModList.get().isLoaded("thermal")) {
                CROP_GREENHOUSE_RECIPE_TYPE.recipeBuilder(flowerName + "_blooming_compost")
                        .inputItems(flower.asStack())
                        .chancedInput(new ItemStack(StarTItemUtils.getItem("thermal", "compost")), 7500, -500)
                        .inputFluids(GTMaterials.Water.getFluid(100))
                        .outputItems(fruit.asStack())
                        .EUtVHA(GTValues.MV + flowerTier)
                        .duration(600)
                        .circuitMeta(2)
                        .save(provider);
            }

            CROP_GREENHOUSE_RECIPE_TYPE.recipeBuilder(flowerName + "_blooming_fertilizer")
                    .inputItems(flower.asStack())
                    .chancedInput(new ItemStack(GTItems.FERTILIZER, 1), 7500, -500)
                    .inputFluids(GTMaterials.Water.getFluid(100))
                    .outputItems(fruit.asStack())
                    .EUtVHA(GTValues.MV + flowerTier)
                    .duration(600)
                    .circuitMeta(3)
                    .save(provider);

            // ===== Hydroponic Garden Recipes =====
            HYDROPONIC_GARDEN_RECIPE_TYPE.recipeBuilder(flowerName + "_blooming_npk")
                    .inputItems(flower.asStack())
                    .inputFluids(GTMaterials.Water.getFluid(500))
                    .outputItems(fruit.asStack())
                    .EUtVHA(GTValues.MV + flowerTier)
                    .duration(600)
                    .circuitMeta(0)
                    .save(provider);

            HYDROPONIC_GARDEN_RECIPE_TYPE.recipeBuilder(flowerName + "_blooming_nrf")
                    .inputItems(flower.asStack())
                    .inputFluids(GTMaterials.Water.getFluid(500),
                            GTMaterials.get("nutrient_rich_fertilizer_solution").getFluid(100))
                    .outputItems(fruit.asStack())
                    .EUtVHA(GTValues.MV + flowerTier)
                    .duration(300)
                    .circuitMeta(1)
                    .save(provider);

            HYDROPONIC_GARDEN_RECIPE_TYPE.recipeBuilder(flowerName + "_blooming_biostimulating")
                    .inputItems(flower.asStack())
                    .inputFluids(GTMaterials.Water.getFluid(500),
                            GTMaterials.get("biostimulating_mixture").getFluid(100))
                    .outputItems(fruit.asStack())
                    .EUtVHA(GTValues.MV + flowerTier)
                    .duration(150)
                    .circuitMeta(2)
                    .save(provider);
        }
    }
}
