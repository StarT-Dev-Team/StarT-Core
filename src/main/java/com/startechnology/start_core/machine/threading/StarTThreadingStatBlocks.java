package com.startechnology.start_core.machine.threading;

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.block.ICoilType;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.api.machine.multiblock.IBatteryData;
import com.gregtechceu.gtceu.api.pattern.MultiblockState;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.error.PatternStringError;
import com.gregtechceu.gtceu.common.block.BatteryBlock;
import com.gregtechceu.gtceu.common.block.CoilBlock;
import com.gregtechceu.gtceu.common.block.FusionCasingBlock;
import com.gregtechceu.gtceu.common.data.GTModels;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.PowerSubstationMachine;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.machine.fusion.StarTFusionCasings;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;

public class StarTThreadingStatBlocks {

    public static BlockEntry<Block> createThreadingStatBlock(StarTThreadingStatsPredicate.ThreadingStatsBlockTracker stats) {
        String name = stats.name.replace(StarTThreadingStatsPredicate.THREADING_STATS_HEADER, "");
        BlockEntry<Block> block = START_REGISTRATE.block(name, Block::new)
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
                .addLayer(() -> RenderType::cutoutMipped)
                .blockstate((ctx, prov) -> {
                    String basePath = "block/threading/" + name + "/overlay";
                    String emissivePath = "block/threading/" + name + "/overlay_emissive";

                    ModelFile model = prov.models().withExistingParent(name,
                            StarTCore.resourceLocation("block/threading_emissive_base"))
                            .texture("base", StarTCore.resourceLocation(basePath))
                            .texture("emissive", StarTCore.resourceLocation(emissivePath))
                            .texture("particle", StarTCore.resourceLocation(basePath));

                    prov.simpleBlock(ctx.get(), model);
                })
                .tag(GTToolType.WRENCH.harvestTags.get(0), BlockTags.MINEABLE_WITH_PICKAXE, CustomTags.TOOL_TIERS[4])
                .item(BlockItem::new)
                .model((ctx, prov) -> {
                    String basePath = "block/threading/" + name + "/overlay";
                    prov.withExistingParent(prov.name(ctx), "block/cube_all")
                            .texture("all", StarTCore.resourceLocation(basePath));
                })
                .build()
                .register();

        StarTThreadingStatsPredicate.THREADING_STAT_BLOCKS.put(stats, block);
        return block;
    }

    public static final BlockEntry<Block> TEST_HELIX = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("test", 4, 0, 0, 0, 0));

    public static final BlockEntry<Block> OVERDRIVE_HELIX_1 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("uhv_overdrive_thread_helix", 5, 4, 15, 0, 0));

    public static final BlockEntry<Block> OVERDRIVE_HELIX_2 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("uxv_overdrive_thread_helix", 10, 8, 25, 1, 0));

    public static final BlockEntry<Block> OVERDRIVE_HELIX_3 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("max_overdrive_thread_helix", 15, 12, 40, 1, 1));

    public static final BlockEntry<Block> COPROCESSOR_HELIX_1 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("uhv_coprocessor_thread_helix", 5, 6, 4, 5, 0));

    public static final BlockEntry<Block> COPROCESSOR_HELIX_2 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("uiv_coprocessor_thread_helix", 10, 12, 8, 10, 0));

    public static final BlockEntry<Block> COPROCESSOR_HELIX_3 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("opv_coprocessor_thread_helix", 15, 18, 12, 16, 0));

    public static final BlockEntry<Block> WEAVER_HELIX_1 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("uhv_weaving_thread_helix", 5, 8, 2, 2, 5));

    public static final BlockEntry<Block> WEAVER_HELIX_2 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("uiv_weaving_thread_helix", 10, 12, 4, 3, 8));

    public static final BlockEntry<Block> WEAVER_HELIX_3 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("opv_weaving_thread_helix", 15, 16, 6, 4, 12));

    public static final BlockEntry<Block> SUPREME_HELIX_1 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("uev_supreme_thread_helix", 30, 0, 0, 0, 0));

    public static final BlockEntry<Block> SUPREME_HELIX_2 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("uxv_supreme_thread_helix", 50, 0, 0, 0, 0));

    public static final BlockEntry<Block> SUPREME_HELIX_3 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("max_supreme_thread_helix", 80, 0, 0, 0, 0));


    public static void init() {
    }

}
