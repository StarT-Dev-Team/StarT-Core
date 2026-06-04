package com.startechnology.start_core.machine.solar.cell;

import com.startechnology.start_core.StarTCore;
import lombok.Getter;

import net.minecraft.resources.ResourceLocation;

import static com.gregtechceu.gtceu.api.GTValues.*;

public enum StarTSolarCells implements StarTSolarCellType {

    EV_SOLAR_CELL("ev_solar_cell", EV, 2, 1024, 350, 1),
    IV_SOLAR_CELL("iv_solar_cell", IV, 2, 1024, 370, 0.95),
    LUV_SOLAR_CELL("luv_solar_cell", LuV, 3, 1024, 390, 0.9),
    ZPM_SOLAR_CELL("zpm_solar_cell", ZPM, 3, 1024, 410, 0.85),
    UV_SOLAR_CELL("uv_solar_cell", UV, 4, 1024, 430, 0.8),
    UHV_SOLAR_CELL("uhv_solar_cell", UHV, 4, 1024, 450, 0.75);

    private final String name;
    @Getter
    private final int tier;
    private final int harvestLevel;
    @Getter
    public final int maxDurability;
    @Getter
    public final int maxTemperature;
    @Getter
    public final int minTemperature = 273;
    @Getter
    public final double temperatureScale;
    @Getter
    public final int euT;

    StarTSolarCells(String name, int tier, int harvestLevel, int durability, int maxTemperature,
                    double temperatureScale) {
        this.name = name;
        this.tier = tier;
        this.harvestLevel = harvestLevel;
        this.maxDurability = durability;
        this.maxTemperature = maxTemperature;
        this.temperatureScale = temperatureScale;
        this.euT = (int) V[tier] / 4;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    @Override
    public ResourceLocation getTexture() {
        return StarTCore.resourceLocation("block/casings/solar_cell/%s".formatted(this.name));
    }

    @Override
    public int getHarvestLevel() {
        return this.harvestLevel;
    }
}
