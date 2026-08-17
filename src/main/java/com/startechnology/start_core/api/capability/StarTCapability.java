package com.startechnology.start_core.api.capability;

import com.startechnology.start_core.machine.abyssal_harvester.StarTAbyssalHarvesterMachine;
import com.startechnology.start_core.machine.bulking.IBulking;
import com.startechnology.start_core.machine.fusion.ReflectorFusionReactorMachine;
import com.startechnology.start_core.machine.hellforge.StarTHellForgeMachine;
import com.startechnology.start_core.machine.modular.StarTModularInterfaceHatchPartMachine;
import com.startechnology.start_core.machine.redstone.RedstoneInterfacePartMachine;
import com.startechnology.start_core.machine.solar.StarTSolarMachine;
import com.startechnology.start_core.machine.threading.StarTThreadingCapableMachine;
import com.startechnology.start_core.machine.vcrc.VacuumChemicalReactionChamberMachine;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StarTCapability {

    private static final Map<Capability<?>, Class<?>> CAPABILITY_TYPES = new HashMap<>();

    public static final Capability<IStarTDreamLinkNetworkMachine> CAPABILITY_DREAM_LINK_NETWORK_MACHINE = register(
            new CapabilityToken<>() {}, IStarTDreamLinkNetworkMachine.class);

    public static final Capability<StarTHellForgeMachine> CAPABILITY_HELL_FORGE_MACHINE = register(
            new CapabilityToken<>() {}, StarTHellForgeMachine.class);

    public static final Capability<RedstoneInterfacePartMachine> CAPABILITY_REDSTONE_INTERFACE = register(
            new CapabilityToken<>() {}, RedstoneInterfacePartMachine.class);

    public static final Capability<StarTAbyssalHarvesterMachine> CAPABILITY_ABYSSAL_HARVESTER = register(
            new CapabilityToken<>() {}, StarTAbyssalHarvesterMachine.class);

    public static final Capability<StarTThreadingCapableMachine> CAPABILITY_THREADING_CAPABLE_MACHINE = register(
            new CapabilityToken<>() {}, StarTThreadingCapableMachine.class);

    public static final Capability<IStarTModularSupportedModules> CAPABILITY_SUPPORTED_MODULES = register(
            new CapabilityToken<>() {}, IStarTModularSupportedModules.class);

    public static final Capability<StarTModularInterfaceHatchPartMachine> CAPABILITY_MODULAR_INTERFACE_HATCH_PART_MACHINE = register(
            new CapabilityToken<>() {}, StarTModularInterfaceHatchPartMachine.class);

    public static final Capability<ReflectorFusionReactorMachine> CAPABILITY_FUSION_REACTOR = register(
            new CapabilityToken<>() {}, ReflectorFusionReactorMachine.class);

    public static final Capability<StarTSolarMachine> CAPABILITY_SOLAR = register(new CapabilityToken<>() {},
            StarTSolarMachine.class);

    public static final Capability<VacuumChemicalReactionChamberMachine> VACUUM_CHEMICAL_REACTION_CHAMBER = register(
            new CapabilityToken<>() {}, VacuumChemicalReactionChamberMachine.class);

    public static final Capability<IBulking> BULKING = register(new CapabilityToken<>() {}, IBulking.class);

    private static <T> Capability<T> register(CapabilityToken<T> token, Class<T> requiredType) {
        var capability = CapabilityManager.get(token);
        CAPABILITY_TYPES.put(capability, requiredType);
        return capability;
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getCapabilityClass(Capability<T> capability) {
        return (Class<T>) Objects.requireNonNull(CAPABILITY_TYPES.get(capability));
    }
}
