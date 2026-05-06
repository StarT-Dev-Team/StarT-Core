package com.startechnology.start_core.mixin;

import com.gregtechceu.gtceu.utils.ResearchManager;
import com.startechnology.start_core.item.StarTItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.item.ItemStack;

@Mixin(value = ResearchManager.class, remap = false)
public class ResearchManagerDataItemMixin {
    
    @Inject(method = "getDefaultResearchStationItem", at = @At("HEAD"), cancellable = true)
    private static void onGetDefaultResearchStationItem(int cwut, CallbackInfoReturnable<ItemStack> cir) {
        if (cwut >= 160 && cwut< 320) {
            cir.setReturnValue(StarTItems.TOOL_DATA_DNA_DISK.asStack());
        }
        if (cwut >= 320) {
            cir.setReturnValue(StarTItems.TOOL_COMPONENT_DATA_CORE.asStack());
        }
    }

}
