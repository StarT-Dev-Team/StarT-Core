package com.startechnology.start_core.api.capability;

import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.forge.GTCapability;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.startechnology.start_core.machine.abyssal_harvester.StarTAbyssalHarvesterMachine;
import com.startechnology.start_core.machine.hellforge.StarTHellForgeMachine;
import com.startechnology.start_core.machine.redstone.StarTRedstoneInterfacePartMachine;

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

        else if (capability == StarTCapability.CAPABILITY_HELL_FORGE_MACHINE) {
            if (machine instanceof StarTHellForgeMachine hellforgeMachine) {
                return StarTCapability.CAPABILITY_HELL_FORGE_MACHINE.orEmpty(capability, LazyOptional.of(() -> hellforgeMachine));
            }
        }

        else if (capability == StarTCapability.CAPABILITY_REDSTONE_INTERFACE) {
            if (machine instanceof StarTRedstoneInterfacePartMachine redstoneMachine) {
                return StarTCapability.CAPABILITY_REDSTONE_INTERFACE.orEmpty(capability, LazyOptional.of(() -> redstoneMachine));
            }
        }

        else if (capability == StarTCapability.CAPABILITY_ABYSSAL_HARVESTER) {
            if (machine instanceof StarTAbyssalHarvesterMachine harvesterMachine) {
                return StarTCapability.CAPABILITY_ABYSSAL_HARVESTER.orEmpty(capability, LazyOptional.of(() -> harvesterMachine));
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

    @Nullable
    public static StarTRedstoneInterfacePartMachine getRedstoneInterfacePartMachine(Level level, BlockPos pos, @Nullable Direction side) {
        return getBlockEntityCapability(StarTCapability.CAPABILITY_REDSTONE_INTERFACE, level, pos, side);
    }

    @Nullable
    public static StarTHellForgeMachine getHellforgeMachine(Level level, BlockPos pos, @Nullable Direction side) {
        return getBlockEntityCapability(StarTCapability.CAPABILITY_HELL_FORGE_MACHINE, level, pos, side);
    }

    @Nullable
    public static StarTAbyssalHarvesterMachine getAbyssalHarvesterMachine(Level level, BlockPos pos, @Nullable Direction side) {
        return getBlockEntityCapability(StarTCapability.CAPABILITY_ABYSSAL_HARVESTER, level, pos, side);
    }
}