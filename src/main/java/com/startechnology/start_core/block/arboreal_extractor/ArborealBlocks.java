package com.startechnology.start_core.block.arboreal_extractor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class ArborealBlocks {

    public static final Map<LeavesType, Supplier<Block>> LEAVES = new HashMap<>();
    public static final Map<LogType, Supplier<Block>> LOGS = new HashMap<>();

    public void init() {
        LEAVES.put(LeavesTypes.RESIN_PRODUCING, () -> Blocks.OAK_LEAVES);
        LEAVES.put(LeavesTypes.RESIN_PRODUCING, () -> Blocks.DARK_OAK_LEAVES);
        LEAVES.put(LeavesTypes.LATEX_PRODUCING, () -> Blocks.JUNGLE_LEAVES);
        LEAVES.put(LeavesTypes.RESIN_PRODUCING, () -> Blocks.AZALEA_LEAVES); // azalea uses oak logs
        LEAVES.put(LeavesTypes.SAP_PRODUCING, () -> Blocks.ACACIA_LEAVES);
        LEAVES.put(LeavesTypes.SAP_PRODUCING, () -> Blocks.SPRUCE_LEAVES);
        LEAVES.put(LeavesTypes.SAP_PRODUCING, () -> Blocks.BIRCH_LEAVES);
        LEAVES.put(LeavesTypes.SAP_PRODUCING, () -> Blocks.MANGROVE_LEAVES);
        LEAVES.put(LeavesTypes.SAP_PRODUCING, () -> Blocks.CHERRY_LEAVES);

        LOGS.put(LogTypes.RESIN_PRODUCING, () -> Blocks.OAK_LOG);
        LOGS.put(LogTypes.RESIN_PRODUCING, () -> Blocks.DARK_OAK_LOG);
        LOGS.put(LogTypes.LATEX_PRODUCING, () -> Blocks.JUNGLE_LOG);
        LOGS.put(LogTypes.SAP_PRODUCING, () -> Blocks.ACACIA_LOG);
        LOGS.put(LogTypes.SAP_PRODUCING, () -> Blocks.SPRUCE_LOG);
        LOGS.put(LogTypes.SAP_PRODUCING, () -> Blocks.BIRCH_LOG);
        LOGS.put(LogTypes.SAP_PRODUCING, () -> Blocks.MANGROVE_LOG);
        LOGS.put(LogTypes.SAP_PRODUCING, () -> Blocks.CHERRY_LOG);
    }
}
