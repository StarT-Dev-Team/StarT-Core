package com.startechnology.start_core.machine.threading;

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.block.ActiveBlock;
import com.gregtechceu.gtceu.api.block.ICoilType;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.utils.FormattingUtil;
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
import com.startechnology.start_core.machine.threading.StarTThreadingStatsPredicate.ThreadingStatsBlockTracker;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;

public class StarTThreadingStatBlocks {

public static List<BlockEntry<StarTThreadingStatBlock>> statBlocks = new ArrayList<>();
    public static List<String> statList = List.of("general", "speed", "efficiency", "parallels", "threading");

    public static class StarTThreadingStatBlock extends ActiveBlock {
        @Getter
        @Setter
        private ThreadingStatsBlockTracker threadingStats;

        public StarTThreadingStatBlock(Properties properties, ThreadingStatsBlockTracker threadingStats) {
            super(properties);
            this.threadingStats = threadingStats;
        }
    }

    public static NonNullBiConsumer<DataGenContext<Block, StarTThreadingStatBlock>, RegistrateBlockstateProvider> createActiveModel(String blockName) {
        return (ctx, prov) -> {
            ActiveBlock block = ctx.getEntry();
            String modelName = ctx.getName();
            
            ResourceLocation textureBase = StarTCore.resourceLocation("block/threading/" + blockName + "/thread");
            
            var inactiveModel = prov.models().cubeAll(
                modelName,
                textureBase
            );
            
            var activeModel = prov.models().withExistingParent(
                modelName + "_active",
                new ResourceLocation("block/cube_all")
            )
            .texture("all", StarTCore.resourceLocation("block/threading/" + blockName + "/thread_active"))
            .texture("particle", StarTCore.resourceLocation("block/threading/" + blockName + "/thread_active"));
            
            prov.getVariantBuilder(block)
                .partialState().with(ActiveBlock.ACTIVE, false)
                    .modelForState().modelFile(inactiveModel).addModel()
                .partialState().with(ActiveBlock.ACTIVE, true)
                    .modelForState().modelFile(activeModel).addModel();
        };
    }

    public static BlockEntry<StarTThreadingStatBlock> createThreadingStatBlock(StarTThreadingStatsPredicate.ThreadingStatsBlockTracker stats) {
        String name = stats.name.replace(StarTThreadingStatsPredicate.THREADING_STATS_HEADER, "");
        
        BlockEntry<StarTThreadingStatBlock> block = START_REGISTRATE
            .block(name, (props) -> new StarTThreadingStatBlock(props, stats))
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .addLayer(() -> RenderType::cutoutMipped)
            .blockstate(createActiveModel(name))
            .tag(GTToolType.WRENCH.harvestTags.get(0), BlockTags.MINEABLE_WITH_PICKAXE, CustomTags.TOOL_TIERS[4])
            .item((blockA, props) -> new BlockItem(blockA, props) {
                @Override
                public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents,
                                          TooltipFlag isAdvanced) {
                    super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
                    tooltipComponents.add(1, Component.translatable("block.start_core.helix_tooltip_title"));
                    if (stack.getItem() instanceof BlockItem blockItem) {
                        if (blockItem.getBlock() instanceof StarTThreadingStatBlock statBlock) {
                            ThreadingStatsBlockTracker stats = statBlock.getThreadingStats();
                            statList.forEach(stat -> {
                                ChatFormatting color = switch (stat) {
                                    case "speed" -> ChatFormatting.GREEN;           // §a
                                    case "efficiency" -> ChatFormatting.LIGHT_PURPLE; // §d
                                    case "parallels" -> ChatFormatting.RED;          // §c
                                    case "threading" -> ChatFormatting.BLUE;         // §9
                                    default -> ChatFormatting.WHITE;                 // §f
                                };
                                tooltipComponents.add(Component.translatable(
                                    "block.start_core.stat." + stat + ".display",
                                    Component.translatable("start_core.machine.threading.stat." + stat),
                                    Component.literal(FormattingUtil.formatNumbers(stats.getStatString(stat))).withStyle(color)
                                ));
                            });
                        }
                    }
                }
            })
            .model((ctx, prov) -> {
                prov.withExistingParent(prov.name(ctx), 
                    StarTCore.resourceLocation("block/" + name));
            })
            .build()
            .register();

        statBlocks.add(block);
        return block;
    }

    public static final BlockEntry<StarTThreadingStatBlock> SUPREME_HELIX_0 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("uv_supreme_thread_helix", 15, 0, 0, 0, 0));    
            
    public static final BlockEntry<StarTThreadingStatBlock> SUPREME_HELIX_1 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("uev_supreme_thread_helix", 30, 0, 0, 0, 0));

    public static final BlockEntry<StarTThreadingStatBlock> SUPREME_HELIX_2 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("uxv_supreme_thread_helix", 60, 0, 0, 0, 0));

    public static final BlockEntry<StarTThreadingStatBlock> SUPREME_HELIX_3 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("max_supreme_thread_helix", 90, 0, 0, 0, 0));

    public static final BlockEntry<StarTThreadingStatBlock> OVERDRIVE_HELIX_1 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("uhv_overdrive_thread_helix", 6, 15, 4, 0, 0)); //Sum 25

    public static final BlockEntry<StarTThreadingStatBlock> OVERDRIVE_HELIX_2 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("uxv_overdrive_thread_helix", 12, 30, 8, 0, 0)); //Sum 50

    public static final BlockEntry<StarTThreadingStatBlock> OVERDRIVE_HELIX_3 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("max_overdrive_thread_helix", 18, 45, 12, 0, 0)); //Sum 75

    public static final BlockEntry<StarTThreadingStatBlock> COPROCESSOR_HELIX_1 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("uhv_coprocessor_thread_helix", 5, 8, 2, 10, 0)); //Sum 25

    public static final BlockEntry<StarTThreadingStatBlock> COPROCESSOR_HELIX_2 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("uiv_coprocessor_thread_helix", 10, 16, 4, 20, 0)); //Sum 50

    public static final BlockEntry<StarTThreadingStatBlock> COPROCESSOR_HELIX_3 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("opv_coprocessor_thread_helix", 15, 24, 6, 30, 0)); //Sum 75

    public static final BlockEntry<StarTThreadingStatBlock> WEAVER_HELIX_1 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("uhv_weaving_thread_helix", 5, 2, 8, 0, 10)); //Sum 25

    public static final BlockEntry<StarTThreadingStatBlock> WEAVER_HELIX_2 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("uiv_weaving_thread_helix", 10, 4, 16, 0, 20)); //Sum 50

    public static final BlockEntry<StarTThreadingStatBlock> WEAVER_HELIX_3 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("opv_weaving_thread_helix", 15, 6, 24, 0, 30)); //Sum 75

    public static final BlockEntry<StarTThreadingStatBlock> TEST_HELIX = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("test", 200, 0, 0, 0, 0));

    public static void init() {
    }

}
