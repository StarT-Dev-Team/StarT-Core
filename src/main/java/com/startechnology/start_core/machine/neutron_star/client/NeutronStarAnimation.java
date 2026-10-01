package com.startechnology.start_core.machine.neutron_star.client;

import com.startechnology.start_core.machine.neutron_star.NeutronStarBalance;
import com.startechnology.start_core.machine.neutron_star.NeutronStarGeometry;
import com.startechnology.start_core.machine.neutron_star.NeutronStarPhase;

import net.minecraft.util.Mth;

import static com.startechnology.start_core.machine.neutron_star.client.NeutronStarField.LOOP_COUNT;

public final class NeutronStarAnimation {

    public static final float STAR_RADIUS = 0.2f;
    public static final float JET_LENGTH = NeutronStarGeometry.COLLECTOR_DISTANCE / NeutronStarGeometry.SCALE + 0.05f;
    public static final float COLOR_PEAK = 0xB3 / 255f;

    public static final class Frame {

        public float starRadius;
        public float surfaceAmplitude;
        public float jetLength;
        public float jetWidthBase;
        public float jetWidthSlope;
        public float starWeight;
        public float loopWeight;
        public float beadWeight;
        public float jetWeight;
        public final float[] grow = new float[LOOP_COUNT];
        public final float[] twist = new float[LOOP_COUNT];
        public float flash;
        public float shellRadius;
        public float shellIntensity;
        public float darkCore;
        public float red;
        public float green;
        public float blue;
    }

    private NeutronStarAnimation() {}

    public static void compute(Frame f, NeutronStarPhase phase, float t, float seconds, float glitchAge,
                               NeutronStarClientState state) {
        float max = Math.max(Math.max(state.red, state.green), Math.max(state.blue, 1e-4f));
        f.red = state.red / max * COLOR_PEAK;
        f.green = state.green / max * COLOR_PEAK;
        f.blue = state.blue / max * COLOR_PEAK;

        float mass = state.mass * (float) NeutronStarBalance.M_TOV;
        float shrink = Mth.clamp((mass - (float) NeutronStarBalance.M_INITIAL) /
                (float) (NeutronStarBalance.M_TOV - NeutronStarBalance.M_INITIAL), 0, 1);
        float near = smoothstep(0.92f, 1, state.mass);
        float radius = STAR_RADIUS * (1 - 0.08f * shrink) *
                (1 + 0.03f * near * (float) Math.sin(seconds * Mth.TWO_PI * 1.5f));
        float amplitude = 0.04f * (1 + 1.5f * near);
        float loop = Mth.clamp(0.6f + 0.4f * (float) Math.sqrt(Math.max(state.spin, 0) / 300f), 0.3f, 1.3f);
        float jet = 0.45f + 0.55f * state.work;
        float star = 1;
        float bead = loop;
        float jetLength = JET_LENGTH;
        float widthBase = 0.003f;
        float widthSlope = 0.02f;
        float flash = 0;
        float shellRadius = 0;
        float shellIntensity = 0;
        float darkCore = 0;
        for (int i = 0; i < LOOP_COUNT; i++) {
            f.grow[i] = 1;
            f.twist[i] = 0;
        }

        if (glitchAge >= 0 && glitchAge < NeutronStarBalance.GLITCH_TICKS) {
            float k = (float) Math.exp(-glitchAge / 6);
            flash = 0.8f * k;
            amplitude *= 1 + 2 * (float) Math.exp(-glitchAge / 10);
            for (int i = 0; i < LOOP_COUNT; i++) f.twist[i] = 0.12f * k * (float) Math.sin(glitchAge * 1.7f + i * 2.3f);
        }

        switch (phase) {
            case CAPTURING -> {
                float p = t / NeutronStarBalance.CAPTURE_TICKS;
                radius *= 0.08f * smoothstep(0, 0.3f, p) + 0.92f * easeOutBack(smoothstep(0.3f, 0.6f, p));
                flash = Math.max(flash, 0.5f * smoothstep(0, 0.3f, p) * (1 - smoothstep(0.35f, 0.6f, p)));
                for (int i = 0; i < LOOP_COUNT; i++) f.grow[i] = easeOutBack((t - 100 - 8 * i) / 40);
                bead *= smoothstep(150, 200, t);
                jetLength = JET_LENGTH * smoothstep(168, 184, t);
                if (t >= 168) flash = Math.max(flash, 1.2f * (float) Math.exp(-(t - 168) / 8));
            }
            case FADING -> {
                float p = t / NeutronStarBalance.FADING_TICKS;
                jetLength = JET_LENGTH * (1 - smoothstep(0, 0.55f, p));
                jet *= (0.3f + 0.7f * (flicker(t, 3, 1) < 0.55f ? 1 : 0)) * (1 - smoothstep(0.3f, 0.6f, p));
                for (int i = 0; i < LOOP_COUNT; i++) {
                    f.grow[i] = 1 - smoothstep(0.1f + 0.04f * i, 0.55f + 0.04f * i, p);
                }
                loop *= 1 - smoothstep(0.2f, 0.7f, p);
                bead *= 1 - smoothstep(0, 0.3f, p);
                radius *= 1 - 0.6f * smoothstep(0.4f, 0.95f, p);
                star *= (1 - 0.85f * smoothstep(0.35f, 0.85f, p)) * (1 - smoothstep(0.9f, 1, p));
            }
            case COLLAPSING -> {
                float p = t / NeutronStarBalance.COLLAPSING_TICKS;
                float wind = smoothstep(0, 0.4f, p);
                for (int i = 0; i < LOOP_COUNT; i++) {
                    f.grow[i] = 1 - wind;
                    f.twist[i] = 2.5f * wind * wind * (1 + 0.1f * i);
                }
                float flare = smoothstep(0, 0.2f, p) * (1 - smoothstep(0.3f, 0.38f, p));
                widthBase *= 1 + 3 * flare;
                widthSlope *= 1 + 1.5f * flare;
                jet *= 1 + 1.5f * flare;
                jetLength = JET_LENGTH * (1 - smoothstep(0.33f, 0.4f, p));
                radius *= 1 - 0.95f * smoothstep(0.4f, 0.6f, p);
                star *= (1 + 2 * smoothstep(0.4f, 0.6f, p)) * (1 - smoothstep(0.6f, 0.66f, p));
                if (p >= 0.6f) {
                    flash = 3 * (float) Math.exp(-(p - 0.6f) * NeutronStarBalance.COLLAPSING_TICKS / 10);
                    shellRadius = NeutronStarGeometry.SHELL_REACH * (float) Math.pow((p - 0.6f) / 0.35f, 0.7f);
                    shellIntensity = 1.2f * (1 - smoothstep(0.6f, 0.95f, p));
                }
                darkCore = 0.03f * smoothstep(0.76f, 0.8f, p) * (1 - smoothstep(0.88f, 1, p));
            }
            case DISRUPTING -> {
                float p = t / NeutronStarBalance.DISRUPTING_TICKS;
                for (int i = 0; i < LOOP_COUNT; i++) f.grow[i] = 1 + 0.9f * smoothstep(0, 0.35f, p);
                loop *= 1 - smoothstep(0.1f, 0.45f, p);
                bead *= 1 - smoothstep(0, 0.2f, p);
                jet = 0;
                jetLength = 0;
                star *= (0.4f + 0.6f * (flicker(t, 2, 7) > 0.45f ? 1 : 0)) * (1 - smoothstep(0.3f, 1, p));
                radius *= 1 - 0.5f * smoothstep(0.2f, 1, p);
            }
            default -> {}
        }

        for (int i = 0; i < LOOP_COUNT; i++) f.grow[i] = Math.max(f.grow[i], 0);
        f.starRadius = radius;
        f.surfaceAmplitude = amplitude;
        f.jetLength = jetLength;
        f.jetWidthBase = widthBase;
        f.jetWidthSlope = widthSlope;
        f.starWeight = star;
        f.loopWeight = loop;
        f.beadWeight = bead;
        f.jetWeight = jet;
        f.flash = flash;
        f.shellRadius = shellRadius;
        f.shellIntensity = shellIntensity;
        f.darkCore = darkCore;
    }

    public static float smoothstep(float a, float b, float x) {
        float t = Mth.clamp((x - a) / (b - a), 0, 1);
        return t * t * (3 - 2 * t);
    }

    static float easeOutBack(float t) {
        t = Mth.clamp(t, 0, 1) - 1;
        return 1 + 2.2f * t * t * t + 1.2f * t * t;
    }

    static float flicker(float t, float period, float seed) {
        double n = Math.sin((Math.floor(t / period) + seed) * 12.9898) * 43758.5453;
        return (float) (n - Math.floor(n));
    }
}
