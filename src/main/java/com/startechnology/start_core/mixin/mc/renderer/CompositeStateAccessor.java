package com.startechnology.start_core.mixin.mc.renderer;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderType.CompositeState.class)
public interface CompositeStateAccessor {

    @Accessor("states")
    ImmutableList<RenderStateShard> start_core$getStates();
}
