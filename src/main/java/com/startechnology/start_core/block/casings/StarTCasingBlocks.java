package com.startechnology.start_core.block.casings;

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.block.ActiveBlock;
import com.gregtechceu.gtceu.api.block.property.GTBlockStateProperties;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.common.block.BoilerFireboxType;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.models.GTModels;
import com.startechnology.start_core.StarTCore;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;

import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.generators.ModelFile;

public class StarTCasingBlocks {

        /// Firebox casings
    
        private static NonNullBiConsumer<DataGenContext<Block, ActiveBlock>, RegistrateBlockstateProvider> createFireboxModel(String name,
            ResourceLocation sideTexture,
            ResourceLocation topBottomTexture
        ) {
        return (ctx, prov) -> {
            ActiveBlock block = ctx.getEntry();
            ModelFile inactive = prov.models().cubeBottomTop(name, sideTexture, topBottomTexture, topBottomTexture);
            ModelFile active = prov.models().withExistingParent(name + "_active", StarTCore.resourceLocation("block/fire_box_active"))
                    .texture("side", sideTexture)
                    .texture("bottom", topBottomTexture)
                    .texture("top", topBottomTexture);
            prov.getVariantBuilder(block)
                    .partialState().with(GTBlockStateProperties.ACTIVE, false).modelForState().modelFile(inactive).addModel()
                    .partialState().with(GTBlockStateProperties.ACTIVE, true).modelForState().modelFile(active).addModel();
        };
    }
        private static BlockEntry<ActiveBlock> createFireboxCasing(String name,
            ResourceLocation sideTexture,
            ResourceLocation topBottomTexture
        ) {
            BlockEntry<ActiveBlock> block = StarTCore.START_REGISTRATE
                    .block("%s_casing".formatted(name), ActiveBlock::new)
                    .initialProperties(() -> Blocks.IRON_BLOCK)
                    .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
                    .addLayer(() -> RenderType::cutoutMipped)
                    .blockstate(createFireboxModel("%s_casing".formatted(name), sideTexture, topBottomTexture))
                    .tag(GTToolType.WRENCH.harvestTags.get(0), BlockTags.MINEABLE_WITH_PICKAXE)
                    .item(BlockItem::new)
                    .build()
                    .register();
        return block;
    }

    // Active casings 

    protected static BlockEntry<ActiveBlock> createActiveCasing(String name, String baseModelPath) {
        return START_REGISTRATE.block(name, ActiveBlock::new)
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .addLayer(() -> RenderType::cutoutMipped)
                .blockstate(GTModels.createActiveModel(StarTCore.resourceLocation(baseModelPath)))
                .tag(GTToolType.WRENCH.harvestTags.get(0), BlockTags.MINEABLE_WITH_PICKAXE)
                .item(BlockItem::new)
                .model((ctx, prov) -> prov.withExistingParent(prov.name(ctx), StarTCore.resourceLocation(baseModelPath)))
                .build()
                .register();
    }


    public static final BlockEntry<ActiveBlock> FIREBOX_ENRICHED_NAQUADAH = createFireboxCasing(
        "enriched_naquadah_firebox",
            StarTCore.resourceLocation("block/casings/naquadah/firebox_casing"),
            StarTCore.resourceLocation("block/casings/naquadah/casing")    
        );


    public static final BlockEntry<ActiveBlock> ENGINE_INTAKE_ENRICHED_NAQUADAH = createActiveCasing("enriched_naquadah_engine_intake_casing",
        "block/variant/enriched_naquadah_engine_intake");

    public static final void init() {}
}
