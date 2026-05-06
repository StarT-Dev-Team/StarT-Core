package com.startechnology.start_core.api.custom_tooltips;

import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;

// This is bad, But it works! And adding custom UI elements to JEI recipes
// is an UTTER pain. So I'm going to do this instead.
public class StarTCustomTooltip {
    private final ArrayList<MutableComponent> tooltipsList = new ArrayList<>();

    public ArrayList<MutableComponent> getTooltips() {
        return tooltipsList;
    }

    public StarTCustomTooltip(ListTag tooltips) {
        tooltips.forEach(tooltip -> {
                tooltipsList.add(
                    Component.translatable(tooltip.getAsString())
                );
            }
        );
    }
}
