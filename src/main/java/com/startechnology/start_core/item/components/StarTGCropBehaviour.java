package com.startechnology.start_core.item.components;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.startechnology.start_core.api.gcrop.StarTGCropManager;
import com.startechnology.start_core.api.gcrop.StarTGCropPlant;
import com.startechnology.start_core.api.gcrop.StarTGCropTrait;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class StarTGCropBehaviour extends StarTNBTTooltipsBehaviour {

    private Integer tier;

    private List<StarTGCropTrait> gcropTraits;

    private Material resource;

    public Integer getCropTier() {
        return tier;
    }

    public List<StarTGCropTrait> getCropTraits() {
        return gcropTraits;
    }

    public Material getCropMaterial() {
        return resource;
    }

    public StarTGCropBehaviour(Integer tier, Material resource, StarTGCropTrait... traits) {
        this.tier = tier;
        this.resource = resource;
        this.gcropTraits = Arrays.asList(traits);
    }

    public static StarTGCropBehaviour getGCropBehaviour(ItemStack gCrop) {
        Item gCropItem = gCrop.getItem();

        if (!(gCropItem instanceof ComponentItem)) return null;

        List<IItemComponent> components = ((ComponentItem) gCropItem).getComponents();
        
        return components.stream()
            .filter(StarTGCropBehaviour.class::isInstance)
            .map(StarTGCropBehaviour.class::cast)
            .findFirst()
            .orElse(null);
    }

    private static String getPrettyTraitSymbol(String symbol, Integer tier) {
        String colourCode = switch (tier) {
            case 1 -> "§9";
            case 2 -> "§1";
            case 3 -> "§5";
            case 4 -> "§2";
            case 5 -> "§a";
            default -> "§7";
        };

        return String.format("%s%s§r", colourCode, symbol);
    }

    public MutableComponent prettyRequiredGCropTraits() {
        Component translatableTraits = gcropTraits.stream()
                .map(
                    trait -> Component.translatable(
                            getPrettyTraitSymbol(trait.getTraitSymbol(), trait.getTraitTier())
                    )
                ).reduce(Component.literal(""), (a, b) -> a.append(b));

        return Component.translatable("behaviour.start_core.gcrop.required_traits", translatableTraits);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents,
            TooltipFlag isAdvanced) {
        StarTGCropPlant stats = StarTGCropManager.gcropGenomeFromTag(stack);
            
        if (stats == null) {
            tooltipComponents.add(Component.translatable("behaviour.start_core.gcrop.no_genome"));
            tooltipComponents.add(Component.literal(""));
            tooltipComponents.add(this.prettyRequiredGCropTraits());
        } else {
            tooltipComponents.add(Component.translatable("behaviour.start_core.bacteria.affinities_header"));
            tooltipComponents.add(Component.translatable("behaviour.start_core.bacteria.affinity_primary", tier));
            tooltipComponents.add(Component.translatable("behaviour.start_core.bacteria.affinity_secondary", tier));
            tooltipComponents.add(Component.translatable("behaviour.start_core.bacteria.affinity_tertiary", tier));
            tooltipComponents.add(Component.translatable("lang.start_core.empty"));
            tooltipComponents.add(Component.translatable("behaviour.start_core.bacteria.affinity_super", tier));
            tooltipComponents.add(Component.translatable("lang.start_core.empty"));
            tooltipComponents.add(Component.translatable("behaviour.start_core.bacteria.stats_header"));
            tooltipComponents.add(Component.translatable("behaviour.start_core.bacteria.stat_production", tier));
            tooltipComponents.add(Component.translatable("behaviour.start_core.bacteria.stat_metabolism", tier));
            tooltipComponents.add(Component.translatable("behaviour.start_core.bacteria.stat_mutability", tier));
        }

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}
