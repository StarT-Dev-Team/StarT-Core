package com.startechnology.start_core.machine.vcr;

public enum VacuumPumpTier {

    ZPM (5f, 80f),
    UV (10f, 85f),
    UHV (15f, 90f),
    UEV (20f, 95f),
    UIV (25f, 100f);

    public final float rate;
    public final float cap;

    VacuumPumpTier(float rate, float cap) {
        this.rate = rate;
        this.cap = cap;
    }
}
