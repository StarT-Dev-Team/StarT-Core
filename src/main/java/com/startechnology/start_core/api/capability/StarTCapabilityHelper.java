package com.startechnology.start_core.api.capability;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.startechnology.start_core.machine.abyssal_harvester.StarTAbyssalHarvesterMachine;
import com.startechnology.start_core.machine.bulking.IBulking;
import com.startechnology.start_core.machine.fusion.ReflectorFusionReactorMachine;
import com.startechnology.start_core.machine.hellforge.StarTHellForgeMachine;
import com.startechnology.start_core.machine.modular.StarTModularInterfaceHatchPartMachine;
import com.startechnology.start_core.machine.redstone.RedstoneInterfacePartMachine;
import com.startechnology.start_core.machine.solar.StarTSolarMachine;
import com.startechnology.start_core.machine.threading.StarTThreadingCapableMachine;
import com.startechnology.start_core.machine.vcrc.VacuumChemicalReactionChamberMachine;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import static com.startechnology.start_core.api.capability.StarTCapability.*;

public class StarTCapabilityHelper {

    private static final Map<Capability<?>, Class<?>> CAPABILITY_TYPES = new HashMap<>();

    static {
        register(CAPABILITY_DREAM_LINK_NETWORK_MACHINE, IStarTDreamLinkNetworkMachine.class);
        register(CAPABILITY_HELL_FORGE_MACHINE, StarTHellForgeMachine.class);
        register(CAPABILITY_REDSTONE_INTERFACE, RedstoneInterfacePartMachine.class);
        register(CAPABILITY_ABYSSAL_HARVESTER, StarTAbyssalHarvesterMachine.class);
        register(CAPABILITY_THREADING_CAPABLE_MACHINE, StarTThreadingCapableMachine.class);
        register(CAPABILITY_FUSION_REACTOR, ReflectorFusionReactorMachine.class);
        register(CAPABILITY_SOLAR, StarTSolarMachine.class);
        register(VACUUM_CHEMICAL_REACTION_CHAMBER, VacuumChemicalReactionChamberMachine.class);
        register(CAPABILITY_SUPPORTED_MODULES, IStarTModularSupportedModules.class);
        register(CAPABILITY_MODULAR_INTERFACE_HATCH_PART_MACHINE,
                StarTModularInterfaceHatchPartMachine.class);
        register(BULKING, IBulking.class);
    }

    private static <T> void register(Capability<T> capability, Class<T> requiredType) {
        CAPABILITY_TYPES.put(capability, requiredType);
    }

    @SuppressWarnings("unchecked")
    private static <T> LazyOptional<T> getCapabilityFromMachine(Capability<T> capability, MetaMachine machine) {
        Class<?> requiredType = CAPABILITY_TYPES.get(capability);
        if (requiredType != null && requiredType.isInstance(machine)) {
            T casted = (T) machine;
            return capability.orEmpty(capability, LazyOptional.of(() -> casted));
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
    public static <T> T getCapability(Capability<T> capability, Level level, BlockPos pos,
                                      @Nullable Direction side) {
        return getBlockEntityCapability(capability, level, pos, side);
    }

    @Nullable
    public static IStarTDreamLinkNetworkMachine getDreamLinkNetworkMachine(Level level, BlockPos pos,
                                                                           @Nullable Direction side) {
        return getCapability(StarTCapability.CAPABILITY_DREAM_LINK_NETWORK_MACHINE, level, pos, side);
    }

    @Nullable
    public static RedstoneInterfacePartMachine getRedstoneInterfacePartMachine(Level level, BlockPos pos,
                                                                               @Nullable Direction side) {
        return getCapability(StarTCapability.CAPABILITY_REDSTONE_INTERFACE, level, pos, side);
    }

    @Nullable
    public static StarTThreadingCapableMachine getThreadingCapableMachine(Level level, BlockPos pos,
                                                                          @Nullable Direction side) {
        return getCapability(StarTCapability.CAPABILITY_THREADING_CAPABLE_MACHINE, level, pos, side);
    }

    @Nullable
    public static StarTHellForgeMachine getHellforgeMachine(Level level, BlockPos pos, @Nullable Direction side) {
        return getCapability(StarTCapability.CAPABILITY_HELL_FORGE_MACHINE, level, pos, side);
    }

    @Nullable
    public static StarTAbyssalHarvesterMachine getAbyssalHarvesterMachine(Level level, BlockPos pos,
                                                                          @Nullable Direction side) {
        return getCapability(StarTCapability.CAPABILITY_ABYSSAL_HARVESTER, level, pos, side);
    }

    @Nullable
    public static ReflectorFusionReactorMachine getFusionReactorMachine(Level level, BlockPos pos,
                                                                        @Nullable Direction side) {
        return getCapability(StarTCapability.CAPABILITY_FUSION_REACTOR, level, pos, side);
    }

    @Nullable
    public static StarTSolarMachine getSolarMachine(Level level, BlockPos pos, @Nullable Direction side) {
        return getCapability(StarTCapability.CAPABILITY_SOLAR, level, pos, side);
    }

    @Nullable
    public static VacuumChemicalReactionChamberMachine getVacuumChemicalReactionChamberMachine(Level level,
                                                                                               BlockPos pos,
                                                                                               @Nullable Direction side) {
        return getCapability(StarTCapability.VACUUM_CHEMICAL_REACTION_CHAMBER, level, pos, side);
    }

    @Nullable
    public static IStarTModularSupportedModules getModularSupportedModules(Level level, BlockPos pos,
                                                                           @Nullable Direction side) {
        return getCapability(StarTCapability.CAPABILITY_SUPPORTED_MODULES, level, pos, side);
    }

    @Nullable
    public static StarTModularInterfaceHatchPartMachine getModularInterfaceHatchPartMachine(Level level, BlockPos pos,
                                                                                            @Nullable Direction side) {
        return getCapability(StarTCapability.CAPABILITY_MODULAR_INTERFACE_HATCH_PART_MACHINE, level, pos, side);
    }

    @Nullable
    public static IBulking getBulkingMachine(Level level, BlockPos pos, @Nullable Direction side) {
        return getCapability(StarTCapability.BULKING, level, pos, side);
    }
}
