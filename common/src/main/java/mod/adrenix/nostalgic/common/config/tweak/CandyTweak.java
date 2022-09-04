package mod.adrenix.nostalgic.common.config.tweak;

import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.reflect.GroupType;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;

public enum CandyTweak implements ITweak
{
    // Block Candy
    FIX_AO,
    CHEST,
    CHEST_VOXEL,
    ENDER_CHEST,
    TRAPPED_CHEST,

    /* Interface Candy */

    CREATIVE_HOTBAR,
    BUTTON_HOVER,

    // Interface Candy - Chat Screen
    CHAT_INPUT,
    CHAT_BOX,

    // Interface Candy - Crafting Screen
    CRAFTING_SCREEN,
    CRAFTING_RECIPE,

    // Interface Candy - Debug Screen
    DEBUG_SCREEN,
    DEBUG_FPS_CHART,
    DEBUG_PIE_CHART,
    DEBUG_ENTITY_ID,
    DEBUG_LIGHT,
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
    ITEM_HOLDING,
    ITEM_MERGING,
    ITEM_MERGE_LIMIT,

    // Item Candy - 2D Items
    FLAT_ITEMS,
    FLAT_FRAMES,
    FLAT_COLORS,
    FLAT_THROW_ITEMS,
    FLAT_ENCHANTED_ITEMS,

    // Item Candy - Display
    DURABILITY_COLORS,
    NO_SELECTED_ITEM_NAME,
    PLAIN_SELECTED_ITEM_NAME,

    // Lighting Candy
    LIGHT_RENDERING,
    LIGHT_FLICKER,
    LIGHT_BRIGHTNESS,
    NETHER_LIGHTING,
    DISABLE_GAMMA,
    SMOOTH_LIGHTING,
    LEAVES_LIGHTING,
    WATER_LIGHTING,

    // Particle Candy
    SWEEP,
    OPAQUE_EXPERIENCE,
    NO_SPRINTING_PARTICLES,
    NO_FALLING_PARTICLES,
    NO_NETHER_PARTICLES,
    NO_DAMAGE_PARTICLES,
    NO_CRIT_PARTICLES,
    NO_MAGIC_HIT_PARTICLES,
    EXPLOSION_PARTICLES,
    MIXED_EXPLOSION_PARTICLES,
    UNOPTIMIZED_EXPLOSION_PARTICLES,

    // Title Screen Candy
    OVERRIDE_TITLE_SCREEN,
    ALPHA_LOGO,
    LOGO_OUTLINE,
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
    TERRAIN_FOG,
    HORIZON_FOG,
    NETHER_FOG,
    DISABLE_SUNRISE_SUNSET_COLOR,
    SUNRISE_SUNSET_FOG,
    SUNRISE_AT_NORTH,
    FOG_COLOR,
    SKY_COLOR,
    SQUARE_BORDER,
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

    /* Implementation */

    private String key;
    private TweakClientCache<?> clientCache;
    private TweakServerCache<?> serverCache;
    private boolean loaded = false;

    @Override public String getKey() { return this.key; }
    @Override public GroupType getGroup() { return GroupType.CANDY; }

    @Override public void setClientCache(TweakClientCache<?> cache) { this.clientCache = cache; }
    @Override public void setServerCache(TweakServerCache<?> cache) { this.serverCache = cache; }
    @Override public TweakServerCache<?> getServerCache() { return this.serverCache; }
    @Override public TweakClientCache<?> getClientCache() { return this.clientCache; }

    @Override public boolean isLoaded() { return this.loaded; }
    @Override public void setLoaded(boolean state) { this.loaded = state; }
    @Override public void setKey(String key) { this.key = key; }
}