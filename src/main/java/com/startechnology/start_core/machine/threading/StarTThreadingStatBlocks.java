package com.startechnology.start_core.machine.threading;

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

import java.util.ArrayList;
import java.util.List;

import com.gregtechceu.gtceu.api.block.ActiveBlock;
import com.gregtechceu.gtceu.api.block.property.GTBlockStateProperties;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.startechnology.start_core.StarTCore;
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
                .partialState().with(GTBlockStateProperties.ACTIVE, false)
                    .modelForState().modelFile(inactiveModel).addModel()
                .partialState().with(GTBlockStateProperties.ACTIVE, true)
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
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("uev_supreme_thread_helix", 20, 0, 0, 0, 0));

    public static final BlockEntry<StarTThreadingStatBlock> SUPREME_HELIX_1 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("uxv_supreme_thread_helix", 40, 0, 0, 0, 0));

    public static final BlockEntry<StarTThreadingStatBlock> SUPREME_HELIX_2 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("max_supreme_thread_helix", 60, 0, 0, 0, 0));

    public static final BlockEntry<StarTThreadingStatBlock> OVERDRIVE_HELIX_0 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("uhv_overdrive_thread_helix", 3, 12, 4, 0, 0)); //Sum 20

    public static final BlockEntry<StarTThreadingStatBlock> OVERDRIVE_HELIX_1 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("uiv_overdrive_thread_helix", 6, 18, 6, 0, 0)); //Sum 30

    public static final BlockEntry<StarTThreadingStatBlock> OVERDRIVE_HELIX_2 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("opv_overdrive_thread_helix", 9, 33, 8, 0, 0)); //Sum 50

    public static final BlockEntry<StarTThreadingStatBlock> COPROCESSOR_HELIX_0 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("uhv_coprocessor_thread_helix", 3, 5, 2, 10, 0)); //Sum 20

    public static final BlockEntry<StarTThreadingStatBlock> COPROCESSOR_HELIX_1 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("uiv_coprocessor_thread_helix", 6, 5, 4, 15, 0)); //Sum 30

    public static final BlockEntry<StarTThreadingStatBlock> COPROCESSOR_HELIX_2 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("opv_coprocessor_thread_helix", 9, 10, 6, 25, 0)); //Sum 50

    public static final BlockEntry<StarTThreadingStatBlock> WEAVER_HELIX_0 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("uhv_weaving_thread_helix", 3, 2, 5, 0, 10)); //Sum 20

    public static final BlockEntry<StarTThreadingStatBlock> WEAVER_HELIX_1 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("uiv_weaving_thread_helix", 6, 4, 5, 0, 15)); //Sum 30

    public static final BlockEntry<StarTThreadingStatBlock> WEAVER_HELIX_2 = createThreadingStatBlock(
            new StarTThreadingStatsPredicate.ThreadingStatsBlockTracker("opv_weaving_thread_helix", 9, 6, 10, 0, 25)); //Sum 50

    public static void init() {
    }

}
