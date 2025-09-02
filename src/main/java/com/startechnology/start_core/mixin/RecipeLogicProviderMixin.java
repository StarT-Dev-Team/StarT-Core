package com.startechnology.start_core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.integration.jade.provider.RecipeLogicProvider;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.machine.steam.SimpleSteamMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.client.util.TooltipHelper;
import com.gregtechceu.gtceu.common.machine.multiblock.steam.SteamParallelMultiblockMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.startechnology.start_core.machine.steam_hatches.StarTSteamParallelMultiblockMachine;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

@Mixin(value=RecipeLogicProvider.class, remap=false)
public class RecipeLogicProviderMixin {
    
    @Overwrite
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, Player player, BlockAccessor block,
                              BlockEntity blockEntity, IPluginConfig config) {
        if (capData.getBoolean("Working")) {
            var recipeInfo = capData.getCompound("Recipe");
            if (!recipeInfo.isEmpty()) {
                var EUt = recipeInfo.getLong("EUt");
                var isInput = recipeInfo.getBoolean("isInput");
                boolean isSteam = false;

                if (blockEntity instanceof MetaMachineBlockEntity mbe) {
                    var machine = mbe.getMetaMachine();
                    if (machine instanceof SimpleSteamMachine ssm) {
                        EUt = (long) (EUt * ssm.getConversionRate());
                        isSteam = true;
                    } else if (machine instanceof SteamParallelMultiblockMachine smb) {
                        EUt = (long) (EUt * smb.getConversionRate());
                        isSteam = true;
                    } else if (machine instanceof StarTSteamParallelMultiblockMachine smb) {
                        EUt = (long) (EUt * smb.getConversionRate());
                        isSteam = true;
                    }
                }

                if (EUt > 0) {
                    MutableComponent text;

                    if (isSteam) {
                        text = Component.literal(FormattingUtil.formatNumbers(EUt)).withStyle(ChatFormatting.GREEN)
                                .append(Component.literal(" mB/t").withStyle(ChatFormatting.RESET));
                    } else {
                        var tier = GTUtil.getOCTierByVoltage(EUt);

                        text = Component.literal(FormattingUtil.formatNumbers(EUt)).withStyle(ChatFormatting.RED)
                                .append(Component.literal(" EU/t").withStyle(ChatFormatting.RESET)
                                        .append(Component.literal(" (").withStyle(ChatFormatting.GREEN)));
                        if (tier < GTValues.TIER_COUNT) {
                            text = text.append(Component.literal(GTValues.VNF[tier])
                                    .withStyle(style -> style.withColor(GTValues.VC[tier])));
                        } else {
                            int speed = tier - 14;
                            text = text.append(Component
                                    .literal("MAX")
                                    .withStyle(style -> style.withColor(TooltipHelper.rainbowColor(speed)))
                                    .append(Component.literal("+")
                                            .withStyle(style -> style.withColor(GTValues.VC[speed]))
                                            .append(Component.literal(FormattingUtil.formatNumbers(tier - 14)))
                                            .withStyle(style -> style.withColor(GTValues.VC[speed]))));

                        }
                        text = text.append(Component.literal(")").withStyle(ChatFormatting.GREEN));
                    }

                    if (isInput) {
                        tooltip.add(Component.translatable("gtceu.top.energy_consumption").append(" ").append(text));
                    } else {
                        tooltip.add(Component.translatable("gtceu.top.energy_production").append(" ").append(text));
                    }
                }
            }
        }
    }

}
