package mod.adrenix.nostalgic.common.config;

import mod.adrenix.nostalgic.client.config.gui.screen.MenuOption;
import mod.adrenix.nostalgic.common.config.tweak.TweakType;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;

public abstract class DefaultConfig
{
    public static class Sound
    {
        // Block Sounds
        public static final boolean OLD_CHEST = true;
        public static final boolean DISABLE_CHEST = true;
        public static final boolean DISABLE_DOOR_PLACE = true;
        public static final boolean DISABLE_BED_PLACE = true;

        // Damage Sounds
        public static final boolean OLD_ATTACK = true;
        public static final boolean OLD_HURT = true;
        public static final boolean OLD_FALL = true;

        // Experience Sounds
        public static final boolean OLD_XP = false;
        public static final boolean DISABLE_XP_PICKUP = false;
        public static final boolean DISABLE_XP_LEVEL = false;

        // Mob Sounds
        public static final boolean OLD_STEP = true;
        public static final boolean IGNORE_MODDED_STEP = false;
    }

    public static class Candy
    {
        // Block Candy
        public static final boolean DISABLE_ALL_OFFSET = false;
        public static final boolean DISABLE_FLOWER_OFFSET = true;
        public static final boolean FIX_AMBIENT_OCCLUSION = true;
        public static final boolean OLD_CHEST = true;
        public static final boolean OLD_CHEST_VOXEL = false;
        public static final boolean OLD_ENDER_CHEST = true;
        public static final boolean OLD_TRAPPED_CHEST = true;
        public static final boolean OLD_TORCH_BRIGHTNESS = true;
        public static final boolean OLD_TORCH_MODEL = true;
        public static final boolean OLD_SOUL_TORCH_MODEL = true;
        public static final boolean OLD_REDSTONE_TORCH_MODEL = true;

        /* Interface Candy */

        public static final TweakVersion.Hotbar OLD_CREATIVE_HOTBAR = TweakVersion.Hotbar.CLASSIC;
        public static final boolean OLD_BUTTON_HOVER = true;

        // Interface - Chat Screen
        public static final boolean OLD_CHAT_INPUT = true;
        public static final boolean OLD_CHAT_BOX = true;

        // Interface - Crafting & Furnace Screen
        public static final TweakType.RecipeBook CRAFTING_BOOK = TweakType.RecipeBook.DISABLED;
        public static final TweakType.RecipeBook FURNACE_BOOK = TweakType.RecipeBook.DISABLED;
        public static final boolean OLD_CRAFTING_SCREEN = true;
        public static final boolean OLD_FURNACE_SCREEN = true;

        // Interface - Debugging Screen
        public static final TweakType.DebugChart FPS_CHART = TweakType.DebugChart.CLASSIC;
        public static final TweakVersion.Generic OLD_DEBUG = TweakVersion.Generic.BETA;
        public static final boolean OLD_PIE_CHART_BACKGROUND = false;
        public static final boolean DISPLAY_LIGHT_LEVELS = false;
        public static final boolean DISPLAY_PIE_CHART = false;
        public static final boolean DISPLAY_TPS_CHART = false;
        public static final boolean DEBUG_ENTITY_ID = true;

        // Interface - Loading & Pause Screen
        public static final TweakVersion.Overlay OLD_LOADING_OVERLAY = TweakVersion.Overlay.ALPHA;
        public static final TweakVersion.PauseLayout OLD_PAUSE_MENU = TweakVersion.PauseLayout.MODERN;
        public static final boolean INCLUDE_MODS_ON_PAUSE = true;
        public static final boolean OLD_LOADING_SCREENS = true;
        public static final boolean REMOVE_LOADING_BAR = false;

        // Interface - Inventory Screen
        public static final TweakType.RecipeBook INVENTORY_BOOK = TweakType.RecipeBook.DISABLED;
        public static final TweakType.InventoryShield INVENTORY_SHIELD = TweakType.InventoryShield.INVISIBLE;
        public static final boolean OLD_INVENTORY = true;
        public static final boolean DISABLE_EMPTY_ARMOR_TEXTURE = false;
        public static final boolean DISABLE_EMPTY_SHIELD_TEXTURE = false;
        public static final boolean INVERTED_PLAYER_LIGHTING = true;
        public static final boolean INVERTED_BLOCK_LIGHTING = true;

        // Interface - Screen Candy
        public static final TweakType.GuiBackground OLD_GUI_BACKGROUND = TweakType.GuiBackground.SOLID_BLACK;
        public static final String CUSTOM_TOP_GRADIENT = "#00000000";
        public static final String CUSTOM_BOTTOM_GRADIENT = "#00000000";
        public static final boolean CUSTOM_GUI_BACKGROUND = false;

        // Interface - Title Screen
        public static final TweakVersion.TitleLayout TITLE_BUTTON_LAYOUT = TweakVersion.TitleLayout.MODERN;
        public static final String TITLE_VERSION_TEXT = "Minecraft %v";
        public static final boolean TITLE_BOTTOM_LEFT_TEXT = false;
        public static final boolean REMOVE_TITLE_MOD_LOADER_TEXT = true;
        public static final boolean REMOVE_TITLE_ACCESSIBILITY = false;
        public static final boolean REMOVE_TITLE_LANGUAGE = false;
        public static final boolean REMOVE_TITLE_REALMS = false;
        public static final boolean INCLUDE_MODS_ON_TITLE = true;
        public static final boolean OVERRIDE_TITLE_SCREEN = true;
        public static final boolean OLD_TITLE_BACKGROUND = true;
        public static final boolean OLD_ALPHA_LOGO = true;
        public static final boolean OLD_LOGO_OUTLINE = true;
        public static final boolean UNCAP_TITLE_FPS = true;

        // Interface - Tooltip Candy
        public static final boolean OLD_TOOLTIP_BOXES = true;
        public static final boolean OLD_NO_ITEM_TOOLTIPS = false;
        public static final boolean SHOW_ENCHANTMENTS_TIP = true;
        public static final boolean SHOW_MODIFIERS_TIP = false;
        public static final boolean SHOW_DYE_TIP = false;

        // Interface - Version Overlay
        public static final TweakType.Corner OLD_OVERLAY_CORNER = TweakType.Corner.TOP_LEFT;
        public static final String OLD_OVERLAY_TEXT = "Minecraft %v";
        public static final boolean OLD_VERSION_OVERLAY = true;

        // Item Candy
        public static final int ITEM_MERGE_LIMIT = 16;
        public static final boolean FIX_ITEM_MODEL_GAP = true;
        public static final boolean OLD_ITEM_HOLDING = true;
        public static final boolean OLD_ITEM_MERGING = true;
        public static final boolean OLD_2D_ITEMS = true;
        public static final boolean OLD_2D_FRAMES = true;
        public static final boolean OLD_2D_COLORS = false;
        public static final boolean OLD_2D_RENDERING = true;
        public static final boolean OLD_2D_THROWN_ITEMS = true;
        public static final boolean OLD_2D_ENCHANTED_ITEMS = false;
        public static final boolean OLD_DURABILITY_COLORS = true;
        public static final boolean OLD_NO_SELECTED_ITEM_NAME = true;
        public static final boolean OLD_PLAIN_SELECTED_ITEM_NAME = false;

        // Lighting Candy
        public static final int MAX_BLOCK_LIGHT = 15;
        public static final boolean OLD_LIGHT_COLOR = true;
        public static final boolean OLD_LIGHT_RENDERING = true;
        public static final boolean OLD_LIGHT_FLICKER = true;
        public static final boolean OLD_NETHER_LIGHTING = true;
        public static final boolean OLD_LEAVES_LIGHTING = true;
        public static final boolean OLD_SMOOTH_LIGHTING = true;
        public static final boolean OLD_WATER_LIGHTING = true;
        public static final boolean DISABLE_BRIGHTNESS = true;

        // Particle Candy
        public static final boolean DISABLE_SPRINTING_PARTICLES = true;
        public static final boolean DISABLE_FALLING_PARTICLES = true;
        public static final boolean DISABLE_NETHER_PARTICLES = true;
        public static final boolean DISABLE_LEVER_PARTICLES = true;
        public static final boolean OLD_SWEEP_PARTICLES = true;
        public static final boolean OLD_OPAQUE_EXPERIENCE = true;
        public static final boolean OLD_NO_DAMAGE_PARTICLES = true;
        public static final boolean OLD_NO_CRIT_PARTICLES = true;
        public static final boolean OLD_NO_MAGIC_HIT_PARTICLES = true;
        public static final boolean OLD_EXPLOSION_PARTICLES = true;
        public static final boolean OLD_MIXED_EXPLOSION_PARTICLES = false;
        public static final boolean UNOPTIMIZED_EXPLOSION_PARTICLES = false;

        // World Candy
        public static final int OLD_CLOUD_HEIGHT = 108;
        public static final int DISABLED_CLOUD_HEIGHT = 192;
        public static final boolean OLD_NAME_TAGS = false;
        public static final boolean OLD_TERRAIN_FOG = true;
        public static final boolean OLD_HORIZON_FOG = true;
        public static final boolean OLD_DARK_VOID_HEIGHT = true;
        public static final boolean OLD_SUNRISE_SUNSET_FOG = true;
        public static final boolean OLD_NETHER_FOG = true;
        public static final boolean OLD_WATER_FOG_COLOR = true;
        public static final boolean OLD_WATER_FOG_DENSITY = true;
        public static final boolean OLD_SUNRISE_AT_NORTH = true;
        public static final boolean OLD_SQUARE_BORDER = true;
        public static final boolean OLD_BLUE_VOID_OVERRIDE = true;
        public static final boolean SMOOTH_WATER_COLOR = true;
        public static final boolean SMOOTH_WATER_DENSITY = true;
        public static final boolean DISABLE_SUNRISE_SUNSET_COLORS = false;

        // World Candy - Void Fog
        public static final String VOID_FOG_COLOR = "#0C0C0CFF";
        public static final int VOID_PARTICLE_RADIUS = 16;
        public static final int VOID_PARTICLE_DENSITY = 20;
        public static final int VOID_PARTICLE_START = -47;
        public static final int VOID_FOG_ENCROACH = 50;
        public static final int VOID_FOG_START = 50;
        public static final boolean DISABLE_VOID_FOG = false;
        public static final boolean CREATIVE_VOID_FOG = true;
        public static final boolean CREATIVE_VOID_PARTICLE = true;
        public static final boolean LIGHT_REMOVES_VOID_FOG = true;

        public static final TweakVersion.Generic OLD_STARS = TweakVersion.Generic.ALPHA;
        public static final TweakVersion.Generic OLD_SKY_COLOR = TweakVersion.Generic.ALPHA;
        public static final TweakVersion.Generic OLD_FOG_COLOR = TweakVersion.Generic.ALPHA;
        public static final TweakVersion.Generic OLD_BLUE_VOID = TweakVersion.Generic.ALPHA;
    }

    public static class Gameplay
    {
        // Bugs
        public static final boolean OLD_LADDER_GAP = true;
        public static final boolean OLD_SQUID_MILKING = true;

        // Combat System
        public static final int ARROW_SPEED = 70;
        public static final boolean INSTANT_BOW = true;
        public static final boolean INVINCIBLE_BOW = true;
        public static final boolean DISABLE_COOLDOWN = true;
        public static final boolean DISABLE_MISS_TIMER = true;
        public static final boolean DISABLE_SWEEP = true;

        // Experience System
        public static final String XP_LEVEL_TEXT = "Level: %a%v";
        public static final String XP_PROGRESS_TEXT = "Experience: %v%";
        public static final TweakType.Corner XP_LEVEL_CORNER = TweakType.Corner.TOP_LEFT;
        public static final TweakType.Corner XP_PROGRESS_CORNER = TweakType.Corner.TOP_LEFT;
        public static final boolean SHOW_XP_LEVEL_TEXT = false;
        public static final boolean SHOW_XP_PROGRESS_TEXT = false;
        public static final boolean USE_DYNAMIC_PROGRESS_COLOR = true;

        public static final boolean DISABLE_EXPERIENCE_BAR = true;
        public static final boolean DISABLE_ORB_SPAWN = true;
        public static final boolean DISABLE_ORB_RENDERING = false;
        public static final boolean DISABLE_ANVIL = false;
        public static final boolean DISABLE_ENCHANT_TABLE = false;

        // Game Mechanics
        public static final boolean OLD_FIRE = false;
        public static final boolean INFINITE_BURN = true;
        public static final boolean INSTANT_AIR = true;
        public static final boolean DISABLE_SWIM = true;
        public static final boolean DISABLE_SPRINT = true;

        // Hunger System
        public static final String HUNGER_FOOD_TEXT = "Food: %v";
        public static final String HUNGER_SATURATION_TEXT = "Saturation: %v%";
        public static final TweakType.Corner HUNGER_FOOD_CORNER = TweakType.Corner.TOP_LEFT;
        public static final TweakType.Corner HUNGER_SATURATION_CORNER = TweakType.Corner.TOP_LEFT;
        public static final boolean SHOW_HUNGER_FOOD_TEXT = false;
        public static final boolean SHOW_HUNGER_SATURATION_TEXT = false;
        public static final boolean USE_DYNAMIC_FOOD_COLOR = true;
        public static final boolean USE_DYNAMIC_SATURATION_COLOR = true;

        public static final boolean DISABLE_HUNGER_BAR = true;
        public static final boolean DISABLE_HUNGER = true;
        public static final boolean INSTANT_EAT = true;
        public static final boolean OLD_FOOD_STACKING = false;
    }

    public static class Animation
    {
        // Arm Animations
        public static final int ARM_SWAY_INTENSITY = 100;
        public static final boolean ARM_SWAY_MIRROR = false;
        public static final boolean OLD_ARM_SWAY = true;
        public static final boolean OLD_SWING = true;
        public static final boolean OLD_SWING_INTERRUPT = true;
        public static final boolean OLD_SWING_DROPPING = true;

        // Item Animations
        public static final boolean OLD_ITEM_COOLDOWN = true;
        public static final boolean OLD_ITEM_REEQUIP = true;
        public static final boolean OLD_TOOL_EXPLOSION = true;

        // Mob Animations
        public static final boolean OLD_ZOMBIE_ARMS = true;
        public static final boolean OLD_SKELETON_ARMS = true;
        public static final boolean OLD_GHAST_CHARGING = true;

        // Player Animations
        public static final boolean DISABLE_DEATH_TOPPLE = true;
        public static final boolean OLD_BACKWARD_WALKING = true;
        public static final boolean OLD_COLLIDE_BOBBING = true;
        public static final boolean OLD_VERTICAL_BOBBING = true;
        public static final boolean OLD_CREATIVE_CROUCH = true;
        public static final boolean OLD_DIRECTIONAL_DAMAGE = true;
        public static final boolean OLD_RANDOM_DAMAGE = true;
        public static final boolean OLD_SNEAKING = true;
    }

    public static class Swing
    {
        public static final boolean OVERRIDE_SPEEDS = false;
        public static final int NEW_SPEED = 6;
        public static final int OLD_SPEED = 8;
        public static final int DISABLED = -1;
        public static final int PHOTOSENSITIVE = 0;
        public static final int ITEM = OLD_SPEED;
        public static final int BLOCK = OLD_SPEED;
        public static final int SWORD = OLD_SPEED;
        public static final int TOOL = OLD_SPEED;
        public static final int HASTE = DISABLED;
        public static final int FATIGUE = DISABLED;
        public static final int GLOBAL = DISABLED;
    }

    public static class Gui
    {
        public static final MenuOption DEFAULT_SCREEN = MenuOption.MAIN_MENU;
        public static final boolean DISPLAY_NEW_TAGS = true;
        public static final boolean DISPLAY_SIDED_TAGS = true;
        public static final boolean DISPLAY_TAG_TOOLTIPS = true;
        public static final boolean DISPLAY_FEATURE_STATUS = true;
        public static final boolean DISPLAY_CATEGORY_TREE = true;
        public static final boolean DISPLAY_ROW_HIGHLIGHT = true;
        public static final boolean DO_ROW_HIGHLIGHT_FADE = true;
        public static final String CATEGORY_TREE_COLOR = "#8B8B8B7F";
        public static final String ROW_HIGHLIGHT_COLOR = "#FFFFFF32";
    }
}
