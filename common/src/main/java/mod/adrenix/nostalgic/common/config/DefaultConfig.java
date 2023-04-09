package mod.adrenix.nostalgic.common.config;

import mod.adrenix.nostalgic.client.config.gui.screen.MenuOption;
import mod.adrenix.nostalgic.common.config.tweak.TweakType;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;
import mod.adrenix.nostalgic.util.common.ItemCommonUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class DefaultConfig
{
    // Setup default item maps after items are registered
    public static void initialize()
    {
        Gameplay.setFoodHealth();
        Gameplay.setFoodSizes();
    }

    public static class Sound
    {
        // Ambient Sounds
        public static final boolean DISABLE_NETHER_AMBIENCE = true;
        public static final boolean DISABLE_WATER_AMBIENCE = true;

        // Block Sounds
        public static final boolean OLD_BED = true;
        public static final boolean OLD_CHEST = true;
        public static final boolean DISABLE_CHEST = true;
        public static final boolean DISABLE_ENDER_CHEST = true;
        public static final boolean DISABLE_TRAPPED_CHEST = true;
        public static final boolean DISABLE_GROWTH = true;
        public static final boolean DISABLE_FURNACE = true;
        public static final boolean DISABLE_DOOR_PLACE = true;
        public static final boolean DISABLE_BED_PLACE = true;
        public static final boolean DISABLE_LAVA_AMBIENCE = true;
        public static final boolean DISABLE_LAVA_POP = true;

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
        public static final boolean DISABLE_SQUID = true;
        public static final boolean DISABLE_FISH_SWIM = true;
        public static final boolean DISABLE_FISH_HURT = true;
        public static final boolean DISABLE_FISH_DEATH = true;
        public static final boolean DISABLE_GENERIC_SWIM = true;
        public static final boolean DISABLE_GLOW_SQUID_OTHER = true;
        public static final boolean DISABLE_GLOW_SQUID_AMBIENCE = false;
        public static final boolean IGNORE_MODDED_STEP = false;
    }

    public static class Candy
    {
        // Block Candy
        public static final TweakVersion.MissingTexture OLD_MISSING_TEXTURE = TweakVersion.MissingTexture.MODERN;
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
        public static final boolean OLD_STAIR_OUTLINE = true;
        public static final boolean OLD_FENCE_OUTLINE = true;
        public static final boolean OLD_WALL_OUTLINE = true;
        public static final boolean OLD_SLAB_OUTLINE = false;

        /* Interface Candy */

        public static final TweakVersion.Hotbar OLD_CREATIVE_HOTBAR = TweakVersion.Hotbar.CLASSIC;
        public static final boolean OLD_BUTTON_HOVER = true;

        // Interface - Anvil Screen
        public static final boolean OLD_ANVIL_SCREEN = true;

        // Interface - Chat Screen
        public static final int CHAT_OFFSET = 0;
        public static final boolean OLD_CHAT_INPUT = true;
        public static final boolean OLD_CHAT_BOX = true;
        public static final boolean DISABLE_SIGNATURE_BOXES = false;

        // Interface - Crafting & Furnace Screen
        public static final TweakType.RecipeBook CRAFTING_BOOK = TweakType.RecipeBook.DISABLED;
        public static final TweakType.RecipeBook FURNACE_BOOK = TweakType.RecipeBook.DISABLED;
        public static final boolean OLD_CRAFTING_SCREEN = true;
        public static final boolean OLD_FURNACE_SCREEN = true;

        // Interface - Debugging Screen
        public static final TweakType.DebugChart FPS_CHART = TweakType.DebugChart.CLASSIC;
        public static final TweakVersion.Generic OLD_DEBUG = TweakVersion.Generic.BETA;
        public static final String DEBUG_BACKGROUND_COLOR = "#50505090";
        public static final boolean OLD_PIE_CHART_BACKGROUND = false;
        public static final boolean SHOW_DEBUG_TEXT_SHADOW = true;
        public static final boolean SHOW_DEBUG_BACKGROUND = false;
        public static final boolean SHOW_DEBUG_TARGET_DATA = false;
        public static final boolean SHOW_DEBUG_FACING_DATA = false;
        public static final boolean SHOW_DEBUG_LIGHT_DATA = false;
        public static final boolean SHOW_DEBUG_BIOME_DATA = false;
        public static final boolean SHOW_DEBUG_GPU_USAGE = false;
        public static final boolean SHOW_DEBUG_PIE_CHART = false;
        public static final boolean SHOW_DEBUG_TPS_CHART = false;
        public static final boolean DEBUG_ENTITY_ID = true;

        // Interface - Loading & Pause Screen
        public static final TweakVersion.Overlay OLD_LOADING_OVERLAY = TweakVersion.Overlay.ALPHA;
        public static final TweakVersion.PauseLayout OLD_PAUSE_MENU = TweakVersion.PauseLayout.MODERN;
        public static final boolean INCLUDE_MODS_ON_PAUSE = true;
        public static final boolean OLD_LOADING_SCREENS = true;
        public static final boolean REMOVE_LOADING_BAR = false;
        public static final boolean REMOVE_EXTRA_PAUSE_BUTTONS = false;

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

        // Interface - Window Title
        public static final String WINDOW_TITLE_TEXT = "Minecraft %v";
        public static final boolean MATCH_VERSION_OVERLAY = false;
        public static final boolean ENABLE_WINDOW_TITLE = false;

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
        public static final boolean OLD_DAMAGE_ARMOR_TINT = true;
        public static final boolean OLD_NO_SELECTED_ITEM_NAME = true;
        public static final boolean OLD_PLAIN_SELECTED_ITEM_NAME = false;

        // Lighting Candy
        public static final int MAX_BLOCK_LIGHT = 15;
        public static final boolean OLD_LIGHT_COLOR = true;
        public static final boolean OLD_LIGHT_RENDERING = true;
        public static final boolean OLD_NETHER_LIGHTING = true;
        public static final boolean OLD_LEAVES_LIGHTING = true;
        public static final boolean OLD_SMOOTH_LIGHTING = true;
        public static final boolean OLD_WATER_LIGHTING = true;
        public static final boolean DISABLE_BRIGHTNESS = true;
        public static final boolean DISABLE_LIGHT_FLICKER = true;
        public static final boolean FIX_CHUNK_BORDER_LAG = true;
        public static final boolean OLD_CLASSIC_LIGHTING = false;

        // Particle Candy
        public static final boolean DISABLE_MODEL_DESTRUCTION_PARTICLES = true;
        public static final boolean DISABLE_UNDERWATER_PARTICLES = true;
        public static final boolean DISABLE_SPRINTING_PARTICLES = true;
        public static final boolean DISABLE_FALLING_PARTICLES = true;
        public static final boolean DISABLE_GROWTH_PARTICLES = true;
        public static final boolean DISABLE_NETHER_PARTICLES = true;
        public static final boolean DISABLE_LEVER_PARTICLES = true;
        public static final boolean DISABLE_LAVA_PARTICLES = false;
        public static final boolean OLD_SWEEP_PARTICLES = true;
        public static final boolean OLD_OPAQUE_EXPERIENCE = true;
        public static final boolean OLD_NO_DAMAGE_PARTICLES = true;
        public static final boolean OLD_NO_CRIT_PARTICLES = true;
        public static final boolean OLD_NO_MAGIC_HIT_PARTICLES = true;
        public static final boolean OLD_EXPLOSION_PARTICLES = true;
        public static final boolean OLD_MIXED_EXPLOSION_PARTICLES = false;
        public static final boolean UNOPTIMIZED_EXPLOSION_PARTICLES = false;

        // World Candy
        public static final String CUSTOM_TERRAIN_FOG_COLOR = "#FFFFFFFF";
        public static final String CUSTOM_NETHER_FOG_COLOR = "#FF0000FF";
        public static final String CUSTOM_WORLD_SKY_COLOR = "#FFFFFFFF";
        public static final String CUSTOM_NETHER_SKY_COLOR = "#FF0000FF";
        public static final String CUSTOM_VOID_SKY_COLOR = "#0000FFFF";
        public static final int OLD_CLOUD_HEIGHT = 108;
        public static final int DISABLED_CLOUD_HEIGHT = 192;
        public static final boolean OLD_NAME_TAGS = false;
        public static final boolean OLD_DARK_VOID_HEIGHT = false;
        public static final boolean OLD_SUNRISE_SUNSET_FOG = true;
        public static final boolean OLD_NETHER_FOG = true;
        public static final boolean OLD_NETHER_SKY = true;
        public static final boolean OLD_WATER_FOG_COLOR = true;
        public static final boolean OLD_WATER_FOG_DENSITY = true;
        public static final boolean OLD_SUNRISE_AT_NORTH = true;
        public static final boolean OLD_DYNAMIC_SKY = true;
        public static final boolean OLD_DYNAMIC_FOG = true;
        public static final boolean OLD_DARK_FOG = true;
        public static final boolean OLD_SQUARE_BORDER = true;
        public static final boolean OLD_BLUE_VOID_OVERRIDE = true;
        public static final boolean SMOOTH_WATER_COLOR = true;
        public static final boolean SMOOTH_WATER_DENSITY = true;
        public static final boolean DISABLE_HORIZON_FOG = false;
        public static final boolean DISABLE_SUNRISE_SUNSET_COLORS = false;
        public static final boolean CUSTOM_TERRAIN_FOG = false;
        public static final boolean CUSTOM_NETHER_FOG = false;
        public static final boolean CUSTOM_WORLD_SKY = false;
        public static final boolean CUSTOM_NETHER_SKY = false;
        public static final boolean CUSTOM_VOID_SKY = false;

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

        // Eye Candy - Tweak Types
        public static final TweakVersion.SkyColor UNIVERSAL_SKY_COLOR = TweakVersion.SkyColor.DISABLED;
        public static final TweakVersion.FogColor UNIVERSAL_FOG_COLOR = TweakVersion.FogColor.DISABLED;
        public static final TweakVersion.WorldFog OLD_WORLD_FOG = TweakVersion.WorldFog.ALPHA_R164;
        public static final TweakVersion.Generic OLD_BLUE_VOID = TweakVersion.Generic.ALPHA;
        public static final TweakVersion.Generic OLD_STARS = TweakVersion.Generic.ALPHA;
    }

    public static class Gameplay
    {
        // Bugs
        public static final boolean OLD_LADDER_GAP = true;
        public static final boolean OLD_SQUID_MILKING = true;

        // Mob System
        public static final int ANIMAL_SPAWN_CAP = 25;
        public static final int MONSTER_SPAWN_CAP = 90;
        public static final boolean DISABLE_SHEEP_EAT_GRASS = true;
        public static final boolean DISABLE_ANIMAL_PANIC = true;
        public static final boolean OLD_ANIMAL_SPAWNING = false;
        public static final boolean OLD_SHEEP_PUNCHING = true;
        public static final boolean ONE_WOOL_PUNCH = false;

        // Mob System - Drops
        public static final boolean OLD_ZOMBIE_DROPS = true;
        public static final boolean OLD_SPIDER_DROPS = true;
        public static final boolean OLD_ZOMBIE_PIGMEN_DROPS = true;
        public static final boolean OLD_SKELETON_DROPS = true;
        public static final boolean OLD_CHICKEN_DROPS = true;
        public static final boolean OLD_SHEEP_DROPS = true;
        public static final boolean OLD_COW_DROPS = true;
        public static final boolean OLD_PIG_DROPS = true;

        public static final boolean OLD_STYLE_ZOMBIE_VILLAGER_DROPS = false;
        public static final boolean OLD_STYLE_CAVE_SPIDER_DROPS = false;
        public static final boolean OLD_STYLE_MOOSHROOM_DROPS = false;
        public static final boolean OLD_STYLE_DROWNED_DROPS = false;
        public static final boolean OLD_STYLE_RABBIT_DROPS = false;
        public static final boolean OLD_STYLE_STRAY_DROPS = false;
        public static final boolean OLD_STYLE_HUSK_DROPS = false;

        // Combat System
        public static final int ARROW_SPEED = 70;
        public static final boolean INSTANT_BOW = true;
        public static final boolean INVINCIBLE_BOW = true;
        public static final boolean DISABLE_COOLDOWN = true;
        public static final boolean DISABLE_MISS_TIMER = true;
        public static final boolean DISABLE_CRITICAL_HIT = true;
        public static final boolean DISABLE_SWEEP = true;
        public static final boolean OLD_DAMAGE_VALUES = true;

        // Experience System
        public static final String XP_LEVEL_TEXT = "Level: %a%v";
        public static final String XP_PROGRESS_TEXT = "Experience: %v%";
        public static final TweakType.Corner XP_LEVEL_CORNER = TweakType.Corner.TOP_LEFT;
        public static final TweakType.Corner XP_PROGRESS_CORNER = TweakType.Corner.TOP_LEFT;
        public static final boolean SHOW_XP_LEVEL_TEXT = false;
        public static final boolean SHOW_XP_PROGRESS_TEXT = false;
        public static final boolean SHOW_XP_PROGRESS_CREATIVE = false;
        public static final boolean SHOW_XP_LEVEL_TEXT_CREATIVE = false;
        public static final boolean USE_DYNAMIC_PROGRESS_COLOR = true;

        public static final boolean DISABLE_EXPERIENCE_BAR = true;
        public static final boolean DISABLE_ORB_SPAWN = true;
        public static final boolean DISABLE_ORB_RENDERING = false;
        public static final boolean DISABLE_ANVIL = false;
        public static final boolean DISABLE_ENCHANT_TABLE = false;

        // Game Mechanics
        public static final boolean OLD_FIRE = false;
        public static final boolean INFINITE_BURN = false;
        public static final boolean INSTANT_AIR = true;
        public static final boolean INSTANT_BONE_MEAL = true;
        public static final boolean DISABLE_SWIM = true;
        public static final boolean DISABLE_SPRINT = true;
        public static final boolean DISABLE_BED_BOUNCE = true;
        public static final boolean LEFT_CLICK_DOOR = true;
        public static final boolean LEFT_CLICK_LEVER = false;
        public static final boolean LEFT_CLICK_BUTTON = false;
        public static final boolean TILLED_GRASS_SEEDS = true;
        public static final boolean CART_BOOSTING = false;

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

        /* Custom Maps */

        /**
         * Gets the edible item's resource key for the default old food stacking map.
         * @param item An edible food item.
         * @return An item resource key.
         */
        private static String getKey(Item item) { return ItemCommonUtil.getResourceKey(item); }

        // Food Health
        public static final int HEALTH_MIN = 0;
        public static final int HEALTH_MAX = 20;
        public static final int HEALTH_RESET = 10;
        public static final Map<String, Integer> DEFAULT_FOOD_HEALTH = new LinkedHashMap<>();

        /**
         * Put the given edible food item into the custom food health map with the given value.
         * @param item An edible food item.
         * @param value The amount of half-hearts to restore.
         */
        private static void setHealth(Item item, int value) { DEFAULT_FOOD_HEALTH.put(getKey(item), value); }

        // Populate Health Map
        private static void setFoodHealth()
        {
            setHealth(Items.ROTTEN_FLESH, 0);
            setHealth(Items.SPIDER_EYE, 0);
            setHealth(Items.CARROT, 1);
            setHealth(Items.MELON_SLICE, 1);
            setHealth(Items.CHORUS_FRUIT, 1);
            setHealth(Items.SWEET_BERRIES, 1);
            setHealth(Items.GLOW_BERRIES, 1);
            setHealth(Items.MUSHROOM_STEW, 10);
            setHealth(Items.BEETROOT_SOUP, 10);
            setHealth(Items.RABBIT_STEW, 10);
            setHealth(Items.SUSPICIOUS_STEW, 10);
            setHealth(Items.GOLDEN_APPLE, 20);
            setHealth(Items.ENCHANTED_GOLDEN_APPLE, 20);
        }

        // Food Stacking
        public static final int ITEM_STACK_MIN = 1;
        public static final int ITEM_STACK_MAX = 64;
        public static final int ITEM_STACK_RESET = 64;
        public static final int FOOD_STACK_RESET = 1;
        public static final Map<String, Integer> DEFAULT_OLD_FOOD_STACKING = new LinkedHashMap<>();

        /**
         * Put the given edible food item into the default old food stacking map with a stack size of eight.
         * @param item An edible food item.
         */
        private static void setEight(Item item) { DEFAULT_OLD_FOOD_STACKING.put(getKey(item), 8); }

        /**
         * Put the given edible food item into the default old food stacking map with a stack size of one.
         * @param item An edible food item.
         */
        private static void setOne(Item item) { DEFAULT_OLD_FOOD_STACKING.put(getKey(item), 1); }

        // Populate Stacking Map
        private static void setFoodSizes()
        {
            setEight(Items.COOKIE);
            setEight(Items.BEETROOT);
            setEight(Items.CARROT);
            setEight(Items.CHORUS_FRUIT);
            setEight(Items.DRIED_KELP);
            setEight(Items.MELON_SLICE);
            setEight(Items.POTATO);
            setEight(Items.POISONOUS_POTATO);
            setEight(Items.SWEET_BERRIES);
            setEight(Items.GLOW_BERRIES);

            setOne(Items.APPLE);
            setOne(Items.BAKED_POTATO);
            setOne(Items.BEEF);
            setOne(Items.BEETROOT_SOUP);
            setOne(Items.BREAD);
            setOne(Items.CHICKEN);
            setOne(Items.COD);
            setOne(Items.COOKED_BEEF);
            setOne(Items.COOKED_CHICKEN);
            setOne(Items.COOKED_COD);
            setOne(Items.COOKED_MUTTON);
            setOne(Items.COOKED_PORKCHOP);
            setOne(Items.COOKED_RABBIT);
            setOne(Items.COOKED_SALMON);
            setOne(Items.ENCHANTED_GOLDEN_APPLE);
            setOne(Items.GOLDEN_APPLE);
            setOne(Items.GOLDEN_CARROT);
            setOne(Items.HONEY_BOTTLE);
            setOne(Items.MUSHROOM_STEW);
            setOne(Items.MUTTON);
            setOne(Items.PORKCHOP);
            setOne(Items.PUFFERFISH);
            setOne(Items.PUMPKIN_PIE);
            setOne(Items.RABBIT);
            setOne(Items.RABBIT_STEW);
            setOne(Items.SALMON);
            setOne(Items.SUSPICIOUS_STEW);
            setOne(Items.TROPICAL_FISH);
        }
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
        public static final boolean OLD_CLASSIC_SWING = false;

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
        public static final boolean OLD_RANDOM_DAMAGE = true;
        public static final boolean OLD_SNEAKING = true;
    }

    public static class Swing
    {
        public static final boolean LEFT_SPEED_ON_RIGHT_INTERACT = true;
        public static final boolean OVERRIDE_SPEEDS = false;
        public static final int MIN_SPEED = 0;
        public static final int MAX_SPEED = 16;
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
        public static final boolean DISPLAY_DONATOR_BANNER = true;
        public static final boolean DISPLAY_NEW_TAGS = true;
        public static final boolean DISPLAY_SIDED_TAGS = true;
        public static final boolean DISPLAY_TAG_TOOLTIPS = true;
        public static final boolean DISPLAY_FEATURE_STATUS = true;
        public static final boolean DISPLAY_CATEGORY_TREE = true;
        public static final boolean DISPLAY_ROW_HIGHLIGHT = true;
        public static final boolean DO_ROW_HIGHLIGHT_FADE = true;
        public static final String CATEGORY_TREE_COLOR = "#8B8B8B7F";
        public static final String ROW_HIGHLIGHT_COLOR = "#FFFFFF32";
        public static final int NUMBER_OF_BACKUPS = 5;
    }
}
