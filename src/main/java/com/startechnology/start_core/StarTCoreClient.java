package com.startechnology.start_core;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderManager;
import com.startechnology.start_core.machine.komaru.client.v2.KomaruRendererV2;

public class StarTCoreClient {

    public static void init() {
        DynamicRenderManager.register(GTCEu.id("komaru_renderer_v2"), KomaruRendererV2.TYPE);
    }

}
