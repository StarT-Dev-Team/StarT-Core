package com.startechnology.start_core.machine.redstone;

import com.lowdragmc.lowdraglib.gui.widget.SelectableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.SelectorWidget;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Supplier;

public class StarTIndicatorSelectorWidget extends SelectorWidget {

    private Supplier<List<StarTRedstoneIndicatorRecord>> recordSupplier;

    public StarTIndicatorSelectorWidget(
            int x, int y, int width, int height,
            Supplier<List<StarTRedstoneIndicatorRecord>> recordSupplier) {
        super(x, y, width, height, List.of(), -1);
        this.recordSupplier = recordSupplier;
        rebuildFromRecords();
    }

    /* THIS SHOULDN'T NEED TO BE CALLED ELSEWHERE SINCE INDICATOR MAP IS NOT CHANGING! */
    public void rebuildFromRecords() {
        List<StarTRedstoneIndicatorRecord> records = recordSupplier.get();

        setCandidates(records.stream()
                .map(StarTRedstoneIndicatorRecord::indicatorKey)
                .toList());

        decorateRows(records);
    }

    /**
     * After the parent has built its SelectableWidgetGroup rows via computeLayout,
     * walk the popUp children and attach a tooltip to each one matching its record.
     */
    private void decorateRows(List<StarTRedstoneIndicatorRecord> records) {
        var children = popUp.widgets;
        for (int i = 0; i < Math.min(children.size(), records.size()); i++) {
            if (!(children.get(i) instanceof SelectableWidgetGroup row)) continue;
            StarTRedstoneIndicatorRecord record = records.get(i);
            row.setHoverTooltips(
                Component.translatable(record.indicatorKey()),
                Component.translatable(record.descriptionKey()),
                Component.translatable(
                    "ui.start_core.redstone_signal",
                    record.redstoneLevel()
                )
            );
        }
    }
}