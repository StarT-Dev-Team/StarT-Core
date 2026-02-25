package com.startechnology.start_core.item.components;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.startechnology.start_core.api.bacteria.StarTBacteriaManager;
import com.startechnology.start_core.api.bacteria.StarTBacteriaStats;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

public class StarTBacteriaBehaviour extends StarTNBTTooltipsBehaviour {

    private Material superfluid;

    private List<Material> possibleBacteriaAffinities;

    public List<Material> getPossibleBacteriaAffinities() {
        return possibleBacteriaAffinities;
    }

    public Material getSuperfluid() {
        return superfluid;
    }

    public List<Fluid> getBehaviourAffinityFluids() {
        return possibleBacteriaAffinities
            .stream()
            .filter(Material::hasFluid)
            .map(Material::getFluid)
            .collect(Collectors.toList());
    }

    public StarTBacteriaBehaviour(Material superfluid, Material... materials) {
        this.superfluid = superfluid;
        this.possibleBacteriaAffinities = Arrays.asList(materials);
    }

    public static StarTBacteriaBehaviour getBacteriaBehaviour(ItemStack bacteria) {
        Item bacteriaItem = bacteria.getItem();

        if (!(bacteriaItem instanceof ComponentItem)) return null;

        List<IItemComponent> components = ((ComponentItem) bacteriaItem).getComponents();
        
        return components.stream()
            .filter(StarTBacteriaBehaviour.class::isInstance)
            .map(StarTBacteriaBehaviour.class::cast)
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
        StarTBacteriaStats stats = StarTBacteriaManager.bacteriaStatsFromTag(stack);
            
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
