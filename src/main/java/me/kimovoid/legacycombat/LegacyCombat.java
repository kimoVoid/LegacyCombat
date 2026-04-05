package me.kimovoid.legacycombat;

import me.kimovoid.legacycombat.config.LCConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LegacyCombat {

    public static final LegacyCombat INSTANCE = new LegacyCombat();
    public static final Logger LOGGER = LogManager.getLogger("LegacyCombat");
    public static LCConfig CONFIG;

    public void init() {
        LOGGER.info("Loading Legacy Combat config...");
        CONFIG = new LCConfig();
        CONFIG.load();
    }
}