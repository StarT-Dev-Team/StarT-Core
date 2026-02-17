package com.startechnology.start_core.api.capability;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.startechnology.start_core.machine.abyssal_harvester.StarTAbyssalHarvesterMachine;
import com.startechnology.start_core.machine.fusion.ReflectorFusionReactorMachine;
import com.startechnology.start_core.machine.hellforge.StarTHellForgeMachine;
import com.startechnology.start_core.machine.redstone.StarTRedstoneInterfacePartMachine;
import com.startechnology.start_core.machine.threading.StarTThreadingCapableMachine;
import com.startechnology.start_core.machine.vcr.VacuumChemicalReactorMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

public class StarTCapabilityHelper {

    private static <T> LazyOptional<T> getCapabilityFromMachine(Capability<T> capability, MetaMachine machine) {
        if (capability == StarTCapability.CAPABILITY_DREAM_LINK_NETWORK_MACHINE) {
            if (machine instanceof IStarTDreamLinkNetworkMachine dreamLinkNetworkMachine) {
                return StarTCapability.CAPABILITY_DREAM_LINK_NETWORK_MACHINE.orEmpty(capability, LazyOptional.of(() -> dreamLinkNetworkMachine));
            }
        } else if (capability == StarTCapability.CAPABILITY_HELL_FORGE_MACHINE) {
            if (machine instanceof StarTHellForgeMachine hellforgeMachine) {
                return StarTCapability.CAPABILITY_HELL_FORGE_MACHINE.orEmpty(capability, LazyOptional.of(() -> hellforgeMachine));
            }
        } else if (capability == StarTCapability.CAPABILITY_REDSTONE_INTERFACE) {
            if (machine instanceof StarTRedstoneInterfacePartMachine redstoneMachine) {
                return StarTCapability.CAPABILITY_REDSTONE_INTERFACE.orEmpty(capability, LazyOptional.of(() -> redstoneMachine));
            }
        } else if (capability == StarTCapability.CAPABILITY_ABYSSAL_HARVESTER) {
            if (machine instanceof StarTAbyssalHarvesterMachine harvesterMachine) {
                return StarTCapability.CAPABILITY_ABYSSAL_HARVESTER.orEmpty(capability, LazyOptional.of(() -> harvesterMachine));
            }
        } else if (capability == StarTCapability.CAPABILITY_THREADING_CAPABLE_MACHINE) {
            if (machine instanceof StarTThreadingCapableMachine threadingCapableMachine) {
                return StarTCapability.CAPABILITY_THREADING_CAPABLE_MACHINE.orEmpty(capability, LazyOptional.of(() -> threadingCapableMachine));
            }
        } else if (capability == StarTCapability.CAPABILITY_FUSION_REACTOR) {
            if (machine instanceof ReflectorFusionReactorMachine fusionReactorMachine) {
                return StarTCapability.CAPABILITY_FUSION_REACTOR.orEmpty(capability, LazyOptional.of(() -> fusionReactorMachine));
            }
        } else if (capability == StarTCapability.VACUUM_CHEMICAL_REACTOR) {
            if (machine instanceof VacuumChemicalReactorMachine vcrMachine) {
                return StarTCapability.VACUUM_CHEMICAL_REACTOR.orEmpty(capability, LazyOptional.of(() -> vcrMachine));
            }
        }

        return LazyOptional.empty();
    }

    @Nullable
    private static <T> T getBlockEntityCapability(Capability<T> capability, Level level, BlockPos pos,
                                                  @Nullable Direction side) {
        if (level.getBlockState(pos).hasBlockEntity()) {
            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof MetaMachineBlockEntity metaMachineBlockEntity) {
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
    public static StarTThreadingCapableMachine getThreadingCapableMachine(Level level, BlockPos pos, @Nullable Direction side) {
        return getBlockEntityCapability(StarTCapability.CAPABILITY_THREADING_CAPABLE_MACHINE, level, pos, side);
    }

    @Nullable
    public static StarTHellForgeMachine getHellforgeMachine(Level level, BlockPos pos, @Nullable Direction side) {
        return getBlockEntityCapability(StarTCapability.CAPABILITY_HELL_FORGE_MACHINE, level, pos, side);
    }

    @Nullable
    public static StarTAbyssalHarvesterMachine getAbyssalHarvesterMachine(Level level, BlockPos pos, @Nullable Direction side) {
        return getBlockEntityCapability(StarTCapability.CAPABILITY_ABYSSAL_HARVESTER, level, pos, side);
    }

    @Nullable
    public static ReflectorFusionReactorMachine getFusionReactorMachine(Level level, BlockPos pos, @Nullable Direction side) {
        return getBlockEntityCapability(StarTCapability.CAPABILITY_FUSION_REACTOR, level, pos, side);
    }

    @Nullable
    public static VacuumChemicalReactorMachine getVacuumChemicalReactorMachine(Level level, BlockPos pos, @Nullable Direction side) {
        return getBlockEntityCapability(StarTCapability.VACUUM_CHEMICAL_REACTOR, level, pos, side);
    }

}