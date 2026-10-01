package com.startechnology.start_core.client.megastructure;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.Setter;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class EmissiveQuads {

    private static final float BODY_OPACITY = 1.35f;
    private static final float HIGHLIGHT_GAIN = 0.6f;
    private static final float BEAM_TEXTURE_LENGTH = 3;

    private final Matrix4f pose;
    private final Matrix3f normal;
    private final MultiBufferSource buffer;
    private final Vector3f view;
    @Setter
    private boolean body;

    public EmissiveQuads(PoseStack.Pose pose, MultiBufferSource buffer, Vector3f view) {
        this.pose = pose.pose();
        this.normal = pose.normal();
        this.buffer = buffer;
        this.view = view;
    }

    public VertexConsumer consumer(ResourceLocation texture) {
        return buffer.getBuffer(body ? MegastructureRenderTypes.translucent(texture) :
                MegastructureRenderTypes.additive(texture));
    }

    public void beam(VertexConsumer consumer, Vector3f start, Vector3f end, float red, float green, float blue,
                     float intensity, float seconds) {
        float length = start.distance(end);
        float scroll = wrap(seconds * 0.5f) * BEAM_TEXTURE_LENGTH;
        if (body) {
            ribbon(consumer, start, end, 0.9f, red, green, blue, intensity, scroll * 0.6f, length);
        } else {
            ribbon(consumer, start, end, 0.3f * (0.6f + 0.4f * intensity), Mth.lerp(0.7f, red, 1),
                    Mth.lerp(0.7f, green, 1), Mth.lerp(0.7f, blue, 1), 1.6f * intensity, scroll, length);
        }
    }

    private void ribbon(VertexConsumer consumer, Vector3f start, Vector3f end, float width, float red, float green,
                        float blue, float intensity, float scroll, float length) {
        var axis = new Vector3f(end).sub(start).normalize();
        var middle = new Vector3f(start).add(end).mul(0.5f);
        var toCamera = new Vector3f(view).sub(middle).normalize();
        var side = new Vector3f(axis).cross(toCamera);
        if (side.lengthSquared() < 1e-6f) side = perpendicular(axis);
        side.normalize().mul(width / 2);

        float v0 = -scroll;
        float v1 = length / BEAM_TEXTURE_LENGTH - scroll;
        emit(consumer, new Vector3f(start).sub(side), red, green, blue, intensity, 0, v0);
        emit(consumer, new Vector3f(start).add(side), red, green, blue, intensity, 1, v0);
        emit(consumer, new Vector3f(end).add(side), red, green, blue, intensity, 1, v1);
        emit(consumer, new Vector3f(end).sub(side), red, green, blue, intensity, 0, v1);
    }

    public void billboard(VertexConsumer consumer, Vector3f center, float size, float red, float green, float blue,
                          float intensity) {
        var toCamera = new Vector3f(view).sub(center).normalize();
        var right = perpendicular(toCamera).mul(size / 2);
        var up = new Vector3f(toCamera).cross(right);
        emit(consumer, new Vector3f(center).sub(right).sub(up), red, green, blue, intensity, 0, 0);
        emit(consumer, new Vector3f(center).add(right).sub(up), red, green, blue, intensity, 1, 0);
        emit(consumer, new Vector3f(center).add(right).add(up), red, green, blue, intensity, 1, 1);
        emit(consumer, new Vector3f(center).sub(right).add(up), red, green, blue, intensity, 0, 1);
    }

    public void emit(VertexConsumer consumer, Vector3f p, float red, float green, float blue, float intensity,
                     float u, float v) {
        consumer.vertex(pose, p.x, p.y, p.z);
        if (body) {
            consumer.color(Mth.clamp(red, 0, 1), Mth.clamp(green, 0, 1), Mth.clamp(blue, 0, 1),
                    Mth.clamp(intensity * BODY_OPACITY, 0, 1));
        } else {
            float gain = intensity * HIGHLIGHT_GAIN;
            consumer.color(Mth.clamp(red * gain, 0, 1), Mth.clamp(green * gain, 0, 1),
                    Mth.clamp(blue * gain, 0, 1), 1f);
        }
        consumer.uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(normal, 0, 1, 0)
                .endVertex();
    }

    public static float wrap(float value) {
        return value - Mth.floor(value);
    }

    public static Vector3f perpendicular(Vector3f direction) {
        var reference = Math.abs(direction.y) < 0.95f ? new Vector3f(0, 1, 0) : new Vector3f(1, 0, 0);
        return new Vector3f(direction).cross(reference).normalize();
    }
}
