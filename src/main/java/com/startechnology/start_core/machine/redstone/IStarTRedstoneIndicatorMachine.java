package com.startechnology.start_core.machine.redstone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;

public interface IStarTRedstoneIndicatorMachine {

    /*
     * WeakHashMap means entries will be removed from here automatically if key not referenced elsewhere
     */
    WeakHashMap<IStarTRedstoneIndicatorMachine, List<StarTRedstoneInterfacePartMachine>> HATCH_CACHE
            = new WeakHashMap<>();

    /*
     * Reverse mapping: indicator key -> list of hatches that have this indicator selected
     * Allows efficient updates by only targeting hatches that care about a specific indicator
     */
    WeakHashMap<IStarTRedstoneIndicatorMachine, Map<String, List<StarTRedstoneInterfacePartMachine>>> INDICATOR_TO_HATCH_MAP
            = new WeakHashMap<>();

    /**
     * Declare all of the indicators for this machine initially
     */
    List<StarTRedstoneIndicatorRecord> getInitialIndicators();

    /**
     * Get the indicator map stored in the machine
     */
    StarTRedstoneIndicatorMap getIndicatorMap();

    default void onRedstoneStructureFormed() {
        if (!(this instanceof MultiblockControllerMachine machine)) return;

        List<StarTRedstoneInterfacePartMachine> hatches = machine.getParts()
                .stream()
                .filter(StarTRedstoneInterfacePartMachine.class::isInstance)
                .map(StarTRedstoneInterfacePartMachine.class::cast)
                .toList();

        HATCH_CACHE.put(this, hatches);

        /* Build indicator -> hatches reverse mapping */
        List<StarTRedstoneIndicatorRecord> indicators = getInitialIndicators();
        Map<String, List<StarTRedstoneInterfacePartMachine>> indicatorMap = new HashMap<>();

        indicators.forEach(indicator -> indicatorMap.put(indicator.indicatorKey(), new ArrayList<>()));

        INDICATOR_TO_HATCH_MAP.put(this, indicatorMap);

        /* Initialize machine's indicator map with initial values set to 0 */
        StarTRedstoneIndicatorMap machineMap = getIndicatorMap();
        indicators.forEach(machineMap::put);

        /* Set up reverse mapping */
        hatches.forEach(hatch -> {
            String currentKey = hatch.getCurrentIndicator().indicatorKey();

            indicatorMap.get(currentKey).add(hatch);
        });
    }

    default void onRedstoneStructureInvalid() {
        HATCH_CACHE.remove(this);
        INDICATOR_TO_HATCH_MAP.remove(this);
    }

    /**
     * Updates only the redstone interfaces that have this indicator selected
     * with the new redstone level for an indicator.
     * This is more efficient than updating all hatches.
     */
    default void setIndicatorValue(String indicatorKey, int redstoneLevel) {
        StarTRedstoneIndicatorMap map = getIndicatorMap();
        StarTRedstoneIndicatorRecord existing = map.getRecord(indicatorKey);

        if (existing != null && existing.redstoneLevel().equals(redstoneLevel)) {
            return;
        }

        map.setRedstoneLevel(indicatorKey, redstoneLevel);

        Map<String, List<StarTRedstoneInterfacePartMachine>> indicatorMap = INDICATOR_TO_HATCH_MAP.get(this);

        if (indicatorMap == null || !indicatorMap.containsKey(indicatorKey)) return;
        
        List<StarTRedstoneInterfacePartMachine> affectedHatches = indicatorMap.get(indicatorKey);

        affectedHatches.forEach(StarTRedstoneInterfacePartMachine::modified);
    }

    /**
     * Called when a hatch changes its selected indicator.
     * Updates the reverse mapping to track which hatch has which indicator selected.
     */
    default void updateHatchIndicatorSelection(StarTRedstoneInterfacePartMachine hatch, 
                                               String oldIndicator, String newIndicator) {
        Map<String, List<StarTRedstoneInterfacePartMachine>> indicatorMap = INDICATOR_TO_HATCH_MAP.get(this);

        if (indicatorMap == null) return;
        
        List<StarTRedstoneInterfacePartMachine> oldList = indicatorMap.get(oldIndicator);

        if (oldList != null) {
            oldList.remove(hatch);
        }
        
        List<StarTRedstoneInterfacePartMachine> newList = indicatorMap.computeIfAbsent(newIndicator, k -> new ArrayList<>());

        if (!newList.contains(hatch)) {
            newList.add(hatch);
        }
    }
}