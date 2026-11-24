package com.startechnology.start_core;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.format.ConfigFormats;

@Config(id = StarTCore.MOD_ID)
public class StarTConfig {

    public static StarTConfig INSTANCE;
    private static final Object LOCK = new Object();

    public static void init() {
        synchronized (LOCK) {
            if (INSTANCE == null) {
                INSTANCE = Configuration.registerConfig(StarTConfig.class, ConfigFormats.yaml()).getConfigInstance();
            }
        }
    }

    @Configurable
    public ClientConfigs client = new ClientConfigs();

    @Configurable
    public SolarConfigs solar = new SolarConfigs();

    @Configurable
    public AE2Configs ae2 = new AE2Configs();

    @Configurable
    public DimensionalFinderConfigs dimensionalFinder = new DimensionalFinderConfigs();

    public enum DifficultyPreset {

        EASY(1, 2, false, 0.60, 0.45, 0.20, 2, 60),
        NORMAL(2, 2, true, 0.60, 0.50, 0.35, 3, 90),
        HARD(2, 2, true, 0.80, 0.60, 0.45, 3, 110);

        public final int altMin;
        public final int altSpread;
        public final boolean forceAddrBranch;
        public final double pAddr;
        public final double pOther;
        public final double pSub;
        public final int passes;
        public final int maxNodes;

        DifficultyPreset(int altMin, int altSpread, boolean forceAddrBranch, double pAddr, double pOther, double pSub,
                         int passes, int maxNodes) {
            this.altMin = altMin;
            this.altSpread = altSpread;
            this.forceAddrBranch = forceAddrBranch;
            this.pAddr = pAddr;
            this.pOther = pOther;
            this.pSub = pSub;
            this.passes = passes;
            this.maxNodes = maxNodes;
        }
    }

    public static class DimensionalFinderConfigs {

        @Configurable
        @Configurable.Comment({
                "Difficulty preset for the Dimensional Finder coordinate tree generation.",
                "Options: EASY, NORMAL, HARD",
                "Default: NORMAL"
        })
        public DifficultyPreset difficulty = DifficultyPreset.NORMAL;
    }

    public static class AE2Configs {

        @Configurable
        @Configurable.Comment({
                "Whether or not to reduce the required permission level for the /ae2 channelmode command from 4 to 2.",
                "Default: true"
        })
        public boolean reduceChannelModePermissionLevel = true;
    }

    public static class ClientConfigs {

        @Configurable
        @Configurable.Comment({ "Whether or not to enable the render for the Komaru Frame.", "Default: true" })
        public boolean komaruRenderer = true;

        @Configurable
        @Configurable.Comment({ "Whether or not EMI/JEI pattern encoding uses universal circuits.", "Default: true" })
        public boolean preferUniversalCircuits = true;
    }

    public static class SolarConfigs {

        @Configurable
        @Configurable.Comment({
                "Heat gained per 6 second cycle on EV-LuV solar panels while producing power.",
                "Default: 0.2"
        })
        public double panelHeatGain = 0.2;

        @Configurable
        @Configurable.Comment({
                "Heat gained per 6 second cycle on UV/UHV solar arrays while producing power and not cooled.",
                "Default: 0.3"
        })
        public double arrayHeatGain = 0.3;

        @Configurable
        @Configurable.Comment({
                "Heat gained per 6 second cycle on UV/UHV solar arrays while producing power and actively cooled.",
                "Default: 0.18"
        })
        public double arrayCooledHeatGain = 0.18;

        @Configurable
        @Configurable.Comment({
                "Heat lost per 6 second cycle when a solar cell is inactive or it is night.",
                "Default: 0.1"
        })
        public double heatLoss = 0.1;

        @Configurable
        @Configurable.Comment({
                "Whether solar cells lose durability each production cycle based on overheat %.",
                "Set to false to disable durability degradation entirely.",
                "Default: true"
        })
        public boolean enableDurabilityLoss = true;

        @Configurable
        @Configurable.Comment({
                "EV solar cell durability.",
                "Default: 1024"
        })
        public int evSolarCellDurability = 1024;

        @Configurable
        @Configurable.Comment({
                "EV solar cell max temperature.",
                "Default: 350"
        })
        public int evSolarCellMaxTemperature = 350;

        @Configurable
        @Configurable.Comment({
                "EV solar cell temperature scale.",
                "Default: 1"
        })
        public double evSolarCellTemperatureScale = 1;

        @Configurable
        @Configurable.Comment({
                "IV solar cell durability.",
                "Default: 1024"
        })
        public int ivSolarCellDurability = 1024;

        @Configurable
        @Configurable.Comment({
                "IV solar cell max temperature.",
                "Default: 370"
        })
        public int ivSolarCellMaxTemperature = 370;

        @Configurable
        @Configurable.Comment({
                "IV solar cell temperature scale.",
                "Default: 0.95"
        })
        public double ivSolarCellTemperatureScale = 0.95;

        @Configurable
        @Configurable.Comment({
                "LuV solar cell durability.",
                "Default: 1024"
        })
        public int luvSolarCellDurability = 1024;

        @Configurable
        @Configurable.Comment({
                "LuV solar cell max temperature.",
                "Default: 390"
        })
        public int luvSolarCellMaxTemperature = 390;

        @Configurable
        @Configurable.Comment({
                "LuV solar cell temperature scale.",
                "Default: 0.9"
        })
        public double luvSolarCellTemperatureScale = 0.9;

        @Configurable
        @Configurable.Comment({
                "ZPM solar cell durability.",
                "Default: 1024"
        })
        public int zpmSolarCellDurability = 1024;

        @Configurable
        @Configurable.Comment({
                "ZPM solar cell max temperature.",
                "Default: 410"
        })
        public int zpmSolarCellMaxTemperature = 410;

        @Configurable
        @Configurable.Comment({
                "ZPM solar cell temperature scale.",
                "Default: 0.85"
        })
        public double zpmSolarCellTemperatureScale = 0.85;

        @Configurable
        @Configurable.Comment({
                "UV solar cell durability.",
                "Default: 1024"
        })
        public int uvSolarCellDurability = 1024;

        @Configurable
        @Configurable.Comment({
                "UV solar cell max temperature.",
                "Default: 430"
        })
        public int uvSolarCellMaxTemperature = 430;

        @Configurable
        @Configurable.Comment({
                "UV solar cell temperature scale.",
                "Default: 0.8"
        })
        public double uvSolarCellTemperatureScale = 0.8;

        @Configurable
        @Configurable.Comment({
                "UHV solar cell durability.",
                "Default: 1024"
        })
        public int uhvSolarCellDurability = 1024;

        @Configurable
        @Configurable.Comment({
                "UHV solar cell max temperature.",
                "Default: 450"
        })
        public int uhvSolarCellMaxTemperature = 450;

        @Configurable
        @Configurable.Comment({
                "UHV solar cell temperature scale.",
                "Default: 0.75"
        })
        public double uhvSolarCellTemperatureScale = 0.75;
    }
}
