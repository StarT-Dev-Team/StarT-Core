package com.startechnology.start_core.api.gcrop;

import com.startechnology.start_core.data.gcrops.StarTGCropTraits;
import com.startechnology.start_core.data.gcrops.StarTGCropTraits.StarTGCropTrait;
import lombok.Getter;

@Getter
public class StarTGCropGene {

    private final StarTGCropTrait trait;
    private final int dominantAlleles;

    public StarTGCropGene(String rawGene) {
        var splitGenome = rawGene.split("-");
        this.trait = StarTGCropTraits.getTrait(splitGenome[0]);
        this.dominantAlleles = Integer.parseInt(splitGenome[1]);
    };

    public StarTGCropGene(StarTGCropTrait trait, int dominantAlleles) {
        this.trait = trait;
        this.dominantAlleles = dominantAlleles;
    };

    /*
     * Converts genes into raw string data for NBT storage
     * <Trait>-<DominantCount>
     * Example:
     * StarTGCropTrait Charred, int 1; => "Charred-1";
     */
    public String toRawGene() {
        return String.format("%s-%s", this.trait.name(), String.valueOf(this.dominantAlleles));
    }
}
