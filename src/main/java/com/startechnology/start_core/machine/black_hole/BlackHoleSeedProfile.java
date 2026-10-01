package com.startechnology.start_core.machine.black_hole;

public record BlackHoleSeedProfile(String id, double initialMass, float spin, int colorTheme, String displayName) {

    public String itemId() {
        return "singularity_seed_" + id;
    }
}
