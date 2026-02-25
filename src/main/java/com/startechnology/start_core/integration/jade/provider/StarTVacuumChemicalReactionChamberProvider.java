package com.startechnology.start_core.integration.jade.provider;

import com.gregtechceu.gtceu.integration.jade.provider.CapabilityBlockProvider;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.capability.StarTCapabilityHelper;
import com.startechnology.start_core.machine.vacuum_pump.VacuumPumpPartMachine;
import com.startechnology.start_core.machine.vcrc.VacuumChemicalReactionChamberMachine;
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

public class StarTVacuumChemicalReactionChamberProvider extends CapabilityBlockProvider<VacuumChemicalReactionChamberMachine> {

    public StarTVacuumChemicalReactionChamberProvider() {
        super(StarTCore.resourceLocation("vacuum_chemical_reaction_chamber_info"));
    }

    @Override
    protected @Nullable VacuumChemicalReactionChamberMachine getCapability(Level level, BlockPos pos, @Nullable Direction side) {
        return StarTCapabilityHelper.getVacuumChemicalReactionChamberMachine(level, pos, side);
    }

    @Override
    protected void write(CompoundTag data, VacuumChemicalReactionChamberMachine capability) {
        data.putInt("vcrc_pump_cap", capability.getPump().getPumpCap());
        data.putInt("vcrc_pump_rate", capability.getPump().getPumpRate());
        data.putFloat("vcrc_vacuum_amount", capability.getVacuumAmount());
        data.putInt("vcrc_vacuum_status", capability.getVacuumStatus().ordinal());
    }

    @Override
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, Player player, BlockAccessor block, BlockEntity blockEntity, IPluginConfig config) {
        if (!capData.contains("vcrc_pump_cap") || !capData.contains("vcrc_pump_rate") || !capData.contains("vcrc_vacuum_amount") || !capData.contains("vcrc_vacuum_status"))
            return;

        var amount = capData.getFloat("vcrc_vacuum_amount");
        var status = VacuumChemicalReactionChamberMachine.Status.of(capData.getInt("vcrc_vacuum_status"));
        tooltip.add(Component.literal("")
                .append(Component.translatable("ui.start_core.vcrc.vacuum_status", VacuumChemicalReactionChamberMachine.formatVacuumStatus(status)))
                .append(", ")
                .append(Component.translatable("ui.start_core.vcrc.vacuum_amount", VacuumChemicalReactionChamberMachine.formatVacuumAmount(amount)))
        );

        var cap = capData.getInt("vcrc_pump_cap");
        var rate = capData.getInt("vcrc_pump_rate");
        tooltip.add(Component.literal("")
                .append(Component.translatable("ui.start_core.vcrc.pump_type.cap", VacuumPumpPartMachine.formatVacuumPumpCap(cap)))
                .append(", ")
                .append(Component.translatable("ui.start_core.vcrc.pump_type.rate", VacuumPumpPartMachine.formatVacuumPumpRate(rate)))
        );
    }
}
