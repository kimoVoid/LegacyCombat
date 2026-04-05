package me.kimovoid.legacycombat.config;

import me.kimovoid.legacycombat.LegacyCombat;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class LCConfig {

    private final static File CONFIG_FILE = new File("config/legacy-combat.yml");
    private YamlConfiguration config;

    /* General */
    private final static String DEBUG = "debug";
    private final static String ATTACK_FREQUENCY = "attack-frequency";
    private final static String PROJECTILE_TICK_TIME = "projectile-tick-time";
    private final static String ROD_VELOCITY = "rod-velocity";
    private final static String ENABLE_FAKE_DEATHS = "enable-fake-deaths";
    private final static String INFLATE_HITBOXES = "inflate-hitboxes";
    private final static String INFLATE_HITBOXES_NEW = "inflate-hitboxes-new";

    /* Entity KB */
    private final static String EXPERIMENTAL_KB = "knockback.experimental";
    private final static String KB_FRICTION = "knockback.friction";
    private final static String KB_HORIZONTAL = "knockback.horizontal";
    private final static String KB_VERTICAL = "knockback.vertical";
    private final static String KB_VERTICAL_LIMIT = "knockback.vertical-limit";
    private final static String KB_EXTRA_HORIZONTAL = "knockback.extra-horizontal";
    private final static String KB_EXTRA_VERTICAL = "knockback.extra-vertical";
    private final static String KB_EXTRA_SPRINT = "knockback.extra-sprint";

    /* Projectile KB */
    private final static String PROJ_HORIZONTAL = "projectile.horizontal";
    private final static String PROJ_VERTICAL = "projectile.vertical";
    private final static String BB_HORIZONTAL = "bow-boost.horizontal";
    private final static String BB_VERTICAL = "bow-boost.vertical";
    private final static String SR_HORIZONTAL = "self-rod.horizontal";
    private final static String SR_VERTICAL = "self-rod.vertical";

    /* Values */
    public boolean debug;
    public int attackFrequency;
    public int projTickTime;
    public double rodVelocity;
    public boolean enableFakeDeaths;
    public float inflateHitboxes;
    public boolean inflateHitboxesNew;

    public boolean kbExperimental;
    public double kbFriction;
    public double kbHorizontal;
    public double kbVertical;
    public double kbVerticalLimit;
    public double kbExtraHorizontal;
    public double kbExtraVertical;
    public double kbExtraSprint;

    public double projHorizontal;
    public double projVertical;
    public double bbHorizontal;
    public double bbVertical;
    public double srHorizontal;
    public double srVertical;

    public void load() {
        this.config = YamlConfiguration.loadConfiguration(CONFIG_FILE);

        this.debug = this.config.getBoolean(DEBUG, false);
        this.attackFrequency = this.config.getInt(ATTACK_FREQUENCY, 20);
        this.projTickTime = this.config.getInt(PROJECTILE_TICK_TIME, 5);
        this.rodVelocity = this.config.getDouble(ROD_VELOCITY, 1.4);
        this.enableFakeDeaths = this.config.getBoolean(ENABLE_FAKE_DEATHS, true);
        this.inflateHitboxes = (float) this.config.getDouble(INFLATE_HITBOXES, 0.1);
        this.inflateHitboxesNew = this.config.getBoolean(INFLATE_HITBOXES_NEW, false);


        this.kbExperimental = this.config.getBoolean(EXPERIMENTAL_KB, false);
        this.kbFriction = this.config.getDouble(KB_FRICTION, 2.0);
        this.kbHorizontal = this.config.getDouble(KB_HORIZONTAL, 0.4);
        this.kbVertical = this.config.getDouble(KB_VERTICAL, 0.35);
        this.kbVerticalLimit = this.config.getDouble(KB_VERTICAL_LIMIT, 0.4);
        this.kbExtraHorizontal = this.config.getDouble(KB_EXTRA_HORIZONTAL, 0.5);
        this.kbExtraVertical = this.config.getDouble(KB_EXTRA_VERTICAL, 0.1);
        this.kbExtraSprint = this.config.getDouble(KB_EXTRA_SPRINT, 1.0);

        this.projHorizontal = this.config.getDouble(PROJ_HORIZONTAL, 0.45);
        this.projVertical = this.config.getDouble(PROJ_VERTICAL, 0.35);
        this.bbHorizontal = this.config.getDouble(BB_HORIZONTAL, 0.5);
        this.bbVertical = this.config.getDouble(BB_VERTICAL, 0.5);
        this.srHorizontal = this.config.getDouble(SR_HORIZONTAL, 0.425);
        this.srVertical = this.config.getDouble(SR_VERTICAL, 0.4);

        this.save();
    }

    public void save() {
        this.config.set(DEBUG, this.debug);
        this.config.set(ATTACK_FREQUENCY, this.attackFrequency);
        this.config.set(PROJECTILE_TICK_TIME, this.projTickTime);
        this.config.set(ROD_VELOCITY, this.rodVelocity);
        this.config.set(ENABLE_FAKE_DEATHS, this.enableFakeDeaths);
        this.config.set(INFLATE_HITBOXES, this.inflateHitboxes);
        this.config.set(INFLATE_HITBOXES_NEW, this.inflateHitboxesNew);

        this.config.set(EXPERIMENTAL_KB, this.kbExperimental);
        this.config.set(KB_FRICTION, this.kbFriction);
        this.config.set(KB_HORIZONTAL, this.kbHorizontal);
        this.config.set(KB_VERTICAL, this.kbVertical);
        this.config.set(KB_VERTICAL_LIMIT, this.kbVerticalLimit);
        this.config.set(KB_EXTRA_HORIZONTAL, this.kbExtraHorizontal);
        this.config.set(KB_EXTRA_VERTICAL, this.kbExtraVertical);
        this.config.set(KB_EXTRA_SPRINT, this.kbExtraSprint);

        this.config.set(PROJ_HORIZONTAL, this.projHorizontal);
        this.config.set(PROJ_VERTICAL, this.projVertical);
        this.config.set(BB_HORIZONTAL, this.bbHorizontal);
        this.config.set(BB_VERTICAL, this.bbVertical);
        this.config.set(SR_HORIZONTAL, this.srHorizontal);
        this.config.set(SR_VERTICAL, this.srVertical);

        try {
            this.config.save(CONFIG_FILE);
        } catch (Exception e) {
            LegacyCombat.LOGGER.error("Failed to save LC config: {}", e.getMessage());
        }
    }
}