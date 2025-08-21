package com.startechnology.start_core.machine.abyssal_containment;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.machine.StarTMachineUtils;
import com.startechnology.start_core.machine.abyssal_harvester.StarTAbyssalHarvesterMachine;

import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.network.chat.Component;


public class StarTAbyssalContainmentMachines {
    
    public static MultiblockMachineDefinition ABYSSAL_CONTAINMENT_ROOM = StarTCore.START_REGISTRATE
        .multiblock("abyssal_containment_room", StarTAbyssalContainmentMachine::new)
        .appearanceBlock(() -> StarTMachineUtils.getKjsBlock(("draco_ware_casing")))
        .recipeType(GTRecipeTypes.DUMMY_RECIPES)
        .langValue("Abyssal Containment Room")
        .pattern(definition -> FactoryBlockPattern.start()
            .aisle("AAAAAAAAAAAAAAAAA", "A######A#A######A", "A####AA###AA####A", "A###A#######A###A", "A##A#########A##A", "A#A###########A#A", "A#A###########A#A", "AA#############AA", "A###############A", "A###############A", "A###############A", "A###############A", "A###############A", "AA#############AA", "A#A###########A#A", "A#A###########A#A", "A##A#########A##A", "A###A#######A###A", "A####AA###AA####A", "A######A#A######A", "AAAAAAAAAAAAAAAAA") 
            .aisle("A######A#A######A", "#BBBBBBCDCBBBBBB#", "#BBBBCCCDCCCBBBB#", "#BBBCCCCDCCCCBBB#", "#BBCCCCEEECCCCBB#", "#BCCCEEEEEEECCCB#", "#BCCCEEFFFEECCCB#", "ACCCEEFFDFFEECCCA", "#CCCEEFFFFFEECCC#", "#CCEEFFFDFFFEECC#", "#DDEEFDFDFDFEEDD#", "#CCEEFFFDFFFEECC#", "#CCCEEFFFFFEECCC#", "ACCCEEFFDFFEECCCA", "#BCCCEEFFFEECCCB#", "#BCCCEEEEEEECCCB#", "#BBCCCCEEECCCCBB#", "#BBBCCCCDCCCCBBB#", "#BBBBCCCDCCCBBBB#", "#BBBBBBCDCBBBBBB#", "A######A#A######A") 
            .aisle("A####AA###AA####A", "#BBBBCCCDCCCBBBB#", "#B#############B#", "#B#############B#", "#B#############B#", "AC#############CA", "AC#############CA", "#C#############C#", "#C#############C#", "#C#############C#", "#D#############D#", "#C#############C#", "#C#############C#", "#C#############C#", "AC#############CA", "AC#############CA", "#B#############B#", "#B#############B#", "#B#############B#", "#BBBBCCCDCCCBBBB#", "A####AA###AA####A") 
            .aisle("A###A#######A###A", "#BBBCCCCDCCCCBBB#", "#B#############B#", "#B#############B#", "AC#############CA", "#C#############C#", "#C#############C#", "#C#############C#", "#C#############C#", "#E#############E#", "#E#############E#", "#E#############E#", "#C#############C#", "#C#############C#", "#C#############C#", "#C#############C#", "AC#############CA", "#B#############B#", "#B#############B#", "#BBBCCCCDCCCCBBB#", "A###A#######A###A") 
            .aisle("A##A#########A##A", "#BBCCCCEEECCCCBB#", "#B#############B#", "AC#############CA", "#C#############C#", "#C#############C#", "#C#############C#", "#E#############E#", "#E#############E#", "#E#############E#", "#E#############E#", "#E#############E#", "#E#############E#", "#E#############E#", "#C#############C#", "#C#############C#", "#C#############C#", "AC#############CA", "#B#############B#", "#BBCCCCEEECCCCBB#", "A##A#########A##A") 
            .aisle("A#A###########A#A", "#BCCCEEEEEEECCCB#", "AC#############CA", "#C#############C#", "#C#############C#", "#E#############E#", "#E#############E#", "#E#############E#", "#E#############E#", "#F#############F#", "#F#############F#", "#F#############F#", "#E#############E#", "#E#############E#", "#E#############E#", "#E#############E#", "#C#############C#", "#C#############C#", "AC#############CA", "#BCCCEEEEEEECCCB#", "A#A###########A#A") 
            .aisle("A#A###########A#A", "#BCCCEEFFFEECCCB#", "AC#############CA", "#C#############C#", "#C#############C#", "#E#############E#", "#E#############E#", "#F#############F#", "#F#############F#", "#F#############F#", "#D#############D#", "#F#############F#", "#F#############F#", "#F#############F#", "#E#############E#", "#E#############E#", "#C#############C#", "#C#############C#", "AC#############CA", "#BCCCEEFFFEECCCB#", "A#A###########A#A") 
            .aisle("AA#############AA", "ACCCEEFFFFFEECCCA", "#C#############C#", "#C#############C#", "#E#############E#", "#E#############E#", "#F#############F#", "#F#############F#", "#F#############F#", "#F#############F#", "#F#############F#", "#F#############F#", "#F#############F#", "#F#############F#", "#F#############F#", "#E#############E#", "#E#############E#", "#C#############C#", "#C#############C#", "ACCCEEFFFFFEECCCA", "AA#############AA") 
            .aisle("A###############A", "#DDDEEFFDFFEEDDD#", "#D#############D#", "#D#############D#", "#E#############E#", "#E#############E#", "#F#############F#", "#D#############D#", "#F#############F#", "#D#############D#", "#D#############D#", "#D#############D#", "#F#############F#", "#D#############D#", "#F#############F#", "#E#############E#", "#E#############E#", "#D#############D#", "#D#############D#", "#DDDEEFFDFFEEDDD#", "A###############A") 
            .aisle("AA#############AA", "ACCCEEFFFFFEECCCA", "#C#############C#", "#C#############C#", "#E#############E#", "#E#############E#", "#F#############F#", "#F#############F#", "#F#############F#", "#F#############F#", "#F#############F#", "#F#############F#", "#F#############F#", "#F#############F#", "#F#############F#", "#E#############E#", "#E#############E#", "#C#############C#", "#C#############C#", "ACCCEEFFFFFEECCCA", "AA#############AA") 
            .aisle("A#A###########A#A", "#BCCCEEFFFEECCCB#", "AC#############CA", "#C#############C#", "#C#############C#", "#E#############E#", "#E#############E#", "#F#############F#", "#F#############F#", "#F#############F#", "#D#############D#", "#F#############F#", "#F#############F#", "#F#############F#", "#E#############E#", "#E#############E#", "#C#############C#", "#C#############C#", "AC#############CA", "#BCCCEEFFFEECCCB#", "A#A###########A#A") 
            .aisle("A#A###########A#A", "#BCCCEEEEEEECCCB#", "AC#############CA", "#C#############C#", "#C#############C#", "#E#############E#", "#E#############E#", "#E#############E#", "#E#############E#", "#F#############F#", "#F#############F#", "#F#############F#", "#E#############E#", "#E#############E#", "#E#############E#", "#E#############E#", "#C#############C#", "#C#############C#", "AC#############CA", "#BCCCEEEEEEECCCB#", "A#A###########A#A") 
            .aisle("A##A#########A##A", "#BBCCCCEEECCCCBB#", "#B#############B#", "AC#############CA", "#C#############C#", "#C#############C#", "#C#############C#", "#E#############E#", "#E#############E#", "#E#############E#", "#E#############E#", "#E#############E#", "#E#############E#", "#E#############E#", "#C#############C#", "#C#############C#", "#C#############C#", "AC#############CA", "#B#############B#", "#BBCCCCEEECCCCBB#", "A##A#########A##A") 
            .aisle("A###A#######A###A", "#BBBCCCCDCCCCBBB#", "#B#############B#", "#B#############B#", "AC#############CA", "#C#############C#", "#C#############C#", "#C#############C#", "#C#############C#", "#E#############E#", "#E#############E#", "#E#############E#", "#C#############C#", "#C#############C#", "#C#############C#", "#C#############C#", "AC#############CA", "#B#############B#", "#B#############B#", "#BBBCCCCDCCCCBBB#", "A###A#######A###A") 
            .aisle("A####AA###AA####A", "#BBBBCCCDCCCBBBB#", "#B#############B#", "#B#############B#", "#B#############B#", "AC#############CA", "AC#############CA", "#C#############C#", "#C#############C#", "#C#############C#", "#D#############D#", "#C#############C#", "#C#############C#", "#C#############C#", "AC#############CA", "AC#############CA", "#B#############B#", "#B#############B#", "#B#############B#", "#BBBBCCCDCCCBBBB#", "A####AA###AA####A") 
            .aisle("A######A#A######A", "#BBBBBBCDCBBBBBB#", "#BBBBCCCDCCCBBBB#", "#BBBCCCCDCCCCBBB#", "#BBCCCCEEECCCCBB#", "#BCCCEEEEEEECCCB#", "#BCCCEEFFFEECCCB#", "ACCCEEFFDFFEECCCA", "#CCCEEFFFFFEECCC#", "#CCEEFFFDFFFEECC#", "#DDEEFDF@FDFEEDD#", "#CCEEFFFDFFFEECC#", "#CCCEEFFFFFEECCC#", "ACCCEEFFDFFEECCCA", "#BCCCEEFFFEECCCB#", "#BCCCEEEEEEECCCB#", "#BBCCCCEEECCCCBB#", "#BBBCCCCDCCCCBBB#", "#BBBBCCCDCCCBBBB#", "#BBBBBBCDCBBBBBB#", "A######A#A######A") 
            .aisle("AAAAAAAAAAAAAAAAA", "A######A#A######A", "A####AA###AA####A", "A###A#######A###A", "A##A#########A##A", "A#A###########A#A", "A#A###########A#A", "AA#############AA", "A###############A", "A###############A", "A###############A", "A###############A", "A###############A", "AA#############AA", "A#A###########A#A", "A#A###########A#A", "A##A#########A##A", "A###A#######A###A", "A####AA###AA####A", "A######A#A######A", "AAAAAAAAAAAAAAAAA") 
            .where("A",  Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTCEuAPI.materialManager.getMaterial("abyssal_alloy"))))
            .where("#", Predicates.any())
            .where("B", Predicates.blocks(StarTMachineUtils.getKjsBlock("draneko_casing")))
            .where("C", Predicates.blocks(GCYMBlocks.CASING_ATOMIC.get()))
            .where("D", Predicates.blocks(StarTMachineUtils.getKjsBlock("draco_ware_casing"))
                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(2))
                .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2))
                .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1)))
            .where("E", Predicates.blocks(StarTMachineUtils.getKjsBlock("abyssal_inductor_hull")))
            .where("F", Predicates.blocks(StarTMachineUtils.getKjsBlock("draco_resilient_fusion_glass")))
            .where("@", Predicates.controller(Predicates.blocks(definition.get())))
            .build()
        ).workableCasingRenderer(KubeJS.id("block/casings/draco_ware_casing"),
            StarTCore.resourceLocation("block/overlay/abyssal_containment"), false)
        .register();

        public static final void init() {};
}
