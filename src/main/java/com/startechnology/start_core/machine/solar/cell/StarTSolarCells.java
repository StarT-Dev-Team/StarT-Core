package com.startechnology.start_core.machine.solar.cell;

import com.startechnology.start_core.StarTCore;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

import static com.gregtechceu.gtceu.api.GTValues.*;

public enum StarTSolarCells implements StarTSolarCellType {
    EV_SOLAR_CELL("ev_solar_cell", EV, 2, 512, 340, 1.2),
    IV_SOLAR_CELL("iv_solar_cell", IV, 2, 768, 360, 1.1),
    LUV_SOLAR_CELL("luv_solar_cell", LuV, 3, 1024, 380, 1),
    UV_SOLAR_CELL("uv_solar_cell", UV, 5, 1536, 425, 0.9),
    UHV_SOLAR_CELL("uhv_solar_cell", UHV, 5, 2048, 450, 0.8);

    private final String name;
    @Getter
    private final int tier;
    private final int harvestLevel;
    @Getter
    public final int durability;
    @Getter
    public final int maxDurability;
    @Getter
    public final int maxTemperature;
    @Getter
    public final int minTemperature = 273;
    @Getter
    public final double temperatureScale;

    StarTSolarCells(String name, int tier, int harvestLevel, int durability, int maxTemperature, double temperatureScale) {
        this.name = name;
        this.tier = tier;
        this.harvestLevel = harvestLevel;
        this.durability = durability;
        this.maxDurability = durability;
        this.maxTemperature = maxTemperature;
        this.temperatureScale = temperatureScale;
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
