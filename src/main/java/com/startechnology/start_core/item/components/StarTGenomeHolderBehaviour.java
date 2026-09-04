package com.startechnology.start_core.item.components;

import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.startechnology.start_core.api.gcrop.StarTGCropGene;
import com.startechnology.start_core.api.gcrop.StarTGCropGenome;
import com.startechnology.start_core.api.gcrop.StarTGCropManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StarTGenomeHolderBehaviour extends StarTNBTTooltipsBehaviour {

    public StarTGenomeHolderBehaviour() {}

    public static StarTGenomeHolderBehaviour getGenomeHolderBehaviour(ItemStack holder) {
        Item gCropItem = holder.getItem();

        if (!(gCropItem instanceof ComponentItem)) return null;

        List<IItemComponent> components = ((ComponentItem) gCropItem).getComponents();

        return components.stream()
                .filter(StarTGenomeHolderBehaviour.class::isInstance)
                .map(StarTGenomeHolderBehaviour.class::cast)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents,
                                TooltipFlag isAdvanced) {
        StarTGCropGenome gCropGenome = StarTGCropManager.gcropGenomeFromTag(stack);

        if (gCropGenome != null) {
            List<StarTGCropGene> resourceGenome = gCropGenome.getResourceGenome();
            List<StarTGCropGene> productionGenome = gCropGenome.getProductionGenome();
            List<StarTGCropGene> auxiliaryGenome = gCropGenome.getAuxiliaryGenome();
            StarTGCropGene climateGene = gCropGenome.getClimateGene();

            if (GTUtil.isShiftDown()) {
                tooltipComponents.add(Component.translatable("behaviour.start_core.genome_holder.genome_header"));
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
                tooltipComponents.add(Component.translatable("behaviour.start_core.genome_holder.traits_header"));
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
        } else {
            tooltipComponents.add(Component.translatable("behaviour.start_core.genome_holder.no_genome"));
        }

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}
