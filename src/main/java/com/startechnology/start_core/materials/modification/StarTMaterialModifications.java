package com.startechnology.start_core.materials.modification;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.BlastProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.DustProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.FluidPipeProperties;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.FluidProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.IngotProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialStack;
import com.gregtechceu.gtceu.api.fluids.FluidBuilder;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKey;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.*;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet.*;
import static com.startechnology.start_core.materials.StarTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;

import static com.startechnology.start_core.StarTCore.LOGGER;

public class StarTMaterialModifications {

    public static void register() {
        StarTGCropProcessingMaterials.register();
        modifyGTMaterials();
        applyCustomFormulas();
    }

    private static void modifyGTMaterials() {
        if (!NaquadahEnriched.hasProperty(PropertyKey.FLUID_PIPE)) {
            NaquadahEnriched.setProperty(PropertyKey.FLUID_PIPE,
                    new FluidPipeProperties(8000, 500, true, true, true, false));
        }
        // Formulas
        Glowstone.setFormula("(Si(FeS2)5(CrAl2O3)Hg3)Au");
        Netherite.setFormula("Nr");
        Netherite.setMaterialARGB(0x1a0d00);
        Netherite.setMaterialIconSet(DULL);
        Netherite.addFlags(GENERATE_ROD, GENERATE_FOIL, DISABLE_DECOMPOSITION);
        Netherite.setComponents(
                new MaterialStack(Debris, 1),
                new MaterialStack(Gold, 1));

        NetherStar.setFormula("✧");
        NetherStar.addFlags(GENERATE_FOIL);

        RareEarth.setFormula("?");
        Thorium.setFormula("Th²³⁰");
        Neptunium.setFormula("Np²³⁷");
        Fermium.setFormula("Fm²⁵⁷");
        Americium.setFormula("Am²⁴⁵");
        PlatinumGroupSludge.setFormula("Pt?");
        EchoShard.setFormula("Ec");
        EchoShard.addFlags(GENERATE_LENS, GENERATE_FOIL);

        // Flags
        Lead.addFlags(GENERATE_GEAR);
        Silver.addFlags(GENERATE_GEAR);
        Naquadah.addFlags(GENERATE_DENSE, GENERATE_FRAME);
        NaquadahEnriched.addFlags(
                GENERATE_DENSE,
                GENERATE_ROTOR,
                GENERATE_GEAR,
                GENERATE_SMALL_GEAR,
                GENERATE_FRAME,
                GENERATE_LONG_ROD);
        Naquadria.addFlags(GENERATE_DENSE);
        Neutronium.addFlags(GENERATE_FOIL, GENERATE_SMALL_GEAR, GENERATE_ROTOR, GENERATE_DENSE);
        Europium.addFlags(GENERATE_SPRING_SMALL, GENERATE_BOLT_SCREW);
        Zirconium.addFlags(GENERATE_FINE_WIRE);
        Hafnium.addFlags(GENERATE_FINE_WIRE);
        Rhenium.addFlags(GENERATE_FINE_WIRE);
        RedSteel.addFlags(GENERATE_ROD, GENERATE_FRAME);
        SterlingSilver.addFlags(GENERATE_ROD, GENERATE_FRAME);
        CertusQuartz.addFlags(GENERATE_LENS);
        Copper.addFlags(GENERATE_GEAR);
        VanadiumGallium.addFlags(GENERATE_FINE_WIRE);
        Titanium.addFlags(GENERATE_FOIL);
        RhodiumPlatedPalladium.addFlags(GENERATE_FRAME, GENERATE_FOIL);
        Palladium.addFlags(GENERATE_ROTOR);
        Darmstadtium.addFlags(GENERATE_FRAME, GENERATE_FOIL);
        RutheniumTriniumAmericiumNeutronate.addFlags(GENERATE_FINE_WIRE);
        Gold.addFlags(GENERATE_GEAR);
        Electrum.addFlags(GENERATE_GEAR);
        BlueAlloy.addFlags(GENERATE_GEAR);
        Cupronickel.addFlags(GENERATE_SPRING_SMALL);
        Kanthal.addFlags(GENERATE_SPRING_SMALL);
        Nichrome.addFlags(GENERATE_SPRING_SMALL);
        TantalumCarbide.addFlags(GENERATE_FOIL);
        Dysprosium.addFlags(GENERATE_LONG_ROD);
        Trinium.addFlags(GENERATE_FINE_WIRE);
        NaquadahAlloy.addFlags(GENERATE_ROUND);
        Ruridit.addFlags(GENERATE_SMALL_GEAR, GENERATE_ROTOR);
        Iridium.addFlags(GENERATE_ROTOR);
        Iron.addFlags(GENERATE_FOIL);
        WroughtIron.addFlags(GENERATE_FRAME);
        RedAlloy.addFlags(GENERATE_SPRING);
        UraniumTriplatinum.addFlags(GENERATE_FINE_WIRE);
        SamariumIronArsenicOxide.addFlags(GENERATE_FINE_WIRE);

        // Periodic table element additions
        Zirconium.setProperty(PropertyKey.INGOT, new IngotProperty());
        setFluid(Zirconium, FluidStorageKeys.LIQUID);
        setBlast(Zirconium, 8100, BlastProperty.GasTier.HIGH, VA[UV], 1200);

        Tellurium.setProperty(PropertyKey.INGOT, new IngotProperty());
        setBlast(Tellurium, 10700, BlastProperty.GasTier.HIGH, VA[UHV], 900);

        Polonium.setProperty(PropertyKey.INGOT, new IngotProperty());
        setBlast(Polonium, 13400, BlastProperty.GasTier.HIGH, VHA[UIV], 1350);

        Astatine.setProperty(PropertyKey.INGOT, new IngotProperty());
        setBlast(Astatine, 12800, BlastProperty.GasTier.HIGH, VA[UHV], 1400);

        Hafnium.setProperty(PropertyKey.INGOT, new IngotProperty());
        setFluid(Hafnium, FluidStorageKeys.LIQUID);
        setBlast(Hafnium, 11900, BlastProperty.GasTier.HIGH, VA[UV], 750);

        Rhenium.setProperty(PropertyKey.INGOT, new IngotProperty());
        setFluid(Rhenium, FluidStorageKeys.LIQUID);
        setBlast(Rhenium, 14800, BlastProperty.GasTier.HIGHEST, VA[UIV], 1200);

        Seaborgium.setProperty(PropertyKey.INGOT, new IngotProperty());
        setFluid(Seaborgium, FluidStorageKeys.LIQUID);
        setBlast(Seaborgium, 13300, BlastProperty.GasTier.HIGH, VA[UEV], 1500);

        Flerovium.setProperty(PropertyKey.INGOT, new IngotProperty());
        setFluid(Flerovium, FluidStorageKeys.LIQUID);
        setBlast(Flerovium, 12200, BlastProperty.GasTier.HIGH, VA[UHV], 1200);

        Dysprosium.setProperty(PropertyKey.INGOT, new IngotProperty());
        setBlast(Dysprosium, 6200, BlastProperty.GasTier.MID, VHA[LuV], 144);

        setBlast(Lutetium, 6600, BlastProperty.GasTier.MID, VA[LuV], 120);

        Selenium.setProperty(PropertyKey.DUST, new DustProperty());
        setFluid(Selenium, FluidStorageKeys.LIQUID);

        Strontium.setProperty(PropertyKey.DUST, new DustProperty());
        Thallium.setProperty(PropertyKey.DUST, new DustProperty());
        Neptunium.setProperty(PropertyKey.DUST, new DustProperty());

        Fermium.setProperty(PropertyKey.DUST, new DustProperty());
        setFluid(Fermium, FluidStorageKeys.LIQUID);

        setFluid(Oganesson, FluidStorageKeys.GAS, FluidStorageKeys.PLASMA);
    }

    private static void applyCustomFormulas() {
        changeFormula(NetheriteTrisulfateComplex, "[*Nr*(SO4)3](OH)2");
        changeFormula(NetheriteHexammineSulfate, "[*Nr*(NH3)6]SO4");
        changeFormula(NetheriteGoldSkystoneAlloy, "Nr4(SkC2)2(Sk(SiAu2)2)");
        changeFormula(NetheriteCertusQuartzSkystoneAlloy, "Nr4(SkC2)2(Sk(SiO2)2)");
        changeFormula(LowSaturationVoidicExcression, "[∅-]");
        changeFormula(ModerateSaturationVoidicExcression, "[∅]");
        changeFormula(HighSaturationVoidicExcression, "[∅+]");
        changeFormula(LethargicVoidicSlurry, "?[∅-]*");
        changeFormula(TemperedVoidicSlurry, "?[∅]*");
        changeFormula(VibrantVoidicSlurry, "?[∅+]*");
        changeFormula(AlphaStateVoidSludge, "?α∅");
        changeFormula(BetaStateVoidSludge, "?β∅");
        changeFormula(GammaStateVoidSludge, "?γ∅");
        changeFormula(DeltaStateVoidSludge, "?δ∅");
        changeFormula(EpsilonStateVoidSludge, "?ε∅");
        changeFormula(ZetaStateVoidSludge, "?ζ∅");
        changeFormula(AlphaStateVoidResidue, "α∅");
        changeFormula(BetaStateVoidResidue, "β∅");
        changeFormula(GammaStateVoidResidue, "γ∅");
        changeFormula(DeltaStateVoidResidue, "δ∅");
        changeFormula(EpsilonStateVoidResidue, "ε∅");
        changeFormula(ZetaStateVoidResidue, "ζ∅");
        changeFormula(OrderCentricVoid, "⚖∅");
        changeFormula(ChaosCentricVoid, "✹∅");
        changeFormula(VoidicWasteResidue, "?∅?");
        changeFormula(SparseElectronAkreyrium, "Ak(?e?)?");
        changeFormula(DenseElectronAkreyrium, "Ak(e)?");
        changeFormula(SparseMuonAkreyrium, "Ak(?μ?)?");
        changeFormula(DenseMuonAkreyrium, "Ak(μ)?");
        changeFormula(SparseTauAkreyrium, "Ak(?τ?)?");
        changeFormula(DenseTauAkreyrium, "Ak(τ)?");
        changeFormula(LeptonSparseAkreyrium, "Ak(?ℓ?)?");
        changeFormula(SilverSulfate, "Ag2(SO4)");
        changeFormula(AcidicWater, "H2O*");
        changeFormula(HydroiodicAcid, "HI*");
    }

    private static void setFluid(Material mat, FluidStorageKey... keys) {
        FluidProperty prop = new FluidProperty();
        for (FluidStorageKey key : keys) {
            prop.getStorage().enqueueRegistration(key, new FluidBuilder());
        }
        mat.setProperty(PropertyKey.FLUID, prop);
    }

    private static void setBlast(Material mat, int temp, BlastProperty.GasTier gasTier, int voltage, int duration) {
        mat.setProperty(PropertyKey.BLAST, new BlastProperty(temp, gasTier, voltage, duration, -1, -1));
    }

    public static void changeFormula(Material material, String formula) {
        if (material != null) {
            material.setFormula(formula);
            return;
        }
        LOGGER.debug("Tried to give a null material the fomula %s!", formula);
    }
}
