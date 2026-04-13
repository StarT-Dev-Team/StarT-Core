package com.startechnology.start_core.machine.redstone;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StarTRedstoneInterfacePartMachine extends TieredIOPartMachine {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            StarTRedstoneInterfacePartMachine.class,
            TieredIOPartMachine.MANAGED_FIELD_HOLDER);

    /* Last indicator value for reducing unecessary block updates */
    private Integer lastIndicator;

    public StarTRedstoneInterfacePartMachine(IMachineBlockEntity holder, int tier, IO io) {
        super(holder, tier, io);

        this.lastIndicator = 0;
    }

    private StarTRedstoneIndicatorMap getMachineIndicatorMap() {
        if (getHolder().getMetaMachine() instanceof IStarTRedstoneIndicatorMachine machine) {
            return machine.getIndicatorMap();
        }

        return null;
    }

    public StarTRedstoneIndicatorRecord getCurrentIndicator() {
        StarTRedstoneIndicatorMap map = getMachineIndicatorMap();

        return map != null ? map.getCurrent() : null;
    }

    public Integer getCurrentLevel() {
        return Math.min(getCurrentIndicator().redstoneLevel(), 15);
    }

    public void setCurrentIndicator(String indicatorKey) {
        StarTRedstoneIndicatorMap map = getMachineIndicatorMap();

        if (map == null) return;

        String oldKey = map.getCurrent().indicatorKey();

        if (oldKey.equals(indicatorKey)) {
            return;
        }

        map.setCurrent(indicatorKey);

        if (this.getHolder() instanceof IStarTRedstoneIndicatorMachine machine) {
            machine.updateHatchIndicatorSelection(this, oldKey, indicatorKey);
        }

        modified();
    }

    @Override
    public int getOutputSignal(@Nullable Direction side) {
        if (side.getOpposite() != this.getFrontFacing()) return 0;

        return getCurrentLevel();
    }

    public void updateBlock() {
        if (getCurrentLevel() != this.lastIndicator) {
            this.lastIndicator = getCurrentLevel();

            notifyBlockUpdate();
        }
    }

    /* Should be called whenever the indicators have been modified */
    public void modified() {
        updateBlock();
    }

    @Override
    public boolean canConnectRedstone(Direction side) {
        if (side == this.getFrontFacing()) return true;

        return false;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public Widget createMainPage(FancyMachineUIWidget widget) {
        return super.createMainPage(widget);
    }

    @Override
    public Widget createUIWidget() {
        WidgetGroup group = new WidgetGroup(0, 0, 182 + 58, 127 + 8);

        group.addWidget(new LabelWidget(55, 15, "start_core.redstone_interface.select"));

        StarTRedstoneIndicatorMap map = getMachineIndicatorMap();

        group.addWidget(new StarTIndicatorSelectorWidget(
                        20, 30, 200, 20,
                        () -> map != null ? map.getOrdered() : List.of())
                .setOnChanged(this::setCurrentIndicator)
                .setSupplier(() -> getCurrentIndicator() != null ? getCurrentIndicator().indicatorKey() : "")
                .setButtonBackground(ResourceBorderTexture.BUTTON_COMMON)
                .setBackground(ColorPattern.BLACK.rectTexture())
                .setValue(getCurrentIndicator() != null ? getCurrentIndicator().indicatorKey() : ""));

        group.setBackground(GuiTextures.BACKGROUND);

        return group;
    }
}
