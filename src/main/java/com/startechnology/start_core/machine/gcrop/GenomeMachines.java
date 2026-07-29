package com.startechnology.start_core.machine.gcrop;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.startechnology.start_core.machine.random_custom_logic.DirtyWorkableElectricMultiblockMachine;
import com.startechnology.start_core.recipe.StarTRecipeTypes;

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

public class GenomeMachines {

    public static final MultiblockMachineDefinition GENOME_OPERATOR_MACHINE = START_REGISTRATE
            .multiblock("genome_operator", DirtyWorkableElectricMultiblockMachine::new)
            .appearanceBlock(GTBlocks.CASING_STEEL_SOLID)
            .langValue("Genome Classification Runic Operation Processor [GCROP]")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeModifiers(GTRecipeModifiers.OC_NON_PERFECT, GTRecipeModifiers.BATCH_MODE)
            .recipeTypes(StarTRecipeTypes.GENOME_GATHERING, StarTRecipeTypes.GENOME_MIXING,
                    StarTRecipeTypes.GENOME_SEPARATING, StarTRecipeTypes.GENOME_INSERTION)
            .pattern(definition -> FactoryBlockPattern.start()
                    // spotless:off
                    .aisle("CCC", "CCC", "CCC")
                    .aisle("CCC", "C C", "CCC")
                    .aisle("CCC", "C@C", "CCC")
                    // spotless:on
                    .where(" ", Predicates.any())
                    .where("C", Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get()).setMinGlobalLimited(4)
                            .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                            .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1)))
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .build())
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/machines/wiremill"))
            .register();

    public static void init() {}
}
