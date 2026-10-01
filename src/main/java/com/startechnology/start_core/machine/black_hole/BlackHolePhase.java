package com.startechnology.start_core.machine.black_hole;

public enum BlackHolePhase {

    IDLE,
    IGNITING,
    STABLE,
    EVAPORATING,
    COLLAPSING;

    private static final BlackHolePhase[] VALUES = values();

    public static BlackHolePhase byId(int id) {
        return id >= 0 && id < VALUES.length ? VALUES[id] : IDLE;
    }

    public boolean isActive() {
        return this != IDLE;
    }
}
