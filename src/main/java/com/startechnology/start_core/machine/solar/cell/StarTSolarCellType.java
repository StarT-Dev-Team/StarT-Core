package com.startechnology.start_core.machine.solar.cell;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

public interface StarTSolarCellType extends StringRepresentable {
    ResourceLocation getTexture();

    int getTier();
    int getHarvestLevel();
    int getMaxDurability();
    int getMaxTemperature();
    int getMinTemperature();
    double getTemperatureScale();
    int getEuT();

}
