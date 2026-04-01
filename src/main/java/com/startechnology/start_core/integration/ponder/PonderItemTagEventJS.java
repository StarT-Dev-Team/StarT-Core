package com.startechnology.start_core.integration.ponder;

import com.startechnology.start_core.integration.kjs.StarTKubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventJS;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.createmod.ponder.foundation.PonderTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class PonderItemTagEventJS extends EventJS {

    private final PonderTagRegistrationHelper<ResourceLocation> helper;

    public PonderItemTagEventJS(PonderTagRegistrationHelper<ResourceLocation> helper) {
        this.helper = helper;
    }

    public void createTag(String name, Consumer<Builder> onCreate) {
        var id = StarTKubeJSPlugin.getKubeId(name);
        PonderJSUtils.TRANSLATED_TAGS.add(id);

        var builder = new Builder(id);
        onCreate.accept(builder);
        builder.fin(helper);
    }

    public void createTag(String id, Item displayItem, String title, String description, @Nullable Ingredient ingredient) {
        createTag(id, builder -> {
            builder.icon(displayItem);
            builder.title(title);
            builder.description(description);
            if (ingredient != null) builder.items(ingredient);
        });
    }

    public void createTag(String id, Item displayItem, String title, String description) {
        createTag(id, displayItem, title, description, null);
    }

    public void add(PonderTag tag, Ingredient ingredient) {
        if (ingredient.isEmpty()) return;

        var tagBuilder = helper.addToTag(tag.getId());
        for (var item : ingredient.getItems()) {
            var id = ForgeRegistries.ITEMS.getKey(item.getItem());
            if (id != null) {
                tagBuilder.add(id);
            }
        }
    }

    @Accessors(fluent = true, chain = true)
    public class Builder {

        private final Set<ResourceLocation> itemIds = new HashSet<>();

        private final ResourceLocation id;

        @Setter
        @Nullable
        private String title;

        @Setter
        @Nullable
        private String description;

        @Setter
        private Item icon = Items.BARRIER;

        @Setter
        private boolean index = true;

        @Setter
        private boolean addItemIconToItems = false;

        private Builder(ResourceLocation id) {
            this.id = id;
        }

        public Builder items(Ingredient ingredient) {
            for (var item : ingredient.getItems()) {
                var itemId = ForgeRegistries.ITEMS.getKey(item.getItem());
                itemIds.add(itemId);
            }
            return this;
        }

        private void fin(PonderTagRegistrationHelper<ResourceLocation> helper) {
            if (title == null) throw new RuntimeException("Title cannot be null for tag " + id);
            if (description == null) throw new RuntimeException("Description cannot be null for tag " + id);

            var tagBuilder = helper.registerTag(id).title(title).description(description);
            if (!index) {
                tagBuilder.addToIndex();
            }
            tagBuilder.item(icon, true, addItemIconToItems);
            tagBuilder.register();

            for (var itemId : itemIds) {
                helper.addTagToComponent(itemId, id);
            }
        }

    }
}
