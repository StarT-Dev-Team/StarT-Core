package com.startechnology.start_core.machine.modular;

import java.util.List;
import java.util.function.Consumer;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.TickTask;

public class StarTModularConduitAutoScalingHatchPartMachine extends StarTModularConduitHatchPartMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            StarTModularConduitAutoScalingHatchPartMachine.class,
            StarTModularConduitHatchPartMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    private long persistedVoltage = 0L;

    @Persisted
    private long persistedAmperage = 0L;
    private static final long BASE_VOLTAGE = GTValues.V[GTValues.ULV];

    public StarTModularConduitAutoScalingHatchPartMachine(IMachineBlockEntity holder, IO io, int tier) {
        super(holder, io, GTValues.ULV, 1);
        initMaxEnergyContainer();
    }

    private void initMaxEnergyContainer() {
        if (this.io == IO.IN) {
            this.energyContainer.resetBasicInfo(Long.MAX_VALUE, BASE_VOLTAGE, 1, 0, 0);
        } else {
            this.energyContainer.resetBasicInfo(Long.MAX_VALUE, 0, 0, BASE_VOLTAGE, 1);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (!LDLib.isRemote()) {
            getLevel().getServer().tell(new TickTask(0, this::restoreEnergyContainer));
        }
    }

    private void restoreEnergyContainer() {
        if (persistedVoltage > BASE_VOLTAGE && persistedAmperage > 0) {
            long maxCapacity = persistedVoltage * 64L * persistedAmperage;
            if (this.io == IO.IN) {
                this.energyContainer.resetBasicInfo(maxCapacity, persistedVoltage, persistedAmperage, 0, 0);
            } else {
                this.energyContainer.resetBasicInfo(maxCapacity, 0, 0, persistedVoltage, persistedAmperage);
            }
        } else {
            long maxCapacity = BASE_VOLTAGE * 64L * 1;

            if (this.io == IO.IN) {
                this.energyContainer.resetBasicInfo(maxCapacity, BASE_VOLTAGE, 1, 0, 0);
            } else {
                this.energyContainer.resetBasicInfo(maxCapacity, 0, 0, BASE_VOLTAGE, 1);
            }
        }
    }

    public void scaleNewEnergyContainer(long voltage, long amperage) {
        long maxCapacity = voltage * 64L * amperage;

        if (getScaledVoltage() == voltage && getScaledAmperage() == amperage) {
            return;
        }

        this.persistedVoltage = voltage;
        this.persistedAmperage = amperage;

        if (this.io == IO.IN) {
            this.energyContainer.resetBasicInfo(maxCapacity, voltage, amperage, 0, 0);
            /* Reform module to update multiblock state (is this a crime?) */
            if (this.controllers.size() > 0) {
                for (var controller : this.controllers) {
                    controller.onPartUnload();
                    controller.onStructureFormed();
                    
                    /* Hopefully this invokes recipe finding logic so the machine doesnt just halt */
                    if (controller instanceof WorkableMultiblockMachine workableMultiblock) {
                        workableMultiblock.getRecipeLogic().serverTick();
                    }
                }
            }

        } else {
            this.energyContainer.resetBasicInfo(maxCapacity, 0, 0, voltage, amperage);
        }

        /* Update the max amount of energy stored in the hatch to the cap */
        this.energyContainer.setEnergyStored(Math.min(this.energyContainer.getEnergyStored(), maxCapacity));
    }


    public static void addDisplayTextToList(Consumer<Component> componentAdder, long scaledVoltage, long scaledAmperage) {
        if (scaledVoltage > BASE_VOLTAGE && scaledAmperage > 0) {
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

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}
