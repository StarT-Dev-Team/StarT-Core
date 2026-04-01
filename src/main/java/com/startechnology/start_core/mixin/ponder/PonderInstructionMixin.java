package com.startechnology.start_core.mixin.ponder;

import com.startechnology.start_core.integration.ponder.PonderErrorHelper;
import dev.latvian.mods.rhino.RhinoException;
import net.createmod.ponder.foundation.PonderScene;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(targets = "net.createmod.ponder.foundation.instruction.PonderInstruction$Simple")
public class PonderInstructionMixin {

    @Shadow(remap = false)
    private Consumer<PonderScene> callback;

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void init(Consumer<PonderScene> argCallback, CallbackInfo ci) {
        callback = ponderScene -> {
            try {
                argCallback.accept(ponderScene);
            } catch (RhinoException e) {
                PonderErrorHelper.reportJsPonderError(e);
                Minecraft.getInstance().setScreen(null);
            }
        };
    }

}
