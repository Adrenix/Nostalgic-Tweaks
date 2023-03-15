package mod.adrenix.nostalgic.common.config.tweak;

import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.reflect.TweakGroup;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import net.fabricmc.api.EnvType;

public enum GameplayTweak implements Tweak
{
    // Bugs

    LADDER_GAP,
    SQUID_MILK,

    // Mob System

    ANIMAL_CAP,
    ANIMAL_PANIC,
    ANIMAL_SPAWNING,
    SHEEP_EAT_GRASS,
    SHEEP_PUNCHING,
    ONE_WOOL_PUNCH,

    // Mob Drops

    ZOMBIE_PIGMEN_DROPS,
    SKELETON_DROPS,
    CHICKEN_DROPS,
    ZOMBIE_DROPS,
    SPIDER_DROPS,
    SHEEP_DROPS,
    COW_DROPS,
    PIG_DROPS,

    ZOMBIE_VILLAGER_DROPS,
    CAVE_SPIDER_DROPS,
    MOOSHROOM_DROPS,
    DROWNED_DROPS,
    RABBIT_DROPS,
    STRAY_DROPS,
    HUSK_DROPS,

    // Combat System

    INSTANT_BOW,
    INVINCIBLE_BOW,
    ARROW_SPEED,
    DISABLE_COOLDOWN,
    DISABLE_MISS_TIMER,
    DISABLE_CRITICAL_HIT,
    DISABLE_SWEEP,
    DAMAGE_VALUES,

    // Experience System

    DISABLE_EXP_BAR,
    SHOW_XP_LEVEL,
    SHOW_XP_LEVEL_CREATIVE,
    XP_LEVEL_CORNER,
    XP_LEVEL_TEXT,
    SHOW_XP_PROGRESS,
    SHOW_XP_PROGRESS_CREATIVE,
    USE_DYNAMIC_PROGRESS_COLOR,
    XP_PROGRESS_CORNER,
    XP_PROGRESS_TEXT,
    ORB_SPAWN,
    ORB_RENDERING,
    ANVIL,
    ENCHANT_TABLE,

    // Hunger System

    DISABLE_HUNGER_BAR,
    SHOW_HUNGER_FOOD,
    USE_DYNAMIC_FOOD_COLOR,
    HUNGER_FOOD_CORNER,
    HUNGER_FOOD_TEXT,
    SHOW_HUNGER_SATURATION,
    USE_DYNAMIC_SATURATION_COLOR,
    HUNGER_SATURATION_CORNER,
    HUNGER_SATURATION_TEXT,
    HUNGER,
    INSTANT_EAT,
    FOOD_STACKING,
    CUSTOM_FOOD_HEALTH,
    CUSTOM_FOOD_STACKING,
    CUSTOM_ITEM_STACKING,

    // Game Mechanics

    SPRINT,
    SWIM,
    BED_BOUNCE,
    FIRE_SPREAD,
    INSTANT_AIR,
    LEFT_CLICK_DOOR,
    LEFT_CLICK_LEVER,
    LEFT_CLICK_BUTTON,
    TILLED_GRASS_SEEDS,
    INSTANT_BONE_MEAL,
    CART_BOOSTING,
    INFINITE_BURN;

    /* Fields */

    /**
     * This field must be defined in the client config within a static block below an entry definition.
     * There are safeguard checks in place to prevent missing, mistyped, or invalid key entries.
     */
    private String key;

    /**
     * Keeps track of whether this tweak is client or server controller.
     */
    private EnvType env = null;

    /**
     * Keeps track of whether this tweak has had its enumeration queried.
     */
    private boolean loaded = false;

    /* Caching */

    private TweakClientCache<?> clientCache;
    private TweakServerCache<?> serverCache;

    /* Tweak Implementation */

    @Override public TweakGroup getGroup() { return TweakGroup.GAMEPLAY; }

    @Override public void setKey(String key) { this.key = key; }
    @Override public String getKey() { return this.key; }

    @Override public void setEnv(EnvType env) { this.env = env; }
    @Override public EnvType getEnv() { return this.env; }

    @Override public void setClientCache(TweakClientCache<?> cache) { this.clientCache = cache; }
    @Override public TweakClientCache<?> getClientCache() { return this.clientCache; }

    @Override public void setServerCache(TweakServerCache<?> cache) { this.serverCache = cache; }
    @Override public TweakServerCache<?> getServerCache() { return this.serverCache; }

    @Override public void setLoaded(boolean state) { this.loaded = state; }
    @Override public boolean isLoaded() { return this.loaded; }
}
