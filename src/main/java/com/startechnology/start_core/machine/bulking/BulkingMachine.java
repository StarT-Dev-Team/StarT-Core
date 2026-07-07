package com.startechnology.start_core.machine.bulking;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BulkingMachine extends WorkableElectricMultiblockMachine implements IBulking {

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
        this.initParamDefaults();
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed()) {
            this.controllerDisplayText(textList);
        }
    }

    @Override
    public void handleDisplayClick(String componentData, ClickData clickData) {
        this.handleClick(componentData, clickData);
    }
}
