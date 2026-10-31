package com.startechnology.start_core.item.claw;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class StarTClawEvents {

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        ItemStack stack = player.getMainHandItem();

        if (event.getHand() != InteractionHand.MAIN_HAND || player.isCreative() ||
                !(stack.getItem() instanceof StarTClawOfLawItem)) {
            return;
        }

        if (player.getCooldowns().isOnCooldown(stack.getItem())) {
            event.setCanceled(true);

            return;
        }

        if (event.getAction() != PlayerInteractEvent.LeftClickBlock.Action.START ||
                !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (StarTClawOfLawItem.canInstantBreak(stack, serverPlayer.level(), event.getPos())) {
            StarTClawOfLawItem.instantBreak(serverPlayer, stack, event.getPos());

            event.setCanceled(true);
        }
    }
}
