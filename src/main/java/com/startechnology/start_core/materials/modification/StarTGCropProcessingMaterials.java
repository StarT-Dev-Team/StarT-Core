package com.startechnology.start_core.materials.modification;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialStack;
import com.startechnology.start_core.api.gcrop.StarTGCropItemType;
import com.startechnology.start_core.data.gcrops.StarTGCropData;

import static com.startechnology.start_core.StarTCore.LOGGER;
import static com.startechnology.start_core.data.gcrops.StarTGCropData.gCropData;
import static com.startechnology.start_core.utils.StarTMaterialUtils.getMaterial;

public class StarTGCropProcessingMaterials {

    public static void register() {
        for (StarTGCropData data : gCropData) {
            runMaterialModification(data.getTier(), data.getId(), data.getMaterialType());
        }
    }

    // Don't ask me how this works exactly, I googled and this is apparently how you can extract and recombine hexcodes
    // into their ARGB components
    private static int blendHexCodes(int firstHex, int secondHex, float ratio) {
        int firstA = (firstHex >> 24 & 0xff);
        int firstR = ((firstHex & 0xff0000) >> 16);
        int firstG = ((firstHex & 0xff00) >> 8);
        int firstB = (firstHex & 0xff);

        int secondA = (secondHex >> 24 & 0xff);
        int secondR = ((secondHex & 0xff0000) >> 16);
        int secondG = ((secondHex & 0xff00) >> 8);
        int secondB = (secondHex & 0xff);

        if (ratio > 1f) ratio = 1f;
        if (ratio < 0f) ratio = 0f;
        float counterRatio = 1f - ratio;

        int newA = (int) ((firstA * ratio) + (secondA * counterRatio));
        int newR = (int) ((firstR * ratio) + (secondR * counterRatio));
        int newG = (int) ((firstG * ratio) + (secondG * counterRatio));
        int newB = (int) ((firstB * ratio) + (secondB * counterRatio));

        int newHex = newA << 24 | newR << 16 | newG << 8 | newB;

        LOGGER.info("Combined hex \"{}\" and \"{}\" into \"{}\"", firstHex, secondHex, newHex);
        return newHex;
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
            extract.setMaterialARGB(blendHexCodes(MaterialColorARBG, 0x77395dFF, 0.8f));
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
            extract.setMaterialARGB(blendHexCodes(MaterialColorARBG, 0x77395dFF, 0.8f));
            extract.setComponents(new MaterialStack(itemMaterial, 1));

            Material tincture = getMaterial(String.format("start_core:%s_fruit_tincture", id));
            tincture.setMaterialARGB(blendHexCodes(MaterialColorARBG, 0x588675FF, 0.8f));
            tincture.setComponents(new MaterialStack(itemMaterial, 1));
        }
        if (tier == 4) {
            Material dissolved = getMaterial(String.format("start_core:dissolved_%s_fruit", id));
            dissolved.setMaterialARGB(blendHexCodes(MaterialColorARBG, 0x395177FF, 0.8f));
            dissolved.setComponents(new MaterialStack(itemMaterial, 1));

            Material concentrated = getMaterial(String.format("start_core:highly_concentrated_%s_fruit_solution", id));
            concentrated.setMaterialARGB(blendHexCodes(MaterialColorARBG, 0x072b64FF, 0.8f));
            concentrated.setComponents(new MaterialStack(itemMaterial, 1));

            Material liquefied = getMaterial(String.format("start_core:liquefied_%s", id));
            liquefied.setMaterialARGB(blendHexCodes(MaterialColorARBG, 0x55b4afFF, 0.8f));
            liquefied.setComponents(new MaterialStack(itemMaterial, 1));

            Material coagulated = getMaterial(String.format("start_core:coagulated_%s", id));
            coagulated.setMaterialARGB(MaterialColorARBG);
            coagulated.setComponents(new MaterialStack(itemMaterial, 1));
        }
        if (tier == 5) {
            Material heated = getMaterial(String.format("start_core:heated_%s_fruit_mixture", id));
            heated.setMaterialARGB(blendHexCodes(MaterialColorARBG, 0x90d815FF, 0.8f));
            heated.setComponents(new MaterialStack(itemMaterial, 1));

            Material concentrate = getMaterial(String.format("start_core:%s_fruit_concentrate", id));
            concentrate.setMaterialARGB(blendHexCodes(MaterialColorARBG, 0x0c8b54FF, 0.8f));
            concentrate.setComponents(new MaterialStack(itemMaterial, 1));

            Material rich = getMaterial(String.format("start_core:%s-rich_mixture", id));
            rich.setMaterialARGB(blendHexCodes(MaterialColorARBG, 0x30718bFF, 0.8f));
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
            tincture.setMaterialARGB(blendHexCodes(MaterialColorARBG, 0x588675FF, 0.8f));
            tincture.setComponents(new MaterialStack(itemMaterial, 1));

            Material concentrated = getMaterial(String.format("start_core:concentrated_%s_fruit_tincture", id));
            concentrated.setMaterialARGB(blendHexCodes(MaterialColorARBG, 0x474B4EFF, 0.8f));
            concentrated.setComponents(new MaterialStack(itemMaterial, 1));

            Material blend = getMaterial(String.format("start_core:%s_fruit_blend", id));
            blend.setMaterialARGB(MaterialColorARBG);
            blend.setComponents(new MaterialStack(itemMaterial, 1));

            Material dissolved = getMaterial(String.format("start_core:dissolved_%s", id));
            dissolved.setMaterialARGB(blendHexCodes(MaterialColorARBG, 0x395177FF, 0.8f));
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
            liquefied.setMaterialARGB(blendHexCodes(MaterialColorARBG, 0x317F43FF, 0.8f));
            liquefied.setComponents(new MaterialStack(itemMaterial, 1));

            Material refined = getMaterial(String.format("start_core:refined_%s_fruit_mixture", id));
            refined.setMaterialARGB(blendHexCodes(MaterialColorARBG, 0x015D52FF, 0.8f));
            refined.setComponents(new MaterialStack(itemMaterial, 1));

            Material pureMix = getMaterial(String.format("start_core:pure_%s_fruit_mixture", id));
            pureMix.setMaterialARGB(blendHexCodes(MaterialColorARBG, 0x2E3A23FF, 0.8f));
            pureMix.setComponents(new MaterialStack(itemMaterial, 1));

            Material impureMix = getMaterial(String.format("start_core:impure_%s_fruit_mixture", id));
            impureMix.setMaterialARGB(blendHexCodes(MaterialColorARBG, 0x8B8C7AFF, 0.8f));
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
