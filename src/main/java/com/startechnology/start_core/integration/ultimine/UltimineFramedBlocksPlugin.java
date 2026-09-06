package com.startechnology.start_core.integration.ultimine;

import dev.ftb.mods.ftbultimine.FTBUltiminePlayerData;
import dev.ftb.mods.ftbultimine.api.rightclick.RegisterRightClickHandlerEvent;
import dev.ftb.mods.ftbultimine.shape.ShapeContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import xfacthd.framedblocks.api.block.IFramedBlock;

import java.util.Collection;

public class UltimineFramedBlocksPlugin {

    public static void init() {
        RegisterRightClickHandlerEvent.REGISTER
                .register(dispatcher -> dispatcher.registerHandler(UltimineFramedBlocksPlugin::handleRightClickBlock));
    }

    private static boolean isFramedBlock(BlockState state) {
        var block = state.getBlock();
        return block instanceof IFramedBlock;
    }

    private static int handleRightClickBlock(ShapeContext shapeContext, InteractionHand hand,
                                             Collection<BlockPos> positions) {
        var targetBlockState = shapeContext.block(shapeContext.pos());
        if (!isFramedBlock(targetBlockState)) return 0;

        var player = shapeContext.player();
        var hitResult = FTBUltiminePlayerData.rayTrace(player);
        if (!(hitResult instanceof BlockHitResult blockHitResult)) return 0;

        var blockPos = blockHitResult.getBlockPos();
        var relativeHitLocation = blockHitResult.getLocation().subtract(blockPos.getCenter());
        var hitFace = blockHitResult.getDirection();
        var originalFace = targetBlockState.getOptionalValue(BlockStateProperties.HORIZONTAL_FACING)
                .orElse(Direction.NORTH);

        return (int) positions.stream().filter(pos -> {
            var blockState = shapeContext.block(pos);
            if (!(blockState.getBlock() instanceof IFramedBlock framedBlock)) return false;

            var facing = blockState.getOptionalValue(BlockStateProperties.HORIZONTAL_FACING).orElse(Direction.NORTH);
            var rotatedHitLocation = new Vec3(relativeHitLocation.toVector3f().rotate(rotation(originalFace, facing)));
            var rotatedFace = rotateDirection(hitFace, originalFace, facing);

            var interaction = framedBlock.handleUse(
                    blockState,
                    player.level(),
                    pos,
                    player,
                    hand,
                    new BlockHitResult(pos.getCenter().add(rotatedHitLocation), rotatedFace, pos, false));

            return interaction.consumesAction();
        }).count();
    }

    private static Quaternionf rotation(Direction from, Direction to) {
        return to.getRotation().mul(from.getRotation().invert(new Quaternionf()));
    }

    private static Direction rotateDirection(Direction dir, Direction from, Direction to) {
        var normal = dir.step().rotate(rotation(from, to));
        return Direction.getNearest(normal.x(), normal.y(), normal.z());
    }
}
