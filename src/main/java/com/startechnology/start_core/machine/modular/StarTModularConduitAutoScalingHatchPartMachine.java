package com.startechnology.start_core.machine.modular;

import java.util.List;
import java.util.function.Consumer;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.client.util.TooltipHelper;
import com.gregtechceu.gtceu.integration.jade.provider.RecipeLogicProvider;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.gregtechceu.gtceu.utils.GTUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import com.startechnology.start_core.mixin.WorkableElectricMultiblockMachineAccessor;

public class StarTModularConduitAutoScalingHatchPartMachine extends StarTModularConduitHatchPartMachine {

    public StarTModularConduitAutoScalingHatchPartMachine(IMachineBlockEntity holder, IO io, int tier) {
        super(holder, io, tier, 0);
    }

    public void scaleNewEnergyContainer(long voltage, long amperage) {
        long maxCapacity = voltage * 64L * amperage;

        if (getScaledVoltage() == voltage && getScaledAmperage() == amperage) {
            return;
        }

        if (this.io == IO.IN) {
            this.energyContainer.resetBasicInfo(maxCapacity, voltage, amperage, 0, 0);
            /* Reform module to update multiblock state (is this a crime?) */
            if (this.controllers.size() > 0) {
                for (var controller : this.controllers) {
                    controller.onStructureInvalid();
                    controller.onStructureFormed();
                }
            }

        } else {
            this.energyContainer.resetBasicInfo(maxCapacity, 0, 0, voltage, amperage);
        }
    }

    public static void addDisplayTextToList(Consumer<Component> componentAdder, long scaledVoltage, long scaledAmperage) {
        if (scaledAmperage > 0 && scaledVoltage > 0) {
            var tier = GTUtil.getTierByVoltage(scaledVoltage);

            componentAdder.accept(
                    Component.translatable("modular.start_core.scaled_voltage", Component.literal(GTValues.VNF[tier]))
                            .withStyle(ChatFormatting.YELLOW));

            componentAdder.accept(Component
                    .translatable("modular.start_core.scaled_amperage",
                            Component.literal(FormattingUtil.formatNumbers(scaledAmperage))
                                    .withStyle(ChatFormatting.WHITE))
                    .withStyle(ChatFormatting.YELLOW));

        } else {
            componentAdder.accept(Component.translatable("modular.start_core.no_scaling").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    protected void addComponentPanelText(List<Component> componentList) {
        addDisplayTextToList((component) -> componentList.add(component), getScaledVoltage(), getScaledAmperage());
        componentList.add(Component.empty());
        super.addComponentPanelText(componentList);
    }

    public long getScaledVoltage() {
        return this.io == IO.IN ? this.energyContainer.getInputVoltage() : this.energyContainer.getOutputVoltage();
    }

    public long getScaledAmperage() {
        return this.io == IO.IN ? this.energyContainer.getInputAmperage() : this.energyContainer.getOutputAmperage();
    }
}
