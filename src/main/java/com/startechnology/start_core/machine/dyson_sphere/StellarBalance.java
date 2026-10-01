package com.startechnology.start_core.machine.dyson_sphere;

public final class StellarBalance {

    public static final long V_UXV = 134_217_728L;

    public static final int M_MIN = 1;
    public static final int M_MAX = 40;
    public static final int DEFAULT_TARGET_MASS = 10;

    public static final int M_SUPERNOVA = 8;
    public static final int M_SEED = 20;

    public static final double M_REF = 10.0;
    public static final double ALPHA = 2.0;
    public static final double P0 = 64.0 * V_UXV;
    public static final double RG_BOOST = 1.6;

    public static final double B_H = 20_000.0;
    public static final double B_HE = 2_000.0;

    public static final double MS_LIFETIME_REF = 6 * 3600.0;
    public static final double RG_LIFETIME_FRACTION = 0.25;
    public static final double E_H = MS_LIFETIME_REF / M_REF;
    public static final double E_HE = RG_LIFETIME_FRACTION * E_H * RG_BOOST;

    public static final int MB_PER_MASS = 100_000;
    public static final int ACCRETION_RATE = 20_000;

    public static final double STARVE_THRESHOLD = 0.05;
    public static final int STARVE_LIMIT = 6000;
    public static final double SUPPLY_SMOOTHING = 0.25;
    public static final int IGNITION_OUTPUT_SECONDS = 60;

    public static final int PROTOSTAR_MIN_TICKS = 600;
    public static final int IGNITION_TICKS = 160;
    public static final int RG_TRANSITION_TICKS = 1200;
    public static final int NEBULA_TICKS = 400;
    public static final int SUPERNOVA_TICKS = 300;
    public static final int FIZZLE_TICKS = 200;
    public static final int DISPERSING_TICKS = 160;
    public static final int ASSEMBLY_TICKS = 200;
    public static final int DISASSEMBLY_TICKS = 160;

    private StellarBalance() {}

    public static double luminosity(double mass) {
        return Math.pow(mass / M_REF, ALPHA);
    }

    public static double nominalOutput(double mass) {
        return P0 * luminosity(mass);
    }

    public static long ignitionCost(double mass) {
        return (long) (nominalOutput(mass) * 20 * IGNITION_OUTPUT_SECONDS);
    }

    public static StellarPhase fate(double mass) {
        return mass >= M_SUPERNOVA ? StellarPhase.SUPERNOVA : StellarPhase.NEBULA;
    }

    public static Remnant remnant(double mass) {
        if (mass >= M_SEED) return Remnant.SINGULARITY_SEED;
        return mass >= M_SUPERNOVA ? Remnant.NEUTRON_STAR : Remnant.NONE;
    }

    public enum Remnant {
        NONE,
        NEUTRON_STAR,
        SINGULARITY_SEED
    }
}
