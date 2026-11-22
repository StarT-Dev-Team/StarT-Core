package com.startechnology.start_core.integration.jade;

import com.startechnology.start_core.integration.jade.provider.StarTModularInterfaceHatchPartMachineProvider;
import com.startechnology.start_core.integration.jade.provider.StarTSolarCellProvider;
import com.startechnology.start_core.integration.jade.provider.StarTThreadedRecipeProvider;
import com.startechnology.start_core.integration.jade.provider.StarTThreadedStatBlockProvider;

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
        registration.registerBlockDataProvider(UniversalMachineJadeProvider.INSTANCE, BlockEntity.class);
        registration.registerBlockDataProvider(new StarTThreadedRecipeProvider(), BlockEntity.class);
        registration.registerBlockDataProvider(new StarTModularInterfaceHatchPartMachineProvider(), BlockEntity.class);
        registration.registerBlockDataProvider(new StarTSolarCellProvider(), BlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(UniversalMachineJadeProvider.INSTANCE, Block.class);
        registration.registerBlockComponent(new StarTThreadedRecipeProvider(), Block.class);
        registration.registerBlockComponent(new StarTModularInterfaceHatchPartMachineProvider(), Block.class);
        registration.registerBlockComponent(new StarTThreadedStatBlockProvider(), Block.class);
        registration.registerBlockComponent(new StarTSolarCellProvider(), Block.class);
    }
}
