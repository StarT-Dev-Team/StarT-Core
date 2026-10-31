package com.startechnology.start_core.item.claw;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.item.IGTTool;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class StarTClawToolRecipe extends CustomRecipe {

    public StarTClawToolRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        return findMaterials(container) != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        Map<StarTClawMode, Material> materials = findMaterials(container);

        if (materials == null) return ItemStack.EMPTY;

        ItemStack result = StarTClawRecipes.copyClaw(container);

        for (Map.Entry<StarTClawMode, Material> entry : materials.entrySet()) {
            StarTClawOfLawItem.setToolMaterial(result, entry.getKey(), entry.getValue());
        }

        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);

        for (int slot = 0; slot < remaining.size(); slot++) {
            ItemStack stack = container.getItem(slot);

            if (stack.getItem() instanceof IGTTool && !(stack.getItem() instanceof StarTClawOfLawItem)) {
                remaining.set(slot, stack.copyWithCount(1));
            }
        }

        return remaining;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return StarTClawRecipes.CLAW_TOOL.get();
    }

    private static Map<StarTClawMode, Material> findMaterials(CraftingContainer container) {
        Map<StarTClawMode, Material> materials = new EnumMap<>(StarTClawMode.class);

        int claws = 0;

        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);

            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof StarTClawOfLawItem) {
                claws++;
            } else if (stack.getItem() instanceof IGTTool tool) {
                StarTClawMode mode = findMode(tool.getToolClasses(stack));

                if (mode == null) return null;

                materials.put(mode, tool.getMaterial());
            } else {
                return null;
            }
        }

        return claws == 1 && !materials.isEmpty() ? materials : null;
    }

    private static StarTClawMode findMode(Set<GTToolType> toolClasses) {
        for (StarTClawMode mode : StarTClawMode.values()) {
            if (mode.getToolType() != null && toolClasses.contains(mode.getToolType())) return mode;
        }

        return null;
    }
}
