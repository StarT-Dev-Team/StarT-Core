package com.startechnology.start_core.machine.dimensional_destabiliser.client;

import com.startechnology.start_core.machine.dimensional_destabiliser.DestabiliserRift;

public final class RiftAnimation {

    public static final float OPENING_TICKS = 30;
    public static final float CLOSING_TICKS = 25;
    public static final float PULSE_TICKS = 30;
    public static final float PULSE_DECAY_TICKS = 8;
    public static final float WORK_ATTACK = 0.6f;
    public static final float WORK_RELEASE = 2.5f;
    public static final float PROGRESS_TIME = 1;

    public enum State {
        NONE,
        OPENING,
        DORMANT,
        ACTIVE,
        CLOSING
    }

    public static final class Frame {

        public float pulse;
        public float open;
        public float width;
        public float glow;
        public float lens;
        public float turbulence;
        public float zipLow;
        public float zipHigh;
        public float flash;
    }

    private RiftAnimation() {}

    public static float age(long gameTime, float partialTick, long eventGameTime) {
        return gameTime - eventGameTime + partialTick;
    }

    public static State state(boolean formed, boolean working, float openAge, float closeAge) {
        if (formed) {
            if (openAge < OPENING_TICKS) return State.OPENING;
            return working ? State.ACTIVE : State.DORMANT;
        }
        return closeAge < CLOSING_TICKS ? State.CLOSING : State.NONE;
    }

    public static float approach(float value, float target, float dt, float rise, float fall) {
        float tau = target > value ? rise : fall;
        return value + (target - value) * (1 - (float) Math.exp(-dt / tau));
    }

    public static float pulse(float age) {
        if (age < 0 || age >= PULSE_TICKS) return 0;
        return (float) Math.exp(-age / PULSE_DECAY_TICKS);
    }

    public static float timeSpeed(float work) {
        return mix(0.25f, 1, work);
    }

    public static void compute(Frame frame, float work, float progress, float pulseAge) {
        float pulse = pulse(pulseAge);
        float open = work * mix(0.35f, 1, progress);
        frame.pulse = pulse;
        frame.open = open;
        frame.width = DestabiliserRift.HALF_WIDTH * open * (1 + 0.25f * pulse);
        frame.glow = mix(0.25f, 1, work) * (1 + 1.2f * pulse);
        frame.lens = mix(0.15f, 1, open) * (1 + 0.5f * pulse);
        frame.turbulence = mix(0.3f, 1, work);
        frame.zipLow = -1;
        frame.zipHigh = 1;
        frame.flash = 0;
    }

    public static void opening(Frame frame, float age) {
        if (age < 0 || age >= OPENING_TICKS) return;
        float q = age / OPENING_TICKS;
        if (q < 0.2f) {
            frame.zipLow = frame.zipHigh = 0;
            frame.flash = q / 0.2f;
            frame.width = 0;
        } else if (q < 0.7f) {
            float u = (q - 0.2f) / 0.5f;
            float e = 1 - (1 - u) * (1 - u);
            frame.zipLow = -e;
            frame.zipHigh = e;
            frame.width = 0;
            frame.flash = Math.max(0, 1 - u / 0.6f);
            frame.glow *= 2 - u;
        } else {
            frame.width *= smoothstep(0.7f, 1, q);
        }
    }

    public static void closing(Frame frame, float age) {
        if (age < 0 || age >= CLOSING_TICKS) return;
        float q = age / CLOSING_TICKS;
        if (q < 0.5f) {
            float u = q / 0.5f;
            float e = 1 - u * u;
            frame.zipLow = -e;
            frame.zipHigh = e;
            frame.width *= 1 - u;
            frame.turbulence *= 2;
        } else {
            frame.zipLow = frame.zipHigh = 0;
            frame.width = 0;
            frame.flash = q < 0.7f ? 1 : (1 - q) / 0.3f;
            if (q < 0.7f) frame.lens *= 2;
            frame.glow *= frame.flash;
        }
    }

    private static float smoothstep(float a, float b, float x) {
        float t = Math.max(0, Math.min(1, (x - a) / (b - a)));
        return t * t * (3 - 2 * t);
    }

    private static float mix(float a, float b, float t) {
        return a + (b - a) * t;
    }
}
