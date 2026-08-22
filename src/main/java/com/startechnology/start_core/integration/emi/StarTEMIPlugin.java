package com.startechnology.start_core.integration.emi;

import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.integration.emi.orevein.GTBedrockFluidEmiCategory;
import com.gregtechceu.gtceu.integration.emi.orevein.GTBedrockOreEmiCategory;
import com.startechnology.start_core.api.gcrop.StarTGCropGenome;
import com.startechnology.start_core.api.gcrop.StarTGCropTrait;
import com.startechnology.start_core.item.gcrops.StarTGCropItems;
import com.gregtechceu.gtceu.integration.emi.recipe.GTRecipeEMICategory;
import com.startechnology.start_core.machine.solar.StarTSolarMachines;
import com.startechnology.start_core.recipe.StarTRecipeTypes;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.startechnology.start_core.machine.drills.StarTDrillingRigs.FLUID_DRILLING_RIGS;

@EmiEntrypoint
public class StarTEMIPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        for (MultiblockMachineDefinition multiBlockDefinition : FLUID_DRILLING_RIGS) {
            if (multiBlockDefinition != null) {
                registry.addWorkstation(GTBedrockFluidEmiCategory.CATEGORY,
                        EmiStack.of(multiBlockDefinition.asStack()));
                registry.addWorkstation(GTBedrockOreEmiCategory.CATEGORY, EmiStack.of(multiBlockDefinition.asStack()));
            }
        }

        addSolarReplacementWorkstation(registry, StarTSolarMachines.SOLAR_PANEL_EV.asStack());
        addSolarReplacementWorkstation(registry, StarTSolarMachines.SOLAR_PANEL_IV.asStack());
        addSolarReplacementWorkstation(registry, StarTSolarMachines.SOLAR_PANEL_LUV.asStack());
        addSolarReplacementWorkstation(registry, StarTSolarMachines.SOLAR_ARRAY_UV.asStack());
        addSolarReplacementWorkstation(registry, StarTSolarMachines.SOLAR_ARRAY_UHV.asStack());

        CBMicroblockRecipes.register(registry);
        registerTraitDescriptions(registry);
    }

    private void registerTraitDescriptions(EmiRegistry registry) {
        for (StarTGCropTrait trait : StarTGCropTrait.TRAITS.values()) {
            if (trait == null) continue;
            List<Component> components = new ArrayList<>();

            String traitId = trait.id().toLowerCase();
            String traitType = trait.genomeType().name().toLowerCase();
            int traitTier = trait.tier();

            Component name = Component.translatable(String.format("behaviour.start_core.trait.%s.name", traitId));
            Component symbol = Component.literal(StarTGCropGenome.getPrettyTrait(
                    Component.translatable(String.format("behaviour.start_core.trait.%s.symbol", traitId)).getString(),
                    traitTier));
            Component type = Component.translatable(String.format("behaviour.start_core.trait.type.%s", traitType));

            Component headerLine = Component.translatable("behaviour.start_core.trait.info.header", name, symbol);
            components.add(headerLine);

            Component typeLine = Component.translatable("behaviour.start_core.trait.info.type", traitTier, type);

            components.add(typeLine);

            if (StarTGCropTrait.traitHasDescription.contains(traitId)) {
                Component effectLine = Component.translatable("behaviour.start_core.trait.info.effects");
                Component description = Component
                        .translatable(String.format("behaviour.start_core.trait.%s.description", traitId));
                components.add(effectLine);
                components.add(description);
            }

            addDescription(registry, StarTGCropItems.DNA_STRAND.asItem(), components);
        }
    }

    private void addDescription(EmiRegistry registry, Item item, List<Component> descriptionComponents) {
        registry.addRecipe(new EmiInfoRecipe(List.of(EmiStack.of(item)), descriptionComponents, null));
    }

    private void addSolarReplacementWorkstation(EmiRegistry registry, ItemStack stack) {
        registry.addWorkstation(
                GTRecipeEMICategory.machineCategory(StarTRecipeTypes.SOLAR_PANEL_REPLACEMENT.getCategory()),
                EmiStack.of(stack));
    }
}
