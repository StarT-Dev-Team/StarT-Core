package com.startechnology.start_core.machine.modular;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.machine.StarTMachineUtils;
import com.startechnology.start_core.machine.StarTPartAbility;
import com.startechnology.start_core.machine.boosting.ModularCombustionBoosting;
import com.startechnology.start_core.machine.boosting.ModularFrameBoosting;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.network.chat.Component;


import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

public class StarTModularCombustionMachines {

    public static final MultiblockMachineDefinition T1_COMBUSTION_MODULE = START_REGISTRATE
            .multiblock("luv_combustion_module", (holder) -> new ModularCombustionBoosting(holder, ModularCombustionBoosting.T1_COMBUSTION_MODULE, StarTCore.resourceLocation("modular_combustion_frame")))
            .appearanceBlock(() -> StarTMachineUtils.getKjsBlock("pallaridium_turbine_casing"))
            .langValue("Unreal Combustion Module")
            .tooltips(
                    Component.translatable("block.start_core.t1_combustion_module_description"),
                    Component.translatable("block.start_core.t1_combustion_module_d0",
                            FormattingUtil.formatNumbers(GTValues.V[GTValues.LuV])),
                    Component.translatable("block.start_core.t1_combustion_module_d1",FormattingUtil.formatNumbers(1000)),
                    Component.translatable("block.start_core.t1_combustion_module_d2",
                            FormattingUtil.formatNumbers(GTValues.V[GTValues.LuV] * 4))
            )
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
            .multiblock("zpm_combustion_module", (holder) -> new ModularCombustionBoosting(holder, ModularCombustionBoosting.T2_COMBUSTION_MODULE, StarTCore.resourceLocation("modular_combustion_frame")))
            .appearanceBlock(() -> StarTMachineUtils.getKjsBlock("enriched_naquadah_turbine_casing"))
            .langValue("supreme Combustion Module")
            .tooltips(
                    Component.translatable("block.start_core.t2_combustion_module_description"),
                    Component.translatable("block.start_core.t2_combustion_module_d0",
                        FormattingUtil.formatNumbers(GTValues.V[GTValues.ZPM])),
                    Component.translatable("block.start_core.t2_combustion_module_d1",FormattingUtil.formatNumbers(1000)),
                    Component.translatable("block.start_core.t2_combustion_module_d2",
                            FormattingUtil.formatNumbers(GTValues.V[GTValues.ZPM] * 4))

            )
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

    public static final MultiblockMachineDefinition T3_COMBUSTION_MODULE = START_REGISTRATE
            .multiblock("uv_combustion_module", (holder) -> new ModularCombustionBoosting(holder, ModularCombustionBoosting.T3_COMBUSTION_MODULE, StarTCore.resourceLocation("modular_combustion_frame")))
            .appearanceBlock(() -> StarTMachineUtils.getKjsBlock("enriched_naquadah_turbine_casing"))
            .langValue("Supreme Rocket Module")
            .tooltips(
                    Component.translatable("block.start_core.t1_rocket_module_description"),
                    Component.translatable("block.start_core.t1_rocket_module_d0", FormattingUtil.formatNumbers(GTValues.V[GTValues.UV])),
                    Component.translatable("block.start_core.t1_rocket_module_d1",FormattingUtil.formatNumbers(1000)),
                    Component.translatable("block.start_core.t1_rocket_module_d2", FormattingUtil.formatNumbers(GTValues.V[GTValues.UV] * 4))

            )
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
    public static final MultiblockMachineDefinition T4_COMBUSTION_MODULE = START_REGISTRATE
            .multiblock("uev_combustion_module", (holder) -> new ModularCombustionBoosting(holder, ModularCombustionBoosting.T4_COMBUSTION_MODULE, StarTCore.resourceLocation("modular_combustion_frame")))
            .langValue("Nyinsane Rocket Module")
            .tooltips(
                    Component.translatable("block.start_core.t2_rocket_module_description"),
                    Component.translatable("block.start_core.t2_rocket_module_d0",FormattingUtil.formatNumbers(GTValues.V[GTValues.UHV])),
                    Component.translatable("block.start_core.t2_rocket_module_d1",FormattingUtil.formatNumbers(1000)),
                    Component.translatable("block.start_core.t2_rocket_module_d2",FormattingUtil.formatNumbers(GTValues.V[GTValues.UHV] * 4))

            )
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
            .multiblock("modular_combustion_frame", (holder) -> new ModularFrameBoosting(holder, StarTCore.resourceLocation("uv_combustion_module"),StarTCore.resourceLocation("uev_combustion_module"),StarTCore.resourceLocation("luv_combustion_module"),StarTCore.resourceLocation("zpm_combustion_module")))
            .langValue("Modular Combustion Frame")
            .tooltips(
                    Component.translatable("block.start_core.modular_combustion_frame_description")
            )
            .appearanceBlock(GTBlocks.CASING_PALLADIUM_SUBSTATION)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeTypes(StarTRecipeTypes.COMBUSTION_FRAME_RECIPE_TYPE)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("  B                     B  ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ")
                    .aisle("  BFFFF             FFFFB  ", "  B                     B  ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ")
                    .aisle("  BBBBBBBBCCCCCCCBBBBBBBB  ", "  B                     B  ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ")
                    .aisle("  BDDDDDDDDDDEDDDDDDDDDDB  ", "  B                     B  ", "  B                     B  ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ")
                    .aisle(" FBDDDDDDDDDDEDDDDDDDDDDBF ", "  B F F  F F   F F  F F B  ", "  B GGG  GGG   GGG  GGG B  ", "    GGG  GGG   GGG  GGG    ", "    GGG  GGG   GGG  GGG    ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ")
                    .aisle(" FBDDDDDDDDDDEDDDDDDDDDDBF ", "  B                     B  ", "  B GGG  GGG   GGG  GGG B  ", "  B GGG  GGG   GGG  GGG B  ", "    GGG  GGG   GGG  GGG    ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ")
                    .aisle("BBBDDEEHHEEEEEEEEEHHEEDDBBB", "BBB                     BBB", "  B GGG  GGG   GGG  GGG B  ", "  B GGG  GGG   GGG  GGG B  ", "    GGG  GGG   GGG  GGG    ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ")
                    .aisle("BEBDDDDDDDDDDEDDDDDDDDDDBEB", "BBB                     BBB", "  B GGG  GGG   GGG  GGG B  ", "  B GGG  GGG   GGG  GGG B  ", "    GGG  GGG   GGG  GGG    ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ")
                    .aisle("CEBDDDDDDDDDDEDDDDDDDDDDBEC", "BHB F F  F F   F F  F F BHB", "  B GGG  GGG   GGG  GGG B  ", "  B GGG  GGG   GGG  GGG B  ", "    GGG  GGG   GGG  GGG    ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ")
                    .aisle("CEBDDDDDDDDDDEDDDDDDDDDDBEC", "BHB                     BHB", "  B  E    E     E    E  B  ", "  B  I    I     I    I  B  ", "  B  F    F     F    F  B  ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ")
                    .aisle("CEBBBBBBBBBBBEBBBBBBBBBBBEC", "BHBBBBBBBBBBBBBBBBBBBBBBBHB", "  BBBEBBBBEBBBBBEBBBBEBBB  ", "  BBBBBBBBBBBBBBBBBBBBBBB  ", "  BBBBBBBBBBBBBBBBBBBBBBB  ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ")
                    .aisle("BEBBBBBBBBBBBEBBBBBBBBBBBEB", "BBB  E    E  E  E    E  BBB", "  B  E    E  E  E    E  B  ", "  B       E  J  E       B  ", "  BBBBBBBBBBBBBBBBBBBBBBB  ", "       KKK  KKK  KKK       ", "       KKK  KKK  KKK       ", "       KLK  KKK  KLK       ", "       LLL  KLK  LLL       ", "       KKK  LLL  KKK       ", "            KKK            ")
                    .aisle("BEEEEEEEEEEEEEEEEEEEEEEEEEB", "BBB FE F  E     E  F EF BBB", "  BJJJJJJJJJJJJJJJJJJJJJB  ", "  B J   EEEEEJEEEEE   J B  ", "  BBMBBBEBBBBEBBBBEBBBMBB  ", "       KNK  KEK  KNK       ", "       K K  KEK  K K       ", "       L L  KNK  L L       ", "       L L  L L  L L       ", "       K K  L L  K K       ", "            K K            ")
                    .aisle("BEBBBBBBBBBBBEBBBBBBBBBBBEB", "BBB  E    E  E  E    E  BBB", "  B  E    E  E  E    E  B  ", "  B       E  J  E       B  ", "  BBBBBBBBBBBBBBBBBBBBBBB  ", "       KKK  KKK  KKK       ", "       KKK  KKK  KKK       ", "       KLK  KKK  KLK       ", "       LLL  KLK  LLL       ", "       KKK  LLL  KKK       ", "            KKK            ")
                    .aisle("CEBBBBBBBBBBBEBBBBBBBBBBBEC", "BHBBBBBBBBBBBBBBBBBBBBBBBHB", "  BBBEBBBBEBBBBBEBBBBEBBB  ", "  BBBBBBBBBBB@BBBBBBBBBBB  ", "  BBBBBBBBBBBBBBBBBBBBBBB  ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ")
                    .aisle("CEBDDDDDDDDDDEDDDDDDDDDDBEC", "BHB                     BHB", "  B  E    E     E    E  B  ", "  B  I    I     I    I  B  ", "  B  F    F     F    F  B  ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ")
                    .aisle("CEBDDDDDDDDDDEDDDDDDDDDDBEC", "BHB F F  F F   F F  F F BHB", "  B GGG  GGG   GGG  GGG B  ", "  B GGG  GGG   GGG  GGG B  ", "    GGG  GGG   GGG  GGG    ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ")
                    .aisle("BEBDDDDDDDDDDEDDDDDDDDDDBEB", "BBB                     BBB", "  B GGG  GGG   GGG  GGG B  ", "  B GGG  GGG   GGG  GGG B  ", "    GGG  GGG   GGG  GGG    ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ")
                    .aisle("BBBDDEEHHEEEEEEEEEHHEEDDBBB", "BBB                     BBB", "  B GGG  GGG   GGG  GGG B  ", "  B GGG  GGG   GGG  GGG B  ", "    GGG  GGG   GGG  GGG    ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ")
                    .aisle(" FBDDDDDDDDDDEDDDDDDDDDDBF ", "  B                     B  ", "  B GGG  GGG   GGG  GGG B  ", "  B GGG  GGG   GGG  GGG B  ", "    GGG  GGG   GGG  GGG    ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ")
                    .aisle(" FBDDDDDDDDDDEDDDDDDDDDDBF ", "  B F F  F F   F F  F F B  ", "  B GGG  GGG   GGG  GGG B  ", "    GGG  GGG   GGG  GGG    ", "    GGG  GGG   GGG  GGG    ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ")
                    .aisle("  BDDDDDDDDDDEDDDDDDDDDDB  ", "  B                     B  ", "  B                     B  ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ")
                    .aisle("  BBBBBBBBCCCCCCCBBBBBBBB  ", "  B                     B  ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ")
                    .aisle("  BFFFF             FFFFB  ", "  B                     B  ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ")
                    .aisle("  B                     B  ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ", "                           ")
                    .where(" ", Predicates.any())
                    .where("B", Predicates.blocks(GTBlocks.CASING_PALLADIUM_SUBSTATION.get())
                            .or(Predicates.abilities(PartAbility.OUTPUT_LASER).setMaxGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.OUTPUT_ENERGY).setMaxGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(4))
                    )
                    .where("C", Predicates.blocks(StarTMachineUtils.getKjsBlock("pallaridium_firebox_casing")))
                    .where("D", Predicates.blocks(GCYMBlocks.CASING_HIGH_TEMPERATURE_SMELTING.get()))
                    .where("E", Predicates.blocks(StarTMachineUtils.getKjsBlock("pallaridium_pipe_casing")))
                    .where("F", Predicates.blocks(StarTMachineUtils.getGTCEuBlock("gtceu:black_steel_frame")))
                    .where("G", Predicates.any()) //modules go here
                    .where("H", Predicates.blocks(StarTMachineUtils.getKjsBlock("pallaridium_engine_intake_casing")))
                    .where("I", Predicates.abilities(StarTPartAbility.MODULAR_NODE))
                    .where("J", Predicates.blocks(StarTMachineUtils.getKjsBlock("pallaridium_gearbox")))
                    .where("K", Predicates.blocks(StarTMachineUtils.getGTCEuBlock("clean_machine_casing")))
                    .where("L", Predicates.blocks(StarTMachineUtils.getKjsBlock("red_steel_casing")))
                    .where("M", Predicates.blocks(StarTMachineUtils.getGTCEuBlock("luv_rotor_holder")))
                    .where("N", Predicates.abilities(PartAbility.MUFFLER))
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .build())
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_palladium_substation"),
                    GTCEu.id("block/machines/alloy_smelter"))
            .register();
    public static void init() {}

}
