package com.startechnology.start_core.item;

import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.gregtechceu.gtceu.common.item.TooltipBehavior;
import com.startechnology.start_core.item.components.StarTBacteriaBehaviour;
import com.startechnology.start_core.item.components.StarTNBTTooltipsBehaviour;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.List;

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;
import static com.startechnology.start_core.materials.StarTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;

public class StarTBacteriaItems {

    public static <T extends IComponentItem> NonNullConsumer<T> attach(IItemComponent components) {
        return item -> item.attachComponents(components);
    }

    public static final ItemEntry<ComponentItem> BACTERIA_DORMANT = START_REGISTRATE
            .item("bacteria_dormant", ComponentItem::create)
            .lang("§3Dormant Bacteria Colony")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTNBTTooltipsBehaviour()))
            .onRegister(attach(new TooltipBehavior(lines -> {
                lines.add(Component.translatable("item.start_core.bacteria_dormant.tooltip"));
            })))
            .register();

    public static final ItemEntry<ComponentItem> BACTERIA_FERMENTIBACTER_SOLVATIS = START_REGISTRATE
            .item("bacteria_fermentibacter_solvatis", ComponentItem::create)
            .lang("§3Fermentibacter Solvatis Bacteria Colony")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTBacteriaBehaviour(
                    Fermentibacter,
                    Ethanol,
                    AceticAcid,
                    Methanol)))
            .register();

    public static final ItemEntry<ComponentItem> BACTERIA_XYLOTOXIBACTER_AROMATICA = START_REGISTRATE
            .item("bacteria_xylotoxibacter_aromatica", ComponentItem::create)
            .lang("§3Xylopseudomonas Creosotica Bacteria Colony")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTBacteriaBehaviour(
                    Xylopseudomonas,
                    Creosote,
                    Naphthalene,
                    Phenol)))
            .register();

    public static final ItemEntry<ComponentItem> BACTERIA_PETROSPIRILLUM_SOLVANS = START_REGISTRATE
            .item("bacteria_petrospirillum_solvans", ComponentItem::create)
            .lang("§3Petrospirillum Solvans Bacteria Colony")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTBacteriaBehaviour(
                    Petrospirillum,
                    Benzene,
                    Ethane,
                    Acetone)))
            .register();

    public static final ItemEntry<ComponentItem> BACTERIA_OCTANIVORAX_SORBITOLENS = START_REGISTRATE
            .item("bacteria_octanivorax_sorbitolens", ComponentItem::create)
            .lang("§3Octanivorax Sorbitolens Bacteria Colony")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTBacteriaBehaviour(
                    Octanivorax,
                    Octane,
                    Sorbitol,
                    Butane)))
            .register();

    public static final ItemEntry<ComponentItem> BACTERIA_BITUMINIMONAS_COMBUSTILIS = START_REGISTRATE
            .item("bacteria_bituminimonas_combustilis", ComponentItem::create)
            .lang("§3Bituminimonas Combustilis Bacteria Colony")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTBacteriaBehaviour(
                    Bituminimonas,
                    Toluene,
                    Butadiene,
                    Propene)))
            .register();

    public static final ItemEntry<ComponentItem> BACTERIA_CARBANOGASIBACTER_VOLATILIS = START_REGISTRATE
            .item("bacteria_carbanogasibacter_volatilis", ComponentItem::create)
            .lang("§3Carbanogasibacter Volatilis Bacteria Colony")
            .properties(prop -> prop.stacksTo(16))
            .onRegister(attach(new StarTBacteriaBehaviour(
                    Carbanogasibacter,
                    Methane,
                    Butene,
                    Ethylene)))
            .register();

    public static List<ItemEntry<ComponentItem>> BACTERIA_ITEMS = Arrays.asList(
            StarTBacteriaItems.BACTERIA_FERMENTIBACTER_SOLVATIS,
            StarTBacteriaItems.BACTERIA_BITUMINIMONAS_COMBUSTILIS,
            StarTBacteriaItems.BACTERIA_CARBANOGASIBACTER_VOLATILIS,
            StarTBacteriaItems.BACTERIA_OCTANIVORAX_SORBITOLENS,
            StarTBacteriaItems.BACTERIA_PETROSPIRILLUM_SOLVANS,
            StarTBacteriaItems.BACTERIA_XYLOTOXIBACTER_AROMATICA);

    public static void init() {}
}
