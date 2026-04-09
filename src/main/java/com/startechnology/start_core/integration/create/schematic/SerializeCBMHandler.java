package com.startechnology.start_core.integration.create.schematic;

import codechicken.multipart.block.TileMultipart;
import codechicken.multipart.init.CBMultipartModContent;
import codechicken.multipart.minecraft.McSidedStatePart;
import codechicken.multipart.util.MultipartLoadHandler;
import com.simibubi.create.api.contraption.transformable.MovedBlockTransformerRegistries;
import com.simibubi.create.api.schematic.nbt.SafeNbtWriterRegistry;
import com.simibubi.create.api.schematic.requirement.SchematicRequirementRegistries;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.startechnology.start_core.mixin.create.SchematicLevelAccessor;
import mrtjp.projectred.core.part.IOrientableFacePart;
import mrtjp.projectred.transmission.part.BaseFaceWirePart;
import net.createmod.catnip.levelWrappers.SchematicLevel;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public class SerializeCBMHandler {

    public static void init() {
        SchematicRequirementRegistries.BLOCKS.register(CBMultipartModContent.MULTIPART_BLOCK.get(), (state, blockEntity) -> ItemRequirement.NONE);
        SchematicRequirementRegistries.BLOCK_ENTITIES.register(CBMultipartModContent.MULTIPART_TILE_TYPE.get(), SerializeCBMHandler::getItemRequirements);
        MovedBlockTransformerRegistries.BLOCK_ENTITY_TRANSFORMERS.register(CBMultipartModContent.MULTIPART_TILE_TYPE.get(), SerializeCBMHandler::transformBlockEntity);
        SafeNbtWriterRegistry.REGISTRY.register(CBMultipartModContent.MULTIPART_TILE_TYPE.get(), SerializeCBMHandler::writeSafe);
    }


    private static ItemRequirement getItemRequirements(BlockEntity blockEntity, BlockState state) {
        if (!(blockEntity instanceof MultipartLoadHandler.TileNBTContainer tileNBTContainer))
            return ItemRequirement.INVALID;
        if (tileNBTContainer.tag == null) return ItemRequirement.NONE;

        // in the schematic world, the tile entity isn't instantiated, so we do it here.
        var tileMultipart = TileMultipart.fromNBT(tileNBTContainer.tag, tileNBTContainer.getBlockPos());
        if (tileMultipart == null) return ItemRequirement.NONE;

        return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, tileMultipart.getDrops());
    }

    private static void writeSafe(BlockEntity blockEntity, CompoundTag compoundTag) {
        var newData = blockEntity.saveWithFullMetadata();
        compoundTag.merge(newData);
    }

    private static Rotation invertRotation(Rotation other) {
        return switch (other) {
            case NONE, CLOCKWISE_180 -> other;
            case CLOCKWISE_90 -> Rotation.COUNTERCLOCKWISE_90;
            case COUNTERCLOCKWISE_90 -> Rotation.CLOCKWISE_90;
        };
    }

    private static void transformBlockEntity(BlockEntity blockEntity, StructureTransform structureTransform) {
        if (!(blockEntity instanceof MultipartLoadHandler.TileNBTContainer tileNBTContainer)) return;

        if (tileNBTContainer.tag == null) return;

        var tileMultipart = TileMultipart.fromNBT(tileNBTContainer.tag, tileNBTContainer.getBlockPos());
        if (tileMultipart == null) return;

        var level = Objects.requireNonNull(blockEntity.getLevel());
        tileMultipart.setLevel(level);

        var newParts = tileMultipart.getPartList().stream().map(part -> {
            if (part instanceof IOrientableFacePart orientableFacePart) {
                var rawSide = orientableFacePart.getSide();
                var rawRotation = orientableFacePart.getRotation();
                var side = Direction.values()[rawSide];
                var newSide = structureTransform.rotateFacing(Direction.values()[rawSide]);
                orientableFacePart.setSide(newSide.ordinal());
                if (side.getAxis() == structureTransform.rotationAxis) {
                    var rotation = Rotation.values()[rawRotation];
                    var newRotation = rotation.getRotated(side == Direction.DOWN ? structureTransform.rotation : invertRotation(structureTransform.rotation));
                    orientableFacePart.setRotation(newRotation.ordinal());
                }
            }
            if (part instanceof BaseFaceWirePart connectableFacePart) {
                var side = Direction.values()[connectableFacePart.getSide()];
                var newSide = structureTransform.rotateFacing(side);
                connectableFacePart.preparePlacement(Direction.values()[newSide.ordinal() ^ 1]);
                // check how to set rotation
            }
            // TODO: handle EdgeMicroblockPart
            // TODO: handle FaceMicroblockPart
            // TODO: handle HollowMicroblockPart
            // TODO: handle CornerMicroblockPart
            if (part instanceof McSidedStatePart sidedPart) {
                var newState = structureTransform.apply(sidedPart.getCurrentState());
                try {
                    part = part.getClass().getConstructor(BlockState.class).newInstance(newState);
                } catch (Exception ex) {
                    // ignored
                }
            }
            return part;
        }).toList();

        tileMultipart.clearParts();
        newParts.forEach(tileMultipart::addPart_impl);

        tileMultipart.clearRemoved();

        if (level instanceof SchematicLevel schematicLevel) {
            var aLevel = (SchematicLevelAccessor) schematicLevel;
            aLevel.start_core$getBlockEntities().put(blockEntity.getBlockPos(), tileMultipart);
            var renderedIndex = aLevel.start_core$getRenderedBlockEntities().indexOf(blockEntity);
            if (renderedIndex >= 0) {
                aLevel.start_core$getRenderedBlockEntities().set(renderedIndex, tileMultipart);
            }
            tileMultipart.notifyTileChange();
            tileMultipart.notifyShapeChange();
        } else {
            tileNBTContainer.tag = tileMultipart.saveWithFullMetadata();
        }
    }

}
