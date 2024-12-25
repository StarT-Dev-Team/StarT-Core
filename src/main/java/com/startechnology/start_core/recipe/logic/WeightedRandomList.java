package com.startechnology.start_core.recipe.logic;

import java.util.ArrayList;
import java.util.List;

import com.startechnology.start_core.StarTCore;

public class WeightedRandomList<T extends Object> {

    private class Entry {
        double accumulatedWeight;
        T object;
    }

    public WeightedRandomList() {
    }

    private List<Entry> entries = new ArrayList<>();
    private double accumulatedWeight;

    public void addEntry(T object, double weight) {
        accumulatedWeight += weight;
        Entry e = new Entry();
        e.object = object;
        e.accumulatedWeight = accumulatedWeight;
        entries.add(e);
    }

    public T getRandom() {
        double r = StarTCore.RNG.nextDouble() * accumulatedWeight;

        for (Entry entry: entries) {
            if (entry.accumulatedWeight >= r) {
                return entry.object;
            }
        }
        return null; //should only happen when there are no entries
    }
}