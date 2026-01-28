package com.startechnology.start_core.api.capability;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;

public interface IStarTModularSupportedModules {
    /* This should return a list of the machine definition ids which are supported */
    @Nullable
    List<ResourceLocation> getSupportedMultiblockIds();
}
