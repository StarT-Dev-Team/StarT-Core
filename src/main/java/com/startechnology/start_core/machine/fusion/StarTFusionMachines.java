package com.startechnology.start_core.machine.fusion;

import com.google.common.collect.Streams;
import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.MultiblockShapeInfo;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.registry.registrate.MultiblockMachineBuilder;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.FusionReactorMachine;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Pair;
import com.startechnology.start_core.api.StarTAPI;
import com.startechnology.start_core.machine.StarTMachineUtils;
import com.startechnology.start_core.machine.StarTPartAbility;
import com.startechnology.start_core.recipe.StarTRecipeModifiers;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.stream.Collector;

public class StarTFusionMachines {

    public static final MultiblockMachineDefinition[] FUSION_REACTORS = StarTMachineUtils.registerTieredMultis(
        "fusion_reactor",
        ReflectorFusionReactorMachine::new, StarTFusionMachines::buildFusionReactor,
        GTValues.LuV, GTValues.ZPM, GTValues.UV, GTValues.UHV, GTValues.UEV, GTValues.UIV
    );

    private static MultiblockMachineDefinition buildFusionReactor(int tier, MultiblockMachineBuilder builder) {
        var aislesPair = switch (tier) {
            case GTValues.UHV -> Pair.of(7, new String[]{
                "                       ", "                       ", "           A           ", "           A           ", "           A           ", "                       ", "                       ",
                "                       ", "           A           ", "                       ", "                       ", "                       ", "           A           ", "                       ",
                "           A           ", "                       ", "                       ", "         OGGGO         ", "                       ", "                       ", "           A           ",
                "           A           ", "                       ", "   A     ICCCI     A   ", "   A   GG#####GG   A   ", "   A     ICCCI     A   ", "                       ", "           A           ",
                "           A           ", "    A             A    ", "       CC     CC       ", "     GE##OGGGO##EG     ", "       CC     CC       ", "    A             A    ", "           A           ",
                "     A           A     ", "           A           ", "     CC         CC     ", "    GKKEG     GEKKG    ", "     CC         CC     ", "           A           ", "     A           A     ",
                "      A         A      ", "                       ", "     C     A     C     ", "    EKG    A    GKE    ", "     C     A     C     ", "                       ", "      A         A      ",
                "                       ", "       A       A       ", "    C             C    ", "   G#E           E#G   ", "    C             C    ", "       A       A       ", "                       ",
                "                       ", "                       ", "    C   A     A   C    ", "   G#G  A     A  G#G   ", "    C   A     A   C    ", "                       ", "                       ",
                "                       ", "                       ", "   I               I   ", "  O#O             O#O  ", "   I               I   ", "                       ", "                       ",
                "                       ", "                       ", "   C               C   ", "  G#G             G#G  ", "   C               C   ", "                       ", "                       ",
                "  AAA             AAA  ", " A   A           A   A ", "A  C  A         A  C  A", "A G#G A         A G#G A", "A  C  A         A  C  A", " A   A           A   A ", "  AAA             AAA  ",
                "                       ", "                       ", "   C               C   ", "  G#G             G#G  ", "   C               C   ", "                       ", "                       ",
                "                       ", "                       ", "   I               I   ", "  O#O             O#O  ", "   I               I   ", "                       ", "                       ",
                "                       ", "                       ", "    C   A     A   C    ", "   G#G  A     A  G#G   ", "    C   A     A   C    ", "                       ", "                       ",
                "                       ", "       A       A       ", "    C             C    ", "   G#E           E#G   ", "    C             C    ", "       A       A       ", "                       ",
                "      A         A      ", "                       ", "     C     A     C     ", "    EKG    A    GKE    ", "     C     A     C     ", "                       ", "      A         A      ",
                "     A           A     ", "           A           ", "     CC         CC     ", "    GKKEG     GEKKG    ", "     CC         CC     ", "           A           ", "     A           A     ",
                "           A           ", "    A             A    ", "       CC     CC       ", "     GE##OG@GO##EG     ", "       CC     CC       ", "    A             A    ", "           A           ",
                "           A           ", "                       ", "   A     ICCCI     A   ", "   A   GG#####GG   A   ", "   A     ICCCI     A   ", "                       ", "           A           ",
                "           A           ", "                       ", "                       ", "         OGSGO         ", "                       ", "                       ", "           A           ",
                "                       ", "           A           ", "                       ", "                       ", "                       ", "           A           ", "                       ",
                "                       ", "                       ", "           A           ", "           A           ", "           A           ", "                       ", "                       ",
            });
            case GTValues.UEV -> Pair.of(3, new String[]{
                "                 ", "      OGGGO      ", "                 ",
                "      ICCCI      ", "    GG#####GG    ", "      ICCCI      ",
                "    CC     CC    ", "   E##OGGGO##E   ", "    CC     CC    ",
                "   C         C   ", "  EKEG     GEKE  ", "   C         C   ",
                "  C           C  ", " G#E         E#G ", "  C           C  ",
                "  C           C  ", " G#G         G#G ", "  C           C  ",
                " I             I ", "O#O           O#O", " I             I ",
                " C             C ", "G#G           G#G", " C             C ",
                " C             C ", "G#G           G#G", " C             C ",
                " C             C ", "G#G           G#G", " C             C ",
                " I             I ", "O#O           O#O", " I             I ",
                "  C           C  ", " G#G         G#G ", "  C           C  ",
                "  C           C  ", " G#E         E#G ", "  C           C  ",
                "   C         C   ", "  EKEG     GEKE  ", "   C         C   ",
                "    CC     CC    ", "   E##OGGGO##E   ", "    CC     CC    ",
                "      ICCCI      ", "    GG#####GG    ", "      ICCCI      ",
                "                 ", "      OGSGO      ", "                 ",
            });
            case GTValues.UIV -> Pair.of(7, new String[]{
                "                         ", "                         ", "         A     A         ", "         A     A         ", "         A     A         ", "                         ", "                         ",
                "                         ", "         A     A         ", "                         ", "                         ", "                         ", "         A     A         ", "                         ",
                "          A   A          ", "                         ", "                         ", "          OGGGO          ", "                         ", "                         ", "          A   A          ",
                "          A   A          ", "                         ", "   A      ICCCI      A   ", "   A    GG#####GG    A   ", "   A      ICCCI      A   ", "                         ", "          A   A          ",
                "          A   A          ", "    A               A    ", "        CC     CC        ", "      GE##OGGGO##EG      ", "        CC     CC        ", "    A               A    ", "          A   A          ",
                "     A             A     ", "           A A           ", "      CC         CC      ", "     GKKEG     GEKKG     ", "      CC         CC      ", "           A A           ", "     A             A     ",
                "      A           A      ", "                         ", "     CC    A A    CC     ", "    GKKG   A A   GKKG    ", "     CC    A A    CC     ", "                         ", "      A           A      ",
                "                         ", "       A         A       ", "     C             C     ", "    EKG           GKE    ", "     C             C     ", "       A         A       ", "                         ",
                "                         ", "                         ", "    C   A       A   C    ", "   G#E  A       A  E#G   ", "    C   A       A   C    ", "                         ", "                         ",
                "                         ", " A                     A ", "A   C               C   A", "A  G#G             G#G  A", "A   C               C   A", " A                     A ", "                         ",
                "  AAA               AAA  ", "                         ", "   I                 I   ", "  O#O               O#O  ", "   I                 I   ", "                         ", "  AAA               AAA  ",
                "                         ", "     A             A     ", "   C  A           A  C   ", "  G#G A           A G#G  ", "   C  A           A  C   ", "     A             A     ", "                         ",
                "                         ", "                         ", "   C                 C   ", "  G#G               G#G  ", "   C                 C   ", "                         ", "                         ",
                "                         ", "     A             A     ", "   C  A           A  C   ", "  G#G A           A G#G  ", "   C  A           A  C   ", "     A             A     ", "                         ",
                "  AAA               AAA  ", "                         ", "   I                 I   ", "  O#O               O#O  ", "   I                 I   ", "                         ", "  AAA               AAA  ",
                "                         ", " A                     A ", "A   C               C   A", "A  G#G             G#G  A", "A   C               C   A", " A                     A ", "                         ",
                "                         ", "                         ", "    C   A       A   C    ", "   G#E  A       A  E#G   ", "    C   A       A   C    ", "                         ", "                         ",
                "                         ", "       A         A       ", "     C             C     ", "    EKG           GKE    ", "     C             C     ", "       A         A       ", "                         ",
                "      A           A      ", "                         ", "     CC    A A    CC     ", "    GKKG   A A   GKKG    ", "     CC    A A    CC     ", "                         ", "      A           A      ",
                "     A             A     ", "           A A           ", "      CC         CC      ", "     GKKEG     GEKKG     ", "      CC         CC      ", "           A A           ", "     A             A     ",
                "          A   A          ", "    A               A    ", "        CC     CC        ", "      GE##OG@GO##EG      ", "        CC     CC        ", "    A               A    ", "          A   A          ",
                "          A   A          ", "                         ", "   A      ICCCI      A   ", "   A    GG#####GG    A   ", "   A      ICCCI      A   ", "                         ", "          A   A          ",
                "          A   A          ", "                         ", "                         ", "          OGSGO          ", "                         ", "                         ", "          A   A          ",
                "                         ", "         A     A         ", "                         ", "                         ", "                         ", "         A     A         ", "                         ",
                "                         ", "                         ", "         A     A         ", "         A     A         ", "         A     A         ", "                         ", "                         ",
            });
            default -> Pair.of(3, new String[]{
                "               ", "      OGO      ", "               ",
                "      ICI      ", "    GG###GG    ", "      ICI      ",
                "    CC   CC    ", "   E##OGO##E   ", "    CC   CC    ",
                "   C       C   ", "  EKEG   GEKE  ", "   C       C   ",
                "  C         C  ", " G#E       E#G ", "  C         C  ",
                "  C         C  ", " G#G       G#G ", "  C         C  ",
                " I           I ", "O#O         O#O", " I           I ",
                " C           C ", "G#G         G#G", " C           C ",
                " I           I ", "O#O         O#O", " I           I ",
                "  C         C  ", " G#G       G#G ", "  C         C  ",
                "  C         C  ", " G#E       E#G ", "  C         C  ",
                "   C       C   ", "  EKEG   GEKE  ", "   C       C   ",
                "    CC   CC    ", "   E##OGO##E   ", "    CC   CC    ",
                "      ICI      ", "    GG###GG    ", "      ICI      ",
                "               ", "      OSO      ", "               ",
            });
        };
        var wy = aislesPair.getFirst();
        var aisles = aislesPair.getSecond();
        var aux = ReflectorFusionReactorMachine.isAuxReactor(tier);
        var maxEnergyTier = ReflectorFusionReactorMachine.getMaxEnergyHatchTier(tier);

        builder
            .langValue(ReflectorFusionReactorMachine.getControllerName(tier))
            .rotationState(RotationState.ALL)
            .recipeType(StarTRecipeTypes.FUSION_RECIPES)
            .appearanceBlock(() -> ReflectorFusionReactorMachine.getCasingState(tier))
            .pattern((definition) -> {
                var casing = Predicates.blocks(ReflectorFusionReactorMachine.getCasingState(tier));
                var glass = Predicates.blocks(ReflectorFusionReactorMachine.getFusionGlass(tier)).or(casing);
                var energyHatch = Predicates.blocks(PartAbility.INPUT_ENERGY.getBlocks(tier).toArray(Block[]::new));

                var pattern = FactoryBlockPattern.start();
                for (var i = 0; i < aisles.length; i += wy) {
                    pattern.aisle(Arrays.stream(aisles).skip(i).limit(wy).toArray(String[]::new));
                }

                pattern
                    .where('S', Predicates.controller(Predicates.blocks(definition.get())))
                    .where('C', casing)
                    .where('G', glass)
                    .where('E', casing.or(energyHatch.setMinGlobalLimited(1).setPreviewCount(16)))
                    .where('K', Predicates.blocks(ReflectorFusionReactorMachine.getCoilState(tier)))
                    .where('O', casing.or(Predicates.abilities(PartAbility.EXPORT_FLUIDS)))
                    .where('I', casing.or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMinGlobalLimited(2)))
                    .where('#', StarTReflectorPredicates.fusionReflectors())
                    .where(' ', Predicates.any());

                if (aux) {
                    pattern
                        .where('A', Predicates.blocks(ReflectorFusionReactorMachine.getAuxiliaryCoilState(tier)))
                        .where('@', casing.or(Predicates.abilities(StarTPartAbility.ABSOLUTE_PARALLEL_HATCH)));
                }

                return pattern.build();
            })
            .shapeInfos(definition -> {
                var shapes = new ArrayList<MultiblockShapeInfo>();

                var casing = ReflectorFusionReactorMachine.getCasingState(tier);
                var glass = ReflectorFusionReactorMachine.getFusionGlass(tier);

                var oN = 'a';
                var oS = 'b';
                var oE = 'c';
                var oW = 'd';
                var eN = 'e';
                var eS = 'f';
                var eE = 'g';
                var eW = 'h';
                var iU = 'i';
                var iD = 'j';

                var pattern = MultiblockShapeInfo.builder();
                var auxOffset = aux ? 3 : 1; // closest position of beam to the edge

                var wz = aisles.length / wy;
                var wx = aisles[0].length();

                Function4<Long, Long, Long, Character, Integer> patternCharIs = (x, y, z, ch) ->
                    aisles[(int) (z * wy + y)].charAt((int) x.longValue()) == ch ? 1 : 0;

                // z is flipped in preview
                for (long z1 = wz - 1; z1 >= 0; z1--) {
                    var z = z1;
                    var aisleStream = Arrays.stream(aisles).skip(z * wy).limit(wy);
                    var aisle = Streams.mapWithIndex(aisleStream, (column, y) -> Streams.mapWithIndex(column.chars().mapToObj(c -> (char) c), (ch, x) -> switch (ch) {
                        case 'E' -> {
                            var northEmpty = patternCharIs.apply(x, y, z + 1, ' ');
                            var southEmpty = patternCharIs.apply(x, y, z - 1, ' ');
                            var westEmpty = patternCharIs.apply(x + 1, y, z, ' ');
                            var eastEmpty = patternCharIs.apply(x - 1, y, z, ' ');

                            if ((northEmpty + southEmpty + westEmpty + eastEmpty) > 1) {
                                if (northEmpty == 1 && patternCharIs.apply(x, y, z + 1, 'K') == 1) yield eN;
                                if (southEmpty == 1 && patternCharIs.apply(x, y, z - 1, 'K') == 1) yield eS;
                                if (westEmpty == 1 && patternCharIs.apply(x - 1, y, z, 'K') == 1) yield eE;
                                if (eastEmpty == 1 && patternCharIs.apply(x + 1, y, z, 'K') == 1) yield eW;
                            }
                            if (northEmpty == 1) yield eN;
                            if (southEmpty == 1) yield eS;
                            if (westEmpty == 1) yield eE;
                            if (eastEmpty == 1) yield eW;
                            yield eN;
                        }
                        case 'O' -> {
                            if (z == auxOffset + 1 || z == (wz - 1) - auxOffset + 1) yield oN;
                            if (z == auxOffset - 1 || z == (wz - 1) - auxOffset - 1) yield oS;
                            if (x == auxOffset - 1 || x == (wx - 1) - auxOffset - 1) yield oW;
                            if (x == auxOffset + 1 || x == (wx - 1) - auxOffset + 1) yield oE;
                            yield oN;
                        }
                        case 'I' -> y < wy / 2 ? iD : iU;
                        default -> ch;
                    }).collect(Collector.of(StringBuilder::new, StringBuilder::append, StringBuilder::append, StringBuilder::toString))).toArray(String[]::new);
                    pattern.aisle(aisle);
                }

                pattern
                    .where('S', definition, Direction.NORTH)
                    .where('C', casing)
                    .where('G', glass)
                    .where('K', ReflectorFusionReactorMachine.getCoilState(tier))
                    .where(oN, GTMachines.FLUID_EXPORT_HATCH[tier], Direction.NORTH)
                    .where(oS, GTMachines.FLUID_EXPORT_HATCH[tier], Direction.SOUTH)
                    .where(oE, GTMachines.FLUID_EXPORT_HATCH[tier], Direction.EAST)
                    .where(oW, GTMachines.FLUID_EXPORT_HATCH[tier], Direction.WEST)
                    .where(eN, GTMachines.ENERGY_INPUT_HATCH[tier], Direction.NORTH)
                    .where(eS, GTMachines.ENERGY_INPUT_HATCH[tier], Direction.SOUTH)
                    .where(eE, GTMachines.ENERGY_INPUT_HATCH[tier], Direction.EAST)
                    .where(eW, GTMachines.ENERGY_INPUT_HATCH[tier], Direction.WEST)
                    .where(iU, GTMachines.FLUID_IMPORT_HATCH[tier], Direction.UP)
                    .where(iD, GTMachines.FLUID_IMPORT_HATCH[tier], Direction.DOWN)
                    .where(' ', Blocks.AIR.defaultBlockState());

                if (aux) {
                    pattern
                        .where('A', ReflectorFusionReactorMachine.getAuxiliaryCoilState(tier))
                        .where('@', ReflectorFusionReactorMachine.getParallelHatch(tier), Direction.SOUTH);
                }


                StarTAPI.FUSION_REFLECTORS.entrySet().stream()
                    .sorted(Comparator.comparingInt(entry -> entry.getKey().getTier()))
                    .forEach(reflector -> {
                        shapes.add(pattern.shallowCopy()
                            .where('#', reflector.getValue().get())
                            .build()
                        );
                        shapes.add(pattern.shallowCopy()
                            .where('#', reflector.getValue().get())
                            .where('G', casing)
                            .build()
                        );
                    });

                return shapes;
            })
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .workableCasingModel(ReflectorFusionReactorMachine.getCasingType(tier).getTexture(),
                GTCEu.id("block/multiblock/fusion_reactor"));


        if (aux) {
            builder
                .recipeModifiers(GTRecipeModifiers.DEFAULT_ENVIRONMENT_REQUIREMENT, ReflectorFusionReactorMachine::recipeModifier, StarTRecipeModifiers.ABSOLUTE_PARALLEL, GTRecipeModifiers.BATCH_MODE)
                .tooltips(
                    Component.translatable("start_core.machine.auxiliary_boosted_fusion_reactor.line"),
                    Component.translatable("start_core.machine.auxiliary_boosted_fusion_reactor.description"),
                    Component.translatable("block.start_core.breaker_line"),
                    Component.translatable("start_core.machine.auxiliary_boosted_fusion_reactor.fusion_info"),
                    Component.translatable("gtceu.machine.fusion_reactor.capacity",
                        ReflectorFusionReactorMachine.calculateEnergyStorageFactor(tier, 16) / 1000000L),
                    Component.translatable("start_core.machine.fusion_reactor.overclocking"),
                    Component.literal(""),
                    Component.translatable("start_core.machine.auxiliary_boosted_fusion_reactor.specific",
                        GTValues.VN[tier], ReflectorFusionReactorMachine.calculateEnergyStorageFactor(tier, 1) / 1000000L
                    ),
                    Component.translatable("block.start_core.breaker_line"),
                    Component.translatable("start_core.machine.auxiliary_boosted_fusion_reactor.parallel_info"),
                    Component.translatable("start_core.machine.auxiliary_boosted_fusion_reactor.parallel_info_1"),
                    Component.translatable("block.start_core.breaker_line"));

        } else {
            builder
                .recipeModifiers(GTRecipeModifiers.DEFAULT_ENVIRONMENT_REQUIREMENT, ReflectorFusionReactorMachine::recipeModifier, GTRecipeModifiers.BATCH_MODE)
                .tooltips(
                    Component.translatable("gtceu.machine.fusion_reactor.capacity",
                        ReflectorFusionReactorMachine.calculateEnergyStorageFactor(tier, 16) / 1000000L),
                    Component.translatable("gtceu.machine.fusion_reactor.overclocking"),
                    Component.translatable("gtceu.multiblock.%s_fusion_reactor.description"
                        .formatted(GTValues.VN[tier].toLowerCase(Locale.ROOT))));
        }

        return builder.register();
    }

    public static void init() {
        FusionReactorMachine.registerFusionTier(GTValues.UHV, " (AUXI)");
        FusionReactorMachine.registerFusionTier(GTValues.UEV, " (MKIV)");
        FusionReactorMachine.registerFusionTier(GTValues.UIV, " (AUXII)");
    }
}
