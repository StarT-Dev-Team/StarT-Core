package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.fluids.FluidBuilder;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.*;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet.*;
import static com.startechnology.start_core.materials.StarTMaterialHelpers.*;
import static com.startechnology.start_core.materials.StarTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;

public class StarTNetherMaterials {

    public static void register() {
        // Mythril Line
        EnrichedMythrillicMixture = registerGTCEuMaterial("enriched_mythrillic_mixture")
                .langValue("Enriched §3Mythrillic§r Mixture")
                .fluid()
                .components(Mystery, 1, Mythril, 1,
                        Mystery, 1)
                .color(0x238213)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        MoltenMythrillicMixture = registerGTCEuMaterial("molten_mythrillic_mixture")
                .langValue("Molten §3Mythrillic§r Mixture")
                .liquid(new FluidBuilder().temperature(3100))
                .components(Mystery, 1, Mythril, 1,
                        Mystery, 1)
                .color(0x238342)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Mythrillic = registerGTCEuMaterial("mythrillic")
                .langValue("§3Mythrillic§r")
                .dust()
                .fluid()
                .components(Mystery, 1, Mythril, 1,
                        Mystery, 1)
                .color(0x238362)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        MythrillicHydride = registerGTCEuMaterial("mythrillic_hydride")
                .langValue("§3Mythrillic§r Hydride")
                .dust()
                .components(Mythril, 1, Hydrogen, 2)
                .color(0x238338)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        // Adamantine Line
        EnrichedAdamantamiteMixture = registerGTCEuMaterial("enriched_adamantamite_mixture")
                .langValue("Enriched §6Adamantamite§r Mixture")
                .fluid()
                .components(Mystery, 1, Adamantine, 1,
                        Mystery, 1)
                .color(0x866e4b)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        MoltenAdamantamiteMixture = registerGTCEuMaterial("molten_adamantamite_mixture")
                .langValue("Molten §6Adamantamite§r Mixture")
                .liquid(new FluidBuilder().temperature(3700))
                .components(Mystery, 1, Adamantine, 1,
                        Mystery, 1)
                .color(0x866e7b)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Adamantamite = registerGTCEuMaterial("adamantamite")
                .langValue("§6Adamantamite§r")
                .dust()
                .fluid()
                .components(Mystery, 1, Adamantine, 1,
                        Mystery, 1)
                .color(0x825f2b)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        AdamantineHydroxide = registerGTCEuMaterial("adamantine_hydroxide")
                .langValue("§6Adamantamite§r Hydroxide")
                .dust()
                .components(Adamantine, 1, Hydrogen, 3, Oxygen, 3)
                .color(0xcb8858)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        // Estalt Line
        MoltenEstaltadyneMixture = registerGTCEuMaterial("molten_estaltadyne_mixture")
                .langValue("Molten §cEstaltadyne§r Mixture")
                .liquid(new FluidBuilder().temperature(3500))
                .components(Mystery, 1, Estalt, 1,
                        Mystery, 1)
                .color(0x8e0505)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Estaltadyne = registerGTCEuMaterial("estaltadyne")
                .langValue("§cEstaltadyne§r")
                .dust()
                .fluid()
                .components(Mystery, 1, Estalt, 1,
                        Mystery, 1)
                .color(0x8e0535)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        EstaltadyneHydride = registerGTCEuMaterial("estaltadyne_hydride")
                .langValue("§cEstaltadyne§r Hydride")
                .dust()
                .components(Estalt, 4, Hydrogen, 9)
                .color(0x8e0505)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        EnrichedEstaltadyneMixture = registerGTCEuMaterial("enriched_estaltadyne_mixture")
                .langValue("Enriched §c§oEstaltadyne§r Mixture")
                .fluid()
                .components(Mystery, 1, EnrichedEstalt, 1,
                        Mystery, 1)
                .color(0xbe4747)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        EnrichedEstaltadyneSolution = registerGTCEuMaterial("enriched_estaltadyne_solution")
                .langValue("Enriched §c§oEstaltadyne§r Solution")
                .fluid()
                .components(Mystery, 1, EnrichedEstalt, 1,
                        Mystery, 1)
                .color(0xbe4717)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        // Calamatium & Isovol Line
        ImpureCalamatiumSolution = registerGTCEuMaterial("impure_calamatium_solution")
                .fluid()
                .color(0x990000)
                .buildAndRegister();

        ImpureIsovolSolution = registerGTCEuMaterial("impure_isovol_solution")
                .fluid()
                .color(0x000066)
                .buildAndRegister();

        CalamatiumSolution = registerGTCEuMaterial("calamatium_solution")
                .fluid()
                .color(0xe60000)
                .buildAndRegister();

        IsovolSolution = registerGTCEuMaterial("isovol_solution")
                .fluid()
                .color(0x6600cc)
                .buildAndRegister();

        CalamatiumFluoride = registerGTCEuMaterial("calamatium_fluoride")
                .dust()
                .components(Calamatium, 1, Fluorine, 2)
                .color(0xcc0066)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        IsovolFluoride = registerGTCEuMaterial("isovol_fluoride")
                .dust()
                .components(Isovol, 1, Fluorine, 2)
                .color(0x9900ff)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        // Atomic Nether Dust Line
        AtomicNetherSludge = registerGTCEuMaterial("atomic_nether_sludge")
                .langValue("Atomic §4Nether§r Sludge")
                .dust()
                .components(Mystery, 1, Mystery, 1,
                        Mystery, 1, Mystery, 1)
                .color(0x883039)
                .iconSet(RADIOACTIVE)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        DeactivatedNether = registerGTCEuMaterial("deactivated_nether")
                .langValue("Deactivated §4Nether§r")
                .dust()
                .components(Mystery, 1, Mystery, 1)
                .color(0x664c4c)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        ActivatedNether = registerGTCEuMaterial("activated_nether")
                .langValue("Activated §4Nether§r")
                .dust()
                .components(Mystery, 1, Mystery, 1)
                .color(0xa01819)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        HafnastideRichSludge = registerGTCEuMaterial("hafnastide_rich_sludge")
                .langValue("Hafnastide-Rich Sludge")
                .dust()
                .components(Mystery, 1, Hafnium, 1, Astatine, 1,
                        Mystery, 1)
                .color(0xa8798a)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        FleroviumRichReSludge = registerGTCEuMaterial("flerovium_rich_re_sludge")
                .langValue("Flerovium-Rich Re Sludge")
                .dust()
                .components(Mystery, 1, Flerovium, 1, Mystery,
                        1, Mystery, 1)
                .color(0x798579)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        PologiumRichSludge = registerGTCEuMaterial("pologium_rich_sludge")
                .langValue("Pologium-Rich Sludge")
                .dust()
                .components(Mystery, 1, Polonium, 1, Seaborgium, 1,
                        Mystery, 1)
                .color(0x576b62)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();
    }
}
