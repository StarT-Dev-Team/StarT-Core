package com.startechnology.start_core.api.capability;

import java.util.UUID;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableRecipeHandlerTrait;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.startechnology.start_core.machine.dreamlink.StarTDreamLinkHatchPartMachine;
import com.startechnology.start_core.machine.dreamlink.StarTDreamLinkManager;

import net.minecraft.core.Direction;

public class StarTNotifiableDreamLinkContainer extends NotifiableEnergyContainer {

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
        StarTNotifiableDreamLinkContainer .class, NotifiableRecipeHandlerTrait.MANAGED_FIELD_HOLDER);

    private TickableSubscription addTickSubscription;

    public StarTNotifiableDreamLinkContainer(MetaMachine machine, long maxCapacity, long maxInputVoltage,
            long maxInputAmperage, long maxOutputVoltage, long maxOutputAmperage) {
        super(machine, maxCapacity, maxInputVoltage, maxInputAmperage, maxOutputVoltage, maxOutputAmperage);
    }

    /* Shorthand for reciever version constructor */
    public static StarTNotifiableDreamLinkContainer receiverContainer(MetaMachine machine, long maxCapacity,
                                                             long maxInputVoltage, long maxInputAmperage) {
        return new StarTNotifiableDreamLinkContainer(machine, maxCapacity, maxInputVoltage, maxInputAmperage, 0L, 0L);
    }

    @Override
    public void onMachineLoad() {
        super.onMachineLoad();

        if (getMachine().getLevel().isClientSide)
            return;

        addTickSubscription = machine.subscribeServerTick(addTickSubscription, this::addToTreeSubscription);
    }

    protected void addToTreeSubscription() {
        if (machine.getOffsetTimer() % 5 == 0) {
            if (machine.getHolder().getOwner() != null && machine.getHolder().getOwner().getUUID() != null) {
                UUID ownerUUID = machine.getHolder().getOwner().getUUID();
                StarTDreamLinkManager.addDevice((StarTDreamLinkHatchPartMachine)getMachine(), ownerUUID);

                this.addTickSubscription.unsubscribe();
                this.addTickSubscription = null;
            }
        }
    }

    @Override
    public void onMachineUnLoad() {
        super.onMachineUnLoad();

        if (getMachine().getLevel().isClientSide)
            return;

        if (machine.getHolder().getOwner() != null && machine.getHolder().getOwner().getUUID() != null) {
            UUID ownerUUID = machine.getHolder().getOwner().getUUID();
            StarTDreamLinkManager.removeDevice((StarTDreamLinkHatchPartMachine)getMachine(), ownerUUID);
        }
    }

    /* Disable input from all sides */
    @Override
    public boolean inputsEnergy(Direction side) {
        return false;
    }


}