package com.startechnology.start_core.machine.komaru.client;

import com.google.common.collect.ImmutableMap;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.gregtechceu.gtceu.client.util.RenderBufferHelper;
import com.gregtechceu.gtceu.utils.GTMatrixUtils;
import com.mojang.blaze3d.vertex.*;
import com.mojang.serialization.Codec;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.machine.komaru.StarTKomaruFrameMachine;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3fc;
import org.joml.Vector4f;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.ByteBuffer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class KomaruRenderer extends DynamicRender<StarTKomaruFrameMachine, KomaruRenderer> {

    public static final Codec<KomaruRenderer> CODEC = Codec.unit(KomaruRenderer::new);
    public static final DynamicRenderType<StarTKomaruFrameMachine, KomaruRenderer> TYPE = new DynamicRenderType<>(KomaruRenderer.CODEC);

    private final ByteBuf byteBuffer = Unpooled.buffer(512);

    public KomaruRenderer() {
    }

    @Override
    public DynamicRenderType<StarTKomaruFrameMachine, KomaruRenderer> getType() {
        return TYPE;
    }

    @Override
    public int getViewDistance() {
        return 1024;
    }

    @Override
    public boolean shouldRenderOffScreen(StarTKomaruFrameMachine machine) {
        return true;
    }

    @Override
    public boolean shouldRender(StarTKomaruFrameMachine machine, Vec3 cameraPos) {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(StarTKomaruFrameMachine machine) {
        // TODO: add caching

        var front = machine.getFrontFacing();
        var upwards = machine.getUpwardsFacing();
        var flipped = machine.isFlipped();
        var back = RelativeDirection.BACK.getRelative(front, upwards, flipped);
        var up = RelativeDirection.UP.getRelative(front, upwards, flipped);

        var centerOffset = 31;
        var beamHeight = 133;

        var center = new Vec3i(back.getStepX() * centerOffset, back.getStepY() * centerOffset, back.getStepZ() * centerOffset)
                .offset(up.getStepX() * 2, up.getStepY() * 2, up.getStepZ() * 2);
        var top = center.offset(up.getStepX() * beamHeight, up.getStepY() * beamHeight, up.getStepZ() * beamHeight);

        var aabb = new AABB(center.getX() + 0.5f, center.getY() + 0.5f, center.getZ() + 0.5f, top.getX() + 0.5f, top.getY() + 0.5f, top.getZ() + 0.5f);
        aabb = aabb.inflate(10, 5, 10);

        return aabb;
    }

    @Override
    public void render(StarTKomaruFrameMachine machine, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        // TODO: add caching

        var front = machine.getFrontFacing();
        var upwards = machine.getUpwardsFacing();
        var flipped = machine.isFlipped();
        var back = RelativeDirection.BACK.getRelative(front, upwards, flipped);
        var up = RelativeDirection.UP.getRelative(front, upwards, flipped);

        var centerOffset = 31;
        var beamHeight = 133;

        var center = new Vec3i(back.getStepX() * centerOffset, back.getStepY() * centerOffset, back.getStepZ() * centerOffset)
                .offset(up.getStepX() * 2, up.getStepY() * 2, up.getStepZ() * 2);
        var top = center.offset(up.getStepX() * beamHeight, up.getStepY() * beamHeight, up.getStepZ() * beamHeight);

        var aabb = new AABB(center.getX() + 0.5f, center.getY() + 0.5f, center.getZ() + 0.5f, top.getX() + 0.5f, top.getY() + 0.5f, top.getZ() + 0.5f);
        aabb = aabb.inflate(5, 1, 5);

        var data = new RenderData(center);
        byteBuffer.clear();
        renderCube(poseStack.last(), data, (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ);
        var builder = (BufferBuilder)buffer.getBuffer(KomaruRenderer.RenderTypes.KOMARU_RENDER);
        builder.putBulkData(byteBuffer.nioBuffer());
    }

    record RenderData(Vec3i center) {
    }

    private void renderCube(PoseStack.Pose pose, RenderData data, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        renderCubeFace(pose, data, minX, maxY, minZ, minX, maxY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ);
        renderCubeFace(pose, data, minX, minY, minZ, maxX, minY, minZ, maxX, minY, maxZ, minX, minY, maxZ);
        renderCubeFace(pose, data, minX, minY, minZ, minX, maxY, minZ, maxX, maxY, minZ, maxX, minY, minZ);
        renderCubeFace(pose, data, minX, minY, maxZ, maxX, minY, maxZ, maxX, maxY, maxZ, minX, maxY, maxZ);
        renderCubeFace(pose, data, minX, minY, minZ, minX, minY, maxZ, minX, maxY, maxZ, minX, maxY, minZ);
        renderCubeFace(pose, data, maxX, minY, minZ, maxX, maxY, minZ, maxX, maxY, maxZ, maxX, minY, maxZ);
    }

    private void renderCubeFace(PoseStack.Pose pose, RenderData data,
                                float x1, float y1, float z1,
                                float x2, float y2, float z2,
                                float x3, float y3, float z3,
                                float x4, float y4, float z4) {
        vertex(pose, data, x1, y1, z1);
        vertex(pose, data, x2, y2, z2);
        vertex(pose, data, x3, y3, z3);
        vertex(pose, data, x4, y4, z4);
    }

    private void vertex(PoseStack.Pose pose, RenderData data, float x, float y, float z) {
        var pos = pose.pose().transform(new Vector4f(x, y, z, 1.0f));

        byteBuffer.ensureWritable(24);
        byteBuffer.writeFloat(pos.x);
        byteBuffer.writeFloat(pos.y);
        byteBuffer.writeFloat(pos.z);
        byteBuffer.writeFloat(data.center.getX() + 0.5f);
        byteBuffer.writeFloat(data.center.getY() + 0.5f);
        byteBuffer.writeFloat(data.center.getZ() + 0.5f);
    }

    @Mod.EventBusSubscriber(modid = StarTCore.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void shaderRegistry(RegisterShadersEvent event) throws IOException {
            event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation("start_core:komaru"), RenderTypes.KOMARU_VERTEX_FORMAT),
                    shaderInstance -> RenderTypes.KOMARU_SHADER = shaderInstance);
        }
    }

    @Mod.EventBusSubscriber(modid = StarTCore.MOD_ID, value = Dist.CLIENT)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onRenderLevelStageEvent(RenderLevelStageEvent event) {
            if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
                RenderTypes.KOMARU_RENDER.setDelay(true);
            }

            if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
                RenderTypes.KOMARU_RENDER.setDelay(false);
                var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
                bufferSource.endBatch(RenderTypes.KOMARU_RENDER);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class RenderTypes extends RenderType {

        public static ShaderInstance KOMARU_SHADER;

        public static final RenderStateShard.ShaderStateShard KOMARU_RENDERTYPE_SHADER = new RenderStateShard.ShaderStateShard(() -> KOMARU_SHADER);

        public static final VertexFormat KOMARU_VERTEX_FORMAT = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder()
                .put("Position", DefaultVertexFormat.ELEMENT_POSITION)
                .put("KomaruPosition", DefaultVertexFormat.ELEMENT_POSITION)
                // add timings data
                .build());

        public static final DelayedRenderType KOMARU_RENDER = new DelayedRenderType("komaru_render_type",
                KOMARU_VERTEX_FORMAT, VertexFormat.Mode.QUADS, 256, false, false,
                RenderType.CompositeState.builder()
                        .setCullState(RenderStateShard.CULL)
                        .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                        .setShaderState(KOMARU_RENDERTYPE_SHADER)
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        .createCompositeState(false));

        // protected access hack
        private RenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
            super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
        }
    }

}
