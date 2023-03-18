package mod.adrenix.nostalgic.common.config.v2.container.group;

import mod.adrenix.nostalgic.common.config.v2.container.TweakCategory;
import mod.adrenix.nostalgic.common.config.v2.container.TweakContainer;

public abstract class CandyGroup
{
    // Block

    public static final TweakContainer BLOCK = TweakContainer.group(TweakCategory.EYE_CANDY, "block");
    public static final TweakContainer BLOCK_CHEST = TweakContainer.group(BLOCK, "block_chest");
    public static final TweakContainer BLOCK_TORCH = TweakContainer.group(BLOCK, "block_torch");
    public static final TweakContainer BLOCK_OUTLINE = TweakContainer.group(BLOCK, "block_outline");

    // Interface

    public static final TweakContainer INTERFACE = TweakContainer.group(TweakCategory.EYE_CANDY, "interface");
    public static final TweakContainer INTERFACE_GUI = TweakContainer.group(INTERFACE, "interface_gui");
    public static final TweakContainer INTERFACE_ANVIL = TweakContainer.group(INTERFACE, "interface_anvil");
    public static final TweakContainer INTERFACE_CHAT = TweakContainer.group(INTERFACE, "interface_chat");
    public static final TweakContainer INTERFACE_CRAFTING = TweakContainer.group(INTERFACE, "interface_crafting");
    public static final TweakContainer INTERFACE_DEBUG = TweakContainer.group(INTERFACE, "interface_debug");
    public static final TweakContainer INTERFACE_DEBUG_COLOR = TweakContainer.group(INTERFACE_DEBUG, "interface_debug_color");
    public static final TweakContainer INTERFACE_DEBUG_EXTRA = TweakContainer.group(INTERFACE_DEBUG, "interface_debug_extra");
    public static final TweakContainer INTERFACE_DEBUG_CHART = TweakContainer.group(INTERFACE_DEBUG, "interface_debug_chart");
    public static final TweakContainer INTERFACE_FURNACE = TweakContainer.group(INTERFACE, "interface_furnace");
    public static final TweakContainer INTERFACE_INVENTORY = TweakContainer.group(INTERFACE, "interface_inventory");
    public static final TweakContainer INTERFACE_LOADING = TweakContainer.group(INTERFACE, "interface_loading");
    public static final TweakContainer INTERFACE_PAUSE = TweakContainer.group(INTERFACE, "interface_pause");
    public static final TweakContainer INTERFACE_TITLE = TweakContainer.group(INTERFACE, "interface_title");
    public static final TweakContainer INTERFACE_TITLE_BUTTON = TweakContainer.group(INTERFACE_TITLE, "interface_title_button");
    public static final TweakContainer INTERFACE_TITLE_LOGO = TweakContainer.group(INTERFACE_TITLE, "interface_title_logo");
    public static final TweakContainer INTERFACE_TITLE_TEXT = TweakContainer.group(INTERFACE_TITLE, "interface_title_text");
    public static final TweakContainer INTERFACE_TOOLTIP = TweakContainer.group(INTERFACE, "interface_tooltip");
    public static final TweakContainer INTERFACE_TOOLTIP_PARTS = TweakContainer.group(INTERFACE_TOOLTIP, "interface_tooltip_parts");
    public static final TweakContainer INTERFACE_VERSION = TweakContainer.group(INTERFACE, "interface_version");
    public static final TweakContainer INTERFACE_WINDOW = TweakContainer.group(INTERFACE, "interface_window");

    // Item

    public static final TweakContainer ITEM = TweakContainer.group(TweakCategory.EYE_CANDY, "item");
    public static final TweakContainer ITEM_FLAT = TweakContainer.group(ITEM, "item_flat");
    public static final TweakContainer ITEM_DISPLAY = TweakContainer.group(ITEM, "item_display");
    public static final TweakContainer ITEM_MERGING = TweakContainer.group(ITEM, "item_merging");

    // Lighting

    public static final TweakContainer LIGHTING = TweakContainer.group(TweakCategory.EYE_CANDY, "lighting");
    public static final TweakContainer LIGHTING_BLOCK = TweakContainer.group(LIGHTING, "lighting_block");
    public static final TweakContainer LIGHTING_WORLD = TweakContainer.group(LIGHTING, "lighting_world");
    public static final TweakContainer LIGHTING_WORLD_SHADER = TweakContainer.group(LIGHTING_WORLD, "lighting_world_shader");

    // Particle

    public static final TweakContainer PARTICLE = TweakContainer.group(TweakCategory.EYE_CANDY, "particle");
    public static final TweakContainer PARTICLE_ATTACK = TweakContainer.group(PARTICLE, "particle_attack");
    public static final TweakContainer PARTICLE_BLOCK = TweakContainer.group(PARTICLE, "particle_block");
    public static final TweakContainer PARTICLE_EXPLOSION = TweakContainer.group(PARTICLE, "particle_explosion");
    public static final TweakContainer PARTICLE_PLAYER = TweakContainer.group(PARTICLE, "particle_player");

    // World

    public static final TweakContainer WORLD = TweakContainer.group(TweakCategory.EYE_CANDY, "world");
    public static final TweakContainer WORLD_FOG = TweakContainer.group(WORLD, "world_fog");
    public static final TweakContainer WORLD_FOG_CUSTOM = TweakContainer.group(WORLD_FOG, "world_fog_custom");
    public static final TweakContainer WORLD_FOG_WATER = TweakContainer.group(WORLD_FOG, "world_fog_water");
    public static final TweakContainer WORLD_SKY = TweakContainer.group(WORLD, "world_sky");
    public static final TweakContainer WORLD_SKY_CUSTOM = TweakContainer.group(WORLD_SKY, "world_sky_custom");
    public static final TweakContainer WORLD_VOID = TweakContainer.group(WORLD, "world_void");
    public static final TweakContainer WORLD_VOID_FOG = TweakContainer.group(WORLD_VOID, "world_void_fog");
    public static final TweakContainer WORLD_VOID_SKY = TweakContainer.group(WORLD_VOID, "world_void_sky");
}
