package com.startechnology.start_core.integration.ponder;

import com.startechnology.start_core.mixin.ponder.PonderIndexAccessor;
import dev.latvian.mods.kubejs.block.predicate.BlockIDPredicate;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.foundation.PonderTag;
import net.createmod.ponder.foundation.SelectionImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PonderJSUtils {

    public static final Set<ResourceLocation> TRANSLATED_TAGS = new HashSet<>();
    public static final Set<ResourceLocation> TRANSLATED_SCENES = new HashSet<>();

    public static Selection convertSelection(@Nullable Object object) {
        if (object instanceof Selection selection) return selection;
        if (object instanceof BoundingBox boundingBox) return SelectionImpl.of(boundingBox);
        if (object instanceof BlockPos blockPos) return SelectionImpl.of(new BoundingBox(blockPos));
        if (object instanceof List<?> list) {
            if (list.size() == 2) {
                var from = UtilsJS.blockPosOf(list.get(0));
                var to = UtilsJS.blockPosOf(list.get(1));
                return SelectionImpl.of(new BoundingBox(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ()));
            }
            if (list.size() == 6) {
                return SelectionImpl.of(new BoundingBox(
                        UtilsJS.parseInt(list.get(0), 0),
                        UtilsJS.parseInt(list.get(1), 0),
                        UtilsJS.parseInt(list.get(2), 0),
                        UtilsJS.parseInt(list.get(3), 0),
                        UtilsJS.parseInt(list.get(4), 0),
                        UtilsJS.parseInt(list.get(5), 0)
                ));
            }
        }
        var pos = UtilsJS.blockPosOf(object);
        return SelectionImpl.of(new BoundingBox(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ()));
    }

    public static PonderTag convertPonderTag(@Nullable Object o) {
        Objects.requireNonNull(o);
        var ponderTag = getTagByName(o.toString()).orElse(null);
        if (ponderTag == null) {
            IllegalArgumentException e = new IllegalArgumentException("Invalid PonderTag: " + o);
            PonderErrorHelper.reportJsPonderError(e);
            throw e;
        }

        return ponderTag;
    }

    public static Optional<PonderTag> getTagByName(ResourceLocation res) {
        return PonderIndexAccessor.getTags().getListedTags().stream().filter(tag -> tag.getId().equals(res)).findFirst();
    }

    public static Optional<PonderTag> getTagByName(String tag) {
        return getTagByName(new ResourceLocation(tag));
    }

    public static BlockState convertBlockStateOf(@Nullable Object object) {
        if (object instanceof BlockState blockState) return blockState;
        if (object instanceof Block block) return block.defaultBlockState();
        if (object instanceof BlockIDPredicate predicate) return predicate.getBlockState();
        if (object instanceof CharSequence str) {
            ResourceLocation location = ResourceLocation.tryParse(str.toString());
            if (location != null) {
                Block block = ForgeRegistries.BLOCKS.getValue(location);
                if (block != null) {
                    return block.defaultBlockState();
                }
            }
        }
        return Blocks.AIR.defaultBlockState();
    }

    public static BlockIDPredicate createBlockID(BlockState state) {
        var predicate = new BlockIDPredicate(ForgeRegistries.BLOCKS.getKey(state.getBlock()));
        for (var entry : state.getValues().entrySet()) {
            predicate.with(entry.getKey().getName(), entry.getValue().toString());
        }
        return predicate;
    }
}
