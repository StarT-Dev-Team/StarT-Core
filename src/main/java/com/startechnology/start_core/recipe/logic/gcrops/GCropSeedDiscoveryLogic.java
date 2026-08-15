package com.startechnology.start_core.recipe.logic.gcrops;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType.ICustomRecipeLogic;
import com.startechnology.start_core.api.gcrop.*;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
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
import static com.startechnology.start_core.utils.StarTMaterialUtils.getMaterial;

public class GCropSeedDiscoveryLogic implements ICustomRecipeLogic {

    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var handlers = StarTCustomLogicUtils.getItemHandlers(holder);

        return StarTCustomLogicUtils.createCustomlogicRecipeWithItemHandlers(handlers,
                this::createSeedDiscoveryRecipe);
    }

    private GTRecipe createSeedDiscoveryRecipe(NotifiableItemStackHandler handler) {
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack itemInSlot = handler.getStackInSlot(i);

            if (itemInSlot.isEmpty()) continue;

            if (!itemInSlot.is(Tags.Items.SEEDS)) continue;

            List<StarTGCropGene> newResourceGenome = new ArrayList<>();
            List<StarTGCropGene> newProductionGenome = new ArrayList<>();
            List<StarTGCropGene> newAuxiliaryGenome = new ArrayList<>();

            List<StarTGCropTrait> tier0Traits = StarTGCropTraits.getTraitsByTier(0);

            for (var trait : tier0Traits) {
                int alleleCount = trait.runTraitFrequencyRandomGene();
                if (alleleCount >= 1) {
                    switch (trait.genomeType()) {
                        case RESOURCE -> {
                            newResourceGenome.add(new StarTGCropGene(trait, alleleCount));
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

            ItemStack gCropRandomSeed = StarTGCropTraits.getCropWithTraits(newResourceGenome, newProductionGenome,
                    newAuxiliaryGenome);

            return StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                    .recipeBuilder("seed_discovery")
                    .inputItems(StarTTagUtils.getTag("forge:seeds"))
                    .inputFluids(getMaterial("start_core:mystical_air").getFluid(1000))
                    .outputItems(gCropRandomSeed)
                    .duration(120)
                    .EUt(GTValues.V[GTValues.ULV])
                    .buildRawRecipe();

        }

        return null;
    }

    @Override
    public void buildRepresentativeRecipes() {
        ItemStack gCropRandomSeed = new ItemStack(GCROP_MALFORMED.asItem());

        gCropRandomSeed.setHoverName(Component.translatable(
                "behaviour.start_core.gcrop.random_crop_name"));
        StarTCustomTooltipsManager.writeCustomTooltipsToItem(gCropRandomSeed.getOrCreateTag(),
                "behaviour.start_core.gcrop.new_random_crop");

        GTRecipe discoveryRecipe = StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                .recipeBuilder("seed_discovery")
                .inputItems(StarTTagUtils.getTag("forge:seeds"))
                .inputFluids(getMaterial("start_core:mystical_air").getFluid(1000))
                .outputItems(gCropRandomSeed)
                .duration(120)
                .EUt(GTValues.V[GTValues.ULV])
                .buildRawRecipe();

        StarTCustomLogicUtils.handleCustomRecipeLogicEMI(StarTRecipeTypes.GCROP_MUTATOR_RECIPES, "gcrops",
                discoveryRecipe);
    }
}
