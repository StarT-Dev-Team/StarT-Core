package com.startechnology.start_core.integration.jade;

import com.startechnology.start_core.integration.jade.provider.StarTAbyssalHarvesterProvider;
import com.startechnology.start_core.integration.jade.provider.StarTBlackHoleProvider;
import com.startechnology.start_core.integration.jade.provider.StarTBulkingProvider;
import com.startechnology.start_core.integration.jade.provider.StarTDreamLinkNetworkBlockProvider;
import com.startechnology.start_core.integration.jade.provider.StarTDysonSphereProvider;
import com.startechnology.start_core.integration.jade.provider.StarTFusionReactorProvider;
import com.startechnology.start_core.integration.jade.provider.StarTHellforgeProvider;
import com.startechnology.start_core.integration.jade.provider.StarTModularInterfaceHatchPartMachineProvider;
import com.startechnology.start_core.integration.jade.provider.StarTNeutronStarProvider;
import com.startechnology.start_core.integration.jade.provider.StarTRedstoneInterfaceProvider;
import com.startechnology.start_core.integration.jade.provider.StarTSolarCellProvider;
import com.startechnology.start_core.integration.jade.provider.StarTSolarMachineProvider;
import com.startechnology.start_core.integration.jade.provider.StarTThreadedRecipeProvider;
import com.startechnology.start_core.integration.jade.provider.StarTThreadedStatBlockProvider;
import com.startechnology.start_core.integration.jade.provider.StarTVacuumChemicalReactionChamberProvider;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

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
        registration.registerBlockDataProvider(new StarTSolarMachineProvider(), BlockEntity.class);
        registration.registerBlockDataProvider(new StarTSolarCellProvider(), BlockEntity.class);
        registration.registerBlockDataProvider(new StarTVacuumChemicalReactionChamberProvider(), BlockEntity.class);
        registration.registerBlockDataProvider(new StarTModularInterfaceHatchPartMachineProvider(), BlockEntity.class);
        registration.registerBlockDataProvider(new StarTBulkingProvider(), BlockEntity.class);
        registration.registerBlockDataProvider(new StarTBlackHoleProvider(), BlockEntity.class);
        registration.registerBlockDataProvider(new StarTDysonSphereProvider(), BlockEntity.class);
        registration.registerBlockDataProvider(new StarTNeutronStarProvider(), BlockEntity.class);
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
        registration.registerBlockComponent(new StarTSolarMachineProvider(), Block.class);
        registration.registerBlockComponent(new StarTSolarCellProvider(), Block.class);
        registration.registerBlockComponent(new StarTVacuumChemicalReactionChamberProvider(), Block.class);
        registration.registerBlockComponent(new StarTModularInterfaceHatchPartMachineProvider(), Block.class);
        registration.registerBlockComponent(new StarTBulkingProvider(), Block.class);
        registration.registerBlockComponent(new StarTBlackHoleProvider(), Block.class);
        registration.registerBlockComponent(new StarTDysonSphereProvider(), Block.class);
        registration.registerBlockComponent(new StarTNeutronStarProvider(), Block.class);
    }
}
