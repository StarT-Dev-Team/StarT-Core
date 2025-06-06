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
        .appearanceBlock(() -> ForgeRegistries.BLOCKS.getValue(KubeJS.id("extreme_temperature_smelting_casing")))
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
            Component.translatable("block.start_core.hellforge_d3"),
            Component.literal(""),
            Component.translatable("block.start_core.hellforge_d4"),
            Component.literal(""),
            Component.translatable("block.start_core.hellforge_d5"),
            Component.translatable("block.start_core.hellforge_d6"),
            Component.translatable("block.start_core.breaker_line")
        )
        .recipeModifiers(GTRecipeModifiers.OC_NON_PERFECT, StarTRecipeModifiers.HELL_FORGE_OC)
        .rotationState(RotationState.NON_Y_AXIS)
        .recipeTypes(StarTRecipeTypes.HELL_FORGE_RECIPES)
        .pattern(definition -> FactoryBlockPattern.start()
            .aisle("####B###B####", "####B###B####", "####B###B####", "####B###B####", "####BBBBB####", "####CCCCC####", "#############", "#############", "#############", "######D######", "####DDDDD####", "######D######", "#############", "#############", "#############", "####CCCCC####", "####BBBBB####", "#############", "####BBBBB####", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "######E######", "######E######", "######E######", "#############", "#############", "#############") 
            .aisle("#B##BBBBB##B#", "#B##FEGEF##B#", "#B##FEGEF##B#", "#B##FEGEF##B#", "#BBB#####BBB#", "#CCCEEEEECCC#", "####EEEEE####", "#####EEE#####", "#############", "#############", "##DD#####DD##", "#############", "#############", "#####EEE#####", "####EEEEE####", "#CCCEEEEECCC#", "#BBB#####BBB#", "####FFFFF####", "#BBB#####BBB#", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "######E######", "####E###E####", "####E###E####", "########E####", "######E######", "#############", "#############") 
            .aisle("##BBBBBBBBB##", "##FF#####FF##", "##FF#####FF##", "##FF#####FF##", "#B#########B#", "#CEE#####EEC#", "##EEH###HEE##", "####H#E#H####", "####HE#EH####", "#############", "#D#########D#", "#############", "####HE#EH####", "####H#E#H####", "##EEH###HEE##", "#CEE#####EEC#", "#B##IIIII##B#", "##FF#####FF##", "#B#########B#", "#############", "#############", "#############", "####DDDDD####", "#############", "#############", "#############", "######E#E####", "####E#E#E####", "#############", "#############", "####E########", "########E####", "#############", "#############") 
            .aisle("##BBBBBBBBB##", "##F#######F##", "##F#######F##", "##F#######F##", "#B#########B#", "#CE#######EC#", "##EH#####HE##", "###H#####H###", "###H#GGG#H###", "###HE#G#EH###", "#D##E###E##D#", "###HE#G#EH###", "###H#GGG#H###", "###H#####H###", "##EH#####HE##", "#CE#######EC#", "#B#II###II#B#", "##F##GGG##F##", "#B###IJI###B#", "#####GGG#####", "#############", "#############", "###D#####D###", "#############", "#############", "#####EHE#####", "####EEHEH####", "####E########", "#########E###", "#########E###", "#############", "#############", "#############", "#############") 
            .aisle("BBBBBBBBBBBBB", "BF#########FB", "BF#########FB", "BF#########FB", "B###########B", "CE#########EC", "#EH#######HE#", "##H#######H##", "##H##B#B##H##", "###E#BGB#E###", "D##E#BKB#E##D", "###E#BGB#E###", "##H#######H##", "##H#######H##", "#EH#######HE#", "CE#########EC", "B#II#####II#B", "#F##GG#GG##F#", "B###IG#GI###B", "####GEBEG####", "####GEBEG####", "#####EBE#####", "##D##EBH##D##", "#####EBE#####", "#####EBE#####", "####EEEEE####", "####EEEEEE###", "#########E###", "#############", "#############", "#########E###", "#############", "#############", "#############") 
            .aisle("#BBBBBBBBBBB#", "#E#########E#", "#E#########E#", "#E#########E#", "B###########B", "CE#########EC", "#E#########E#", "#E#########E#", "##EGB###BGE##", "####B###B####", "D###B###B###D", "####B###B####", "##EG#####GE##", "#E#########E#", "#E#########E#", "CE#########EC", "B#I#######I#B", "#F#GG###GG#F#", "B##IG###GI##B", "###GE###EG###", "####E#B#E####", "####E###E####", "##D#E###E#D##", "####H###E####", "####EEEEE####", "####EBBBEE###", "########HE###", "#############", "#############", "#############", "#############", "#############", "#############", "#############") 
            .aisle("#BBBBBBBBBBB#", "#G#########G#", "#G#########G#", "#G#########G#", "B###########B", "CE#########EC", "#E#########E#", "#EE#######EE#", "###G##E##G###", "D##GG#E#GG##D", "D###K#L#K###D", "D##GG#E#GG##D", "###G##E##G###", "#EE#######EE#", "#E#########E#", "CE#########EC", "B#I#######I#B", "#F#G#####G#F#", "B##J#####J##B", "###GB###BG###", "####B#B#B####", "####B#B#B####", "##D#B###B#D##", "####B###B####", "####BEEEB####", "#####BBBEE###", "#########H###", "#############", "#############", "#############", "#############", "#############", "#############", "#############") 
            .aisle("#BBBBBBBBBBB#", "#E#########E#", "#E#########E#", "#E#########E#", "B###########B", "CE#########EC", "#E#########E#", "#E#########E#", "##EGB###BGE##", "####B###B####", "D###B###B###D", "####B###B####", "##EG#####GE##", "#E#########E#", "#E#########E#", "CE#########EC", "B#I#######I#B", "#F#GG###GG#F#", "B##IG###GI##B", "###GE###EG###", "####E###E####", "####E###E####", "##D#E###E#D##", "####H###H####", "####EEBEEE###", "########HEE##", "#########EE##", "##########E##", "##########E##", "#############", "#############", "#############", "#############", "#############") 
            .aisle("BBBBBBBBBBBBB", "BF#########FB", "BF#########FB", "BF#########FB", "B###########B", "CE#########EC", "#EH#######HE#", "##H#######H##", "##H##B#B##H##", "###E#BGB#E###", "D##E#BKB#E##D", "###E#BGB#E###", "##H#######H##", "##H#######H##", "#EH#######HE#", "CE#########EC", "B#II#####II#B", "#F##GG#GG##F#", "B###IG#GI###B", "####GEBEG####", "####GEBEG####", "#####EBE#####", "##D##HBE##D##", "#####EBE#####", "#####EBE#####", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############") 
            .aisle("##BBBBBBBBB##", "##F#######F##", "##F#######F##", "##F#######F##", "#B#########B#", "#CE#######EC#", "##EH#####HE##", "###H#####H###", "###H#GGG#H###", "###HE#G#EH###", "#D##E###E##D#", "###HE#G#EH###", "###H#GGG#H###", "###H#####H###", "##EH#####HE##", "#CE#######EC#", "#B#II###II#B#", "##F##GGG##F##", "#B###IJI###B#", "#####GGG#####", "#############", "#############", "###D#####D###", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############") 
            .aisle("##BBBBBBBBB##", "##FF#####FF##", "##FF#####FF##", "##FF#####FF##", "#B#########B#", "#CEE#####EEC#", "##EEH###HEE##", "####H#E#H####", "####HE#EH####", "#############", "#D#########D#", "#############", "####HE#EH####", "####H#E#H####", "##EEH###HEE##", "#CEE#####EEC#", "#B##IIIII##B#", "##FF#####FF##", "#B#########B#", "#############", "#############", "#############", "####DDDDD####", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############") 
            .aisle("#B##BBBBB##B#", "#B##FEEEF##B#", "#B##FE@EF##B#", "#B##FEEEF##B#", "#BBB#####BBB#", "#CCCEEEEECCC#", "####EEEEE####", "#####EEE#####", "#############", "#############", "##DD#####DD##", "#############", "#############", "#####EEE#####", "####EEEEE####", "#CCCEEEEECCC#", "#BBB#####BBB#", "####FFFFF####", "#BBB#####BBB#", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############") 
            .aisle("####B###B####", "####B###B####", "####B###B####", "####B###B####", "####BBBBB####", "####CCCCC####", "#############", "#############", "#############", "######D######", "####DDDDD####", "######D######", "#############", "#############", "#############", "####CCCCC####", "####BBBBB####", "#############", "####BBBBB####", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############", "#############") 
            .where("#", Predicates.any())
            .where("B", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(KubeJS.id("noble_mixing_casing"))))
            .where("C", Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTCEuAPI.materialManager.getMaterial("gtceu:astrenalloy_nx"))))
            .where("D", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.of("thermal:signalum_glass", ':'))))
            .where("E", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(KubeJS.id("extreme_temperature_smelting_casing")))
                .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2).setPreviewCount(0))
                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(16).setPreviewCount(0))
                .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMaxGlobalLimited(16).setPreviewCount(0))
                .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1)))
            .where("F", Predicates.blocks(GCYMBlocks.HEAT_VENT.get()))
            .where("G", Predicates.blocks(GCYMBlocks.CASING_ATOMIC.get()))
            .where("H", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(KubeJS.id("hydronalium_casing"))))
            .where("I", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(KubeJS.id("enriched_naquadah_machine_casing"))))
            .where("J", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(KubeJS.id("runic_stabilization_casing"))))
            .where("K", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.of("thermal_extra:shellite_glass", ':'))))
            .where("L", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(KubeJS.id("heart_of_the_flame"))))
            .where("@", Predicates.controller(Predicates.blocks(definition.get())))
        .build()
    )
    .workableCasingRenderer(KubeJS.id("block/casings/ultimate_multis/extreme_temperature_smelting_casing"),
        GTCEu.id("block/machines/alloy_smelter"), false)
    .register();

    public static void init() {}
}
