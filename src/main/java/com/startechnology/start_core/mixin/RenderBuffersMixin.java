package com.startechnology.start_core.mixin;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.startechnology.start_core.machine.komaru.client.v2.KomaruRendererV2;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.SortedMap;

@Mixin(RenderBuffers.class)
public class RenderBuffersMixin {

    @Shadow
    @Final
    private SortedMap<RenderType, BufferBuilder> fixedBuffers;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void injectConstructor(CallbackInfo ci) {
        fixedBuffers.put(KomaruRendererV2.RenderTypes.KOMARU_RENDER, new BufferBuilder(KomaruRendererV2.RenderTypes.KOMARU_RENDER.bufferSize()));
    }

}
