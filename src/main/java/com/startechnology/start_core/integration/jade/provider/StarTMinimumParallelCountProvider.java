package com.startechnology.start_core.integration.jade.provider;

import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.capability.IParallelHatch;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.integration.jade.provider.CapabilityBlockProvider;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.capability.StarTCapabilityHelper;
import com.startechnology.start_core.machine.parallel.IStarTMinimumParallelHatch;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class StarTMinimumParallelCountProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    @Override
    public ResourceLocation getUid() {
        return StarTCore.resourceLocation("min_parallel");
    }

     public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof MetaMachineBlockEntity blockEntity) {
            if (blockEntity.getMetaMachine() instanceof IStarTMinimumParallelHatch minimumParallelHatch) {
                compoundTag.putInt("minParallel", minimumParallelHatch.getMinimumParallels());
            } else if (blockEntity.getMetaMachine() instanceof IMultiController controller) {

                /* this will let us view the minimum display on the machine controller too not just the hatch */
                controller.getParallelHatch()
                        .ifPresent(parallelHatch -> {
                            if (parallelHatch instanceof IStarTMinimumParallelHatch minimumParallelHatch) {
                                compoundTag.putInt("minParallel", minimumParallelHatch.getMinimumParallels());
                            }
                        });
            }
        }
    }

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        if (blockAccessor.getServerData().contains("minParallel")) {
            Integer minParallels = blockAccessor.getServerData().getInt("minParallel");

            if (minParallels > 1) {
                Component minParallelComponent = Component.literal(FormattingUtil.formatNumbers(minParallels))
                        .withStyle(ChatFormatting.DARK_PURPLE);
                iTooltip.add(Component.translatable("start_core.parallel_hatch.jade_min_parallel", minParallelComponent));
            }
        }
    }


}
