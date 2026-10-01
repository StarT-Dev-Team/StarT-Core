package com.startechnology.start_core.machine.dyson_sphere;

public enum RingAssembly {

    NONE,
    ASSEMBLING,
    ASSEMBLED,
    DISASSEMBLING;

    private static final RingAssembly[] VALUES = values();

    public static RingAssembly byId(int id) {
        return id >= 0 && id < VALUES.length ? VALUES[id] : NONE;
    }

    public boolean isStanding() {
        return this == ASSEMBLING || this == ASSEMBLED;
    }
}
