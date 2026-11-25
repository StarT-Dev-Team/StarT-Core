package com.startechnology.start_core.machine.threading;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.ComponentPanel;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ScrollerView;
import com.lowdragmc.lowdraglib2.gui.util.ClickData;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;
import java.util.function.Supplier;

public class StarTThreadingControllerPartMachine extends MultiblockPartMachine {

    /* Track controller for updating, screw demeter */
    private Supplier<StarTThreadingCapableMachine> machineSupplier;

    public StarTThreadingControllerPartMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    public void setAssociatedController(StarTThreadingCapableMachine controller) {
        this.machineSupplier = () -> controller;
    }

    public void clearController() {
        this.machineSupplier = null;
    }

    @Override
    public boolean shouldOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        return true;
    }

    @Override
    public boolean canShared() {
        return false;
    }

    private void addComponentPanelText(List<Component> componentList) {
        if (machineSupplier == null) {
            componentList.add(Component.literal("No controller connected").withStyle(ChatFormatting.RED));
            return;
        }

        StarTThreadingCapableMachine machine = this.machineSupplier.get();

        componentList.add(Component.translatable("start_core.machine.threading_controller.stat.display")
                .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.translatable("start_core.machine.threading_controller.stat.display.hover")))));

        componentList
                .add(Component
                        .translatable(
                                "start_core.machine.threading_controller.stat.display_general_remaining",
                                Component.translatable("start_core.machine.threading.stat.general"),
                                Component.literal(FormattingUtil.formatNumbers(machine.getRemainingAssignable()))
                                        .withStyle(ChatFormatting.YELLOW))
                        .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Component
                                        .translatable("start_core.machine.threading_controller.stat.general_hover")))));

        for (String stat : machine.getStatTypes()) {
            componentList.add(Component.empty());
            var statPointLine = Component
                    .translatable("start_core.machine.threading_controller.stat.display_assign",
                            Component.translatable("start_core.machine.threading.stat." + stat),
                            Component.literal(FormattingUtil.formatNumbers(machine.getStatAssigned(stat)))
                                    .withStyle(ChatFormatting.YELLOW),
                            Component.literal(FormattingUtil.formatNumbers(machine.getStatTotal(stat)))
                                    .withStyle(ChatFormatting.YELLOW))
                    .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            Component.translatable(
                                    "start_core.machine.threading_controller.stat." + stat + ".hover"))));

            /* Stat add/remove buttons */
            statPointLine.append(" ");
            statPointLine.append(ComponentPanel.withButton(
                    Component.translatable("start_core.machine.threading_controller.stat.assign")
                            .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    Component.translatable(
                                            "start_core.machine.threading_controller.stat.assign.hover")))),
                    "add_" + stat));

            statPointLine.append(" ");
            statPointLine.append(ComponentPanel.withButton(
                    Component.translatable("start_core.machine.threading_controller.stat.remove")
                            .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    Component.translatable(
                                            "start_core.machine.threading_controller.stat.remove.hover")))),
                    "remove_" + stat));

            componentList.add(statPointLine);
            componentList
                    .add(machine.getPrettyFormat(stat)
                            .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    Component.translatable(
                                            "start_core.machine.threading_controller.stat." + stat + ".hover")))));

        }

        /* Blocks display */
        componentList.add(Component.empty());

        componentList.add(Component.translatable("start_core.machine.threading_controller.list_components")
                .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.translatable("start_core.machine.threading_controller.list_components.hover")))));

        machine.getStats().forEach((name, stats) -> {
            Component component = Component.translatable(
                    "start_core.machine.threading_controller.component_format",
                    Component.translatable("block.start_core." + name).withStyle(ChatFormatting.GRAY),
                    Component.literal(FormattingUtil.formatNumbers(stats.amount)).withStyle(ChatFormatting.AQUA));
            componentList.add(component);
        });

        componentList.add(Component.empty());
    }

    /* Handle the click inside of the component panel above */
    public void onThreadingControllerPanelClick(String componentData, ClickData clickData) {
        // Check if machine is for some reason null
        if (machineSupplier == null) {
            return;
        }

        StarTThreadingCapableMachine machine = machineSupplier.get();

        if (!clickData.isRemote) {
            int amount = 1;

            if (clickData.isShiftClick && !clickData.isCtrlClick) {
                amount = 5;
            } else if (clickData.isCtrlClick && !clickData.isShiftClick) {
                amount = 20;
            } else if (clickData.isCtrlClick) {
                amount = Integer.MAX_VALUE;
            }

            /* Handle addition/removal on server side. */
            if (componentData.startsWith("add_")) {
                String stat = componentData.replace("add_", "");
                machine.assignStat(stat, amount);
            } else if (componentData.startsWith("remove_")) {
                String stat = componentData.replace("remove_", "");
                machine.unassignStat(stat, amount);
            }
        }
    }

    @Override
    public UIElement createUIWidget() {
        var title = new Label();
        title.setText(Component.translatable("block.start_core.threading_controller"));

        var content = new UIElement();
        content.layout(layout -> layout.paddingAll(4).gapAll(3));
        content.addChildren(title, new ComponentPanel(this::addComponentPanelText)
                .setMaxWidthLimit(234)
                .clickHandler(this::onThreadingControllerPanelClick));

        var screen = new ScrollerView();
        screen.layout(layout -> layout.width(242).height(117));
        screen.style(style -> style.background(GuiTextures.DISPLAY));
        screen.addScrollViewChild(content);

        var group = new UIElement();
        group.layout(layout -> layout.paddingAll(4));
        group.style(style -> style.background(GuiTextures.BACKGROUND_INVERSE));
        group.addChild(screen);
        return group;
    }
}
