package com.startechnology.start_core.api.gcrop;

import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.data.gcrops.StarTTraitData;
import com.startechnology.start_core.utils.StarTStringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record StarTGCropTrait(String id, int tier, int frequency, int alleleCount,
                              boolean recessive, StarTTraitData.GenomeType genomeType, String symbolOverwrite) {

    public StarTGCropTrait(String id, int tier, int frequency, int alleleCount, boolean recessive,
                           StarTTraitData.GenomeType genomeType, String symbolOverwrite) {
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

    public static final Map<String, StarTGCropTrait> TRAITS = new HashMap<>();
    public static final Map<String, String> langData = new HashMap<>();
    public static final List<String> traitHasDescription = new ArrayList<>();

    public StarTGCropTrait(String id, int tier, int frequency, int alleleCount,
                           StarTTraitData.GenomeType genomeType) {
        this(id, tier, frequency, alleleCount, false, genomeType, "");
    }

    public StarTGCropTrait(String id, int tier, int frequency, boolean recessive,
                           StarTTraitData.GenomeType genomeType) {
        this(id, tier, frequency, 2, recessive, genomeType, "");
    }

    public StarTGCropTrait(String id, int tier, int frequency, StarTTraitData.GenomeType genomeType) {
        this(id, tier, frequency, 2, false, genomeType, "");
    }

    public StarTGCropTrait(String id, int tier, int frequency, StarTTraitData.GenomeType genomeType,
                           String symbolOverwrite) {
        this(id, tier, frequency, 2, false, genomeType, symbolOverwrite);
    }

    public void addTraitDescription(String description) {
        String descriptionKey = String.format("behaviour.start_core.trait.%s.description", this.id);
        langData.put(descriptionKey, description);
        traitHasDescription.add(this.id);
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

    public String getSortingString() {
        return String.format("%s-%s", this.tier, this.id);
    }
}
