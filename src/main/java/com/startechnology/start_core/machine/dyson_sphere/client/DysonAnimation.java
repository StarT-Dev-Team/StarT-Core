package com.startechnology.start_core.machine.dyson_sphere.client;

import com.startechnology.start_core.machine.dyson_sphere.DysonSphereMachine;
import com.startechnology.start_core.machine.dyson_sphere.RingAssembly;
import com.startechnology.start_core.machine.dyson_sphere.StellarBalance;
import org.joml.Matrix3f;
import org.joml.Vector3f;

import net.minecraft.util.Mth;

import static com.startechnology.start_core.machine.dyson_sphere.DysonSphereGeometry.RED_GIANT_RADIUS;
import static com.startechnology.start_core.machine.dyson_sphere.DysonSphereGeometry.RING_COUNT;
import static com.startechnology.start_core.machine.dyson_sphere.DysonSphereGeometry.STAR_RADIUS;
import static com.startechnology.start_core.machine.dyson_sphere.DysonSphereGeometry.ringRadius;

public final class DysonAnimation {

    public static final class Frame {

        public float radius = STAR_RADIUS;
        public float red, green, blue;
        public float activity;
        public float giant;
        public float heat;
        public float brightness = 1;
        public float protostar;
        public float shellRadius;
        public float shellIntensity;
        public float shellNebula;
        public float ringGlow;
        public float ringHeat;
        public float ringBreak;
        public final float[] ringSweep = { 1, 1, 1, 1, 1, 1 };
        public float rays = 0.55f;
        public float haloExtent;
        public float pylonBeams;
        public float anchorBeam;
    }

    private DysonAnimation() {}

    public static Frame compute(DysonSphereMachine machine, DysonClientState state, float phaseTime,
                                float assemblyTime, float seconds) {
        var f = new Frame();
        f.red = state.red;
        f.green = state.green;
        f.blue = state.blue;
        f.activity = state.activity();
        f.ringGlow = 0.15f + 0.85f * f.activity;
        float shockHeat = 0;

        switch (machine.getPhase()) {
            case IDLE -> {
                f.radius = 0;
                f.brightness = 0;
                f.ringGlow = 0.05f;
                f.rays = 0;
            }
            case PROTOSTAR -> {
                float share = Mth.clamp(state.mass / machine.getTargetMass(), 0, 1);
                f.radius = 4 + 4 * share;
                f.protostar = 1;
                f.heat = -0.1f + 0.15f * share;
                f.brightness = 0.45f + 0.35f * share;
                f.activity = 0;
                f.ringGlow = 0.05f;
                f.rays = 0.25f;
                f.pylonBeams = 1;
                f.anchorBeam = 1;
            }
            case IGNITING -> {
                float t = phaseTime;
                float contract = smoothstep(0, 85, t);
                float flash = bell(t, 80, 115);
                float settle = easeOutBack(Mth.clamp((t - 100) / 60, 0, 1));
                float shock = smoothstep(90, 160, t);
                f.radius = t < 100 ? Mth.lerp(contract, 8, 4.5f) : Mth.lerp(settle, 4.5f, STAR_RADIUS);
                f.protostar = 1 - smoothstep(40, 100, t);
                f.heat = 0.35f * contract + 0.6f * flash;
                f.brightness = 0.8f + 3 * flash;
                f.activity = 0.2f;
                if (t > 90) {
                    f.shellRadius = 5 + 70 * shock;
                    f.shellIntensity = (1 - shock) * 1.5f;
                    shockHeat = 1 - shock;
                }
                f.rays = 0.55f + 0.4f * flash;
                f.pylonBeams = 1 - smoothstep(60, 100, t);
                f.anchorBeam = f.pylonBeams;
            }
            case MAIN_SEQUENCE -> starve(f, state, seconds);
            case RED_GIANT_TRANSITION -> {
                float t = Mth.clamp(phaseTime / StellarBalance.RG_TRANSITION_TICKS, 0, 1);
                f.radius = Mth.lerp(smoothstep(0, 1, t), STAR_RADIUS, RED_GIANT_RADIUS);
                f.giant = t;
                f.ringHeat = 0.35f * t;
                starve(f, state, seconds);
            }
            case RED_GIANT -> {
                float instability = 1 - smoothstep(0, 0.35f, machine.getDisplayCore() / 255f);
                float pulse = Mth.sin(Mth.TWO_PI * seconds / 7.5f);
                f.radius = RED_GIANT_RADIUS *
                        (1 + 0.02f * Mth.sin(Mth.TWO_PI * seconds / 40) + 0.05f * instability * pulse);
                f.giant = 1;
                f.ringHeat = 0.35f + 0.25f * instability;
                starve(f, state, seconds);
                f.brightness *= 1 - 0.12f * instability * (0.5f + 0.5f * Mth.sin(Mth.TWO_PI * seconds / 7.5f + 1.3f));
                f.activity = Math.max(f.activity, 0.6f * instability);
            }
            case NEBULA -> {
                float t = Mth.clamp(phaseTime / StellarBalance.NEBULA_TICKS, 0, 1);
                f.radius = Mth.lerp(smoothstep(0, 0.6f, t), RED_GIANT_RADIUS, 1.2f);
                f.giant = 1 - t;
                f.heat = 0.4f * t;
                f.brightness = 1 - 0.8f * smoothstep(0.5f, 1, t);
                f.shellRadius = 14 + 60 * t;
                f.shellIntensity = 0.9f * (1 - smoothstep(0.6f, 1, t));
                f.shellNebula = 1;
                f.activity = 0.1f;
                f.ringGlow = 0.1f;
                f.rays = 0.3f;
            }
            case SUPERNOVA -> {
                float t = Mth.clamp(phaseTime / StellarBalance.SUPERNOVA_TICKS, 0, 1);
                float implode = smoothstep(0, 0.1f, t);
                float flash = bell(t, 0.08f, 0.25f);
                float shock = smoothstep(0.1f, 1, t);
                f.radius = Mth.lerp(implode, RED_GIANT_RADIUS, 1.2f);
                f.giant = 1 - implode;
                f.heat = 0.8f * flash + 0.3f * implode;
                f.brightness = (1 + 6 * flash) * (1 - 0.9f * smoothstep(0.4f, 1, t));
                if (t > 0.1f) {
                    f.shellRadius = 3 + 90 * shock;
                    f.shellIntensity = (1 - shock) * 2;
                    shockHeat = 1 - shock;
                }
                f.activity = 0;
                f.ringGlow = 0;
                f.rays = 0.55f + 0.5f * flash;
            }
            case FIZZLE -> {
                float t = Mth.clamp(phaseTime / StellarBalance.FIZZLE_TICKS, 0, 1);
                f.radius = STAR_RADIUS - 4 * t;
                f.heat = -0.45f * t;
                f.brightness = 1 - smoothstep(0.3f, 1, t);
                f.activity = 0;
                f.ringGlow = 0.1f * (1 - t);
                f.rays = 0.55f * (1 - t);
            }
            case DISPERSING -> {
                float t = Mth.clamp(phaseTime / StellarBalance.DISPERSING_TICKS, 0, 1);
                f.radius = STAR_RADIUS * (1 + 0.8f * t);
                f.brightness = 1 - smoothstep(0, 1, t);
                f.heat = -0.2f * t;
                f.activity = 0;
                f.rays = 0.55f * (1 - t);
            }
        }
        if (shockHeat > 0) {
            f.ringHeat = Math.max(f.ringHeat, shockHeat * (f.shellRadius > ringRadius(0) ? 1 : 0.3f));
        }

        var assembly = machine.getAssembly();
        if (assembly == RingAssembly.ASSEMBLING) {
            for (int i = 0; i < RING_COUNT; i++) {
                f.ringSweep[i] = Mth.clamp((assemblyTime - i * 15) / 110, 0, 1);
            }
            f.anchorBeam = Math.max(f.anchorBeam, 1 - smoothstep(150, 200, assemblyTime));
        } else if (assembly == RingAssembly.DISASSEMBLING) {
            f.ringBreak = assemblyTime / StellarBalance.DISASSEMBLY_TICKS;
            f.ringGlow = 0;
        } else if (assembly == RingAssembly.NONE) {
            for (int i = 0; i < RING_COUNT; i++) f.ringSweep[i] = 0;
        }
        f.haloExtent = Math.max(Math.max(f.radius * 9, f.shellRadius * 1.3f), 1);
        return f;
    }

    private static void starve(Frame f, DysonClientState state, float seconds) {
        float deficit = 1 - state.supply;
        float flicker = deficit * 0.25f * (0.5f + 0.5f * Mth.sin(seconds * 9.1f) * Mth.sin(seconds * 3.7f));
        f.brightness = 1 - 0.35f * deficit - flicker;
        if (deficit > 0.9f) f.brightness *= 0.85f + 0.15f * Mth.sin(seconds * Mth.TWO_PI / 3);
        f.ringGlow *= 1 - 0.6f * deficit * (0.5f + 0.5f * Mth.sin(seconds * 5.3f) * Mth.sin(seconds * 2.1f));
    }

    public static Matrix3f[] ringFrames(float seed, float[] precession, float[] spin) {
        var frames = new Matrix3f[RING_COUNT];
        float golden = Mth.PI * (3 - Mth.sqrt(5));
        for (int i = 0; i < RING_COUNT; i++) {
            float y = 1 - (i + 0.5f) / RING_COUNT * 2;
            float r = Mth.sqrt(1 - y * y);
            var axis = new Vector3f(Mth.cos(golden * i + seed) * r, y, Mth.sin(golden * i + seed) * r);
            float tilt = 0.9f + 0.25f * ((i * 0.618f) % 1);
            var base = new Vector3f(axis).mul(Mth.cos(tilt)).add(perpendicular(axis).mul(Mth.sin(tilt)));
            var normal = rotate(base, axis, precession[i]).normalize();
            var tangent = rotate(perpendicular(normal), normal, spin[i]);
            var bitangent = new Vector3f(normal).cross(tangent);
            frames[i] = new Matrix3f(tangent, bitangent, normal);
        }
        return frames;
    }

    private static Vector3f rotate(Vector3f v, Vector3f axis, float angle) {
        var a = new Vector3f(axis).normalize();
        float cos = Mth.cos(angle);
        float sin = Mth.sin(angle);
        return new Vector3f(v).mul(cos).add(new Vector3f(a).cross(v).mul(sin))
                .add(new Vector3f(a).mul(a.dot(v) * (1 - cos)));
    }

    private static Vector3f perpendicular(Vector3f v) {
        var reference = Math.abs(v.y) < 0.95f ? new Vector3f(0, 1, 0) : new Vector3f(1, 0, 0);
        return new Vector3f(v).cross(reference).normalize();
    }

    public static float smoothstep(float edge0, float edge1, float x) {
        float t = Mth.clamp((x - edge0) / (edge1 - edge0), 0, 1);
        return t * t * (3 - 2 * t);
    }

    private static float bell(float x, float start, float end) {
        if (x <= start || x >= end) return 0;
        return Mth.sin(Mth.PI * (x - start) / (end - start));
    }

    private static float easeOutBack(float t) {
        float c1 = 1.70158f;
        float s = t - 1;
        return 1 + (c1 + 1) * s * s * s + c1 * s * s;
    }
}
