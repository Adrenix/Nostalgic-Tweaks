package mod.adrenix.nostalgic.config;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.config.factory.Config;
import mod.adrenix.nostalgic.config.factory.ConfigMeta;
import mod.adrenix.nostalgic.config.factory.LoaderException;
import mod.adrenix.nostalgic.tweak.TweakValidator;
import mod.adrenix.nostalgic.tweak.config.*;
import mod.adrenix.nostalgic.tweak.enums.*;
import mod.adrenix.nostalgic.tweak.listing.ItemMap;
import mod.adrenix.nostalgic.tweak.listing.ItemSet;
import mod.adrenix.nostalgic.tweak.listing.StringSet;

/**
 * <b color=red>IMPORTANT</b>
 * <p>
 * Any update to a field name in this class that is associated with a server tweak must update its counterpart field
 * name in the server config structure class as well.
 *
 * @see mod.adrenix.nostalgic.config.ServerConfig
 */

// This class only serves as a structure definition for Gson
@SuppressWarnings("unused")
@Config(filename = NostalgicTweaks.MOD_ID)
public class ClientConfig implements ConfigMeta
{
    /* Config Metadata */

    @Override
    public void validate() throws LoaderException
    {
        new TweakValidator(true).scan(ClientConfig.class);
    }

    /* Root */

    public boolean modEnabled = ModTweak.ENABLED.fromJson("modEnabled");
    public boolean serverSideOnly = ModTweak.SERVER_SIDE_ONLY.fromJson("serverSideOnly");
    public boolean serverLogging = ModTweak.SERVER_LOGGING.fromJson("serverLogging");
    public boolean serverDebugMode = ModTweak.SERVER_DEBUG.fromJson("serverDebugMode");

    /* Client Config */

    public static class Mod
    {
        // Internal

        public StringSet favoriteTweaks = ModTweak.FAVORITE_TWEAKS.fromJson("favoriteTweaks");
        public boolean openedConfigScreen = ModTweak.OPENED_CONFIG_SCREEN.fromJson("openedConfigScreen");
        public boolean openedSupporterScreen = ModTweak.OPENED_SUPPORTER_SCREEN.fromJson("openedSupporterScreen");

        // Menu Hotkeys

        public MenuOption defaultScreen = ModTweak.DEFAULT_SCREEN.fromJson("defaultScreen");
        public int openConfigBinding = ModTweak.OPEN_CONFIG_BINDING.fromJson("openConfigBinding");
        public int fogBinding = ModTweak.FOG_BINDING.fromJson("fogBinding");

        // Config Management

        public int numberOfBackups = ModTweak.NUMBER_OF_BACKUPS.fromJson("numberOfBackups");

        // Toast Control

        public boolean showWelcomeToast = ModTweak.SHOW_WELCOME_TOAST.fromJson("showWelcomeToast");
        public boolean showHandshakeToast = ModTweak.SHOW_HANDSHAKE_TOAST.fromJson("showHandshakeToast");
        public boolean showClientToServerToast = ModTweak.SHOW_CLIENT_TO_SERVER_TOAST.fromJson("showClientToServerToast");
        public boolean showServerToClientToast = ModTweak.SHOW_SERVER_TO_CLIENT_TOAST.fromJson("showServerToClientToast");

        // Menu Visuals

        public boolean smoothScroll = ModTweak.SMOOTH_SCROLL.fromJson("smoothScroll");
        public int menuBackgroundOpacity = ModTweak.MENU_BACKGROUND_OPACITY.fromJson("menuBackgroundOpacity");

        // Menu Tags

        public boolean displayNewTags = ModTweak.DISPLAY_NEW_TAGS.fromJson("displayNewTags");
        public boolean displayTagTooltips = ModTweak.DISPLAY_TAG_TOOLTIPS.fromJson("displayTagTooltips");

        // Menu Tree

        public boolean displayCategoryTree = ModTweak.DISPLAY_CATEGORY_TREE.fromJson("displayCategoryTree");
        public int categoryTreeOpacity = ModTweak.CATEGORY_TREE_OPACITY.fromJson("categoryTreeOpacity");

        // Menu Rows

        public boolean displayRowHighlight = ModTweak.DISPLAY_ROW_HIGHLIGHT.fromJson("displayRowHighlight");
        public boolean displayRowHighlightFade = ModTweak.DISPLAY_ROW_HIGHLIGHT_FADE.fromJson("displayRowHighlightFade");
        public boolean overrideRowHighlight = ModTweak.OVERRIDE_ROW_HIGHLIGHT.fromJson("overrideRowHighlight");
        public int rowHighlightOpacity = ModTweak.ROW_HIGHLIGHT_OPACITY.fromJson("rowHighlightOpacity");
    }

    public Mod mod = new Mod();

    public static class Sound
    {
        // Ambient

        public boolean disableNetherAmbience = SoundTweak.DISABLE_NETHER_AMBIENCE.fromJson("disableNetherAmbience");
        public boolean disableWaterAmbience = SoundTweak.DISABLE_WATER_AMBIENCE.fromJson("disableWaterAmbience");

        // Bed Block

        public boolean oldBed = SoundTweak.OLD_BED.fromJson("oldBed");
        public boolean disableBedPlace = SoundTweak.DISABLE_BED_PLACE.fromJson("disableBedPlace");

        // Chest Block

        public boolean oldChest = SoundTweak.OLD_CHEST.fromJson("oldChest");
        public boolean disableChest = SoundTweak.DISABLE_CHEST.fromJson("disableChest");

        // Lava Block

        public boolean disableLavaAmbience = SoundTweak.DISABLE_LAVA_AMBIENCE.fromJson("disableLavaAmbience");
        public boolean disableLavaPop = SoundTweak.DISABLE_LAVA_POP.fromJson("disableLavaPop");

        // Blocks

        public boolean disableGrowth = SoundTweak.DISABLE_GROWTH.fromJson("disableGrowth");
        public boolean disableFurnace = SoundTweak.DISABLE_FURNACE.fromJson("disableFurnace");
        public boolean disableDoorPlace = SoundTweak.DISABLE_DOOR_PLACE.fromJson("disableDoorPlace");

        // Damage

        public boolean oldAttack = SoundTweak.OLD_ATTACK.fromJson("oldAttack");
        public boolean oldHurt = SoundTweak.OLD_HURT.fromJson("oldHurt");
        public boolean oldFall = SoundTweak.OLD_FALL.fromJson("oldFall");

        // Experience

        public boolean oldXp = SoundTweak.OLD_XP.fromJson("oldXp");
        public boolean disableXpPickup = SoundTweak.DISABLE_XP_PICKUP.fromJson("disableXpPickup");
        public boolean disableXpLevel = SoundTweak.DISABLE_XP_LEVEL.fromJson("disableXpLevel");

        // Mobs

        public boolean disableGenericSwim = SoundTweak.DISABLE_GENERIC_SWIM.fromJson("disableGenericSwim");
        public boolean disableFishSwim = SoundTweak.DISABLE_FISH_SWIM.fromJson("disableFishSwim");
        public boolean disableFishHurt = SoundTweak.DISABLE_FISH_HURT.fromJson("disableFishHurt");
        public boolean disableFishDeath = SoundTweak.DISABLE_FISH_DEATH.fromJson("disableFishDeath");
        public boolean disableSquid = SoundTweak.DISABLE_SQUID.fromJson("disableSquid");
        public boolean disableGlowSquidOther = SoundTweak.DISABLE_GLOW_SQUID_OTHER.fromJson("disableGlowSquidOther");
        public boolean disableGlowSquidAmbience = SoundTweak.DISABLE_GLOW_SQUID_AMBIENCE.fromJson("disableGlowSquidAmbience");
        public boolean oldStep = SoundTweak.OLD_STEP.fromJson("oldStep");
        public boolean ignoreModdedStep = SoundTweak.IGNORE_MODDED_STEP.fromJson("ignoreModdedStep");
    }

    public Sound sound = new Sound();

    public static class EyeCandy
    {
        // Block

        public ItemSet ambientOcclusionBlocks = CandyTweak.AMBIENT_OCCLUSION_BLOCKS.fromJson("ambientOcclusionBlocks");
        public boolean disableAllOffset = CandyTweak.DISABLE_ALL_OFFSET.fromJson("disableAllOffset");
        public boolean disableFlowerOffset = CandyTweak.DISABLE_FLOWER_OFFSET.fromJson("disableFlowerOffset");
        public MissingTexture oldMissingTexture = CandyTweak.OLD_MISSING_TEXTURE.fromJson("oldMissingTexture");

        // Hitbox Outlines

        public boolean oldStairOutline = CandyTweak.OLD_STAIR_OUTLINE.fromJson("oldStairOutline");
        public boolean oldFenceOutline = CandyTweak.OLD_FENCE_OUTLINE.fromJson("oldFenceOutline");
        public boolean oldSlabOutline = CandyTweak.OLD_SLAB_OUTLINE.fromJson("oldSlabOutline");
        public boolean oldWallOutline = CandyTweak.OLD_WALL_OUTLINE.fromJson("oldWallOutline");
        public ItemSet oldBlockOutlines = CandyTweak.OLD_BLOCK_OUTLINES.fromJson("oldBlockOutlines");

        // Chests

        public boolean oldChest = CandyTweak.OLD_CHEST.fromJson("oldChest");
        public boolean oldChestVoxel = CandyTweak.OLD_CHEST_VOXEL.fromJson("oldChestVoxel");
        public boolean oldEnderChest = CandyTweak.OLD_ENDER_CHEST.fromJson("oldEnderChest");
        public boolean oldTrappedChest = CandyTweak.OLD_TRAPPED_CHEST.fromJson("oldTrappedChest");

        // Torch

        public boolean oldTorchBrightness = CandyTweak.OLD_TORCH_BRIGHTNESS.fromJson("oldTorchBrightness");
        public boolean oldTorchModel = CandyTweak.OLD_TORCH_MODEL.fromJson("oldTorchModel");
        public boolean oldRedstoneTorchModel = CandyTweak.OLD_REDSTONE_TORCH_MODEL.fromJson("oldRedstoneTorchModel");
        public boolean oldSoulTorchModel = CandyTweak.OLD_SOUL_TORCH_MODEL.fromJson("oldSoulTorchModel");

        // Interface

        public boolean oldButtonHover = CandyTweak.OLD_BUTTON_HOVER.fromJson("oldButtonHover");
        public Hotbar oldCreativeHotbar = CandyTweak.OLD_CREATIVE_HOTBAR.fromJson("oldCreativeHotbar");

        // Window Title

        public boolean enableWindowTitle = CandyTweak.ENABLE_WINDOW_TITLE.fromJson("enableWindowTitle");
        public boolean matchVersionOverlay = CandyTweak.MATCH_VERSION_OVERLAY.fromJson("matchVersionOverlay");
        public String windowTitleText = CandyTweak.WINDOW_TITLE_TEXT.fromJson("windowTitleText");

        // Debug Screen

        public Generic oldDebug = CandyTweak.OLD_DEBUG.fromJson("oldDebug");
        public boolean debugEntityId = CandyTweak.DEBUG_ENTITY_ID.fromJson("debugEntityId");

        // Debug Chart

        public DebugChart fpsChart = CandyTweak.FPS_CHART.fromJson("fpsChart");
        public boolean showDebugTpsChart = CandyTweak.SHOW_DEBUG_TPS_CHART.fromJson("showDebugTpsChart");
        public boolean showDebugPieChart = CandyTweak.SHOW_DEBUG_PIE_CHART.fromJson("showDebugPieChart");
        public boolean oldPieChartBackground = CandyTweak.OLD_PIE_CHART_BACKGROUND.fromJson("oldPieChartBackground");

        // Debug Color

        public boolean showDebugTextShadow = CandyTweak.SHOW_DEBUG_TEXT_SHADOW.fromJson("showDebugTextShadow");
        public boolean showDebugBackground = CandyTweak.SHOW_DEBUG_BACKGROUND.fromJson("showDebugBackground");
        public String debugBackgroundColor = CandyTweak.DEBUG_BACKGROUND_COLOR.fromJson("debugBackgroundColor");

        // Debug Extra

        public boolean showDebugGpuUsage = CandyTweak.SHOW_DEBUG_GPU_USAGE.fromJson("showDebugGpuUsage");
        public boolean showDebugLightData = CandyTweak.SHOW_DEBUG_LIGHT_DATA.fromJson("showDebugLightData");
        public boolean showDebugFacingData = CandyTweak.SHOW_DEBUG_FACING_DATA.fromJson("showDebugFacingData");
        public boolean showDebugTargetData = CandyTweak.SHOW_DEBUG_TARGET_DATA.fromJson("showDebugTargetData");
        public boolean showDebugBiomeData = CandyTweak.SHOW_DEBUG_BIOME_DATA.fromJson("showDebugBiomeData");

        // Inventory Screen

        public boolean oldInventory = CandyTweak.OLD_INVENTORY.fromJson("oldInventory");
        public RecipeBook inventoryBook = CandyTweak.INVENTORY_BOOK.fromJson("inventoryBook");
        public InventoryShield inventoryShield = CandyTweak.INVENTORY_SHIELD.fromJson("inventoryShield");
        public boolean disableEmptyArmorTexture = CandyTweak.DISABLE_EMPTY_ARMOR_TEXTURE.fromJson("disableEmptyArmorTexture");
        public boolean disableEmptyShieldTexture = CandyTweak.DISABLE_EMPTY_SHIELD_TEXTURE.fromJson("disableEmptyShieldTexture");
        public boolean invertedBlockLighting = CandyTweak.INVERTED_BLOCK_LIGHTING.fromJson("invertedBlockLighting");
        public boolean invertedPlayerLighting = CandyTweak.INVERTED_PLAYER_LIGHTING.fromJson("invertedPlayerLighting");

        // GUI

        public GuiBackground oldGuiBackground = CandyTweak.OLD_GUI_BACKGROUND.fromJson("oldGuiBackground");
        public boolean customGuiBackground = CandyTweak.CUSTOM_GUI_BACKGROUND.fromJson("customGuiBackground");
        public String customTopGradient = CandyTweak.CUSTOM_TOP_GRADIENT.fromJson("customTopGradient");
        public String customBottomGradient = CandyTweak.CUSTOM_BOTTOM_GRADIENT.fromJson("customBottomGradient");

        // Loading Overlay

        public Overlay oldLoadingOverlay = CandyTweak.OLD_LOADING_OVERLAY.fromJson("oldLoadingOverlay");
        public boolean removeLoadingBar = CandyTweak.REMOVE_LOADING_BAR.fromJson("removeLoadingBar");
        public boolean oldLoadingScreens = CandyTweak.OLD_LOADING_SCREENS.fromJson("oldLoadingScreens");

        // Version Overlay

        public boolean oldVersionOverlay = CandyTweak.OLD_VERSION_OVERLAY.fromJson("oldVersionOverlay");
        public Corner oldOverlayCorner = CandyTweak.OLD_OVERLAY_CORNER.fromJson("oldOverlayCorner");
        public String oldOverlayText = CandyTweak.OLD_OVERLAY_TEXT.fromJson("oldOverlayText");

        // Pause Screen

        public PauseLayout oldPauseMenu = CandyTweak.OLD_PAUSE_MENU.fromJson("oldPauseMenu");
        public boolean includeModsOnPause = CandyTweak.INCLUDE_MODS_ON_PAUSE.fromJson("includeModsOnPause");
        public boolean removeExtraPauseButtons = CandyTweak.REMOVE_EXTRA_PAUSE_BUTTONS.fromJson("removeExtraPauseButtons");

        // Anvil Screen

        public boolean oldAnvilScreen = CandyTweak.OLD_ANVIL_SCREEN.fromJson("oldAnvilScreen");

        // Crafting Screen

        public boolean oldCraftingScreen = CandyTweak.OLD_CRAFTING_SCREEN.fromJson("oldCraftingScreen");
        public RecipeBook craftingBook = CandyTweak.CRAFTING_BOOK.fromJson("craftingBook");

        // Furnace Screen

        public boolean oldFurnaceScreen = CandyTweak.OLD_FURNACE_SCREEN.fromJson("oldFurnaceScreen");
        public RecipeBook furnaceBook = CandyTweak.FURNACE_BOOK.fromJson("furnaceBook");

        // Chat Screen

        public boolean oldChatInput = CandyTweak.OLD_CHAT_INPUT.fromJson("oldChatInput");
        public boolean oldChatBox = CandyTweak.OLD_CHAT_BOX.fromJson("oldChatBox");
        public boolean disableSignatureBoxes = CandyTweak.DISABLE_SIGNATURE_BOXES.fromJson("disableSignatureBoxes");
        public int chatOffset = CandyTweak.CHAT_OFFSET.fromJson("chatOffset");

        // Tooltips

        public boolean oldTooltipBoxes = CandyTweak.OLD_TOOLTIP_BOXES.fromJson("oldTooltipBoxes");
        public boolean oldNoItemTooltips = CandyTweak.OLD_NO_ITEM_TOOLTIPS.fromJson("oldNoItemTooltips");

        // Tooltip Parts

        public boolean showEnchantmentTip = CandyTweak.SHOW_ENCHANTMENT_TIP.fromJson("showEnchantmentTip");
        public boolean showModifierTip = CandyTweak.SHOW_MODIFIER_TIP.fromJson("showModifierTip");
        public boolean showDyeTip = CandyTweak.SHOW_DYE_TIP.fromJson("showDyeTip");

        // Items

        public boolean fixItemModelGap = CandyTweak.FIX_ITEM_MODEL_GAP.fromJson("fixItemModelGap");
        public boolean oldDamageArmorTint = CandyTweak.OLD_DAMAGE_ARMOR_TINT.fromJson("oldDamageArmorTint");
        public boolean oldItemHolding = CandyTweak.OLD_ITEM_HOLDING.fromJson("oldItemHolding");
        public ItemSet ignoredHoldingItems = CandyTweak.IGNORED_HOLDING_ITEMS.fromJson("ignoredHoldingItems");

        // Item Merging

        public int itemMergeLimit = CandyTweak.ITEM_MERGE_LIMIT.fromJson("itemMergeLimit");
        public boolean oldItemMerging = CandyTweak.OLD_ITEM_MERGING.fromJson("oldItemMerging");

        // 2D Items

        public boolean old2dColors = CandyTweak.OLD_2D_COLORS.fromJson("old2dColors");
        public boolean old2dItems = CandyTweak.OLD_2D_ITEMS.fromJson("old2dItems");
        public boolean old2dFrames = CandyTweak.OLD_2D_FRAMES.fromJson("old2dFrames");
        public boolean old2dThrownItems = CandyTweak.OLD_2D_THROWN_ITEMS.fromJson("old2dThrownItems");
        public boolean old2dEnchantedItems = CandyTweak.OLD_2D_ENCHANTED_ITEMS.fromJson("old2dEnchantedItems");
        public boolean old2dRendering = CandyTweak.OLD_2D_RENDERING.fromJson("old2dRendering");

        // Item Display

        public boolean oldDurabilityColors = CandyTweak.OLD_DURABILITY_COLORS.fromJson("oldDurabilityColors");
        public boolean oldNoSelectedItemName = CandyTweak.OLD_NO_SELECTED_ITEM_NAME.fromJson("oldNoSelectedItemName");
        public boolean oldPlainSelectedItemName = CandyTweak.OLD_PLAIN_SELECTED_ITEM_NAME.fromJson("oldPlainSelectedItemName");

        // World Lighting

        public boolean fixChunkBorderLag = CandyTweak.FIX_CHUNK_BORDER_LAG.fromJson("fixChunkBorderLag");
        public boolean disableBrightness = CandyTweak.DISABLE_BRIGHTNESS.fromJson("disableBrightness");
        public boolean disableLightFlicker = CandyTweak.DISABLE_LIGHT_FLICKER.fromJson("disableLightFlicker");
        public boolean oldClassicLighting = CandyTweak.OLD_CLASSIC_LIGHTING.fromJson("oldClassicLighting");
        public boolean oldNetherLighting = CandyTweak.OLD_NETHER_LIGHTING.fromJson("oldNetherLighting");
        public boolean oldLightRendering = CandyTweak.OLD_LIGHT_RENDERING.fromJson("oldLightRendering");
        public boolean oldLightColor = CandyTweak.OLD_LIGHT_COLOR.fromJson("oldLightColor");
        public boolean oldSmoothLighting = CandyTweak.OLD_SMOOTH_LIGHTING.fromJson("oldSmoothLighting");
        public int maxBlockLight = CandyTweak.MAX_BLOCK_LIGHT.fromJson("maxBlockLight");

        // Block Lighting

        public boolean oldLeavesLighting = CandyTweak.OLD_LEAVES_LIGHTING.fromJson("oldLeavesLighting");
        public boolean oldWaterLighting = CandyTweak.OLD_WATER_LIGHTING.fromJson("oldWaterLighting");

        // Particles

        public boolean oldOpaqueExperience = CandyTweak.OLD_OPAQUE_EXPERIENCE.fromJson("oldOpaqueExperience");
        public boolean disableNetherParticles = CandyTweak.DISABLE_NETHER_PARTICLES.fromJson("disableNetherParticles");
        public boolean disableUnderwaterParticles = CandyTweak.DISABLE_UNDERWATER_PARTICLES.fromJson("disableUnderwaterParticles");

        // Block Particles

        public boolean disableLavaParticles = CandyTweak.DISABLE_LAVA_PARTICLES.fromJson("disableLavaParticles");
        public boolean disableLeverParticles = CandyTweak.DISABLE_LEVER_PARTICLES.fromJson("disableLeverParticles");
        public boolean disableModelDestructionParticles = CandyTweak.DISABLE_MODEL_DESTRUCTION_PARTICLES.fromJson("disableModelDestructionParticles");
        public boolean disableGrowthParticles = CandyTweak.DISABLE_GROWTH_PARTICLES.fromJson("disableGrowthParticles");

        // Player Particles

        public boolean disableFallingParticles = CandyTweak.DISABLE_FALLING_PARTICLES.fromJson("disableFallingParticles");
        public boolean disableSprintingParticles = CandyTweak.DISABLE_SPRINTING_PARTICLES.fromJson("disableSprintingParticles");

        // Attack Particles

        public boolean oldSweepParticles = CandyTweak.OLD_SWEEP_PARTICLES.fromJson("oldSweepParticles");
        public boolean oldNoDamageParticles = CandyTweak.OLD_NO_DAMAGE_PARTICLES.fromJson("oldNoDamageParticles");
        public boolean oldNoCritParticles = CandyTweak.OLD_NO_CRIT_PARTICLES.fromJson("oldNoCritParticles");
        public boolean oldNoMagicHitParticles = CandyTweak.OLD_NO_MAGIC_HIT_PARTICLES.fromJson("oldNoMagicHitParticles");

        // Explosion Particles

        public boolean oldExplosionParticles = CandyTweak.OLD_EXPLOSION_PARTICLES.fromJson("oldExplosionParticles");
        public boolean oldMixedExplosionParticles = CandyTweak.OLD_MIXED_EXPLOSION_PARTICLES.fromJson("oldMixedExplosionParticles");
        public boolean unoptimizedExplosionParticles = CandyTweak.UNOPTIMIZED_EXPLOSION_PARTICLES.fromJson("unoptimizedExplosionParticles");

        // Title Screen

        public boolean overrideTitleScreen = CandyTweak.OVERRIDE_TITLE_SCREEN.fromJson("overrideTitleScreen");
        public boolean oldTitleBackground = CandyTweak.OLD_TITLE_BACKGROUND.fromJson("oldTitleBackground");
        public boolean uncapTitleFPS = CandyTweak.UNCAP_TITLE_FPS.fromJson("uncapTitleFPS");

        // Title Screen Logo

        public boolean oldAlphaLogo = CandyTweak.OLD_ALPHA_LOGO.fromJson("oldAlphaLogo");
        public boolean oldLogoOutline = CandyTweak.OLD_LOGO_OUTLINE.fromJson("oldLogoOutline");

        // Title Screen Buttons

        public TitleLayout oldButtonLayout = CandyTweak.OLD_BUTTON_LAYOUT.fromJson("oldButtonLayout");
        public boolean includeModsOnTitle = CandyTweak.INCLUDE_MODS_ON_TITLE.fromJson("includeModsOnTitle");
        public boolean removeTitleRealmsButton = CandyTweak.REMOVE_TITLE_REALMS_BUTTON.fromJson("removeTitleRealmsButton");
        public boolean removeTitleAccessibilityButton = CandyTweak.REMOVE_TITLE_ACCESSIBILITY_BUTTON.fromJson("removeTitleAccessibilityButton");
        public boolean removeTitleLanguageButton = CandyTweak.REMOVE_TITLE_LANGUAGE_BUTTON.fromJson("removeTitleLanguageButton");

        // Title Screen Text

        public String titleVersionText = CandyTweak.TITLE_VERSION_TEXT.fromJson("titleVersionText");
        public boolean titleBottomLeftText = CandyTweak.TITLE_BOTTOM_LEFT_TEXT.fromJson("titleBottomLeftText");
        public boolean removeTitleModLoaderText = CandyTweak.REMOVE_TITLE_MOD_LOADER_TEXT.fromJson("removeTitleModLoaderText");

        // World

        public boolean oldSquareBorder = CandyTweak.OLD_SQUARE_BORDER.fromJson("oldSquareBorder");
        public boolean oldNameTags = CandyTweak.OLD_NAME_TAGS.fromJson("oldNameTags");

        // World Fog

        public WorldFog oldWorldFog = CandyTweak.OLD_WORLD_FOG.fromJson("oldWorldFog");
        public boolean disableHorizonFog = CandyTweak.DISABLE_HORIZON_FOG.fromJson("disableHorizonFog");
        public boolean oldNetherFog = CandyTweak.OLD_NETHER_FOG.fromJson("oldNetherFog");
        public boolean oldSunriseSunsetFog = CandyTweak.OLD_SUNRISE_SUNSET_FOG.fromJson("oldSunriseSunsetFog");
        public boolean oldDarkFog = CandyTweak.OLD_DARK_FOG.fromJson("oldDarkFog");
        public boolean oldDynamicFogColor = CandyTweak.OLD_DYNAMIC_FOG_COLOR.fromJson("oldDynamicFogColor");
        public FogColor universalFogColor = CandyTweak.UNIVERSAL_FOG_COLOR.fromJson("universalFogColor");

        // Custom World Fog

        public boolean customTerrainFog = CandyTweak.CUSTOM_TERRAIN_FOG.fromJson("customTerrainFog");
        public String customTerrainFogColor = CandyTweak.CUSTOM_TERRAIN_FOG_COLOR.fromJson("customTerrainFogColor");
        public boolean customNetherFog = CandyTweak.CUSTOM_NETHER_FOG.fromJson("customNetherFog");
        public String customNetherFogColor = CandyTweak.CUSTOM_NETHER_FOG_COLOR.fromJson("customNetherFogColor");

        // Water Fog

        public boolean oldWaterFogDensity = CandyTweak.OLD_WATER_FOG_DENSITY.fromJson("oldWaterFogDensity");
        public boolean oldWaterFogColor = CandyTweak.OLD_WATER_FOG_COLOR.fromJson("oldWaterFogColor");
        public boolean smoothWaterDensity = CandyTweak.SMOOTH_WATER_DENSITY.fromJson("smoothWaterDensity");
        public boolean smoothWaterColor = CandyTweak.SMOOTH_WATER_COLOR.fromJson("smoothWaterColor");

        // World Sky

        public boolean disableSunriseSunsetColors = CandyTweak.DISABLE_SUNRISE_SUNSET_COLORS.fromJson("disableSunriseSunsetColors");
        public boolean oldSunriseAtNorth = CandyTweak.OLD_SUNRISE_AT_NORTH.fromJson("oldSunriseAtNorth");
        public Generic oldStars = CandyTweak.OLD_STARS.fromJson("oldStars");
        public boolean oldDynamicSkyColor = CandyTweak.OLD_DYNAMIC_SKY_COLOR.fromJson("oldDynamicSkyColor");
        public SkyColor universalSkyColor = CandyTweak.UNIVERSAL_SKY_COLOR.fromJson("universalSkyColor");
        public boolean oldNetherSky = CandyTweak.OLD_NETHER_SKY.fromJson("oldNetherSky");
        public int oldCloudHeight = CandyTweak.OLD_CLOUD_HEIGHT.fromJson("oldCloudHeight");

        // Custom World Sky

        public boolean customWorldSky = CandyTweak.CUSTOM_WORLD_SKY.fromJson("customWorldSky");
        public String customWorldSkyColor = CandyTweak.CUSTOM_WORLD_SKY_COLOR.fromJson("customWorldSkyColor");
        public boolean customNetherSky = CandyTweak.CUSTOM_NETHER_SKY.fromJson("customNetherSky");
        public String customNetherSkyColor = CandyTweak.CUSTOM_NETHER_SKY_COLOR.fromJson("customNetherSkyColor");

        // Void Sky

        public Generic oldBlueVoid = CandyTweak.OLD_BLUE_VOID.fromJson("oldBlueVoid");
        public boolean oldBlueVoidOverride = CandyTweak.OLD_BLUE_VOID_OVERRIDE.fromJson("oldBlueVoidOverride");
        public boolean oldDarkVoidHeight = CandyTweak.OLD_DARK_VOID_HEIGHT.fromJson("oldDarkVoidHeight");
        public boolean customVoidSky = CandyTweak.CUSTOM_VOID_SKY.fromJson("customVoidSky");
        public String customVoidSkyColor = CandyTweak.CUSTOM_VOID_SKY_COLOR.fromJson("customVoidSkyColor");

        // Void Fog

        public boolean disableVoidFog = CandyTweak.DISABLE_VOID_FOG.fromJson("disableVoidFog");
        public boolean creativeVoidFog = CandyTweak.CREATIVE_VOID_FOG.fromJson("creativeVoidFog");
        public boolean creativeVoidParticles = CandyTweak.CREATIVE_VOID_PARTICLES.fromJson("creativeVoidParticles");
        public boolean lightRemovesVoidFog = CandyTweak.LIGHT_REMOVES_VOID_FOG.fromJson("lightRemovesVoidFog");
        public String voidFogColor = CandyTweak.VOID_FOG_COLOR.fromJson("voidFogColor");
        public int voidFogEncroach = CandyTweak.VOID_FOG_ENCROACH.fromJson("voidFogEncroach");
        public int voidFogStart = CandyTweak.VOID_FOG_START.fromJson("voidFogStart");
        public int voidParticleStart = CandyTweak.VOID_PARTICLE_START.fromJson("voidParticleStart");
        public int voidParticleRadius = CandyTweak.VOID_PARTICLE_RADIUS.fromJson("voidParticleRadius");
        public int voidParticleDensity = CandyTweak.VOID_PARTICLE_DENSITY.fromJson("voidParticleDensity");
    }

    public EyeCandy eyeCandy = new EyeCandy();

    public static class Gameplay
    {
        // Bugs

        public boolean oldLadderGap = GameplayTweak.OLD_LADDER_GAP.fromJson("oldLadderGap");
        public boolean oldSquidMilking = GameplayTweak.OLD_SQUID_MILKING.fromJson("oldSquidMilking");

        // Mob AI

        public boolean disableAnimalPanic = GameplayTweak.DISABLE_ANIMAL_PANIC.fromJson("disableAnimalPanic");

        // Mob Spawning

        public int animalSpawnCap = GameplayTweak.ANIMAL_SPAWN_CAP.fromJson("animalSpawnCap");
        public boolean oldAnimalSpawning = GameplayTweak.OLD_ANIMAL_SPAWNING.fromJson("oldAnimalSpawning");

        // Sheep

        public boolean disableSheepEatGrass = GameplayTweak.DISABLE_SHEEP_EAT_GRASS.fromJson("disableSheepEatGrass");
        public boolean oldSheepPunching = GameplayTweak.OLD_SHEEP_PUNCHING.fromJson("oldSheepPunching");
        public boolean oneWoolPunch = GameplayTweak.ONE_WOOL_PUNCH.fromJson("oneWoolPunch");

        // Classic Mob Drops

        public boolean oldZombiePigmenDrops = GameplayTweak.OLD_ZOMBIE_PIGMEN_DROPS.fromJson("oldZombiePigmenDrops");
        public boolean oldSkeletonDrops = GameplayTweak.OLD_SKELETON_DROPS.fromJson("oldSkeletonDrops");
        public boolean oldChickenDrops = GameplayTweak.OLD_CHICKEN_DROPS.fromJson("oldChickenDrops");
        public boolean oldZombieDrops = GameplayTweak.OLD_ZOMBIE_DROPS.fromJson("oldZombieDrops");
        public boolean oldSpiderDrops = GameplayTweak.OLD_SPIDER_DROPS.fromJson("oldSpiderDrops");
        public boolean oldSheepDrops = GameplayTweak.OLD_SHEEP_DROPS.fromJson("oldSheepDrops");
        public boolean oldCowDrops = GameplayTweak.OLD_COW_DROPS.fromJson("oldCowDrops");
        public boolean oldPigDrops = GameplayTweak.OLD_PIG_DROPS.fromJson("oldPigDrops");

        // Modern Mob Drops

        public boolean oldStyleZombieVillagerDrops = GameplayTweak.OLD_STYLE_ZOMBIE_VILLAGER_DROPS.fromJson("oldStyleZombieVillagerDrops");
        public boolean oldStyleCaveSpiderDrops = GameplayTweak.OLD_STYLE_CAVE_SPIDER_DROPS.fromJson("oldStyleCaveSpiderDrops");
        public boolean oldStyleMooshroomDrops = GameplayTweak.OLD_STYLE_MOOSHROOM_DROPS.fromJson("oldStyleMooshroomDrops");
        public boolean oldStyleDrownedDrops = GameplayTweak.OLD_STYLE_DROWNED_DROPS.fromJson("oldStyleDrownedDrops");
        public boolean oldStyleRabbitDrops = GameplayTweak.OLD_STYLE_RABBIT_DROPS.fromJson("oldStyleRabbitDrops");
        public boolean oldStyleStrayDrops = GameplayTweak.OLD_STYLE_STRAY_DROPS.fromJson("oldStyleStrayDrops");
        public boolean oldStyleHuskDrops = GameplayTweak.OLD_STYLE_HUSK_DROPS.fromJson("oldStyleHuskDrops");

        // Combat

        public boolean oldDamageValues = GameplayTweak.OLD_DAMAGE_VALUES.fromJson("oldDamageValues");
        public boolean disableCooldown = GameplayTweak.DISABLE_COOLDOWN.fromJson("disableCooldown");
        public boolean disableMissTimer = GameplayTweak.DISABLE_MISS_TIMER.fromJson("disableMissTimer");
        public boolean disableCriticalHit = GameplayTweak.DISABLE_CRITICAL_HIT.fromJson("disableCriticalHit");
        public boolean disableSweep = GameplayTweak.DISABLE_SWEEP.fromJson("disableSweep");

        // Combat Bow

        public int arrowSpeed = GameplayTweak.ARROW_SPEED.fromJson("arrowSpeed");
        public boolean instantBow = GameplayTweak.INSTANT_BOW.fromJson("instantBow");
        public boolean invincibleBow = GameplayTweak.INVINCIBLE_BOW.fromJson("invincibleBow");

        // Experience Bar

        public boolean disableExperienceBar = GameplayTweak.DISABLE_EXPERIENCE_BAR.fromJson("disableExperienceBar");

        // Alternative Experience Text

        public boolean showXpLevelText = GameplayTweak.SHOW_XP_LEVEL_TEXT.fromJson("showXpLevelText");
        public boolean showXpLevelInCreative = GameplayTweak.SHOW_XP_LEVEL_IN_CREATIVE.fromJson("showXpLevelInCreative");
        public Corner altXpLevelCorner = GameplayTweak.ALT_XP_LEVEL_CORNER.fromJson("altXpLevelCorner");
        public String altXpLevelText = GameplayTweak.ALT_XP_LEVEL_TEXT.fromJson("altXpLevelText");

        // Alternative Progress Text

        public boolean showXpProgressText = GameplayTweak.SHOW_XP_PROGRESS_TEXT.fromJson("showXpProgressText");
        public boolean showXpProgressInCreative = GameplayTweak.SHOW_XP_PROGRESS_IN_CREATIVE.fromJson("showXpProgressInCreative");
        public boolean useDynamicProgressColor = GameplayTweak.USE_DYNAMIC_PROGRESS_COLOR.fromJson("useDynamicProgressColor");
        public Corner altXpProgressCorner = GameplayTweak.ALT_XP_PROGRESS_CORNER.fromJson("altXpProgressCorner");
        public String altXpProgressText = GameplayTweak.ALT_XP_PROGRESS_TEXT.fromJson("altXpProgressText");

        // Experience Orb

        public boolean disableOrbSpawn = GameplayTweak.DISABLE_ORB_SPAWN.fromJson("disableOrbSpawn");
        public boolean disableOrbRendering = GameplayTweak.DISABLE_ORB_RENDERING.fromJson("disableOrbRendering");

        // Experience Blocks

        public boolean disableAnvil = GameplayTweak.DISABLE_ANVIL.fromJson("disableAnvil");
        public boolean disableEnchantTable = GameplayTweak.DISABLE_ENCHANT_TABLE.fromJson("disableEnchantTable");

        // Player Mechanics

        public boolean disableSprint = GameplayTweak.DISABLE_SPRINT.fromJson("disableSprint");
        public boolean leftClickDoor = GameplayTweak.LEFT_CLICK_DOOR.fromJson("leftClickDoor");
        public boolean leftClickLever = GameplayTweak.LEFT_CLICK_LEVER.fromJson("leftClickLever");
        public boolean leftClickButton = GameplayTweak.LEFT_CLICK_BUTTON.fromJson("leftClickButton");

        // Farming Mechanics

        public boolean instantBonemeal = GameplayTweak.INSTANT_BONEMEAL.fromJson("instantBonemeal");
        public boolean tilledGrassSeeds = GameplayTweak.TILLED_GRASS_SEEDS.fromJson("tilledGrassSeeds");

        // Fire Mechanics

        public boolean oldFire = GameplayTweak.OLD_FIRE.fromJson("oldFire");
        public boolean infiniteBurn = GameplayTweak.INFINITE_BURN.fromJson("infiniteBurn");

        // Swimming Mechanics

        public boolean instantAir = GameplayTweak.INSTANT_AIR.fromJson("instantAir");
        public boolean disableSwim = GameplayTweak.DISABLE_SWIM.fromJson("disableSwim");

        // Minecart Mechanics

        public boolean cartBoosting = GameplayTweak.CART_BOOSTING.fromJson("cartBoosting");

        // Block Mechanics

        public boolean disableBedBounce = GameplayTweak.DISABLE_BED_BOUNCE.fromJson("disableBedBounce");

        // Hunger Bar

        public boolean disableHungerBar = GameplayTweak.DISABLE_HUNGER_BAR.fromJson("disableHungerBar");

        // Alternative Food Text

        public boolean showHungerFoodText = GameplayTweak.SHOW_HUNGER_FOOD_TEXT.fromJson("showHungerFoodText");
        public boolean useDynamicFoodColor = GameplayTweak.USE_DYNAMIC_FOOD_COLOR.fromJson("useDynamicFoodColor");
        public Corner altHungerFoodCorner = GameplayTweak.ALT_HUNGER_FOOD_CORNER.fromJson("altHungerFoodCorner");
        public String altHungerFoodText = GameplayTweak.ALT_HUNGER_FOOD_TEXT.fromJson("altHungerFoodText");

        // Alternative Saturation Text

        public boolean showHungerSaturationText = GameplayTweak.SHOW_HUNGER_SATURATION_TEXT.fromJson("showHungerSaturationText");
        public boolean useDynamicSaturationColor = GameplayTweak.USE_DYNAMIC_SATURATION_COLOR.fromJson("useDynamicSaturationColor");
        public Corner altHungerSaturationCorner = GameplayTweak.ALT_HUNGER_SATURATION_CORNER.fromJson("altHungerSaturationCorner");
        public String altHungerSaturationText = GameplayTweak.ALT_HUNGER_SATURATION_TEXT.fromJson("altHungerSaturationText");

        // Food

        public boolean instantEat = GameplayTweak.INSTANT_EAT.fromJson("instantEat");
        public boolean disableHunger = GameplayTweak.DISABLE_HUNGER.fromJson("disableHunger");
        public ItemMap<Integer> customFoodHealth = GameplayTweak.CUSTOM_FOOD_HEALTH.fromJson("customFoodHealth");
        public boolean oldFoodStacking = GameplayTweak.OLD_FOOD_STACKING.fromJson("oldFoodStacking");
        public ItemMap<Integer> customFoodStacking = GameplayTweak.CUSTOM_FOOD_STACKING.fromJson("customFoodStacking");
        public ItemMap<Integer> customItemStacking = GameplayTweak.CUSTOM_ITEM_STACKING.fromJson("customItemStacking");
    }

    public Gameplay gameplay = new Gameplay();

    public static class Animation
    {
        // Arm

        public boolean oldArmSway = AnimationTweak.OLD_ARM_SWAY.fromJson("oldArmSway");
        public boolean armSwayMirror = AnimationTweak.ARM_SWAY_MIRROR.fromJson("armSwayMirror");
        public int armSwayIntensity = AnimationTweak.ARM_SWAY_INTENSITY.fromJson("armSwayIntensity");
        public boolean oldSwing = AnimationTweak.OLD_SWING.fromJson("oldSwing");
        public boolean oldSwingInterrupt = AnimationTweak.OLD_SWING_INTERRUPT.fromJson("oldSwingInterrupt");
        public boolean oldSwingDropping = AnimationTweak.OLD_SWING_DROPPING.fromJson("oldSwingDropping");
        public boolean oldClassicSwing = AnimationTweak.OLD_CLASSIC_SWING.fromJson("oldClassicSwing");

        // Item

        public boolean oldItemCooldown = AnimationTweak.OLD_ITEM_COOLDOWN.fromJson("oldItemCooldown");
        public boolean oldItemReequip = AnimationTweak.OLD_ITEM_REEQUIP.fromJson("oldItemReequip");
        public boolean oldToolExplosion = AnimationTweak.OLD_TOOL_EXPLOSION.fromJson("oldToolExplosion");

        // Mob

        public boolean oldZombieArms = AnimationTweak.OLD_ZOMBIE_ARMS.fromJson("oldZombieArms");
        public boolean oldSkeletonArms = AnimationTweak.OLD_SKELETON_ARMS.fromJson("oldSkeletonArms");
        public boolean oldGhastCharging = AnimationTweak.OLD_GHAST_CHARGING.fromJson("oldGhastCharging");

        // Player

        public boolean oldBackwardWalking = AnimationTweak.OLD_BACKWARD_WALKING.fromJson("oldBackwardWalking");
        public boolean oldCollideBobbing = AnimationTweak.OLD_COLLIDE_BOBBING.fromJson("oldCollideBobbing");
        public boolean oldVerticalBobbing = AnimationTweak.OLD_VERTICAL_BOBBING.fromJson("oldVerticalBobbing");
        public boolean oldCreativeCrouch = AnimationTweak.OLD_CREATIVE_CROUCH.fromJson("oldCreativeCrouch");
        public boolean oldDirectionalDamage = AnimationTweak.OLD_DIRECTIONAL_DAMAGE.fromJson("oldDirectionalDamage");
        public boolean oldRandomDamage = AnimationTweak.OLD_RANDOM_DAMAGE.fromJson("oldRandomDamage");
        public boolean oldSneaking = AnimationTweak.OLD_SNEAKING.fromJson("oldSneaking");
        public boolean disableDeathTopple = AnimationTweak.DISABLE_DEATH_TOPPLE.fromJson("disableDeathTopple");
    }

    public Animation animation = new Animation();

    public static class Swing
    {
        // Global Speeds

        public boolean overrideSpeeds = SwingTweak.OVERRIDE_SPEEDS.fromJson("overrideSpeeds");
        public boolean leftClickSpeedOnBlockInteract = SwingTweak.LEFT_CLICK_SPEED_ON_BLOCK_INTERACT.fromJson("leftClickSpeedOnBlockInteract");
        public int leftGlobalSpeed = SwingTweak.LEFT_GLOBAL_SPEED.fromJson("leftGlobalSpeed");
        public int rightGlobalSpeed = SwingTweak.RIGHT_GLOBAL_SPEED.fromJson("rightGlobalSpeed");

        // Item Speeds

        public int leftItemSpeed = SwingTweak.LEFT_ITEM_SPEED.fromJson("leftItemSpeed");
        public int rightItemSpeed = SwingTweak.RIGHT_ITEM_SPEED.fromJson("rightItemSpeed");
        public int leftToolSpeed = SwingTweak.LEFT_TOOL_SPEED.fromJson("leftToolSpeed");
        public int rightToolSpeed = SwingTweak.RIGHT_TOOL_SPEED.fromJson("rightToolSpeed");
        public int leftBlockSpeed = SwingTweak.LEFT_BLOCK_SPEED.fromJson("leftBlockSpeed");
        public int rightBlockSpeed = SwingTweak.RIGHT_BLOCK_SPEED.fromJson("rightBlockSpeed");
        public int leftSwordSpeed = SwingTweak.LEFT_SWORD_SPEED.fromJson("leftSwordSpeed");
        public int rightSwordSpeed = SwingTweak.RIGHT_SWORD_SPEED.fromJson("rightSwordSpeed");

        // Potion Speeds

        public int leftHasteSpeed = SwingTweak.LEFT_HASTE_SPEED.fromJson("leftHasteSpeed");
        public int rightHasteSpeed = SwingTweak.RIGHT_HASTE_SPEED.fromJson("rightHasteSpeed");
        public int leftFatigueSpeed = SwingTweak.LEFT_FATIGUE_SPEED.fromJson("leftFatigueSpeed");
        public int rightFatigueSpeed = SwingTweak.RIGHT_FATIGUE_SPEED.fromJson("rightFatigueSpeed");

        // Custom Speeds

        public ItemMap<Integer> leftClickSwingSpeeds = SwingTweak.LEFT_CLICK_SWING_SPEEDS.fromJson("leftClickSwingSpeeds");
        public ItemMap<Integer> rightClickSwingSpeeds = SwingTweak.RIGHT_CLICK_SWING_SPEEDS.fromJson("rightClickSwingSpeeds");
    }

    public Swing swing = new Swing();
}
