package com.startechnology.start_core.machine.hellforge.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.client.megastructure.EmissiveQuads;
import com.startechnology.start_core.machine.black_hole.client.BlackHoleRenderTypes;
import com.startechnology.start_core.machine.hellforge.HellFlameProfile;
import org.joml.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

final class HellFlameFallbackRenderer {

    private static final ResourceLocation HELLFIRE = texture("hell_flame_hellfire");
    private static final ResourceLocation SOULFIRE = texture("hell_flame_soulfire");
    private static final ResourceLocation TOP = texture("hell_flame_top");
    private static final int COLUMNS = 8;
    private static final int ROWS = HellFlameAnimation.FLIPBOOK_FRAMES / COLUMNS;
    private static final float BODY = 1 / 1.35f;
    private static final float HIGHLIGHT = 1 / 0.6f;

    private HellFlameFallbackRenderer() {}

    private static ResourceLocation texture(String name) {
        return StarTCore.resourceLocation("textures/misc/" + name + ".png");
    }

    static void render(HellFlameClientState state, PoseStack poseStack, MultiBufferSource buffer, Vec3 origin) {
        var profile = state.profile();
        var frame = state.frame;
        var base = state.base();
        var view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().subtract(base).toVector3f();
        var axis = state.up().step();
        var toCamera = new Vector3f(view).normalize();
        float facing = HellFlameAnimation.smoothstep(0.45f, 0.9f, Math.abs(toCamera.dot(axis)));
        float top = facing * facing;
        float side = 1 - top;
        var lift = new Vector3f(axis).mul(frame.lift);

        float scale = frame.height / profile.height;
        float bottom = -0.25f * scale;
        float height = 6 * profile.bakeReach * scale;
        var across = new Vector3f(axis).cross(toCamera);
        if (across.lengthSquared() < 1e-6f) across = EmissiveQuads.perpendicular(axis);
        var right = across.normalize().mul(profile.bakeReach * frame.width);
        var up = new Vector3f(axis).mul(height / 2);
        var center = new Vector3f(axis).mul(bottom + height / 2).add(lift);
        int cell = HellFlameAnimation.flipbookFrame(state.flameTime);
        float u0 = (float) (cell % COLUMNS) / COLUMNS, v0 = (float) (cell / COLUMNS) / ROWS;
        float u1 = u0 + 1f / COLUMNS, v1 = v0 + 1f / ROWS;

        var topRight = EmissiveQuads.perpendicular(axis).mul(profile.bakeReach * frame.width);
        var topUp = new Vector3f(axis).cross(topRight);
        var topCenter = new Vector3f(axis).mul(0.35f * frame.height).add(lift);

        float red = Mth.lerp(frame.tintAmount, 1, frame.tintRed);
        float green = Mth.lerp(frame.tintAmount, 1, frame.tintGreen);
        float blue = Mth.lerp(frame.tintAmount, 1, frame.tintBlue);
        var theme = profile.theme;
        float peak = Math.max(theme.x(), Math.max(theme.y(), theme.z()));
        var texture = profile == HellFlameProfile.FIRE ? SOULFIRE : HELLFIRE;

        poseStack.pushPose();
        poseStack.translate(base.x - origin.x, base.y - origin.y, base.z - origin.z);
        var quads = new EmissiveQuads(poseStack.last(), buffer, view);
        quads.setBody(true);
        if (side > 0.001f) {
            quad(quads, quads.consumer(texture), center, right, up, u0, v0, u1, v1, 0, 0, 0, side * BODY);
        }
        if (top > 0.001f) {
            quad(quads, quads.consumer(TOP), topCenter, topRight, topUp, 0, 0, 1, 1, 0, 0, 0, top * BODY);
        }
        quads.setBody(false);
        if (side > 0.001f) {
            var consumer = quads.consumer(texture);
            for (float light = frame.intensity * side; light > 0.001f; light--) {
                quad(quads, consumer, center, right, up, u0, v0, u1, v1, red, green, blue,
                        Math.min(light, 1) * HIGHLIGHT);
            }
        }
        if (top > 0.001f) {
            var consumer = quads.consumer(TOP);
            for (float light = frame.intensity * top; light > 0.001f; light--) {
                quad(quads, consumer, topCenter, topRight, topUp, 0, 0, 1, 1, red * theme.x() / peak,
                        green * theme.y() / peak, blue * theme.z() / peak, Math.min(light, 1) * HIGHLIGHT);
            }
        }
        quads.billboard(quads.consumer(BlackHoleRenderTypes.GLOW_TEXTURE), new Vector3f(axis).mul(0.4f).add(lift),
                2.6f * profile.radius * frame.width, theme.x(), theme.y(), theme.z(),
                0.35f * frame.intensity * HIGHLIGHT);
        poseStack.popPose();
    }

    private static void quad(EmissiveQuads quads, VertexConsumer consumer, Vector3f center, Vector3f right,
                             Vector3f up, float u0, float v0, float u1, float v1, float red, float green, float blue,
                             float intensity) {
        quads.emit(consumer, new Vector3f(center).sub(right).sub(up), red, green, blue, intensity, u0, v1);
        quads.emit(consumer, new Vector3f(center).add(right).sub(up), red, green, blue, intensity, u1, v1);
        quads.emit(consumer, new Vector3f(center).add(right).add(up), red, green, blue, intensity, u1, v0);
        quads.emit(consumer, new Vector3f(center).sub(right).add(up), red, green, blue, intensity, u0, v0);
    }
}
