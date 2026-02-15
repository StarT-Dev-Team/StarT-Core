package com.startechnology.start_core.machine.redstone;

import java.util.Optional;

import com.lowdragmc.lowdraglib.syncdata.AccessorOp;
import com.lowdragmc.lowdraglib.syncdata.accessor.CustomObjectAccessor;
import com.lowdragmc.lowdraglib.syncdata.payload.FriendlyBufPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

public record StarTRedstoneIndicatorRecord(
        String indicatorKey,
        Component indicatorComponent,
        Component descriptionComponent,
        Integer redstoneLevel,
        Integer ordering) {

    public static final StarTRedstoneIndicatorRecord DEFAULT = new StarTRedstoneIndicatorRecord(
            "variadic.start_core.default",
            Component.translatable("variadic.start_core.indicator.default"),
            Component.translatable("variadic.start_core.description.default"), 0, 0);

    public StarTRedstoneIndicatorRecord withRedstoneLevel(int newLevel) {
        return new StarTRedstoneIndicatorRecord(indicatorKey, indicatorComponent, descriptionComponent, newLevel,
                ordering);
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
            serializedHolder.writeUtf(Component.Serializer.toJson(indicatorRecord.indicatorComponent));
            serializedHolder.writeUtf(Component.Serializer.toJson(indicatorRecord.descriptionComponent));
            serializedHolder.writeInt(indicatorRecord.redstoneLevel);
            serializedHolder.writeInt(indicatorRecord.ordering);

            return FriendlyBufPayload.of(serializedHolder);
        }

        @Override
        public StarTRedstoneIndicatorRecord deserialize(AccessorOp op, ITypedPayload<?> payload) {
            if (payload instanceof FriendlyBufPayload buffer) {
                String indicatorKey = buffer.getPayload().readUtf();
                Component indicatorComponent = Optional
                        .ofNullable(Component.Serializer.fromJson(buffer.getPayload().readUtf()))
                        .orElse(Component.literal(indicatorKey));
                Component descriptionComponent = Optional
                        .ofNullable(Component.Serializer.fromJson(buffer.getPayload().readUtf()))
                        .orElse(Component.empty());
                Integer redstoneLevel = buffer.getPayload().readInt();
                Integer ordering = buffer.getPayload().readInt();
                return new StarTRedstoneIndicatorRecord(indicatorKey, indicatorComponent, descriptionComponent,
                        redstoneLevel, ordering);
            }
            return null;
        }

    }
}
