package com.startechnology.start_core.client.megastructure;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntSupplier;

public final class InstanceCollector<T> {

    private final List<T> instances = new ArrayList<>();
    private final Function<T, Vec3> center;
    private final IntSupplier limit;
    private int claimed;

    public InstanceCollector(Function<T, Vec3> center, IntSupplier limit) {
        this.center = center;
        this.limit = limit;
    }

    public void clear() {
        instances.clear();
        claimed = 0;
    }

    public void add(T instance) {
        instances.add(instance);
    }

    public boolean isEmpty() {
        return instances.isEmpty();
    }

    public boolean claim() {
        return claimed++ < limit.getAsInt();
    }

    public List<T> closest(Vec3 camera) {
        instances.sort(Comparator.comparingDouble(instance -> center.apply(instance).distanceToSqr(camera)));
        return instances.subList(0, Math.min(instances.size(), limit.getAsInt()));
    }

    public List<T> overflow() {
        return instances.subList(Math.min(instances.size(), limit.getAsInt()), instances.size());
    }
}
