package com.startechnology.start_core.api.gcrop;

import com.startechnology.start_core.data.gcrops.StarTGCropTraits;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;

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
    @NotNull
    @Getter
    private StarTGCropGene climateGene = new StarTGCropGene(StarTGCropTraits.None, 1);

    public StarTGCropGenome(@NotNull List<StarTGCropGene> resourceGenome,
                            @NotNull List<StarTGCropGene> productionGenome,
                            @NotNull List<StarTGCropGene> auxiliaryGenome,
                            @NotNull StarTGCropGene climateGene) {
        this.resourceGenome = resourceGenome;
        this.productionGenome = productionGenome;
        this.auxiliaryGenome = auxiliaryGenome;
        this.climateGene = climateGene;
    }

    public StarTGCropGenome(@NotNull List<StarTGCropGene> resourceGenome,
                            @NotNull List<StarTGCropGene> productionGenome,
                            @NotNull List<StarTGCropGene> auxiliaryGenome) {
        this.resourceGenome = resourceGenome;
        this.productionGenome = productionGenome;
        this.auxiliaryGenome = auxiliaryGenome;
    }

    public boolean hasTrait(StarTGCropTraits.StarTGCropTrait trait) {
        StarTGCropTraits.GenomeType genomeType = trait.genomeType();
        List<StarTGCropGene> necessaryGenome = new ArrayList<>();

        switch (genomeType) {
            case RESOURCE -> necessaryGenome = resourceGenome;
            case PRODUCTION -> necessaryGenome = productionGenome;
            case AUXILIARY -> necessaryGenome = auxiliaryGenome;
            case CLIMATE -> {
                if (climateGene.getTrait().equals("empty")) return false;
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

    public StarTGCropGenome(CompoundTag gCropGenomeCompound) {
        ListTag resourceGenomeList = gCropGenomeCompound.getList(GCROP_RESOURCE_GENOME_NBT_TAG, Tag.TAG_STRING);
        resourceGenomeList.forEach(gene -> this.resourceGenome.add(new StarTGCropGene(gene.getAsString())));

        ListTag productionGenomeList = gCropGenomeCompound.getList(GCROP_PRODUCTION_GENOME_NBT_TAG,
                Tag.TAG_STRING);
        productionGenomeList.forEach(gene -> this.productionGenome.add(new StarTGCropGene(gene.getAsString())));

        ListTag auxiliaryGenomeList = gCropGenomeCompound.getList(GCROP_AUXILIARY_GENOME_NBT_TAG, Tag.TAG_STRING);
        auxiliaryGenomeList.forEach(gene -> this.auxiliaryGenome.add(new StarTGCropGene(gene.getAsString())));

        String climateGenome = gCropGenomeCompound.getString(GCROP_CLIMATE_GENOME_NBT_TAG);
        this.climateGene = new StarTGCropGene(climateGenome);
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
        gCropGenomeCompound.put(GCROP_CLIMATE_GENOME_NBT_TAG, StringTag.valueOf(climateGene.toRawGene()));

        return gCropGenomeCompound;
    }
}
