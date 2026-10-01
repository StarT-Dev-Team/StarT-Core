package com.startechnology.start_core.machine.dyson_sphere;

import com.gregtechceu.gtceu.api.gui.fancy.IFancyConfigurator;
import com.lowdragmc.lowdraglib.gui.texture.ColorRectTexture;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.startechnology.start_core.machine.megastructure.StarColor;

import net.minecraft.network.chat.Component;

public class DysonStarConfigurator implements IFancyConfigurator {

    private final DysonSphereMachine machine;

    public DysonStarConfigurator(DysonSphereMachine machine) {
        this.machine = machine;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("start_core.machine.dyson_sphere.configurator");
    }

    @Override
    public IGuiTexture getIcon() {
        return swatch();
    }

    private IGuiTexture swatch() {
        return new ColorRectTexture(0xFF000000 | machine.getStarColor());
    }

    @Override
    public Widget createConfigurator() {
        var group = new WidgetGroup(0, 0, 150, 72);
        group.addWidget(new LabelWidget(6, 6, "start_core.machine.dyson_sphere.configurator.color"));
        var color = new TextFieldWidget(6, 19, 96, 12, () -> StarColor.format(machine.getStarColor()),
                text -> StarColor.parse(text).ifPresent(machine::setStarColor));
        color.setValidator(text -> StarColor.parse(text).isPresent() ? text : color.getCurrentString());
        color.setMaxStringLength(16);
        group.addWidget(color);
        group.addWidget(new ImageWidget(108, 17, 36, 16, this::swatch));

        group.addWidget(new LabelWidget(6, 40, "start_core.machine.dyson_sphere.configurator.target_mass"));
        group.addWidget(new TextFieldWidget(6, 53, 48, 12, () -> String.valueOf(machine.getTargetMass()),
                text -> machine.setTargetMass(Integer.parseInt(text)))
                .setNumbersOnly(StellarBalance.M_MIN, StellarBalance.M_MAX));
        return group;
    }
}
