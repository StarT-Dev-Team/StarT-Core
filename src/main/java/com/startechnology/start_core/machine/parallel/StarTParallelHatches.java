package com.startechnology.start_core.machine.parallel;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ParallelHatchPartMachine;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.machine.StarTMachineUtils;
import com.startechnology.start_core.machine.StarTPartAbility;

import net.minecraft.network.chat.Component;

public class StarTParallelHatches {
    
    public static final MachineDefinition[] ABSOLUTE_PARALLEL_HATCH = StarTMachineUtils.registerTieredMachines("absolute_parallel_hatch",
        StarTAbsoluteParallelHatchMachine::new,
        (tier, builder) -> builder
                .langValue(switch (tier) {
                    case 9 -> "Epic";
                    case 10 -> "Mega";
                    case 11 -> "Hyper";
                    default -> "Unknown";
                } + " Absolute Parallel Mastery Hatch")
                .rotationState(RotationState.ALL)
                .abilities(StarTPartAbility.ABSOLUTE_PARALLEL_HATCH)
                .workableTieredHullRenderer(StarTCore.resourceLocation("block/parallel/absolute_parallel_hatch_mk" + (tier - 8)))
                .tooltips(Component.translatable("start_core.machine.absolute_parallel_hatch_mk" + (tier - 8) + ".tooltip"))
                .tooltips(Component.translatable("start_core.machine.absolute_parallel_hatch_energy.tooltip"))
                .register(),
        GTValues.UHV, GTValues.UEV, GTValues.UIV
    );

    public static final MachineDefinition[] PARALLEL_HATCH = StarTMachineUtils.registerTieredMachines("parallel_hatch",
        ParallelHatchPartMachine::new,
        (tier, builder) -> builder
                .langValue(switch (tier) {
                    case 9 -> "Epic";
                    case 10 -> "Mega";
                    case 11 -> "Hyper";
                    default -> "Unknown";
                } + " Parallel Control Hatch")
                .rotationState(RotationState.ALL)
                .abilities(PartAbility.PARALLEL_HATCH)
                .workableTieredHullRenderer(StarTCore.resourceLocation("block/parallel/parallel_hatch_mk" + (tier - 4)))
                .tooltips(Component.translatable("start_core.machine.parallel_hatch_mk" + (tier - 4) + ".tooltip"))
                .register(),
        GTValues.UHV, GTValues.UEV, GTValues.UIV
    );

    public static void init() {}
}
