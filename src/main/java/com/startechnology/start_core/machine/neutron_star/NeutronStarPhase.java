package com.startechnology.start_core.machine.neutron_star;

public enum NeutronStarPhase {

    IDLE(0),
    CAPTURING(NeutronStarBalance.CAPTURE_TICKS),
    ACTIVE(0),
    FADING(NeutronStarBalance.FADING_TICKS),
    COLLAPSING(NeutronStarBalance.COLLAPSING_TICKS),
    DISRUPTING(NeutronStarBalance.DISRUPTING_TICKS);

    private static final NeutronStarPhase[] VALUES = values();

    public final int duration;

    NeutronStarPhase(int duration) {
        this.duration = duration;
    }

    public static NeutronStarPhase byId(int id) {
        return id >= 0 && id < VALUES.length ? VALUES[id] : IDLE;
    }

    public boolean hasStar() {
        return this != IDLE;
    }

    public boolean isEnding() {
        return this == FADING || this == COLLAPSING || this == DISRUPTING;
    }
}
