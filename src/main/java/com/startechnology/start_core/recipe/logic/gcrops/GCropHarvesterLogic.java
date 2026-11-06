package com.startechnology.start_core.recipe.logic.gcrops;

import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType.ICustomRecipeLogic;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
import com.startechnology.start_core.api.gcrop.*;
import com.startechnology.start_core.item.components.StarTGCropBehaviour;
import com.startechnology.start_core.item.gcrops.StarTGCropItems;
import com.startechnology.start_core.materials.StarTMaterials;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import com.startechnology.start_core.utils.StarTCustomLogicUtils;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static com.startechnology.start_core.item.gcrops.StarTGCropItems.*;

public class GCropHarvesterLogic implements ICustomRecipeLogic {

    private static Map<Integer, Fluid> GROWTH_FLUIDS;

    /**
     * Lazily initializes and returns the map of crop tiers to their required growth fluid.
     *
     * @return an immutable map of minimum tier to required {@link Fluid}
     */
    private static Map<Integer, Fluid> getGrowthFluids() {
        if (GROWTH_FLUIDS == null) GROWTH_FLUIDS = Map.of(
                0, GTMaterials.Water.getFluid(),
                2, GTMaterials.Biomass.getFluid(),
                4, StarTMaterials.NpkSolution.getFluid(),
                5, StarTMaterials.NutrientRichFertilizerSolution.getFluid(),
                6, StarTMaterials.BiostimulatingMixture.getFluid());
        return GROWTH_FLUIDS;
    }

    private static Map<Integer, Item> GROWTH_ITEMS;

    /**
     * Lazily initializes and returns the map of crop tiers to their required fertilizer item.
     *
     * @return an immutable map of minimum tier to required {@link Item}
     */
    private static Map<Integer, Item> getGrowthItems() {
        if (GROWTH_ITEMS == null) GROWTH_ITEMS = Map.of(
                1, Items.BONE_MEAL,
                3, GTItems.FERTILIZER.asItem(),
                5, ChemicalHelper.get(TagPrefix.dust, GTMaterials.Phosphate).getItem(),
                7, ChemicalHelper.get(TagPrefix.dust, GTMaterials.Strontium).getItem());
        return GROWTH_ITEMS;
    }

    public record GrowthRequirement<T>(T requirement, int amount) {}

    /**
     * Determines the fertilizer item and amount required to harvest a crop of the specified tier.
     * Higher-tier crops accept lower-tier fertilizers at exponentially scaled amounts.
     *
     * @param cropTier the tier of the crop being harvested
     * @return the {@link GrowthRequirement} for the fertilizer item, or {@code null} if none is required
     */
    public static @Nullable GrowthRequirement<Item> getFertilizerRequirement(int cropTier) {
        Map<Integer, Item> items = getGrowthItems();
        for (int j = cropTier; j >= 0; j--) {
            Item item = items.get(j);
            if (item != null) {
                int amount = 1 << (2 * (cropTier - j));
                return new GrowthRequirement<>(item, amount);
            }
        }
        return null;
    }

    /**
     * Determines the growth fluid and volume (in mB) required to harvest a crop of the specified tier.
     * Higher-tier crops accept lower-tier fluids at exponentially scaled amounts.
     *
     * @param cropTier the tier of the crop being harvested
     * @return the {@link GrowthRequirement} for the growth fluid, or {@code null} if none is required
     */
    public static @Nullable GrowthRequirement<Fluid> getGrowthFluidRequirement(int cropTier) {
        Map<Integer, Fluid> fluids = getGrowthFluids();
        for (int j = cropTier; j >= 0; j--) {
            Fluid fluid = fluids.get(j);
            if (fluid != null) {
                int amount = 100 << (2 * (cropTier - j));
                return new GrowthRequirement<>(fluid, amount);
            }
        }
        return null;
    }

    /**
     * Scans the item handlers of the machine to build a harvester recipe matching any installed crop.
     *
     * @param holder the recipe capability holder representing the harvester machine
     * @return the constructed {@link GTRecipe}, or {@code null} if no valid crop or requirements match
     */
    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var itemHandlers = StarTCustomLogicUtils.getItemHandlersMap(holder);
        if (itemHandlers.isEmpty()) return null;

        List<List<ItemStack>> allItems = StarTCustomLogicUtils.getAllItems(itemHandlers);

        for (List<ItemStack> itemSet : allItems) {
            GTRecipe recipe = createHarvesterRecipe(itemSet, holder);
            if (recipe != null) return recipe;
        }

        return null;
    }

    /**
     * Builds a harvest recipe for a crop item, applying genetic modifiers (duration, voltage, yields,
     * fluid/fertilizer consumption, and daytime/climate requirements) based on the crop's genome.
     *
     * @param itemSet the list of available input items
     * @param holder  the machine capability holder used for climate queries
     * @return the tailored harvester {@link GTRecipe}, or {@code null} if inputs are invalid
     */
    private GTRecipe createHarvesterRecipe(List<ItemStack> itemSet, IRecipeCapabilityHolder holder) {
        for (ItemStack stack : itemSet) {
            StarTGCropBehaviour cropBehaviour = StarTGCropBehaviour.getGCropBehaviour(stack);
            if (cropBehaviour == null) continue;

            StarTGCropGenome gCropGenome = StarTGCropManager.gcropGenomeFromTag(stack);
            if (gCropGenome == null) continue;

            ItemStack potentialCrop = stack.copyWithCount(1);

            ItemEntry<ComponentItem> fruit = GCROP_FRUITMAP.get(cropBehaviour.getCropMaterial());
            if (fruit == null) continue;

            int cropTier = cropBehaviour.getCropTier();

            int duration = (cropTier == 0) ? 160 : 160 * cropTier;

            // TODO: replace liniar search with map lookup
            if (gCropGenome.hasTrait("quickened")) duration = (int) Math.round(duration * 0.9);
            if (gCropGenome.hasTrait("speedy")) duration = (int) Math.round(duration * 0.9);
            if (gCropGenome.hasTrait("fast")) duration = (int) Math.round(duration * 0.9);

            if (gCropGenome.hasTrait("slow")) duration = (int) Math.round(duration * 1.2);
            if (gCropGenome.hasTrait("stunted")) duration = (int) Math.round(duration * 1.2);

            if (gCropGenome.hasTrait("early")) {
                duration = (int) Math.round(duration * 0.7);
                ItemEntry<ComponentItem> flower = GCROP_FLOWERMAP.get(cropBehaviour.getCropMaterial());
                if (flower != null) fruit = flower;
            }

            int EUtV = StarTGCropItems.tierVoltages.get(cropTier);
            if (gCropGenome.hasTrait("empowered")) EUtV -= 1;

            GrowthRequirement<Item> fertilizerReq = getFertilizerRequirement(cropTier);
            Item fertilizerItem = fertilizerReq != null ? fertilizerReq.requirement() : null;
            int fertilizerAmount = fertilizerReq != null ? fertilizerReq.amount() : 0;

            GrowthRequirement<Fluid> fluidReq = getGrowthFluidRequirement(cropTier);
            Fluid growthFluid = fluidReq != null ? fluidReq.requirement() : null;
            int fluidAmount = fluidReq != null ? fluidReq.amount() : 0;

            if (gCropGenome.hasTrait("thirsty")) fluidAmount = (int) Math.round(fluidAmount * 1.2);

            if (gCropGenome.hasTrait("gluttonous")) {
                fertilizerAmount = (int) Math.round(fertilizerAmount * 1.2);
                fluidAmount = (int) Math.round(fluidAmount * 1.2);
            }

            int minFruitAmount = 1;
            int maxFruitAmount = 4;
            if (gCropGenome.hasTrait("enormous")) {
                if (StarTCore.RNG.nextIntBetweenInclusive(1, 100) < 60) maxFruitAmount += 2;
            }

            if (gCropGenome.hasTrait("branching")) {
                for (int j = 0; j < 3; j++) if (StarTCore.RNG.nextIntBetweenInclusive(1, 100) < 60) maxFruitAmount += 2;
                for (int j = 0; j < 2; j++) if (StarTCore.RNG.nextIntBetweenInclusive(1, 100) < 70) minFruitAmount += 1;
            }

            if (gCropGenome.hasTrait("shriveled")) {
                maxFruitAmount -= 3;
                minFruitAmount -= 2;
            }

            if (gCropGenome.hasTrait("proliferating")) {
                fertilizerAmount = fertilizerAmount * 2;
                fluidAmount = fluidAmount * 2;
                duration = (int) Math.round(duration * 1.15);
                minFruitAmount += 4;
                maxFruitAmount += 2;
            }

            if (gCropGenome.hasTrait("sprawling")) {
                fertilizerAmount = fertilizerAmount * 10;
                fluidAmount = fluidAmount * 10;
                duration = (int) Math.round(duration * 1.6);
                maxFruitAmount = maxFruitAmount * 8;
                minFruitAmount = minFruitAmount * 8;
            }

            if (gCropGenome.hasTrait("autotroph")) {
                fertilizerAmount = fertilizerAmount * 3;
                fluidAmount = fluidAmount * 3;
                duration = duration * 5;
                maxFruitAmount = maxFruitAmount * 4;
                minFruitAmount = minFruitAmount * 4;
            }

            int baseChance = 750 * cropTier;
            int chanceIncrease = 100 * cropTier;

            StarTGCropGene climateGene = gCropGenome.getClimateGene();
            StarTClimateType expectedClimate = climateGene == null ? null :
                    StarTClimateType.getClimateFromTrait(climateGene.getTrait());
            StarTClimateType actualClimateType = IClimateProvider.getClimateFromMachine(holder);
            boolean hasEqualClimate = expectedClimate != null && expectedClimate.equals(actualClimateType);

            boolean alwaysNight = false;

            if (hasEqualClimate) {
                switch (climateGene.getTrait().id()) {
                    case "frosty" -> {
                        duration = (int) Math.round(duration * 1.2);
                        fertilizerAmount = (int) Math.round(fertilizerAmount * 0.8);
                    }
                    case "scorching" -> {
                        EUtV = EUtV - 1;
                        alwaysNight = true;
                    }
                    case "tropical" -> {
                        fertilizerAmount = (int) Math.round(fertilizerAmount * 1.2);
                        if (maxFruitAmount > 20) maxFruitAmount = (int) Math.round(maxFruitAmount * 1.2);
                        else maxFruitAmount = maxFruitAmount + 4;
                    }
                    case "desertic" -> {
                        if (maxFruitAmount > 20) maxFruitAmount = (int) Math.round(maxFruitAmount * 0.8);
                        else maxFruitAmount = maxFruitAmount - 2;
                        fluidAmount = (int) Math.round(fluidAmount * 0.8);
                    }
                    case "damp" -> {
                        fluidAmount = (int) Math.round(fluidAmount * 1.2);
                        duration = (int) Math.round(duration * 0.8);
                    }
                }
            }

            if (maxFruitAmount <= minFruitAmount) maxFruitAmount = minFruitAmount + 1;

            // Cap voltage at ULV to avoid empowered/scorching setting it below
            if (EUtV < 0) EUtV = 0;

            GTRecipeBuilder harvestRecipe = StarTRecipeTypes.GCROP_HARVESTER_RECIPES
                    .recipeBuilder(fruit.getId().getPath() + "_harvest")
                    .outputItemsRanged(fruit.asStack(), UniformInt.of(minFruitAmount, maxFruitAmount))
                    .duration(duration)
                    .EUtVA(EUtV);

            if (cropTier == 0) {
                harvestRecipe.notConsumable(potentialCrop);
            } else {
                harvestRecipe.chancedInput(potentialCrop, baseChance, chanceIncrease);
            }

            if (fertilizerItem != null) harvestRecipe.inputItems(new ItemStack(fertilizerItem, fertilizerAmount));

            if (growthFluid != null) harvestRecipe.inputFluids(new FluidStack(growthFluid, fluidAmount));

            if (alwaysNight) harvestRecipe.daytime(true);
            else if (!gCropGenome.hasTrait("diurnal")) harvestRecipe.daytime(gCropGenome.hasTrait("nocturnal"));

            return harvestRecipe.buildRawRecipe();
        }

        return null;
    }

    @Override
    public void buildRepresentativeRecipes() {
        for (ItemEntry<ComponentItem> crop : GCROP_ITEMS) {
            ItemStack gCrop = crop.asStack();

            StarTCustomTooltipsManager.writeCustomTooltipsToItem(gCrop.getOrCreateTag(),
                    "behaviour.start_core.gcrop.harvester.gcrop",
                    "behaviour.start_core.gcrop.harvester.disclaimer");

            StarTGCropBehaviour cropBehaviour = StarTGCropBehaviour.getGCropBehaviour(gCrop);
            if (cropBehaviour == null) continue;

            ItemEntry<ComponentItem> fruit = GCROP_FRUITMAP.get(cropBehaviour.getCropMaterial());
            ItemStack fruitItem = fruit.asStack();

            StarTCustomTooltipsManager.writeCustomTooltipsToItem(fruitItem.getOrCreateTag(),
                    "behaviour.start_core.gcrop.harvester.fruit");

            int cropTier = cropBehaviour.getCropTier();

            GrowthRequirement<Item> fertilizerReq = getFertilizerRequirement(cropTier);
            GrowthRequirement<Fluid> fluidReq = getGrowthFluidRequirement(cropTier);

            GTRecipeBuilder harvestRecipe = StarTRecipeTypes.GCROP_HARVESTER_RECIPES
                    .recipeBuilder(fruit.getId().getPath() + "_harvest")
                    .outputItemsRanged(fruitItem, UniformInt.of(1, 4))
                    .duration((cropTier == 0) ? 160 : 160 * cropTier)
                    .daytime()
                    .EUtVA(StarTGCropItems.tierVoltages.get(cropTier));

            if (cropTier == 0) {
                harvestRecipe.notConsumable(gCrop);
            } else {
                harvestRecipe.chancedInput(gCrop, 750 * cropTier, 100 * cropTier);
            }

            if (fertilizerReq != null) {
                ItemStack fertilizerItemStack = new ItemStack(fertilizerReq.requirement(), fertilizerReq.amount());
                StarTCustomTooltipsManager.writeCustomTooltipsToItem(fertilizerItemStack.getOrCreateTag(),
                        "behaviour.start_core.gcrop.harvester.fertilizer");

                harvestRecipe.inputItems(fertilizerItemStack);
            }

            if (fluidReq != null) {
                FluidStack growthFluidStack = new FluidStack(fluidReq.requirement(), fluidReq.amount());
                StarTCustomTooltipsManager.writeCustomTooltipsToItem(growthFluidStack.getOrCreateTag(),
                        "behaviour.start_core.gcrop.harvester.fluid");

                harvestRecipe.inputFluids(growthFluidStack);
            }

            StarTCustomLogicUtils.handleCustomRecipeLogicEMI(
                    StarTRecipeTypes.GCROP_HARVESTER_RECIPES, "gcrops", harvestRecipe.buildRawRecipe());
        }
    }
}
