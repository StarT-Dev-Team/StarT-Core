package com.startechnology.start_core.block.arboreal_extractor;

public enum LogTypes implements LogType {

    RESIN_PRODUCING("resin"),
    LATEX_PRODUCING("latex"),
    SAP_PRODUCING("other");

    private final String name;

    LogTypes(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
