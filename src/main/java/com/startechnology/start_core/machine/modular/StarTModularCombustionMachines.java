package com.startechnology.start_core.machine.modular;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.machine.StarTMachineUtils;
import com.startechnology.start_core.machine.StarTPartAbility;
import com.startechnology.start_core.machine.boosting.ModularCombustionBoosting;
import com.startechnology.start_core.machine.boosting.ModularFrameBoosting;
import dev.latvian.mods.kubejs.KubeJS;


import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

public class StarTModularCombustionMachines {

    public static final MultiblockMachineDefinition T1_COMBUSTION_MODULE = START_REGISTRATE
            .multiblock("t1_combustion_module", (holder) -> new ModularCombustionBoosting(holder, ModularCombustionBoosting.T1_COMBUSTION_MODULE, StarTCore.resourceLocation("modular_combustion_frame")))
            .appearanceBlock(() -> StarTMachineUtils.getKjsBlock("pallaridium_turbine_casing"))
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeTypes(GTRecipeTypes.COMBUSTION_GENERATOR_FUELS)
            .recipeModifier(ModularCombustionBoosting::recipeModifier)
            .generator(true)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("AAA", "ABA", "AAA")
                    .aisle("CCC", "CDC", "CCC")
                    .aisle("AEA", "ADA", "AFA")
                    .aisle("AEA", "HDH", "AIA")
                    .aisle("AAA", "A@A", "AAA")
                    .where("A", Predicates.blocks(StarTMachineUtils.getKjsBlock("pallaridium_turbine_casing")))//will be a kjs casing
                    .where("B", Predicates.abilities(StarTPartAbility.MODULAR_TERMINAL))
                    .where("C", Predicates.blocks(StarTMachineUtils.getKjsBlock("pallaridium_engine_intake_casing")))
                    .where("D", Predicates.blocks(StarTMachineUtils.getKjsBlock("pallaridium_gearbox")))
                    .where("E", Predicates.blocks(StarTMachineUtils.getKjsBlock("pallaridium_turbine_casing"))
                                    .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS))
                                    .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS))
                    )
                    .where("F", Predicates.abilities(PartAbility.MAINTENANCE))
                    .where("H", Predicates.blocks(StarTMachineUtils.getGTCEuBlock("luv_rotor_holder")))
                    .where("I", Predicates.abilities(PartAbility.MUFFLER))
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .build())
            .workableCasingModel(KubeJS.id("block/casings/pallaridium/turbine_casing"),
                    GTCEu.id("block/machines/alloy_smelter"))
            .register();

    public static final MultiblockMachineDefinition T2_COMBUSTION_MODULE = START_REGISTRATE
            .multiblock("t2_combustion_module", (holder) -> new ModularCombustionBoosting(holder, ModularCombustionBoosting.T2_COMBUSTION_MODULE, StarTCore.resourceLocation("modular_combustion_frame")))
            .appearanceBlock(() -> StarTMachineUtils.getKjsBlock("enriched_naquadah_turbine_casing"))
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeTypes(GTRecipeTypes.COMBUSTION_GENERATOR_FUELS)
            .recipeModifier(ModularCombustionBoosting::recipeModifier)
            .generator(true)
            //T2 Combustion Module
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("AAA", "ABA", "AAA")
                    .aisle("CCC", "CDC", "CCC")
                    .aisle("AEA", "ADA", "AFA")
                    .aisle("AEA", "HDH", "AIA")
                    .aisle("AAA", "A@A", "AAA")
                    .where("A", Predicates.blocks(StarTMachineUtils.getKjsBlock("enriched_naquadah_turbine_casing")))
                    .where("B", Predicates.abilities(StarTPartAbility.MODULAR_TERMINAL))
                    .where("C", Predicates.blocks(StarTMachineUtils.getKjsBlock("enriched_naquadah_engine_intake_casing")))
                    .where("D", Predicates.blocks(StarTMachineUtils.getKjsBlock("enriched_naquadah_gearbox")))
                    .where("E", Predicates.blocks(StarTMachineUtils.getKjsBlock("enriched_naquadah_turbine_casing"))
                                    .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS))
                                    .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS))
                    )
                    .where("F", Predicates.abilities(PartAbility.MAINTENANCE))
                    .where("H", Predicates.blocks(StarTMachineUtils.getGTCEuBlock("zpm_rotor_holder")))
                    .where("I", Predicates.abilities(PartAbility.MUFFLER))
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .build())
            .workableCasingModel(KubeJS.id("block/casings/naquadah/turbine_casing"),
                    GTCEu.id("block/machines/alloy_smelter"))
            .register();

    public static final MultiblockMachineDefinition T1_ROCKET_MODULE = START_REGISTRATE
            .multiblock("t1_rocket_module", (holder) -> new ModularCombustionBoosting(holder, ModularCombustionBoosting.T1_ROCKET_MODULE, StarTCore.resourceLocation("modular_combustion_frame")))
            .appearanceBlock(() -> StarTMachineUtils.getKjsBlock("enriched_naquadah_turbine_casing"))
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeTypes(GTRecipeTypes.COMBUSTION_GENERATOR_FUELS)
            .recipeModifier(ModularCombustionBoosting::recipeModifier)
            .generator(true)
            //T1 Rocket Module
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("AAA", "ABA", "AAA")
                    .aisle("CFC", "CDC", "CEC")
                    .aisle("CFC", "GDG", "CHC")
                    .aisle("CFC", "CDC", "CEC")
                    .aisle("AAA", "A@A", "AAA")
                    .where("A", Predicates.blocks(StarTMachineUtils.getKjsBlock("enriched_naquadah_engine_intake_casing")))
                    .where("B", Predicates.abilities(StarTPartAbility.MODULAR_TERMINAL))
                    .where("C", Predicates.blocks(StarTMachineUtils.getKjsBlock("enriched_naquadah_turbine_casing")))
                    .where("D", Predicates.blocks(StarTMachineUtils.getKjsBlock("enriched_naquadah_gearbox")))
                    .where("E", Predicates.abilities(PartAbility.MUFFLER))
                    .where("F", Predicates.blocks(StarTMachineUtils.getKjsBlock("enriched_naquadah_turbine_casing"))
                                    .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS))
                                    .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS))
                    )
                    .where("G", Predicates.blocks(StarTMachineUtils.getGTCEuBlock("zpm_rotor_holder")))
                    .where("H", Predicates.abilities(PartAbility.MAINTENANCE))
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .build())
            .workableCasingModel(KubeJS.id("block/casings/naquadah/turbine_casing"),
                    GTCEu.id("block/machines/alloy_smelter"))
            .register();

    //T2 Rocket Module
    public static final MultiblockMachineDefinition T2_ROCKET_MODULE = START_REGISTRATE
            .multiblock("t2_rocket_module", (holder) -> new ModularCombustionBoosting(holder, ModularCombustionBoosting.T2_ROCKET_MODULE, StarTCore.resourceLocation("modular_combustion_frame")))
            .appearanceBlock(() -> StarTMachineUtils.getKjsBlock("nyanium_turbine_casing"))
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeTypes(GTRecipeTypes.COMBUSTION_GENERATOR_FUELS)
            .recipeModifier(ModularCombustionBoosting::recipeModifier)
            .generator(true)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("AAA", "ABA", "AAA")
                    .aisle("CFC", "CDC", "CEC")
                    .aisle("CFC", "GDG", "CHC")
                    .aisle("CFC", "CDC", "CEC")
                    .aisle("AAA", "A@A", "AAA")
                    .where("A", Predicates.blocks(StarTMachineUtils.getKjsBlock("nyanium_engine_intake_casing")))
                    .where("B", Predicates.abilities(StarTPartAbility.MODULAR_TERMINAL))
                    .where("C", Predicates.blocks(StarTMachineUtils.getKjsBlock("nyanium_turbine_casing")))
                    .where("D", Predicates.blocks(StarTMachineUtils.getKjsBlock("nyanium_gearbox")))
                    .where("E", Predicates.abilities(PartAbility.MUFFLER))
                    .where("F", Predicates.blocks(StarTMachineUtils.getKjsBlock("nyanium_turbine_casing"))
                                    .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS))
                                    .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS))
                    )
                    .where("G", Predicates.blocks(StarTMachineUtils.getGTCEuBlock("uhv_rotor_holder")))
                    .where("H", Predicates.abilities(PartAbility.MAINTENANCE))
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .build())
            .workableCasingModel(KubeJS.id("block/casings/nyanium/turbine_casing"),
                    GTCEu.id("block/machines/alloy_smelter"))
            .register();

    public static final MultiblockMachineDefinition MODULAR_COMBUSTION_FRAME = START_REGISTRATE
            .multiblock("modular_combustion_frame", (holder) -> new ModularFrameBoosting(holder, StarTCore.resourceLocation("t2_rocket_module"),StarTCore.resourceLocation("t1_rocket_module"),StarTCore.resourceLocation("t1_combustion_module"),StarTCore.resourceLocation("t2_combustion_module")))
            .appearanceBlock(() -> StarTMachineUtils.getKjsBlock("enriched_naquadah_machine_casing"))
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeTypes(GTRecipeTypes.DUMMY_RECIPES)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("A                         A", "                           ", "                           ", "                           ", "                           ")
                    .aisle("ABBB                   BBBA", "A                         A", "                           ", "                           ", "                           ")
                    .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAA", "A                         A", "                           ", "                           ", "                           ")
                    .aisle("ACCCCCCCCCCCCCCCCCCCCCCCCCA", "A                         A", "A                         A", "                           ", "                           ")
                    .aisle("ACCCCCCCCCCCCCCCCCCCCCCCCCA", "A B B  B B  B B  B B  B B A", "A ---  ---  ---  ---  --- A", "  ---  ---  ---  ---  ---  ", "  ---  ---  ---  ---  ---  ")
                    .aisle("ACCCCCCCCCCCCCCCCCCCCCCCCCA", "A                         A", "A ---  ---  ---  ---  --- A", "A ---  ---  ---  ---  --- A", "  ---  ---  ---  ---  ---  ")
                    .aisle("ACCCCCCCCCCCCCCCCCCCCCCCCCA", "A                         A", "A ---  ---  ---  ---  --- A", "A ---  ---  ---  ---  --- A", "  ---  ---  ---  ---  ---  ")
                    .aisle("ACCCCCCCCCCCCCCCCCCCCCCCCCA", "A                         A", "A ---  ---  ---  ---  --- A", "A ---  ---  ---  ---  --- A", "  ---  ---  ---  ---  ---  ")
                    .aisle("ACCCCCCCCCCCCCCCCCCCCCCCCCA", "A B B  B B  B B  B B  B B A", "A ---  ---  ---  ---  --- A", "A ---  ---  ---  ---  --- A", "A ---  ---  ---  ---  --- A")
                    .aisle("ACCCCCCCCCCCCCCCCCCCCCCCCCA", "A                         A", "A  E    E    E    E    E  A", "A  F    F    F    F    F  A", "A  B    B    B    B    B  A")
                    .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAAAAAAAAAAAAAA", "AAAEAAAAEAAAAEAAAAEAAAAEAAA", "AAAEAAAAEAAAAEAAAAEAAAAEAAA", "AAAAAAAAAAAAAAAAAAAAAAAAAAA")
                    .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAA", "A EEEEEEEEEEEEEEEEEEEEEEEEA", "A  E    E    E    E    E  A", "A EEE  EEE  EEE  EEE  EEE A", "AAGGGAAGGGAAGGGAAGGGAAGGGAA")
                    .aisle("AAHHHHAAAAAAAAAAAAAAAHHHHHA", "AAAAEA    E     E    AEAAAA", "AAAAAA       E       AAAAAA", "AAAAAAAA           AAAAAAAA", " AAAAAAAAAAAAAAAAAAAAAAAAA ")
                    .aisle("    AAHHHHHHAAAHHHHHHAA    ", "    AAAAAAEEEEEEEAAAAAA    ", "      AAAAAA E AAAAAA      ", "        AAAAAAAAAAA        ", "                           ")
                    .aisle("          AAHHHAA          ", "          AAA@AAA          ", "            AAA            ", "                           ", "                           ")
                    .where("A", Predicates.blocks(StarTMachineUtils.getKjsBlock("enriched_naquadah_machine_casing"))
                            .or(Predicates.abilities(PartAbility.OUTPUT_LASER).setMaxGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.OUTPUT_ENERGY).setMaxGlobalLimited(1))
                    )
                    .where(" ", Predicates.any())
                    .where("B", Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTCEuAPI.materialManager.getMaterial("gtceu:naquadah_alloy"))))
                    .where("C", Predicates.blocks(GCYMBlocks.CASING_HIGH_TEMPERATURE_SMELTING.get()))
                    .where("-", Predicates.any()) //modules go here
                    .where("E", Predicates.blocks(StarTMachineUtils.getKjsBlock("enriched_naquadah_pipe_casing")))
                    .where("F", Predicates.abilities(StarTPartAbility.MODULAR_NODE))
                    .where("G", Predicates.blocks(StarTMachineUtils.getKjsBlock("enriched_naquadah_engine_intake_casing")))
                    .where("H", Predicates.blocks(StarTMachineUtils.getKjsBlock("enriched_naquadah_heat_escape_casing")))
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .build())
            .workableCasingModel(KubeJS.id("block/casings/naquadah/casing"),
                    GTCEu.id("block/machines/alloy_smelter"))
            .register();
    public static void init() {}

}
