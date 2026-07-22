package com.startechnology.start_core.machine.ore_factory;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.startechnology.start_core.machine.StarTMachineUtils;
import com.startechnology.start_core.machine.StarTPartAbility;
import com.startechnology.start_core.machine.modular_combustion.ModularCombustionBoosting;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import dev.latvian.mods.kubejs.KubeJS;

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

public class testmachines {

    public static final MultiblockMachineDefinition ORE_TEST = START_REGISTRATE
            .multiblock("ore_test", OreFactoryMachine::new)
            .appearanceBlock(() -> StarTMachineUtils.getKjsBlock("pallaridium_turbine_casing"))
            .langValue("Ore Test")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeTypes(StarTRecipeTypes.ORE_FACTORY_RECIPE)
            .recipeModifier(OreFactoryMachine::recipeModifier)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("AAA", "ABA", "AAA")
                    .aisle("CCC", "CDC", "CCC")
                    .aisle("AEA", "ADA", "AFA")
                    .aisle("AEA", "EDE", "AIA")
                    .aisle("AAA", "A@A", "AAA")
                    .where("A", Predicates.blocks(StarTMachineUtils.getKjsBlock("pallaridium_turbine_casing")))
                    .where("B", Predicates.abilities(PartAbility.INPUT_ENERGY))
                    .where("C", Predicates.blocks(StarTMachineUtils.getKjsBlock("pallaridium_engine_intake_casing")))
                    .where("D", Predicates.blocks(StarTMachineUtils.getKjsBlock("pallaridium_gearbox")))
                    .where("E", Predicates.blocks(StarTMachineUtils.getKjsBlock("pallaridium_turbine_casing"))
                            .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS))
                            .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS)))
                    .where("F", Predicates.abilities(PartAbility.MAINTENANCE))
                    .where("I", Predicates.abilities(PartAbility.MUFFLER))
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .build())
            .workableCasingModel(
                    KubeJS.id("block/casings/pallaridium/turbine_casing"),
                    GTCEu.id("block/machines/alloy_smelter"))
            .register();

    public static void init() {}
}