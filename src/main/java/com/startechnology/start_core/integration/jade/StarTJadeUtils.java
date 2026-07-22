package com.startechnology.start_core.integration.jade;

import net.minecraft.nbt.CompoundTag;

public class StarTJadeUtils {
    
    public static boolean hasData(CompoundTag tag, String... keys) {
        for (String key : keys) {
            if (!tag.contains(key)) return false;
        }
        return true;
    }

}
