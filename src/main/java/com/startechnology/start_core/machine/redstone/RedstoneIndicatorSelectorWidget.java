package com.startechnology.start_core.machine.redstone;

import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Selector;
import com.lowdragmc.lowdraglib2.gui.ui.utils.UIElementProvider;

import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class RedstoneIndicatorSelectorWidget extends Selector<String> {

    private final Supplier<List<RedstoneIndicatorRecord>> recordSupplier;

    private Map<String, RedstoneIndicatorRecord> recordMap = new HashMap<>();

    public RedstoneIndicatorSelectorWidget(Supplier<List<RedstoneIndicatorRecord>> recordSupplier) {
        this.recordSupplier = recordSupplier;
        setCandidateUIProvider(this::createCandidateUI);
        rebuildFromRecords();
    }

    public void rebuildFromRecords() {
        var records = recordSupplier.get();
        recordMap = records.stream()
                .collect(Collectors.toMap(RedstoneIndicatorRecord::indicatorKey, r -> r, (a, b) -> a));
        setCandidates(records.stream().map(RedstoneIndicatorRecord::indicatorKey).toList());
    }

    private RedstoneIndicatorRecord recordOf(String key) {
        if (key == null) return RedstoneIndicatorRecord.DEFAULT;
        return recordMap.getOrDefault(key, RedstoneIndicatorRecord.DEFAULT);
    }

    private UIElement createCandidateUI(String key) {
        var record = recordOf(key);
        return UIElementProvider.<String>text(k -> record.indicatorComponent()).apply(key)
                .style(style -> style.tooltips(
                        record.indicatorComponent(),
                        record.descriptionComponent(),
                        Component.translatable("ui.start_core.redstone_signal", record.redstoneLevel())));
    }
}
