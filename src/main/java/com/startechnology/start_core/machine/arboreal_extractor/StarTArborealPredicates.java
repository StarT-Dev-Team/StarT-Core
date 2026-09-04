package com.startechnology.start_core.machine.arboreal_extractor;

import com.google.common.collect.Sets;
import com.gregtechceu.gtceu.api.pattern.MultiblockState;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.error.PatternStringError;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.startechnology.start_core.block.arboreal_extractor.ArborealBlocks;

import com.startechnology.start_core.block.arboreal_extractor.TreeDefinition;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.stream.Collectors;

public class StarTArborealPredicates {

    public static String CONTEXT_KEY_TREE_TYPE = "treeType";

    public static @Nullable Set<TreeDefinition> getTreeTypeCandidates(MultiblockState state) {
        return state.getMatchContext().get(CONTEXT_KEY_TREE_TYPE);
    }

    private static boolean compareCandidates(MultiblockState state, Set<TreeDefinition> candidates) {
        if (candidates.isEmpty()) {
            // just fail when it's not leaves
            return false;
        }

        var prevCandidates = getTreeTypeCandidates(state);
        if (prevCandidates == null) {
            // first match, accept any candidates
            state.getMatchContext().set(CONTEXT_KEY_TREE_TYPE, candidates);
            return true;
        }
        if (candidates.containsAll(prevCandidates)) {
            // compatible sets, avoid allocations
            return true;
        }
        var intersection = Sets.newHashSet(Sets.intersection(prevCandidates, candidates));
        if (intersection.isEmpty()) {
            // incompatible sets
            return false;
        }
        // reduce the allowed tree types
        state.getMatchContext().set(CONTEXT_KEY_TREE_TYPE, intersection);
        return true;
    }

    public static TraceabilityPredicate leaves() {
        return new TraceabilityPredicate(state -> {
            var blockState = state.getBlockState();
            var candidates = ArborealBlocks.TREES.stream().filter(tree -> blockState.is(tree.getLeaves().get()))
                    .collect(Collectors.toSet());

            if (compareCandidates(state, candidates)) {
                return true;
            }
            state.setError(new PatternStringError("start_core.multiblock.pattern.error.arboreal_tree"));
            return false;
        }, () -> ArborealBlocks.TREES.stream()
                .map(tree -> BlockInfo.fromBlockState(tree.getLeaves().get().defaultBlockState()))
                .toArray(BlockInfo[]::new))
                .addTooltips(Component.translatable("start_core.multiblock.pattern.error.arboreal_tree"));
    }

    public static TraceabilityPredicate logs() {
        return new TraceabilityPredicate(state -> {
            var blockState = state.getBlockState();
            var candidates = ArborealBlocks.TREES.stream().filter(tree -> blockState.is(tree.getLog().get()))
                    .collect(Collectors.toSet());

            if (compareCandidates(state, candidates)) {
                return true;
            }
            state.setError(new PatternStringError("start_core.multiblock.pattern.error.arboreal_tree"));
            return false;
        }, () -> ArborealBlocks.TREES.stream()
                .map(tree -> BlockInfo.fromBlockState(tree.getLog().get().defaultBlockState()))
                .toArray(BlockInfo[]::new))
                .addTooltips(Component.translatable("start_core.multiblock.pattern.error.arboreal_tree"));
    }
}
