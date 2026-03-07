package com.startechnology.start_core.api.capability;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public interface IStarTModularSupportedModules {
    /* This return if this multiblock id is a supported module, being tested from a position */
    boolean isSupportedMultiblockId(ResourceLocation id, BlockPos fromPos);
}
