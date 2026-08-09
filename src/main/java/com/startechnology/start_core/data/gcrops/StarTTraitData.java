package com.startechnology.start_core.data.gcrops;

import com.startechnology.start_core.api.gcrop.*;

public class StarTTraitData {

    public enum GenomeType {
        RESOURCE,
        PRODUCTION,
        AUXILIARY,
        CLIMATE,
    }

    public static void init() {
        // Resource Traits
        Charred = new StarTGCropTrait("charred", 0, 3000, GenomeType.RESOURCE);

        Vibrant = new StarTGCropTrait("vibrant", 0, 3000, GenomeType.RESOURCE);

        Tough = new StarTGCropTrait("tough", 0, 3000, GenomeType.RESOURCE);

        Fluorescent = new StarTGCropTrait("fluorescent", 0, 3000, GenomeType.RESOURCE);

        Metallic = new StarTGCropTrait("metallic", 1, 2500, GenomeType.RESOURCE);

        Crystalline = new StarTGCropTrait("crystalline", 1, 2500, GenomeType.RESOURCE);

        Dusty = new StarTGCropTrait("dusty", 1, 2500, GenomeType.RESOURCE);

        Woody = new StarTGCropTrait("woody", 1, 500, GenomeType.RESOURCE);

        Coarse = new StarTGCropTrait("coarse", 2, 2000, GenomeType.RESOURCE);

        Shiny = new StarTGCropTrait("shiny", 2, 2000, GenomeType.RESOURCE);

        Illuminating = new StarTGCropTrait("illuminating", 3, 2000, GenomeType.RESOURCE);

        Mineralic = new StarTGCropTrait("mineralic", 3, 2000, GenomeType.RESOURCE);

        Sulfuric = new StarTGCropTrait("sulfuric", 4, 1500, GenomeType.RESOURCE);

        Aetheric = new StarTGCropTrait("aetheric", 4, 1500, GenomeType.RESOURCE);

        Energetic = new StarTGCropTrait("energetic", 5, 1000, GenomeType.RESOURCE);

        Adaptive = new StarTGCropTrait("adaptive", 5, 1000, GenomeType.RESOURCE);

        Apothic = new StarTGCropTrait("apothic", 6, 500, GenomeType.RESOURCE);

        Siliceous = new StarTGCropTrait("siliceous", 7, 200, GenomeType.RESOURCE);

        // Production Traits
        Quickened = new StarTGCropTrait("quickened", 1, 2000, GenomeType.PRODUCTION);
        Quickened.addTraitDescription("- Multiplies recipe duration by a factor of §a0.9§r (multiplicative)");

        Thirsty = new StarTGCropTrait("thirsty", 1, 1500, GenomeType.PRODUCTION);
        Thirsty.addTraitDescription("- Multiplies recipe fluid consumption by a factor of §a1.2§r (multiplicative)");

        Speedy = new StarTGCropTrait("speedy", 2, 1500, GenomeType.PRODUCTION);
        Speedy.addTraitDescription("- Multiplies recipe duration by a factor of §a0.9§r (multiplicative)");

        Slow = new StarTGCropTrait("slow", 2, 1000, GenomeType.PRODUCTION);
        Slow.addTraitDescription("- Multiplies recipe duration by a factor of §a1.2§r (multiplicative)");

        Fast = new StarTGCropTrait("fast", 3, 1000, GenomeType.PRODUCTION);
        Fast.addTraitDescription("- Multiplies recipe duration by a factor of §a0.9§r (multiplicative)");

        Stunted = new StarTGCropTrait("stunted", 3, 500, GenomeType.PRODUCTION);
        Stunted.addTraitDescription("- Multiplies recipe duration by a factor of §a1.2§r (multiplicative)");

        Gluttonous = new StarTGCropTrait("gluttonous", 3, 500, GenomeType.PRODUCTION);
        Gluttonous.addTraitDescription("- Multiplies recipe input amounts by a factor of §a1.2§r (multiplicative)");

        Enormous = new StarTGCropTrait("enormous", 4, 500, GenomeType.PRODUCTION);
        Enormous.addTraitDescription("- Gives a §a60%§r chance to get §a+2§r on your maximum fruits (cumulative)");

        Shriveled = new StarTGCropTrait("shriveled", 4, 400, GenomeType.PRODUCTION, "Sr");
        Shriveled.addTraitDescription("""
                - Gives §a-2§r on minimum fruits (cumulative)
                - Gives §a-3§r on maximum fruits (cumulative)
                """);

        Branching = new StarTGCropTrait("branching", 5, 500, GenomeType.PRODUCTION);
        Branching.addTraitDescription("""
                - Gives §a3 60%§r chances for §a+2§r on maximum fruits (cumulative)
                - Gives §a2 70%§r chances for §a+1§r on minimum fruits (cumulative)
                """);

        Proliferating = new StarTGCropTrait("proliferating", 5, 500, GenomeType.PRODUCTION);
        Proliferating.addTraitDescription("""
                - Multiplies recipe consumption by a factor of §a2§r (multiplicative)
                - Multiplies recipe duration by a factor of §a1.15§r (multiplicative)
                - Gives §a+4§r on minimum fruits (cumulative)
                - Gives §a+2§r on maximum fruits (cumulative)
                """);

        Empowered = new StarTGCropTrait("empowered", 6, 100, GenomeType.PRODUCTION);
        Empowered.addTraitDescription("Lowers the recipe voltage by 1§r (cumulative)");

        // 8x on min and max fruit (multiplicative), 60% duration increase (multiplicative), 10x input consumption
        // (multiplicative)
        Sprawling = new StarTGCropTrait("sprawling", 7, 50, GenomeType.PRODUCTION, "Sw");
        Sprawling.addTraitDescription("""
                - Multiplies recipe consumption by a factor of §a10§r (multiplicative)
                - Multiplies recipe duration by a factor of §a1.6§r (multiplicative)
                - Multiplies minimum fruits by a factor of §a8§r (multiplicative)
                - Multiplies maximum fruits by a factor of §a8§r (multiplicative)
                """);

        // 3x on inputs (multiplicative), 4x on outputs (multiplicative), 5x duration multiplier (multiplicative),
        Autotroph = new StarTGCropTrait("autotroph", 7, 50, GenomeType.PRODUCTION);
        Autotroph.addTraitDescription("""
                - Multiplies recipe consumption by a factor of §a3§r (multiplicative)
                - Multiplies recipe duration by a factor of §a5§r (multiplicative)
                - Multiplies minimum fruits by a factor of §a4§r (multiplicative)
                - Multiplies maximum fruits by a factor of §a4§r (multiplicative)
                """);

        // Auxiliary Traits
        Nocturnal = new StarTGCropTrait("nocturnal", 2, 2000, GenomeType.AUXILIARY, "Nc");
        Nocturnal.addTraitDescription("- Adjusts the recipe to only run during the night");

        Diurnal = new StarTGCropTrait("diurnal", 4, 1500, GenomeType.AUXILIARY);
        Diurnal.addTraitDescription("- Adjusts the recipe to ignore time-based conditions");

        Early = new StarTGCropTrait("early", 4, 100, GenomeType.AUXILIARY);
        Early.addTraitDescription("""
                - Adjusts the recipe to output flowers instead of fruits
                - Multiplies the recipe duration by a factor of §a0.7§r (multiplicative)
                """);

        // Climate Traits
        Frosty = new StarTGCropTrait("frosty", 1, 1000, 1, GenomeType.CLIMATE);

        Scorching = new StarTGCropTrait("scorching", 1, 1000, 1, GenomeType.CLIMATE);

        Tropical = new StarTGCropTrait("tropical", 1, 1000, 1, GenomeType.CLIMATE);

        Desertic = new StarTGCropTrait("desertic", 1, 1000, 1, GenomeType.CLIMATE);

        Damp = new StarTGCropTrait("damp", 1, 1000, 1, GenomeType.CLIMATE);
    }

    // Tier 0
    public static StarTGCropTrait Charred;
    public static StarTGCropTrait Vibrant;
    public static StarTGCropTrait Tough;
    public static StarTGCropTrait Fluorescent;

    // Tier 1
    public static StarTGCropTrait Metallic;
    public static StarTGCropTrait Crystalline;
    public static StarTGCropTrait Dusty;
    public static StarTGCropTrait Woody;

    public static StarTGCropTrait Quickened;
    public static StarTGCropTrait Thirsty;

    public static StarTGCropTrait Frosty;
    public static StarTGCropTrait Scorching;
    public static StarTGCropTrait Tropical;
    public static StarTGCropTrait Desertic;
    public static StarTGCropTrait Damp;

    // Tier 2
    public static StarTGCropTrait Coarse;
    public static StarTGCropTrait Shiny;

    public static StarTGCropTrait Speedy;
    public static StarTGCropTrait Slow;

    public static StarTGCropTrait Nocturnal;

    // Tier 3
    public static StarTGCropTrait Illuminating;
    public static StarTGCropTrait Mineralic;

    public static StarTGCropTrait Fast;
    public static StarTGCropTrait Stunted;
    public static StarTGCropTrait Gluttonous;

    // Tier 4
    public static StarTGCropTrait Sulfuric;
    public static StarTGCropTrait Aetheric;

    public static StarTGCropTrait Enormous;
    public static StarTGCropTrait Shriveled;

    public static StarTGCropTrait Diurnal;
    public static StarTGCropTrait Early;

    // Tier 5
    public static StarTGCropTrait Energetic;
    public static StarTGCropTrait Adaptive;

    public static StarTGCropTrait Branching;
    public static StarTGCropTrait Proliferating;

    // Tier 6
    public static StarTGCropTrait Apothic;

    public static StarTGCropTrait Empowered;

    // Tier 7
    public static StarTGCropTrait Siliceous;

    public static StarTGCropTrait Sprawling;
    public static StarTGCropTrait Autotroph;
}
