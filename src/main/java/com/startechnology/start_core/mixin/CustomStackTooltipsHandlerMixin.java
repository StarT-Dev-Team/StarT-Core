package com.startechnology.start_core.mixin;

import java.util.List;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gregtechceu.gtceu.client.TooltipsHandler;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltip;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.fluids.FluidStack;

@Mixin(TooltipsHandler.class)
public class CustomStackTooltipsHandlerMixin {
    
    @Inject(method = "appendTooltips", at = @At("HEAD"), remap = false, cancellable = true)
    private static void onAppendTooltips(ItemStack stack, TooltipFlag flag, List<Component> tooltips, CallbackInfo ci) {
        if (stack == null || stack.isEmpty() || !stack.hasTag()) return;
        
        if (StarTCustomTooltipsManager.hasCustomTooltip(stack.getOrCreateTag())) {
            ci.cancel();
        }
    }

    @Inject(method = "appendFluidTooltips", at = @At("HEAD"), remap = false, cancellable = true)
    private static void onAppendFluidTooltips(FluidStack fluidStack, Consumer<Component> tooltips, TooltipFlag flag, CallbackInfo ci) {
        if (fluidStack == null || fluidStack.isEmpty() || !fluidStack.hasTag()) return;
        
        if (StarTCustomTooltipsManager.hasCustomTooltip(fluidStack.getOrCreateTag())) {
            StarTCustomTooltip customTooltips = StarTCustomTooltipsManager.customTooltipFromTag(fluidStack.getOrCreateTag());

            customTooltips.getTooltips().forEach(
                tooltip -> tooltips.accept(tooltip)
            );

            ci.cancel();
        }
    }
}
