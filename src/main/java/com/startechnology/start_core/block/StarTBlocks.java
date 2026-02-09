package com.startechnology.start_core.block;

import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.StarTAPI;
import com.startechnology.start_core.api.vacuumpump.IVacuumPumpType;
import com.startechnology.start_core.block.fusion.StarTFusionBlocks;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Blocks;

public class StarTBlocks {

    public static final BlockEntry<VacuumPumpBlock> VACUUM_PUMP_ZPM = createVacuumPumpBlock(VacuumPumpBlock.VacuumPumpType.ZPM);
    public static final BlockEntry<VacuumPumpBlock> VACUUM_PUMP_UV = createVacuumPumpBlock(VacuumPumpBlock.VacuumPumpType.UV);
    public static final BlockEntry<VacuumPumpBlock> VACUUM_PUMP_UHV = createVacuumPumpBlock(VacuumPumpBlock.VacuumPumpType.UHV);
    public static final BlockEntry<VacuumPumpBlock> VACUUM_PUMP_UEV = createVacuumPumpBlock(VacuumPumpBlock.VacuumPumpType.UEV);
    public static final BlockEntry<VacuumPumpBlock> VACUUM_PUMP_UIV = createVacuumPumpBlock(VacuumPumpBlock.VacuumPumpType.UIV);

    public static void init() {
        StarTFusionBlocks.init();
    }

    private static BlockEntry<VacuumPumpBlock> createVacuumPumpBlock(IVacuumPumpType pumpType) {
        var pumpBlock = StarTCore.START_REGISTRATE
                .block("%s_vacuum_pump_block".formatted(pumpType.getName()), p -> new VacuumPumpBlock(p, pumpType))
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
                .addLayer(() -> RenderType::cutoutMipped)
                .blockstate((ctx, prov) -> prov.models().cubeAll(ctx.getName(), pumpType.getTexture()))
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .item(BlockItem::new)
                .build()
                .register();
        StarTAPI.VACUUM_PUMPS.put(pumpType, pumpBlock);
        return pumpBlock;
    }

}
