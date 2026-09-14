package com.startechnology.start_core.mixin.eb;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.startechnology.start_core.integration.eb.ExtendedCommonEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import nl.requios.effortlessbuilding.CommonEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = CommonEvents.class, remap = false)
public class CommonEventsMixin {

    @WrapOperation(method = "onBlockPlaced",
                   at = @At(value = "INVOKE",
                            target = "Lnl/requios/effortlessbuilding/CommonEvents;isPlayerHoldingBlock(Lnet/minecraft/world/entity/player/Player;)Z"))
    private static boolean onBlockPlaced(Player player, Operation<Boolean> original) {
        return ExtendedCommonEvents.isBlockPlacementItem(player.getItemInHand(InteractionHand.MAIN_HAND), player);
    }
}
