package com.startechnology.start_core.machine.komaru.client;

import cofh.core.client.PostEffect;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderManager;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.machine.komaru.StarTKomaruFrameMachine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = StarTCore.MOD_ID, value = Dist.CLIENT)
public final class HookLevelRenderer {

    public static List<StarTKomaruFrameMachine> COLLECTED_RENDERS = new ArrayList<>();

    private static final CubeMapTexture CUBE_MAP_TEXTURE = new CubeMapTexture(StarTCore.resourceLocation("textures/rift_skybox"), false);

    static {
        DynamicRenderManager.register(StarTCore.resourceLocation("komaru_renderer"), KomaruRenderer.TYPE);
    }

    private static void onRenderAfterParticles(RenderLevelStageEvent event) {
        if (Minecraft.useShaderTransparency()) {
            updateKomaruFancyPostEffect();
        } else {
            updateKomaruPostEffect();
        }
    }


    private static void updateKomaruPostEffect() {
        var rtMain = Minecraft.getInstance().getMainRenderTarget();

        var chain = KOMARU_POST_EFFECT.getPostChain();
        var rtBase = chain.getTempTarget("base");

        GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, rtMain.frameBufferId);
        GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, rtBase.frameBufferId);
        GlStateManager._glBlitFrameBuffer(0, 0, rtMain.width, rtMain.height, 0, 0, rtBase.width, rtBase.height, GlConst.GL_DEPTH_BUFFER_BIT | GlConst.GL_COLOR_BUFFER_BIT, GlConst.GL_NEAREST);
        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);
    }

    public static final PostEffect KOMARU_POST_EFFECT = new PostEffect(new ResourceLocation("start_core", "komaru")) {
        public boolean isEnabled() {
            return super.isEnabled() && !Minecraft.useShaderTransparency();
        }

        @Override
        public void begin(float partialTick) {
            super.begin(partialTick);
            COLLECTED_RENDERS.clear();
        }

        @Override
        public void end(float partialTick) {
            if (COLLECTED_RENDERS.isEmpty()) return;

            if (!CUBE_MAP_TEXTURE.loaded()) {
                CUBE_MAP_TEXTURE.load(Minecraft.getInstance().getResourceManager());
            }

            var pass = chain.passes.get(0);
            var machine = COLLECTED_RENDERS.get(0);
            var effect = pass.getEffect();

            fillCommonEffectUniforms(effect);
            RenderSystem.activeTexture(GL30.GL_TEXTURE0 + 2);
            GL11.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, CUBE_MAP_TEXTURE.getId());

            effect.safeGetUniform("BeamOrigin").set(getBeamOrigin(machine));
            effect.safeGetUniform("CubeMapSampler").set(2);

            RenderSystem.depthMask(true);
            super.end(partialTick);

            RenderSystem.activeTexture(GL30.GL_TEXTURE0 + 2);
            GL11.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, 0);
        }
    };

    private static void updateKomaruFancyPostEffect() {
        var levelRenderer = Minecraft.getInstance().levelRenderer;
        var rtMain = Minecraft.getInstance().getMainRenderTarget();

        var chain = KOMARU_FANCY_POST_EFFECT.getPostChain();
        var rtBase = chain.getTempTarget("base");

        GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, rtMain.frameBufferId);
        GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, rtBase.frameBufferId);
        GlStateManager._glBlitFrameBuffer(0, 0, rtMain.width, rtMain.height, 0, 0, rtBase.width, rtBase.height, GlConst.GL_DEPTH_BUFFER_BIT | GlConst.GL_COLOR_BUFFER_BIT, GlConst.GL_NEAREST);
        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);

        var translucentTarget = levelRenderer.getTranslucentTarget();
        var itemEntityTarget = levelRenderer.getItemEntityTarget();
        var particlesTarget = levelRenderer.getParticlesTarget();
        var cloudsTarget = levelRenderer.getCloudsTarget();
        var weatherTarget = levelRenderer.getWeatherTarget();

        if (translucentTarget != null && chain.customRenderTargets.get("translucent") != translucentTarget) {
            chain.customRenderTargets.put("translucent", translucentTarget);
            for (var pass1 : chain.passes) {
                var pass = (ReplaceablePostPass) pass1;
                pass.startcore$replaceAuxAsset("TranslucentSampler", translucentTarget::getColorTextureId, translucentTarget.width, translucentTarget.height);
                pass.startcore$replaceAuxAsset("TranslucentDepthSampler", translucentTarget::getDepthTextureId, translucentTarget.width, translucentTarget.height);
            }
        }
        if (itemEntityTarget != null && chain.customRenderTargets.get("itemEntity") != itemEntityTarget) {
            chain.customRenderTargets.put("itemEntity", itemEntityTarget);
            for (var pass1 : chain.passes) {
                var pass = (ReplaceablePostPass) pass1;
                pass.startcore$replaceAuxAsset("ItemEntitySampler", itemEntityTarget::getColorTextureId, itemEntityTarget.width, itemEntityTarget.height);
                pass.startcore$replaceAuxAsset("ItemEntityDepthSampler", itemEntityTarget::getDepthTextureId, itemEntityTarget.width, itemEntityTarget.height);
            }
        }
        if (particlesTarget != null && chain.customRenderTargets.get("particles") != particlesTarget) {
            chain.customRenderTargets.put("particles", particlesTarget);
            for (var pass1 : chain.passes) {
                var pass = (ReplaceablePostPass) pass1;
                pass.startcore$replaceAuxAsset("ParticlesSampler", particlesTarget::getColorTextureId, particlesTarget.width, particlesTarget.height);
                pass.startcore$replaceAuxAsset("ParticlesDepthSampler", particlesTarget::getDepthTextureId, particlesTarget.width, particlesTarget.height);
            }
        }
        if (cloudsTarget != null && chain.customRenderTargets.get("clouds") != cloudsTarget) {
            chain.customRenderTargets.put("clouds", cloudsTarget);
            for (var pass1 : chain.passes) {
                var pass = (ReplaceablePostPass) pass1;
                pass.startcore$replaceAuxAsset("CloudsSampler", cloudsTarget::getColorTextureId, cloudsTarget.width, cloudsTarget.height);
                pass.startcore$replaceAuxAsset("CloudsDepthSampler", cloudsTarget::getDepthTextureId, cloudsTarget.width, cloudsTarget.height);
            }
        }
        if (weatherTarget != null && chain.customRenderTargets.get("weather") != weatherTarget) {
            chain.customRenderTargets.put("weather", weatherTarget);
            for (var pass1 : chain.passes) {
                var pass = (ReplaceablePostPass) pass1;
                pass.startcore$replaceAuxAsset("WeatherSampler", weatherTarget::getColorTextureId, weatherTarget.width, weatherTarget.height);
                pass.startcore$replaceAuxAsset("WeatherDepthSampler", weatherTarget::getDepthTextureId, weatherTarget.width, weatherTarget.height);
            }
        }
    }

    public static final PostEffect KOMARU_FANCY_POST_EFFECT = new PostEffect(new ResourceLocation("start_core", "komaru_fancy")) {
        public boolean isEnabled() {
            return super.isEnabled() && Minecraft.useShaderTransparency();
        }

        @Override
        public void begin(float partialTick) {
            super.begin(partialTick);
            COLLECTED_RENDERS.clear();
        }

        @Override
        public void end(float partialTick) {
            if (COLLECTED_RENDERS.isEmpty()) return;

            for (var pass : this.chain.passes) {
                var machine = COLLECTED_RENDERS.get(0);
                var effect = pass.getEffect();
                fillCommonEffectUniforms(effect);
                effect.safeGetUniform("BeamOrigin").set(getBeamOrigin(machine));
            }

            RenderSystem.depthMask(true);

            super.end(partialTick);


        }
    };

    private static Vector3f getBeamOrigin(StarTKomaruFrameMachine machine) {
        var blockPos = machine.getPos();
        var front = machine.getFrontFacing();
        var upwards = machine.getUpwardsFacing();
        var flipped = machine.isFlipped();
        var back = RelativeDirection.BACK.getRelative(front, upwards, flipped);
        var left = RelativeDirection.LEFT.getRelative(front, upwards, flipped);
        var centerX = blockPos.getX() + back.getStepX() * 31f + left.getStepX() * 0f + 0.5f;
        var centerY = blockPos.getY() + back.getStepY() * 0f + left.getStepY() * 0f + 0.5f;
        var centerZ = blockPos.getZ() + back.getStepZ() * 31f + left.getStepZ() * 0f + 0.5f;
        return new Vector3f(centerX, centerY, centerZ);
    }

    @SubscribeEvent
    public static void onRenderLevelStageEvent(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) onRenderAfterParticles(event);
    }

    private static void fillCommonEffectUniforms(EffectInstance instance) {
        var mc = Minecraft.getInstance();
        var projectionMatrix = RenderSystem.getProjectionMatrix();
        var invProjectionMatrix = new Matrix4f(projectionMatrix).invert();
        var invViewRotMatrix = new Matrix4f(RenderSystem.getInverseViewRotationMatrix()); // bug in MC, doesn't support mat3 in EffectInstance
        var camera = mc.gameRenderer.getMainCamera();
        var cameraPosition = camera.getPosition().toVector3f();

        instance.safeGetUniform("GameProjMat").set(projectionMatrix);
        instance.safeGetUniform("GameInvProjMat").set(invProjectionMatrix);
        instance.safeGetUniform("GameInvViewRotMat").set(invViewRotMatrix);
        instance.safeGetUniform("CameraNearPlane").set(0.05F);
        instance.safeGetUniform("CameraFarPlane").set(mc.gameRenderer.getDepthFar());
        instance.safeGetUniform("CameraPosition").set(cameraPosition);
        instance.safeGetUniform("GameTime").set(RenderSystem.getShaderGameTime());
    }

    private static void fillCommonShaderUniforms(ShaderInstance instance, PoseStack poseStack, Matrix4f projectionMatrix) {
        var mc = Minecraft.getInstance();
        var renderTarget = mc.getMainRenderTarget();
        var invProjectionMatrix = new Matrix4f(projectionMatrix).invert();
        var camera = mc.gameRenderer.getMainCamera();
        var cameraPosition = camera.getPosition().toVector3f();

        instance.safeGetUniform("CameraNearPlane").set(0.05F);
        instance.safeGetUniform("CameraFarPlane").set(mc.gameRenderer.getDepthFar());
        instance.safeGetUniform("CameraPosition").set(cameraPosition);
        instance.safeGetUniform("ViewResolution").set((float) renderTarget.viewWidth, (float) renderTarget.viewHeight);
        instance.safeGetUniform("InvProjMat").set(invProjectionMatrix);

        if (instance.MODEL_VIEW_MATRIX != null) {
            instance.MODEL_VIEW_MATRIX.set(poseStack.last().pose());
        }
        if (instance.PROJECTION_MATRIX != null) {
            instance.PROJECTION_MATRIX.set(projectionMatrix);
        }
        if (instance.INVERSE_VIEW_ROTATION_MATRIX != null) {
            instance.INVERSE_VIEW_ROTATION_MATRIX.set(RenderSystem.getInverseViewRotationMatrix());
        }
        if (instance.COLOR_MODULATOR != null) {
            instance.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
        }
        if (instance.GLINT_ALPHA != null) {
            instance.GLINT_ALPHA.set(RenderSystem.getShaderGlintAlpha());
        }
        if (instance.FOG_START != null) {
            instance.FOG_START.set(RenderSystem.getShaderFogStart());
        }
        if (instance.FOG_END != null) {
            instance.FOG_END.set(RenderSystem.getShaderFogEnd());
        }
        if (instance.FOG_COLOR != null) {
            instance.FOG_COLOR.set(RenderSystem.getShaderFogColor());
        }
        if (instance.FOG_SHAPE != null) {
            instance.FOG_SHAPE.set(RenderSystem.getShaderFogShape().getIndex());
        }
        if (instance.TEXTURE_MATRIX != null) {
            instance.TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix());
        }
        if (instance.GAME_TIME != null) {
            instance.GAME_TIME.set(RenderSystem.getShaderGameTime());
        }
        RenderSystem.setupShaderLights(instance);
    }
}
