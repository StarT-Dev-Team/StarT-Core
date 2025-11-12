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
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
import com.mojang.serialization.JsonOps;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.capability.StarTCapabilityHelper;
import com.startechnology.start_core.machine.threading.StarTThreadingCapableMachine;

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

public class StarTThreadedRecipeProvider extends CapabilityBlockProvider<StarTThreadingCapableMachine> {
    public StarTThreadedRecipeProvider() {
        super(StarTCore.resourceLocation("threading_recipes"));
    }

    @Override
    protected @Nullable StarTThreadingCapableMachine getCapability(Level level, BlockPos pos,
            @Nullable Direction side) {
        var capability = StarTCapabilityHelper.getThreadingCapableMachine(level, pos, side);

        if (capability != null)
            return capability;

        return null;
    }

    @Override
    protected void write(CompoundTag data, StarTThreadingCapableMachine capability) {
        int threadIndex = 0;
        for (var threadExecutionContext : capability.getActiveThreads()) {
            GTRecipe recipe = threadExecutionContext.recipe;
            String threadPrefix = "thread_" + threadIndex;

            // TODO: Changed in GTM 7
            if (recipe != null) {
                int recipeTier = RecipeHelper.getPreOCRecipeEuTier(recipe);
                int chanceTier = recipeTier + recipe.ocLevel;
                var function = recipe.getType().getChanceFunction();
                var itemContents = recipe.getOutputContents(ItemRecipeCapability.CAP);
                var fluidContents = recipe.getOutputContents(FluidRecipeCapability.CAP);

                ListTag itemTags = new ListTag();
                for (var item : itemContents) {
                    var stacks = ItemRecipeCapability.CAP.of(item.content).getItems();
                    if (stacks.length == 0)
                        continue;
                    if (stacks[0].isEmpty())
                        continue;
                    var stack = stacks[0];

                    var itemTag = new CompoundTag();
                    GTUtil.saveItemStack(stack, itemTag);
                    if (item.chance < item.maxChance) {
                        int count = stack.getCount();
                        double countD = (double) count * recipe.parallels *
                                function.getBoostedChance(item, recipeTier, chanceTier) / item.maxChance;
                        count = countD < 1 ? 1 : (int) Math.round(countD);
                        itemTag.putInt("Count", count);
                    }
                    itemTags.add(itemTag);
                }

                if (!itemTags.isEmpty()) {
                    data.put(threadPrefix + "OutputItems", itemTags);
                }

                ListTag fluidTags = new ListTag();
                for (var fluid : fluidContents) {
                    var stacks = FluidRecipeCapability.CAP.of(fluid.content).getStacks();
                    if (stacks.length == 0)
                        continue;
                    if (stacks[0].isEmpty())
                        continue;
                    var stack = stacks[0];

                    var fluidTag = new CompoundTag();
                    stack.writeToNBT(fluidTag);
                    if (fluid.chance < fluid.maxChance) {
                        int amount = stack.getAmount();
                        double amountD = (double) amount * recipe.parallels *
                                function.getBoostedChance(fluid, recipeTier, chanceTier) / fluid.maxChance;
                        amount = amountD < 1 ? 1 : (int) Math.round(amountD);
                        fluidTag.putInt("Amount", amount);
                    }
                    fluidTags.add(fluidTag);
                }

                if (!fluidTags.isEmpty()) {
                    data.put(threadPrefix + "OutputFluids", fluidTags);
                }

                var EUt = RecipeHelper.getInputEUt(recipe);
                var isInput = true;
                if (EUt == 0) {
                    isInput = false;
                    EUt = RecipeHelper.getOutputEUt(recipe);
                }

                data.putLong(threadPrefix + "EUt", EUt);
                data.putBoolean(threadPrefix + "isInput", isInput);
                data.putBoolean(threadPrefix + "isWorking", threadExecutionContext.isWorking);

                data.putInt(threadPrefix + "Progress",
                        threadExecutionContext.totalDuration - threadExecutionContext.ticksRemaining);
                data.putInt(threadPrefix + "MaxProgress", threadExecutionContext.totalDuration);
                threadIndex++;
            }
        }

        data.putInt("thread_amount", threadIndex);
    }

    @Override
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, Player player, BlockAccessor block,
            BlockEntity blockEntity, IPluginConfig config) {

        int threadAmount = capData.getInt("thread_amount");
        for (int i = 0; i < threadAmount; i++) {
            tooltip.add(Component
                    .literal(LocalizationUtils.format("start_core.machine.threading_controller.jade_thread_header",
                            FormattingUtil.formatNumbers(i + 1))));
            String threadPrefix = "thread_" + i;

            // WORKABLE PROVIDER
            int currentProgress = capData.getInt(threadPrefix + "Progress");
            int maxProgress = capData.getInt(threadPrefix + "MaxProgress");
            MutableComponent text;

            if (maxProgress < 20) {
                text = Component.translatable("gtceu.jade.progress_tick", currentProgress, maxProgress);
            } else {
                text = Component.translatable("gtceu.jade.progress_sec", Math.round(currentProgress / 20.0F),
                        Math.round(maxProgress / 20.0F));
            }

            if (maxProgress > 0) {
                int color = generateThreadColor(i);
                tooltip.add(
                        tooltip.getElementHelper().progress(
                                getProgress(currentProgress, maxProgress),
                                text,
                                tooltip.getElementHelper().progressStyle().color(color).textColor(-1),
                                Util.make(BoxStyle.DEFAULT, style -> style.borderColor = 0xFF555555),
                                true));
            }

            /* EU/T Display */
            var EUt = capData.getLong(threadPrefix + "EUt");
            var isWorking = capData.getBoolean(threadPrefix + "isWorking");
            var isInput = capData.getBoolean(threadPrefix + "isInput");
                boolean isSteam = false;
                if (blockEntity instanceof MetaMachineBlockEntity mbe) {
                    var machine = mbe.getMetaMachine();
                    if (machine instanceof SimpleSteamMachine ssm) {
                        EUt = (long) (EUt * ssm.getConversionRate());
                        isSteam = true;
                    } else if (machine instanceof SteamParallelMultiblockMachine smb) {
                        EUt = (long) (EUt * smb.getConversionRate());
                        isSteam = true;
                    }
                }

            if (EUt > 0 && isWorking) {

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

            List<ItemStack> outputItems = new ArrayList<>();
            if (capData.contains(threadPrefix + "OutputItems", Tag.TAG_LIST)) {
                ListTag itemTags = capData.getList(threadPrefix + "OutputItems", Tag.TAG_COMPOUND);
                if (!itemTags.isEmpty()) {
                    for (Tag tag : itemTags) {
                        if (tag instanceof CompoundTag tCompoundTag) {
                            var stack = GTUtil.loadItemStack(tCompoundTag);
                            if (!stack.isEmpty()) {
                                outputItems.add(stack);
                            }
                        }
                    }
                }
            }
            List<FluidStack> outputFluids = new ArrayList<>();
            if (capData.contains(threadPrefix + "OutputFluids", Tag.TAG_LIST)) {
                ListTag fluidTags = capData.getList(threadPrefix + "OutputFluids", Tag.TAG_COMPOUND);
                for (Tag tag : fluidTags) {
                    if (tag instanceof CompoundTag tCompoundTag) {
                        var stack = FluidStack.loadFluidStackFromNBT(tCompoundTag);
                        if (!stack.isEmpty()) {
                            outputFluids.add(stack);
                        }
                    }
                }
            }
            if (!outputItems.isEmpty() || !outputFluids.isEmpty()) {
                tooltip.add(Component.translatable("gtceu.top.recipe_output"));
            }
            addItemTooltips(tooltip, outputItems);
            addFluidTooltips(tooltip, outputFluids);
        }
    }

    private void addItemTooltips(ITooltip iTooltip, List<ItemStack> outputItems) {
        IElementHelper helper = iTooltip.getElementHelper();
        for (ItemStack itemOutput : outputItems) {
            if (itemOutput != null && !itemOutput.isEmpty()) {
                int count = itemOutput.getCount();
                itemOutput.setCount(1);
                iTooltip.add(helper.smallItem(itemOutput));
                Component text = Component.literal(" ")
                        .append(String.valueOf(count))
                        .append("× ")
                        .append(getItemName(itemOutput))
                        .withStyle(ChatFormatting.WHITE);
                iTooltip.append(text);
            }
        }
    }

    private void addFluidTooltips(ITooltip iTooltip, List<FluidStack> outputFluids) {
        for (FluidStack fluidOutput : outputFluids) {
            if (fluidOutput != null && !fluidOutput.isEmpty()) {
                iTooltip.add(GTElementHelper.smallFluid(getFluid(fluidOutput)));
                Component text = Component.literal(" ")
                        .append(FluidTextHelper.getUnicodeMillibuckets(fluidOutput.getAmount(), true))
                        .append(" ")
                        .append(getFluidName(fluidOutput))
                        .withStyle(ChatFormatting.WHITE);
                iTooltip.append(text);
            }
        }
    }

    private Component getItemName(ItemStack stack) {
        return ComponentUtils.wrapInSquareBrackets(stack.getItem().getDescription()).withStyle(ChatFormatting.WHITE);
    }

    private Component getFluidName(FluidStack stack) {
        return ComponentUtils.wrapInSquareBrackets(stack.getDisplayName()).withStyle(ChatFormatting.WHITE);
    }

    private JadeFluidObject getFluid(FluidStack stack) {
        return JadeFluidObject.of(stack.getFluid(), stack.getAmount());
    }

    /**
     * Generate a vibrant color for a thread based on its index
     * 
     * @param threadIndex The zero-based thread index
     * @return ARGB color integer (0xAARRGGBB format)
     */
    private static int generateThreadColor(int threadIndex) {
        float goldenRatio = 0.618033988749895f;
        float hue = ((threadIndex + 1) * goldenRatio) % 1.0f;

        float saturation = 0.85f;
        float value = 0.85f;

        int rgb = hsvToRgb(hue, saturation, value);

        // Add alpha channel
        return 0xFF000000 | rgb;
    }

    /**
     * Convert HSV color to RGB
     * 
     * @param h Hue [0.0, 1.0]
     * @param s Saturation [0.0, 1.0]
     * @param v Value [0.0, 1.0]
     * @return RGB color as integer (without alpha)
     */
    private static int hsvToRgb(float h, float s, float v) {
        int hi = (int) (h * 6);
        float f = h * 6 - hi;
        float p = v * (1 - s);
        float q = v * (1 - f * s);
        float t = v * (1 - (1 - f) * s);

        float r, g, b;
        switch (hi % 6) {
            case 0:
                r = v;
                g = t;
                b = p;
                break;
            case 1:
                r = q;
                g = v;
                b = p;
                break;
            case 2:
                r = p;
                g = v;
                b = t;
                break;
            case 3:
                r = p;
                g = q;
                b = v;
                break;
            case 4:
                r = t;
                g = p;
                b = v;
                break;
            case 5:
                r = v;
                g = p;
                b = q;
                break;
            default:
                r = g = b = 0;
                break;
        }

        int red = (int) (r * 255);
        int green = (int) (g * 255);
        int blue = (int) (b * 255);

        return (red << 16) | (green << 8) | blue;
    }
}
