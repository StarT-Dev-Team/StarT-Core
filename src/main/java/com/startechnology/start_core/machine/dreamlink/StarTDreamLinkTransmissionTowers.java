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

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;
import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class StarTDreamLinkTransmissionTowers {
    public static MultiblockMachineBuilder makeDreamlinkNode(String name, Integer range, Integer connections, Boolean checkDimension) {
        var multiBuilder = START_REGISTRATE
            .multiblock(name, (holder) -> new StarTDreamLinkTransmissionMachine(holder, range, connections, checkDimension))
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
            .workableCasingModel(KubeJS.id("block/casings/abydos_multis/superalloy_casing"),
                StarTCore.resourceLocation("block/dreamlink/" + name))
            .rotationState(RotationState.NON_Y_AXIS);

        if (range != -1)
            multiBuilder.tooltips(
                Component.translatable("start_core.machine.dream_link_tower.range_description", range)
            );
        else
            multiBuilder.tooltips(
                Component.translatable("start_core.machine.dream_link_tower." + name + ".range_description")
            );

        if (connections != -1)
            multiBuilder.tooltips(
                Component.translatable("start_core.machine.dream_link_tower.connections_description", connections)
            );
        else
            multiBuilder.tooltips(
                Component.translatable("start_core.machine.dream_link_tower.infinite_connections_description")
            );    

        multiBuilder.tooltips(
            Component.literal(""),
            Component.translatable("start_core.machine.dream_link_tower.copy_description"),
            Component.translatable("block.start_core.breaker_line")
        );

        return multiBuilder;
    }

        public static final MultiblockMachineDefinition DREAM_LINK_NODE = makeDreamlinkNode("dream_link_node", 16, 16, true)
            .pattern(definition -> FactoryBlockPattern.start()
                .aisle("         ", "         ", "         ", "     B   ", "    B    ", "   B     ", "         ", "         ", "         ", "         ", "         ")
                .aisle("   CCC   ", "  B      ", "      B  ", "         ", "         ", "         ", "  BCCC   ", "      B  ", "         ", "         ", "         ")
                .aisle("  DEEED  ", "  D   DB ", " BD   D  ", "  D   D  ", "  D   D  ", "  D   D  ", "  DEEEDB ", " BD E D  ", "  D   D  ", "    F    ", "  F   F  ")
                .aisle(" CEEEEEC ", "   E E   ", "   E E   ", "B  E E   ", "   E E   ", "   E E  B", " CEEEEEC ", "         ", "         ", "         ", "         ")
                .aisle(" CEEEEEC ", "    F    ", "    F    ", "    @    ", "B   F   B", "    F    ", " CEEFEEC ", "  E   E  ", "    F    ", "  F   F  ", "         ")
                .aisle(" CEEEEEC ", "   E E   ", "   E E   ", "   E E  B", "   E E   ", "B  E E   ", " CEEEEEC ", "         ", "         ", "         ", "         ")
                .aisle("  DEEED  ", " BD   D  ", "  D   DB ", "  D   D  ", "  D   D  ", "  D   D  ", " BDEEED  ", "  D E DB ", "  D   D  ", "    F    ", "  F   F  ")
                .aisle("   CCC   ", "      B  ", "  B      ", "         ", "         ", "         ", "   CCCB  ", "  B      ", "         ", "         ", "         ")
                .aisle("         ", "         ", "         ", "   B     ", "    B    ", "     B   ", "         ", "         ", "         ", "         ", "         ")
                .where(" ", Predicates.any())
                .where("B", Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTCEuAPI.materialManager.getMaterial("gtceu:europium"))))
                .where("C", Predicates.blocks(GTBlocks.COMPUTER_HEAT_VENT.get()))
                .where("D", Predicates.blocks(GTBlocks.HIGH_POWER_CASING.get()))
                .where("E", Predicates.blocks(StarTMachineUtils.getKjsBlock(("superalloy_casing")))
                    .or(Predicates.abilities(PartAbility.INPUT_LASER)))
                .where("F", Predicates.blocks(GTBlocks.SUPERCONDUCTING_COIL.get()))
                .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                .build())
            .register();

        public static final MultiblockMachineDefinition ONEIRIC_RELAY = makeDreamlinkNode("oneiric_relay", 32, 64, true)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("         ", "         ", "         ", "     B   ", "    B    ", "   B     ", "         ", "         ", "     B   ", "    B    ", "   B     ", "         ", "         ", "         ", "         ", "         ")
                    .aisle("   CCC   ", "  B      ", "      B  ", "         ", "         ", "         ", "  BCCC   ", "      B  ", "         ", "         ", "         ", "  B      ", "   CCCB  ", "         ", "    D    ", " D     D ")
                    .aisle("  EFFFE  ", "  E   EB ", " BE   E  ", "  E   E  ", "  E   E  ", "  E   E  ", "  EFFFEB ", " BE F E  ", "  E F E  ", "    D    ", "  D F D  ", "    F  B ", " BFFFFF  ", "  F F F  ", "  F   F  ", "         ")
                    .aisle(" CFFFFFC ", "   F F   ", "   F F   ", "B  F F   ", "   F F   ", "   F F  B", " CFFFFFC ", "         ", "B        ", "         ", "        B", "         ", " CFDDDFC ", "         ", "         ", "         ")
                    .aisle(" CFFFFFC ", "    D    ", "    D    ", "    @    ", "B   D   B", "    D    ", " CFFDFFC ", "  F   F  ", "  F   F  ", "B D   D B", "  F   F  ", "  F   F  ", " CFDDDFC ", "  F   F  ", " D     D ", "         ")
                    .aisle(" CFFFFFC ", "   F F   ", "   F F   ", "   F F  B", "   F F   ", "B  F F   ", " CFFFFFC ", "         ", "        B", "         ", "B        ", "         ", " CFDDDFC ", "         ", "         ", "         ")
                    .aisle("  EFFFE  ", " BE   E  ", "  E   EB ", "  E   E  ", "  E   E  ", "  E   E  ", " BEFFFE  ", "  E F EB ", "  E F E  ", "    D    ", "  D F D  ", " B  F    ", "  FFFFFB ", "  F F F  ", "  F   F  ", "         ")
                    .aisle("   CCC   ", "      B  ", "  B      ", "         ", "         ", "         ", "   CCCB  ", "  B      ", "         ", "         ", "         ", "      B  ", "  BCCC   ", "         ", "    D    ", " D     D ")
                    .aisle("         ", "         ", "         ", "   B     ", "    B    ", "     B   ", "         ", "         ", "   B     ", "    B    ", "     B   ", "         ", "         ", "         ", "         ", "         ")
                    .where(" ", Predicates.any())
                .where("B", Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTCEuAPI.materialManager.getMaterial("gtceu:europium"))))
                .where("C", Predicates.blocks(GTBlocks.COMPUTER_HEAT_VENT.get()))
                .where("D", Predicates.blocks(GTBlocks.SUPERCONDUCTING_COIL.get()))
                .where("E", Predicates.blocks(GTBlocks.HIGH_POWER_CASING.get()))
                .where("F", Predicates.blocks(StarTMachineUtils.getKjsBlock("superalloy_casing") )
                    .or(Predicates.abilities(PartAbility.INPUT_LASER)))
                .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                .build())
            .register();

        public static final MultiblockMachineDefinition DAYDREAM_SPIRE = makeDreamlinkNode("daydream_spire", 64, -1, true)
            .pattern(definition -> FactoryBlockPattern.start()
                .aisle("                         ", "                         ", "                         ", "                         ", "                         ", "                         ", "                         ", "                         ", "                         ", "                         ", "          BBBBB          ", "                         ", "                         ", "                         ", "                         ", "                         ", "                         ")
                .aisle("                         ", "                         ", "                         ", "                         ", "                         ", "                         ", "                         ", "                         ", "                         ", "                         ", "        BB     BB        ", "                         ", "                         ", "                         ", "                         ", "            B            ", "         B     B         ")
                .aisle("                         ", "                         ", "                         ", "             C           ", "            C            ", "           C             ", "                         ", "                         ", "             C           ", "            C            ", "       B   C     B       ", "                         ", "                         ", "                         ", "                         ", "                         ", "                         ")
                .aisle("           DDD           ", "          C              ", "    C         C       C  ", "   C                 C   ", "  C                 C    ", "                         ", "    C     CDDD        C  ", "   C          C      C   ", "                         ", "                         ", "       B         B       ", "          C              ", "           DDDC          ", "                         ", "            B            ", "         B     B         ", "       B         B       ")
                .aisle("  DDD     EFFFE     DDD  ", " C   C    E   EC   C   C ", "         CE   E          ", "          E   E          ", "          E   E          ", " C   C    E   E    C   C ", "          EFFFEC         ", "         CE F E          ", "  BBB     E F E     BBB  ", "            B            ", "      B   B F B   B      ", "            F  C         ", "   B     CFFFFF      B   ", " B   B    F F F    B   B ", "          F   F          ", "                         ", "                         ")
                .aisle(" DFFFD   DFFFFFD   DFFFD ", "   F       F F       F   ", "C  F       F F    C  F   ", "  FFF   C  F F      FFF  ", "   F  C    F F       F  C", "   F       F F  C    F   ", "C        DFFFFFD  C      ", "                         ", " B   B  C          B   B ", "                         ", "      B         C B      ", "                         ", "         DFBBBFD         ", "                         ", "                         ", "                         ", "                         ")
                .aisle(" DFFFDDDDDFFFFFDDDDDFFFD ", "  FEF       B       FEF  ", "  FEF       B       FEF  ", "C FEF C     @     C FEF C", "  FEF   C   B   C   FEF  ", "  FEF       B       FEF  ", "   E     DFFBFFD     E   ", "C  E  C   F   F   C  E  C", " B E B    F   F    B E B ", "   E    C B   B C    E   ", "      B   F   F   B      ", "   B      F   F      B   ", " B   B   DFBBBFD   B   B ", "          F   F          ", "         B     B         ", "       B         B       ", "                         ")
                .aisle(" DFFFD   DFFFFFD   DFFFD ", "   F       F F       F   ", "   F  C    F F       F  C", "  FFF      F F  C   FFF  ", "C  F       F F    C  F   ", "   F    C  F F       F   ", "      C  DFFFFFD        C", "                         ", " B   B          C  B   B ", "                         ", "      B C         B      ", "                         ", "         DFBBBFD         ", "                         ", "                         ", "                         ", "                         ")
                .aisle("  DDD     EFFFE     DDD  ", " C   C   CE   E    C   C ", "          E   EC         ", "          E   E          ", "          E   E          ", " C   C    E   E    C   C ", "         CEFFFE          ", "          E F EC         ", "  BBB     E F E     BBB  ", "            B            ", "      B   B F B   B      ", "         C  F            ", "   B      FFFFFC     B   ", " B   B    F F F    B   B ", "          F   F          ", "                         ", "                         ")
                .aisle("           DDD           ", "              C          ", "  C       C         C    ", "   C                 C   ", "    C                 C  ", "                         ", "  C        DDDC     C    ", "   C      C          C   ", "                         ", "                         ", "       B         B       ", "              C          ", "          CDDD           ", "                         ", "            B            ", "         B     B         ", "       B         B       ")
                .aisle("                         ", "                         ", "                         ", "           C             ", "            C            ", "             C           ", "                         ", "                         ", "           C             ", "            C            ", "       B     C   B       ", "                         ", "                         ", "                         ", "                         ", "                         ", "                         ")
                .aisle("                         ", "                         ", "                         ", "                         ", "                         ", "                         ", "                         ", "                         ", "                         ", "                         ", "        BB     BB        ", "                         ", "                         ", "                         ", "                         ", "            B            ", "         B     B         ")
                .aisle("                         ", "                         ", "                         ", "                         ", "                         ", "                         ", "                         ", "                         ", "                         ", "                         ", "          BBBBB          ", "                         ", "                         ", "                         ", "                         ", "                         ", "                         ")
                .where(" ", Predicates.any())
                .where("B", Predicates.blocks(GTBlocks.SUPERCONDUCTING_COIL.get()))
                .where("C", Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTCEuAPI.materialManager.getMaterial( "gtceu:europium"))))
                .where("D", Predicates.blocks(GTBlocks.COMPUTER_HEAT_VENT.get()))
                .where("E", Predicates.blocks(GTBlocks.HIGH_POWER_CASING.get()))
                .where("F", Predicates.blocks(StarTMachineUtils.getKjsBlock("superalloy_casing"))
                    .or(Predicates.abilities(PartAbility.INPUT_LASER)))
                .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                .build())
            .register();

        public static final MultiblockMachineDefinition BEACON_OF_LUCIDITY = makeDreamlinkNode("beacon_of_lucidity", -1, -1, true)
            .pattern(definition -> FactoryBlockPattern.start()
                .aisle("CA")
                .where("C", Predicates.controller(Predicates.blocks(definition.get())))
                .where("A", Predicates.abilities(PartAbility.INPUT_LASER))
                .build()
            )
            .register();
                    
        public static final MultiblockMachineDefinition PARAGON_OF_THE_VEIL = makeDreamlinkNode("paragon_of_the_veil", -1, -1, false)
            .pattern(definition -> FactoryBlockPattern.start()
                .aisle("CA")
                .where("C", Predicates.controller(Predicates.blocks(definition.get())))
                .where("A", Predicates.abilities(PartAbility.INPUT_LASER))
                .build()
            )
            .register();

    public static void init() {}
}
