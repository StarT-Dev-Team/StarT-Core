package com.startechnology.start_core.machine.black_hole;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class BlackHoleSeeds {

    public static final String RECIPE_DATA_SEED = "seed_profile";
    public static final String RECIPE_DATA_MASS = "mass";

    private static final Map<String, BlackHoleSeedProfile> PROFILES = new LinkedHashMap<>();

    public static final BlackHoleSeedProfile GARGANTUA = register(
            new BlackHoleSeedProfile("gargantua", 100.0, 0.6f, 0xFFB46A, "Gargantua"));
    public static final BlackHoleSeedProfile AZURE = register(
            new BlackHoleSeedProfile("azure", 100.0, 0.9f, 0x7FB8FF, "Azure"));
    public static final BlackHoleSeedProfile CRIMSON = register(
            new BlackHoleSeedProfile("crimson", 100.0, 0.3f, 0xFF4A3A, "Crimson"));
    public static final BlackHoleSeedProfile STELLAR_REMNANT = register(
            new BlackHoleSeedProfile("stellar_remnant", 150.0, 0.9f, 0xCFE4FF, "Stellar Remnant"));
    public static final BlackHoleSeedProfile PULSAR_COLLAPSE = register(
            new BlackHoleSeedProfile("pulsar_collapse", 180.0, 0.95f, 0x5FE8FF, "Pulsar Collapse"));

    private static BlackHoleSeedProfile register(BlackHoleSeedProfile profile) {
        PROFILES.put(profile.id(), profile);
        return profile;
    }

    public static BlackHoleSeedProfile get(String id) {
        return PROFILES.get(id);
    }

    public static Collection<BlackHoleSeedProfile> all() {
        return Collections.unmodifiableCollection(PROFILES.values());
    }
}
