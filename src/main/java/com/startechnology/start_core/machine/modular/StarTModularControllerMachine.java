package com.startechnology.start_core.machine.modular;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.startechnology.start_core.api.capability.IStarTDreamLinkNetworkRecieveEnergy;
import com.startechnology.start_core.api.capability.StarTCapabilityHelper;

import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import net.minecraft.resources.ResourceLocation;

public class StarTModularControllerMachine extends WorkableElectricMultiblockMachine {

    protected List<ResourceLocation> supportedMultiblockIds;

    private EnergyContainerList inputHatches;
    private EnergyContainerList outputConduits;

    protected ConditionalSubscriptionHandler tickSubscription;
    protected TickableSubscription tryTickSub;

    private boolean readyToUpdate;


    public StarTModularControllerMachine(IMachineBlockEntity holder, ResourceLocation... supportedMultiblockIds) {
        super(holder);
        this.supportedMultiblockIds = Arrays.asList(supportedMultiblockIds);
        this.readyToUpdate = false;
        this.tickSubscription = new ConditionalSubscriptionHandler(this, this::transferModuleInterfacesTick, this::isFormed);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        List<IEnergyContainer> inputs = new ArrayList<>();
        List<IEnergyContainer> outputs = new ArrayList<>();

        Map<Long, IO> ioMap = getMultiblockState().getMatchContext().getOrCreate("ioMap", Long2ObjectMaps::emptyMap);

        for (IMultiPart part : getParts()) {
            if (part instanceof StarTModularInterfaceHatchPartMachine interfaceHatch) {
                interfaceHatch.setSupportedModules(new ArrayList<>(supportedMultiblockIds));
                
                if (part instanceof StarTModularConduitHatchPartMachine conduitMachine) {
                    outputs.add(conduitMachine.energyContainer);
                }
            }

            IO io = ioMap.getOrDefault(part.self().getPos().asLong(), IO.IN);
            if (io == IO.NONE) continue;

            for (var handler : part.getRecipeHandlers()) {
                var handlerIO = handler.getHandlerIO();
                // If IO not compatible
                if (io != IO.IN && handlerIO != IO.IN && io != handlerIO) continue;
                if (handler.hasCapability(EURecipeCapability.CAP) &&
                        handler instanceof IEnergyContainer container) {
                    
                    inputs.add(container);
                    traitSubscriptions.add(handler.subscribe(tickSubscription::updateSubscription));
                }
            }
        }

        this.outputConduits = new EnergyContainerList(outputs);
        this.inputHatches = new EnergyContainerList(inputs);
        this.readyToUpdate = true;
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (getLevel().isClientSide)
            return;

        tryTickSub = subscribeServerTick(tryTickSub, this::tryTransferConduitEnergy);
    }

    @Override
    public void onUnload() {
        super.onUnload();
    
        if (getLevel().isClientSide)
            return;

        if (tryTickSub != null) {
            tryTickSub.unsubscribe();
            tryTickSub = null;

            tickSubscription.unsubscribe();
            tickSubscription = null;
        }
    }

    protected void tryTransferConduitEnergy() {
        // Transfer energy tick only every 3 seconds (same as dream-link)
        if (getOffsetTimer() % 60 == 0 && this.readyToUpdate) {
            transferModuleInterfacesTick();
        }
    }
    
    protected void transferModuleInterfacesTick() {
        if (getLevel().isClientSide || !this.readyToUpdate || !isWorkingEnabled())
            return;

        /* Transfer from the input hatches to the output conduits */
        long energyStored = inputHatches.getEnergyStored();
        if (energyStored <= 0) return;

        long totalEnergyTransferred = outputConduits.changeEnergy(inputHatches.getEnergyStored());
        
        if (totalEnergyTransferred > 0) {
            inputHatches.removeEnergy(totalEnergyTransferred);
        }
    }

}
