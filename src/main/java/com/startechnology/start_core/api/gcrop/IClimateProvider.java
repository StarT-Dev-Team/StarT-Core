package com.startechnology.start_core.api.gcrop;

import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public interface IClimateProvider {

    Set<StarTClimateType> getProvidedClimates();

    static Set<StarTClimateType> getProvidedClimatesFromMachine(IRecipeCapabilityHolder holder) {
        IMultiController controller = null;
        if (holder instanceof IMultiController c) {
            controller = c;
        } else if (holder instanceof RecipeLogic logic && logic.getMachine() instanceof IMultiController c) {
            controller = c;
        }

        if (controller != null) {
            Set<StarTClimateType> climates = new HashSet<>();
            for (IMultiPart part : controller.getParts()) {
                if (part instanceof IClimateProvider provider) {
                    climates.addAll(provider.getProvidedClimates());
                }
            }
            return climates;
        }
        return Collections.emptySet();
    }

    static StarTClimateType getClimateFromMachine(IRecipeCapabilityHolder holder) {
        return getProvidedClimatesFromMachine(holder).stream().findFirst().orElse(null);
    }
}
