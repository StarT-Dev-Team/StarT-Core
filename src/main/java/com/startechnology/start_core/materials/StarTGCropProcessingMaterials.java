package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet;
import com.gregtechceu.gtceu.api.fluids.FluidBuilder;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.gcrop.StarTGCropItemType;
import com.startechnology.start_core.data.gcrops.StarTGCropData;

import java.util.HashMap;
import java.util.List;

import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.startechnology.start_core.StarTCore.LOGGER;
import static com.startechnology.start_core.materials.StarTTagPrefixes.*;
import static com.startechnology.start_core.data.gcrops.StarTGCropData.gCropData;

public class StarTGCropProcessingMaterials {

    public static HashMap<String, Material> fruitLineMaterials = new HashMap<String, Material>();

    public static void register() {
        for (StarTGCropData data : gCropData) {
            runMaterialGen(data.getTier(), data.getId(), data.getMaterialType());
        }

        generateAuxiliaryMaterials();
    }

    private static void generateAuxiliaryMaterials() {
        new Material.Builder(StarTCore.resourceLocation("poor_mineral-rich_bio_waste"))
                .liquid(new FluidBuilder())
                .color(000000)
                .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                .buildAndRegister();

        new Material.Builder(StarTCore.resourceLocation("poor_charged_bio_waste"))
                .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                .color(000000)
                .components()
                .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                .buildAndRegister();

        new Material.Builder(StarTCore.resourceLocation("unstable_ion_blend"))
                .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                .color(000000)
                .components()
                .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                .buildAndRegister();

        new Material.Builder(StarTCore.resourceLocation("mineral-rich_bio_waste"))
                .liquid(new FluidBuilder())
                .color(000000)
                .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                .buildAndRegister();
    }

    private static void runMaterialGen(int tier, String id, StarTGCropItemType type) {
        id = (id.equals("sheldonite")) ? "cooperite" : id;
        Material itemMaterial = GTMaterials.get(id);
        int MaterialColor = 000000;
        int MaterialColorARGB = itemMaterial.getMaterialARGB();
        int MaterialColorRBG = itemMaterial.getMaterialRGB();
        // int MaterialColor1 = itemMaterial.getColors();

        LOGGER.debug("Introducing colors for material: \"{}\"", id);
        LOGGER.debug(List.of(MaterialColorARGB, MaterialColorRBG));
        LOGGER.debug(List.of(MaterialColorARGB, MaterialColorRBG));
        // Issue: KubeJS materials are not initialized at this point in time

        if (tier == 0) {
            String dyeColor = id.substring(0, id.length() - 4);

            new Material.Builder(
                    StarTCore.resourceLocation(String.format("%s_pigment", dyeColor)))
                    .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                    .color(MaterialColorRBG)
                    .components(new Object[] { itemMaterial, 1 })
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();
        }
        if (tier == 1) {
            new Material.Builder(StarTCore.resourceLocation(String.format("%s_extract", id)))
                    .liquid(new FluidBuilder())
                    .color(MaterialColorRBG)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();
        }
        if (tier == 2) {
            new Material.Builder(
                    StarTCore.resourceLocation(String.format("charged_%s_fruit_pulp", id)))
                    .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                    .color(MaterialColorRBG)
                    .components(new Object[] { itemMaterial, 1 })
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(
                    StarTCore.resourceLocation(String.format("%s_fruit_pulp", id)))
                    .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                    .color(MaterialColorRBG)
                    .components(new Object[] { itemMaterial, 1 })
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();
        }
        if (tier == 3) {
            new Material.Builder(StarTCore.resourceLocation(String.format("concentrated_%s_extract", id)))
                    .liquid(new FluidBuilder())
                    .color(MaterialColorRBG)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(StarTCore.resourceLocation(String.format("%s_fruit_tincture", id)))
                    .liquid(new FluidBuilder())
                    .color(MaterialColorRBG)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();
        }
        if (tier == 4) {
            new Material.Builder(StarTCore.resourceLocation(String.format("dissolved_%s_fruit", id)))
                    .liquid(new FluidBuilder())
                    .color(MaterialColorRBG)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(StarTCore.resourceLocation(String.format("highly_concentrated_%s_fruit_solution", id)))
                    .liquid(new FluidBuilder())
                    .color(MaterialColorRBG)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(StarTCore.resourceLocation(String.format("liquefied_%s", id)))
                    .liquid(new FluidBuilder())
                    .color(MaterialColorRBG)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(
                    StarTCore.resourceLocation(String.format("coagulated_%s", id)))
                    .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                    .color(MaterialColorRBG)
                    .components(new Object[] { itemMaterial, 1 })
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();
        }
        if (tier == 5) {
            new Material.Builder(StarTCore.resourceLocation(String.format("heated_%s_fruit_mixture", id)))
                    .liquid(new FluidBuilder())
                    .color(MaterialColorRBG)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(StarTCore.resourceLocation(String.format("%s_fruit_concentrate", id)))
                    .liquid(new FluidBuilder())
                    .color(MaterialColorRBG)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(StarTCore.resourceLocation(String.format("%s-rich_mixture", id)))
                    .liquid(new FluidBuilder())
                    .color(MaterialColorRBG)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            if (!type.equals(StarTGCropItemType.ORE)) {
                new Material.Builder(StarTCore.resourceLocation(String.format("mystical_%s_essence_mixture", id)))
                        .liquid(new FluidBuilder())
                        .color(MaterialColorRBG)
                        .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                        .buildAndRegister();
            }
        }
        if (tier == 6) {
            new Material.Builder(
                    StarTCore.resourceLocation(String.format("compressed_%s_fruit", id)))
                    .ingot().ignoredTagPrefixes(nugget, block, dust, dustTiny, dustSmall, dustBlock)
                    .color(MaterialColorRBG)
                    .components(new Object[] { itemMaterial, 1 })
                    .iconSet(MaterialIconSet.DULL)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(
                    StarTCore.resourceLocation(String.format("%s_fruit_pulp", id)))
                    .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                    .color(MaterialColorRBG)
                    .components(new Object[] { itemMaterial, 1 })
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(StarTCore.resourceLocation(String.format("%s_fruit_tincture", id)))
                    .liquid(new FluidBuilder())
                    .color(MaterialColorRBG)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(StarTCore.resourceLocation(String.format("concentrated_%s_fruit_tincture", id)))
                    .liquid(new FluidBuilder())
                    .color(MaterialColorRBG)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(StarTCore.resourceLocation(String.format("dissolved_%s", id)))
                    .liquid(new FluidBuilder())
                    .color(MaterialColorRBG)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(
                    StarTCore.resourceLocation(String.format("%s_fruit_blend", id)))
                    .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                    .color(MaterialColorRBG)
                    .components(new Object[] { itemMaterial, 1 })
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();
        }
        if (tier == 7) {
            new Material.Builder(
                    StarTCore.resourceLocation(String.format("compressed_%s_fruit", id)))
                    .ingot().ignoredTagPrefixes(nugget, block, dust, dustTiny, dustSmall, dustBlock)
                    .color(MaterialColorRBG)
                    .components(new Object[] { itemMaterial, 1 })
                    .iconSet(MaterialIconSet.DULL)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(
                    StarTCore.resourceLocation(String.format("%s_fruit_pulp", id)))
                    .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                    .color(MaterialColorRBG)
                    .components(new Object[] { itemMaterial, 1 })
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(StarTCore.resourceLocation(String.format("liquefied_%s_fruit_pulp", id)))
                    .liquid(new FluidBuilder())
                    .color(MaterialColorRBG)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(StarTCore.resourceLocation(String.format("refined_%s_fruit_mixture", id)))
                    .liquid(new FluidBuilder())
                    .color(MaterialColorRBG)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(StarTCore.resourceLocation(String.format("pure_%s_fruit_mixture", id)))
                    .liquid(new FluidBuilder())
                    .color(MaterialColorRBG)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(StarTCore.resourceLocation(String.format("impure_%s_fruit_mixture", id)))
                    .liquid(new FluidBuilder())
                    .color(MaterialColorRBG)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(
                    StarTCore.resourceLocation(String.format("pure_%s_fruit_dust", id)))
                    .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                    .color(MaterialColorRBG)
                    .components(new Object[] { itemMaterial, 1 })
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(
                    StarTCore.resourceLocation(String.format("charged_pure_%s_fruit_dust", id)))
                    .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                    .color(MaterialColorRBG)
                    .components(new Object[] { itemMaterial, 1 })
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(
                    StarTCore.resourceLocation(String.format("charged_%s_fruit_pulp", id)))
                    .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                    .color(MaterialColorRBG)
                    .components(new Object[] { itemMaterial, 1 })
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .ignoredTagPrefixes(dustTiny, dustSmall)
                    .buildAndRegister();

            new Material.Builder(
                    StarTCore.resourceLocation(String.format("overcharged_unstable_%s_powder", id)))
                    .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                    .color(MaterialColorRBG)
                    .components(new Object[] { itemMaterial, 1 })
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(
                    StarTCore.resourceLocation(String.format("%s_composite", id)))
                    .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                    .color(MaterialColorRBG)
                    .components(new Object[] { itemMaterial, 1 })
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();
        }
    }
}
