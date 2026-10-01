package com.startechnology.start_core.machine.dyson_sphere;

import static com.startechnology.start_core.machine.dyson_sphere.StellarBalance.ACCRETION_RATE;
import static com.startechnology.start_core.machine.dyson_sphere.StellarBalance.B_H;
import static com.startechnology.start_core.machine.dyson_sphere.StellarBalance.B_HE;
import static com.startechnology.start_core.machine.dyson_sphere.StellarBalance.E_H;
import static com.startechnology.start_core.machine.dyson_sphere.StellarBalance.E_HE;
import static com.startechnology.start_core.machine.dyson_sphere.StellarBalance.MB_PER_MASS;
import static com.startechnology.start_core.machine.dyson_sphere.StellarBalance.P0;
import static com.startechnology.start_core.machine.dyson_sphere.StellarBalance.PROTOSTAR_MIN_TICKS;
import static com.startechnology.start_core.machine.dyson_sphere.StellarBalance.RG_BOOST;
import static com.startechnology.start_core.machine.dyson_sphere.StellarBalance.STARVE_LIMIT;
import static com.startechnology.start_core.machine.dyson_sphere.StellarBalance.STARVE_THRESHOLD;
import static com.startechnology.start_core.machine.dyson_sphere.StellarBalance.SUPPLY_SMOOTHING;
import static com.startechnology.start_core.machine.dyson_sphere.StellarBalance.luminosity;

public final class StellarState {

    public enum Fuel {
        NONE,
        HYDROGEN,
        HELIUM_PLASMA
    }

    public StellarPhase phase = StellarPhase.IDLE;
    public long phaseTicks;
    public double mass;
    public double coreHydrogen;
    public double coreHelium;
    public double lastSupply;
    public double supply;
    public int starvedTicks;
    public long ignitionPaid;
    public double output;

    public Fuel fuel() {
        return switch (phase) {
            case IDLE, PROTOSTAR, MAIN_SEQUENCE -> Fuel.HYDROGEN;
            case RED_GIANT_TRANSITION, RED_GIANT -> Fuel.HELIUM_PLASMA;
            default -> Fuel.NONE;
        };
    }

    public double fuelRequest(int targetMass, int speed) {
        return switch (phase) {
            case IDLE, PROTOSTAR -> Math.max(0, Math.min((double) ACCRETION_RATE * speed,
                    (targetMass - mass) * MB_PER_MASS));
            case MAIN_SEQUENCE, RED_GIANT_TRANSITION, RED_GIANT -> need();
            default -> 0;
        };
    }

    public double need() {
        return switch (phase) {
            case MAIN_SEQUENCE -> B_H * luminosity(mass);
            case RED_GIANT_TRANSITION, RED_GIANT -> B_HE * luminosity(mass) * RG_BOOST;
            default -> 0;
        };
    }

    public long energyRequest(int targetMass) {
        return switch (phase) {
            case PROTOSTAR -> Math.max(0, StellarBalance.ignitionCost(Math.max(mass, targetMass)) - ignitionPaid);
            case IGNITING -> Math.max(0, StellarBalance.ignitionCost(mass) - ignitionPaid);
            default -> 0;
        };
    }

    public void pay(long energy) {
        ignitionPaid += energy;
    }

    public void tick(int targetMass) {
        phaseTicks++;
        switch (phase) {
            case PROTOSTAR -> {
                if (mass >= targetMass && phaseTicks >= PROTOSTAR_MIN_TICKS) enter(StellarPhase.IGNITING);
            }
            case IGNITING -> {
                if (phaseTicks < phase.duration) return;
                if (ignitionPaid >= StellarBalance.ignitionCost(mass)) {
                    ignitionPaid = 0;
                    coreHydrogen = 1;
                    coreHelium = 0;
                    supply = 1;
                    enter(StellarPhase.MAIN_SEQUENCE);
                } else {
                    enter(StellarPhase.PROTOSTAR);
                }
            }
            case RED_GIANT_TRANSITION -> {
                if (phaseTicks >= phase.duration) enter(StellarPhase.RED_GIANT);
            }
            case NEBULA, SUPERNOVA, FIZZLE, DISPERSING -> {
                if (phaseTicks >= phase.duration) reset();
            }
            default -> {}
        }
    }

    public void step(double dt, double consumed, int speed) {
        output = 0;
        switch (phase) {
            case IDLE -> {
                if (consumed <= 0) return;
                enter(StellarPhase.PROTOSTAR);
                mass += consumed / MB_PER_MASS;
            }
            case PROTOSTAR -> mass += consumed / MB_PER_MASS;
            case MAIN_SEQUENCE, RED_GIANT_TRANSITION, RED_GIANT -> burn(dt, consumed, speed);
            default -> {}
        }
    }

    private void burn(double dt, double consumed, int speed) {
        double need = need();
        double f = need > 0 ? Math.min(consumed / need, 1) : 0;
        lastSupply = f;
        supply += (f - supply) * SUPPLY_SMOOTHING;
        double effective = luminosity(mass) * f;

        switch (phase) {
            case MAIN_SEQUENCE -> {
                output = P0 * effective;
                coreHydrogen -= effective * dt * speed / (E_H * mass);
                if (coreHydrogen <= 0) {
                    coreHydrogen = 0;
                    coreHelium = 1;
                    enter(StellarPhase.RED_GIANT_TRANSITION);
                }
            }
            case RED_GIANT_TRANSITION -> {
                double progress = Math.min(phaseTicks / (double) phase.duration, 1);
                output = P0 * effective * (1 + (RG_BOOST - 1) * progress);
            }
            case RED_GIANT -> {
                output = P0 * effective * RG_BOOST;
                coreHelium -= effective * RG_BOOST * dt * speed / (E_HE * mass);
                if (coreHelium <= 0) {
                    coreHelium = 0;
                    enter(StellarBalance.fate(mass));
                    output = 0;
                    return;
                }
            }
            default -> {}
        }

        starvedTicks = f < STARVE_THRESHOLD ? starvedTicks + (int) Math.round(dt * 20) : 0;
        if (starvedTicks > STARVE_LIMIT) {
            enter(StellarPhase.FIZZLE);
            output = 0;
        }
    }

    public void disperse() {
        if (!phase.hasStar()) return;
        output = 0;
        enter(StellarPhase.DISPERSING);
    }

    public void reset() {
        phase = StellarPhase.IDLE;
        phaseTicks = 0;
        mass = 0;
        coreHydrogen = 0;
        coreHelium = 0;
        lastSupply = 0;
        supply = 0;
        starvedTicks = 0;
        ignitionPaid = 0;
        output = 0;
    }

    private void enter(StellarPhase next) {
        phase = next;
        phaseTicks = 0;
        starvedTicks = 0;
    }
}
