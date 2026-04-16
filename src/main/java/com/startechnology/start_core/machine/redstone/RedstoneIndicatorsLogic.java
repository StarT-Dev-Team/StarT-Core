package com.startechnology.start_core.machine.redstone;

import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.lowdragmc.lowdraglib.syncdata.IEnhancedManaged;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.annotation.UpdateListener;
import com.lowdragmc.lowdraglib.syncdata.field.FieldManagedStorage;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RedstoneIndicatorsLogic implements IEnhancedManaged {

    @Getter
    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(RedstoneIndicatorsLogic.class);

    @Getter
    @Persisted
    @DescSynced
    @UpdateListener(methodName = "onIndicatorRecordsSet")
    private final List<RedstoneIndicatorRecord> indicatorRecords = new ArrayList<>();

    private final Map<String, RedstoneIndicatorRecord> indicatorRecordsMap = new HashMap<>();

    private final List<RedstoneInterfacePartMachine> redstoneParts = new ArrayList<>();

    private final @Nullable IRedstoneIndicatorMachine machine;

    public RedstoneIndicatorsLogic(IMultiController machine) {
        this.machine = machine instanceof IRedstoneIndicatorMachine redstoneMachine ? redstoneMachine : null;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onChanged() {
        if (machine == null) return;
        machine.self().onChanged();
    }

    @Override
    public void scheduleRenderUpdate() {
        if (machine == null) return;
        machine.self().scheduleRenderUpdate();
    }

    @SuppressWarnings("unused")
    private void onIndicatorRecordsSet(List<RedstoneIndicatorRecord> newValue, List<RedstoneIndicatorRecord> oldValue) {
        updateRecordsMap(newValue);
    }

    private void updateRecordsMap(List<RedstoneIndicatorRecord> value) {
        indicatorRecordsMap.clear();
        value.forEach(r -> indicatorRecordsMap.put(r.indicatorKey(), r));
    }

    public void onStructureFormed() {
        indicatorRecords.clear();
        redstoneParts.clear();

        if (machine == null) return;

        indicatorRecords.addAll(machine.getInitialIndicators());
        updateRecordsMap(indicatorRecords);

        redstoneParts.addAll(machine.getParts().stream()
                .filter(RedstoneInterfacePartMachine.class::isInstance)
                .map(RedstoneInterfacePartMachine.class::cast)
                .peek(part -> part.setRedstoneValue(getIndicator(part.getIndicatorKey()).redstoneLevel()))
                .toList());
    }

    public void onStructureInvalid() {
        indicatorRecords.clear();
        indicatorRecordsMap.clear();
        redstoneParts.clear();
    }

    public List<RedstoneIndicatorRecord> getSortedIndicators() {
        return indicatorRecords.stream()
                .sorted(Comparator.comparingInt(RedstoneIndicatorRecord::ordering).thenComparing(RedstoneIndicatorRecord::indicatorKey))
                .collect(Collectors.toList());
    }

    public RedstoneIndicatorRecord getIndicator(@Nullable String indicatorKey) {
        return Optional.ofNullable(indicatorRecordsMap.get(indicatorKey)).orElse(RedstoneIndicatorRecord.DEFAULT);
    }

    public void setIndicatorValue(String indicatorKey, int redstoneLevel) {
        var record = indicatorRecordsMap.get(indicatorKey);
        if (record == null || record.redstoneLevel() == redstoneLevel) return;

        var newValue = record.withRedstoneLevel(redstoneLevel);
        indicatorRecords.set(indicatorRecords.indexOf(record), newValue);
        indicatorRecordsMap.put(record.indicatorKey(), newValue);

        for (var part : redstoneParts) {
            if (part.getIndicatorKey() == null || !part.getIndicatorKey().equals(indicatorKey)) continue;
            if (part.getRedstoneValue() != redstoneLevel) continue;
            part.setRedstoneValue(redstoneLevel);
        }
    }
}
