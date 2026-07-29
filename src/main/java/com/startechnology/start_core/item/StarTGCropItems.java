package com.startechnology.start_core.item;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.api.item.TagPrefixItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.item.TooltipBehavior;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.gcrop.StarTGCropItemType;
import com.startechnology.start_core.item.components.StarTFruitBehaviour;
import com.startechnology.start_core.item.components.StarTGCropBehaviour;
import com.startechnology.start_core.item.components.StarTGenomeHolderBehaviour;
import com.startechnology.start_core.item.components.StarTNBTTooltipsBehaviour;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.network.chat.Component;

import static com.gregtechceu.gtceu.common.data.models.GTModels.createTextureModel;
import static com.startechnology.start_core.StarTCore.START_REGISTRATE;
import static com.startechnology.start_core.api.gcrop.StarTGCropItemType.*;
import static com.startechnology.start_core.data.gcrops.StarTGCropTraits.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

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

    public static @Nullable ItemEntry<ComponentItem> getGCropByGenome(@NotNull List<StarTGCropTrait> traits) {
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
                    StarTCore.resourceLocation("item/gcrops/malformed_gcrop")))
            .register();

    public static final ItemEntry<ComponentItem> EMPTY_GENOME_HOLDER = START_REGISTRATE
            .item("empty_genome_holder", ComponentItem::create)
            .lang("§3Empty Genome Holder")
            .properties(prop -> prop.stacksTo(64))
            .model((ctx, prov) -> createTextureModel(ctx, prov,
                    StarTCore.resourceLocation("item/gcrops/malformed_gcrop")))
            .register();

    public static final ItemEntry<ComponentItem> GCROP_MALFORMED = START_REGISTRATE
            .item("malformed_gcrop", ComponentItem::create)
            .lang("§3Malformed GCrop")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTNBTTooltipsBehaviour()))
            .onRegister(attach(new TooltipBehavior(lines -> {
                lines.add(Component.translatable("item.start_core.malformed_gcrop.tooltip"));
            })))
            .onRegister(attach(new StarTGCropBehaviour(-1, GTMaterials.Stone)))
            .model((ctx, prov) -> createTextureModel(ctx, prov,
                    StarTCore.resourceLocation("item/gcrops/malformed_gcrop")))
            .register();

    private static void registerGCrop(String id, String name,
                                      StarTGCropItemType materialType, String flowerType,
                                      StarTGCropTrait... traits) {
        var newMaterial = GTMaterials.get(id.equals("sheldonite") ? "cooperite" : id);

        ItemColor itemColor = TagPrefixItem.tintColor(newMaterial);

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
                        StarTCore.resourceLocation(String.format("item/gcrops/seed_%s", flowerType))))
                .color(() -> () -> itemColor)
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
                        StarTCore.resourceLocation(String.format("item/gcrops/fruit_%s", flowerType))))
                .color(() -> () -> itemColor)
                .register();

        ItemEntry<ComponentItem> gCropFlower = START_REGISTRATE
                .item(String.format("%s_flower", id), ComponentItem::create)
                .lang(String.format("§3Arcanthus %s Fruit", name))
                .properties(prop -> prop.stacksTo(64))
                .onRegister(attach(new StarTFruitBehaviour(
                        highestTier,
                        newMaterial,
                        materialType)))
                .model((ctx, prov) -> createTextureModel(ctx, prov,
                        StarTCore.resourceLocation(String.format("item/gcrops/flower_%s", flowerType))))
                .color(() -> () -> itemColor)
                .register();

        GCROP_ITEMS.add(gCropItem);
        GCROP_FLOWERS.add(gCropFlower);
        GCROP_FLOWERMAP.put(newMaterial, gCropFlower);
        GCROP_FRUITS.add(gCropFruit);
        GCROP_FRUITMAP.put(newMaterial, gCropFruit);
    }

    static {
        // Tier 0
        registerGCrop("black_dye", "Tinctoria Umbra", DYE, "one", Charred);

        registerGCrop("red_dye", "Tinctoria Ignis", DYE, "three", Charred, Vibrant, Tough);

        registerGCrop("green_dye", "Tinctoria Sylva", DYE, "one", Charred, Tough, Fluorescent);

        registerGCrop("brown_dye", "Tinctoria Terra", DYE, "four", Charred, Tough);

        registerGCrop("blue_dye", "Tinctoria Tempestas", DYE, "one", Charred, Vibrant);

        registerGCrop("purple_dye", "Tinctoria Nyxia", DYE, "three", Charred, Vibrant, Fluorescent);

        registerGCrop("cyan_dye", "Tinctoria Maris", DYE, "two", Charred, Vibrant, Tough, Fluorescent);

        registerGCrop("light_gray_dye", "Tinctoria Bruma", DYE, "two", Fluorescent);

        registerGCrop("gray_dye", "Tinctoria Petra", DYE, "four", Charred, Fluorescent);

        registerGCrop("pink_dye", "Tinctoria Aurora", DYE, "two", Vibrant, Fluorescent);

        registerGCrop("lime_dye", "Tinctoria Vitae", DYE, "one", Tough, Fluorescent);

        registerGCrop("yellow_dye", "Tinctoria Solis", DYE, "three", Tough);

        registerGCrop("light_blue_dye", "Tinctoria Caelum", DYE, "one", Vibrant);

        registerGCrop("magenta_dye", "Tinctoria Arcana", DYE, "two", Vibrant, Tough, Fluorescent);

        registerGCrop("orange_dye", "Tinctoria Phoenicis", DYE, "one", Vibrant, Tough);

        registerGCrop("white_dye", "Tinctoria Lucis", DYE, "four");

        // Tier 1
        registerGCrop("iron", "Thumbergia Ferro", DUST, "three", Fluorescent, Metallic);

        registerGCrop("copper", "Thumbergia Aeris", DUST, "four", Vibrant, Tough, Metallic);

        registerGCrop("zinc", "Thumbergia Cadmiae", DUST, "one", Vibrant, Metallic);

        registerGCrop("tin", "Thumbergia Stagni", DUST, "two", Vibrant, Metallic);

        registerGCrop("lead", "Thumbergia Plumbum", DUST, "four", Charred, Metallic);

        registerGCrop("nether_quartz", "Thumbergia Petram", GEM, "one", Crystalline);

        registerGCrop("diamond", "Thumbergia Adamas", GEM, "three", Vibrant, Crystalline);

        registerGCrop("amethyst", "Thumbergia Hyacintho", GEM, "one", Charred, Vibrant, Fluorescent, Crystalline);

        registerGCrop("lapis", "Thumbergia Pristis", GEM, "four", Charred, Vibrant, Crystalline);

        registerGCrop("emerald", "Thumbergia Smaragd", GEM, "two", Tough, Fluorescent, Crystalline);

        registerGCrop("redstone", "Thumbergia Rubrum", DUST, "four", Charred, Vibrant, Tough, Dusty);

        registerGCrop("sulfur", "Thumbergia Vulcanus", DUST, "three", Vibrant, Tough, Dusty);

        registerGCrop("glowstone", "Thumbergia Solaris", DUST, "one", Dusty);

        registerGCrop("ender_pearl", "Thumbergia Marganis", GEM, "two", Charred, Fluorescent, Crystalline);

        // Tier 2
        registerGCrop("gold", "Potentilla Aurum", DUST, "four", Tough, Metallic, Shiny);

        registerGCrop("silver", "Potentilla Argentum", DUST, "one", Fluorescent, Metallic, Shiny);

        registerGCrop("coal", "Potentilla Calculus", GEM, "three", Charred, Crystalline, Coarse);

        registerGCrop("sodalite", "Potentilla Azura", ORE, "one", Charred, Vibrant, Metallic, Coarse);

        registerGCrop("pentlandite", "Potentilla Aurantiaco", ORE, "four", Vibrant, Tough, Metallic, Coarse);

        registerGCrop("realgar", "Potentilla Coccineum", GEM, "two", Charred, Vibrant, Tough, Crystalline, Coarse);

        registerGCrop("ruby", "Potentilla Rubore", GEM, "one", Charred, Vibrant, Tough, Crystalline, Shiny);

        registerGCrop("sapphire", "Potentilla Sapphirus", GEM, "two", Vibrant, Crystalline, Shiny);

        // Tier 3
        registerGCrop("spessartine", "Bergerocereus Aurangemma", GEM, "one", Vibrant, Tough, Crystalline, Illuminating);

        registerGCrop("apatite", "Bergerocereus Dolosus", GEM, "four", Vibrant, Crystalline, Illuminating);

        registerGCrop("monazite", "Bergerocereus Solus", GEM, "one", Charred, Tough, Fluorescent, Crystalline,
                Illuminating);

        registerGCrop("topaz", "Bergerocereus Caloris", GEM, "four", Vibrant, Tough, Crystalline, Shiny, Illuminating);

        registerGCrop("certus_quartz", "Bergerocereus Certibus", GEM, "two", Charred, Vibrant, Tough, Fluorescent,
                Crystalline, Shiny, Illuminating);

        registerGCrop("lepidolite", "Bergerocereus Squamae", ORE, "four", Charred, Vibrant, Tough, Metallic, Coarse,
                Mineralic);

        registerGCrop("pyrochlore", "Bergerocereus Viridignis", ORE, "one", Vibrant, Metallic, Coarse, Mineralic);

        registerGCrop("pyrolusite", "Bergerocereus Lava", ORE, "two", Charred, Fluorescent, Metallic, Coarse,
                Mineralic);

        registerGCrop("magnesite", "Bergerocereus Magnetes", ORE, "one", Metallic, Coarse, Mineralic);

        registerGCrop("cobaltite", "Bergerocereus Parviridi", ORE, "two", Charred, Vibrant, Metallic, Shiny, Mineralic);

        registerGCrop("vanadium_magnetite", "Bergerocereus Pulchritudo", ORE, "two", Charred, Metallic, Shiny,
                Mineralic);

        registerGCrop("chromite", "Bergerocereus Pigmentatio", ORE, "two", Tough, Fluorescent, Metallic, Shiny,
                Mineralic);

        registerGCrop("rare_earth", "Bergerocereus Terra", DUST, "one", Vibrant, Fluorescent, Dusty, Shiny,
                Illuminating, Mineralic);

        // Tier 4
        registerGCrop("zavaritskite", "Gnaphalium Fluxus", ORE, "one", Charred, Metallic, Shiny, Mineralic, Sulfuric);

        registerGCrop("beryllium", "Gnaphalium Dulcis", ORE, "two", Vibrant, Metallic, Coarse, Mineralic, Aetheric);

        registerGCrop("barite", "Gnaphalium Gravibus", ORE, "four", Tough, Metallic, Coarse, Mineralic, Sulfuric);

        registerGCrop("chalcopyrite", "Gnaphalium ", ORE, "three", Fluorescent, Metallic, Coarse, Mineralic, Sulfuric);

        registerGCrop("bornite", "Gnaphalium Flaeris", ORE, "two", Vibrant, Metallic, Shiny, Mineralic, Sulfuric);

        registerGCrop("pollucite", "Gnaphalium Geminos", ORE, "two", Fluorescent, Metallic, Shiny, Mineralic, Aetheric);

        registerGCrop("cassiterite", "Gnaphalium Stannum", ORE, "four", Tough, Metallic, Coarse, Mineralic, Aetheric);

        registerGCrop("tantalite", "Gnaphalium Tormentati", ORE, "four", Charred, Metallic, Shiny, Mineralic, Aetheric);

        registerGCrop("nether_air", "Gnaphalium Caelitcher", LIQUID, "one", Charred, Crystalline, Illuminating,
                Sulfuric);

        registerGCrop("ender_air", "Gnaphalium Caelinanis", LIQUID, "two", Fluorescent, Crystalline, Illuminating,
                Aetheric);

        // Tier 5
        registerGCrop("tungstate", "Dicanthium Lupispuma", ORE, "four", Vibrant, Tough, Fluorescent, Metallic, Coarse,
                Mineralic, Aetheric, Adaptive);

        registerGCrop("ilmenite", "Dicanthium Metallans", ORE, "four", Charred, Tough, Metallic, Coarse, Mineralic,
                Sulfuric, Energetic);

        registerGCrop("sheldonite", "Dicanthium Argentinium", ORE, "one", Tough, Metallic, Shiny, Mineralic, Sulfuric,
                Adaptive);

        registerGCrop("molybdenite", "Dicanthium Iniuriambum", ORE, "one", Vibrant, Fluorescent, Dusty, Shiny,
                Mineralic, Sulfuric, Adaptive);

        registerGCrop("bauxite", "Dicanthium Lumetallum", ORE, "three", Vibrant, Tough, Metallic, Coarse, Mineralic,
                Sulfuric, Adaptive);

        registerGCrop("pitchblende", "Dicanthium Deucaeli", ORE, "three", Charred, Vibrant, Dusty, Coarse, Mineralic,
                Sulfuric, Energetic);

        registerGCrop("bastnasite", "Dicanthium Cultio", ORE, "two", Charred, Metallic, Coarse, Mineralic, Aetheric,
                Adaptive);

        registerGCrop("blaze", "Dicanthium Elementignis", LIQUID, "one", Charred, Dusty, Shiny, Illuminating, Aetheric,
                Energetic);

        registerGCrop("blizz", "Dicanthium Elementacies", LIQUID, "three", Vibrant, Dusty, Shiny, Illuminating,
                Aetheric, Energetic);

        registerGCrop("basalz", "Dicanthium Elementerra", LIQUID, "two", Tough, Dusty, Shiny, Illuminating, Aetheric,
                Energetic);

        registerGCrop("blitz", "Dicanthium Elementulgur", LIQUID, "four", Fluorescent, Dusty, Shiny, Illuminating,
                Aetheric, Energetic);

        // Tier 6
        registerGCrop("naquadah", "Echinocereus Metalligrum", ORE, "one", Charred, Tough, Metallic, Crystalline, Shiny,
                Mineralic, Sulfuric, Energetic, Apothic);

        registerGCrop("debris", "Echinocereus Rudera", DUST, "three", Charred, Tough, Metallic, Coarse, Mineralic,
                Sulfuric, Adaptive, Apothic);

        // Tier 7
        registerGCrop("titanite", "Psoralidium Cuneus", ORE, "three", Metallic, Shiny, Mineralic, Aetheric, Energetic,
                Siliceous);

        registerGCrop("xenotime", "Psoralidium Vanonor", ORE, "four", Vibrant, Tough, Crystalline, Shiny, Illuminating,
                Sulfuric, Energetic, Apothic, Siliceous);

        registerGCrop("zapolite", "Psoralidium ", ORE, "three", Charred, Vibrant, Fluorescent, Metallic, Coarse,
                Mineralic, Sulfuric, Adaptive, Apothic, Siliceous);

        registerGCrop("lautarite", "Psoralidium Resistentia", ORE, "one", Charred, Vibrant, Dusty, Coarse, Illuminating,
                Aetheric, Energetic, Apothic, Siliceous);

        registerGCrop("crookesite", "Psoralidium Ramusculus", ORE, "one", Tough, Fluorescent, Dusty, Shiny, Mineralic,
                Sulfuric, Adaptive, Siliceous);

        registerGCrop("kitkaite", "Psoralidium Sulfentum", ORE, "four", Fluorescent, Dusty, Coarse, Mineralic, Aetheric,
                Adaptive, Siliceous);

        registerGCrop("celestine", "Psoralidium Coelicola", ORE, "two", Vibrant, Fluorescent, Dusty, Coarse, Mineralic,
                Aetheric, Adaptive, Siliceous);
    }

    public static void init() {}
}
