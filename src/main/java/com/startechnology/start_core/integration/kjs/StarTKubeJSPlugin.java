package com.startechnology.start_core.integration.kjs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.RegistryInfo;

public class StarTKubeJSPlugin extends KubeJSPlugin {

    @Override
    public void init() {
        super.init();
        RegistryInfo.BLOCK.addType("gtceu:fusion_reflector", FusionReflectorBlockBuilder.class, FusionReflectorBlockBuilder::new);
    }
}
