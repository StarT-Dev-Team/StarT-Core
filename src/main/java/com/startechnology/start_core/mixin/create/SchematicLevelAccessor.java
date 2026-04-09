package com.startechnology.start_core.mixin.create;

import net.createmod.catnip.levelWrappers.SchematicLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(SchematicLevel.class)
public interface SchematicLevelAccessor {

    @Accessor("blockEntities")
    Map<BlockPos, BlockEntity> start_core$getBlockEntities();

    @Accessor("renderedBlockEntities")
    List<BlockEntity> start_core$getRenderedBlockEntities();


}
