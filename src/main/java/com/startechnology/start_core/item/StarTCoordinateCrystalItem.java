package com.startechnology.start_core.item;

import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.startechnology.start_core.item.components.StarTCoordinateCrystalBehaviour;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class StarTCoordinateCrystalItem extends ComponentItem {

    protected StarTCoordinateCrystalItem(Properties properties) {
        super(properties);
    }

    public static StarTCoordinateCrystalItem create(Properties properties) {
        return new StarTCoordinateCrystalItem(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return StarTCoordinateCrystalBehaviour.isEngraved(stack) || super.isFoil(stack);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        if (StarTCoordinateCrystalBehaviour.isDimensionCrystal(stack)) {
            return Rarity.EPIC;
        }
        return Rarity.RARE;
    }
}
