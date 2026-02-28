package com.startechnology.start_core.machine.solar.cell;

import com.startechnology.start_core.StarTCore;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

import static com.gregtechceu.gtceu.api.GTValues.*;

public enum StarTSolarCells implements StarTSolarCellType {
    EV_SOLAR_CELL("ev_solar_cell", EV, 2, 500, 500),
    IV_SOLAR_CELL("iv_solar_cell", IV, 2, 1000, 500),
    LUV_SOLAR_CELL("luv_solar_cell", LuV, 3, 1500, 500),
    UV_SOLAR_CELL("uv_solar_cell", UV, 5, 2000, 500),
    UHV_SOLAR_CELL("uhv_solar_cell", UHV, 5, 2500, 500);

    private final String name;
    @Getter
    private final int tier;
    private final int harvestLevel;
    @Getter
    public final int durability;
    @Getter
    public final int maxTemperature;
    @Getter
    public final int minTemperature = 273;

    StarTSolarCells(String name, int tier, int harvestLevel, int durability, int maxTemperature) {
        this.name = name;
        this.tier = tier;
        this.harvestLevel = harvestLevel;
        this.durability = durability;
        this.maxTemperature = maxTemperature;
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
