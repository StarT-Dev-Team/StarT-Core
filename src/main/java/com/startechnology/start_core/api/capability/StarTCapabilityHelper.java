package com.startechnology.start_core.api.capability;

import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.forge.GTCapability;
import com.gregtechceu.gtceu.api.machine.MetaMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class StarTCapabilityHelper {
    private static <T> LazyOptional<T> getCapabilityFromMachine(Capability<T> capability, MetaMachine machine) {
        if (capability == StarTCapability.CAPABILITY_DREAM_LINK_NETWORK_MACHINE) {
            if (machine instanceof IStarTDreamLinkNetworkMachine dreamLinkNetworkMachine) {
                return StarTCapability.CAPABILITY_DREAM_LINK_NETWORK_MACHINE.orEmpty(capability, LazyOptional.of(() -> dreamLinkNetworkMachine));
            }
        }

        return LazyOptional.empty();
    }

    @Nullable
    private static <T> T getBlockEntityCapability(Capability<T> capability, Level level, BlockPos pos,
                                                  @Nullable Direction side) {
        if (level.getBlockState(pos).hasBlockEntity()) {
            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null && blockEntity instanceof MetaMachineBlockEntity metaMachineBlockEntity) {
                MetaMachine machine = metaMachineBlockEntity.getMetaMachine();
                return getCapabilityFromMachine(capability, machine).resolve().orElse(null);
            }
        }
        return null;
    }

    @Nullable
    public static IStarTDreamLinkNetworkMachine getDreamLinkNetworkMachine(Level level, BlockPos pos, @Nullable Direction side) {
        return getBlockEntityCapability(StarTCapability.CAPABILITY_DREAM_LINK_NETWORK_MACHINE, level, pos, side);
    }
}