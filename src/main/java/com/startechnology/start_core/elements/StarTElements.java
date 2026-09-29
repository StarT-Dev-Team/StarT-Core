package com.startechnology.start_core.elements;

import com.gregtechceu.gtceu.api.data.chemical.Element;
import com.gregtechceu.gtceu.common.data.GTElements;

public class StarTElements {

    // Abydos elements
    public static Element Zapolgium;
    public static Element Akreyrium;
    public static Element Dysprosium;

    // End elements
    public static Element Xeproda;
    public static Element Rhexis;
    public static Element Chalyblux;
    public static Element Voidic;
    public static Element Dragon;
    public static Element Riftic;
    public static Element Faetic;

    // Extra elements
    public static Element Mystery;
    public static Element Excited;
    public static Element Skystone;
    public static Element Fluix;
    public static Element Star;
    public static Element Soul;

    // Gate elements
    public static Element PurifiedNaquadah;
    public static Element Echo;

    // Nether elements
    public static Element Mythril;
    public static Element Adamantine;
    public static Element Estalt;
    public static Element EnrichedEstalt;
    public static Element Calamatium;
    public static Element Isovol;
    public static Element AncientNetherite;
    public static Element Aurourium;

    // Netherite elements
    public static Element Debris;
    public static Element Netherite;
    public static Element PureNetherite;

    // Nuclear elements
    public static Element Uranium233;
    public static Element Plutonium238;
    public static Element Plutonium244;
    public static Element Americium241;
    public static Element Curium244;
    public static Element Californium252;
    public static Element Einsteinium253;

    public static void init() {
        // Abydos elements
        Zapolgium = register(138, 217, "zapolgium", "Zg");
        Akreyrium = register(164, 280, "akreyrium", "Ak");
        Dysprosium = register(66, 98, "dysprosium", "Dy");

        // End elements
        Xeproda = register(146, 245, "xeproda", "Xp");
        Rhexis = register(147, 247, "rhexis", "Rx");
        Chalyblux = register(148, 249, "chalyblux", "Cx");
        Voidic = register(-1, 540, "voidic", "∅");
        Dragon = register(-1, 500, "dragon", "🜍");
        Riftic = register(-1, 800, "riftic", "🌀");
        Faetic = register(-1, 400, "faetic", "𐦍");

        // Extra elements
        Mystery = register(-1, 1, "mystery", "?");
        Excited = register(-1, 1, "excited", "*");
        Skystone = register(-1, 12, "skystone", "Sk");
        Fluix = register(-1, 18, "fluix", "Fx");
        Star = register(-1, 152, "star", "✧");
        Soul = register(-1, 1, "soul", "Ⱉ");

        // Gate elements
        PurifiedNaquadah = register(154, 250, "purified_naquadah", "Nq-");
        Echo = register(152, 252, "echo_r", "Ec");

        // Nether elements
        Mythril = register(133, 193, "mythril", "My");
        Adamantine = register(132, 182, "adamantine", "Ad");
        Estalt = register(134, 199, "estalt", "El");
        EnrichedEstalt = register(134, 212, "enriched_estalt", "El+");
        Calamatium = register(135, 211, "calamatium", "Ct");
        Isovol = register(136, 221, "isovol", "Is");
        AncientNetherite = register(124, 195, "ancient_netherite", "Nr+");
        Aurourium = register(151, 251, "aurourium", "*A*");

        // Netherite elements
        Debris = register(122, 138, "debris", "?");
        Netherite = register(124, 191, "netherite", "Nr");
        PureNetherite = register(124, 187, "pure_netherite", "*Nr*");

        // Nuclear elements
        Uranium233 = register(92, 141, "uranium_233", "U²³³");
        Plutonium238 = register(94, 144, "plutonium_238", "Pu²³⁸");
        Plutonium244 = register(94, 150, "plutonium_244", "Pu²⁴⁴");
        Americium241 = register(95, 146, "americium_241", "Am²⁴¹");
        Curium244 = register(96, 148, "curium_244", "Cm²⁴⁴");
        Californium252 = register(98, 154, "californium_252", "Cf²⁵²");
        Einsteinium253 = register(99, 154, "einsteinium_253", "Es²⁵³");

        StarTElementModifications.init();
    }

    public static Element register(int protons, int neutrons, String name, String symbol) {
        return GTElements.createAndRegister(protons, neutrons, -1, null, name, symbol, false);
    }
}
