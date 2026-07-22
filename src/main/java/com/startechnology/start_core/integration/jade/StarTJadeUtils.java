package com.startechnology.start_core.integration.jade;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.steam.SimpleSteamMachine;
import com.gregtechceu.gtceu.client.util.TooltipHelper;
import com.gregtechceu.gtceu.common.machine.multiblock.steam.SteamParallelMultiblockMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.gregtechceu.gtceu.utils.GTUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;

public class StarTJadeUtils {

    public static boolean hasData(CompoundTag tag, String... keys) {
        for (String key : keys) {
            if (!tag.contains(key))
                return false;
        }
        return true;
    }

    public static MutableComponent euTDisplay(long EUt, BlockEntity blockEntity) {
        MutableComponent text = Component.empty();
        boolean isSteam = false;

        if (EUt > 0) {
            if (blockEntity instanceof MetaMachineBlockEntity mbe) {
                var machine = mbe.getMetaMachine();
                if (machine instanceof SimpleSteamMachine ssm) {
                    EUt = (long) Math.ceil(EUt * ssm.getConversionRate());
                    isSteam = true;
                } else if (machine instanceof SteamParallelMultiblockMachine smb) {
                    EUt = (long) Math.ceil(EUt * smb.getConversionRate());
                    isSteam = true;
                }
            }

            if (isSteam) {
                text = Component.translatable("gtceu.jade.fluid_use", FormattingUtil.formatNumbers(EUt))
                        .withStyle(ChatFormatting.GREEN);
            } else {
                var tier = GTUtil.getTierByVoltage(EUt);
                float minAmperage = (float) EUt / GTValues.V[tier];

                text = Component
                        .translatable("gtceu.recipe.eu.total",
                                FormattingUtil.formatNumbers(EUt))
                        .withStyle(ChatFormatting.RED);

                MutableComponent voltageTier;
                if (tier < GTValues.TIER_COUNT - 1) {
                    voltageTier = Component.literal(GTValues.VNF[tier])
                            .withStyle(style -> style.withColor(GTValues.VC[tier]));
                } else {
                    int calculatedSpeed = Mth
                            .ceil(Math.log((double) EUt / GTValues.V[GTValues.MAX]) / Math.log(4));
                    int speed = Mth.clamp(calculatedSpeed, 0, GTValues.TIER_COUNT);
                    if (speed == 0) {
                        voltageTier = Component.literal(GTValues.VNF[tier])
                                .withStyle(style -> style.withColor(GTValues.VC[tier]));
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
            }

        }
        return text;
    }

}
