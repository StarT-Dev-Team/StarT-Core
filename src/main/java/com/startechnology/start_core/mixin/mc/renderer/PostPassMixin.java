package com.startechnology.start_core.mixin.mc.renderer;

import com.startechnology.start_core.machine.komaru.client.ReplaceablePostPass;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.renderer.PostPass;

import java.util.List;
import java.util.function.IntSupplier;

@Mixin(PostPass.class)
public abstract class PostPassMixin implements ReplaceablePostPass {

    @Shadow
    @Final
    private List<IntSupplier> auxAssets;

    @Shadow
    @Final
    private List<String> auxNames;

    @Shadow
    @Final
    private List<Integer> auxWidths;

    @Shadow
    @Final
    private List<Integer> auxHeights;

    @Shadow
    public void addAuxAsset(String auxName, IntSupplier auxFramebuffer, int width, int height) {}

    @Override
    public void start_core$replaceAuxAsset(String auxName, IntSupplier auxFramebuffer, int width, int height) {
        var index = auxNames.indexOf(auxName);
        if (index >= 0) {
            auxAssets.set(index, auxFramebuffer);
            auxWidths.set(index, width);
            auxHeights.set(index, height);
            return;
        }
        addAuxAsset(auxName, auxFramebuffer, width, height);
    }
}
