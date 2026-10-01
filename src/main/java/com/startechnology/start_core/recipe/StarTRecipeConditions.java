package com.startechnology.start_core.recipe;

import com.gregtechceu.gtceu.api.recipe.condition.RecipeConditionType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.startechnology.start_core.machine.neutron_star.NeutronSpinCondition;

public final class StarTRecipeConditions {

    public static final RecipeConditionType<NeutronSpinCondition> NEUTRON_SPIN = GTRegistries.RECIPE_CONDITIONS
            .register("neutron_spin", new RecipeConditionType<>(NeutronSpinCondition::new, NeutronSpinCondition.CODEC));

    private StarTRecipeConditions() {}

    public static void init() {}
}
