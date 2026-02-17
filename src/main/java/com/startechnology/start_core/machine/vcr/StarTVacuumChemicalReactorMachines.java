package com.startechnology.start_core.machine.vcr;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.startechnology.start_core.machine.StarTMachineUtils;
import com.startechnology.start_core.machine.StarTPartAbility;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import dev.latvian.mods.kubejs.KubeJS;

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;


public class StarTVacuumChemicalReactorMachines {

    public static final MultiblockMachineDefinition VACUUM_CHEMICAL_REACTOR = START_REGISTRATE
          .multiblock("vacuumchemicalreactor", VacuumChemicalReactorMachine::new)
          .appearanceBlock(() -> StarTMachineUtils.getKjsBlock("enriched_naquadah_machine_casing"))
            .recipeTypes(StarTRecipeTypes.VACUUM_CHEMICAL_REACTOR_RECIPES)
          .recipeModifiers(GTRecipeModifiers.OC_NON_PERFECT, VacuumChemicalReactorMachine::recipeModifier)
          .rotationState(RotationState.NON_Y_AXIS)
          .pattern(definition -> FactoryBlockPattern.start()
                  .aisle("aacbbbcaaaaa", "aagaaagaaaaa", "aagaaagaaaaa", "aagaaagaaaaa", "aacbbbcaaaaa")
                  .aisle("aacbbbcaaaaa", "aaadfdaaaaaa", "aaadfdaaaaaa", "aaadfdaaaaaa", "aacbibcaaaaa")
                  .aisle("aacbbbcaaaaa", "aaafhfaaaaaa", "aaafhfaaaaaa", "aaafhfaaaaaa", "aaciiicaaaaa")
                  .aisle("aacbbbcaaaaa", "aaadddaaaaaa", "aaadhdaaaaaa", "aaadddaaaaaa", "aacbibcaaaaa")
                  .aisle("aacbbbcccccb", "aaabbbbbbbbb", "aaabhbbibibb", "aaabbbbbbbbb", "aacbbbcccccb")
                  .aisle("agbbbbbbbbbc", "bgbbaaaaaaaj", "bbbbhhhhhhaj", "bgbbaaaaaaaj", "agbbjjjjjjjc")
                  .aisle("bgbbbbbbbbbb", "abababbbbbbb", "ababhbbbbhbb", "abababbbbbbb", "bgbbjbbbbbbb")
                  .aisle("bbbbbbaaacaa", "abababaaddda", "akhhhbaadhda", "abababaaddda", "bbbbjbaaacaa")
                  .aisle("bgbbbbabacab", "ababajabdddb", "ababhjabdhdb", "ababajabdddb", "bgbbjbabacab")
                  .aisle("aggbbbabcccb", "bgbbababdddb", "bbbbhbaihhhi", "bgbbababdddb", "aggbjbabcccb")
                  .aisle("aaabcbabaaab", "aaabbbabdddb", "aaabebabfffb", "aaabbbabdddb", "aaabcbabaaab")
                  .where("a", Predicates.any())
                  .where("b", Predicates.blocks(StarTMachineUtils.getKjsBlock("enriched_naquadah_machine_casing"))
                    .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(8).setPreviewCount(0))
                    .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMaxGlobalLimited(2).setPreviewCount(0))
                    .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(2).setPreviewCount(0))
                    .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(2).setPreviewCount(0))
                    .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2)).setMinGlobalLimited(1)
                    .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1))
                    .or(Predicates.abilities(StarTPartAbility.REDSTONE_INTERFACE).setMaxGlobalLimited(4).setPreviewCount(0)))
                  .where("c", Predicates.blocks(StarTMachineUtils.getKjsBlock("enriched_naquadah_firebox_casing")))
                  .where("d", Predicates.blocks(StarTMachineUtils.getKjsBlock("polycarbonate_casing")))
                  .where("e", Predicates.controller(Predicates.blocks(definition.get())))
                  .where("f", Predicates.blocks(GTBlocks.FUSION_GLASS.get()))
                  .where("g", Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTCEuAPI.materialManager.getMaterial("gtceu:naquadah_alloy"))))
                  .where("h", Predicates.blocks(StarTMachineUtils.getKjsBlock("enriched_naquadah_pipe_casing")))
                  .where("i", Predicates.blocks(StarTMachineUtils.getKjsBlock("enriched_naquadah_engine_intake_casing")))
                  .where("j", Predicates.blocks(GCYMBlocks.MOLYBDENUM_DISILICIDE_COIL_BLOCK.get()))
                  .where("k", StarTVacuumPumpPredicates.vacuumPumps())
                  .build())
            .workableCasingModel(KubeJS.id("block/casings/naquadah/casing"),
                    GTCEu.id("block/machines/chemical_reactor"))
            .register();


            public static void init() {}

}