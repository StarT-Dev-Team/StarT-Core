package com.startechnology.start_core.recipe.recipes;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.startechnology.start_core.item.StarTItems;
import com.startechnology.start_core.machine.black_hole.BlackHoleBalance;
import com.startechnology.start_core.machine.black_hole.BlackHoleSeeds;

import net.minecraft.data.recipes.FinishedRecipe;

import java.util.Map;
import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.L;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.dust;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.ingot;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Neutronium;
import static com.startechnology.start_core.recipe.StarTRecipeTypes.BLACK_HOLE_FEEDING_RECIPES;
import static com.startechnology.start_core.recipe.StarTRecipeTypes.BLACK_HOLE_IGNITION_RECIPES;

public class BlackHoleRecipes {

    private static final Map<Material, Double> EXOTIC_MULTIPLIERS = Map.of(Neutronium, 10.0);

    public static void init(Consumer<FinishedRecipe> provider) {
        for (var profile : BlackHoleSeeds.all()) {
            BLACK_HOLE_IGNITION_RECIPES.recipeBuilder(profile.id())
                    .inputItems(StarTItems.SINGULARITY_SEEDS.get(profile.id()).asStack())
                    .addData(BlackHoleSeeds.RECIPE_DATA_SEED, profile.id())
                    .duration(BlackHoleBalance.IGNITION_TICKS)
                    .save(provider);
        }

        for (var material : GTCEuAPI.materialManager.getRegisteredMaterials()) {
            if (material.getMass() <= 0) continue;
            float mass = (float) (material.getMass() * BlackHoleBalance.MATTER_EFFICIENCY *
                    EXOTIC_MULTIPLIERS.getOrDefault(material, 1.0));
            var id = material.getModid() + "_" + material.getName();

            if (material.hasProperty(PropertyKey.DUST)) feedItem(provider, id, material, dust, mass);
            if (material.hasProperty(PropertyKey.INGOT)) feedItem(provider, id, material, ingot, mass);
            if (material.hasProperty(PropertyKey.FLUID) && material.getFluid() != null) {
                int amount = material.hasProperty(PropertyKey.INGOT) ? L : 1000;
                BLACK_HOLE_FEEDING_RECIPES.recipeBuilder(id + "_fluid")
                        .inputFluids(material.getFluid(amount))
                        .addData(BlackHoleSeeds.RECIPE_DATA_MASS, mass)
                        .duration(20)
                        .save(provider);
            }
        }
    }

    private static void feedItem(Consumer<FinishedRecipe> provider, String id, Material material, TagPrefix prefix,
                                 float mass) {
        if (ChemicalHelper.get(prefix, material).isEmpty()) return;
        BLACK_HOLE_FEEDING_RECIPES.recipeBuilder(id + "_" + prefix.name)
                .inputItems(prefix, material)
                .addData(BlackHoleSeeds.RECIPE_DATA_MASS, mass)
                .duration(20)
                .save(provider);
    }
}
