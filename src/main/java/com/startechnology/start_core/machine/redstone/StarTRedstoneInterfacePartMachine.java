package com.startechnology.start_core.machine.redstone;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.SelectorWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.minecraft.core.Direction;

public class StarTRedstoneInterfacePartMachine extends TieredIOPartMachine {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            StarTRedstoneInterfacePartMachine.class,
            TieredIOPartMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    protected StarTRedstoneIndicatorMap indicatorMap;

    public StarTRedstoneInterfacePartMachine(IMachineBlockEntity holder, int tier, IO io) {
        super(holder, tier, io);
        this.indicatorMap = new StarTRedstoneIndicatorMap();
    }

    public StarTRedstoneIndicatorRecord getCurrentIndicator() {
        return this.indicatorMap.getCurrent();
    }

    public void setCurrentIndicator(String indicatorKey) {
        this.indicatorMap.setCurrent(indicatorKey);
    }

    public void putIndicator(StarTRedstoneIndicatorRecord indicator) {
        this.indicatorMap.put(indicator);
    }

    public void updateIndicator(String indicatorKey, Integer redstoneLevel) {
        this.indicatorMap.setRedstoneLevel(indicatorKey, redstoneLevel);
    }

    @Override
    public int getOutputSignal(@Nullable Direction side) {
        if (side.getOpposite() != this.getFrontFacing())
            return 0;
        return this.indicatorMap.getCurrent().redstoneLevel();
    }

    public void clearIndicators() {
        this.indicatorMap.clear();
    }

    @Override
    public boolean canConnectRedstone(Direction side) {
        if (side == this.getFrontFacing())
            return true;
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

    private List<String> getIndicatorKeys() {
        return this.indicatorMap.getOrdered().stream().map(
                record -> record.indicatorKey()).toList();
    }

    @Override
    public Widget createUIWidget() {
        WidgetGroup group = new WidgetGroup(0, 0, 182 + 58, 117 + 8);
        group.addWidget(new LabelWidget(55, 35, "start_core.redstone_interface.select"));
        group.addWidget(new SelectorWidget(20, 50, 200, 20, getIndicatorKeys(), -1)
                .setOnChanged(indicator -> {
                    setCurrentIndicator(indicator);
                })
                .setSupplier(() -> getCurrentIndicator().indicatorKey())
                .setButtonBackground(ResourceBorderTexture.BUTTON_COMMON)
                .setBackground(ColorPattern.BLACK.rectTexture())
                .setValue(getCurrentIndicator().indicatorKey()));

        group.setBackground(GuiTextures.BACKGROUND);
        return group;
    }
}
