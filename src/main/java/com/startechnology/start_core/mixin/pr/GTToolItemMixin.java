package com.startechnology.start_core.mixin.pr;

import com.gregtechceu.gtceu.api.item.tool.GTToolItem;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.item.tool.ToolHelper;
import mrtjp.projectred.api.IScrewdriver;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@Mixin(value = GTToolItem.class, remap = false)
public class GTToolItemMixin implements IScrewdriver {

    @Override
    public boolean canUse(Player player, ItemStack stack) {
        var types = ToolHelper.getToolTypes(stack);
        return !types.isEmpty() && ToolHelper.canUse(stack) && types.contains(GTToolType.SCREWDRIVER);
    }

    @Override
    public void damageScrewdriver(Player player, ItemStack itemStack) {
        ToolHelper.damageItem(itemStack, player);
    }

}
