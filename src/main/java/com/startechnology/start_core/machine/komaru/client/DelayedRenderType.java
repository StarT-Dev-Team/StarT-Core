package com.startechnology.start_core.machine.komaru.client;

import com.mojang.blaze3d.vertex.VertexFormat;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class DelayedRenderType extends RenderType {

    @Setter
    @Getter
    private boolean delay;

    private final CompositeState state;

    public DelayedRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, CompositeState state) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, () -> state.states.forEach(RenderStateShard::setupRenderState), () -> state.states.forEach(RenderStateShard::clearRenderState));
        this.state = state;
    }

    protected final CompositeState state() {
        return this.state;
    }

    @Override
    public String toString() {
        return "RenderType[" + name + ":" + state + "]";
    }

}
