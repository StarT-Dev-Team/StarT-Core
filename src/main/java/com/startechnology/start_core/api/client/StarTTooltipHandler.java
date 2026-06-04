package com.startechnology.start_core.api.client;

import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltip;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class StarTTooltipHandler {

    public static void appendTooltip(ItemStack stack, TooltipFlag flag, List<Component> tooltips) {
        // Custom tooltips
        if (stack == null) return;
        if (stack.isEmpty()) return;
        if (!stack.hasTag()) return;

        if (StarTCustomTooltipsManager.hasCustomTooltip(stack.getOrCreateTag())) {
            StarTCustomTooltip customTooltips = StarTCustomTooltipsManager.customTooltipFromTag(stack.getOrCreateTag());

            tooltips.subList(1, tooltips.size()).clear();
            tooltips.addAll(customTooltips.getTooltips());
        }
    }
}
