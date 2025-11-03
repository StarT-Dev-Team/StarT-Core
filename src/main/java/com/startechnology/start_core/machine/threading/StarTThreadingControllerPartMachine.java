package com.startechnology.start_core.machine.threading;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IWorkableMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.pattern.util.PatternMatchContext;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
import com.startechnology.start_core.machine.dreamlink.StarTDreamLinkHatchPartMachine;
import com.startechnology.start_core.machine.dreamlink.StarTDreamLinkRangeRenderer;
import com.startechnology.start_core.machine.dreamlink.StarTDreamWidgetGroup;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class StarTThreadingControllerPartMachine extends MultiblockPartMachine {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            StarTThreadingControllerPartMachine.class,
            MultiblockPartMachine.MANAGED_FIELD_HOLDER);

    /* Track controller for updating, screw demeter */
    private StarTThreadingCapableMachine machine;

    public StarTThreadingControllerPartMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    public void setAssociatedController(StarTThreadingCapableMachine controller) {
        this.machine = controller;
    }

    public void clearController() {
        this.machine = null;
    }

    @Override
    public boolean shouldOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        return true;
    }

    @Override
    public boolean canShared() {
        return false;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    private void addComponentPanelText(List<Component> componentList) {
        if (machine == null) {
            componentList.add(Component.literal("No controller connected").withStyle(ChatFormatting.RED));
            return;
        }

        componentList.add(Component.translatable("start_core.machine.threading_controller.stat.display")
                .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.translatable("start_core.machine.threading_controller.stat.display.hover")))));

        componentList
                .add(Component
                        .literal(LocalizationUtils.format(
                                "start_core.machine.threading_controller.stat.display_general_remaining",
                                LocalizationUtils.format("start_core.machine.threading.stat.general"),
                                FormattingUtil.formatNumbers(machine.getRemainingAssignable())))
                        .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Component
                                        .translatable("start_core.machine.threading_controller.stat.general_hover")))));

        for (String stat : machine.getStatTypes()) {
            componentList.add(Component.literal(""));
            var statPointLine = Component
                    .literal(LocalizationUtils.format("start_core.machine.threading_controller.stat.display_assign",
                            LocalizationUtils.format("start_core.machine.threading.stat." + stat),
                            FormattingUtil.formatNumbers(machine.getStatAssigned(stat)),
                            FormattingUtil.formatNumbers(machine.getStatTotal(stat)))

                    )
                    .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            Component.translatable(
                                    "start_core.machine.threading_controller.stat." + stat + ".hover"))));

            /* Stat add/remove buttons */
            statPointLine.append(" ");
            statPointLine.append(ComponentPanelWidget.withButton(
                    Component.translatable("start_core.machine.threading_controller.stat.assign")
                            .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    Component.translatable(
                                            "start_core.machine.threading_controller.stat.assign.hover")))),
                    "add_" + stat));

            statPointLine.append(" ");
            statPointLine.append(ComponentPanelWidget.withButton(
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
        componentList.add(Component.literal(""));

        componentList.add(Component.translatable("start_core.machine.threading_controller.list_components")
                .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.translatable("start_core.machine.threading_controller.list_components.hover")))));

        machine.getStats().forEach((name, stats) -> {
            Component component = Component.literal(
                    LocalizationUtils.format("start_core.machine.threading_controller.component_format",
                            LocalizationUtils.format("block.start_core." + name),
                            FormattingUtil.formatNumbers(stats.amount)));
            componentList.add(component);
        });

        componentList.add(Component.literal(""));
    }

    
    /* Handle the click inside of the component panel above */
    public void onThreadingControllerPanelClick(String componentData, ClickData clickData) {        
        // Check if machine is for some reason null
        if (machine == null) {
            return;
        }
        
        if (!clickData.isRemote) {
			int amount = 1;
            
            if (clickData.isShiftClick && !clickData.isCtrlClick) {
                amount = 5;
            } else if (clickData.isCtrlClick && !clickData.isShiftClick) {
                amount = 10;
            } else if (clickData.isCtrlClick && clickData.isShiftClick) {
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
    public Widget createUIWidget() {
        WidgetGroup group = new WidgetGroup(0, 0, 242 + 8, 117 + 8);
        group.addWidget(
                new DraggableScrollableWidgetGroup(4, 4, 242, 117).setBackground(GuiTextures.DISPLAY)
                        .addWidget(
                                new LabelWidget(4, 5, LocalizationUtils.format("block.start_core.threading_controller")))
                        .addWidget(new ComponentPanelWidget(4, 20, this::addComponentPanelText).clickHandler(this::onThreadingControllerPanelClick)));

        group.setBackground(GuiTextures.BACKGROUND_INVERSE);
        return group;
    }

}
