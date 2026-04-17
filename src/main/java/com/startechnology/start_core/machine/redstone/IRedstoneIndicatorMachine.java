package com.startechnology.start_core.machine.redstone;

import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;

import java.util.List;

public interface IRedstoneIndicatorMachine extends IMultiController {

    List<RedstoneIndicatorRecord> getInitialIndicators();

    default RedstoneIndicatorsLogic getRedstoneIndicatorsTrait() {
        var self = (IMultiblockControllerMachineRedstoneIndicators)this.self();
        return self.start_core$getRedstoneIndicatorsTrait();
    }

    default void setIndicatorValue(String indicatorKey, int redstoneLevel) {
        getRedstoneIndicatorsTrait().setIndicatorValue(indicatorKey, redstoneLevel);
    }

    default int getIndicatorValue(String indicatorKey) {
        return getRedstoneIndicatorsTrait().getIndicator(indicatorKey).redstoneLevel();
    }

}
