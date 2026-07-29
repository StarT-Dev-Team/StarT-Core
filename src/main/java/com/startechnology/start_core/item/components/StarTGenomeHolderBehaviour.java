package com.startechnology.start_core.item.components;

import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
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
            tooltipComponents.add(Component.translatable("behaviour.start_core.genome_holder.header"));

            List<StarTGCropGene> resourceGenome = gCropGenome.getResourceGenome();
            List<StarTGCropGene> productionGenome = gCropGenome.getProductionGenome();
            List<StarTGCropGene> auxiliaryGenome = gCropGenome.getAuxiliaryGenome();
            StarTGCropGene climateGene = gCropGenome.getClimateGene();

            if (!resourceGenome.isEmpty()) {
                tooltipComponents
                        .add(Component.translatable("behaviour.start_core.gcrop.resource_genome",
                                StarTGCropGenome.prettyGenomeGCropTraits(resourceGenome, false)));
            }
            if (!productionGenome.isEmpty()) {
                tooltipComponents
                        .add(Component.translatable("behaviour.start_core.gcrop.production_genome",
                                StarTGCropGenome.prettyGenomeGCropTraits(productionGenome, false)));
            }
            if (!auxiliaryGenome.isEmpty()) {
                tooltipComponents
                        .add(Component.translatable("behaviour.start_core.gcrop.auxiliary_genome",
                                StarTGCropGenome.prettyGenomeGCropTraits(auxiliaryGenome, false)));
            }
            if (climateGene != null) {
                tooltipComponents
                        .add(Component.translatable("behaviour.start_core.gcrop.climate_gene",
                                StarTGCropGenome.prettyGenomeGCropTraits(List.of(climateGene), false)));
            }
        } else {
            tooltipComponents.add(Component.translatable("behaviour.start_core.genome_holder.no_genome"));
        }

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}
