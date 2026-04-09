package com.startechnology.start_core.mixin.ccl;

import codechicken.lib.render.block.BlockRenderingRegistry;
import codechicken.lib.render.block.ICCBlockRenderer;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Predicate;

@Mixin(value = BlockRenderingRegistry.class, remap = false)
public interface BlockRenderingRegistryAccessor {

    @Invoker("findFor")
    static ICCBlockRenderer invokeFindFor(Block block, Predicate<ICCBlockRenderer> predicate) {
        throw new AssertionError();
    }

}
