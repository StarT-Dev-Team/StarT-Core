package com.startechnology.start_core.machine.redstone;

import java.util.List;
import java.util.WeakHashMap;

import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;

public interface IStarTRedstoneIndicatorMachine {

    /*
     * WeakHashMap means entries will be removed from here automatically if key not referenced elsewhere
     */
    WeakHashMap<IStarTRedstoneIndicatorMachine, List<StarTRedstoneInterfacePartMachine>> HATCH_CACHE
            = new WeakHashMap<>();

    /**
     * Declare all of the indicators for this machine initially
     */
    List<StarTRedstoneIndicatorRecord> getInitialIndicators();

    default void onRedstoneStructureFormed() {
        if (!(this instanceof MultiblockControllerMachine machine)) return;

        List<StarTRedstoneInterfacePartMachine> hatches = machine.getParts()
                .stream()
                .filter(StarTRedstoneInterfacePartMachine.class::isInstance)
                .map(StarTRedstoneInterfacePartMachine.class::cast)
                .toList();

        HATCH_CACHE.put(this, hatches);

        /* Update hatches with the initial values */
        List<StarTRedstoneIndicatorRecord> indicators = getInitialIndicators();
        hatches.forEach(hatch -> {
            hatch.clearIndicators();
            indicators.forEach(hatch::putIndicator);
        });
    }

    default void onRedstoneStructureInvalid() {
        HATCH_CACHE.remove(this);
    }

    /**
     * Updates all the associated redstone interfaces on this machine
     * with the new redstone level for an indicator
     */
    default void setIndicatorValue(String indicatorKey, int redstoneLevel) {
        List<StarTRedstoneInterfacePartMachine> hatches = HATCH_CACHE.get(this);
        if (hatches == null || hatches.isEmpty()) return;
        hatches.forEach(hatch -> hatch.updateIndicator(indicatorKey, redstoneLevel));
    }
}