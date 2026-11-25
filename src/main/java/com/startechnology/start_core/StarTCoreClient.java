package com.startechnology.start_core;

import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderManager;
import com.startechnology.start_core.client.megastructure.SceneCapture;
import com.startechnology.start_core.item.StarTItems;
import com.startechnology.start_core.item.components.StarTCoordinateCrystalBehaviour;
import com.startechnology.start_core.machine.dimensional_destabiliser.client.RiftRender;
import com.startechnology.start_core.machine.dimensional_destabiliser.client.RiftRenderManager;
import com.startechnology.start_core.machine.komaru.client.KomaruRenderer;
import com.startechnology.start_core.machine.komaru.client.KomaruRendererManager;
import net.minecraft.client.renderer.item.ItemProperties;

public class StarTCoreClient {

    public static void init() {
        // always register, so we don't have to change the machine definition
        DynamicRenderManager.register(StarTCore.resourceLocation("komaru_renderer"), KomaruRenderer.TYPE);
        DynamicRenderManager.register(StarTCore.resourceLocation("rift_renderer"), RiftRender.TYPE);
        SceneCapture.init();
        RiftRenderManager.init();

        if (StarTConfig.INSTANCE.client.komaruRenderer) {
            KomaruRendererManager.init();
        }
    }

    public static void registerItemProperties() {
        ItemProperties.register(StarTItems.COORDINATE_CRYSTAL.asItem(),
                StarTCore.resourceLocation("crystal_variant"),
                (stack, level, entity, seed) -> StarTCoordinateCrystalBehaviour.getCrystalVariant(stack));
    }
}
