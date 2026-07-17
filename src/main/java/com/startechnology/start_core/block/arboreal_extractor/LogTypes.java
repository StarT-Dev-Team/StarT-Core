package com.startechnology.start_core.block.arboreal_extractor;

public enum LogTypes implements LogType {

    OAK("oak"),
    SPRUCE("spruce"),
    BIRCH("birch"),
    JUNGLE("jungle"),
    ACACIA("acacia"),
    DARK_OAK("dark_oak"),
    MANGROVE("mangrove");

    private final String name;

    LogTypes(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
    
}
