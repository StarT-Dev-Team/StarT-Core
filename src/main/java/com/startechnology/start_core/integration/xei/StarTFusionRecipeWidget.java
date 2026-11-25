package com.startechnology.start_core.integration.xei;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;
import com.gregtechceu.gtceu.data.lang.LangHandler;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.FusionReactorMachine;
import com.gregtechceu.gtceu.integration.xei.widgets.GTRecipeWidget;
import com.lowdragmc.lowdraglib2.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.startechnology.start_core.api.StarTAPI;
import com.startechnology.start_core.block.FusionReflectorBlock;
import org.lwjgl.glfw.GLFW;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import dev.vfyjxf.taffy.style.TaffyPosition;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StarTFusionRecipeWidget extends GTRecipeWidget {

    private final List<FusionReflectorBlock> validReflectors;
    private int reflectorIndex = 0;
    private Button reflectorButton;
    private Label recipeReflectorText;

    public StarTFusionRecipeWidget(GTRecipe recipe) {
        super(recipe);

        validReflectors = recipe.data.contains("reflector_tier") ? StarTAPI.FUSION_REFLECTORS.entrySet().stream()
                .filter(entry -> entry.getKey().getTier() >= recipe.data.getInt("reflector_tier"))
                .sorted(Comparator.comparingInt(entry -> entry.getKey().getTier()))
                .map(entry -> entry.getValue().get())
                .toList() : List.of();

        if (voltageTextWidget != null && !validReflectors.isEmpty()) {
            reflectorButton = new Button().noText();
            reflectorButton.layout(layout -> layout.positionType(TaffyPosition.ABSOLUTE).left(getReflectorXOffset())
                    .top(getVoltageTextY() - 6).width(16).height(16).paddingAll(0));
            reflectorButton.setOnClick(event -> setRecipeReflector(event.button));

            addChild(reflectorButton);
            updateOC(false);
        }
    }

    private int getReflectorXOffset() {
        return getVoltageXOffset(getTier(), getWidgetWidth()) - getXOffset() - 24;
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

        var reflectorX = getReflectorXOffset();
        var reflectorTexture = new ItemStackTexture(new ItemStack(reflector));
        reflectorButton.layout(layout -> layout.left(reflectorX));
        reflectorButton.buttonStyle(style -> style.baseTexture(reflectorTexture).hoverTexture(reflectorTexture)
                .pressedTexture(reflectorTexture));

        var minReflector = validReflectors.get(0);

        List<Component> tooltips = new ArrayList<>(LangHandler.getMultiLang("start_core.recipe.reflector.min",
                minReflector.getName(), getTierText(minReflector), reflector.getName(), getTierText(reflector)));

        tooltips.addAll(LangHandler.getMultiLang("start_core.recipe.reflector.tooltip"));
        setTooltips(reflectorButton, tooltips);
    }

    private static Component getTierText(FusionReflectorBlock reflector) {
        return Component.literal("T" + reflector.reflectorType.getTier()).withStyle(ChatFormatting.AQUA);
    }

    public void setRecipeReflectorText(Label text) {
        recipeReflectorText = text;

        updateRecipeReflectorText();
    }

    private void updateRecipeReflectorText() {
        if (recipeReflectorText == null || validReflectors == null || validReflectors.isEmpty()) return;

        recipeReflectorText.setText(Component.translatable("start_core.recipe.reflector.tier",
                Component.literal("T" + validReflectors.get(reflectorIndex).reflectorType.getTier())
                        .withStyle(ChatFormatting.AQUA),
                Component.literal("T" + recipe.data.getInt("reflector_tier")).withStyle(ChatFormatting.AQUA)));
    }
}
