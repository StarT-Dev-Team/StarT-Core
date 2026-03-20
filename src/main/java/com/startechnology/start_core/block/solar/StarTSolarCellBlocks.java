package com.startechnology.start_core.block.solar;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.machine.solar.cell.StarTSolarCell;
import com.startechnology.start_core.machine.solar.cell.StarTSolarCellBlockEntity;
import com.startechnology.start_core.machine.solar.cell.StarTSolarCellType;
import com.startechnology.start_core.machine.solar.cell.StarTSolarCells;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

public class StarTSolarCellBlocks {
    public static final Map<StarTSolarCellType, Supplier<StarTSolarCell>> SOLAR_CELLS = new HashMap<>();

    public static NonNullBiConsumer<DataGenContext<Block, StarTSolarCell>, RegistrateBlockstateProvider> createSolarCellModel(StarTSolarCellType solarCellType) {
        var tier = solarCellType.getTier();
        var isLowTier = tier < 7;
        var tierName = GTValues.VN[tier].toLowerCase(Locale.ROOT);

        return (ctx, prov) -> prov.simpleBlock(ctx.getEntry(), prov.models()
            .withExistingParent("%s_solar_cell".formatted(tierName), "minecraft:block/slab")
            .texture("top", StarTCore.resourceLocation("block/solar/cells/%s".formatted(tierName)))
            .texture("side", StarTCore.resourceLocation("block/solar/%s_side".formatted(isLowTier ? "low" : "high")))
            .texture("bottom", StarTCore.resourceLocation("block/solar/%s_bottom".formatted(isLowTier ? "low" : "high"))));
    }

    public static BlockEntry<StarTSolarCell> createSolarCellBlock(StarTSolarCellType solarCellType) {
        BlockEntry<StarTSolarCell> block = START_REGISTRATE
            .block(solarCellType.getSerializedName(), p -> new StarTSolarCell(p, solarCellType))
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false).noOcclusion())
            .blockstate(createSolarCellModel(solarCellType))
            .addLayer(() -> RenderType::translucent)
            .tag(GTToolType.WRENCH.harvestTags.get(0), CustomTags.TOOL_TIERS[solarCellType.getHarvestLevel()])
            .item(BlockItem::new)
            .build()
            .register();

        SOLAR_CELLS.put(solarCellType, block);

        return block;
    }

    public static final BlockEntry<StarTSolarCell> EV_SOLAR_CELL = createSolarCellBlock(StarTSolarCells.EV_SOLAR_CELL);
    public static final BlockEntry<StarTSolarCell> IV_SOLAR_CELL = createSolarCellBlock(StarTSolarCells.IV_SOLAR_CELL);
    public static final BlockEntry<StarTSolarCell> LUV_SOLAR_CELL = createSolarCellBlock(StarTSolarCells.LUV_SOLAR_CELL);
    public static final BlockEntry<StarTSolarCell> ZPM_SOLAR_CELL = createSolarCellBlock(StarTSolarCells.ZPM_SOLAR_CELL);
    public static final BlockEntry<StarTSolarCell> UV_SOLAR_CELL = createSolarCellBlock(StarTSolarCells.UV_SOLAR_CELL);
    public static final BlockEntry<StarTSolarCell> UHV_SOLAR_CELL = createSolarCellBlock(StarTSolarCells.UHV_SOLAR_CELL);
    public static final BlockEntityEntry<BlockEntity> START_SOLAR_CELL_BLOCK_ENTITY = START_REGISTRATE
        .blockEntity("solar_cell", (type, pos, blockState) -> new StarTSolarCellBlockEntity(type, pos, blockState, 0))
        .onRegister(StarTSolarCellBlockEntity::onBlockEntityRegister)
        .validBlocks(EV_SOLAR_CELL, IV_SOLAR_CELL, LUV_SOLAR_CELL, ZPM_SOLAR_CELL, UV_SOLAR_CELL, UHV_SOLAR_CELL)
        .register();

    public static void init() {
    }
}
