package com.startechnology.start_core.machine.gcrop;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.startechnology.start_core.machine.StarTMachineUtils;
import com.startechnology.start_core.recipe.StarTRecipeTypes;

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

public class VoidMesh {

    public static final MultiblockMachineDefinition VOID_MESH = START_REGISTRATE
            .multiblock("void_mesh", WorkableElectricMultiblockMachine::new)
            .appearanceBlock(GTBlocks.CASING_STEEL_SOLID)
            .langValue("Void Mesh [VM]")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeModifiers(GTRecipeModifiers.OC_NON_PERFECT, GTRecipeModifiers.BATCH_MODE)
            .recipeTypes(StarTRecipeTypes.VOID_MESH)
            .pattern(definition -> FactoryBlockPattern.start()
                    // spotless:off
                    .aisle("###AAA###", "#AABBBAA#", "ABBB@BBBA", "#AABBBAA#", "###AAA###")
                    // spotless:on
                    .where("#", Predicates.any())
                    .where("A", Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get()).setMinGlobalLimited(4)
                            .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                            .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1)))
                    .where("B", Predicates.blocks(StarTMachineUtils.getKjsBlock("meshblock")))
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .where(" ", Predicates.any())
                    .build())
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/machines/gas_collector"))
            .register();

    public static void init() {}
}
