package com.startechnology.start_core.integration.jade.provider;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMaintenanceMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.integration.jade.provider.CapabilityBlockProvider;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.capability.IStarTDreamLinkNetworkMachine;
import com.startechnology.start_core.api.capability.StarTCapabilityHelper;
import com.startechnology.start_core.machine.hellforge.StarTHellForgeMachine;
import com.startechnology.start_core.machine.redstone.StarTRedstoneInterfacePartMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class StarTRedstoneInterfaceProvider extends CapabilityBlockProvider<StarTRedstoneInterfacePartMachine> {

    public StarTRedstoneInterfaceProvider() {
        super(StarTCore.resourceLocation("variadic_redstone_info"));
    }

    @Override
    protected @Nullable StarTRedstoneInterfacePartMachine getCapability(Level level, BlockPos pos,
            @Nullable Direction side) {
        var capability = StarTCapabilityHelper.getRedstoneInterfacePartMachine(level, pos, side);

        if (capability != null)
            return capability;

        return null;
    }

    /* Used for storing data for the addTooltip method ? */
    @Override
    protected void write(CompoundTag data, StarTRedstoneInterfacePartMachine capability) {
        data.putInt("signal_level", capability.getCurrentLevel());
        data.putString("indicator", Component.Serializer.toJson(capability.getCurrentIndicator().indicatorComponent()));
    }

    /* Adds a new tooltip under the Jade stuff */
    @Override
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, Player player, BlockAccessor block,
            BlockEntity blockEntity, IPluginConfig config) {
        if (capData.contains("signal_level") && capData.contains("indicator")) {
            Integer signal_level = capData.getInt("signal_level");
            String indicatorJson = capData.getString("indicator");

            Component indicatorComponent = Optional
                    .ofNullable(Component.Serializer.fromJson(indicatorJson))
                    .orElse(Component.literal(indicatorJson));

            tooltip.add(Component.translatable("ui.start_core.redstone_signal", signal_level));
            tooltip.add(Component.translatable("ui.start_core.indicator", indicatorComponent));
        }
    }

}
