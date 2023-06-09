package mod.adrenix.nostalgic.client.config;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.annotation.TweakGui;
import mod.adrenix.nostalgic.client.config.annotation.TweakReload;
import mod.adrenix.nostalgic.client.config.annotation.container.TweakCategory;
import mod.adrenix.nostalgic.client.config.annotation.container.TweakEmbed;
import mod.adrenix.nostalgic.client.config.annotation.container.TweakSubcategory;
import mod.adrenix.nostalgic.client.config.gui.screen.MenuOption;
import mod.adrenix.nostalgic.common.config.DefaultConfig;
import mod.adrenix.nostalgic.common.config.ValidateConfig;
import mod.adrenix.nostalgic.common.config.annotation.TweakData;
import mod.adrenix.nostalgic.common.config.auto.ConfigData;
import mod.adrenix.nostalgic.common.config.auto.annotation.Config;
import mod.adrenix.nostalgic.common.config.list.ListId;
import mod.adrenix.nostalgic.common.config.reflect.TweakStatus;
import mod.adrenix.nostalgic.common.config.tweak.*;
import mod.adrenix.nostalgic.util.common.LangUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The server controlled tweaks in this config need to stay in sync with the fields in the server config.
 * Any updates in this class or that class will require an update in both config classes.
 *
 * @see mod.adrenix.nostalgic.server.config.ServerConfig
 */

@Config(name = NostalgicTweaks.MOD_ID)
public class ClientConfig implements ConfigData
{
    /* Config Validation */

    @Override public void validatePostLoad() throws ValidationException { ValidateConfig.scan(this); }

    /* Universal State */

    @TweakData.Ignore public static final String ROOT_KEY = "isModEnabled";

    @TweakGui.NoTooltip
    @TweakData.Client
    @TweakData.EntryStatus(status = TweakStatus.LOADED)
    @TweakReload.Resources
    public boolean isModEnabled = true;

    /* Client Config */

    @TweakData.Ignore
    public Sound sound = new Sound();
    public static class Sound
    {
        /**
         * Ambient Sounds
         */

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.AMBIENT_SOUND)
        public boolean disableNetherAmbience = DefaultConfig.Sound.DISABLE_NETHER_AMBIENCE;
        static { SoundTweak.DISABLE_NETHER_AMBIENCE.setKey("disableNetherAmbience"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.AMBIENT_SOUND)
        public boolean disableWaterAmbience = DefaultConfig.Sound.DISABLE_WATER_AMBIENCE;
        static { SoundTweak.DISABLE_WATER_AMBIENCE.setKey("disableWaterAmbience"); }

        /**
         * Block Sounds
         */

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.BLOCK_BED_SOUND)
        public boolean oldBed = DefaultConfig.Sound.OLD_BED;
        static { SoundTweak.OLD_BED.setKey("oldBed"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.BLOCK_BED_SOUND)
        public boolean disableBedPlace = DefaultConfig.Sound.DISABLE_BED_PLACE;
        static { SoundTweak.DISABLE_BED.setKey("disableBedPlace"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.BLOCK_CHEST_SOUND)
        public boolean oldChest = DefaultConfig.Sound.OLD_CHEST;
        static { SoundTweak.OLD_CHEST.setKey("oldChest"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.BLOCK_CHEST_SOUND)
        public boolean disableChest = DefaultConfig.Sound.DISABLE_CHEST;
        static { SoundTweak.DISABLE_CHEST.setKey("disableChest"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.BLOCK_CHEST_SOUND)
        public boolean disableEnderChest = DefaultConfig.Sound.DISABLE_ENDER_CHEST;
        static { SoundTweak.DISABLE_ENDER_CHEST.setKey("disableEnderChest"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.BLOCK_CHEST_SOUND)
        public boolean disableTrappedChest = DefaultConfig.Sound.DISABLE_TRAPPED_CHEST;
        static { SoundTweak.DISABLE_TRAPPED_CHEST.setKey("disableTrappedChest"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.BLOCK_LAVAL_SOUND)
        public boolean disableLavaAmbience = DefaultConfig.Sound.DISABLE_LAVA_AMBIENCE;
        static { SoundTweak.DISABLE_LAVA_AMBIENCE.setKey("disableLavaAmbience"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.BLOCK_LAVAL_SOUND)
        public boolean disableLavaPop = DefaultConfig.Sound.DISABLE_LAVA_POP;
        static { SoundTweak.DISABLE_LAVA_POP.setKey("disableLavaPop"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.BLOCK_SOUND)
        public boolean disableGrowth = DefaultConfig.Sound.DISABLE_GROWTH;
        static { SoundTweak.DISABLE_GROWTH.setKey("disableGrowth"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.BLOCK_SOUND)
        public boolean disableFurnace = DefaultConfig.Sound.DISABLE_FURNACE;
        static { SoundTweak.DISABLE_FURNACE.setKey("disableFurnace"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.BLOCK_SOUND)
        public boolean disableDoorPlace = DefaultConfig.Sound.DISABLE_DOOR_PLACE;
        static { SoundTweak.DISABLE_DOOR.setKey("disableDoorPlace"); }

        /**
         * Damage Sounds
         */

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.DAMAGE_SOUND)
        public boolean oldAttack = DefaultConfig.Sound.OLD_ATTACK;
        static { SoundTweak.OLD_ATTACK.setKey("oldAttack"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.DAMAGE_SOUND)
        public boolean oldHurt = DefaultConfig.Sound.OLD_HURT;
        static { SoundTweak.OLD_HURT.setKey("oldHurt"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.DAMAGE_SOUND)
        public boolean oldFall = DefaultConfig.Sound.OLD_FALL;
        static { SoundTweak.OLD_FALL.setKey("oldFall"); }

        /**
         * Experience Sounds
         */

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.EXPERIENCE_SOUND)
        public boolean oldXp = DefaultConfig.Sound.OLD_XP;
        static { SoundTweak.OLD_XP.setKey("oldXp"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.EXPERIENCE_SOUND)
        public boolean disableXpPickup = DefaultConfig.Sound.DISABLE_XP_PICKUP;
        static { SoundTweak.DISABLE_PICKUP.setKey("disableXpPickup"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.EXPERIENCE_SOUND)
        public boolean disableXpLevel = DefaultConfig.Sound.DISABLE_XP_LEVEL;
        static { SoundTweak.DISABLE_LEVEL.setKey("disableXpLevel"); }

        /**
         * Mob Sounds
         */

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.MOB_GENERIC_SOUND)
        public boolean disableGenericSwim = DefaultConfig.Sound.DISABLE_GENERIC_SWIM;
        static { SoundTweak.DISABLE_GENERIC_SWIM.setKey("disableGenericSwim"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.MOB_FISH_SOUND)
        public boolean disableFishSwim = DefaultConfig.Sound.DISABLE_FISH_SWIM;
        static { SoundTweak.DISABLE_FISH_SWIM.setKey("disableFishSwim"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.MOB_FISH_SOUND)
        public boolean disableFishHurt = DefaultConfig.Sound.DISABLE_FISH_HURT;
        static { SoundTweak.DISABLE_FISH_HURT.setKey("disableFishHurt"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.MOB_FISH_SOUND)
        public boolean disableFishDeath = DefaultConfig.Sound.DISABLE_FISH_DEATH;
        static { SoundTweak.DISABLE_FISH_DEATH.setKey("disableFishDeath"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.MOB_SQUID_SOUND)
        public boolean disableSquid = DefaultConfig.Sound.DISABLE_SQUID;
        static { SoundTweak.DISABLE_SQUID.setKey("disableSquid"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.MOB_SQUID_SOUND)
        public boolean disableGlowSquidOther = DefaultConfig.Sound.DISABLE_GLOW_SQUID_OTHER;
        static { SoundTweak.DISABLE_GLOW_SQUID_OTHER.setKey("disableGlowSquidOther"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.MOB_SQUID_SOUND)
        public boolean disableGlowSquidAmbience = DefaultConfig.Sound.DISABLE_GLOW_SQUID_AMBIENCE;
        static { SoundTweak.DISABLE_GLOW_SQUID_AMBIENCE.setKey("disableGlowSquidAmbience"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.MOB_SOUND)
        public boolean oldStep = DefaultConfig.Sound.OLD_STEP;
        static { SoundTweak.OLD_STEP.setKey("oldStep"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Placement(pos = TweakGui.Position.BOTTOM, order = 1)
        @TweakGui.Category(container = TweakCategory.MOB_SOUND)
        public boolean ignoreModdedStep = DefaultConfig.Sound.IGNORE_MODDED_STEP;
        static { SoundTweak.IGNORE_MODDED_STEP.setKey("ignoreModdedStep"); }
    }

    @TweakData.Ignore
    public EyeCandy eyeCandy = new EyeCandy();
    public static class EyeCandy
    {
        /**
         * Block Candy
         */

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.BLOCK_CANDY)
        @TweakReload.Chunks
        public boolean fixAmbientOcclusion = DefaultConfig.Candy.FIX_AMBIENT_OCCLUSION;
        static { CandyTweak.FIX_AO.setKey("fixAmbientOcclusion"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.BLOCK_CANDY)
        @TweakReload.Chunks
        public boolean disableAllOffset = DefaultConfig.Candy.DISABLE_ALL_OFFSET;
        static { CandyTweak.DISABLE_ALL_OFFSET.setKey("disableAllOffset"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.BLOCK_CANDY)
        @TweakReload.Chunks
        public boolean disableFlowerOffset = DefaultConfig.Candy.DISABLE_FLOWER_OFFSET;
        static { CandyTweak.DISABLE_FLOWER_OFFSET.setKey("disableFlowerOffset"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.BLOCK_CANDY)
        @TweakReload.Resources
        public TweakVersion.MissingTexture oldMissingTexture = DefaultConfig.Candy.OLD_MISSING_TEXTURE;
        static { CandyTweak.OLD_MISSING_TEXTURE.setKey("oldMissingTexture"); }

        // Block - Hitbox Outlines

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.BLOCK_OUTLINE_CANDY)
        public boolean oldStairOutline = DefaultConfig.Candy.OLD_STAIR_OUTLINE;
        static { CandyTweak.OLD_STAIR_OUTLINE.setKey("oldStairOutline"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.BLOCK_OUTLINE_CANDY)
        public boolean oldFenceOutline = DefaultConfig.Candy.OLD_FENCE_OUTLINE;
        static { CandyTweak.OLD_FENCE_OUTLINE.setKey("oldFenceOutline"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.BLOCK_OUTLINE_CANDY)
        public boolean oldSlabOutline = DefaultConfig.Candy.OLD_SLAB_OUTLINE;
        static { CandyTweak.OLD_SLAB_OUTLINE.setKey("oldSlabOutline"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.BLOCK_OUTLINE_CANDY)
        public boolean oldWallOutline = DefaultConfig.Candy.OLD_WALL_OUTLINE;
        static { CandyTweak.OLD_WALL_OUTLINE.setKey("oldWallOutline"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Subcategory(container = TweakSubcategory.BLOCK_OUTLINE_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.BOTTOM, order = 1)
        @TweakData.List(id = ListId.FULL_BLOCK_OUTLINE)
        public Set<String> oldBlockOutlines = new HashSet<>();
        static { CandyTweak.FULL_BLOCK_OUTLINE.setKey("oldBlockOutlines"); }

        // Block - Chest Candy

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakReload.Resources
        @TweakGui.Optifine(incompatible = false)
        @TweakGui.Subcategory(container = TweakSubcategory.BLOCK_CHEST_CANDY)
        public boolean oldChest = DefaultConfig.Candy.OLD_CHEST;
        static { CandyTweak.CHEST.setKey("oldChest"); }

        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Warning
        @TweakGui.Subcategory(container = TweakSubcategory.BLOCK_CHEST_CANDY)
        @TweakReload.Chunks
        public boolean oldChestVoxel = DefaultConfig.Candy.OLD_CHEST_VOXEL;
        static { CandyTweak.CHEST_VOXEL.setKey("oldChestVoxel"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.BLOCK_CHEST_CANDY)
        @TweakReload.Resources
        public boolean oldEnderChest = DefaultConfig.Candy.OLD_ENDER_CHEST;
        static { CandyTweak.ENDER_CHEST.setKey("oldEnderChest"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.BLOCK_CHEST_CANDY)
        @TweakReload.Resources
        public boolean oldTrappedChest = DefaultConfig.Candy.OLD_TRAPPED_CHEST;
        static { CandyTweak.TRAPPED_CHEST.setKey("oldTrappedChest"); }

        // Block - Torch Candy

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        @TweakGui.Subcategory(container = TweakSubcategory.BLOCK_TORCH_CANDY)
        @TweakReload.Chunks
        public boolean oldTorchBrightness = DefaultConfig.Candy.OLD_TORCH_BRIGHTNESS;
        static { CandyTweak.TORCH_BRIGHTNESS.setKey("oldTorchBrightness"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Sodium
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        @TweakGui.Subcategory(container = TweakSubcategory.BLOCK_TORCH_CANDY)
        @TweakReload.Chunks
        public boolean oldTorchModel = DefaultConfig.Candy.OLD_TORCH_MODEL;
        static { CandyTweak.TORCH_MODEL.setKey("oldTorchModel"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Sodium
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 3)
        @TweakGui.Subcategory(container = TweakSubcategory.BLOCK_TORCH_CANDY)
        @TweakReload.Chunks
        public boolean oldRedstoneTorchModel = DefaultConfig.Candy.OLD_REDSTONE_TORCH_MODEL;
        static { CandyTweak.REDSTONE_TORCH_MODEL.setKey("oldRedstoneTorchModel"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Sodium
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 4)
        @TweakGui.Subcategory(container = TweakSubcategory.BLOCK_TORCH_CANDY)
        @TweakReload.Chunks
        public boolean oldSoulTorchModel = DefaultConfig.Candy.OLD_SOUL_TORCH_MODEL;
        static { CandyTweak.SOUL_TORCH_MODEL.setKey("oldSoulTorchModel"); }

        /**
         * Interface Candy
         */

        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.FAIL)
        @TweakGui.Category(container = TweakCategory.INTERFACE_CANDY)
        public boolean oldButtonHover = DefaultConfig.Candy.OLD_BUTTON_HOVER;
        static { CandyTweak.BUTTON_HOVER.setKey("oldButtonHover"); }

        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.INTERFACE_CANDY)
        public TweakVersion.Hotbar oldCreativeHotbar = DefaultConfig.Candy.OLD_CREATIVE_HOTBAR;
        static { CandyTweak.CREATIVE_HOTBAR.setKey("oldCreativeHotbar"); }

        // Interface - Window Title

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.DisabledBoolean(value = false)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_WINDOW_CANDY)
        public boolean enableWindowTitle = DefaultConfig.Candy.ENABLE_WINDOW_TITLE;
        static { CandyTweak.ENABLE_WINDOW_TITLE.setKey("enableWindowTitle"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Alert(condition = TweakGui.Condition.WINDOW_TITLE_DISABLED, langKey = LangUtil.Gui.ALERT_WINDOW_TITLE_DISABLED)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_WINDOW_CANDY)
        public boolean matchVersionOverlay = DefaultConfig.Candy.MATCH_VERSION_OVERLAY;
        static { CandyTweak.MATCH_VERSION_OVERLAY.setKey("matchVersionOverlay"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Alert(condition = TweakGui.Condition.WINDOW_TITLE_DISABLED, langKey = LangUtil.Gui.ALERT_WINDOW_TITLE_DISABLED)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 3)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_WINDOW_CANDY)
        public String windowTitleText = DefaultConfig.Candy.WINDOW_TITLE_TEXT;
        static { CandyTweak.WINDOW_TITLE.setKey("windowTitleText"); }

        // Interface - Debug Screen Candy

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_DEBUG_CANDY)
        public TweakVersion.Generic oldDebug = DefaultConfig.Candy.OLD_DEBUG;
        static { CandyTweak.DEBUG_SCREEN.setKey("oldDebug"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Placement(pos = TweakGui.Position.BOTTOM, order = 1)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_DEBUG_CANDY)
        public boolean debugEntityId = DefaultConfig.Candy.DEBUG_ENTITY_ID;
        static { CandyTweak.DEBUG_ENTITY_ID.setKey("debugEntityId"); }

        // Interface - Debug Charts

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        @TweakGui.Embed(container = TweakEmbed.DEBUG_CHART_CANDY)
        public TweakType.DebugChart fpsChart = DefaultConfig.Candy.FPS_CHART;
        static { CandyTweak.DEBUG_FPS_CHART.setKey("fpsChart"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        @TweakGui.Embed(container = TweakEmbed.DEBUG_CHART_CANDY)
        public boolean showDebugTpsChart = DefaultConfig.Candy.SHOW_DEBUG_TPS_CHART;
        static { CandyTweak.DEBUG_TPS_CHART.setKey("showDebugTpsChart"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 3)
        @TweakGui.Embed(container = TweakEmbed.DEBUG_CHART_CANDY)
        public boolean showDebugPieChart = DefaultConfig.Candy.SHOW_DEBUG_PIE_CHART;
        static { CandyTweak.DEBUG_PIE_CHART.setKey("showDebugPieChart"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 4)
        @TweakGui.Embed(container = TweakEmbed.DEBUG_CHART_CANDY)
        public boolean oldPieChartBackground = DefaultConfig.Candy.OLD_PIE_CHART_BACKGROUND;
        static { CandyTweak.OLD_PIE_BACKGROUND.setKey("oldPieChartBackground"); }

        // Interface - Debug Color Background

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.DisabledBoolean(value = false)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        @TweakGui.Embed(container = TweakEmbed.DEBUG_COLOR_CANDY)
        public boolean showDebugTextShadow = DefaultConfig.Candy.SHOW_DEBUG_TEXT_SHADOW;
        static { CandyTweak.DEBUG_SHOW_SHADOW.setKey("showDebugTextShadow"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.DisabledBoolean(value = true)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        @TweakGui.Embed(container = TweakEmbed.DEBUG_COLOR_CANDY)
        public boolean showDebugBackground = DefaultConfig.Candy.SHOW_DEBUG_BACKGROUND;
        static { CandyTweak.DEBUG_SHOW_COLOR.setKey("showDebugBackground"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.Color(reset = DefaultConfig.Candy.DEBUG_BACKGROUND_COLOR)
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.DisabledString(value = DefaultConfig.Candy.DEBUG_BACKGROUND_COLOR)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 3)
        @TweakGui.Embed(container = TweakEmbed.DEBUG_COLOR_CANDY)
        public String debugBackgroundColor = DefaultConfig.Candy.DEBUG_BACKGROUND_COLOR;
        static { CandyTweak.DEBUG_COLOR.setKey("debugBackgroundColor"); }

        // Interface - Extra Debug Information

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.DEBUG_EXTRA_CANDY)
        public boolean showDebugGpuUsage = DefaultConfig.Candy.SHOW_DEBUG_GPU_USAGE;
        static { CandyTweak.DEBUG_GPU.setKey("showDebugGpuUsage"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.DEBUG_EXTRA_CANDY)
        public boolean showDebugLightData = DefaultConfig.Candy.SHOW_DEBUG_LIGHT_DATA;
        static { CandyTweak.DEBUG_LIGHT.setKey("showDebugLightData"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.DEBUG_EXTRA_CANDY)
        public boolean showDebugFacingData = DefaultConfig.Candy.SHOW_DEBUG_FACING_DATA;
        static { CandyTweak.DEBUG_FACING.setKey("showDebugFacingData"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.DEBUG_EXTRA_CANDY)
        public boolean showDebugTargetData = DefaultConfig.Candy.SHOW_DEBUG_TARGET_DATA;
        static { CandyTweak.DEBUG_TARGETED.setKey("showDebugTargetData"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.DEBUG_EXTRA_CANDY)
        public boolean showDebugBiomeData = DefaultConfig.Candy.SHOW_DEBUG_BIOME_DATA;
        static { CandyTweak.DEBUG_BIOME.setKey("showDebugBiomeData"); }
        
        // Interface - Inventory Screen Candy

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_INVENTORY_CANDY)
        public boolean oldInventory = DefaultConfig.Candy.OLD_INVENTORY;
        static { CandyTweak.OLD_INVENTORY.setKey("oldInventory"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_INVENTORY_CANDY)
        public TweakType.RecipeBook inventoryBook = DefaultConfig.Candy.INVENTORY_BOOK;
        static { CandyTweak.INVENTORY_BOOK.setKey("inventoryBook"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 3)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_INVENTORY_CANDY)
        @TweakGui.Alert(condition = TweakGui.Condition.SHIELD_CONFLICT, langKey = LangUtil.Gui.ALERT_SHIELD)
        public TweakType.InventoryShield inventoryShield = DefaultConfig.Candy.INVENTORY_SHIELD;
        static { CandyTweak.INVENTORY_SHIELD.setKey("inventoryShield"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 4)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_INVENTORY_CANDY)
        public boolean disableEmptyArmorTexture = DefaultConfig.Candy.DISABLE_EMPTY_ARMOR_TEXTURE;
        static { CandyTweak.DISABLE_EMPTY_ARMOR.setKey("disableEmptyArmorTexture"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 5)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_INVENTORY_CANDY)
        public boolean disableEmptyShieldTexture = DefaultConfig.Candy.DISABLE_EMPTY_SHIELD_TEXTURE;
        static { CandyTweak.DISABLE_EMPTY_SHIELD.setKey("disableEmptyShieldTexture"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Placement(pos = TweakGui.Position.BOTTOM, order = 1)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_INVENTORY_CANDY)
        public boolean invertedBlockLighting = DefaultConfig.Candy.INVERTED_BLOCK_LIGHTING;
        static { CandyTweak.INVERTED_BLOCK_LIGHTING.setKey("invertedBlockLighting"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Placement(pos = TweakGui.Position.BOTTOM, order = 2)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_INVENTORY_CANDY)
        public boolean invertedPlayerLighting = DefaultConfig.Candy.INVERTED_PLAYER_LIGHTING;
        static { CandyTweak.INVERTED_PLAYER_LIGHTING.setKey("invertedPlayerLighting"); }

        // Interface - Screen Candy

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_GUI_CANDY)
        public TweakType.GuiBackground oldGuiBackground = DefaultConfig.Candy.OLD_GUI_BACKGROUND;
        static { CandyTweak.OLD_GUI_BACKGROUND.setKey("oldGuiBackground"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_GUI_CANDY)
        public boolean customGuiBackground = DefaultConfig.Candy.CUSTOM_GUI_BACKGROUND;
        static { CandyTweak.CUSTOM_GUI_BACKGROUND.setKey("customGuiBackground"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.Color(reset = DefaultConfig.Candy.CUSTOM_TOP_GRADIENT)
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 3)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_GUI_CANDY)
        public String customTopGradient = DefaultConfig.Candy.CUSTOM_TOP_GRADIENT;
        static { CandyTweak.CUSTOM_TOP_GRADIENT.setKey("customTopGradient"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.Color(reset = DefaultConfig.Candy.CUSTOM_BOTTOM_GRADIENT)
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 4)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_GUI_CANDY)
        public String customBottomGradient = DefaultConfig.Candy.CUSTOM_BOTTOM_GRADIENT;
        static { CandyTweak.CUSTOM_BOTTOM_GRADIENT.setKey("customBottomGradient"); }

        // Interface - Loading Screen

        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_LOADING_CANDY)
        public TweakVersion.Overlay oldLoadingOverlay = DefaultConfig.Candy.OLD_LOADING_OVERLAY;
        static { CandyTweak.LOADING_OVERLAY.setKey("oldLoadingOverlay"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Optifine(incompatible = false)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_LOADING_CANDY)
        public boolean removeLoadingBar = DefaultConfig.Candy.REMOVE_LOADING_BAR;
        static { CandyTweak.REMOVE_LOADING_BAR.setKey("removeLoadingBar"); }

        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.FAIL)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_LOADING_CANDY)
        public boolean oldLoadingScreens = DefaultConfig.Candy.OLD_LOADING_SCREENS;
        static { CandyTweak.LOADING_SCREENS.setKey("oldLoadingScreens"); }

        // Interface - Version Overlay

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_VERSION_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        public boolean oldVersionOverlay = DefaultConfig.Candy.OLD_VERSION_OVERLAY;
        static { CandyTweak.VERSION_OVERLAY.setKey("oldVersionOverlay"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_VERSION_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        public TweakType.Corner oldOverlayCorner = DefaultConfig.Candy.OLD_OVERLAY_CORNER;
        static { CandyTweak.VERSION_CORNER.setKey("oldOverlayCorner"); }

        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_VERSION_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 3)
        public String oldOverlayText = DefaultConfig.Candy.OLD_OVERLAY_TEXT;
        static { CandyTweak.VERSION_TEXT.setKey("oldOverlayText"); }

        // Interface - Pause Screen

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_PAUSE_CANDY)
        public TweakVersion.PauseLayout oldPauseMenu = DefaultConfig.Candy.OLD_PAUSE_MENU;
        static { CandyTweak.PAUSE_LAYOUT.setKey("oldPauseMenu"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_PAUSE_CANDY)
        public boolean includeModsOnPause = DefaultConfig.Candy.INCLUDE_MODS_ON_PAUSE;
        static { CandyTweak.PAUSE_MODS.setKey("includeModsOnPause"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_PAUSE_CANDY)
        public boolean removeExtraPauseButtons = DefaultConfig.Candy.REMOVE_EXTRA_PAUSE_BUTTONS;
        static { CandyTweak.PAUSE_REMOVE_EXTRA.setKey("removeExtraPauseButtons"); }

        // Interface - Anvil Screen

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_ANVIL_CANDY)
        public boolean oldAnvilScreen = DefaultConfig.Candy.OLD_ANVIL_SCREEN;
        static { CandyTweak.ANVIL_SCREEN.setKey("oldAnvilScreen"); }

        // Interface - Crafting Screen

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_CRAFTING_CANDY)
        public boolean oldCraftingScreen = DefaultConfig.Candy.OLD_CRAFTING_SCREEN;
        static { CandyTweak.CRAFTING_SCREEN.setKey("oldCraftingScreen"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_CRAFTING_CANDY)
        public TweakType.RecipeBook craftingBook = DefaultConfig.Candy.CRAFTING_BOOK;
        static { CandyTweak.CRAFTING_RECIPE.setKey("craftingBook"); }

        // Interface - Furnace Screen

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_FURNACE_CANDY)
        public boolean oldFurnaceScreen = DefaultConfig.Candy.OLD_FURNACE_SCREEN;
        static { CandyTweak.FURNACE_SCREEN.setKey("oldFurnaceScreen"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_FURNACE_CANDY)
        public TweakType.RecipeBook furnaceBook = DefaultConfig.Candy.FURNACE_BOOK;
        static { CandyTweak.FURNACE_RECIPE.setKey("furnaceBook"); }

        // Interface - Chat Screen

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_CHAT_CANDY)
        public boolean oldChatInput = DefaultConfig.Candy.OLD_CHAT_INPUT;
        static { CandyTweak.CHAT_INPUT.setKey("oldChatInput"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_CHAT_CANDY)
        public boolean oldChatBox = DefaultConfig.Candy.OLD_CHAT_BOX;
        static { CandyTweak.CHAT_BOX.setKey("oldChatBox"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_CHAT_CANDY)
        public boolean disableSignatureBoxes = DefaultConfig.Candy.DISABLE_SIGNATURE_BOXES;
        static { CandyTweak.SIGNATURE_BOXES.setKey("disableSignatureBoxes"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakData.BoundedSlider(min = 0, max = 32, reset = DefaultConfig.Candy.CHAT_OFFSET)
        @TweakGui.Slider(type = TweakGui.SliderType.GENERIC, langKey = LangUtil.Gui.SLIDER_OFFSET)
        @TweakGui.Placement(pos = TweakGui.Position.BOTTOM, order = 1)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_CHAT_CANDY)
        public int chatOffset = DefaultConfig.Candy.CHAT_OFFSET;
        static { CandyTweak.CHAT_OFFSET.setKey("chatOffset"); }

        /* Interface - Tooltip Candy */

        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.FAIL)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_TOOLTIP_CANDY)
        public boolean oldTooltipBoxes = DefaultConfig.Candy.OLD_TOOLTIP_BOXES;
        static { CandyTweak.TOOLTIP_BOXES.setKey("oldTooltipBoxes"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_TOOLTIP_CANDY)
        public boolean oldNoItemTooltips = DefaultConfig.Candy.OLD_NO_ITEM_TOOLTIPS;
        static { CandyTweak.NO_ITEM_TOOLTIPS.setKey("oldNoItemTooltips"); }

        // Tooltip Candy - Tooltip Parts

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.DisabledBoolean(value = true)
        @TweakGui.Embed(container = TweakEmbed.TOOLTIP_PARTS_CANDY)
        public boolean showEnchantmentTip = DefaultConfig.Candy.SHOW_ENCHANTMENTS_TIP;
        static { CandyTweak.ENCHANTMENT_TIP.setKey("showEnchantmentTip"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.DisabledBoolean(value = true)
        @TweakGui.Embed(container = TweakEmbed.TOOLTIP_PARTS_CANDY)
        public boolean showModifiersTip = DefaultConfig.Candy.SHOW_MODIFIERS_TIP;
        static { CandyTweak.MODIFIERS_TIP.setKey("showModifiersTip"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.DisabledBoolean(value = true)
        @TweakGui.Embed(container = TweakEmbed.TOOLTIP_PARTS_CANDY)
        public boolean showDyeTip = DefaultConfig.Candy.SHOW_DYE_TIP;
        static { CandyTweak.DYE_TIP.setKey("showDyeTip"); }

        /**
         * Item Candy
         */

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        @TweakGui.Category(container = TweakCategory.ITEM_CANDY)
        @TweakReload.Resources
        public boolean fixItemModelGap = DefaultConfig.Candy.FIX_ITEM_MODEL_GAP;
        static { CandyTweak.FIX_ITEM_MODEL_GAP.setKey("fixItemModelGap"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Optifine
        @TweakGui.Category(container = TweakCategory.ITEM_CANDY)
        public boolean oldDamageArmorTint = DefaultConfig.Candy.OLD_DAMAGE_ARMOR_TINT;
        static { CandyTweak.DAMAGE_ARMOR_TINT.setKey("oldDamageArmorTint"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.ITEM_CANDY)
        public boolean oldItemHolding = DefaultConfig.Candy.OLD_ITEM_HOLDING;
        static { CandyTweak.ITEM_HOLDING.setKey("oldItemHolding"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Placement(pos = TweakGui.Position.BOTTOM, order = 1)
        @TweakGui.Category(container = TweakCategory.ITEM_CANDY)
        @TweakData.List(id = ListId.IGNORED_ITEM_HOLDING)
        public Set<String> ignoredHoldingItems = new HashSet<>();
        static { CandyTweak.IGNORED_ITEM_HOLDING.setKey("ignoredHoldingItems"); }

        @TweakData.Ignore
        public Set<String> disabledIgnoredHoldingItems = new HashSet<>();

        // Item - Merging

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakData.BoundedSlider(min = 1, max = 64, reset = DefaultConfig.Candy.ITEM_MERGE_LIMIT)
        @TweakGui.Slider(type = TweakGui.SliderType.GENERIC, langKey = LangUtil.Gui.SLIDER_LIMIT)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        @TweakGui.Subcategory(container = TweakSubcategory.MERGING_ITEM_CANDY)
        public int itemMergeLimit = DefaultConfig.Candy.ITEM_MERGE_LIMIT;
        static { CandyTweak.ITEM_MERGE_LIMIT.setKey("itemMergeLimit"); }

        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.MERGING_ITEM_CANDY)
        public boolean oldItemMerging = DefaultConfig.Candy.OLD_ITEM_MERGING;
        static { CandyTweak.ITEM_MERGING.setKey("oldItemMerging"); }

        // Item - 2D Candy

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.FLAT_ITEM_CANDY)
        public boolean old2dColors = DefaultConfig.Candy.OLD_2D_COLORS;
        static { CandyTweak.FLAT_COLORS.setKey("old2dColors"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.FLAT_ITEM_CANDY)
        public boolean old2dItems = DefaultConfig.Candy.OLD_2D_ITEMS;
        static { CandyTweak.FLAT_ITEMS.setKey("old2dItems"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.FLAT_ITEM_CANDY)
        public boolean old2dFrames = DefaultConfig.Candy.OLD_2D_FRAMES;
        static { CandyTweak.FLAT_FRAMES.setKey("old2dFrames"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.FLAT_ITEM_CANDY)
        public boolean old2dThrownItems = DefaultConfig.Candy.OLD_2D_THROWN_ITEMS;
        static { CandyTweak.FLAT_THROW_ITEMS.setKey("old2dThrownItems"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.FLAT_ITEM_CANDY)
        public boolean old2dEnchantedItems = DefaultConfig.Candy.OLD_2D_ENCHANTED_ITEMS;
        static { CandyTweak.FLAT_ENCHANTED_ITEMS.setKey("old2dEnchantedItems"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Optifine
        @TweakGui.Subcategory(container = TweakSubcategory.FLAT_ITEM_CANDY)
        public boolean old2dRendering = DefaultConfig.Candy.OLD_2D_RENDERING;
        static { CandyTweak.FLAT_RENDERING.setKey("old2dRendering"); }

        // Item - Interface

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.DISPLAY_ITEM_CANDY)
        public boolean oldDurabilityColors = DefaultConfig.Candy.OLD_DURABILITY_COLORS;
        static { CandyTweak.DURABILITY_COLORS.setKey("oldDurabilityColors"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.DISPLAY_ITEM_CANDY)
        public boolean oldNoSelectedItemName = DefaultConfig.Candy.OLD_NO_SELECTED_ITEM_NAME;
        static { CandyTweak.NO_SELECTED_ITEM_NAME.setKey("oldNoSelectedItemName"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.DISPLAY_ITEM_CANDY)
        public boolean oldPlainSelectedItemName = DefaultConfig.Candy.OLD_PLAIN_SELECTED_ITEM_NAME;
        static { CandyTweak.PLAIN_SELECTED_ITEM_NAME.setKey("oldPlainSelectedItemName"); }

        /**
         * Lighting Candy
         */

        // Lighting - World Lighting Candy

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        @TweakGui.Subcategory(container = TweakSubcategory.LIGHTING_WORLD_CANDY)
        public boolean fixChunkBorderLag = DefaultConfig.Candy.FIX_CHUNK_BORDER_LAG;
        static { CandyTweak.FIX_CHUNK_BORDER_LAG.setKey("fixChunkBorderLag"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        @TweakGui.Subcategory(container = TweakSubcategory.LIGHTING_WORLD_CANDY)
        @TweakGui.Alert(condition = TweakGui.Condition.BRIGHTNESS_CONFLICT, langKey = LangUtil.Gui.ALERT_BRIGHTNESS)
        public boolean disableBrightness = DefaultConfig.Candy.DISABLE_BRIGHTNESS;
        static { CandyTweak.DISABLE_BRIGHTNESS.setKey("disableBrightness"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 3)
        @TweakGui.Subcategory(container = TweakSubcategory.LIGHTING_WORLD_CANDY)
        public boolean disableLightFlicker = DefaultConfig.Candy.DISABLE_LIGHT_FLICKER;
        static { CandyTweak.LIGHT_FLICKER.setKey("disableLightFlicker"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.LIGHTING_WORLD_CANDY)
        @TweakReload.Chunks
        public boolean oldClassicLighting = DefaultConfig.Candy.OLD_CLASSIC_LIGHTING;
        static { CandyTweak.CLASSIC_LIGHTING.setKey("oldClassicLighting"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.LIGHTING_WORLD_CANDY)
        @TweakReload.Chunks
        public boolean oldNetherLighting = DefaultConfig.Candy.OLD_NETHER_LIGHTING;
        static { CandyTweak.NETHER_LIGHTING.setKey("oldNetherLighting"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakData.Conflict(modId = "lod")
        @TweakGui.Optifine(incompatible = false)
        @TweakGui.Sodium(incompatible = false)
        @TweakGui.Subcategory(container = TweakSubcategory.LIGHTING_WORLD_CANDY)
        @TweakReload.Chunks
        public boolean oldLightRendering = DefaultConfig.Candy.OLD_LIGHT_RENDERING;
        static { CandyTweak.LIGHT_RENDERING.setKey("oldLightRendering"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.LIGHTING_WORLD_CANDY)
        @TweakGui.Alert(condition = TweakGui.Condition.LIGHT_CONFLICT, langKey = LangUtil.Gui.ALERT_LIGHT)
        public boolean oldLightColor = DefaultConfig.Candy.OLD_LIGHT_COLOR;
        static { CandyTweak.LIGHT_COLOR.setKey("oldLightColor"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.LIGHTING_WORLD_CANDY)
        @TweakReload.Chunks
        public boolean oldSmoothLighting = DefaultConfig.Candy.OLD_SMOOTH_LIGHTING;
        static { CandyTweak.SMOOTH_LIGHTING.setKey("oldSmoothLighting"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakData.BoundedSlider(min = 0, max = 15, reset = DefaultConfig.Candy.MAX_BLOCK_LIGHT)
        @TweakGui.Slider(type = TweakGui.SliderType.GENERIC, langKey = LangUtil.Gui.SLIDER_BLOCK_LIGHT)
        @TweakGui.Embed(container = TweakEmbed.SHADER_LIGHT)
        @TweakReload.Chunks
        public int maxBlockLight = DefaultConfig.Candy.MAX_BLOCK_LIGHT;
        static { CandyTweak.MAX_BLOCK_LIGHT.setKey("maxBlockLight"); }

        // Lighting - Block Lighting Candy

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.LIGHTING_BLOCK_CANDY)
        @TweakReload.Chunks
        public boolean oldLeavesLighting = DefaultConfig.Candy.OLD_LEAVES_LIGHTING;
        static { CandyTweak.LEAVES_LIGHTING.setKey("oldLeavesLighting"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.LIGHTING_BLOCK_CANDY)
        @TweakReload.Chunks
        public boolean oldWaterLighting = DefaultConfig.Candy.OLD_WATER_LIGHTING;
        static { CandyTweak.WATER_LIGHTING.setKey("oldWaterLighting"); }

        /**
         * Particle Candy
         */

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.PARTICLE_CANDY)
        public boolean oldOpaqueExperience = DefaultConfig.Candy.OLD_OPAQUE_EXPERIENCE;
        static { CandyTweak.OPAQUE_EXPERIENCE.setKey("oldOpaqueExperience"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.PARTICLE_CANDY)
        public boolean disableNetherParticles = DefaultConfig.Candy.DISABLE_NETHER_PARTICLES;
        static { CandyTweak.NO_NETHER_PARTICLES.setKey("disableNetherParticles"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.PARTICLE_CANDY)
        public boolean disableUnderwaterParticles = DefaultConfig.Candy.DISABLE_UNDERWATER_PARTICLES;
        static { CandyTweak.NO_UNDERWATER_PARTICLES.setKey("disableUnderwaterParticles"); }

        // Particle - Block Candy

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.PARTICLE_BLOCK_CANDY)
        public boolean disableLavaParticles = DefaultConfig.Candy.DISABLE_LAVA_PARTICLES;
        static { CandyTweak.NO_LAVA_PARTICLES.setKey("disableLavaParticles"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.PARTICLE_BLOCK_CANDY)
        public boolean disableLeverParticles = DefaultConfig.Candy.DISABLE_LEVER_PARTICLES;
        static { CandyTweak.NO_LEVER_PARTICLES.setKey("disableLeverParticles"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.PARTICLE_BLOCK_CANDY)
        public boolean disableModelDestructionParticles = DefaultConfig.Candy.DISABLE_MODEL_DESTRUCTION_PARTICLES;
        static { CandyTweak.NO_MODEL_DESTRUCTION_PARTICLES.setKey("disableModelDestructionParticles"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.PARTICLE_BLOCK_CANDY)
        public boolean disableGrowthParticles = DefaultConfig.Candy.DISABLE_GROWTH_PARTICLES;
        static { CandyTweak.NO_GROWTH_PARTICLES.setKey("disableGrowthParticles"); }

        // Particle - Player Candy

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.PARTICLE_PLAYER_CANDY)
        public boolean disableFallingParticles = DefaultConfig.Candy.DISABLE_FALLING_PARTICLES;
        static { CandyTweak.NO_FALLING_PARTICLES.setKey("disableFallingParticles"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.PARTICLE_PLAYER_CANDY)
        public boolean disableSprintingParticles = DefaultConfig.Candy.DISABLE_SPRINTING_PARTICLES;
        static { CandyTweak.NO_SPRINTING_PARTICLES.setKey("disableSprintingParticles"); }

        // Particle - Attack Candy

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.PARTICLE_ATTACK_CANDY)
        public boolean oldSweepParticles = DefaultConfig.Candy.OLD_SWEEP_PARTICLES;
        static { CandyTweak.SWEEP.setKey("oldSweepParticles"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.PARTICLE_ATTACK_CANDY)
        public boolean oldNoDamageParticles = DefaultConfig.Candy.OLD_NO_DAMAGE_PARTICLES;
        static { CandyTweak.NO_DAMAGE_PARTICLES.setKey("oldNoDamageParticles"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.PARTICLE_ATTACK_CANDY)
        public boolean oldNoCritParticles = DefaultConfig.Candy.OLD_NO_CRIT_PARTICLES;
        static { CandyTweak.NO_CRIT_PARTICLES.setKey("oldNoCritParticles"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.PARTICLE_ATTACK_CANDY)
        public boolean oldNoMagicHitParticles = DefaultConfig.Candy.OLD_NO_MAGIC_HIT_PARTICLES;
        static { CandyTweak.NO_MAGIC_HIT_PARTICLES.setKey("oldNoMagicHitParticles"); }

        // Particle - Explosion Candy

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.PARTICLE_EXPLOSION_CANDY)
        public boolean oldExplosionParticles = DefaultConfig.Candy.OLD_EXPLOSION_PARTICLES;
        static { CandyTweak.EXPLOSION_PARTICLES.setKey("oldExplosionParticles"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.PARTICLE_EXPLOSION_CANDY)
        public boolean oldMixedExplosionParticles = DefaultConfig.Candy.OLD_MIXED_EXPLOSION_PARTICLES;
        static { CandyTweak.MIXED_EXPLOSION_PARTICLES.setKey("oldMixedExplosionParticles"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.PARTICLE_EXPLOSION_CANDY)
        public boolean unoptimizedExplosionParticles = DefaultConfig.Candy.UNOPTIMIZED_EXPLOSION_PARTICLES;
        static { CandyTweak.UNOPTIMIZED_EXPLOSION_PARTICLES.setKey("unoptimizedExplosionParticles"); }

        /**
         * Title Screen Candy (Embedded in Interface Candy)
         */

        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.FAIL)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_TITLE_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        public boolean overrideTitleScreen = DefaultConfig.Candy.OVERRIDE_TITLE_SCREEN;
        static { CandyTweak.OVERRIDE_TITLE_SCREEN.setKey("overrideTitleScreen"); }

        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_TITLE_CANDY)
        public boolean oldTitleBackground = DefaultConfig.Candy.OLD_TITLE_BACKGROUND;
        static { CandyTweak.TITLE_BACKGROUND.setKey("oldTitleBackground"); }

        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Subcategory(container = TweakSubcategory.INTERFACE_TITLE_CANDY)
        public boolean uncapTitleFPS = DefaultConfig.Candy.UNCAP_TITLE_FPS;
        static { CandyTweak.UNCAP_TITLE_FPS.setKey("uncapTitleFPS"); }

        // Title Screen - Logo

        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Embed(container = TweakEmbed.TITLE_LOGO_CANDY)
        public boolean oldAlphaLogo = DefaultConfig.Candy.OLD_ALPHA_LOGO;
        static { CandyTweak.ALPHA_LOGO.setKey("oldAlphaLogo"); }

        // Title Screen - Buttons

        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Embed(container = TweakEmbed.TITLE_BUTTON_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        public TweakVersion.TitleLayout oldButtonLayout = DefaultConfig.Candy.TITLE_BUTTON_LAYOUT;
        static { CandyTweak.TITLE_BUTTON_LAYOUT.setKey("oldButtonLayout"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Embed(container = TweakEmbed.TITLE_BUTTON_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        public boolean includeModsOnTitle = DefaultConfig.Candy.INCLUDE_MODS_ON_TITLE;
        static { CandyTweak.TITLE_MODS_BUTTON.setKey("includeModsOnTitle"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Embed(container = TweakEmbed.TITLE_BUTTON_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 3)
        public boolean removeTitleRealmsButton = DefaultConfig.Candy.REMOVE_TITLE_REALMS;
        static { CandyTweak.TITLE_REALMS.setKey("removeTitleRealmsButton"); }

        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Embed(container = TweakEmbed.TITLE_BUTTON_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 4)
        public boolean removeTitleAccessibilityButton = DefaultConfig.Candy.REMOVE_TITLE_ACCESSIBILITY;
        static { CandyTweak.TITLE_ACCESSIBILITY.setKey("removeTitleAccessibilityButton"); }

        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Embed(container = TweakEmbed.TITLE_BUTTON_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 5)
        public boolean removeTitleLanguageButton = DefaultConfig.Candy.REMOVE_TITLE_LANGUAGE;
        static { CandyTweak.TITLE_LANGUAGE.setKey("removeTitleLanguageButton"); }

        // Title Screen - Text

        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Embed(container = TweakEmbed.TITLE_TEXT_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        public String titleVersionText = DefaultConfig.Candy.TITLE_VERSION_TEXT;

        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Embed(container = TweakEmbed.TITLE_TEXT_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        public boolean titleBottomLeftText = DefaultConfig.Candy.TITLE_BOTTOM_LEFT_TEXT;
        static { CandyTweak.TITLE_BOTTOM_LEFT_TEXT.setKey("titleBottomLeftText"); }

        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Embed(container = TweakEmbed.TITLE_TEXT_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 3)
        public boolean removeTitleModLoaderText = DefaultConfig.Candy.REMOVE_TITLE_MOD_LOADER_TEXT;
        static { CandyTweak.TITLE_MOD_LOADER_TEXT.setKey("removeTitleModLoaderText"); }

        /**
         * World Candy
         */

        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.WORLD_CANDY)
        @TweakReload.Chunks
        public boolean oldSquareBorder = DefaultConfig.Candy.OLD_SQUARE_BORDER;
        static { CandyTweak.SQUARE_BORDER.setKey("oldSquareBorder"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.WORLD_CANDY)
        public boolean oldNameTags = DefaultConfig.Candy.OLD_NAME_TAGS;
        static { CandyTweak.NAME_TAGS.setKey("oldNameTags"); }

        // World - Fog Candy

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.WORLD_FOG_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        public TweakVersion.WorldFog oldWorldFog = DefaultConfig.Candy.OLD_WORLD_FOG;
        static { CandyTweak.WORLD_FOG.setKey("oldWorldFog"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.WORLD_FOG_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        public boolean disableHorizonFog = DefaultConfig.Candy.DISABLE_HORIZON_FOG;
        static { CandyTweak.DISABLE_HORIZON_FOG.setKey("disableHorizonFog"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.WORLD_FOG_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 3)
        public boolean oldNetherFog = DefaultConfig.Candy.OLD_NETHER_FOG;
        static { CandyTweak.NETHER_FOG.setKey("oldNetherFog"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.WORLD_FOG_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 4)
        public boolean oldSunriseSunsetFog = DefaultConfig.Candy.OLD_SUNRISE_SUNSET_FOG;
        static { CandyTweak.SUNRISE_SUNSET_FOG.setKey("oldSunriseSunsetFog"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.WORLD_FOG_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 5)
        public boolean oldDarkFog = DefaultConfig.Candy.OLD_DARK_FOG;
        static { CandyTweak.DARK_FOG.setKey("oldDarkFog"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Alert(condition = TweakGui.Condition.DYNAMIC_FOG_CONFLICT, langKey = LangUtil.Gui.ALERT_DYNAMIC_FOG)
        @TweakGui.Subcategory(container = TweakSubcategory.WORLD_FOG_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 6)
        public boolean oldDynamicFogColor = DefaultConfig.Candy.OLD_DYNAMIC_FOG;
        static { CandyTweak.DYNAMIC_FOG_COLOR.setKey("oldDynamicFogColor"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Alert(condition = TweakGui.Condition.UNIVERSAL_FOG_CONFLICT, langKey = LangUtil.Gui.ALERT_UNIVERSAL_FOG)
        @TweakGui.Subcategory(container = TweakSubcategory.WORLD_FOG_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 7)
        public TweakVersion.FogColor universalFogColor = DefaultConfig.Candy.UNIVERSAL_FOG_COLOR;
        static { CandyTweak.UNIVERSAL_FOG_COLOR.setKey("universalFogColor"); }

        // World - Fog Candy - Custom

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.CUSTOM_FOG_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        public boolean customTerrainFog = DefaultConfig.Candy.CUSTOM_TERRAIN_FOG;
        static { CandyTweak.CUSTOM_TERRAIN_FOG.setKey("customTerrainFog"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakData.Color(reset = DefaultConfig.Candy.CUSTOM_TERRAIN_FOG_COLOR)
        @TweakGui.Embed(container = TweakEmbed.CUSTOM_FOG_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        public String customTerrainFogColor = DefaultConfig.Candy.CUSTOM_TERRAIN_FOG_COLOR;
        static { CandyTweak.CUSTOM_TERRAIN_FOG_COLOR.setKey("customTerrainFogColor"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.CUSTOM_FOG_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 3)
        public boolean customNetherFog = DefaultConfig.Candy.CUSTOM_NETHER_FOG;
        static { CandyTweak.CUSTOM_NETHER_FOG.setKey("customNetherFog"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakData.Color(reset = DefaultConfig.Candy.CUSTOM_NETHER_FOG_COLOR)
        @TweakGui.Embed(container = TweakEmbed.CUSTOM_FOG_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 4)
        public String customNetherFogColor = DefaultConfig.Candy.CUSTOM_NETHER_FOG_COLOR;
        static { CandyTweak.CUSTOM_NETHER_FOG_COLOR.setKey("customNetherFogColor"); }

        // World - Fog Candy - Water

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.WATER_FOG_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        public boolean oldWaterFogDensity = DefaultConfig.Candy.OLD_WATER_FOG_DENSITY;
        static { CandyTweak.WATER_FOG_DENSITY.setKey("oldWaterFogDensity"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.WATER_FOG_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        public boolean oldWaterFogColor = DefaultConfig.Candy.OLD_WATER_FOG_COLOR;
        static { CandyTweak.WATER_FOG_COLOR.setKey("oldWaterFogColor"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.WATER_FOG_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 3)
        public boolean smoothWaterDensity = DefaultConfig.Candy.SMOOTH_WATER_DENSITY;
        static { CandyTweak.SMOOTH_WATER_DENSITY.setKey("smoothWaterDensity"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.WATER_FOG_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 4)
        public boolean smoothWaterColor = DefaultConfig.Candy.SMOOTH_WATER_COLOR;
        static { CandyTweak.SMOOTH_WATER_COLOR.setKey("smoothWaterColor"); }

        // World - Sky Candy

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.WORLD_SKY_CANDY)
        public boolean disableSunriseSunsetColors = DefaultConfig.Candy.DISABLE_SUNRISE_SUNSET_COLORS;
        static { CandyTweak.DISABLE_SUNRISE_SUNSET_COLOR.setKey("disableSunriseSunsetColors"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.WORLD_SKY_CANDY)
        public boolean oldSunriseAtNorth = DefaultConfig.Candy.OLD_SUNRISE_AT_NORTH;
        static { CandyTweak.SUNRISE_AT_NORTH.setKey("oldSunriseAtNorth"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.WORLD_SKY_CANDY)
        public TweakVersion.Generic oldStars = DefaultConfig.Candy.OLD_STARS;
        static { CandyTweak.STARS.setKey("oldStars"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Alert(condition = TweakGui.Condition.DYNAMIC_SKY_CONFLICT, langKey = LangUtil.Gui.ALERT_DYNAMIC_SKY)
        @TweakGui.Subcategory(container = TweakSubcategory.WORLD_SKY_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.BOTTOM, order = 1)
        public boolean oldDynamicSkyColor = DefaultConfig.Candy.OLD_DYNAMIC_SKY;
        static { CandyTweak.DYNAMIC_SKY_COLOR.setKey("oldDynamicSkyColor"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Alert(condition = TweakGui.Condition.UNIVERSAL_SKY_CONFLICT, langKey = LangUtil.Gui.ALERT_UNIVERSAL_SKY)
        @TweakGui.Subcategory(container = TweakSubcategory.WORLD_SKY_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.BOTTOM, order = 2)
        public TweakVersion.SkyColor universalSkyColor = DefaultConfig.Candy.UNIVERSAL_SKY_COLOR;
        static { CandyTweak.UNIVERSAL_SKY_COLOR.setKey("universalSkyColor"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.WORLD_SKY_CANDY)
        public boolean oldNetherSky = DefaultConfig.Candy.OLD_NETHER_SKY;
        static { CandyTweak.NETHER_SKY.setKey("oldNetherSky"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakData.BoundedSlider(min = 108, max = DefaultConfig.Candy.DISABLED_CLOUD_HEIGHT, reset = DefaultConfig.Candy.OLD_CLOUD_HEIGHT)
        @TweakGui.DisabledInteger(value = DefaultConfig.Candy.DISABLED_CLOUD_HEIGHT)
        @TweakGui.Subcategory(container = TweakSubcategory.WORLD_SKY_CANDY)
        @TweakGui.Slider(type = TweakGui.SliderType.CLOUD)
        public int oldCloudHeight = DefaultConfig.Candy.OLD_CLOUD_HEIGHT;
        static { CandyTweak.CLOUD_HEIGHT.setKey("oldCloudHeight"); }

        // World - Custom Sky

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.CUSTOM_SKY_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        public boolean customWorldSky = DefaultConfig.Candy.CUSTOM_WORLD_SKY;
        static { CandyTweak.CUSTOM_WORLD_SKY.setKey("customWorldSky"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakData.Color(reset = DefaultConfig.Candy.CUSTOM_WORLD_SKY_COLOR)
        @TweakGui.Embed(container = TweakEmbed.CUSTOM_SKY_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        public String customWorldSkyColor = DefaultConfig.Candy.CUSTOM_WORLD_SKY_COLOR;
        static { CandyTweak.CUSTOM_WORLD_SKY_COLOR.setKey("customWorldSkyColor"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.CUSTOM_SKY_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 3)
        public boolean customNetherSky = DefaultConfig.Candy.CUSTOM_NETHER_SKY;
        static { CandyTweak.CUSTOM_NETHER_SKY.setKey("customNetherSky"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakData.Color(reset = DefaultConfig.Candy.CUSTOM_NETHER_SKY_COLOR)
        @TweakGui.Embed(container = TweakEmbed.CUSTOM_SKY_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 4)
        public String customNetherSkyColor = DefaultConfig.Candy.CUSTOM_NETHER_SKY_COLOR;
        static { CandyTweak.CUSTOM_NETHER_SKY_COLOR.setKey("customNetherSkyColor"); }

        /* World - Void Candy */

        // Void Sky Candy

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        @TweakGui.Embed(container = TweakEmbed.VOID_SKY_CANDY)
        public TweakVersion.Generic oldBlueVoid = DefaultConfig.Candy.OLD_BLUE_VOID;
        static { CandyTweak.BLUE_VOID.setKey("oldBlueVoid"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.VOID_SKY_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        public boolean oldBlueVoidOverride = DefaultConfig.Candy.OLD_BLUE_VOID_OVERRIDE;
        static { CandyTweak.BLUE_VOID_OVERRIDE.setKey("oldBlueVoidOverride"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Alert(condition = TweakGui.Condition.VOID_CONFLICT, langKey = LangUtil.Gui.ALERT_VOID)
        @TweakGui.Embed(container = TweakEmbed.VOID_SKY_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 3)
        public boolean oldDarkVoidHeight = DefaultConfig.Candy.OLD_DARK_VOID_HEIGHT;
        static { CandyTweak.DARK_VOID_HEIGHT.setKey("oldDarkVoidHeight"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.VOID_SKY_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 4)
        public boolean customVoidSky = DefaultConfig.Candy.CUSTOM_VOID_SKY;
        static { CandyTweak.CUSTOM_VOID_SKY.setKey("customVoidSky"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakData.Color(reset = DefaultConfig.Candy.CUSTOM_VOID_SKY_COLOR)
        @TweakGui.Embed(container = TweakEmbed.VOID_SKY_CANDY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 5)
        public String customVoidSkyColor = DefaultConfig.Candy.CUSTOM_VOID_SKY_COLOR;
        static { CandyTweak.CUSTOM_VOID_SKY_COLOR.setKey("customVoidSkyColor"); }

        // Void Fog Candy

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.DisabledBoolean(value = true)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        @TweakGui.Embed(container = TweakEmbed.VOID_FOG_CANDY)
        public boolean disableVoidFog = DefaultConfig.Candy.DISABLE_VOID_FOG;
        static { CandyTweak.DISABLE_VOID_FOG.setKey("disableVoidFog"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        @TweakGui.Embed(container = TweakEmbed.VOID_FOG_CANDY)
        public boolean creativeVoidFog = DefaultConfig.Candy.CREATIVE_VOID_FOG;
        static { CandyTweak.CREATIVE_VOID_FOG.setKey("creativeVoidFog"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 3)
        @TweakGui.Embed(container = TweakEmbed.VOID_FOG_CANDY)
        public boolean creativeVoidParticles = DefaultConfig.Candy.CREATIVE_VOID_PARTICLE;
        static { CandyTweak.CREATIVE_VOID_PARTICLE.setKey("creativeVoidParticles"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.DisabledBoolean(value = true)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 4)
        @TweakGui.Embed(container = TweakEmbed.VOID_FOG_CANDY)
        public boolean lightRemovesVoidFog = DefaultConfig.Candy.LIGHT_REMOVES_VOID_FOG;
        static { CandyTweak.LIGHT_REMOVES_VOID_FOG.setKey("lightRemovesVoidFog"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.Color(reset = DefaultConfig.Candy.VOID_FOG_COLOR)
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 5)
        @TweakGui.Embed(container = TweakEmbed.VOID_FOG_CANDY)
        public String voidFogColor = DefaultConfig.Candy.VOID_FOG_COLOR;
        static { CandyTweak.VOID_FOG_COLOR.setKey("voidFogColor"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakData.BoundedSlider(min = 0, max = 100, reset = DefaultConfig.Candy.VOID_FOG_ENCROACH)
        @TweakGui.Slider(type = TweakGui.SliderType.GENERIC, suffix = "%", langKey = LangUtil.Gui.SLIDER_ENCROACH)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 6)
        @TweakGui.Embed(container = TweakEmbed.VOID_FOG_CANDY)
        public int voidFogEncroach = DefaultConfig.Candy.VOID_FOG_ENCROACH;
        static { CandyTweak.VOID_FOG_ENCROACH.setKey("voidFogEncroach"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakData.BoundedSlider(min = -64, max = 320, reset = DefaultConfig.Candy.VOID_FOG_START)
        @TweakGui.Slider(type = TweakGui.SliderType.GENERIC, langKey = LangUtil.Gui.SLIDER_Y_LEVEL)
        @TweakGui.Placement(pos = TweakGui.Position.BOTTOM, order = 1)
        @TweakGui.Embed(container = TweakEmbed.VOID_FOG_CANDY)
        public int voidFogStart = DefaultConfig.Candy.VOID_FOG_START;
        static { CandyTweak.VOID_FOG_START.setKey("voidFogStart"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakData.BoundedSlider(min = -64, max = 320, reset = DefaultConfig.Candy.VOID_PARTICLE_START)
        @TweakGui.Slider(type = TweakGui.SliderType.GENERIC, langKey = LangUtil.Gui.SLIDER_Y_LEVEL)
        @TweakGui.Placement(pos = TweakGui.Position.BOTTOM, order = 2)
        @TweakGui.Embed(container = TweakEmbed.VOID_FOG_CANDY)
        public int voidParticleStart = DefaultConfig.Candy.VOID_PARTICLE_START;
        static { CandyTweak.VOID_PARTICLE_START.setKey("voidParticleStart"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakData.BoundedSlider(min = 0, max = 32, reset = DefaultConfig.Candy.VOID_PARTICLE_RADIUS)
        @TweakGui.Slider(type = TweakGui.SliderType.GENERIC, langKey = LangUtil.Gui.SLIDER_RADIUS)
        @TweakGui.Placement(pos = TweakGui.Position.BOTTOM, order = 3)
        @TweakGui.Embed(container = TweakEmbed.VOID_FOG_CANDY)
        public int voidParticleRadius = DefaultConfig.Candy.VOID_PARTICLE_RADIUS;
        static { CandyTweak.VOID_PARTICLE_RADIUS.setKey("voidParticleRadius"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakData.BoundedSlider(min = 0, max = 100, reset = DefaultConfig.Candy.VOID_PARTICLE_DENSITY)
        @TweakGui.Slider(type = TweakGui.SliderType.GENERIC, suffix = "%", langKey = LangUtil.Gui.SLIDER_DENSITY)
        @TweakGui.Placement(pos = TweakGui.Position.BOTTOM, order = 4)
        @TweakGui.Embed(container = TweakEmbed.VOID_FOG_CANDY)
        public int voidParticleDensity = DefaultConfig.Candy.VOID_PARTICLE_DENSITY;
        static { CandyTweak.VOID_PARTICLE_DENSITY.setKey("voidParticleDensity"); }
    }

    @TweakData.Ignore
    public Gameplay gameplay = new Gameplay();
    public static class Gameplay
    {
        /**
         * Bugs
         */

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.BUG_GAMEPLAY)
        public boolean oldLadderGap = DefaultConfig.Gameplay.OLD_LADDER_GAP;
        static { GameplayTweak.LADDER_GAP.setKey("oldLadderGap"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.BUG_GAMEPLAY)
        public boolean oldSquidMilking = DefaultConfig.Gameplay.OLD_SQUID_MILKING;
        static { GameplayTweak.SQUID_MILK.setKey("oldSquidMilking"); }

        /**
         * Mob System
         */

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakData.BoundedSlider(min = 0, max = 100, reset = DefaultConfig.Gameplay.MONSTER_SPAWN_CAP)
        @TweakGui.Category(container = TweakCategory.MOB_GAMEPLAY)
        @TweakGui.Slider(type = TweakGui.SliderType.GENERIC, langKey = LangUtil.Gui.SLIDER_CAP)
        public int monsterSpawnCap = DefaultConfig.Gameplay.MONSTER_SPAWN_CAP;
        static { GameplayTweak.MONSTER_CAP.setKey("monsterSpawnCap"); }

        // Artificial Intelligence

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.MOB_AI_GAMEPLAY)
        public boolean disableAnimalPanic = DefaultConfig.Gameplay.DISABLE_ANIMAL_PANIC;
        static { GameplayTweak.ANIMAL_PANIC.setKey("disableAnimalPanic"); }

        // Animal - Spawn

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakData.BoundedSlider(min = 0, max = 100, reset = DefaultConfig.Gameplay.ANIMAL_SPAWN_CAP)
        @TweakGui.Embed(container = TweakEmbed.ANIMAL_MOB_SPAWN)
        @TweakGui.Slider(type = TweakGui.SliderType.GENERIC, langKey = LangUtil.Gui.SLIDER_CAP)
        public int animalSpawnCap = DefaultConfig.Gameplay.ANIMAL_SPAWN_CAP;
        static { GameplayTweak.ANIMAL_CAP.setKey("animalSpawnCap"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.ANIMAL_MOB_SPAWN)
        public boolean oldAnimalSpawning = DefaultConfig.Gameplay.OLD_ANIMAL_SPAWNING;
        static { GameplayTweak.ANIMAL_SPAWNING.setKey("oldAnimalSpawning"); }

        // Animal - Sheep

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.ANIMAL_MOB_SHEEP)
        public boolean disableSheepEatGrass = DefaultConfig.Gameplay.DISABLE_SHEEP_EAT_GRASS;
        static { GameplayTweak.SHEEP_EAT_GRASS.setKey("disableSheepEatGrass"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.ANIMAL_MOB_SHEEP)
        public boolean oldSheepPunching = DefaultConfig.Gameplay.OLD_SHEEP_PUNCHING;
        static { GameplayTweak.SHEEP_PUNCHING.setKey("oldSheepPunching"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.ANIMAL_MOB_SHEEP)
        public boolean oneWoolPunch = DefaultConfig.Gameplay.ONE_WOOL_PUNCH;
        static { GameplayTweak.ONE_WOOL_PUNCH.setKey("oneWoolPunch"); }

        /* Mob Drops */

        // Classic Mob Drops

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.CLASSIC_MOB_DROPS)
        public boolean oldZombiePigmenDrops = DefaultConfig.Gameplay.OLD_ZOMBIE_PIGMEN_DROPS;
        static { GameplayTweak.ZOMBIE_PIGMEN_DROPS.setKey("oldZombiePigmenDrops"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.CLASSIC_MOB_DROPS)
        public boolean oldSkeletonDrops = DefaultConfig.Gameplay.OLD_SKELETON_DROPS;
        static { GameplayTweak.SKELETON_DROPS.setKey("oldSkeletonDrops"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.CLASSIC_MOB_DROPS)
        public boolean oldChickenDrops = DefaultConfig.Gameplay.OLD_CHICKEN_DROPS;
        static { GameplayTweak.CHICKEN_DROPS.setKey("oldChickenDrops"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.CLASSIC_MOB_DROPS)
        public boolean oldZombieDrops = DefaultConfig.Gameplay.OLD_ZOMBIE_DROPS;
        static { GameplayTweak.ZOMBIE_DROPS.setKey("oldZombieDrops"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.CLASSIC_MOB_DROPS)
        public boolean oldSpiderDrops = DefaultConfig.Gameplay.OLD_SPIDER_DROPS;
        static { GameplayTweak.SPIDER_DROPS.setKey("oldSpiderDrops"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.CLASSIC_MOB_DROPS)
        public boolean oldSheepDrops = DefaultConfig.Gameplay.OLD_SHEEP_DROPS;
        static { GameplayTweak.SHEEP_DROPS.setKey("oldSheepDrops"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.CLASSIC_MOB_DROPS)
        public boolean oldCowDrops = DefaultConfig.Gameplay.OLD_COW_DROPS;
        static { GameplayTweak.COW_DROPS.setKey("oldCowDrops"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.CLASSIC_MOB_DROPS)
        public boolean oldPigDrops = DefaultConfig.Gameplay.OLD_PIG_DROPS;
        static { GameplayTweak.PIG_DROPS.setKey("oldPigDrops"); }

        // Modern Mob Drops

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.MODERN_MOB_DROPS)
        public boolean oldStyleZombieVillagerDrops = DefaultConfig.Gameplay.OLD_STYLE_ZOMBIE_VILLAGER_DROPS;
        static { GameplayTweak.ZOMBIE_VILLAGER_DROPS.setKey("oldStyleZombieVillagerDrops"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.MODERN_MOB_DROPS)
        public boolean oldStyleCaveSpiderDrops = DefaultConfig.Gameplay.OLD_STYLE_CAVE_SPIDER_DROPS;
        static { GameplayTweak.CAVE_SPIDER_DROPS.setKey("oldStyleCaveSpiderDrops"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.MODERN_MOB_DROPS)
        public boolean oldStyleMooshroomDrops = DefaultConfig.Gameplay.OLD_STYLE_MOOSHROOM_DROPS;
        static { GameplayTweak.MOOSHROOM_DROPS.setKey("oldStyleMooshroomDrops"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.MODERN_MOB_DROPS)
        public boolean oldStyleDrownedDrops = DefaultConfig.Gameplay.OLD_STYLE_DROWNED_DROPS;
        static { GameplayTweak.DROWNED_DROPS.setKey("oldStyleDrownedDrops"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.MODERN_MOB_DROPS)
        public boolean oldStyleRabbitDrops = DefaultConfig.Gameplay.OLD_STYLE_RABBIT_DROPS;
        static { GameplayTweak.RABBIT_DROPS.setKey("oldStyleRabbitDrops"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.MODERN_MOB_DROPS)
        public boolean oldStyleStrayDrops = DefaultConfig.Gameplay.OLD_STYLE_STRAY_DROPS;
        static { GameplayTweak.STRAY_DROPS.setKey("oldStyleStrayDrops"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.MODERN_MOB_DROPS)
        public boolean oldStyleHuskDrops = DefaultConfig.Gameplay.OLD_STYLE_HUSK_DROPS;
        static { GameplayTweak.HUSK_DROPS.setKey("oldStyleHuskDrops"); }

        /**
         * Combat System
         */

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.COMBAT_GAMEPLAY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        public boolean oldDamageValues = DefaultConfig.Gameplay.OLD_DAMAGE_VALUES;
        static { GameplayTweak.DAMAGE_VALUES.setKey("oldDamageValues"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.COMBAT_GAMEPLAY)
        public boolean disableCooldown = DefaultConfig.Gameplay.DISABLE_COOLDOWN;
        static { GameplayTweak.DISABLE_COOLDOWN.setKey("disableCooldown"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.COMBAT_GAMEPLAY)
        public boolean disableMissTimer = DefaultConfig.Gameplay.DISABLE_MISS_TIMER;
        static { GameplayTweak.DISABLE_MISS_TIMER.setKey("disableMissTimer"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.COMBAT_GAMEPLAY)
        public boolean disableCriticalHit = DefaultConfig.Gameplay.DISABLE_CRITICAL_HIT;
        static { GameplayTweak.DISABLE_CRITICAL_HIT.setKey("disableCriticalHit"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.COMBAT_GAMEPLAY)
        public boolean disableSweep = DefaultConfig.Gameplay.DISABLE_SWEEP;
        static { GameplayTweak.DISABLE_SWEEP.setKey("disableSweep"); }

        // Combat - Bow

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakData.BoundedSlider(min = 0, max = 100, reset = DefaultConfig.Gameplay.ARROW_SPEED)
        @TweakGui.Subcategory(container = TweakSubcategory.COMBAT_BOW_GAMEPLAY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        @TweakGui.Slider(type = TweakGui.SliderType.INTENSITY)
        public int arrowSpeed = DefaultConfig.Gameplay.ARROW_SPEED;
        static { GameplayTweak.ARROW_SPEED.setKey("arrowSpeed"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.COMBAT_BOW_GAMEPLAY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        public boolean instantBow = DefaultConfig.Gameplay.INSTANT_BOW;
        static { GameplayTweak.INSTANT_BOW.setKey("instantBow"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.COMBAT_BOW_GAMEPLAY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 3)
        public boolean invincibleBow = DefaultConfig.Gameplay.INVINCIBLE_BOW;
        static { GameplayTweak.INVINCIBLE_BOW.setKey("invincibleBow"); }

        /**
         * Experience System
         */

        /* Experience - Bar */

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.EXPERIENCE_BAR_GAMEPLAY)
        public boolean disableExperienceBar = DefaultConfig.Gameplay.DISABLE_EXPERIENCE_BAR;
        static { GameplayTweak.DISABLE_EXP_BAR.setKey("disableExperienceBar"); }

        // Alternative Level Text

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.ALT_XP_LEVEL_GAMEPLAY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        public boolean showXpLevelText = DefaultConfig.Gameplay.SHOW_XP_LEVEL_TEXT;
        static { GameplayTweak.SHOW_XP_LEVEL.setKey("showXpLevelText"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.ALT_XP_LEVEL_GAMEPLAY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        public boolean showXpLevelInCreative = DefaultConfig.Gameplay.SHOW_XP_LEVEL_TEXT_CREATIVE;
        static { GameplayTweak.SHOW_XP_LEVEL_CREATIVE.setKey("showXpLevelInCreative"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Embed(container = TweakEmbed.ALT_XP_LEVEL_GAMEPLAY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 3)
        public TweakType.Corner altXpLevelCorner = DefaultConfig.Gameplay.XP_LEVEL_CORNER;
        static { GameplayTweak.XP_LEVEL_CORNER.setKey("altXpLevelCorner"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Embed(container = TweakEmbed.ALT_XP_LEVEL_GAMEPLAY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 4)
        public String altXpLevelText = DefaultConfig.Gameplay.XP_LEVEL_TEXT;
        static { GameplayTweak.XP_LEVEL_TEXT.setKey("altXpLevelText"); }

        // Alternative Progress Text

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.ALT_XP_PROGRESS_GAMEPLAY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        public boolean showXpProgressText = DefaultConfig.Gameplay.SHOW_XP_PROGRESS_TEXT;
        static { GameplayTweak.SHOW_XP_PROGRESS.setKey("showXpProgressText"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.ALT_XP_PROGRESS_GAMEPLAY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        public boolean showXpProgressInCreative = DefaultConfig.Gameplay.SHOW_XP_PROGRESS_CREATIVE;
        static { GameplayTweak.SHOW_XP_PROGRESS_CREATIVE.setKey("showXpProgressInCreative"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Embed(container = TweakEmbed.ALT_XP_PROGRESS_GAMEPLAY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 3)
        public boolean useDynamicProgressColor = DefaultConfig.Gameplay.USE_DYNAMIC_PROGRESS_COLOR;
        static { GameplayTweak.USE_DYNAMIC_PROGRESS_COLOR.setKey("useDynamicProgressColor"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Embed(container = TweakEmbed.ALT_XP_PROGRESS_GAMEPLAY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 4)
        public TweakType.Corner altXpProgressCorner = DefaultConfig.Gameplay.XP_PROGRESS_CORNER;
        static { GameplayTweak.XP_PROGRESS_CORNER.setKey("altXpProgressCorner"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Embed(container = TweakEmbed.ALT_XP_PROGRESS_GAMEPLAY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 5)
        public String altXpProgressText = DefaultConfig.Gameplay.XP_PROGRESS_TEXT;
        static { GameplayTweak.XP_PROGRESS_TEXT.setKey("altXpProgressText"); }

        // Experience - Orb

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.EXPERIENCE_ORB_GAMEPLAY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        public boolean disableOrbSpawn = DefaultConfig.Gameplay.DISABLE_ORB_SPAWN;
        static { GameplayTweak.ORB_SPAWN.setKey("disableOrbSpawn"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.EXPERIENCE_ORB_GAMEPLAY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        public boolean disableOrbRendering = DefaultConfig.Gameplay.DISABLE_ORB_RENDERING;
        static { GameplayTweak.ORB_RENDERING.setKey("disableOrbRendering"); }

        // Experience - Blocks

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.EXPERIENCE_BLOCK_GAMEPLAY)
        public boolean disableAnvil = DefaultConfig.Gameplay.DISABLE_ANVIL;
        static { GameplayTweak.ANVIL.setKey("disableAnvil"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.EXPERIENCE_BLOCK_GAMEPLAY)
        public boolean disableEnchantTable = DefaultConfig.Gameplay.DISABLE_ENCHANT_TABLE;
        static { GameplayTweak.ENCHANT_TABLE.setKey("disableEnchantTable"); }

        /**
         * Game Mechanics
         */

        // Mechanics - Player

        @TweakGui.New
        @TweakData.Dynamic
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.MECHANICS_PLAYER_GAMEPLAY)
        public boolean disableSprint = DefaultConfig.Gameplay.DISABLE_SPRINT;
        static { GameplayTweak.SPRINT.setKey("disableSprint"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.MECHANICS_PLAYER_GAMEPLAY)
        public boolean leftClickDoor = DefaultConfig.Gameplay.LEFT_CLICK_DOOR;
        static { GameplayTweak.LEFT_CLICK_DOOR.setKey("leftClickDoor"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.MECHANICS_PLAYER_GAMEPLAY)
        public boolean leftClickLever = DefaultConfig.Gameplay.LEFT_CLICK_LEVER;
        static { GameplayTweak.LEFT_CLICK_LEVER.setKey("leftClickLever"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.MECHANICS_PLAYER_GAMEPLAY)
        public boolean leftClickButton = DefaultConfig.Gameplay.LEFT_CLICK_BUTTON;
        static { GameplayTweak.LEFT_CLICK_BUTTON.setKey("leftClickButton"); }

        // Mechanics - Farming

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.MECHANICS_FARMING_GAMEPLAY)
        public boolean instantBonemeal = DefaultConfig.Gameplay.INSTANT_BONE_MEAL;
        static { GameplayTweak.INSTANT_BONE_MEAL.setKey("instantBonemeal"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.MECHANICS_FARMING_GAMEPLAY)
        public boolean tilledGrassSeeds = DefaultConfig.Gameplay.TILLED_GRASS_SEEDS;
        static { GameplayTweak.TILLED_GRASS_SEEDS.setKey("tilledGrassSeeds"); }

        // Mechanics - Fire

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Warning
        @TweakGui.Subcategory(container = TweakSubcategory.MECHANICS_FIRE_GAMEPLAY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        public boolean oldFire = DefaultConfig.Gameplay.OLD_FIRE;
        static { GameplayTweak.FIRE_SPREAD.setKey("oldFire"); }

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.MECHANICS_FIRE_GAMEPLAY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        public boolean infiniteBurn = DefaultConfig.Gameplay.INFINITE_BURN;
        static { GameplayTweak.INFINITE_BURN.setKey("infiniteBurn"); }

        // Mechanics - Swimming

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.MECHANICS_SWIMMING_GAMEPLAY)
        public boolean instantAir = DefaultConfig.Gameplay.INSTANT_AIR;
        static { GameplayTweak.INSTANT_AIR.setKey("instantAir"); }

        @TweakGui.New
        @TweakData.Dynamic
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.MECHANICS_SWIMMING_GAMEPLAY)
        public boolean disableSwim = DefaultConfig.Gameplay.DISABLE_SWIM;
        static { GameplayTweak.SWIM.setKey("disableSwim"); }

        // Mechanics - Cart

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.MECHANICS_CART_GAMEPLAY)
        public boolean cartBoosting = DefaultConfig.Gameplay.CART_BOOSTING;
        static { GameplayTweak.CART_BOOSTING.setKey("cartBoosting"); }

        // Mechanics - Block

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.MECHANICS_BLOCK_GAMEPLAY)
        public boolean disableBedBounce = DefaultConfig.Gameplay.DISABLE_BED_BOUNCE;
        static { GameplayTweak.BED_BOUNCE.setKey("disableBedBounce"); }

        /**
         * Hunger System
         */

        /* Hunger - Bar */

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.HUNGER_BAR_GAMEPLAY)
        public boolean disableHungerBar = DefaultConfig.Gameplay.DISABLE_HUNGER_BAR;
        static { GameplayTweak.DISABLE_HUNGER_BAR.setKey("disableHungerBar"); }

        // Alternative Food Text

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.ALT_HUNGER_FOOD_GAMEPLAY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        public boolean showHungerFoodText = DefaultConfig.Gameplay.SHOW_HUNGER_FOOD_TEXT;
        static { GameplayTweak.SHOW_HUNGER_FOOD.setKey("showHungerFoodText"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Embed(container = TweakEmbed.ALT_HUNGER_FOOD_GAMEPLAY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        public boolean useDynamicFoodColor = DefaultConfig.Gameplay.USE_DYNAMIC_FOOD_COLOR;
        static { GameplayTweak.USE_DYNAMIC_FOOD_COLOR.setKey("useDynamicFoodColor"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Embed(container = TweakEmbed.ALT_HUNGER_FOOD_GAMEPLAY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 3)
        public TweakType.Corner altHungerFoodCorner = DefaultConfig.Gameplay.HUNGER_FOOD_CORNER;
        static { GameplayTweak.HUNGER_FOOD_CORNER.setKey("altHungerFoodCorner"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Embed(container = TweakEmbed.ALT_HUNGER_FOOD_GAMEPLAY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 4)
        public String altHungerFoodText = DefaultConfig.Gameplay.HUNGER_FOOD_TEXT;
        static { GameplayTweak.HUNGER_FOOD_TEXT.setKey("altHungerFoodText"); }

        // Alternative Saturation Text

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Embed(container = TweakEmbed.ALT_HUNGER_SATURATION_GAMEPLAY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        public boolean showHungerSaturationText = DefaultConfig.Gameplay.SHOW_HUNGER_SATURATION_TEXT;
        static { GameplayTweak.SHOW_HUNGER_SATURATION.setKey("showHungerSaturationText"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Embed(container = TweakEmbed.ALT_HUNGER_SATURATION_GAMEPLAY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        public boolean useDynamicSaturationColor = DefaultConfig.Gameplay.USE_DYNAMIC_SATURATION_COLOR;
        static { GameplayTweak.USE_DYNAMIC_SATURATION_COLOR.setKey("useDynamicSaturationColor"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Embed(container = TweakEmbed.ALT_HUNGER_SATURATION_GAMEPLAY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 3)
        public TweakType.Corner altHungerSaturationCorner = DefaultConfig.Gameplay.HUNGER_SATURATION_CORNER;
        static { GameplayTweak.HUNGER_SATURATION_CORNER.setKey("altHungerSaturationCorner"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Embed(container = TweakEmbed.ALT_HUNGER_SATURATION_GAMEPLAY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 4)
        public String altHungerSaturationText = DefaultConfig.Gameplay.HUNGER_SATURATION_TEXT;
        static { GameplayTweak.HUNGER_SATURATION_TEXT.setKey("altHungerSaturationText"); }

        // Hunger - Food

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.HUNGER_FOOD_GAMEPLAY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        public boolean instantEat = DefaultConfig.Gameplay.INSTANT_EAT;
        static { GameplayTweak.INSTANT_EAT.setKey("instantEat"); }

        // Hunger - Nutrition

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.HUNGER_FOOD_GAMEPLAY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        public boolean disableHunger = DefaultConfig.Gameplay.DISABLE_HUNGER;
        static { GameplayTweak.HUNGER.setKey("disableHunger"); }

        @TweakData.Ignore private static final int HEALTH_MIN = DefaultConfig.Gameplay.HEALTH_MIN;
        @TweakData.Ignore private static final int HEALTH_MAX = DefaultConfig.Gameplay.HEALTH_MAX;
        @TweakData.Ignore private static final int HEALTH_RESET = DefaultConfig.Gameplay.HEALTH_RESET;

        @TweakGui.New
        @TweakData.Server
        @TweakData.List(id = ListId.CUSTOM_FOOD_HEALTH)
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakData.BoundedSlider(min = HEALTH_MIN, max = HEALTH_MAX, reset = HEALTH_RESET)
        @TweakGui.Alert(condition = TweakGui.Condition.CUSTOM_FOOD_HEALTH_CONFLICT, langKey = LangUtil.Gui.ALERT_FOOD_HEALTH)
        @TweakGui.Subcategory(container = TweakSubcategory.HUNGER_FOOD_GAMEPLAY)
        @TweakGui.Slider(type = TweakGui.SliderType.HEARTS)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 4)
        public Map<String, Integer> customFoodHealth = new HashMap<>();
        static { GameplayTweak.CUSTOM_FOOD_HEALTH.setKey("customFoodHealth"); }

        @TweakData.Ignore
        public Set<String> disabledFoodHealth = new HashSet<>();

        // Hunger - Stacking

        @TweakGui.New
        @TweakData.Server
        @TweakData.EntryStatus
        @TweakGui.Subcategory(container = TweakSubcategory.HUNGER_FOOD_GAMEPLAY)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 3)
        public boolean oldFoodStacking = DefaultConfig.Gameplay.OLD_FOOD_STACKING;
        static { GameplayTweak.FOOD_STACKING.setKey("oldFoodStacking"); }

        @TweakData.Ignore private static final int ITEM_MIN = DefaultConfig.Gameplay.ITEM_STACK_MIN;
        @TweakData.Ignore private static final int ITEM_MAX = DefaultConfig.Gameplay.ITEM_STACK_MAX;
        @TweakData.Ignore private static final int FOOD_RESET = DefaultConfig.Gameplay.FOOD_STACK_RESET;

        @TweakGui.New
        @TweakData.Server
        @TweakData.List(id = ListId.CUSTOM_FOOD_STACKING)
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakData.BoundedSlider(min = ITEM_MIN, max = ITEM_MAX, reset = FOOD_RESET)
        @TweakGui.Alert(condition = TweakGui.Condition.CUSTOM_FOOD_STACKING_CONFLICT, langKey = LangUtil.Gui.ALERT_FOOD_STACKING)
        @TweakGui.Subcategory(container = TweakSubcategory.HUNGER_FOOD_GAMEPLAY)
        @TweakGui.Slider(type = TweakGui.SliderType.GENERIC, langKey = LangUtil.Gui.SLIDER_STACK)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 5)
        public Map<String, Integer> customFoodStacking = new HashMap<>();
        static { GameplayTweak.CUSTOM_FOOD_STACKING.setKey("customFoodStacking"); }

        @TweakData.Ignore
        public Set<String> disabledFoodStacking = new HashSet<>();

        // Custom Item Stacking

        @TweakData.Ignore private static final int ITEM_RESET = DefaultConfig.Gameplay.ITEM_STACK_RESET;

        @TweakGui.New
        @TweakData.Server
        @TweakData.List(id = ListId.CUSTOM_ITEM_STACKING)
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakData.BoundedSlider(min = ITEM_MIN, max = ITEM_MAX, reset = ITEM_RESET)
        @TweakGui.Slider(type = TweakGui.SliderType.GENERIC, langKey = LangUtil.Gui.SLIDER_STACK)
        @TweakGui.Category(container = TweakCategory.MECHANICS_GAMEPLAY)
        public Map<String, Integer> customItemStacking = new HashMap<>();
        static { GameplayTweak.CUSTOM_ITEM_STACKING.setKey("customItemStacking"); }
    }

    @TweakData.Ignore
    public Animation animation = new Animation();
    public static class Animation
    {
        /**
         * Arm Animations
         */

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.ARM_ANIMATION)
        public boolean oldArmSway = DefaultConfig.Animation.OLD_ARM_SWAY;
        static { AnimationTweak.ARM_SWAY.setKey("oldArmSway"); }

        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.Category(container = TweakCategory.ARM_ANIMATION)
        public boolean armSwayMirror = DefaultConfig.Animation.ARM_SWAY_MIRROR;
        static { AnimationTweak.ARM_SWAY_MIRROR.setKey("armSwayMirror"); }

        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakData.BoundedSlider(min = 0, max = 300, reset = DefaultConfig.Animation.ARM_SWAY_INTENSITY)
        @TweakGui.Category(container = TweakCategory.ARM_ANIMATION)
        @TweakGui.Slider(type = TweakGui.SliderType.INTENSITY)
        public int armSwayIntensity = DefaultConfig.Animation.ARM_SWAY_INTENSITY;
        static { AnimationTweak.ARM_SWAY_INTENSITY.setKey("armSwayIntensity"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.ARM_ANIMATION)
        public boolean oldSwing = DefaultConfig.Animation.OLD_SWING;
        static { AnimationTweak.ITEM_SWING.setKey("oldSwing"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.ARM_ANIMATION)
        public boolean oldSwingInterrupt = DefaultConfig.Animation.OLD_SWING_INTERRUPT;
        static { AnimationTweak.SWING_INTERRUPT.setKey("oldSwingInterrupt"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.ARM_ANIMATION)
        public boolean oldSwingDropping = DefaultConfig.Animation.OLD_SWING_DROPPING;
        static { AnimationTweak.SWING_DROP.setKey("oldSwingDropping"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.ARM_ANIMATION)
        public boolean oldClassicSwing = DefaultConfig.Animation.OLD_CLASSIC_SWING;
        static { AnimationTweak.CLASSIC_SWING.setKey("oldClassicSwing"); }

        /**
         * Item Animations
         */

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.ITEM_ANIMATION)
        public boolean oldItemCooldown = DefaultConfig.Animation.OLD_ITEM_COOLDOWN;
        static { AnimationTweak.COOLDOWN.setKey("oldItemCooldown"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.ITEM_ANIMATION)
        public boolean oldItemReequip = DefaultConfig.Animation.OLD_ITEM_REEQUIP;
        static { AnimationTweak.REEQUIP.setKey("oldItemReequip"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.ITEM_ANIMATION)
        public boolean oldToolExplosion = DefaultConfig.Animation.OLD_TOOL_EXPLOSION;
        static { AnimationTweak.TOOL_EXPLODE.setKey("oldToolExplosion"); }

        /**
         * Mob Animations
         */

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.MOB_ANIMATION)
        public boolean oldZombieArms = DefaultConfig.Animation.OLD_ZOMBIE_ARMS;
        static { AnimationTweak.ZOMBIE_ARMS.setKey("oldZombieArms"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.MOB_ANIMATION)
        public boolean oldSkeletonArms = DefaultConfig.Animation.OLD_SKELETON_ARMS;
        static { AnimationTweak.SKELETON_ARMS.setKey("oldSkeletonArms"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.MOB_ANIMATION)
        public boolean oldGhastCharging = DefaultConfig.Animation.OLD_GHAST_CHARGING;
        static { AnimationTweak.GHAST_CHARGING.setKey("oldGhastCharging"); }

        /**
         * Player Animations
         */

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.PLAYER_ANIMATION)
        public boolean oldBackwardWalking = DefaultConfig.Animation.OLD_BACKWARD_WALKING;
        static { AnimationTweak.BACKWARD_WALK.setKey("oldBackwardWalking"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.PLAYER_ANIMATION)
        public boolean oldCollideBobbing = DefaultConfig.Animation.OLD_COLLIDE_BOBBING;
        static { AnimationTweak.COLLIDE_BOB.setKey("oldCollideBobbing"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.PLAYER_ANIMATION)
        public boolean oldVerticalBobbing = DefaultConfig.Animation.OLD_VERTICAL_BOBBING;
        static { AnimationTweak.BOB_VERTICAL.setKey("oldVerticalBobbing"); }

        @TweakGui.New
        @TweakData.Dynamic
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.PLAYER_ANIMATION)
        public boolean oldCreativeCrouch = DefaultConfig.Animation.OLD_CREATIVE_CROUCH;
        static { AnimationTweak.CREATIVE_CROUCH.setKey("oldCreativeCrouch"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.PLAYER_ANIMATION)
        public boolean oldRandomDamage = DefaultConfig.Animation.OLD_RANDOM_DAMAGE;
        static { AnimationTweak.RANDOM_DAMAGE.setKey("oldRandomDamage"); }

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.PLAYER_ANIMATION)
        public boolean oldSneaking = DefaultConfig.Animation.OLD_SNEAKING;
        static { AnimationTweak.SNEAK_SMOOTH.setKey("oldSneaking"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.Category(container = TweakCategory.PLAYER_ANIMATION)
        public boolean disableDeathTopple = DefaultConfig.Animation.DISABLE_DEATH_TOPPLE;
        static { AnimationTweak.DEATH_TOPPLE.setKey("disableDeathTopple"); }
    }

    @TweakData.Ignore
    public Swing swing = new Swing();
    public static class Swing
    {
        /* Swing Speed Constants */

        @TweakData.Ignore private static final int MIN = DefaultConfig.Swing.MIN_SPEED;
        @TweakData.Ignore private static final int MAX = DefaultConfig.Swing.MAX_SPEED;
        @TweakData.Ignore private static final int GLOBAL = DefaultConfig.Swing.GLOBAL;

        /* Swing Speed Tweaks */

        @TweakData.Client
        @TweakData.EntryStatus
        @TweakGui.DisabledBoolean(value = true)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        public boolean overrideSpeeds = DefaultConfig.Swing.OVERRIDE_SPEEDS;
        static { SwingTweak.OVERRIDE_SPEEDS.setKey("overrideSpeeds"); }

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakGui.DisabledBoolean(value = true)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        public boolean leftClickSpeedOnBlockInteract = DefaultConfig.Swing.LEFT_SPEED_ON_RIGHT_INTERACT;

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakData.BoundedSlider(min = GLOBAL, max = MAX, reset = GLOBAL)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 3)
        public int leftGlobalSpeed = DefaultConfig.Swing.GLOBAL;

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakData.BoundedSlider(min = GLOBAL, max = MAX, reset = GLOBAL)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 4)
        public int rightGlobalSpeed = DefaultConfig.Swing.GLOBAL;

        /* Item Swing Speeds */

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakData.BoundedSlider(min = MIN, max = MAX, reset = DefaultConfig.Swing.ITEM)
        @TweakGui.Category(container = TweakCategory.ITEM_SWING)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        public int leftItemSpeed = DefaultConfig.Swing.ITEM;

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakData.BoundedSlider(min = MIN, max = MAX, reset = DefaultConfig.Swing.ITEM)
        @TweakGui.Category(container = TweakCategory.ITEM_SWING)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        public int rightItemSpeed = DefaultConfig.Swing.ITEM;

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakData.BoundedSlider(min = MIN, max = MAX, reset = DefaultConfig.Swing.TOOL)
        @TweakGui.Category(container = TweakCategory.ITEM_SWING)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 3)
        public int leftToolSpeed = DefaultConfig.Swing.TOOL;

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakData.BoundedSlider(min = MIN, max = MAX, reset = DefaultConfig.Swing.TOOL)
        @TweakGui.Category(container = TweakCategory.ITEM_SWING)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 4)
        public int rightToolSpeed = DefaultConfig.Swing.TOOL;

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakData.BoundedSlider(min = MIN, max = MAX, reset = DefaultConfig.Swing.BLOCK)
        @TweakGui.Category(container = TweakCategory.ITEM_SWING)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 5)
        public int leftBlockSpeed = DefaultConfig.Swing.BLOCK;

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakData.BoundedSlider(min = MIN, max = MAX, reset = DefaultConfig.Swing.BLOCK)
        @TweakGui.Category(container = TweakCategory.ITEM_SWING)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 6)
        public int rightBlockSpeed = DefaultConfig.Swing.BLOCK;

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakData.BoundedSlider(min = MIN, max = MAX, reset = DefaultConfig.Swing.SWORD)
        @TweakGui.Category(container = TweakCategory.ITEM_SWING)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 7)
        public int leftSwordSpeed = DefaultConfig.Swing.SWORD;

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakData.BoundedSlider(min = MIN, max = MAX, reset = DefaultConfig.Swing.SWORD)
        @TweakGui.Category(container = TweakCategory.ITEM_SWING)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 8)
        public int rightSwordSpeed = DefaultConfig.Swing.SWORD;

        /* Potion Swing Speeds */

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakData.BoundedSlider(min = GLOBAL, max = MAX, reset = GLOBAL)
        @TweakGui.Category(container = TweakCategory.POTION_SWING)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 1)
        public int leftHasteSpeed = DefaultConfig.Swing.HASTE;

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakData.BoundedSlider(min = GLOBAL, max = MAX, reset = GLOBAL)
        @TweakGui.Category(container = TweakCategory.POTION_SWING)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 2)
        public int rightHasteSpeed = DefaultConfig.Swing.HASTE;

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakData.BoundedSlider(min = GLOBAL, max = MAX, reset = GLOBAL)
        @TweakGui.Category(container = TweakCategory.POTION_SWING)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 3)
        public int leftFatigueSpeed = DefaultConfig.Swing.FATIGUE;

        @TweakGui.New
        @TweakData.Client
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        @TweakData.BoundedSlider(min = GLOBAL, max = MAX, reset = GLOBAL)
        @TweakGui.Category(container = TweakCategory.POTION_SWING)
        @TweakGui.Placement(pos = TweakGui.Position.TOP, order = 4)
        public int rightFatigueSpeed = DefaultConfig.Swing.FATIGUE;

        /* Custom Swing Speeds */

        @TweakGui.NotAutomated
        @TweakData.List(id = ListId.LEFT_CLICK_SPEEDS)
        public Map<String, Integer> leftClickSwingSpeeds = new HashMap<>();
        static { SwingTweak.LEFT_CLICK_SPEEDS.setKey("leftClickSwingSpeeds"); }

        @TweakGui.NotAutomated
        @TweakData.List(id = ListId.RIGHT_CLICK_SPEEDS)
        public Map<String, Integer> rightClickSwingSpeeds = new HashMap<>();
        static { SwingTweak.RIGHT_CLICK_SPEEDS.setKey("rightClickSwingSpeeds"); }
    }

    @TweakData.Ignore
    public Gui gui = new Gui();
    public static class Gui
    {
        /**
         * Control flag that determines if the user has successfully interacted with the tutorial toast.
         * If so, then the toast will not appear the next time a title screen is displayed.
         */
        @TweakData.Ignore
        public boolean interactedWithConfig = false;

        /**
         * Control flag that determines if the donator banner is displayed when the settings screen is opened.
         * This field is saved when the banner toggle button is clicked.
         */
        @TweakData.Ignore
        public boolean displayDonatorBanner = DefaultConfig.Gui.DISPLAY_DONATOR_BANNER;

        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        public MenuOption defaultScreen = DefaultConfig.Gui.DEFAULT_SCREEN;
        static { GuiTweak.DEFAULT_SCREEN.setKey("defaultScreen"); }

        @TweakGui.Slider(type = TweakGui.SliderType.GENERIC, langKey = LangUtil.Gui.GENERAL_MANAGEMENT_MAX_SLIDER)
        @TweakData.BoundedSlider(min = -1, max = 64, reset = DefaultConfig.Gui.NUMBER_OF_BACKUPS)
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        public int numberOfBackups = DefaultConfig.Gui.NUMBER_OF_BACKUPS;
        static { GuiTweak.MAXIMUM_BACKUPS.setKey("numberOfBackups"); }

        @SuppressWarnings("unused")
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        public boolean displayNewTags = DefaultConfig.Gui.DISPLAY_NEW_TAGS;
        static { GuiTweak.DISPLAY_NEW_TAGS.setKey("displayNewTags"); }

        @SuppressWarnings("unused")
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        public boolean displaySidedTags = DefaultConfig.Gui.DISPLAY_SIDED_TAGS;
        static { GuiTweak.DISPLAY_SIDED_TAGS.setKey("displaySidedTags"); }

        @SuppressWarnings("unused")
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        public boolean displayTagTooltips = DefaultConfig.Gui.DISPLAY_TAG_TOOLTIPS;
        static { GuiTweak.DISPLAY_TAG_TOOLTIPS.setKey("displayTagTooltips"); }

        @SuppressWarnings("unused")
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        public boolean displayFeatureStatus = DefaultConfig.Gui.DISPLAY_FEATURE_STATUS;
        static { GuiTweak.DISPLAY_FEATURE_STATUS.setKey("displayFeatureStatus"); }

        @SuppressWarnings("unused")
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        public boolean displayCategoryTree = DefaultConfig.Gui.DISPLAY_CATEGORY_TREE;
        static { GuiTweak.DISPLAY_CATEGORY_TREE.setKey("displayCategoryTree"); }

        @SuppressWarnings("unused")
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        public String categoryTreeColor = DefaultConfig.Gui.CATEGORY_TREE_COLOR;
        static { GuiTweak.CATEGORY_TREE_COLOR.setKey("categoryTreeColor"); }

        @SuppressWarnings("unused")
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        public boolean displayRowHighlight = DefaultConfig.Gui.DISPLAY_ROW_HIGHLIGHT;
        static { GuiTweak.DISPLAY_ROW_HIGHLIGHT.setKey("displayRowHighlight"); }

        @SuppressWarnings("unused")
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        public boolean doRowHighlightFade = DefaultConfig.Gui.DO_ROW_HIGHLIGHT_FADE;
        static { GuiTweak.ROW_HIGHLIGHT_FADE.setKey("doRowHighlightFade"); }

        @SuppressWarnings("unused")
        @TweakData.EntryStatus(status = TweakStatus.LOADED)
        public String rowHighlightColor = DefaultConfig.Gui.ROW_HIGHLIGHT_COLOR;
        static { GuiTweak.ROW_HIGHLIGHT_COLOR.setKey("rowHighlightColor"); }
    }
}