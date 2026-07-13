package com.startechnology.start_core.item;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.item.TooltipBehavior;
import com.startechnology.start_core.item.components.StarTGCropBehaviour;
import com.startechnology.start_core.item.components.StarTNBTTooltipsBehaviour;
import com.startechnology.start_core.data.gcrops.StarTGCropTraits;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import net.minecraft.network.chat.Component;

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

import java.util.ArrayList;
import java.util.List;

public class StarTGCropItems {

    public static final List<ItemEntry<ComponentItem>> GCROP_ITEMS = new ArrayList();

    public static <T extends IComponentItem> NonNullConsumer<T> attach(IItemComponent components) {
        return item -> item.attachComponents(components);
    }

    public static final ItemEntry<ComponentItem> GCROP_MALFORMED = START_REGISTRATE
            .item("malformed_gcrop", ComponentItem::create)
            .lang("§3Malformed GCrop")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTNBTTooltipsBehaviour()))
            .onRegister(attach(new TooltipBehavior(lines -> {
                lines.add(Component.translatable("item.start_core.gcrop_malformed.tooltip"));
            })))
            .register();

    private static void registerGCrop(String id, String name, int tier, Material material,
                                      StarTGCropTraits.StarTGCropTrait... traits) {
        var gCropItem = START_REGISTRATE
                .item(String.format("%s_gcrop", id), ComponentItem::create)
                .lang(String.format("§3%s GCrop", name))
                .properties(prop -> prop.stacksTo(16))
                .onRegister(attach(new StarTGCropBehaviour(
                        tier,
                        material,
                        traits)))
                .register();
        GCROP_ITEMS.add(gCropItem);
    }

    static {
        registerGCrop("black_dye", "Black", 0, GTMaterials.DyeBlack, StarTGCropTraits.Charred);

        registerGCrop("red_dye", "Red", 0, GTMaterials.DyeRed,
                StarTGCropTraits.Charred, StarTGCropTraits.Vibrant, StarTGCropTraits.Tough);

        registerGCrop("green_dye", "Green", 0,
                GTMaterials.DyeGreen, StarTGCropTraits.Charred, StarTGCropTraits.Tough, StarTGCropTraits.Fluorescent);

        registerGCrop("brown_dye", "Brown", 0,
                GTMaterials.DyeBrown, StarTGCropTraits.Charred, StarTGCropTraits.Tough);

        registerGCrop("blue_dye", "Blue", 0,
                GTMaterials.DyeBlue, StarTGCropTraits.Charred, StarTGCropTraits.Vibrant);

        registerGCrop("purple_dye", "Purple", 0,
                GTMaterials.DyePurple, StarTGCropTraits.Charred, StarTGCropTraits.Vibrant,
                StarTGCropTraits.Fluorescent);

        registerGCrop("cyan_dye", "Cyan", 0,
                GTMaterials.DyeCyan, StarTGCropTraits.Charred, StarTGCropTraits.Vibrant, StarTGCropTraits.Tough,
                StarTGCropTraits.Fluorescent);

        registerGCrop("light_gray_dye", "Light Gray", 0,
                GTMaterials.DyeLightGray, StarTGCropTraits.Fluorescent);

        registerGCrop("gray_dye", "Gray", 0,
                GTMaterials.DyeGray, StarTGCropTraits.Charred, StarTGCropTraits.Fluorescent);

        registerGCrop("pink_dye", "Pink", 0,
                GTMaterials.DyePink, StarTGCropTraits.Vibrant, StarTGCropTraits.Fluorescent);

        registerGCrop("lime_dye", "Lime", 0,
                GTMaterials.DyeLime, StarTGCropTraits.Tough, StarTGCropTraits.Fluorescent);

        registerGCrop("yellow_dye", "Yellow", 0,
                GTMaterials.DyeYellow, StarTGCropTraits.Tough);

        registerGCrop("light_blue_dye", "Light Blue", 0,
                GTMaterials.DyeLightBlue, StarTGCropTraits.Vibrant);

        registerGCrop("magenta_dye", "Magenta", 0,
                GTMaterials.DyeMagenta, StarTGCropTraits.Vibrant, StarTGCropTraits.Tough, StarTGCropTraits.Fluorescent);

        registerGCrop("orange_dye", "Orange", 0,
                GTMaterials.DyeOrange, StarTGCropTraits.Vibrant, StarTGCropTraits.Tough);

        registerGCrop("white_dye", "White", 0,
                GTMaterials.DyeWhite);
    }

    public static void init() {}
}
