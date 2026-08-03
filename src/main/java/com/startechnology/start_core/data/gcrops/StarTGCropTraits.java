package com.startechnology.start_core.data.gcrops;

import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.gcrop.StarTGCropGene;
import com.startechnology.start_core.api.gcrop.StarTGCropManager;
import com.startechnology.start_core.api.gcrop.StarTGCropGenome;
import com.startechnology.start_core.item.gcrops.StarTGCropItems;
import com.startechnology.start_core.utils.StarTStringUtils;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.ItemStack;

import java.util.*;

import static com.startechnology.start_core.item.gcrops.StarTGCropItems.GCROP_MALFORMED;

public class StarTGCropTraits {

    public static final Comparator<StarTGCropTraits.StarTGCropTrait> TRAIT_COMPARATOR = Comparator
            .comparing(StarTGCropTraits.StarTGCropTrait::id);

    public enum GenomeType {
        RESOURCE,
        PRODUCTION,
        AUXILIARY,
        CLIMATE,
    }

    public static final Map<String, StarTGCropTrait> TRAITS = new HashMap<>();
    public static final Map<String, String> langData = new HashMap<>();

    public record StarTGCropTrait(String id, int tier, int frequency, int alleleCount,
                                  boolean recessive, GenomeType genomeType, String symbolOverwrite) {

        public StarTGCropTrait(String id, int tier, int frequency, int alleleCount, boolean recessive,
                               GenomeType genomeType, String symbolOverwrite) {
            this.id = id;
            this.tier = tier;
            this.frequency = frequency;
            this.alleleCount = alleleCount;
            this.recessive = recessive;
            this.genomeType = genomeType;
            this.symbolOverwrite = symbolOverwrite;

            TRAITS.put(id.toLowerCase(), this);

            String nameKey = String.format("behaviour.start_core.trait.%s.name", id);
            String symbolKey = String.format("behaviour.start_core.trait.%s.symbol", id);
            String name = StarTStringUtils.snakeCaseToSentence(id);
            String symbol = StarTStringUtils.snakeCaseToSentence(id).substring(0, 2);
            if (!symbolOverwrite.isEmpty()) symbol = symbolOverwrite;
            langData.put(nameKey, name);
            langData.put(symbolKey, symbol);
        }

        public StarTGCropTrait(String id, int tier, int frequency, int alleleCount,
                               GenomeType genomeType) {
            this(id, tier, frequency, alleleCount, false, genomeType, "");
        }

        public StarTGCropTrait(String id, int tier, int frequency, boolean recessive,
                               GenomeType genomeType) {
            this(id, tier, frequency, 2, recessive, genomeType, "");
        }

        public StarTGCropTrait(String id, int tier, int frequency, GenomeType genomeType) {
            this(id, tier, frequency, 2, false, genomeType, "");
        }

        public StarTGCropTrait(String id, int tier, int frequency, GenomeType genomeType, String symbolOverwrite) {
            this(id, tier, frequency, 2, false, genomeType, symbolOverwrite);
        }

        /**
         * Generates a random number between 0 and alleleCount (inclusive), based on the frequency of the trait.
         *
         * @return int
         */
        public int runTraitFrequencyRandomGene() {
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
                                              List<StarTGCropGene> auxiliaryGenome,
                                              StarTGCropGene climateGene) {
        List<StarTGCropTrait> allResourceTraits = new ArrayList<>();

        for (StarTGCropGene gene : resourceGenome) {
            allResourceTraits.add(gene.getTrait());
        }

        allResourceTraits.sort(TRAIT_COMPARATOR);

        ItemEntry<ComponentItem> gCropItem = StarTGCropItems.getGCropByGenome(allResourceTraits);
        ItemStack newGCrop = (gCropItem == null) ? new ItemStack(GCROP_MALFORMED.get()) : gCropItem.asStack();

        StarTGCropGenome newGenome;

        if (climateGene == null) newGenome = new StarTGCropGenome(resourceGenome, productionGenome,
                auxiliaryGenome);
        else newGenome = new StarTGCropGenome(resourceGenome, productionGenome,
                auxiliaryGenome, climateGene);

        StarTGCropManager.writeGCRopGenomeToItem(newGCrop.getOrCreateTag(), newGenome);

        return newGCrop;
    }

    public static ItemStack getCropWithTraits(List<StarTGCropGene> resourceGenome,
                                              List<StarTGCropGene> productionGenome,
                                              List<StarTGCropGene> auxiliaryGenome) {
        return getCropWithTraits(resourceGenome, productionGenome, auxiliaryGenome, null);
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
        // 10% duration reduction (multiplicative)
        Quickened = new StarTGCropTrait("quickened", 1, 2000, GenomeType.PRODUCTION);

        // 20% increase in fluid consumption (multiplicative)
        Thirsty = new StarTGCropTrait("thirsty", 1, 1500, GenomeType.PRODUCTION);

        // 10% duration reduction (multiplicative)
        Speedy = new StarTGCropTrait("speedy", 2, 1500, GenomeType.PRODUCTION);

        // 20% duration increase (multiplicative)
        Slow = new StarTGCropTrait("slow", 2, 1000, GenomeType.PRODUCTION);

        // 10% duration reduction (multiplicative)
        Fast = new StarTGCropTrait("fast", 3, 1000, GenomeType.PRODUCTION);

        // 20% duration increase (multiplicative)
        Stunted = new StarTGCropTrait("stunted", 3, 500, GenomeType.PRODUCTION);

        // 20% input consumption increase (multiplicative)
        Gluttonous = new StarTGCropTrait("gluttonous", 3, 500, GenomeType.PRODUCTION);

        // 60% chance for +2 on max fruit (cumulative)
        Enormous = new StarTGCropTrait("enormous", 4, 500, GenomeType.PRODUCTION);

        // -2 on min fruits (cumulative), -3 on max fruits (cumulative)
        Shriveled = new StarTGCropTrait("shriveled", 4, 400, GenomeType.PRODUCTION, "Sr");

        // 3x 60% chance for +2 on max fruit (cumulative) and 2x 70% chance for +1 on min fruit (cumulative)
        Branching = new StarTGCropTrait("branching", 5, 500, GenomeType.PRODUCTION);

        // 2x input consumption, 15% duration increase (multiplicative), +4 on min fruit (cumulative), +2 on max fruit
        // (cumulative)
        Proliferating = new StarTGCropTrait("proliferating", 5, 500, GenomeType.PRODUCTION);

        // -1 energy tier
        Empowered = new StarTGCropTrait("empowered", 6, 100, GenomeType.PRODUCTION);

        // 8x on min and max fruit (multiplicative), 60% duration increase (multiplicative), 10x input consumption
        // (multiplicative)
        Sprawling = new StarTGCropTrait("sprawling", 7, 50, GenomeType.PRODUCTION, "Sw");

        // 3x on inputs (multiplicative), 4x on outputs (multiplicative), 5x duration multiplier (multiplicative),
        Autotroph = new StarTGCropTrait("autotroph", 7, 50, GenomeType.PRODUCTION);

        // Auxiliary Traits
        // nighttime only
        Nocturnal = new StarTGCropTrait("nocturnal", 2, 2000, GenomeType.AUXILIARY, "Nc");

        // no time requirement
        Diurnal = new StarTGCropTrait("diurnal", 4, 1500, GenomeType.AUXILIARY);

        // fruit -> flower, 30% duration reduction (multiplicative)
        Early = new StarTGCropTrait("early", 4, 100, GenomeType.AUXILIARY);

        // Climate Traits
        Frosty = new StarTGCropTrait("frost", 1, 1000, 1, GenomeType.CLIMATE);

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
