package com.startechnology.start_core.item.claw.client;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.block.PipeBlock;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.item.tool.ToolHelper;
import com.startechnology.start_core.item.StarTItems;
import com.startechnology.start_core.item.claw.StarTClawMode;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.HashSet;
import java.util.Set;

public class StarTClawSuggestions {

    @Nullable
    public static StarTClawMode suggest(@Nullable HitResult hitResult, @Nullable Level level) {
        if (hitResult == null || level == null) return null;

        if (hitResult instanceof EntityHitResult entityHit) {
            return entityHit.getEntity() instanceof LivingEntity ? StarTClawMode.SWORD : null;
        }

        if (!(hitResult instanceof BlockHitResult blockHit) || hitResult.getType() != HitResult.Type.BLOCK) return null;

        BlockState state = level.getBlockState(blockHit.getBlockPos());
        Block block = state.getBlock();

        if (block instanceof PipeBlock<?, ?, ?> pipe) return byToolType(pipe.getPipeTuneTool());
        if (block instanceof MetaMachineBlock) return StarTClawMode.WRENCH;

        int harvestLevel = StarTItems.CLAW_OF_LAW.get().getTotalHarvestLevel(ItemStack.EMPTY);

        for (StarTClawMode mode : StarTClawMode.values()) {
            if (mode.getToolType() == null) continue;
            if (ToolHelper.isToolEffective(state, toolClasses(mode), harvestLevel)) return mode;
        }

        return null;
    }

    @Nullable
    private static StarTClawMode byToolType(GTToolType toolType) {
        for (StarTClawMode mode : StarTClawMode.values()) {
            if (mode.getToolType() == toolType) return mode;
        }

        return null;
    }

    private static Set<GTToolType> toolClasses(StarTClawMode mode) {
        Set<GTToolType> toolClasses = new HashSet<>(mode.getToolType().toolClasses);

        toolClasses.add(mode.getToolType());

        return toolClasses;
    }
}
