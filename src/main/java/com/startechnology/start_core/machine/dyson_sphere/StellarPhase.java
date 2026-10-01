package com.startechnology.start_core.machine.dyson_sphere;

public enum StellarPhase {

    IDLE(0),
    PROTOSTAR(0),
    IGNITING(StellarBalance.IGNITION_TICKS),
    MAIN_SEQUENCE(0),
    RED_GIANT_TRANSITION(StellarBalance.RG_TRANSITION_TICKS),
    RED_GIANT(0),
    NEBULA(StellarBalance.NEBULA_TICKS),
    SUPERNOVA(StellarBalance.SUPERNOVA_TICKS),
    FIZZLE(StellarBalance.FIZZLE_TICKS),
    DISPERSING(StellarBalance.DISPERSING_TICKS);

    private static final StellarPhase[] VALUES = values();

    public final int duration;

    StellarPhase(int duration) {
        this.duration = duration;
    }

    public static StellarPhase byId(int id) {
        return id >= 0 && id < VALUES.length ? VALUES[id] : IDLE;
    }

    public boolean hasStar() {
        return this == PROTOSTAR || this == IGNITING || isBurning();
    }

    public boolean isBurning() {
        return this == MAIN_SEQUENCE || this == RED_GIANT_TRANSITION || this == RED_GIANT;
    }

    public boolean isEnding() {
        return this == NEBULA || this == SUPERNOVA || this == FIZZLE || this == DISPERSING;
    }
}
