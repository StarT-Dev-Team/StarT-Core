package com.startechnology.start_core.mixin.ponder;

import com.google.common.collect.Multimap;
import net.createmod.ponder.foundation.PonderTag;
import net.createmod.ponder.foundation.registration.PonderLocalization;
import net.createmod.ponder.foundation.registration.PonderTagRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

@Mixin(value = PonderTagRegistry.class, remap = false)
public interface PonderTagRegistryAccessor {

    @Accessor(value = "MISSING")
    PonderTag getMissing();

    @Accessor(value = "localization")
    PonderLocalization getLocalization();

    @Accessor(value = "componentTagMap")
    Multimap<ResourceLocation, ResourceLocation> getComponentTagMap();

    @Accessor(value = "registeredTags")
    Map<ResourceLocation, PonderTag> getRegisteredTags();

    @Accessor(value = "listedTags")
    List<PonderTag> getListedTags();

}
