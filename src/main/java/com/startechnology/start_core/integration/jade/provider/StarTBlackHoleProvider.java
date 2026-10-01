package com.startechnology.start_core.integration.jade.provider;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.integration.jade.StarTJadeUtils;
import com.startechnology.start_core.machine.black_hole.BlackHoleBalance;
import com.startechnology.start_core.machine.black_hole.BlackHoleGeneratorMachine;
import com.startechnology.start_core.machine.black_hole.BlackHolePhase;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class StarTBlackHoleProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    @Override
    public ResourceLocation getUid() {
        return StarTCore.resourceLocation("black_hole_generator");
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (accessor.getBlockEntity() instanceof MetaMachineBlockEntity blockEntity &&
                blockEntity.getMetaMachine() instanceof BlackHoleGeneratorMachine machine) {
            data.putInt("bhPhase", machine.getPhase().ordinal());
            data.putDouble("bhMass", machine.getMass());
            data.putDouble("bhStability", machine.getStability());
            data.putLong("bhOutput", machine.getOutputEUt());
        }
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        var data = accessor.getServerData();
        if (!StarTJadeUtils.hasData(data, "bhPhase", "bhMass", "bhStability", "bhOutput")) return;

        var phase = BlackHolePhase.byId(data.getInt("bhPhase"));
        tooltip.add(Component.translatable("start_core.machine.black_hole_generator.phase",
                Component.translatable("start_core.machine.black_hole_generator.phase." +
                        phase.name().toLowerCase())));
        if (phase != BlackHolePhase.STABLE) return;

        double stability = data.getDouble("bhStability");
        tooltip.add(Component.translatable("start_core.machine.black_hole_generator.jade",
                FormattingUtil.formatNumber2Places(data.getDouble("bhMass")),
                Math.round(stability * 100))
                .withStyle(stability < BlackHoleBalance.WARNING_STABILITY ? ChatFormatting.RED : ChatFormatting.GRAY));
        tooltip.add(StarTJadeUtils.euTDisplay(data.getLong("bhOutput"), accessor.getBlockEntity()));
    }
}
