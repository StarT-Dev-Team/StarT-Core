package com.startechnology.start_core.machine.vcr;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.block.IMachineBlock;
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
import com.startechnology.start_core.machine.vcr.StarTVacuumChemicalReactorMachine;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.world.level.block.Blocks;

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;


public class StarTVacuumChemicalReactorMachines {

    public static final MultiblockMachineDefinition VACUUM_CHEMICAL_REACTOR = START_REGISTRATE
          .multiblock("vacuumchemicalreactor", StarTVacuumChemicalReactorMachine::new)
          .appearanceBlock(() -> StarTMachineUtils.getKjsBlock("enriched_naquadah_machine_casing"))
            .recipeTypes(StarTRecipeTypes.VACUUM_CHEMICAL_REACTOR_RECIPES)
          .recipeModifiers(GTRecipeModifiers.OC_NON_PERFECT)
          .rotationState(RotationState.NON_Y_AXIS)
          .pattern(definition -> FactoryBlockPattern.start()
                  .aisle("aaabcccccb", "aaabbbbbbb", "aaabiiiiib", "aaabbbbbbb", "aaabfffffb", "aaaaaaaaaa", "aaaaaaaaaa")
                  .aisle("affbbbbbbb", "bfbbaaaaai", "bbbbgggggi", "bfbbahaaai", "affbbbbbbb", "aaaaaaaaaa", "aaaaaaaaaa")
                  .aisle("bfbbbbbbbb", "abababbbbb", "ababgbbbgb", "abababbbbb", "bfbbbbfffb", "aaaaaaaaaa", "aaaaaaaaaa")
                  .aisle("bbbbbbaaaa", "abababaaba", "ajgggbabgb", "abababaaba", "bbbbhbaaaa", "aaaaaaaaaa", "aaaaaaaaaa")
                  .aisle("bfbbbbabbb", "ababaiabbb", "ababgiabgb", "ababaiabbb", "bfbbbbabbb", "aaaaaaabbb", "aaaaaaabbb")
                  .aisle("affbbbacgc", "bfbbababgb", "bbbbgbabgb", "bfbbabaeae", "affbhbaeae", "aaaaaaaeae", "aaaaaaabbb")
                  .aisle("aaabcbaccc", "aaabbbabbb", "aaabdbabbb", "aaabbbaeee", "aaabcbaeee", "aaaaaaaeee", "aaaaaaabbb")
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
                  .where("d", Predicates.controller(Predicates.blocks(definition.get())))
                  .where("e", Predicates.blocks(GTBlocks.FUSION_GLASS.get()))
                  .where("f", Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTCEuAPI.materialManager.getMaterial("gtceu:naquadah_alloy"))))
                  .where("g", Predicates.blocks(StarTMachineUtils.getKjsBlock("enriched_naquadah_pipe_casing")))
                  .where("h", Predicates.blocks(StarTMachineUtils.getKjsBlock("enriched_naquadah_engine_intake_casing")))
                  .where("i", Predicates.blocks(GCYMBlocks.MOLYBDENUM_DISILICIDE_COIL_BLOCK.get()))
                  .where("j", StarTVacuumPumpPredicates.vacuumPumps())
                  .build())
            .workableCasingModel(KubeJS.id("block/casings/naquadah/casing"),
                    GTCEu.id("block/machines/chemical_reactor"))
            .register();


            public static void init() {}

}