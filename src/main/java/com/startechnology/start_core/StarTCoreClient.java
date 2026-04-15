package com.startechnology.start_core;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderManager;
import com.startechnology.start_core.machine.komaru.client.KomaruRenderer;

public class StarTCoreClient {

    public static void init() {
        DynamicRenderManager.register(GTCEu.id("komaru_renderer"), KomaruRenderer.TYPE);
    }

}
