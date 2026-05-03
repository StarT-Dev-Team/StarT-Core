package com.startechnology.start_core.integration.create.schematic;

import net.minecraft.nbt.CompoundTag;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NbtRemoverDefinition {

    private Map<Class<?>, NbtRemover> removers;

    public NbtRemoverDefinition(Map<Class<?>, List<String>> filters) {
        removers = filters.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    var ret = new NbtRemover(entry.getValue());
                    var clz = entry.getKey().getSuperclass();
                    while (clz != null) {
                        var filter = filters.get(clz);
                        if (filter != null) filter.forEach(ret::addAllowed);
                        clz = clz.getSuperclass();
                    }
                    return ret;
                }));
    }

    public CompoundTag apply(Class<?> clz, CompoundTag tag) {
        NbtRemover remover = null;
        while (clz != null) {
            remover = removers.get(clz);
            if (remover != null) break;
            clz = clz.getSuperclass();
        }
        if (remover == null) return null;
        return remover.apply(tag);
    }
}
