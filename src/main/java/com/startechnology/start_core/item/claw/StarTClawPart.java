package com.startechnology.start_core.item.claw;

import lombok.Getter;

public enum StarTClawPart {

    TOES("toes", 0x687080),
    MITT("mitt", 0xF2F2F2),
    CUFF("cuff", 0x384858);

    @Getter
    private final String key;
    @Getter
    private final int defaultColor;

    StarTClawPart(String key, int defaultColor) {
        this.key = key;
        this.defaultColor = defaultColor;
    }

    public static StarTClawPart byRow(int row) {
        return values()[Math.min(row, values().length - 1)];
    }
}
