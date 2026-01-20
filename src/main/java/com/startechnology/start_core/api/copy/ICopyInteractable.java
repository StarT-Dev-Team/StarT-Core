package com.startechnology.start_core.api.copy;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ICopyInteractable {
    Component copySettings = Component.translatable("start_core.mechanical_memory_card.copy_settings");
    Component pasteSettings = Component.translatable("start_core.mechanical_memory_card.paste_settings");

    default InteractionResult onUse(Player player, ItemStack card) {
        return InteractionResult.PASS;
    }

    default InteractionResult onShiftUse(Player player, ItemStack card) {
        return InteractionResult.PASS;
    }
}
