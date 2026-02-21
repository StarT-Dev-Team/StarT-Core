package com.startechnology.start_core.data;

import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import com.gregtechceu.gtceu.common.data.models.GTMachineModels;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class StarTModels {

    public static MachineBuilder.ModelInitializer activeOverlayTieredHullModel(
            ResourceLocation baseModel,
            Function<Integer, ResourceLocation> getOverlayTexture,
            Function<Integer, ResourceLocation> getActiveOverlayTexture) {
        return (context, provider, builder) -> {
            var tier = builder.getOwner().getTier();
            var overlayTexture = getOverlayTexture.apply(tier);
            var overlayActiveTexture = getActiveOverlayTexture.apply(tier);

            builder.forAllStatesModels(state -> {
                var active = state.getValue(GTMachineModelProperties.IS_ACTIVE);
                var model = provider.models().nested().parent(provider.models().getExistingFile(baseModel));
                model.texture("overlay", active ? overlayActiveTexture : overlayTexture);
                return GTMachineModels.tieredHullTextures(model, tier);
            });
            builder.addReplaceableTextures("bottom", "top", "side");
        };
    }

}
