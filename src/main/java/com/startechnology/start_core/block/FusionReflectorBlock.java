package com.startechnology.start_core.block;

import com.gregtechceu.gtceu.api.block.ActiveBlock;
import com.startechnology.start_core.api.reflector.FusionReflectorType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FusionReflectorBlock extends ActiveBlock {

    public FusionReflectorType reflectorType;

    public FusionReflectorBlock(BlockBehaviour.Properties properties, FusionReflectorType reflectorType) {
        super(properties);
        this.reflectorType = reflectorType;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        var tier = reflectorType.getTier();
        tooltip.add(Component.translatable("start_core.machine.reflector.tooltip_reflector_tier", "T%d".formatted(tier)));
    }
}
