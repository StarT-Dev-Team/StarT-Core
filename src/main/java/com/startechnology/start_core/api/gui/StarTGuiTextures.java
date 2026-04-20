package com.startechnology.start_core.api.gui;

import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.startechnology.start_core.StarTCore;

public class StarTGuiTextures {
    // HPCA Component GUI Textures for the grid.
    public static final ResourceTexture NANOFLUIDIC_HEAT_SINK_COMPONENT = new ResourceTexture(
        StarTCore.resourceLocation("textures/gui/widget/hpca/nanofluidic_heat_sink_component.png")
    );

    public static final ResourceTexture MODULAR_INTERFACE_MISSING = new ResourceTexture(
        StarTCore.resourceLocation("textures/gui/missing_modular_link.png")
    );

    public static final ResourceTexture HPCA_ICON_OPTIMIZED_COMPUTATION_COMPONENT = new ResourceTexture(
        StarTCore.resourceLocation("textures/gui/widget/hpca/optimized_computation_component.png")
    );
    public static final ResourceTexture HPCA_ICON_OPTIMIZED_DAMAGED_COMPUTATION_COMPONENT = new ResourceTexture(
        StarTCore.resourceLocation("textures/gui/widget/hpca/damaged_optimized_computation_component.png")
    );
}
