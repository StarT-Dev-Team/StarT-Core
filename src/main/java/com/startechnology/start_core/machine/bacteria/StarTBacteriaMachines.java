package com.startechnology.start_core.machine.bacteria;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.startechnology.start_core.machine.StarTMachineUtils;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.network.chat.Component;

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

public class StarTBacteriaMachines {
    public static final MultiblockMachineDefinition BACTERIAL_BREEDING_VAT = START_REGISTRATE
        .multiblock("bacterial_breeding_vat", BacterialVatMachine::new)
        .appearanceBlock(() -> StarTMachineUtils.getKjsBlock(("peek_casing")))
        .langValue("Bacterial Breeding Vat")
        .tooltips(
            Component.translatable("block.start_core.bacteria_multiblock_line"),
            Component.translatable("block.start_core.vat_description"),
            Component.translatable("block.start_core.breaker_line"),
            Component.translatable("block.start_core.vat1"),
            Component.translatable("block.start_core.vat2"),
            Component.translatable("block.start_core.gap"),
            Component.translatable("block.start_core.vat3"),
            Component.translatable("block.start_core.gap"),
            Component.translatable("block.start_core.vat4"),
            Component.translatable("block.start_core.breaker_line"),
            Component.translatable("block.start_core.parallels")
        )
        .rotationState(RotationState.NON_Y_AXIS)
        .recipeModifiers(GTRecipeModifiers.PARALLEL_HATCH, GTRecipeModifiers.OC_NON_PERFECT, GTRecipeModifiers.BATCH_MODE)
        .recipeTypes(StarTRecipeTypes.BACTERIAL_BREEDING_VAT_RECIPES)
        .pattern(definition -> FactoryBlockPattern.start()
            .aisle("    BBBBB    ", "             ", "             ", "             ", "     C C     ", "      C      ", "     CCC     ")
            .aisle("  BBBDDDBBB  ", "     EEE     ", "   C EEE C   ", "    C   C    ", "             ", "     EEE     ", "   CC   CC   ")
            .aisle(" BBDDDDDDDBB ", "  CEEDDDEEC  ", "   EEDDDEE   ", "     EEE     ", "     EEE     ", "   EEEEEEE   ", "  C       C  ")
            .aisle(" BDDDDDDDDDB ", "  EDDF FDDE  ", " CEDD G DDEC ", "   EE   EE   ", "   EEEEEEE   ", "  EEEE EEEE  ", " C         C ")
            .aisle("BBDDDDDDDDDBB", "  ED F F DE  ", "  EDG G GDE  ", " C E     E C ", "   EEHHHEE   ", "  EE     EE  ", " C         C ")
            .aisle("BDDDDDDDDDDDB", " EDFF D FFDE ", " ED  GGG  DE ", "  E       E  ", "C EEHHHHHEE C", " EEE     EEE ", "C           C")
            .aisle("BDDDDDDDDDDDB", " ED  DGD  DE ", " EDGGGDGGGDE ", "  E       E  ", "  EEHHHHHEE  ", "CEE       EEC", "C           C")
            .aisle("BDDDDDDDDDDDB", " EDFF D FFDE ", " ED  GGG  DE ", "  E       E  ", "C EEHHHHHEE C", " EEE     EEE ", "C           C")
            .aisle("BBDDDDDDDDDBB", "  ED F F DE  ", "  EDG G GDE  ", " C E     E C ", "   EEHHHEE   ", "  EE     EE  ", " C         C ")
            .aisle(" BDDDDDDDDDB ", "  EDDF FDDE  ", " CEDD G DDEC ", "   EE   EE   ", "   EEEEEEE   ", "  EEEE EEEE  ", " C         C ")
            .aisle(" BBDDDDDDDBB ", "  CEEDDDEEC  ", "   EEDDDEE   ", "     EEE     ", "     EEE     ", "   EEEEEEE   ", "  C       C  ")
            .aisle("  BBBDDDBBB  ", "     EEE     ", "   C E@E C   ", "    C   C    ", "             ", "     EEE     ", "   CC   CC   ")
            .aisle("    BBBBB    ", "             ", "             ", "             ", "     C C     ", "      C      ", "     CCC     ")
            .where(" ", Predicates.any())
            .where("B", Predicates.blocks(StarTMachineUtils.getKjsBlock("enriched_naquadah_firebox_casing")))
            .where("C", Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTCEuAPI.materialManager.getMaterial("gtceu:trinaquadalloy"))))
            .where("D", Predicates.blocks(StarTMachineUtils.getKjsBlock("enriched_naquadah_machine_casing")))
            .where("E", Predicates.blocks(StarTMachineUtils.getKjsBlock("peek_casing"))
                .setMinGlobalLimited(164)
                .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1))
                .or(Predicates.abilities(PartAbility.PARALLEL_HATCH).setMaxGlobalLimited(1)))
            .where("F", Predicates.blocks(GCYMBlocks.MOLYBDENUM_DISILICIDE_COIL_BLOCK.get()))
            .where("G", Predicates.blocks(GTBlocks.CASING_PTFE_INERT.get()))
            .where("H", Predicates.blocks(GTBlocks.FUSION_GLASS.get()))
            .where("@", Predicates.controller(Predicates.blocks(definition.get())))
            .build()
        )
        .workableCasingModel(KubeJS.id("block/casings/basic/machine_casing_peek"), GTCEu.id("block/machines/brewery"))
        .register();

    public static final MultiblockMachineDefinition BACTERIAL_RUNIC_MUTATOR = START_REGISTRATE
        .multiblock("bacterial_runic_mutator", BacterialRunicMutator::new)
        .appearanceBlock(GTBlocks.ADVANCED_COMPUTER_CASING)
        .langValue("Bacterial Runic Mutator")
        .tooltips(
            Component.translatable("block.start_core.bacteria_multiblock_line"),
            Component.translatable("block.start_core.runic_mutator_description"),
            Component.translatable("block.start_core.breaker_line"),
            Component.translatable("block.start_core.rm0"),
            Component.translatable("block.start_core.rm1"),
            Component.translatable("block.start_core.breaker_line"),
            Component.translatable("block.start_core.rm3"),
            Component.translatable("block.start_core.rm4"),
            Component.translatable("block.start_core.breaker_line")
        )
        .recipeModifiers(GTRecipeModifiers.OC_NON_PERFECT)
        .rotationState(RotationState.NON_Y_AXIS)
        .recipeTypes(StarTRecipeTypes.BACTERIAL_RUNIC_MUTATOR_RECIPES)
        .pattern(definition -> FactoryBlockPattern.start()
            .aisle("               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "     BBBBB     ", "               ", "               ", "               ")
            .aisle("    CCCCCCC    ", "      CCC      ", "               ", "               ", "               ", "               ", "               ", "               ", "     DDDDD     ", "     D   D     ", "  BBBBEEEBBBB  ", "      BBB      ", "               ", "               ")
            .aisle("   CCCCCCCCC   ", "    CCCECCC    ", "    D     D    ", "               ", "               ", "               ", "               ", "   D       D   ", "   DD     DD   ", "  D         D  ", " BB    E    BB ", "   BBBBBBBBB   ", "       B       ", "               ")
            .aisle("  CCCCCCCCCCC  ", "   CCCFFFCCC   ", "               ", "   D       D   ", "   D       D   ", "   D       D   ", "   D       D   ", "  D         D  ", "  D         D  ", "               ", " B     E     B ", "  BGGBBEBBGGB  ", "      BBB      ", "               ")
            .aisle(" CCCCCCCCCCCCC ", "  CCCCCECCCCC  ", "  D         D  ", "               ", "               ", "               ", "               ", "               ", "  D         D  ", "               ", " B           B ", "  BGBB E BBGB  ", "    BBBBBBB    ", "     B   B     ")
            .aisle(" CCCCCCCCCCCCC ", "  CCCCCECCCCC  ", "               ", "               ", "               ", "               ", "               ", "               ", " D           D ", " D           D ", "BB           BB", "  BBB  E  BBB  ", "    BGBEBGB    ", "    BGBBBGB    ")
            .aisle(" CCCCCCCCCCCCC ", " CCFCCEEECCFCC ", "      HHH      ", "               ", "               ", "       F       ", "      FFF      ", "       F       ", " D           D ", "               ", "BE    HHH    EB", " BBB  EEE  BBB ", "   BBBBEBBBB   ", "     BBFBB     ")
            .aisle(" CCCCCCCCCCCCC ", " CEFEEEEEEEFEC ", "      HHH      ", "               ", "               ", "      FFF      ", "      F F      ", "      FFF      ", " D           D ", "               ", "BEEE  HHH  EEEB", " BBEEEE EEEEBB ", "  BBBEEEEEBBB  ", "     BFFFB     ")
            .aisle(" CCCCCCCCCCCCC ", " CCFCCEEECCFCC ", "      HHH      ", "               ", "               ", "       F       ", "      F@F      ", "       F       ", " D           D ", "               ", "BE    HHH    EB", " BBB  EEE  BBB ", "   BBBBEBBBB   ", "     BBFBB     ")
            .aisle(" CCCCCCCCCCCCC ", "  CCCCCECCCCC  ", "               ", "               ", "               ", "               ", "               ", "               ", " D           D ", " D           D ", "BB           BB", "  BBB  E  BBB  ", "    BGBEBGB    ", "    BGBBBGB    ")
            .aisle(" CCCCCCCCCCCCC ", "  CCCCCECCCCC  ", "  D         D  ", "               ", "               ", "               ", "               ", "               ", "  D         D  ", "               ", " B           B ", "  BGBB E BBGB  ", "    BBBBBBB    ", "     B   B     ")
            .aisle("  CCCCCCCCCCC  ", "   CCCFFFCCC   ", "               ", "   D       D   ", "   D       D   ", "   D       D   ", "   D       D   ", "  D         D  ", "  D         D  ", "               ", " B     E     B ", "  BGGBBEBBGGB  ", "      BBB      ", "               ")
            .aisle("   CCCCCCCCC   ", "    CCCECCC    ", "    D     D    ", "               ", "               ", "               ", "               ", "   D       D   ", "   DD     DD   ", "  D         D  ", " BB    E    BB ", "   BBBBBBBBB   ", "       B       ", "               ")
            .aisle("    CCCCCCC    ", "      CCC      ", "               ", "               ", "               ", "               ", "               ", "               ", "     DDDDD     ", "     D   D     ", "  BBBBEEEBBBB  ", "      BBB      ", "               ", "               ")
            .aisle("               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "     BBBBB     ", "               ", "               ", "               ")
            .where(" ", Predicates.any())
            .where("B", Predicates.blocks(GCYMBlocks.CASING_ATOMIC.get()))
            .where("C", Predicates.blocks(StarTMachineUtils.getKjsBlock("peek_casing")))
            .where("D", Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTCEuAPI.materialManager.getMaterial("gtceu:trinaquadalloy"))))
            .where("E", Predicates.blocks(GTBlocks.COMPUTER_CASING.get()))
            .where("F", Predicates.blocks(GTBlocks.ADVANCED_COMPUTER_CASING.get())
                    .setMinGlobalLimited(25)
                    .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                    .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1)))
            .where("G", Predicates.blocks(GTBlocks.FUSION_GLASS.get()))
            .where("H", Predicates.blocks(GTBlocks.COMPUTER_HEAT_VENT.get()))
            .where("@", Predicates.controller(Predicates.blocks(definition.get())))
            .build()
        )
        .sidedWorkableCasingModel(GTCEu.id("block/casings/hpca/advanced_computer_casing"), GTCEu.id("block/multiblock/implosion_compressor"))
        .register();

    public static final MultiblockMachineDefinition BACTERIAL_HYDROCARBON_HARVESTER = START_REGISTRATE
        .multiblock("bacterial_hydrocarbon_harvester", WorkableElectricMultiblockMachine::new)
        .appearanceBlock(() -> StarTMachineUtils.getKjsBlock(("peek_casing")))
        .langValue("Bacterial Hydrocarbon Harvester")
        .tooltips(
            Component.translatable("block.start_core.bacteria_multiblock_line"),
            Component.translatable("block.start_core.harvester_description"),
            Component.translatable("block.start_core.breaker_line"),
            Component.translatable("block.start_core.hv0"),
            Component.translatable("block.start_core.hv1"),
            Component.translatable("block.start_core.gap"),
            Component.translatable("block.start_core.hv3"),
            Component.translatable("block.start_core.gap"),
            Component.translatable("block.start_core.hv4"),
            Component.translatable("block.start_core.gap"),
            Component.translatable("block.start_core.hv5"),
            Component.translatable("block.start_core.gap"),
            Component.translatable("block.start_core.hv6"),
            Component.translatable("block.start_core.gap"),
            Component.translatable("block.start_core.hv7"),
            Component.translatable("block.start_core.breaker_line"),
            Component.translatable("block.start_core.parallels")
        )
        .rotationState(RotationState.NON_Y_AXIS)
        .recipeModifiers(GTRecipeModifiers.PARALLEL_HATCH, GTRecipeModifiers.OC_NON_PERFECT, GTRecipeModifiers.BATCH_MODE)
        .recipeTypes(StarTRecipeTypes.BACTERIAL_HYDROCARBON_HARVESTER_RECIPES)
        .pattern(definition -> FactoryBlockPattern.start()
            .aisle("THHHT", "TKKKT", "T   T", "T   T", "T   T", "TKKKT", "TXXXT")
            .aisle("HKKKH", "K   K", " KKK ", " KMK ", " KKK ", "K   K", "XSSSX")
            .aisle("HKKKH", "K P K", " KPK ", " MPM ", " KPK ", "K P K", "XSSSX")
            .aisle("HKKKH", "K   K", " KKK ", " KMK ", " KKK ", "K P K", "XSSSX")
            .aisle("THHHT", "TKCKT", "T   T", "T   T", "T   T", "TKKKT", "TXXXT")
            .where("C", Predicates.controller(Predicates.blocks(definition.get())))
            .where("K", Predicates.blocks(StarTMachineUtils.getKjsBlock(("peek_casing")))
                .setMinGlobalLimited(38)
                .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1))
                .or(Predicates.abilities(PartAbility.PARALLEL_HATCH).setMaxGlobalLimited(1)))
            .where("S", Predicates.blocks(GTBlocks.FILTER_CASING_STERILE.get()))
            .where("P", Predicates.blocks(GTBlocks.CASING_POLYTETRAFLUOROETHYLENE_PIPE.get()))
            .where("M", Predicates.blocks(GCYMBlocks.MOLYBDENUM_DISILICIDE_COIL_BLOCK.get()))
            .where("H", Predicates.blocks(GCYMBlocks.HEAT_VENT.get()))
            .where("X", Predicates.blocks(StarTMachineUtils.getKjsBlock("enriched_naquadah_engine_intake_casing")))
            .where("T", Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTCEuAPI.materialManager.getMaterial("gtceu:trinaquadalloy"))))
            .where(" ", Predicates.any())
            .build()
        )
        .workableCasingModel(KubeJS.id("block/casings/basic/machine_casing_peek"), GTCEu.id("block/machines/distillery"))
        .register();

    public static void init() {
    }
}
