package com.startechnology.start_core.item.gcrops;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.tag.TagUtil;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.item.TooltipBehavior;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.gcrop.*;
import com.startechnology.start_core.data.gcrops.StarTGCropData;
import com.startechnology.start_core.data.gcrops.StarTTraitData.GenomeType;
import com.startechnology.start_core.item.components.StarTFruitBehaviour;
import com.startechnology.start_core.item.components.StarTGCropBehaviour;
import com.startechnology.start_core.item.components.StarTGenomeHolderBehaviour;
import com.startechnology.start_core.item.components.StarTNBTTooltipsBehaviour;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import net.minecraft.network.chat.Component;

import static com.gregtechceu.gtceu.common.data.models.GTModels.createTextureModel;
import static com.startechnology.start_core.StarTCore.START_REGISTRATE;
import static com.startechnology.start_core.api.gcrop.StarTGCropTraits.TRAIT_COMPARATOR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class StarTGCropItems {

    public static final List<ItemEntry<ComponentItem>> GCROP_ITEMS = new ArrayList<>();

    public static final List<ItemEntry<ComponentItem>> GCROP_FLOWERS = new ArrayList<>();
    public static final HashMap<Material, ItemEntry<ComponentItem>> GCROP_FLOWERMAP = new HashMap<>();

    public static final List<ItemEntry<ComponentItem>> GCROP_FRUITS = new ArrayList<>();
    public static final HashMap<Material, ItemEntry<ComponentItem>> GCROP_FRUITMAP = new HashMap<>();

    public static <T extends IComponentItem> NonNullConsumer<T> attach(IItemComponent components) {
        return item -> item.attachComponents(components);
    }

    public static Map<Integer, Integer> tierVoltages = Map.ofEntries(
            Map.entry(0, GTValues.ULV),
            Map.entry(1, GTValues.LV),
            Map.entry(2, GTValues.MV),
            Map.entry(3, GTValues.HV),
            Map.entry(4, GTValues.EV),
            Map.entry(5, GTValues.IV),
            Map.entry(6, GTValues.LuV),
            Map.entry(7, GTValues.UV),
            Map.entry(8, GTValues.UEV));

    public static @Nullable ItemEntry<ComponentItem> getGCropByGenome(@NotNull List<StarTGCropTrait> traits) {
        traits.sort(TRAIT_COMPARATOR);
        for (var gCrop : GCROP_ITEMS) {
            var behaviour = StarTGCropBehaviour.getGCropBehaviour(gCrop.asStack());
            if (behaviour == null) continue;
            behaviour.getCropTraits().sort(TRAIT_COMPARATOR);
            var resourceTraits = behaviour.getCropTraits().stream()
                    .filter(trait -> trait.genomeType() == GenomeType.RESOURCE).toList();
            if (resourceTraits.equals(traits)) {
                return gCrop;
            }
        }
        return null;
    }

    public static final ItemEntry<ComponentItem> FILLED_GENOME_HOLDER = START_REGISTRATE
            .item("filled_genome_holder", ComponentItem::create)
            .lang("§6Filled Genome Holder")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTNBTTooltipsBehaviour()))
            .onRegister(attach(new StarTGenomeHolderBehaviour()))
            .model((ctx, prov) -> createTextureModel(ctx, prov,
                    StarTCore.resourceLocation("item/gcrops/filled_genome_holder")))
            .register();

    public static final ItemEntry<ComponentItem> EMPTY_GENOME_HOLDER = START_REGISTRATE
            .item("empty_genome_holder", ComponentItem::create)
            .lang("§3Empty Genome Holder")
            .properties(prop -> prop.stacksTo(64))
            .onRegister(attach(new TooltipBehavior(lines -> {
                lines.add(Component.translatable("behaviour.start_core.genome_holder.no_genome"));
            })))
            .model((ctx, prov) -> createTextureModel(ctx, prov,
                    StarTCore.resourceLocation("item/gcrops/empty_genome_holder")))
            .register();

    public static final ItemEntry<ComponentItem> GCROP_MALFORMED = START_REGISTRATE
            .item("malformed_gcrop", ComponentItem::create)
            .lang("§3Malformed GCrop")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTNBTTooltipsBehaviour()))
            .onRegister(attach(new TooltipBehavior(lines -> {
                lines.add(Component.translatable("item.start_core.malformed_gcrop.tooltip"));
            })))
            .onRegister(attach(new StarTGCropBehaviour(-1, GTMaterials.Stone, new ArrayList<>())))
            .model((ctx, prov) -> createTextureModel(ctx, prov,
                    StarTCore.resourceLocation("item/gcrops/malformed_gcrop")))
            .register();

    public static TagKey<Item> gCropTag = TagUtil.createModItemTag("gcrop");
    public static TagKey<Item> gCropFlowerTag = TagUtil.createModItemTag("gcrop_flower");

    private static void registerGCrop(String id, String name,
                                      StarTGCropItemType materialType, String textureType,
                                      List<StarTGCropTrait> traits) {
        var newMaterial = GTMaterials.get(id.equals("sheldonite") ? "cooperite" : id);

        int highestTier = 0;
        for (var trait : traits) {
            int traitTier = trait.tier();
            if (traitTier > highestTier) highestTier = traitTier;
        }

        ItemEntry<ComponentItem> gCropItem = START_REGISTRATE
                .item(String.format("%s_gcrop", id), ComponentItem::create)
                .lang(String.format("§3Arcanthus %s GCrop", name))
                .properties(prop -> prop.stacksTo(16))
                .onRegister(attach(new StarTGCropBehaviour(
                        highestTier,
                        newMaterial,
                        traits)))
                .model((ctx, prov) -> createTextureModel(ctx, prov,
                        StarTCore.resourceLocation(String.format("item/gcrops/seed_%s", textureType))))
                .color(() -> () -> (itemStack, index) -> newMaterial.getLayerARGB(index))
                .tag(gCropTag)
                .register();

        ItemEntry<ComponentItem> gCropFruit = START_REGISTRATE
                .item(String.format("%s_fruit", id), ComponentItem::create)
                .lang(String.format("§3Arcanthus %s Fruit", name))
                .properties(prop -> prop.stacksTo(64))
                .onRegister(attach(new StarTFruitBehaviour(
                        highestTier,
                        newMaterial,
                        materialType)))
                .model((ctx, prov) -> createTextureModel(ctx, prov,
                        StarTCore.resourceLocation(String.format("item/gcrops/fruit_%s", textureType))))
                .color(() -> () -> (itemStack, index) -> newMaterial.getLayerARGB(index))
                .tag(TagUtil.createModItemTag("gcrop_fruit/tier_" + highestTier))
                .register();

        ItemEntry<ComponentItem> gCropFlower = START_REGISTRATE
                .item(String.format("%s_flower", id), ComponentItem::create)
                .lang(String.format("§3Arcanthus %s Flower", name))
                .properties(prop -> prop.stacksTo(64))
                .onRegister(attach(new StarTFruitBehaviour(
                        highestTier,
                        newMaterial,
                        materialType)))
                .model((ctx, prov) -> createTextureModel(ctx, prov,
                        StarTCore.resourceLocation(String.format("item/gcrops/flower_%s", textureType))))
                .color(() -> () -> (itemStack, index) -> newMaterial.getLayerARGB(index))
                .tag(gCropFlowerTag)
                .register();

        GCROP_ITEMS.add(gCropItem);
        GCROP_FLOWERS.add(gCropFlower);
        GCROP_FLOWERMAP.put(newMaterial, gCropFlower);
        GCROP_FRUITS.add(gCropFruit);
        GCROP_FRUITMAP.put(newMaterial, gCropFruit);

        StarTFruitItems.createNeededProcessingItems(id, highestTier);
    }

    public static void init() {
        for (StarTGCropData cropData : StarTGCropData.gCropData) {
            registerGCrop(cropData.getId(), cropData.getName(), cropData.getMaterialType(), cropData.getTextureType(),
                    cropData.getTraits());
        }
        StarTTraitItems.init();
    }
}
