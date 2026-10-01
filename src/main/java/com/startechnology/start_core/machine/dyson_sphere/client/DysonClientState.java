package com.startechnology.start_core.machine.dyson_sphere.client;

import com.startechnology.start_core.machine.dyson_sphere.DysonSphereMachine;
import com.startechnology.start_core.machine.dyson_sphere.RingAssembly;
import com.startechnology.start_core.machine.dyson_sphere.StellarBalance;
import com.startechnology.start_core.machine.dyson_sphere.StellarPhase;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

import java.util.Map;
import java.util.WeakHashMap;

import static com.startechnology.start_core.machine.dyson_sphere.DysonSphereGeometry.RING_COUNT;

public final class DysonClientState {

    private static final Map<DysonSphereMachine, DysonClientState> STATES = new WeakHashMap<>();
    private static final float SMOOTHING_RATE = 4;
    private static final float COLOR_RATE = 3;

    public float mass;
    public float output;
    public float supply = 1;
    public float red, green, blue;
    public final float[] precession = new float[RING_COUNT];
    public final float[] spin = new float[RING_COUNT];
    private float ringSpeed;
    private DysonHumSound hum;
    private long humStartNanos;
    private long lastNanos;
    private boolean initialized;

    public static DysonClientState of(DysonSphereMachine machine) {
        return STATES.computeIfAbsent(machine, m -> new DysonClientState());
    }

    public float activity() {
        return Mth.clamp(output * (float) StellarBalance.RG_BOOST, 0, 1);
    }

    void keepHum(DysonSphereMachine machine) {
        var sounds = Minecraft.getInstance().getSoundManager();
        boolean starting = System.nanoTime() - humStartNanos < 1_000_000_000L;
        if (hum != null && !hum.isStopped() && (starting || sounds.isActive(hum))) return;
        var next = new DysonHumSound(machine, this);
        if (next.silent()) return;
        hum = next;
        humStartNanos = System.nanoTime();
        sounds.play(next);
    }

    public void update(DysonSphereMachine machine) {
        long now = System.nanoTime();
        int color = machine.getStarColor();
        float targetRed = (color >> 16 & 0xFF) / 255f;
        float targetGreen = (color >> 8 & 0xFF) / 255f;
        float targetBlue = (color & 0xFF) / 255f;
        float targetOutput = machine.getDisplayOutput() / 255f;
        float targetSupply = machine.getPhase().isBurning() ? machine.getDisplaySupply() / 255f : 1;
        float targetSpeed = machine.getAssembly() == RingAssembly.DISASSEMBLING ? 0 : 0.02f + 0.1f * activity();

        if (!initialized) {
            initialized = true;
            mass = machine.getDisplayMass();
            output = targetOutput;
            supply = targetSupply;
            red = targetRed;
            green = targetGreen;
            blue = targetBlue;
            ringSpeed = targetSpeed;
            for (int i = 0; i < RING_COUNT; i++) {
                precession[i] = i * 1.3f;
                spin[i] = i * 0.7f;
            }
            lastNanos = now;
            return;
        }

        float dt = Math.min((now - lastNanos) / 1e9f, 0.25f);
        lastNanos = now;
        float k = 1 - (float) Math.exp(-SMOOTHING_RATE * dt);
        float colorK = 1 - (float) Math.exp(-COLOR_RATE * dt);
        if (machine.getDisplayMass() > 0 || machine.getPhase() == StellarPhase.IDLE) {
            mass = Mth.lerp(k, mass, machine.getDisplayMass());
        }
        output = Mth.lerp(k, output, targetOutput);
        supply = Mth.lerp(k, supply, targetSupply);
        red = Mth.lerp(colorK, red, targetRed);
        green = Mth.lerp(colorK, green, targetGreen);
        blue = Mth.lerp(colorK, blue, targetBlue);
        ringSpeed = Mth.lerp(k * 0.5f, ringSpeed, targetSpeed);

        for (int i = 0; i < RING_COUNT; i++) {
            float direction = i % 2 == 0 ? 1 : -1;
            float factor = 0.7f + 0.12f * i;
            precession[i] = (precession[i] + direction * ringSpeed * factor * dt) % Mth.TWO_PI;
            spin[i] = (spin[i] - direction * ringSpeed * 1.6f * factor * dt) % Mth.TWO_PI;
        }
    }
}
