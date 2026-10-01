package com.startechnology.start_core.machine.dyson_sphere.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.client.megastructure.EmissiveQuads;
import com.startechnology.start_core.client.megastructure.MegastructureRenderTypes;
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
import static com.startechnology.start_core.machine.dyson_sphere.DysonSphereGeometry.RING_BASE_RADIUS;
import static com.startechnology.start_core.machine.dyson_sphere.DysonSphereGeometry.RING_COUNT;
import static com.startechnology.start_core.machine.dyson_sphere.DysonSphereGeometry.RING_THICKNESS;
import static com.startechnology.start_core.machine.dyson_sphere.DysonSphereGeometry.RING_WIDTH;
import static com.startechnology.start_core.machine.dyson_sphere.DysonSphereGeometry.ringRadius;

final class DysonFallbackRenderer {

    private static final ResourceLocation GRANULATION = texture("dyson_granulation");
    private static final ResourceLocation GRANULATION_GIANT = texture("dyson_granulation_giant");
    private static final ResourceLocation SPOTS = texture("dyson_spots");
    private static final ResourceLocation PANEL = texture("dyson_ring_panel");
    private static final ResourceLocation STRIP = texture("dyson_ring_strip");
    private static final ResourceLocation CORONA = texture("dyson_corona");

    private static final int SLICES = 48;
    private static final int STACKS = 24;
    private static final int RING_SEGMENTS = 128;
    private static final int BREAK_SEGMENTS = 8;
    private static final int INNER_ROWS = 3;
    private static final float PANELS_PER_TEXTURE = 8;
    private static final float TURNS_PER_SECOND = 12f / 3600f;
    private static final float CORONA_REACH = 3;
    private static final float STRIP_LIFT = 0.03f;

    private final PoseStack.Pose pose;
    private final Matrix4f matrix;
    private final MultiBufferSource buffer;
    private final DysonAnimation.Frame frame;
    private final Matrix3f[] rings;
    private final Vector3f[] ringNormals = new Vector3f[RING_COUNT];
    private final Vector3f view;
    private final Vector3f theme;
    private final float seconds;
    private final float seed;
    private final float ambient;
    private final boolean lit;

    DysonFallbackRenderer(PoseStack.Pose pose, MultiBufferSource buffer, DysonAnimation.Frame frame, Matrix3f[] rings,
                          Vector3f view, float seconds, float seed, float ambient) {
        this.pose = pose;
        this.matrix = pose.pose();
        this.buffer = buffer;
        this.frame = frame;
        this.rings = rings;
        this.view = view;
        this.seconds = seconds;
        this.seed = seed;
        this.ambient = ambient;
        this.lit = frame.radius > 0.01f && frame.brightness > 0.001f;
        for (int i = 0; i < RING_COUNT; i++) ringNormals[i] = rings[i].getColumn(2, new Vector3f());
        float giant = 0.6f * frame.giant;
        theme = new Vector3f(frame.red, frame.green * Mth.lerp(giant, 1, 0.55f), frame.blue * Mth.lerp(giant, 1, 0.3f));
        theme.div(Math.max(Math.max(theme.x, theme.y), Math.max(theme.z, 1e-4f)));
    }

    void render() {
        if (lit) {
            star(buffer.getBuffer(RenderType.beaconBeam(starTexture(), false)), false);
            star(buffer.getBuffer(RenderType.beaconBeam(SPOTS, true)), true);
        }
        rings(buffer.getBuffer(RenderType.entityCutoutNoCull(PANEL)), Layer.SURFACE);
        if (frame.ringGlow > 0.001f) rings(buffer.getBuffer(MegastructureRenderTypes.additive(STRIP)), Layer.STRIP);
        if (frame.ringHeat > 0.001f) rings(buffer.getBuffer(MegastructureRenderTypes.additive(PANEL)), Layer.HEAT);

        var quads = new EmissiveQuads(pose, buffer, view);
        if (frame.shellIntensity > 0.001f && frame.shellRadius > 0) {
            for (int pass = 0; pass < 2; pass++) {
                quads.setBody(pass == 0);
                shell(quads, quads.consumer(BlackHoleRenderTypes.BAND_TEXTURE));
            }
        }
        quads.setBody(false);
        if (lit) corona(quads);
        welds(quads);
    }

    private ResourceLocation starTexture() {
        return frame.giant > 0.5f || frame.protostar > 0.5f ? GRANULATION_GIANT : GRANULATION;
    }

    private void star(VertexConsumer consumer, boolean spots) {
        float turn = seed / Mth.TWO_PI - TURNS_PER_SECOND * seconds;
        var color = new Vector3f();
        for (int stack = 0; stack < STACKS; stack++) {
            float lat0 = Mth.PI * stack / STACKS - Mth.HALF_PI;
            float lat1 = Mth.PI * (stack + 1) / STACKS - Mth.HALF_PI;
            for (int slice = 0; slice < SLICES; slice++) {
                float lon0 = Mth.TWO_PI * slice / SLICES;
                float lon1 = Mth.TWO_PI * (slice + 1) / SLICES;
                starVertex(consumer, lat0, lon0, turn, spots, color);
                starVertex(consumer, lat1, lon0, turn, spots, color);
                starVertex(consumer, lat1, lon1, turn, spots, color);
                starVertex(consumer, lat0, lon1, turn, spots, color);
            }
        }
    }

    private void starVertex(VertexConsumer consumer, float lat, float lon, float turn, boolean spots,
                            Vector3f color) {
        var n = new Vector3f(Mth.cos(lat) * Mth.cos(lon), Mth.sin(lat), Mth.cos(lat) * Mth.sin(lon));
        var p = new Vector3f(n).mul(frame.radius);
        float mu = Mth.clamp(n.dot(new Vector3f(view).sub(p).normalize()), 0, 1);
        float a = Mth.lerp(frame.giant, 0.47f, 0.8f);
        float b = Mth.lerp(frame.giant, 0.23f, 0.12f);
        float limb = Math.max(1 - a * (1 - mu) - b * (1 - mu) * (1 - mu), 0);
        float alpha = 1;
        if (spots) {
            ramp(0.3f + frame.heat, color).mul(limb * 0.6f * frame.brightness);
            alpha = (0.35f + 0.65f * frame.activity) * (1 - frame.protostar) * (1 - 0.5f * frame.giant);
        } else {
            ramp(0.74f - 0.38f * (1 - mu) + frame.heat, color).mul(limb * 1.25f);
            if (frame.protostar > 0) {
                var dust = ramp(0.26f + frame.heat, new Vector3f()).mul(0.75f * Mth.lerp(mu, 0.45f, 1));
                color.lerp(dust, frame.protostar);
            }
            color.mul(frame.brightness);
        }
        float u = lon / Mth.TWO_PI + turn;
        float v = 0.5f - lat / Mth.PI;
        consumer.vertex(matrix, p.x, p.y, p.z)
                .color(Mth.clamp(color.x, 0, 1), Mth.clamp(color.y, 0, 1), Mth.clamp(color.z, 0, 1), alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(pose.normal(), n.x, n.y, n.z)
                .endVertex();
    }

    private Vector3f ramp(float t, Vector3f out) {
        t = Mth.clamp(t, 0, 1);
        if (t < 0.5f) {
            return out.set(theme).mul(theme).lerp(theme, t * 2);
        }
        var hot = new Vector3f(theme).lerp(new Vector3f(1), 0.85f);
        return out.set(theme).lerp(hot, t * 2 - 1);
    }

    private enum Layer {
        SURFACE,
        STRIP,
        HEAT
    }

    private void rings(VertexConsumer consumer, Layer layer) {
        for (int i = 0; i < RING_COUNT; i++) {
            float sweep = frame.ringSweep[i];
            if (sweep <= 0) continue;
            float radius = ringRadius(i);
            float repeats = Math.max(1, Math.round(Mth.floor(radius * 2) / PANELS_PER_TEXTURE));
            for (int s = 0; s < RING_SEGMENTS; s++) {
                float t0 = s / (float) RING_SEGMENTS;
                if (t0 >= sweep) break;
                float t1 = Math.min((s + 1) / (float) RING_SEGMENTS, sweep);
                var segment = new RingSegment(i, s * BREAK_SEGMENTS / RING_SEGMENTS, radius, t0, t1, repeats);
                if (layer == Layer.SURFACE) {
                    segment.surface(consumer);
                } else {
                    segment.overlay(consumer, layer);
                }
            }
        }
    }

    private final class RingSegment {

        private final int ring;
        private final int breakSegment;
        private final float radius;
        private final float t0, t1;
        private final float c0, s0, c1, s1;
        private final float repeats;

        RingSegment(int ring, int breakSegment, float radius, float t0, float t1, float repeats) {
            this.ring = ring;
            this.breakSegment = breakSegment;
            this.radius = radius;
            this.t0 = t0;
            this.t1 = t1;
            this.repeats = repeats;
            c0 = Mth.cos(t0 * Mth.TWO_PI);
            s0 = Mth.sin(t0 * Mth.TWO_PI);
            c1 = Mth.cos(t1 * Mth.TWO_PI);
            s1 = Mth.sin(t1 * Mth.TWO_PI);
        }

        void surface(VertexConsumer consumer) {
            float half = RING_WIDTH / 2;
            float outer = radius + RING_THICKNESS;
            for (int row = 0; row < INNER_ROWS; row++) {
                float v0 = row / (float) INNER_ROWS;
                float v1 = (row + 1) / (float) INNER_ROWS;
                quad(consumer, radius, radius, Mth.lerp(v0, -half, half), Mth.lerp(v1, -half, half), v0, v1, -1, 0);
            }
            quad(consumer, outer, outer, -half, half, 0, 1, 1, 0);
            quad(consumer, radius, outer, half, half, 0.97f, 0.97f, 0, 1);
            quad(consumer, radius, outer, -half, -half, 0.03f, 0.03f, 0, -1);
        }

        void overlay(VertexConsumer consumer, Layer layer) {
            float half = RING_WIDTH / 2;
            float r = radius - STRIP_LIFT;
            quad(consumer, r, r, -half, half, 0, 1, -1, 0, layer);
        }

        private void quad(VertexConsumer consumer, float ra, float rb, float ha, float hb, float va, float vb,
                          float radial, float axial) {
            quad(consumer, ra, rb, ha, hb, va, vb, radial, axial, Layer.SURFACE);
        }

        private void quad(VertexConsumer consumer, float ra, float rb, float ha, float hb, float va, float vb,
                          float radial, float axial, Layer layer) {
            vertex(consumer, ra, ha, false, va, radial, axial, layer);
            vertex(consumer, rb, hb, false, vb, radial, axial, layer);
            vertex(consumer, rb, hb, true, vb, radial, axial, layer);
            vertex(consumer, ra, ha, true, va, radial, axial, layer);
        }

        private void vertex(VertexConsumer consumer, float r, float h, boolean end, float v, float radial,
                            float axial, Layer layer) {
            float c = end ? c1 : c0;
            float s = end ? s1 : s0;
            var local = new Vector3f(c * r, s * r, h);
            var normal = new Vector3f(c * radial, s * radial, axial);
            if (frame.ringBreak > 0) breakApart(local, normal);
            var p = rings[ring].transform(local);
            var n = rings[ring].transform(normal);
            float u = (end ? t1 : t0) * repeats;

            if (layer == Layer.SURFACE) {
                var color = ringLight(p, n, ring);
                consumer.vertex(matrix, p.x, p.y, p.z)
                        .color(Mth.clamp(color.x, 0, 1), Mth.clamp(color.y, 0, 1), Mth.clamp(color.z, 0, 1), 1)
                        .uv(u, v)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(LightTexture.FULL_BRIGHT)
                        .normal(pose.normal(), 0, 1, 0)
                        .endVertex();
                return;
            }
            var color = new Vector3f();
            if (layer == Layer.STRIP) {
                float flicker = 0.85f + 0.15f * Mth.sin(seconds * 2.3f + ring * 1.7f + u * 3.1f);
                color.set(theme).mul(frame.ringGlow * flicker * 1.6f);
            } else {
                float proximity = (float) Math.exp(-Math.max(p.length() - frame.radius, 0) / 6);
                color.set(1, 0.38f, 0.12f).mul(frame.ringHeat * proximity * 1.4f);
            }
            consumer.vertex(matrix, p.x, p.y, p.z)
                    .color(Mth.clamp(color.x, 0, 1), Mth.clamp(color.y, 0, 1), Mth.clamp(color.z, 0, 1), 1)
                    .uv(u, v)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(pose.normal(), 0, 1, 0)
                    .endVertex();
        }

        private void breakApart(Vector3f local, Vector3f normal) {
            float mid = (breakSegment + 0.5f) / BREAK_SEGMENTS * Mth.TWO_PI;
            var outward = new Vector3f(Mth.cos(mid), Mth.sin(mid), 0);
            var pivot = new Vector3f(outward).mul(Mth.sqrt(local.x * local.x + local.y * local.y));
            var axis = new Vector3f(-Mth.sin(mid), Mth.cos(mid), 0);
            float angle = frame.ringBreak * (1.2f + 0.35f * breakSegment) * (breakSegment % 2 == 0 ? 1 : -1);
            local.sub(pivot).rotateAxis(angle, axis.x, axis.y, axis.z).add(pivot)
                    .add(outward.mul(frame.ringBreak * 24));
            normal.rotateAxis(angle, axis.x, axis.y, axis.z);
        }
    }

    private Vector3f ringLight(Vector3f p, Vector3f n, int ring) {
        var color = new Vector3f(theme).lerp(new Vector3f(1), 0.5f).mul(0.05f * frame.brightness);
        color.add(ambient * (0.22f + 0.14f * n.y), ambient * (0.22f + 0.14f * n.y), ambient * (0.22f + 0.14f * n.y));
        if (!lit) return color;
        float distance = p.length();
        float sinA = Mth.clamp(frame.radius / distance, 0, 1);
        float nl = -n.dot(p) / distance;
        float diffuse = Mth.clamp((nl + sinA) / (1 + sinA), 0, 1);
        diffuse *= diffuse;
        if (diffuse <= 0) return color;
        var star = new Vector3f(theme).lerp(new Vector3f(1), 0.45f)
                .mul(frame.brightness * (1.2f + 8 * sinA * sinA) * 0.3f * diffuse * shadow(p, ring));
        return color.add(star);
    }

    private float shadow(Vector3f p, int self) {
        float lit = 1;
        float half = RING_WIDTH / 2;
        for (int i = 0; i < self; i++) {
            var n = ringNormals[i];
            float axial = p.dot(n);
            float radial = new Vector3f(n).mul(-axial).add(p).length();
            float r = ringRadius(i);
            if (radial <= r) continue;
            float along = 1 - r / radial;
            float height = axial * (1 - along);
            float projected = frame.radius * along;
            float edge = height / Math.max(projected + half, 1e-3f);
            float coverage = half / Math.max(projected, 1e-3f) * 1.2732395f *
                    Mth.sqrt(Math.max(1 - edge * edge, 0));
            lit *= 1 - Mth.clamp(coverage, 0, 0.95f);
        }
        return lit;
    }

    private void corona(EmissiveQuads quads) {
        float reach = Math.min(CORONA_REACH * frame.radius, 0.95f * RING_BASE_RADIUS);
        float level = frame.brightness * (1 - 0.8f * frame.protostar);
        var tint = new Vector3f(theme).lerp(new Vector3f(1), 0.55f);
        quads.billboard(quads.consumer(CORONA), new Vector3f(), reach * 2, tint.x, tint.y, tint.z, level / 0.6f);
    }

    private void shell(EmissiveQuads quads, VertexConsumer consumer) {
        var toCamera = new Vector3f(view).normalize();
        var e1 = perpendicular(toCamera);
        var e2 = new Vector3f(toCamera).cross(e1);
        float width = Mth.lerp(frame.shellNebula, 1.6f, frame.shellRadius * 0.3f);
        float inner = Math.max(frame.shellRadius - width, 0);
        float outer = frame.shellRadius + 0.4f;
        var tint = new Vector3f(theme).lerp(new Vector3f(1), 0.75f).lerp(new Vector3f(0.55f, 0.75f, 0.8f),
                frame.shellNebula);
        float level = frame.shellIntensity * 1.2f;
        for (int segment = 0; segment < RING_SEGMENTS; segment++) {
            float phi0 = Mth.TWO_PI * segment / RING_SEGMENTS;
            float phi1 = Mth.TWO_PI * (segment + 1) / RING_SEGMENTS;
            quads.emit(consumer, plane(e1, e2, inner, phi0), tint.x, tint.y, tint.z, level, phi0 / Mth.TWO_PI, 0);
            quads.emit(consumer, plane(e1, e2, outer, phi0), tint.x, tint.y, tint.z, level, phi0 / Mth.TWO_PI, 1);
            quads.emit(consumer, plane(e1, e2, outer, phi1), tint.x, tint.y, tint.z, level, phi1 / Mth.TWO_PI, 1);
            quads.emit(consumer, plane(e1, e2, inner, phi1), tint.x, tint.y, tint.z, level, phi1 / Mth.TWO_PI, 0);
        }
    }

    private static Vector3f plane(Vector3f e1, Vector3f e2, float r, float phi) {
        return new Vector3f(e1).mul(Mth.cos(phi) * r).add(new Vector3f(e2).mul(Mth.sin(phi) * r));
    }

    private void welds(EmissiveQuads quads) {
        var consumer = quads.consumer(BlackHoleRenderTypes.GLOW_TEXTURE);
        var tint = new Vector3f(theme).lerp(new Vector3f(1), 0.8f);
        for (int i = 0; i < RING_COUNT; i++) {
            float sweep = frame.ringSweep[i];
            if (sweep <= 0 || sweep >= 1) continue;
            float angle = sweep * Mth.TWO_PI;
            float radius = ringRadius(i) + RING_THICKNESS / 2;
            var tip = rings[i].transform(new Vector3f(Mth.cos(angle) * radius, Mth.sin(angle) * radius, 0));
            quads.billboard(consumer, tip, RING_WIDTH * 1.4f, tint.x, tint.y, tint.z, 1.6f);
        }
    }

    private static ResourceLocation texture(String name) {
        return StarTCore.resourceLocation("textures/misc/" + name + ".png");
    }
}
