package com.startechnology.start_core.mixin.compat;

import appeng.api.stacks.GenericStack;
import appeng.menu.slot.FakeSlot;
import com.almostreliable.merequester.client.RequestSlot;
import com.almostreliable.merequester.platform.Platform;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "appeng.integration.modules.jeirei.DropTargets$FakeSlotDropTarget", remap = false)
public abstract class FakeSlotDropTargetMixin {
    @Shadow
    private static ItemStack wrapFilterAsItem(GenericStack genericStack) {
        throw new AssertionError();
    }

    @Shadow
    @Final
    private FakeSlot slot;

    /**
     * Adds compatibility for emi
     */
    @Inject(
            method = "drop",
            at = @At("HEAD"),
            cancellable = true
    )
    private void start$drop(GenericStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (slot instanceof RequestSlot requestSlot) {
            Platform.sendDragAndDrop(
                    requestSlot.getRequesterReference().getRequesterId(),
                    requestSlot.getSlot(),
                    wrapFilterAsItem(stack)
            );
            cir.setReturnValue(true);
        }
    }
}
