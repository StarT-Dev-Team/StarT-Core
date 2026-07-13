package com.startechnology.start_core.machine.komaru.client.v2;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.startechnology.start_core.mixin.mc.renderer.CompositeStateAccessor;
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

    public DelayedRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
                             boolean affectsCrumbling, boolean sortOnUpload, CompositeState state) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload,
                () -> ((CompositeStateAccessor) (Object) state).start_core$getStates()
                        .forEach(RenderStateShard::setupRenderState),
                () -> ((CompositeStateAccessor) (Object) state).start_core$getStates()
                        .forEach(RenderStateShard::clearRenderState));
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
