package com.startechnology.start_core.machine.solar.cell;

import com.gregtechceu.gtceu.syncdata.LegacyManagedData;
import com.lowdragmc.lowdraglib2.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib2.syncdata.holder.blockentity.ISyncPersistRPCBlockEntity;
import com.lowdragmc.lowdraglib2.syncdata.storage.FieldManagedStorage;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class StarTSolarCellBlockEntity extends BlockEntity implements ISyncPersistRPCBlockEntity {

    @Getter
    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    @Getter
    @Setter
    @Persisted
    private double temperature = 300;
    @Getter
    @Setter
    @Persisted
    private int durability;
    @Getter
    @Setter
    @Persisted
    private boolean broken;

    public StarTSolarCellBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int durability) {
        super(type, pos, state);

        this.durability = durability;
        this.broken = false;
    }

    public static void onBlockEntityRegister(BlockEntityType<? extends BlockEntity> solarCellBlockEntityBlockEntityType) {}

    @Override
    public void load(CompoundTag tag) {
        LegacyManagedData.upgradeBlockEntityTag(getRootStorage(), tag, getSyncTag());
        super.load(tag);
    }
}
