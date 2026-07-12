package com.startechnology.start_core.item.components;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.startechnology.start_core.api.gcrop.StarTGCropManager;
import com.startechnology.start_core.api.gcrop.StarTGCropPlants;
import com.startechnology.start_core.api.gcrop.StarTGCropTrait;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StarTGCropBehaviour extends StarTNBTTooltipsBehaviour {

    private Integer tier;

    private List<StarTGCropTrait> gcropTraits;

    private Material resource;

    public Integer getCropTier() {
        return tier;
    }

    public StarTGCropBehaviour(Integer tier, Material resource, StarTGCropTrait... traits) {
        this.tier = tier;
        this.resource = resource;
        this.gcropTraits = Arrays.asList(traits);
    }

    public static StarTGCropBehaviour getBacteriaBehaviour(ItemStack bacteria) {
        Item bacteriaItem = bacteria.getItem();

        if (!(bacteriaItem instanceof ComponentItem)) return null;

        List<IItemComponent> components = ((ComponentItem) bacteriaItem).getComponents();
        
        return components.stream()
            .filter(StarTGCropBehaviour.class::isInstance)
            .map(StarTGCropBehaviour.class::cast)
            .findFirst()
            .orElse(null);
    }

    public MutableComponent prettyPossibleBacteriaAffinities() {
        List<Component> translatableAffinities = possibleBacteriaAffinities.stream()
            .map(
                material -> Component.translatable(
                    material.getFluid().getFluidType().getDescriptionId()
                ).withStyle(ChatFormatting.DARK_PURPLE)
            )
            .collect(Collectors.toList());

        return Component.translatable("behaviour.start_core.bacteria.possible_affinities", translatableAffinities.toArray());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents,
            TooltipFlag isAdvanced) {
        StarTGCropPlants stats = StarTGCropManager.gcropGenomeFromTag(stack);
            
        if (stats == null) {
            tooltipComponents.add(Component.translatable("behaviour.start_core.bacteria.no_stats"));
            tooltipComponents.add(Component.literal(""));
            tooltipComponents.add(this.prettyPossibleBacteriaAffinities());
        } else {
            tooltipComponents.add(Component.translatable("behaviour.start_core.bacteria.affinities_header"));
            tooltipComponents.add(Component.translatable("behaviour.start_core.bacteria.affinity_primary", stats.getFluidPretty(stats.getPrimary()).withStyle(ChatFormatting.LIGHT_PURPLE)));
            tooltipComponents.add(Component.translatable("behaviour.start_core.bacteria.affinity_secondary", stats.getFluidPretty(stats.getSecondary()).withStyle(ChatFormatting.LIGHT_PURPLE)));
            tooltipComponents.add(Component.translatable("behaviour.start_core.bacteria.affinity_tertiary", stats.getFluidPretty(stats.getTertiary()).withStyle(ChatFormatting.LIGHT_PURPLE)));
            tooltipComponents.add(Component.translatable(""));
            tooltipComponents.add(Component.translatable("behaviour.start_core.bacteria.affinity_super", stats.getFluidPretty(stats.getSuperFluid()).withStyle(ChatFormatting.LIGHT_PURPLE)));
            tooltipComponents.add(Component.translatable(""));
            tooltipComponents.add(Component.translatable("behaviour.start_core.bacteria.stats_header"));
            tooltipComponents.add(Component.translatable("behaviour.start_core.bacteria.stat_production", stats.getProductionPretty()));
            tooltipComponents.add(Component.translatable("behaviour.start_core.bacteria.stat_metabolism", stats.getMetabolismPretty()));
            tooltipComponents.add(Component.translatable("behaviour.start_core.bacteria.stat_mutability", stats.getMutabilityPretty()));
        }

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}
