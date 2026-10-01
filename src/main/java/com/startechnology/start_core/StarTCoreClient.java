package com.startechnology.start_core;

import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderManager;
import com.startechnology.start_core.client.megastructure.SceneCapture;
import com.startechnology.start_core.machine.black_hole.client.BlackHoleRender;
import com.startechnology.start_core.machine.black_hole.client.BlackHoleRenderManager;
import com.startechnology.start_core.machine.dyson_sphere.client.DysonSphereRender;
import com.startechnology.start_core.machine.dyson_sphere.client.DysonSphereRenderManager;
import com.startechnology.start_core.machine.hellforge.client.HellFlameRender;
import com.startechnology.start_core.machine.hellforge.client.HellFlameRenderManager;
import com.startechnology.start_core.machine.komaru.client.KomaruRenderer;
import com.startechnology.start_core.machine.komaru.client.KomaruRendererManager;
import com.startechnology.start_core.machine.neutron_star.client.NeutronStarRender;
import com.startechnology.start_core.machine.neutron_star.client.NeutronStarRenderManager;

public class StarTCoreClient {

    public static void init() {
        // always register, so we don't have to change the machine definition
        DynamicRenderManager.register(StarTCore.resourceLocation("komaru_renderer"), KomaruRenderer.TYPE);
        DynamicRenderManager.register(StarTCore.resourceLocation("black_hole_renderer"), BlackHoleRender.TYPE);
        DynamicRenderManager.register(StarTCore.resourceLocation("dyson_sphere_renderer"), DysonSphereRender.TYPE);
        DynamicRenderManager.register(StarTCore.resourceLocation("neutron_star_renderer"), NeutronStarRender.TYPE);
        DynamicRenderManager.register(StarTCore.resourceLocation("hell_flame_renderer"), HellFlameRender.TYPE);
        SceneCapture.init();
        BlackHoleRenderManager.init();
        DysonSphereRenderManager.init();
        NeutronStarRenderManager.init();
        HellFlameRenderManager.init();

        if (StarTConfig.INSTANCE.client.komaruRenderer) {
            KomaruRendererManager.init();
        }
    }
}
