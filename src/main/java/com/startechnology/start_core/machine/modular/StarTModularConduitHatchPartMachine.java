package com.startechnology.start_core.machine.modular;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IDataInfoProvider;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;
import com.gregtechceu.gtceu.common.item.PortableScannerBehavior;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.startechnology.start_core.api.capability.IStarTDreamLinkNetworkMachine;
import com.startechnology.start_core.api.capability.StarTNotifiableDreamLinkContainer;
import com.startechnology.start_core.machine.dreamlink.StarTDreamLinkHatchPartMachine;

import net.minecraft.network.chat.Component;

public class StarTModularConduitHatchPartMachine extends StarTModularInterfaceHatchPartMachine implements IDataInfoProvider  {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(StarTModularConduitHatchPartMachine.class,
        StarTModularInterfaceHatchPartMachine.MANAGED_FIELD_HOLDER);
    
    @Persisted
    @Nullable
    protected NotifiableEnergyContainer energyContainer;

    public StarTModularConduitHatchPartMachine(IMachineBlockEntity holder, IO io, int tier, int amperage) {
        super(holder, io, tier);

        long maxCapacity = GTValues.V[tier] * 64L * amperage;
        long maxIOVoltage = GTValues.V[tier];

        if (this.io == IO.IN) {
            this.energyContainer = NotifiableEnergyContainer.receiverContainer(this, maxCapacity, maxIOVoltage, amperage);
            this.energyContainer.setSideInputCondition(s -> s == getFrontFacing());
        } else {
            this.energyContainer = NotifiableEnergyContainer.emitterContainer(this, maxCapacity, maxIOVoltage, amperage);
            this.energyContainer.setSideOutputCondition(s -> s == getFrontFacing());
        }
    }

    @Override
    public boolean canShared() {
        return false;
    }

    @Override
    public List<Component> getDataInfo(PortableScannerBehavior.DisplayMode mode) {
        if (mode == PortableScannerBehavior.DisplayMode.SHOW_ALL ||
                mode == PortableScannerBehavior.DisplayMode.SHOW_ELECTRICAL_INFO) {
            return Collections.singletonList(Component.translatable(
                    String.format("%d/%d EU", energyContainer.getEnergyStored(), energyContainer.getEnergyCapacity())));
        }
        return new ArrayList<>();
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public int tintColor(int index) {
        if (index == 2) {
            return GTValues.VC[getTier()];
        }
        return super.tintColor(index);
    }
}
