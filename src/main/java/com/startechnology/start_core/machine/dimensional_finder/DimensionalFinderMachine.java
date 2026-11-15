package com.startechnology.start_core.machine.dimensional_finder;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.research.OpticalComputationMachine;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.startechnology.start_core.StarTConfig;
import com.startechnology.start_core.api.dimensional_finder.DimensionalFinderDimension;
import com.startechnology.start_core.data.dimensional_finder.StarTDimensionalFinderData;
import com.startechnology.start_core.item.components.StarTCoordinateCrystalBehaviour;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class DimensionalFinderMachine extends OpticalComputationMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            DimensionalFinderMachine.class,
            OpticalComputationMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @DescSynced
    @Getter
    private int rewindSteps = 1;

    @Persisted
    @DescSynced
    @Getter
    private boolean activeRun = false;

    @Persisted
    @DescSynced
    @Getter
    private String activeDimensionId = "";

    @Persisted
    @DescSynced
    @Getter
    private int[] currentPath = new int[0];

    @Persisted
    @DescSynced
    @Getter
    private int prevCoord = -1;

    @Persisted
    @DescSynced
    @Getter
    private int excludedCoord = -1;

    @Persisted
    @DescSynced
    @Getter
    private boolean identifyingShortest = false;

    @Persisted
    private String activeDifficultyName = "";

    public @Nullable StarTConfig.DifficultyPreset getActiveDifficulty() {
        if (activeDifficultyName == null || activeDifficultyName.isEmpty()) return null;

        try {
            return StarTConfig.DifficultyPreset.valueOf(activeDifficultyName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Persisted
    @DescSynced
    @Getter
    private int currentCycle = 0;

    @Persisted
    private int pendingNextCoord = -1;

    @Persisted
    private boolean pendingTerminal = false;

    @DescSynced
    @Setter
    private boolean rewindInvalid = false;

    @DescSynced
    @Getter
    @Setter
    private int rewindInvalidPathLen = 0;

    public DimensionalFinderMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    protected @NotNull RecipeLogic createRecipeLogic(Object... args) {
        return new DimensionalFinderRecipeLogic(this);
    }

    @Override
    protected @NotNull GTRecipe getRealRecipe(@NotNull GTRecipe recipe) {
        return recipe;
    }

    // @Override
    // public boolean keepSubscribing() {
    // return true;
    // }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        this.resetRunState();
    }

    public void resetRunState() {
        this.activeRun = false;
        this.activeDimensionId = "";
        this.currentPath = new int[0];
        this.prevCoord = -1;
        this.excludedCoord = -1;
        this.identifyingShortest = false;
        this.currentCycle = 0;
        this.activeDifficultyName = "";
        this.pendingNextCoord = -1;
        this.pendingTerminal = false;
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        if (!super.beforeWorking(recipe)) return false;

        if (recipe != null && recipe.data != null && recipe.data.getBoolean("df_custom")) {
            this.rewindInvalid = false;
            if (!this.activeRun) {
                this.activeRun = true;
                this.activeDimensionId = recipe.data.getString("df_dim");
                this.currentPath = recipe.data.getIntArray("df_initial_path");
                this.prevCoord = recipe.data.getInt("df_initial_prev");
                this.excludedCoord = recipe.data.getInt("df_initial_excl");
                this.identifyingShortest = false;
                this.currentCycle = 1;
                this.activeDifficultyName = recipe.data.getString("df_difficulty");
            }
            this.pendingNextCoord = recipe.data.getInt("df_next_coord");
            this.pendingTerminal = recipe.data.getBoolean("df_terminal");
        }

        return true;
    }

    @Override
    public void afterWorking() {
        super.afterWorking();

        if (this.activeRun) {
            if (this.pendingTerminal) {
                resetRunState();
            } else {
                if (this.pendingNextCoord >= 0) {
                    int lastCoord = (this.currentPath.length > 0) ? this.currentPath[this.currentPath.length - 1] : -1;
                    this.prevCoord = lastCoord;
                    this.excludedCoord = -1;

                    int[] nextPath = Arrays.copyOf(this.currentPath, this.currentPath.length + 1);
                    nextPath[nextPath.length - 1] = this.pendingNextCoord;
                    this.currentPath = nextPath;

                    DimensionalFinderDimension dimension = StarTDimensionalFinderData
                            .getDimension(this.activeDimensionId);
                    StarTConfig.DifficultyPreset preset = getActiveDifficulty();

                    if (preset == null) preset = DimensionalFinderDimension.getCurrentPreset();
                    if (dimension != null && dimension.getTree(preset).getRoot().getCoord() == this.pendingNextCoord) {
                        this.identifyingShortest = true;
                    }
                }
                this.currentCycle++;
            }
        }

        this.getRecipeLogic().markLastRecipeDirty();
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;

        MutableComponent rewindLine = Component.translatable("ui.start_core.dimensional_finder.rewind")
                .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.translatable("ui.start_core.dimensional_finder.rewind.hover"))));
        rewindLine.append(ComponentPanelWidget.withButton(
                Component.literal("[-]").withStyle(ChatFormatting.YELLOW), "rewind_dec"));
        rewindLine.append(" ");
        rewindLine.append(Component.literal(Integer.toString(this.rewindSteps)).withStyle(ChatFormatting.AQUA));
        rewindLine.append(" ");
        rewindLine.append(ComponentPanelWidget.withButton(
                Component.literal("[+]").withStyle(ChatFormatting.YELLOW), "rewind_inc"));
        textList.add(rewindLine);

        if (!this.activeRun && this.rewindInvalid) {
            textList.add(Component.translatable("ui.start_core.dimensional_finder.rewind_error",
                    this.rewindSteps, this.rewindInvalidPathLen).withStyle(ChatFormatting.RED));
        }

        if (this.activeRun) {
            textList.add(Component.translatable("ui.start_core.dimensional_finder.target",
                    StarTCoordinateCrystalBehaviour.getDimensionDisplayName(this.activeDimensionId)
                            .withStyle(ChatFormatting.GOLD)));

            if (this.identifyingShortest) {
                textList.add(Component.translatable("ui.start_core.dimensional_finder.phase.shortest")
                        .withStyle(ChatFormatting.LIGHT_PURPLE));
            } else {
                textList.add(Component.translatable("ui.start_core.dimensional_finder.phase.navigating",
                        this.currentCycle).withStyle(ChatFormatting.GREEN));
            }

            textList.add(Component.translatable("ui.start_core.dimensional_finder.current_path",
                    Component.literal(StarTCoordinateCrystalBehaviour.formatCoordinates(this.currentPath))
                            .withStyle(ChatFormatting.AQUA)));
        }
    }

    @Override
    public void handleDisplayClick(String componentData, ClickData clickData) {
        super.handleDisplayClick(componentData, clickData);

        if (!clickData.isRemote) {
            if ("rewind_dec".equals(componentData)) {
                this.rewindSteps = Math.max(1, this.rewindSteps - 1);
                this.rewindInvalid = false;
                this.getRecipeLogic().markLastRecipeDirty();
            } else if ("rewind_inc".equals(componentData)) {
                this.rewindSteps = Math.min(64, this.rewindSteps + 1);
                this.rewindInvalid = false;
                this.getRecipeLogic().markLastRecipeDirty();
            }
        }
    }

    public static class DimensionalFinderRecipeLogic extends RecipeLogic {

        public DimensionalFinderRecipeLogic(DimensionalFinderMachine machine) {
            super(machine);
        }

        @Override
        public void onRecipeFinish() {
            super.onRecipeFinish();
            if (getMachine() instanceof DimensionalFinderMachine finder && finder.isActiveRun() && isIdle()) {
                this.findAndHandleRecipe();
            }
        }
    }
}
