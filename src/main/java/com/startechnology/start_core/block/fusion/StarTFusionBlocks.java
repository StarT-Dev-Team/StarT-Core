package com.startechnology.start_core.block.fusion;

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

import com.gregtechceu.gtceu.api.block.ActiveBlock;
import com.gregtechceu.gtceu.api.block.IFusionCasingType;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.common.block.FusionCasingBlock;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.machine.fusion.StarTFusionCasings;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.client.model.generators.ModelFile;

public class StarTFusionBlocks {
    public static NonNullBiConsumer<DataGenContext<Block, FusionCasingBlock>, RegistrateBlockstateProvider> createFusionCasingModel(String name,
                                                                                                                                    IFusionCasingType casingType) {
        return (ctx, prov) -> {
            ActiveBlock block = ctx.getEntry();
            ModelFile inactive = prov.models().cubeAll(name, casingType.getTexture());
            ModelFile active = prov.models().withExistingParent(name + "_active", StarTCore.resourceLocation("block/cube_2_layer/all"))
                    .texture("bot_all", casingType.getTexture())
                    .texture("top_all", new ResourceLocation(casingType.getTexture() + "_bloom"));
            prov.getVariantBuilder(block)
                    .partialState().with(ActiveBlock.ACTIVE, false).modelForState().modelFile(inactive).addModel()
                    .partialState().with(ActiveBlock.ACTIVE, true).modelForState().modelFile(active).addModel();
        };
    }

    private static BlockEntry<FusionCasingBlock> createFusionCasing(IFusionCasingType casingType) {
        BlockEntry<FusionCasingBlock> casingBlock = START_REGISTRATE
                .block(casingType.getSerializedName(), p -> new FusionCasingBlock(p, casingType))
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(properties -> properties.strength(5.0f, 10.0f).sound(SoundType.METAL))
                .addLayer(() -> RenderType::cutoutMipped)
                .blockstate(createFusionCasingModel(casingType.getSerializedName(), casingType))
                .tag(GTToolType.WRENCH.harvestTags.get(0), CustomTags.TOOL_TIERS[casingType.getHarvestLevel()])
                .item(BlockItem::new)
                .build()
                .register();
        GTBlocks.ALL_FUSION_CASINGS.put(casingType, casingBlock);
        return casingBlock;
    }

    public static final BlockEntry<FusionCasingBlock> AUXILIARY_BOOSTED_FUSION_CASING_MK1 = createFusionCasing(
        StarTFusionCasings.AUXILIARY_BOOSTED_FUSION_CASING_MK1);

    public static final BlockEntry<FusionCasingBlock> AUXILIARY_FUSION_COIL_MK1 = createFusionCasing(
        StarTFusionCasings.AUXILIARY_FUSION_COIL_MK1);

    public static final BlockEntry<FusionCasingBlock> FUSION_CASING_MK4 = createFusionCasing(
        StarTFusionCasings.FUSION_CASING_MK4);

    public static final BlockEntry<FusionCasingBlock> ADVANCED_FUSION_COIL = createFusionCasing(
        StarTFusionCasings.ADVANCED_FUSION_COIL);

    public static void init() {}
}
