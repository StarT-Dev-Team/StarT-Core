package com.startechnology.start_core.machine.bulking;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.startechnology.start_core.recipe.StarTParallelTypes;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BulkingMachine extends WorkableElectricMultiblockMachine {

    @Persisted
    @Getter
    @Setter
    private BulkingType bulkingType;

    @Persisted
    @Getter
    @Setter
    private boolean forcedBulking;

    public BulkingMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        this.bulkingType = BulkingType.BUKLING_4_3;
        this.forcedBulking = false;
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (machine instanceof BulkingMachine bulkingMachine && bulkingMachine.isFormed()) {
            int throughputModifier = bulkingMachine.getBulkingType().throughputModifier;
            double durationModifier = bulkingMachine.getBulkingType().durationModifier;

            var parallelsAvailable = Math.max(0,
                    ParallelLogic.getParallelAmountWithoutEU(machine, recipe, throughputModifier));

            if (parallelsAvailable >= throughputModifier) {

                return ModifierFunction.builder()
                        .modifyAllContents(ContentModifier.multiplier(throughputModifier))
                        .durationMultiplier(durationModifier)
                        .parallels(throughputModifier, StarTParallelTypes.BULK_PROCESSING)
                        .build();

            } else if (bulkingMachine.isForcedBulking()) {
                return ModifierFunction.cancel(Component.literal("Not enough inputs to force bulk!"));
            }
        }
        return ModifierFunction.IDENTITY;
    }

    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed()) {
            textList.add(Component.literal(
                    "Throughput multiplier: " + ChatFormatting.GREEN + this.getBulkingType().throughputModifier));
            textList.add(Component
                    .literal("Duration multiplier: " + ChatFormatting.GREEN + this.getBulkingType().durationModifier));

            MutableComponent forcedBulkingText = Component.literal("Forced bulking: " +
                    ((this.isForcedBulking()) ? ChatFormatting.GREEN + "Enabled" : ChatFormatting.RED + "Disabled"));
            forcedBulkingText.append(" ");
            forcedBulkingText.append(ComponentPanelWidget.withButton(Component.literal("[switch]"), "forced"));
            textList.add(forcedBulkingText);

            MutableComponent buttonText = Component.literal("Bulking type: ");
            buttonText.append(" ");
            buttonText.append(ComponentPanelWidget.withButton(Component.literal("[<<]"), "prev"));
            buttonText.append(" ");
            buttonText.append(Component.literal(this.getBulkingType().name));
            buttonText.append(" ");
            buttonText.append(ComponentPanelWidget.withButton(Component.literal("[>>]"), "next"));
            textList.add(buttonText);
        }
    }

    public void handleDisplayClick(String componentData, ClickData clickData) {
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

enum BulkingType {

    BUKLING_4_3(4, 3.25),
    BULKING_8_6(8, 6.5),
    BULKING_16_13(16, 13),
    BULKING_32_26(32, 26),
    BULKING_64_52(64, 52);

    public int throughputModifier;
    public double durationModifier;
    public String name;

    private BulkingType(int throughputModifier, double durationModifier) {
        this.throughputModifier = throughputModifier;
        this.durationModifier = durationModifier;
        this.name = String.format("%d:%d", throughputModifier, durationModifier);
    }

    public BulkingType next() {
        return values()[(this.ordinal() + 1) % values().length];
    }

    public BulkingType prev() {
        return values()[(this.ordinal() + values().length - 1) % values().length];
    }
}
