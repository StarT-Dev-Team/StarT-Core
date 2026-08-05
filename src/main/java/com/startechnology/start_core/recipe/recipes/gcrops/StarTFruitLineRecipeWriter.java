package com.startechnology.start_core.recipe.recipes.gcrops;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.startechnology.start_core.api.gcrop.StarTGCropItemType;
import com.startechnology.start_core.data.gcrops.StarTGCropData;
import com.startechnology.start_core.item.gcrops.StarTGCropItems;
import com.startechnology.start_core.utils.StarTItemUtils;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.startechnology.start_core.StarTCore.LOGGER;
import static com.startechnology.start_core.data.gcrops.StarTGCropData.gCropData;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.startechnology.start_core.item.gcrops.StarTFruitItems.*;
import static com.startechnology.start_core.item.gcrops.StarTGCropItems.GCROP_FRUITMAP;
import static com.startechnology.start_core.recipe.StarTRecipeTypes.*;
import static com.startechnology.start_core.utils.StarTMaterialUtils.getMaterial;

public class StarTFruitLineRecipeWriter {

    public static void init(Consumer<FinishedRecipe> provider) {
        gCropFruitLineRecipes(provider);
    }

    public static void gCropFruitLineRecipes(Consumer<FinishedRecipe> provider) {
        for (StarTGCropData data : gCropData) {
            runRecipeGen(provider, data.getTier(), data.getId(), data.getMaterialType(), data.getResultMaterial(),
                    data.getYield());
        }

        runSecondaryLineRecipes(provider);
    }

    private static List getFruitLineResult(String material, StarTGCropItemType materialType) {
        Material rawMaterial = getMaterial(material);
        var result = new ArrayList<>();

        switch (materialType) {
            case LIQUID -> result.add(rawMaterial.getFluid());
            case DUST -> result.add(ChemicalHelper.get(dust, rawMaterial).getItem());
            case ORE -> result.add(ChemicalHelper.get(rawOre, rawMaterial).getItem());
            case GEM -> result.add(ChemicalHelper.get(gem, rawMaterial).getItem());
        }

        return result;
    }

    public static void runSecondaryLineRecipes(Consumer<FinishedRecipe> provider) {
        CENTRIFUGE_RECIPES.recipeBuilder("unstable_ion_blend_separation")
                .inputItems(ChemicalHelper.get(dust, getMaterial("start_core:unstable_ion_blend")))
                .outputFluids(GTMaterials.get("npk_solution").getFluid(2000))
                .outputItems(ChemicalHelper.get(dust, Strontium))
                .outputItems(ChemicalHelper.get(dust, getMaterial("start_core:poor_charged_bio_waste"), 2))
                .EUtVA(GTValues.ZPM)
                .duration(200)
                .save(provider);

        CENTRIFUGE_RECIPES.recipeBuilder("unstable_ion_blend_separation")
                .inputItems(ChemicalHelper.get(dust, getMaterial("start_core:unstable_ion_blend")))
                .outputFluids(GTMaterials.get("npk_solution").getFluid(2000))
                .outputItems(ChemicalHelper.get(dust, Strontium))
                .outputItems(ChemicalHelper.get(dust, getMaterial("start_core:poor_charged_bio_waste"), 2))
                .EUtVA(GTValues.ZPM)
                .duration(200)
                .save(provider);

        ELECTROLYZER_RECIPES.recipeBuilder("poor_charged_bio_waste_separation")
                .inputItems(ChemicalHelper.get(dust, getMaterial("start_core:poor_charged_bio_waste"), 2))
                .outputFluids(Biomass.getFluid(1000))
                .outputItems(ChemicalHelper.get(dust, IronMagnetic))
                .duration(200)
                .EUtVA(GTValues.MV)
                .save(provider);

        CENTRIFUGE_RECIPES.recipeBuilder("mineral_rich_bio_waste_separation")
                .inputFluids(getMaterial("start_core:mineral_rich_bio_waste").getFluid(1000))
                .outputFluids(GTMaterials.get("npk_solution").getFluid(1000))
                .outputFluids(Glycerol.getFluid(1000))
                .outputFluids(getMaterial("start_core:poor_mineral_rich_bio_waste").getFluid(2000))
                .EUtVA(GTValues.LuV)
                .duration(200)
                .save(provider);

        ELECTROLYZER_RECIPES.recipeBuilder("poor_mineral_rich_bio_waste_separation")
                .inputFluids(getMaterial("start_core:poor_mineral_rich_bio_waste").getFluid(1000))
                .outputFluids(Biomass.getFluid(1000))
                .outputFluids(Lava.getFluid(1000))
                .duration(200)
                .EUtVA(GTValues.LV)
                .save(provider);

        VOID_MESH.recipeBuilder("mystical_essence_harvesting")
                .circuitMeta(0)
                .outputItems(ChemicalHelper.get(dust, getMaterial("start_core:mystical_essence")))
                .duration(200)
                .EUtVA(GTValues.LV)
                .save(provider);

        VOID_GAS_COLLECTOR.recipeBuilder("mystical_air_harvesting")
                .circuitMeta(0)
                .outputFluids(getMaterial("start_core:mystical_air").getFluid(1000))
                .duration(200)
                .EUtVA(GTValues.EV)
                .save(provider);

        MIXER_RECIPES.recipeBuilder("mystical_air_mixing")
                .inputItems(ChemicalHelper.get(dust, getMaterial("start_core:mystical_essence")))
                .inputFluids(Air.getFluid(10000))
                .outputFluids(getMaterial("start_core:mystical_air").getFluid(10000))
                .EUtVA(GTValues.LV)
                .duration(200)
                .save(provider);
    }

    public static void runRecipeGen(Consumer<FinishedRecipe> provider, int tier, String id,
                                    StarTGCropItemType materialType, String resultMaterial, int yield) {
        if (id.equals("sheldonite")) id = "cooperite";
        if (resultMaterial == null) resultMaterial = id;

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
                            ChemicalHelper.get(dust, getMaterial(String.format("start_core:%s_pigment", dyeColor)), 16))
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);

            FLUID_SOLIDFICATION_RECIPES.recipeBuilder(String.format("%s_dilution", id))
                    .inputItems(ChemicalHelper.get(dust, getMaterial(String.format("start_core:%s_pigment", dyeColor))))
                    .inputFluids(Water.getFluid(100))
                    .outputItems(new ItemStack(StarTItemUtils.getItem(id)))
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);
        } else if (tier == 1) {
            EXTRACTOR_RECIPES.recipeBuilder(String.format("%s_extract", id))
                    .inputItems(fruit.asStack())
                    .outputFluids(getMaterial(String.format("start_core:%s_extract", id)).getFluid(4000))
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);

            var resultRecipe = CENTRIFUGE_RECIPES.recipeBuilder(String.format("%s_extract_separation", id))
                    .inputFluids(getMaterial(String.format("start_core:%s_extract", id)).getFluid(1000))
                    .outputFluids(getMaterial("start_core:poor_mineral_rich_bio_waste").getFluid(1000))
                    .EUtVA(EUtV)
                    .duration(200);

            // Proof of concept variable fluid/item result
            var result = getFruitLineResult(resultMaterial, materialType).get(0);
            if (result instanceof Item) resultRecipe.outputItems(new ItemStack((Item) result, yield));
            if (result instanceof Fluid) resultRecipe.outputItems(new FluidStack((Fluid) result, 1000 * yield));

            resultRecipe.save(provider);
        } else if (tier == 2) {
            FORGE_HAMMER_RECIPES.recipeBuilder(String.format("%s_smashing", id))
                    .inputItems(fruit.asStack())
                    .outputItems(
                            ChemicalHelper.get(dust, getMaterial(String.format("start_core:%s_fruit_pulp", id)), 4))
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);

            POLARIZER_RECIPES.recipeBuilder(String.format("%s_charging", id))
                    .inputItems(ChemicalHelper.get(dust, getMaterial(String.format("start_core:%s_fruit_pulp", id))))
                    .outputItems(ChemicalHelper.get(dust,
                            getMaterial(String.format("start_core:charged_%s_fruit_pulp", id))))
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);

            ELECTROMAGNETIC_SEPARATOR_RECIPES.recipeBuilder(String.format("%s_separation", id))
                    .inputItems(ChemicalHelper.get(dust,
                            getMaterial(String.format("start_core:charged_%s_fruit_pulp", id))))
                    .outputItems(new ItemStack((Item) getFruitLineResult(resultMaterial, materialType).get(0), yield))
                    .outputItems(ChemicalHelper.get(dust, getMaterial("start_core:poor_charged_bio_waste")))
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);
        } else if (tier == 3) {
            CUTTER_RECIPES.recipeBuilder(String.format("%s_slicing", id))
                    .inputItems(fruit.asStack())
                    .outputItems(FRUIT_SLICES.get(id).asStack(5))
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);

            BREWING_RECIPES.recipeBuilder(String.format("%s_brewing", id))
                    .inputItems(FRUIT_SLICES.get(id).asStack())
                    .inputFluids(Water.getFluid(1000))
                    .outputFluids(getMaterial(String.format("start_core:%s_fruit_tincture", id)).getFluid(1000))
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);

            DISTILLATION_RECIPES.recipeBuilder(String.format("%s_tincture_distillation", id))
                    .inputFluids(getMaterial(String.format("start_core:%s_fruit_tincture", id)).getFluid(1000))
                    .outputItems(dust, Wood)
                    .outputFluids(getMaterial(String.format("start_core:concentrated_%s_extract", id)).getFluid(750))
                    .outputFluids(Ethanol.getFluid(250))
                    .duration(200)
                    .EUtVA(EUtV)
                    .save(provider);

            FLUID_SOLIDFICATION_RECIPES.recipeBuilder(String.format("%s_solidification", id))
                    .inputItems(ChemicalHelper.get(dust, Stone))
                    .inputFluids(getMaterial(String.format("start_core:concentrated_%s_extract", id)).getFluid(250))
                    .outputItems(new ItemStack((Item) getFruitLineResult(resultMaterial, materialType).get(0), yield))
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);
        } else if (tier == 4) {
            BREWING_RECIPES.recipeBuilder(String.format("%s_brewing", id))
                    .inputItems(fruit.asStack())
                    .inputFluids(Water.getFluid(1000))
                    .outputFluids(getMaterial(String.format("start_core:dissolved_%s_fruit", id)).getFluid(1000))
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);

            DISTILLERY_RECIPES.recipeBuilder(String.format("dissolved_%s_fruit_distilling", id))
                    .inputFluids(getMaterial(String.format("start_core:dissolved_%s_fruit", id)).getFluid(1000))
                    .outputFluids(getMaterial(String.format("start_core:highly_concentrated_%s_fruit_solution", id))
                            .getFluid(300))
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);

            CENTRIFUGE_RECIPES.recipeBuilder(String.format("highly_concentrated_%s_fruit_solution_loosening", id))
                    .inputFluids(getMaterial(String.format("start_core:highly_concentrated_%s_fruit_solution", id))
                            .getFluid(100))
                    .outputFluids(getMaterial(String.format("start_core:liquefied_%s", id)).getFluid(1000))
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);

            if (materialType.equals(StarTGCropItemType.LIQUID)) {
                MIXER_RECIPES.recipeBuilder(String.format("liquefied_%s_sublimation", id))
                        .inputFluids(getMaterial(String.format("start_core:liquefied_%s", id)).getFluid(400))
                        .inputFluids(getMaterial("start_core:mystical_air").getFluid(9000))
                        .outputFluids(new FluidStack((Fluid) getFruitLineResult(resultMaterial, materialType).get(0),
                                1000 * yield))
                        .EUtVA(EUtV)
                        .duration(200)
                        .save(provider);
            } else {
                MIXER_RECIPES.recipeBuilder(String.format("liquefied_%s_coagulation", id))
                        .inputFluids(getMaterial(String.format("start_core:liquefied_%s", id)).getFluid(250))
                        .inputItems(ChemicalHelper.get(dust, Calcium))
                        .outputItems(
                                ChemicalHelper.get(dust, getMaterial(String.format("start_core:coagulated_%s", id))))
                        .EUtVA(EUtV)
                        .duration(200)
                        .save(provider);

                MACERATOR_RECIPES.recipeBuilder(String.format("coagulated_%s_shredding", id))
                        .inputItems(
                                ChemicalHelper.get(dust, getMaterial(String.format("start_core:coagulated_%s", id))))
                        .outputItems(
                                new ItemStack((Item) getFruitLineResult(resultMaterial, materialType).get(0), yield))
                        .EUtVA(EUtV)
                        .duration(200)
                        .save(provider);
            }
        } else if (tier == 5) {
            BLAST_RECIPES.recipeBuilder(String.format("%s_fruit_popping", id))
                    .inputItems(fruit.asStack())
                    .outputItems(POPPED_FRUITS.get(id).asStack())
                    .outputFluids(Steam.getFluid(2000))
                    .blastFurnaceTemp(3000)
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);

            EXTRACTOR_RECIPES.recipeBuilder(String.format("popped_%s_fruit_extraction", id))
                    .inputItems(POPPED_FRUITS.get(id).asStack())
                    .outputFluids(getMaterial(String.format("start_core:heated_%s_fruit_mixture", id)).getFluid(2000))
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);

            DISTILLATION_RECIPES.recipeBuilder(String.format("heated_%s_fruit_mixture_distillation", id))
                    .inputFluids(getMaterial(String.format("start_core:heated_%s_fruit_mixture", id)).getFluid(1000))
                    .outputFluids(getMaterial(String.format("start_core:%s_fruit_concentrate", id)).getFluid(500))
                    .outputFluids(Water.getFluid(500))
                    .duration(200)
                    .EUtVA(EUtV)
                    .save(provider);

            ELECTROLYZER_RECIPES.recipeBuilder(String.format("%s_fruit_concentrate_separation", id))
                    .inputFluids(getMaterial(String.format("start_core:%s_fruit_concentrate", id)).getFluid(1000))
                    .outputFluids(getMaterial(String.format("start_core:%s-rich_mixture", id)).getFluid(3000))
                    .outputItems(ChemicalHelper.get(dust, MetalMixture))
                    .outputFluids(Lava.getFluid(1000))
                    .duration(200)
                    .EUtVA(EUtV)
                    .save(provider);

            if (materialType.equals(StarTGCropItemType.LIQUID)) {
                FLUID_SOLIDFICATION_RECIPES.recipeBuilder(String.format("demystified_%s_essence_solidification", id))
                        .inputFluids(getMaterial(String.format("start_core:%s-rich_mixture", id)).getFluid(300))
                        .inputItems(ChemicalHelper.get(dust, MetalMixture))
                        .outputItems(ChemicalHelper.get(dust,
                                getMaterial(String.format("start_core:demystified_%s_essence", id))))
                        .EUtVA(EUtV)
                        .duration(200)
                        .save(provider);

                BREWING_RECIPES.recipeBuilder(String.format("%s_brewing", id))
                        .inputItems(ChemicalHelper.get(dust,
                                getMaterial(String.format("start_core:demystified_%s_essence", id))))
                        .inputFluids(getMaterial("start_core:mystical_air").getFluid(1000))
                        .outputFluids(
                                new FluidStack((Fluid) getFruitLineResult(resultMaterial, materialType).get(0), 1000))
                        .EUtVA(EUtV)
                        .duration(200)
                        .save(provider);
            } else {
                AUTOCLAVE_RECIPES.recipeBuilder("autoclave_" + id + "_distilled")
                        .inputItems(ChemicalHelper.get(dust, Stone))
                        .inputFluids(getMaterial(String.format("start_core:%s-rich_mixture", id)).getFluid(250))
                        .outputItems(new ItemStack((Item) getFruitLineResult(resultMaterial, materialType).get(0)))
                        .EUtVA(EUtV)
                        .duration(200)
                        .save(provider);
            }
        } else if (tier == 6) {
            MACERATOR_RECIPES.recipeBuilder(String.format("%s_fruit_shredding", id))
                    .inputItems(fruit.asStack())
                    .outputItems(
                            ChemicalHelper.get(dust, getMaterial(String.format("start_core:shredded_%s_fruit", id)), 8))
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);

            EXTRACTOR_RECIPES.recipeBuilder(String.format("shredded_%s_fruit_extraction", id))
                    .inputItems(
                            ChemicalHelper.get(dust, getMaterial(String.format("start_core:shredded_%s_fruit", id))))
                    .outputItems(ChemicalHelper.get(dust, getMaterial(String.format("start_core:%s_fruit_pulp", id))))
                    .outputFluids(getMaterial(String.format("start_core:%s_fruit_tincture", id)).getFluid(250))
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);

            COMPRESSOR_RECIPES.recipeBuilder(String.format("%s_fruit_pulp_compression", id))
                    .inputItems(ChemicalHelper.get(dust, getMaterial(String.format("start_core:%s_fruit_pulp", id)), 4))
                    .outputItems(
                            ChemicalHelper.get(ingot, getMaterial(String.format("start_core:compressed_%s_fruit", id))))
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);

            CENTRIFUGE_RECIPES.recipeBuilder(String.format("%s_fruit_tincture_separation", id))
                    .inputFluids(getMaterial(String.format("start_core:%s_fruit_tincture", id)).getFluid(1000))
                    .outputFluids(
                            getMaterial(String.format("start_core:concentrated_%s_fruit_tincture", id)).getFluid(1000))
                    .outputFluids(Water.getFluid(2000))
                    .outputItems(ChemicalHelper.get(dust, Wood))
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);

            DISTILLERY_RECIPES.recipeBuilder(String.format("concentrated_%s_fruit_tincture_distilling", id))
                    .inputItems(
                            ChemicalHelper.get(ingot, getMaterial(String.format("start_core:compressed_%s_fruit", id))))
                    .inputFluids(
                            getMaterial(String.format("start_core:concentrated_%s_fruit_tincture", id)).getFluid(1000))
                    .outputItems(
                            ChemicalHelper.get(dust, getMaterial(String.format("start_core:%s_fruit_blend", id)), 2))
                    .outputFluids(getMaterial("start_core:mineral_rich_bio_waste").getFluid(500))
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);

            MIXER_RECIPES.recipeBuilder(String.format("liquefied_%s_coagulation", id))
                    .inputItems(ChemicalHelper.get(dust, getMaterial(String.format("start_core:%s_fruit_blend", id))))
                    .inputFluids(GTMaterials.get("carbon_acid").getFluid(1000))
                    .outputFluids(getMaterial(String.format("start_core:dissolved_%s", id)).getFluid(4000))
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);

            BLAST_RECIPES.recipeBuilder(String.format("dissolved_%s_precipitation", id))
                    .inputFluids(getMaterial(String.format("start_core:dissolved_%s", id)).getFluid(1000))
                    .outputItems(new ItemStack((Item) getFruitLineResult(resultMaterial, materialType).get(0)))
                    .outputFluids(Steam.getFluid(2000))
                    .blastFurnaceTemp(4000)
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);
        } else if (tier == 7) {
            COMPRESSOR_RECIPES.recipeBuilder(String.format("%s_fruit_compression", id))
                    .inputItems(fruit.asStack())
                    .outputItems(
                            ChemicalHelper.get(ingot, getMaterial(String.format("start_core:compressed_%s_fruit", id))))
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);

            BLAST_RECIPES.recipeBuilder(String.format("compressed_%s_fruit_popping", id))
                    .inputItems(
                            ChemicalHelper.get(ingot, getMaterial(String.format("start_core:compressed_%s_fruit", id))))
                    .outputItems(POPPED_FRUITS.get(id).asStack())
                    .outputFluids(Steam.getFluid(2000))
                    .blastFurnaceTemp(6000)
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);

            MACERATOR_RECIPES.recipeBuilder(String.format("popped_%s_fruit_shredding", id))
                    .inputItems(POPPED_FRUITS.get(id).asStack())
                    .outputItems(ChemicalHelper.get(dust,
                            getMaterial(String.format("start_core:compressed_%s_fruit_pulp", id)), 4))
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);

            EXTRACTOR_RECIPES.recipeBuilder(String.format("compressed_%s_fruit_pulp_extraction", id))
                    .inputItems(ChemicalHelper.get(dust,
                            getMaterial(String.format("start_core:compressed_%s_fruit_pulp", id))))
                    .outputFluids(getMaterial(String.format("start_core:liquefied_%s_fruit_pulp", id)).getFluid(2000))
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);

            DISTILLATION_RECIPES.recipeBuilder(String.format("liquefied_%s_fruit_pulp_distillation", id))
                    .inputFluids(getMaterial(String.format("start_core:liquefied_%s_fruit_pulp", id)).getFluid(1000))
                    .outputFluids(getMaterial(String.format("start_core:refined_%s_fruit_mixture", id)).getFluid(1000))
                    .outputItems(ChemicalHelper.get(dust, getMaterial(String.format("start_core:pure_%s_fruit", id))))
                    .duration(200)
                    .EUtVA(EUtV)
                    .save(provider);

            FLUID_HEATER_RECIPES.recipeBuilder(String.format("refined_%s_fruit_mixture_evaporation", id))
                    .inputFluids(getMaterial(String.format("start_core:refined_%s_fruit_mixture", id)).getFluid(1000))
                    .outputFluids(getMaterial(String.format("start_core:pure_%s_fruit_mixture", id)).getFluid(800))
                    .duration(200)
                    .EUtVA(EUtV)
                    .save(provider);

            BREWING_RECIPES.recipeBuilder(String.format("refined_%s_fruit_mixture_dilution", id))
                    .inputFluids(getMaterial(String.format("start_core:refined_%s_fruit_mixture", id)).getFluid(1000))
                    .inputItems(Items.SAND)
                    .outputFluids(getMaterial(String.format("start_core:impure_%s_fruit_mixture", id)).getFluid(1200))
                    .duration(200)
                    .EUtVA(EUtV)
                    .save(provider);

            POLARIZER_RECIPES.recipeBuilder(String.format("pure_%s_fruit_charging", id))
                    .inputItems(ChemicalHelper.get(dust, getMaterial(String.format("start_core:pure_%s_fruit", id))))
                    .outputItems(ChemicalHelper.get(dust,
                            getMaterial(String.format("start_core:charged_pure_%s_fruit", id))))
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);

            MIXER_RECIPES.recipeBuilder(String.format("charged_%s_fruit_pulp_mixing", id))
                    .inputItems(ChemicalHelper.get(dust,
                            getMaterial(String.format("start_core:charged_pure_%s_fruit", id))))
                    .inputFluids(getMaterial(String.format("start_core:impure_%s_fruit_mixture", id)).getFluid(600))
                    .inputFluids(getMaterial(String.format("start_core:pure_%s_fruit_mixture", id)).getFluid(400))
                    .outputItems(ChemicalHelper.get(dust,
                            getMaterial(String.format("start_core:charged_%s_fruit_pulp", id))))
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);

            ELECTROLYZER_RECIPES.recipeBuilder(String.format("charged_%s_fruit_pulp_overcharging", id))
                    .inputItems(ChemicalHelper.get(dust,
                            getMaterial(String.format("start_core:charged_%s_fruit_pulp", id))))
                    .outputItems(ChemicalHelper.get(dust,
                            getMaterial(String.format("start_core:overcharged_unstable_%s_powder", id))))
                    .duration(200)
                    .EUtVA(EUtV)
                    .save(provider);

            ELECTROMAGNETIC_SEPARATOR_RECIPES
                    .recipeBuilder(String.format("overcharged_unstable_%s_powder_separation", id))
                    .inputItems(ChemicalHelper.get(dust,
                            getMaterial(String.format("start_core:overcharged_unstable_%s_powder", id))))
                    .outputItems(ChemicalHelper.get(dust, getMaterial(String.format("start_core:%s_composite", id))))
                    .outputItems(ChemicalHelper.get(dust, getMaterial("start_core:unstable_ion_blend")))
                    .EUtVA(EUtV)
                    .duration(200)
                    .save(provider);

            if (materialType.equals(StarTGCropItemType.LIQUID)) {
                EXTRACTOR_RECIPES.recipeBuilder(String.format("%s_composite_extraction", id))
                        .inputItems(ChemicalHelper.get(dust, getMaterial(String.format("start_core:%s_composite", id))))
                        .outputFluids(new FluidStack((Fluid) getFruitLineResult(resultMaterial, materialType).get(0),
                                1000 * yield))
                        .EUtVA(EUtV)
                        .duration(200)
                        .save(provider);
            } else {
                IMPLOSION_RECIPES.recipeBuilder(String.format("%s_composite_crystallization", id))
                        .inputItems(ChemicalHelper.get(dust, getMaterial(String.format("start_core:%s_composite", id))))
                        .inputItems(GTBlocks.INDUSTRIAL_TNT)
                        .outputItems(new ItemStack((Item) getFruitLineResult(resultMaterial, materialType).get(0)))
                        .outputItems(ChemicalHelper.get(dust, DarkAsh))
                        .EUtVA(EUtV)
                        .duration(200)
                        .save(provider);
            }
        }
    }
}
