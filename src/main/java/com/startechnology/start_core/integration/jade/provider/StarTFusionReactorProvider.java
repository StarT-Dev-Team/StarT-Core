package com.startechnology.start_core.integration.jade.provider;

import com.gregtechceu.gtceu.integration.jade.provider.CapabilityBlockProvider;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.capability.StarTCapabilityHelper;
import com.startechnology.start_core.api.reflector.FusionReflectorType;
import com.startechnology.start_core.machine.fusion.ReflectorFusionReactorMachine;
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

import java.util.Optional;

public class StarTFusionReactorProvider extends CapabilityBlockProvider<ReflectorFusionReactorMachine> {

    public StarTFusionReactorProvider() {
        super(StarTCore.resourceLocation("fusion_reactor_info"));
    }

    @Override
    protected @Nullable ReflectorFusionReactorMachine getCapability(Level level, BlockPos pos, @Nullable Direction side) {
        return StarTCapabilityHelper.getFusionReactorMachine(level, pos, side);
    }

    @Override
    protected void write(CompoundTag data, ReflectorFusionReactorMachine capability) {
        data.putInt("reflector_tier", Optional.ofNullable(capability.getReflectorType()).map(FusionReflectorType::getTier).orElse(0));
    }

    @Override
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, Player player, BlockAccessor block, BlockEntity blockEntity, IPluginConfig config) {
        if (!capData.contains("reflector_tier")) return;
        int tier = capData.getInt("reflector_tier");
        if (tier <= 0) return;
        tooltip.add(Component.translatable("ui.start_core.fusion_reactor.reflector_tier_info", "T%d".formatted(tier)));
    }
}
