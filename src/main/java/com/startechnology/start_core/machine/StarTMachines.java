package com.startechnology.start_core.machine;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.machines.GTMachineUtils;
import com.startechnology.start_core.machine.abyssal_containment.StarTAbyssalContainmentMachines;
import com.startechnology.start_core.machine.abyssal_harvester.StarTAbyssalharvesterMachines;
import com.startechnology.start_core.machine.bacteria.StarTBacteriaMachines;
import com.startechnology.start_core.machine.converter.StarTConverterMachine;
import com.startechnology.start_core.machine.crates.StarTCrates;
import com.startechnology.start_core.machine.dreamlink.StarTDreamLinkHatches;
import com.startechnology.start_core.machine.dreamlink.StarTDreamLinkTransmissionTowers;
import com.startechnology.start_core.machine.drills.StarTDrillingRigs;
import com.startechnology.start_core.machine.drum.StarTDrumMachines;
import com.startechnology.start_core.machine.fusion.StarTFusionMachines;
import com.startechnology.start_core.machine.hellforge.StarTHellForgeMachines;
import com.startechnology.start_core.machine.hpca.StarTHPCAParts;
import com.startechnology.start_core.machine.komaru.StarTKomaruFrameMachines;
import com.startechnology.start_core.machine.maintenance.StarTMaintenanceMachines;
import com.startechnology.start_core.machine.modular.StarTModularConnectionHatches;
import com.startechnology.start_core.machine.modular_combustion.StarTModularCombustionMachines;
import com.startechnology.start_core.machine.parallel.StarTParallelHatches;
import com.startechnology.start_core.machine.redstone.StarTRedstoneInterfaces;
import com.startechnology.start_core.machine.solar.StarTSolarMachines;
import com.startechnology.start_core.machine.threading.StarTThreadingControllerMachines;
import com.startechnology.start_core.machine.threading.StarTThreadingStatBlocks;
import com.startechnology.start_core.machine.vacuum_pump.StarTVacuumPumpMachines;
import com.startechnology.start_core.recipe.StarTRecipeTypes;

import static com.gregtechceu.gtceu.common.data.machines.GTMachineUtils.ELECTRIC_TIERS;

import com.gregtechceu.gtceu.GTCEu;

public class StarTMachines {

    public static void init() {
        StarTHPCAParts.init();
        StarTBacteriaMachines.init();
        StarTFusionMachines.init();
        StarTConverterMachine.init();
        StarTParallelHatches.init();
        StarTDrumMachines.init();
        StarTDreamLinkHatches.init();
        StarTDreamLinkTransmissionTowers.init();
        StarTHellForgeMachines.init();
        StarTRedstoneInterfaces.init();
        StarTAbyssalharvesterMachines.init();
        StarTMaintenanceMachines.init();
        StarTAbyssalContainmentMachines.init();
        StarTThreadingControllerMachines.init();
        StarTThreadingStatBlocks.init();
        StarTCrates.init();
        StarTSolarMachines.init();
        StarTModularConnectionHatches.init();
        StarTVacuumPumpMachines.init();
        StarTDrillingRigs.init();
        StarTKomaruFrameMachines.init();
        StarTModularCombustionMachines.init();

        MachineDefinition[] GCROP_MUTATOR = GTMachineUtils.registerTieredMachines("gcrop_mutator",
                (holder, tier) -> new SimpleTieredMachine(holder, tier, GTMachineUtils.defaultTankSizeFunction),
                (tier, builder) -> builder
                        .langValue("%s Macerator %s".formatted(GTValues.VLVH[tier], GTValues.VLVT[tier]))
                        .rotationState(RotationState.NON_Y_AXIS)
                        .recipeType(StarTRecipeTypes.GCROP_MUTATOR_RECIPES)
                        .addOutputLimit(ItemRecipeCapability.CAP, switch (tier) {
                            case 1, 2 -> 1;
                            case 3 -> 3;
                            default -> 4;
                        })
                        .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
                        .workableTieredHullModel(GTCEu.id("block/machines/macerator"))
                        .register(),
                ELECTRIC_TIERS);
    }
}
