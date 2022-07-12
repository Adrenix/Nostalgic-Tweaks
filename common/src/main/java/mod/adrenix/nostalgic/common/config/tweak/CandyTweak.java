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

    // Interface Candy
    LOADING_OVERLAY,
    CREATIVE_HOTBAR,
    REMOVE_LOADING_BAR,
    VERSION_OVERLAY,
    PAUSE_LAYOUT,
    CHAT_INPUT,
    CHAT_BOX,
    BUTTON_HOVER,
    TOOLTIP_BOXES,
    NO_ITEM_TOOLTIPS,
    DURABILITY_COLORS,
    LOADING_SCREENS,
    NO_SELECTED_ITEM_NAME,
    PLAIN_SELECTED_ITEM_NAME,

    // Item Candy
    FIX_ITEM_MODEL_GAP,
    ITEM_HOLDING,
    ITEM_MERGING,
    FLAT_ITEMS,
    FLAT_FRAMES,
    FLAT_THROW_ITEMS,
    FLAT_ENCHANTED_ITEMS,

    // Lighting Candy
    LIGHT_FLICKER,
    NETHER_LIGHTING,
    LIGHTING,
    SMOOTH_LIGHTING,
    LEAVES_LIGHTING,
    WATER_LIGHTING,

    // Particle Candy
    SWEEP,
    OPAQUE_EXPERIENCE,
    NO_DAMAGE_PARTICLES,
    NO_CRIT_PARTICLES,
    NO_MAGIC_HIT_PARTICLES,
    EXPLOSION_PARTICLES,
    MIXED_EXPLOSION_PARTICLES,

    // Title Screen Candy
    OVERRIDE_TITLE_SCREEN,
    ALPHA_LOGO,
    LOGO_OUTLINE,
    TITLE_REALMS,
    TITLE_BUTTON_LAYOUT,
    TITLE_BACKGROUND,
    TITLE_ACCESSIBILITY,
    TITLE_LANGUAGE,
    TITLE_MOD_LOADER_TEXT,
    TITLE_BOTTOM_LEFT_TEXT,
    UNCAP_TITLE_FPS,

    // World Candy
    TERRAIN_FOG,
    HORIZON_FOG,
    NETHER_FOG,
    SUNRISE_SUNSET_FOG,
    SUNRISE_AT_NORTH,
    FOG_COLOR,
    SKY_COLOR,
    BLUE_VOID,
    BLUE_VOID_OVERRIDE,
    DARK_VOID_HEIGHT,
    SQUARE_BORDER,
    STARS,
    CLOUD_HEIGHT;

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
