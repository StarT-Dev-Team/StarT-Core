package com.startechnology.start_core.api.bacteria;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public class StarTBacteriaManager {
    public static final String BACTERIA_STATS_NBT_TAG = "bacteria_stats";

    public static void writeBacteriaStatsToItem(CompoundTag bacteriaCompound, StarTBacteriaStats bacteriaStats) {
        bacteriaCompound.put(BACTERIA_STATS_NBT_TAG, bacteriaStats.toCompoundTag());
    }

    public static boolean hasBacteriaStats(CompoundTag bacteriaCompound) {
        if (bacteriaCompound == null || bacteriaCompound.isEmpty()) return false;
        return bacteriaCompound.contains(BACTERIA_STATS_NBT_TAG, Tag.TAG_COMPOUND);
    }

    public static StarTBacteriaStats bacteriaStatsFromTag(ItemStack stack) {
        if (!stack.hasTag()) return null;
        
        CompoundTag bacteriaCompound =  stack.getOrCreateTag();

        if (!hasBacteriaStats(bacteriaCompound)) {
            return null;
        }

        CompoundTag statsTag = bacteriaCompound.getCompound(BACTERIA_STATS_NBT_TAG);
        return new StarTBacteriaStats(statsTag);
    }
}
