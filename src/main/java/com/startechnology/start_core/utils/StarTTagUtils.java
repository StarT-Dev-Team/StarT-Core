package com.startechnology.start_core.utils;

import org.jetbrains.annotations.NotNull;

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
    public static TagKey<Item> getTag(@NotNull String tagString) {
        return ForgeRegistries.ITEMS.tags().createTagKey(new ResourceLocation(tagString));
    }
}
