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

    public static class ClientConfigs {

        @Configurable
        @Configurable.Comment({"Whether or not to enable the render for the Komau Frame.", "Default: true"})
        public boolean komaruRenderer = true;

    }

}
