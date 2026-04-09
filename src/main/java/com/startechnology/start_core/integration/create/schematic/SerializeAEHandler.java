package com.startechnology.start_core.integration.create.schematic;

import appeng.api.inventories.ISegmentedInventory;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.core.definitions.AEBlockEntities;
import appeng.core.definitions.AEBlocks;
import appeng.facade.FacadePart;
import appeng.parts.AEBasePart;
import appeng.util.SettingsFrom;
import com.mojang.datafixers.util.Pair;
import com.simibubi.create.api.contraption.transformable.MovedBlockTransformerRegistries;
import com.simibubi.create.api.schematic.nbt.SafeNbtWriterRegistry;
import com.simibubi.create.api.schematic.requirement.SchematicRequirementRegistries;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class SerializeAEHandler {

    public static void init() {
        SchematicRequirementRegistries.BLOCKS.register(AEBlocks.CABLE_BUS.block(), (state, blockEntity) -> ItemRequirement.NONE);
        for (var blockEntityType : AEBlockEntities.getBlockEntityTypes().values()) {
            SchematicRequirementRegistries.BLOCK_ENTITIES.register(blockEntityType, SerializeAEHandler::getItemRequirements);
            MovedBlockTransformerRegistries.BLOCK_ENTITY_TRANSFORMERS.register(blockEntityType, SerializeAEHandler::transformBlockEntity);
            SafeNbtWriterRegistry.REGISTRY.register(blockEntityType, SerializeAEHandler::writeSafe);
        }
    }

    private static ItemRequirement getItemRequirements(BlockEntity blockEntity, BlockState blockState) {
        if (!(blockEntity instanceof AEBaseBlockEntity baseBlockEntity)) return ItemRequirement.INVALID;

        var list = new ArrayList<ItemStack>();

        if (baseBlockEntity instanceof CableBusBlockEntity cableBusBlockEntity) {
            cableBusBlockEntity.getCableBus().addPartDrops(list);
            cableBusBlockEntity.addAdditionalDrops(cableBusBlockEntity.getLevel(), cableBusBlockEntity.getBlockPos(), list);
        } else {
            baseBlockEntity.addAdditionalDrops(baseBlockEntity.getLevel(), baseBlockEntity.getBlockPos(), list);
        }

        return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, list);
    }

    private static <T extends ISegmentedInventory & Clearable> void clearInventoryExceptUpgrades(T segmentedAndClearable) {
        var upgradesStacks = List.<ItemStack>of();
        var upgrades = segmentedAndClearable.getSubInventory(ISegmentedInventory.UPGRADES);
        if (upgrades != null) {
            upgradesStacks = Lists.newArrayList(upgrades.iterator());
        }
        segmentedAndClearable.clearContent();
        if (!upgradesStacks.isEmpty()) {
            for (var upgrade : upgradesStacks) {
                upgrades.addItems(upgrade);
            }
        }
    }

    private static void writeSafe(BlockEntity blockEntity, CompoundTag data) {
        data.merge(blockEntity.saveWithoutMetadata());
    }

    private static void transformBlockEntity(BlockEntity blockEntity, StructureTransform transform) {
        if (!(blockEntity instanceof AEBaseBlockEntity baseBlockEntity)) return;

        if (baseBlockEntity instanceof CableBusBlockEntity cableBusBlockEntity) {
            var partsWithDirection = Arrays.stream(Direction.values()).flatMap(dir -> {
                var part = cableBusBlockEntity.getCableBus().getPart(dir);
                if (part == null) return Stream.empty();
                return Stream.of(Pair.of(dir, part));
            }).toList();

            // clear inventory except upgrades
            partsWithDirection.stream()
                    .map(Pair::getSecond)
                    .filter(AEBasePart.class::isInstance)
                    .map(AEBasePart.class::cast)
                    .forEach(SerializeAEHandler::clearInventoryExceptUpgrades);

            var partsToMove = partsWithDirection.stream().flatMap(pair -> {
                var dir = pair.getFirst();
                var part = pair.getSecond();
                var newDir = transform.rotateFacing(transform.mirrorFacing(dir));
                if (dir == newDir) return Stream.empty();
                var data = new CompoundTag();
                part.exportSettings(SettingsFrom.MEMORY_CARD, data);
                cableBusBlockEntity.getCableBus().removePartFromSide(dir);
                return Stream.of(Triple.of(newDir, part, data));
            }).toList();

            for (var triple : partsToMove) {
                var dir = triple.getLeft();
                var part = (appeng.api.parts.IPart) triple.getMiddle();
                var data = triple.getRight();

                var newPart = cableBusBlockEntity.getCableBus().addPart(part.getPartItem(), dir, null);
                if (newPart == null) continue; // TODO: error report?
                newPart.importSettings(SettingsFrom.MEMORY_CARD, data, null);
            }

            var facades = Arrays.stream(Direction.values()).flatMap(dir -> {
                var facade = cableBusBlockEntity.getFacadeContainer().getFacade(dir);
                if (facade == null) return Stream.empty();
                var newDir = transform.rotateFacing(transform.mirrorFacing(dir));
                if (dir == newDir) return Stream.empty();
                cableBusBlockEntity.getFacadeContainer().removeFacade(cableBusBlockEntity, dir);
                return Stream.of(Pair.of(newDir, facade));
            }).toList();

            for (var pair : facades) {
                var dir = pair.getFirst();
                var facade = pair.getSecond();
                cableBusBlockEntity.getFacadeContainer().addFacade(new FacadePart(facade.getItemStack(), dir));
            }
            return;
        }

        clearInventoryExceptUpgrades(baseBlockEntity);
    }

}
