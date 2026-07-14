package com.startechnology.start_core.recipe.logic.gcrops;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType.ICustomRecipeLogic;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.gcrop.StarTGCropGene;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
import com.startechnology.start_core.api.gcrop.StarTGCropManager;
import com.startechnology.start_core.api.gcrop.StarTGCropPlant;
import com.startechnology.start_core.data.gcrops.StarTGCropTraits;
import com.startechnology.start_core.item.StarTGCropItems;
import com.startechnology.start_core.item.components.StarTGCropBehaviour;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import com.startechnology.start_core.utils.StarTTagUtils;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Comparator;

import static com.startechnology.start_core.item.StarTGCropItems.GCROP_MALFORMED;

public class GCropSeedDiscoveryLogic implements ICustomRecipeLogic {

    @Override
    public void buildRepresentativeRecipes() {
        ItemStack gCropRandomSeed = new ItemStack(GCROP_MALFORMED.asItem());
        StarTCustomTooltipsManager.writeCustomTooltipsToItem(gCropRandomSeed.getOrCreateTag(),
                "behaviour.start_core.gcrop.random_crop");

        gCropRandomSeed.setHoverName(Component.translatable(
                "behaviour.start_core.gcrop.random_crop_name"));

        GTRecipe discoveryRecipe = StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                .recipeBuilder("seed_discovery")
                .inputItems(StarTTagUtils.getTag("forge:seeds"))
                .inputFluids(GTMaterials.Air.getFluid(1000))
                .outputItems(gCropRandomSeed)
                .duration(120)
                .EUt(GTValues.V[GTValues.MV])
                .buildRawRecipe();

        // for EMI to detect it's a synthetic recipe (not ever in JSON)
        discoveryRecipe.setId(discoveryRecipe.getId().withPrefix("/"));
        StarTRecipeTypes.GCROP_MUTATOR_RECIPES.addToMainCategory(discoveryRecipe);
    }

    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        List<NotifiableItemStackHandler> handlers = Objects
                .requireNonNullElseGet(holder.getCapabilitiesFlat(IO.IN, ItemRecipeCapability.CAP),
                        Collections::emptyList)
                .stream()
                .filter(NotifiableItemStackHandler.class::isInstance)
                .map(NotifiableItemStackHandler.class::cast)
                .filter(i -> i.getSlots() >= 1)
                .toList();

        if (handlers.isEmpty()) return null;

        // Return for the first recipe found
        for (NotifiableItemStackHandler handler : handlers) {
            GTRecipe recipe = createDormantAwakeningRecipe(handler);
            if (recipe != null) return recipe;
        }

        return null;
    }

    private int runTraitFrequencyRandomGene(int frequency, int alleleCount) {
        int traitCount = 0;
        for (int i = 0; i < alleleCount; i++) {
            int randomLargePercent = StarTCore.RNG.nextIntBetweenInclusive(1, 10000);
            if (randomLargePercent < frequency) traitCount++;
        }
        return traitCount;
    }

    private GTRecipe createDormantAwakeningRecipe(NotifiableItemStackHandler handler) {
        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack itemInSlot = handler.getStackInSlot(i);

            if (!itemInSlot.isEmpty()) {
                List<StarTGCropGene> newResourceGenome = new ArrayList<>();
                List<StarTGCropGene> newProductionGenome = new ArrayList<>();
                List<StarTGCropGene> newAuxiliaryGenome = new ArrayList<>();
                List<StarTGCropTraits.StarTGCropTrait> allTraits = new ArrayList<>();

                for (StarTGCropTraits.StarTGCropTrait trait : StarTGCropTraits.TRAITS.values()) {
                    if (trait.tier() == 0) {
                        int alleleCount = runTraitFrequencyRandomGene(trait.frequency(), 2);
                        if (alleleCount >= 1) {
                            if (trait.genomeType() == StarTGCropTraits.GenomeType.RESOURCE) {
                                newResourceGenome.add(new StarTGCropGene(trait, alleleCount));
                                allTraits.add(trait);
                            } else if (trait.genomeType() == StarTGCropTraits.GenomeType.PRODUCTION) {
                                newProductionGenome.add(new StarTGCropGene(trait, alleleCount));
                            } else if (trait.genomeType() == StarTGCropTraits.GenomeType.AUXILIARY) {
                                newAuxiliaryGenome.add(new StarTGCropGene(trait, alleleCount));
                            }
                        }
                    }
                }

                allTraits.sort(Comparator.comparing(StarTGCropTraits.StarTGCropTrait::name));

                ItemStack gCropRandomSeed = new ItemStack(GCROP_MALFORMED.get());

                for (ItemEntry<ComponentItem> gCropItemEntry : StarTGCropItems.GCROP_ITEMS) {
                    ItemStack gCropItem = new ItemStack(gCropItemEntry.get());
                    StarTGCropBehaviour gCropBehaviour = StarTGCropBehaviour.getGCropBehaviour(gCropItem);

                    if (gCropBehaviour == null) continue;

                    List<StarTGCropTraits.StarTGCropTrait> gCropTraits = gCropBehaviour.getCropTraits();
                    gCropTraits.sort(Comparator.comparing(StarTGCropTraits.StarTGCropTrait::name));

                    if (gCropTraits.equals(allTraits)) {
                        gCropRandomSeed = gCropItem;
                        break;
                    } ;
                }

                StarTGCropPlant newGCropGenome = new StarTGCropPlant(newResourceGenome, newProductionGenome,
                        newAuxiliaryGenome);

                StarTGCropManager.writeGCRopGenomeToItem(gCropRandomSeed.getOrCreateTag(), newGCropGenome);

                return StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                        .recipeBuilder("seed_discovery")
                        .inputItems(StarTTagUtils.getTag("forge:seeds"))
                        .inputFluids(GTMaterials.Air.getFluid(1000))
                        .outputItems(gCropRandomSeed)
                        .duration(120)
                        .EUt(GTValues.V[GTValues.MV])
                        .buildRawRecipe();
            }
        }

        return null;
    }
}
