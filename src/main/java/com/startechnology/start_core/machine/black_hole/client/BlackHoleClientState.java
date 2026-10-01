package com.startechnology.start_core.machine.black_hole.client;

import com.startechnology.start_core.machine.black_hole.BlackHoleGeneratorMachine;
import com.startechnology.start_core.machine.black_hole.BlackHolePhase;

import net.minecraft.util.Mth;

import java.util.Map;
import java.util.WeakHashMap;

public final class BlackHoleClientState {

    private static final Map<BlackHoleGeneratorMachine, BlackHoleClientState> STATES = new WeakHashMap<>();
    private static final float SMOOTHING_RATE = 4f;

    public float mass;
    public float stability;
    public final float[] beams = new float[6];
    private long lastNanos;
    private boolean initialized;

    public static BlackHoleClientState of(BlackHoleGeneratorMachine machine) {
        return STATES.computeIfAbsent(machine, m -> new BlackHoleClientState());
    }

    public void update(BlackHoleGeneratorMachine machine) {
        long now = System.nanoTime();
        float targetMass = machine.getDisplayMass();
        float targetStability = machine.getDisplayStability() / 255f;
        if (!initialized) {
            initialized = true;
            mass = targetMass;
            stability = targetStability;
            for (int i = 0; i < beams.length; i++) beams[i] = machine.getBeamLevel(i) / 15f;
            lastNanos = now;
            return;
        }

        float dt = Math.min((now - lastNanos) / 1e9f, 0.25f);
        lastNanos = now;
        float k = 1 - (float) Math.exp(-SMOOTHING_RATE * dt);
        if (targetMass > 0 || machine.getPhase() == BlackHolePhase.IDLE) mass = Mth.lerp(k, mass, targetMass);
        stability = Mth.lerp(k, stability, targetStability);
        for (int i = 0; i < beams.length; i++) {
            beams[i] = Mth.lerp(k, beams[i], machine.getBeamLevel(i) / 15f);
        }
    }

    public static float phaseTime(BlackHoleGeneratorMachine machine, float partialTick) {
        var level = machine.getLevel();
        if (level == null) return 0;
        return Math.max(0, level.getGameTime() + partialTick - machine.getPhaseStartGameTime());
    }
}
