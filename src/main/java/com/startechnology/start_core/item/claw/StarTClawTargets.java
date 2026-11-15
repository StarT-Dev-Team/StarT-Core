package com.startechnology.start_core.item.claw;

import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.capability.ICoverable;
import com.gregtechceu.gtceu.api.cover.CoverBehavior;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public final class StarTClawTargets {

    private StarTClawTargets() {}

    @Nullable
    public static CoverBehavior getCover(Level level, BlockHitResult hitResult) {
        ICoverable coverable = GTCapabilityHelper.getCoverable(level, hitResult.getBlockPos(), null);

        if (coverable == null) return null;

        Direction side = ICoverable.determineGridSideHit(hitResult);

        return side == null ? null : coverable.getCoverAtSide(side);
    }
}
