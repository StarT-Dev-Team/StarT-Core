package com.startechnology.start_core.recipe.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.startechnology.start_core.machine.lapidaries.LapidaryGeneratorMachine;
import com.startechnology.start_core.materials.StarTMaterials;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import com.startechnology.start_core.utils.StarTCustomLogicUtils;

import net.minecraft.world.item.ItemStack;

public class LapidaryExtractionLogic implements GTRecipeType.ICustomRecipeLogic {

    public static final double FLAWLESS_MULTIPLIER = 2.5d;
    public static final double EXQUISITE_MULTIPLIER = 6.25d;

    /*
     * NOTES:
     * Current implementation is only loosely based on the concept.
     * Original concept does not properly allign with how GT generator recipes work (constant EU/t, variable duration).
     * Original concept proposes a constant duration (5s) and variable EU/t generator (1024 * (2.5) ^ (number of
     * distinct gems) && 1536 * (2.5) ^ (number of distinct gems))
     * To be closer to the original concept, we should think of a formula that encourages the variety of gems used.
     * Implementation bellow is subject to change.
     */

    public static final Map<ItemStack, Integer> GEM_FUELS = Map.ofEntries(
            Map.entry(ChemicalHelper.get(TagPrefix.gem, GTMaterials.Lapis), 10_000),
            Map.entry(ChemicalHelper.get(TagPrefix.gemFlawless, GTMaterials.Lapis),
                    (int) (10_000 * FLAWLESS_MULTIPLIER)),
            Map.entry(ChemicalHelper.get(TagPrefix.gemExquisite, GTMaterials.Lapis),
                    (int) (10_000 * EXQUISITE_MULTIPLIER)),
            Map.entry(ChemicalHelper.get(TagPrefix.gem, GTMaterials.Amethyst), 10_000),
            Map.entry(ChemicalHelper.get(TagPrefix.gemFlawless, GTMaterials.Amethyst),
                    (int) (10_000 * FLAWLESS_MULTIPLIER)),
            Map.entry(ChemicalHelper.get(TagPrefix.gemExquisite, GTMaterials.Amethyst),
                    (int) (10_000 * EXQUISITE_MULTIPLIER)),
            Map.entry(ChemicalHelper.get(TagPrefix.gem, GTMaterials.NetherQuartz), 10_000),
            Map.entry(ChemicalHelper.get(TagPrefix.gemFlawless, GTMaterials.NetherQuartz),
                    (int) (10_000 * FLAWLESS_MULTIPLIER)),
            Map.entry(ChemicalHelper.get(TagPrefix.gemExquisite, GTMaterials.NetherQuartz),
                    (int) (10_000 * EXQUISITE_MULTIPLIER)),
            Map.entry(ChemicalHelper.get(TagPrefix.gem, GTMaterials.Ruby), 31_250),
            Map.entry(ChemicalHelper.get(TagPrefix.gemFlawless, GTMaterials.Ruby),
                    (int) (31_250 * FLAWLESS_MULTIPLIER)),
            Map.entry(ChemicalHelper.get(TagPrefix.gemExquisite, GTMaterials.Ruby),
                    (int) (31_250 * EXQUISITE_MULTIPLIER)),
            Map.entry(ChemicalHelper.get(TagPrefix.gem, GTMaterials.Emerald), 31_250),
            Map.entry(ChemicalHelper.get(TagPrefix.gemFlawless, GTMaterials.Emerald),
                    (int) (31_250 * FLAWLESS_MULTIPLIER)),
            Map.entry(ChemicalHelper.get(TagPrefix.gemExquisite, GTMaterials.Emerald),
                    (int) (31_250 * EXQUISITE_MULTIPLIER)),
            Map.entry(ChemicalHelper.get(TagPrefix.gem, GTMaterials.Sapphire), 31_250),
            Map.entry(ChemicalHelper.get(TagPrefix.gemFlawless, GTMaterials.Sapphire),
                    (int) (31_250 * FLAWLESS_MULTIPLIER)),
            Map.entry(ChemicalHelper.get(TagPrefix.gemExquisite, GTMaterials.Sapphire),
                    (int) (31_250 * EXQUISITE_MULTIPLIER)),
            Map.entry(ChemicalHelper.get(TagPrefix.gem, GTMaterials.Diamond), 75_000),
            Map.entry(ChemicalHelper.get(TagPrefix.gemFlawless, GTMaterials.Diamond),
                    (int) (75_000 * FLAWLESS_MULTIPLIER)),
            Map.entry(ChemicalHelper.get(TagPrefix.gemExquisite, GTMaterials.Diamond),
                    (int) (75_000 * EXQUISITE_MULTIPLIER)),
            Map.entry(ChemicalHelper.get(TagPrefix.gem, StarTMaterials.Diatron), 187_500),
            Map.entry(ChemicalHelper.get(TagPrefix.gemFlawless, StarTMaterials.Diatron),
                    (int) (187_500 * FLAWLESS_MULTIPLIER)),
            Map.entry(ChemicalHelper.get(TagPrefix.gemExquisite, StarTMaterials.Diatron),
                    (int) (187_500 * EXQUISITE_MULTIPLIER)));

    @Override
    public GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var itemHandlers = StarTCustomLogicUtils.getItemHandlersMap(holder);
        if (itemHandlers.isEmpty()) return null;

        List<List<ItemStack>> allItems = StarTCustomLogicUtils.getAllItems(itemHandlers);

        for (List<ItemStack> itemSet : allItems) {
            GTRecipe recipe = createLapidaryExtractionRecipe(itemSet, holder);
            if (recipe != null) return recipe;
        }

        return null;
    }

    private GTRecipe createLapidaryExtractionRecipe(List<ItemStack> itemSet, IRecipeCapabilityHolder holder) {
        if (!(holder instanceof LapidaryGeneratorMachine lapidaryGenerator)) return null;

        List<ItemStack> foundFuels = new ArrayList<>();
        int totalEu = 0;
        int minCount = lapidaryGenerator.getMaxGemParallels();

        for (ItemStack stack : itemSet) {

            if (!stack.isEmpty() && GEM_FUELS.containsKey(stack.copyWithCount(1))) {
                int count = stack.getCount();
                if (count < minCount) {
                    minCount = count;
                }
                foundFuels.add(stack.copyWithCount(1));
                totalEu += GEM_FUELS.get(stack.copyWithCount(1));
            }
        }

        if (foundFuels.isEmpty()) return null;

        // double fuelMultiplier = 1.0;

        // if (foundFuels.size() > 1) {
        // fuelMultiplier = totalEu / foundFuels.size();
        // }

        final int gemParallels = minCount;
        lapidaryGenerator.setGemParallels(gemParallels);
        ItemStack[] gemInputs = foundFuels.stream().map(item -> item.copyWithCount(gemParallels))
                .toArray(ItemStack[]::new);

        return StarTRecipeTypes.LAPIDARY_EXTRACTION_RECIPES
                .recipeBuilder("lapidary_extraction")
                .inputItems(gemInputs)
                .duration((int) (totalEu * gemParallels / 2048))
                .EUt(2048)
                .buildRawRecipe();
    }

    @Override
    public void buildRepresentativeRecipes() {
        GEM_FUELS.forEach((fuel, eu) -> {
            GTRecipe recipe = StarTRecipeTypes.LAPIDARY_EXTRACTION_RECIPES
                    .recipeBuilder(fuel.getItem().toString().split(":")[1] + "_lapidary_extraction_representative")
                    .inputItems(fuel)
                    .duration(eu / 2048)
                    .EUt(2048)
                    .buildRawRecipe();

            StarTCustomLogicUtils.handleCustomRecipeLogicEMI(StarTRecipeTypes.LAPIDARY_EXTRACTION_RECIPES,
                    "lapidary_extraction", recipe);
        });
    }
}
