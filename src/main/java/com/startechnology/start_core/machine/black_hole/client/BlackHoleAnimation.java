package com.startechnology.start_core.machine.black_hole.client;

import com.startechnology.start_core.machine.black_hole.BlackHoleBalance;
import com.startechnology.start_core.machine.black_hole.BlackHoleGeneratorMachine;
import com.startechnology.start_core.machine.black_hole.BlackHoleSeeds;

import org.joml.Vector3f;

import net.minecraft.util.Mth;

import static com.startechnology.start_core.machine.black_hole.StarTBlackHoleMachines.STABILIZER_COUNT;
import static com.startechnology.start_core.machine.black_hole.StarTBlackHoleMachines.STABILIZER_DISTANCE;

public final class BlackHoleAnimation {

    private static final int DEFAULT_THEME = 0xFFB46A;
    private static final float SETTLED_DISK = 0.8f;
    private static final float SETTLED_GLOW = 0.4f;

    public static final class Frame {

        public float red, green, blue;
        public float spin;

        public float horizon;
        public float horizonWobble;

        public float diskIntensity;
        public float diskInner, diskOuter;
        public float diskSpeed = 1;
        public float diskHeat;
        public float diskTilt;
        public float diskWobble;

        public float arcIntensity;
        public float ringIntensity;
        public float glowIntensity;
        public float flare;
        public float flareSize;
        public float shockRadius;
        public float shockIntensity;

        public final float[] beamIntensity = new float[STABILIZER_COUNT];
        public final float[] beamReach = new float[STABILIZER_COUNT];
        public final float[] beamFlicker = new float[STABILIZER_COUNT];
        public float muzzleIntensity;
    }

    private BlackHoleAnimation() {}

    public static Frame compute(BlackHoleGeneratorMachine machine, BlackHoleClientState state, float time,
                                float worldTime) {
        var frame = new Frame();
        var profile = BlackHoleSeeds.get(machine.getSeedProfileId());
        int theme = profile != null ? profile.colorTheme() : DEFAULT_THEME;
        frame.red = (float) Math.pow(((theme >> 16) & 0xFF) / 255f, 1.5);
        frame.green = (float) Math.pow(((theme >> 8) & 0xFF) / 255f, 1.5);
        frame.blue = (float) Math.pow((theme & 0xFF) / 255f, 1.5);
        frame.spin = machine.getSpin();

        float horizon = (float) BlackHoleBalance.horizonRadius(state.mass);
        float inner = (float) BlackHoleBalance.diskInnerRadius(horizon, frame.spin);
        float outer = (float) BlackHoleBalance.diskOuterRadius(horizon);
        frame.diskInner = inner;
        frame.diskOuter = outer;

        switch (machine.getPhase()) {
            case IDLE -> frame.muzzleIntensity = 0.15f;
            case IGNITING -> igniting(frame, time, horizon, inner, outer);
            case STABLE -> stable(frame, machine, state, worldTime, horizon);
            case EVAPORATING -> evaporating(frame, time, horizon, inner, outer);
            case COLLAPSING -> collapsing(frame, time, worldTime, horizon, inner, outer);
        }
        return frame;
    }

    private static void igniting(Frame frame, float time, float horizon, float inner, float outer) {
        for (int i = 0; i < STABILIZER_COUNT; i++) {
            float start = (i / 2f) * 30f;
            float ramp = smoothstep(start, start + 30, time);
            frame.beamIntensity[i] = ramp;
            frame.beamReach[i] = ramp;
            frame.beamFlicker[i] = 0.3f * (1 - ramp);
        }
        frame.muzzleIntensity = 0.3f;

        float flare = bell(time, 85, 135);
        frame.flare = 1.6f * flare;
        frame.flareSize = horizon * (0.6f + 2.4f * flare);
        frame.horizon = horizon * easeOutBack(Mth.clamp((time - 100) / 60f, 0, 1));

        float disk = smoothstep(140, 200, time);
        frame.diskIntensity = SETTLED_DISK * disk;
        frame.diskInner = Mth.lerp(disk, outer * 0.95f, inner);
        frame.arcIntensity = frame.diskIntensity;
        frame.ringIntensity = smoothstep(120, 170, time);
        frame.glowIntensity = SETTLED_GLOW * frame.ringIntensity;
    }

    private static void stable(Frame frame, BlackHoleGeneratorMachine machine, BlackHoleClientState state,
                               float worldTime, float horizon) {
        float feed = machine.getDisplayFeed() / 255f;
        float instability = Mth.clamp((float) (BlackHoleBalance.WARNING_STABILITY - state.stability) /
                (float) BlackHoleBalance.WARNING_STABILITY, 0, 1);
        float flicker = 1 - 0.35f * instability * wave(worldTime, 3.1f);

        frame.horizon = horizon;
        frame.horizonWobble = 0.08f * instability;
        frame.diskIntensity = (SETTLED_DISK + 0.4f * feed) * flicker;
        frame.diskSpeed = 1 + 1.5f * feed;
        frame.diskHeat = instability;
        frame.diskTilt = 0.25f * instability * Mth.sin(worldTime * 0.07f);
        frame.diskWobble = 0.15f * horizon * instability;
        frame.arcIntensity = frame.diskIntensity;
        frame.ringIntensity = flicker;
        frame.glowIntensity = SETTLED_GLOW + 0.2f * feed;
        for (int i = 0; i < STABILIZER_COUNT; i++) {
            frame.beamIntensity[i] = state.beams[i];
            frame.beamReach[i] = 1;
            frame.beamFlicker[i] = Mth.clamp(1 - state.beams[i] + 0.5f * instability, 0, 1);
        }
        frame.muzzleIntensity = 1;
    }

    private static void evaporating(Frame frame, float time, float horizon, float inner, float outer) {
        float t = Mth.clamp(time / BlackHoleBalance.EVAPORATION_TICKS, 0, 1);
        float beams = 1 - smoothstep(0, 0.5f, t);
        for (int i = 0; i < STABILIZER_COUNT; i++) {
            frame.beamIntensity[i] = beams;
            frame.beamReach[i] = 1;
            frame.beamFlicker[i] = 0.4f * (1 - beams);
        }
        frame.muzzleIntensity = beams;

        frame.horizon = horizon * (float) Math.pow(1 - t, 1.5);
        frame.diskIntensity = SETTLED_DISK * (1 - t) * (1 - t);
        frame.diskOuter = Mth.lerp(t, outer, inner * 1.1f);
        frame.arcIntensity = frame.diskIntensity;
        frame.ringIntensity = 1 - t;
        frame.glowIntensity = SETTLED_GLOW * (1 - t);

        float flash = bell(t, 0.8f, 1.0f);
        frame.flare = 0.7f * flash;
        frame.flareSize = horizon * 3;
    }

    private static void collapsing(Frame frame, float time, float worldTime, float horizon, float inner,
                                   float outer) {
        float t = Mth.clamp(time / BlackHoleBalance.COLLAPSE_TICKS, 0, 1);
        float beams = 1 - smoothstep(0.1f, 0.55f, t);
        for (int i = 0; i < STABILIZER_COUNT; i++) {
            frame.beamIntensity[i] = beams * (hash((int) (worldTime * 0.5f) * 7 + i) > 0.35f ? 1 : 0.15f);
            frame.beamReach[i] = 1;
            frame.beamFlicker[i] = 0.8f;
        }
        frame.muzzleIntensity = beams;

        float infall = smoothstep(0, 0.7f, t);
        float fade = 1 - smoothstep(0.8f, 0.9f, t);
        frame.diskIntensity = SETTLED_DISK * (1 + 2 * infall) * fade;
        frame.diskInner = Mth.lerp(infall, inner, horizon * 1.05f);
        frame.diskOuter = Mth.lerp(smoothstep(0, 0.75f, t), outer, inner);
        frame.diskHeat = t;
        frame.diskSpeed = 1 + 4 * t;
        frame.arcIntensity = frame.diskIntensity;
        frame.ringIntensity = fade;
        frame.glowIntensity = SETTLED_GLOW * (1 + infall) * fade;

        frame.horizonWobble = 0.2f * (1 - smoothstep(0.5f, 0.7f, t));
        frame.horizon = horizon * (1 - smoothstep(0.6f, 0.85f, t));

        float flash = bell(t, 0.82f, 1.0f);
        frame.flare = 2.5f * flash;
        frame.flareSize = horizon * 6;

        float shock = smoothstep(0.82f, 1.0f, t);
        if (shock > 0) {
            frame.shockRadius = Mth.lerp(shock, horizon, STABILIZER_DISTANCE + 2);
            frame.shockIntensity = 1 - shock;
        }
    }

    public static Vector3f[] diskBasis(Vector3f[] axes, float tilt) {
        float cos = Mth.cos(tilt);
        float sin = Mth.sin(tilt);
        var normal = new Vector3f(axes[2]).mul(cos).add(new Vector3f(axes[4]).mul(sin));
        var bitangent = new Vector3f(axes[4]).mul(cos).sub(new Vector3f(axes[2]).mul(sin));
        return new Vector3f[] { new Vector3f(axes[0]), bitangent, normal };
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
        float c3 = c1 + 1;
        float s = t - 1;
        return 1 + c3 * s * s * s + c1 * s * s;
    }

    public static float wave(float time, float seed) {
        return 0.5f + 0.5f * Mth.sin(time * 0.23f * seed + seed) * Mth.sin(time * 0.59f + seed * 1.7f);
    }

    private static float hash(int n) {
        n = (n << 13) ^ n;
        return ((n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) / (float) 0x7fffffff;
    }
}
