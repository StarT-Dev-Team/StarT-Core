package com.startechnology.start_core.machine.black_hole;

public final class BlackHoleBalance {

    public static final long V_UIV = 33_554_432L;

    public static final double M_NOMINAL = 100.0;
    public static final double M_MIN = 10.0;

    public static final double K_EVAP = 5_000.0;
    public static final double K_ACC = 0.01;

    public static final double P_MASS = 48.0 * V_UIV / M_NOMINAL;
    public static final double P_ACC = 16.0 * V_UIV / (K_ACC * M_NOMINAL);

    public static final double D0 = 2.7e5;
    public static final double D1 = 1.35e6;

    public static final double S_RECOVER = 0.05;
    public static final double K_DEF = 0.05;
    public static final double K_IMB = 0.05;
    public static final double DEFICIT_EPSILON = 1e-3;
    public static final double WARNING_STABILITY = 0.5;

    public static final double FEED_SMOOTHING = 0.25;
    public static final double MATTER_EFFICIENCY = 0.001;

    public static final int IGNITION_TICKS = 200;
    public static final int EVAPORATION_TICKS = 100;
    public static final int COLLAPSE_TICKS = 120;

    public static final long IGNITION_EUT_PER_STABILIZER = (long) (demand(M_NOMINAL, 0) * 60 * 20 /
            IGNITION_TICKS / 6);

    private BlackHoleBalance() {}

    public static double evaporation(double mass) {
        return K_EVAP / Math.max(mass * mass, 1e-6);
    }

    public static double accretionLimit(double mass) {
        return K_ACC * mass;
    }

    public static double demand(double mass, double dMdt) {
        return D0 * Math.pow(mass, 1.5) + D1 * Math.abs(dMdt) * mass;
    }

    public static double outputEfficiency(double stability) {
        return lerp(0.25, 1.0, smoothstep(0.3, 1.0, stability));
    }

    public static double output(double mass, double feedRate, double stability) {
        return (P_MASS * mass + P_ACC * feedRate) * outputEfficiency(stability);
    }

    public static final double MAX_VISUAL_RADIUS = 13.0;
    public static final double MAX_HORIZON_RADIUS = 5.0;

    public static double horizonRadius(double mass) {
        return Math.min(3.0 * Math.cbrt(Math.max(mass, 0) / M_NOMINAL), MAX_HORIZON_RADIUS);
    }

    public static double diskInnerRadius(double horizonRadius, double spin) {
        return horizonRadius * (1.7 - 0.4 * spin);
    }

    public static double diskOuterRadius(double horizonRadius) {
        return Math.min(horizonRadius * 5.0, MAX_VISUAL_RADIUS);
    }

    public static double lensingRadius(double horizonRadius) {
        return 1.8 * diskOuterRadius(horizonRadius);
    }

    static double smoothstep(double edge0, double edge1, double x) {
        double t = Math.min(Math.max((x - edge0) / (edge1 - edge0), 0.0), 1.0);
        return t * t * (3 - 2 * t);
    }

    static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }
}
