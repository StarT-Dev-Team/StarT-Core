package com.startechnology.start_core.integration.jade.provider;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.client.util.TooltipHelper;
import com.gregtechceu.gtceu.integration.jade.provider.CapabilityBlockProvider;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.capability.StarTCapabilityHelper;
import com.startechnology.start_core.machine.solar.StarTSolarMachine;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class StarTSolarMachineProvider extends CapabilityBlockProvider<StarTSolarMachine> {
    public StarTSolarMachineProvider() {
        super(StarTCore.resourceLocation("solar_machine_info"));
    }

    @Override
    protected @Nullable StarTSolarMachine getCapability(Level level, BlockPos pos, @Nullable Direction side) {
        return StarTCapabilityHelper.getSolarMachine(level, pos, side);
    }

    @Override
    protected void write(CompoundTag data, StarTSolarMachine capability) {
        data.putInt("euT", capability.getEuT());
        data.putBoolean("formed", capability.isFormed());
        data.putInt("totalCells", capability.getCellAmount());
        data.putInt("brokenCells", capability.getBrokenCells());
    }

    @Override
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, Player player, BlockAccessor block,
                              BlockEntity blockEntity, IPluginConfig config) {
        if (capData.contains("euT") && capData.contains("formed") && capData.contains("totalCells") && capData.contains("brokenCells")) {
            if (!capData.getBoolean("formed")) return;

            int euT = capData.getInt("euT");
            int totalCells = capData.getInt("totalCells");

            if (euT > 0) {
                var tier = GTUtil.getTierByVoltage(euT);
                float minAmperage = (float) euT / GTValues.V[tier];

                MutableComponent text;
                MutableComponent voltageTier;

                text = Component.translatable("gtceu.recipe.eu.total", FormattingUtil.formatNumbers(euT)).withStyle(ChatFormatting.RED);

                if (tier < GTValues.TIER_COUNT - 1) {
                    voltageTier = Component.literal(GTValues.VNF[tier]).withStyle(style -> style.withColor(GTValues.VC[tier]));
                } else {
                    int calculatedSpeed = Mth.ceil(Math.log((double) euT / GTValues.V[GTValues.MAX]) / Math.log(4));
                    int speed = Mth.clamp(calculatedSpeed, 0, GTValues.TIER_COUNT);

                    if (speed == 0) {
                        voltageTier = Component.literal(GTValues.VNF[tier]).withStyle(style -> style.withColor(GTValues.VC[tier]));
                    } else {
                        minAmperage = (float) (minAmperage / Math.pow(4, speed));
                        voltageTier = Component.literal("MAX")
                                .withStyle(style -> style.withColor(TooltipHelper.rainbowColor(speed)))
                                .append(Component.literal("+")
                                        .withStyle(style -> style.withColor(GTValues.VC[speed]))
                                        .append(FormattingUtil.formatNumbers(speed)));
                    }
                }

                text.append(Component.translatable("gtceu.universal.padded_parentheses",
                        (Component.translatable("gtceu.recipe.eu.amp_notation",
                                FormattingUtil.formatNumber2Places(minAmperage),
                                voltageTier))
                                .withStyle(ChatFormatting.WHITE)));

                tooltip.add(2, Component.translatable("gtceu.top.energy_production").append(" ").append(text));
            }

            tooltip.add(Component.translatable("solar.start_core.solar_machine.cell_tooltip", totalCells - capData.getInt("brokenCells"), totalCells));
        }
    }
}
