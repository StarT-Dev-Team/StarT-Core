package com.startechnology.start_core.client.megastructure;

import com.startechnology.start_core.StarTConfig;

public enum RenderTier {

    FULL,
    FALLBACK,
    OFF;

    public static RenderTier resolve(StarTConfig.RenderTierMode mode, boolean fullAvailable) {
        return switch (mode) {
            case OFF -> OFF;
            case FALLBACK -> FALLBACK;
            case FULL -> fullAvailable ? FULL : FALLBACK;
            case AUTO -> fullAvailable && !ShaderpackCompat.isShaderPackInUse() ? FULL : FALLBACK;
        };
    }
}
