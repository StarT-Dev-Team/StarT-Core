package com.startechnology.start_core.data.gcrops;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StarTGCropTraits {

    public static final Comparator<StarTGCropTraits.StarTGCropTrait> TRAIT_COMPARATOR = Comparator
            .comparing(StarTGCropTraits.StarTGCropTrait::name);

    public enum GenomeType {
        RESOURCE,
        PRODUCTION,
        AUXILIARY
    }

    public static final Map<String, StarTGCropTrait> TRAITS = new HashMap<>();

    public record StarTGCropTrait(String name, String symbol, int tier, int frequency, GenomeType genomeType) {

        public StarTGCropTrait(String name, String symbol, int tier, int frequency, GenomeType genomeType) {
            this.name = name;
            this.symbol = symbol;
            this.tier = tier;
            this.frequency = frequency;
            this.genomeType = genomeType;
            TRAITS.put(name, this);
        }
    }

    public static StarTGCropTrait getTrait(String name) {
        return TRAITS.get(name);
    }

    public static List<StarTGCropTrait> getTraitsByTier(int tier) {
        return TRAITS.values().stream()
                .filter(trait -> trait.tier() == tier)
                .toList();
    }

    public static void init() {
        // Resource Traits
        Charred = new StarTGCropTrait("Charred", "Ch", 0, 3000, GenomeType.RESOURCE);

        Vibrant = new StarTGCropTrait("Vibrant", "Vi", 0, 3000, GenomeType.RESOURCE);

        Tough = new StarTGCropTrait("Tough", "Th", 0, 3000, GenomeType.RESOURCE);

        Fluorescent = new StarTGCropTrait("Fluorescent", "Fl", 0, 3000, GenomeType.RESOURCE);

        Metallic = new StarTGCropTrait("Metallic", "Me", 1, 3000, GenomeType.RESOURCE);

        Crystalline = new StarTGCropTrait("Crystalline", "Cr", 1, 3000, GenomeType.RESOURCE);

        Dusty = new StarTGCropTrait("Dusty", "Du", 1, 3000, GenomeType.RESOURCE);

        Woody = new StarTGCropTrait("Woody", "Wo", 1, 3000, GenomeType.RESOURCE);

        Coarse = new StarTGCropTrait("Coarse", "Co", 2, 3000, GenomeType.RESOURCE);

        Shiny = new StarTGCropTrait("Shiny", "Sh", 2, 3000, GenomeType.RESOURCE);

        // Production Traits
        Speedy = new StarTGCropTrait("Speedy", "Sp", 0, 3000, GenomeType.PRODUCTION);
    }

    // Tier 0
    public static StarTGCropTrait Charred;
    public static StarTGCropTrait Vibrant;
    public static StarTGCropTrait Tough;
    public static StarTGCropTrait Fluorescent;

    public static StarTGCropTrait Speedy;

    // Tier 1
    public static StarTGCropTrait Metallic;
    public static StarTGCropTrait Crystalline;
    public static StarTGCropTrait Dusty;
    public static StarTGCropTrait Woody;

    // Tier 2
    public static StarTGCropTrait Coarse;
    public static StarTGCropTrait Shiny;
}
