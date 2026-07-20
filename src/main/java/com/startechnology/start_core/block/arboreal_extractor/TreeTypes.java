package com.startechnology.start_core.block.arboreal_extractor;

import lombok.Getter;

public enum TreeTypes implements TreeType {

    RESIN_PRODUCING("resin"),
    LATEX_PRODUCING("latex"),
    SAP_PRODUCING("other");

    @Getter
    private final String name;

    TreeTypes(String name) {
        this.name = name;
    }
}
