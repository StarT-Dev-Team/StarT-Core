package com.startechnology.start_core.machine.dimensional_destabiliser.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.startechnology.start_core.StarTCore;

import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.startechnology.start_core.client.megastructure.MegastructureShaders.register;

@Mod.EventBusSubscriber(modid = StarTCore.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class RiftShaders {

    static ShaderInstance rift;

    private RiftShaders() {}

    static boolean loaded() {
        return rift != null;
    }

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) {
        register(event, "rift", DefaultVertexFormat.POSITION, shader -> rift = shader);
    }
}
