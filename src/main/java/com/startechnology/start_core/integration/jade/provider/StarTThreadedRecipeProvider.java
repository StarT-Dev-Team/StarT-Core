package com.startechnology.start_core.integration.jade.provider;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.SimpleGeneratorMachine;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.steam.SimpleSteamMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;
import com.gregtechceu.gtceu.api.recipe.ingredient.IntProviderFluidIngredient;
import com.gregtechceu.gtceu.api.recipe.ingredient.IntProviderIngredient;
import com.gregtechceu.gtceu.api.recipe.ingredient.SizedIngredient;
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
import com.google.gson.JsonObject;
import appeng.datagen.providers.localization.LocalizationProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
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

            if (recipe != null) {
                int recipeTier = RecipeHelper.getPreOCRecipeEuTier(recipe);
                int chanceTier = recipeTier + recipe.ocLevel;
                var function = recipe.getType().getChanceFunction();
                var itemContents = recipe.getOutputContents(ItemRecipeCapability.CAP);
                var fluidContents = recipe.getOutputContents(FluidRecipeCapability.CAP);
                int runs = recipe.getTotalRuns();

                ListTag itemTags = new ListTag();
                for (var item : itemContents) {
                    CompoundTag itemTag;
                    if (item.content instanceof IntProviderIngredient provider) {
                        // don't roll for output but do copy for chance and batch
                        IntProviderIngredient chanced = provider;
                        if (item.chance < item.maxChance) {
                            double countD = (double) runs *
                                    function.getBoostedChance(item, recipeTier, chanceTier) / item.maxChance;
                            chanced = (IntProviderIngredient) ItemRecipeCapability.CAP.copyWithModifier(provider,
                                    ContentModifier.multiplier(countD));
                        }
                        itemTag = (CompoundTag) JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, chanced.toJson());
                    } else {
                        var stacks = ItemRecipeCapability.CAP.of(item.content).getItems();
                        if (stacks.length == 0 || stacks[0].isEmpty()) continue;
                        var stack = stacks[0];
                        itemTag = new CompoundTag();
                        GTUtil.saveItemStack(stack, itemTag);
                        if (item.chance < item.maxChance) {
                            int count = stack.getCount();
                            double countD = (double) count * runs *
                                    function.getBoostedChance(item, recipeTier, chanceTier) / item.maxChance;
                            count = Math.max(1, (int) Math.round(countD));
                            itemTag.putInt("Count", count);
                        }
                    }
                    itemTags.add(itemTag);
                }

                if (!itemTags.isEmpty()) {
                    data.put("OutputItems", itemTags);
                }

                ListTag fluidTags = new ListTag();
                for (var fluid : fluidContents) {
                    CompoundTag fluidTag;
                    if (fluid.content instanceof IntProviderFluidIngredient provider) {
                        // don't bother rolling output for nothing
                        IntProviderFluidIngredient chanced = provider;
                        if (fluid.chance < fluid.maxChance) {
                            double countD = (double) runs *
                                    function.getBoostedChance(fluid, recipeTier, chanceTier) / fluid.maxChance;
                            chanced = (IntProviderFluidIngredient) FluidRecipeCapability.CAP.copyWithModifier(provider,
                                    ContentModifier.multiplier(countD));
                        }
                        fluidTag = chanced.toNBT();
                    } else {
                        FluidStack[] stacks = FluidRecipeCapability.CAP.of(fluid.content).getStacks();
                        if (stacks.length == 0) continue;
                        if (stacks[0].isEmpty()) continue;
                        var stack = stacks[0];
                        fluidTag = new CompoundTag();
                        stack.writeToNBT(fluidTag);

                        if (fluid.chance < fluid.maxChance) {
                            int amount = stacks[0].getAmount();
                            double amountD = (double) amount * runs *
                                    function.getBoostedChance(fluid, recipeTier, chanceTier) / fluid.maxChance;
                            amount = Math.max(1, (int) Math.round(amountD));
                            fluidTag.putInt("Amount", amount);
                        }
                    }
                    fluidTags.add(fluidTag);
                }

                if (!fluidTags.isEmpty()) {
                    data.put("OutputFluids", fluidTags);
                }

                var EUt = RecipeHelper.getRealEUtWithIO(recipe);

                data.putLong("EUt", EUt.getTotalEU());
                data.putLong("voltage", getVoltage(capability));
                data.putBoolean("isInput", EUt.isInput());
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
                    .translatable("start_core.machine.threading_controller.jade_thread_header",
                            Component.literal(FormattingUtil.formatNumbers(i + 1)).withStyle(ChatFormatting.GOLD)));
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
            var EUt = capData.getLong("EUt");
            var isInput = capData.getBoolean("isInput");
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
                    var voltage = capData.getLong("voltage");
                    var tier = GTUtil.getTierByVoltage(voltage);
                    float minAmperage = (float) EUt / voltage;

                    text = Component
                            .translatable("gtceu.jade.amperage_use",
                                    FormattingUtil.formatNumber2Places(minAmperage))
                            .withStyle(ChatFormatting.RED)
                            .append(Component.translatable("gtceu.jade.at").withStyle(ChatFormatting.GREEN));
                    if (tier < GTValues.TIER_COUNT) {
                        text = text.append(Component.literal(GTValues.VNF[tier])
                                .withStyle(style -> style.withColor(GTValues.VC[tier])));
                    } else {
                        int speed = Mth.clamp(tier - GTValues.TIER_COUNT - 1, 0, GTValues.TIER_COUNT);
                        text = text.append(Component.literal("MAX")
                                .withStyle(style -> style.withColor(TooltipHelper.rainbowColor(speed)))
                                .append(Component.literal("+")
                                        .withStyle(style -> style.withColor(GTValues.VC[speed]))
                                        .append(FormattingUtil.formatNumbers(speed))));

                    }
                    text.append(Component.translatable("gtceu.universal.padded_parentheses",
                            (Component.translatable("gtceu.recipe.eu.total",
                                    FormattingUtil.formatNumbers(EUt))))
                            .withStyle(ChatFormatting.WHITE));
                }

                if (isInput) {
                    tooltip.add(Component.translatable("gtceu.top.energy_consumption").append(" ").append(text));
                } else {
                    tooltip.add(Component.translatable("gtceu.top.energy_production").append(" ").append(text));
                }
            }

            /* Recipe items add tooltip */
            List<Ingredient> outputItems = new ArrayList<>();
            if (capData.contains("OutputItems", Tag.TAG_LIST)) {
                ListTag itemTags = capData.getList("OutputItems", Tag.TAG_COMPOUND);
                if (!itemTags.isEmpty()) {
                    for (Tag tag : itemTags) {
                        if (tag instanceof CompoundTag tCompoundTag) {
                            if (tCompoundTag.contains("count_provider")) {
                                var ingredient = IntProviderIngredient.SERIALIZER
                                        .parse((JsonObject) NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, tCompoundTag));
                                outputItems.add(ingredient);
                            } else {
                                var stack = GTUtil.loadItemStack(tCompoundTag);
                                if (!stack.isEmpty()) {
                                    outputItems.add(SizedIngredient.create(stack));
                                }
                            }
                        }
                    }
                }
            }
            List<FluidIngredient> outputFluids = new ArrayList<>();
            if (capData.contains("OutputFluids", Tag.TAG_LIST)) {
                ListTag fluidTags = capData.getList("OutputFluids", Tag.TAG_COMPOUND);
                for (Tag tag : fluidTags) {
                    if (tag instanceof CompoundTag tCompoundTag) {
                        if (tCompoundTag.contains("count_provider")) {
                            var ingredient = IntProviderFluidIngredient.fromNBT(tCompoundTag);
                            outputFluids.add(ingredient);
                        } else {
                            var stack = FluidStack.loadFluidStackFromNBT(tCompoundTag);
                            if (!stack.isEmpty()) {
                                outputFluids.add(FluidIngredient.of(stack));
                            }
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

    private void addItemTooltips(ITooltip iTooltip, List<Ingredient> outputItems) {
        IElementHelper helper = iTooltip.getElementHelper();
        for (Ingredient itemOutput : outputItems) {
            if (itemOutput != null && !itemOutput.isEmpty()) {
                ItemStack item;
                MutableComponent text = CommonComponents.space();
                if (itemOutput instanceof IntProviderIngredient provider) {
                    item = provider.getInner().getItems()[0];
                    text = text.append(Component.translatable("gtceu.gui.content.range",
                            String.valueOf(provider.getCountProvider().getMinValue()),
                            String.valueOf(provider.getCountProvider().getMaxValue())));
                } else {
                    item = itemOutput.getItems()[0];
                    text.append(String.valueOf(item.getCount()));
                    item.setCount(1);
                }
                text.append(Component.translatable("gtceu.gui.content.times_item",
                        getItemName(item))
                        .withStyle(ChatFormatting.WHITE));

                iTooltip.add(helper.smallItem(item));
                iTooltip.append(text);
            }
        }
    }


    private void addFluidTooltips(ITooltip iTooltip, List<FluidIngredient> outputFluids) {
        for (FluidIngredient fluidOutput : outputFluids) {
            if (fluidOutput != null && !fluidOutput.isEmpty()) {
                FluidStack stack;
                MutableComponent text = CommonComponents.space();
                if (fluidOutput instanceof IntProviderFluidIngredient provider) {
                    stack = provider.getInner().getStacks()[0];
                    text.append(Component.translatable("gtceu.gui.content.range",
                            FluidTextHelper.getUnicodeMillibuckets(provider.getCountProvider().getMinValue(), true),
                            FluidTextHelper.getUnicodeMillibuckets(provider.getCountProvider().getMaxValue(), true)));
                } else {
                    stack = fluidOutput.getStacks()[0];
                    text.append(FluidTextHelper.getUnicodeMillibuckets(stack.getAmount(), true));
                }
                text.append(CommonComponents.space())
                        .append(getFluidName(stack))
                        .withStyle(ChatFormatting.WHITE);

                iTooltip.add(GTElementHelper.smallFluid(getFluid(stack)));
                iTooltip.append(text);
            }
        }
    }

    public static long getVoltage(MetaMachine machine) {
        long voltage = -1;
        if (machine instanceof SimpleTieredMachine actualMachine) {
            voltage = GTValues.V[actualMachine.getTier()];
        } else if (machine instanceof SimpleGeneratorMachine actualMachine) {
            voltage = GTValues.V[actualMachine.getTier()];
        } else if (machine instanceof WorkableElectricMultiblockMachine actualMachine) {
            voltage = actualMachine.getParts().stream()
                    .filter(EnergyHatchPartMachine.class::isInstance)
                    .map(EnergyHatchPartMachine.class::cast)
                    .mapToLong(dynamo -> GTValues.V[dynamo.getTier()])
                    .max()
                    .orElse(-1);
        }
        // default display as LV, this shouldn't happen because a machine is either electric or steam
        if (voltage == -1) voltage = 32;
        return voltage;
    }

    private Component getItemName(ItemStack stack) {
        return stack.getDisplayName().copy().withStyle(ChatFormatting.WHITE);
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
