package com.startechnology.start_core.recipe.logic.gcrops;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType.ICustomRecipeLogic;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
import com.startechnology.start_core.api.gcrop.StarTGCropGene;
import com.startechnology.start_core.api.gcrop.StarTGCropTrait;
import com.startechnology.start_core.api.gcrop.StarTGCropTraits;
import com.startechnology.start_core.materials.StarTMaterials;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import com.startechnology.start_core.utils.StarTCustomLogicUtils;
import com.startechnology.start_core.utils.StarTTagUtils;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.startechnology.start_core.item.gcrops.StarTGCropItems.GCROP_MALFORMED;

public class GCropSeedDiscoveryLogic implements ICustomRecipeLogic {

    private static List<StarTGCropTrait> TIER_0_TRAITS;

    /**
     * Lazily initializes and caches the list of all registered Tier 0 traits.
     *
     * @return the list of Tier 0 {@link StarTGCropTrait}s
     */
    private static List<StarTGCropTrait> getTier0Traits() {
        if (TIER_0_TRAITS == null) TIER_0_TRAITS = StarTGCropTraits.getTraitsByTier(0);
        return TIER_0_TRAITS;
    }

    /**
     * Scans item handlers of the machine to find seed items suitable for GCrop seed discovery.
     *
     * @param holder the recipe capability holder representing the mutator machine
     * @return the constructed seed discovery {@link GTRecipe}, or {@code null} if no seeds are present
     */
    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var itemHandlers = StarTCustomLogicUtils.getItemHandlers(holder);
        if (itemHandlers.isEmpty()) return null;

        return StarTCustomLogicUtils.createCustomlogicRecipeWithItemHandlers(itemHandlers,
                this::createSeedDiscoveryRecipe);
    }

    /**
     * Inspects an item handler for any seed item matching the {@code forge:seeds} tag and rolls
     * random Tier 0 genes to generate a newly discovered GCrop seed.
     *
     * @param handler the item stack handler to inspect
     * @return the created {@link GTRecipe}, or {@code null} if no seeds are found
     */
    private GTRecipe createSeedDiscoveryRecipe(NotifiableItemStackHandler handler) {
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack itemInSlot = handler.getStackInSlot(i);

            if (itemInSlot.isEmpty()) continue;

            if (!itemInSlot.is(Tags.Items.SEEDS)) continue;

            List<StarTGCropGene> newResourceGenome = new ArrayList<>();
            List<StarTGCropGene> newProductionGenome = new ArrayList<>();
            List<StarTGCropGene> newAuxiliaryGenome = new ArrayList<>();

            for (var trait : getTier0Traits()) {
                int alleleCount = trait.runTraitFrequencyRandomGene();
                if (alleleCount >= 1) {
                    switch (trait.genomeType()) {
                        case RESOURCE -> newResourceGenome.add(new StarTGCropGene(trait, alleleCount));
                        case PRODUCTION -> newProductionGenome.add(new StarTGCropGene(trait, alleleCount));
                        case AUXILIARY -> newAuxiliaryGenome.add(new StarTGCropGene(trait, alleleCount));
                    }
                }
            }

            ItemStack gCropRandomSeed = StarTGCropTraits.getCropWithTraits(newResourceGenome, newProductionGenome,
                    newAuxiliaryGenome);

            return StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                    .recipeBuilder("seed_discovery")
                    .inputItems(StarTTagUtils.getTag("forge:seeds"))
                    .inputFluids(StarTMaterials.MysticalAir.getFluid(1000))
                    .outputItems(gCropRandomSeed)
                    .duration(120)
                    .EUt(GTValues.V[GTValues.ULV])
                    .buildRawRecipe();
        }

        return null;
    }

    @Override
    public void buildRepresentativeRecipes() {
        ItemStack gCropRandomSeed = GCROP_MALFORMED.asStack();

        gCropRandomSeed.setHoverName(Component.translatable(
                "behaviour.start_core.gcrop.random_crop_name"));
        StarTCustomTooltipsManager.writeCustomTooltipsToItem(gCropRandomSeed.getOrCreateTag(),
                "behaviour.start_core.gcrop.new_random_crop");

        GTRecipe discoveryRecipe = StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                .recipeBuilder("seed_discovery")
                .inputItems(StarTTagUtils.getTag("forge:seeds"))
                .inputFluids(StarTMaterials.MysticalAir.getFluid(1000))
                .outputItems(gCropRandomSeed)
                .duration(120)
                .EUt(GTValues.V[GTValues.ULV])
                .buildRawRecipe();

        StarTCustomLogicUtils.handleCustomRecipeLogicEMI(StarTRecipeTypes.GCROP_MUTATOR_RECIPES, "gcrops",
                discoveryRecipe);
    }
}
