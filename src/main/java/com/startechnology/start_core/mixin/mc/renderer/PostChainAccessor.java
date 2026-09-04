package com.startechnology.start_core.mixin.mc.renderer;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(PostChain.class)
public interface PostChainAccessor {

    @Accessor("passes")
    List<PostPass> start_core$getPasses();

    @Accessor("customRenderTargets")
    Map<String, RenderTarget> start_core$getCustomRenderTargets();
}
