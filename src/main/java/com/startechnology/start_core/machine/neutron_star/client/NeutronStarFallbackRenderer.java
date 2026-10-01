package com.startechnology.start_core.machine.neutron_star.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.client.megastructure.EmissiveQuads;
import com.startechnology.start_core.machine.black_hole.client.BlackHoleRenderTypes;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import static com.startechnology.start_core.client.megastructure.EmissiveQuads.perpendicular;
import static com.startechnology.start_core.machine.neutron_star.NeutronStarGeometry.HALO_BOUND;
import static com.startechnology.start_core.machine.neutron_star.NeutronStarGeometry.SCALE;
import static com.startechnology.start_core.machine.neutron_star.client.NeutronStarField.LOOP_COUNT;

final class NeutronStarFallbackRenderer {

    private static final ResourceLocation SURFACE = texture("neutron_star_surface");
    private static final ResourceLocation HALO = texture("neutron_star_halo");
    private static final ResourceLocation JET = texture("neutron_star_jet");
    private static final ResourceLocation LOOP = texture("neutron_star_loop");

    private static final int SLICES = 32;
    private static final int STACKS = 16;
    private static final int LOOP_SEGMENTS = 32;
    private static final float JET_SPAN = 0.25f;
    private static final float LOOP_SPAN = 0.08f;
    private static final float SURFACE_GAIN = 2.6f;
    private static final float HIGHLIGHT = 1 / 0.6f;
    private static final float[] LOOP_SHAPE = { 0.17f, 0.2f, 0.6f, 0.5f };

    private final PoseStack.Pose pose;
    private final Matrix4f matrix;
    private final MultiBufferSource buffer;
    private final NeutronStarAnimation.Frame frame;
    private final NeutronStarField field;
    private final float beadPhase;
    private final Vector3f view;
    private final Vector3f theme;
    private final float radius;

    NeutronStarFallbackRenderer(PoseStack.Pose pose, MultiBufferSource buffer, NeutronStarAnimation.Frame frame,
                                NeutronStarField field, float beadPhase, Vector3f view) {
        this.pose = pose;
        this.matrix = pose.pose();
        this.buffer = buffer;
        this.frame = frame;
        this.field = field;
        this.beadPhase = beadPhase;
        this.view = view;
        this.theme = new Vector3f(frame.red, frame.green, frame.blue).div(NeutronStarAnimation.COLOR_PEAK);
        this.radius = frame.starRadius * SCALE;
    }

    private static ResourceLocation texture(String name) {
        return StarTCore.resourceLocation("textures/misc/" + name + ".png");
    }

    void render() {
        if (radius > 0.05f && frame.starWeight > 0.01f) {
            boolean solid = frame.starWeight >= 0.99f;
            star(buffer.getBuffer(RenderType.beaconBeam(SURFACE, !solid)), solid ? 1 : frame.starWeight);
        }

        var quads = new EmissiveQuads(pose, buffer, view);
        if (frame.darkCore > 0.001f) {
            quads.setBody(true);
            quads.billboard(quads.consumer(BlackHoleRenderTypes.GLOW_TEXTURE), new Vector3f(),
                    frame.darkCore * SCALE * 4, 0, 0, 0, 1);
        }
        if (frame.jetLength > 0.001f && frame.jetWeight > 0.001f) {
            for (int pass = 0; pass < 2; pass++) {
                quads.setBody(pass == 0);
                jets(quads, quads.consumer(JET));
            }
        }
        quads.setBody(false);
        if (frame.loopWeight > 0.001f) loops(quads, quads.consumer(LOOP));
        if (frame.beadWeight > 0.001f) beads(quads, quads.consumer(BlackHoleRenderTypes.GLOW_TEXTURE));
        halo(quads);
        if (frame.shellIntensity > 0.001f && frame.shellRadius > 0) {
            for (int pass = 0; pass < 2; pass++) {
                quads.setBody(pass == 0);
                shell(quads, quads.consumer(BlackHoleRenderTypes.BAND_TEXTURE));
            }
        }
    }

    private void star(VertexConsumer consumer, float alpha) {
        var local = new Matrix3f(field.star).transpose();
        var color = new Vector3f(theme).mul(SURFACE_GAIN * Math.min(frame.starWeight, 1) * (1 + frame.flash));
        for (int stack = 0; stack < STACKS; stack++) {
            float lat0 = Mth.PI * stack / STACKS - Mth.HALF_PI;
            float lat1 = Mth.PI * (stack + 1) / STACKS - Mth.HALF_PI;
            for (int slice = 0; slice < SLICES; slice++) {
                float lon0 = Mth.TWO_PI * slice / SLICES - Mth.PI;
                float lon1 = Mth.TWO_PI * (slice + 1) / SLICES - Mth.PI;
                starVertex(consumer, local, lat0, lon0, color, alpha);
                starVertex(consumer, local, lat1, lon0, color, alpha);
                starVertex(consumer, local, lat1, lon1, color, alpha);
                starVertex(consumer, local, lat0, lon1, color, alpha);
            }
        }
    }

    private void starVertex(VertexConsumer consumer, Matrix3f local, float lat, float lon, Vector3f color,
                            float alpha) {
        var n = local.transform(new Vector3f(Mth.cos(lat) * Mth.cos(lon), Mth.sin(lat), Mth.cos(lat) * Mth.sin(lon)));
        var p = new Vector3f(n).mul(radius);
        float mu = Mth.clamp(n.dot(new Vector3f(view).sub(p).normalize()), 0, 1);
        float limb = 1 + 0.35f * (1 - mu);
        consumer.vertex(matrix, p.x, p.y, p.z)
                .color(Mth.clamp(color.x * limb, 0, 1), Mth.clamp(color.y * limb, 0, 1),
                        Mth.clamp(color.z * limb, 0, 1), alpha)
                .uv(lon / Mth.TWO_PI + 0.5f, 0.5f - lat / Mth.PI)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(pose.normal(), n.x, n.y, n.z)
                .endVertex();
    }

    private void halo(EmissiveQuads quads) {
        float level = Math.min(frame.starWeight, 1) * (0.5f + 0.5f * Math.min(frame.loopWeight, 1.3f)) + frame.flash;
        if (level <= 0.001f) return;
        var tint = new Vector3f(theme).mul(1, 0.8f, 1);
        quads.billboard(quads.consumer(HALO), new Vector3f(), 2 * HALO_BOUND * SCALE, tint.x, tint.y, tint.z,
                level * HIGHLIGHT);
        if (frame.flash > 0.001f) {
            var white = new Vector3f(theme).lerp(new Vector3f(1), 0.5f);
            quads.billboard(quads.consumer(BlackHoleRenderTypes.GLOW_TEXTURE), new Vector3f(),
                    radius * 4 + 6 * frame.flash, white.x, white.y, white.z, Math.min(frame.flash, 1.5f) * HIGHLIGHT);
        }
    }

    private void jets(EmissiveQuads quads, VertexConsumer consumer) {
        var axis = field.jetAxis(new Vector3f());
        float length = frame.jetLength * SCALE;
        float full = NeutronStarAnimation.JET_LENGTH * SCALE;
        float half = JET_SPAN * SCALE * (frame.jetWidthSlope / 0.02f);
        for (int side = -1; side <= 1; side += 2) {
            var direction = new Vector3f(axis).mul(side);
            var start = new Vector3f(direction).mul(radius);
            var end = new Vector3f(direction).mul(length);
            var middle = new Vector3f(start).add(end).mul(0.5f);
            var across = new Vector3f(direction).cross(new Vector3f(view).sub(middle));
            if (across.lengthSquared() < 1e-6f) across = perpendicular(direction);
            across.normalize().mul(half);
            float v0 = 1 - radius / full;
            float v1 = 1 - length / full;
            float level = frame.jetWeight * HIGHLIGHT;
            quads.emit(consumer, new Vector3f(start).sub(across), theme.x, theme.y, theme.z, level, 0, v0);
            quads.emit(consumer, new Vector3f(start).add(across), theme.x, theme.y, theme.z, level, 1, v0);
            quads.emit(consumer, new Vector3f(end).add(across), theme.x, theme.y, theme.z, level, 1, v1);
            quads.emit(consumer, new Vector3f(end).sub(across), theme.x, theme.y, theme.z, level, 0, v1);
        }
    }

    private void loops(EmissiveQuads quads, VertexConsumer consumer) {
        float half = LOOP_SPAN * SCALE;
        float level = Math.min(frame.loopWeight, 1.3f) * HIGHLIGHT;
        for (int i = 0; i < LOOP_COUNT; i++) {
            float grow = frame.grow[i];
            if (grow <= 0.001f) continue;
            var toWorld = new Matrix3f(field.loops[i]).transpose();
            var previous = loopPoint(toWorld, grow, 0);
            for (int s = 1; s <= LOOP_SEGMENTS; s++) {
                var next = loopPoint(toWorld, grow, s * Mth.TWO_PI / LOOP_SEGMENTS);
                var middle = new Vector3f(previous).add(next).mul(0.5f);
                var across = new Vector3f(next).sub(previous).cross(new Vector3f(view).sub(middle));
                if (across.lengthSquared() < 1e-8f) across = perpendicular(new Vector3f(next).sub(previous));
                across.normalize().mul(half);
                quads.emit(consumer, new Vector3f(previous).sub(across), theme.x, theme.y, theme.z, level, 0, 0);
                quads.emit(consumer, new Vector3f(previous).add(across), theme.x, theme.y, theme.z, level, 1, 0);
                quads.emit(consumer, new Vector3f(next).add(across), theme.x, theme.y, theme.z, level, 1, 1);
                quads.emit(consumer, new Vector3f(next).sub(across), theme.x, theme.y, theme.z, level, 0, 1);
                previous = next;
            }
        }
    }

    private static Vector3f loopPoint(Matrix3f toWorld, float grow, float t) {
        var q = new Vector3f((-LOOP_SHAPE[0] + Mth.cos(t) * LOOP_SHAPE[1]) / LOOP_SHAPE[2], 0,
                Mth.sin(t) * LOOP_SHAPE[1] / LOOP_SHAPE[3]);
        return toWorld.transform(q).mul(grow * SCALE);
    }

    private void beads(EmissiveQuads quads, VertexConsumer consumer) {
        float level = Math.min(frame.beadWeight, 1.3f) * HIGHLIGHT;
        var tint = new Vector3f(theme).lerp(new Vector3f(1), 0.4f);
        for (int i = 0; i < LOOP_COUNT; i++) {
            float grow = frame.grow[i];
            if (grow <= 0.001f) continue;
            var toWorld = new Matrix3f(field.loops[i]).transpose();
            var bead = loopPoint(toWorld, grow, beadPhase + i);
            quads.billboard(consumer, bead, 1.4f, tint.x, tint.y, tint.z, level);
        }
    }

    private void shell(EmissiveQuads quads, VertexConsumer consumer) {
        var toCamera = new Vector3f(view).normalize();
        var e1 = perpendicular(toCamera);
        var e2 = new Vector3f(toCamera).cross(e1);
        float outer = frame.shellRadius * SCALE;
        float inner = outer * 0.85f;
        var tint = new Vector3f(theme).lerp(new Vector3f(1), 0.35f);
        float level = frame.shellIntensity;
        for (int segment = 0; segment < LOOP_SEGMENTS * 2; segment++) {
            float phi0 = Mth.TWO_PI * segment / (LOOP_SEGMENTS * 2);
            float phi1 = Mth.TWO_PI * (segment + 1) / (LOOP_SEGMENTS * 2);
            quads.emit(consumer, plane(e1, e2, inner, phi0), tint.x, tint.y, tint.z, level, phi0 / Mth.TWO_PI, 0);
            quads.emit(consumer, plane(e1, e2, outer, phi0), tint.x, tint.y, tint.z, level, phi0 / Mth.TWO_PI, 1);
            quads.emit(consumer, plane(e1, e2, outer, phi1), tint.x, tint.y, tint.z, level, phi1 / Mth.TWO_PI, 1);
            quads.emit(consumer, plane(e1, e2, inner, phi1), tint.x, tint.y, tint.z, level, phi1 / Mth.TWO_PI, 0);
        }
    }

    private static Vector3f plane(Vector3f e1, Vector3f e2, float r, float phi) {
        return new Vector3f(e1).mul(Mth.cos(phi) * r).add(new Vector3f(e2).mul(Mth.sin(phi) * r));
    }
}
