package com.startechnology.start_core.integration.jade;

import com.startechnology.start_core.integration.jade.provider.*;

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
        registration.registerBlockDataProvider(new StarTFusionReactorProvider(), BlockEntity.class);
        registration.registerBlockDataProvider(new StarTVacuumChemicalReactorProvider(), BlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(new StarTDreamLinkNetworkBlockProvider(), Block.class);
        registration.registerBlockComponent(new StarTHellforgeProvider(), Block.class);
        registration.registerBlockComponent(new StarTRedstoneInterfaceProvider(), Block.class);
        registration.registerBlockComponent(new StarTAbyssalHarvesterProvider(), Block.class);
        registration.registerBlockComponent(new StarTThreadedRecipeProvider(), Block.class);
        registration.registerBlockComponent(new StarTThreadedStatBlockProvider(), Block.class);
        registration.registerBlockComponent(new StarTFusionReactorProvider(), Block.class);
        registration.registerBlockComponent(new StarTVacuumChemicalReactorProvider(), Block.class);
    }
}
