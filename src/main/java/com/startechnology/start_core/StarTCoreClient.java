package com.startechnology.start_core;

import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderManager;
import com.startechnology.start_core.client.komaru.KomaruRenderer;
import com.startechnology.start_core.client.komaru.KomaruRendererManager;

public class StarTCoreClient {

    public static void init() {
        // always register, so we don't have to change the machine definition
        DynamicRenderManager.register(StarTCore.resourceLocation("komaru_renderer"), KomaruRenderer.TYPE);

        if (StarTConfig.INSTANCE.client.komaruRenderer) {
            KomaruRendererManager.init();
        }
    }
}
