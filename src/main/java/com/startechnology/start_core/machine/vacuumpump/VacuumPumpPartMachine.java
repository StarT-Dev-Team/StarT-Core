package com.startechnology.start_core.machine.vacuumpump;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredPartMachine;
import com.startechnology.start_core.machine.vcr.VacuumChemicalReactorMachine;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class VacuumPumpPartMachine extends TieredPartMachine implements IVacuumPump {

    public VacuumPumpPartMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier);
    }

    public static int getVacuumCap(int tier) {
        return switch(tier) {
            case GTValues.UV -> 85;
            case GTValues.UHV -> 90;
            case GTValues.UEV -> 95;
            case GTValues.UIV -> 100;
            default -> 80; // ZPM
        };
    }

    public static int getVacuumRate(int tier) {
        return switch(tier) {
            case GTValues.UV -> 10;
            case GTValues.UHV -> 15;
            case GTValues.UEV -> 20;
            case GTValues.UIV -> 25;
            default -> 5; // ZPM
        };
    }

    public static Component formatVacuumPumpCap(int cap) {
        var status = cap == 100 ? VacuumChemicalReactorMachine.Status.FULL_VACUUM : VacuumChemicalReactorMachine.Status.PARTIAL_VACUUM;
        return Component.literal(cap + "%").withStyle(status.getColor());
    }

    public static Component formatVacuumPumpRate(int rate) {
        return Component.literal(rate + "%");
    }


    @Override
    public boolean shouldOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        return false;
    }

    @Override
    public int getPumpCap() {
        return getVacuumCap(this.tier);
    }

    @Override
    public int getPumpRate() {
        return getVacuumRate(this.tier);
    }
}
