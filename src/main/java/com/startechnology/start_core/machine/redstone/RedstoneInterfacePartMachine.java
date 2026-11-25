package com.startechnology.start_core.machine.redstone;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib2.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib2.syncdata.annotation.UpdateListener;
import com.startechnology.start_core.api.machine.IStarTJadeDisplayable;
import dev.vfyjxf.taffy.style.AlignItems;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RedstoneInterfacePartMachine extends TieredIOPartMachine implements IStarTJadeDisplayable {

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

    @SuppressWarnings("unused")
    private void onRedstoneValueSet(int oldValue, int newValue) {
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

    private String getSelectedIndicatorKey() {
        return indicatorKey != null ? indicatorKey : RedstoneIndicatorRecord.DEFAULT.indicatorKey();
    }

    @Override
    public UIElement createUIWidget() {
        var group = new UIElement();
        group.layout(l -> l.width(240).alignItems(AlignItems.CENTER).gapAll(6).paddingAll(8));

        var label = new Label().setText(Component.translatable("start_core.redstone_interface.select"));
        var selector = new RedstoneIndicatorSelectorWidget(this::getSortedIndicators);
        selector.layout(l -> l.width(200).height(20));
        selector.setValue(getSelectedIndicatorKey(), false);
        selector.bind(DataBindingBuilder.string(this::getSelectedIndicatorKey, this::setIndicatorKey).build());

        group.addChildren(label, selector);
        return group;
    }

    @Override
    public void writeJadeData(CompoundTag data) {
        data.putInt("signal_level", getRedstoneValue());
        data.putString("indicator", Component.Serializer.toJson(getCurrentIndicator().indicatorComponent()));
    }

    @Override
    public void appendJadeTooltip(CompoundTag data, Consumer<Component> tooltip) {
        if (!data.contains("signal_level") || !data.contains("indicator")) return;

        var signalLevel = data.getInt("signal_level");
        var indicatorJson = data.getString("indicator");
        var indicatorComponent = Optional.ofNullable(Component.Serializer.fromJson(indicatorJson))
                .map(Component.class::cast)
                .orElse(RedstoneIndicatorRecord.DEFAULT.indicatorComponent());

        tooltip.accept(Component.translatable("ui.start_core.redstone_signal", signalLevel));
        tooltip.accept(Component.translatable("ui.start_core.indicator", indicatorComponent));
    }
}
