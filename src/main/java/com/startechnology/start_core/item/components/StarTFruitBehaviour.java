package com.startechnology.start_core.item.components;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.startechnology.start_core.data.gcrops.StarTGCropItemType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StarTFruitBehaviour extends StarTNBTTooltipsBehaviour {

    private final int tier;

    private final Material resource;

    private final StarTGCropItemType materialType;

    public int getCropTier() {
        return tier;
    }

    public Material getCropMaterial() {
        return resource;
    }

    public StarTGCropItemType getCropMaterialType() {
        return materialType;
    }

    public StarTFruitBehaviour(int tier, Material resource, StarTGCropItemType materialType) {
        this.tier = tier;
        this.resource = resource;
        this.materialType = materialType;
    }

    public static StarTFruitBehaviour getFruitBehaviour(ItemStack fruit) {
        Item fruitItem = fruit.getItem();

        if (!(fruitItem instanceof ComponentItem)) return null;

        List<IItemComponent> components = ((ComponentItem) fruitItem).getComponents();

        return components.stream()
                .filter(StarTFruitBehaviour.class::isInstance)
                .map(StarTFruitBehaviour.class::cast)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents,
                                TooltipFlag isAdvanced) {
        MutableComponent fruitResource = Component.translatable(
                String.format("behaviour.start_core.gcrop.type.%s", this.materialType.getName()),
                Component.translatable(String.format("material.gtceu.%s", this.resource.getName())));

        tooltipComponents.add(Component.translatable("behaviour.start_core.gcrop.fruit_header"));
        tooltipComponents
                .add(Component.translatable("behaviour.start_core.gcrop.fruit_resource", fruitResource));
        tooltipComponents
                .add(Component.translatable("behaviour.start_core.gcrop.fruit_tier",
                        Component.literal(Integer.toString(this.tier))));

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}
