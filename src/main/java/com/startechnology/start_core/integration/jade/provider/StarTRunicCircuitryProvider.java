package com.startechnology.start_core.integration.jade.provider;

import com.gregtechceu.gtceu.integration.jade.provider.CapabilityBlockProvider;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.capability.StarTCapabilityHelper;
import com.startechnology.start_core.integration.jade.StarTJadeUtils;
import com.startechnology.start_core.machine.runic_circuitry.RunicCircuitryAssemblyStationMachine;
import com.startechnology.start_core.machine.runic_circuitry.RunicCircuitryAssemblyStationMachine.LensValidationStatus;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class StarTRunicCircuitryProvider extends CapabilityBlockProvider<RunicCircuitryAssemblyStationMachine> {

    public StarTRunicCircuitryProvider() {
        super(StarTCore.resourceLocation("runic_circuitry_info"));
    }

    @Override
    protected @Nullable RunicCircuitryAssemblyStationMachine getCapability(Level level, BlockPos pos,
                                                                           @Nullable Direction side) {
        return StarTCapabilityHelper.getRunicCircuitryAssemblyStationMachine(level, pos, side);
    }

    @Override
    protected void write(CompoundTag data, RunicCircuitryAssemblyStationMachine capability) {
        data.putBoolean("formed", capability.isFormed());
        data.putInt("requiredLensIndex", capability.getRequiredLensIndex());
        data.putInt("activeLensIndex", capability.getActiveLensIndex());
        data.putBoolean("working", capability.getRecipeLogic().isWorking());
        data.putInt("validationStatus", capability.getValidationStatus().ordinal());
    }

    @Override
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, Player player, BlockAccessor block,
                              BlockEntity blockEntity, IPluginConfig config) {
        if (!StarTJadeUtils.hasData(capData, "formed", "requiredLensIndex", "activeLensIndex", "working",
                "validationStatus") || !capData.getBoolean("formed")) {
            return;
        }

        RunicCircuitryAssemblyStationMachine.appendLensDisplayLines(
                tooltip::add,
                capData.getInt("requiredLensIndex"),
                capData.getInt("activeLensIndex"),
                capData.getBoolean("working"),
                LensValidationStatus.of(capData.getInt("validationStatus")));
    }
}
