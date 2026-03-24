package com.startechnology.start_core.mixin.ponder;

import com.startechnology.start_core.integration.ponder.WorldInstructionExtension;
import net.createmod.ponder.foundation.PonderSceneBuilder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PonderSceneBuilder.PonderWorldInstructions.class)
public abstract class PonderWorldInstructionsMixin implements WorldInstructionExtension {

    @Shadow(remap = false)
    @Final
    PonderSceneBuilder this$0;

    @Override
    public PonderSceneBuilder startcore$builder() {
        return this$0;
    }

}