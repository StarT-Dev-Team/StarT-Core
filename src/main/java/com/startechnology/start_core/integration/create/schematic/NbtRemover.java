package com.startechnology.start_core.integration.create.schematic;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class NbtRemover {

    private final TagTree allowed;

    public NbtRemover(List<String> allowed) {
        this.allowed = new TagTree(allowed, s -> new TagEntry());
    }

    public void addAllowed(String path) {
        allowed.put(path, new TagEntry());
    }

    public CompoundTag apply(CompoundTag tag) {
        return allowed.applyFilter(tag);
    }

    private record TagEntry() {

    }

    private record TagTree(Map<String, Either<TagEntry, TagTree>> tree) {
        public TagTree() {
            this(new HashMap<>());
        }

        public TagTree(List<String> paths, Function<String, TagEntry> map) {
            this();
            paths.forEach(s -> put(s, map.apply(s)));
        }

        private CompoundTag applyFilter(CompoundTag tag) {
            var result = new CompoundTag();
            for (var k : tag.getAllKeys()) {
                get(k).flatMap(either -> either.map(
                        left -> Optional.ofNullable(tag.get(k)),
                        right -> Optional.ofNullable(tag.get(k))
                                .stream().filter(CompoundTag.class::isInstance).map(CompoundTag.class::cast).findFirst()
                                .map(right::applyFilter)
                )).ifPresent(toAdd -> result.put(k, toAdd));
            }
            return result;
        }

        private static Pair<String, String> splitPath(String path) {
            var index = path.indexOf(".");
            if (index > 0) return Pair.of(path.substring(0, index), path.substring(index + 1));
            return Pair.of(path, "");
        }

        public Optional<Either<TagEntry, TagTree>> get(String path) {
            if (path.isEmpty()) return Optional.empty();
            var split = splitPath(path);
            var value = Optional.ofNullable(tree.get(split.getFirst()));
            if (split.getSecond().isEmpty()) return value;
            return value.flatMap(node -> node.right().flatMap(x -> x.get(split.getSecond())));
        }

        public Optional<TagEntry> getTail(String path) {
            return get(path).flatMap(node -> node.left());
        }

        public Optional<TagTree> subTree(String path) {
            return get(path).flatMap(node -> node.right());
        }

        public void put(String path, TagEntry value) {
            if (path.isEmpty()) return;
            var split = splitPath(path);
            tree.compute(split.getFirst(), (k, v) -> {
                if (split.getSecond().isEmpty()) return Either.left(value);
                var newTree = v == null ? new TagTree() : v.map((ignored) -> new TagTree(), t -> t);
                newTree.put(split.getSecond(), value);
                return Either.right(newTree);
            });
        }
    }

}
