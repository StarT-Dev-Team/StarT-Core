package com.startechnology.start_core.integration.jade.provider;

import com.gregtechceu.gtceu.integration.jade.provider.CapabilityBlockProvider;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.capability.StarTCapabilityHelper;
import com.startechnology.start_core.machine.vacuumpump.VacuumPumpPartMachine;
import com.startechnology.start_core.machine.vcr.VacuumChemicalReactorMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class StarTVacuumChemicalReactorProvider extends CapabilityBlockProvider<VacuumChemicalReactorMachine> {

    public StarTVacuumChemicalReactorProvider() {
        super(StarTCore.resourceLocation("vacuum_chemical_reactor_info"));
    }

    @Override
    protected @Nullable VacuumChemicalReactorMachine getCapability(Level level, BlockPos pos, @Nullable Direction side) {
        return StarTCapabilityHelper.getVacuumChemicalReactorMachine(level, pos, side);
    }

    @Override
    protected void write(CompoundTag data, VacuumChemicalReactorMachine capability) {
        data.putInt("vcr_pump_cap", capability.getPump().getPumpCap());
        data.putInt("vcr_pump_rate", capability.getPump().getPumpRate());
        data.putFloat("vcr_vacuum_amount", capability.getVacuumAmount());
        data.putInt("vcr_vacuum_status", capability.getVacuumStatus().ordinal());
    }

    @Override
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, Player player, BlockAccessor block, BlockEntity blockEntity, IPluginConfig config) {
        if (!capData.contains("vcr_pump_cap") || !capData.contains("vcr_pump_rate") || !capData.contains("vcr_vacuum_amount") || !capData.contains("vcr_vacuum_status"))
            return;

        var amount = capData.getFloat("vcr_vacuum_amount");
        var status = VacuumChemicalReactorMachine.Status.of(capData.getInt("vcr_vacuum_status"));
        tooltip.add(Component.literal("")
                .append(Component.translatable("ui.start_core.vcr.vacuum_status", VacuumChemicalReactorMachine.formatVacuumStatus(status)))
                .append(", ")
                .append(Component.translatable("ui.start_core.vcr.vacuum_amount", VacuumChemicalReactorMachine.formatVacuumAmount(amount)))
        );

        var cap = capData.getInt("vcr_pump_cap");
        var rate = capData.getInt("vcr_pump_rate");
        tooltip.add(Component.literal("")
                .append(Component.translatable("ui.start_core.vcr.pump_type.cap", VacuumPumpPartMachine.formatVacuumPumpCap(cap)))
                .append(", ")
                .append(Component.translatable("ui.start_core.vcr.pump_type.rate", VacuumPumpPartMachine.formatVacuumPumpRate(rate)))
        );
    }
}
