package com.startechnology.start_core.integration.create.schematic;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.machine.multiblock.part.FluidHatchPartMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ItemBusPartMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.*;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.simibubi.create.api.contraption.transformable.MovedBlockTransformerRegistries;
import com.simibubi.create.api.schematic.nbt.SafeNbtWriterRegistry;
import com.simibubi.create.api.schematic.requirement.SchematicRequirementRegistries;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.startechnology.start_core.mixin.CoverBehaviorAccessor;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SerializeGTMHandler {

    public static void init() {

        GTRegistries.MACHINES.registry().values().stream().map(MachineDefinition::getBlockEntityType).distinct().forEach(type -> {
            SafeNbtWriterRegistry.REGISTRY.register(type, SerializeGTMHandler::writeSafe);
            SchematicRequirementRegistries.BLOCK_ENTITIES.register(type, SerializeGTMHandler::getItemRequirements);
            MovedBlockTransformerRegistries.BLOCK_ENTITY_TRANSFORMERS.register(type, SerializeGTMHandler::transformBlockEntity);
        });
    }

    private static void transformBlockEntity(BlockEntity blockEntity, StructureTransform structureTransform) {
        if (!(blockEntity instanceof IMachineBlockEntity machineBlockEntity)) return;
        var metaMachine = machineBlockEntity.getMetaMachine();

        var coverContainer = metaMachine.getCoverContainer();
        var covers = Arrays.stream(Direction.values())
                .map(dir -> {
                    var result = Pair.of(coverContainer.getCoverAtSide(dir), dir);
                    coverContainer.setCoverAtSide(null, dir);
                    return result;
                })
                .filter(s -> Objects.nonNull(s.getFirst())).toList();

        for (var coverPair : covers) {
            var newDirection = structureTransform.rotateFacing(structureTransform.mirrorFacing(coverPair.getSecond()));
            coverContainer.setCoverAtSide(coverPair.getFirst(), newDirection);
            ((CoverBehaviorAccessor)coverPair.getFirst()).start_core$setAttachedSide(newDirection);
        }
    }

    private static ItemRequirement getItemRequirements(BlockEntity blockEntity, BlockState state) {
        if (!(blockEntity instanceof IMachineBlockEntity machineBlockEntity)) return ItemRequirement.INVALID;
        var metaMachine = machineBlockEntity.getMetaMachine();

        var requirements = new ArrayList<ItemStack>();
        var covers = metaMachine.getCoverContainer();
        Arrays.stream(Direction.values()).map(covers::getCoverAtSide).filter(Objects::nonNull).forEach(cover -> {
            var item = cover.getPickItem();
            if (!item.isEmpty()) requirements.add(item);
            requirements.addAll(cover.getAdditionalDrops());
        });
        return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, requirements);
    }

    private static void writeSafe(BlockEntity blockEntity, CompoundTag data) {
        if (!(blockEntity instanceof IMachineBlockEntity machineBlockEntity)) return;
        var metaMachine = machineBlockEntity.getMetaMachine();
        var rawData = blockEntity.saveWithoutMetadata();

        TagTree<Boolean> filters = null;
        Class<?> clz = metaMachine.getClass();
        while (clz != null) {
            filters = nbtFilters.get(clz);
            if (filters != null) break;
            clz = clz.getSuperclass();
        }

        if (filters != null) {
            data.merge(filterCompoundTag(rawData, filters));
        }
    }

    private record TagTree<T>(Map<String, Either<T, TagTree<T>>> tree) {
        public TagTree() {
            this(new HashMap<>());
        }

        public TagTree(List<String> paths, Function<String, T> map) {
            this();
            paths.forEach(s -> put(s, map.apply(s)));
        }

        private static Pair<String, String> splitPath(String path) {
            var index = path.indexOf(".");
            if (index > 0) return Pair.of(path.substring(0, index), path.substring(index + 1));
            return Pair.of(path, "");
        }

        public Optional<Either<T, TagTree<T>>> get(String path) {
            if (path.isEmpty()) return Optional.empty();
            var split = splitPath(path);
            var value = Optional.ofNullable(tree.get(split.getFirst()));
            if (split.getSecond().isEmpty()) return value;
            return value.flatMap(node -> node.right().flatMap(x -> x.get(split.getSecond())));
        }

        public Optional<T> getTail(String path) {
            return get(path).flatMap(node -> node.left());
        }

        public Optional<TagTree<T>> subTree(String path) {
            return get(path).flatMap(node -> node.right());
        }

        public void put(String path, T value) {
            if (path.isEmpty()) return;
            var split = splitPath(path);
            tree.compute(split.getFirst(), (k, v) -> {
                if (split.getSecond().isEmpty()) return Either.left(value);
                var newTree = v == null ? new TagTree<T>() : v.map((ignored) -> new TagTree<T>(), t -> t);
                newTree.put(split.getSecond(), value);
                return Either.right(newTree);
            });
        }
    }

    private static CompoundTag filterCompoundTag(CompoundTag tag, TagTree<Boolean> allowed) {
        var result = new CompoundTag();
        for (var k : tag.getAllKeys()) {
            allowed.get(k).flatMap(either -> either.map(
                    left -> Optional.ofNullable(tag.get(k)),
                    right -> Optional.ofNullable(tag.get(k))
                            .stream().filter(CompoundTag.class::isInstance).map(CompoundTag.class::cast).findFirst()
                            .map(compound -> filterCompoundTag(compound, right))
            )).ifPresent(toAdd -> result.put(k, toAdd));
        }
        return result;
    }

    private static final Map<Class<?>, TagTree<Boolean>> nbtFilters = makeGTWriters(Map.ofEntries(
            Map.entry(MetaMachine.class, List.of("paintingColor", "cover")),
            Map.entry(WorkableMultiblockMachine.class, List.of("activeRecipeType", "isMuffled", "voidingMode")),
            Map.entry(WorkableElectricMultiblockMachine.class, List.of("batchEnabled")),
            Map.entry(TieredIOPartMachine.class, List.of("workingEnabled")),
            Map.entry(ItemBusPartMachine.class, List.of("isDistinct", "circuitSlotEnabled", "circuitInventory", "filterHandler")),
            Map.entry(FluidHatchPartMachine.class, List.of("isDistinct", "circuitSlotEnabled", "circuitInventory", "tank.lockedFluid")),
            Map.entry(MEBusPartMachine.class, List.of("exposeAllSides", "ticksPerCycle")),
            Map.entry(MEHatchPartMachine.class, List.of("exposeAllSides", "ticksPerCycle")),
            Map.entry(MEInputBusPartMachine.class, List.of("inventory.inventory")),
            Map.entry(MEInputHatchPartMachine.class, List.of("tank.inventory")),
            Map.entry(MEStockingBusPartMachine.class, List.of("autoPull", "minStackSize")),
            Map.entry(MEStockingHatchPartMachine.class, List.of("autoPull", "minStackSize"))
    ));

    private static Map<Class<?>, TagTree<Boolean>> makeGTWriters(Map<Class<?>, List<String>> filters) {
        return filters.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    var ret = new TagTree<>(entry.getValue(), v -> true);
                    var clz = entry.getKey().getSuperclass();
                    while (clz != null) {
                        var filter = filters.get(clz);
                        if (filter != null) filter.forEach(f -> ret.put(f, true));
                        clz = clz.getSuperclass();
                    }
                    return ret;
                }));
    }

}
