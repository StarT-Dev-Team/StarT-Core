package com.startechnology.start_core.block;

import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.vacuumpump.IVacuumPumpType;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class VacuumPumpBlock extends Block {

    public IVacuumPumpType pumpTier;

    public VacuumPumpBlock(Properties properties, IVacuumPumpType pumpTier) {
        super(properties);
        this.pumpTier = pumpTier;
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockAndTintGetter level, BlockPos pos, Direction side, @Nullable BlockState sourceState, @Nullable BlockPos sourcePos) {
        return defaultBlockState();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("start_core.machine.vacuum_pump.tooltip_cap", pumpTier.formatCap()));
        tooltip.add(Component.translatable("start_core.machine.vacuum_pump.tooltip_rate", pumpTier.formatRate()));
    }

    public enum VacuumPumpType implements IVacuumPumpType {

        // TODO: add textures
        ZPM("zpm", 5, 80, StarTCore.resourceLocation("block/pumps/zpm")),
        UV("uv", 10, 85, StarTCore.resourceLocation("block/pumps/uv")),
        UHV("uhv", 15, 90, StarTCore.resourceLocation("block/pumps/uhv")),
        UEV("uev", 20, 95, StarTCore.resourceLocation("block/pumps/uev")),
        UIV("uiv", 25, 100, StarTCore.resourceLocation("block/pumps/uiv"));

        @Getter
        public final String name;

        @Getter
        public final int rate;

        @Getter
        public final int cap;

        @Getter
        public final ResourceLocation texture;

        VacuumPumpType(String name, int rate, int cap, ResourceLocation texture) {
            this.name = name;
            this.rate = rate;
            this.cap = cap;
            this.texture = texture;
        }

        @Override
        public int getTier() {
            return this.ordinal();
        }

    }
}
