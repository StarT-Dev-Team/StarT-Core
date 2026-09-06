package com.startechnology.start_core.machine.bulking;

import java.text.DecimalFormat;

public enum BulkingType {

    BUKLING_4_3(4, 3.25),
    BULKING_8_6(8, 6.5),
    BULKING_16_13(16, 13),
    BULKING_32_26(32, 26),
    BULKING_64_52(64, 52);

    public int throughputModifier;
    public double durationModifier;
    public String name;

    private BulkingType(int throughputModifier, double durationModifier) {
        this.throughputModifier = throughputModifier;
        this.durationModifier = durationModifier;
        this.name = String.format("%s:%s", throughputModifier, new DecimalFormat("0.##").format(durationModifier));
    }

    public BulkingType next() {
        return values()[(this.ordinal() + 1) % values().length];
    }

    public BulkingType prev() {
        return values()[(this.ordinal() + values().length - 1) % values().length];
    }
}
