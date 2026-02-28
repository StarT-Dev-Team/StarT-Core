package com.startechnology.start_core.machine.solar.cell;

import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static com.startechnology.start_core.block.solar.StarTSolarCellBlocks.START_SOLAR_CELL_BLOCK_ENTITY;

public class StarTSolarCell extends Block implements EntityBlock {
    @Getter
    private final StarTSolarCellType solarCellType;
    @Getter
    private StarTSolarCellBlockEntity solarCellBlockEntity;

    public StarTSolarCell(Block.Properties properties, StarTSolarCellType solarCellType) {
        super(properties);

        this.solarCellType = solarCellType;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        var solarCellBlockEntity = new StarTSolarCellBlockEntity(START_SOLAR_CELL_BLOCK_ENTITY.get(), pos, state, solarCellType.getDurability());

        this.solarCellBlockEntity = solarCellBlockEntity;

        return solarCellBlockEntity;
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

    public int calculateDurabilityDamage(double tempPercent) {
        if (tempPercent < 0.75) return 1;
        if (tempPercent < 0.85) return 2;
        if (tempPercent < 0.95) return 4;
        return 8;
    }

    private static final VoxelShape BOTTOM_SLAB_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return BOTTOM_SLAB_SHAPE;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return BOTTOM_SLAB_SHAPE;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Temperature: " + solarCellBlockEntity.getTemperature() + " °C").withStyle(ChatFormatting.RED));
        tooltip.add(Component.literal("Durability: " + solarCellBlockEntity.getDurability() + "/" + solarCellType.getDurability()).withStyle(ChatFormatting.GREEN));

        super.appendHoverText(stack, level, tooltip, flag);
    }
}
