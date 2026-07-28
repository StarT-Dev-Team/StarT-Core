package com.startechnology.start_core.integration.eb;

import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nl.requios.effortlessbuilding.EffortlessBuildingClient;
import nl.requios.effortlessbuilding.buildmode.BuildModeEnum;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ExtendedClientEvents {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        // Mirror the server-side suppression before the client predicts BlockItem placement and consumption.

        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        var usesCustomPlacement = EffortlessBuildingClient.BUILD_MODES.getBuildMode() != BuildModeEnum.DISABLED ||
                EffortlessBuildingClient.BUILD_SETTINGS.isQuickReplacing();
        if (usesCustomPlacement && ExtendedCommonEvents.isBlockPlacementItem(event.getItemStack(), event.getEntity())) {
            event.setUseItem(Event.Result.DENY);
        }
    }
}
