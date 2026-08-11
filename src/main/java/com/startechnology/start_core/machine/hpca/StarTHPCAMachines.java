package com.startechnology.start_core.machine.hpca;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.research.HPCAMachine;
import com.gregtechceu.gtceu.data.lang.LangHandler;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.gui.StarTGuiTextures;

public class StarTHPCAMachines {

    public static final MachineDefinition IMPROVED_HIGH_PERFORMANCE_COMPUTING_ARRAY = StarTCore.START_REGISTRATE
            .multiblock("improved_high_performance_computation_array",
                    (holder) -> new HPCAMachine(holder, 5, StarTGuiTextures.HPCA_COMPONENT_OUTLINE_5X5))
            .langValue("Improved High Performance Computation Array [IHPCA]")
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(GTBlocks.COMPUTER_CASING)
            .recipeType(GTRecipeTypes.DUMMY_RECIPES)
            .tooltipBuilder((itemStack, components) -> {
                components.addAll(LangHandler.getMultiLang("gtceu.machine.high_performance_computation_array.tooltip"));
            })
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("AA", "CC", "CC", "CC", "CC", "CC", "AA")
                    .aisle("VA", "XV", "XV", "XV", "XV", "XV", "VA")
                    .aisle("VA", "XV", "XV", "XV", "XV", "XV", "VA")
                    .aisle("VA", "XV", "XV", "XV", "XV", "XV", "VA")
                    .aisle("VA", "XV", "XV", "XV", "XV", "XV", "VA")
                    .aisle("VA", "XV", "XV", "XV", "XV", "XV", "VA")
                    .aisle("AS", "CC", "CC", "CC", "CC", "CC", "AA")
                    .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .where('A', Predicates.blocks(GTBlocks.ADVANCED_COMPUTER_CASING.get()))
                    .where('V', Predicates.blocks(GTBlocks.COMPUTER_HEAT_VENT.get()))
                    .where('X', Predicates.abilities(PartAbility.HPCA_COMPONENT))
                    .where('C', Predicates.blocks(GTBlocks.COMPUTER_CASING.get()).setMinGlobalLimited(5)
                            .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMinGlobalLimited(1)
                                    .setMaxGlobalLimited(2, 1))
                            .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.COMPUTATION_DATA_TRANSMISSION).setExactLimit(1))
                            .or(Predicates.autoAbilities(true, false, false)))
                    .build())
            // TODO: shapeinfo
            .sidedWorkableCasingModel(GTCEu.id("block/casings/hpca/computer_casing"),
                    GTCEu.id("block/multiblock/hpca"))
            .register();

    public static void init() {}
}
