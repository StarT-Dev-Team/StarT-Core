package com.startechnology.start_core.client.megastructure;

import com.gregtechceu.gtceu.GTCEu;
import net.irisshaders.iris.api.v0.IrisApi;

public final class ShaderpackCompat {

    private static final IrisApi irisApi = IrisApi.getInstance();
    private static final boolean isLoaded = GTCEu.Mods.isIrisOculusLoaded();

    private ShaderpackCompat() {}

    public static boolean isShaderPackInUse() {
        return isLoaded && irisApi.isShaderPackInUse();
    }

    public static boolean isRenderingShadowPass() {
        return isLoaded && irisApi.isRenderingShadowPass();
    }
}
