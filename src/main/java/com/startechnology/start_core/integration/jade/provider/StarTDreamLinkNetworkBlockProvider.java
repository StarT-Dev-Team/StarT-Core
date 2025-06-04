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

public class StarTDreamLinkNetworkBlockProvider extends CapabilityBlockProvider<IStarTDreamLinkNetworkMachine> {

    public StarTDreamLinkNetworkBlockProvider() {
        super(StarTCore.resourceLocation("dream_link_network_info"));
    }

    @Override
    protected @Nullable IStarTDreamLinkNetworkMachine getCapability(Level level, BlockPos pos,
            @Nullable Direction side) {
        var capability = StarTCapabilityHelper.getDreamLinkNetworkMachine(level, pos, side);

        if (capability != null)
            return capability;

        if (MetaMachine.getMachine(level, pos) instanceof IMultiController controller) {
            for (var part : controller.getParts()) {
                if (part instanceof IStarTDreamLinkNetworkMachine dreamLinkNetworkMachine) {
                    return dreamLinkNetworkMachine;
                }
            }
        }
        return null;
    }

    /* Used for storing data for the addTooltip method ? */
    @Override
    protected void write(CompoundTag data, IStarTDreamLinkNetworkMachine capability) {
        data.putString("network", capability.getNetwork());
        data.putBoolean("dreaming", capability.isDreaming());
    }

    /* Adds a new tooltip under the Jade stuff */
    @Override
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, Player player, BlockAccessor block,
            BlockEntity blockEntity, IPluginConfig config) {
        if (capData.contains("network") && capData.contains("dreaming"))
        {
            String network = capData.getString("network");
            Boolean dreaming = capData.getBoolean("dreaming");

            if (dreaming)
                tooltip.add(Component.translatable("start_core.machine.dream_link.active_network", network));
            else
                tooltip.add(Component.translatable("start_core.machine.dream_link.inactive_network", network));
        }
    }
    
}
