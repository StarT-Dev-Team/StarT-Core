package com.startechnology.start_core.api.gui;

import com.lowdragmc.lowdraglib2.gui.texture.SpriteTexture;
import com.startechnology.start_core.StarTCore;

public class StarTGuiTextures {

    // HPCA Component GUI Textures for the grid.
    public static final SpriteTexture NANOFLUIDIC_HEAT_SINK_COMPONENT = SpriteTexture.of(
            StarTCore.resourceLocation("textures/gui/widget/hpca/nanofluidic_heat_sink_component.png"));

    public static final SpriteTexture MODULAR_INTERFACE_MISSING = SpriteTexture.of(
            StarTCore.resourceLocation("textures/gui/missing_modular_link.png"));

    public static final SpriteTexture HPCA_ICON_OPTIMIZED_COMPUTATION_COMPONENT = SpriteTexture.of(
            StarTCore.resourceLocation("textures/gui/widget/hpca/optimized_computation_component.png"));
    public static final SpriteTexture HPCA_ICON_OPTIMIZED_DAMAGED_COMPUTATION_COMPONENT = SpriteTexture.of(
            StarTCore.resourceLocation("textures/gui/widget/hpca/damaged_optimized_computation_component.png"));

    public static final SpriteTexture HPCA_COMPONENT_OUTLINE_5X5 = SpriteTexture.of(
            StarTCore.resourceLocation("textures/gui/component_outline_5x5.png"));
}
