package com.startechnology.start_core.integration.jade.provider;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.steam.SimpleSteamMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.ingredient.IntProviderIngredient;
import com.gregtechceu.gtceu.client.util.TooltipHelper;
import com.gregtechceu.gtceu.common.machine.multiblock.part.EnergyHatchPartMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.steam.SteamParallelMultiblockMachine;
import com.gregtechceu.gtceu.integration.jade.GTElementHelper;
import com.gregtechceu.gtceu.integration.jade.provider.CapabilityBlockProvider;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.mojang.serialization.JsonOps;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.capability.StarTCapabilityHelper;
import com.startechnology.start_core.machine.threading.StarTThreadingCapableMachine;
import com.startechnology.start_core.machine.threading.StarTThreadingStatBlocks;
import com.startechnology.start_core.machine.threading.StarTThreadingStatBlocks.StarTThreadingStatBlock;
import com.startechnology.start_core.machine.threading.StarTThreadingStatsPredicate.ThreadingStatsBlockTracker;

import appeng.datagen.providers.localization.LocalizationProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.fluid.JadeFluidObject;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.util.FluidTextHelper;
import snownee.jade.api.IBlockComponentProvider;

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
