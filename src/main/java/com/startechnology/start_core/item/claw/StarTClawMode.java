package com.startechnology.start_core.item.claw;

import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum StarTClawMode {

    NONE(null),
    PICKAXE(GTToolType.PICKAXE),
    AXE(GTToolType.AXE),
    SHOVEL(GTToolType.SHOVEL),
    HOE(GTToolType.HOE),
    SAW(GTToolType.SAW),
    SCREWDRIVER(GTToolType.SCREWDRIVER),
    WRENCH(GTToolType.WRENCH),
    SOFT_MALLET(GTToolType.SOFT_MALLET),
    HARD_HAMMER(GTToolType.HARD_HAMMER),
    CROWBAR(GTToolType.CROWBAR),
    WIRE_CUTTER(GTToolType.WIRE_CUTTER),
    KNIFE(GTToolType.KNIFE),
    SWORD(GTToolType.SWORD);

    @Getter
    @Nullable
    private final GTToolType toolType;
    @Getter
    private final String id;
    @Getter
    private final String langKey;

    StarTClawMode(@Nullable GTToolType toolType) {
        this.toolType = toolType;
        this.id = name().toLowerCase(Locale.ROOT);
        this.langKey = "item.start_core.claw_of_law.mode." + id;
    }

    public StarTClawMode next() {
        return values()[(this.ordinal() + 1) % values().length];
    }

    public StarTClawMode prev() {
        return values()[(this.ordinal() + values().length - 1) % values().length];
    }

    public static StarTClawMode getByName(String name) {
        for (StarTClawMode mode : values()) {
            if (mode.id.equals(name)) {
                return mode;
            }
        }

        return NONE;
    }
}
