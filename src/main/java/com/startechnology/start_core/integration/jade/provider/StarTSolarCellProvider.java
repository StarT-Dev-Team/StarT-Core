package com.startechnology.start_core.integration.jade.provider;

import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.machine.solar.cell.StarTSolarCellBlockEntity;
import com.startechnology.start_core.machine.solar.cell.StarTSolarCellType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class StarTSolarCellProvider implements IBlockComponentProvider {
    public StarTSolarCellProvider() {
    }

    @Override
    public ResourceLocation getUid() {
        return StarTCore.resourceLocation("solar_cells");
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor block, IPluginConfig config) {
        if (block.getBlockEntity() instanceof StarTSolarCellBlockEntity solarCellBlockEntity) {
            StarTSolarCellType solarCellType = (StarTSolarCellType) solarCellBlockEntity.getType();

            tooltip.add(Component.translatable("solar.start_core.solar_cell.temperature_tooltip", FormattingUtil.formatNumbers(solarCellBlockEntity.getTemperature()), solarCellType.getMaxTemperature()));
            tooltip.add(Component.translatable("solar.start_core.solar_cell.durability_tooltip", solarCellBlockEntity.getDurability(), solarCellType.getMaxDurability()));
        }
    }
}
