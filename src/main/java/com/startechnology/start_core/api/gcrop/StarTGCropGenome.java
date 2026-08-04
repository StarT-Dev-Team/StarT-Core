package com.startechnology.start_core.api.gcrop;

import com.startechnology.start_core.data.gcrops.StarTTraitData;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class StarTGCropGenome {

    public static final String GCROP_RESOURCE_GENOME_NBT_TAG = "gcrop_resource_genome";
    public static final String GCROP_PRODUCTION_GENOME_NBT_TAG = "gcrop_production_genome";
    public static final String GCROP_AUXILIARY_GENOME_NBT_TAG = "gcrop_auxiliary_genome";
    public static final String GCROP_CLIMATE_GENOME_NBT_TAG = "gcrop_climate_genome";

    @NotNull
    @Getter
    private List<StarTGCropGene> resourceGenome = new ArrayList<>();
    @NotNull
    @Getter
    private List<StarTGCropGene> productionGenome = new ArrayList<>();
    @NotNull
    @Getter
    private List<StarTGCropGene> auxiliaryGenome = new ArrayList<>();
    @Getter
    private StarTGCropGene climateGene = null;

    public StarTGCropGenome(@NotNull List<StarTGCropGene> resourceGenome,
                            @NotNull List<StarTGCropGene> productionGenome,
                            @NotNull List<StarTGCropGene> auxiliaryGenome,
                            StarTGCropGene climateGene) {
        resourceGenome.sort(GENE_COMPARATOR);
        productionGenome.sort(GENE_COMPARATOR);
        auxiliaryGenome.sort(GENE_COMPARATOR);
        this.resourceGenome = resourceGenome;
        this.productionGenome = productionGenome;
        this.auxiliaryGenome = auxiliaryGenome;
        this.climateGene = climateGene;
    }

    public StarTGCropGenome(@NotNull List<StarTGCropGene> resourceGenome,
                            @NotNull List<StarTGCropGene> productionGenome,
                            @NotNull List<StarTGCropGene> auxiliaryGenome) {
        resourceGenome.sort(GENE_COMPARATOR);
        productionGenome.sort(GENE_COMPARATOR);
        auxiliaryGenome.sort(GENE_COMPARATOR);
        this.resourceGenome = resourceGenome;
        this.productionGenome = productionGenome;
        this.auxiliaryGenome = auxiliaryGenome;
    }

    public static final Comparator<StarTGCropGene> GENE_COMPARATOR = Comparator
            .comparing(gene -> gene.getTrait().getSortingString());

    public boolean hasTrait(StarTGCropTrait trait) {
        StarTTraitData.GenomeType genomeType = trait.genomeType();
        List<StarTGCropGene> necessaryGenome = new ArrayList<>();

        switch (genomeType) {
            case RESOURCE -> necessaryGenome = resourceGenome;
            case PRODUCTION -> necessaryGenome = productionGenome;
            case AUXILIARY -> necessaryGenome = auxiliaryGenome;
            case CLIMATE -> {
                if (climateGene == null) return false;
                return climateGene.getTrait().equals(trait);
            }
        }

        if (necessaryGenome.isEmpty()) return false;

        for (StarTGCropGene gene : necessaryGenome) {
            if (gene.getTrait().equals(trait)) return true;
        }
        return false;
    }

    public boolean hasTrait(String traitName) {
        var trait = StarTGCropTraits.getTrait(traitName);
        if (trait == null) return false;
        return this.hasTrait(trait);
    }

    public boolean isEmpty() {
        if (!resourceGenome.isEmpty()) return false;
        if (!productionGenome.isEmpty()) return false;
        if (!auxiliaryGenome.isEmpty()) return false;
        if (climateGene != null) return false;

        return true;
    }

    public static String getPrettyTrait(String value, int tier) {
        String colourCode = switch (tier) {
            case -1 -> "§4"; // unknown trait
            case 1 -> "§9";
            case 2 -> "§1";
            case 3 -> "§5";
            case 4 -> "§2";
            case 5 -> "§a";
            case 6 -> "§c";
            case 7 -> "§e";
            default -> "§7";
        };

        return String.format("%s%s§r", colourCode, value);
    }

    public static MutableComponent prettyGenomeGCropTraits(List<StarTGCropGene> genome, boolean full) {
        return genome.stream()
                .map(
                        gene -> {
                            StarTGCropTrait trait = gene.getTrait();
                            String traitSymbol;
                            int traitTier;
                            if (trait == null) {
                                traitSymbol = full ? "Unknown" : "??";
                                traitTier = -1;
                            } else {
                                traitSymbol = Component.translatable(String.format("behaviour.start_core.trait.%s.%s",
                                        trait.id(), full ? "name" : "symbol")).getString();
                                traitTier = trait.tier();
                            }
                            return Component.translatable(getPrettyTrait(traitSymbol, traitTier));
                        })
                .reduce(Component.literal(""), MutableComponent::append);
    }

    public static MutableComponent prettyGCropGene(StarTGCropGene gene) {
        StarTGCropTrait trait = gene.getTrait();
        String traitName;
        int alleleCount = gene.getDominantAlleles();
        String squares;
        int traitTier;
        if (trait == null) {
            traitName = "Unknown";
            traitTier = -1;
            squares = "NaN";
        } else {
            squares = StringUtils.repeat('■', alleleCount) + StringUtils.repeat('□', trait.alleleCount() - alleleCount);
            traitName = Component.translatable(String.format("behaviour.start_core.trait.%s.name", trait.id()))
                    .getString();
            traitTier = trait.tier();
        }
        return Component.literal(getPrettyTrait(String.format("%s: %s", traitName, squares), traitTier));
    }

    public StarTGCropGenome(CompoundTag gCropGenomeCompound) {
        ListTag resourceGenomeList = gCropGenomeCompound.getList(GCROP_RESOURCE_GENOME_NBT_TAG, Tag.TAG_STRING);
        resourceGenomeList.forEach(gene -> {
            StarTGCropGene newGene = new StarTGCropGene(gene.getAsString());
            if (newGene.getTrait() != null) this.resourceGenome.add(newGene);
        });

        ListTag productionGenomeList = gCropGenomeCompound.getList(GCROP_PRODUCTION_GENOME_NBT_TAG,
                Tag.TAG_STRING);
        productionGenomeList.forEach(gene -> {
            StarTGCropGene newGene = new StarTGCropGene(gene.getAsString());
            if (newGene.getTrait() != null) this.productionGenome.add(newGene);
        });

        ListTag auxiliaryGenomeList = gCropGenomeCompound.getList(GCROP_AUXILIARY_GENOME_NBT_TAG, Tag.TAG_STRING);
        auxiliaryGenomeList.forEach(gene -> {
            StarTGCropGene newGene = new StarTGCropGene(gene.getAsString());
            if (newGene.getTrait() != null) this.auxiliaryGenome.add(newGene);
        });

        String climateGenome = gCropGenomeCompound.getString(GCROP_CLIMATE_GENOME_NBT_TAG);
        if (!climateGenome.equals("empty")) this.climateGene = new StarTGCropGene(climateGenome);
    }

    public CompoundTag toCompoundTag() {
        CompoundTag gCropGenomeCompound = new CompoundTag();

        ListTag resourceGenomeList = new ListTag();
        for (StarTGCropGene gene : resourceGenome) {
            resourceGenomeList.add(StringTag.valueOf(gene.toRawGene()));
        }

        ListTag productionGenomeList = new ListTag();
        for (StarTGCropGene gene : productionGenome) {
            productionGenomeList.add(StringTag.valueOf(gene.toRawGene()));
        }

        ListTag auxiliaryGenomeList = new ListTag();
        for (StarTGCropGene gene : auxiliaryGenome) {
            auxiliaryGenomeList.add(StringTag.valueOf(gene.toRawGene()));
        }

        gCropGenomeCompound.put(GCROP_RESOURCE_GENOME_NBT_TAG, resourceGenomeList);
        gCropGenomeCompound.put(GCROP_PRODUCTION_GENOME_NBT_TAG, productionGenomeList);
        gCropGenomeCompound.put(GCROP_AUXILIARY_GENOME_NBT_TAG, auxiliaryGenomeList);
        if (climateGene != null)
            gCropGenomeCompound.put(GCROP_CLIMATE_GENOME_NBT_TAG, StringTag.valueOf(climateGene.toRawGene()));
        else gCropGenomeCompound.put(GCROP_CLIMATE_GENOME_NBT_TAG, StringTag.valueOf("empty"));

        return gCropGenomeCompound;
    }
}
