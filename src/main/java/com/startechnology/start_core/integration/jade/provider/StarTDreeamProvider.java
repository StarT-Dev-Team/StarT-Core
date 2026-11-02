package com.startechnology.start_core.integration.jade.provider;

import com.gregtechceu.gtceu.integration.jade.provider.CapabilityBlockProvider;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.capability.StarTCapabilityHelper;
import com.startechnology.start_core.integration.jade.StarTJadeUtils;
import com.startechnology.start_core.machine.dreeam.StarTDreeamMachine;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Locale;

public class StarTDreeamProvider extends CapabilityBlockProvider<StarTDreeamMachine> {

    public StarTDreeamProvider() {
        super(StarTCore.resourceLocation("dreeam_link_info"));
    }

    @Override
    protected @Nullable StarTDreeamMachine getCapability(Level level, BlockPos pos, @Nullable Direction side) {
        return StarTCapabilityHelper.getDreeamMachine(level, pos, side);
    }

    @Override
    protected void write(CompoundTag data, StarTDreeamMachine capability) {
        data.putBoolean("formed", capability.isFormed());
        data.putString("role", capability.getRole().name());
        data.putString("state", capability.getLinkState().name());
    }

    @Override
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, Player player, BlockAccessor block,
                              BlockEntity blockEntity, IPluginConfig config) {
        if (StarTJadeUtils.hasData(capData, "formed", "role", "state") && capData.getBoolean("formed")) {
            tooltip.add(Component.translatable(
                    "start_core.machine.dreeam.role." + capData.getString("role").toLowerCase(Locale.ROOT)));
            tooltip.add(Component.translatable(
                    "start_core.machine.dreeam.state." + capData.getString("state").toLowerCase(Locale.ROOT)));
        }
    }
}
