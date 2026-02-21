package com.startechnology.start_core.machine.solar;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class StarTSolarCellBlockEntity extends BlockEntity {
    @Getter
    @Setter
    private int temperature = 300;
    @Getter
    @Setter
    private int durability = 500;

    public StarTSolarCellBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void onBlockEntityRegister(BlockEntityType<StarTSolarCellBlockEntity> solarCellBlockEntityBlockEntityType) {}

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.putInt("temperature", temperature);
        tag.putInt("durability", durability);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        this.temperature = tag.getInt("temperature");
        this.durability = tag.getInt("durability");
    }
}
