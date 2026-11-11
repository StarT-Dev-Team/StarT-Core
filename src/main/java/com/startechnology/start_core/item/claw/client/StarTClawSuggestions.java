package com.startechnology.start_core.item.claw.client;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.block.PipeBlock;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.item.tool.ToolHelper;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMaintenanceMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.startechnology.start_core.item.StarTItems;
import com.startechnology.start_core.item.claw.StarTClawMode;
import com.startechnology.start_core.item.claw.StarTClawTargets;
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

        if (StarTClawTargets.getCover(level, blockHit) != null) return StarTClawMode.CROWBAR;

        BlockState state = level.getBlockState(blockHit.getBlockPos());
        Block block = state.getBlock();

        if (block instanceof PipeBlock<?, ?, ?> pipe) {
            GTToolType toolType = pipe.getPipeTuneTool();

            for (StarTClawMode mode : StarTClawMode.values()) {
                if (mode.getToolType() == toolType) return mode;
            }
        }

        if (block instanceof MetaMachineBlock) {
            MetaMachine machine = MetaMachine.getMachine(level, blockHit.getBlockPos());

            if (machine instanceof IMaintenanceMachine maintenance && maintenance.hasMaintenanceProblems())
                return StarTClawMode.NONE;
            if (machine instanceof IMultiPart) return StarTClawMode.SCREWDRIVER;
            if (machine instanceof IRecipeLogicMachine logicMachine && logicMachine.getRecipeLogic().isWorking())
                return StarTClawMode.SOFT_MALLET;

            return StarTClawMode.WRENCH;
        }

        int harvestLevel = StarTItems.CLAW_OF_LAW.get().getTotalHarvestLevel(ItemStack.EMPTY);

        for (StarTClawMode mode : StarTClawMode.values()) {
            if (mode.getToolType() == null) continue;

            Set<GTToolType> toolClasses = new HashSet<>(mode.getToolType().toolClasses);

            toolClasses.add(mode.getToolType());

            if (ToolHelper.isToolEffective(state, toolClasses, harvestLevel)) return mode;
        }

        return null;
    }
}
