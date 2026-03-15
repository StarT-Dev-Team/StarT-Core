package com.startechnology.start_core.machine.solar.cell;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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

    public StarTSolarCell(Block.Properties properties, StarTSolarCellType solarCellType) {
        super(properties);

        this.solarCellType = solarCellType;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StarTSolarCellBlockEntity(START_SOLAR_CELL_BLOCK_ENTITY.get(), pos, state, solarCellType.getMaxDurability());
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        BlockEntity be = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);

        if (be instanceof StarTSolarCellBlockEntity solarBlockEntity) {
            ItemStack stack = new ItemStack(this);
            CompoundTag tag = solarBlockEntity.saveWithoutMetadata().copy();

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
        StarTSolarCellType solarCellType = getSolarCellType();

        CompoundTag tag = stack.getTag();

        tooltip.add(Component.translatable("solar.start_core.solar_cell.tooltip1"));
        tooltip.add(Component.translatable("solar.start_core.solar_cell.tooltip2"));
        tooltip.add(Component.translatable("solar.start_core.solar_cell.tooltip3"));
        tooltip.add(Component.translatable("gtceu.universal.tooltip.voltage_out", FormattingUtil.formatNumbers(solarCellType.getEuT()), GTValues.VNF[solarCellType.getTier()]));

        if (tag != null && tag.contains("BlockEntityTag", Tag.TAG_COMPOUND)) {
            CompoundTag beTag = tag.getCompound("BlockEntityTag");

            if (beTag.getBoolean("broken")) {
                tooltip.add(Component.translatable("solar.start_core.solar_cell.is_broken"));
            } else {
                tooltip.add(Component.translatable("solar.start_core.solar_cell.temperature_tooltip", FormattingUtil.formatNumbers(beTag.getDouble("temperature")), this.solarCellType.getMaxTemperature()));
                tooltip.add(Component.translatable("solar.start_core.solar_cell.durability_tooltip", beTag.getInt("durability"), this.solarCellType.getMaxDurability()));
            }
        } else {
            tooltip.add(Component.translatable("solar.start_core.solar_cell.temperature_tooltip", 300, this.solarCellType.getMaxTemperature()));
            tooltip.add(Component.translatable("solar.start_core.solar_cell.durability_tooltip", this.solarCellType.getMaxDurability(), this.solarCellType.getMaxDurability()));
        }

        super.appendHoverText(stack, level, tooltip, flag);
    }
}
