package com.startechnology.start_core.machine.komaru.client;

import cofh.core.client.PostEffect;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.machine.komaru.StarTKomaruFrameMachine;
import com.startechnology.start_core.mixin.mc.renderer.PostChainAccessor;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;

public final class KomaruRendererManager {

    private static @Nullable List<StarTKomaruFrameMachine> COLLECTED_RENDERS;
    private static CubeMapTexture CUBE_MAP_TEXTURE;
    private static PostEffect KOMARU_POST_EFFECT;
    private static PostEffect KOMARU_FANCY_POST_EFFECT;

    public static void init() {
        CUBE_MAP_TEXTURE = new CubeMapTexture(StarTCore.resourceLocation("textures/rift_skybox"), false);
        COLLECTED_RENDERS = new ArrayList<>();
        KOMARU_POST_EFFECT = makeKomaruPostEffect();
        KOMARU_FANCY_POST_EFFECT = makeKomaruFancyPostEffect();
        MinecraftForge.EVENT_BUS.addListener(KomaruRendererManager::onRenderLevelStageEvent);
    }

    public static void addRenderer(StarTKomaruFrameMachine machine) {
        if (COLLECTED_RENDERS == null) return;
        COLLECTED_RENDERS.add(machine);
    }

    public static void onRenderLevelStageEvent(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            // noinspection StatementWithEmptyBody
            if (Minecraft.useShaderTransparency()) {
                // TODO: readd
                // updateKomaruFancyPostEffect();
            } else {
                updateKomaruPostEffect();
            }
        }
    }

    private static void updateKomaruPostEffect() {
        var rtMain = Minecraft.getInstance().getMainRenderTarget();

        var chain = KOMARU_POST_EFFECT.getPostChain();
        var rtBase = chain.getTempTarget("base");

        GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, rtMain.frameBufferId);
        GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, rtBase.frameBufferId);
        GlStateManager._glBlitFrameBuffer(0, 0, rtMain.width, rtMain.height, 0, 0, rtBase.width, rtBase.height,
                GlConst.GL_DEPTH_BUFFER_BIT | GlConst.GL_COLOR_BUFFER_BIT, GlConst.GL_NEAREST);
        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, rtMain.frameBufferId);
    }

    private static PostEffect makeKomaruPostEffect() {
        return new PostEffect(new ResourceLocation("start_core", "komaru")) {

            public boolean isEnabled() {
                return super.isEnabled() && !Minecraft.useShaderTransparency();
            }

            @Override
            public void begin(float partialTick) {
                super.begin(partialTick);
                assert COLLECTED_RENDERS != null;
                COLLECTED_RENDERS.clear();
            }

            @Override
            public void end(float partialTick) {
                assert COLLECTED_RENDERS != null;
                if (COLLECTED_RENDERS.isEmpty()) return;

                if (!CUBE_MAP_TEXTURE.loaded()) {
                    CUBE_MAP_TEXTURE.load(Minecraft.getInstance().getResourceManager());
                }

                var chainPasses = ((PostChainAccessor) chain).start_core$getPasses();

                var pass = chainPasses.get(0);
                var effect = pass.getEffect();
                fillCommonEffectUniforms(effect, partialTick);

                for (var machine : COLLECTED_RENDERS) {
                    var beamOrigin = getBeamOrigin(machine);

                    effect.safeGetUniform("AnimationTicks").set(machine.getRendererAnimationTicks());
                    effect.safeGetUniform("AnimationType").set(machine.getRendererAnimationType());
                    effect.safeGetUniform("BeamOrigin").set(beamOrigin);
                    RenderSystem.activeTexture(GL30.GL_TEXTURE0 + 2);
                    GL11.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, CUBE_MAP_TEXTURE.getId());
                    effect.safeGetUniform("CubeMapSampler").set(2);

                    RenderSystem.depthMask(true);

                    // super.end(partialTick);
                    for (PostPass postpass : chainPasses) {
                        postpass.process(partialTick / 20.0f);
                    }

                    RenderSystem.activeTexture(GL30.GL_TEXTURE0 + 2);
                    GL11.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, 0);
                }
            }
        };
    }

    private static void updateKomaruFancyPostEffect() {
        var levelRenderer = Minecraft.getInstance().levelRenderer;
        var rtMain = Minecraft.getInstance().getMainRenderTarget();

        var chain = KOMARU_FANCY_POST_EFFECT.getPostChain();
        var rtBase = chain.getTempTarget("base");

        GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, rtMain.frameBufferId);
        GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, rtBase.frameBufferId);
        GlStateManager._glBlitFrameBuffer(0, 0, rtMain.width, rtMain.height, 0, 0, rtBase.width, rtBase.height,
                GlConst.GL_DEPTH_BUFFER_BIT | GlConst.GL_COLOR_BUFFER_BIT, GlConst.GL_NEAREST);
        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);

        var translucentTarget = levelRenderer.getTranslucentTarget();
        var itemEntityTarget = levelRenderer.getItemEntityTarget();
        var particlesTarget = levelRenderer.getParticlesTarget();
        var cloudsTarget = levelRenderer.getCloudsTarget();
        var weatherTarget = levelRenderer.getWeatherTarget();

        var chainPasses = ((PostChainAccessor) chain).start_core$getPasses();
        var chainRenderTargets = ((PostChainAccessor) chain).start_core$getCustomRenderTargets();

        if (translucentTarget != null && chainRenderTargets.get("translucent") != translucentTarget) {
            chainRenderTargets.put("translucent", translucentTarget);
            for (var pass1 : chainPasses) {
                var pass = (ReplaceablePostPass) pass1;
                pass.start_core$replaceAuxAsset("TranslucentSampler", translucentTarget::getColorTextureId,
                        translucentTarget.width, translucentTarget.height);
                pass.start_core$replaceAuxAsset("TranslucentDepthSampler", translucentTarget::getDepthTextureId,
                        translucentTarget.width, translucentTarget.height);
            }
        }
        if (itemEntityTarget != null && chainRenderTargets.get("itemEntity") != itemEntityTarget) {
            chainRenderTargets.put("itemEntity", itemEntityTarget);
            for (var pass1 : chainPasses) {
                var pass = (ReplaceablePostPass) pass1;
                pass.start_core$replaceAuxAsset("ItemEntitySampler", itemEntityTarget::getColorTextureId,
                        itemEntityTarget.width, itemEntityTarget.height);
                pass.start_core$replaceAuxAsset("ItemEntityDepthSampler", itemEntityTarget::getDepthTextureId,
                        itemEntityTarget.width, itemEntityTarget.height);
            }
        }
        if (particlesTarget != null && chainRenderTargets.get("particles") != particlesTarget) {
            chainRenderTargets.put("particles", particlesTarget);
            for (var pass1 : chainPasses) {
                var pass = (ReplaceablePostPass) pass1;
                pass.start_core$replaceAuxAsset("ParticlesSampler", particlesTarget::getColorTextureId,
                        particlesTarget.width, particlesTarget.height);
                pass.start_core$replaceAuxAsset("ParticlesDepthSampler", particlesTarget::getDepthTextureId,
                        particlesTarget.width, particlesTarget.height);
            }
        }
        if (cloudsTarget != null && chainRenderTargets.get("clouds") != cloudsTarget) {
            chainRenderTargets.put("clouds", cloudsTarget);
            for (var pass1 : chainPasses) {
                var pass = (ReplaceablePostPass) pass1;
                pass.start_core$replaceAuxAsset("CloudsSampler", cloudsTarget::getColorTextureId, cloudsTarget.width,
                        cloudsTarget.height);
                pass.start_core$replaceAuxAsset("CloudsDepthSampler", cloudsTarget::getDepthTextureId,
                        cloudsTarget.width, cloudsTarget.height);
            }
        }
        if (weatherTarget != null && chainRenderTargets.get("weather") != weatherTarget) {
            chainRenderTargets.put("weather", weatherTarget);
            for (var pass1 : chainPasses) {
                var pass = (ReplaceablePostPass) pass1;
                pass.start_core$replaceAuxAsset("WeatherSampler", weatherTarget::getColorTextureId, weatherTarget.width,
                        weatherTarget.height);
                pass.start_core$replaceAuxAsset("WeatherDepthSampler", weatherTarget::getDepthTextureId,
                        weatherTarget.width, weatherTarget.height);
            }
        }
    }

    public static PostEffect makeKomaruFancyPostEffect() {
        return new PostEffect(new ResourceLocation("start_core", "komaru_fancy")) {

            public boolean isEnabled() {
                return false;
                // return super.isEnabled() && Minecraft.useShaderTransparency();
            }

            @Override
            public void begin(float partialTick) {
                super.begin(partialTick);
                assert COLLECTED_RENDERS != null;
                COLLECTED_RENDERS.clear();
            }

            @Override
            public void end(float partialTick) {
                assert COLLECTED_RENDERS != null;
                if (COLLECTED_RENDERS.isEmpty()) return;

                if (!CUBE_MAP_TEXTURE.loaded()) {
                    CUBE_MAP_TEXTURE.load(Minecraft.getInstance().getResourceManager());
                }

                var chainPasses = ((PostChainAccessor) chain).start_core$getPasses();
                var pass = chainPasses.get(0);
                var machine = COLLECTED_RENDERS.get(0);
                var beamOrigin = getBeamOrigin(machine);
                var effect = pass.getEffect();

                fillCommonEffectUniforms(effect, partialTick);
                effect.safeGetUniform("AnimationTicks").set(machine.getRendererAnimationTicks());
                effect.safeGetUniform("AnimationType").set(machine.getRendererAnimationType());
                effect.safeGetUniform("BeamOrigin").set(beamOrigin);
                RenderSystem.activeTexture(GL30.GL_TEXTURE0 + 12);
                GL11.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, CUBE_MAP_TEXTURE.getId());
                effect.safeGetUniform("CubeMapSampler").set(12);

                RenderSystem.depthMask(true);
                super.end(partialTick);

                RenderSystem.activeTexture(GL30.GL_TEXTURE0 + 12);
                GL11.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, 0);
            }
        };
    }

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

    private static void fillCommonEffectUniforms(EffectInstance instance, float partialTicks) {
        var mc = Minecraft.getInstance();
        var projectionMatrix = RenderSystem.getProjectionMatrix();
        var invViewRotMatrix = new Matrix4f(RenderSystem.getInverseViewRotationMatrix());
        var camera = mc.gameRenderer.getMainCamera();
        var cameraPosition = camera.getPosition().toVector3f();

        var viewRotMatrix = new Matrix4f(invViewRotMatrix).invert();
        var invProjViewRotMatrix = new Matrix4f(RenderSystem.getProjectionMatrix())
                .mul(viewRotMatrix).invert();

        instance.safeGetUniform("InvProjViewRotMat").set(invProjViewRotMatrix);
        instance.safeGetUniform("GameProjMat").set(projectionMatrix);
        instance.safeGetUniform("GameInvViewRotMat").set(invViewRotMatrix);
        instance.safeGetUniform("CameraNearPlane").set(0.05F);
        instance.safeGetUniform("CameraFarPlane").set(mc.gameRenderer.getDepthFar());
        instance.safeGetUniform("CameraPosition").set(cameraPosition);
        instance.safeGetUniform("GameTime").set(RenderSystem.getShaderGameTime());
    }
}
