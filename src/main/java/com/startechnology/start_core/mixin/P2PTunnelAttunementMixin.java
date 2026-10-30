package com.startechnology.start_core.mixin;

import appeng.api.features.P2PTunnelAttunement;
import com.startechnology.start_core.StarTConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.capabilities.Capability;

@Mixin(value = P2PTunnelAttunement.class, remap = false)
public class P2PTunnelAttunementMixin {

    @Inject(method = "registerAttunementTag", at = @At("HEAD"), cancellable = true)
    private static void start$onRegisterAttunementTag(ItemLike tunnel, CallbackInfo ci) {
        if (start$isFEP2PTunnelDisabled(tunnel)) {
            ci.cancel();
        }
    }

    @Inject(method = "registerAttunementApi", at = @At("HEAD"), cancellable = true)
    private static void start$onRegisterAttunementApi(ItemLike tunnelPart, Capability<?> cap, Component description,
                                                      CallbackInfo ci) {
        if (start$isFEP2PTunnelDisabled(tunnelPart)) {
            ci.cancel();
        }
    }

    @Inject(method = "getTunnelPartByTriggerItem", at = @At("RETURN"), cancellable = true)
    private static void start$onGetTunnelPartByTriggerItem(ItemStack trigger, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack result = cir.getReturnValue();
        if (!result.isEmpty() && start$isFEP2PTunnelDisabled(result.getItem())) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }

    @Unique
    private static boolean start$isFEP2PTunnelDisabled(ItemLike tunnel) {
        if (StarTConfig.INSTANCE == null) {
            StarTConfig.init();
        }
        return !StarTConfig.INSTANCE.ae2.enableFEP2PTunnelAttunement &&
                tunnel != null &&
                tunnel.asItem() == P2PTunnelAttunement.ENERGY_TUNNEL.asItem();
    }
}
