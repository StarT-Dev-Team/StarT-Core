package com.startechnology.start_core.machine.black_hole.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.startechnology.start_core.client.megastructure.EmissiveQuads;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;

import static com.startechnology.start_core.client.megastructure.EmissiveQuads.perpendicular;
import static com.startechnology.start_core.client.megastructure.EmissiveQuads.wrap;
import static com.startechnology.start_core.machine.black_hole.StarTBlackHoleMachines.STABILIZER_COUNT;
import static com.startechnology.start_core.machine.black_hole.StarTBlackHoleMachines.STABILIZER_DISTANCE;

final class BlackHoleFallbackRenderer {

    private static final int SPHERE_SLICES = 32;
    private static final int SPHERE_STACKS = 20;
    private static final int DISK_RINGS = 12;
    private static final int SEGMENTS = 96;
    private static final float DISK_TEXTURE_REPEAT = 3;
    private static final float FLOW_PERIOD = 5;
    private static final float RIGID_SPEED = 0.03f;

    private final Matrix4f pose;
    private final MultiBufferSource buffer;
    private final EmissiveQuads quads;
    private final BlackHoleAnimation.Frame frame;
    private final Vector3f view;
    private final Vector3f e1, e2, n;
    private final Vector3f[] axes;
    private final float seconds;

    BlackHoleFallbackRenderer(PoseStack.Pose pose, MultiBufferSource buffer, BlackHoleAnimation.Frame frame,
                              Vector3f view, Vector3f[] axes, Vector3f[] basis, float worldTime) {
        this.pose = pose.pose();
        this.buffer = buffer;
        this.quads = new EmissiveQuads(pose, buffer, view);
        this.frame = frame;
        this.view = view;
        this.axes = axes;
        this.seconds = worldTime / 20f;
        this.e1 = basis[0];
        this.e2 = basis[1];
        this.n = basis[2];
    }

    void render(boolean beams, boolean geometry) {
        if (geometry && frame.horizon > 0.01f) horizon();
        emissive(true, beams, geometry);
        emissive(false, beams, geometry);
        glows(quads.consumer(BlackHoleRenderTypes.GLOW_TEXTURE), beams, geometry);
    }

    private void emissive(boolean body, boolean beams, boolean geometry) {
        quads.setBody(body);
        if (geometry) {
            boolean hasHorizon = frame.horizon > 0.01f;
            var disk = quads.consumer(BlackHoleRenderTypes.DISK_TEXTURE);
            if (frame.diskIntensity > 0.001f && frame.diskOuter > frame.diskInner) disk(disk, body);
            if (frame.arcIntensity > 0.001f && hasHorizon) lensedArcs(disk);

            var band = quads.consumer(BlackHoleRenderTypes.BAND_TEXTURE);
            if (frame.ringIntensity > 0.001f && hasHorizon) photonRing(band);
            if (frame.shockIntensity > 0.001f) shockwave(band);
        }

        if (beams) {
            var beam = quads.consumer(BlackHoleRenderTypes.BEAM_TEXTURE);
            for (int i = 0; i < STABILIZER_COUNT; i++) beam(beam, i);
        }
    }

    private void horizon() {
        var consumer = buffer.getBuffer(BlackHoleRenderTypes.HORIZON);
        for (int stack = 0; stack < SPHERE_STACKS; stack++) {
            float theta0 = Mth.PI * stack / SPHERE_STACKS;
            float theta1 = Mth.PI * (stack + 1) / SPHERE_STACKS;
            for (int slice = 0; slice < SPHERE_SLICES; slice++) {
                float phi0 = Mth.TWO_PI * slice / SPHERE_SLICES;
                float phi1 = Mth.TWO_PI * (slice + 1) / SPHERE_SLICES;
                sphereVertex(consumer, theta0, phi0);
                sphereVertex(consumer, theta1, phi0);
                sphereVertex(consumer, theta1, phi1);
                sphereVertex(consumer, theta0, phi1);
            }
        }
    }

    private void sphereVertex(VertexConsumer consumer, float theta, float phi) {
        float wobble = 1 + frame.horizonWobble * Mth.sin(3 * phi + seconds * 6) * Mth.sin(2 * theta);
        float r = frame.horizon * wobble;
        float sinTheta = Mth.sin(theta);
        consumer.vertex(pose, r * sinTheta * Mth.cos(phi), r * Mth.cos(theta), r * sinTheta * Mth.sin(phi))
                .color(0, 0, 0, 255).endVertex();
    }

    private void disk(VertexConsumer consumer, boolean body) {
        if (body) {
            diskLayer(consumer, -1, 1, wrap(seconds * RIGID_SPEED));
            return;
        }
        float cycle = seconds / FLOW_PERIOD;
        for (int layer = 0; layer < 2; layer++) {
            float phase = wrap(cycle + layer * 0.5f);
            diskLayer(consumer, phase * FLOW_PERIOD, 1 - Math.abs(2 * phase - 1), layer * 0.37f);
        }
    }

    private void diskLayer(VertexConsumer consumer, float flowTime, float weight, float shift) {
        float ratio = frame.diskOuter / frame.diskInner;
        for (int ring = 0; ring < DISK_RINGS; ring++) {
            float r0 = frame.diskInner * (float) Math.pow(ratio, ring / (float) DISK_RINGS);
            float r1 = frame.diskInner * (float) Math.pow(ratio, (ring + 1) / (float) DISK_RINGS);
            for (int segment = 0; segment < SEGMENTS; segment++) {
                float phi0 = Mth.TWO_PI * segment / SEGMENTS;
                float phi1 = Mth.TWO_PI * (segment + 1) / SEGMENTS;
                diskVertex(consumer, r0, phi0, segment, flowTime, weight, shift);
                diskVertex(consumer, r1, phi0, segment, flowTime, weight, shift);
                diskVertex(consumer, r1, phi1, segment + 1, flowTime, weight, shift);
                diskVertex(consumer, r0, phi1, segment + 1, flowTime, weight, shift);
            }
        }
    }

    private void diskVertex(VertexConsumer consumer, float r, float phi, int segment, float flowTime, float weight,
                            float shift) {
        float cos = Mth.cos(phi);
        float sin = Mth.sin(phi);
        float lift = frame.diskWobble * Mth.sin(2 * phi + seconds * 1.5f);
        var radial = new Vector3f(e1).mul(cos).add(new Vector3f(e2).mul(sin));
        var p = new Vector3f(radial).mul(r).add(new Vector3f(n).mul(lift));

        float x = Mth.clamp((r - frame.diskInner) / (frame.diskOuter - frame.diskInner), 0, 1);
        float temperature = (float) Math.pow(frame.diskInner / r, 0.75);
        float edges = BlackHoleAnimation.smoothstep(0, 0.1f, x) * (1 - BlackHoleAnimation.smoothstep(0.5f, 1, x));

        var color = shade(p, new Vector3f(n).cross(radial), r, temperature);
        float intensity = frame.diskIntensity * (0.35f + 0.65f * temperature * temperature) * edges * color[3] *
                weight;

        float u = segment / (float) SEGMENTS * DISK_TEXTURE_REPEAT;
        if (flowTime < 0) {
            u -= shift * DISK_TEXTURE_REPEAT;
        } else {
            float omega = (float) Math.pow(frame.diskInner / r, 1.5) * 0.12f * frame.diskSpeed *
                    (0.7f + 0.6f * frame.spin);
            u += shift - flowTime * omega * DISK_TEXTURE_REPEAT;
        }
        quads.emit(consumer, p, color[0], color[1], color[2], intensity, u, x);
    }

    private float[] shade(Vector3f position, Vector3f velocity, float r, float temperature) {
        float heat = temperature * temperature;
        float red = Mth.lerp(heat, frame.red, 1.0f);
        float green = Mth.lerp(heat * 0.8f, frame.green, 0.85f);
        float blue = Mth.lerp(heat * 0.6f, frame.blue, 0.6f);

        float schwarzschild = frame.horizon > 0.01f ? frame.horizon / 2.6f : frame.diskInner / 4.4f;
        float beta = Math.min(0.6f, Mth.sqrt(schwarzschild / (2 * Math.max(r - schwarzschild, 0.01f)))) *
                (0.75f + 0.25f * frame.spin);
        var toCamera = new Vector3f(view).sub(position).normalize();
        float cosTheta = velocity.dot(toCamera);
        float gamma = 1 / Mth.sqrt(1 - beta * beta);
        float g = 1 / (gamma * (1 - beta * cosTheta));
        float beaming = Mth.clamp(g * g * g, 0.15f, 3f);
        float redshift = Mth.sqrt(Math.max(0, 1 - schwarzschild / r));

        if (g > 1) {
            float shift = Mth.clamp((g - 1) * 0.8f, 0, 0.5f);
            red = Mth.lerp(shift, red, 0.85f);
            green = Mth.lerp(shift, green, 0.9f);
            blue = Mth.lerp(shift, blue, 1f);
        } else {
            green *= 0.7f + 0.3f * g;
            blue *= 0.5f + 0.5f * g;
        }
        float hot = frame.diskHeat * 0.6f;
        red = Mth.lerp(hot, red, 0.8f);
        green = Mth.lerp(hot, green, 0.9f);
        blue = Mth.lerp(hot, blue, 1f);
        return new float[] { red, green, blue, beaming * redshift };
    }

    private void lensedArcs(VertexConsumer consumer) {
        var toCamera = new Vector3f(view).normalize();
        var up = new Vector3f(n).sub(new Vector3f(toCamera).mul(n.dot(toCamera)));
        if (up.lengthSquared() < 1e-4f) return;
        up.normalize();
        var right = new Vector3f(up).cross(toCamera).normalize();

        float edgeOn = 1 - Math.abs(n.dot(toCamera));
        float strength = frame.arcIntensity * (0.25f + 0.75f * edgeOn);
        float approach = new Vector3f(n).cross(right).dot(toCamera);

        float inner = frame.horizon * 1.08f;
        float outer = inner + (frame.diskOuter - frame.diskInner) * 0.28f;
        for (int segment = 0; segment < SEGMENTS; segment++) {
            float psi0 = Mth.TWO_PI * segment / SEGMENTS;
            float psi1 = Mth.TWO_PI * (segment + 1) / SEGMENTS;
            arcVertex(consumer, right, up, inner, psi0, 0, strength, approach);
            arcVertex(consumer, right, up, outer, psi0, 1, strength, approach);
            arcVertex(consumer, right, up, outer, psi1, 1, strength, approach);
            arcVertex(consumer, right, up, inner, psi1, 0, strength, approach);
        }
    }

    private void arcVertex(VertexConsumer consumer, Vector3f right, Vector3f up, float r, float psi, float v,
                           float strength, float approach) {
        float cos = Mth.cos(psi);
        float sin = Mth.sin(psi);
        var p = new Vector3f(right).mul(cos * r).add(new Vector3f(up).mul(sin * r));
        float image = sin >= 0 ? 1 : 0.5f;
        float merge = (float) Math.pow(Math.abs(sin), 0.5);
        float doppler = Mth.clamp(1 + 0.6f * approach * cos, 0.35f, 1.7f);
        float radial = 1 - v * v;
        float intensity = strength * image * merge * doppler * radial;
        float u = psi / Mth.TWO_PI * 2 - wrap(seconds * 0.05f);
        quads.emit(consumer, p, Mth.lerp(0.4f, frame.red, 1), Mth.lerp(0.4f, frame.green, 0.85f),
                Mth.lerp(0.4f, frame.blue, 0.6f), intensity, u, 0.2f + 0.6f * v);
    }

    private void photonRing(VertexConsumer consumer) {
        var toCamera = new Vector3f(view).normalize();
        var right = perpendicular(toCamera);
        var up = new Vector3f(toCamera).cross(right).normalize();
        float approach = new Vector3f(n).cross(right).dot(toCamera);
        float inner = frame.horizon * 0.98f;
        float outer = frame.horizon * 1.1f;
        for (int segment = 0; segment < SEGMENTS; segment++) {
            float psi0 = Mth.TWO_PI * segment / SEGMENTS;
            float psi1 = Mth.TWO_PI * (segment + 1) / SEGMENTS;
            ringVertex(consumer, right, up, inner, psi0, 0, approach);
            ringVertex(consumer, right, up, outer, psi0, 1, approach);
            ringVertex(consumer, right, up, outer, psi1, 1, approach);
            ringVertex(consumer, right, up, inner, psi1, 0, approach);
        }
    }

    private void ringVertex(VertexConsumer consumer, Vector3f right, Vector3f up, float r, float psi, float v,
                            float approach) {
        var p = new Vector3f(right).mul(Mth.cos(psi) * r).add(new Vector3f(up).mul(Mth.sin(psi) * r));
        float intensity = frame.ringIntensity * Mth.clamp(1 + 0.4f * approach * Mth.cos(psi), 0.4f, 1.6f);
        quads.emit(consumer, p, Mth.lerp(0.7f, frame.red, 1), Mth.lerp(0.7f, frame.green, 0.95f),
                Mth.lerp(0.7f, frame.blue, 0.85f), intensity, psi / Mth.TWO_PI, v);
    }

    private void shockwave(VertexConsumer consumer) {
        float inner = Math.max(frame.shockRadius - 1.2f, 0);
        float outer = frame.shockRadius + 0.4f;
        for (int segment = 0; segment < SEGMENTS; segment++) {
            float phi0 = Mth.TWO_PI * segment / SEGMENTS;
            float phi1 = Mth.TWO_PI * (segment + 1) / SEGMENTS;
            planeVertex(consumer, inner, phi0, 0);
            planeVertex(consumer, outer, phi0, 1);
            planeVertex(consumer, outer, phi1, 1);
            planeVertex(consumer, inner, phi1, 0);
        }
    }

    private void planeVertex(VertexConsumer consumer, float r, float phi, float v) {
        var p = new Vector3f(e1).mul(Mth.cos(phi) * r).add(new Vector3f(e2).mul(Mth.sin(phi) * r));
        quads.emit(consumer, p, Mth.lerp(0.7f, frame.red, 1), Mth.lerp(0.7f, frame.green, 1),
                Mth.lerp(0.7f, frame.blue, 1), frame.shockIntensity * 1.5f, phi / Mth.TWO_PI, v);
    }

    private float beamEnd() {
        return Math.max(frame.horizon * 1.02f, 0.25f);
    }

    private float flicker(int index) {
        return 1 - frame.beamFlicker[index] * BlackHoleAnimation.wave(seconds * 20, 1.3f + index * 0.9f);
    }

    private void beam(VertexConsumer consumer, int index) {
        float intensity = frame.beamIntensity[index] * flicker(index);
        if (intensity <= 0.001f || frame.beamReach[index] <= 0.001f) return;

        var start = new Vector3f(axes[index]).mul(STABILIZER_DISTANCE - 0.5f);
        var target = new Vector3f(axes[index]).mul(beamEnd());
        var end = new Vector3f(start).lerp(target, frame.beamReach[index]);
        quads.beam(consumer, start, end, frame.red, frame.green, frame.blue, intensity, seconds);
    }

    private void glows(VertexConsumer consumer, boolean beams, boolean geometry) {
        var origin = new Vector3f();
        if (geometry && frame.glowIntensity > 0.001f && frame.horizon > 0.01f) {
            quads.billboard(consumer, origin, frame.diskOuter * 1.2f, frame.red, frame.green, frame.blue,
                    frame.glowIntensity * 0.3f);
            quads.billboard(consumer, origin, frame.horizon * 2.6f, Mth.lerp(0.5f, frame.red, 1),
                    Mth.lerp(0.5f, frame.green, 0.95f), Mth.lerp(0.5f, frame.blue, 0.9f), frame.glowIntensity * 0.35f);
        }
        if (geometry && frame.flare > 0.001f) {
            quads.billboard(consumer, origin, Math.max(frame.flareSize, 0.5f), 1, Mth.lerp(0.3f, frame.green, 1),
                    Mth.lerp(0.3f, frame.blue, 1), frame.flare);
        }

        if (!beams || frame.muzzleIntensity <= 0.001f) return;
        for (int i = 0; i < STABILIZER_COUNT; i++) {
            float level = frame.muzzleIntensity * Math.max(frame.beamIntensity[i], 0.15f) * flicker(i);
            var muzzle = new Vector3f(axes[i]).mul(STABILIZER_DISTANCE - 0.6f);
            quads.billboard(consumer, muzzle, 1 + 1.2f * level, frame.red, frame.green, frame.blue, level);
            if (frame.beamReach[i] > 0.99f && frame.beamIntensity[i] > 0.01f) {
                var impact = new Vector3f(axes[i]).mul(beamEnd());
                quads.billboard(consumer, impact, 1.4f, frame.red, frame.green, frame.blue,
                        0.7f * frame.beamIntensity[i] * flicker(i));
            }
        }
    }
}
