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
import com.startechnology.start_core.api.gcrop.StarTGCropGene;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
import com.startechnology.start_core.api.gcrop.StarTGCropManager;
import com.startechnology.start_core.api.gcrop.StarTGCropPlant;
import com.startechnology.start_core.data.gcrops.StarTGCropTraits;
import com.startechnology.start_core.item.StarTGCropItems;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import com.startechnology.start_core.utils.StarTCustomLogicUtils;
import com.startechnology.start_core.utils.StarTTagUtils;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

        StarTCustomLogicUtils.handleCustomRecipeLogicEMI(StarTRecipeTypes.GCROP_MUTATOR_RECIPES, discoveryRecipe);
    }

    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var handlers = StarTCustomLogicUtils.getItemHandlers(holder);

        return StarTCustomLogicUtils.createCustomlogicRecipeWithItemHandlers(handlers,
                this::createSeedDiscoveryRecipe);
    }

    private GTRecipe createSeedDiscoveryRecipe(NotifiableItemStackHandler handler) {
        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack itemInSlot = handler.getStackInSlot(i);

            if (itemInSlot.isEmpty()) continue;

            List<StarTGCropGene> newResourceGenome = new ArrayList<>();
            List<StarTGCropGene> newProductionGenome = new ArrayList<>();
            List<StarTGCropGene> newAuxiliaryGenome = new ArrayList<>();
            List<StarTGCropTraits.StarTGCropTrait> allTraits = new ArrayList<>();

            List<StarTGCropTraits.StarTGCropTrait> tier0Traits = StarTGCropTraits.getTraitsByTier(0);

            for (var trait : tier0Traits) {
                int alleleCount = trait.runTraitFrequencyRandomGene(2);
                if (alleleCount >= 1) {
                    switch (trait.genomeType()) {
                        case RESOURCE -> {
                            newResourceGenome.add(new StarTGCropGene(trait, alleleCount));
                            allTraits.add(trait);
                        }
                        case PRODUCTION -> {
                            newProductionGenome.add(new StarTGCropGene(trait, alleleCount));
                        }
                        case AUXILIARY -> {
                            newAuxiliaryGenome.add(new StarTGCropGene(trait, alleleCount));
                        }
                    }
                }
            }

            allTraits.sort(StarTGCropTraits.TRAIT_COMPARATOR);

            ItemEntry<ComponentItem> gCropItem = StarTGCropItems.getGCropByGenome(allTraits);
            ItemStack gCropRandomSeed = (gCropItem == null) ? new ItemStack(GCROP_MALFORMED.get()) :
                    gCropItem.asStack();

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

        return null;
    }
}
