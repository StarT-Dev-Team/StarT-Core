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

    /*
     * ======================
     *        NOTE
     * ======================
     * 
     * For the indicatorComponent read these things !!:
     *
     * LDLIB TextTexture only takes a string which is rendered for each selectable dropdown component 
     * and the button that spawns the dropdown.
     * 
     * This means that the only way to render colour in the variadic selector is through the usage of
     * format specifiers in the lang string. See example for hell forge: "Percentage to §c%s"
     * 
     * You may now ask: "Can't we use the withStyle and ChatFormatting methods of the 
     * component to pass it there? Why do we need to put it in the lang string?". Excellent deduction.
     * 
     * Minecraft does not provide a method to render Components to their string variants with formatting
     * codes for the colours which have formatting codes. This means that if we simply get a string using
     * the Component, we won't have our beautiful colours!
     * 
     * We could write a whole parser ourselves but that is uneeded extra complexity in handling every edge 
     * case etc IMO.
     * 
     * So when you create an indicatorComponent: 
     * 
     * Remember to use both *withStyle & chatFormatting* for the proper rendering in the hover tooltip
     * and *include the format strings in your lang string* (reset allows you to reset to the colour both 
     * in the hatch & jade provider) for the TextTextures!.
     * 
     * Thanks for reading!
     * 
     * ======================
     */

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
