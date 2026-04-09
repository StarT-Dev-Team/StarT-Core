package com.startechnology.start_core.mixin.ccb;

import codechicken.lib.data.MCDataOutput;
import codechicken.multipart.api.part.MultiPart;
import codechicken.multipart.block.TileMultipart;
import codechicken.multipart.network.MultiPartSPH;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(value = MultiPartSPH.class, remap = false)
public class MultiPartSPHMixin {

    @Inject(method = "sendDescUpdate", at = @At("HEAD"), cancellable = true)
    private static void injectSendDescUpdate(TileMultipart tile, CallbackInfo ci) {
        if (tile.getLevel() instanceof ServerLevel) {
            return;
        }
        ci.cancel();
    }

    @Inject(method = "sendAddPart", at = @At("HEAD"), cancellable = true)
    private static void injectSendAddPart(TileMultipart tile, MultiPart part, CallbackInfo ci) {
        if (tile.getLevel() instanceof ServerLevel) {
            return;
        }
        ci.cancel();
    }

    @Inject(method = "sendRemPart", at = @At("HEAD"), cancellable = true)
    private static void injectSendRemPart(TileMultipart tile, int partIdx, CallbackInfo ci) {
        if (tile.getLevel() instanceof ServerLevel) {
            return;
        }
        ci.cancel();
    }

    @Inject(method = "dispatchPartUpdate", at = @At("HEAD"), cancellable = true)
    private static void injectDispatchPartUpdate(MultiPart part, Consumer<MCDataOutput> func, CallbackInfo ci) {
        if (part.level() instanceof ServerLevel) {
            return;
        }
        ci.cancel();
    }

}
