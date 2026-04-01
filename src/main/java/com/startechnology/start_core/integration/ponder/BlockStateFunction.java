package com.startechnology.start_core.integration.ponder;

import dev.latvian.mods.kubejs.block.predicate.BlockIDPredicate;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaObject;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public interface BlockStateFunction extends Function<BlockIDPredicate, BlockState> {

    static BlockStateFunction of(Context ctx, @Nullable Object object) {
        if (object instanceof BaseFunction function) {
            @SuppressWarnings("unchecked")
            var fn = (Function<BlockIDPredicate, BlockStateFunction>) NativeJavaObject.createInterfaceAdapter(ctx, Function.class, function);
            return blockIDPredicate -> {
                var result = fn.apply(blockIDPredicate);
                return BlockStateFunction.of(ctx, result).apply(blockIDPredicate);
            };
        }
        var blockState = PonderJSUtils.convertBlockStateOf(object);
        return ($) -> blockState;
    }

    static UnaryOperator<BlockState> from(BlockStateFunction function) {
        return blockState -> {
            var predicate = PonderJSUtils.createBlockID(blockState);
            return function.apply(predicate);
        };
    }

}
