package com.startechnology.start_core.machine.bulking;

import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.ParametersAreNonnullByDefault;

import com.lowdragmc.lowdraglib2.gui.util.ClickData;
import com.gregtechceu.gtceu.api.gui.widget.ComponentPanel;
import com.startechnology.start_core.api.machine.IStarTJadeDisplayable;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IBulking extends IStarTJadeDisplayable {

    public BulkingType getBulkingType();

    public void setBulkingType(BulkingType type);

    public boolean isForcedBulking();

    public void setForcedBulking(boolean forced);

    default void initParamDefaults() {
        this.setBulkingType(BulkingType.BUKLING_4_3);
        this.setForcedBulking(false);
    }

    final static DecimalFormat df = new DecimalFormat("0.##");

    default List<Component> controllerDisplayText(List<Component> textList) {
        textList.add(Component.translatable("start_core.bulking.throughput_multiplier",
                this.getBulkingType().throughputModifier));
        textList.add(Component.translatable("start_core.bulking.duration_multiplier",
                df.format(this.getBulkingType().durationModifier)));

        MutableComponent forcedBulkingText = Component.translatable("start_core.bulking.forced");
        forcedBulkingText.append(ComponentPanel.withButton(Component
                .translatable((this.isForcedBulking()) ? "start_core.util.enabled" : "start_core.util.disabled")
                .withStyle(ChatFormatting.UNDERLINE), "forced"));
        textList.add(forcedBulkingText);

        MutableComponent buttonText = Component.translatable("start_core.bulking.type");
        buttonText.append(" ");
        buttonText.append(ComponentPanel.withButton(Component.literal("[<<]"), "prev"));
        buttonText.append(" ");
        buttonText.append(Component.literal(this.getBulkingType().name));
        buttonText.append(" ");
        buttonText.append(ComponentPanel.withButton(Component.literal("[>>]"), "next"));
        textList.add(buttonText);
        return textList;
    }

    default void handleClick(String componentData, ClickData clickData) {
        if (!clickData.isRemote) {
            if (componentData.equals("prev")) {
                this.setBulkingType(this.getBulkingType().prev());
            } else if (componentData.equals("next")) {
                this.setBulkingType(this.getBulkingType().next());
            }
            if (componentData.equals("forced")) {
                this.setForcedBulking(!this.isForcedBulking());
            }
        }
    }

    @Override
    default void writeJadeData(CompoundTag data) {
        if (this.getBulkingType() != null) {
            data.putString("bulking_type", this.getBulkingType().name);
        }
        data.putBoolean("forced_bulking", this.isForcedBulking());
    }

    @Override
    default void appendJadeTooltip(CompoundTag data, Consumer<Component> tooltip) {
        if (data.contains("bulking_type") && data.contains("forced_bulking")) {
            String bulkingType = data.getString("bulking_type");
            tooltip.accept(Component.translatable("ui.start_core.bulking.type", bulkingType));
            boolean forcedBulking = data.getBoolean("forced_bulking");
            tooltip.accept(Component.translatable("ui.start_core.bulking.forced_bulking",
                    Component.translatable((forcedBulking) ? "start_core.util.enabled" : "start_core.util.disabled")));
        }
    }
}
