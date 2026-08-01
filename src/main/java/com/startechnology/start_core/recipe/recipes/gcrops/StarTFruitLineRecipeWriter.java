package com.startechnology.start_core.recipe.recipes.gcrops;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.startechnology.start_core.api.gcrop.StarTGCropItemType;
import com.startechnology.start_core.data.gcrops.StarTGCropData;
import com.startechnology.start_core.item.gcrops.StarTGCropItems;
import com.startechnology.start_core.utils.StarTItemUtils;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.startechnology.start_core.StarTCore.LOGGER;
import static com.startechnology.start_core.data.gcrops.StarTGCropData.gCropData;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.startechnology.start_core.item.gcrops.StarTGCropItems.GCROP_FRUITMAP;

public class StarTFruitLineRecipeWriter {

    public static void init(Consumer<FinishedRecipe> provider) {
        gCropFruitLineRecipes(provider);
    }

    public static void gCropFruitLineRecipes(Consumer<FinishedRecipe> provider) {
        for (StarTGCropData data : gCropData) {
            runRecipeGen(provider, data.getTier(), data.getId(), data.getMaterialType());
        }
    }

    private static Material getMaterial(String materialId) {
        return GTCEuAPI.materialManager.getMaterial(materialId);
    }

    private static List getFruitLineResult(String material, StarTGCropItemType materialType) {
        Material rawMaterial = GTMaterials.get(material);
        var result = new ArrayList<>();

        switch (materialType) {
            case LIQUID -> result.add(rawMaterial.getFluid());
            case DUST -> result.add(ChemicalHelper.get(dust, rawMaterial));
            case ORE -> result.add(ChemicalHelper.get(rawOre, rawMaterial));
            case GEM -> result.add(ChemicalHelper.get(gem, rawMaterial));
        }

        return result;
    }

    public static void runRecipeGen(Consumer<FinishedRecipe> provider, int tier, String id,
                                    StarTGCropItemType materialType) {
        id = (id.equals("sheldonite")) ? "cooperite" : id;
        var fruit = GCROP_FRUITMAP.get(GTMaterials.get(id));
        if (fruit == null) {
            LOGGER.debug("Error finding material with id: \"{}\"", id);
            return;
        }

        int EUtV = StarTGCropItems.tierVoltages.get(tier);

        if (tier == 0) {
            String dyeColor = id.substring(0, id.length() - 4);

            EXTRACTOR_RECIPES.recipeBuilder(String.format("%s_pigment_extraction", dyeColor))
                    .inputItems(fruit.asStack())
                    .outputItems(
                            ChemicalHelper.get(dust, getMaterial(String.format("start_core:%s_pigment", dyeColor)), 4))
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);

            FLUID_SOLIDFICATION_RECIPES.recipeBuilder(String.format("%s_dilution", id))
                    .inputItems(ChemicalHelper.get(dust, getMaterial(String.format("start_core:%s_pigment", dyeColor))))
                    .inputFluids(GTMaterials.Water.getFluid(100))
                    .outputItems(new ItemStack(StarTItemUtils.getItem(id)))
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);
        }
        if (tier == 1) {
            EXTRACTOR_RECIPES.recipeBuilder(String.format("%s_extract", id))
                    .inputItems(fruit.asStack())
                    .outputFluids(getMaterial(String.format("start_core:%s_extract", id)).getFluid(1000))
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);

            var resultRecipe = CENTRIFUGE_RECIPES.recipeBuilder(String.format("%s_extract_separation", id))
                    .inputFluids(getMaterial(String.format("start_core:%s_extract", id)).getFluid(1000))
                    .outputFluids(getMaterial("start_core:poor_mineral-rich_bio_waste").getFluid(1000))
                    .EUtVA(EUtV)
                    .duration(200);

            // This doesn't work :(
            var result = getFruitLineResult(id, materialType).get(0);
            if (result instanceof Item) resultRecipe.outputItems(new ItemStack((Item) result));
            if (result instanceof Fluid) resultRecipe.outputItems(new FluidStack((Fluid) result, 1000));

            resultRecipe.save(provider);
        }
    }
}
