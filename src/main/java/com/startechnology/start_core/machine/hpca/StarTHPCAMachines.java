package com.startechnology.start_core.machine.hpca;

import java.util.ArrayList;
import java.util.List;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.MultiblockShapeInfo;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.data.machines.GTResearchMachines;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.research.HPCAMachine;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.data.lang.LangHandler;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.gui.StarTGuiTextures;
import com.startechnology.start_core.block.StarTRunicCasings;

import net.minecraft.core.Direction;

public class StarTHPCAMachines {

    public static final MachineDefinition IMPROVED_HIGH_PERFORMANCE_COMPUTING_ARRAY = StarTCore.START_REGISTRATE
            .multiblock("improved_high_performance_computation_array",
                    (holder) -> new HPCAMachine(holder, 5, StarTGuiTextures.HPCA_COMPONENT_OUTLINE_5X5))
            .langValue("Improved High Performance Computation Array [IHPCA]")
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(StarTRunicCasings.RUNIC_COMPUTER_CASING)
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
                    .where('A', Predicates.blocks(StarTRunicCasings.RUNIC_COMPUTER_CASING.get()))
                    .where('V', Predicates.blocks(GTBlocks.COMPUTER_HEAT_VENT.get()))
                    .where('X', Predicates.abilities(PartAbility.HPCA_COMPONENT))
                    .where('C', Predicates.blocks(GTBlocks.COMPUTER_CASING.get()).setMinGlobalLimited(5)
                            .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMinGlobalLimited(1)
                                    .setMaxGlobalLimited(2, 1))
                            .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.COMPUTATION_DATA_TRANSMISSION).setExactLimit(1))
                            .or(Predicates.autoAbilities(true, false, false)))
                    .build())
        /*
            .shapeInfos(definition -> {
                List<MultiblockShapeInfo> shapeInfo = new ArrayList<>();
                MultiblockShapeInfo.ShapeInfoBuilder builder = MultiblockShapeInfo.builder()
                        .aisle("SA", "MC", "CC", "CC", "CC", "CC", "AA")
                        .aisle("VA", "1V", "1V", "1V", "1V", "1V", "VA")
                        .aisle("VA", "1V", "2V", "1V", "3V", "1V", "VA")
                        .aisle("VA", "1V", "2V", "5V", "4V", "1V", "VA")
                        .aisle("VA", "1V", "2V", "1V", "3V", "1V", "VA")
                        .aisle("VA", "1V", "1V", "1V", "1V", "1V", "VA")
                        .aisle("AA", "EC", "HC", "OC", "CC", "CC", "AA")
                        .where('S', definition, Direction.NORTH)
                        .where('A', StarTRunicCasings.RUNIC_COMPUTER_CASING)
                        .where('V', GTBlocks.COMPUTER_HEAT_VENT)
                        .where('C', GTBlocks.COMPUTER_CASING)
                        .where('E', GTMachines.ENERGY_INPUT_HATCH[GTValues.ZPM], Direction.SOUTH)
                        .where('H', GTMachines.FLUID_IMPORT_HATCH[GTValues.LV], Direction.SOUTH)
                        .where('O', GTResearchMachines.COMPUTATION_HATCH_TRANSMITTER, Direction.SOUTH)
                        .where('M', ConfigHolder.INSTANCE.machines.enableMaintenance ?
                                GTMachines.MAINTENANCE_HATCH.defaultBlockState().setValue(
                                        GTMachines.MAINTENANCE_HATCH.get().getRotationState().property,
                                        Direction.NORTH) :
                                StarTRunicCasings.RUNIC_COMPUTER_CASING.getDefaultState());

                shapeInfo.add(builder.shallowCopy()
                        .where('1', StarTHPCAParts.HPCA_NANOFLUIDIC_HEAT_SINK_COMPONENT, Direction.WEST)
                        .where('2', StarTHPCAParts.HPCA_OPTIMIZED_COMPUTATION_COMPONENT, Direction.WEST)
                        .where('3', GTResearchMachines.HPCA_ADVANCED_COMPUTATION_COMPONENT, Direction.WEST)
                        .where('4', GTResearchMachines.HPCA_ACTIVE_COOLER_COMPONENT, Direction.WEST)
                        .where('5', GTResearchMachines.HPCA_BRIDGE_COMPONENT, Direction.WEST)
                        .build());

                return shapeInfo;
            })
        */
            .sidedWorkableCasingModel(StarTCore.resourceLocation("block/casings/hpca/runic_computer_casing"),
                    GTCEu.id("block/multiblock/hpca"))
            .register();

    public static void init() {}
}
