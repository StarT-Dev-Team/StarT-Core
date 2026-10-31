package com.startechnology.start_core.item.claw;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class StarTClawDyeRecipe extends CustomRecipe {

    private static final int RESET_COLOR = -1;

    public StarTClawDyeRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        return findColors(container) != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        Map<StarTClawPart, Integer> colors = findColors(container);

        if (colors == null) return ItemStack.EMPTY;

        ItemStack result = StarTClawRecipes.copyClaw(container);

        for (Map.Entry<StarTClawPart, Integer> entry : colors.entrySet()) {
            if (entry.getValue() == RESET_COLOR) {
                StarTClawOfLawItem.clearColor(result, entry.getKey());
            } else {
                StarTClawOfLawItem.setColor(result, entry.getKey(), entry.getValue());
            }
        }

        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return height >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return StarTClawRecipes.CLAW_DYE.get();
    }

    private static Map<StarTClawPart, Integer> findColors(CraftingContainer container) {
        Map<StarTClawPart, float[]> sums = new EnumMap<>(StarTClawPart.class);
        Map<StarTClawPart, Integer> counts = new EnumMap<>(StarTClawPart.class);
        Set<StarTClawPart> resets = EnumSet.noneOf(StarTClawPart.class);

        int claws = 0;

        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);

            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof StarTClawOfLawItem) {
                claws++;
            } else if (stack.getItem() instanceof DyeItem dye) {
                StarTClawPart part = StarTClawPart.byRow(slot / container.getWidth());

                float[] rgb = dye.getDyeColor().getTextureDiffuseColors();
                float[] sum = sums.computeIfAbsent(part, key -> new float[3]);

                sum[0] += rgb[0];
                sum[1] += rgb[1];
                sum[2] += rgb[2];

                counts.merge(part, 1, Integer::sum);
            } else if (stack.is(Items.SNOWBALL)) {
                resets.add(StarTClawPart.byRow(slot / container.getWidth()));
            } else {
                return null;
            }
        }

        if (claws != 1 || (sums.isEmpty() && resets.isEmpty())) return null;

        for (StarTClawPart part : resets) {
            if (sums.containsKey(part)) return null;
        }

        Map<StarTClawPart, Integer> colors = new EnumMap<>(StarTClawPart.class);

        sums.forEach((part, sum) -> {
            int count = counts.get(part);
            int red = Math.round(sum[0] / count * 255.0F);
            int green = Math.round(sum[1] / count * 255.0F);
            int blue = Math.round(sum[2] / count * 255.0F);

            colors.put(part, (red << 16) | (green << 8) | blue);
        });

        for (StarTClawPart part : resets) {
            colors.put(part, RESET_COLOR);
        }

        return colors;
    }
}
