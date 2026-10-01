package com.startechnology.start_core.machine.hellforge.client;

import com.startechnology.start_core.machine.hellforge.HellFlameProfile;

public final class HellFlameAnimation {

    public static final int KINDLE_TICKS = 40;
    public static final int GUTTER_TICKS = 30;
    public static final int BURST_TICKS = 40;
    public static final int FLIPBOOK_FRAMES = 32;
    private static final float FLIPBOOK_LOOP = 1.6f;
    private static final float PILOT = 0.3f;
    private static final float BURST_RISE_TICKS = 4;
    private static final float BURST_DECAY_TICKS = 12;

    public enum State {
        NONE,
        KINDLING,
        BURNING,
        GUTTERING
    }

    public static final class Frame {

        public float height;
        public float width;
        public float intensity;
        public float core;
        public float turbulence;
        public float burst;
        public float tintRed, tintGreen, tintBlue, tintAmount;
        public float lift;
    }

    private HellFlameAnimation() {}

    public static float age(long gameTime, float partialTick, long eventGameTime) {
        return gameTime - eventGameTime + partialTick;
    }

    public static State state(boolean formed, float kindleAge, float gutterAge) {
        if (formed) return kindleAge < KINDLE_TICKS ? State.KINDLING : State.BURNING;
        return gutterAge < GUTTER_TICKS ? State.GUTTERING : State.NONE;
    }

    public static float burst(int heatBurst, float age) {
        if (age < 0 || age >= BURST_TICKS) return 0;
        float envelope = age < BURST_RISE_TICKS ? age / BURST_RISE_TICKS :
                (float) Math.exp(-(age - BURST_RISE_TICKS) / BURST_DECAY_TICKS);
        return HellFlameProfile.burstStrength(heatBurst) * envelope;
    }

    public static float approach(float value, float target, float dt, float rise, float fall) {
        float tau = target > value ? rise : fall;
        return value + (target - value) * (1 - (float) Math.exp(-dt / tau));
    }

    public static void compute(Frame frame, HellFlameProfile profile, float heat, float roar, int heatBurst,
                               float burstAge) {
        float burst = burst(heatBurst, burstAge);
        float size = (float) Math.sqrt(heat);
        frame.burst = burst;
        frame.height = profile.height * mix(PILOT, 1, size) * (1 + 0.2f * roar) * (1 + 0.35f * burst);
        frame.width = mix(0.6f, 1, size) * (1 + 0.1f * roar + 0.1f * burst);
        frame.intensity = mix(0.45f, 1, heat) * (1 + 0.15f * roar) * (1 + 0.8f * burst);
        frame.core = mix(0.75f, 1.1f, heat) * profile.coreScale;
        frame.turbulence = 1 + 0.6f * roar + 0.5f * burst;
        frame.tintRed = (heatBurst >> 16 & 0xFF) / 255f;
        frame.tintGreen = (heatBurst >> 8 & 0xFF) / 255f;
        frame.tintBlue = (heatBurst & 0xFF) / 255f;
        frame.tintAmount = 0.6f * burst;
        frame.lift = 0;
    }

    public static void kindle(Frame frame, float age) {
        float p = Math.max(age, 0) / KINDLE_TICKS;
        float grow, spread = 1;
        if (p < 0.15f) {
            grow = 0.1f + 0.05f * p / 0.15f;
            spread = 0.4f;
            frame.intensity *= 2.2f;
            frame.core *= 1.3f;
        } else if (p < 0.6f) {
            float ease = 1 - square(1 - (p - 0.15f) / 0.45f);
            grow = 0.15f + ease;
            spread = 0.4f + 0.6f * ease;
            frame.turbulence *= 1.5f;
        } else {
            float settle = smoothstep(0.6f, 1, p);
            grow = 1.15f - 0.15f * settle;
            frame.turbulence *= 1.5f - 0.5f * settle;
        }
        frame.intensity *= 1 + 0.8f * (float) Math.exp(-square((p - 0.25f) / 0.05f));
        frame.height *= grow;
        frame.width *= spread;
    }

    public static void gutter(Frame frame, float age) {
        age = Math.max(age, 0);
        float p = age / GUTTER_TICKS;
        float dip = 0.45f + 0.55f * flicker(age, 2, 3);
        frame.turbulence *= 2;
        if (p < 0.4f) {
            float u = p / 0.4f;
            frame.intensity *= dip * (1 - 0.3f * u);
            frame.lift = 0.2f * frame.height * u * u;
        } else {
            float u = (p - 0.4f) / 0.6f;
            frame.intensity *= dip * 0.7f * square(1 - u);
            frame.lift = frame.height * (0.2f + 0.6f * u);
            frame.width *= 1 - 0.6f * u;
            frame.height *= 1 - 0.5f * u;
        }
    }

    public static float flameSpeed(float heat, float roar) {
        return (0.8f + 0.4f * heat) * (1 + 0.8f * roar);
    }

    public static int flipbookFrame(float flameTime) {
        return (int) (flameTime / FLIPBOOK_LOOP * FLIPBOOK_FRAMES) % FLIPBOOK_FRAMES;
    }

    public static float emberRate(float heat, float roar) {
        return 2 + 6 * heat + 8 * roar;
    }

    private static float flicker(float ticks, float period, float seed) {
        double n = Math.sin((Math.floor(ticks / period) + seed) * 12.9898) * 43758.5453;
        return (float) (n - Math.floor(n));
    }

    private static float mix(float a, float b, float t) {
        return a + (b - a) * t;
    }

    static float smoothstep(float edge0, float edge1, float x) {
        float t = Math.min(Math.max((x - edge0) / (edge1 - edge0), 0), 1);
        return t * t * (3 - 2 * t);
    }

    private static float square(float x) {
        return x * x;
    }
}
