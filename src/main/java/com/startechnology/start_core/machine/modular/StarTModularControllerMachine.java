package com.startechnology.start_core.machine.modular;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StarTModularControllerMachine extends WorkableElectricMultiblockMachine {

    protected List<ResourceLocation> supportedMultiblockIds;

    protected EnergyContainerList inputHatches;
    protected EnergyContainerList outputConduits;

    protected ConditionalSubscriptionHandler tickSubscription;
    protected TickableSubscription tryTickSub;

    protected boolean readyToUpdate;

    public StarTModularControllerMachine(IMachineBlockEntity holder, ResourceLocation... supportedMultiblockIds) {
        super(holder);
        this.supportedMultiblockIds = Arrays.asList(supportedMultiblockIds);
        this.readyToUpdate = false;
        this.tickSubscription = new ConditionalSubscriptionHandler(this, this::transferModuleInterfacesTick,
                this::isFormed);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        List<IEnergyContainer> inputs = new ArrayList<>();
        List<IEnergyContainer> outputs = new ArrayList<>();

        Long2ObjectMap<IO> ioMap = getMultiblockState().getMatchContext().getOrCreate("ioMap",
                Long2ObjectMaps::emptyMap);

        for (IMultiPart part : getParts()) {
            if (part instanceof StarTModularInterfaceHatchPartMachine interfaceHatch) {
                interfaceHatch.setSupportedModules(new ArrayList<>(supportedMultiblockIds));

                if (part instanceof StarTModularConduitHatchPartMachine conduitMachine) {
                    outputs.add(conduitMachine.energyContainer);
                }
            }

            IO io = ioMap.getOrDefault(part.self().getPos().asLong(), IO.BOTH);
            if (io == IO.NONE) continue;

            var handlerLists = part.getRecipeHandlers();
            for (var handlerList : handlerLists) {
                if (!handlerList.isValid(io)) continue;

                var containers = handlerList.getCapability(EURecipeCapability.CAP).stream()
                        .filter(IEnergyContainer.class::isInstance)
                        .map(IEnergyContainer.class::cast)
                        .toList();

                if (handlerList.getHandlerIO().support(IO.IN)) {
                    inputs.addAll(containers);
                }

                traitSubscriptions
                        .add(handlerList.subscribe(tickSubscription::updateSubscription, EURecipeCapability.CAP));
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

        tryTickSub = subscribeServerTick(tryTickSub, this::transferEnergy);
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

    protected void transferEnergy() {
        // Transfer energy tick only every 3 seconds (same as dream-link)
        if (getOffsetTimer() % 60 == 0 && this.readyToUpdate) {
            // conduit i/o
            transferModuleInterfacesTick();
            transferToOutputs();
        }
    }

    protected boolean transferModuleInterfacesTick() {
        if (getLevel().isClientSide || !this.readyToUpdate || !isWorkingEnabled()) return false;

        /* Transfer from the input hatches to the output conduits */
        long energyStored = inputHatches.getEnergyStored();
        if (energyStored <= 0) return false;

        long totalEnergyTransferred = outputConduits.changeEnergy(inputHatches.getEnergyStored());

        if (totalEnergyTransferred > 0) {
            inputHatches.removeEnergy(totalEnergyTransferred);
            return true;
        }
        return false;
    }

    protected boolean transferToOutputs() {
        if (getLevel().isClientSide || !this.readyToUpdate || !isWorkingEnabled()) return false;

        long outputEnergy = outputConduits.getEnergyStored();
        if (outputEnergy <= 0) return false;

        var handlers = getOutputs();
        var transferred = handlers.addEnergy(outputEnergy);
        if (transferred > 0) {
            outputConduits.removeEnergy(transferred);
            return true;
        }
        return false;
    }

    protected EnergyContainerList getOutputs() {
        List<IEnergyContainer> containers = new ArrayList<>();
        List<IRecipeHandler<?>> handlers = this.getCapabilitiesFlat(IO.OUT, EURecipeCapability.CAP);

        for (var handler : handlers) {
            if (handler instanceof IEnergyContainer container && !isModularConduitContainer(container)) {
                containers.add(container);
            }
        }
        return new EnergyContainerList(containers);
    }

    private boolean isModularConduitContainer(IEnergyContainer container) {
        for (var part : getParts()) {
            if (part instanceof StarTModularConduitHatchPartMachine conduit &&
                    conduit.getEnergyContainer() == container) {
                return true;
            }
        }

        return false;
    }
}
