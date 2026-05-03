package com.startechnology.start_core.integration.create.schematic;

import com.gregtechceu.gtceu.api.blockentity.PipeBlockEntity;
import com.gregtechceu.gtceu.api.capability.ICoverable;
import com.gregtechceu.gtceu.api.cover.CoverBehavior;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.pipenet.Node;
import com.gregtechceu.gtceu.api.pipenet.PipeCoverContainer;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.data.GTBlockEntities;
import com.gregtechceu.gtceu.common.machine.multiblock.part.FluidHatchPartMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ItemBusPartMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.*;
import com.mojang.datafixers.util.Pair;
import com.simibubi.create.api.contraption.transformable.MovedBlockTransformerRegistries;
import com.simibubi.create.api.schematic.nbt.SafeNbtWriterRegistry;
import com.simibubi.create.api.schematic.requirement.SchematicRequirementRegistries;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.startechnology.start_core.mixin.CoverBehaviorAccessor;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.*;

public class SerializeGTMHandler {

    public static void init() {
        GTRegistries.MACHINES.registry().values().stream().map(MachineDefinition::getBlockEntityType).distinct().forEach(type -> {
            SafeNbtWriterRegistry.REGISTRY.register(type, SerializeGTMHandler::machineWriteSafe);
            SchematicRequirementRegistries.BLOCK_ENTITIES.register(type, SerializeGTMHandler::machineItemRequirements);
            MovedBlockTransformerRegistries.BLOCK_ENTITY_TRANSFORMERS.register(type, SerializeGTMHandler::machineTransform);
        });

        var gtPipes = List.of(GTBlockEntities.CABLE, GTBlockEntities.FLUID_PIPE, GTBlockEntities.ITEM_PIPE, GTBlockEntities.LASER_PIPE, GTBlockEntities.OPTICAL_PIPE, GTBlockEntities.DUCT_PIPE);
        gtPipes.stream().map(RegistryEntry::get).forEach(type -> {
            SafeNbtWriterRegistry.REGISTRY.register(type, SerializeGTMHandler::cableWriteSafe);
            SchematicRequirementRegistries.BLOCK_ENTITIES.register(type, SerializeGTMHandler::cableItemRequirements);
            MovedBlockTransformerRegistries.BLOCK_ENTITY_TRANSFORMERS.register(type, SerializeGTMHandler::transformCable);
        });
    }

    private static void cableWriteSafe(BlockEntity blockEntity, CompoundTag data) {
        if (!(blockEntity instanceof PipeBlockEntity<?, ?> pipeBlockEntity)) return;
        var rawData = blockEntity.saveWithoutMetadata();
        var filtered = cableNbtFilters.apply(pipeBlockEntity.getClass(), rawData);
        if (filtered != null) data.merge(filtered);
    }

    private static void machineTransform(BlockEntity blockEntity, StructureTransform structureTransform) {
        if (!(blockEntity instanceof IMachineBlockEntity machineBlockEntity)) return;
        var metaMachine = machineBlockEntity.getMetaMachine();
        transformCoverContainer(metaMachine.getCoverContainer(), structureTransform);
    }

    private static void transformCable(BlockEntity blockEntity, StructureTransform structureTransform) {
        if (!(blockEntity instanceof PipeBlockEntity<?, ?> pipeBlockEntity)) return;

        var connections = pipeBlockEntity.getConnections();
        var blockedConnections = pipeBlockEntity.getBlockedConnections();
        var newConnections = Node.ALL_CLOSED;
        var newBlockedConnections = Node.ALL_CLOSED;
        for (var direction : Direction.values()) {
            var newDirection = structureTransform.rotateFacing(structureTransform.mirrorFacing(direction));
            var mask = 1 << direction.ordinal();
            if ((connections & mask) != 0) {
                newConnections |= 1 << newDirection.ordinal();
            }
            if ((blockedConnections & mask) != 0) {
                newBlockedConnections |= 1 << newDirection.ordinal();
            }
        }
        pipeBlockEntity.setBlockedConnections(newConnections);
        pipeBlockEntity.setConnections(newBlockedConnections);
        transformCoverContainer(pipeBlockEntity.getCoverContainer(), structureTransform);
    }

    private static void transformCoverContainer(ICoverable coverContainer, StructureTransform structureTransform) {
        List<Pair<CoverBehavior, Direction>> covers = Arrays.stream(Direction.values())
                .map(dir -> {
                    var result = Pair.of(coverContainer.getCoverAtSide(dir), dir);
                    coverContainer.setCoverAtSide(null, dir);
                    return result;
                })
                .filter(s -> Objects.nonNull(s.getFirst()))
                .toList();

        for (var coverPair : covers) {
            var newDirection = structureTransform.rotateFacing(structureTransform.mirrorFacing(coverPair.getSecond()));
            coverContainer.setCoverAtSide(coverPair.getFirst(), newDirection);
            ((CoverBehaviorAccessor)coverPair.getFirst()).start_core$setAttachedSide(newDirection);
        }
    }

    private static ItemRequirement machineItemRequirements(BlockEntity blockEntity, BlockState state) {
        if (!(blockEntity instanceof IMachineBlockEntity machineBlockEntity)) return ItemRequirement.INVALID;
        var metaMachine = machineBlockEntity.getMetaMachine();
        var requirements = new ArrayList<>(getCoverContainerRequirements(metaMachine.getCoverContainer()));
        return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, requirements);
    }

    private static ItemRequirement cableItemRequirements(BlockEntity blockEntity, BlockState state) {
        if (!(blockEntity instanceof PipeBlockEntity<?, ?> pipeBlockEntity)) return ItemRequirement.INVALID;
        var requirements = new ArrayList<>(getCoverContainerRequirements(pipeBlockEntity.getCoverContainer()));
        return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, requirements);
    }

    private static List<ItemStack> getCoverContainerRequirements(@Nullable ICoverable covers) {
        if (covers == null) return List.of();
        var requirements = new ArrayList<ItemStack>();
        Arrays.stream(Direction.values()).map(covers::getCoverAtSide).filter(Objects::nonNull).forEach(cover -> {
            var item = cover.getPickItem();
            if (!item.isEmpty()) requirements.add(item);
            requirements.addAll(cover.getAdditionalDrops());
        });
        return requirements;
    }

    private static void machineWriteSafe(BlockEntity blockEntity, CompoundTag data) {
        if (!(blockEntity instanceof IMachineBlockEntity machineBlockEntity)) return;
        var metaMachine = machineBlockEntity.getMetaMachine();
        var rawData = blockEntity.saveWithoutMetadata();
        var filtered = metaMachineNbtFilters.apply(metaMachine.getClass(), rawData);
        if (filtered != null) data.merge(filtered);
    }

    private static final NbtRemoverDefinition metaMachineNbtFilters = new NbtRemoverDefinition(Map.ofEntries(
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

    private static final NbtRemoverDefinition cableNbtFilters = new NbtRemoverDefinition(Map.ofEntries(
            Map.entry(PipeBlockEntity.class, List.of("paintingColor", "cover", "connections", "blockedConnections"))
    ));

}
