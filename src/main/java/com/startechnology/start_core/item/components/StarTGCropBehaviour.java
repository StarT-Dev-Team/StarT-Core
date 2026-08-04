package com.startechnology.start_core.item.components;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.startechnology.start_core.api.gcrop.*;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.startechnology.start_core.item.gcrops.StarTGCropItems.GCROP_MALFORMED;

public class StarTGCropBehaviour extends StarTNBTTooltipsBehaviour {

    private final int tier;
    @Getter
    private final Material material;

    private final List<StarTGCropTrait> gcropTraits;

    public int getCropTier() {
        return tier;
    }

    public Material getCropMaterial() {
        return material;
    }

    public List<StarTGCropTrait> getCropTraits() {
        return gcropTraits;
    }

    public StarTGCropBehaviour(int tier, Material material, List<StarTGCropTrait> traits) {
        this.tier = tier;
        this.material = material;
        this.gcropTraits = traits;
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

    public MutableComponent prettyRequiredGCropTraits() {
        var translatableAffinities = gcropTraits.stream()
                .map(
                        trait -> Component.translatable(
                                StarTGCropGenome.getPrettyTrait(Component
                                        .translatable(String.format("behaviour.start_core.trait.%s.symbol", trait.id()))
                                        .getString(), trait.tier())))
                .reduce(Component.literal(""), MutableComponent::append);

        return Component.translatable("behaviour.start_core.gcrop.required_traits", translatableAffinities);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents,
                                TooltipFlag isAdvanced) {
        StarTGCropGenome gCropGenome = StarTGCropManager.gcropGenomeFromTag(stack);

        boolean malformed = stack.is(GCROP_MALFORMED.asItem());

        if (gCropGenome != null) {
            List<StarTGCropGene> resourceGenome = gCropGenome.getResourceGenome();
            List<StarTGCropGene> productionGenome = gCropGenome.getProductionGenome();
            List<StarTGCropGene> auxiliaryGenome = gCropGenome.getAuxiliaryGenome();
            StarTGCropGene climateGene = gCropGenome.getClimateGene();

            if (GTUtil.isShiftDown()) {
                tooltipComponents.add(Component.translatable("behaviour.start_core.gcrop.genome_header"));
                if (!resourceGenome.isEmpty()) {
                    tooltipComponents.add(Component.translatable("behaviour.start_core.gcrop.resource_genome"));
                    for (StarTGCropGene gene : resourceGenome) {
                        tooltipComponents.add(Component.translatable("behaviour.start_core.gcrop.gene_holder",
                                StarTGCropGenome.prettyGCropGene(gene)));
                    }
                }
                if (!productionGenome.isEmpty()) {
                    tooltipComponents.add(Component.translatable("behaviour.start_core.gcrop.production_genome"));
                    for (StarTGCropGene gene : productionGenome) {
                        tooltipComponents.add(Component.translatable("behaviour.start_core.gcrop.gene_holder",
                                StarTGCropGenome.prettyGCropGene(gene)));
                    }
                }
                if (!auxiliaryGenome.isEmpty()) {
                    tooltipComponents.add(Component.translatable("behaviour.start_core.gcrop.auxiliary_genome"));
                    for (StarTGCropGene gene : auxiliaryGenome) {
                        tooltipComponents.add(Component.translatable("behaviour.start_core.gcrop.gene_holder",
                                StarTGCropGenome.prettyGCropGene(gene)));
                    }
                }
                if (climateGene != null) {
                    tooltipComponents
                            .add(Component.translatable("behaviour.start_core.gcrop.climate_genome",
                                    StarTGCropGenome.prettyGenomeGCropTraits(List.of(climateGene), true)));
                }
                tooltipComponents.add(Component.empty());
            } else {
                tooltipComponents.add(Component.translatable("behaviour.start_core.gcrop.traits_header"));
                if (!resourceGenome.isEmpty()) {
                    tooltipComponents
                            .add(Component.translatable("behaviour.start_core.gcrop.resource_traits",
                                    StarTGCropGenome.prettyGenomeGCropTraits(resourceGenome, false)));
                }
                if (!productionGenome.isEmpty()) {
                    tooltipComponents
                            .add(Component.translatable("behaviour.start_core.gcrop.production_traits",
                                    StarTGCropGenome.prettyGenomeGCropTraits(productionGenome, false)));
                }
                if (!auxiliaryGenome.isEmpty()) {
                    tooltipComponents
                            .add(Component.translatable("behaviour.start_core.gcrop.auxiliary_traits",
                                    StarTGCropGenome.prettyGenomeGCropTraits(auxiliaryGenome, false)));
                }
                if (climateGene != null) {
                    tooltipComponents
                            .add(Component.translatable("behaviour.start_core.gcrop.climate_gene",
                                    StarTGCropGenome.prettyGenomeGCropTraits(List.of(climateGene), false)));
                }
                tooltipComponents.add(Component.empty());
                tooltipComponents.add(Component.translatable("behaviour.start_core.gcrop.show_full_genome"));
                tooltipComponents.add(Component.empty());
            }
        } else if (!malformed) {
            tooltipComponents.add(Component.translatable("behaviour.start_core.gcrop.no_genome"));
            tooltipComponents.add(Component.empty());
            tooltipComponents.add(this.prettyRequiredGCropTraits());
        }

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}
