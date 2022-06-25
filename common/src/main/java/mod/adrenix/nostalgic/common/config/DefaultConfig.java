package mod.adrenix.nostalgic.common.config;

import mod.adrenix.nostalgic.client.config.gui.screen.MenuOption;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;

public abstract class DefaultConfig
{
    public static class Sound
    {
        public static final boolean OLD_ATTACK = true;
        public static final boolean OLD_HURT = true;
        public static final boolean OLD_FALL = true;
        public static final boolean OLD_STEP = true;
        public static final boolean OLD_DOOR = true;
        public static final boolean OLD_BED = true;
        public static final boolean OLD_XP = false;
    }

    public static class Candy
    {
        // Block Candy
        public static final boolean FIX_AMBIENT_OCCLUSION = true;
        public static final boolean OLD_CHEST = true;
        public static final boolean OLD_CHEST_VOXEL = false;
        public static final boolean OLD_ENDER_CHEST = true;
        public static final boolean OLD_TRAPPED_CHEST = true;

        // Interface Candy
        public static final TweakVersion.Overlay OLD_LOADING_OVERLAY = TweakVersion.Overlay.ALPHA;
        public static final TweakVersion.Hotbar OLD_CREATIVE_HOTBAR = TweakVersion.Hotbar.CLASSIC;
        public static final String OLD_OVERLAY_TEXT = "Minecraft %v";
        public static final boolean REMOVE_LOADING_BAR = false;
        public static final boolean OLD_CHAT_INPUT = true;
        public static final boolean OLD_CHAT_BOX = true;
        public static final boolean OLD_VERSION_OVERLAY = true;
        public static final boolean OLD_BUTTON_HOVER = true;
        public static final boolean OLD_TOOLTIP_BOXES = true;
        public static final boolean OLD_NO_ITEM_TOOLTIPS = false;
        public static final boolean OLD_DURABILITY_COLORS = true;
        public static final boolean OLD_LOADING_SCREENS = true;
        public static final boolean OLD_NO_SELECTED_ITEM_NAME = true;
        public static final boolean OLD_PLAIN_SELECTED_ITEM_NAME = false;

        // Item Candy
        public static final boolean FIX_ITEM_MODEL_GAP = true;
        public static final boolean OLD_ITEM_HOLDING = true;
        public static final boolean OLD_ITEM_MERGING = true;
        public static final boolean OLD_2D_ITEMS = true;
        public static final boolean OLD_2D_FRAMES = true;
        public static final boolean OLD_2D_THROWN_ITEMS = true;
        public static final boolean OLD_2D_ENCHANTED_ITEMS = false;

        // Lighting Candy
        public static final boolean OLD_LIGHTING = true;
        public static final boolean OLD_LIGHT_FLICKER = true;
        public static final boolean OLD_NETHER_LIGHTING = true;
        public static final boolean OLD_LEAVES_LIGHTING = true;
        public static final boolean OLD_SMOOTH_LIGHTING = true;
        public static final boolean OLD_WATER_LIGHTING = false;

        // Particle Candy
        public static final boolean OLD_SWEEP_PARTICLES = true;
        public static final boolean OLD_OPAQUE_EXPERIENCE = true;
        public static final boolean OLD_NO_DAMAGE_PARTICLES = true;
        public static final boolean OLD_NO_CRIT_PARTICLES = true;
        public static final boolean OLD_NO_MAGIC_HIT_PARTICLES = true;
        public static final boolean OLD_EXPLOSION_PARTICLES = true;
        public static final boolean OLD_MIXED_EXPLOSION_PARTICLES = false;

        // Title Screen Candy
        public static final TweakVersion.ButtonLayout TITLE_BUTTON_LAYOUT = TweakVersion.ButtonLayout.MODERN;
        public static final String TITLE_VERSION_TEXT = "Minecraft %v";
        public static final boolean TITLE_BOTTOM_LEFT_TEXT = false;
        public static final boolean REMOVE_TITLE_MOD_LOADER_TEXT = true;
        public static final boolean REMOVE_TITLE_ACCESSIBILITY = false;
        public static final boolean REMOVE_TITLE_LANGUAGE = false;
        public static final boolean OVERRIDE_TITLE_SCREEN = true;
        public static final boolean OLD_TITLE_BACKGROUND = true;
        public static final boolean OLD_ALPHA_LOGO = true;
        public static final boolean OLD_LOGO_OUTLINE = true;
        public static final boolean UNCAP_TITLE_FPS = true;

        // World Candy
        public static final int OLD_CLOUD_HEIGHT = 108;
        public static final boolean OLD_TERRAIN_FOG = true;
        public static final boolean OLD_HORIZON_FOG = true;
        public static final boolean OLD_DARK_VOID_HEIGHT = true;
        public static final boolean OLD_SUNRISE_SUNSET_FOG = true;
        public static final boolean OLD_NETHER_FOG = true;
        public static final boolean OLD_SUNRISE_AT_NORTH = true;
        public static final boolean OLD_SQUARE_BORDER = true;
        public static final boolean OLD_BLUE_VOID_OVERRIDE = true;
        public static final boolean OLD_STARS = true;

        public static final TweakVersion.Generic OLD_SKY_COLOR = TweakVersion.Generic.ALPHA;
        public static final TweakVersion.Generic OLD_FOG_COLOR = TweakVersion.Generic.ALPHA;
        public static final TweakVersion.Generic OLD_BLUE_VOID = TweakVersion.Generic.ALPHA;
    }

    public static class Animation
    {
        public static final int ARM_SWAY_INTENSITY = 100;
        public static final boolean ARM_SWAY_MIRROR = false;
        public static final boolean OLD_ARM_SWAY = true;
        public static final boolean OLD_SWING = true;
        public static final boolean OLD_ITEM_COOLDOWN = true;
        public static final boolean OLD_ITEM_REEQUIP = true;
        public static final boolean OLD_COLLIDE_BOBBING = true;
        public static final boolean OLD_VERTICAL_BOBBING = true;
        public static final boolean OLD_SNEAKING = true;
        public static final boolean OLD_SWING_DROPPING = true;
        public static final boolean OLD_TOOL_EXPLOSION = true;
        public static final boolean OLD_ZOMBIE_ARMS = true;
        public static final boolean OLD_SKELETON_ARMS = true;
        public static final boolean OLD_GHAST_CHARGING = true;
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
    }
}
