package com.startechnology.start_core.machine.solar;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.machine.StarTMachineUtils;
import com.startechnology.start_core.machine.StarTPartAbility;
import com.startechnology.start_core.machine.solar.cell.StarTSolarCellPredicates;
import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

public class StarTSolarMachines {
    public static TextColor customGoldColor = TextColor.parseColor("#FDB813");

    public static final MultiblockMachineDefinition SOLAR_PANEL_EV = START_REGISTRATE
        .multiblock("ev_solar_panel", holder -> new StarTSolarMachine(holder, EV))
        .langValue("EV Solar Panel")
        .tooltips(
            Component.translatable("block.start_core.solar_multiblock_line").setStyle(Style.EMPTY.withColor(customGoldColor)),
            Component.translatable("solar.start_core.solar_machine.tooltip1"),
            Component.translatable("solar.start_core.solar_machine.tooltip2"),
            Component.translatable("solar.start_core.solar_machine.tooltip3"),
            Component.translatable("block.start_core.breaker_line"),
            Component.translatable("solar.start_core.solar_machine.tooltip4", StarTSolarMachine.getOutputModifier(EV)),
            Component.translatable("solar.start_core.solar_machine.tooltip5")
        )
        .rotationState(RotationState.NON_Y_AXIS)
        .recipeType(GTRecipeTypes.DUMMY_RECIPES)
        .appearanceBlock(GTBlocks.CASING_STEEL_SOLID)
        .pattern(definition -> FactoryBlockPattern.start()
            .aisle("CCC", "   ", "SSS")
            .aisle("CCC", " F ", "SSS")
            .aisle("C@C", "   ", "SSS")
            .where(" ", Predicates.air())
            .where("S", StarTSolarCellPredicates.solarCells())
            .where("F", Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTCEuAPI.materialManager.getMaterial("steel"))))
            .where("C", Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get())
                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(1))
                .or(Predicates.abilities(PartAbility.OUTPUT_ENERGY).setExactLimit(1))
                .or(Predicates.abilities(StarTPartAbility.REDSTONE_INTERFACE).setMaxGlobalLimited(1)))
            .where("@", Predicates.controller(Predicates.blocks(definition.get())))
            .build()
        )
        .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"), StarTCore.resourceLocation("block/solar/overlay/ev"))
        .register();

    public static final MultiblockMachineDefinition SOLAR_PANEL_IV = START_REGISTRATE
        .multiblock("iv_solar_panel", holder -> new StarTSolarMachine(holder, IV))
        .langValue("IV Solar Panel")
        .tooltips(
            Component.translatable("block.start_core.solar_multiblock_line").setStyle(Style.EMPTY.withColor(customGoldColor)),
            Component.translatable("solar.start_core.solar_machine.tooltip1"),
            Component.translatable("solar.start_core.solar_machine.tooltip2"),
            Component.translatable("solar.start_core.solar_machine.tooltip3"),
            Component.translatable("block.start_core.breaker_line"),
            Component.translatable("solar.start_core.solar_machine.tooltip4", StarTSolarMachine.getOutputModifier(IV)),
            Component.translatable("solar.start_core.solar_machine.tooltip5")
        )
        .rotationState(RotationState.NON_Y_AXIS)
        .recipeType(GTRecipeTypes.DUMMY_RECIPES)
        .appearanceBlock(GTBlocks.CASING_STEEL_SOLID)
        .pattern(definition -> FactoryBlockPattern.start()
            .aisle("CCCCC", "     ", "SSSSS")
            .aisle("CCCCC", " F F ", "SSSSS")
            .aisle("CC@CC", "     ", "SSSSS")
            .where(" ", Predicates.air())
            .where("S", StarTSolarCellPredicates.solarCells())
            .where("F", Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTCEuAPI.materialManager.getMaterial("steel"))))
            .where("C", Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get())
                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(1))
                .or(Predicates.abilities(PartAbility.OUTPUT_ENERGY).setExactLimit(1))
                .or(Predicates.abilities(StarTPartAbility.REDSTONE_INTERFACE).setMaxGlobalLimited(1)))
            .where("@", Predicates.controller(Predicates.blocks(definition.get())))
            .build()
        )
        .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"), StarTCore.resourceLocation("block/solar/overlay/iv"))
        .register();

    public static final MultiblockMachineDefinition SOLAR_PANEL_LUV = START_REGISTRATE
        .multiblock("luv_solar_panel", holder -> new StarTSolarMachine(holder, LuV))
        .langValue("LuV Solar Panel")
        .tooltips(
            Component.translatable("block.start_core.solar_multiblock_line").setStyle(Style.EMPTY.withColor(customGoldColor)),
            Component.translatable("solar.start_core.solar_machine.tooltip1"),
            Component.translatable("solar.start_core.solar_machine.tooltip2"),
            Component.translatable("solar.start_core.solar_machine.tooltip3"),
            Component.translatable("block.start_core.breaker_line"),
            Component.translatable("solar.start_core.solar_machine.tooltip4", StarTSolarMachine.getOutputModifier(LuV)),
            Component.translatable("solar.start_core.solar_machine.tooltip5")
        )
        .rotationState(RotationState.NON_Y_AXIS)
        .recipeType(GTRecipeTypes.DUMMY_RECIPES)
        .appearanceBlock(GTBlocks.CASING_STEEL_SOLID)
        .pattern(definition -> FactoryBlockPattern.start()
            .aisle("CCCCCCC", "       ", "SSSSSSS")
            .aisle("CCCCCCC", " F F F ", "SSSSSSS")
            .aisle("CCC@CCC", "       ", "SSSSSSS")
            .where(" ", Predicates.air())
            .where("S", StarTSolarCellPredicates.solarCells())
            .where("F", Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTCEuAPI.materialManager.getMaterial("steel"))))
            .where("C", Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get())
                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(1))
                .or(Predicates.abilities(PartAbility.OUTPUT_ENERGY).setExactLimit(1))
                .or(Predicates.abilities(StarTPartAbility.REDSTONE_INTERFACE).setMaxGlobalLimited(1)))
            .where("@", Predicates.controller(Predicates.blocks(definition.get())))
            .build()
        )
        .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"), StarTCore.resourceLocation("block/solar/overlay/luv"))
        .register();

    public static final MultiblockMachineDefinition SOLAR_ARRAY_UV = START_REGISTRATE
        .multiblock("uv_solar_array", holder -> new StarTSolarMachine(holder, UV))
        .langValue("UV Solar Array")
        .tooltips(
            Component.translatable("block.start_core.solar_multiblock_line").setStyle(Style.EMPTY.withColor(customGoldColor)),
            Component.translatable("solar.start_core.solar_machine.tooltip1"),
            Component.translatable("solar.start_core.solar_machine.tooltip2"),
            Component.translatable("solar.start_core.solar_machine.tooltip3"),
            Component.translatable("block.start_core.breaker_line"),
            Component.translatable("solar.start_core.solar_machine.tooltip4", StarTSolarMachine.getOutputModifier(UV)),
            Component.translatable("solar.start_core.solar_machine.tooltip5"),
            Component.translatable("block.start_core.breaker_line"),
            Component.translatable("solar.start_core.solar_machine.tooltip6", "§71000", Component.translatable("material.gtceu.deionized_water")),
            Component.translatable("block.start_core.breaker_line"),
            Component.translatable("solar.start_core.solar_machine.tooltip7")
        )
        .rotationState(RotationState.NON_Y_AXIS)
        .recipeType(GTRecipeTypes.DUMMY_RECIPES)
        .appearanceBlock(() -> StarTMachineUtils.getKjsBlock("enriched_naquadah_machine_casing"))
        .pattern(definition -> FactoryBlockPattern.start()
            .aisle("           ", "           ", "           ", "  SSSSSSS  ", "           ")
            .aisle("           ", "           ", "     F     ", " SS     SS ", "           ")
            .aisle("           ", "     F     ", "   SSSSS   ", "SS       SS", "           ")
            .aisle("           ", "     F     ", "  SS   SS  ", "S         S", "           ")
            .aisle("    CCC    ", "    FCF    ", "  S     S  ", "S         S", "           ")
            .aisle("    CCC    ", "  FFCBCFF  ", " FS  F  SF ", "S    @    S", "     F     ")
            .aisle("    CCC    ", "    FCF    ", "  S     S  ", "S         S", "           ")
            .aisle("           ", "     F     ", "  SS   SS  ", "S         S", "           ")
            .aisle("           ", "     F     ", "   SSSSS   ", "SS       SS", "           ")
            .aisle("           ", "           ", "     F     ", " SS     SS ", "           ")
            .aisle("           ", "           ", "           ", "  SSSSSSS  ", "           ")
            .where(" ", Predicates.any())
            .where("S", StarTSolarCellPredicates.solarCells())
            .where("F", Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTCEuAPI.materialManager.getMaterial("naquadah_alloy"))))
            .where("B", Predicates.blocks(GTBlocks.BATTERY_LAPOTRONIC_UV.get()))
            .where("C", Predicates.blocks(StarTMachineUtils.getKjsBlock("enriched_naquadah_machine_casing"))
                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(1))
                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(1))
                .or(Predicates.abilities(PartAbility.OUTPUT_LASER).setExactLimit(1))
                .or(Predicates.abilities(StarTPartAbility.REDSTONE_INTERFACE).setMaxGlobalLimited(1)))
            .where("@", Predicates.controller(Predicates.blocks(definition.get())))
            .build()
        )
        .workableCasingModel(KubeJS.id("block/casings/naquadah/casing"), StarTCore.resourceLocation("block/solar/overlay/uv"))
        .register();

    public static final MultiblockMachineDefinition SOLAR_ARRAY_UHV = START_REGISTRATE
        .multiblock("uhv_solar_array", holder -> new StarTSolarMachine(holder, UHV))
        .langValue("UHV Solar Array")
        .tooltips(
            Component.translatable("block.start_core.solar_multiblock_line").setStyle(Style.EMPTY.withColor(customGoldColor)),
            Component.translatable("solar.start_core.solar_machine.tooltip1"),
            Component.translatable("solar.start_core.solar_machine.tooltip2"),
            Component.translatable("solar.start_core.solar_machine.tooltip3"),
            Component.translatable("block.start_core.breaker_line"),
            Component.translatable("solar.start_core.solar_machine.tooltip4", StarTSolarMachine.getOutputModifier(UHV)),
            Component.translatable("solar.start_core.solar_machine.tooltip5"),
            Component.translatable("block.start_core.breaker_line"),
            Component.translatable("solar.start_core.solar_machine.tooltip6", "§72500", Component.translatable("material.gtceu.deionized_water")),
            Component.translatable("block.start_core.breaker_line"),
            Component.translatable("solar.start_core.solar_machine.tooltip7")
        )
        .recipeType(GTRecipeTypes.DUMMY_RECIPES)
        .appearanceBlock(() -> StarTMachineUtils.getKjsBlock("enriched_naquadah_machine_casing"))
        .pattern(definition -> FactoryBlockPattern.start()
            .aisle("                 ", "                 ", "                 ", "                 ", "     SSSSSSS     ", "                 ", "                 ")
            .aisle("                 ", "                 ", "                 ", "        F        ", "   SSSSSSSSSSS   ", "                 ", "                 ")
            .aisle("                 ", "                 ", "                 ", "        F        ", "  SSSSS   SSSSS  ", "                 ", "                 ")
            .aisle("                 ", "                 ", "        F        ", "     SSSSSSS     ", " SSS         SSS ", "                 ", "                 ")
            .aisle("                 ", "                 ", "        F        ", "    SSS   SSS    ", " SS           SS ", "                 ", "                 ")
            .aisle("                 ", "        F        ", "      SSSSS      ", "   SSS     SSS   ", "SSS           SSS", "                 ", "                 ")
            .aisle("      FCCCF      ", "        F        ", "     SS   SS     ", "   SS       SS   ", "SSS           SSS", "                 ", "                 ")
            .aisle("      CCCCC      ", "       FCF       ", "     S  C  S     ", "   S         S   ", "SS             SS", "                 ", "                 ")
            .aisle("      CCCCC      ", "     FFCCCFF     ", "   FFS CBC SFF   ", " FFS    F    SFF ", "SS      @      SS", "        F        ", "        F        ")
            .aisle("      CCCCC      ", "       FCF       ", "     S  C  S     ", "   S         S   ", "SS             SS", "                 ", "                 ")
            .aisle("      FCCCF      ", "        F        ", "     SS   SS     ", "   SS       SS   ", "SSS           SSS", "                 ", "                 ")
            .aisle("                 ", "        F        ", "      SSSSS      ", "   SSS     SSS   ", "SSS           SSS", "                 ", "                 ")
            .aisle("                 ", "                 ", "        F        ", "    SSS   SSS    ", " SS           SS ", "                 ", "                 ")
            .aisle("                 ", "                 ", "        F        ", "     SSSSSSS     ", " SSS         SSS ", "                 ", "                 ")
            .aisle("                 ", "                 ", "                 ", "        F        ", "  SSSSS   SSSSS  ", "                 ", "                 ")
            .aisle("                 ", "                 ", "                 ", "        F        ", "   SSSSSSSSSSS   ", "                 ", "                 ")
            .aisle("                 ", "                 ", "                 ", "                 ", "     SSSSSSS     ", "                 ", "                 ")
            .where(" ", Predicates.any())
            .where("S", StarTSolarCellPredicates.solarCells())
            .where("F", Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTCEuAPI.materialManager.getMaterial("naquadah_alloy"))))
            .where("B", Predicates.blocks(GTBlocks.BATTERY_ULTIMATE_UHV.get()))
            .where("C", Predicates.blocks(StarTMachineUtils.getKjsBlock("enriched_naquadah_machine_casing"))
                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(1))
                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(1))
                .or(Predicates.abilities(PartAbility.OUTPUT_LASER).setExactLimit(1))
                .or(Predicates.abilities(StarTPartAbility.REDSTONE_INTERFACE).setMaxGlobalLimited(1)))
            .where("@", Predicates.controller(Predicates.blocks(definition.get())))
            .build()
        )
        .workableCasingModel(KubeJS.id("block/casings/naquadah/casing"), StarTCore.resourceLocation("block/solar/overlay/uhv"))
        .register();

    public static void init() {
    }
}
