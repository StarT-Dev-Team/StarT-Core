package com.startechnology.start_core.machine.redstone;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.common.machine.storage.CreativeEnergyContainerMachine;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.SelectorWidget;
import com.lowdragmc.lowdraglib.gui.widget.SwitchWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.startechnology.start_core.machine.hellforge.StarTHellForgeMachine;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;

public class StarTRedstoneInterfacePartMachine extends TieredIOPartMachine {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(StarTRedstoneInterfacePartMachine.class,
        TieredIOPartMachine.MANAGED_FIELD_HOLDER);

    public static final String DEFAULT_INDICATOR = "Click here to select (if any)";

    @Persisted
    protected String indicator;
    protected HashMap<String, Integer> indicatorMap;
    
    @Persisted
    Integer currentSignal;

    protected int setTier;

    public int getCurrentSignal() {
        return Integer.min(this.currentSignal, 15);
    }

    public String getCurrentIndicator() {
        return this.indicator;
    }

    public void setIndicator(String indicator) {
        this.indicator = indicator;
        this.updateCurrentSignal();
    }

    public void setIndicatorSignal(String indicator, int currentSignal) {
        indicatorMap.put(indicator, currentSignal);
        this.updateCurrentSignal();
    }

    public void updateCurrentSignal() {
        int newSignal = this.indicatorMap.getOrDefault(indicator, 0);
        if (newSignal == this.currentSignal) return;
        this.currentSignal = newSignal;
        notifyBlockUpdate();
    }

    public StarTRedstoneInterfacePartMachine(IMachineBlockEntity holder, int tier, IO io) {
        super(holder, tier, io);
        this.indicator = DEFAULT_INDICATOR;
        this.indicatorMap = new HashMap<>();
        this.currentSignal = 0;
    }
    
    @Override
    public int getOutputSignal(@Nullable Direction side) {
        if (side.getOpposite() != this.getFrontFacing()) return 0;
        return this.currentSignal;
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

    private List<String> getIndicators() {
        // Sanity checking..
        if ((this.controllers.size()) < 1) return List.of();

        if ((this.controllers.first() instanceof IStarTRedstoneInterfacableMachine redstoneMachine)) {
            return redstoneMachine.getIndicators();
        }

        return List.of();
    }

    @Override
    public Widget createMainPage(FancyMachineUIWidget widget) {
        return super.createMainPage(widget);
    }

    @Override
    public Widget createUIWidget() {
        WidgetGroup group = new WidgetGroup(0, 0, 182 + 58, 117 + 8);
        group.addWidget(new LabelWidget(55, 35, "start_core.redstone_interface.select"));
        group.addWidget(new SelectorWidget(20, 50, 200, 20, getIndicators(), -1)
                        .setOnChanged(indicator -> {
                            setIndicator(indicator);
                        })
                        .setSupplier(() -> this.indicator)
                        .setButtonBackground(ResourceBorderTexture.BUTTON_COMMON)
                        .setBackground(ColorPattern.BLACK.rectTexture())
                        .setValue(this.indicator));

        group.setBackground(GuiTextures.BACKGROUND);
        return group;
    }
}
