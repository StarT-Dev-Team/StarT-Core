package com.startechnology.start_core.api.gcrop;

import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.startechnology.start_core.data.gcrops.StarTTraitData;
import com.startechnology.start_core.item.gcrops.StarTGCropItems;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.startechnology.start_core.api.gcrop.StarTGCropTrait.TRAITS;
import static com.startechnology.start_core.item.gcrops.StarTGCropItems.GCROP_MALFORMED;

public class StarTGCropTraits {

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

    public static List<StarTGCropTrait> getTraitsByType(StarTTraitData.GenomeType genomeType) {
        return TRAITS.values().stream()
                .filter(trait -> trait.genomeType() == genomeType)
                .toList();
    }

    public static List<StarTGCropTrait> getTraitsByType(StarTTraitData.GenomeType genomeType,
                                                        List<StarTGCropTrait> traits) {
        return traits.stream()
                .filter(trait -> trait.genomeType() == genomeType)
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

    public static final Comparator<StarTGCropTrait> TRAIT_COMPARATOR = Comparator
            .comparing(StarTGCropTrait::getSortingString);
}
