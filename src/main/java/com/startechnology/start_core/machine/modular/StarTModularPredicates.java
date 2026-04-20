package com.startechnology.start_core.machine.modular;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.pattern.MultiblockState;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.lowdragmc.lowdraglib.utils.BlockInfo;

import net.minecraft.world.level.block.entity.BlockEntity;

public class StarTModularPredicates {
    private static Predicate<MultiblockState> createKeyedAutoScalingConduitPredicate(String storageKey, IO io) {
        return (MultiblockState blockWorldState) -> {
            BlockEntity state = blockWorldState.getTileEntity();
        
            if (state instanceof IMachineBlockEntity machineBlockEntity &&
                    machineBlockEntity.getMetaMachine() instanceof StarTModularConduitAutoScalingHatchPartMachine interfaceHatchPartMachine
            ) {
                if (io == IO.OUT && !interfaceHatchPartMachine.isTerminal() || io == IO.IN && interfaceHatchPartMachine.isTerminal()) {
                    return false;
                }

                ArrayList<StarTModularConduitAutoScalingHatchPartMachine> interfaces = blockWorldState.getMatchContext().getOrDefault(storageKey, new ArrayList<>());
                
                interfaces.add(interfaceHatchPartMachine);
                blockWorldState.getMatchContext().set(storageKey, interfaces);
                return true;
            }
            return false;
        };
    }

    public static TraceabilityPredicate createKeyedAutoScalingTerminalPredicate(String storageKey) {
        return new TraceabilityPredicate(createKeyedAutoScalingConduitPredicate(storageKey, IO.OUT), () -> Arrays.asList(new MachineDefinition[]{
            StarTModularConnectionHatches.MODULAR_AUTO_SCALING_CONDUIT_TERMINAL
        }).stream()
                .map((MachineDefinition machineDefinition) -> new BlockInfo(machineDefinition.getBlock()))
                .toArray(BlockInfo[]::new));
    }
}
