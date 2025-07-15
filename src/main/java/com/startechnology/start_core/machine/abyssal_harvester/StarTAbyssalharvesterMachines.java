package com.startechnology.start_core.machine.abyssal_harvester;

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.startechnology.start_core.machine.StarTMachineUtils;
import com.startechnology.start_core.machine.StarTPartAbility;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.startechnology.start_core.block.casings.StarTCasingBlocks;

import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.network.chat.Component;

public class StarTAbyssalharvesterMachines {
    
    public static MultiblockMachineDefinition ABYSSAL_HARVESTER = START_REGISTRATE
        .multiblock("abyssal_harvester", StarTAbyssalHarvesterMachine::new)
        .appearanceBlock(() -> StarTMachineUtils.getKjsBlock(("draneko_casing")))
        .langValue("Abyssal Harvester")
        .tooltips(
            Component.translatable("start_core.machine.abyssal_harvester.line"),
            Component.translatable("start_core.machine.abyssal_harvester.description"),
            Component.translatable("block.start_core.breaker_line"),
            Component.translatable("start_core.machine.abyssal_harvester.ah0"),
            Component.translatable("start_core.machine.abyssal_harvester.ah1"),
            Component.translatable("block.start_core.gap"),
            Component.translatable("start_core.machine.abyssal_harvester.ah2"),
            Component.translatable("block.start_core.gap"),
            Component.translatable("start_core.machine.abyssal_harvester.ah3"),
            Component.translatable("block.start_core.gap"),
            Component.translatable("machine.start_core.redstone_interfacing"),
            Component.translatable("start_core.machine.abyssal_harvester.ah4"),
            Component.translatable("block.start_core.breaker_line")
        )
        .recipeTypes(StarTRecipeTypes.ABYSSAL_HARVESTER_RECIPES)
        .recipeModifiers(GTRecipeModifiers.OC_NON_PERFECT, StarTAbyssalHarvesterMachine::recipeModifier)
        .pattern(definition -> FactoryBlockPattern.start()
            .aisle("             ", "             ", "             ", "       D     ", "      DD     ", "     DD      ", "     D       ", "       D     ", "      DD     ", "     DD      ", "     D       ", "       D     ", "      DD     ", "     DD      ", "             ", "             ", "             ")
            .aisle("             ", "             ", "     DDD     ", "   DD   D    ", "   D         ", "         D   ", "    D   DD   ", "   DD   D    ", "   D         ", "         D   ", "    D   DD   ", "   DD   D    ", "   D         ", "         D   ", "     DDD     ", "             ", "             ")
            .aisle("             ", "     OOO     ", "   DD   DD   ", "             ", "  D       D  ", "  D       D  ", "             ", "             ", "  D       D  ", "  D       D  ", "             ", "             ", "  D       D  ", "  D       D  ", "   DD   DD   ", "     OOO     ", "             ")
            .aisle("             ", "   OOOOOOO   ", "  D  GGB  D  ", "     GGB   D ", "      G    D ", " D           ", " D           ", "           D ", "           D ", " D           ", " D           ", "           D ", "      G    D ", " D   BGG     ", "  D  BGG  D  ", "   OOOOOOO   ", "             ")
            .aisle("      D      ", "   OOODOOO   ", "  D GGGGG D  ", " D  GGGGB  D ", "    GGGGB    ", "     GGG     ", " D    G    D ", " D         D ", "             ", "             ", " D    G    D ", " D   GGG   D ", "    BGGGG    ", "    BGGGG    ", "  D GGGGG D  ", "   OOODOOO   ", "      D      ")
            .aisle("     DID     ", "  OOODDDOOO  ", " D GGGGGGG D ", "D  GGGGGGG   ", "D   GGGGB    ", "    GGGGB   D", "     BGG    D", "D    BBG     ", "D     B      ", "     GBB    D", "     GGB    D", "D   BGGGG    ", "D   BGGGG    ", "   GGGGGGG  D", " D GGGGGGG D ", "  OOODDDOOO  ", "     DID     ")
            .aisle("    DIIID    ", "  OODDPDDOO  ", " D GGGPGGG D ", "   GGGPGGG   ", "D  GGGPGGG  D", "D   BGPGB   D", "    BGPGB    ", "     GPG     ", "D    GPG    D", "D    GPG    D", "    BGPGB    ", "    BGPGB    ", "D  GGGPGGG  D", "D  GGGPGGG  D", " D GGGPGGG D ", "  OOODPDOOO  ", "    DIIID    ")
            .aisle("     DID     ", "  OOODDDOOO  ", " D GGGGGGG D ", "   GGGGGGG  D", "    BGGGG   D", "D   BGGGG    ", "D    GGB     ", "     GBB    D", "      B     D", "D    BBG     ", "D    BGG     ", "    GGGGB   D", "    GGGGB   D", "D  GGGGGGG   ", " D GGGGGGG D ", "  OOODDDOOO  ", "     DID     ")
            .aisle("      D      ", "   OOODOOO   ", "  D GGGGG D  ", " g  BGGGG  D ", "    BGGGG    ", "     GGG     ", " D    G    D ", " D         D ", "             ", "             ", " D    G    D ", " D   GGG   D ", "    GGGGB    ", "    GGGGB    ", "  D GGGGG D  ", "   OOODOOO   ", "      D      ")
            .aisle("             ", "   OOOOOOO   ", "  D  BGG  D  ", " g   BGG     ", " g    G      ", "           D ", "           D ", " D           ", " D           ", "           D ", "           D ", " D           ", " D    G      ", "     GGB   g ", "  D  GGB  D  ", "   OOOOOOO   ", "             ")
            .aisle("             ", "     OOO     ", "   DD   DD   ", "             ", "  g       D  ", "  g       D  ", "             ", "             ", "  D       D  ", "  D       D  ", "             ", "             ", "  D       g  ", "  D       g  ", "   DD   DD   ", "     OOO     ", "             ")
            .aisle("             ", "             ", "     DDD     ", "    D   DD   ", "         D   ", "   g         ", "   gg   D    ", "    g   DD   ", "         D   ", "   D         ", "   DD   g    ", "    D   gg   ", "         g   ", "   D         ", "     DDD     ", "             ", "             ")
            .aisle("             ", "             ", "             ", "     D       ", "     DD      ", "      DD     ", "       D     ", "     D       ", "     D@      ", "      DD     ", "       D     ", "     D       ", "     DD      ", "      DD     ", "             ", "             ", "             ")
            .where('@', Predicates.controller(Predicates.blocks(definition.get())))
            .where('D', Predicates.blocks(StarTMachineUtils.getKjsBlock("draneko_casing")))
            .where('G', Predicates.blocks(StarTMachineUtils.getKjsBlock("abyssal_drill_1")))
            .where('B', Predicates.blocks(StarTMachineUtils.getKjsBlock("abyssal_drill_2")))
            .where('g', Predicates.blocks(StarTMachineUtils.getKjsBlock("draco_resilient_fusion_glass")))
            .where('I', Predicates.blocks(StarTCasingBlocks.ENGINE_INTAKE_ENRICHED_NAQUADAH.get()))
            .where('P', Predicates.blocks(StarTMachineUtils.getKjsBlock("enriched_naquadah_pipe_casing")))
            .where('O', Predicates.blocks(StarTMachineUtils.getKjsBlock("draneko_casing"))
                .setMinGlobalLimited(80)
                .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                .or(Predicates.abilities(StarTPartAbility.REDSTONE_INTERFACE).setMaxGlobalLimited(4).setPreviewCount(0))
                .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1)))
            .where(' ', Predicates.any())
            // .where('O', Predicates.abilities(PartAbility.EXPORT_FLUIDS))
            // .where('I', Predicates.abilities(PartAbility.IMPORT_FLUIDS)
            //     .or(Predicates.abilities(PartAbility.IMPORT_ITEMS)))
            .build()
        )
        .workableCasingRenderer(KubeJS.id("block/casings/ultimate_multis/draneko_casing"),
            GTCEu.id("block/machines/alloy_smelter"), false)
        .register();

    public static void init() {}
}
