package com.startechnology.start_core.mixin.claw;

import com.gregtechceu.gtceu.api.item.tool.ToolHelper;
import com.startechnology.start_core.item.claw.StarTClawOfLawItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ToolHelper.class, remap = false)
public class ToolHelperMixin {

    @Inject(method = "damageItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;I)V",
            at = @At("HEAD"),
            cancellable = true)
    private static void startCore$chargeClawAction(ItemStack stack, @Nullable LivingEntity user, int damage,
                                                   CallbackInfo callback) {
        if (!(stack.getItem() instanceof StarTClawOfLawItem)) return;

        StarTClawOfLawItem.chargeAction(stack, user);
        callback.cancel();
    }
}
