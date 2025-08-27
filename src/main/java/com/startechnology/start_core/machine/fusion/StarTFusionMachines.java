package com.startechnology.start_core.machine.fusion;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.MultiblockShapeInfo;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.FusionReactorMachine;
import com.startechnology.start_core.block.fusion.StarTFusionBlocks;
import com.startechnology.start_core.machine.StarTMachineUtils;
import com.startechnology.start_core.machine.StarTPartAbility;
import com.startechnology.start_core.recipe.StarTRecipeModifiers;
import com.gregtechceu.gtceu.api.pattern.Predicates;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class StarTFusionMachines {
    public static String fusionTierString(int tier) {
        return switch (tier) {
            case GTValues.UHV -> "I";
            default -> "I";
        };
    }

    public static final MultiblockMachineDefinition[] AUXILIARY_BOOSTED_FUSION_REACTOR = StarTMachineUtils.registerTieredMultis(
        "auxiliary_boosted_fusion_reactor", AuxiliaryBoostedFusionReactor::new, 
        (tier, builder) ->
            builder
            .rotationState(RotationState.ALL)
            .langValue("Auxiliary Boosted Fusion Reactor MK %s".formatted(fusionTierString(tier)))
            .recipeType(GTRecipeTypes.FUSION_RECIPES)
            .recipeModifiers(GTRecipeModifiers.DEFAULT_ENVIRONMENT_REQUIREMENT,
                    StarTRecipeModifiers.ABSOLUTE_PARALLEL,
                    AuxiliaryBoostedFusionReactor::recipeModifier)
            .tooltips(
                Component.translatable("start_core.machine.auxiliary_boosted_fusion_reactor.line"),
                Component.translatable("start_core.machine.auxiliary_boosted_fusion_reactor.description"),
                Component.translatable("block.start_core.breaker_line"),
                Component.translatable("start_core.machine.auxiliary_boosted_fusion_reactor.fusion_info"),
                Component.translatable("gtceu.machine.fusion_reactor.capacity",
                        AuxiliaryBoostedFusionReactor.calculateEnergyStorageFactor(tier, 16) / 1000000L),
                Component.translatable("start_core.machine.fusion_reactor.overclocking"),
                Component.literal(""),
                Component.translatable("start_core.machine.auxiliary_boosted_fusion_reactor.specific", 
                    GTValues.VN[tier], AuxiliaryBoostedFusionReactor.calculateEnergyStorageFactor(tier, 1) / 1000000L
                ),
                Component.literal(""),
                Component.translatable("start_core.machine.auxiliary_boosted_fusion_reactor.parallel_info"),
                Component.translatable("start_core.machine.auxiliary_boosted_fusion_reactor.parallel_info_1"),
                Component.translatable("block.start_core.breaker_line")
            )
            .appearanceBlock(() -> AuxiliaryBoostedFusionReactor.getCasingState(tier))
            .pattern((definition) -> { 
                var casing = Predicates.blocks(AuxiliaryBoostedFusionReactor.getCasingState(tier));
                return FactoryBlockPattern.start()
	                .aisle("#######################", "#######################", "###########B###########", "###########B###########", "###########B###########", "#######################", "#######################") 
	                .aisle("#######################", "###########B###########", "#######################", "#######################", "#######################", "###########B###########", "#######################") 
                    .aisle("###########B###########", "#######################", "#######################", "#########CDDDC#########", "#######################", "#######################", "###########B###########") 
                    .aisle("###########B###########", "#######################", "###B#####EAAAE#####B###", "###B###DD#####DD###B###", "###B#####EAAAE#####B###", "#######################", "###########B###########") 
                    .aisle("###########B###########", "####B#############B####", "#######AA#####AA#######", "#####DF##CDDDC##FD#####", "#######AA#####AA#######", "####B#############B####", "###########B###########") 
                    .aisle("#####B###########B#####", "###########B###########", "#####AA#########AA#####", "####DGGFD#####DFGGD####", "#####AA#########AA#####", "###########B###########", "#####B###########B#####") 
                    .aisle("######B#########B######", "#######################", "#####A#####B#####A#####", "####FGD####B####DGF####", "#####A#####B#####A#####", "#######################", "######B#########B######") 
                    .aisle("#######################", "#######B#######B#######", "####A#############A####", "###D#F###########F#D###", "####A#############A####", "#######B#######B#######", "#######################") 
                    .aisle("#######################", "#######################", "####A###B#####B###A####", "###D#D##B#####B##D#D###", "####A###B#####B###A####", "#######################", "#######################") 
                    .aisle("#######################", "#######################", "###E###############E###", "##C#C#############C#C##", "###E###############E###", "#######################", "#######################") 
                    .aisle("#######################", "#######################", "###A###############A###", "##D#D#############D#D##", "###A###############A###", "#######################", "#######################") 
                    .aisle("##BBB#############BBB##", "#B###B###########B###B#", "B##A##B#########B##A##B", "B#D#D#B#########B#D#D#B", "B##A##B#########B##A##B", "#B###B###########B###B#", "##BBB#############BBB##") 
                    .aisle("#######################", "#######################", "###A###############A###", "##D#D#############D#D##", "###A###############A###", "#######################", "#######################") 
                    .aisle("#######################", "#######################", "###E###############E###", "##C#C#############C#C##", "###E###############E###", "#######################", "#######################") 
                    .aisle("#######################", "#######################", "####A###B#####B###A####", "###D#D##B#####B##D#D###", "####A###B#####B###A####", "#######################", "#######################") 
                    .aisle("#######################", "#######B#######B#######", "####A#############A####", "###D#F###########F#D###", "####A#############A####", "#######B#######B#######", "#######################") 
                    .aisle("######B#########B######", "#######################", "#####A#####B#####A#####", "####FGD####B####DGF####", "#####A#####B#####A#####", "#######################", "######B#########B######") 
                    .aisle("#####B###########B#####", "###########B###########", "#####AA#########AA#####", "####DGGFD#####DFGGD####", "#####AA#########AA#####", "###########B###########", "#####B###########B#####") 
                    .aisle("###########B###########", "####B#############B####", "#######AA#####AA#######", "#####DF##CD@DC##FD#####", "#######AA#####AA#######", "####B#############B####", "###########B###########") 
                    .aisle("###########B###########", "#######################", "###B#####EAAAE#####B###", "###B###DD#####DD###B###", "###B#####EAAAE#####B###", "#######################", "###########B###########") 
                    .aisle("###########B###########", "#######################", "#######################", "#########CDSDC#########", "#######################", "#######################", "###########B###########") 
                    .aisle("#######################", "###########B###########", "#######################", "#######################", "#######################", "###########B###########", "#######################") 
                    .aisle("#######################", "#######################", "###########B###########", "###########B###########", "###########B###########", "#######################", "#######################") 
                    .where('S', Predicates.controller(Predicates.blocks(definition.get())))
                    .where("A", casing)
                    .where("#", Predicates.any())
                    .where("B", Predicates.blocks(AuxiliaryBoostedFusionReactor.getAuxiliaryCoilState(tier)))
                    .where("C", casing.or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMinGlobalLimited(1)))
                    .where("D", casing)
                    .where("E", casing.or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMinGlobalLimited(2)))
                    .where("G", Predicates.blocks(AuxiliaryBoostedFusionReactor.getCoilState(tier)))
                    .where("F", casing.or(
                        Predicates.blocks(PartAbility.INPUT_ENERGY.getBlockRange(tier, GTValues.UHV).toArray(Block[]::new))
                                .setMinGlobalLimited(1).setPreviewCount(16)))
                    .where('@', casing.or(Predicates.abilities(StarTPartAbility.ABSOLUTE_PARALLEL_HATCH)))
                    .build();
            })
            .shapeInfos((controller) -> {
                List<MultiblockShapeInfo> shapeInfos = new ArrayList<>();

                MultiblockShapeInfo.ShapeInfoBuilder baseBuilder = MultiblockShapeInfo.builder()
                    .aisle("#######################", "#######################", "###########B###########", "###########B###########", "###########B###########", "#######################", "#######################") 
                    .aisle("#######################", "###########B###########", "#######################", "#######################", "#######################", "###########B###########", "#######################") 
                    .aisle("###########B###########", "#######################", "#######################", "#########NDMDN#########", "#######################", "#######################", "###########B###########") 
                    .aisle("###########B###########", "#######################", "###B#####XAAAX#####B###", "###B###DD#####DD###B###", "###B#####UAAAU#####B###", "#######################", "###########B###########") 
                    .aisle("###########B###########", "####B#############B####", "#######AA#####AA#######", "#####Dn##SD@DS##nD#####", "#######AA#####AA#######", "####B#############B####", "###########B###########") 
                    .aisle("#####B###########B#####", "###########B###########", "#####AA#########AA#####", "####DGGsD#####DsGGD####", "#####AA#########AA#####", "###########B###########", "#####B###########B#####") 
                    .aisle("######B#########B######", "#######################", "#####A#####B#####A#####", "####wGD####B####DGe####", "#####A#####B#####A#####", "#######################", "######B#########B######") 
                    .aisle("#######################", "#######B#######B#######", "####A#############A####", "###D#e###########w#D###", "####A#############A####", "#######B#######B#######", "#######################") 
                    .aisle("#######################", "#######################", "####A###B#####B###A####", "###D#D##B#####B##D#D###", "####A###B#####B###A####", "#######################", "#######################") 
                    .aisle("#######################", "#######################", "###X###############X###", "##W#E#############W#E##", "###U###############U###", "#######################", "#######################") 
                    .aisle("#######################", "#######################", "###A###############A###", "##D#D#############D#D##", "###A###############A###", "#######################", "#######################") 
                    .aisle("##BBB#############BBB##", "#B###B###########B###B#", "B##A##B#########B##A##B", "B#D#D#B#########B#D#D#B", "B##A##B#########B##A##B", "#B###B###########B###B#", "##BBB#############BBB##") 
                    .aisle("#######################", "#######################", "###A###############A###", "##D#D#############D#D##", "###A###############A###", "#######################", "#######################") 
                    .aisle("#######################", "#######################", "###X###############X###", "##W#E#############W#E##", "###U###############U###", "#######################", "#######################") 
                    .aisle("#######################", "#######################", "####A###B#####B###A####", "###D#D##B#####B##D#D###", "####A###B#####B###A####", "#######################", "#######################") 
                    .aisle("#######################", "#######B#######B#######", "####A#############A####", "###D#e###########w#D###", "####A#############A####", "#######B#######B#######", "#######################") 
                    .aisle("######B#########B######", "#######################", "#####A#####B#####A#####", "####wGD####B####DGe####", "#####A#####B#####A#####", "#######################", "######B#########B######") 
                    .aisle("#####B###########B#####", "###########B###########", "#####AA#########AA#####", "####DGGnD#####DnGGD####", "#####AA#########AA#####", "###########B###########", "#####B###########B#####") 
                    .aisle("###########B###########", "####B#############B####", "#######AA#####AA#######", "#####Ds##NDDDN##sD#####", "#######AA#####AA#######", "####B#############B####", "###########B###########") 
                    .aisle("###########B###########", "#######################", "###B#####XAAAX#####B###", "###B###DD#####DD###B###", "###B#####UAAAU#####B###", "#######################", "###########B###########") 
                    .aisle("###########B###########", "#######################", "#######################", "#########SDDDS#########", "#######################", "#######################", "###########B###########") 
                    .aisle("#######################", "###########B###########", "#######################", "#######################", "#######################", "###########B###########", "#######################") 
                    .aisle("#######################", "#######################", "###########B###########", "###########B###########", "###########B###########", "#######################", "#######################") 
                    .where('M', controller, Direction.NORTH)
                    .where('A', AuxiliaryBoostedFusionReactor.getCasingState(tier))
                    .where('D', GTBlocks.FUSION_GLASS.get())
                    .where('#', Blocks.AIR.defaultBlockState())
                    .where('G', AuxiliaryBoostedFusionReactor.getCoilState(tier))
                    .where('W', GTMachines.FLUID_EXPORT_HATCH[tier], Direction.WEST)
                    .where('E', GTMachines.FLUID_EXPORT_HATCH[tier], Direction.EAST)
                    .where('S', GTMachines.FLUID_EXPORT_HATCH[tier], Direction.SOUTH)
                    .where('N', GTMachines.FLUID_EXPORT_HATCH[tier], Direction.NORTH)
                    .where('w', GTMachines.ENERGY_INPUT_HATCH[tier], Direction.WEST)
                    .where('e', GTMachines.ENERGY_INPUT_HATCH[tier], Direction.EAST)
                    .where('s', GTMachines.ENERGY_INPUT_HATCH[tier], Direction.SOUTH)
                    .where('n', GTMachines.ENERGY_INPUT_HATCH[tier], Direction.NORTH)
                    .where('U', GTMachines.FLUID_IMPORT_HATCH[tier], Direction.UP)
                    .where('X', GTMachines.FLUID_IMPORT_HATCH[tier], Direction.DOWN)
                    .where('@', AuxiliaryBoostedFusionReactor.getParallelHatch(tier), Direction.SOUTH)
                    .where('B', AuxiliaryBoostedFusionReactor.getAuxiliaryCoilState(tier));

                shapeInfos.add(baseBuilder.shallowCopy()
                    .where('A', AuxiliaryBoostedFusionReactor.getCasingState(tier))
                    .build());
                shapeInfos.add(baseBuilder.build());
                return shapeInfos;
            })
            .workableCasingRenderer(AuxiliaryBoostedFusionReactor.getCasingType(tier).getTexture(),
                GTCEu.id("block/multiblock/fusion_reactor"), false)
            .register(),
        GTValues.UHV);


    public static final MultiblockMachineDefinition[] FUSION_REACTOR_MK4 = StarTMachineUtils.registerTieredMultis(
        "fusion_reactor", FusionReactorMachine::new, 
        (tier, builder) ->
            builder
            .rotationState(RotationState.ALL)
            .langValue("Fusion Reactor MK %s".formatted(fusionTierString(tier)))
            .recipeType(GTRecipeTypes.FUSION_RECIPES)
            .recipeModifiers(GTRecipeModifiers.DEFAULT_ENVIRONMENT_REQUIREMENT,
                FusionReactorMachine::recipeModifier)
            .tooltips(
                Component.translatable("gtceu.machine.fusion_reactor.capacity",
                FusionReactorMachine.calculateEnergyStorageFactor(tier, 16) / 1000000L),
                Component.translatable("gtceu.machine.fusion_reactor.overclocking"),
                Component.translatable("start_core.multiblock.%s_fusion_reactor.description"
                    .formatted(GTValues.VN[tier].toLowerCase(Locale.ROOT)))
            )
            .appearanceBlock(() -> StarTFusionBlocks.FUSION_CASING_MK4.get())
            .pattern((definition) -> { 
                var casing = Predicates.blocks(StarTFusionBlocks.FUSION_CASING_MK4.get());
                return FactoryBlockPattern.start()
                    .aisle("                 ", "      BCCCB      ", "                 ") 
                    .aisle("      DEEED      ", "    CC#####CC    ", "      DEEED      ") 
                    .aisle("    EE     EE    ", "   F##BCCCB##F   ", "    EE     EE    ") 
                    .aisle("   E         E   ", "  FGFC     CFGF  ", "   E         E   ") 
                    .aisle("  E           E  ", " C#F         F#C ", "  E           E  ") 
                    .aisle("  E           E  ", " C#C         C#C ", "  E           E  ") 
                    .aisle(" D             D ", "B#B           B#B", " D             D ") 
                    .aisle(" E             E ", "C#C           C#C", " E             E ") 
                    .aisle(" E             E ", "C#C           C#C", " E             E ") 
                    .aisle(" E             E ", "C#C           C#C", " E             E ") 
                    .aisle(" D             D ", "B#B           B#B", " D             D ") 
                    .aisle("  E           E  ", " C#C         C#C ", "  E           E  ") 
                    .aisle("  E           E  ", " C#F         F#C ", "  E           E  ") 
                    .aisle("   E         E   ", "  FGFC     CFGF  ", "   E         E   ") 
                    .aisle("    EE     EE    ", "   F##BCCCB##F   ", "    EE     EE    ") 
                    .aisle("      DEEED      ", "    CC#####CC    ", "      DEEED      ") 
                    .aisle("                 ", "      BC@CB      ", "                 ")
                    .where(' ', Predicates.any())
                    .where('#', Predicates.air())
                    .where('B', casing
                        .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMinGlobalLimited(1)))
                    .where('C', casing)
                    .where('D', casing
                        .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMinGlobalLimited(1)))
                    .where('E', casing)
                    .where('F', casing
                        .or(Predicates.blocks(PartAbility.INPUT_ENERGY.getBlockRange(tier, GTValues.UEV).toArray(Block[]::new)).setMinGlobalLimited(1).setPreviewCount(16))) 
                    .where('G', Predicates.blocks(StarTFusionBlocks.ADVANCED_FUSION_COIL.get()))
                    .where('@', Predicates.controller(Predicates.blocks(definition.get())))
                .build();
            })
            .shapeInfos((controller) -> {
                List<MultiblockShapeInfo> shapeInfos = new ArrayList<>();

                MultiblockShapeInfo.ShapeInfoBuilder baseBuilder = MultiblockShapeInfo.builder()
                    .aisle("                 ", "      BCCCB      ", "                 ") 
                    .aisle("      DEEED      ", "    CC#####CC    ", "      DEEED      ") 
                    .aisle("    EE     EE    ", "   F##BCCCB##F   ", "    EE     EE    ") 
                    .aisle("   E         E   ", "  FGFC     CFGF  ", "   E         E   ") 
                    .aisle("  E           E  ", " C#F         F#C ", "  E           E  ") 
                    .aisle("  E           E  ", " C#C         C#C ", "  E           E  ") 
                    .aisle(" D             D ", "B#B           B#B", " D             D ") 
                    .aisle(" E             E ", "C#C           C#C", " E             E ") 
                    .aisle(" E             E ", "C#C           C#C", " E             E ") 
                    .aisle(" E             E ", "C#C           C#C", " E             E ") 
                    .aisle(" D             D ", "B#B           B#B", " D             D ") 
                    .aisle("  E           E  ", " C#C         C#C ", "  E           E  ") 
                    .aisle("  E           E  ", " C#F         F#C ", "  E           E  ") 
                    .aisle("   E         E   ", "  FGFC     CFGF  ", "   E         E   ") 
                    .aisle("    EE     EE    ", "   F##BCCCB##F   ", "    EE     EE    ") 
                    .aisle("      DEEED      ", "    CC#####CC    ", "      DEEED      ") 
                    .aisle("                 ", "      BC@CB      ", "                 ")
                    .where(' ', Predicates.any())
                    .where('#', Predicates.air())
                    .where('B', casing
                        .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMinGlobalLimited(1)))
                    .where('C', Predicates.blocks(StarTMachineUtils.getKjsBlock("kubejs:draco_resilient_fusion_glass")))
                    .where('D', casing
                        .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMinGlobalLimited(1)))
                    .where('E', casing)
                    .where('F', casing
                        .or(Predicates.blocks(PartAbility.INPUT_ENERGY.getBlockRange(tier, GTValues.UEV).toArray(Block[]::new)).setMinGlobalLimited(1).setPreviewCount(16)))                            
                    .where('G', Predicates.blocks(StarTFusionBlocks.ADVANCED_FUSION_COIL.get()))
                    .where('@', controller(Predicates.blocks(definition.get())))

                shapeInfos.add(baseBuilder.shallowCopy()
                    .build());
                shapeInfos.add(baseBuilder.build());
                return shapeInfos;
            })
            .workableCasingRenderer(StarTFusionCasings.FUSION_CASING_MK4.getTexture(),
                GTCEu.id("block/multiblock/fusion_reactor"), false)
            .register(),
        GTValues.UEV);

    public static void init() {
        FusionReactorMachine.registerFusionTier(GTValues.UHV, " (AUXI)");
        FusionReactorMachine.registerFusionTier(GTValues.UEV, " (MKIV)");
        FusionReactorMachine.registerFusionTier(GTValues.UIV, " (AUXII)");
    }   
}
