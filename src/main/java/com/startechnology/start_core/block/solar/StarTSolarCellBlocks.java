package com.startechnology.start_core.block.solar;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.machine.solar.StarTSolarCell;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Locale;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

public class StarTSolarCellBlocks {
    public static NonNullBiConsumer<DataGenContext<Block, StarTSolarCell>, RegistrateBlockstateProvider> createSolarCellModel(String tierName) {
        return(ctx, prov) -> prov.simpleBlock(ctx.getEntry(), prov.models().cubeAll("%s_solar_cell".formatted(tierName), StarTCore.resourceLocation("block/casings/solar_cell/%s".formatted(tierName))));
    }

    public static BlockEntry<StarTSolarCell> createSolarCellBlock(int tier) {
        String tierName = GTValues.VN[tier].toLowerCase(Locale.ROOT);

        return START_REGISTRATE
                .block("%s_solar_cell".formatted(tierName), StarTSolarCell::new)
                .lang("%s Solar Cell".formatted(GTValues.VN[tier]))
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
                .blockstate(createSolarCellModel(tierName))
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .item(BlockItem::new)
                .build()
                .register();
    }

    public static final BlockEntry<StarTSolarCell> EV_SOLAR_CELL = createSolarCellBlock(EV);
    public static final BlockEntry<StarTSolarCell> IV_SOLAR_CELL = createSolarCellBlock(IV);
    public static final BlockEntry<StarTSolarCell> LUV_SOLAR_CELL = createSolarCellBlock(LuV);
    public static final BlockEntry<StarTSolarCell> UV_SOLAR_CELL = createSolarCellBlock(UV);
    public static final BlockEntry<StarTSolarCell> UHV_SOLAR_CELL = createSolarCellBlock(UHV);

    public static void init() {}
}
