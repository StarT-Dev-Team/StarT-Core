package com.startechnology.start_core.integration.jade.provider;

import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMaintenanceMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.integration.jade.provider.CapabilityBlockProvider;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.capability.IStarTDreamLinkNetworkMachine;
import com.startechnology.start_core.api.capability.StarTCapabilityHelper;
import com.startechnology.start_core.machine.hellforge.StarTHellForgeMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class StarTHellforgeProvider extends CapabilityBlockProvider<StarTHellForgeMachine> {

    public StarTHellforgeProvider() {
        super(StarTCore.resourceLocation("hellforge_heat_info"));
    }

    @Override
    protected @Nullable StarTHellForgeMachine getCapability(Level level, BlockPos pos,
            @Nullable Direction side) {
        var capability = StarTCapabilityHelper.getHellforgeMachine(level, pos, side);

        if (capability != null)
            return capability;
            
        return null;
    }

    /* Used for storing data for the addTooltip method ? */
    @Override
    protected void write(CompoundTag data, StarTHellForgeMachine capability) {
        data.putInt("temperature", capability.getCrucibleTemperature());
    }

    /* Adds a new tooltip under the Jade stuff */
    @Override
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, Player player, BlockAccessor block,
            BlockEntity blockEntity, IPluginConfig config) {
        if (capData.contains("temperature"))
        {
            Integer temperature = capData.getInt("temperature");
            tooltip.add(Component.translatable("ui.start_core.hellforge_crucible", temperature));
        }
    }
    
}
