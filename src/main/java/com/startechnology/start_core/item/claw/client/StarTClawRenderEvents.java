package com.startechnology.start_core.item.claw.client;

import com.startechnology.start_core.StarTCore;

import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = StarTCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class StarTClawRenderEvents {

    private StarTClawRenderEvents() {}

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(StarTClawPawModel.PLAYER_LAYER, StarTClawPawModel::createPlayerLayer);
        event.registerLayerDefinition(StarTClawPawModel.SLIM_PLAYER_LAYER,
                StarTClawPawModel::createSlimPlayerLayer);
    }

    @SubscribeEvent
    public static void addPlayerLayers(EntityRenderersEvent.AddLayers event) {
        for (String skin : event.getSkins()) {
            PlayerRenderer renderer = event.getSkin(skin);

            if (renderer != null) {
                renderer.addLayer(new StarTClawPawLayer(renderer));
            }
        }
    }
}
