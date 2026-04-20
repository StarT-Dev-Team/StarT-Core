package com.startechnology.start_core.machine.redstone;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.annotation.UpdateListener;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RedstoneInterfacePartMachine extends TieredIOPartMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(RedstoneInterfacePartMachine.class, TieredIOPartMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @DescSynced
    @Getter
    private @Nullable String indicatorKey = null;

    @DescSynced
    @Getter
    @UpdateListener(methodName = "onRedstoneValueSet")
    private int redstoneValue = 0;

    private @Nullable IRedstoneIndicatorMachine machine;

    private final IMachineBlockEntity holder;

    public RedstoneInterfacePartMachine(IMachineBlockEntity holder, int tier, IO io) {
        super(holder, tier, io);
        this.holder = holder;
    }

    public void setRedstoneValue(int redstoneValue) {
        this.redstoneValue = redstoneValue;
        markDirty();
        notifyBlockUpdate();
        notifyAdjacentBlockUpdate();
    }

    @Override
    public void addedToController(IMultiController controller) {
        super.addedToController(controller);
        if (controller instanceof IRedstoneIndicatorMachine redstoneMachine) {
            machine = redstoneMachine;
            setRedstoneValue(machine.getIndicatorValue(indicatorKey));
        }
    }

    @Override
    public void removedFromController(IMultiController controller) {
        super.removedFromController(controller);
        machine = null;
        setRedstoneValue(0);
    }

    @Override
    public void onControllersUpdated(Set<BlockPos> newPositions, Set<BlockPos> old) {
        super.onControllersUpdated(newPositions, old);
        machine = controllers.stream()
                .filter(IRedstoneIndicatorMachine.class::isInstance)
                .map(IRedstoneIndicatorMachine.class::cast)
                .findFirst().orElse(null);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @SuppressWarnings("unused")
    private void onRedstoneValueSet(int newValue, int oldValue) {
        if (newValue == oldValue) return;
        holder.scheduleRenderUpdate();
    }

    @Override
    public int getOutputSignal(@Nullable Direction side) {
        if (side == null || side.getOpposite() != getFrontFacing()) {
            return super.getOutputSignal(side);
        }
        return redstoneValue;
    }

    public RedstoneIndicatorRecord getCurrentIndicator() {
        if (machine == null || indicatorKey == null) return RedstoneIndicatorRecord.DEFAULT;
        return machine.getRedstoneIndicatorsTrait().getIndicator(indicatorKey);
    }

    @Override
    public boolean canConnectRedstone(Direction side) {
        if (side.getOpposite() != getFrontFacing()) {
            return super.canConnectRedstone(side);
        }
        return true;
    }

    private List<RedstoneIndicatorRecord> getSortedIndicators() {
        if (machine == null) return List.of();
        return machine.getRedstoneIndicatorsTrait().getSortedIndicators();
    }

    private void setIndicatorKey(@Nullable String indicatorKey) {
        this.indicatorKey = indicatorKey;
        if (!isRemote() && machine != null) {
            setRedstoneValue(machine.getIndicatorValue(indicatorKey));
        }
    }

    @Override
    public Widget createMainPage(FancyMachineUIWidget widget) {
        return super.createMainPage(widget);
    }

    @Override
    public Widget createUIWidget() {
        var group = new WidgetGroup(0, 0, 182 + 58, 127 + 8);

        group.addWidget(new LabelWidget(55, 15, "start_core.redstone_interface.select"));
        group.addWidget(new RedstoneIndicatorSelectorWidget(20, 30, 200, 20, this::getSortedIndicators)
                .setOnChanged(this::setIndicatorKey)
                .setSupplier(() -> indicatorKey != null ? indicatorKey : "variadic.start_core.default")
                .setButtonBackground(ResourceBorderTexture.BUTTON_COMMON)
                .setBackground(ColorPattern.BLACK.rectTexture())
                .setValue(indicatorKey != null ? indicatorKey : "variadic.start_core.default"));

        group.setBackground(GuiTextures.BACKGROUND);

        return group;
    }
}
