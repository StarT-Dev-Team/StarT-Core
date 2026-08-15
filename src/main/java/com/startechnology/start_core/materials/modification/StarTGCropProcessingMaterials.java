package com.startechnology.start_core.materials.modification;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialStack;
import com.startechnology.start_core.api.gcrop.StarTGCropItemType;
import com.startechnology.start_core.data.gcrops.StarTGCropData;

import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.startechnology.start_core.data.gcrops.StarTGCropData.gCropData;
import static com.startechnology.start_core.utils.StarTMaterialUtils.getMaterial;

public class StarTGCropProcessingMaterials {

    public static void register() {
        for (StarTGCropData data : gCropData) {
            runMaterialModification(data.getTier(), data.getId(), data.getMaterialType());
        }
    }

    private static void runMaterialModification(int tier, String id, StarTGCropItemType type) {
        id = (id.equals("sheldonite")) ? "cooperite" : id;
        Material itemMaterial = getMaterial(id);

        int MaterialColorARBG = itemMaterial.getMaterialARGB();

        if (tier == 0) {
            String dyeColor = id.substring(0, id.length() - 4);

            Material pigment = getMaterial(String.format("start_core:%s_pigment", dyeColor));
            pigment.setMaterialARGB(MaterialColorARBG);
            pigment.setComponents(new MaterialStack(itemMaterial, 1));
        }
        if (tier == 1) {
            Material extract = getMaterial(String.format("start_core:%s_extract", id));
            extract.setMaterialARGB(MaterialColorARBG);
            extract.setComponents(new MaterialStack(itemMaterial, 1));
        }
        if (tier == 2) {
            Material pulp = getMaterial(String.format("start_core:%s_fruit_pulp", id));
            pulp.setMaterialARGB(MaterialColorARBG);
            pulp.setComponents(new MaterialStack(itemMaterial, 1));

            Material charged_pulp = getMaterial(String.format("start_core:charged_%s_fruit_pulp", id));
            charged_pulp.setMaterialARGB(MaterialColorARBG);
            charged_pulp.setComponents(new MaterialStack(itemMaterial, 1));
        }
        if (tier == 3) {
            Material extract = getMaterial(String.format("start_core:concentrated_%s_extract", id));
            extract.setMaterialARGB(MaterialColorARBG);
            extract.setComponents(new MaterialStack(itemMaterial, 1));

            Material tincture = getMaterial(String.format("start_core:%s_fruit_tincture", id));
            tincture.setMaterialARGB(MaterialColorARBG);
            tincture.setComponents(new MaterialStack(itemMaterial, 1));
        }
        if (tier == 4) {
            Material dissolved = getMaterial(String.format("start_core:dissolved_%s_fruit", id));
            dissolved.setMaterialARGB(MaterialColorARBG);
            dissolved.setComponents(new MaterialStack(itemMaterial, 1));

            Material concentrated = getMaterial(String.format("start_core:highly_concentrated_%s_fruit_solution", id));
            concentrated.setMaterialARGB(MaterialColorARBG);
            concentrated.setComponents(new MaterialStack(itemMaterial, 1));

            Material liquefied = getMaterial(String.format("start_core:liquefied_%s", id));
            liquefied.setMaterialARGB(MaterialColorARBG);
            liquefied.setComponents(new MaterialStack(itemMaterial, 1));

            Material coagulated = getMaterial(String.format("start_core:coagulated_%s", id));
            coagulated.setMaterialARGB(MaterialColorARBG);
            coagulated.setComponents(new MaterialStack(itemMaterial, 1));
        }
        if (tier == 5) {
            Material heated = getMaterial(String.format("start_core:heated_%s_fruit_mixture", id));
            heated.setMaterialARGB(MaterialColorARBG);
            heated.setComponents(new MaterialStack(itemMaterial, 1));

            Material concentrate = getMaterial(String.format("start_core:%s_fruit_concentrate", id));
            concentrate.setMaterialARGB(MaterialColorARBG);
            concentrate.setComponents(new MaterialStack(itemMaterial, 1));

            Material rich = getMaterial(String.format("start_core:%s-rich_mixture", id));
            rich.setMaterialARGB(MaterialColorARBG);
            rich.setComponents(new MaterialStack(itemMaterial, 1));

            if (type.equals(StarTGCropItemType.LIQUID)) {
                Material essence = getMaterial(String.format("start_core:demystified_%s_essence", id));
                essence.setMaterialARGB(MaterialColorARBG);
                essence.setComponents(new MaterialStack(itemMaterial, 1));
            }
        }
        if (tier == 6) {
            Material shredded = getMaterial(String.format("start_core:shredded_%s_fruit", id));
            shredded.setMaterialARGB(MaterialColorARBG);
            shredded.setComponents(new MaterialStack(itemMaterial, 1));

            Material pulp = getMaterial(String.format("start_core:%s_fruit_pulp", id));
            pulp.setMaterialARGB(MaterialColorARBG);
            pulp.setComponents(new MaterialStack(itemMaterial, 1));

            Material compressed = getMaterial(String.format("start_core:compressed_%s_fruit", id));
            compressed.setMaterialARGB(MaterialColorARBG);
            compressed.setComponents(new MaterialStack(itemMaterial, 1));

            Material tincture = getMaterial(String.format("start_core:%s_fruit_tincture", id));
            tincture.setMaterialARGB(MaterialColorARBG);
            tincture.setComponents(new MaterialStack(itemMaterial, 1));

            Material concentrated = getMaterial(String.format("start_core:concentrated_%s_fruit_tincture", id));
            concentrated.setMaterialARGB(MaterialColorARBG);
            concentrated.setComponents(new MaterialStack(itemMaterial, 1));

            Material blend = getMaterial(String.format("start_core:%s_fruit_blend", id));
            blend.setMaterialARGB(MaterialColorARBG);
            blend.setComponents(new MaterialStack(itemMaterial, 1));

            Material dissolved = getMaterial(String.format("start_core:dissolved_%s", id));
            dissolved.setMaterialARGB(MaterialColorARBG);
            dissolved.setComponents(new MaterialStack(itemMaterial, 1));
        }
        if (tier >= 7) {
            Material compressed = getMaterial(String.format("start_core:compressed_%s_fruit", id));
            compressed.setMaterialARGB(MaterialColorARBG);
            compressed.setComponents(new MaterialStack(itemMaterial, 1));

            Material compPulp = getMaterial(String.format("start_core:compressed_%s_fruit_pulp", id));
            compPulp.setMaterialARGB(MaterialColorARBG);
            compPulp.setComponents(new MaterialStack(itemMaterial, 1));

            Material liquefied = getMaterial(String.format("start_core:liquefied_%s_fruit_pulp", id));
            liquefied.setMaterialARGB(MaterialColorARBG);
            liquefied.setComponents(new MaterialStack(itemMaterial, 1));

            Material refined = getMaterial(String.format("start_core:refined_%s_fruit_mixture", id));
            refined.setMaterialARGB(MaterialColorARBG);
            refined.setComponents(new MaterialStack(itemMaterial, 1));

            Material pureMix = getMaterial(String.format("start_core:pure_%s_fruit_mixture", id));
            pureMix.setMaterialARGB(MaterialColorARBG);
            pureMix.setComponents(new MaterialStack(itemMaterial, 1));

            Material impureMix = getMaterial(String.format("start_core:impure_%s_fruit_mixture", id));
            impureMix.setMaterialARGB(MaterialColorARBG);
            impureMix.setComponents(new MaterialStack(itemMaterial, 1));

            Material pure = getMaterial(String.format("start_core:pure_%s_fruit", id));
            pure.setMaterialARGB(MaterialColorARBG);
            pure.setComponents(new MaterialStack(itemMaterial, 1));

            Material chargedPure = getMaterial(String.format("start_core:charged_pure_%s_fruit", id));
            chargedPure.setMaterialARGB(MaterialColorARBG);
            chargedPure.setComponents(new MaterialStack(itemMaterial, 1));

            Material chargedPulp = getMaterial(String.format("start_core:charged_%s_fruit_pulp", id));
            chargedPulp.setMaterialARGB(MaterialColorARBG);
            chargedPulp.setComponents(new MaterialStack(itemMaterial, 1));

            Material overcharged = getMaterial(String.format("start_core:overcharged_unstable_%s_powder", id));
            overcharged.setMaterialARGB(MaterialColorARBG);
            overcharged.setFormula(String.format("(%s)(%s)", itemMaterial.getChemicalFormula(),
                    getMaterial("start_core:unstable_ion_blend").getChemicalFormula()));

            Material composite = getMaterial(String.format("start_core:%s_composite", id));
            composite.setMaterialARGB(MaterialColorARBG);
            overcharged.setFormula(String.format("(%s)", itemMaterial.getChemicalFormula()));
        }
    }
}
