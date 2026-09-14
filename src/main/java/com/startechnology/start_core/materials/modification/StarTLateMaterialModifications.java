package com.startechnology.start_core.materials.modification;

import static com.startechnology.start_core.materials.StarTMaterials.*;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;

import static com.startechnology.start_core.StarTCore.LOGGER;

public class StarTLateMaterialModifications {

    public static void register() {
        StarTGCropProcessingMaterials.register();
        applyCustomFormulas();
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

    public static void changeFormula(Material material, String formula) {
        if (material != null) {
            material.setFormula(formula);
            return;
        }
        LOGGER.debug("Tried to give a null material the fomula %s!", formula);
    }
}
