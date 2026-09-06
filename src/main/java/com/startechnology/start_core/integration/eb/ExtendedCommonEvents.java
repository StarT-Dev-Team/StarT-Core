package com.startechnology.start_core.integration.eb;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import nl.requios.effortlessbuilding.EffortlessBuilding;
import nl.requios.effortlessbuilding.compatibility.CompatHelper;
import nl.requios.effortlessbuilding.systems.ServerBuildState;

@Mod.EventBusSubscriber
public class ExtendedCommonEvents {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        // Stop vanilla item placement before BlockItem can shrink the held stack.
        // Block interaction is deliberately left enabled, so doors, chests, etc. still work normally.

        if (event.getSide() == LogicalSide.CLIENT) return;
        Player player = event.getEntity();
        if (player instanceof FakePlayer) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        // Don't interfere while our custom placement logic applies the final block set.
        if (EffortlessBuilding.SERVER_BLOCK_PLACER.isPlacingOrBreakingBlocks()) return;

        if (!ServerBuildState.isLikeVanilla(player) && isBlockPlacementItem(event.getItemStack(), player)) {
            event.setUseItem(Event.Result.DENY);
        }
    }

    public static boolean isBlockPlacementItem(ItemStack itemStack, Player player) {
        return itemStack.getItem() instanceof BlockItem ||
                (CompatHelper.isItemBlockProxy(itemStack) && !player.isShiftKeyDown());
    }
}
