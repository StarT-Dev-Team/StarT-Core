package com.startechnology.start_core.machine.solar;

import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.block.solar.StarTSolarCellBlocks;
import com.startechnology.start_core.machine.StarTMachineUtils;
import com.startechnology.start_core.machine.StarTPartAbility;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.world.level.block.Blocks;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

public class StarTSolarMachines {
    public static final MultiblockMachineDefinition SOLAR_PANEL_EV = START_REGISTRATE
            .multiblock("solar_panel_ev", holder -> new StarTSolarMachine(holder, EV))
            .langValue("EV Solar Panel")
            .rotationState(RotationState.Y_AXIS)
            .recipeType(StarTRecipeTypes.SOLAR_ENERGY)
            .appearanceBlock(() -> StarTMachineUtils.getKjsBlock("ev_solar_casing"))
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("CCC", "SSS")
                    .aisle("CCC", "SSS")
                    .aisle("C@C", "SSS")
                    .where("S", Predicates.blocks(StarTSolarCellBlocks.EV_SOLAR_CELL.get()))
                    .where("C", Predicates.blocks(StarTMachineUtils.getKjsBlock("ev_solar_casing"))
                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(1).setPreviewCount(1))
                            .or(Predicates.abilities(PartAbility.OUTPUT_ENERGY).setMaxGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1))
                            .or(Predicates.abilities(StarTPartAbility.REDSTONE_INTERFACE).setMaxGlobalLimited(1).setPreviewCount(1)))
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .build()
            )
            .workableCasingModel(KubeJS.id("block/casing/solar_casings/ev_solar_casing"), StarTCore.resourceLocation("block/overlay/sterile_maintenance"))
            .register();

    public static final MultiblockMachineDefinition SOLAR_PANEL_IV = START_REGISTRATE
            .multiblock("solar_panel_iv", holder -> new StarTSolarMachine(holder, EV))
            .langValue("IV Solar Panel")
            .rotationState(RotationState.Y_AXIS)
            .recipeType(StarTRecipeTypes.SOLAR_ENERGY)
            .appearanceBlock(() -> StarTMachineUtils.getKjsBlock("iv_solar_casing"))
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("CCCCC", "SSSSS")
                    .aisle("CCCCC", "SSSSS")
                    .aisle("CCCCC", "SSSSS")
                    .aisle("CCCCC", "SSSSS")
                    .aisle("CC@CC", "SSSSS")
                    .where("S", Predicates.blocks(StarTSolarCellBlocks.IV_SOLAR_CELL.get()))
                    .where("C", Predicates.blocks(StarTMachineUtils.getKjsBlock("iv_solar_casing"))
                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(1).setPreviewCount(1))
                            .or(Predicates.abilities(PartAbility.OUTPUT_ENERGY).setMaxGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1))
                            .or(Predicates.abilities(StarTPartAbility.REDSTONE_INTERFACE).setMaxGlobalLimited(1).setPreviewCount(1)))
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .build()
            )
            .workableCasingModel(KubeJS.id("block/casing/solar_casings/iv_solar_casing"), StarTCore.resourceLocation("block/overlay/sterile_maintenance"))
            .register();

    public static final MultiblockMachineDefinition SOLAR_PANEL_LUV = START_REGISTRATE
            .multiblock("solar_panel_luv", holder -> new StarTSolarMachine(holder, EV))
            .langValue("LuV Solar Panel")
            .rotationState(RotationState.Y_AXIS)
            .recipeType(StarTRecipeTypes.SOLAR_ENERGY)
            .appearanceBlock(() -> StarTMachineUtils.getKjsBlock("luv_solar_casing"))
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("CCCCCCC", "SSSSSSS")
                    .aisle("CCCCCCC", "SSSSSSS")
                    .aisle("CCCCCCC", "SSSSSSS")
                    .aisle("CCCCCCC", "SSSSSSS")
                    .aisle("CCCCCCC", "SSSSSSS")
                    .aisle("CCCCCCC", "SSSSSSS")
                    .aisle("CCC@CCC", "SSSSSSS")
                    .where("S", Predicates.blocks(StarTSolarCellBlocks.LUV_SOLAR_CELL.get()))
                    .where("C", Predicates.blocks(StarTMachineUtils.getKjsBlock("luv_solar_casing"))
                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(1).setPreviewCount(1))
                            .or(Predicates.abilities(PartAbility.OUTPUT_ENERGY).setMaxGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1))
                            .or(Predicates.abilities(StarTPartAbility.REDSTONE_INTERFACE).setMaxGlobalLimited(1).setPreviewCount(1)))
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .build()
            )
            .workableCasingModel(KubeJS.id("block/casing/solar_casings/luv_solar_casing"), StarTCore.resourceLocation("block/overlay/sterile_maintenance"))
            .register();

    public static final MultiblockMachineDefinition SOLAR_ARRAY_UV = START_REGISTRATE
            .multiblock("solar_array_uv", holder -> new StarTSolarMachine(holder, UV))
            .langValue("UV Solar Panel")
            .rotationState(RotationState.Y_AXIS)
            .recipeType(StarTRecipeTypes.SOLAR_ENERGY)
            .appearanceBlock(() -> StarTMachineUtils.getKjsBlock("uv_solar_casing"))
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("   SSSSS   ")
                    .aisle("  S     S  ")
                    .aisle(" S  SSS  S ")
                    .aisle("S  S   S  S")
                    .aisle("S S CCC S S")
                    .aisle("S S CCC S S")
                    .aisle("S S C@C S S")
                    .aisle("S  S   S  S")
                    .aisle(" S  SSS  S ")
                    .aisle("  S     S  ")
                    .aisle("   SSSSS   ")
                    .where("S", Predicates.blocks(StarTSolarCellBlocks.UV_SOLAR_CELL.get()))
                    .where("C", Predicates.blocks(StarTMachineUtils.getKjsBlock("uv_solar_casing"))
                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(1).setPreviewCount(1))
                            .or(Predicates.abilities(PartAbility.OUTPUT_ENERGY).setMaxGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1))
                            .or(Predicates.abilities(StarTPartAbility.REDSTONE_INTERFACE).setMaxGlobalLimited(1).setPreviewCount(1)))
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .build()
            )
            .workableCasingModel(KubeJS.id("block/casing/solar_casings/uv_solar_casing"), StarTCore.resourceLocation("block/overlay/sterile_maintenance"))
            .register();

    public static final MultiblockMachineDefinition SOLAR_ARRAY_UHV = START_REGISTRATE
            .multiblock("solar_array_uhv", holder -> new StarTSolarMachine(holder, UHV))
            .langValue("UHV Solar Panel")
            .rotationState(RotationState.Y_AXIS)
            .recipeType(StarTRecipeTypes.SOLAR_ENERGY)
            .appearanceBlock(() -> StarTMachineUtils.getKjsBlock("uhv_solar_casing"))
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("     SSSSS     ")
                    .aisle("   SS     SS   ")
                    .aisle("  S  SSSSS  S  ")
                    .aisle(" S  S     S  S ")
                    .aisle(" S S  SSS  S S ")
                    .aisle("S S  S   S  S S")
                    .aisle("S S S CCC S S S")
                    .aisle("S S S CCC S S S")
                    .aisle("S S S C@C S S S")
                    .aisle("S S  S   S  S S")
                    .aisle(" S S  SSS  S S ")
                    .aisle(" S  S     S  S ")
                    .aisle("  S  SSSSS  S  ")
                    .aisle("   SS     SS   ")
                    .aisle("     SSSSS     ")
                    .where("S", Predicates.blocks(StarTSolarCellBlocks.UHV_SOLAR_CELL.get()))
                    .where("C", Predicates.blocks(StarTMachineUtils.getKjsBlock("uhv_solar_casing"))
                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(1).setPreviewCount(1))
                            .or(Predicates.abilities(PartAbility.OUTPUT_ENERGY).setMaxGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1))
                            .or(Predicates.abilities(StarTPartAbility.REDSTONE_INTERFACE).setMaxGlobalLimited(1).setPreviewCount(1)))
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .build()
            )
            .workableCasingModel(KubeJS.id("block/casing/solar_casings/uhv_solar_casing"), StarTCore.resourceLocation("block/overlay/sterile_maintenance"))
            .register();

    public static void init() {}
}
