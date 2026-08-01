package com.startechnology.start_core.recipe;

import com.gregtechceu.gtceu.api.recipe.ParallelType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;

public class StarTParallelTypes {

    public static ParallelType HELLFORGE;
    public static ParallelType THROUGHPUT_BOOSTING;
    public static ParallelType BULK_PROCESSING;
    public static ParallelType THREADING;
    public static ParallelType MODULAR_COMBUSTION;
    public static ParallelType ABYSSAL_HARVESTER;
    public static ParallelType COMPOUND_GENERATOR;

    public static void init() {
        HELLFORGE = register("hellforge");
        THROUGHPUT_BOOSTING = register("throughput_boosting");
        BULK_PROCESSING = register("bulk_processing");
        THREADING = register("threading");
        MODULAR_COMBUSTION = register("modular_combustion");
        ABYSSAL_HARVESTER = register("abyssal_harvester");
        COMPOUND_GENERATOR = register("compound_generator");
    }

    public static ParallelType register(String name) {
        return GTRegistries.PARALLEL_TYPES.register(name, new ParallelType(name));
    }
}
