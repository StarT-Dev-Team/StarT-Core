package com.startechnology.start_core.materials;

public class StarTMaterials {

    public static void register() {
        StarTHellForgeHeatingLiquids.register();
        StarTBacteriaLiquids.register();
        StarTMaterialFlags.init();
        StarTTagPrefixes.init();
        StarTSteams.init();
    }
}
