package com.startechnology.start_core.machine.dyson_sphere.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.startechnology.start_core.StarTCore;

import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.startechnology.start_core.client.megastructure.MegastructureShaders.register;

@Mod.EventBusSubscriber(modid = StarTCore.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DysonShaders {

    static ShaderInstance star;
    static ShaderInstance ring;
    static ShaderInstance halo;
    static ShaderInstance godRays;

    private DysonShaders() {}

    static boolean loaded() {
        return star != null && ring != null && halo != null && godRays != null;
    }

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) {
        register(event, "dyson_star", DefaultVertexFormat.POSITION, shader -> star = shader);
        register(event, "dyson_ring", DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, shader -> ring = shader);
        register(event, "dyson_halo", DefaultVertexFormat.POSITION, shader -> halo = shader);
        register(event, "dyson_godrays", DefaultVertexFormat.POSITION, shader -> godRays = shader);
    }
}
