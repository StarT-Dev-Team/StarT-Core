package com.startechnology.start_core.machine.komaru;

import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.startechnology.start_core.machine.modular.StarTModularPredicates;

public class StarTKomaruPredicates {
    public static String ADVANCED_STORAGE_KEY = "advancedModules";
    public static String BASIC_STORAGE_KEY = "basicModules";

    public static TraceabilityPredicate advancedModulesPredicate = StarTModularPredicates
            .createKeyedAutoScalingTerminalPredicate(ADVANCED_STORAGE_KEY);

    public static TraceabilityPredicate basicModulesPredicate = StarTModularPredicates
            .createKeyedAutoScalingTerminalPredicate(BASIC_STORAGE_KEY);
}
