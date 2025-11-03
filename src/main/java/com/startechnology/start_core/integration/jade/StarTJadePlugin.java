package com.startechnology.start_core.integration.jade;

import java.util.List;

import com.gregtechceu.gtceu.integration.jade.provider.CapabilityBlockProvider;
import com.gregtechceu.gtceu.integration.jade.provider.MaintenanceBlockProvider;
import com.startechnology.start_core.integration.jade.provider.StarTAbyssalHarvesterProvider;
import com.startechnology.start_core.integration.jade.provider.StarTDreamLinkNetworkBlockProvider;
import com.startechnology.start_core.integration.jade.provider.StarTHellforgeProvider;
import com.startechnology.start_core.integration.jade.provider.StarTRedstoneInterfaceProvider;
import com.startechnology.start_core.integration.jade.provider.StarTThreadedRecipeProvider;

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
        registration.registerBlockDataProvider(new StarTHellforgeProvider(), BlockEntity.class);
        registration.registerBlockDataProvider(new StarTRedstoneInterfaceProvider(), BlockEntity.class);
        registration.registerBlockDataProvider(new StarTAbyssalHarvesterProvider(), BlockEntity.class);
        registration.registerBlockDataProvider(new StarTThreadedRecipeProvider(), BlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(new StarTDreamLinkNetworkBlockProvider(), Block.class);
        registration.registerBlockComponent(new StarTHellforgeProvider(), Block.class);
        registration.registerBlockComponent(new StarTRedstoneInterfaceProvider(), Block.class);
        registration.registerBlockComponent(new StarTAbyssalHarvesterProvider(), Block.class);       
        registration.registerBlockComponent(new StarTThreadedRecipeProvider(), Block.class);
    }
}
