package com.startechnology.start_core.integration.jade.provider;

import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.machine.threading.StarTThreadingStatBlocks;
import com.startechnology.start_core.machine.threading.StarTThreadingStatBlocks.StarTThreadingStatBlock;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class StarTThreadedStatBlockProvider implements IBlockComponentProvider {
    public StarTThreadedStatBlockProvider() {
    }

    @Override
    public ResourceLocation getUid() {
        return StarTCore.resourceLocation("threading_stat_blocks");
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor block, IPluginConfig config) {
        if (block.getBlock() instanceof StarTThreadingStatBlock threadingBlock) {
            tooltip.add(Component.translatable("block.start_core.helix_tooltip_title"));
            for (String stat : StarTThreadingStatBlocks.statList) {
                ChatFormatting color = switch (stat) {
                    case "speed" -> ChatFormatting.GREEN;           // §a
                    case "efficiency" -> ChatFormatting.LIGHT_PURPLE; // §d
                    case "parallels" -> ChatFormatting.RED;          // §c
                    case "threading" -> ChatFormatting.BLUE;         // §9
                    default -> ChatFormatting.WHITE;                 // §f
                };
                tooltip.add(Component.translatable("block.start_core.stat." + stat + ".display", 
                    Component.translatable("start_core.machine.threading.stat." + stat), 
                    Component.literal(FormattingUtil.formatNumbers(threadingBlock.getThreadingStats().getStatString(stat))).withStyle(color)));
            }
        }

    }
}
