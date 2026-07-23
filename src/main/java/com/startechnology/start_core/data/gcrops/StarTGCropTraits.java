package com.startechnology.start_core.data.gcrops;

import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.gcrop.StarTGCropGene;
import com.startechnology.start_core.api.gcrop.StarTGCropManager;
import com.startechnology.start_core.api.gcrop.StarTGCropGenome;
import com.startechnology.start_core.item.StarTGCropItems;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.ItemStack;

import java.util.*;

import static com.startechnology.start_core.item.StarTGCropItems.GCROP_MALFORMED;

public class StarTGCropTraits {

    public static final Comparator<StarTGCropTraits.StarTGCropTrait> TRAIT_COMPARATOR = Comparator
            .comparing(StarTGCropTraits.StarTGCropTrait::name);

    public enum GenomeType {
        RESOURCE,
        PRODUCTION,
        AUXILIARY
    }

    public static final Map<String, StarTGCropTrait> TRAITS = new HashMap<>();

    public record StarTGCropTrait(String name, String symbol, int tier, int frequency, int alleleCount,
                                  boolean recessive, GenomeType genomeType) {

        public StarTGCropTrait(String name, String symbol, int tier, int frequency, int alleleCount, boolean recessive,
                               GenomeType genomeType) {
            this.name = name;
            this.symbol = symbol;
            this.tier = tier;
            this.frequency = frequency;
            this.alleleCount = alleleCount;
            this.recessive = recessive;
            this.genomeType = genomeType;
            TRAITS.put(name.toLowerCase(), this);
        }

        public StarTGCropTrait(String name, String symbol, int tier, int frequency, int alleleCount,
                               GenomeType genomeType) {
            this(name, symbol, tier, frequency, alleleCount, false, genomeType);
        }

        public StarTGCropTrait(String name, String symbol, int tier, int frequency, boolean recessive,
                               GenomeType genomeType) {
            this(name, symbol, tier, frequency, 2, recessive, genomeType);
        }

        public StarTGCropTrait(String name, String symbol, int tier, int frequency, GenomeType genomeType) {
            this(name, symbol, tier, frequency, 2, false, genomeType);
        }

        /**
         * Generates a random number between 0 and alleleCount (inclusive), based on the frequency of the trait.
         *
         * @param alleleCount int
         * @return int
         */
        public int runTraitFrequencyRandomGene(int alleleCount) {
            int traitCount = 0;
            for (int i = 0; i < alleleCount; i++) {
                if (StarTCore.RNG.nextIntBetweenInclusive(1, 10000) < this.frequency) traitCount++;
            }
            return traitCount;
        }
    }

    public static StarTGCropTrait getTrait(String name) {
        return TRAITS.get(name.toLowerCase());
    }

    public static List<StarTGCropTrait> getTraitsByTier(int tier) {
        return getTraitsBetweenTiersInclusive(tier, tier);
    }

    public static List<StarTGCropTrait> getTraitsBelowTierInclusive(int tier) {
        return getTraitsBetweenTiersInclusive(0, tier);
    }

    public static List<StarTGCropTrait> getTraitsBetweenTiersInclusive(int lower, int upper) {
        return TRAITS.values().stream()
                .filter(trait -> trait.tier() <= upper && trait.tier() >= lower)
                .toList();
    }

    public static List<StarTGCropTrait> getTraitsByType(GenomeType genomeType) {
        return TRAITS.values().stream()
                .filter(trait -> trait.genomeType == genomeType)
                .toList();
    }

    public static List<StarTGCropTrait> getTraitsByType(GenomeType genomeType, List<StarTGCropTrait> traits) {
        return traits.stream()
                .filter(trait -> trait.genomeType == genomeType)
                .toList();
    }

    public static ItemStack getCropWithTraits(List<StarTGCropGene> resourceGenome,
                                              List<StarTGCropGene> productionGenome,
                                              List<StarTGCropGene> auxiliaryGenome) {
        List<StarTGCropTrait> allResourceTraits = new ArrayList<>();

        for (StarTGCropGene gene : resourceGenome) {
            allResourceTraits.add(gene.getTrait());
        }

        allResourceTraits.sort(TRAIT_COMPARATOR);

        ItemEntry<ComponentItem> gCropItem = StarTGCropItems.getGCropByGenome(allResourceTraits);
        ItemStack newGCrop = (gCropItem == null) ? new ItemStack(GCROP_MALFORMED.get()) : gCropItem.asStack();

        StarTGCropGenome newGenome = new StarTGCropGenome(resourceGenome, productionGenome,
                auxiliaryGenome);

        StarTGCropManager.writeGCRopGenomeToItem(newGCrop.getOrCreateTag(), newGenome);

        return newGCrop;
    }

    public static void init() {
        // Resource Traits
        Charred = new StarTGCropTrait("Charred", "Ch", 0, 3000, GenomeType.RESOURCE);

        Vibrant = new StarTGCropTrait("Vibrant", "Vi", 0, 3000, GenomeType.RESOURCE);

        Tough = new StarTGCropTrait("Tough", "Th", 0, 3000, GenomeType.RESOURCE);

        Fluorescent = new StarTGCropTrait("Fluorescent", "Fl", 0, 3000, GenomeType.RESOURCE);

        Metallic = new StarTGCropTrait("Metallic", "Me", 1, 2500, GenomeType.RESOURCE);

        Crystalline = new StarTGCropTrait("Crystalline", "Cr", 1, 2500, GenomeType.RESOURCE);

        Dusty = new StarTGCropTrait("Dusty", "Du", 1, 2500, GenomeType.RESOURCE);

        Woody = new StarTGCropTrait("Woody", "Wo", 1, 500, GenomeType.RESOURCE);

        Coarse = new StarTGCropTrait("Coarse", "Co", 2, 2000, GenomeType.RESOURCE);

        Shiny = new StarTGCropTrait("Shiny", "Sh", 2, 2000, GenomeType.RESOURCE);

        Illuminating = new StarTGCropTrait("Illuminating", "Il", 3, 2000, GenomeType.RESOURCE);

        Mineralic = new StarTGCropTrait("Mineralic", "Mi", 3, 2000, GenomeType.RESOURCE);

        Sulfuric = new StarTGCropTrait("Sulfuric", "Su", 4, 1500, GenomeType.RESOURCE);

        Aetheric = new StarTGCropTrait("Aetheric", "Ae", 4, 1500, GenomeType.RESOURCE);

        Energetic = new StarTGCropTrait("Energetic", "En", 5, 1000, GenomeType.RESOURCE);

        Adaptive = new StarTGCropTrait("Adaptive", "Ad", 5, 1000, GenomeType.RESOURCE);

        Apothic = new StarTGCropTrait("Apothic", "Ap", 6, 500, GenomeType.RESOURCE);

        Siliceous = new StarTGCropTrait("Siliceous", "Si", 7, 200, GenomeType.RESOURCE);

        // Production Traits
        Speedy = new StarTGCropTrait("Speedy", "Sp", 0, 2000, GenomeType.PRODUCTION);

        // Auxiliary Traits
        Dry = new StarTGCropTrait("Dry", "Dy", 0, 3000, GenomeType.AUXILIARY);

        Nocturnal = new StarTGCropTrait("Nocturnal", "Nc", 2, 2000, GenomeType.AUXILIARY);

        Diurnal = new StarTGCropTrait("Diurnal", "Di", 4, 1500, GenomeType.AUXILIARY);
    }

    // Tier 0
    public static StarTGCropTrait Charred;
    public static StarTGCropTrait Vibrant;
    public static StarTGCropTrait Tough;
    public static StarTGCropTrait Fluorescent;

    public static StarTGCropTrait Speedy;

    public static StarTGCropTrait Dry;

    // Tier 1
    public static StarTGCropTrait Metallic;
    public static StarTGCropTrait Crystalline;
    public static StarTGCropTrait Dusty;
    public static StarTGCropTrait Woody;

    // Tier 2
    public static StarTGCropTrait Coarse;
    public static StarTGCropTrait Shiny;

    public static StarTGCropTrait Nocturnal;

    // Tier 3
    public static StarTGCropTrait Illuminating;
    public static StarTGCropTrait Mineralic;

    // Tier 4
    public static StarTGCropTrait Sulfuric;
    public static StarTGCropTrait Aetheric;

    public static StarTGCropTrait Diurnal;

    // Tier 5
    public static StarTGCropTrait Energetic;
    public static StarTGCropTrait Adaptive;

    // Tier 6
    public static StarTGCropTrait Apothic;

    // Tier 7
    public static StarTGCropTrait Siliceous;
}
