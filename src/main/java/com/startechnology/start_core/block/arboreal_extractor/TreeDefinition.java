package com.startechnology.start_core.block.arboreal_extractor;

import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public interface TreeDefinition {

    String getName();

    Supplier<Block> getLeaves();

    Supplier<Block> getLog();

    TreeType getTreeType();

    default Component getTranslatedName() {
        return Component.translatable("start_core.tree_definition." + getName());
    }

    class Simple implements TreeDefinition {

        @Getter
        private final String name;
        @Getter
        private final Supplier<Block> leaves;
        @Getter
        private final Supplier<Block> log;
        @Getter
        private final TreeType treeType;

        public Simple(String name, Supplier<Block> leaves, Supplier<Block> log, TreeType treeType) {
            this.name = name;
            this.leaves = leaves;
            this.log = log;
            this.treeType = treeType;
        }
    }
}
