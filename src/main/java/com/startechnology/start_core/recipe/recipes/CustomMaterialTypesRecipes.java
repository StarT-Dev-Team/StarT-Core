package com.startechnology.start_core.recipe.recipes;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.startechnology.start_core.materials.StarTTagPrefixes;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.FORMING_PRESS_RECIPES;
import static com.startechnology.start_core.recipe.StarTRecipeTypes.TITAN_FORGE_RECIPES;

public class CustomMaterialTypesRecipes {

    public static final void init(Consumer<FinishedRecipe> provider) {
        materialFlagRecipes(provider);
    }

    public static void materialFlagRecipes(Consumer<FinishedRecipe> provider) {

        for (Material material : GTCEuAPI.materialManager.getRegisteredMaterials()) {
            if (material.hasFlag(MaterialFlags.NO_UNIFICATION) ||
                    material.hasFlag(MaterialFlags.DISABLE_MATERIAL_RECIPES)) {
                continue;
            }

            if (material.hasFlag(MaterialFlags.GENERATE_FOIL)) {

                FORMING_PRESS_RECIPES.recipeBuilder(String.format("press_%s_foil_ream", material.getName()))
                        .notConsumable(GTItems.SHAPE_MOLD_CYLINDER)
                        .inputItems(TagPrefix.foil, material, 96)
                        .outputItems(StarTTagPrefixes.foilReam, material)
                        .duration((int)(material.getMass() * 128))
                        .EUt(GTValues.VA[GTValues.HV])
                        .save(provider);

                if (material.hasProperty(PropertyKey.INGOT)) {
                    TITAN_FORGE_RECIPES.recipeBuilder(String.format("forge_%s_foil_ream", material.getName()))
                            .inputItems(TagPrefix.ingot, material, 16)
                            .outputItems(StarTTagPrefixes.foilReam, material)
                            .duration((int)(material.getMass() * 16))
                            .EUt(GTValues.VHA[GTValues.IV])
                            .circuitMeta(10)
                            .save(provider);
                }

            }

            if (material.hasFlag(MaterialFlags.GENERATE_FINE_WIRE)) {

                FORMING_PRESS_RECIPES.recipeBuilder(String.format("press_%s_wire_spool", material.getName()))
                        .notConsumable(GTItems.SHAPE_MOLD_CYLINDER)
                        .inputItems(TagPrefix.wireFine, material, 96)
                        .outputItems(StarTTagPrefixes.wireSpool, material)
                        .duration((int)(material.getMass() * 64))
                        .EUt(GTValues.VA[GTValues.HV])
                        .save(provider);

                if (material.hasProperty(PropertyKey.INGOT)) {
                    TITAN_FORGE_RECIPES.recipeBuilder(String.format("forge_%s_wire_spool", material.getName()))
                            .inputItems(TagPrefix.ingot, material, 8)
                            .outputItems(StarTTagPrefixes.wireSpool, material)
                            .duration((int) (material.getMass() * 8))
                            .EUt(GTValues.VHA[GTValues.IV])
                            .circuitMeta(3)
                            .save(provider);
                }

            }

        }

    }
}
