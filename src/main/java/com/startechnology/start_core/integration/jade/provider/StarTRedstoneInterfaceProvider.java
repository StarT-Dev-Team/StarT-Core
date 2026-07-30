package com.startechnology.start_core.integration.jade.provider;

import com.gregtechceu.gtceu.integration.jade.provider.CapabilityBlockProvider;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.capability.StarTCapabilityHelper;
import com.startechnology.start_core.integration.jade.StarTJadeUtils;
import com.startechnology.start_core.machine.redstone.RedstoneIndicatorRecord;
import com.startechnology.start_core.machine.redstone.RedstoneInterfacePartMachine;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Optional;

public class StarTRedstoneInterfaceProvider extends CapabilityBlockProvider<RedstoneInterfacePartMachine> {

    public StarTRedstoneInterfaceProvider() {
        super(StarTCore.resourceLocation("variadic_redstone_info"));
    }

    @Override
    protected @Nullable RedstoneInterfacePartMachine getCapability(Level level, BlockPos pos,
                                                                   @Nullable Direction side) {
        return StarTCapabilityHelper.getRedstoneInterfacePartMachine(level, pos, side);
    }

    /* Used for storing data for the addTooltip method? */
    @Override
    protected void write(CompoundTag data, RedstoneInterfacePartMachine capability) {
        data.putInt("signal_level", capability.getRedstoneValue());
        data.putString("indicator", Component.Serializer.toJson(capability.getCurrentIndicator().indicatorComponent()));
    }

    /* Adds a new tooltip under the Jade stuff */
    @Override
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, Player player, BlockAccessor block,
                              BlockEntity blockEntity, IPluginConfig config) {
        if (!StarTJadeUtils.hasData(capData, "signal_level", "indicator")) return;

        var signalLevel = capData.getInt("signal_level");

        var indicatorJson = capData.getString("indicator");
        var indicatorComponent = Optional.ofNullable(Component.Serializer.fromJson(indicatorJson))
                .map(Component.class::cast)
                .orElse(RedstoneIndicatorRecord.DEFAULT.indicatorComponent());

        tooltip.add(Component.translatable("ui.start_core.redstone_signal", signalLevel));
        tooltip.add(Component.translatable("ui.start_core.indicator", indicatorComponent));
    }
}
