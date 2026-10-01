package com.startechnology.start_core.recipe.recipes;

import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.startechnology.start_core.item.StarTItems;
import com.startechnology.start_core.machine.neutron_star.NeutronSpinCondition;
import com.startechnology.start_core.machine.neutron_star.NeutronStarBalance;

import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.UXV;
import static com.gregtechceu.gtceu.api.GTValues.VA;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.block;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.dust;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.ingot;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Helium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Hydrogen;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Neutronium;
import static com.startechnology.start_core.recipe.StarTRecipeTypes.NEUTRON_STAR_ACCRETION_RECIPES;
import static com.startechnology.start_core.recipe.StarTRecipeTypes.NEUTRON_STAR_CAPTURE_RECIPES;
import static com.startechnology.start_core.recipe.StarTRecipeTypes.NEUTRON_STAR_FORGE_RECIPES;

public class NeutronStarRecipes {

    public static final String RECIPE_DATA_SPIN_DRAW = "spin_draw";
    public static final String RECIPE_DATA_MASS_PER_MB = "mass_per_mb";
    public static final String RECIPE_DATA_SPIN_PER_MB = "spin_per_mb";
    public static final String RECIPE_DATA_MASS = "mass";
    public static final String RECIPE_DATA_SPIN = "spin";
    private static final int FUEL_MB = 100;
    private static final String HIDE_DURATION = "hide_duration";

    public static void init(Consumer<FinishedRecipe> provider) {
        NEUTRON_STAR_FORGE_RECIPES.recipeBuilder("neutronium_ingot")
                .inputItems(dust, Neutronium)
                .outputItems(ingot, Neutronium)
                .addCondition(new NeutronSpinCondition(NeutronStarBalance.SPIN_TIERS[1]))
                .addData(RECIPE_DATA_SPIN_DRAW, 0.5f)
                .duration(200).EUt(VA[UXV])
                .save(provider);
        NEUTRON_STAR_FORGE_RECIPES.recipeBuilder("neutronium_block")
                .inputItems(ingot, Neutronium, 9)
                .outputItems(block, Neutronium)
                .addCondition(new NeutronSpinCondition(NeutronStarBalance.SPIN_TIERS[2]))
                .addData(RECIPE_DATA_SPIN_DRAW, 1f)
                .duration(400).EUt(VA[UXV])
                .save(provider);

        NEUTRON_STAR_ACCRETION_RECIPES.recipeBuilder("hydrogen")
                .inputFluids(Hydrogen.getFluid(FUEL_MB))
                .addData(RECIPE_DATA_MASS_PER_MB, (float) NeutronStarBalance.HYDROGEN_MASS_PER_MB)
                .addData(RECIPE_DATA_SPIN_PER_MB, (float) NeutronStarBalance.HYDROGEN_SPIN_PER_MB)
                .addData(HIDE_DURATION, true)
                .save(provider);
        NEUTRON_STAR_ACCRETION_RECIPES.recipeBuilder("helium_plasma")
                .inputFluids(Helium.getFluid(FluidStorageKeys.PLASMA, FUEL_MB))
                .addData(RECIPE_DATA_MASS_PER_MB, (float) NeutronStarBalance.HELIUM_MASS_PER_MB)
                .addData(RECIPE_DATA_SPIN_PER_MB, (float) NeutronStarBalance.HELIUM_SPIN_PER_MB)
                .addData(HIDE_DURATION, true)
                .save(provider);

        NEUTRON_STAR_CAPTURE_RECIPES.recipeBuilder("neutron_star_remnant")
                .inputItems(StarTItems.NEUTRON_STAR_REMNANT.asStack())
                .addData(RECIPE_DATA_MASS, (float) NeutronStarBalance.M_INITIAL)
                .addData(RECIPE_DATA_SPIN, (float) NeutronStarBalance.F_INITIAL)
                .duration(NeutronStarBalance.CAPTURE_TICKS).EUt(NeutronStarBalance.CAPTURE_EUT)
                .save(provider);
    }
}
