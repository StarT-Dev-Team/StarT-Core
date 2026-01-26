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

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.network.chat.Component;
import net.minecraftforge.registries.ForgeRegistries;

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
        .pattern(definition -> 
            FactoryBlockPattern.start()
            .aisle("  FFFFF  ", "    T    ", "    T    ", "  TTTTT  ", "         ", "         ", "         ", "         ","  KKKKK  ")
            .aisle(" FFKKKFF ", " T KKK T ", " T KKK T ", " TTKKKTT ", "   KKK   ", "   KMK   ", "   KMK   ", "   KKK   "," KK   KK ")
            .aisle("FFKKKKKFF", "  K   K  ", "  K P K  ", "TTK   KTT", "  K P K  ", "  MK KM  ", "  MKKKM  ", "  K   K  ","KK     KK")
            .aisle("FKKKKKKKF", " K     K ", " K  P  K ", "TK     KT", " K  P  K ", " KK   KK ", " KKGGGKK ", " K     K ","K       K")
            .aisle("FKKKKKKKF", "TKPPPPPKT", "TK  P  KT", "TKPPPPPKT", " K  P  K ", " MPPPPPM ", " MKGGGKM ", " K     K ","K       K")
            .aisle("FKKKKKKKF", " K     K ", " K  P  K ", "TK     KT", " K  P  K ", " KK   KK ", " KKGGGKK ", " K     K ","K       K")
            .aisle("FFKKKKKFF", "  K   K  ", "  K P K  ", "TTK   KTT", "  K P K  ", "  MK KM  ", "  MKKKM  ", "  K   K  ","KK     KK")
            .aisle(" FFKKKFF ", " T KKK T ", " T KKK T ", " TTKKKTT ", "   KKK   ", "   KCK   ", "   KKK   ", "   KKK   "," KK   KK ")
            .aisle("  FFFFF  ", "    T    ", "    T    ", "  TTTTT  ", "         ", "         ", "         ", "         ","  KKKKK  ")
            .where("C", Predicates.controller(Predicates.blocks(definition.get())))
            .where("K", Predicates.blocks(StarTMachineUtils.getKjsBlock(("peek_casing")))
                .setMinGlobalLimited(172)
                .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1))
                .or(Predicates.abilities(PartAbility.PARALLEL_HATCH).setMaxGlobalLimited(1)))
            .where("F", Predicates.blocks(GTBlocks.FIREBOX_TUNGSTENSTEEL.get()))
            .where("P", Predicates.blocks(GTBlocks.CASING_POLYTETRAFLUOROETHYLENE_PIPE.get()))     
            .where("M", Predicates.blocks(GCYMBlocks.MOLYBDENUM_DISILICIDE_COIL_BLOCK.get()))
            .where("G", Predicates.blocks(GTBlocks.CASING_LAMINATED_GLASS.get()))
            .where("T", Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTCEuAPI.materialManager.getMaterial("gtceu:trinaquadalloy"))))
            .where(" ", Predicates.any())
            .build()
        )
        .workableCasingModel(KubeJS.id("block/casings/machine_casing_peek"),
            GTCEu.id("block/machines/brewery"))
        .register();

    public static final MultiblockMachineDefinition BACTERIAL_RUNIC_MUTATOR = START_REGISTRATE
        .multiblock("bacterial_runic_mutator", BacterialRunicMutator::new)
        .appearanceBlock(() -> StarTMachineUtils.getKjsBlock(("peek_casing")))
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
            .aisle("   KKKKK   ","   KKKKK   ","           ","           ","           ","           ","           ","           ","           ","           ","           ","           ","           ")
            .aisle(" KKKKKKKKK "," KKKKKKKKK ","           ","           ","           ","           ","           ","  AA   AA  ","  AAAAAAA  ","  AAAAAAA  ","  AAAAAAA  ","   AAAAA   ","           ")
            .aisle(" KTMMMMMTK "," KTHHHHHTK ","  T     T  ","  T     T  ","  T     T  ","  T     T  ","  T     T  "," ATT   TTA "," AT TTT TA "," ATCSSSCTA "," AT  T  TA ","  AA T AA  ","   AAAAA   ")
            .aisle("KKMMFFFMMKK","KKHHGGGHHKK","           ","           ","           ","           ","           "," AT     TA "," A   T   A "," ACCCSCCCA "," A       A "," AA     AA ","  AAAAAAA  ")
            .aisle("KKMFFFFFMKK","KKHGGGGGHKK","           ","           ","           ","           ","           ","     T     "," AT  S  TA "," ASCCCCCSA "," A       A "," A       A ","  AAAAAAA  ")
            .aisle("KKMFFKFFMKK","KKHGGKGGHKK","     K     ","           ","     G     ","     C     ","     C     ","    TCT    "," ATTSFSTTA "," ASSCTCSSA "," AT  T  TA "," AT  T  TA ","  AAAAAAA  ")
            .aisle("KKMFFFFFMKK","KKHGGGGGHKK","           ","           ","           ","           ","           ","     T     "," AT  S  TA "," ASCCCCCSA "," A       A "," A       A ","  AAAAAAA  ")
            .aisle("KKMMFFFMMKK","KKHHGGGHHKK","           ","           ","           ","           ","           "," AT     TA "," A   T   A "," ACCCSCCCA "," A       A "," AA     AA ","  AAAAAAA  ")
            .aisle(" KTMMMMMTK "," KTHHHHHTK ","  T     T  ","  T     T  ","  T     T  ","  T     T  ","  T     T  "," ATT   TTA "," AT TTT TA "," ATCSSSCTA "," AT  T  TA ","  AA T AA  ","   AAAAA   ")
            .aisle(" KKKKKKKKK "," KKKKKKKKK ","           ","           ","           ","           ","           ","  AA   AA  ","  AAAAAAA  ","  AAAAAAA  ","  AAAAAAA  ","   AAAAA   ","           ")
            .aisle("   KKKKK   ","   KK#KK   ","           ","           ","           ","           ","           ","           ","           ","           ","           ","           ","           ")

            .where("#", Predicates.controller(Predicates.blocks(definition.get())))
            .where("K", Predicates.blocks(StarTMachineUtils.getKjsBlock(("peek_casing")))
                .setMinGlobalLimited(96)
                .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1)))
            .where("A", Predicates.blocks(GCYMBlocks.CASING_ATOMIC.get()))
            .where("T", Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTCEuAPI.materialManager.getMaterial("gtceu:trinaquadalloy"))))
            .where("M", Predicates.blocks(GTBlocks.FUSION_CASING_MK3.get()))
            .where("C", Predicates.blocks(GTBlocks.HIGH_POWER_CASING.get()))
            .where("S", Predicates.blocks(GTBlocks.FILTER_CASING_STERILE.get()))
            .where("H", Predicates.blocks(GTBlocks.COMPUTER_HEAT_VENT.get()))
            .where("F", Predicates.blocks(GTBlocks.FUSION_COIL.get()))
            .where("G", Predicates.blocks(GTBlocks.FUSION_GLASS.get()))
            .where(" ", Predicates.any())
        .build()
    )
    .workableCasingModel(KubeJS.id("block/casings/machine_casing_peek"),
        GTCEu.id("block/multiblock/implosion_compressor"))
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
            .where("X", Predicates.blocks(GTBlocks.CASING_EXTREME_ENGINE_INTAKE.get()))
            .where("T", Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTCEuAPI.materialManager.getMaterial("gtceu:trinaquadalloy"))))
            .where(" ", Predicates.any())
            .build()
        )
        .workableCasingModel(KubeJS.id("block/casings/machine_casing_peek"),
            GTCEu.id("block/machines/distillery"))
        .register();

    public static void init() {
    }
}
