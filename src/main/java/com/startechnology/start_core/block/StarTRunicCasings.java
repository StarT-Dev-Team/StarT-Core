package com.startechnology.start_core.block;

import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.startechnology.start_core.StarTCore;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

public class StarTRunicCasings {

    public static final BlockEntry<Block> RUNIC_COMPUTER_CASING = START_REGISTRATE
            .block("runic_computer_casing", Block::new)
            .lang("Runic Computer Casing")
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
            .blockstate((ctx, prov) -> {
                prov.simpleBlock(ctx.getEntry(),
                        prov.models().getExistingFile(StarTCore.resourceLocation("block/runic_computer_casing")));
            })
            .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
            .item(BlockItem::new)
            .build()
            .register();

    public static final BlockEntry<Block> RUNIC_HIGH_POWER_CASING = START_REGISTRATE
            .block("runic_high_power_casing", Block::new)
            .lang("Runic High Power Casing")
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
            .addLayer(() -> RenderType::solid)
            .exBlockstate((ctx, prov) -> {
                prov.simpleBlock(ctx.getEntry(),
                        prov.models().cubeAll(ctx.getName(),
                                StarTCore.resourceLocation("block/casings/hpca/runic_power_casing")));
            })
            .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
            .item(BlockItem::new)
            .build()
            .register();

    public static void init() {}
}
