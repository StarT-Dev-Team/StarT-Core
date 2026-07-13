package com.startechnology.start_core.api.gcrop;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public class StarTGCropManager {
    public static final String GCROP_GENOME_NBT_TAG = "gcrop_genome";

    public static void writeGCRopGenomeToItem(CompoundTag gcropCompound, StarTGCropPlant bacteriaStats) {
        gcropCompound.put(GCROP_GENOME_NBT_TAG, bacteriaStats.toCompoundTag());
    }

    public static boolean hasGCropGenome(CompoundTag gcropCompound) {
        if (gcropCompound == null || gcropCompound.isEmpty()) return false;
        return gcropCompound.contains(GCROP_GENOME_NBT_TAG, Tag.TAG_COMPOUND);
    }

    public static StarTGCropPlant gcropGenomeFromTag(ItemStack stack) {
        if (stack.hasTag() == false) return null;
        
        CompoundTag gcropCompound =  stack.getOrCreateTag();

        if (!hasGCropGenome(gcropCompound)) {
            return null;
        }

        CompoundTag statsTag = gcropCompound.getCompound(GCROP_GENOME_NBT_TAG);
        return new StarTGCropPlant(statsTag);
    }
}
