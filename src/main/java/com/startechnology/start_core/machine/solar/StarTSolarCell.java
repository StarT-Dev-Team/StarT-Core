package com.startechnology.start_core.machine.solar;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static com.startechnology.start_core.block.solar.StarTSolarCellBlocks.START_SOLAR_CELL_BLOCK_ENTITY;

public class StarTSolarCell extends Block implements EntityBlock {
    @Getter
    @Setter
    private int tier;

    public StarTSolarCell(Block.Properties properties, int tier) {
        super(properties);

        this.tier = tier;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StarTSolarCellBlockEntity(START_SOLAR_CELL_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {

        BlockEntity be = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);

        if (be instanceof StarTSolarCellBlockEntity solarBlockEntity) {

            ItemStack stack = new ItemStack(this);

            CompoundTag tag = solarBlockEntity.saveWithoutMetadata();

            stack.getOrCreateTag().put("BlockEntityTag", tag);

            return Collections.singletonList(stack);
        }

        return super.getDrops(state, builder);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {

        if (stack.hasTag() && stack.getTag().contains("BlockEntityTag")) {

            BlockEntity be = level.getBlockEntity(pos);

            if (be instanceof StarTSolarCellBlockEntity solarBlockEntity) {
                CompoundTag tag = stack.getTag().getCompound("BlockEntityTag");

                solarBlockEntity.load(tag);
            }
        }

        super.setPlacedBy(level, pos, state, placer, stack);
    }
}
