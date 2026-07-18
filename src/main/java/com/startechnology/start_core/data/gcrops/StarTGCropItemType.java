package com.startechnology.start_core.data.gcrops;

public enum StarTGCropItemType {

    LIQUID("liquid"),
    DUST("dust"),
    ORE("ore"),
    DYE("dye"),
    GEM("gem");

    private final String name;

    StarTGCropItemType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
