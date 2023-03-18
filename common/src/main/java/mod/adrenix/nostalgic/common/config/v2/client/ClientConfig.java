package mod.adrenix.nostalgic.common.config.v2.client;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.gui.screen.MenuOption;
import mod.adrenix.nostalgic.common.config.ValidateConfig;
import mod.adrenix.nostalgic.common.config.auto.ConfigData;
import mod.adrenix.nostalgic.common.config.auto.annotation.Config;
import mod.adrenix.nostalgic.common.config.tweak.TweakType;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;
import mod.adrenix.nostalgic.common.config.v2.tweak.*;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * IMPORTANT:
 *
 * Any update to a field name in this class that is associated with a server tweak also needs updated in the server
 * config class as well.
 *
 * @see mod.adrenix.nostalgic.server.config.ServerConfig
 */

@SuppressWarnings("unused") // This class only serves as a structure definition for the config JSON.
@Config(name = "v2" + NostalgicTweaks.MOD_ID)
public class ClientConfig implements ConfigData
{
    /* Config Validation */

    @Override
    public void validatePostLoad() throws ValidationException
    {
        ValidateConfig.scan(this);
    }

    /* Global Mod State */

    public boolean isModEnabled = ModTweak.ENABLED.setAndGet("isModEnabled");

    /* Client Config */

    public static class Sound
    {
        // Ambient

        public boolean disableNetherAmbience = SoundTweak.DISABLE_NETHER_AMBIENCE.setAndGet("disableNetherAmbience");
        public boolean disableWaterAmbience = SoundTweak.DISABLE_WATER_AMBIENCE.setAndGet("disableWaterAmbience");

        // Bed Block

        public boolean oldBed = SoundTweak.OLD_BED.setAndGet("oldBed");
        public boolean disableBedPlace = SoundTweak.DISABLE_BED_PLACE.setAndGet("disableBedPlace");

        // Chest Block

        public boolean oldChest = SoundTweak.OLD_CHEST.setAndGet("oldChest");
        public boolean disableChest = SoundTweak.DISABLE_CHEST.setAndGet("disableChest");

        // Lava Block

        public boolean disableLavaAmbience = SoundTweak.DISABLE_LAVA_AMBIENCE.setAndGet("disableLavaAmbience");
        public boolean disableLavaPop = SoundTweak.DISABLE_LAVA_POP.setAndGet("disableLavaPop");

        // Blocks

        public boolean disableGrowth = SoundTweak.DISABLE_GROWTH.setAndGet("disableGrowth");
        public boolean disableFurnace = SoundTweak.DISABLE_FURNACE.setAndGet("disableFurnace");
        public boolean disableDoorPlace = SoundTweak.DISABLE_DOOR_PLACE.setAndGet("disableDoorPlace");

        // Damage

        public boolean oldAttack = SoundTweak.OLD_ATTACK.setAndGet("oldAttack");
        public boolean oldHurt = SoundTweak.OLD_HURT.setAndGet("oldHurt");
        public boolean oldFall = SoundTweak.OLD_FALL.setAndGet("oldFall");

        // Experience

        public boolean oldXp = SoundTweak.OLD_XP.setAndGet("oldXp");
        public boolean disableXpPickup = SoundTweak.DISABLE_XP_PICKUP.setAndGet("disableXpPickup");
        public boolean disableXpLevel = SoundTweak.DISABLE_XP_LEVEL.setAndGet("disableXpLevel");

        // Mobs

        public boolean disableGenericSwim = SoundTweak.DISABLE_GENERIC_SWIM.setAndGet("disableGenericSwim");
        public boolean disableFishSwim = SoundTweak.DISABLE_FISH_SWIM.setAndGet("disableFishSwim");
        public boolean disableFishHurt = SoundTweak.DISABLE_FISH_HURT.setAndGet("disableFishHurt");
        public boolean disableFishDeath = SoundTweak.DISABLE_FISH_DEATH.setAndGet("disableFishDeath");
        public boolean disableSquid = SoundTweak.DISABLE_SQUID.setAndGet("disableSquid");
        public boolean disableGlowSquidOther = SoundTweak.DISABLE_GLOW_SQUID_OTHER.setAndGet("disableGlowSquidOther");
        public boolean disableGlowSquidAmbience = SoundTweak.DISABLE_GLOW_SQUID_AMBIENCE.setAndGet("disableGlowSquidAmbience");
        public boolean oldStep = SoundTweak.OLD_STEP.setAndGet("oldStep");
        public boolean ignoreModdedStep = SoundTweak.IGNORE_MODDED_STEP.setAndGet("ignoreModdedStep");
    }

    public Sound sound = new Sound();

    public static class EyeCandy
    {
        // Block

        public boolean fixAmbientOcclusion = CandyTweak.FIX_AMBIENT_OCCLUSION.setAndGet("fixAmbientOcclusion");
        public boolean disableAllOffset = CandyTweak.DISABLE_ALL_OFFSET.setAndGet("disableAllOffset");
        public boolean disableFlowerOffset = CandyTweak.DISABLE_FLOWER_OFFSET.setAndGet("disableFlowerOffset");
        public TweakVersion.MissingTexture oldMissingTexture = CandyTweak.OLD_MISSING_TEXTURE.setAndGet("oldMissingTexture");

        // Hitbox Outlines

        public boolean oldStairOutline = CandyTweak.OLD_STAIR_OUTLINE.setAndGet("oldStairOutline");
        public boolean oldFenceOutline = CandyTweak.OLD_FENCE_OUTLINE.setAndGet("oldFenceOutline");
        public boolean oldSlabOutline = CandyTweak.OLD_SLAB_OUTLINE.setAndGet("oldSlabOutline");
        public boolean oldWallOutline = CandyTweak.OLD_WALL_OUTLINE.setAndGet("oldWallOutline");
        public HashSet<String> oldBlockOutlines = CandyTweak.OLD_BLOCK_OUTLINES.setAndGet("oldBlockOutlines");

        // Chests

        public boolean oldChest = CandyTweak.OLD_CHEST.setAndGet("oldChest");
        public boolean oldChestVoxel = CandyTweak.OLD_CHEST_VOXEL.setAndGet("oldChestVoxel");
        public boolean oldEnderChest = CandyTweak.OLD_ENDER_CHEST.setAndGet("oldEnderChest");
        public boolean oldTrappedChest = CandyTweak.OLD_TRAPPED_CHEST.setAndGet("oldTrappedChest");

        // Torch

        public boolean oldTorchBrightness = CandyTweak.OLD_TORCH_BRIGHTNESS.setAndGet("oldTorchBrightness");
        public boolean oldTorchModel = CandyTweak.OLD_TORCH_MODEL.setAndGet("oldTorchModel");
        public boolean oldRedstoneTorchModel = CandyTweak.OLD_REDSTONE_TORCH_MODEL.setAndGet("oldRedstoneTorchModel");
        public boolean oldSoulTorchModel = CandyTweak.OLD_SOUL_TORCH_MODEL.setAndGet("oldSoulTorchModel");

        // Interface

        public boolean oldButtonHover = CandyTweak.OLD_BUTTON_HOVER.setAndGet("oldButtonHover");
        public TweakVersion.Hotbar oldCreativeHotbar = CandyTweak.OLD_CREATIVE_HOTBAR.setAndGet("oldCreativeHotbar");

        // Window Title

        public boolean enableWindowTitle = CandyTweak.ENABLE_WINDOW_TITLE.setAndGet("enableWindowTitle");
        public boolean matchVersionOverlay = CandyTweak.MATCH_VERSION_OVERLAY.setAndGet("matchVersionOverlay");
        public String windowTitleText = CandyTweak.WINDOW_TITLE_TEXT.setAndGet("windowTitleText");

        // Debug Screen

        public TweakVersion.Generic oldDebug = CandyTweak.OLD_DEBUG.setAndGet("oldDebug");
        public boolean debugEntityId = CandyTweak.DEBUG_ENTITY_ID.setAndGet("debugEntityId");

        // Debug Chart

        public TweakType.DebugChart fpsChart = CandyTweak.FPS_CHART.setAndGet("fpsChart");
        public boolean showDebugTpsChart = CandyTweak.SHOW_DEBUG_TPS_CHART.setAndGet("showDebugTpsChart");
        public boolean showDebugPieChart = CandyTweak.SHOW_DEBUG_PIE_CHART.setAndGet("showDebugPieChart");
        public boolean oldPieChartBackground = CandyTweak.OLD_PIE_CHART_BACKGROUND.setAndGet("oldPieChartBackground");

        // Debug Color

        public boolean showDebugTextShadow = CandyTweak.SHOW_DEBUG_TEXT_SHADOW.setAndGet("showDebugTextShadow");
        public boolean showDebugBackground = CandyTweak.SHOW_DEBUG_BACKGROUND.setAndGet("showDebugBackground");
        public String debugBackgroundColor = CandyTweak.DEBUG_BACKGROUND_COLOR.setAndGet("debugBackgroundColor");

        // Debug Extra

        public boolean showDebugGpuUsage = CandyTweak.SHOW_DEBUG_GPU_USAGE.setAndGet("showDebugGpuUsage");
        public boolean showDebugLightData = CandyTweak.SHOW_DEBUG_LIGHT_DATA.setAndGet("showDebugLightData");
        public boolean showDebugFacingData = CandyTweak.SHOW_DEBUG_FACING_DATA.setAndGet("showDebugFacingData");
        public boolean showDebugTargetData = CandyTweak.SHOW_DEBUG_TARGET_DATA.setAndGet("showDebugTargetData");
        public boolean showDebugBiomeData = CandyTweak.SHOW_DEBUG_BIOME_DATA.setAndGet("showDebugBiomeData");

        // Inventory Screen

        public boolean oldInventory = CandyTweak.OLD_INVENTORY.setAndGet("oldInventory");
        public TweakType.RecipeBook inventoryBook = CandyTweak.INVENTORY_BOOK.setAndGet("inventoryBook");
        public TweakType.InventoryShield inventoryShield = CandyTweak.INVENTORY_SHIELD.setAndGet("inventoryShield");
        public boolean disableEmptyArmorTexture = CandyTweak.DISABLE_EMPTY_ARMOR_TEXTURE.setAndGet("disableEmptyArmorTexture");
        public boolean disableEmptyShieldTexture = CandyTweak.DISABLE_EMPTY_SHIELD_TEXTURE.setAndGet("disableEmptyShieldTexture");
        public boolean invertedBlockLighting = CandyTweak.INVERTED_BLOCK_LIGHTING.setAndGet("invertedBlockLighting");
        public boolean invertedPlayerLighting = CandyTweak.INVERTED_PLAYER_LIGHTING.setAndGet("invertedPlayerLighting");

        // GUI

        public TweakType.GuiBackground oldGuiBackground = CandyTweak.OLD_GUI_BACKGROUND.setAndGet("oldGuiBackground");
        public boolean customGuiBackground = CandyTweak.CUSTOM_GUI_BACKGROUND.setAndGet("customGuiBackground");
        public String customTopGradient = CandyTweak.CUSTOM_TOP_GRADIENT.setAndGet("customTopGradient");
        public String customBottomGradient = CandyTweak.CUSTOM_BOTTOM_GRADIENT.setAndGet("customBottomGradient");

        // Loading Overlay

        public TweakVersion.Overlay oldLoadingOverlay = CandyTweak.OLD_LOADING_OVERLAY.setAndGet("oldLoadingOverlay");
        public boolean removeLoadingBar = CandyTweak.REMOVE_LOADING_BAR.setAndGet("removeLoadingBar");
        public boolean oldLoadingScreens = CandyTweak.OLD_LOADING_SCREENS.setAndGet("oldLoadingScreens");

        // Version Overlay

        public boolean oldVersionOverlay = CandyTweak.OLD_VERSION_OVERLAY.setAndGet("oldVersionOverlay");
        public TweakType.Corner oldOverlayCorner = CandyTweak.OLD_OVERLAY_CORNER.setAndGet("oldOverlayCorner");
        public String oldOverlayText = CandyTweak.OLD_OVERLAY_TEXT.setAndGet("oldOverlayText");

        // Pause Screen

        public TweakVersion.PauseLayout oldPauseMenu = CandyTweak.OLD_PAUSE_MENU.setAndGet("oldPauseMenu");
        public boolean includeModsOnPause = CandyTweak.INCLUDE_MODS_ON_PAUSE.setAndGet("includeModsOnPause");
        public boolean removeExtraPauseButtons = CandyTweak.REMOVE_EXTRA_PAUSE_BUTTONS.setAndGet("removeExtraPauseButtons");

        // Anvil Screen

        public boolean oldAnvilScreen = CandyTweak.OLD_ANVIL_SCREEN.setAndGet("oldAnvilScreen");

        // Crafting Screen

        public boolean oldCraftingScreen = CandyTweak.OLD_CRAFTING_SCREEN.setAndGet("oldCraftingScreen");
        public TweakType.RecipeBook craftingBook = CandyTweak.CRAFTING_BOOK.setAndGet("craftingBook");

        // Furnace Screen

        public boolean oldFurnaceScreen = CandyTweak.OLD_FURNACE_SCREEN.setAndGet("oldFurnaceScreen");
        public TweakType.RecipeBook furnaceBook = CandyTweak.FURNACE_BOOK.setAndGet("furnaceBook");

        // Chat Screen

        public boolean oldChatInput = CandyTweak.OLD_CHAT_INPUT.setAndGet("oldChatInput");
        public boolean oldChatBox = CandyTweak.OLD_CHAT_BOX.setAndGet("oldChatBox");
        public boolean disableSignatureBoxes = CandyTweak.DISABLE_SIGNATURE_BOXES.setAndGet("disableSignatureBoxes");
        public int chatOffset = CandyTweak.CHAT_OFFSET.setAndGet("chatOffset");

        // Tooltips

        public boolean oldTooltipBoxes = CandyTweak.OLD_TOOLTIP_BOXES.setAndGet("oldTooltipBoxes");
        public boolean oldNoItemTooltips = CandyTweak.OLD_NO_ITEM_TOOLTIPS.setAndGet("oldNoItemTooltips");

        // Tooltip Parts

        public boolean showEnchantmentTip = CandyTweak.SHOW_ENCHANTMENT_TIP.setAndGet("showEnchantmentTip");
        public boolean showModifiersTip = CandyTweak.SHOW_MODIFIERS_TIP.setAndGet("showModifiersTip");
        public boolean showDyeTip = CandyTweak.SHOW_DYE_TIP.setAndGet("showDyeTip");

        // Items

        public boolean fixItemModelGap = CandyTweak.FIX_ITEM_MODEL_GAP.setAndGet("fixItemModelGap");
        public boolean oldDamageArmorTint = CandyTweak.OLD_DAMAGE_ARMOR_TINT.setAndGet("oldDamageArmorTint");
        public boolean oldItemHolding = CandyTweak.OLD_ITEM_HOLDING.setAndGet("oldItemHolding");
        public LinkedHashSet<String> ignoredHoldingItems = CandyTweak.IGNORED_HOLDING_ITEMS.setAndGet("ignoredHoldingItems");

        // Item Merging

        public int itemMergeLimit = CandyTweak.ITEM_MERGE_LIMIT.setAndGet("itemMergeLimit");
        public boolean oldItemMerging = CandyTweak.OLD_ITEM_MERGING.setAndGet("oldItemMerging");

        // 2D Items

        public boolean old2dColors = CandyTweak.OLD_2D_COLORS.setAndGet("old2dColors");
        public boolean old2dItems = CandyTweak.OLD_2D_ITEMS.setAndGet("old2dItems");
        public boolean old2dFrames = CandyTweak.OLD_2D_FRAMES.setAndGet("old2dFrames");
        public boolean old2dThrownItems = CandyTweak.OLD_2D_THROWN_ITEMS.setAndGet("old2dThrownItems");
        public boolean old2dEnchantedItems = CandyTweak.OLD_2D_ENCHANTED_ITEMS.setAndGet("old2dEnchantedItems");
        public boolean old2dRendering = CandyTweak.OLD_2D_RENDERING.setAndGet("old2dRendering");

        // Item Display

        public boolean oldDurabilityColors = CandyTweak.OLD_DURABILITY_COLORS.setAndGet("oldDurabilityColors");
        public boolean oldNoSelectedItemName = CandyTweak.OLD_NO_SELECTED_ITEM_NAME.setAndGet("oldNoSelectedItemName");
        public boolean oldPlainSelectedItemName = CandyTweak.OLD_PLAIN_SELECTED_ITEM_NAME.setAndGet("oldPlainSelectedItemName");

        // World Lighting

        public boolean fixChunkBorderLag = CandyTweak.FIX_CHUNK_BORDER_LAG.setAndGet("fixChunkBorderLag");
        public boolean disableBrightness = CandyTweak.DISABLE_BRIGHTNESS.setAndGet("disableBrightness");
        public boolean disableLightFlicker = CandyTweak.DISABLE_LIGHT_FLICKER.setAndGet("disableLightFlicker");
        public boolean oldClassicLighting = CandyTweak.OLD_CLASSIC_LIGHTING.setAndGet("oldClassicLighting");
        public boolean oldNetherLighting = CandyTweak.OLD_NETHER_LIGHTING.setAndGet("oldNetherLighting");
        public boolean oldLightRendering = CandyTweak.OLD_LIGHT_RENDERING.setAndGet("oldLightRendering");
        public boolean oldLightColor = CandyTweak.OLD_LIGHT_COLOR.setAndGet("oldLightColor");
        public boolean oldSmoothLighting = CandyTweak.OLD_SMOOTH_LIGHTING.setAndGet("oldSmoothLighting");
        public int maxBlockLight = CandyTweak.MAX_BLOCK_LIGHT.setAndGet("maxBlockLight");

        // Block Lighting

        public boolean oldLeavesLighting = CandyTweak.OLD_LEAVES_LIGHTING.setAndGet("oldLeavesLighting");
        public boolean oldWaterLighting = CandyTweak.OLD_WATER_LIGHTING.setAndGet("oldWaterLighting");

        // Particles

        public boolean oldOpaqueExperience = CandyTweak.OLD_OPAQUE_EXPERIENCE.setAndGet("oldOpaqueExperience");
        public boolean disableNetherParticles = CandyTweak.DISABLE_NETHER_PARTICLES.setAndGet("disableNetherParticles");
        public boolean disableUnderwaterParticles = CandyTweak.DISABLE_UNDERWATER_PARTICLES.setAndGet("disableUnderwaterParticles");

        // Block Particles

        public boolean disableLavaParticles = CandyTweak.DISABLE_LAVA_PARTICLES.setAndGet("disableLavaParticles");
        public boolean disableLeverParticles = CandyTweak.DISABLE_LEVER_PARTICLES.setAndGet("disableLeverParticles");
        public boolean disableModelDestructionParticles = CandyTweak.DISABLE_MODEL_DESTRUCTION_PARTICLES.setAndGet("disableModelDestructionParticles");
        public boolean disableGrowthParticles = CandyTweak.DISABLE_GROWTH_PARTICLES.setAndGet("disableGrowthParticles");

        // Player Particles

        public boolean disableFallingParticles = CandyTweak.DISABLE_FALLING_PARTICLES.setAndGet("disableFallingParticles");
        public boolean disableSprintingParticles = CandyTweak.DISABLE_SPRINTING_PARTICLES.setAndGet("disableSprintingParticles");

        // Attack Particles

        public boolean oldSweepParticles = CandyTweak.OLD_SWEEP_PARTICLES.setAndGet("oldSweepParticles");
        public boolean oldNoDamageParticles = CandyTweak.OLD_NO_DAMAGE_PARTICLES.setAndGet("oldNoDamageParticles");
        public boolean oldNoCritParticles = CandyTweak.OLD_NO_CRIT_PARTICLES.setAndGet("oldNoCritParticles");
        public boolean oldNoMagicHitParticles = CandyTweak.OLD_NO_MAGIC_HIT_PARTICLES.setAndGet("oldNoMagicHitParticles");

        // Explosion Particles

        public boolean oldExplosionParticles = CandyTweak.OLD_EXPLOSION_PARTICLES.setAndGet("oldExplosionParticles");
        public boolean oldMixedExplosionParticles = CandyTweak.OLD_MIXED_EXPLOSION_PARTICLES.setAndGet("oldMixedExplosionParticles");
        public boolean unoptimizedExplosionParticles = CandyTweak.UNOPTIMIZED_EXPLOSION_PARTICLES.setAndGet("unoptimizedExplosionParticles");

        // Title Screen

        public boolean overrideTitleScreen = CandyTweak.OVERRIDE_TITLE_SCREEN.setAndGet("overrideTitleScreen");
        public boolean oldTitleBackground = CandyTweak.OLD_TITLE_BACKGROUND.setAndGet("oldTitleBackground");
        public boolean uncapTitleFPS = CandyTweak.UNCAP_TITLE_FPS.setAndGet("uncapTitleFPS");

        // Title Screen Logo

        public boolean oldAlphaLogo = CandyTweak.OLD_ALPHA_LOGO.setAndGet("oldAlphaLogo");
        public boolean oldLogoOutline = CandyTweak.OLD_LOGO_OUTLINE.setAndGet("oldLogoOutline");

        // Title Screen Buttons

        public TweakVersion.TitleLayout oldButtonLayout = CandyTweak.OLD_BUTTON_LAYOUT.setAndGet("oldButtonLayout");
        public boolean includeModsOnTitle = CandyTweak.INCLUDE_MODS_ON_TITLE.setAndGet("includeModsOnTitle");
        public boolean removeTitleRealmsButton = CandyTweak.REMOVE_TITLE_REALMS_BUTTON.setAndGet("removeTitleRealmsButton");
        public boolean removeTitleAccessibilityButton = CandyTweak.REMOVE_TITLE_ACCESSIBILITY_BUTTON.setAndGet("removeTitleAccessibilityButton");
        public boolean removeTitleLanguageButton = CandyTweak.REMOVE_TITLE_LANGUAGE_BUTTON.setAndGet("removeTitleLanguageButton");

        // Title Screen Text

        public String titleVersionText = CandyTweak.TITLE_VERSION_TEXT.setAndGet("titleVersionText");
        public boolean titleBottomLeftText = CandyTweak.TITLE_BOTTOM_LEFT_TEXT.setAndGet("titleBottomLeftText");
        public boolean removeTitleModLoaderText = CandyTweak.REMOVE_TITLE_MOD_LOADER_TEXT.setAndGet("removeTitleModLoaderText");

        // World

        public boolean oldSquareBorder = CandyTweak.OLD_SQUARE_BORDER.setAndGet("oldSquareBorder");
        public boolean oldNameTags = CandyTweak.OLD_NAME_TAGS.setAndGet("oldNameTags");

        // World Fog

        public TweakVersion.WorldFog oldWorldFog = CandyTweak.OLD_WORLD_FOG.setAndGet("oldWorldFog");
        public boolean disableHorizonFog = CandyTweak.DISABLE_HORIZON_FOG.setAndGet("disableHorizonFog");
        public boolean oldNetherFog = CandyTweak.OLD_NETHER_FOG.setAndGet("oldNetherFog");
        public boolean oldSunriseSunsetFog = CandyTweak.OLD_SUNRISE_SUNSET_FOG.setAndGet("oldSunriseSunsetFog");
        public boolean oldDarkFog = CandyTweak.OLD_DARK_FOG.setAndGet("oldDarkFog");
        public boolean oldDynamicFogColor = CandyTweak.OLD_DYNAMIC_FOG_COLOR.setAndGet("oldDynamicFogColor");
        public TweakVersion.FogColor universalFogColor = CandyTweak.UNIVERSAL_FOG_COLOR.setAndGet("universalFogColor");

        // Custom World Fog

        public boolean customTerrainFog = CandyTweak.CUSTOM_TERRAIN_FOG.setAndGet("customTerrainFog");
        public String customTerrainFogColor = CandyTweak.CUSTOM_TERRAIN_FOG_COLOR.setAndGet("customTerrainFogColor");
        public boolean customNetherFog = CandyTweak.CUSTOM_NETHER_FOG.setAndGet("customNetherFog");
        public String customNetherFogColor = CandyTweak.CUSTOM_NETHER_FOG_COLOR.setAndGet("customNetherFogColor");

        // Water Fog

        public boolean oldWaterFogDensity = CandyTweak.OLD_WATER_FOG_DENSITY.setAndGet("oldWaterFogDensity");
        public boolean oldWaterFogColor = CandyTweak.OLD_WATER_FOG_COLOR.setAndGet("oldWaterFogColor");
        public boolean smoothWaterDensity = CandyTweak.SMOOTH_WATER_DENSITY.setAndGet("smoothWaterDensity");
        public boolean smoothWaterColor = CandyTweak.SMOOTH_WATER_COLOR.setAndGet("smoothWaterColor");

        // World Sky

        public boolean disableSunriseSunsetColors = CandyTweak.DISABLE_SUNRISE_SUNSET_COLORS.setAndGet("disableSunriseSunsetColors");
        public boolean oldSunriseAtNorth = CandyTweak.OLD_SUNRISE_AT_NORTH.setAndGet("oldSunriseAtNorth");
        public TweakVersion.Generic oldStars = CandyTweak.OLD_STARS.setAndGet("oldStars");
        public boolean oldDynamicSkyColor = CandyTweak.OLD_DYNAMIC_SKY_COLOR.setAndGet("oldDynamicSkyColor");
        public TweakVersion.SkyColor universalSkyColor = CandyTweak.UNIVERSAL_SKY_COLOR.setAndGet("universalSkyColor");
        public boolean oldNetherSky = CandyTweak.OLD_NETHER_SKY.setAndGet("oldNetherSky");
        public int oldCloudHeight = CandyTweak.OLD_CLOUD_HEIGHT.setAndGet("oldCloudHeight");

        // Custom World Sky

        public boolean customWorldSky = CandyTweak.CUSTOM_WORLD_SKY.setAndGet("customWorldSky");
        public String customWorldSkyColor = CandyTweak.CUSTOM_WORLD_SKY_COLOR.setAndGet("customWorldSkyColor");
        public boolean customNetherSky = CandyTweak.CUSTOM_NETHER_SKY.setAndGet("customNetherSky");
        public String customNetherSkyColor = CandyTweak.CUSTOM_NETHER_SKY_COLOR.setAndGet("customNetherSkyColor");

        // Void Sky

        public TweakVersion.Generic oldBlueVoid = CandyTweak.OLD_BLUE_VOID.setAndGet("oldBlueVoid");
        public boolean oldBlueVoidOverride = CandyTweak.OLD_BLUE_VOID_OVERRIDE.setAndGet("oldBlueVoidOverride");
        public boolean oldDarkVoidHeight = CandyTweak.OLD_DARK_VOID_HEIGHT.setAndGet("oldDarkVoidHeight");
        public boolean customVoidSky = CandyTweak.CUSTOM_VOID_SKY.setAndGet("customVoidSky");
        public String customVoidSkyColor = CandyTweak.CUSTOM_VOID_SKY_COLOR.setAndGet("customVoidSkyColor");

        // Void Fog

        public boolean disableVoidFog = CandyTweak.DISABLE_VOID_FOG.setAndGet("disableVoidFog");
        public boolean creativeVoidFog = CandyTweak.CREATIVE_VOID_FOG.setAndGet("creativeVoidFog");
        public boolean creativeVoidParticles = CandyTweak.CREATIVE_VOID_PARTICLES.setAndGet("creativeVoidParticles");
        public boolean lightRemovesVoidFog = CandyTweak.LIGHT_REMOVES_VOID_FOG.setAndGet("lightRemovesVoidFog");
        public String voidFogColor = CandyTweak.VOID_FOG_COLOR.setAndGet("voidFogColor");
        public int voidFogEncroach = CandyTweak.VOID_FOG_ENCROACH.setAndGet("voidFogEncroach");
        public int voidFogStart = CandyTweak.VOID_FOG_START.setAndGet("voidFogStart");
        public int voidParticleStart = CandyTweak.VOID_PARTICLE_START.setAndGet("voidParticleStart");
        public int voidParticleRadius = CandyTweak.VOID_PARTICLE_RADIUS.setAndGet("voidParticleRadius");
        public int voidParticleDensity = CandyTweak.VOID_PARTICLE_DENSITY.setAndGet("voidParticleDensity");
    }

    public EyeCandy eyeCandy = new EyeCandy();

    public static class Gameplay
    {
        // Bugs

        public boolean oldLadderGap = GameplayTweak.OLD_LADDER_GAP.setAndGet("oldLadderGap");
        public boolean oldSquidMilking = GameplayTweak.OLD_SQUID_MILKING.setAndGet("oldSquidMilking");

        // Mob AI

        public boolean disableAnimalPanic = GameplayTweak.DISABLE_ANIMAL_PANIC.setAndGet("disableAnimalPanic");

        // Mob Spawning

        public int animalSpawnCap = GameplayTweak.ANIMAL_SPAWN_CAP.setAndGet("animalSpawnCap");
        public boolean oldAnimalSpawning = GameplayTweak.OLD_ANIMAL_SPAWNING.setAndGet("oldAnimalSpawning");

        // Sheep

        public boolean disableSheepEatGrass = GameplayTweak.DISABLE_SHEEP_EAT_GRASS.setAndGet("disableSheepEatGrass");
        public boolean oldSheepPunching = GameplayTweak.OLD_SHEEP_PUNCHING.setAndGet("oldSheepPunching");
        public boolean oneWoolPunch = GameplayTweak.ONE_WOOL_PUNCH.setAndGet("oneWoolPunch");

        // Classic Mob Drops

        public boolean oldZombiePigmenDrops = GameplayTweak.OLD_ZOMBIE_PIGMEN_DROPS.setAndGet("oldZombiePigmenDrops");
        public boolean oldSkeletonDrops = GameplayTweak.OLD_SKELETON_DROPS.setAndGet("oldSkeletonDrops");
        public boolean oldChickenDrops = GameplayTweak.OLD_CHICKEN_DROPS.setAndGet("oldChickenDrops");
        public boolean oldZombieDrops = GameplayTweak.OLD_ZOMBIE_DROPS.setAndGet("oldZombieDrops");
        public boolean oldSpiderDrops = GameplayTweak.OLD_SPIDER_DROPS.setAndGet("oldSpiderDrops");
        public boolean oldSheepDrops = GameplayTweak.OLD_SHEEP_DROPS.setAndGet("oldSheepDrops");
        public boolean oldCowDrops = GameplayTweak.OLD_COW_DROPS.setAndGet("oldCowDrops");
        public boolean oldPigDrops = GameplayTweak.OLD_PIG_DROPS.setAndGet("oldPigDrops");

        // Modern Mob Drops

        public boolean oldStyleZombieVillagerDrops = GameplayTweak.OLD_STYLE_ZOMBIE_VILLAGER_DROPS.setAndGet("oldStyleZombieVillagerDrops");
        public boolean oldStyleCaveSpiderDrops = GameplayTweak.OLD_STYLE_CAVE_SPIDER_DROPS.setAndGet("oldStyleCaveSpiderDrops");
        public boolean oldStyleMooshroomDrops = GameplayTweak.OLD_STYLE_MOOSHROOM_DROPS.setAndGet("oldStyleMooshroomDrops");
        public boolean oldStyleDrownedDrops = GameplayTweak.OLD_STYLE_DROWNED_DROPS.setAndGet("oldStyleDrownedDrops");
        public boolean oldStyleRabbitDrops = GameplayTweak.OLD_STYLE_RABBIT_DROPS.setAndGet("oldStyleRabbitDrops");
        public boolean oldStyleStrayDrops = GameplayTweak.OLD_STYLE_STRAY_DROPS.setAndGet("oldStyleStrayDrops");
        public boolean oldStyleHuskDrops = GameplayTweak.OLD_STYLE_HUSK_DROPS.setAndGet("oldStyleHuskDrops");

        // Combat

        public boolean oldDamageValues = GameplayTweak.OLD_DAMAGE_VALUES.setAndGet("oldDamageValues");
        public boolean disableCooldown = GameplayTweak.DISABLE_COOLDOWN.setAndGet("disableCooldown");
        public boolean disableMissTimer = GameplayTweak.DISABLE_MISS_TIMER.setAndGet("disableMissTimer");
        public boolean disableCriticalHit = GameplayTweak.DISABLE_CRITICAL_HIT.setAndGet("disableCriticalHit");
        public boolean disableSweep = GameplayTweak.DISABLE_SWEEP.setAndGet("disableSweep");

        // Combat Bow

        public int arrowSpeed = GameplayTweak.ARROW_SPEED.setAndGet("arrowSpeed");
        public boolean instantBow = GameplayTweak.INSTANT_BOW.setAndGet("instantBow");
        public boolean invincibleBow = GameplayTweak.INVINCIBLE_BOW.setAndGet("invincibleBow");

        // Experience Bar

        public boolean disableExperienceBar = GameplayTweak.DISABLE_EXPERIENCE_BAR.setAndGet("disableExperienceBar");

        // Alternative Experience Text

        public boolean showXpLevelText = GameplayTweak.SHOW_XP_LEVEL_TEXT.setAndGet("showXpLevelText");
        public boolean showXpLevelInCreative = GameplayTweak.SHOW_XP_LEVEL_IN_CREATIVE.setAndGet("showXpLevelInCreative");
        public TweakType.Corner altXpLevelCorner = GameplayTweak.ALT_XP_LEVEL_CORNER.setAndGet("altXpLevelCorner");
        public String altXpLevelText = GameplayTweak.ALT_XP_LEVEL_TEXT.setAndGet("altXpLevelText");

        // Alternative Progress Text

        public boolean showXpProgressText = GameplayTweak.SHOW_XP_PROGRESS_TEXT.setAndGet("showXpProgressText");
        public boolean showXpProgressInCreative = GameplayTweak.SHOW_XP_PROGRESS_IN_CREATIVE.setAndGet("showXpProgressInCreative");
        public boolean useDynamicProgressColor = GameplayTweak.USE_DYNAMIC_PROGRESS_COLOR.setAndGet("useDynamicProgressColor");
        public TweakType.Corner altXpProgressCorner = GameplayTweak.ALT_XP_PROGRESS_CORNER.setAndGet("altXpProgressCorner");
        public String altXpProgressText = GameplayTweak.ALT_XP_PROGRESS_TEXT.setAndGet("altXpProgressText");

        // Experience Orb

        public boolean disableOrbSpawn = GameplayTweak.DISABLE_ORB_SPAWN.setAndGet("disableOrbSpawn");
        public boolean disableOrbRendering = GameplayTweak.DISABLE_ORB_RENDERING.setAndGet("disableOrbRendering");

        // Experience Blocks

        public boolean disableAnvil = GameplayTweak.DISABLE_ANVIL.setAndGet("disableAnvil");
        public boolean disableEnchantTable = GameplayTweak.DISABLE_ENCHANT_TABLE.setAndGet("disableEnchantTable");

        // Player Mechanics

        public boolean disableSprint = GameplayTweak.DISABLE_SPRINT.setAndGet("disableSprint");
        public boolean leftClickDoor = GameplayTweak.LEFT_CLICK_DOOR.setAndGet("leftClickDoor");
        public boolean leftClickLever = GameplayTweak.LEFT_CLICK_LEVER.setAndGet("leftClickLever");
        public boolean leftClickButton = GameplayTweak.LEFT_CLICK_BUTTON.setAndGet("leftClickButton");

        // Farming Mechanics

        public boolean instantBonemeal = GameplayTweak.INSTANT_BONEMEAL.setAndGet("instantBonemeal");
        public boolean tilledGrassSeeds = GameplayTweak.TILLED_GRASS_SEEDS.setAndGet("tilledGrassSeeds");

        // Fire Mechanics

        public boolean oldFire = GameplayTweak.OLD_FIRE.setAndGet("oldFire");
        public boolean infiniteBurn = GameplayTweak.INFINITE_BURN.setAndGet("infiniteBurn");

        // Swimming Mechanics

        public boolean instantAir = GameplayTweak.INSTANT_AIR.setAndGet("instantAir");
        public boolean disableSwim = GameplayTweak.DISABLE_SWIM.setAndGet("disableSwim");

        // Minecart Mechanics

        public boolean cartBoosting = GameplayTweak.CART_BOOSTING.setAndGet("cartBoosting");

        // Block Mechanics

        public boolean disableBedBounce = GameplayTweak.DISABLE_BED_BOUNCE.setAndGet("disableBedBounce");

        // Hunger Bar

        public boolean disableHungerBar = GameplayTweak.DISABLE_HUNGER_BAR.setAndGet("disableHungerBar");

        // Alternative Food Text

        public boolean showHungerFoodText = GameplayTweak.SHOW_HUNGER_FOOD_TEXT.setAndGet("showHungerFoodText");
        public boolean useDynamicFoodColor = GameplayTweak.USE_DYNAMIC_FOOD_COLOR.setAndGet("useDynamicFoodColor");
        public TweakType.Corner altHungerFoodCorner = GameplayTweak.ALT_HUNGER_FOOD_CORNER.setAndGet("altHungerFoodCorner");
        public String altHungerFoodText = GameplayTweak.ALT_HUNGER_FOOD_TEXT.setAndGet("altHungerFoodText");

        // Alternative Saturation Text

        public boolean showHungerSaturationText = GameplayTweak.SHOW_HUNGER_SATURATION_TEXT.setAndGet("showHungerSaturationText");
        public boolean useDynamicSaturationColor = GameplayTweak.USE_DYNAMIC_SATURATION_COLOR.setAndGet("useDynamicSaturationColor");
        public TweakType.Corner altHungerSaturationCorner = GameplayTweak.ALT_HUNGER_SATURATION_CORNER.setAndGet("altHungerSaturationCorner");
        public String altHungerSaturationText = GameplayTweak.ALT_HUNGER_SATURATION_TEXT.setAndGet("altHungerSaturationText");

        // Food

        public boolean instantEat = GameplayTweak.INSTANT_EAT.setAndGet("instantEat");
        public boolean disableHunger = GameplayTweak.DISABLE_HUNGER.setAndGet("disableHunger");
        public LinkedHashMap<String, Integer> customFoodHealth = GameplayTweak.CUSTOM_FOOD_HEALTH.setAndGet("customFoodHealth");
        public boolean oldFoodStacking = GameplayTweak.OLD_FOOD_STACKING.setAndGet("oldFoodStacking");
        public LinkedHashMap<String, Integer> customFoodStacking = GameplayTweak.CUSTOM_FOOD_STACKING.setAndGet("customFoodStacking");
        public LinkedHashMap<String, Integer> customItemStacking = GameplayTweak.CUSTOM_ITEM_STACKING.setAndGet("customItemStacking");
    }

    public Gameplay gameplay = new Gameplay();

    public static class Animation
    {
        // Arm

        public boolean oldArmSway = AnimationTweak.OLD_ARM_SWAY.setAndGet("oldArmSway");
        public boolean armSwayMirror = AnimationTweak.ARM_SWAY_MIRROR.setAndGet("armSwayMirror");
        public int armSwayIntensity = AnimationTweak.ARM_SWAY_INTENSITY.setAndGet("armSwayIntensity");
        public boolean oldSwing = AnimationTweak.OLD_SWING.setAndGet("oldSwing");
        public boolean oldSwingInterrupt = AnimationTweak.OLD_SWING_INTERRUPT.setAndGet("oldSwingInterrupt");
        public boolean oldSwingDropping = AnimationTweak.OLD_SWING_DROPPING.setAndGet("oldSwingDropping");
        public boolean oldClassicSwing = AnimationTweak.OLD_CLASSIC_SWING.setAndGet("oldClassicSwing");

        // Item

        public boolean oldItemCooldown = AnimationTweak.OLD_ITEM_COOLDOWN.setAndGet("oldItemCooldown");
        public boolean oldItemReequip = AnimationTweak.OLD_ITEM_REEQUIP.setAndGet("oldItemReequip");
        public boolean oldToolExplosion = AnimationTweak.OLD_TOOL_EXPLOSION.setAndGet("oldToolExplosion");

        // Mob

        public boolean oldZombieArms = AnimationTweak.OLD_ZOMBIE_ARMS.setAndGet("oldZombieArms");
        public boolean oldSkeletonArms = AnimationTweak.OLD_SKELETON_ARMS.setAndGet("oldSkeletonArms");
        public boolean oldGhastCharging = AnimationTweak.OLD_GHAST_CHARGING.setAndGet("oldGhastCharging");

        // Player

        public boolean oldBackwardWalking = AnimationTweak.OLD_BACKWARD_WALKING.setAndGet("oldBackwardWalking");
        public boolean oldCollideBobbing = AnimationTweak.OLD_COLLIDE_BOBBING.setAndGet("oldCollideBobbing");
        public boolean oldVerticalBobbing = AnimationTweak.OLD_VERTICAL_BOBBING.setAndGet("oldVerticalBobbing");
        public boolean oldCreativeCrouch = AnimationTweak.OLD_CREATIVE_CROUCH.setAndGet("oldCreativeCrouch");
        public boolean oldDirectionalDamage = AnimationTweak.OLD_DIRECTIONAL_DAMAGE.setAndGet("oldDirectionalDamage");
        public boolean oldRandomDamage = AnimationTweak.OLD_RANDOM_DAMAGE.setAndGet("oldRandomDamage");
        public boolean oldSneaking = AnimationTweak.OLD_SNEAKING.setAndGet("oldSneaking");
        public boolean disableDeathTopple = AnimationTweak.DISABLE_DEATH_TOPPLE.setAndGet("disableDeathTopple");
    }

    public Animation animation = new Animation();

    public static class Swing
    {
        // Global Speeds

        public boolean overrideSpeeds = SwingTweak.OVERRIDE_SPEEDS.setAndGet("overrideSpeeds");
        public boolean leftClickSpeedOnBlockInteract = SwingTweak.LEFT_CLICK_SPEED_ON_BLOCK_INTERACT.setAndGet("leftClickSpeedOnBlockInteract");
        public int leftGlobalSpeed = SwingTweak.LEFT_GLOBAL_SPEED.setAndGet("leftGlobalSpeed");
        public int rightGlobalSpeed = SwingTweak.RIGHT_GLOBAL_SPEED.setAndGet("rightGlobalSpeed");

        // Item Speeds

        public int leftItemSpeed = SwingTweak.LEFT_ITEM_SPEED.setAndGet("leftItemSpeed");
        public int rightItemSpeed = SwingTweak.RIGHT_ITEM_SPEED.setAndGet("rightItemSpeed");
        public int leftToolSpeed = SwingTweak.LEFT_TOOL_SPEED.setAndGet("leftToolSpeed");
        public int rightToolSpeed = SwingTweak.RIGHT_TOOL_SPEED.setAndGet("rightToolSpeed");
        public int leftBlockSpeed = SwingTweak.LEFT_BLOCK_SPEED.setAndGet("leftBlockSpeed");
        public int rightBlockSpeed = SwingTweak.RIGHT_BLOCK_SPEED.setAndGet("rightBlockSpeed");
        public int leftSwordSpeed = SwingTweak.LEFT_SWORD_SPEED.setAndGet("leftSwordSpeed");
        public int rightSwordSpeed = SwingTweak.RIGHT_SWORD_SPEED.setAndGet("rightSwordSpeed");

        // Potion Speeds

        public int leftHasteSpeed = SwingTweak.LEFT_HASTE_SPEED.setAndGet("leftHasteSpeed");
        public int rightHasteSpeed = SwingTweak.RIGHT_HASTE_SPEED.setAndGet("rightHasteSpeed");
        public int leftFatigueSpeed = SwingTweak.LEFT_FATIGUE_SPEED.setAndGet("leftFatigueSpeed");
        public int rightFatigueSpeed = SwingTweak.RIGHT_FATIGUE_SPEED.setAndGet("rightFatigueSpeed");

        // Custom Speeds

        public LinkedHashMap<String, Integer> leftClickSwingSpeeds = SwingTweak.LEFT_CLICK_SWING_SPEEDS.setAndGet("leftClickSwingSpeeds");
        public LinkedHashMap<String, Integer> rightClickSwingSpeeds = SwingTweak.RIGHT_CLICK_SWING_SPEEDS.setAndGet("rightClickSwingSpeeds");
    }

    public Swing swing = new Swing();

    public static class Mod
    {
        public boolean interactedWithConfig = ModTweak.INTERACTED_WITH_CONFIG.setAndGet("interactedWithConfig");
        public boolean displayDonatorBanner = ModTweak.DISPLAY_DONATOR_BANNER.setAndGet("displayDonatorBanner");
        public MenuOption defaultScreen = ModTweak.DEFAULT_SCREEN.setAndGet("defaultScreen");
        public int numberOfBackups = ModTweak.NUMBER_OF_BACKUPS.setAndGet("numberOfBackups");
        public boolean displayNewTags = ModTweak.DISPLAY_NEW_TAGS.setAndGet("displayNewTags");
        public boolean displaySidedTags = ModTweak.DISPLAY_SIDED_TAGS.setAndGet("displaySidedTags");
        public boolean displayTagTooltips = ModTweak.DISPLAY_TAG_TOOLTIPS.setAndGet("displayTagTooltips");
        public boolean displayTweakStatus = ModTweak.DISPLAY_TWEAK_STATUS.setAndGet("displayTweakStatus");
        public boolean displayCategoryTree = ModTweak.DISPLAY_CATEGORY_TREE.setAndGet("displayCategoryTree");
        public String categoryTreeColor = ModTweak.CATEGORY_TREE_COLOR.setAndGet("categoryTreeColor");
        public boolean displayRowHighlight = ModTweak.DISPLAY_ROW_HIGHLIGHT.setAndGet("displayRowHighlight");
        public boolean doRowHighlightFade = ModTweak.DO_ROW_HIGHLIGHT_FADE.setAndGet("doRowHighlightFade");
        public String rowHighlightColor = ModTweak.ROW_HIGHLIGHT_COLOR.setAndGet("rowHighlightColor");
    }

    public Mod mod = new Mod();
}
