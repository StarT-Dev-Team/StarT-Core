package com.startechnology.start_core.recipe.recipes;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.startechnology.start_core.materials.StarTTagPrefixes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.PACKER_RECIPES;

public class DustBlockRecipeHandler {

    public static final void init(Consumer<FinishedRecipe> provider) {
        generateDustBlockRecipes(provider);
    }

    public static void generateDustBlockRecipes(Consumer<FinishedRecipe> provider) {
        for (Material material : GTCEuAPI.materialManager.getRegisteredMaterials()) {
            if (material.hasFlag(MaterialFlags.NO_UNIFICATION) ||
                    material.hasFlag(MaterialFlags.DISABLE_MATERIAL_RECIPES)) {
                continue;
            }

            if (material.hasProperty(PropertyKey.DUST)) {

                var dustBlock = ChemicalHelper.get(StarTTagPrefixes.dustBlock, material);
                var dust_9x = ChemicalHelper.get(TagPrefix.dust, material, 9);

                // Compressing recipes
                VanillaRecipeHelper.addShapedRecipe(provider, String.format("%s_dust_to_dust_block", material.getName()), dustBlock,
                        "DDD",
                                "DDD",
                                "DDD",
                        'D', new MaterialEntry(TagPrefix.dust, material));

                PACKER_RECIPES.recipeBuilder(String.format("package_%s_dust_block", material.getName()))
                        .inputItems(dust_9x)
                        .circuitMeta(3)
                        .outputItems(dustBlock)
                        .save(provider);

                // Decompressing recipes
                VanillaRecipeHelper.addShapelessRecipe(provider, String.format("%s_dust_block_to_dust", material.getName()), dust_9x,
                new MaterialEntry(StarTTagPrefixes.dustBlock, material));

                PACKER_RECIPES.recipeBuilder(String.format("unpackage_%s_dust_block", material.getName()))
                        .inputItems(dustBlock)
                        .circuitMeta(1)
                        .outputItems(dust_9x)
                        .save(provider);
            }

        }
    }

}
