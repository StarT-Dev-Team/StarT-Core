package com.startechnology.start_core.integration.jade.provider;

import com.gregtechceu.gtceu.integration.jade.provider.CapabilityBlockProvider;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.capability.StarTCapabilityHelper;
import com.startechnology.start_core.integration.jade.StarTJadeUtils;
import com.startechnology.start_core.machine.heat_chamber.HeatChamberMachine;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class StarTHeatChamberProvider extends CapabilityBlockProvider<HeatChamberMachine> {

    public StarTHeatChamberProvider() {
        super(StarTCore.resourceLocation("heat_chamber_info"));
    }

    @Override
    protected @Nullable HeatChamberMachine getCapability(Level level, BlockPos pos, @Nullable Direction side) {
        return StarTCapabilityHelper.getHeatChamberMachine(level, pos, side);
    }

    @Override
    protected void write(CompoundTag data, HeatChamberMachine capability) {
        data.putInt("hc_temperature", capability.getTemperature());
        data.putInt("hc_target_temperature", capability.getTargetTemperature());
        data.putInt("hc_heating_rate", capability.getHeatingRate());
        data.putInt("hc_status", capability.getHeatStatus().ordinal());
    }

    @Override
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, Player player, BlockAccessor block,
                              BlockEntity blockEntity, IPluginConfig config) {
        if (!StarTJadeUtils.hasData(capData, "hc_temperature", "hc_target_temperature",
                "hc_heating_rate", "hc_status")) {
            return;
        }

        int temperature = capData.getInt("hc_temperature");
        int targetTemperature = capData.getInt("hc_target_temperature");
        int rate = capData.getInt("hc_heating_rate");
        var status = HeatChamberMachine.Status.of(capData.getInt("hc_status"));

        tooltip.add(Component.empty()
                .append(Component.translatable("ui.start_core.heat_chamber.status",
                        HeatChamberMachine.formatHeatStatus(status)))
                .append(", ")
                .append(Component.translatable("ui.start_core.heat_chamber.temperature",
                        HeatChamberMachine.formatTemperature(temperature, status))));

        var rateLine = Component.empty();
        if (targetTemperature > HeatChamberMachine.DEFAULT_BASE_TEMPERATURE) {
            rateLine.append(Component.translatable("ui.start_core.heat_chamber.target_temperature",
                    Component.literal(FormattingUtil.formatNumbers(targetTemperature) + " K")
                            .withStyle(ChatFormatting.GOLD)))
                    .append(", ");
        }
        rateLine.append(Component.translatable("ui.start_core.heat_chamber.rate",
                HeatChamberMachine.formatHeatingRate(rate)));
        tooltip.add(rateLine);
    }
}
