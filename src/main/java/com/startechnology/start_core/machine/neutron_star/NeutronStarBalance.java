package com.startechnology.start_core.machine.neutron_star;

public final class NeutronStarBalance {

    public static final long V_UXV = 134_217_728L;

    public static final int DEFAULT_COLOR = 0x3380B3;

    public static final double F_INITIAL = 60;
    public static final double F_MAX = 716;
    public static final double F_DEATH = 1;
    public static final int DEFAULT_TARGET_SPIN = 200;

    public static final double M_INITIAL = 1.4;
    public static final double M_TOV = 2.3;
    public static final double COLLAPSE_WARNING = 0.9;

    public static final double K_BRAKE = 1.4e-4;
    public static final double N_BRAKE = 1.5;

    public static final double J_ACC = M_INITIAL;
    public static final int ACCRETION_RATE = 1000;
    public static final double HYDROGEN_MASS_PER_MB = 8.5e-8;
    public static final double HYDROGEN_SPIN_PER_MB = 0.0045;
    public static final double HELIUM_MASS_PER_MB = 1.25e-7;
    public static final double HELIUM_SPIN_PER_MB = 0.009;

    public static final int[] SPIN_TIERS = { 1, 30, 150, 450 };
    private static final String[] TIER_NAMES = { "I", "II", "III", "IV" };
    public static final double SPEED_CAP = 2;
    public static final double DRAW_REFERENCE = 4;

    public static final int GLITCH_PERIOD = 1200;
    public static final double GLITCH_SIZE = 0.01;
    public static final double GLITCH_CHANCE = 0.08;

    public static final long CAPTURE_EUT = 4 * V_UXV;
    public static final int CAPTURE_GRACE_TICKS = 20;

    public static final int CAPTURE_TICKS = 240;
    public static final int FADING_TICKS = 400;
    public static final int COLLAPSING_TICKS = 200;
    public static final int DISRUPTING_TICKS = 120;
    public static final int GLITCH_TICKS = 40;

    private NeutronStarBalance() {}

    public static double braking(double spin) {
        return K_BRAKE * Math.pow(Math.max(spin, 0), N_BRAKE);
    }

    public static double glitchChance(double spin) {
        return GLITCH_CHANCE * Math.sqrt(Math.max(spin, 0) / F_MAX);
    }

    public static double glitchRoll(long position, long window) {
        long hash = (position * 0x9E3779B97F4A7C15L) ^ (window * 0xC2B2AE3D27D4EB4FL);
        hash ^= hash >>> 29;
        hash *= 0xBF58476D1CE4E5B9L;
        hash ^= hash >>> 32;
        return (hash >>> 11) * 0x1.0p-53;
    }

    public static double processingSpeed(double spin, double minSpin) {
        return Math.min(Math.max(spin / Math.max(minSpin, SPIN_TIERS[0]), 1), SPEED_CAP);
    }

    public static int spinTier(double spin) {
        int tier = 0;
        while (tier < SPIN_TIERS.length && spin >= SPIN_TIERS[tier]) tier++;
        return tier;
    }

    public static String tierName(int tier) {
        return tier <= 0 ? "-" : TIER_NAMES[Math.min(tier, TIER_NAMES.length) - 1];
    }

    public static int encodeSpin(double spin) {
        double value = Math.log1p(Math.max(spin, 0)) / Math.log1p(F_MAX);
        return (int) Math.round(Math.min(Math.max(value, 0), 1) * 1023);
    }

    public static double decodeSpin(int encoded) {
        return Math.expm1(encoded / 1023.0 * Math.log1p(F_MAX));
    }

    public static double timeToDeath(double spin, double spinUp, double draw, int speed) {
        double time = 0;
        for (int i = 0; i < 4000 && spin >= F_DEATH; i++) {
            double net = spinUp - (braking(spin) + draw) * speed;
            if (net >= 0) return Double.POSITIVE_INFINITY;
            double dt = Math.max(0.005 * spin, 0.01) / -net;
            spin += net * dt;
            time += dt;
        }
        return spin < F_DEATH ? time : Double.POSITIVE_INFINITY;
    }
}
