package com.startechnology.start_core.machine.redstone;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.lowdragmc.lowdraglib.syncdata.AccessorOp;
import com.lowdragmc.lowdraglib.syncdata.accessor.CustomObjectAccessor;
import com.lowdragmc.lowdraglib.syncdata.payload.FriendlyBufPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import com.startechnology.start_core.StarTCore;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

public class StarTRedstoneIndicatorMap {

    private final Map<String, StarTRedstoneIndicatorRecord> records = new HashMap<>();
    private StarTRedstoneIndicatorRecord current = StarTRedstoneIndicatorRecord.DEFAULT;

    /* LDLIB sync accessor so we can sync/persist the redstone indicator map of hatches. */
    public static class StarTRedstoneIndicatorMapAccessor
            extends CustomObjectAccessor<StarTRedstoneIndicatorMap> {

        private static final StarTRedstoneIndicatorRecord.StarTRedstoneIndicatorRecordAccessor RECORD_ACCESSOR = new StarTRedstoneIndicatorRecord.StarTRedstoneIndicatorRecordAccessor();

        public StarTRedstoneIndicatorMapAccessor() {
            super(StarTRedstoneIndicatorMap.class, true);
        }

        @Override
        public ITypedPayload<?> serialize(AccessorOp op, StarTRedstoneIndicatorMap map) {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

            buf.writeInt(map.records.size());
            for (StarTRedstoneIndicatorRecord record : map.records.values()) {
                FriendlyBufPayload recordPayload = (FriendlyBufPayload) RECORD_ACCESSOR.serialize(op, record);
                byte[] bytes = recordPayload.getPayload().array();
                buf.writeInt(bytes.length);
                buf.writeBytes(bytes);
            }

            buf.writeUtf(map.current.indicatorKey());

            return FriendlyBufPayload.of(buf);
        }

        @Override
        public StarTRedstoneIndicatorMap deserialize(AccessorOp op, ITypedPayload<?> payload) {
            if (!(payload instanceof FriendlyBufPayload bufPayload))
                return null;

            FriendlyByteBuf buf = bufPayload.getPayload();
            StarTRedstoneIndicatorMap map = new StarTRedstoneIndicatorMap();

            int count = buf.readInt();
            for (int i = 0; i < count; i++) {
                int length = buf.readInt();
                FriendlyByteBuf recordBuf = new FriendlyByteBuf(buf.readBytes(length));
                StarTRedstoneIndicatorRecord record = RECORD_ACCESSOR.deserialize(op, FriendlyBufPayload.of(recordBuf));
                if (record != null) {
                    map.records.put(record.indicatorKey(), record);
                }
            }

            String currentKey = buf.readUtf();
            map.current = map.records.getOrDefault(currentKey, StarTRedstoneIndicatorRecord.DEFAULT);

            return map;
        }
    }

    /**
     * Insert or replace a record. The map is keyed by indicatorKey.
     * If a record with the same key already exists it will be overwritten.
     */
    public void put(StarTRedstoneIndicatorRecord record) {
        if (record == null) {
            StarTCore.LOGGER.warn("Attempted to insert null record into StarTRedstoneIndicatorMap");
            return;
        }
        records.put(record.indicatorKey(), record);
        if (current.indicatorKey().equals(record.indicatorKey())) {
            current = record;
        }
    }

    /**
     * Set the current indicator by its indicatorKey.
     */
    public void setCurrent(String indicatorKey) {
        StarTRedstoneIndicatorRecord found = records.get(indicatorKey);
        if (found == null) {
            StarTCore.LOGGER.warn("Attempted to set current indicator in indicator map to non-existent key: {}",
                    indicatorKey);
            return;
        }
        current = found;
    }

    /**
     * Returns the currently selected indicator.
     * Should be Never null and defaults to DEFAULT.
     */
    public StarTRedstoneIndicatorRecord getCurrent() {
        return current;
    }

    /**
     * Updates the redstone level of an existing record by its indicatorKey.
     */
    public void setRedstoneLevel(String indicatorKey, int newLevel) {
        StarTRedstoneIndicatorRecord existing = records.get(indicatorKey);
        if (existing == null) {
            StarTCore.LOGGER.warn("Attempted to update redstone level for non-existent key: {}", indicatorKey);
            return;
        }

        StarTRedstoneIndicatorRecord updated = existing.withRedstoneLevel(newLevel);
        records.put(indicatorKey, updated);

        if (current.indicatorKey().equals(indicatorKey)) {
            current = updated;
        }
    }

    /**
     * Returns all records sorted ascending by their ordering field.
     * Ties are broken by indicatorKey alphabetically for stable ordering.
     */
    public List<StarTRedstoneIndicatorRecord> getOrdered() {
        return records.values().stream()
                .sorted(Comparator
                        .comparingInt(StarTRedstoneIndicatorRecord::ordering)
                        .thenComparing(StarTRedstoneIndicatorRecord::indicatorKey))
                .collect(Collectors.toList());
    }

    /**
     * Clears the map, emptying all values
     */
    public void clear() {
        this.current = StarTRedstoneIndicatorRecord.DEFAULT;
        this.records.clear();
    }
}