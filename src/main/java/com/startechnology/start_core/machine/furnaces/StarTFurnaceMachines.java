package com.startechnology.start_core.machine.furnaces;

import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.common.data.machines.GTMachineUtils;
import com.startechnology.start_core.recipe.StarTRecipeTypes;

public class StarTFurnaceMachines {
    
    public static final MachineDefinition[] ELECTRIC_BLAST_FURNACES = GTMachineUtils.registerSimpleMachines("electric_blast_furnace",
            StarTRecipeTypes.BLAST_FURNACE_RECIPES);

    public static final MachineDefinition[] ELECTRIC_SMOKERS = GTMachineUtils.registerSimpleMachines("electric_smoker",
            StarTRecipeTypes.SMOKER_RECIPES);

    public static void init() {
    }
}
