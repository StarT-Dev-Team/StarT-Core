package com.startechnology.start_core.recipe.logic;

import com.google.common.collect.Streams;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.integration.xei.handlers.item.CycleItemStackHandler;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.mojang.datafixers.util.Function3;
import com.startechnology.start_core.block.arboreal_extractor.ArborealBlocks;
import com.startechnology.start_core.block.arboreal_extractor.TreeType;
import com.startechnology.start_core.machine.StarTMachineUtils;
import com.startechnology.start_core.machine.arboreal_extractor.ArborealExtractorMachine;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ArborealExtractorRecipeLogic implements GTRecipeType.ICustomRecipeLogic {

    // Ordered from weakest to strongest
    private static final List<Supplier<Item>> FERTILIZERS = List.of(
            StarTMachineUtils.lazyItem("minecraft:bone_meal"),
            StarTMachineUtils.lazyItem("thermal:compost"),
            StarTMachineUtils.lazyItem("gtceu:fertilizer"));

    private static final Supplier<Item> IRON_SCREW = StarTMachineUtils.lazyItem("gtceu:iron_screw");
    private static final Supplier<Item> WOOD_SCREW = StarTMachineUtils.lazyItem("gtceu:wood_screw");

    private static final int BASE_OUTPUT = 100;
    private static final int FERTILIZER_BUFF = 50;

    private static boolean isScrew(ItemStack itemStack) {
        return itemStack.is(IRON_SCREW.get()) || itemStack.is(WOOD_SCREW.get());
    }

    private GTRecipe makeRecipe(int fertilizerIndex, TreeType treeType) {
        var fluidOutput = BASE_OUTPUT + fertilizerIndex * FERTILIZER_BUFF;
        var fertilizer = fertilizerIndex > 0 ? FERTILIZERS.get(fertilizerIndex - 1).get() : null;

        var recipeId = treeType.name();
        if (fertilizer != null) {
            recipeId += "_" + StarTMachineUtils.getItemId(fertilizer).getPath();
        }
        var builder = StarTRecipeTypes.ARBOREAL_EXTRACTOR_RECIPES
                .recipeBuilder(recipeId)
                .duration(400)
                .outputFluids(new FluidStack(treeType.getFluid().get(), fluidOutput))
                .addData("treeType", treeType.getName());

        if (fertilizer != null) {
            builder.notConsumable(IRON_SCREW.get());
            builder.chancedInput(new ItemStack(fertilizer, 1), 2500, 0);
        } else {
            builder.notConsumable(WOOD_SCREW.get());
        }

        return builder.buildRawRecipe();
    }

    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        if (!(holder instanceof ArborealExtractorMachine arborealExtractor)) {
            return null;
        }

        var items = holder.getCapabilitiesFlat(IO.IN, ItemRecipeCapability.CAP).stream()
                .flatMap(handler -> handler.getContents().stream().filter(ItemStack.class::isInstance)
                        .map(ItemStack.class::cast))
                .toList();

        if (items.stream().noneMatch(ArborealExtractorRecipeLogic::isScrew))
            return null;

        for (var i = 0; i < FERTILIZERS.size(); ++i) {
            var fertilizer = FERTILIZERS.get(i).get();
            var itemStack = items.stream().filter(s -> s.is(fertilizer)).findFirst().orElse(null);
            if (itemStack != null) {
                return makeRecipe(i + 1, arborealExtractor.getTreeType());
            }
        }
        return makeRecipe(0, arborealExtractor.getTreeType());
    }

    @Override
    public void buildRepresentativeRecipes() {
        for (var i = 0; i < FERTILIZERS.size() + 1; ++i) {
            for (var treeType : TreeType.values()) {
                var recipe = makeRecipe(i, treeType);
                recipe.setId(recipe.getId().withPrefix("/"));
                StarTRecipeTypes.ARBOREAL_EXTRACTOR_RECIPES.addToMainCategory(recipe);
            }
        }
    }

    public static GTRecipeType.CustomDataInfoResult getDataInfo(GTRecipeType.CustomDataInfoConfiguration config) {
        var rawTreeType = config.recipe().data.getString("treeType");
        var treeType = TreeType.of(rawTreeType);

        if (treeType == null) {
            return new GTRecipeType.CustomDataInfoResult(Component.empty(), null);
        }

        var tooltips = Streams.concat(
                Stream.of(Component.translatable("recipe.arboreal_extractor.tree_definition.tooltip")
                        .withStyle(ChatFormatting.GREEN)),
                ArborealBlocks.TREES.stream()
                        .filter(tree -> treeType == tree.getTreeType())
                        .map(tree -> (Component) Component.literal("- ").append(tree.getTranslatedName())))
                .toList();

        var info = Component.translatable("recipe.arboreal_extractor.tree_type.tooltip",
                treeType.getTranslatedName().copy().withStyle(ChatFormatting.GOLD));

        return new GTRecipeType.CustomDataInfoResult(info, (label, widget) -> {
            label.setHoverTooltips(tooltips);
        });
    }

    public static void uiBuilder(GTRecipe recipe, WidgetGroup widgetGroup) {
        var rawTreeType = recipe.data.getString("treeType");
        var treeType = TreeType.of(rawTreeType);
        if (treeType == null) return;

        var treeTypes = ArborealBlocks.TREES.stream().filter(tree -> treeType == tree.getTreeType()).toList();

        var stackHandler = new CycleItemStackHandler(List.of(
                treeTypes.stream().map(tree -> new ItemStack(tree.getLog().get())).toList(),
                treeTypes.stream().map(tree -> new ItemStack(tree.getLeaves().get())).toList()));

        var centerX = widgetGroup.getSize().width - 25;
        var centerY = widgetGroup.getSize().height - 40;

        Function3<Integer, Integer, Integer, Widget> makeSlot = (slot, x,
                                                                 y) -> {
            var slotWidget = new SlotWidget(stackHandler, slot, centerX + x,
                    centerY + y, false, false);
            slotWidget.setDrawHoverOverlay(false);
            slotWidget.setBackground(IGuiTexture.EMPTY);
            return slotWidget;
        };

        widgetGroup.addWidget(makeSlot.apply(1, -6, -3));
        widgetGroup.addWidget(makeSlot.apply(1, 6, -3));
        widgetGroup.addWidget(makeSlot.apply(0, 0, 16));
        widgetGroup.addWidget(makeSlot.apply(0, 0, 8));
        widgetGroup.addWidget(makeSlot.apply(0, 0, 0));
        widgetGroup.addWidget(makeSlot.apply(1, 0, -8));
        widgetGroup.addWidget(makeSlot.apply(1, -6, 3));
        widgetGroup.addWidget(makeSlot.apply(1, 6, 3));
    }
}
