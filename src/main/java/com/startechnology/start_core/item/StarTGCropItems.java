package com.startechnology.start_core.item;

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

import java.util.Arrays;
import java.util.List;

public class StarTGCropItems {
    public static <T extends IComponentItem> NonNullConsumer<T> attach(IItemComponent components) {
        return item -> item.attachComponents(components);
    }

    public static final ItemEntry<ComponentItem> GCROP_MALFORMED = START_REGISTRATE.item("malformed_gcrop", ComponentItem::create)
            .lang("§3Malformed GCrop")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTNBTTooltipsBehaviour()))
            .onRegister(attach(new TooltipBehavior(lines -> {
                lines.add(Component.translatable("item.start_core.gcrop_malformed.tooltip"));
            })))
            .register();

    public static final ItemEntry<ComponentItem> GCROP_BLACK_DYE = START_REGISTRATE.item("black_dye_gcrop", ComponentItem::create)
            .lang("§3Black GCrop")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTGCropBehaviour(
                    0,
                    GTMaterials.DyeBlack,
                    StarTGCropTraits.Charred
            )))
            .register();

    public static final ItemEntry<ComponentItem> GCROP_RED_DYE = START_REGISTRATE.item("red_dye_gcrop", ComponentItem::create)
            .lang("§3Red GCrop")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTGCropBehaviour(
                    0,
                    GTMaterials.DyeRed,
                    StarTGCropTraits.Charred, StarTGCropTraits.Vibrant, StarTGCropTraits.Tough
            )))
            .register();

    public static final ItemEntry<ComponentItem> GCROP_GREEN_DYE = START_REGISTRATE.item("green_dye_gcrop", ComponentItem::create)
            .lang("§3Green GCrop")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTGCropBehaviour(
                    0,
                    GTMaterials.DyeGreen,
                    StarTGCropTraits.Charred, StarTGCropTraits.Tough, StarTGCropTraits.Fluorescent
            )))
            .register();

    public static final ItemEntry<ComponentItem> GCROP_BROWN_DYE = START_REGISTRATE.item("brown_dye_gcrop", ComponentItem::create)
            .lang("§3Brown GCrop")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTGCropBehaviour(
                    0,
                    GTMaterials.DyeBrown,
                    StarTGCropTraits.Charred, StarTGCropTraits.Tough
            )))
            .register();

    public static final ItemEntry<ComponentItem> GCROP_BLUE_DYE = START_REGISTRATE.item("blue_dye_gcrop", ComponentItem::create)
            .lang("§3Blue GCrop")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTGCropBehaviour(
                    0,
                    GTMaterials.DyeBlue,
                    StarTGCropTraits.Charred, StarTGCropTraits.Vibrant
            )))
            .register();

    public static final ItemEntry<ComponentItem> GCROP_PURPLE_DYE = START_REGISTRATE.item("purple_dye_gcrop", ComponentItem::create)
            .lang("§3Purple GCrop")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTGCropBehaviour(
                    0,
                    GTMaterials.DyePurple,
                    StarTGCropTraits.Charred, StarTGCropTraits.Vibrant, StarTGCropTraits.Fluorescent
            )))
            .register();

    public static final ItemEntry<ComponentItem> GCROP_CYAN_DYE = START_REGISTRATE.item("cyan_dye_gcrop", ComponentItem::create)
            .lang("§3Cyan GCrop")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTGCropBehaviour(
                    0,
                    GTMaterials.DyeCyan,
                    StarTGCropTraits.Charred, StarTGCropTraits.Vibrant, StarTGCropTraits.Tough, StarTGCropTraits.Fluorescent
            )))
            .register();

    public static final ItemEntry<ComponentItem> GCROP_LIGHT_GRAY_DYE = START_REGISTRATE.item("light_gray_dye_gcrop", ComponentItem::create)
            .lang("§3Light Gray GCrop")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTGCropBehaviour(
                    0,
                    GTMaterials.DyeLightGray,
                    StarTGCropTraits.Fluorescent
            )))
            .register();

    public static final ItemEntry<ComponentItem> GCROP_GRAY_DYE = START_REGISTRATE.item("gray_dye_gcrop", ComponentItem::create)
            .lang("§3Gray GCrop")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTGCropBehaviour(
                    0,
                    GTMaterials.DyeGray,
                    StarTGCropTraits.Charred, StarTGCropTraits.Fluorescent
            )))
            .register();

    public static final ItemEntry<ComponentItem> GCROP_PINK_DYE = START_REGISTRATE.item("pink_dye_gcrop", ComponentItem::create)
            .lang("§3Pink GCrop")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTGCropBehaviour(
                    0,
                    GTMaterials.DyePink,
                    StarTGCropTraits.Vibrant, StarTGCropTraits.Fluorescent
            )))
            .register();

    public static final ItemEntry<ComponentItem> GCROP_LIME_DYE = START_REGISTRATE.item("lime_dye_gcrop", ComponentItem::create)
            .lang("§3Lime GCrop")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTGCropBehaviour(
                    0,
                    GTMaterials.DyeLime,
                    StarTGCropTraits.Tough, StarTGCropTraits.Fluorescent
            )))
            .register();

    public static final ItemEntry<ComponentItem> GCROP_YELLOW_DYE = START_REGISTRATE.item("yellow_dye_gcrop", ComponentItem::create)
            .lang("§3Yellow GCrop")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTGCropBehaviour(
                    0,
                    GTMaterials.DyeYellow,
                    StarTGCropTraits.Tough
            )))
            .register();

    public static final ItemEntry<ComponentItem> GCROP_LIGHT_BLUE_DYE = START_REGISTRATE.item("light_blue_dye_gcrop", ComponentItem::create)
            .lang("§3Light Blue GCrop")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTGCropBehaviour(
                    0,
                    GTMaterials.DyeLightBlue,
                    StarTGCropTraits.Vibrant
            )))
            .register();

    public static final ItemEntry<ComponentItem> GCROP_MAGENTA_DYE = START_REGISTRATE.item("magenta_dye_gcrop", ComponentItem::create)
            .lang("§3Magenta GCrop")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTGCropBehaviour(
                    0,
                    GTMaterials.DyeMagenta,
                    StarTGCropTraits.Vibrant, StarTGCropTraits.Tough, StarTGCropTraits.Fluorescent
            )))
            .register();

    public static final ItemEntry<ComponentItem> GCROP_ORANGE_DYE = START_REGISTRATE.item("orange_dye_gcrop", ComponentItem::create)
            .lang("§3Orange GCrop")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTGCropBehaviour(
                    0,
                    GTMaterials.DyeOrange,
                    StarTGCropTraits.Vibrant, StarTGCropTraits.Tough
            )))
            .register();

    public static final ItemEntry<ComponentItem> GCROP_WHITE_DYE = START_REGISTRATE.item("white_dye_gcrop", ComponentItem::create)
            .lang("§3White GCrop")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTGCropBehaviour(
                    0,
                    GTMaterials.DyeWhite
            )))
            .register();

    public static List<ItemEntry<ComponentItem>> GCROP_ITEMS = Arrays.asList(
            StarTGCropItems.GCROP_BLACK_DYE,
            StarTGCropItems.GCROP_RED_DYE,
            StarTGCropItems.GCROP_GREEN_DYE,
            StarTGCropItems.GCROP_BROWN_DYE,
            StarTGCropItems.GCROP_BLUE_DYE,
            StarTGCropItems.GCROP_PURPLE_DYE,
            StarTGCropItems.GCROP_CYAN_DYE,
            StarTGCropItems.GCROP_LIGHT_GRAY_DYE,
            StarTGCropItems.GCROP_GRAY_DYE,
            StarTGCropItems.GCROP_PINK_DYE,
            StarTGCropItems.GCROP_LIME_DYE,
            StarTGCropItems.GCROP_YELLOW_DYE,
            StarTGCropItems.GCROP_LIGHT_BLUE_DYE,
            StarTGCropItems.GCROP_MAGENTA_DYE,
            StarTGCropItems.GCROP_ORANGE_DYE,
            StarTGCropItems.GCROP_WHITE_DYE
    );

    public static void init() {
    }
}
