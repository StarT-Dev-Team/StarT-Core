package com.startechnology.start_core.integration.jade;

import com.gregtechceu.gtceu.integration.jade.provider.MaintenanceBlockProvider;
import com.startechnology.start_core.integration.jade.provider.StarTDreamLinkNetworkBlockProvider;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class StarTJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(new StarTDreamLinkNetworkBlockProvider(), BlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(new StarTDreamLinkNetworkBlockProvider(), Block.class);
    }
}
