package com.startechnology.start_core.item.components;

import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltip;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class StarTNBTTooltipsBehaviour implements IAddInformation {

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents,
            TooltipFlag isAdvanced) {
        
        if (stack == null) return;
        if (stack.isEmpty()) return;
        if (!stack.hasTag()) return;

        StarTCustomTooltip tooltips = StarTCustomTooltipsManager.customTooltipFromTag(stack.getOrCreateTag());

        if (tooltips != null) {
            tooltipComponents.subList(1, tooltipComponents.size()).clear();
            tooltipComponents.addAll(tooltips.getTooltips());
        }
    }
    
}
