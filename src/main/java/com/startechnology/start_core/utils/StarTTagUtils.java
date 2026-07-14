package com.startechnology.start_core.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class StarTTagUtils {

    /**
     * Converts a string into a tag used for recipe inputs.
     *
     * @param tagString the input string formatted as "<modid>:<id>"; must not be {@code null}
     * @return the converted tag as a recipe input
     * @throws NullPointerException if {@code tagString} is {@code null}
     */
    public static String easyTagBuilder(String tagString) {
        var seperatedTag = tagString.split(":");
        TagKey<Item> tagKey = ForgeRegistries.ITEMS.tags()
                .createTagKey(new ResourceLocation(seperatedTag[0], seperatedTag[1]));
        return ForgeRegistries.ITEMS.tags().getTag(tagKey);
    }
}
