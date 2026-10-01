package com.startechnology.start_core.client.megastructure;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.startechnology.start_core.StarTCore;

import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.client.event.RegisterShadersEvent;

import java.util.function.Consumer;

public final class MegastructureShaders {

    private MegastructureShaders() {}

    public static void register(RegisterShadersEvent event, String name, VertexFormat format,
                                Consumer<ShaderInstance> target) {
        try {
            event.registerShader(new ShaderInstance(event.getResourceProvider(), StarTCore.resourceLocation(name),
                    format), target);
        } catch (Exception e) {
            target.accept(null);
            StarTCore.LOGGER.error("Failed to load the shader {}", name, e);
        }
    }
}
