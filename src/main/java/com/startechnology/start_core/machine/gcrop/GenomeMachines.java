package com.startechnology.start_core.machine.gcrop;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.startechnology.start_core.machine.StarTMachineUtils;
import com.startechnology.start_core.machine.random_custom_logic.DirtyWorkableElectricMultiblockMachine;
import com.startechnology.start_core.recipe.StarTRecipeTypes;

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

public class GenomeMachines {

    public static final MultiblockMachineDefinition GENOME_OPERATOR_MACHINE = START_REGISTRATE
            .multiblock("genome_operator", DirtyWorkableElectricMultiblockMachine::new)
            .appearanceBlock(GTBlocks.CASING_HSSE_STURDY)
            .langValue("Genome Classification Runic Operation Processor [GCROP]")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeModifiers(GTRecipeModifiers.OC_NON_PERFECT)
            .recipeTypes(StarTRecipeTypes.GENOME_GATHERING, StarTRecipeTypes.GENOME_MIXING,
                    StarTRecipeTypes.GENOME_SEPARATING, StarTRecipeTypes.GENOME_INSERTION)
            .pattern(definition -> FactoryBlockPattern.start()
                    // spotless:off
                    .aisle("     AAAAA     ", "     AAAAA     ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "    BBBBBBB    ", "               ", "               ", "               ")
                    .aisle("   AAAAAAAAA   ", "   AAACCCAAA   ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "  BBBDDDDDBBB  ", "     BBBBB     ", "               ", "               ")
                    .aisle("  AAAAAAAAAAA  ", "  AEEAAAAAEEA  ", "     D   D     ", "     D   D     ", "     D   D     ", "    D     D    ", "    D     D    ", "               ", "               ", " BBBD     DBBB ", "    BBBFBBB    ", "               ", "               ")
                    .aisle(" AAAAAAAAAAAAA ", " AEAAAGGGAAAEA ", "               ", "               ", "               ", "               ", "               ", "   D       D   ", "   D       D   ", " BBD       DBB ", "   BBBBFBBBB   ", "      HFH      ", "               ")
                    .aisle(" AAAAAAAAAAAAA ", " AEAGGGGGGGAEA ", "               ", "               ", "               ", "  D         D  ", "  D         D  ", "               ", "               ", "BBD   III   DBB", "  BBBBBBBBBBB  ", "    HHHHHHH    ", "               ")
                    .aisle("AAAAAAAAAAAAAAA", "AAAAGGGJGGGAAAA", "  D         D  ", "  D         D  ", "  D         D  ", "               ", "               ", "               ", "               ", "BD   I   I   DB", " BBBBBKKKBBBBB ", "    HHKKKHH    ", "      BBB      ")
                    .aisle("AAAAAAAAAAAAAAA", "ACAGGGGJGGGGACA", "      EEE      ", "       E       ", "               ", "               ", "               ", "               ", "               ", "BD  I  E  I  DB", " BBBBKEEEKBBBB ", "   HHKLLLKHH   ", "     BBLBB     ")
                    .aisle("AAAAAAAAAAAAAAA", "ACAGGJJJJJGGACA", "      EME      ", "      EME      ", "       M       ", "               ", "       N       ", "               ", "       M       ", "BD  I EME I  DB", " BFFBKEMEKBFFB ", "   FHKLLLKHF   ", "     BLLLB     ")
                    .aisle("AAAAAAAAAAAAAAA", "ACAGGGGJGGGGACA", "      EEE      ", "       E       ", "               ", "               ", "               ", "               ", "               ", "BD  I  E  I  DB", " BBBBKEEEKBBBB ", "   HHKLLLKHH   ", "     BBLBB     ")
                    .aisle("AAAAAAAAAAAAAAA", "AAAAGGGJGGGAAAA", "  D         D  ", "  D         D  ", "  D         D  ", "               ", "               ", "               ", "               ", "BD   I   I   DB", " BBBBBKKKBBBBB ", "    HHKKKHH    ", "      BBB      ")
                    .aisle(" AAAAAAAAAAAAA ", " AEAGGGGGGGAEA ", "               ", "               ", "               ", "  D         D  ", "  D         D  ", "               ", "               ", "BBD   III   DBB", "  BBBBBBBBBBB  ", "    HHHHHHH    ", "               ")
                    .aisle(" AAAAAAAAAAAAA ", " AEAAAGGGAAAEA ", "               ", "               ", "               ", "               ", "               ", "   D       D   ", "   D       D   ", " BBD       DBB ", "   BBBBFBBBB   ", "      HFH      ", "               ")
                    .aisle("  AAAAAAAAAAA  ", "  AEEAAAAAEEA  ", "     D   D     ", "     D   D     ", "     D   D     ", "    D     D    ", "    D     D    ", "               ", "               ", " BBBD     DBBB ", "    BBBFBBB    ", "               ", "               ")
                    .aisle("   AAAAAAAAA   ", "   AAACCCAAA   ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "  BBBDDDDDBBB  ", "     BBBBB     ", "               ", "               ")
                    .aisle("     AAAAA     ", "     AA@AA     ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "    BBBBBBB    ", "               ", "               ", "               ")
                    // spotless:on
                    .where("A", Predicates.blocks(GTBlocks.CASING_HSSE_STURDY.get()).setMinGlobalLimited(120)
                            .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                            .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1)))
                    .where("B", Predicates.blocks(GTBlocks.CASING_PALLADIUM_SUBSTATION.get()))
                    .where("C", Predicates.blocks(StarTMachineUtils.getKjsBlock("superdense_assembly_control_casing")))
                    .where("D", Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTMaterials.Tritanium)))
                    .where("E", Predicates.blocks(GTBlocks.ADVANCED_COMPUTER_CASING.get()))
                    .where("F", Predicates.blocks(StarTMachineUtils.getKjsBlock("reinforced_fusion_glass")))
                    .where("G", Predicates.blocks(StarTMachineUtils.getKjsBlock("superdense_machine_casing")))
                    .where("H", Predicates.blocks(StarTMachineUtils.getKjsBlock("prismalium_casing")))
                    .where("I", Predicates.blocks(GTBlocks.SUPERCONDUCTING_COIL.get()))
                    .where("J", Predicates.blocks(GTBlocks.HIGH_POWER_CASING.get()))
                    .where("K", Predicates.blocks(StarTMachineUtils.getKjsBlock("melodium_casing")))
                    .where("L", Predicates.blocks(StarTMachineUtils.getKjsBlock("stellarium_casing")))
                    .where("M", Predicates.blocks(StarTMachineUtils.getKjsBlock("superalloy_casing")))
                    .where("N", Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.block, GTMaterials.NetherStar)))
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .where(" ", Predicates.any())
                    .build())
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_sturdy_hsse"),
                    GTCEu.id("block/machines/wiremill"))
            .register();

    public static void init() {}
}
// Invalid descriptor on gtceu.mixins.json:jei.FluidHelperMixin->@Inject::gtceu$injectFluidTooltips(
// Lmezz/jei/api/gui/builder/ITooltipBuilder;
// Lnet/minecraftforge/fluids/FluidStack;
// Lnet/minecraft/world/item/TooltipFlag;
// Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V!

// Expected (
// Ljava/util/List;
// Lnet/minecraftforge/fluids/FluidStack;
// Lnet/minecraft/world/item/TooltipFlag;
// Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V

// but found (
// Lmezz/jei/api/gui/builder/ITooltipBuilder;
// Lnet/minecraftforge/fluids/FluidStack;
// Lnet/minecraft/world/item/TooltipFlag;
// Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V
//
// [INJECT Applicator Phase -> gtceu.mixins.json:jei.FluidHelperMixin -> Apply Injections -> -> Inject ->
// gtceu.mixins.json:jei.FluidHelperMixin->@Inject::gtceu$injectFluidTooltips(
// Lmezz/jei/api/gui/builder/ITooltipBuilder;
// Lnet/minecraftforge/fluids/FluidStack;
// Lnet/minecraft/world/item/TooltipFlag;
// Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V]
