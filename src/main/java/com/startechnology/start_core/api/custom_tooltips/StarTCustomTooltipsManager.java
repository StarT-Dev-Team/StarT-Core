package com.startechnology.start_core.api.custom_tooltips;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.Arrays;

public class StarTCustomTooltipsManager {
    public static final String CUSTOM_TOOLTIPS_NBT_TAG = "custom_tooltips";

    public static boolean hasCustomTooltip(CompoundTag customTooltipTag) {
        if (customTooltipTag == null || customTooltipTag.isEmpty()) return false;
        return customTooltipTag.contains(CUSTOM_TOOLTIPS_NBT_TAG, Tag.TAG_LIST);
    }

    public static void writeCustomTooltipsToItem(CompoundTag tooltipsTag, String... tooltips) {
        ListTag tooltipsList = new ListTag();

        Arrays.asList(tooltips).forEach(tooltip -> {
            tooltipsList.add(StringTag.valueOf(tooltip));
        });
        
        tooltipsTag.put(CUSTOM_TOOLTIPS_NBT_TAG, tooltipsList);
    }

    public static StarTCustomTooltip customTooltipFromTag(CompoundTag customTooltipTag) {
        if (!hasCustomTooltip(customTooltipTag)) {
            return null;
        }

        ListTag tooltipTags = customTooltipTag.getList(CUSTOM_TOOLTIPS_NBT_TAG, 8);
        return new StarTCustomTooltip(tooltipTags);
    }

}
