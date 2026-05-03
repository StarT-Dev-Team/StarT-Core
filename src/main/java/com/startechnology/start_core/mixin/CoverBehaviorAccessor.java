package com.startechnology.start_core.mixin;

import com.gregtechceu.gtceu.api.cover.CoverBehavior;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = CoverBehavior.class, remap = false)
public interface CoverBehaviorAccessor {

    @Mutable
    @Accessor("attachedSide")
    void start_core$setAttachedSide(Direction side);

}
