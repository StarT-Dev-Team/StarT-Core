package com.startechnology.start_core.machine.abyssal_harvester;

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.startechnology.start_core.machine.StarTMachineUtils;
import com.startechnology.start_core.recipe.StarTRecipeTypes;

import dev.latvian.mods.kubejs.KubeJS;

public class StarTAbyssalharvesterMachines {
    
    public static MultiblockMachineDefinition ABYSSAL_HARVESTER = START_REGISTRATE
        .multiblock("abyssal_harvester", StarTAbyssalHarvesterMachine::new)
        .appearanceBlock(() -> StarTMachineUtils.getKjsBlock(("extreme_temperature_smelting_casing")))
        .langValue("Abyssal Harvseter")
        .recipeTypes(StarTRecipeTypes.ABYSSAL_HARVESTER_RECIPES)
        .recipeModifier(StarTAbyssalHarvesterMachine::recipeModifier)
        .pattern(definition -> FactoryBlockPattern.start()
            .aisle("@O")
            .where('@', Predicates.controller(Predicates.blocks(definition.get())))
            .where('O', Predicates.abilities(PartAbility.EXPORT_FLUIDS))
            .build()
        )
        .workableCasingRenderer(KubeJS.id("block/casings/ultimate_multis/extreme_temperature_smelting_casing"),
            GTCEu.id("block/machines/alloy_smelter"), false)
        .register();

    public static void init() {}
}
