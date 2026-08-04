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
    private final List<StarTGCropTrait> traits;
    @Getter
    private final int tier;

    public StarTGCropData(String id, String name, StarTGCropItemType materialType, int yield, String textureType,
                          StarTGCropTrait... traits) {
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
        this.traits = Arrays.asList(traits);
        this.tier = highestTier;

        gCropData.add(this);
    }

    public static void init() {
        // Tier 0
        new StarTGCropData("black_dye", "Tinctoria Umbra", DYE, 1, "one", Charred);

        new StarTGCropData("red_dye", "Tinctoria Ignis", DYE, 1, "three", Charred, Vibrant, Tough);

        new StarTGCropData("green_dye", "Tinctoria Sylva", DYE, 1, "one", Charred, Tough, Fluorescent);

        new StarTGCropData("brown_dye", "Tinctoria Terra", DYE, 1, "four", Charred, Tough);

        new StarTGCropData("blue_dye", "Tinctoria Tempestas", DYE, 1, "one", Charred, Vibrant);

        new StarTGCropData("purple_dye", "Tinctoria Nyxia", DYE, 1, "three", Charred, Vibrant, Fluorescent);

        new StarTGCropData("cyan_dye", "Tinctoria Maris", DYE, 1, "two", Charred, Vibrant, Tough, Fluorescent);

        new StarTGCropData("light_gray_dye", "Tinctoria Bruma", DYE, 1, "two", Fluorescent);

        new StarTGCropData("gray_dye", "Tinctoria Petra", DYE, 1, "four", Charred, Fluorescent);

        new StarTGCropData("pink_dye", "Tinctoria Aurora", DYE, 1, "two", Vibrant, Fluorescent);

        new StarTGCropData("lime_dye", "Tinctoria Vitae", DYE, 1, "one", Tough, Fluorescent);

        new StarTGCropData("yellow_dye", "Tinctoria Solis", DYE, 1, "three", Tough);

        new StarTGCropData("light_blue_dye", "Tinctoria Caelum", DYE, 1, "one", Vibrant);

        new StarTGCropData("magenta_dye", "Tinctoria Arcana", DYE, 1, "two", Vibrant, Tough, Fluorescent);

        new StarTGCropData("orange_dye", "Tinctoria Phoenicis", DYE, 1, "one", Vibrant, Tough);

        new StarTGCropData("white_dye", "Tinctoria Lucis", DYE, 1, "four");

        // Tier 1
        new StarTGCropData("iron", "Thumbergia Ferro", DUST, 1, "three", Fluorescent, Metallic);

        new StarTGCropData("copper", "Thumbergia Aeris", DUST, 1, "four", Vibrant, Tough, Metallic);

        new StarTGCropData("zinc", "Thumbergia Cadmiae", DUST, 1, "one", Vibrant, Metallic);

        new StarTGCropData("tin", "Thumbergia Stagni", DUST, 1, "two", Vibrant, Metallic);

        new StarTGCropData("lead", "Thumbergia Plumbum", DUST, 1, "four", Charred, Metallic);

        new StarTGCropData("nether_quartz", "Thumbergia Petram", GEM, 1, "one", Crystalline);

        new StarTGCropData("diamond", "Thumbergia Adamas", GEM, 1, "three", Vibrant, Crystalline);

        new StarTGCropData("amethyst", "Thumbergia Hyacintho", GEM, 1, "one", Charred, Vibrant, Fluorescent, Crystalline);

        new StarTGCropData("lapis", "Thumbergia Pristis", GEM, 1, "four", Charred, Vibrant, Crystalline);

        new StarTGCropData("emerald", "Thumbergia Smaragd", GEM, 1, "two", Tough, Fluorescent, Crystalline);

        new StarTGCropData("redstone", "Thumbergia Rubrum", DUST, 1, "four", Charred, Vibrant, Tough, Dusty);

        new StarTGCropData("sulfur", "Thumbergia Vulcanus", DUST, 1, "three", Vibrant, Tough, Dusty);

        new StarTGCropData("glowstone", "Thumbergia Solaris", DUST, 1, "one", Dusty);

        new StarTGCropData("ender_pearl", "Thumbergia Marganis", GEM, 1, "two", Charred, Fluorescent, Crystalline);

        // Tier 2
        new StarTGCropData("gold", "Potentilla Aurum", DUST, 1, "four", Tough, Metallic, Shiny);

        new StarTGCropData("silver", "Potentilla Argentum", DUST, 1, "one", Fluorescent, Metallic, Shiny);

        new StarTGCropData("coal", "Potentilla Calculus", GEM, 1, "three", Charred, Crystalline, Coarse);

        new StarTGCropData("sodalite", "Potentilla Azura", ORE, 1, "one", Charred, Vibrant, Metallic, Coarse);

        new StarTGCropData("pentlandite", "Potentilla Aurantiaco", ORE, 1, "four", Vibrant, Tough, Metallic, Coarse);

        new StarTGCropData("realgar", "Potentilla Coccineum", GEM, 1, "two", Charred, Vibrant, Tough, Crystalline, Coarse);

        new StarTGCropData("ruby", "Potentilla Rubore", GEM, 1, "one", Charred, Vibrant, Tough, Crystalline, Shiny);

        new StarTGCropData("sapphire", "Potentilla Sapphirus", GEM, 1, "two", Vibrant, Crystalline, Shiny);

        // Tier 3
        new StarTGCropData("spessartine", "Bergerocereus Aurangemma", GEM, 1, "one", Vibrant, Tough, Crystalline,
                Illuminating);

        new StarTGCropData("apatite", "Bergerocereus Dolosus", GEM, 1, "four", Vibrant, Crystalline, Illuminating);

        new StarTGCropData("monazite", "Bergerocereus Solus", GEM, 1, "one", Charred, Tough, Fluorescent, Crystalline,
                Illuminating);

        new StarTGCropData("topaz", "Bergerocereus Caloris", GEM, 1, "four", Vibrant, Tough, Crystalline, Shiny,
                Illuminating);

        new StarTGCropData("certus_quartz", "Bergerocereus Certibus", GEM, 1, "two", Charred, Vibrant, Tough, Fluorescent,
                Crystalline, Shiny, Illuminating);

        new StarTGCropData("lepidolite", "Bergerocereus Squamae", ORE, 1, "four", Charred, Vibrant, Tough, Metallic,
                Coarse, Mineralic);

        new StarTGCropData("pyrochlore", "Bergerocereus Viridignis", ORE, 1, "one", Vibrant, Metallic, Coarse, Mineralic);

        new StarTGCropData("pyrolusite", "Bergerocereus Lava", ORE, 1, "two", Charred, Fluorescent, Metallic, Coarse,
                Mineralic);

        new StarTGCropData("magnesite", "Bergerocereus Magnetes", ORE, 1, "one", Metallic, Coarse, Mineralic);

        new StarTGCropData("cobaltite", "Bergerocereus Parviridi", ORE, 1, "two", Charred, Vibrant, Metallic, Shiny,
                Mineralic);

        new StarTGCropData("vanadium_magnetite", "Bergerocereus Pulchritudo", ORE, 1, "two", Charred, Metallic, Shiny,
                Mineralic);

        new StarTGCropData("chromite", "Bergerocereus Pigmentatio", ORE, 1, "two", Tough, Fluorescent, Metallic, Shiny,
                Mineralic);

        new StarTGCropData("rare_earth", "Bergerocereus Terra", DUST, 1, "one", Vibrant, Fluorescent, Dusty, Shiny,
                Illuminating, Mineralic);

        // Tier 4
        new StarTGCropData("zavaritskite", "Gnaphalium Fluxus", ORE, 1, "one", Charred, Metallic, Shiny, Mineralic,
                Sulfuric);

        new StarTGCropData("beryllium", "Gnaphalium Dulcis", ORE, 1, "two", Vibrant, Metallic, Coarse, Mineralic,
                Aetheric);

        new StarTGCropData("barite", "Gnaphalium Gravibus", ORE, 1, "four", Tough, Metallic, Coarse, Mineralic, Sulfuric);

        new StarTGCropData("chalcopyrite", "Gnaphalium ", ORE, 1, "three", Fluorescent, Metallic, Coarse, Mineralic,
                Sulfuric);

        new StarTGCropData("bornite", "Gnaphalium Flaeris", ORE, 1, "two", Vibrant, Metallic, Shiny, Mineralic, Sulfuric);

        new StarTGCropData("pollucite", "Gnaphalium Geminos", ORE, 1, "two", Fluorescent, Metallic, Shiny, Mineralic,
                Aetheric);

        new StarTGCropData("cassiterite", "Gnaphalium Stannum", ORE, 1, "four", Tough, Metallic, Coarse, Mineralic,
                Aetheric);

        new StarTGCropData("tantalite", "Gnaphalium Tormentati", ORE, 1, "four", Charred, Metallic, Shiny, Mineralic,
                Aetheric);

        new StarTGCropData("nether_air", "Gnaphalium Caelitcher", LIQUID, 10, "one", Charred, Crystalline, Illuminating,
                Sulfuric);

        new StarTGCropData("ender_air", "Gnaphalium Caelinanis", LIQUID, 10, "two", Fluorescent, Crystalline, Illuminating,
                Aetheric);

        // Tier 5
        new StarTGCropData("tungstate", "Dicanthium Lupispuma", ORE, 1, "four", Vibrant, Tough, Fluorescent, Metallic,
                Coarse, Mineralic, Aetheric, Adaptive);

        new StarTGCropData("ilmenite", "Dicanthium Metallans", ORE, 1, "four", Charred, Tough, Metallic, Coarse, Mineralic,
                Sulfuric, Energetic);

        new StarTGCropData("sheldonite", "Dicanthium Argentinium", ORE, 1, "one", Tough, Metallic, Shiny, Mineralic,
                Sulfuric, Adaptive);

        new StarTGCropData("molybdenite", "Dicanthium Iniuriambum", ORE, 1, "one", Vibrant, Fluorescent, Dusty, Shiny,
                Mineralic, Sulfuric, Adaptive);

        new StarTGCropData("bauxite", "Dicanthium Lumetallum", ORE, 1, "three", Vibrant, Tough, Metallic, Coarse,
                Mineralic, Sulfuric, Adaptive);

        new StarTGCropData("pitchblende", "Dicanthium Deucaeli", ORE, 1, "three", Charred, Vibrant, Dusty, Coarse,
                Mineralic, Sulfuric, Energetic);

        new StarTGCropData("bastnasite", "Dicanthium Cultio", ORE, 1, "two", Charred, Metallic, Coarse, Mineralic,
                Aetheric, Adaptive);

        new StarTGCropData("blaze", "Dicanthium Elementignis", LIQUID, 1, "one", Charred, Dusty, Shiny, Illuminating,
                Aetheric, Energetic);

        new StarTGCropData("blizz", "Dicanthium Elementacies", LIQUID, 1, "three", Vibrant, Dusty, Shiny, Illuminating,
                Aetheric, Energetic);

        new StarTGCropData("basalz", "Dicanthium Elementerra", LIQUID, 1, "two", Tough, Dusty, Shiny, Illuminating,
                Aetheric, Energetic);

        new StarTGCropData("blitz", "Dicanthium Elementulgur", LIQUID, 1, "four", Fluorescent, Dusty, Shiny, Illuminating,
                Aetheric, Energetic);

        // Tier 6
        new StarTGCropData("naquadah", "Echinocereus Metalligrum", ORE, 1, "one", Charred, Tough, Metallic, Crystalline,
                Shiny, Mineralic, Sulfuric, Energetic, Apothic);

        new StarTGCropData("debris", "Echinocereus Rudera", DUST, 1, "three", Charred, Tough, Metallic, Coarse, Mineralic,
                Sulfuric, Adaptive, Apothic);

        // Tier 7
        new StarTGCropData("titanite", "Psoralidium Cuneus", ORE, 1, "three", Metallic, Shiny, Mineralic, Aetheric,
                Energetic, Siliceous);

        new StarTGCropData("xenotime", "Psoralidium Vanonor", ORE, 1, "four", Vibrant, Tough, Crystalline, Shiny,
                Illuminating, Sulfuric, Energetic, Apothic, Siliceous);

        new StarTGCropData("zapolite", "Psoralidium ", ORE, 1, "three", Charred, Vibrant, Fluorescent, Metallic, Coarse,
                Mineralic, Sulfuric, Adaptive, Apothic, Siliceous);

        new StarTGCropData("lautarite", "Psoralidium Resistentia", ORE, 1, "one", Charred, Vibrant, Dusty, Coarse,
                Illuminating, Aetheric, Energetic, Apothic, Siliceous);

        new StarTGCropData("crookesite", "Psoralidium Ramusculus", ORE, 1, "one", Tough, Fluorescent, Dusty, Shiny,
                Mineralic, Sulfuric, Adaptive, Siliceous);

        new StarTGCropData("kitkaite", "Psoralidium Sulfentum", ORE, 1, "four", Fluorescent, Dusty, Coarse, Mineralic,
                Aetheric, Adaptive, Siliceous);

        new StarTGCropData("celestine", "Psoralidium Coelicola", ORE, 1, "two", Vibrant, Fluorescent, Dusty, Coarse,
                Mineralic, Aetheric, Adaptive, Siliceous);
    }
}
