package mod.adrenix.nostalgic.client.config;

import com.google.common.collect.Maps;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.annotation.TweakClient;
import mod.adrenix.nostalgic.client.config.gui.screen.MenuOption;
import mod.adrenix.nostalgic.common.config.DefaultConfig;
import mod.adrenix.nostalgic.common.config.annotation.TweakSide;
import mod.adrenix.nostalgic.common.config.reflect.StatusType;
import mod.adrenix.nostalgic.common.config.tweak.*;
import mod.adrenix.nostalgic.util.common.LangUtil;

import java.util.Map;

/**
 * The server controlled tweaks in this config need to stay in sync with the fields in the server config.
 * Any updates in this class or that class will require an update in both config classes.
 *
 * @see mod.adrenix.nostalgic.server.config.ServerConfig
 */

@Config(name = NostalgicTweaks.MOD_ID)
public class ClientConfig implements ConfigData
{
    @TweakSide.Ignore public static final int MIN = 0;
    @TweakSide.Ignore public static final int MAX = 16;
    @TweakSide.Ignore public static final String ROOT_KEY = "isModEnabled";

    @TweakSide.Client
    @TweakSide.EntryStatus(status = StatusType.LOADED)
    @TweakClient.Gui.NoTooltip
    @TweakClient.Run.ReloadResources
    public boolean isModEnabled = true;

    @TweakSide.Ignore
    public Sound sound = new Sound();
    public static class Sound
    {
        /**
         * Block Sounds
         */

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.BLOCK_CHEST_SOUND)
        public boolean oldChest = DefaultConfig.Sound.OLD_CHEST;
        static { SoundTweak.OLD_CHEST.setKey("oldChest"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.BLOCK_CHEST_SOUND)
        public boolean disableChest = DefaultConfig.Sound.DISABLE_CHEST;
        static { SoundTweak.DISABLE_CHEST.setKey("disableChest"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.BLOCK_SOUND)
        public boolean disableGrowth = DefaultConfig.Sound.DISABLE_GROWTH;
        static { SoundTweak.DISABLE_GROWTH.setKey("disableGrowth"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.BLOCK_SOUND)
        public boolean disableDoorPlace = DefaultConfig.Sound.DISABLE_DOOR_PLACE;
        static { SoundTweak.DISABLE_DOOR.setKey("disableDoorPlace"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.BLOCK_SOUND)
        public boolean disableBedPlace = DefaultConfig.Sound.DISABLE_BED_PLACE;
        static { SoundTweak.DISABLE_BED.setKey("disableBedPlace"); }

        /**
         * Damage Sounds
         */

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.DAMAGE_SOUND)
        public boolean oldAttack = DefaultConfig.Sound.OLD_ATTACK;
        static { SoundTweak.OLD_ATTACK.setKey("oldAttack"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.DAMAGE_SOUND)
        public boolean oldHurt = DefaultConfig.Sound.OLD_HURT;
        static { SoundTweak.OLD_HURT.setKey("oldHurt"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.DAMAGE_SOUND)
        public boolean oldFall = DefaultConfig.Sound.OLD_FALL;
        static { SoundTweak.OLD_FALL.setKey("oldFall"); }

        /**
         * Experience Sounds
         */

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.EXPERIENCE_SOUND)
        public boolean oldXp = DefaultConfig.Sound.OLD_XP;
        static { SoundTweak.OLD_XP.setKey("oldXp"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.EXPERIENCE_SOUND)
        public boolean disableXpPickup = DefaultConfig.Sound.DISABLE_XP_PICKUP;
        static { SoundTweak.DISABLE_PICKUP.setKey("disableXpPickup"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.EXPERIENCE_SOUND)
        public boolean disableXpLevel = DefaultConfig.Sound.DISABLE_XP_LEVEL;
        static { SoundTweak.DISABLE_LEVEL.setKey("disableXpLevel"); }

        /**
         * Mob Sounds
         */

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.MOB_SOUND)
        public boolean oldStep = DefaultConfig.Sound.OLD_STEP;
        static { SoundTweak.OLD_STEP.setKey("oldStep"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.MOB_SOUND)
        public boolean disableSquid = DefaultConfig.Sound.DISABLE_SQUID;
        static { SoundTweak.DISABLE_SQUID.setKey("disableSquid"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.BOTTOM, order = 1)
        @TweakClient.Gui.Cat(group = TweakClient.Category.MOB_SOUND)
        public boolean ignoreModdedStep = DefaultConfig.Sound.IGNORE_MODDED_STEP;
        static { SoundTweak.IGNORE_MODDED_STEP.setKey("ignoreModdedStep"); }
    }

    @TweakSide.Ignore
    public EyeCandy eyeCandy = new EyeCandy();
    public static class EyeCandy
    {
        /**
         * Block Candy
         */

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Run.ReloadChunks
        @TweakClient.Gui.Cat(group = TweakClient.Category.BLOCK_CANDY)
        public boolean fixAmbientOcclusion = DefaultConfig.Candy.FIX_AMBIENT_OCCLUSION;
        static { CandyTweak.FIX_AO.setKey("fixAmbientOcclusion"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Run.ReloadChunks
        @TweakClient.Gui.Cat(group = TweakClient.Category.BLOCK_CANDY)
        public boolean disableAllOffset = DefaultConfig.Candy.DISABLE_ALL_OFFSET;
        static { CandyTweak.DISABLE_ALL_OFFSET.setKey("disableAllOffset"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Run.ReloadChunks
        @TweakClient.Gui.Cat(group = TweakClient.Category.BLOCK_CANDY)
        public boolean disableFlowerOffset = DefaultConfig.Candy.DISABLE_FLOWER_OFFSET;
        static { CandyTweak.DISABLE_FLOWER_OFFSET.setKey("disableFlowerOffset"); }

        // Block - Chest Candy

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Run.ReloadResources
        @TweakClient.Gui.Optifine(incompatible = false)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.BLOCK_CHEST_CANDY)
        public boolean oldChest = DefaultConfig.Candy.OLD_CHEST;
        static { CandyTweak.CHEST.setKey("oldChest"); }

        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Run.ReloadChunks
        @TweakClient.Gui.Warning
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.BLOCK_CHEST_CANDY)
        public boolean oldChestVoxel = DefaultConfig.Candy.OLD_CHEST_VOXEL;
        static { CandyTweak.CHEST_VOXEL.setKey("oldChestVoxel"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Run.ReloadResources
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.BLOCK_CHEST_CANDY)
        public boolean oldEnderChest = DefaultConfig.Candy.OLD_ENDER_CHEST;
        static { CandyTweak.ENDER_CHEST.setKey("oldEnderChest"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Run.ReloadResources
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.BLOCK_CHEST_CANDY)
        public boolean oldTrappedChest = DefaultConfig.Candy.OLD_TRAPPED_CHEST;
        static { CandyTweak.TRAPPED_CHEST.setKey("oldTrappedChest"); }

        // Block - Torch Candy

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Run.ReloadChunks
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.BLOCK_TORCH_CANDY)
        public boolean oldTorchBrightness = DefaultConfig.Candy.OLD_TORCH_BRIGHTNESS;
        static { CandyTweak.TORCH_BRIGHTNESS.setKey("oldTorchBrightness"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Run.ReloadChunks
        @TweakClient.Gui.Sodium
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 2)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.BLOCK_TORCH_CANDY)
        public boolean oldTorchModel = DefaultConfig.Candy.OLD_TORCH_MODEL;
        static { CandyTweak.TORCH_MODEL.setKey("oldTorchModel"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Run.ReloadChunks
        @TweakClient.Gui.Sodium
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 3)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.BLOCK_TORCH_CANDY)
        public boolean oldRedstoneTorchModel = DefaultConfig.Candy.OLD_REDSTONE_TORCH_MODEL;
        static { CandyTweak.REDSTONE_TORCH_MODEL.setKey("oldRedstoneTorchModel"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Run.ReloadChunks
        @TweakClient.Gui.Sodium
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 4)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.BLOCK_TORCH_CANDY)
        public boolean oldSoulTorchModel = DefaultConfig.Candy.OLD_SOUL_TORCH_MODEL;
        static { CandyTweak.SOUL_TORCH_MODEL.setKey("oldSoulTorchModel"); }

        /**
         * Interface Candy
         */

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.FAIL)
        @TweakClient.Gui.Cat(group = TweakClient.Category.INTERFACE_CANDY)
        public boolean oldButtonHover = DefaultConfig.Candy.OLD_BUTTON_HOVER;
        static { CandyTweak.BUTTON_HOVER.setKey("oldButtonHover"); }

        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.INTERFACE_CANDY)
        public TweakVersion.Hotbar oldCreativeHotbar = DefaultConfig.Candy.OLD_CREATIVE_HOTBAR;
        static { CandyTweak.CREATIVE_HOTBAR.setKey("oldCreativeHotbar"); }

        // Interface - Debug Screen Candy

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_DEBUG_CANDY)
        public TweakVersion.Generic oldDebug = DefaultConfig.Candy.OLD_DEBUG;
        static { CandyTweak.DEBUG_SCREEN.setKey("oldDebug"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 2)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_DEBUG_CANDY)
        public TweakType.DebugChart fpsChart = DefaultConfig.Candy.FPS_CHART;
        static { CandyTweak.DEBUG_FPS_CHART.setKey("fpsChart"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 3)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_DEBUG_CANDY)
        public boolean displayTpsChart = DefaultConfig.Candy.DISPLAY_TPS_CHART;
        static { CandyTweak.DEBUG_TPS_CHART.setKey("displayTpsChart"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 4)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_DEBUG_CANDY)
        public boolean displayLightLevels = DefaultConfig.Candy.DISPLAY_LIGHT_LEVELS;
        static { CandyTweak.DEBUG_LIGHT.setKey("displayLightLevels"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 5)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_DEBUG_CANDY)
        public boolean displayPieChart = DefaultConfig.Candy.DISPLAY_PIE_CHART;
        static { CandyTweak.DEBUG_PIE_CHART.setKey("displayPieChart"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 6)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_DEBUG_CANDY)
        public boolean oldPieChartBackground = DefaultConfig.Candy.OLD_PIE_CHART_BACKGROUND;
        static { CandyTweak.OLD_PIE_BACKGROUND.setKey("oldPieChartBackground"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.BOTTOM, order = 1)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_DEBUG_CANDY)
        public boolean debugEntityId = DefaultConfig.Candy.DEBUG_ENTITY_ID;
        static { CandyTweak.DEBUG_ENTITY_ID.setKey("debugEntityId"); }

        // Interface - Inventory Screen Candy

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_INVENTORY_CANDY)
        public boolean oldInventory = DefaultConfig.Candy.OLD_INVENTORY;
        static { CandyTweak.OLD_INVENTORY.setKey("oldInventory"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 2)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_INVENTORY_CANDY)
        public TweakType.RecipeBook inventoryBook = DefaultConfig.Candy.INVENTORY_BOOK;
        static { CandyTweak.INVENTORY_BOOK.setKey("inventoryBook"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 3)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_INVENTORY_CANDY)
        public TweakType.InventoryShield inventoryShield = DefaultConfig.Candy.INVENTORY_SHIELD;
        static { CandyTweak.INVENTORY_SHIELD.setKey("inventoryShield"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 4)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_INVENTORY_CANDY)
        public boolean disableEmptyArmorTexture = DefaultConfig.Candy.DISABLE_EMPTY_ARMOR_TEXTURE;
        static { CandyTweak.DISABLE_EMPTY_ARMOR.setKey("disableEmptyArmorTexture"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 5)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_INVENTORY_CANDY)
        public boolean disableEmptyShieldTexture = DefaultConfig.Candy.DISABLE_EMPTY_SHIELD_TEXTURE;
        static { CandyTweak.DISABLE_EMPTY_SHIELD.setKey("disableEmptyShieldTexture"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.BOTTOM, order = 1)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_INVENTORY_CANDY)
        public boolean invertedBlockLighting = DefaultConfig.Candy.INVERTED_BLOCK_LIGHTING;
        static { CandyTweak.INVERTED_BLOCK_LIGHTING.setKey("invertedBlockLighting"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.BOTTOM, order = 2)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_INVENTORY_CANDY)
        public boolean invertedPlayerLighting = DefaultConfig.Candy.INVERTED_PLAYER_LIGHTING;
        static { CandyTweak.INVERTED_PLAYER_LIGHTING.setKey("invertedPlayerLighting"); }

        // Interface - Screen Candy

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_GUI_CANDY)
        public TweakType.GuiBackground oldGuiBackground = DefaultConfig.Candy.OLD_GUI_BACKGROUND;
        static { CandyTweak.OLD_GUI_BACKGROUND.setKey("oldGuiBackground"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 2)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_GUI_CANDY)
        public boolean customGuiBackground = DefaultConfig.Candy.CUSTOM_GUI_BACKGROUND;
        static { CandyTweak.CUSTOM_GUI_BACKGROUND.setKey("customGuiBackground"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.Color
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 3)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_GUI_CANDY)
        public String customTopGradient = DefaultConfig.Candy.CUSTOM_TOP_GRADIENT;
        static { CandyTweak.CUSTOM_TOP_GRADIENT.setKey("customTopGradient"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.Color
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 4)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_GUI_CANDY)
        public String customBottomGradient = DefaultConfig.Candy.CUSTOM_BOTTOM_GRADIENT;
        static { CandyTweak.CUSTOM_BOTTOM_GRADIENT.setKey("customBottomGradient"); }

        // Interface - Loading Screen

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_LOADING_CANDY)
        public TweakVersion.Overlay oldLoadingOverlay = DefaultConfig.Candy.OLD_LOADING_OVERLAY;
        static { CandyTweak.LOADING_OVERLAY.setKey("oldLoadingOverlay"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Optifine(incompatible = false)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 2)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_LOADING_CANDY)
        public boolean removeLoadingBar = DefaultConfig.Candy.REMOVE_LOADING_BAR;
        static { CandyTweak.REMOVE_LOADING_BAR.setKey("removeLoadingBar"); }

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.FAIL)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_LOADING_CANDY)
        public boolean oldLoadingScreens = DefaultConfig.Candy.OLD_LOADING_SCREENS;
        static { CandyTweak.LOADING_SCREENS.setKey("oldLoadingScreens"); }

        // Interface - Version Overlay

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_VERSION_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        public boolean oldVersionOverlay = DefaultConfig.Candy.OLD_VERSION_OVERLAY;
        static { CandyTweak.VERSION_OVERLAY.setKey("oldVersionOverlay"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_VERSION_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 2)
        public TweakType.Corner oldOverlayCorner = DefaultConfig.Candy.OLD_OVERLAY_CORNER;
        static { CandyTweak.VERSION_CORNER.setKey("oldOverlayCorner"); }

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_VERSION_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 3)
        public String oldOverlayText = DefaultConfig.Candy.OLD_OVERLAY_TEXT;
        static { CandyTweak.VERSION_TEXT.setKey("oldOverlayText"); }

        // Interface - Pause Screen

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_PAUSE_CANDY)
        public TweakVersion.PauseLayout oldPauseMenu = DefaultConfig.Candy.OLD_PAUSE_MENU;
        static { CandyTweak.PAUSE_LAYOUT.setKey("oldPauseMenu"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_PAUSE_CANDY)
        public boolean includeModsOnPause = DefaultConfig.Candy.INCLUDE_MODS_ON_PAUSE;
        static { CandyTweak.PAUSE_MODS.setKey("includeModsOnPause"); }

        // Interface - Crafting Screen

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_CRAFTING_CANDY)
        public boolean oldCraftingScreen = DefaultConfig.Candy.OLD_CRAFTING_SCREEN;
        static { CandyTweak.CRAFTING_SCREEN.setKey("oldCraftingScreen"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 2)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_CRAFTING_CANDY)
        public TweakType.RecipeBook craftingBook = DefaultConfig.Candy.CRAFTING_BOOK;
        static { CandyTweak.CRAFTING_RECIPE.setKey("craftingBook"); }

        // Interface - Furnace Screen

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_FURNACE_CANDY)
        public boolean oldFurnaceScreen = DefaultConfig.Candy.OLD_FURNACE_SCREEN;
        static { CandyTweak.FURNACE_SCREEN.setKey("oldFurnaceScreen"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 2)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_FURNACE_CANDY)
        public TweakType.RecipeBook furnaceBook = DefaultConfig.Candy.FURNACE_BOOK;
        static { CandyTweak.FURNACE_RECIPE.setKey("furnaceBook"); }

        // Interface - Chat Screen

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_CHAT_CANDY)
        public boolean oldChatInput = DefaultConfig.Candy.OLD_CHAT_INPUT;
        static { CandyTweak.CHAT_INPUT.setKey("oldChatInput"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_CHAT_CANDY)
        public boolean oldChatBox = DefaultConfig.Candy.OLD_CHAT_BOX;
        static { CandyTweak.CHAT_BOX.setKey("oldChatBox"); }

        /* Interface - Tooltip Candy */

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.FAIL)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_TOOLTIP_CANDY)
        public boolean oldTooltipBoxes = DefaultConfig.Candy.OLD_TOOLTIP_BOXES;
        static { CandyTweak.TOOLTIP_BOXES.setKey("oldTooltipBoxes"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 2)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_TOOLTIP_CANDY)
        public boolean oldNoItemTooltips = DefaultConfig.Candy.OLD_NO_ITEM_TOOLTIPS;
        static { CandyTweak.NO_ITEM_TOOLTIPS.setKey("oldNoItemTooltips"); }

        // Tooltip Candy - Tooltip Parts

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.DisabledBoolean(value = true)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.TOOLTIP_PARTS_CANDY)
        public boolean showEnchantmentTip = DefaultConfig.Candy.SHOW_ENCHANTMENTS_TIP;
        static { CandyTweak.ENCHANTMENT_TIP.setKey("showEnchantmentTip"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.DisabledBoolean(value = true)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.TOOLTIP_PARTS_CANDY)
        public boolean showModifiersTip = DefaultConfig.Candy.SHOW_MODIFIERS_TIP;
        static { CandyTweak.MODIFIERS_TIP.setKey("showModifiersTip"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.DisabledBoolean(value = true)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.TOOLTIP_PARTS_CANDY)
        public boolean showDyeTip = DefaultConfig.Candy.SHOW_DYE_TIP;
        static { CandyTweak.DYE_TIP.setKey("showDyeTip"); }

        /**
         * Item Candy
         */

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Run.ReloadResources
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        @TweakClient.Gui.Cat(group = TweakClient.Category.ITEM_CANDY)
        public boolean fixItemModelGap = DefaultConfig.Candy.FIX_ITEM_MODEL_GAP;
        static { CandyTweak.FIX_ITEM_MODEL_GAP.setKey("fixItemModelGap"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.ITEM_CANDY)
        public boolean oldItemHolding = DefaultConfig.Candy.OLD_ITEM_HOLDING;
        static { CandyTweak.ITEM_HOLDING.setKey("oldItemHolding"); }

        // Item - Merging

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.SliderType(slider = TweakClient.Gui.Slider.GENERIC, langKey = LangUtil.Gui.SLIDER_LIMIT)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.MERGING_ITEM_CANDY)
        @ConfigEntry.BoundedDiscrete(min = 1, max = 64)
        public int itemMergeLimit = DefaultConfig.Candy.ITEM_MERGE_LIMIT;
        static { CandyTweak.ITEM_MERGE_LIMIT.setKey("itemMergeLimit"); }

        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.MERGING_ITEM_CANDY)
        public boolean oldItemMerging = DefaultConfig.Candy.OLD_ITEM_MERGING;
        static { CandyTweak.ITEM_MERGING.setKey("oldItemMerging"); }

        // Item - 2D Candy

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.FLAT_ITEM_CANDY)
        public boolean old2dColors = DefaultConfig.Candy.OLD_2D_COLORS;
        static { CandyTweak.FLAT_COLORS.setKey("old2dColors"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.FLAT_ITEM_CANDY)
        public boolean old2dItems = DefaultConfig.Candy.OLD_2D_ITEMS;
        static { CandyTweak.FLAT_ITEMS.setKey("old2dItems"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.FLAT_ITEM_CANDY)
        public boolean old2dFrames = DefaultConfig.Candy.OLD_2D_FRAMES;
        static { CandyTweak.FLAT_FRAMES.setKey("old2dFrames"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.FLAT_ITEM_CANDY)
        public boolean old2dThrownItems = DefaultConfig.Candy.OLD_2D_THROWN_ITEMS;
        static { CandyTweak.FLAT_THROW_ITEMS.setKey("old2dThrownItems"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.FLAT_ITEM_CANDY)
        public boolean old2dEnchantedItems = DefaultConfig.Candy.OLD_2D_ENCHANTED_ITEMS;
        static { CandyTweak.FLAT_ENCHANTED_ITEMS.setKey("old2dEnchantedItems"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Optifine
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.FLAT_ITEM_CANDY)
        public boolean old2dRendering = DefaultConfig.Candy.OLD_2D_RENDERING;
        static { CandyTweak.FLAT_RENDERING.setKey("old2dRendering"); }

        // Item - Interface

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.DISPLAY_ITEM_CANDY)
        public boolean oldDurabilityColors = DefaultConfig.Candy.OLD_DURABILITY_COLORS;
        static { CandyTweak.DURABILITY_COLORS.setKey("oldDurabilityColors"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.DISPLAY_ITEM_CANDY)
        public boolean oldNoSelectedItemName = DefaultConfig.Candy.OLD_NO_SELECTED_ITEM_NAME;
        static { CandyTweak.NO_SELECTED_ITEM_NAME.setKey("oldNoSelectedItemName"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.DISPLAY_ITEM_CANDY)
        public boolean oldPlainSelectedItemName = DefaultConfig.Candy.OLD_PLAIN_SELECTED_ITEM_NAME;
        static { CandyTweak.PLAIN_SELECTED_ITEM_NAME.setKey("oldPlainSelectedItemName"); }

        /**
         * Lighting Candy
         */

        // Lighting - World Lighting Candy

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.LIGHTING_WORLD_CANDY)
        public boolean disableBrightness = DefaultConfig.Candy.DISABLE_BRIGHTNESS;
        static { CandyTweak.DISABLE_BRIGHTNESS.setKey("disableBrightness"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.LIGHTING_WORLD_CANDY)
        public boolean oldLightFlicker = DefaultConfig.Candy.OLD_LIGHT_FLICKER;
        static { CandyTweak.LIGHT_FLICKER.setKey("oldLightFlicker"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.LIGHTING_WORLD_CANDY)
        public boolean oldNetherLighting = DefaultConfig.Candy.OLD_NETHER_LIGHTING;
        static { CandyTweak.NETHER_LIGHTING.setKey("oldNetherLighting"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Run.ReloadChunks
        @TweakClient.Gui.Optifine(incompatible = false)
        @TweakClient.Gui.Sodium(incompatible = false)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.LIGHTING_WORLD_CANDY)
        public boolean oldLightRendering = DefaultConfig.Candy.OLD_LIGHT_RENDERING;
        static { CandyTweak.LIGHT_RENDERING.setKey("oldLightRendering"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.LIGHTING_WORLD_CANDY)
        public boolean oldLightColor = DefaultConfig.Candy.OLD_LIGHT_COLOR;
        static { CandyTweak.LIGHT_COLOR.setKey("oldLightColor"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Run.ReloadChunks
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.LIGHTING_WORLD_CANDY)
        public boolean oldSmoothLighting = DefaultConfig.Candy.OLD_SMOOTH_LIGHTING;
        static { CandyTweak.SMOOTH_LIGHTING.setKey("oldSmoothLighting"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Run.ReloadChunks
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.SHADER_LIGHT)
        @TweakClient.Gui.SliderType(slider = TweakClient.Gui.Slider.GENERIC, langKey = LangUtil.Gui.SLIDER_BLOCK_LIGHT)
        @ConfigEntry.BoundedDiscrete(min = 0, max = 15)
        public int maxBlockLight = DefaultConfig.Candy.MAX_BLOCK_LIGHT;
        static { CandyTweak.MAX_BLOCK_LIGHT.setKey("maxBlockLight"); }

        // Lighting - Block Lighting Candy

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Run.ReloadChunks
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.LIGHTING_BLOCK_CANDY)
        public boolean oldLeavesLighting = DefaultConfig.Candy.OLD_LEAVES_LIGHTING;
        static { CandyTweak.LEAVES_LIGHTING.setKey("oldLeavesLighting"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Run.ReloadChunks
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.LIGHTING_BLOCK_CANDY)
        public boolean oldWaterLighting = DefaultConfig.Candy.OLD_WATER_LIGHTING;
        static { CandyTweak.WATER_LIGHTING.setKey("oldWaterLighting"); }

        /**
         * Particle Candy
         */

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.PARTICLE_CANDY)
        public boolean oldOpaqueExperience = DefaultConfig.Candy.OLD_OPAQUE_EXPERIENCE;
        static { CandyTweak.OPAQUE_EXPERIENCE.setKey("oldOpaqueExperience"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.PARTICLE_CANDY)
        public boolean disableNetherParticles = DefaultConfig.Candy.DISABLE_NETHER_PARTICLES;
        static { CandyTweak.NO_NETHER_PARTICLES.setKey("disableNetherParticles"); }

        // Particle - Block Candy

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.PARTICLE_BLOCK_CANDY)
        public boolean disableLeverParticles = DefaultConfig.Candy.DISABLE_LEVER_PARTICLES;
        static { CandyTweak.NO_LEVER_PARTICLES.setKey("disableLeverParticles"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.PARTICLE_BLOCK_CANDY)
        public boolean disableGrowthParticles = DefaultConfig.Candy.DISABLE_GROWTH_PARTICLES;
        static { CandyTweak.NO_GROWTH_PARTICLES.setKey("disableGrowthParticles"); }

        // Particle - Player Candy

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.PARTICLE_PLAYER_CANDY)
        public boolean disableFallingParticles = DefaultConfig.Candy.DISABLE_FALLING_PARTICLES;
        static { CandyTweak.NO_FALLING_PARTICLES.setKey("disableFallingParticles"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.PARTICLE_PLAYER_CANDY)
        public boolean disableSprintingParticles = DefaultConfig.Candy.DISABLE_SPRINTING_PARTICLES;
        static { CandyTweak.NO_SPRINTING_PARTICLES.setKey("disableSprintingParticles"); }

        // Particle - Attack Candy

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.PARTICLE_ATTACK_CANDY)
        public boolean oldSweepParticles = DefaultConfig.Candy.OLD_SWEEP_PARTICLES;
        static { CandyTweak.SWEEP.setKey("oldSweepParticles"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.PARTICLE_ATTACK_CANDY)
        public boolean oldNoDamageParticles = DefaultConfig.Candy.OLD_NO_DAMAGE_PARTICLES;
        static { CandyTweak.NO_DAMAGE_PARTICLES.setKey("oldNoDamageParticles"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.PARTICLE_ATTACK_CANDY)
        public boolean oldNoCritParticles = DefaultConfig.Candy.OLD_NO_CRIT_PARTICLES;
        static { CandyTweak.NO_CRIT_PARTICLES.setKey("oldNoCritParticles"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.PARTICLE_ATTACK_CANDY)
        public boolean oldNoMagicHitParticles = DefaultConfig.Candy.OLD_NO_MAGIC_HIT_PARTICLES;
        static { CandyTweak.NO_MAGIC_HIT_PARTICLES.setKey("oldNoMagicHitParticles"); }

        // Particle - Explosion Candy

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.PARTICLE_EXPLOSION_CANDY)
        public boolean oldExplosionParticles = DefaultConfig.Candy.OLD_EXPLOSION_PARTICLES;
        static { CandyTweak.EXPLOSION_PARTICLES.setKey("oldExplosionParticles"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.PARTICLE_EXPLOSION_CANDY)
        public boolean oldMixedExplosionParticles = DefaultConfig.Candy.OLD_MIXED_EXPLOSION_PARTICLES;
        static { CandyTweak.MIXED_EXPLOSION_PARTICLES.setKey("oldMixedExplosionParticles"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.PARTICLE_EXPLOSION_CANDY)
        public boolean unoptimizedExplosionParticles = DefaultConfig.Candy.UNOPTIMIZED_EXPLOSION_PARTICLES;
        static { CandyTweak.UNOPTIMIZED_EXPLOSION_PARTICLES.setKey("unoptimizedExplosionParticles"); }

        /**
         * Title Screen Candy (Embedded in Interface Candy)
         */

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.FAIL)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_TITLE_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        public boolean overrideTitleScreen = DefaultConfig.Candy.OVERRIDE_TITLE_SCREEN;
        static { CandyTweak.OVERRIDE_TITLE_SCREEN.setKey("overrideTitleScreen"); }

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_TITLE_CANDY)
        public boolean oldTitleBackground = DefaultConfig.Candy.OLD_TITLE_BACKGROUND;
        static { CandyTweak.TITLE_BACKGROUND.setKey("oldTitleBackground"); }

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.INTERFACE_TITLE_CANDY)
        public boolean uncapTitleFPS = DefaultConfig.Candy.UNCAP_TITLE_FPS;
        static { CandyTweak.UNCAP_TITLE_FPS.setKey("uncapTitleFPS"); }

        // Title Screen - Logo

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.TITLE_LOGO_CANDY)
        public boolean oldAlphaLogo = DefaultConfig.Candy.OLD_ALPHA_LOGO;
        static { CandyTweak.ALPHA_LOGO.setKey("oldAlphaLogo"); }

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.TITLE_LOGO_CANDY)
        public boolean oldLogoOutline = DefaultConfig.Candy.OLD_LOGO_OUTLINE;
        static { CandyTweak.LOGO_OUTLINE.setKey("oldLogoOutline"); }

        // Title Screen - Buttons

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.TITLE_BUTTON_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        public TweakVersion.TitleLayout oldButtonLayout = DefaultConfig.Candy.TITLE_BUTTON_LAYOUT;
        static { CandyTweak.TITLE_BUTTON_LAYOUT.setKey("oldButtonLayout"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.TITLE_BUTTON_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 2)
        public boolean includeModsOnTitle = DefaultConfig.Candy.INCLUDE_MODS_ON_TITLE;
        static { CandyTweak.TITLE_MODS_BUTTON.setKey("includeModsOnTitle"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.TITLE_BUTTON_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 3)
        public boolean removeTitleRealmsButton = DefaultConfig.Candy.REMOVE_TITLE_REALMS;
        static { CandyTweak.TITLE_REALMS.setKey("removeTitleRealmsButton"); }

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.TITLE_BUTTON_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 4)
        public boolean removeTitleAccessibilityButton = DefaultConfig.Candy.REMOVE_TITLE_ACCESSIBILITY;
        static { CandyTweak.TITLE_ACCESSIBILITY.setKey("removeTitleAccessibilityButton"); }

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.TITLE_BUTTON_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 5)
        public boolean removeTitleLanguageButton = DefaultConfig.Candy.REMOVE_TITLE_LANGUAGE;
        static { CandyTweak.TITLE_LANGUAGE.setKey("removeTitleLanguageButton"); }

        // Title Screen - Text

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.TITLE_TEXT_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        public String titleVersionText = DefaultConfig.Candy.TITLE_VERSION_TEXT;

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.TITLE_TEXT_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 2)
        public boolean titleBottomLeftText = DefaultConfig.Candy.TITLE_BOTTOM_LEFT_TEXT;
        static { CandyTweak.TITLE_BOTTOM_LEFT_TEXT.setKey("titleBottomLeftText"); }

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.TITLE_TEXT_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 3)
        public boolean removeTitleModLoaderText = DefaultConfig.Candy.REMOVE_TITLE_MOD_LOADER_TEXT;
        static { CandyTweak.TITLE_MOD_LOADER_TEXT.setKey("removeTitleModLoaderText"); }

        /**
         * World Candy
         */

        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Run.ReloadChunks
        @TweakClient.Gui.Cat(group = TweakClient.Category.WORLD_CANDY)
        public boolean oldSquareBorder = DefaultConfig.Candy.OLD_SQUARE_BORDER;
        static { CandyTweak.SQUARE_BORDER.setKey("oldSquareBorder"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.WORLD_CANDY)
        public boolean oldNameTags = DefaultConfig.Candy.OLD_NAME_TAGS;
        static { CandyTweak.NAME_TAGS.setKey("oldNameTags"); }

        // World - Fog Candy

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.WORLD_FOG_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        public boolean oldTerrainFog = DefaultConfig.Candy.OLD_TERRAIN_FOG;
        static { CandyTweak.TERRAIN_FOG.setKey("oldTerrainFog"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.WORLD_FOG_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 2)
        public boolean oldHorizonFog = DefaultConfig.Candy.OLD_HORIZON_FOG;
        static { CandyTweak.HORIZON_FOG.setKey("oldHorizonFog"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.WORLD_FOG_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 3)
        public boolean oldNetherFog = DefaultConfig.Candy.OLD_NETHER_FOG;
        static { CandyTweak.NETHER_FOG.setKey("oldNetherFog"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.WORLD_FOG_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 4)
        public boolean oldSunriseSunsetFog = DefaultConfig.Candy.OLD_SUNRISE_SUNSET_FOG;
        static { CandyTweak.SUNRISE_SUNSET_FOG.setKey("oldSunriseSunsetFog"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.WORLD_FOG_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 5)
        public TweakVersion.Generic oldFogColor = DefaultConfig.Candy.OLD_FOG_COLOR;
        static { CandyTweak.FOG_COLOR.setKey("oldFogColor"); }

        // World - Fog Candy - Water

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.WATER_FOG_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        public boolean oldWaterFogDensity = DefaultConfig.Candy.OLD_WATER_FOG_DENSITY;
        static { CandyTweak.WATER_FOG_DENSITY.setKey("oldWaterFogDensity"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.WATER_FOG_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 2)
        public boolean oldWaterFogColor = DefaultConfig.Candy.OLD_WATER_FOG_COLOR;
        static { CandyTweak.WATER_FOG_COLOR.setKey("oldWaterFogColor"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.WATER_FOG_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 3)
        public boolean smoothWaterDensity = DefaultConfig.Candy.SMOOTH_WATER_DENSITY;
        static { CandyTweak.SMOOTH_WATER_DENSITY.setKey("smoothWaterDensity"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.WATER_FOG_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 4)
        public boolean smoothWaterColor = DefaultConfig.Candy.SMOOTH_WATER_COLOR;
        static { CandyTweak.SMOOTH_WATER_COLOR.setKey("smoothWaterColor"); }

        // World - Sky Candy

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.WORLD_SKY_CANDY)
        public boolean disableSunriseSunsetColors = DefaultConfig.Candy.DISABLE_SUNRISE_SUNSET_COLORS;
        static { CandyTweak.DISABLE_SUNRISE_SUNSET_COLOR.setKey("disableSunriseSunsetColors"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.WORLD_SKY_CANDY)
        public boolean oldSunriseAtNorth = DefaultConfig.Candy.OLD_SUNRISE_AT_NORTH;
        static { CandyTweak.SUNRISE_AT_NORTH.setKey("oldSunriseAtNorth"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.WORLD_SKY_CANDY)
        public TweakVersion.Generic oldStars = DefaultConfig.Candy.OLD_STARS;
        static { CandyTweak.STARS.setKey("oldStars"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.WORLD_SKY_CANDY)
        public TweakVersion.Generic oldSkyColor = DefaultConfig.Candy.OLD_SKY_COLOR;
        static { CandyTweak.SKY_COLOR.setKey("oldSkyColor"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.WORLD_SKY_CANDY)
        @TweakClient.Gui.SliderType(slider = TweakClient.Gui.Slider.CLOUD)
        @TweakClient.Gui.DisabledInteger(value = DefaultConfig.Candy.DISABLED_CLOUD_HEIGHT)
        @ConfigEntry.BoundedDiscrete(min = 108, max = DefaultConfig.Candy.DISABLED_CLOUD_HEIGHT)
        public int oldCloudHeight = DefaultConfig.Candy.OLD_CLOUD_HEIGHT;
        static { CandyTweak.CLOUD_HEIGHT.setKey("oldCloudHeight"); }

        /* World - Void Candy */

        // Void Sky Candy

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.VOID_SKY_CANDY)
        public TweakVersion.Generic oldBlueVoid = DefaultConfig.Candy.OLD_BLUE_VOID;
        static { CandyTweak.BLUE_VOID.setKey("oldBlueVoid"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.VOID_SKY_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 2)
        public boolean oldBlueVoidOverride = DefaultConfig.Candy.OLD_BLUE_VOID_OVERRIDE;
        static { CandyTweak.BLUE_VOID_OVERRIDE.setKey("oldBlueVoidOverride"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.VOID_SKY_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 4)
        public boolean oldDarkVoidHeight = DefaultConfig.Candy.OLD_DARK_VOID_HEIGHT;
        static { CandyTweak.DARK_VOID_HEIGHT.setKey("oldDarkVoidHeight"); }

        // Void Fog Candy

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.DisabledBoolean(value = true)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.VOID_FOG_CANDY)
        public boolean disableVoidFog = DefaultConfig.Candy.DISABLE_VOID_FOG;
        static { CandyTweak.DISABLE_VOID_FOG.setKey("disableVoidFog"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 2)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.VOID_FOG_CANDY)
        public boolean creativeVoidFog = DefaultConfig.Candy.CREATIVE_VOID_FOG;
        static { CandyTweak.CREATIVE_VOID_FOG.setKey("creativeVoidFog"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 3)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.VOID_FOG_CANDY)
        public boolean creativeVoidParticles = DefaultConfig.Candy.CREATIVE_VOID_PARTICLE;
        static { CandyTweak.CREATIVE_VOID_PARTICLE.setKey("creativeVoidParticles"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.DisabledBoolean(value = true)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 4)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.VOID_FOG_CANDY)
        public boolean lightRemovesVoidFog = DefaultConfig.Candy.LIGHT_REMOVES_VOID_FOG;
        static { CandyTweak.LIGHT_REMOVES_VOID_FOG.setKey("lightRemovesVoidFog"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.Color
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 5)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.VOID_FOG_CANDY)
        public String voidFogColor = DefaultConfig.Candy.VOID_FOG_COLOR;
        static { CandyTweak.VOID_FOG_COLOR.setKey("voidFogColor"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.SliderType(slider = TweakClient.Gui.Slider.GENERIC, suffix = "%", langKey = LangUtil.Gui.SLIDER_ENCROACH)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 6)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.VOID_FOG_CANDY)
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int voidFogEncroach = DefaultConfig.Candy.VOID_FOG_ENCROACH;
        static { CandyTweak.VOID_FOG_ENCROACH.setKey("voidFogEncroach"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.SliderType(slider = TweakClient.Gui.Slider.GENERIC, langKey = LangUtil.Gui.SLIDER_Y_LEVEL)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.BOTTOM, order = 1)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.VOID_FOG_CANDY)
        @ConfigEntry.BoundedDiscrete(min = -64, max = 320)
        public int voidFogStart = DefaultConfig.Candy.VOID_FOG_START;
        static { CandyTweak.VOID_FOG_START.setKey("voidFogStart"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.SliderType(slider = TweakClient.Gui.Slider.GENERIC, langKey = LangUtil.Gui.SLIDER_Y_LEVEL)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.BOTTOM, order = 2)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.VOID_FOG_CANDY)
        @ConfigEntry.BoundedDiscrete(min = -64, max = 320)
        public int voidParticleStart = DefaultConfig.Candy.VOID_PARTICLE_START;
        static { CandyTweak.VOID_PARTICLE_START.setKey("voidParticleStart"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.SliderType(slider = TweakClient.Gui.Slider.GENERIC, langKey = LangUtil.Gui.SLIDER_RADIUS)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.BOTTOM, order = 3)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.VOID_FOG_CANDY)
        @ConfigEntry.BoundedDiscrete(min = 0, max = 32)
        public int voidParticleRadius = DefaultConfig.Candy.VOID_PARTICLE_RADIUS;
        static { CandyTweak.VOID_PARTICLE_RADIUS.setKey("voidParticleRadius"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.SliderType(slider = TweakClient.Gui.Slider.GENERIC, suffix = "%", langKey = LangUtil.Gui.SLIDER_DENSITY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.BOTTOM, order = 4)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.VOID_FOG_CANDY)
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int voidParticleDensity = DefaultConfig.Candy.VOID_PARTICLE_DENSITY;
        static { CandyTweak.VOID_PARTICLE_DENSITY.setKey("voidParticleDensity"); }
    }

    @TweakSide.Ignore
    public Gameplay gameplay = new Gameplay();
    public static class Gameplay
    {
        /**
         * Bugs
         */

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.BUG_GAMEPLAY)
        public boolean oldLadderGap = DefaultConfig.Gameplay.OLD_LADDER_GAP;
        static { GameplayTweak.LADDER_GAP.setKey("oldLadderGap"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.BUG_GAMEPLAY)
        public boolean oldSquidMilking = DefaultConfig.Gameplay.OLD_SQUID_MILKING;
        static { GameplayTweak.SQUID_MILK.setKey("oldSquidMilking"); }

        /**
         * Mob System
         */

        // Artificial Intelligence

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.MOB_AI_GAMEPLAY)
        public boolean disableAnimalPanic = DefaultConfig.Gameplay.DISABLE_ANIMAL_PANIC;
        static { GameplayTweak.ANIMAL_PANIC.setKey("disableAnimalPanic"); }

        // Animal - Spawn

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.ANIMAL_MOB_SPAWN)
        @TweakClient.Gui.SliderType(slider = TweakClient.Gui.Slider.GENERIC, langKey = LangUtil.Gui.SLIDER_CAP)
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int animalSpawnCap = DefaultConfig.Gameplay.ANIMAL_SPAWN_CAP;
        static { GameplayTweak.ANIMAL_CAP.setKey("animalSpawnCap"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.ANIMAL_MOB_SPAWN)
        public boolean oldAnimalSpawning = DefaultConfig.Gameplay.OLD_ANIMAL_SPAWNING;
        static { GameplayTweak.ANIMAL_SPAWNING.setKey("oldAnimalSpawning"); }

        // Animal - Sheep

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.ANIMAL_MOB_SHEEP)
        public boolean disableSheepEatGrass = DefaultConfig.Gameplay.DISABLE_SHEEP_EAT_GRASS;
        static { GameplayTweak.SHEEP_EAT_GRASS.setKey("disableSheepEatGrass"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.ANIMAL_MOB_SHEEP)
        public boolean oldSheepPunching = DefaultConfig.Gameplay.OLD_SHEEP_PUNCHING;
        static { GameplayTweak.SHEEP_PUNCHING.setKey("oldSheepPunching"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.ANIMAL_MOB_SHEEP)
        public boolean oneWoolPunch = DefaultConfig.Gameplay.ONE_WOOL_PUNCH;
        static { GameplayTweak.ONE_WOOL_PUNCH.setKey("oneWoolPunch"); }

        /* Mob Drops */

        // Classic Mob Drops

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.CLASSIC_MOB_DROPS)
        public boolean oldZombiePigmenDrops = DefaultConfig.Gameplay.OLD_ZOMBIE_PIGMEN_DROPS;
        static { GameplayTweak.ZOMBIE_PIGMEN_DROPS.setKey("oldZombiePigmenDrops"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.CLASSIC_MOB_DROPS)
        public boolean oldSkeletonDrops = DefaultConfig.Gameplay.OLD_SKELETON_DROPS;
        static { GameplayTweak.SKELETON_DROPS.setKey("oldSkeletonDrops"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.CLASSIC_MOB_DROPS)
        public boolean oldChickenDrops = DefaultConfig.Gameplay.OLD_CHICKEN_DROPS;
        static { GameplayTweak.CHICKEN_DROPS.setKey("oldChickenDrops"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.CLASSIC_MOB_DROPS)
        public boolean oldZombieDrops = DefaultConfig.Gameplay.OLD_ZOMBIE_DROPS;
        static { GameplayTweak.ZOMBIE_DROPS.setKey("oldZombieDrops"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.CLASSIC_MOB_DROPS)
        public boolean oldSpiderDrops = DefaultConfig.Gameplay.OLD_SPIDER_DROPS;
        static { GameplayTweak.SPIDER_DROPS.setKey("oldSpiderDrops"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.CLASSIC_MOB_DROPS)
        public boolean oldSheepDrops = DefaultConfig.Gameplay.OLD_SHEEP_DROPS;
        static { GameplayTweak.SHEEP_DROPS.setKey("oldSheepDrops"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.CLASSIC_MOB_DROPS)
        public boolean oldCowDrops = DefaultConfig.Gameplay.OLD_COW_DROPS;
        static { GameplayTweak.COW_DROPS.setKey("oldCowDrops"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.CLASSIC_MOB_DROPS)
        public boolean oldPigDrops = DefaultConfig.Gameplay.OLD_PIG_DROPS;
        static { GameplayTweak.PIG_DROPS.setKey("oldPigDrops"); }

        // Modern Mob Drops

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.MODERN_MOB_DROPS)
        public boolean oldStyleZombieVillagerDrops = DefaultConfig.Gameplay.OLD_STYLE_ZOMBIE_VILLAGER_DROPS;
        static { GameplayTweak.ZOMBIE_VILLAGER_DROPS.setKey("oldStyleZombieVillagerDrops"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.MODERN_MOB_DROPS)
        public boolean oldStyleCaveSpiderDrops = DefaultConfig.Gameplay.OLD_STYLE_CAVE_SPIDER_DROPS;
        static { GameplayTweak.CAVE_SPIDER_DROPS.setKey("oldStyleCaveSpiderDrops"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.MODERN_MOB_DROPS)
        public boolean oldStyleMooshroomDrops = DefaultConfig.Gameplay.OLD_STYLE_MOOSHROOM_DROPS;
        static { GameplayTweak.MOOSHROOM_DROPS.setKey("oldStyleMooshroomDrops"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.MODERN_MOB_DROPS)
        public boolean oldStyleDrownedDrops = DefaultConfig.Gameplay.OLD_STYLE_DROWNED_DROPS;
        static { GameplayTweak.DROWNED_DROPS.setKey("oldStyleDrownedDrops"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.MODERN_MOB_DROPS)
        public boolean oldStyleRabbitDrops = DefaultConfig.Gameplay.OLD_STYLE_RABBIT_DROPS;
        static { GameplayTweak.RABBIT_DROPS.setKey("oldStyleRabbitDrops"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.MODERN_MOB_DROPS)
        public boolean oldStyleStrayDrops = DefaultConfig.Gameplay.OLD_STYLE_STRAY_DROPS;
        static { GameplayTweak.STRAY_DROPS.setKey("oldStyleStrayDrops"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.MODERN_MOB_DROPS)
        public boolean oldStyleHuskDrops = DefaultConfig.Gameplay.OLD_STYLE_HUSK_DROPS;
        static { GameplayTweak.HUSK_DROPS.setKey("oldStyleHuskDrops"); }

        /**
         * Combat System
         */

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.COMBAT_GAMEPLAY)
        public boolean disableCooldown = DefaultConfig.Gameplay.DISABLE_COOLDOWN;
        static { GameplayTweak.DISABLE_COOLDOWN.setKey("disableCooldown"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.COMBAT_GAMEPLAY)
        public boolean disableMissTimer = DefaultConfig.Gameplay.DISABLE_MISS_TIMER;
        static { GameplayTweak.DISABLE_MISS_TIMER.setKey("disableMissTimer"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.COMBAT_GAMEPLAY)
        public boolean disableSweep = DefaultConfig.Gameplay.DISABLE_SWEEP;
        static { GameplayTweak.DISABLE_SWEEP.setKey("disableSweep"); }

        // Combat - Bow

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.COMBAT_BOW_GAMEPLAY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        @TweakClient.Gui.SliderType(slider = TweakClient.Gui.Slider.INTENSITY)
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int arrowSpeed = DefaultConfig.Gameplay.ARROW_SPEED;
        static { GameplayTweak.ARROW_SPEED.setKey("arrowSpeed"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.COMBAT_BOW_GAMEPLAY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 2)
        public boolean instantBow = DefaultConfig.Gameplay.INSTANT_BOW;
        static { GameplayTweak.INSTANT_BOW.setKey("instantBow"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.COMBAT_BOW_GAMEPLAY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 3)
        public boolean invincibleBow = DefaultConfig.Gameplay.INVINCIBLE_BOW;
        static { GameplayTweak.INVINCIBLE_BOW.setKey("invincibleBow"); }

        /**
         * Experience System
         */

        /* Experience - Bar */

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.EXPERIENCE_BAR_GAMEPLAY)
        public boolean disableExperienceBar = DefaultConfig.Gameplay.DISABLE_EXPERIENCE_BAR;
        static { GameplayTweak.DISABLE_EXP_BAR.setKey("disableExperienceBar"); }

        // Alternative Level Text

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.ALT_XP_LEVEL_GAMEPLAY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        public boolean showXpLevelText = DefaultConfig.Gameplay.SHOW_XP_LEVEL_TEXT;
        static { GameplayTweak.SHOW_XP_LEVEL.setKey("showXpLevelText"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.ALT_XP_LEVEL_GAMEPLAY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 2)
        public TweakType.Corner altXpLevelCorner = DefaultConfig.Gameplay.XP_LEVEL_CORNER;
        static { GameplayTweak.XP_LEVEL_CORNER.setKey("altXpLevelCorner"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.ALT_XP_LEVEL_GAMEPLAY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 3)
        public String altXpLevelText = DefaultConfig.Gameplay.XP_LEVEL_TEXT;
        static { GameplayTweak.XP_LEVEL_TEXT.setKey("altXpLevelText"); }

        // Alternative Progress Text

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.ALT_XP_PROGRESS_GAMEPLAY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        public boolean showXpProgressText = DefaultConfig.Gameplay.SHOW_XP_PROGRESS_TEXT;
        static { GameplayTweak.SHOW_XP_PROGRESS.setKey("showXpProgressText"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.ALT_XP_PROGRESS_GAMEPLAY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 2)
        public boolean useDynamicProgressColor = DefaultConfig.Gameplay.USE_DYNAMIC_PROGRESS_COLOR;
        static { GameplayTweak.USE_DYNAMIC_PROGRESS_COLOR.setKey("useDynamicProgressColor"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.ALT_XP_PROGRESS_GAMEPLAY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 3)
        public TweakType.Corner altXpProgressCorner = DefaultConfig.Gameplay.XP_PROGRESS_CORNER;
        static { GameplayTweak.XP_PROGRESS_CORNER.setKey("altXpProgressCorner"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.ALT_XP_PROGRESS_GAMEPLAY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 4)
        public String altXpProgressText = DefaultConfig.Gameplay.XP_PROGRESS_TEXT;
        static { GameplayTweak.XP_PROGRESS_TEXT.setKey("altXpProgressText"); }

        // Experience - Orb

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.EXPERIENCE_ORB_GAMEPLAY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        public boolean disableOrbSpawn = DefaultConfig.Gameplay.DISABLE_ORB_SPAWN;
        static { GameplayTweak.ORB_SPAWN.setKey("disableOrbSpawn"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.EXPERIENCE_ORB_GAMEPLAY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 2)
        public boolean disableOrbRendering = DefaultConfig.Gameplay.DISABLE_ORB_RENDERING;
        static { GameplayTweak.ORB_RENDERING.setKey("disableOrbRendering"); }

        // Experience - Blocks

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.EXPERIENCE_BLOCK_GAMEPLAY)
        public boolean disableAnvil = DefaultConfig.Gameplay.DISABLE_ANVIL;
        static { GameplayTweak.ANVIL.setKey("disableAnvil"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.EXPERIENCE_BLOCK_GAMEPLAY)
        public boolean disableEnchantTable = DefaultConfig.Gameplay.DISABLE_ENCHANT_TABLE;
        static { GameplayTweak.ENCHANT_TABLE.setKey("disableEnchantTable"); }

        /**
         * Game Mechanics
         */

        // Mechanics - Player

        @TweakClient.Gui.New
        @TweakSide.Dynamic
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.MECHANICS_PLAYER_GAMEPLAY)
        public boolean disableSprint = DefaultConfig.Gameplay.DISABLE_SPRINT;
        static { GameplayTweak.SPRINT.setKey("disableSprint"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.MECHANICS_PLAYER_GAMEPLAY)
        public boolean leftClickDoor = DefaultConfig.Gameplay.LEFT_CLICK_DOOR;
        static { GameplayTweak.LEFT_CLICK_DOOR.setKey("leftClickDoor"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.MECHANICS_PLAYER_GAMEPLAY)
        public boolean leftClickLever = DefaultConfig.Gameplay.LEFT_CLICK_LEVER;
        static { GameplayTweak.LEFT_CLICK_LEVER.setKey("leftClickLever"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.MECHANICS_PLAYER_GAMEPLAY)
        public boolean leftClickButton = DefaultConfig.Gameplay.LEFT_CLICK_BUTTON;
        static { GameplayTweak.LEFT_CLICK_BUTTON.setKey("leftClickButton"); }

        // Mechanics - Farming

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.MECHANICS_FARMING_GAMEPLAY)
        public boolean instantBonemeal = DefaultConfig.Gameplay.INSTANT_BONE_MEAL;
        static { GameplayTweak.INSTANT_BONE_MEAL.setKey("instantBonemeal"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.MECHANICS_FARMING_GAMEPLAY)
        public boolean tilledGrassSeeds = DefaultConfig.Gameplay.TILLED_GRASS_SEEDS;
        static { GameplayTweak.TILLED_GRASS_SEEDS.setKey("tilledGrassSeeds"); }

        // Mechanics - Fire

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Warning
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.MECHANICS_FIRE_GAMEPLAY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        public boolean oldFire = DefaultConfig.Gameplay.OLD_FIRE;
        static { GameplayTweak.FIRE_SPREAD.setKey("oldFire"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.MECHANICS_FIRE_GAMEPLAY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 2)
        public boolean infiniteBurn = DefaultConfig.Gameplay.INFINITE_BURN;
        static { GameplayTweak.INFINITE_BURN.setKey("infiniteBurn"); }

        // Mechanics - Swimming

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.MECHANICS_SWIMMING_GAMEPLAY)
        public boolean instantAir = DefaultConfig.Gameplay.INSTANT_AIR;
        static { GameplayTweak.INSTANT_AIR.setKey("instantAir"); }

        @TweakClient.Gui.New
        @TweakSide.Dynamic
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.MECHANICS_SWIMMING_GAMEPLAY)
        public boolean disableSwim = DefaultConfig.Gameplay.DISABLE_SWIM;
        static { GameplayTweak.SWIM.setKey("disableSwim"); }

        /**
         * Hunger System
         */

        /* Hunger - Bar */

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.HUNGER_BAR_GAMEPLAY)
        public boolean disableHungerBar = DefaultConfig.Gameplay.DISABLE_HUNGER_BAR;
        static { GameplayTweak.DISABLE_HUNGER_BAR.setKey("disableHungerBar"); }

        // Alternative Food Text

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.ALT_HUNGER_FOOD_GAMEPLAY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        public boolean showHungerFoodText = DefaultConfig.Gameplay.SHOW_HUNGER_FOOD_TEXT;
        static { GameplayTweak.SHOW_HUNGER_FOOD.setKey("showHungerFoodText"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.ALT_HUNGER_FOOD_GAMEPLAY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 2)
        public boolean useDynamicFoodColor = DefaultConfig.Gameplay.USE_DYNAMIC_FOOD_COLOR;
        static { GameplayTweak.USE_DYNAMIC_FOOD_COLOR.setKey("useDynamicFoodColor"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.ALT_HUNGER_FOOD_GAMEPLAY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 3)
        public TweakType.Corner altHungerFoodCorner = DefaultConfig.Gameplay.HUNGER_FOOD_CORNER;
        static { GameplayTweak.HUNGER_FOOD_CORNER.setKey("altHungerFoodCorner"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.ALT_HUNGER_FOOD_GAMEPLAY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 4)
        public String altHungerFoodText = DefaultConfig.Gameplay.HUNGER_FOOD_TEXT;
        static { GameplayTweak.HUNGER_FOOD_TEXT.setKey("altHungerFoodText"); }

        // Alternative Saturation Text

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.ALT_HUNGER_SATURATION_GAMEPLAY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        public boolean showHungerSaturationText = DefaultConfig.Gameplay.SHOW_HUNGER_SATURATION_TEXT;
        static { GameplayTweak.SHOW_HUNGER_SATURATION.setKey("showHungerSaturationText"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.ALT_HUNGER_SATURATION_GAMEPLAY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 2)
        public boolean useDynamicSaturationColor = DefaultConfig.Gameplay.USE_DYNAMIC_SATURATION_COLOR;
        static { GameplayTweak.USE_DYNAMIC_SATURATION_COLOR.setKey("useDynamicSaturationColor"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.ALT_HUNGER_SATURATION_GAMEPLAY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 3)
        public TweakType.Corner altHungerSaturationCorner = DefaultConfig.Gameplay.HUNGER_SATURATION_CORNER;
        static { GameplayTweak.HUNGER_SATURATION_CORNER.setKey("altHungerSaturationCorner"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Emb(group = TweakClient.Embedded.ALT_HUNGER_SATURATION_GAMEPLAY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 4)
        public String altHungerSaturationText = DefaultConfig.Gameplay.HUNGER_SATURATION_TEXT;
        static { GameplayTweak.HUNGER_SATURATION_TEXT.setKey("altHungerSaturationText"); }

        // Hunger - Food

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.HUNGER_FOOD_GAMEPLAY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        public boolean disableHunger = DefaultConfig.Gameplay.DISABLE_HUNGER;
        static { GameplayTweak.HUNGER.setKey("disableHunger"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.HUNGER_FOOD_GAMEPLAY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 2)
        public boolean instantEat = DefaultConfig.Gameplay.INSTANT_EAT;
        static { GameplayTweak.INSTANT_EAT.setKey("instantEat"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Subcategory.HUNGER_FOOD_GAMEPLAY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 3)
        public boolean oldFoodStacking = DefaultConfig.Gameplay.OLD_FOOD_STACKING;
        static { GameplayTweak.FOOD_STACKING.setKey("oldFoodStacking"); }
    }

    @TweakSide.Ignore
    public Animation animation = new Animation();
    public static class Animation
    {
        /**
         * Arm Animations
         */

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.ARM_ANIMATION)
        public boolean oldArmSway = DefaultConfig.Animation.OLD_ARM_SWAY;
        static { AnimationTweak.ARM_SWAY.setKey("oldArmSway"); }

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Cat(group = TweakClient.Category.ARM_ANIMATION)
        public boolean armSwayMirror = DefaultConfig.Animation.ARM_SWAY_MIRROR;
        static { AnimationTweak.ARM_SWAY_MIRROR.setKey("armSwayMirror"); }

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Cat(group = TweakClient.Category.ARM_ANIMATION)
        @TweakClient.Gui.SliderType(slider = TweakClient.Gui.Slider.INTENSITY)
        @ConfigEntry.BoundedDiscrete(min = 0, max = 300)
        public int armSwayIntensity = DefaultConfig.Animation.ARM_SWAY_INTENSITY;
        static { AnimationTweak.ARM_SWAY_INTENSITY.setKey("armSwayIntensity"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.ARM_ANIMATION)
        public boolean oldSwing = DefaultConfig.Animation.OLD_SWING;
        static { AnimationTweak.ITEM_SWING.setKey("oldSwing"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.ARM_ANIMATION)
        public boolean oldSwingInterrupt = DefaultConfig.Animation.OLD_SWING_INTERRUPT;
        static { AnimationTweak.SWING_INTERRUPT.setKey("oldSwingInterrupt"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.ARM_ANIMATION)
        public boolean oldSwingDropping = DefaultConfig.Animation.OLD_SWING_DROPPING;
        static { AnimationTweak.SWING_DROP.setKey("oldSwingDropping"); }

        /**
         * Item Animations
         */

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.ITEM_ANIMATION)
        public boolean oldItemCooldown = DefaultConfig.Animation.OLD_ITEM_COOLDOWN;
        static { AnimationTweak.COOLDOWN.setKey("oldItemCooldown"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.ITEM_ANIMATION)
        public boolean oldItemReequip = DefaultConfig.Animation.OLD_ITEM_REEQUIP;
        static { AnimationTweak.REEQUIP.setKey("oldItemReequip"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.ITEM_ANIMATION)
        public boolean oldToolExplosion = DefaultConfig.Animation.OLD_TOOL_EXPLOSION;
        static { AnimationTweak.TOOL_EXPLODE.setKey("oldToolExplosion"); }

        /**
         * Mob Animations
         */

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.MOB_ANIMATION)
        public boolean oldZombieArms = DefaultConfig.Animation.OLD_ZOMBIE_ARMS;
        static { AnimationTweak.ZOMBIE_ARMS.setKey("oldZombieArms"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.MOB_ANIMATION)
        public boolean oldSkeletonArms = DefaultConfig.Animation.OLD_SKELETON_ARMS;
        static { AnimationTweak.SKELETON_ARMS.setKey("oldSkeletonArms"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.MOB_ANIMATION)
        public boolean oldGhastCharging = DefaultConfig.Animation.OLD_GHAST_CHARGING;
        static { AnimationTweak.GHAST_CHARGING.setKey("oldGhastCharging"); }

        /**
         * Player Animations
         */

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.PLAYER_ANIMATION)
        public boolean oldBackwardWalking = DefaultConfig.Animation.OLD_BACKWARD_WALKING;
        static { AnimationTweak.BACKWARD_WALK.setKey("oldBackwardWalking"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.PLAYER_ANIMATION)
        public boolean oldCollideBobbing = DefaultConfig.Animation.OLD_COLLIDE_BOBBING;
        static { AnimationTweak.COLLIDE_BOB.setKey("oldCollideBobbing"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.PLAYER_ANIMATION)
        public boolean oldVerticalBobbing = DefaultConfig.Animation.OLD_VERTICAL_BOBBING;
        static { AnimationTweak.BOB_VERTICAL.setKey("oldVerticalBobbing"); }

        @TweakClient.Gui.New
        @TweakSide.Dynamic
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.PLAYER_ANIMATION)
        public boolean oldCreativeCrouch = DefaultConfig.Animation.OLD_CREATIVE_CROUCH;
        static { AnimationTweak.CREATIVE_CROUCH.setKey("oldCreativeCrouch"); }

        @TweakClient.Gui.New
        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.PLAYER_ANIMATION)
        public boolean oldDirectionalDamage = DefaultConfig.Animation.OLD_DIRECTIONAL_DAMAGE;
        static { AnimationTweak.DIRECTIONAL_DAMAGE.setKey("oldDirectionalDamage"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.PLAYER_ANIMATION)
        public boolean oldRandomDamage = DefaultConfig.Animation.OLD_RANDOM_DAMAGE;
        static { AnimationTweak.RANDOM_DAMAGE.setKey("oldRandomDamage"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.PLAYER_ANIMATION)
        public boolean oldSneaking = DefaultConfig.Animation.OLD_SNEAKING;
        static { AnimationTweak.SNEAK_SMOOTH.setKey("oldSneaking"); }

        @TweakClient.Gui.New
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Cat(group = TweakClient.Category.PLAYER_ANIMATION)
        public boolean disableDeathTopple = DefaultConfig.Animation.DISABLE_DEATH_TOPPLE;
        static { AnimationTweak.DEATH_TOPPLE.setKey("disableDeathTopple"); }
    }

    @TweakSide.Ignore
    public Swing swing = new Swing();
    public static class Swing
    {
        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.DisabledBoolean(value = true)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        public boolean overrideSpeeds = DefaultConfig.Swing.OVERRIDE_SPEEDS;
        static { SwingTweak.OVERRIDE_SPEEDS.setKey("overrideSpeeds"); }

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 2)
        @ConfigEntry.BoundedDiscrete(min = DefaultConfig.Swing.GLOBAL, max = MAX)
        public int global = DefaultConfig.Swing.GLOBAL;

        /* Item Swing Speeds */

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Cat(group = TweakClient.Category.ITEM_SWING)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        @ConfigEntry.BoundedDiscrete(min = MIN, max = MAX)
        public int item = DefaultConfig.Swing.ITEM;

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Cat(group = TweakClient.Category.ITEM_SWING)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 2)
        @ConfigEntry.BoundedDiscrete(min = MIN, max = MAX)
        public int tool = DefaultConfig.Swing.TOOL;

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Cat(group = TweakClient.Category.ITEM_SWING)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 3)
        @ConfigEntry.BoundedDiscrete(min = MIN, max = MAX)
        public int block = DefaultConfig.Swing.BLOCK;

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Cat(group = TweakClient.Category.ITEM_SWING)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 4)
        @ConfigEntry.BoundedDiscrete(min = MIN, max = MAX)
        public int sword = DefaultConfig.Swing.SWORD;

        /* Potion Swing Speeds */

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Cat(group = TweakClient.Category.POTION_SWING)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        @ConfigEntry.BoundedDiscrete(min = DefaultConfig.Swing.GLOBAL, max = MAX)
        public int haste = DefaultConfig.Swing.HASTE;

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Cat(group = TweakClient.Category.POTION_SWING)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 2)
        @ConfigEntry.BoundedDiscrete(min = DefaultConfig.Swing.GLOBAL, max = MAX)
        public int fatigue = DefaultConfig.Swing.FATIGUE;
    }

    @TweakSide.Ignore
    public Gui gui = new Gui();
    public static class Gui
    {
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        public MenuOption defaultScreen = DefaultConfig.Gui.DEFAULT_SCREEN;
        static { GuiTweak.DEFAULT_SCREEN.setKey("defaultScreen"); }

        @SuppressWarnings("unused")
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        public boolean displayNewTags = DefaultConfig.Gui.DISPLAY_NEW_TAGS;
        static { GuiTweak.DISPLAY_NEW_TAGS.setKey("displayNewTags"); }

        @SuppressWarnings("unused")
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        public boolean displaySidedTags = DefaultConfig.Gui.DISPLAY_SIDED_TAGS;
        static { GuiTweak.DISPLAY_SIDED_TAGS.setKey("displaySidedTags"); }

        @SuppressWarnings("unused")
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        public boolean displayTagTooltips = DefaultConfig.Gui.DISPLAY_TAG_TOOLTIPS;
        static { GuiTweak.DISPLAY_TAG_TOOLTIPS.setKey("displayTagTooltips"); }

        @SuppressWarnings("unused")
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        public boolean displayFeatureStatus = DefaultConfig.Gui.DISPLAY_FEATURE_STATUS;
        static { GuiTweak.DISPLAY_FEATURE_STATUS.setKey("displayFeatureStatus"); }

        @SuppressWarnings("unused")
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        public boolean displayCategoryTree = DefaultConfig.Gui.DISPLAY_CATEGORY_TREE;
        static { GuiTweak.DISPLAY_CATEGORY_TREE.setKey("displayCategoryTree"); }

        @SuppressWarnings("unused")
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        public String categoryTreeColor = DefaultConfig.Gui.CATEGORY_TREE_COLOR;
        static { GuiTweak.CATEGORY_TREE_COLOR.setKey("categoryTreeColor"); }

        @SuppressWarnings("unused")
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        public boolean displayRowHighlight = DefaultConfig.Gui.DISPLAY_ROW_HIGHLIGHT;
        static { GuiTweak.DISPLAY_ROW_HIGHLIGHT.setKey("displayRowHighlight"); }

        @SuppressWarnings("unused")
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        public boolean doRowHighlightFade = DefaultConfig.Gui.DO_ROW_HIGHLIGHT_FADE;
        static { GuiTweak.ROW_HIGHLIGHT_FADE.setKey("doRowHighlightFade"); }

        @SuppressWarnings("unused")
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        public String rowHighlightColor = DefaultConfig.Gui.ROW_HIGHLIGHT_COLOR;
        static { GuiTweak.ROW_HIGHLIGHT_COLOR.setKey("rowHighlightColor"); }
    }

    @TweakSide.Ignore public Map<String, Integer> custom = Maps.newHashMap();
}