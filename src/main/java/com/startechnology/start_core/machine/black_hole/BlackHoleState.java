package com.startechnology.start_core.machine.black_hole;

import static com.startechnology.start_core.machine.black_hole.BlackHoleBalance.DEFICIT_EPSILON;
import static com.startechnology.start_core.machine.black_hole.BlackHoleBalance.FEED_SMOOTHING;
import static com.startechnology.start_core.machine.black_hole.BlackHoleBalance.K_DEF;
import static com.startechnology.start_core.machine.black_hole.BlackHoleBalance.K_IMB;
import static com.startechnology.start_core.machine.black_hole.BlackHoleBalance.M_MIN;
import static com.startechnology.start_core.machine.black_hole.BlackHoleBalance.S_RECOVER;
import static com.startechnology.start_core.machine.black_hole.BlackHoleBalance.evaporation;

public final class BlackHoleState {

    public enum Outcome {
        STABLE,
        EVAPORATE,
        COLLAPSE
    }

    public double mass;
    public double stability = 1.0;
    public double feedRate;
    public double dMdt;
    public double demand;
    public double output;

    public void reset(double initialMass) {
        mass = initialMass;
        stability = 1.0;
        feedRate = 0;
        dMdt = 0;
        demand = mass > 0 ? BlackHoleBalance.demand(mass, 0) : 0;
        output = 0;
    }

    public Outcome step(double dt, double consumed, double[] supply) {
        double f = consumed / dt;
        feedRate += (f - feedRate) * FEED_SMOOTHING;
        dMdt = f - evaporation(mass);
        mass = Math.max(0, mass + dMdt * dt);

        double deficit = 0.0;
        double imbalance = 0.0;
        for (int i = 0; i < supply.length; i++) {
            deficit += 1 - clamp01(supply[i]);
            if (i % 2 == 0) imbalance += Math.abs(clamp01(supply[i]) - clamp01(supply[i + 1]));
        }
        if (deficit < DEFICIT_EPSILON) {
            stability += S_RECOVER * (1 - stability) * dt;
        } else {
            stability -= (K_DEF * deficit + K_IMB * imbalance) * dt;
        }
        stability = clamp01(stability);

        demand = BlackHoleBalance.demand(mass, dMdt);
        output = BlackHoleBalance.output(mass, feedRate, stability);

        if (stability <= 0) return Outcome.COLLAPSE;
        if (mass < M_MIN) return Outcome.EVAPORATE;
        return Outcome.STABLE;
    }

    public double demandPerStabilizer() {
        return demand / 6;
    }

    private static double clamp01(double value) {
        return Math.min(Math.max(value, 0), 1);
    }
}
