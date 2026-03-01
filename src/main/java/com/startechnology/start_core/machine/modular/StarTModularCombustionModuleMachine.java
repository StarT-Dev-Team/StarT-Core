package com.startechnology.start_core.machine.modular;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StarTModularCombustionModuleMachine extends WorkableElectricMultiblockMachine {

    private final List<ResourceLocation> acceptedFrameIds;

    public StarTModularCombustionModuleMachine(IMachineBlockEntity holder, ResourceLocation... acceptedFrameIds) {
        super(holder);
        this.acceptedFrameIds = List.copyOf(Arrays.asList(acceptedFrameIds));
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        addFrameIds(acceptedFrameIds);
    }

    @Override
    public void onStructureInvalid() {
        addFrameIds(Collections.emptyList());
        super.onStructureInvalid();
    }

    private void addFrameIds(List<ResourceLocation> ids) {
        for (IMultiPart part : getParts()) {
            if (part instanceof StarTModularInterfaceHatchPartMachine hatch
                    && hatch.isTerminal()) {
                hatch.setSupportedModules(ids);
            }
        }
    }
}
