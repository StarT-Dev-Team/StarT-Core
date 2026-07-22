package com.startechnology.start_core.block.arboreal_extractor;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.level.block.Blocks;

public class ArborealBlocks {

    public static final List<TreeDefinition> TREES = new ArrayList<>();

    public static void init() {
        TREES.add(new TreeDefinition.Simple("oak", () -> Blocks.OAK_LEAVES, () -> Blocks.OAK_LOG,
                TreeType.RESIN_PRODUCING));
        TREES.add(new TreeDefinition.Simple("dark_oak", () -> Blocks.DARK_OAK_LEAVES, () -> Blocks.DARK_OAK_LOG,
                TreeType.RESIN_PRODUCING));
        TREES.add(new TreeDefinition.Simple("jungle", () -> Blocks.JUNGLE_LEAVES, () -> Blocks.JUNGLE_LOG,
                TreeType.LATEX_PRODUCING));
        TREES.add(new TreeDefinition.Simple("azalea", () -> Blocks.AZALEA_LEAVES, () -> Blocks.OAK_LOG,
                TreeType.RESIN_PRODUCING));
        TREES.add(new TreeDefinition.Simple("acacia", () -> Blocks.ACACIA_LEAVES, () -> Blocks.ACACIA_LOG,
                TreeType.SAP_PRODUCING));
        TREES.add(new TreeDefinition.Simple("spruce", () -> Blocks.SPRUCE_LEAVES, () -> Blocks.SPRUCE_LOG,
                TreeType.SAP_PRODUCING));
        TREES.add(new TreeDefinition.Simple("birch", () -> Blocks.BIRCH_LEAVES, () -> Blocks.BIRCH_LOG,
                TreeType.SAP_PRODUCING));
        TREES.add(new TreeDefinition.Simple("mangrove", () -> Blocks.MANGROVE_LEAVES, () -> Blocks.MANGROVE_LOG,
                TreeType.SAP_PRODUCING));
        TREES.add(new TreeDefinition.Simple("cherry", () -> Blocks.CHERRY_LEAVES, () -> Blocks.CHERRY_LOG,
                TreeType.SAP_PRODUCING));
    }
}
