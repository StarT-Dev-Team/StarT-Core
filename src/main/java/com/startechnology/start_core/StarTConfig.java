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

    public static class ClientConfigs {

        @Configurable
        @Configurable.Comment({"Whether or not to enable the render for the Komau Frame.", "Default: true"})
        public boolean komaruRenderer = true;

    }

    public static class SolarConfigs {

        @Configurable
        @Configurable.Comment({
                "Heat gained per 6 second cycle on EV–LuV solar panels while producing power.",
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
    }
}
