package com.startechnology.start_core.item.components;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.startechnology.start_core.api.gcrop.StarTGCropGene;
import com.startechnology.start_core.api.gcrop.StarTGCropManager;
import com.startechnology.start_core.api.gcrop.StarTGCropGenome;
import com.startechnology.start_core.data.gcrops.StarTGCropTraits.StarTGCropTrait;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

import static com.startechnology.start_core.item.StarTGCropItems.GCROP_MALFORMED;

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

    public StarTGCropBehaviour(int tier, Material material, StarTGCropTrait... traits) {
        this.tier = tier;
        this.material = material;
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

    private static String getPrettyTraitSymbol(String symbol, int tier) {
        String colourCode = switch (tier) {
            case 1 -> "§9";
            case 2 -> "§1";
            case 3 -> "§5";
            case 4 -> "§2";
            case 5 -> "§a";
            case 6 -> "§c";
            case 7 -> "§e";
            default -> "§7";
        };

        return String.format("%s%s§r", colourCode, symbol);
    }

    public MutableComponent prettyRequiredGCropTraits() {
        var translatableAffinities = gcropTraits.stream()
                .map(
                        trait -> Component.translatable(
                                getPrettyTraitSymbol(trait.symbol(), trait.tier())))
                .reduce(Component.literal(""), MutableComponent::append);

        return Component.translatable("behaviour.start_core.gcrop.required_traits", translatableAffinities);
    }

    public MutableComponent prettyGenomeGCropTraits(List<StarTGCropGene> genome, boolean full) {
        return genome.stream()
                .map(
                        gene -> {
                            StarTGCropTrait trait = gene.getTrait();
                            return Component
                                    .translatable(
                                            getPrettyTraitSymbol(full ? trait.name() : trait.symbol(), trait.tier()));
                        })
                .reduce(Component.literal(full ? ", " : ""), MutableComponent::append);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents,
                                TooltipFlag isAdvanced) {
        StarTGCropGenome gCropGenome = StarTGCropManager.gcropGenomeFromTag(stack);

        boolean malformed = stack.is(GCROP_MALFORMED.asItem());

        if (gCropGenome != null) {
            tooltipComponents.add(Component.translatable("behaviour.start_core.gcrop.genome_header"));
            tooltipComponents
                    .add(Component.translatable("behaviour.start_core.gcrop.resource_genome",
                            prettyGenomeGCropTraits(gCropGenome.getResourceGenome(), false)));
            tooltipComponents.add(
                    Component.translatable("behaviour.start_core.gcrop.production_genome",
                            prettyGenomeGCropTraits(gCropGenome.getProductionGenome(), false)));
            tooltipComponents
                    .add(Component.translatable("behaviour.start_core.gcrop.auxiliary_genome",
                            prettyGenomeGCropTraits(gCropGenome.getAuxiliaryGenome(), false)));
        } else if (!malformed) {
            tooltipComponents.add(Component.translatable("behaviour.start_core.gcrop.no_genome"));
            tooltipComponents.add(Component.empty());
            tooltipComponents.add(this.prettyRequiredGCropTraits());
        }

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}
