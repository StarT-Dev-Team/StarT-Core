package com.startechnology.start_core.machine.komaru.client.v2;

import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
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
import org.joml.Matrix4f;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class KomaruRendererV2 extends DynamicRender<StarTKomaruFrameMachine, KomaruRendererV2> {

    private static final List<RenderData> TO_RENDER = Collections.synchronizedList(new ArrayList<>());
    private static final BufferBuilder BUFFER_BUILDER = new BufferBuilder(256); // also this shouldn't be allocated each frame
    private static final ByteBuf BYTE_BUFFER = Unpooled.buffer(512);

    record RenderData(StarTKomaruFrameMachine machine, float partialTick, Matrix4f transform, Matrix4f modelViewTransform) {
    }

    public static final Codec<KomaruRendererV2> CODEC = Codec.unit(KomaruRendererV2::new);
    public static final DynamicRenderType<StarTKomaruFrameMachine, KomaruRendererV2> TYPE = new DynamicRenderType<>(KomaruRendererV2.CODEC);


    public KomaruRendererV2() {
    }

    @Override
    public DynamicRenderType<StarTKomaruFrameMachine, KomaruRendererV2> getType() {
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
        var transform = poseStack.last().pose().get(new Matrix4f());
        var mvTransform = RenderSystem.getModelViewMatrix().get(new Matrix4f());
        var data = new RenderData(machine, partialTick, transform, mvTransform);
        TO_RENDER.add(data);
    }

    private static void renderImpl(RenderData data) {
        var front = data.machine.getFrontFacing();
        var upwards = data.machine.getUpwardsFacing();
        var flipped = data.machine.isFlipped();
        var back = RelativeDirection.BACK.getRelative(front, upwards, flipped);
        var up = RelativeDirection.UP.getRelative(front, upwards, flipped);

        var centerOffset = 31;
        var beamHeight = 133;

        var center = new Vec3i(back.getStepX() * centerOffset, back.getStepY() * centerOffset, back.getStepZ() * centerOffset)
                .offset(up.getStepX() * 2, up.getStepY() * 2, up.getStepZ() * 2);
        var top = center.offset(up.getStepX() * beamHeight, up.getStepY() * beamHeight, up.getStepZ() * beamHeight);

        var aabb = new AABB(center.getX() + 0.5f, center.getY() + 0.5f, center.getZ() + 0.5f, top.getX() + 0.5f, top.getY() + 0.5f, top.getZ() + 0.5f);
        aabb = aabb.inflate(10, 5, 10);

        BUFFER_BUILDER.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION);
        renderCube(data, (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ);
        var renderedBuffer = BUFFER_BUILDER.end();

        RenderTypes.KOMARU_RENDER.setupRenderState();
        var shaderInstance = RenderTypes.KOMARU_SHADER;
        shaderInstance.KOMARU_POSITION.set(center.getX() + 0.5f, center.getY() + 0.5f, center.getZ() + 0.5f);
        shaderInstance.apply();
        RenderSystem.disableDepthTest();
        BufferUploader.draw(renderedBuffer);
        shaderInstance.clear();
        RenderTypes.KOMARU_RENDER.clearRenderState();
    }

    private static void renderCube(RenderData data, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        renderCubeFace(data, minX, maxY, minZ, minX, maxY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ);
        renderCubeFace(data, minX, minY, minZ, maxX, minY, minZ, maxX, minY, maxZ, minX, minY, maxZ);
        renderCubeFace(data, minX, minY, minZ, minX, maxY, minZ, maxX, maxY, minZ, maxX, minY, minZ);
        renderCubeFace(data, minX, minY, maxZ, maxX, minY, maxZ, maxX, maxY, maxZ, minX, maxY, maxZ);
        renderCubeFace(data, minX, minY, minZ, minX, minY, maxZ, minX, maxY, maxZ, minX, maxY, minZ);
        renderCubeFace(data, maxX, minY, minZ, maxX, maxY, minZ, maxX, maxY, maxZ, maxX, minY, maxZ);
    }

    private static void renderCubeFace(RenderData data,
                                       float x1, float y1, float z1,
                                       float x2, float y2, float z2,
                                       float x3, float y3, float z3,
                                       float x4, float y4, float z4) {
        BUFFER_BUILDER.vertex(data.transform, x1, y1, z1);
        BUFFER_BUILDER.vertex(data.transform, x2, y2, z2);
        BUFFER_BUILDER.vertex(data.transform, x3, y3, z3);
        BUFFER_BUILDER.vertex(data.transform, x1, y1, z1);
        BUFFER_BUILDER.vertex(data.transform, x3, y3, z3);
        BUFFER_BUILDER.vertex(data.transform, x4, y4, z4);
    }

    @Mod.EventBusSubscriber(modid = StarTCore.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void shaderRegistry(RegisterShadersEvent event) throws IOException {
            event.registerShader(new KomaruShaderInstance(event.getResourceProvider(), new ResourceLocation("start_core:komaru_v2"), DefaultVertexFormat.POSITION),
                    shaderInstance -> RenderTypes.KOMARU_SHADER = (KomaruShaderInstance)shaderInstance);
        }
    }

    @Mod.EventBusSubscriber(modid = StarTCore.MOD_ID, value = Dist.CLIENT)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onRenderLevelStageEvent(RenderLevelStageEvent event) {
            if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
                TO_RENDER.clear();
            }

            if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES && !TO_RENDER.isEmpty()) {
                var rtMain = Minecraft.getInstance().getMainRenderTarget();
                RenderTypes.KOMARU_SHADER.updateRenderTarget(rtMain);
                var rtBase = RenderTypes.KOMARU_SHADER.renderTarget;

                GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, rtMain.frameBufferId);
                GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, rtBase.frameBufferId);
                GlStateManager._glBlitFrameBuffer(0, 0, rtMain.width, rtMain.height, 0, 0, rtBase.width, rtBase.height, GlConst.GL_DEPTH_BUFFER_BIT | GlConst.GL_COLOR_BUFFER_BIT, GlConst.GL_NEAREST);
                GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);
            }

            if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER && !TO_RENDER.isEmpty()) {
                var poseStack = event.getPoseStack();
                var modelViewMatrix = poseStack.last().pose().get(new Matrix4f());
                var projectionMatrix = event.getProjectionMatrix();
                var invProjectionMatrix = new Matrix4f(projectionMatrix).invert();

                var mc = Minecraft.getInstance();
                var camera = mc.gameRenderer.getMainCamera();
                var cameraPosition = camera.getPosition().toVector3f();

                var shader = RenderTypes.KOMARU_SHADER;
                shader.setSampler("DiffuseSampler", shader.renderTarget.getColorTextureId());
                shader.setSampler("DiffuseSamplerDepth", shader.renderTarget.getDepthTextureId());
                shader.MODEL_VIEW_MATRIX.set(modelViewMatrix);
                shader.PROJECTION_MATRIX.set(projectionMatrix);
                shader.SCREEN_SIZE.set(shader.renderTarget.width, shader.renderTarget.height);
                shader.GAME_TIME.set(RenderSystem.getShaderGameTime());
                shader.INVERSE_PROJECTION_MATRIX.set(invProjectionMatrix);
                shader.INVERSE_VIEW_ROTATION_MATRIX.set(RenderSystem.getInverseViewRotationMatrix());
                shader.CAMERA_NEAR_PLANE.set(0.05F);
                shader.CAMERA_FAR_PLANE.set(mc.gameRenderer.getDepthFar());
                shader.CAMERA_POSITION.set(cameraPosition);
                RenderSystem.setupShaderLights(shader);

                TO_RENDER.forEach(KomaruRendererV2::renderImpl);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class RenderTypes extends RenderType {

        public static KomaruShaderInstance KOMARU_SHADER;

        public static final RenderStateShard.ShaderStateShard KOMARU_RENDERTYPE_SHADER = new RenderStateShard.ShaderStateShard(() -> KOMARU_SHADER);

        public static final DelayedRenderType KOMARU_RENDER = new DelayedRenderType("komaru_render_type",
                DefaultVertexFormat.POSITION, VertexFormat.Mode.TRIANGLES, 256, false, false,
                RenderType.CompositeState.builder()
                        .setCullState(RenderStateShard.NO_CULL)
                        .setWriteMaskState(RenderStateShard.COLOR_WRITE)
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
