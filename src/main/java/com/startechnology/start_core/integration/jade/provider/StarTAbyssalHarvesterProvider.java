package com.startechnology.start_core.integration.jade.provider;

import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.integration.jade.provider.CapabilityBlockProvider;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.capability.StarTCapabilityHelper;
import com.startechnology.start_core.machine.abyssal_harvester.StarTAbyssalHarvesterMachine;
import com.startechnology.start_core.machine.hellforge.StarTHellForgeMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class StarTAbyssalHarvesterProvider extends CapabilityBlockProvider<StarTAbyssalHarvesterMachine> {
    
    public StarTAbyssalHarvesterProvider() {
        super(StarTCore.resourceLocation("abyssal_harvester_info"));
    }

    @Override
    protected @Nullable StarTAbyssalHarvesterMachine getCapability(Level level, BlockPos pos,
            @Nullable Direction side) {
        var capability = StarTCapabilityHelper.getAbyssalHarvesterMachine(level, pos, side);

        if (capability != null)
            return capability;
            
        return null;
    }

    /* Used for storing data for the addTooltip method ? */
    @Override
    protected void write(CompoundTag data, StarTAbyssalHarvesterMachine capability) {
        data.putInt("entropy", capability.getEntropy());
    }

    /* Adds a new tooltip under the Jade stuff */
    @Override
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, Player player, BlockAccessor block,
            BlockEntity blockEntity, IPluginConfig config) {
        if (capData.contains("entropy"))
        {
            Integer entropy = capData.getInt("entropy");
            tooltip.add(Component.translatable("ui.start_core.abyssal_harvester", entropy));
        }
    }
    
}
