package com.startechnology.start_core.machine.komaru;

import java.util.ArrayList;
import java.util.List;

import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IWorkableMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableLaserContainer;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.common.machine.multiblock.part.LaserHatchPartMachine;
import com.startechnology.start_core.machine.modular.StarTModularConduitAutoScalingHatchPartMachine;
import com.startechnology.start_core.mixin.LaserHatchPartMachineAccessor;

import net.minecraft.resources.ResourceLocation;

public class StarTKomaruFrameMachine extends WorkableElectricMultiblockMachine {

    protected List<StarTModularConduitAutoScalingHatchPartMachine> basicTerminals;
    protected List<StarTModularConduitAutoScalingHatchPartMachine> advancedTerminals;

    /* Lists for easy transfer to terminals */
    protected EnergyContainerList terminals;
    protected List<NotifiableEnergyContainer> terminalContainers;
    protected boolean readyToUpdate = false;

    private NotifiableLaserContainer inputLaserContainer = null;

    protected ConditionalSubscriptionHandler tickSubscription;
    protected TickableSubscription tryTickSub;

    public StarTKomaruFrameMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
    
        /* Gather the different terminals for each type */
        basicTerminals = this.getMultiblockState().getMatchContext().getOrDefault(StarTKomaruPredicates.BASIC_STORAGE_KEY, new ArrayList<>());
        advancedTerminals = this.getMultiblockState().getMatchContext().getOrDefault(StarTKomaruPredicates.ADVANCED_STORAGE_KEY, new ArrayList<>());

        /* Get the first laser hatch for input to KOMARU */
        for (IMultiPart part : getParts()) {
            if (part instanceof LaserHatchPartMachine laserHatch) {
                inputLaserContainer = ((LaserHatchPartMachineAccessor) laserHatch).start_core$getLaserContainer();
                inputLaserContainer.addChangedListener(this::transferModuleInterfacesTick);
                break;
            }
        }

        terminalContainers = new ArrayList<>();
        this.setupTerminals();
        terminals = new EnergyContainerList(terminalContainers);
        this.readyToUpdate = true;
    }

    private long getBasicAmperage() {
        long inputAmperage = this.inputLaserContainer.getInputAmperage();

        if (inputAmperage < 1024) {
            return 4;
        }

        else if (inputAmperage < 4096) {
            return 16;
        }

        else {
            return 64;
        }
    }


    private long getAdvancedAmperage() {
        long inputAmperage = this.inputLaserContainer.getInputAmperage();

        if (inputAmperage < 1024) {
            return 16;
        }

        else if (inputAmperage < 4096) {
            return 64;
        }

        else {
            return 256;
        }
    }

    private long getModuleVoltage() {
        return this.inputLaserContainer.getInputVoltage();
    }

    private ModifierFunction moduleRecipeModifierExample(MetaMachine machine, GTRecipe recipe) {
        /* blah blah blah recipe modifier */
        return ModifierFunction.builder()
            .durationMultiplier(2)
            .build();
    }

    private void afterBasicModuleWorking(IWorkableMultiController controller) {
        /* Do some logic after basic or advanced thingo works */
    }

    private void afterAdvancedModuleWorking(IWorkableMultiController controller) {
        /* Do some logic after basic or advanced thingo works */
    }

    private void setupTerminals() {
        for (StarTModularConduitAutoScalingHatchPartMachine basicTerminal : basicTerminals) {
            basicTerminal.setSupportedModules(List.of(new ResourceLocation("gtceu:basic_type_module")));
            basicTerminal.resetSupportedModule();

            if (this.inputLaserContainer != null) {
                basicTerminal.scaleNewEnergyContainer(getModuleVoltage(), getBasicAmperage());
                terminalContainers.add(basicTerminal.getEnergyContainer());

                basicTerminal.setSupportedMachineConsumer(basicNode -> {
                    /* Should be a StarTModularConduitAutoScalingHatchPartMachine */
                    if (basicNode instanceof StarTModularConduitAutoScalingHatchPartMachine autoScalingHatch) {
                        autoScalingHatch.scaleNewEnergyContainer(getModuleVoltage(), getBasicAmperage());
                        autoScalingHatch.setRecipeModifier(this::moduleRecipeModifierExample);
                        autoScalingHatch.setModuleAfterWorkConsumer(this::afterBasicModuleWorking);
                    }
                });
            }
        }

        for (StarTModularConduitAutoScalingHatchPartMachine advancedTerminal : advancedTerminals) {
            advancedTerminal.setSupportedModules(List.of(new ResourceLocation("gtceu:advanced_type_module")));
            advancedTerminal.resetSupportedModule();
            
            if (this.inputLaserContainer != null) {
                advancedTerminal.scaleNewEnergyContainer(getModuleVoltage(), getAdvancedAmperage());
                terminalContainers.add(advancedTerminal.getEnergyContainer());

                advancedTerminal.setSupportedMachineConsumer(advancedNode -> {
                    if (advancedNode instanceof StarTModularConduitAutoScalingHatchPartMachine autoScalingHatch) {
                        autoScalingHatch.scaleNewEnergyContainer(getModuleVoltage(), getAdvancedAmperage());
                        autoScalingHatch.setRecipeModifier(this::moduleRecipeModifierExample);
                        autoScalingHatch.setModuleAfterWorkConsumer(this::afterAdvancedModuleWorking);
                    }
                });
            }
        }
    }


    @Override
    public void onLoad() {
        super.onLoad();

        if (getLevel().isClientSide)
            return;

        tryTickSub = subscribeServerTick(tryTickSub, this::tryTransferTerminalEnergy);
    }

    @Override
    public void onUnload() {
        super.onUnload();
    
        if (getLevel().isClientSide)
            return;

        if (tryTickSub != null) {
            tryTickSub.unsubscribe();
            tryTickSub = null;
        }

        if (tickSubscription != null) {
            tickSubscription.unsubscribe();
            tickSubscription = null;
        }
    }

    protected void tryTransferTerminalEnergy() {
        // Transfer energy tick only every 3 seconds 
        if (getOffsetTimer() % 60 == 0) {
            transferModuleInterfacesTick();
        }
    }
    
    protected void transferModuleInterfacesTick() {
        if (getLevel().isClientSide || !this.readyToUpdate || !isWorkingEnabled() || inputLaserContainer == null)
            return;

        /* Transfer from the input hatches to the output conduits */
        long energyStored = inputLaserContainer.getEnergyStored();
        if (energyStored <= 0) return;

        long totalEnergyTransferred = terminals.changeEnergy(energyStored);
        
        if (totalEnergyTransferred > 0) {
            inputLaserContainer.removeEnergy(totalEnergyTransferred);
        }
    }
}
