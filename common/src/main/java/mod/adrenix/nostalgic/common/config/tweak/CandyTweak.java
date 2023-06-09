package mod.adrenix.nostalgic.common.config.tweak;

import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.reflect.TweakGroup;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import net.fabricmc.api.EnvType;

public enum CandyTweak implements Tweak
{
    // Block Candy
    FIX_AO,
    CHEST,
    CHEST_VOXEL,
    ENDER_CHEST,
    TRAPPED_CHEST,
    TORCH_BRIGHTNESS,
    TORCH_MODEL,
    SOUL_TORCH_MODEL,
    REDSTONE_TORCH_MODEL,
    OLD_STAIR_OUTLINE,
    OLD_FENCE_OUTLINE,
    OLD_SLAB_OUTLINE,
    FULL_BLOCK_OUTLINE,
    OLD_WALL_OUTLINE,
    OLD_MISSING_TEXTURE,
    DISABLE_FLOWER_OFFSET,
    DISABLE_ALL_OFFSET,

    /* Interface Candy */

    CREATIVE_HOTBAR,
    BUTTON_HOVER,

    // Interface Candy - Window Title
    WINDOW_TITLE,
    MATCH_VERSION_OVERLAY,
    ENABLE_WINDOW_TITLE,

    // Interface Candy - Anvil Screen
    ANVIL_SCREEN,

    // Interface Candy - Chat Screen
    CHAT_INPUT,
    CHAT_BOX,
    CHAT_OFFSET,
    SIGNATURE_BOXES,

    // Interface Candy - Crafting Screen
    CRAFTING_SCREEN,
    CRAFTING_RECIPE,

    // Interface Candy - Debug Screen
    DEBUG_COLOR,
    DEBUG_SCREEN,
    DEBUG_FPS_CHART,
    DEBUG_TPS_CHART,
    DEBUG_PIE_CHART,
    DEBUG_ENTITY_ID,
    DEBUG_GPU,
    DEBUG_FACING,
    DEBUG_LIGHT,
    DEBUG_BIOME,
    DEBUG_TARGETED,
    DEBUG_SHOW_COLOR,
    DEBUG_SHOW_SHADOW,
    OLD_PIE_BACKGROUND,

    // Interface Candy - Furnace Screen
    FURNACE_SCREEN,
    FURNACE_RECIPE,

    // Interface Candy - Inventory Screen
    OLD_INVENTORY,
    INVENTORY_BOOK,
    INVENTORY_SHIELD,
    DISABLE_EMPTY_ARMOR,
    DISABLE_EMPTY_SHIELD,
    INVERTED_PLAYER_LIGHTING,
    INVERTED_BLOCK_LIGHTING,

    // Interface Candy - Loading Screen
    LOADING_OVERLAY,
    LOADING_SCREENS,
    REMOVE_LOADING_BAR,

    // Interface Candy - Pause Screen
    PAUSE_LAYOUT,
    PAUSE_MODS,
    PAUSE_REMOVE_EXTRA,

    // Interface Candy - Screen Candy
    OLD_GUI_BACKGROUND,
    CUSTOM_GUI_BACKGROUND,
    CUSTOM_TOP_GRADIENT,
    CUSTOM_BOTTOM_GRADIENT,

    // Interface Candy - Tooltips
    TOOLTIP_BOXES,
    NO_ITEM_TOOLTIPS,
    ENCHANTMENT_TIP,
    MODIFIERS_TIP,
    DYE_TIP,

    // Interface Candy - Version Overlay
    VERSION_OVERLAY,
    VERSION_CORNER,
    VERSION_TEXT,

    // Item Candy
    FIX_ITEM_MODEL_GAP,
    IGNORED_ITEM_HOLDING,
    ITEM_HOLDING,
    ITEM_MERGING,
    ITEM_MERGE_LIMIT,
    DAMAGE_ARMOR_TINT,

    // Item Candy - 2D Items
    FLAT_ITEMS,
    FLAT_FRAMES,
    FLAT_COLORS,
    FLAT_RENDERING,
    FLAT_THROW_ITEMS,
    FLAT_ENCHANTED_ITEMS,

    // Item Candy - Display
    DURABILITY_COLORS,
    NO_SELECTED_ITEM_NAME,
    PLAIN_SELECTED_ITEM_NAME,

    // Lighting Candy
    MAX_BLOCK_LIGHT,
    LIGHT_COLOR,
    LIGHT_RENDERING,
    LIGHT_FLICKER,
    NETHER_LIGHTING,
    SMOOTH_LIGHTING,
    LEAVES_LIGHTING,
    WATER_LIGHTING,
    CLASSIC_LIGHTING,
    DISABLE_BRIGHTNESS,
    FIX_CHUNK_BORDER_LAG,

    // Particle Candy
    SWEEP,
    OPAQUE_EXPERIENCE,
    NO_MODEL_DESTRUCTION_PARTICLES,
    NO_UNDERWATER_PARTICLES,
    NO_SPRINTING_PARTICLES,
    NO_FALLING_PARTICLES,
    NO_NETHER_PARTICLES,
    NO_GROWTH_PARTICLES,
    NO_DAMAGE_PARTICLES,
    NO_LEVER_PARTICLES,
    NO_LAVA_PARTICLES,
    NO_CRIT_PARTICLES,
    NO_MAGIC_HIT_PARTICLES,
    EXPLOSION_PARTICLES,
    MIXED_EXPLOSION_PARTICLES,
    UNOPTIMIZED_EXPLOSION_PARTICLES,

    // Title Screen Candy
    OVERRIDE_TITLE_SCREEN,
    ALPHA_LOGO,
    TITLE_REALMS,
    TITLE_BUTTON_LAYOUT,
    TITLE_MODS_BUTTON,
    TITLE_BACKGROUND,
    TITLE_ACCESSIBILITY,
    TITLE_LANGUAGE,
    TITLE_MOD_LOADER_TEXT,
    TITLE_BOTTOM_LEFT_TEXT,
    UNCAP_TITLE_FPS,

    // World Candy
    NAME_TAGS,
    NETHER_FOG,
    NETHER_SKY,
    WORLD_FOG,
    WATER_FOG_COLOR,
    WATER_FOG_DENSITY,
    SMOOTH_WATER_COLOR,
    SMOOTH_WATER_DENSITY,
    DISABLE_HORIZON_FOG,
    DISABLE_SUNRISE_SUNSET_COLOR,
    SUNRISE_SUNSET_FOG,
    SUNRISE_AT_NORTH,
    CUSTOM_TERRAIN_FOG_COLOR,
    CUSTOM_TERRAIN_FOG,
    CUSTOM_NETHER_FOG_COLOR,
    CUSTOM_NETHER_FOG,
    CUSTOM_NETHER_SKY_COLOR,
    CUSTOM_NETHER_SKY,
    CUSTOM_WORLD_SKY_COLOR,
    CUSTOM_WORLD_SKY,
    CUSTOM_VOID_SKY_COLOR,
    CUSTOM_VOID_SKY,
    UNIVERSAL_FOG_COLOR,
    UNIVERSAL_SKY_COLOR,
    DYNAMIC_FOG_COLOR,
    DYNAMIC_SKY_COLOR,
    SQUARE_BORDER,
    DARK_FOG,
    STARS,
    CLOUD_HEIGHT,

    // Void Candy
    BLUE_VOID,
    BLUE_VOID_OVERRIDE,
    DARK_VOID_HEIGHT,
    DISABLE_VOID_FOG,
    CREATIVE_VOID_FOG,
    CREATIVE_VOID_PARTICLE,
    LIGHT_REMOVES_VOID_FOG,
    VOID_FOG_COLOR,
    VOID_FOG_START,
    VOID_FOG_ENCROACH,
    VOID_PARTICLE_START,
    VOID_PARTICLE_DENSITY,
    VOID_PARTICLE_RADIUS;

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

    @Override public TweakGroup getGroup() { return TweakGroup.CANDY; }

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
