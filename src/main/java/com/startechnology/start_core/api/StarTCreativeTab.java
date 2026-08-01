package com.startechnology.start_core.api;

import com.gregtechceu.gtceu.common.data.GTCreativeModeTabs;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.item.StarTItems;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

public class StarTCreativeTab {

    public static RegistryEntry<CreativeModeTab> START_CORE = START_REGISTRATE.defaultCreativeTab(StarTCore.MOD_ID,
            builder -> builder
                    .displayItems(
                            new GTCreativeModeTabs.RegistrateDisplayItemsGenerator(StarTCore.MOD_ID, START_REGISTRATE))
                    .title(Component.translatable("tab.start_core.creative"))
                    .icon(StarTItems.TOOL_DATA_DNA_DISK::asStack)
                    .build())
            .register();

    public static void init() {}
}
