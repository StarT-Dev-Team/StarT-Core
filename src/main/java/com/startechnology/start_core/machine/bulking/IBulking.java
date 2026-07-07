package com.startechnology.start_core.machine.bulking;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IBulking {

    public BulkingType getBulkingType();

    public void setBulkingType(BulkingType type);

    public boolean isForcedBulking();

    public void setForcedBulking(boolean forced);

    default void initParamDefaults() {
        this.setBulkingType(BulkingType.BUKLING_4_3);
        this.setForcedBulking(false);
    }

    default List<Component> controllerDisplayText(List<Component> textList) {
        textList.add(Component.translatable("start_core.bulking.throughput_multiplier",
                this.getBulkingType().throughputModifier));
        textList.add(Component.translatable("start_core.bulking.duration_multiplier",
                this.getBulkingType().durationModifier));

        MutableComponent forcedBulkingText = Component.translatable("start_core.bulking.forced");
        forcedBulkingText.append(ComponentPanelWidget.withButton(Component
                .translatable((this.isForcedBulking()) ? "start_core.util.enabled" : "start_core.util.disabled")
                .withStyle(ChatFormatting.UNDERLINE), "forced"));
        textList.add(forcedBulkingText);

        MutableComponent buttonText = Component.translatable("start_core.bulking.type");
        buttonText.append(" ");
        buttonText.append(ComponentPanelWidget.withButton(Component.literal("[<<]"), "prev"));
        buttonText.append(" ");
        buttonText.append(Component.literal(this.getBulkingType().name));
        buttonText.append(" ");
        buttonText.append(ComponentPanelWidget.withButton(Component.literal("[>>]"), "next"));
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
}
