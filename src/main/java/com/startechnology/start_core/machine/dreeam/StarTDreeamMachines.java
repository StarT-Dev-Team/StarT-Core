package com.startechnology.start_core.machine.dreeam;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.MultiblockShapeInfo;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.machine.StarTMachineUtils;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.ArrayList;

import static com.gregtechceu.gtceu.api.GTValues.UV;
import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

public class StarTDreeamMachines {

    public static final MultiblockMachineDefinition DREEAM = START_REGISTRATE
            .multiblock("dimensional_runically_engraved_energy_access_medium", StarTDreeamMachine::new)
            .langValue("Dimensional Runically Engraved Energy Access Medium [DREEAM]")
            .tooltips(
                    Component.translatable("start_core.machine.dreeam.tooltip.0"),
                    Component.translatable("block.start_core.breaker_line"),
                    Component.translatable("start_core.machine.dreeam.tooltip.1"),
                    Component.translatable("start_core.machine.dreeam.tooltip.2"),
                    Component.translatable("start_core.machine.dreeam.tooltip.3"),
                    Component.translatable("block.start_core.breaker_line"),
                    Component.translatable("start_core.machine.dreeam.tooltip.4"),
                    Component.translatable("start_core.machine.dreeam.tooltip.5"),
                    Component.translatable("start_core.machine.dreeam.tooltip.6"),
                    Component.translatable("start_core.machine.dreeam.tooltip.7"))
            .bottomTooltips(Component.translatable("start.multiblock_contest.1M.top_5", Component.literal("ggki")))
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTRecipeTypes.DUMMY_RECIPES)
            .appearanceBlock(GTBlocks.ADVANCED_COMPUTER_CASING)
            .pattern(definition -> FactoryBlockPattern.start()
                    // spotless:off
                    .aisle("     A     ", "      A    ", "           ", "           ", "           ", "    A      ", "     A     ", "      A    ", "           ", "           ", "           ", "    A      ", "     A     ", "      A    ", "           ", "           ", "           ")
                    .aisle("   CCCCC   ", "           ", "       A   ", "           ", "   A       ", "           ", "           ", "           ", "       A   ", "           ", "   A       ", "           ", "           ", "           ", "       A   ", "           ", "    AAA    ")
                    .aisle("  CCCCCCC  ", "   CCCCC   ", "           ", "  A     A  ", "           ", "           ", "           ", "           ", "           ", "  ADDDDDA  ", "           ", "  E     E  ", "     E     ", "           ", "           ", "  A     A  ", "   A   A   ")
                    .aisle(" CCCCCCCCC ", "  CFFGFFC  ", " A FAGAF   ", "   FAGAF   ", "   FGGGF A ", "   FGBGF   ", "   FGGGF   ", "   FAGAF   ", " A FAGAF   ", "  DFGGGFD  ", "   F G F A ", "     G     ", "           ", "   E   E   ", " A   E     ", "           ", "  A     A  ")
                    .aisle(" CCCCCCCCC ", "A CFCCCFC  ", "   AEEEA   ", "   AEEEA   ", "   GEEEG   ", "   GEEEG  A", "   GEEEG   ", "A  AEEEA   ", "   AEEEA   ", "  DGAAAGD  ", "           ", "          A", "           ", "A          ", "           ", "           ", " A       A ")
                    .aisle("ACCCCCCCCCA", "  CGCFCGC  ", "   GEFEG   ", "   GEFEG   ", "   GEFEG   ", "   HEFEH   ", "A  GEFEG  A", "   GEFEG   ", "   GEFEG   ", "  DGAAAGD  ", "   G   G   ", "   G   G   ", "A E     E A", "           ", "   E   E   ", "           ", " A   E   A ")
                    .aisle(" CCCCCCCCC ", "  CFCCCFC A", "   AEEEA   ", "   AEEEA   ", "   GEEEG   ", "A  GEEEG   ", "   GEEEG   ", "   AEEEA  A", "   AEEEA   ", "  DGAAAGD  ", "           ", "A          ", "           ", "          A", "           ", "           ", " A       A ")
                    .aisle(" CCCCCCCCC ", "  CFFGFFC  ", "   FAGAF A ", "   FAGAF   ", " A FGGGF   ", "   FG@GF   ", "   FGGGF   ", "   FAGAF   ", "   FAGAF A ", "  DFGGGFD  ", " A F G F   ", "     G     ", "           ", "   E   E   ", "     E   A ", "           ", "  A     A  ")
                    .aisle("  CCCCCCC  ", "   CCCCC   ", "           ", "  A     A  ", "           ", "           ", "           ", "           ", "           ", "  ADDDDDA  ", "           ", "  E     E  ", "     E     ", "           ", "           ", "  A     A  ", "   A   A   ")
                    .aisle("   CCCCC   ", "           ", "   A       ", "           ", "       A   ", "           ", "           ", "           ", "   A       ", "           ", "       A   ", "           ", "           ", "           ", "   A       ", "           ", "    AAA    ")
                    .aisle("     A     ", "    A      ", "           ", "           ", "           ", "      A    ", "     A     ", "    A      ", "           ", "           ", "           ", "      A    ", "     A     ", "    A      ", "           ", "           ", "           ")
                    // spotless:on
                    .where(' ', Predicates.any())
                    .where('A', Predicates.blocks(GTBlocks.FUSION_GLASS.get()))
                    .where('B', Predicates.blocks(StarTMachineUtils.getBlock("sgjourney:classic_stargate_base_block")))
                    .where('C', Predicates.blocks(GTBlocks.COMPUTER_CASING.get()))
                    .where('D', Predicates.blocks(GTBlocks.COMPUTER_HEAT_VENT.get()))
                    .where('E', Predicates.blocks(GTBlocks.SUPERCONDUCTING_COIL.get()))
                    .where('F', Predicates.blocks(GTBlocks.HIGH_POWER_CASING.get()))
                    .where('G', Predicates.blocks(GTBlocks.ADVANCED_COMPUTER_CASING.get())
                            .or(Predicates.abilities(PartAbility.INPUT_LASER).setMinGlobalLimited(0))
                            .or(Predicates.abilities(PartAbility.OUTPUT_LASER).setMinGlobalLimited(0)))
                    .where('H',
                            Predicates.blocks(StarTMachineUtils.getBlock("sgjourney:classic_stargate_chevron_block")))
                    .where('@', Predicates.controller(Predicates.blocks(definition.get())))
                    .build())
            .shapeInfos(definition -> {
                var shapes = new ArrayList<MultiblockShapeInfo>();
                var pattern = MultiblockShapeInfo.builder()
                        // spotless:off
                    .aisle("     A     ", "      A    ", "           ", "           ", "           ", "    A      ", "     A     ", "      A    ", "           ", "           ", "           ", "    A      ", "     A     ", "      A    ", "           ", "           ", "           ")
                    .aisle("   CCCCC   ", "           ", "       A   ", "           ", "   A       ", "           ", "           ", "           ", "       A   ", "           ", "   A       ", "           ", "           ", "           ", "       A   ", "           ", "    AAA    ")
                    .aisle("  CCCCCCC  ", "   CCCCC   ", "           ", "  A     A  ", "           ", "           ", "           ", "           ", "           ", "  ADDDDDA  ", "           ", "  E     E  ", "     E     ", "           ", "           ", "  A     A  ", "   A   A   ")
                    .aisle(" CCCCCCCCC ", "  CFFGFFC  ", " A FAGAF   ", "   FAGAF   ", "   FLLLF A ", "   FL@LF   ", "   FLLLF   ", "   FAGAF   ", " A FAGAF   ", "  DFGGGFD  ", "   F G F A ", "     G     ", "           ", "   E   E   ", " A   E     ", "           ", "  A     A  ")
                    .aisle(" CCCCCCCCC ", "A CFCCCFC  ", "   AEEEA   ", "   AEEEA   ", "   GEEEG   ", "   GEEEG  A", "   GEEEG   ", "A  AEEEA   ", "   AEEEA   ", "  DGAAAGD  ", "           ", "          A", "           ", "A          ", "           ", "           ", " A       A ")
                    .aisle("ACCCCCCCCCA", "  CGCFCGC  ", "   GEFEG   ", "   GEFEG   ", "   GEFEG   ", "   HEFEH   ", "A  GEFEG  A", "   GEFEG   ", "   GEFEG   ", "  DGAAAGD  ", "   G   G   ", "   G   G   ", "A E     E A", "           ", "   E   E   ", "           ", " A   E   A ")
                    .aisle(" CCCCCCCCC ", "  CFCCCFC A", "   AEEEA   ", "   AEEEA   ", "   GEEEG   ", "A  GEEEG   ", "   GEEEG   ", "   AEEEA  A", "   AEEEA   ", "  DGAAAGD  ", "           ", "A          ", "           ", "          A", "           ", "           ", " A       A ")
                    .aisle(" CCCCCCCCC ", "  CFFGFFC  ", "   FAGAF A ", "   FAGAF   ", " A FGGGF   ", "   FGBGF   ", "   FGGGF   ", "   FAGAF   ", "   FAGAF A ", "  DFGGGFD  ", " A F G F   ", "     G     ", "           ", "   E   E   ", "     E   A ", "           ", "  A     A  ")
                    .aisle("  CCCCCCC  ", "   CCCCC   ", "           ", "  A     A  ", "           ", "           ", "           ", "           ", "           ", "  ADDDDDA  ", "           ", "  E     E  ", "     E     ", "           ", "           ", "  A     A  ", "   A   A   ")
                    .aisle("   CCCCC   ", "           ", "   A       ", "           ", "       A   ", "           ", "           ", "           ", "   A       ", "           ", "       A   ", "           ", "           ", "           ", "   A       ", "           ", "    AAA    ")
                    .aisle("     A     ", "    A      ", "           ", "           ", "           ", "      A    ", "     A     ", "    A      ", "           ", "           ", "           ", "      A    ", "     A     ", "    A      ", "           ", "           ", "           ")
                    // spotless:on
                        .where(' ', Blocks.AIR)
                        .where('A', GTBlocks.FUSION_GLASS)
                        .where('B', StarTMachineUtils.block("sgjourney:classic_stargate_base_block")
                                .defaultBlockState()
                                .trySetValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH))
                        .where('C', GTBlocks.COMPUTER_CASING)
                        .where('D', GTBlocks.COMPUTER_HEAT_VENT)
                        .where('E', GTBlocks.SUPERCONDUCTING_COIL)
                        .where('F', GTBlocks.HIGH_POWER_CASING)
                        .where('G', GTBlocks.ADVANCED_COMPUTER_CASING)
                        .where('H', StarTMachineUtils.block("sgjourney:classic_stargate_chevron_block"))
                        .where('@', definition, Direction.NORTH);

                shapes.add(pattern.shallowCopy()
                        .where('L', GTMachines.LASER_INPUT_HATCH_4096[UV], Direction.NORTH)
                        .build());
                shapes.add(pattern.shallowCopy()
                        .where('L', GTMachines.LASER_OUTPUT_HATCH_4096[UV], Direction.NORTH)
                        .build());

                return shapes;
            })
            .sidedWorkableCasingModel(GTCEu.id("block/casings/hpca/advanced_computer_casing"),
                    StarTCore.resourceLocation("block/dreamlink/dream_link_node"))
            .register();

    public static void init() {}
}
