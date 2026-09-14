package com.startechnology.start_core.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class StarTItemUtils {

    /**
     * Converts a namespace and id into an Item
     *
     * @param namespace the namespace under which the item is registered (e.g. {@code minecraft}); must not be
     *                  {@code null}
     * @param itemId    the item id you are looking for (e.g. {@code stone}); must not be {@code null}
     * @return an Item of your namespace and id
     * @throws NullPointerException if {@code namespace} or {@code itemId} is {@code null}
     */
    public static Item getItem(@NotNull String namespace, @NotNull String itemId) {
        return Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(namespace, itemId)));
    }

    /**
     * Converts an id into an Item
     * <p>
     * Namespace defaults to {@code minecraft} if it isn't found
     *
     * @param itemId the item id you are looking for (e.g. {@code minecraft:stone}); must not be {@code null}
     * @return an Item of your id
     * @throws NullPointerException if {@code itemId} is {@code null}
     */
    public static Item getItem(@NotNull String itemId) {
        if (itemId.contains(":")) {
            var parts = itemId.split(":");
            return getItem(parts[0], parts[1]);
        } else return getItem("minecraft", itemId);
    }
}
