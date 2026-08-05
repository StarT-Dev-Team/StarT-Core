package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet;
import com.gregtechceu.gtceu.api.fluids.FluidBuilder;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.gcrop.StarTGCropItemType;
import com.startechnology.start_core.data.gcrops.StarTGCropData;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.startechnology.start_core.materials.StarTTagPrefixes.*;
import static com.startechnology.start_core.data.gcrops.StarTGCropData.gCropData;
import static com.startechnology.start_core.utils.StarTMaterialUtils.getMaterial;

public class StarTGCropProcessingMaterials {

    public static void register() {
        for (StarTGCropData data : gCropData) {
            runMaterialGen(data.getTier(), data.getId(), data.getMaterialType());
        }

        generateAuxiliaryMaterials();
    }

    private static void generateAuxiliaryMaterials() {
        new Material.Builder(StarTCore.resourceLocation("mystical_air"))
                .liquid(new FluidBuilder())
                .color(0x6ca9dd)
                .components(Air, 1, Biomass, 1) // Biomass is a placeholder for ? :sob:
                .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                .buildAndRegister();

        new Material.Builder(StarTCore.resourceLocation("mystical_essence"))
                .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                .color(0x1769af)
                .components(Biomass, 1)
                .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                .buildAndRegister();

        new Material.Builder(StarTCore.resourceLocation("poor_mineral_rich_bio_waste"))
                .liquid(new FluidBuilder())
                .components(Biomass, 1, Lava, 1)
                .color(0xfba92b)
                .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                .buildAndRegister();

        new Material.Builder(StarTCore.resourceLocation("mineral_rich_bio_waste"))
                .liquid(new FluidBuilder())
                .color(0x988b3c)
                .components(getMaterial("poor_mineral_rich_bio_waste"), 2, Glycerol, 1, getMaterial("npk_solution"), 1)
                .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                .buildAndRegister();

        new Material.Builder(StarTCore.resourceLocation("poor_charged_bio_waste"))
                .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                .color(0x81d8b3)
                .components(Biomass, 1, IronMagnetic, 1)
                .iconSet(MAGNETIC)
                .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                .buildAndRegister();

        new Material.Builder(StarTCore.resourceLocation("unstable_ion_blend"))
                .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                .color(0x6f857b)
                .components(getMaterial("poor_charged_bio_waste"), 2, Strontium, 1, getMaterial("gtceu:npk_solution"),
                        2)
                .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                .buildAndRegister();
    }

    private static void runMaterialGen(int tier, String id, StarTGCropItemType type) {
        id = (id.equals("sheldonite")) ? "cooperite" : id;

        if (tier == 0) {
            String dyeColor = id.substring(0, id.length() - 4);

            new Material.Builder(
                    StarTCore.resourceLocation(String.format("%s_pigment", dyeColor)))
                    .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();
        }
        if (tier == 1) {
            new Material.Builder(StarTCore.resourceLocation(String.format("%s_extract", id)))
                    .liquid(new FluidBuilder())
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();
        }
        if (tier == 2) {
            new Material.Builder(
                    StarTCore.resourceLocation(String.format("charged_%s_fruit_pulp", id)))
                    .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                    .iconSet(MAGNETIC)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(
                    StarTCore.resourceLocation(String.format("%s_fruit_pulp", id)))
                    .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();
        }
        if (tier == 3) {
            new Material.Builder(StarTCore.resourceLocation(String.format("concentrated_%s_extract", id)))
                    .liquid(new FluidBuilder())
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(StarTCore.resourceLocation(String.format("%s_fruit_tincture", id)))
                    .liquid(new FluidBuilder())
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();
        }
        if (tier == 4) {
            new Material.Builder(StarTCore.resourceLocation(String.format("dissolved_%s_fruit", id)))
                    .liquid(new FluidBuilder())
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(StarTCore.resourceLocation(String.format("highly_concentrated_%s_fruit_solution", id)))
                    .liquid(new FluidBuilder())
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(StarTCore.resourceLocation(String.format("liquefied_%s", id)))
                    .liquid(new FluidBuilder())
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(
                    StarTCore.resourceLocation(String.format("coagulated_%s", id)))
                    .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();
        }
        if (tier == 5) {

            var hmix = new Material.Builder(StarTCore.resourceLocation(String.format("heated_%s_fruit_mixture", id)))
                    .liquid(new FluidBuilder())
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES);

            var conc = new Material.Builder(StarTCore.resourceLocation(String.format("%s_fruit_concentrate", id)))
                    .liquid(new FluidBuilder())
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES);

            var rmix = new Material.Builder(StarTCore.resourceLocation(String.format("%s-rich_mixture", id)))
                    .liquid(new FluidBuilder())
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES);

            if (id.equals("cooperite")) {
                hmix.langValue("Heated Sheldonite Fruit Mixture");
                conc.langValue("Sheldonite Fruit Concentrate");
                rmix.langValue("Sheldonite-Rich Mixture");
            }

            hmix.buildAndRegister();
            conc.buildAndRegister();
            rmix.buildAndRegister();

            if (type.equals(StarTGCropItemType.LIQUID)) {
                new Material.Builder(StarTCore.resourceLocation(String.format("demystified_%s_essence", id)))
                        .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                        .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                        .buildAndRegister();
            }
        }
        if (tier == 6) {
            new Material.Builder(
                    StarTCore.resourceLocation(String.format("shredded_%s_fruit", id)))
                    .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(
                    StarTCore.resourceLocation(String.format("%s_fruit_pulp", id)))
                    .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(
                    StarTCore.resourceLocation(String.format("compressed_%s_fruit", id)))
                    .ingot().ignoredTagPrefixes(nugget, block, dust, dustTiny, dustSmall, dustBlock)
                    .iconSet(MaterialIconSet.DULL)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(StarTCore.resourceLocation(String.format("%s_fruit_tincture", id)))
                    .liquid(new FluidBuilder())
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(StarTCore.resourceLocation(String.format("concentrated_%s_fruit_tincture", id)))
                    .liquid(new FluidBuilder())
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(
                    StarTCore.resourceLocation(String.format("%s_fruit_blend", id)))
                    .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(StarTCore.resourceLocation(String.format("dissolved_%s", id)))
                    .liquid(new FluidBuilder())
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();
        }
        if (tier == 7) {
            new Material.Builder(
                    StarTCore.resourceLocation(String.format("compressed_%s_fruit", id)))
                    .ingot().ignoredTagPrefixes(nugget, block, dust, dustTiny, dustSmall, dustBlock)
                    .iconSet(MaterialIconSet.DULL)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(
                    StarTCore.resourceLocation(String.format("compressed_%s_fruit_pulp", id)))
                    .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(StarTCore.resourceLocation(String.format("liquefied_%s_fruit_pulp", id)))
                    .liquid(new FluidBuilder())
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(StarTCore.resourceLocation(String.format("refined_%s_fruit_mixture", id)))
                    .liquid(new FluidBuilder())
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(StarTCore.resourceLocation(String.format("pure_%s_fruit_mixture", id)))
                    .liquid(new FluidBuilder())
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(StarTCore.resourceLocation(String.format("impure_%s_fruit_mixture", id)))
                    .liquid(new FluidBuilder())
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(
                    StarTCore.resourceLocation(String.format("pure_%s_fruit", id)))
                    .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(
                    StarTCore.resourceLocation(String.format("charged_pure_%s_fruit", id)))
                    .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                    .iconSet(MAGNETIC)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(
                    StarTCore.resourceLocation(String.format("charged_%s_fruit_pulp", id)))
                    .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                    .iconSet(MAGNETIC)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .ignoredTagPrefixes(dustTiny, dustSmall)
                    .buildAndRegister();

            new Material.Builder(
                    StarTCore.resourceLocation(String.format("overcharged_unstable_%s_powder", id)))
                    .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                    .iconSet(MAGNETIC)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();

            new Material.Builder(
                    StarTCore.resourceLocation(String.format("%s_composite", id)))
                    .dust().ignoredTagPrefixes(dustTiny, dustSmall, dustBlock)
                    .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.DISABLE_MATERIAL_RECIPES)
                    .buildAndRegister();
        }
    }
}
