package com.startechnology.start_core.machine.dreamlink;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.registry.registrate.MultiblockMachineBuilder;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.machine.StarTMachineUtils;
import com.startechnology.start_core.machine.fusion.AuxiliaryBoostedFusionReactor;

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;
import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class StarTDreamLinkTransmissionTowers {
    public static MultiblockMachineBuilder makeDreamlinkNode(String name, Integer range, Boolean checkDimension) {
        var multiBuilder = START_REGISTRATE
            .multiblock(name, (holder) -> new StarTDreamLinkTransmissionMachine(holder, range, checkDimension))
            .tooltips(
                Component.translatable("start_core.machine.dream_link_tower.line"),
                Component.translatable("start_core.machine." + name + ".description"),
                Component.translatable("block.start_core.breaker_line"),
                Component.translatable("start_core.machine.dream_link_tower.beam_info"),
                Component.translatable("start_core.machine.dream_link_tower.beam_description"),
                Component.translatable("block.start_core.breaker_line"),
                Component.translatable("start_core.machine.dream_link_tower.node_info")
            )
            .appearanceBlock(() -> StarTMachineUtils.getKjsBlock(("superalloy_casing")))
            .recipeType(GTRecipeTypes.DUMMY_RECIPES)
            .workableCasingRenderer(KubeJS.id("block/casings/superalloy_casing"),
                StarTCore.resourceLocation("block/dreamlink/" + name), false)    
            .rotationState(RotationState.NON_Y_AXIS);

        if (range != -1)
            multiBuilder.tooltips(
                Component.translatable("start_core.machine.dream_link_tower.range_description", range)
            );
        else
            multiBuilder.tooltips(
                Component.translatable("start_core.machine.dream_link_tower." + name + ".range_description")
            );

        multiBuilder.tooltips(
            Component.literal(""),
            Component.translatable("start_core.machine.dream_link_tower.copy_description"),
            Component.translatable("block.start_core.breaker_line")
        );

        return multiBuilder;
    }

        public static final MultiblockMachineDefinition DREAM_LINK_NODE = makeDreamlinkNode("dream_link_node", 24, true)
            .pattern(definition -> FactoryBlockPattern.start()
                .aisle("####BBB####", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "###########") 
                .aisle("##BBCCCBB##", "###########", "#####D#####", "#####E#####", "#####D#####", "###########", "###########", "###########", "#####D#####", "#####E#####", "#####D#####") 
                .aisle("#BCCCFCCCB#", "#####G#####", "##D#####D##", "##E#####E##", "##D#####D##", "#####G#####", "###########", "#####G#####", "##D#####D##", "##E#####E##", "##D#####D##") 
                .aisle("#BCFCCCFCB#", "###G###G###", "###########", "###########", "###########", "###G###G###", "####HHH####", "###G###G###", "###########", "###########", "###########") 
                .aisle("BCCCCCCCCCB", "###########", "###########", "###########", "###########", "###########", "###H###H###", "###########", "###########", "###########", "###########") 
                .aisle("BCFCCBCCFCB", "##G##B##G##", "#D###B###D#", "#E###@###E#", "#D#######D#", "##G#####G##", "###H#I#H###", "##G#####G##", "#D#######D#", "#E###I###E#", "#D#######D#") 
                .aisle("BCCCCCCCCCB", "###########", "###########", "###########", "###########", "###########", "###H###H###", "###########", "###########", "###########", "###########") 
                .aisle("#BCFCCCFCB#", "###G###G###", "###########", "###########", "###########", "###G###G###", "####HHH####", "###G###G###", "###########", "###########", "###########") 
                .aisle("#BCCCFCCCB#", "#####G#####", "##D#####D##", "##E#####E##", "##D#####D##", "#####G#####", "###########", "#####G#####", "##D#####D##", "##E#####E##", "##D#####D##") 
                .aisle("##BBCCCBB##", "###########", "#####D#####", "#####E#####", "#####D#####", "###########", "###########", "###########", "#####D#####", "#####E#####", "#####D#####") 
                .aisle("####BBB####", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "###########") 
                .where("#", Predicates.any())
                .where("B", Predicates.blocks(StarTMachineUtils.getKjsBlock(("superalloy_casing")))
                    .or(Predicates.abilities(PartAbility.INPUT_LASER)))
                .where("C", Predicates.blocks(StarTMachineUtils.getKjsBlock(("enriched_naquadah_machine_casing"))))
                .where("D", Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTCEuAPI.materialManager.getMaterial("gtceu:hsla_steel"))))
                .where("E", Predicates.blocks(GCYMBlocks.CASING_NONCONDUCTING.get()))
                .where("F", Predicates.blocks(GTBlocks.FUSION_COIL.get()))
                .where("G", Predicates.blocks(GTBlocks.FUSION_GLASS.get()))
                .where("H", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.of("thermal:signalum_glass", ':')))) // thermal signalum glass
                .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                .where("I", Predicates.blocks(GTBlocks.SUPERCONDUCTING_COIL.get()))
                .build())
            .register();

        public static final MultiblockMachineDefinition ONEIRIC_RELAY = makeDreamlinkNode("oneiric_relay", 48, true)
            .pattern(definition -> FactoryBlockPattern.start()
                .aisle("####BBB####", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "###########") 
                .aisle("##BBCCCBB##", "###########", "#####D#####", "#####E#####", "#####D#####", "###########", "###########", "###########", "#####D#####", "#####E#####", "#####F#####", "#####E#####", "#####D#####", "###########", "###########") 
                .aisle("#BCCCFCCCB#", "#####G#####", "##D#####D##", "##E#####E##", "##D#####D##", "#####G#####", "###########", "#####G#####", "##D#####D##", "##E#####E##", "##F#####F##", "##E#####E##", "##D#####D##", "#####G#####", "###########") 
                .aisle("#BCFCCCFCB#", "###G###G###", "###########", "###########", "###########", "###G###G###", "####HHH####", "###G###G###", "###########", "###########", "###########", "###########", "###########", "###G###G###", "###########") 
                .aisle("BCCCCCCCCCB", "###########", "###########", "###########", "###########", "###########", "###H###H###", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "###########") 
                .aisle("BCFCCBCCFCB", "##G##B##G##", "#D###B###D#", "#E###@###E#", "#D#######D#", "##G#####G##", "###H#I#H###", "##G#####G##", "#D#######D#", "#E###J###E#", "#F###F###F#", "#E###J###E#", "#D#######D#", "##G#####G##", "#####I#####") 
                .aisle("BCCCCCCCCCB", "###########", "###########", "###########", "###########", "###########", "###H###H###", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "###########") 
                .aisle("#BCFCCCFCB#", "###G###G###", "###########", "###########", "###########", "###G###G###", "####HHH####", "###G###G###", "###########", "###########", "###########", "###########", "###########", "###G###G###", "###########") 
                .aisle("#BCCCFCCCB#", "#####G#####", "##D#####D##", "##E#####E##", "##D#####D##", "#####G#####", "###########", "#####G#####", "##D#####D##", "##E#####E##", "##F#####F##", "##E#####E##", "##D#####D##", "#####G#####", "###########") 
                .aisle("##BBCCCBB##", "###########", "#####D#####", "#####E#####", "#####D#####", "###########", "###########", "###########", "#####D#####", "#####E#####", "#####F#####", "#####E#####", "#####D#####", "###########", "###########") 
                .aisle("####BBB####", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "###########") 
                .where("#", Predicates.any())
                .where("B", Predicates.blocks(StarTMachineUtils.getKjsBlock(("superalloy_casing")))
                        .or(Predicates.abilities(PartAbility.INPUT_LASER)))
                .where("C", Predicates.blocks(StarTMachineUtils.getKjsBlock(("enriched_naquadah_machine_casing"))))
                .where("D", Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTCEuAPI.materialManager.getMaterial("gtceu:hsla_steel"))))
                .where("E", Predicates.blocks(GCYMBlocks.CASING_NONCONDUCTING.get()))
                .where("F", Predicates.blocks(GTBlocks.FUSION_COIL.get()))
                .where("G", Predicates.blocks(GTBlocks.FUSION_GLASS.get()))
                .where("H", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.of("thermal:signalum_glass", ':')))) // replace with gtceu/kubejs block if needed
                .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                .where("I", Predicates.blocks(GTBlocks.SUPERCONDUCTING_COIL.get()))
                .where("J", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.of("thermal_extra:shellite_glass", ':')))) // replace with gtceu/kubejs block if needed
                .build()
            )
            .register();

        public static final MultiblockMachineDefinition DAYDREAM_SPIRE = makeDreamlinkNode("daydream_spire", 96, true)
            .pattern(definition -> FactoryBlockPattern.start()
                .aisle("####BBB####", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "#####C#####", "#####D#####", "#####E#####", "#####D#####", "#####C#####") 
                .aisle("##BBFFFBB##", "###########", "#####C#####", "#####D#####", "#####C#####", "###########", "###########", "###########", "#####C#####", "#####D#####", "#C###E###C#", "#D#######D#", "#E#######E#", "#D#######D#", "#C#######C#") 
                .aisle("#BFFFEFFFB#", "#####G#####", "##C#####C##", "##D#####D##", "##C#####C##", "#####G#####", "###########", "#####G#####", "##C#####C##", "##D#####D##", "##E#####E##", "###########", "#####H#####", "#####E#####", "#####H#####") 
                .aisle("#BFEFFFEFB#", "###G###G###", "###########", "###########", "###########", "###G###G###", "####III####", "###G###G###", "###########", "###########", "###########", "###########", "###H###H###", "###E###E###", "###H###H###") 
                .aisle("BFFFFFFFFFB", "###########", "###########", "###########", "###########", "###########", "###I###I###", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "###########") 
                .aisle("BFEFFBFFEFB", "##G##B##G##", "#C###B###C#", "#D###@###D#", "#C#######C#", "##G#####G##", "###I#J#I###", "##G#####G##", "#C#######C#", "#D###K###D#", "CE###E###EC", "D####K####D", "E#H#####H#E", "D#E##J##E#D", "C#H#####H#C") 
                .aisle("BFFFFFFFFFB", "###########", "###########", "###########", "###########", "###########", "###I###I###", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "###########") 
                .aisle("#BFEFFFEFB#", "###G###G###", "###########", "###########", "###########", "###G###G###", "####III####", "###G###G###", "###########", "###########", "###########", "###########", "###H###H###", "###E###E###", "###H###H###") 
                .aisle("#BFFFEFFFB#", "#####G#####", "##C#####C##", "##D#####D##", "##C#####C##", "#####G#####", "###########", "#####G#####", "##C#####C##", "##D#####D##", "##E#####E##", "###########", "#####H#####", "#####E#####", "#####H#####") 
                .aisle("##BBFFFBB##", "###########", "#####C#####", "#####D#####", "#####C#####", "###########", "###########", "###########", "#####C#####", "#####D#####", "#C###E###C#", "#D#######D#", "#E#######E#", "#D#######D#", "#C#######C#") 
                .aisle("####BBB####", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "###########", "#####C#####", "#####D#####", "#####E#####", "#####D#####", "#####C#####") 
                .where("#", Predicates.any())
                .where("B", Predicates.blocks(StarTMachineUtils.getKjsBlock(("superalloy_casing")))
                        .or(Predicates.abilities(PartAbility.INPUT_LASER)))
                .where("C", Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTCEuAPI.materialManager.getMaterial("gtceu:hsla_steel"))))
                .where("D", Predicates.blocks(GCYMBlocks.CASING_NONCONDUCTING.get()))
                .where("E", Predicates.blocks(GTBlocks.FUSION_COIL.get()))
                .where("F", Predicates.blocks(StarTMachineUtils.getKjsBlock(("enriched_naquadah_machine_casing"))))
                .where("G", Predicates.blocks(GTBlocks.FUSION_GLASS.get()))
                .where("H", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.of("thermal:lumium_glass", ':')))) // replace with gtceu/kubejs block if needed
                .where("I", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.of("thermal:signalum_glass", ':')))) // replace with gtceu/kubejs block if needed
                .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                .where("J", Predicates.blocks(GTBlocks.SUPERCONDUCTING_COIL.get()))
                .where("K", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.of("thermal_extra:shellite_glass", ':')))) // replace with gtceu/kubejs block if needed
                .build()
            )
            .register();

        public static final MultiblockMachineDefinition BEACON_OF_LUCIDITY = makeDreamlinkNode("beacon_of_lucidity", -1, true)
            .pattern(definition -> FactoryBlockPattern.start()
                .aisle("CA")
                .where("C", Predicates.controller(Predicates.blocks(definition.get())))
                .where("A", Predicates.abilities(PartAbility.INPUT_LASER))
                .build()
            )
            .register();
                    
        public static final MultiblockMachineDefinition PARAGON_OF_THE_VEIL = makeDreamlinkNode("paragon_of_the_veil", -1, false)
            .pattern(definition -> FactoryBlockPattern.start()
                .aisle("CA")
                .where("C", Predicates.controller(Predicates.blocks(definition.get())))
                .where("A", Predicates.abilities(PartAbility.INPUT_LASER))
                .build()
            )
            .register();

    public static void init() {}
}
