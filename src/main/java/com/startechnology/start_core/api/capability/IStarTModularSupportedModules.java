package com.startechnology.start_core.api.capability;

import java.util.function.Consumer;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public interface IStarTModularSupportedModules {
    /* This return if this multiblock id is a supported module, being tested from a position */
    boolean isSupportedMultiblockId(ResourceLocation id, BlockPos fromPos);

    /* This returns an optional consumer on connection */
    Consumer<IStarTModularSupportedModules> getOnSupportedConsumer();

    /* Force invalidate this modular support item */
    void invalidateSupportedModule();
}
