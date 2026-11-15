package com.startechnology.start_core.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class StarTTooltipUtils {

    public static Component withHover(MutableComponent line, String hoverKey) {
        return line.withStyle(Style.EMPTY.withHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(hoverKey))));
    }
}
