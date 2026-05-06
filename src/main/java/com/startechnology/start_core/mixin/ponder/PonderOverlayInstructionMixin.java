package com.startechnology.start_core.mixin.ponder;

import com.startechnology.start_core.integration.ponder.OverlayInstructionsExtension;
import net.createmod.ponder.foundation.PonderSceneBuilder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = PonderSceneBuilder.PonderOverlayInstructions.class, remap = false)
public abstract class PonderOverlayInstructionMixin implements OverlayInstructionsExtension {

    @Shadow
    @Final
    PonderSceneBuilder this$0;

    @Override
    public PonderSceneBuilder startcore$builder() {
        return this$0;
    }

}