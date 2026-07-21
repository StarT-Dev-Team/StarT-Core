package com.startechnology.start_core.integration;

import codechicken.microblock.api.MicroMaterial;
import codechicken.microblock.item.ItemMicroBlock;
import codechicken.microblock.util.MicroMaterialRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Optional;

public class CBMicroblockUtils {

    public static List<MicroMaterial> getMicroMaterials() {
        return List.of(MicroMaterialRegistry.MICRO_MATERIALS.getValues().toArray(MicroMaterial[]::new));
    }

    public static Optional<List<ItemStack>> convertToMaterials(List<MicroMaterial> materials, ItemStack stack) {
        if (stack.is(Items.COBBLESTONE)) {
            return Optional.of(materials.stream().map(MicroMaterial::getItem).toList());
        }
        if (stack.getItem() instanceof ItemMicroBlock) {
            return Optional.of(materials.stream().map(mat -> ItemMicroBlock.createStack(
                    stack.getCount(),
                    ItemMicroBlock.getFactoryID(stack),
                    ItemMicroBlock.getSize(stack),
                    mat)).toList());
        }
        return Optional.empty();
    }

    public static ItemStack convertToMaterial(MicroMaterial material, ItemStack stack) {
        if (stack.is(Items.COBBLESTONE)) {
            return material.getItem();
        }
        if (stack.getItem() instanceof ItemMicroBlock) {
            return ItemMicroBlock.createStack(
                    stack.getCount(),
                    ItemMicroBlock.getFactoryID(stack),
                    ItemMicroBlock.getSize(stack),
                    material);
        }
        return stack;
    }
}
