package com.startechnology.start_core.block.arboreal_extractor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;

public class ArborealBlocks {
    
    public static final Map<LeavesType, Supplier<LeavesBlock>> LEAVES = new HashMap<>();
    public static final Map<LogType, Supplier<Block>> LOGS = new HashMap<>();

}
