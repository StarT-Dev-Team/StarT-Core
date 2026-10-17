package com.startechnology.start_core.integration.xei;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;
import com.gregtechceu.gtceu.data.lang.LangHandler;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.FusionReactorMachine;
import com.gregtechceu.gtceu.integration.xei.widgets.GTRecipeWidget;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.startechnology.start_core.api.StarTAPI;
import com.startechnology.start_core.block.FusionReflectorBlock;
import org.lwjgl.glfw.GLFW;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StarTFusionRecipeWidget extends GTRecipeWidget {

    private final List<FusionReflectorBlock> validReflectors;
    private int reflectorIndex = 0;
    private ButtonWidget reflectorButton;
    private LabelWidget recipeReflectorText;

    public StarTFusionRecipeWidget(GTRecipe recipe) {
        super(recipe);

        validReflectors = recipe.data.contains("reflector_tier") ? StarTAPI.FUSION_REFLECTORS.entrySet().stream()
                .filter(entry -> entry.getKey().getTier() >= recipe.data.getInt("reflector_tier"))
                .sorted(Comparator.comparingInt(entry -> entry.getKey().getTier()))
                .map(entry -> entry.getValue().get())
                .toList() : List.of();

        if (voltageTextWidget != null && !validReflectors.isEmpty()) {
            reflectorButton = new ButtonWidget(getReflectorXOffset(), voltageTextWidget.getSelfPositionY() - 6, 16, 16,
                    cd -> setRecipeReflector(cd.button));

            addWidget(reflectorButton);
            updateOC(false);
        }
    }

    private int getReflectorXOffset() {
        return getVoltageXOffset(getTier(), getSize().width) - getXOffset() - 24;
    }

    private void setRecipeReflector(int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            reflectorIndex++;
        } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            reflectorIndex--;
        } else if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
            reflectorIndex = 0;
        }

        reflectorIndex = Mth.clamp(reflectorIndex, 0, validReflectors.size() - 1);

        updateOC(false);
    }

    private int getReflectorOCs() {
        if (validReflectors == null || validReflectors.isEmpty()) return 0;

        return validReflectors.get(reflectorIndex).reflectorType.getTier() - recipe.data.getInt("reflector_tier");
    }

    @Override
    protected OverclockingLogic createOCLogic(boolean perfect) {
        return FusionReactorMachine.FUSION_OC;
    }

    @Override
    protected int getPerfectOCs(OverclockingLogic.OCResult result) {
        return 0;
    }

    @Override
    protected double getDurationMultiplier() {
        return Math.pow(OverclockingLogic.PERFECT_HALF_DURATION_FACTOR, getReflectorOCs());
    }

    @Override
    protected double getEUtMultiplier() {
        return Math.pow(OverclockingLogic.PERFECT_HALF_VOLTAGE_FACTOR, getReflectorOCs());
    }

    @Override
    protected List<Component> getDurationMultiplierTexts() {
        if (getReflectorOCs() == 0) return List.of();

        long speed = Math.round(100 / getDurationMultiplier());

        return List.of(Component.translatable("start_core.recipe.oc.duration.reflector",
                Component.literal(speed + "%").withStyle(ChatFormatting.GREEN)));
    }

    @Override
    protected List<Component> getEUtMultiplierTexts() {
        if (getReflectorOCs() == 0) return List.of();

        return List.of(Component.translatable("start_core.recipe.oc.usage.reflector",
                Component.literal("x" + Math.round(getEUtMultiplier())).withStyle(ChatFormatting.RED)));
    }

    @Override
    protected void onRecipeOverclockWidgetUpdated() {
        if (reflectorButton == null) return;

        var reflector = validReflectors.get(reflectorIndex);

        reflectorButton.setSelfPositionX(getReflectorXOffset());
        reflectorButton.setButtonTexture(new ItemStackTexture(new ItemStack(reflector)));

        var minReflector = validReflectors.get(0);

        List<Component> tooltips = new ArrayList<>(LangHandler.getMultiLang("start_core.recipe.reflector.min",
                minReflector.getName(), getTierText(minReflector), reflector.getName(), getTierText(reflector)));

        tooltips.addAll(LangHandler.getMultiLang("start_core.recipe.reflector.tooltip"));
        reflectorButton.setHoverTooltips(tooltips.toArray(Component[]::new));
    }

    private static Component getTierText(FusionReflectorBlock reflector) {
        return Component.literal("T" + reflector.reflectorType.getTier()).withStyle(ChatFormatting.AQUA);
    }

    public void setRecipeReflectorText(LabelWidget text) {
        recipeReflectorText = text;

        updateRecipeReflectorText();
    }

    private void updateRecipeReflectorText() {
        if (recipeReflectorText == null || validReflectors == null || validReflectors.isEmpty()) return;

        recipeReflectorText.setComponent(Component.translatable("start_core.recipe.reflector.tier",
                Component.literal("T" + validReflectors.get(reflectorIndex).reflectorType.getTier())
                        .withStyle(ChatFormatting.AQUA),
                Component.literal("T" + recipe.data.getInt("reflector_tier")).withStyle(ChatFormatting.AQUA)));
    }
}
