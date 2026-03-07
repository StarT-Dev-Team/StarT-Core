package com.startechnology.start_core.machine.redstone;

import org.jetbrains.annotations.Nullable;

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
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.LazyManaged;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.minecraft.core.Direction;

public class StarTRedstoneInterfacePartMachine extends TieredIOPartMachine {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            StarTRedstoneInterfacePartMachine.class,
            TieredIOPartMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @DescSynced
    @LazyManaged
    protected StarTRedstoneIndicatorMap indicatorMap;

    /* Last indicator value for reducing unecessary block updates */
    private Integer lastIndicator;

    public StarTRedstoneInterfacePartMachine(IMachineBlockEntity holder, int tier, IO io) {
        super(holder, tier, io);
        this.lastIndicator = 0;
        this.indicatorMap = new StarTRedstoneIndicatorMap();
    }

    public StarTRedstoneIndicatorRecord getCurrentIndicator() {
        return this.indicatorMap.getCurrent();
    }

    public Integer getCurrentLevel() {
        return Math.min(getCurrentIndicator().redstoneLevel(), 15);
    }


    private void syncMap() {
        markDirty("indicatorMap");
    }

    public void setCurrentIndicator(String indicatorKey) {
        this.indicatorMap.setCurrent(indicatorKey);
        modified();
    }

    public void putIndicator(StarTRedstoneIndicatorRecord indicator) {
        this.indicatorMap.put(indicator);
        modified();
    }

    public void updateIndicator(String indicatorKey, Integer redstoneLevel) {
        this.indicatorMap.setRedstoneLevel(indicatorKey, redstoneLevel);
        modified();
    }

    @Override
    public int getOutputSignal(@Nullable Direction side) {
        if (side.getOpposite() != this.getFrontFacing())
            return 0;
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
        syncMap();
        updateBlock();
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

    @Override
    public Widget createUIWidget() {
        if (!getLevel().isClientSide) syncMap();
        WidgetGroup group = new WidgetGroup(0, 0, 182 + 58, 127 + 8);

        group.addWidget(new LabelWidget(55, 15, "start_core.redstone_interface.select"));

        group.addWidget(new StarTIndicatorSelectorWidget(
                        20, 30, 200, 20,
                        () -> this.indicatorMap.getOrdered())
                .setOnChanged(this::setCurrentIndicator)
                .setSupplier(() -> getCurrentIndicator().indicatorKey())
                .setButtonBackground(ResourceBorderTexture.BUTTON_COMMON)
                .setBackground(ColorPattern.BLACK.rectTexture())
                .setValue(getCurrentIndicator().indicatorKey()));

        group.setBackground(GuiTextures.BACKGROUND);
        return group;
    }
}
