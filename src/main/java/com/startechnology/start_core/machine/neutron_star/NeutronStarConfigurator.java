package com.startechnology.start_core.machine.neutron_star;

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

public class NeutronStarConfigurator implements IFancyConfigurator {

    private final NeutronStarForgeMachine machine;

    public NeutronStarConfigurator(NeutronStarForgeMachine machine) {
        this.machine = machine;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("start_core.machine.neutron_star_forge.configurator");
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
        var group = new WidgetGroup(0, 0, 150, 84);
        group.addWidget(new LabelWidget(6, 6, "start_core.machine.neutron_star_forge.configurator.color"));
        var color = new TextFieldWidget(6, 19, 96, 12, () -> StarColor.format(machine.getStarColor()),
                text -> StarColor.parse(text).ifPresent(machine::setStarColor));
        color.setValidator(text -> StarColor.parse(text).isPresent() ? text : color.getCurrentString());
        color.setMaxStringLength(16);
        group.addWidget(color);
        group.addWidget(new ImageWidget(108, 17, 36, 16, this::swatch));

        group.addWidget(new LabelWidget(6, 40, "start_core.machine.neutron_star_forge.configurator.target_spin"));
        group.addWidget(new TextFieldWidget(6, 53, 48, 12, () -> String.valueOf(machine.getTargetSpin()),
                text -> machine.setTargetSpin(Integer.parseInt(text)))
                .setNumbersOnly(0, (int) NeutronStarBalance.F_MAX));
        group.addWidget(new LabelWidget(6, 69, "start_core.machine.neutron_star_forge.configurator.target_spin_hint"));
        return group;
    }
}
