package com.startechnology.start_core.api.gcrop;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum StarTClimateType {

    FROSTY("frosty"),
    SCORCHING("scorching"),
    TROPICAL("tropical"),
    DESERTIC("desertic"),
    DAMP("damp");

    @Getter
    private final String id;

    StarTClimateType(String id) {
        this.id = id;
    }

    @Nullable
    public static StarTClimateType getByName(@Nullable String name) {
        if (name == null) return null;
        String lowerName = name.toLowerCase(Locale.ROOT);
        for (StarTClimateType type : values()) {
            if (type.id.equalsIgnoreCase(lowerName) || type.name().equalsIgnoreCase(lowerName)) {
                return type;
            }
        }
        return null;
    }

    @Nullable
    public static StarTClimateType getClimateFromTrait(@Nullable StarTGCropTrait trait) {
        return (trait == null) ? null : getByName(trait.id());
    }
}
