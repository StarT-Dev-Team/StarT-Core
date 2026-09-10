package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.data.chemical.material.properties.BlastProperty;
import com.gregtechceu.gtceu.api.fluids.FluidBuilder;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.*;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet.*;
import static com.startechnology.start_core.materials.StarTMaterialHelpers.*;
import static com.startechnology.start_core.materials.StarTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;

public class StarTVoidAbyssalMaterials {

    public static void register() {
        // Abyssal Harvesting
        LowSaturationVoidicExcression = registerGTCEuMaterial("low_saturation_voidic_excression")
                .liquid(new FluidBuilder().temperature(19999))
                .components(Mystery, 1)
                .color(0x0a0a0a)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        ModerateSaturationVoidicExcression = registerGTCEuMaterial("moderate_saturation_voidic_excression")
                .liquid(new FluidBuilder().temperature(19999))
                .components(Mystery, 1)
                .color(0x111111)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        HighSaturationVoidicExcression = registerGTCEuMaterial("high_saturation_voidic_excression")
                .liquid(new FluidBuilder().temperature(19999))
                .components(Mystery, 1)
                .color(0x1a0e12)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        LethargicVoidicSlurry = registerGTCEuMaterial("lethargic_voidic_slurry")
                .liquid(new FluidBuilder().temperature(14999))
                .components(Mystery, 1)
                .color(0x7a7a7a)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        TemperedVoidicSlurry = registerGTCEuMaterial("tempered_voidic_slurry")
                .liquid(new FluidBuilder().temperature(14999))
                .components(Mystery, 1)
                .color(0x666666)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        VibrantVoidicSlurry = registerGTCEuMaterial("vibrant_voidic_slurry")
                .liquid(new FluidBuilder().temperature(14999))
                .components(Mystery, 1)
                .color(0x4c4c4c)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        AlphaStateVoidSludge = registerGTCEuMaterial("alpha_state_void_sludge")
                .langValue("α-State Void Sludge")
                .liquid(new FluidBuilder().temperature(12500))
                .components(Mystery, 1)
                .color(0x4a294a)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        BetaStateVoidSludge = registerGTCEuMaterial("beta_state_void_sludge")
                .langValue("β-State Void Sludge")
                .liquid(new FluidBuilder().temperature(25000))
                .components(Mystery, 1)
                .color(0x39504a)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        GammaStateVoidSludge = registerGTCEuMaterial("gamma_state_void_sludge")
                .langValue("γ-State Void Sludge")
                .liquid(new FluidBuilder().temperature(37500))
                .components(Mystery, 1)
                .color(0x3a3f5a)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        DeltaStateVoidSludge = registerGTCEuMaterial("delta_state_void_sludge")
                .langValue("δ-State Void Sludge")
                .liquid(new FluidBuilder().temperature(50000))
                .components(Mystery, 1)
                .color(0x4d3b2e)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        EpsilonStateVoidSludge = registerGTCEuMaterial("epsilon_state_void_sludge")
                .langValue("ε-State Void Sludge")
                .liquid(new FluidBuilder().temperature(62500))
                .components(Mystery, 1)
                .color(0x2e4d3b)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        ZetaStateVoidSludge = registerGTCEuMaterial("zeta_state_void_sludge")
                .langValue("ζ-State Void Sludge")
                .liquid(new FluidBuilder().temperature(75000))
                .components(Mystery, 1)
                .color(0x3f2e4d)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        AlphaStateVoidResidue = registerGTCEuMaterial("alpha_state_void_residue")
                .langValue("α-State Void Residue")
                .liquid(new FluidBuilder().temperature(12500))
                .components(Mystery, 1)
                .color(0x652165)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        BetaStateVoidResidue = registerGTCEuMaterial("beta_state_void_residue")
                .langValue("β-State Void Residue")
                .liquid(new FluidBuilder().temperature(25000))
                .components(Mystery, 1)
                .color(0x486053)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        GammaStateVoidResidue = registerGTCEuMaterial("gamma_state_void_residue")
                .langValue("γ-State Void Residue")
                .liquid(new FluidBuilder().temperature(37500))
                .components(Mystery, 1)
                .color(0x464a66)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        DeltaStateVoidResidue = registerGTCEuMaterial("delta_state_void_residue")
                .langValue("δ-State Void Residue")
                .liquid(new FluidBuilder().temperature(50000))
                .components(Mystery, 1)
                .color(0x5a4638)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        EpsilonStateVoidResidue = registerGTCEuMaterial("epsilon_state_void_residue")
                .langValue("ε-State Void Residue")
                .liquid(new FluidBuilder().temperature(62500))
                .components(Mystery, 1)
                .color(0x365a46)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        ZetaStateVoidResidue = registerGTCEuMaterial("zeta_state_void_residue")
                .langValue("ζ-State Void Residue")
                .liquid(new FluidBuilder().temperature(75000))
                .components(Mystery, 1)
                .color(0x54386a)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        OrderCentricVoid = registerGTCEuMaterial("order_centric_void")
                .langValue("Order-Centric Void")
                .liquid(new FluidBuilder().temperature(80000))
                .components(Mystery, 1)
                .color(0xf0e060)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        ChaosCentricVoid = registerGTCEuMaterial("chaos_centric_void")
                .langValue("Chaos-Centric Void")
                .liquid(new FluidBuilder().temperature(80000))
                .components(Mystery, 1)
                .color(0xc040f0)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        VoidicWasteResidue = registerGTCEuMaterial("voidic_waste_residue")
                .liquid(new FluidBuilder().temperature(10000))
                .components(Mystery, 1)
                .color(0x6b4a2f)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        // Faematter Line
        ImpureFaematter = registerGTCEuMaterial("impure_faematter")
                .fluid()
                .components(Mystery, 1, Faetic, 1,
                        Mystery, 1)
                .color(0xe8bce7)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Faematter = registerGTCEuMaterial("faematter")
                .fluid()
                .components(Faetic, 1)
                .color(0xf593f3)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        StabilizationMixtureBase = registerGTCEuMaterial("stabilization_mixture_base")
                .fluid()
                .components(Poly34EthylenedioxythiophenePolystyreneSulfonate, 5,
                        RunicConvergenceInfusion, 17, UtopianAkreyrium, 10)
                .color(0xb6b4c2)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        InfernalStabilizationMixture = registerGTCEuMaterial("infernal_stabilization_mixture")
                .fluid()
                .components(StabilizationMixtureBase, 24, MagmadaAlloy, 1)
                .color(0xbd4444)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        AbyssalStabilizationMixture = registerGTCEuMaterial("abyssal_stabilization_mixture")
                .fluid()
                .components(StabilizationMixtureBase, 24, AbyssalAlloy, 1)
                .color(0x7e44bd)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        DraconicStabilizationMixture = registerGTCEuMaterial("draconic_stabilization_mixture")
                .fluid()
                .components(AbyssalStabilizationMixture, 1, InfernalStabilizationMixture, 1)
                .color(0x302185)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        KaleidoscopeAgitationSerum = registerGTCEuMaterial("kaleidoscope_agitation_serum")
                .fluid()
                .components(Mystery, 1)
                .color(0x9afc88)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        // Riftic Lines
        RiftionExtract = registerGTCEuMaterial("riftion_extract")
                .fluid()
                .components(Mystery, 999, Riftic, 1,
                        Mystery, 999)
                .color(0x8f5d8c)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Riftion = registerGTCEuMaterial("riftion")
                .liquid(new FluidBuilder().temperature(999989999))
                .plasma()
                .components(RiftionExtract, 1, Neutronium, 1)
                .color(0xf0fbff)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        HighlyUnstableRiftSource = registerGTCEuMaterial("highly_unstable_rift_source")
                .fluid()
                .components(Riftic, 1, Excited, 1)
                .color(0x5e1c5b)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        DestabilizedRiftSource = registerGTCEuMaterial("destabilized_rift_source")
                .fluid()
                .components(Riftic, 1, Mystery, 1)
                .color(0x854181)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        AscensionRiftSlurry = registerGTCEuMaterial("ascension_rift_slurry")
                .fluid()
                .components(Riftic, 1, Mystery, 1)
                .color(0xedb2ea)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        AbyssalRiftSlurry = registerGTCEuMaterial("abyssal_rift_slurry")
                .fluid()
                .components(Riftic, 1, Mystery, 1)
                .color(0x3c265c)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        RimulaTFoundation = registerGTCEuMaterial("rimula_t_foundation")
                .langValue("Rimula-Tempus Foundation")
                .fluid()
                .components(Riftic, 1, Mystery, 1)
                .color(0xe5d1eb)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        RimulaSFoundation = registerGTCEuMaterial("rimula_s_foundation")
                .langValue("Rimula-Spacial Foundation")
                .fluid()
                .components(Riftic, 1, Mystery, 1)
                .color(0x230145)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        TrueRimulaFoundation = registerGTCEuMaterial("true_rimula_foundation")
                .fluid()
                .components(Riftic, 1, Mystery, 1)
                .color(0x9e22bd)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        PrimordialExtract = registerGTCEuMaterial("primordial_extract")
                .fluid()
                .components(Riftic, 1, Voidic, 1, Mystery, 1)
                .color(0x504985)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        PrimordialResidue = registerGTCEuMaterial("primordial_residue")
                .fluid()
                .components(Riftic, 1, Voidic, 1)
                .color(0x2b2080)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        CondensedRimula = registerGTCEuMaterial("condensed_rimula")
                .fluid()
                .components(Riftic, 8, Mystery, 1)
                .color(0xa16296)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        RifticConcentrate = registerGTCEuMaterial("riftic_concentrate")
                .fluid()
                .components(Riftic, 8)
                .color(0xb5109a)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        FaeticExtract = registerGTCEuMaterial("faetic_extract")
                .fluid()
                .components(Riftic, 1, Faetic, 1,
                        Mystery, 1)
                .color(0x7da7b0)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        PrismaticHypergurmalium = registerGTCEuMaterial("prismatic_hypergurmalium")
                .fluid()
                .components(Riftic, 1, Faetic, 1)
                .color(0x4fdfff)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        // Draco Organic Lines
        DraconicHormoneResidue = registerGTCEuMaterial("draconic_hormone_residue")
                .fluid()
                .color(0x6c4d6e)
                .buildAndRegister();

        DracEndocriticMedium = registerGTCEuMaterial("drac_endocritic_medium")
                .langValue("Drac-Endocritic Medium")
                .fluid()
                .color(0x75577a)
                .buildAndRegister();

        DracAurouricEndocrinalMedium = registerGTCEuMaterial("drac_aurouric_endocrinal_medium")
                .langValue("Drac-Aurouric Endocrinal Medium")
                .fluid()
                .color(0x6678a6)
                .buildAndRegister();

        PrecursorSerum = registerGTCEuMaterial("precursor_serum")
                .fluid()
                .color(0x8c6fa3)
                .buildAndRegister();

        AbyssalNutrientBlend = registerGTCEuMaterial("abyssal_nutrient_blend")
                .fluid()
                .color(0x4a3b33)
                .buildAndRegister();

        CondensedAbyssalNutrientBlend = registerGTCEuMaterial("condensed_abyssal_nutrient_blend")
                .fluid()
                .color(0x5c4038)
                .buildAndRegister();

        AminoPrimedMedium = registerGTCEuMaterial("amino_primed_medium")
                .fluid()
                .color(0x705a64)
                .buildAndRegister();

        DracPeptideAminoResidue = registerGTCEuMaterial("drac_peptide_amino_residue")
                .langValue("Drac-Peptide Amino Residue")
                .fluid()
                .color(0x7a5c82)
                .buildAndRegister();

        Voidrenin = registerGTCEuMaterial("voidrenin")
                .fluid()
                .color(0x1a1a1a)
                .buildAndRegister();

        Terrathroxin = registerGTCEuMaterial("terrathroxin")
                .fluid()
                .color(0x3f5a3f)
                .buildAndRegister();

        Stormcallin = registerGTCEuMaterial("stormcallin")
                .fluid()
                .color(0x4a6c82)
                .buildAndRegister();

        Cryokinase = registerGTCEuMaterial("cryokinase")
                .fluid()
                .color(0x7ba6b3)
                .buildAndRegister();

        Ignisferin = registerGTCEuMaterial("ignisferin")
                .fluid()
                .color(0xa6533a)
                .buildAndRegister();

        BreathHormoneComplex = registerGTCEuMaterial("breath_hormone_complex")
                .langValue("Breath-Hormone Complex")
                .fluid()
                .color(0x8c7080)
                .buildAndRegister();

        Hemavyrin = registerGTCEuMaterial("hemavyrin")
                .fluid()
                .color(0x6b2a2a)
                .buildAndRegister();

        Aethermetin = registerGTCEuMaterial("aethermetin")
                .fluid()
                .color(0x7a6f9e)
                .buildAndRegister();

        Metavorexin = registerGTCEuMaterial("metavorexin")
                .fluid()
                .color(0x5c3a73)
                .buildAndRegister();

        Dracotropin = registerGTCEuMaterial("dracotropin")
                .fluid()
                .color(0x4d6a59)
                .buildAndRegister();

        Pyrothyin = registerGTCEuMaterial("pyrothyin")
                .fluid()
                .color(0x8c4d3a)
                .buildAndRegister();

        GrowthHormoneComplex = registerGTCEuMaterial("growth_hormone_complex")
                .langValue("Growth-Hormone Complex")
                .fluid()
                .color(0x756b7d)
                .buildAndRegister();

        // Akreyrium Line
        LeptonCoalescingSuperalloy = registerGTCEuMaterial("lepton_coalescing_superalloy")
                .langValue("Lepton-Coalescing Superalloy")
                .ingot()
                .fluid()
                .components(ThalliumTungstate, 4, Nickel, 2, Graphene, 4,
                        Niobium, 3, Bismuth, 4)
                .color(0x80d1c8)
                .iconSet(DULL)
                .blast(b -> b.temp(5300, BlastProperty.GasTier.HIGH).blastStats(VA[LuV], 1400))
                .flags(GENERATE_PLATE, GENERATE_ROD, GENERATE_FRAME, GENERATE_FOIL)
                .buildAndRegister();

        LeptonSparseAkreyrium = registerGTCEuMaterial("lepton_sparse_akreyrium")
                .langValue("Lepton-Sparse Akreyrium")
                .fluid()
                .components(UtopianAkreyrium, 1, Mystery, 1)
                .color(0x6e6e87)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        LeptonFluxAkreyrium = registerGTCEuMaterial("lepton_flux_akreyrium")
                .langValue("Lepton-Flux Akreyrium")
                .fluid()
                .components(UtopianAkreyrium, 1, LeptonCoalescingSuperalloy, 6, Mystery, 1)
                .color(0xaca2ba)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        GrittyAkreyrium = registerGTCEuMaterial("gritty_akreyrium")
                .fluid()
                .components(UtopianAkreyrium, 1, Mystery, 1)
                .color(0x464655)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        AkreyriumPcbGraphiteNanoparticleCoolant = registerGTCEuMaterial("akreyrium_pcb_graphite_nanoparticle_coolant")
                .fluid()
                .components(PCBCoolant, 5, UtopianAkreyrium, 2, Graphite, 32)
                .color(0x676763)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        LeptonFlavourFoundationalFlux = registerGTCEuMaterial("lepton_flavour_foundational_flux")
                .langValue("Lepton-Flavour Foundational Flux")
                .fluid()
                .components(LeptonCoalescingSuperalloy, 6, Mystery, 1)
                .color(0xe5cee1)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        LightTauInfusionFlux = registerGTCEuMaterial("light_tau_infusion_flux")
                .langValue("Light §bTau §rInfusion Flux")
                .fluid()
                .components(Mystery, 1)
                .color(0xe5cee1)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        HeavyTauInfusionFlux = registerGTCEuMaterial("heavy_tau_infusion_flux")
                .langValue("Heavy §bTau §rInfusion Flux")
                .fluid()
                .components(LightTauInfusionFlux, 1)
                .color(0xdfdae9)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        SuperlightTauInfusionFlux = registerGTCEuMaterial("superlight_tau_infusion_flux")
                .langValue("Superlight §bTau §rInfusion Flux")
                .fluid()
                .components(LightTauInfusionFlux, 1)
                .color(0xd9e7f0)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        SuperheavyTauInfusionFlux = registerGTCEuMaterial("superheavy_tau_infusion_flux")
                .langValue("Superheavy §bTau §rInfusion Flux")
                .fluid()
                .components(HeavyTauInfusionFlux, 1)
                .color(0xccffff)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        EtherealTauInfusionFlux = registerGTCEuMaterial("ethereal_tau_infusion_flux")
                .langValue("Ethereal §bTau §rInfusion Flux")
                .fluid()
                .components(SuperheavyTauInfusionFlux, 2, SuperlightTauInfusionFlux, 2)
                .color(0x99ccff)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        SparseTauAkreyrium = registerGTCEuMaterial("sparse_tau_akreyrium")
                .langValue("§bTau§r Sparse Akreyrium")
                .liquid(new FluidBuilder().customStill())
                .components(UtopianAkreyrium, 1, Mystery, 1)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        DenseTauAkreyrium = registerGTCEuMaterial("dense_tau_akreyrium")
                .langValue("§bTau§r Dense Akreyrium")
                .liquid(new FluidBuilder().customStill())
                .components(UtopianAkreyrium, 1, Mystery, 1, EtherealTauInfusionFlux, 1)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        TwinklingMuonInfusionFlux = registerGTCEuMaterial("twinkling_muon_infusion_flux")
                .langValue("Twinkling §aMuon§r Infusion Flux")
                .fluid()
                .components(Mystery, 1)
                .color(0xddd8dc)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        GlowingMuonInfusionFlux = registerGTCEuMaterial("glowing_muon_infusion_flux")
                .langValue("Glowing §aMuon§r Infusion Flux")
                .fluid()
                .components(Mystery, 1)
                .color(0xd5e1d6)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        ShiningMuonInfusionFlux = registerGTCEuMaterial("shining_muon_infusion_flux")
                .langValue("Shining §aMuon§r Infusion Flux")
                .fluid()
                .components(Mystery, 1)
                .color(0xcdebd1)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        RadiantMuonInfusionFlux = registerGTCEuMaterial("radiant_muon_infusion_flux")
                .langValue("Radiant §aMuon§r Infusion Flux")
                .fluid()
                .components(Mystery, 1)
                .color(0xc5f4cb)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        BrilliantMuonInfusionFlux = registerGTCEuMaterial("brilliant_muon_infusion_flux")
                .langValue("Brilliant §aMuon§r Infusion Flux")
                .fluid()
                .components(Mystery, 1)
                .color(0xbdfec6)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        SparseMuonAkreyrium = registerGTCEuMaterial("sparse_muon_akreyrium")
                .langValue("§aMuon§r Sparse Akreyrium")
                .liquid(new FluidBuilder().customStill())
                .components(UtopianAkreyrium, 1, Mystery, 1)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        DenseMuonAkreyrium = registerGTCEuMaterial("dense_muon_akreyrium")
                .langValue("§aMuon§r Dense Akreyrium")
                .liquid(new FluidBuilder().customStill())
                .components(UtopianAkreyrium, 1, Mystery, 1, BrilliantMuonInfusionFlux, 1)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        MonoPhaseElectronInfusionFlux = registerGTCEuMaterial("mono_phase_electron_infusion_flux")
                .langValue("Mono-Phase §dElectron§r Infusion Flux")
                .fluid()
                .components(Mystery, 1)
                .color(0xe0c5f6)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        DiPhaseElectronInfusionAgent = registerGTCEuMaterial("di_phase_electron_infusion_agent")
                .langValue("Di-Phase §dElectron§r Infusion Agent")
                .dust()
                .components(Mystery, 1)
                .color(0xe0bded)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        TriPhaseElectronInfusionAgent = registerGTCEuMaterial("tri_phase_electron_infusion_agent")
                .langValue("Tri-Phase §dElectron§r Infusion Agent")
                .dust()
                .components(Mystery, 1)
                .color(0xdfb6e4)
                .iconSet(MAGNETIC)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        WeakGammaPhaseElectronInfusionAgent = registerGTCEuMaterial("weak_gamma_phase_electron_infusion_agent")
                .langValue("Weak γ-Phase §dElectron§r Infusion Agent")
                .dust()
                .components(Mystery, 1)
                .color(0x856783)
                .iconSet(MAGNETIC)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        WeakBetaPhaseElectronInfusionAgent = registerGTCEuMaterial("weak_beta_phase_electron_infusion_agent")
                .langValue("Weak β-Phase §dElectron§r Infusion Agent")
                .dust()
                .components(Mystery, 1)
                .color(0x6b4f66)
                .iconSet(MAGNETIC)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        GammaPhaseElectronInfusionAgent = registerGTCEuMaterial("gamma_phase_electron_infusion_agent")
                .langValue("γ-Phase §dElectron§r Infusion Agent")
                .dust()
                .components(Mystery, 1)
                .color(0xdeafdc)
                .iconSet(MAGNETIC)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        BetaPhaseElectronInfusionAgent = registerGTCEuMaterial("beta_phase_electron_infusion_agent")
                .langValue("β-Phase §dElectron§r Infusion Agent")
                .dust()
                .components(Mystery, 1)
                .color(0xdda8d3)
                .iconSet(MAGNETIC)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        AlphaPhaseElectronInfusionAgent = registerGTCEuMaterial("alpha_phase_electron_infusion_agent")
                .langValue("α-Phase §dElectron§r Infusion Agent")
                .dust()
                .components(Mystery, 1)
                .color(0xdc99c1)
                .iconSet(MAGNETIC)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        AlternatingPhaseElectronInfusionFlux = registerGTCEuMaterial("alternating_phase_electron_infusion_flux")
                .langValue("Alternating §dElectron§r Infusion Flux")
                .fluid()
                .components(Mystery, 1)
                .color(0xdeadb3)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        SparseElectronAkreyrium = registerGTCEuMaterial("sparse_electron_akreyrium")
                .langValue("§dElectron§r Sparse Akreyrium")
                .liquid(new FluidBuilder().customStill())
                .components(UtopianAkreyrium, 1, Mystery, 1)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        DenseElectronAkreyrium = registerGTCEuMaterial("dense_electron_akreyrium")
                .langValue("§dElectron§r Dense Akreyrium")
                .liquid(new FluidBuilder().customStill())
                .components(UtopianAkreyrium, 1, Mystery, 1)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();
    }
}
