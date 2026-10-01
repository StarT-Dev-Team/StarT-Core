package com.startechnology.start_core.machine.neutron_star.client;

import com.startechnology.start_core.machine.neutron_star.NeutronStarBalance;
import com.startechnology.start_core.machine.neutron_star.NeutronStarForgeMachine;
import com.startechnology.start_core.machine.neutron_star.NeutronStarPhase;

import net.minecraft.util.Mth;

import java.util.Map;
import java.util.WeakHashMap;

public final class NeutronStarClientState {

    private static final Map<NeutronStarForgeMachine, NeutronStarClientState> STATES = new WeakHashMap<>();
    private static final float SMOOTHING_RATE = 4;
    private static final float COLOR_RATE = 3;
    private static final float SURFACE_WRAP = 3600;

    public float spin;
    public float mass;
    public float work;
    public float feed;
    public float red, green, blue;
    public float spinAngle;
    public float beadPhase;
    public float surfaceTime;
    public final NeutronStarAnimation.Frame frame = new NeutronStarAnimation.Frame();
    public final NeutronStarField field = new NeutronStarField();
    private long lastNanos;
    private boolean initialized;

    public static NeutronStarClientState of(NeutronStarForgeMachine machine) {
        return STATES.computeIfAbsent(machine, m -> new NeutronStarClientState());
    }

    public static float visualOmega(float spin) {
        return Mth.clamp(0.9f * (float) Math.pow(Math.max(spin, 0) / 60f, 0.35f), 0.25f, 3f);
    }

    public void update(NeutronStarForgeMachine machine, float gameTime) {
        long now = System.nanoTime();
        int color = machine.getStarColor();
        float targetRed = (color >> 16 & 0xFF) / 255f;
        float targetGreen = (color >> 8 & 0xFF) / 255f;
        float targetBlue = (color & 0xFF) / 255f;
        float targetSpin = (float) NeutronStarBalance.decodeSpin(machine.getDisplaySpin());
        float targetMass = machine.getDisplayMass() / 255f;
        float targetWork = machine.getDisplayWork() / 255f;
        float targetFeed = machine.getDisplayFeed() / 255f;

        if (!initialized) {
            initialized = true;
            spin = targetSpin;
            mass = targetMass;
            work = targetWork;
            feed = targetFeed;
            red = targetRed;
            green = targetGreen;
            blue = targetBlue;
            long hash = machine.getPos().asLong() * 0x9E3779B97F4A7C15L;
            spinAngle = (hash >>> 40) / (float) (1 << 24) * Mth.TWO_PI;
            beadPhase = (hash >>> 16 & 0xFFFFFF) / (float) (1 << 24) * Mth.TWO_PI;
            surfaceTime = (hash >>> 4 & 0xFFF) / 4096f * SURFACE_WRAP;
            lastNanos = now;
            return;
        }

        float dt = Mth.clamp((now - lastNanos) / 1e9f, 0, 0.1f);
        lastNanos = now;
        float k = 1 - (float) Math.exp(-SMOOTHING_RATE * dt);
        float c = 1 - (float) Math.exp(-COLOR_RATE * dt);
        spin += (targetSpin - spin) * k;
        mass += (targetMass - mass) * k;
        work += (targetWork - work) * k;
        feed += (targetFeed - feed) * k;
        red += (targetRed - red) * c;
        green += (targetGreen - green) * c;
        blue += (targetBlue - blue) * c;

        float omega = visualOmega(spin);
        float glitchAge = gameTime - machine.getGlitchStartGameTime();
        if (glitchAge >= 0 && glitchAge < NeutronStarBalance.GLITCH_TICKS) {
            omega *= 1 + 4 * (float) Math.exp(-glitchAge / 6);
        }
        float beads = omega * 5 / 0.9f;
        var phase = machine.getPhase();
        if (phase == NeutronStarPhase.FADING) {
            beads *= 1 - NeutronStarAnimation.smoothstep(0, 0.3f,
                    (gameTime - machine.getPhaseStartGameTime()) / NeutronStarBalance.FADING_TICKS);
        }
        spinAngle = wrap(spinAngle + omega * dt, Mth.TWO_PI);
        beadPhase = wrap(beadPhase + beads * dt, Mth.TWO_PI);
        surfaceTime = wrap(surfaceTime + (1 + 2 * feed) * dt, SURFACE_WRAP);
    }

    private static float wrap(float value, float period) {
        return value - period * Mth.floor(value / period);
    }
}
