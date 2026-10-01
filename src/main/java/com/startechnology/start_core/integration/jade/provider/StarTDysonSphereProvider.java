package com.startechnology.start_core.integration.jade.provider;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.integration.jade.StarTJadeUtils;
import com.startechnology.start_core.machine.dyson_sphere.DysonSphereMachine;
import com.startechnology.start_core.machine.dyson_sphere.StellarPhase;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class StarTDysonSphereProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    @Override
    public ResourceLocation getUid() {
        return StarTCore.resourceLocation("dyson_sphere");
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (accessor.getBlockEntity() instanceof MetaMachineBlockEntity blockEntity &&
                blockEntity.getMetaMachine() instanceof DysonSphereMachine machine) {
            data.putInt("dsPhase", machine.getPhase().ordinal());
            data.putDouble("dsMass", machine.getMass());
            data.putLong("dsOutput", machine.getOutputEUt());
        }
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        var data = accessor.getServerData();
        if (!StarTJadeUtils.hasData(data, "dsPhase", "dsMass", "dsOutput")) return;

        var phase = StellarPhase.byId(data.getInt("dsPhase"));
        tooltip.add(Component.translatable("start_core.machine.dyson_sphere.phase",
                DysonSphereMachine.phaseName(phase)));
        if (phase == StellarPhase.IDLE) return;

        tooltip.add(Component.translatable("start_core.machine.dyson_sphere.mass",
                FormattingUtil.formatNumber2Places((float) data.getDouble("dsMass"))).withStyle(ChatFormatting.GRAY));
        if (phase.isBurning()) tooltip.add(StarTJadeUtils.euTDisplay(data.getLong("dsOutput"),
                accessor.getBlockEntity()));
    }
}
