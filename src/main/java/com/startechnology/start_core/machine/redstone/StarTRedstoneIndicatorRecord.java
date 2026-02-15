package com.startechnology.start_core.machine.redstone;

import com.lowdragmc.lowdraglib.syncdata.AccessorOp;
import com.lowdragmc.lowdraglib.syncdata.accessor.CustomObjectAccessor;
import com.lowdragmc.lowdraglib.syncdata.payload.FriendlyBufPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

public record StarTRedstoneIndicatorRecord(
        String indicatorKey,
        String descriptionKey,
        Integer redstoneLevel,
        Integer ordering) {

    public static final StarTRedstoneIndicatorRecord DEFAULT = new StarTRedstoneIndicatorRecord(
            "variadic.start_core.default",
            "variadic.start_core.description.default", 0, 0);

    public StarTRedstoneIndicatorRecord withRedstoneLevel(int newLevel) {
        return new StarTRedstoneIndicatorRecord(indicatorKey, descriptionKey, newLevel, ordering);
    }

    /*
     * This accessor allows LDLIB to serialize/deserialize over network for the sync
     * annotations
     */
    public static class StarTRedstoneIndicatorRecordAccessor
            extends CustomObjectAccessor<StarTRedstoneIndicatorRecord> {

        public StarTRedstoneIndicatorRecordAccessor() {
            super(StarTRedstoneIndicatorRecord.class, true);
        }

        @Override
        public ITypedPayload<?> serialize(AccessorOp op, StarTRedstoneIndicatorRecord indicatorRecord) {
            FriendlyByteBuf serializedHolder = new FriendlyByteBuf(Unpooled.buffer());

            serializedHolder.writeUtf(indicatorRecord.indicatorKey);
            serializedHolder.writeUtf(indicatorRecord.descriptionKey);
            serializedHolder.writeInt(indicatorRecord.redstoneLevel);
            serializedHolder.writeInt(indicatorRecord.ordering);

            return FriendlyBufPayload.of(serializedHolder);
        }

        @Override
        public StarTRedstoneIndicatorRecord deserialize(AccessorOp op, ITypedPayload<?> payload) {
            if (payload instanceof FriendlyBufPayload buffer) {
                String indicatorKey = buffer.getPayload().readUtf();
                String descriptionKey = buffer.getPayload().readUtf();
                Integer redstoneLevel = buffer.getPayload().readInt();
                Integer ordering = buffer.getPayload().readInt();

                return new StarTRedstoneIndicatorRecord(indicatorKey, descriptionKey, redstoneLevel, ordering);
            }
            return null;
        }

    }
}
