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

    private List<Material> possibleBacteriaAffinities;
    private Material mainBacteriaOutput;

    public List<Material> getPossibleBacteriaAffinities() {
        return possibleBacteriaAffinities;
    }

    public List<Fluid> getBehaviourAffinityFluids() {
        return possibleBacteriaAffinities
            .stream()
            .filter(Material::hasFluid)
            .map(material -> material.getFluid())
            .collect(Collectors.toList());
    }

    public Fluid getBehaviourMainFluid() {
        if (!mainBacteriaOutput.hasFluid()) return null;
        return mainBacteriaOutput.getFluid();
    }

    public StarTBacteriaBehaviour(Material mainMaterial, Material... materials) {
        this.mainBacteriaOutput = mainMaterial;
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
            tooltipComponents.add(Component.translatable("behaviour.start_core.bacteria.stats_header"));
            tooltipComponents.add(Component.translatable("behaviour.start_core.bacteria.stat_production", stats.getProductionPretty()));
            tooltipComponents.add(Component.translatable("behaviour.start_core.bacteria.stat_metabolism", stats.getMetabolismPretty()));
            tooltipComponents.add(Component.translatable("behaviour.start_core.bacteria.stat_mutability", stats.getMutabilityPretty()));
            tooltipComponents.add(Component.literal(""));
            tooltipComponents.add(Component.translatable("behaviour.start_core.bacteria.stat_affinity", stats.getAffinityPretty().withStyle(ChatFormatting.DARK_PURPLE)));
        }

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}
