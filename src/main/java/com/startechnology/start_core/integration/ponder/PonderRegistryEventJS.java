package com.startechnology.start_core.integration.ponder;

import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.integration.kjs.StarTKubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventJS;
import net.createmod.ponder.api.scene.PonderStoryBoard;
import net.createmod.ponder.foundation.PonderStoryBoardEntry;
import net.createmod.ponder.foundation.registration.PonderSceneRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PonderRegistryEventJS extends EventJS {

    public static final ResourceLocation BASIC_STRUCTURE = StarTCore.resourceLocation("basic");

    private final PonderSceneRegistry sceneRegistry;

    public PonderRegistryEventJS(PonderSceneRegistry sceneRegistry) {
        this.sceneRegistry = sceneRegistry;
    }

    public Builder create(Ingredient ingredient) {
        if (ingredient.isEmpty()) {
            throw new IllegalArgumentException("Provided items must not be empty!");
        }

        Set<ResourceLocation> itemIds = Arrays
                .stream(ingredient.getItems())
                .map(ItemStack::getItem)
                .map(ForgeRegistries.ITEMS::getKey)
                .collect(Collectors.toSet());
        return new Builder(itemIds);
    }

    public class Builder {

        private final Set<ResourceLocation> itemIds;
        private final Set<ResourceLocation> tags;

        public Builder(Set<ResourceLocation> itemIds) {
            this.itemIds = itemIds;
            this.tags = new HashSet<>();
        }

        public Builder tag(ResourceLocation... tags) {
            this.tags.addAll(Arrays.asList(tags));
            return this;
        }

        public Builder scene(String name, String title, PonderStoryBoard scene) {
            return scene(name, title, BASIC_STRUCTURE, scene);
        }

        private Builder scene(String name, String title, ResourceLocation structure, PonderStoryBoard storyBoard, ResourceLocation... tags) {
            var id = StarTKubeJSPlugin.getKubeId(name);
            PonderJSUtils.TRANSLATED_SCENES.add(id);

            PonderStoryBoard wrapper = (scene, util) -> {
                scene.title(id.getPath(), title);
                try {
                    storyBoard.program(scene, util);
                } catch(Exception e) {
                    PonderErrorHelper.reportJsPonderError(e);
                }
            };

            for (var itemId : itemIds) {
                var entry = new PonderStoryBoardEntry(wrapper, id.getNamespace(), structure, itemId);
                entry.highlightTags(tags);
                sceneRegistry.addStoryBoard(entry);
            }

            return this;
        }
    }

}
