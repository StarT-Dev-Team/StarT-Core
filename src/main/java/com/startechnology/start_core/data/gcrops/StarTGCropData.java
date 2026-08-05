package com.startechnology.start_core.data.gcrops;

import com.startechnology.start_core.api.gcrop.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.startechnology.start_core.data.gcrops.StarTTraitData.*;
import static com.startechnology.start_core.api.gcrop.StarTGCropItemType.*;

public class StarTGCropData {

    public static List<StarTGCropData> gCropData = new ArrayList<>();

    @Getter
    private final String id;
    @Getter
    private final String name;
    @Getter
    private final StarTGCropItemType materialType;
    @Getter
    private final int yield;
    @Getter
    private final String textureType;
    @Getter
    private final String resultMaterial;
    @Getter
    private final List<StarTGCropTrait> traits;
    @Getter
    private final int tier;

    public StarTGCropData(String id, String name, StarTGCropItemType materialType, int yield, String textureType,
                          String resultMaterial, StarTGCropTrait... traits) {
        int highestTier = 0;
        for (var trait : traits) {
            int traitTier = trait.tier();
            if (traitTier > highestTier) highestTier = traitTier;
        }

        this.id = id;
        this.name = name;
        this.materialType = materialType;
        this.yield = yield;
        this.textureType = textureType;
        this.resultMaterial = resultMaterial;
        this.traits = Arrays.asList(traits);
        this.tier = highestTier;

        gCropData.add(this);
    }

    public StarTGCropData(String id, String name, StarTGCropItemType materialType, int yield, String textureType,
                          StarTGCropTrait... traits) {
        this(id, name, materialType, yield, textureType, null, traits);
    }

    public StarTGCropData(String id, String name, StarTGCropItemType materialType, String textureType,
                          StarTGCropTrait... traits) {
        this(id, name, materialType, 1, textureType, null, traits);
    }

    public StarTGCropData(String id, String name, StarTGCropItemType materialType, String textureType,
                          String resultMaterial, StarTGCropTrait... traits) {
        this(id, name, materialType, 1, textureType, resultMaterial, traits);
    }

    public static void init() {
        // Tier 0
        new StarTGCropData("black_dye", "Tinctoria Umbra", DYE, "one", Charred);

        new StarTGCropData("red_dye", "Tinctoria Ignis", DYE, "three", Charred, Vibrant, Tough);

        new StarTGCropData("green_dye", "Tinctoria Sylva", DYE, "one", Charred, Tough, Fluorescent);

        new StarTGCropData("brown_dye", "Tinctoria Terra", DYE, "four", Charred, Tough);

        new StarTGCropData("blue_dye", "Tinctoria Tempestas", DYE, "one", Charred, Vibrant);

        new StarTGCropData("purple_dye", "Tinctoria Nyxia", DYE, "three", Charred, Vibrant, Fluorescent);

        new StarTGCropData("cyan_dye", "Tinctoria Maris", DYE, "two", Charred, Vibrant, Tough, Fluorescent);

        new StarTGCropData("light_gray_dye", "Tinctoria Bruma", DYE, "two", Fluorescent);

        new StarTGCropData("gray_dye", "Tinctoria Petra", DYE, "four", Charred, Fluorescent);

        new StarTGCropData("pink_dye", "Tinctoria Aurora", DYE, "two", Vibrant, Fluorescent);

        new StarTGCropData("lime_dye", "Tinctoria Vitae", DYE, "one", Tough, Fluorescent);

        new StarTGCropData("yellow_dye", "Tinctoria Solis", DYE, "three", Tough);

        new StarTGCropData("light_blue_dye", "Tinctoria Caelum", DYE, "one", Vibrant);

        new StarTGCropData("magenta_dye", "Tinctoria Arcana", DYE, "two", Vibrant, Tough, Fluorescent);

        new StarTGCropData("orange_dye", "Tinctoria Phoenicis", DYE, "one", Vibrant, Tough);

        new StarTGCropData("white_dye", "Tinctoria Lucis", DYE, "four");

        // Tier 1
        new StarTGCropData("iron", "Thumbergia Ferro", DUST, "three", Fluorescent, Metallic);

        new StarTGCropData("copper", "Thumbergia Aeris", DUST, "four", Vibrant, Tough, Metallic);

        new StarTGCropData("zinc", "Thumbergia Cadmiae", DUST, "one", Vibrant, Metallic);

        new StarTGCropData("tin", "Thumbergia Stagni", DUST, "two", Vibrant, Metallic);

        new StarTGCropData("lead", "Thumbergia Plumbum", DUST, "four", Charred, Metallic);

        new StarTGCropData("nether_quartz", "Thumbergia Petram", GEM, "one", Crystalline);

        new StarTGCropData("diamond", "Thumbergia Adamas", GEM, "three", Vibrant, Crystalline);

        new StarTGCropData("amethyst", "Thumbergia Hyacintho", GEM, "one", Charred, Vibrant, Fluorescent, Crystalline);

        new StarTGCropData("lapis", "Thumbergia Pristis", GEM, "four", Charred, Vibrant, Crystalline);

        new StarTGCropData("emerald", "Thumbergia Smaragd", GEM, "two", Tough, Fluorescent, Crystalline);

        new StarTGCropData("redstone", "Thumbergia Rubrum", DUST, "four", Charred, Vibrant, Tough, Dusty);

        new StarTGCropData("sulfur", "Thumbergia Vulcanus", DUST, "three", Vibrant, Tough, Dusty);

        new StarTGCropData("glowstone", "Thumbergia Solaris", DUST, "one", Dusty);

        new StarTGCropData("ender_pearl", "Thumbergia Marganis", GEM, "two", Charred, Fluorescent, Crystalline);

        // Tier 2
        new StarTGCropData("gold", "Potentilla Aurum", DUST, "four", Tough, Metallic, Shiny);

        new StarTGCropData("silver", "Potentilla Argentum", DUST, "one", Fluorescent, Metallic, Shiny);

        new StarTGCropData("coal", "Potentilla Calculus", GEM, "three", Charred, Crystalline, Coarse);

        new StarTGCropData("sodalite", "Potentilla Azura", ORE, "one", Charred, Vibrant, Metallic, Coarse);

        new StarTGCropData("pentlandite", "Potentilla Aurantiaco", ORE, "four", Vibrant, Tough, Metallic, Coarse);

        new StarTGCropData("realgar", "Potentilla Coccineum", GEM, "two", Charred, Vibrant, Tough, Crystalline, Coarse);

        new StarTGCropData("ruby", "Potentilla Rubore", GEM, "one", Charred, Vibrant, Tough, Crystalline, Shiny);

        new StarTGCropData("sapphire", "Potentilla Sapphirus", GEM, "two", Vibrant, Crystalline, Shiny);

        // Tier 3
        new StarTGCropData("spessartine", "Bergerocereus Aurangemma", GEM, "one", Vibrant, Tough, Crystalline,
                Illuminating);

        new StarTGCropData("apatite", "Bergerocereus Dolosus", GEM, "four", Vibrant, Crystalline, Illuminating);

        new StarTGCropData("monazite", "Bergerocereus Solus", GEM, "one", Charred, Tough, Fluorescent, Crystalline,
                Illuminating);

        new StarTGCropData("topaz", "Bergerocereus Caloris", GEM, "four", Vibrant, Tough, Crystalline, Shiny,
                Illuminating);

        new StarTGCropData("certus_quartz", "Bergerocereus Certibus", GEM, "two", Charred, Vibrant, Tough, Fluorescent,
                Crystalline, Shiny, Illuminating);

        new StarTGCropData("lepidolite", "Bergerocereus Squamae", ORE, "four", Charred, Vibrant, Tough, Metallic,
                Coarse, Mineralic);

        new StarTGCropData("pyrochlore", "Bergerocereus Viridignis", ORE, "one", Vibrant, Metallic, Coarse, Mineralic);

        new StarTGCropData("pyrolusite", "Bergerocereus Lava", ORE, "two", Charred, Fluorescent, Metallic, Coarse,
                Mineralic);

        new StarTGCropData("magnesite", "Bergerocereus Magnetes", ORE, "one", Metallic, Coarse, Mineralic);

        new StarTGCropData("cobaltite", "Bergerocereus Parviridi", ORE, "two", Charred, Vibrant, Metallic, Shiny,
                Mineralic);

        new StarTGCropData("vanadium_magnetite", "Bergerocereus Pulchritudo", ORE, "two", Charred, Metallic, Shiny,
                Mineralic);

        new StarTGCropData("chromite", "Bergerocereus Pigmentatio", ORE, "two", Tough, Fluorescent, Metallic, Shiny,
                Mineralic);

        new StarTGCropData("rare_earth", "Bergerocereus Terra", DUST, "one", Vibrant, Fluorescent, Dusty, Shiny,
                Illuminating, Mineralic);

        // Tier 4
        new StarTGCropData("zavaritskite", "Gnaphalium Fluxus", ORE, "one", Charred, Metallic, Shiny, Mineralic,
                Sulfuric);

        new StarTGCropData("beryllium", "Gnaphalium Dulcis", ORE, "two", Vibrant, Metallic, Coarse, Mineralic,
                Aetheric);

        new StarTGCropData("barite", "Gnaphalium Gravibus", ORE, "four", Tough, Metallic, Coarse, Mineralic, Sulfuric);

        new StarTGCropData("chalcopyrite", "Gnaphalium ", ORE, "three", Fluorescent, Metallic, Coarse, Mineralic,
                Sulfuric);

        new StarTGCropData("bornite", "Gnaphalium Flaeris", ORE, "two", Vibrant, Metallic, Shiny, Mineralic, Sulfuric);

        new StarTGCropData("pollucite", "Gnaphalium Geminos", ORE, "two", Fluorescent, Metallic, Shiny, Mineralic,
                Aetheric);

        new StarTGCropData("cassiterite", "Gnaphalium Stannum", ORE, "four", Tough, Metallic, Coarse, Mineralic,
                Aetheric);

        new StarTGCropData("tantalite", "Gnaphalium Tormentati", ORE, "four", Charred, Metallic, Shiny, Mineralic,
                Aetheric);

        new StarTGCropData("nether_air", "Gnaphalium Caelitcher", LIQUID, 10, "one", Charred, Crystalline, Illuminating,
                Sulfuric);

        new StarTGCropData("ender_air", "Gnaphalium Caelinanis", LIQUID, 10, "two", Fluorescent, Crystalline,
                Illuminating,
                Aetheric);

        // Tier 5
        new StarTGCropData("tungstate", "Dicanthium Lupispuma", ORE, "four", Vibrant, Tough, Fluorescent, Metallic,
                Coarse, Mineralic, Aetheric, Adaptive);

        new StarTGCropData("ilmenite", "Dicanthium Metallans", ORE, "four", Charred, Tough, Metallic, Coarse, Mineralic,
                Sulfuric, Energetic);

        new StarTGCropData("sheldonite", "Dicanthium Argentinium", ORE, "one", Tough, Metallic, Shiny, Mineralic,
                Sulfuric, Adaptive);

        new StarTGCropData("molybdenite", "Dicanthium Iniuriambum", ORE, "one", Vibrant, Fluorescent, Dusty, Shiny,
                Mineralic, Sulfuric, Adaptive);

        new StarTGCropData("bauxite", "Dicanthium Lumetallum", ORE, "three", Vibrant, Tough, Metallic, Coarse,
                Mineralic, Sulfuric, Adaptive);

        new StarTGCropData("pitchblende", "Dicanthium Deucaeli", ORE, "three", Charred, Vibrant, Dusty, Coarse,
                Mineralic, Sulfuric, Energetic);

        new StarTGCropData("bastnasite", "Dicanthium Cultio", ORE, "two", Charred, Metallic, Coarse, Mineralic,
                Aetheric, Adaptive);

        new StarTGCropData("blaze", "Dicanthium Elementignis", LIQUID, "one", Charred, Dusty, Shiny, Illuminating,
                Aetheric, Energetic);

        new StarTGCropData("blizz", "Dicanthium Elementacies", LIQUID, "three", Vibrant, Dusty, Shiny, Illuminating,
                Aetheric, Energetic);

        new StarTGCropData("basalz", "Dicanthium Elementerra", LIQUID, "two", Tough, Dusty, Shiny, Illuminating,
                Aetheric, Energetic);

        new StarTGCropData("blitz", "Dicanthium Elementulgur", LIQUID, "four", Fluorescent, Dusty, Shiny, Illuminating,
                Aetheric, Energetic);

        // Tier 6
        new StarTGCropData("naquadah", "Echinocereus Metalligrum", ORE, "one", Charred, Tough, Metallic, Crystalline,
                Shiny, Mineralic, Sulfuric, Energetic, Apothic);

        new StarTGCropData("debris", "Echinocereus Rudera", DUST, "three", Charred, Tough, Metallic, Coarse, Mineralic,
                Sulfuric, Adaptive, Apothic);

        // Tier 7
        new StarTGCropData("titanite", "Psoralidium Cuneus", LIQUID, "three", "titanite_residue", Metallic, Shiny,
                Mineralic, Aetheric,
                Energetic, Siliceous);

        new StarTGCropData("xenotime", "Psoralidium Vanonor", LIQUID, "four", "rare_earth_leach_mixture", Vibrant,
                Tough, Crystalline, Shiny,
                Illuminating, Sulfuric, Energetic, Apothic, Siliceous);

        new StarTGCropData("zapolite", "Psoralidium ", DUST, "three", "zapolgium_oxide", Charred, Vibrant, Fluorescent,
                Metallic, Coarse,
                Mineralic, Sulfuric, Adaptive, Apothic, Siliceous);

        new StarTGCropData("lautarite", "Psoralidium Resistentia", ORE, "one", Charred, Vibrant, Dusty, Coarse,
                Illuminating, Aetheric, Energetic, Apothic, Siliceous);

        new StarTGCropData("crookesite", "Psoralidium Ramusculus", ORE, "one", Tough, Fluorescent, Dusty, Shiny,
                Mineralic, Sulfuric, Adaptive, Siliceous);

        new StarTGCropData("kitkaite", "Psoralidium Sulfentum", ORE, "four", Fluorescent, Dusty, Coarse, Mineralic,
                Aetheric, Adaptive, Siliceous);

        new StarTGCropData("celestine", "Psoralidium Coelicola", ORE, "two", Vibrant, Fluorescent, Dusty, Coarse,
                Mineralic, Aetheric, Adaptive, Siliceous);
    }
}
