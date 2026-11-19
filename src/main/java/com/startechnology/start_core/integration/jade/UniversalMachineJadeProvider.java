package com.startechnology.start_core.integration.jade;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.machine.IStarTJadeDisplayable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class UniversalMachineJadeProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    public static final ResourceLocation UID = StarTCore.resourceLocation("machine_display");
    public static final UniversalMachineJadeProvider INSTANCE = new UniversalMachineJadeProvider();

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor accessor) {
        if (accessor.getBlockEntity() instanceof IMachineBlockEntity holder &&
                holder.getMetaMachine() instanceof IStarTJadeDisplayable displayable) {
            CompoundTag data = new CompoundTag();
            displayable.writeJadeData(data);
            if (!data.isEmpty()) {
                compoundTag.put(UID.toString(), data);
            }
        }
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag serverData = accessor.getServerData();
        if (serverData.contains(UID.toString()) &&
                accessor.getBlockEntity() instanceof IMachineBlockEntity holder &&
                holder.getMetaMachine() instanceof IStarTJadeDisplayable displayable) {
            displayable.appendJadeTooltip(serverData.getCompound(UID.toString()), tooltip::add);
        }
    }
}
