package com.startechnology.start_core.mixin;

import com.startechnology.start_core.machine.komaru.client.DelayedRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiBufferSource.BufferSource.class)
public class MultiBufferSourceBufferSourceMixin {

    @Inject(method = "endBatch(Lnet/minecraft/client/renderer/RenderType;)V", at = @At("HEAD"), cancellable = true)
    private void injectEndBatch(RenderType renderType, CallbackInfo ci) {
        if (renderType instanceof DelayedRenderType delayedRenderType && delayedRenderType.isDelay()) {
            ci.cancel();
        }
    }

}
