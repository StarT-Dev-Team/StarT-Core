package com.startechnology.start_core.mixin.ponder;

import com.startechnology.start_core.integration.ponder.OverlayInstructionsExtension;
import net.createmod.ponder.foundation.PonderSceneBuilder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PonderSceneBuilder.PonderOverlayInstructions.class)
public abstract class PonderOverlayInstructionMixin implements OverlayInstructionsExtension {

    @Shadow(remap = false)
    @Final
    PonderSceneBuilder this$0;

    @Override
    public PonderSceneBuilder startcore$builder() {
        return this$0;
    }

}