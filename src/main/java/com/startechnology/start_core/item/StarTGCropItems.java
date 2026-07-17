package com.startechnology.start_core.item;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.item.TooltipBehavior;
import com.startechnology.start_core.data.gcrops.StarTGCropItemType;
import com.startechnology.start_core.item.components.StarTFruitBehaviour;
import com.startechnology.start_core.item.components.StarTGCropBehaviour;
import com.startechnology.start_core.item.components.StarTNBTTooltipsBehaviour;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import net.minecraft.network.chat.Component;

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;
import static com.startechnology.start_core.data.gcrops.StarTGCropItemType.*;
import static com.startechnology.start_core.data.gcrops.StarTGCropTraits.*;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

public class StarTGCropItems {

    public static final List<ItemEntry<ComponentItem>> GCROP_ITEMS = new ArrayList<>();

    public static <T extends IComponentItem> NonNullConsumer<T> attach(IItemComponent components) {
        return item -> item.attachComponents(components);
    }

    public static final ItemEntry<ComponentItem> GCROP_MALFORMED = START_REGISTRATE
            .item("malformed_gcrop", ComponentItem::create)
            .lang("§3Malformed GCrop")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTNBTTooltipsBehaviour()))
            .onRegister(attach(new TooltipBehavior(lines -> {
                lines.add(Component.translatable("item.start_core.malformed_gcrop.tooltip"));
            })))
            .onRegister(attach(new StarTGCropBehaviour(
                    -1)))
            .register();

    private static void registerGCrop(String id, String name, int tier, Material material,
                                      StarTGCropItemType materialType,
                                      StarTGCropTrait... traits) {
        ItemEntry<ComponentItem> gCropItem = START_REGISTRATE
                .item(String.format("%s_gcrop", id), ComponentItem::create)
                .lang(String.format("§3Arcanthus %s GCrop", name))
                .properties(prop -> prop.stacksTo(16))
                .onRegister(attach(new StarTGCropBehaviour(
                        tier,
                        traits)))
                .register();

        ItemEntry<ComponentItem> gCropFruit = START_REGISTRATE
                .item(String.format("%s_fruit", id), ComponentItem::create)
                .lang(String.format("§3Arcanthus %s Fruit", name))
                .properties(prop -> prop.stacksTo(64))
                .onRegister(attach(new StarTFruitBehaviour(
                        tier,
                        material,
                        materialType)))
                .register();

        GCROP_ITEMS.add(gCropItem);
    }

    static {
        registerGCrop("black_dye", "Tinctoria Umbra", 0, GTMaterials.DyeBlack, DYE, Charred);

        registerGCrop("red_dye", "Tinctoria Ignis", 0, GTMaterials.DyeRed, DYE,
                Charred, Vibrant, Tough);

        registerGCrop("green_dye", "Tinctoria Sylva", 0,
                GTMaterials.DyeGreen, DYE, Charred, Tough, Fluorescent);

        registerGCrop("brown_dye", "Tinctoria Terra", 0,
                GTMaterials.DyeBrown, DYE, Charred, Tough);

        registerGCrop("blue_dye", "Tinctoria Tempestas", 0,
                GTMaterials.DyeBlue, DYE, Charred, Vibrant);

        registerGCrop("purple_dye", "Tinctoria Nyxia", 0,
                GTMaterials.DyePurple, DYE, Charred, Vibrant,
                Fluorescent);

        registerGCrop("cyan_dye", "Tinctoria Maris", 0,
                GTMaterials.DyeCyan, DYE, Charred, Vibrant, Tough,
                Fluorescent);

        registerGCrop("light_gray_dye", "Tinctoria Bruma", 0,
                GTMaterials.DyeLightGray, DYE, Fluorescent);

        registerGCrop("gray_dye", "Tinctoria Petra", 0,
                GTMaterials.DyeGray, DYE, Charred, Fluorescent);

        registerGCrop("pink_dye", "Tinctoria Aurora", 0,
                GTMaterials.DyePink, DYE, Vibrant, Fluorescent);

        registerGCrop("lime_dye", "Tinctoria Vitae", 0,
                GTMaterials.DyeLime, DYE, Tough, Fluorescent);

        registerGCrop("yellow_dye", "Tinctoria Solis", 0,
                GTMaterials.DyeYellow, DYE, Tough);

        registerGCrop("light_blue_dye", "Tinctoria Caelum", 0,
                GTMaterials.DyeLightBlue, DYE, Vibrant);

        registerGCrop("magenta_dye", "Tinctoria Arcana", 0,
                GTMaterials.DyeMagenta, DYE, Vibrant, Tough, Fluorescent);

        registerGCrop("orange_dye", "Tinctoria Phoenicis", 0,
                GTMaterials.DyeOrange, DYE, Vibrant, Tough);

        registerGCrop("white_dye", "Tinctoria Lucis", 0,
                GTMaterials.DyeWhite, DYE);

        // Tier 1
        registerGCrop("iron", "Thumbergia Ferro", 1,
                GTMaterials.Iron, DUST, Fluorescent, Metallic);

        registerGCrop("copper", "Thumbergia Aeris", 1,
                GTMaterials.Copper, DUST, Vibrant, Tough, Metallic);

        registerGCrop("zinc", "Thumbergia Cadmiae", 1,
                GTMaterials.Zinc, DUST, Vibrant, Metallic);

        registerGCrop("tin", "Thumbergia Stagni", 1,
                GTMaterials.Tin, DUST, Vibrant, Metallic);

        registerGCrop("lead", "Thumbergia Plumbum", 1,
                GTMaterials.Lead, DUST, Charred, Metallic);

        registerGCrop("quartz", "Thumbergia Petram", 1,
                GTMaterials.NetherQuartz, GEM, Crystalline);

        registerGCrop("diamond", "Thumbergia Adamas", 1,
                GTMaterials.Diamond, GEM, Vibrant, Crystalline);

        registerGCrop("amethyst", "Thumbergia Hyacintho", 1,
                GTMaterials.Amethyst, GEM, Charred, Vibrant, Fluorescent,
                Crystalline);

        registerGCrop("lapis", "Thumbergia Pristis", 1,
                GTMaterials.Lapis, GEM, Charred, Vibrant, Crystalline);

        registerGCrop("emerald", "Thumbergia Smaragd", 1,
                GTMaterials.Emerald, GEM, Tough, Fluorescent,
                Crystalline);

        registerGCrop("redstone", "Thumbergia Rubrum", 1,
                GTMaterials.Redstone, DUST, Charred, Vibrant, Tough,
                Dusty);

        registerGCrop("sulfur", "Thumbergia Vulcanus", 1,
                GTMaterials.Sulfur, DUST, Vibrant, Tough, Dusty);

        registerGCrop("glowstone", "Thumbergia Solaris", 1,
                GTMaterials.Glowstone, DUST, Dusty);

        registerGCrop("ender", "Thumbergia Marganis", 1,
                GTMaterials.EnderPearl, GEM, Charred, Fluorescent,
                Crystalline);

        registerGCrop("gold", "Potentilla Aurum", 2,
                GTMaterials.Gold, DUST, Tough, Metallic, Shiny);

        registerGCrop("silver", "Potentilla Argentum", 2,
                GTMaterials.Silver, DUST, Fluorescent, Metallic, Shiny);

        registerGCrop("coal", "Potentilla Calculus", 2,
                GTMaterials.Coal, GEM, Charred, Crystalline, Coarse);

        registerGCrop("sodalite", "Potentilla Azura", 2,
                GTMaterials.Sodalite, ORE, Charred, Vibrant, Metallic,
                Coarse);

        registerGCrop("pentlandite", "Potentilla Aurantiaco", 2,
                GTMaterials.Pentlandite, ORE, Vibrant, Tough, Metallic,
                Coarse);

        registerGCrop("realgar", "Potentilla Coccineum", 2,
                GTMaterials.Realgar, GEM, Charred, Vibrant, Tough,
                Crystalline, Coarse);

        registerGCrop("ruby", "Potentilla Rubore", 2,
                GTMaterials.Ruby, GEM, Charred, Vibrant, Tough,
                Crystalline, Shiny);

        registerGCrop("sapphire", "Potentilla Sapphirus", 2,
                GTMaterials.Sapphire, GEM, Vibrant, Crystalline, Shiny);
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

    public static void init() {}
}
