package com.startechnology.start_core.integration.jade;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public abstract class MachineJadeProvider<M extends MetaMachine>
                                         implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    private final ResourceLocation uid;
    private final Class<M> machineClass;

    protected MachineJadeProvider(ResourceLocation uid, Class<M> machineClass) {
        this.uid = uid;
        this.machineClass = machineClass;
    }

    @Override
    public ResourceLocation getUid() {
        return this.uid;
    }

    @Nullable
    protected M getMachine(Level level, BlockPos pos) {
        MetaMachine machine = MetaMachine.getMachine(level, pos);
        return machineClass.isInstance(machine) ? machineClass.cast(machine) : null;
    }

    protected abstract void write(CompoundTag data, M machine);

    protected abstract void addTooltip(CompoundTag data, ITooltip tooltip, BlockAccessor accessor,
                                       IPluginConfig config);

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor accessor) {
        M machine = this.getMachine(accessor.getLevel(), accessor.getPosition());
        if (machine != null) {
            CompoundTag data = new CompoundTag();
            this.write(data, machine);
            compoundTag.put(uid.toString(), data);
        }
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag serverData = accessor.getServerData();
        if (serverData.contains(uid.toString())) {
            this.addTooltip(serverData.getCompound(uid.toString()), tooltip, accessor, config);
        }
    }
}
