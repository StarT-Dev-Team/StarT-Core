package com.startechnology.start_core.integration.jade.provider;

import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.machine.solar.cell.StarTSolarCell;
import com.startechnology.start_core.machine.solar.cell.StarTSolarCellBlockEntity;
import com.startechnology.start_core.machine.solar.cell.StarTSolarCellType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class StarTSolarCellProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    @Override
    public ResourceLocation getUid() {
        return StarTCore.resourceLocation("solar_cells");
    }

    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof StarTSolarCellBlockEntity blockEntity) {
            compoundTag.putDouble("temperature", blockEntity.getTemperature());
            compoundTag.putInt("durability", blockEntity.getDurability());
            compoundTag.putBoolean("broken", blockEntity.isBroken());
        }
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor block, IPluginConfig config) {
        var serverData = block.getServerData();

        if (serverData.contains("temperature") && serverData.contains("durability") && serverData.contains("broken")) {
            if (serverData.getBoolean("broken")) {
                tooltip.add(Component.translatable("solar.start_core.solar_cell.is_broken"));
            } else if (block.getBlock() instanceof StarTSolarCell solarBlock) {
                StarTSolarCellType solarCellType = solarBlock.getSolarCellType();

                tooltip.add(Component.translatable("solar.start_core.solar_cell.temperature_tooltip", FormattingUtil.formatNumbers(serverData.getDouble("temperature")), solarCellType.getMaxTemperature()));
                tooltip.add(Component.translatable("solar.start_core.solar_cell.durability_tooltip", serverData.getInt("durability"), solarCellType.getMaxDurability()));
            }
        }
    }
}
