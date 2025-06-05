package com.startechnology.start_core.machine.hellforge;

import java.util.Map;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.startechnology.start_core.materials.StarTHellForgeHeatingLiquids;

public class StarTHellForgeMachine extends WorkableElectricMultiblockMachine {
    public StarTHellForgeMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    /* A map of fluids to their maximum heat cap for the Hell Forge */
    public static Map<Material, Integer> fluidsMap = Map.of(
        StarTHellForgeHeatingLiquids.FlamewakeSolvent, 1000,
        StarTHellForgeHeatingLiquids.EmberheartNectar, 1600,
        StarTHellForgeHeatingLiquids.IgniferousElixir, 2800,
        StarTHellForgeHeatingLiquids.BlazingPhlogiston, 5200
    );

    /**
     * Retrieves the appropriate heating fluid for the Hellforge based on the required temperature.
     * The method selects the fluid with the highest temperature cap that is still equal to or greater than the input temperature.
     *
     * @param temperature The required temperature of the Hellforge.
     * @return The Material representing the heating fluid, or null if no fluid can meet the required temperature.
     */
    public static Material getHellforgeHeatingLiquid(Integer temperature) {
        Material selectedFluid = null;
        int smallestCapAboveTemperature = Integer.MAX_VALUE;

        // Iterate over all fluid entries
        for (Map.Entry<Material, Integer> entry : fluidsMap.entrySet()) {
            int fluidCap = entry.getValue();

            // Select the fluid if its cap is >= required temperature AND is the smallest such cap found so far
            if (fluidCap >= temperature && fluidCap < smallestCapAboveTemperature) {
                smallestCapAboveTemperature = fluidCap;
                selectedFluid = entry.getKey();
            }
        }

        return selectedFluid; // May be null if no suitable fluid is found
    }


    @Override
    public void afterWorking() {
        super.afterWorking();
        getRecipeLogic().markLastRecipeDirty();
    }
}
