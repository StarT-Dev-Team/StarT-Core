package com.startechnology.start_core.machine.redstone;

import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.machine.StarTPartAbility;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

public class StarTRedstoneInterfaces {

    public static final Integer REDSTONE_INTERFACE_TIER = GTValues.UEV;

    public static Function<IMachineBlockEntity, MetaMachine> getHolder(
        IO io
    ) {
        return holder -> new StarTRedstoneInterfacePartMachine(holder, REDSTONE_INTERFACE_TIER, io);
    }

    public static MachineBuilder<MachineDefinition> buildRedstoneHatch(
        String name,
        IO io
    ) {
        return StarTCore.START_REGISTRATE.machine(
            name, getHolder(io))
            .tooltips(
                Component.translatable("start_core.redstone_hatch.d0"),
                Component.translatable("start_core.redstone_hatch.d1")
            )
            .rotationState(RotationState.ALL)
            .abilities(StarTPartAbility.REDSTONE_INTERFACE)
            .tier(REDSTONE_INTERFACE_TIER);
    }

    public static final MachineDefinition REDSTONE_IN_HATCH = buildRedstoneHatch("redstone_in_hatch", IO.IN)
        .abilities(StarTPartAbility.REDSTONE_INPUT_INTERFACE)
        .workableTieredHullRenderer(StarTCore.resourceLocation("block/redstone/redstone_in"))
        .register();

    public static final MachineDefinition REDSTONE_OUT_HATCH = buildRedstoneHatch("redstone_out_hatch", IO.OUT)
        .abilities(StarTPartAbility.REDSTONE_OUTPUT_INTERFACE)
        .workableTieredHullRenderer(StarTCore.resourceLocation("block/redstone/redstone_out"))
        .register();

    public static void init() {}
}
