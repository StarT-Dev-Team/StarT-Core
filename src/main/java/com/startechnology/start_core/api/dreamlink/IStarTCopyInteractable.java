package com.startechnology.start_core.api.dreamlink;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IStarTCopyInteractable {

    default InteractionResult onCopyUse(Player player, ItemStack copyItem) {
        return InteractionResult.PASS;
    }

    default InteractionResult onCopyShiftUse(Player player, ItemStack copyItem) {
        return InteractionResult.PASS;
    }
}
