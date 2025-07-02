package com.startechnology.start_core.machine.hellforge;

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
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.startechnology.start_core.machine.StarTMachineUtils;
import com.startechnology.start_core.machine.StarTPartAbility;
import com.startechnology.start_core.machine.bacteria.BacterialRunicMutator;
import com.startechnology.start_core.recipe.StarTRecipeModifiers;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class StarTHellForgeMachines {
    
    public static final MultiblockMachineDefinition HELL_FORGE = START_REGISTRATE
        .multiblock("hellforge", StarTHellForgeMachine::new)
        .appearanceBlock(() -> StarTMachineUtils.getKjsBlock(("extreme_temperature_smelting_casing")))
        .langValue("Hell Forge")
        .tooltips(
            Component.translatable("block.start_core.hellforge_multiblock_line"),
            Component.translatable("block.start_core.hellforge_description"),
            Component.translatable("block.start_core.breaker_line"),
            Component.translatable("block.start_core.hellforge_d0"),
            Component.translatable("block.start_core.hellforge_d1"),
            Component.literal(""),
            Component.translatable("block.start_core.hellforge_d2"),
            Component.literal(""),
            Component.translatable("block.start_core.hellforge_d9"),
            Component.literal(""),
            Component.translatable("block.start_core.hellforge_d3"),
            Component.literal(""),
            Component.translatable("block.start_core.hellforge_d5"),
            Component.translatable("block.start_core.hellforge_d6"),
            Component.literal(""),
            Component.translatable("block.start_core.hellforge_d7"),
            Component.translatable("block.start_core.hellforge_d8"),
            Component.translatable("block.start_core.breaker_line")
        )
        .recipeModifiers(GTRecipeModifiers.OC_NON_PERFECT, StarTRecipeModifiers.HELL_FORGE_OC)
        .rotationState(RotationState.NON_Y_AXIS)
        .recipeTypes(StarTRecipeTypes.HELL_FORGE_RECIPES)
        .pattern(definition -> FactoryBlockPattern.start()
            .aisle("####B###B####", "####B###B####", "####B###B####", "####B###B####", "####BBBBB####", "####CCCCC####", "#############", "#############", "#############", "######D######", "####DDDDD####", "######D######", "#############", "#############", "#############", "####CCCCC####", "####BBBBB####", "#############", "####BBBBB####", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "######E######", "######E######", "######E######", "#############") 
            .aisle("#B##BBBBB##B#", "#B##FEEEF##B#", "#B##FEGEF##B#", "#B##FEEEF##B#", "#BBBBBBBBBBB#", "#CCCEEEEECCC#", "####EEEEE####", "#####EEE#####", "#############", "#############", "##DD#####DD##", "#############", "#############", "#####EEE#####", "####EEEEE####", "#CCCEEEEECCC#", "#BBBHHHHHBBB#", "####FFFFF####", "#BBB#####BBB#", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "######E######", "####E###E####", "####E###E####", "########E####", "######E######") 
            .aisle("##BBFBBBFBB##", "##FF#####FF##", "##FF#####FF##", "##FF#####FF##", "#BBBFFFFFBBB#", "#CHHHHHHHHHC#", "##HHHHHHHHH##", "####HHEHH####", "#####E#E#####", "#############", "#D#########D#", "#############", "#####E#E#####", "####HHEHH####", "##HHHHHHHHH##", "#CHHHHHHHHHC#", "#BHHIIIIIHHB#", "##FF#####FF##", "#B#########B#", "#############", "#############", "#############", "####DDDDD####", "#############", "#############", "#############", "######E#E####", "####E#E#E####", "#############", "#############", "####E########", "########E####") 
            .aisle("##BFFBBBFFB##", "##F##III##F##", "##F##III##F##", "##F##III##F##", "#BBFFIIIFFBB#", "#CHHHIIIHHHC#", "##HHHIIIHHH##", "###HHIIIHH###", "###HHJJJHH###", "###HE#J#EH###", "#D##E###E##D#", "###HE#J#EH###", "###HHJJJHH###", "###HHIIIHH###", "##HHHIIIHHH##", "#CHHHIIIHHHC#", "#BHIIIIIIIHB#", "##F##JJJ##F##", "#B###IKI###B#", "#####JJJ#####", "#############", "#############", "###D#####D###", "#############", "#############", "#####E#E#####", "####EEHEH####", "####E########", "#########E###", "#########E###", "#############", "#############") 
            .aisle("BBFFBBBBBFFBB", "BF##GIFIG##FB", "BF##GJ#JG##FB", "BF##GJ#JG##FB", "BBFFG###GFFBB", "CEHHG###GHHEC", "#EHHG###GHHE#", "##HHG###GHH##", "###HGBCBGH###", "###EGBJBGE###", "D##EGBLBGE##D", "###EGBJBGE###", "###HGBCBGH###", "##HHG###GHH##", "#EHHG###GHHE#", "CEHHG###GHHEC", "BHIIG###GIIHB", "#F##JJ#JJ##F#", "B###IJ#JI###B", "####JEBEJ####", "####JEBEJ####", "#####EBE#####", "##D##EBH##D##", "#####EBE#####", "#####EBE#####", "####EEEEE####", "####HEEEEE###", "#########E###", "#############", "#############", "#########E###", "#############") 
            .aisle("#BFBBBBBBBFB#", "#E#IIIIIII#E#", "#E#IJ###JI#E#", "#E#IJ###JI#E#", "BBFI#####IFBB", "CEHI#####IHEC", "#EHI#####IHE#", "#EHI#####IHE#", "##EJB#C#BJE##", "####B###B####", "D###B###B###D", "####B###B####", "##EJB#C#BJE##", "#EHI#####IHE#", "#EHI#####IHE#", "CEHI#####IHEC", "BHII#####IIHB", "#F#JJ###JJ#F#", "B##IJ###JI##B", "###JE###EJ###", "####E#B#E####", "####E###E####", "##D#E###E#D##", "####H###E####", "####EEEEE####", "####EBBBEE###", "########HE###", "#############", "#############", "#############", "#############", "#############") 
            .aisle("#BFBBBBBBBFB#", "#E#IFIIIFI#E#", "#G#I##G##I#G#", "#E#I##G##I#E#", "BBFI##G##IFBB", "CEHI##G##IHEC", "#EHI#####IHE#", "#EEI#####IEE#", "###JCCECCJ###", "D##JJ#E#JJ##D", "D###L#M#L###D", "D##JJ#E#JJ##D", "###JCCECCJ###", "#EEI#####IEE#", "#EHI#####IHE#", "CEHI#####IHEC", "BHII#####IIHB", "#F#J#####J#F#", "B##K#####K##B", "###JB###BJ###", "####B#B#B####", "####B#B#B####", "##D#B###B#D##", "####B###B####", "####BEEEB####", "#####BBBEE###", "#############", "#############", "#############", "#############", "#############", "#############") 
            .aisle("#BFBBBBBBBFB#", "#E#IIIIIII#E#", "#E#IJ###JI#E#", "#E#IJ###JI#E#", "BBFI#####IFBB", "CEHI#####IHEC", "#EHI#####IHE#", "#EHI#####IHE#", "##EJB#C#BJE##", "####B###B####", "D###B###B###D", "####B###B####", "##EJB#C#BJE##", "#EHI#####IHE#", "#EHI#####IHE#", "CEHI#####IHEC", "BHII#####IIHB", "#F#JJ###JJ#F#", "B##IJ###JI##B", "###JE###EJ###", "####E###E####", "####E###E####", "##D#E###E#D##", "####H###H####", "####EEBEEE###", "########HEE##", "#########EE##", "##########E##", "##########E##", "#############", "#############", "#############") 
            .aisle("BBFFBBBBBFFBB", "BF##GIFIG##FB", "BF##GJ#JG##FB", "BF##GJ#JG##FB", "BBFFG###GFFBB", "CEHHG###GHHEC", "#EHHG###GHHE#", "##HHG###GHH##", "###HGBCBGH###", "###EGBJBGE###", "D##EGBLBGE##D", "###EGBJBGE###", "###HGBCBGH###", "##HHG###GHH##", "#EHHG###GHHE#", "CEHHG###GHHEC", "BHIIG###GIIHB", "#F##JJ#JJ##F#", "B###IJ#JI###B", "####JEBEJ####", "####JEBEJ####", "#####EBE#####", "##D##HBE##D##", "#####EBE#####", "#####EBE#####", "#############", "#############", "#############", "#############", "#############", "#############", "#############") 
            .aisle("##BFFBBBFFB##", "##F##III##F##", "##F##III##F##", "##F##III##F##", "#BBFFIIIFFBB#", "#CHHHIIIHHHC#", "##HHHIIIHHH##", "###HHIIIHH###", "###HHJJJHH###", "###HE#J#EH###", "#D##E###E##D#", "###HE#J#EH###", "###HHJJJHH###", "###HHIIIHH###", "##HHHIIIHHH##", "#CHHHIIIHHHC#", "#BHIIIIIIIHB#", "##F##JJJ##F##", "#B###IKI###B#", "#####JJJ#####", "#############", "#############", "###D#####D###", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############") 
            .aisle("##BBFFFFFBB##", "##FF#####FF##", "##FF#####FF##", "##FF#####FF##", "#BBBFFFFFBBB#", "#CHHHHHHHHHC#", "##HHHHHHHHH##", "####HHEHH####", "#####E#E#####", "#############", "#D#########D#", "#############", "#####E#E#####", "####HHEHH####", "##HHHHHHHHH##", "#CHHHHHHHHHC#", "#BHHIIIIIHHB#", "##FF#####FF##", "#B#########B#", "#############", "#############", "#############", "####DDDDD####", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############") 
            .aisle("#B##BBBBB##B#", "#B##FEEEF##B#", "#B##FE@EF##B#", "#B##FEEEF##B#", "#BBBBBBBBBBB#", "#CCCEEEEECCC#", "####EEEEE####", "#####EEE#####", "#############", "#############", "##DD#####DD##", "#############", "#############", "#####EEE#####", "####EEEEE####", "#CCCEEEEECCC#", "#BBBHHHHHBBB#", "####FFFFF####", "#BBB#####BBB#", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############") 
            .aisle("####B###B####", "####B###B####", "####B###B####", "####B###B####", "####BBBBB####", "####CCCCC####", "#############", "#############", "#############", "######D######", "####DDDDD####", "######D######", "#############", "#############", "#############", "####CCCCC####", "####BBBBB####", "#############", "####BBBBB####", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############") 
            .where("#", Predicates.any())
            .where("B", Predicates.blocks(StarTMachineUtils.getKjsBlock("noble_mixing_casing")))
            .where("C", Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTCEuAPI.materialManager.getMaterial("gtceu:astrenalloy_nx"))))
            .where("D", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.of("thermal:signalum_glass", ':'))))
            .where("E", Predicates.blocks(StarTMachineUtils.getKjsBlock("extreme_temperature_smelting_casing"))
                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(8).setPreviewCount(0))
                .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMaxGlobalLimited(2).setPreviewCount(0))
                .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2))
                .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1))
                .or(Predicates.abilities(StarTPartAbility.REDSTONE_INTERFACE).setMaxGlobalLimited(4).setPreviewCount(0)))
            .where("F", Predicates.blocks(GCYMBlocks.HEAT_VENT.get()))
            .where("G", Predicates.blocks(StarTMachineUtils.getKjsBlock("enriched_naquadah_pipe_casing")))
            .where("H", Predicates.blocks(StarTMachineUtils.getKjsBlock("reinforced_brimstone_casing")))
            .where("I", Predicates.blocks(StarTMachineUtils.getKjsBlock("enriched_naquadah_machine_casing")))
            .where("J", Predicates.blocks(GCYMBlocks.CASING_ATOMIC.get()))
            .where("K", Predicates.blocks(StarTMachineUtils.getKjsBlock("runic_stabilization_casing")))
            .where("L",Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.of("thermal_extra:shellite_glass", ':'))))
            .where("M", Predicates.blocks(StarTMachineUtils.getKjsBlock("heart_of_the_flame")))
            .where("@", Predicates.controller(Predicates.blocks(definition.get())))
            .build())
    .workableCasingRenderer(KubeJS.id("block/casings/ultimate_multis/extreme_temperature_smelting_casing"),
        GTCEu.id("block/machines/alloy_smelter"), false)
    .register();

    public static void init() {}
}
