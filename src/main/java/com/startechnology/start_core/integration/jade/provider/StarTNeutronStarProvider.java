package com.startechnology.start_core.integration.jade.provider;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.integration.jade.StarTJadeUtils;
import com.startechnology.start_core.machine.neutron_star.NeutronStarBalance;
import com.startechnology.start_core.machine.neutron_star.NeutronStarForgeMachine;
import com.startechnology.start_core.machine.neutron_star.NeutronStarPhase;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class StarTNeutronStarProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    @Override
    public ResourceLocation getUid() {
        return StarTCore.resourceLocation("neutron_star_forge");
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (accessor.getBlockEntity() instanceof MetaMachineBlockEntity blockEntity &&
                blockEntity.getMetaMachine() instanceof NeutronStarForgeMachine machine) {
            data.putInt("nsPhase", machine.getPhase().ordinal());
            data.putDouble("nsSpin", machine.getSpin());
            data.putDouble("nsMass", machine.getMass());
        }
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        var data = accessor.getServerData();
        if (!StarTJadeUtils.hasData(data, "nsPhase", "nsSpin", "nsMass")) return;

        var phase = NeutronStarPhase.byId(data.getInt("nsPhase"));
        tooltip.add(Component.translatable("start_core.machine.neutron_star_forge.phase",
                NeutronStarForgeMachine.phaseName(phase)));
        if (phase != NeutronStarPhase.ACTIVE) return;

        double spin = data.getDouble("nsSpin");
        tooltip.add(Component.translatable("start_core.machine.neutron_star_forge.jade",
                FormattingUtil.formatNumber2Places((float) spin),
                NeutronStarBalance.tierName(NeutronStarBalance.spinTier(spin)),
                Math.round(data.getDouble("nsMass") / NeutronStarBalance.M_TOV * 100))
                .withStyle(ChatFormatting.GOLD));
    }
}
