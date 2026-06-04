package com.startechnology.start_core.mixin.embeddium;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.jellysquid.mods.sodium.client.gl.shader.ShaderLoader;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ShaderLoader.class, remap = false)
public class ShaderLoaderMixin {

    @ModifyReturnValue(method = "getShaderSource", at = @At("RETURN"))
    private static String injectLoadShaderGetShaderSource(String contents, @Local(argsOnly = true) ResourceLocation name) {
        if (name.getPath().equals("include/chunk_material.glsl")) {
            contents = contents.replace("float[4](0.0, 0.1, 0.5, 1.0)", "float[4](0.05, 0.1, 0.5, 1.0)");
        }
        return contents;
    }

}
