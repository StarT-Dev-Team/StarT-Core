package com.startechnology.start_core.mixin.embeddium;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.jellysquid.mods.sodium.client.render.chunk.shader.ChunkShaderOptions;
import me.jellysquid.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ChunkShaderOptions.class, remap = false)
public class ChunkShaderOptionsMixin {

    @WrapOperation(method = "constants", at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/chunk/terrain/TerrainRenderPass;supportsFragmentDiscard()Z"))
    private boolean wrapConstants(TerrainRenderPass instance, Operation<Boolean> original) {
        return true;
    }

}
