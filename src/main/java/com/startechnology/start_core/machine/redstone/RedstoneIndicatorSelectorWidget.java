package com.startechnology.start_core.machine.redstone;

import com.lowdragmc.lowdraglib.gui.widget.SelectableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.SelectorWidget;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class RedstoneIndicatorSelectorWidget extends SelectorWidget {

    private final Supplier<List<RedstoneIndicatorRecord>> recordSupplier;

    private Map<String, RedstoneIndicatorRecord> recordMap = new HashMap<>();

    public RedstoneIndicatorSelectorWidget(int x, int y, int width, int height,
                                           Supplier<List<RedstoneIndicatorRecord>> recordSupplier) {
        super(x, y, width, height, List.of(), -1);
        this.recordSupplier = recordSupplier;
        rebuildFromRecords();
    }

    public void rebuildFromRecords() {
        List<RedstoneIndicatorRecord> records = recordSupplier.get();
        recordMap = records.stream()
                .collect(Collectors.toMap(
                        RedstoneIndicatorRecord::indicatorKey,
                        r -> r));
        setCandidates(records.stream()
                .map(RedstoneIndicatorRecord::indicatorKey)
                .toList());
        decorateRows(records);
        // Re-apply the display name for the currently selected value
        updateButtonDisplay();
    }

    @Override
    public SelectorWidget setValue(String value) {
        super.setValue(value);
        updateButtonDisplay();
        return this;
    }

    private void updateButtonDisplay() {
        if (RedstoneIndicatorRecord.DEFAULT.indicatorKey().equals(currentValue)) {
            textTexture.updateText(escapeFormatSpecifiers(RedstoneIndicatorRecord.DEFAULT.indicatorComponent().getString()));
            return;
        }

        RedstoneIndicatorRecord record = recordMap.get(currentValue);
        if (record != null) {
            textTexture.updateText(escapeFormatSpecifiers(record.indicatorComponent().getString()));
        }
    }

    private static String escapeFormatSpecifiers(String text) {
        return text.replace("%", "%%");
    }

    private void decorateRows(List<RedstoneIndicatorRecord> records) {
        int width = candidates.size() > maxCount ? getSize().width - 4 : getSize().width;

        for (int i = 0; i < selectables.size() && i < records.size(); i++) {
            SelectableWidgetGroup row = selectables.get(i);
            RedstoneIndicatorRecord record = records.get(i);

            row.clearAllWidgets();
            row.addWidget(new ImageWidget(0, 0, width, 15,
                    // hacky escape, but without copy pasting lots of code this is the best way
                    // and it should be the only required escape ?
                    new TextTexture(escapeFormatSpecifiers(record.indicatorComponent().getString()))
                            .setWidth(width)
                            .setType(TextTexture.TextType.ROLL))
                    .appendHoverTooltips(
                            record.indicatorComponent(),
                            record.descriptionComponent(),
                            Component.translatable("ui.start_core.redstone_signal",
                                    record.redstoneLevel())));
        }
    }
}