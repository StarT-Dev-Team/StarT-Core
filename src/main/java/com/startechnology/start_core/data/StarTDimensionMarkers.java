package com.startechnology.start_core.data;

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

import java.util.function.Supplier;

import com.gregtechceu.gtceu.api.data.DimensionMarker;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class StarTDimensionMarkers {
    
    public static final BlockEntry<Block> ABYDOS_MARKER = createMarker("abydos");

    public static final DimensionMarker OVERWORLD = createAndRegister(ResourceLocation.of("sgjourney:abydos", ':'), 0,
        () -> ABYDOS_MARKER, null);

    public static DimensionMarker createAndRegister(ResourceLocation dim, int tier, Supplier<ItemLike> supplier,
                                                    String overrideName) {
        DimensionMarker marker = new DimensionMarker(tier, supplier, overrideName);
        marker.register(dim);
        return marker;
    }

    private static BlockEntry<Block> createMarker(String name) {
        return START_REGISTRATE.block("%s_marker".formatted(name), Block::new)
                .lang(FormattingUtil.toEnglishName(name))
                .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models().cube(ctx.getName(),
                        prov.modLoc("block/dim_markers/%s/down".formatted(name)),
                        prov.modLoc("block/dim_markers/%s/up".formatted(name)),
                        prov.modLoc("block/dim_markers/%s/north".formatted(name)),
                        prov.modLoc("block/dim_markers/%s/south".formatted(name)),
                        prov.modLoc("block/dim_markers/%s/east".formatted(name)),
                        prov.modLoc("block/dim_markers/%s/west".formatted(name)))
                        .texture("particle", "#north")
                        .guiLight(BlockModel.GuiLight.FRONT)))
                .simpleItem()
                .register();
    }

    public static final void init() {}
}
