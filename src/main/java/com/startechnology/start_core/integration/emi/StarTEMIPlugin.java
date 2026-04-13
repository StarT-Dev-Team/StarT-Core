package com.startechnology.start_core.integration.emi;

import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.integration.emi.orevein.GTBedrockFluidEmiCategory;
import com.gregtechceu.gtceu.integration.emi.orevein.GTBedrockOreEmiCategory;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;

import static com.startechnology.start_core.machine.drills.StarTDrillingRigs.FLUID_DRILLING_RIGS;

@EmiEntrypoint
public class StarTEMIPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        for (MultiblockMachineDefinition multiBlockDefinition : FLUID_DRILLING_RIGS) {
            if (multiBlockDefinition != null) {
                registry.addWorkstation(GTBedrockFluidEmiCategory.CATEGORY, EmiStack.of(multiBlockDefinition.asStack()));
                registry.addWorkstation(GTBedrockOreEmiCategory.CATEGORY, EmiStack.of(multiBlockDefinition.asStack()));
            }
        }
    }
}
