package com.startechnology.start_core.integration.jade.provider;

import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.integration.jade.provider.CapabilityBlockProvider;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.capability.StarTCapabilityHelper;
import com.startechnology.start_core.integration.jade.StarTJadeUtils;
import com.startechnology.start_core.machine.bulking.IBulking;

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

public class StarTBulkingProvider extends CapabilityBlockProvider<IBulking> {

    public StarTBulkingProvider() {
        super(StarTCore.resourceLocation("bulking_info"));
    }

    @Override
    protected @Nullable IBulking getCapability(Level level, BlockPos pos, @Nullable Direction side) {
        return StarTCapabilityHelper.getBulkingMachine(level, pos, side);
    }

    @Override
    protected void write(CompoundTag data, IBulking capability) {
        data.putString("bulking_type", capability.getBulkingType().name);
        data.putBoolean("forced_bulking", capability.isForcedBulking());
    }

    @Override
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, Player player, BlockAccessor block,
                              BlockEntity blockEntity, IPluginConfig config) {
        if (StarTJadeUtils.hasData(capData, "bulking_type", "forced_bulking")) {
            String bulkingType = capData.getString("bulking_type");
            tooltip.add(Component.translatable("ui.start_core.bulking.type", bulkingType));
            boolean forcedBulking = capData.getBoolean("forced_bulking");
            tooltip.add(Component.translatable("config.jade.plugin_start_core.forced_bulking",
                    Component.translatable((forcedBulking) ? "start_core.util.enabled" : "start_core.util.disabled")));
        }
    }
}
