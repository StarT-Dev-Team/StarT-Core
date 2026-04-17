package com.startechnology.start_core.machine.redstone;

import com.lowdragmc.lowdraglib.syncdata.payload.ObjectTypedPayload;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.experimental.Accessors;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.Nullable;


@Accessors(fluent = true)
public record RedstoneIndicatorRecord(String indicatorKey, Component indicatorComponent, Component descriptionComponent, int redstoneLevel, int ordering) {

    public static final Codec<RedstoneIndicatorRecord> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("indicatorKey").forGetter(RedstoneIndicatorRecord::indicatorKey),
            ExtraCodecs.COMPONENT.fieldOf("indicatorComponent").forGetter(RedstoneIndicatorRecord::indicatorComponent),
            ExtraCodecs.COMPONENT.fieldOf("descriptionComponent").forGetter(RedstoneIndicatorRecord::descriptionComponent),
            Codec.INT.fieldOf("redstoneLevel").forGetter(RedstoneIndicatorRecord::redstoneLevel),
            Codec.INT.fieldOf("ordering").forGetter(RedstoneIndicatorRecord::ordering)
    ).apply(instance, RedstoneIndicatorRecord::new));



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

    public static final RedstoneIndicatorRecord DEFAULT = new RedstoneIndicatorRecord(
            "variadic.start_core.default",
            Component.translatable("variadic.start_core.indicator.default"),
            Component.translatable("variadic.start_core.description.default"), 0, 0);

    public RedstoneIndicatorRecord withRedstoneLevel(int newLevel) {
        return new RedstoneIndicatorRecord(indicatorKey, indicatorComponent, descriptionComponent, newLevel,
                ordering);
    }

    public static class Payload extends ObjectTypedPayload<RedstoneIndicatorRecord> {

        @Override
        public @Nullable Tag serializeNBT() {
            return RedstoneIndicatorRecord.CODEC.encodeStart(NbtOps.INSTANCE, payload).result().orElseThrow();
        }

        @Override
        public void deserializeNBT(Tag tag) {
            payload = RedstoneIndicatorRecord.CODEC.parse(NbtOps.INSTANCE, tag).result().orElseThrow();
        }
    }

}
