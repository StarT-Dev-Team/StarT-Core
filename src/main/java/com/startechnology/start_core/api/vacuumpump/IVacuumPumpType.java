package com.startechnology.start_core.api.vacuumpump;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public interface IVacuumPumpType {

    @NotNull String getName();

    float getRate();

    float getCap();

    int getTier();

    ResourceLocation getTexture();

}
