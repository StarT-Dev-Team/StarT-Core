package com.startechnology.start_core.machine.dreamlink;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.registry.registrate.MultiblockMachineBuilder;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.machine.fusion.AuxiliaryBoostedFusionReactor;

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;
import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.network.chat.Component;

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
                Component.literal(""),
                Component.translatable("start_core.machine.dream_link_tower.node_info")
            )
            .recipeType(GTRecipeTypes.DUMMY_RECIPES)
            .workableCasingRenderer(KubeJS.id("block/casings/machine_casing_peek"),
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
                .aisle("CA")
                .where("C", Predicates.controller(Predicates.blocks(definition.get())))
                .where("A", Predicates.abilities(PartAbility.INPUT_LASER))
                .build()
            )
            .register();

        public static final MultiblockMachineDefinition ONEIRIC_RELAY = makeDreamlinkNode("oneiric_relay", 48, true)
            .pattern(definition -> FactoryBlockPattern.start()
                .aisle("CA")
                .where("C", Predicates.controller(Predicates.blocks(definition.get())))
                .where("A", Predicates.abilities(PartAbility.INPUT_LASER))
                .build()
            )
            .register();

        public static final MultiblockMachineDefinition DAYDREAM_SPIRE = makeDreamlinkNode("daydream_spire", 96, true)
            .pattern(definition -> FactoryBlockPattern.start()
                .aisle("CA")
                .where("C", Predicates.controller(Predicates.blocks(definition.get())))
                .where("A", Predicates.abilities(PartAbility.INPUT_LASER))
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
