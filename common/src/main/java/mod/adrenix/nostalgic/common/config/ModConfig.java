package mod.adrenix.nostalgic.common.config;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.client.config.ClientConfigCache;
import mod.adrenix.nostalgic.common.config.reflect.StatusType;
import mod.adrenix.nostalgic.common.config.tweak.*;
import mod.adrenix.nostalgic.network.packet.PacketS2CTweakUpdate;
import mod.adrenix.nostalgic.server.config.ServerConfig;
import mod.adrenix.nostalgic.server.config.ServerConfigCache;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import mod.adrenix.nostalgic.util.client.NetUtil;
import mod.adrenix.nostalgic.util.common.PacketUtil;
import net.minecraft.SharedConstants;

/**
 * This utility class acts as the interface for parts of the mod that need to know the state of tweaks.
 * This is used by both the client and server, so it is recommended to keep vanilla client code out.
 */

public abstract class ModConfig
{
    /* Server Config References */

    private static final ServerConfig.EyeCandy SERVER_CANDY = ServerConfigCache.getCandy();
    private static final ServerConfig.Gameplay SERVER_GAMEPLAY = ServerConfigCache.getGameplay();
    private static final ServerConfig.Animation SERVER_ANIMATION = ServerConfigCache.getAnimation();

    /* Client Config References */

    private static final ClientConfig.Animation ANIMATION = ClientConfigCache.getAnimation();
    private static final ClientConfig.Gameplay GAMEPLAY = ClientConfigCache.getGameplay();
    private static final ClientConfig.EyeCandy CANDY = ClientConfigCache.getCandy();
    private static final ClientConfig.Sound SOUND = ClientConfigCache.getSound();
    private static final ClientConfig CONFIG = ClientConfigCache.getRoot();

    /**
     * Loads the given tweak and checks if its cached value on disk should be used.
     *
     * The server will always use the disk value since there is no global mod state.
     * The client will use the disk value if the mod is enabled, the connection is verified, and the tweak is not
     * dynamic.
     *
     * @param tweak The tweak to load and to check if the value on disk should be used.
     * @return Whether to use what's saved on disk.
     */
    public static boolean isTweakOn(ITweak tweak)
    {
        // Code is querying this tweak - load it
        loadTweak(tweak);

        // The server does not need to use a universal enabled/disable state.
        if (NostalgicTweaks.isServer())
            return true;
        else if (NetUtil.isLocalHost())
            return true;
        else if (isTweakExempt(tweak))
            return true;

        TweakServerCache<?> cache = TweakServerCache.get(tweak);

        // If the tweak is server side, and we're not connected to an N.T server, disable the tweak
        if (!NostalgicTweaks.isNetworkVerified() && cache != null && !cache.isDynamic())
            return false;

        return CONFIG.isModEnabled;
    }

    /**
     * Some tweaks need a special exception if their code fires before the connection verification is synced.
     * @param tweak The tweak to check against.
     * @return Whether the tweak is exempt from network verification checks.
     */
    private static boolean isTweakExempt(ITweak tweak)
    {
        return CONFIG.isModEnabled && tweak.equals(CandyTweak.CREATIVE_HOTBAR);
    }

    /**
     * Loads a tweak by updating its status within the all tweak caches.
     * If the server is loading a tweak, then an update packet needs to be sent to all connected players.
     *
     * @param tweak The tweak to load.
     */
    private static void loadTweak(ITweak tweak)
    {
        TweakServerCache<?> cache = TweakServerCache.get(tweak);

        if (!tweak.isLoaded() || (cache != null && cache.getStatus().equals(StatusType.FAIL)))
        {
            tweak.setEnabled();

            // Server cache status syncing
            if (cache != null)
            {
                cache.setStatus(StatusType.LOADED);

                // Some tweaks will be executing code before the server is started
                // Therefore check for server instance before sending a packet
                if (NostalgicTweaks.isServer() && NostalgicTweaks.getServer() != null)
                    PacketUtil.sendToAll(new PacketS2CTweakUpdate(cache));
                else if (NostalgicTweaks.isClient())
                {
                    if (NetUtil.getIntegratedServer() != null)
                        PacketUtil.sendToAll(NetUtil.getIntegratedServer().getPlayerList().getPlayers(), new PacketS2CTweakUpdate(cache));
                }
            }
        }
    }

    /**
     * Get which config file on disk should return the value.
     * @param tweak The tweak used to find the value on disk.
     * @param client A {@link ClientConfig client config} field.
     * @param server A {@link ServerConfig server config} field.
     * @param <T> The value expected on disk.
     * @return What is kept on disk based on the current environment.
     */
    private static <T> T getSidedTweak(ITweak tweak, T client, T server)
    {
        if (NostalgicTweaks.isServer())
            return server;
        else if (NetUtil.isSingleplayer())
            return client;

        TweakServerCache<T> cache = TweakServerCache.get(tweak);
        boolean isDynamic = cache != null && cache.isDynamic();

        if (isDynamic && NetUtil.isMultiplayer() && !NostalgicTweaks.isNetworkVerified())
            return client;

        if (isDynamic || (cache != null && NostalgicTweaks.isNetworkVerified()))
            return cache.getServerCache();
        return client;
    }

    /**
     * Check if a tweak is enabled based on if the mod is enabled and the tweak on disk is enabled.
     * @param tweak The tweak to check.
     * @param client A {@link ClientConfig client config} boolean field.
     * @return Whether the tweak should be considered on.
     */
    private static boolean getBoolTweak(ITweak tweak, boolean client)
    {
        return isTweakOn(tweak) && client;
    }

    /**
     * Check if a tweak is enabled based on which logical side is using the mod.
     * @param tweak The tweak to check.
     * @param client A {@link ClientConfig client config} boolean field.
     * @param server A {@link ServerConfig server config} boolean field.
     * @return Whether the tweak should be considered on.
     */
    private static boolean getSidedBoolTweak(ITweak tweak, boolean client, boolean server)
    {
        return isTweakOn(tweak) && getSidedTweak(tweak, client, server);
    }

    /**
     * Get the tweak enumeration stored on disk for the given tweak.
     * @param tweak The tweak to check which enumeration it's using.
     * @param client A {@link ClientConfig client config} tweak enumeration field.
     * @param <E> The enumeration stored within the tweak.
     * @return The enumeration saved on disk, or the disabled value if the mod state is off.
     */
    private static <E extends Enum<E> & IDisableTweak<E>> E getEnum(ITweak tweak, E client)
    {
        return !isTweakOn(tweak) ? client.getDisabled() : client;
    }

    /**
     * Get a client or server tweak enumeration stored on disk for the given tweak.
     * @param tweak The tweak to check which enumeration it's using.
     * @param client A {@link ClientConfig client config} tweak enumeration field.
     * @param server A {@link ServerConfig server config} tweak enumeration field.
     * @param <E> The enumeration stored within the tweak.
     * @return The enumeration saved on disk, or the disabled value if the mod is client-side and the mod state is off.
     */
    @SuppressWarnings("SameParameterValue") // Temporary suppression until another sided enum tweak is created
    private static <E extends Enum<E> & IDisableTweak<E>> E getSidedEnum(ITweak tweak, E client, E server)
    {
        return NostalgicTweaks.isClient() ? getEnum(tweak, client) : server;
    }

    /**
     * Get a parsed colored string with placeholders replaced with the given text and value.
     * @param text The input text to parse.
     * @param value The value to replace %v with.
     * @return A parsed color string with the given value included.
     */
    private static String parseColor(String text, String value)
    {
        text = text.replaceAll("%([a-fA-F\\d])", "§$1");
        text = text.replaceAll("%v", value);
        return text;
    }

    /* Root Tweaks */

    public static boolean isModEnabled() { return CONFIG.isModEnabled; }

    /* Sound Tweaks */

    public static class Sound
    {
        public static boolean ignoreModdedStep() { return getBoolTweak(SoundTweak.IGNORE_MODDED_STEP, SOUND.ignoreModdedStep); }
        public static boolean disableXpPickup() { return getBoolTweak(SoundTweak.DISABLE_PICKUP, SOUND.disableXpPickup); }
        public static boolean disableXpLevel() { return getBoolTweak(SoundTweak.DISABLE_LEVEL, SOUND.disableXpLevel); }
        public static boolean disableChest() { return getBoolTweak(SoundTweak.DISABLE_CHEST, SOUND.disableChest); }
        public static boolean disableDoor() { return getBoolTweak(SoundTweak.DISABLE_DOOR, SOUND.disableDoorPlace); }
        public static boolean disableBed() { return getBoolTweak(SoundTweak.DISABLE_BED, SOUND.disableBedPlace); }
        public static boolean oldAttack() { return getBoolTweak(SoundTweak.OLD_ATTACK, SOUND.oldAttack); }
        public static boolean oldDamage() { return getBoolTweak(SoundTweak.OLD_HURT, SOUND.oldHurt); }
        public static boolean oldChest() { return getBoolTweak(SoundTweak.OLD_CHEST, SOUND.oldChest); }
        public static boolean oldFall() { return getBoolTweak(SoundTweak.OLD_FALL, SOUND.oldFall); }
        public static boolean oldStep() { return getBoolTweak(SoundTweak.OLD_STEP, SOUND.oldStep); }
        public static boolean oldXp() { return getBoolTweak(SoundTweak.OLD_XP, SOUND.oldXp); }
    }

    /* Eye Candy Tweaks */

    public static class Candy
    {
        /* Boolean Tweaks */

        // Block Candy
        public static boolean fixAmbientOcclusion() { return getBoolTweak(CandyTweak.FIX_AO, CANDY.fixAmbientOcclusion); }
        public static boolean oldTrappedChest() { return getBoolTweak(CandyTweak.TRAPPED_CHEST, CANDY.oldTrappedChest); }
        public static boolean oldEnderChest() { return getBoolTweak(CandyTweak.ENDER_CHEST, CANDY.oldEnderChest); }
        public static boolean oldChestVoxel() { return getSidedBoolTweak(CandyTweak.CHEST_VOXEL, CANDY.oldChestVoxel, SERVER_CANDY.oldChestVoxel); }
        public static boolean oldChest() { return getBoolTweak(CandyTweak.CHEST, CANDY.oldChest); }

        // Interface Candy
        public static TweakType.Corner oldOverlayCorner() { return getEnum(CandyTweak.VERSION_CORNER, CANDY.oldOverlayCorner); }
        public static boolean oldPlainSelectedItemName() { return getBoolTweak(CandyTweak.PLAIN_SELECTED_ITEM_NAME, CANDY.oldPlainSelectedItemName); }
        public static boolean oldNoSelectedItemName() { return getBoolTweak(CandyTweak.NO_SELECTED_ITEM_NAME, CANDY.oldNoSelectedItemName); }
        public static boolean oldDurabilityColors() { return getBoolTweak(CandyTweak.DURABILITY_COLORS, CANDY.oldDurabilityColors); }
        public static boolean includeModsOnPause() { return getBoolTweak(CandyTweak.PAUSE_MODS, CANDY.includeModsOnPause); }
        public static boolean oldVersionOverlay() { return getBoolTweak(CandyTweak.VERSION_OVERLAY, CANDY.oldVersionOverlay); }
        public static boolean oldLoadingScreens() { return getBoolTweak(CandyTweak.LOADING_SCREENS, CANDY.oldLoadingScreens); }
        public static boolean removeLoadingBar() { return getBoolTweak(CandyTweak.REMOVE_LOADING_BAR, CANDY.removeLoadingBar); }
        public static boolean oldButtonHover() { return getBoolTweak(CandyTweak.BUTTON_HOVER, CANDY.oldButtonHover); }
        public static boolean debugEntityId() { return getBoolTweak(CandyTweak.DEBUG_ENTITY_ID, CANDY.debugEntityId); }
        public static boolean oldChatInput() { return getBoolTweak(CandyTweak.CHAT_INPUT, CANDY.oldChatInput); }
        public static boolean oldChatBox() { return getBoolTweak(CandyTweak.CHAT_BOX, CANDY.oldChatBox); }

        // Interface - Inventory Candy
        public static TweakType.RecipeBook getInventoryBook() { return getEnum(CandyTweak.INVENTORY_BOOK, CANDY.inventoryBook); }
        public static TweakType.InventoryShield getInventoryShield() { return getEnum(CandyTweak.INVENTORY_SHIELD, CANDY.inventoryShield); }
        public static boolean oldInventory() { return getBoolTweak(CandyTweak.OLD_INVENTORY, CANDY.oldInventory); }
        public static boolean disableEmptyShield() { return getBoolTweak(CandyTweak.DISABLE_EMPTY_SHIELD, CANDY.disableEmptyShieldTexture); }
        public static boolean disableEmptyArmor() { return getBoolTweak(CandyTweak.DISABLE_EMPTY_ARMOR, CANDY.disableEmptyArmorTexture); }
        public static boolean invertPlayerLight() { return getBoolTweak(CandyTweak.INVERTED_PLAYER_LIGHTING, CANDY.invertedPlayerLighting); }
        public static boolean invertBlockLight() { return getBoolTweak(CandyTweak.INVERTED_BLOCK_LIGHTING, CANDY.invertedBlockLighting); }

        // Interface - Crafting & Furnace Screen
        public static TweakType.RecipeBook getCraftingBook() { return getEnum(CandyTweak.CRAFTING_RECIPE, CANDY.craftingBook); }
        public static TweakType.RecipeBook getFurnaceBook() { return getEnum(CandyTweak.FURNACE_RECIPE, CANDY.furnaceBook); }
        public static boolean oldCraftingScreen() { return getBoolTweak(CandyTweak.CRAFTING_SCREEN, CANDY.oldCraftingScreen); }
        public static boolean oldFurnaceScreen() { return getBoolTweak(CandyTweak.FURNACE_SCREEN, CANDY.oldFurnaceScreen); }

        // Interface - Screen Candy
        public static TweakType.GuiBackground oldGuiBackground() { return getEnum(CandyTweak.OLD_GUI_BACKGROUND, CANDY.oldGuiBackground); }
        public static boolean customGuiBackground() { return getBoolTweak(CandyTweak.CUSTOM_GUI_BACKGROUND, CANDY.customGuiBackground); }
        public static String customTopGradient() { return CANDY.customTopGradient; }
        public static String customBottomGradient() { return CANDY.customBottomGradient; }

        // Interface - Tooltip Candy
        public static boolean addDyeTip() { return getBoolTweak(CandyTweak.DYE_TIP, CANDY.showDyeTip); }
        public static boolean oldTooltips() { return getBoolTweak(CandyTweak.TOOLTIP_BOXES, CANDY.oldTooltipBoxes); }
        public static boolean addModifiersTip() { return getBoolTweak(CandyTweak.MODIFIERS_TIP, CANDY.showModifiersTip); }
        public static boolean addEnchantmentTip() { return getBoolTweak(CandyTweak.ENCHANTMENT_TIP, CANDY.showEnchantmentTip); }
        public static boolean oldNoItemTooltips() { return getBoolTweak(CandyTweak.NO_ITEM_TOOLTIPS, CANDY.oldNoItemTooltips); }

        // Item Candy
        public static boolean fixItemModelGaps() { return getBoolTweak(CandyTweak.FIX_ITEM_MODEL_GAP, CANDY.fixItemModelGap); }
        public static boolean oldFloatingItems() { return getBoolTweak(CandyTweak.FLAT_ITEMS, CANDY.old2dItems); }
        public static boolean oldFlatEnchantment() { return getBoolTweak(CandyTweak.FLAT_ENCHANTED_ITEMS, CANDY.old2dEnchantedItems) && oldFloatingItems(); }
        public static boolean oldFlatThrowing() { return getBoolTweak(CandyTweak.FLAT_THROW_ITEMS, CANDY.old2dThrownItems); }
        public static boolean oldItemHolding() { return getBoolTweak(CandyTweak.ITEM_HOLDING, CANDY.oldItemHolding); }
        public static boolean oldItemMerging() { return getSidedBoolTweak(CandyTweak.ITEM_MERGING, CANDY.oldItemMerging, SERVER_CANDY.oldItemMerging); }
        public static boolean oldFlatFrames() { return getBoolTweak(CandyTweak.FLAT_FRAMES, CANDY.old2dFrames); }

        // Lighting Candy
        public static boolean oldLightBrightness() { return getBoolTweak(CandyTweak.LIGHT_BRIGHTNESS, CANDY.oldLightBrightness); }
        public static boolean oldLightRendering() { return getBoolTweak(CandyTweak.LIGHT_RENDERING, CANDY.oldLighting); }
        public static boolean oldSmoothLighting() { return getBoolTweak(CandyTweak.SMOOTH_LIGHTING, CANDY.oldSmoothLighting); }
        public static boolean oldNetherLighting() { return getBoolTweak(CandyTweak.NETHER_LIGHTING, CANDY.oldNetherLighting); }
        public static boolean oldLeavesLighting() { return getBoolTweak(CandyTweak.LEAVES_LIGHTING, CANDY.oldLeavesLighting); }
        public static boolean oldWaterLighting() { return getSidedBoolTweak(CandyTweak.WATER_LIGHTING, CANDY.oldWaterLighting, SERVER_CANDY.oldWaterLighting); }
        public static boolean oldLightFlicker() { return getBoolTweak(CandyTweak.LIGHT_FLICKER, CANDY.oldLightFlicker); }
        public static boolean disableGamma() { return getBoolTweak(CandyTweak.DISABLE_GAMMA, CANDY.disableGamma); }

        // Particle Candy
        public static boolean oldNoCriticalHitParticles() { return getBoolTweak(CandyTweak.NO_CRIT_PARTICLES, CANDY.oldNoCritParticles); }
        public static boolean oldMixedExplosionParticles() { return getBoolTweak(CandyTweak.MIXED_EXPLOSION_PARTICLES, CANDY.oldMixedExplosionParticles); }
        public static boolean oldNoEnchantHitParticles() { return getBoolTweak(CandyTweak.NO_MAGIC_HIT_PARTICLES, CANDY.oldNoMagicHitParticles); }
        public static boolean disableNetherParticles() { return getBoolTweak(CandyTweak.NO_NETHER_PARTICLES, CANDY.disableNetherParticles); }
        public static boolean oldExplosionParticles() { return getBoolTweak(CandyTweak.EXPLOSION_PARTICLES, CANDY.oldExplosionParticles); }
        public static boolean oldNoDamageParticles() { return getBoolTweak(CandyTweak.NO_DAMAGE_PARTICLES, CANDY.oldNoDamageParticles); }
        public static boolean oldOpaqueExperience() { return getBoolTweak(CandyTweak.OPAQUE_EXPERIENCE, CANDY.oldOpaqueExperience); }
        public static boolean oldSweepParticles() { return getBoolTweak(CandyTweak.SWEEP, CANDY.oldSweepParticles); }

        // Title Screen Candy
        public static boolean includeModsOnTitle() { return getBoolTweak(CandyTweak.TITLE_MODS_BUTTON, CANDY.includeModsOnTitle); }
        public static boolean overrideTitleScreen() { return getBoolTweak(CandyTweak.OVERRIDE_TITLE_SCREEN, CANDY.overrideTitleScreen); }
        public static boolean removeAccessibilityButton() { return getBoolTweak(CandyTweak.TITLE_ACCESSIBILITY, CANDY.removeTitleAccessibilityButton); }
        public static boolean removeTitleModLoaderText() { return getBoolTweak(CandyTweak.TITLE_MOD_LOADER_TEXT, CANDY.removeTitleModLoaderText); }
        public static boolean removeLanguageButton() { return getBoolTweak(CandyTweak.TITLE_LANGUAGE, CANDY.removeTitleLanguageButton); }
        public static boolean removeRealmsButton() { return getBoolTweak(CandyTweak.TITLE_REALMS, CANDY.removeTitleRealmsButton); }
        public static boolean titleBottomLeftText() { return getBoolTweak(CandyTweak.TITLE_BOTTOM_LEFT_TEXT, CANDY.titleBottomLeftText); }
        public static boolean oldTitleBackground() { return getBoolTweak(CandyTweak.TITLE_BACKGROUND, CANDY.oldTitleBackground); }
        public static boolean oldLogoOutline() { return getBoolTweak(CandyTweak.LOGO_OUTLINE, CANDY.oldLogoOutline); }
        public static boolean oldAlphaLogo() { return getBoolTweak(CandyTweak.ALPHA_LOGO, CANDY.oldAlphaLogo); }
        public static boolean uncapTitleFPS() { return getBoolTweak(CandyTweak.UNCAP_TITLE_FPS, CANDY.uncapTitleFPS); }

        // World Candy
        public static boolean disableSunriseSunsetColor() { return getBoolTweak(CandyTweak.DISABLE_SUNRISE_SUNSET_COLOR, CANDY.disableSunriseSunsetColors); }
        public static boolean oldSunriseSunsetFog() { return getBoolTweak(CandyTweak.SUNRISE_SUNSET_FOG, CANDY.oldSunriseSunsetFog); }
        public static boolean oldBlueVoidOverride() { return getBoolTweak(CandyTweak.BLUE_VOID_OVERRIDE, CANDY.oldBlueVoidOverride); }
        public static boolean oldDarkVoidHeight() { return getBoolTweak(CandyTweak.DARK_VOID_HEIGHT, CANDY.oldDarkVoidHeight); }
        public static boolean oldSunriseAtNorth() { return getBoolTweak(CandyTweak.SUNRISE_AT_NORTH, CANDY.oldSunriseAtNorth); }
        public static boolean oldSquareBorder() { return getSidedBoolTweak(CandyTweak.SQUARE_BORDER, CANDY.oldSquareBorder, SERVER_CANDY.oldSquareBorder); }
        public static boolean oldTerrainFog() { return getBoolTweak(CandyTweak.TERRAIN_FOG, CANDY.oldTerrainFog); }
        public static boolean oldHorizonFog() { return getBoolTweak(CandyTweak.HORIZON_FOG, CANDY.oldHorizonFog); }
        public static boolean oldNetherFog() { return getBoolTweak(CandyTweak.NETHER_FOG, CANDY.oldNetherFog); }
        public static boolean oldNameTags() { return getBoolTweak(CandyTweak.NAME_TAGS, CANDY.oldNameTags); }

        // Void Fog
        public static String getVoidFogColor() { return CANDY.voidFogColor; }
        public static boolean disableVoidFog() { return getBoolTweak(CandyTweak.DISABLE_VOID_FOG, CANDY.disableVoidFog); }
        public static boolean creativeVoidFog() { return getBoolTweak(CandyTweak.CREATIVE_VOID_FOG, CANDY.creativeVoidFog); }
        public static boolean creativeVoidParticles() { return getBoolTweak(CandyTweak.CREATIVE_VOID_PARTICLE, CANDY.creativeVoidParticles); }
        public static boolean shouldLightRemoveVoidFog() { return getBoolTweak(CandyTweak.LIGHT_REMOVES_VOID_FOG, CANDY.lightRemovesVoidFog); }

        /* Version Tweaks */

        public static TweakVersion.TitleLayout getButtonLayout() { return getEnum(CandyTweak.TITLE_BUTTON_LAYOUT, CANDY.oldButtonLayout); }
        public static TweakVersion.PauseLayout getPauseLayout() { return getEnum(CandyTweak.PAUSE_LAYOUT, CANDY.oldPauseMenu); }
        public static TweakVersion.Overlay getLoadingOverlay() { return getEnum(CandyTweak.LOADING_OVERLAY, CANDY.oldLoadingOverlay); }
        public static TweakVersion.Generic getSkyColor() { return getEnum(CandyTweak.SKY_COLOR, CANDY.oldSkyColor); }
        public static TweakVersion.Generic getFogColor() { return getEnum(CandyTweak.FOG_COLOR, CANDY.oldFogColor); }
        public static TweakVersion.Generic getBlueVoid() { return getEnum(CandyTweak.BLUE_VOID, CANDY.oldBlueVoid); }
        public static TweakVersion.Generic getStars() { return getEnum(CandyTweak.STARS, CANDY.oldStars); }
        public static TweakVersion.Hotbar getHotbar() { return getSidedEnum(CandyTweak.CREATIVE_HOTBAR, CANDY.oldCreativeHotbar, SERVER_CANDY.oldCreativeHotbar); }

        /* String Tweaks */

        private static final String MINECRAFT_VERSION = SharedConstants.getCurrentVersion().getName();

        public static String getOverlayText() { return parseColor(CANDY.oldOverlayText, MINECRAFT_VERSION); }
        public static String getVersionText() { return parseColor(CANDY.titleVersionText, MINECRAFT_VERSION); }

        /* Integer Tweaks */

        public static int getVoidParticleDensity() { return isTweakOn(CandyTweak.VOID_PARTICLE_DENSITY) ? CANDY.voidParticleDensity : DefaultConfig.Candy.VOID_PARTICLE_DENSITY; }
        public static int getVoidParticleRadius() { return isTweakOn(CandyTweak.VOID_PARTICLE_RADIUS) ? CANDY.voidParticleRadius : DefaultConfig.Candy.VOID_PARTICLE_RADIUS; }
        public static int getVoidParticleStart() { return isTweakOn(CandyTweak.VOID_PARTICLE_START) ? CANDY.voidParticleStart + 64 : DefaultConfig.Candy.VOID_PARTICLE_START + 64; }
        public static int getVoidFogEncroach() { return isTweakOn(CandyTweak.VOID_FOG_ENCROACH) ? CANDY.voidFogEncroach : DefaultConfig.Candy.VOID_FOG_ENCROACH; }
        public static int getVoidFogStart() { return isTweakOn(CandyTweak.VOID_FOG_START) ? CANDY.voidFogStart + 64 : DefaultConfig.Candy.VOID_FOG_START + 64; }
        public static int getCloudHeight() { return isTweakOn(CandyTweak.CLOUD_HEIGHT) ? CANDY.oldCloudHeight : DefaultConfig.Candy.DISABLED_CLOUD_HEIGHT; }
    }

    /* Gameplay Tweaks */

    public static class Gameplay
    {
        // Combat System
        public static int instantBowSpeed()
        {
            return isTweakOn(GameplayTweak.ARROW_SPEED) ? getSidedTweak(GameplayTweak.ARROW_SPEED, GAMEPLAY.arrowSpeed, SERVER_GAMEPLAY.arrowSpeed) : 0;
        }

        public static boolean disableMissTime() { return getSidedBoolTweak(GameplayTweak.DISABLE_MISS_TIMER, GAMEPLAY.disableMissTimer, SERVER_GAMEPLAY.disableMissTimer); }
        public static boolean disableCooldown() { return getSidedBoolTweak(GameplayTweak.DISABLE_COOLDOWN, GAMEPLAY.disableCooldown, SERVER_GAMEPLAY.disableCooldown); }
        public static boolean invincibleBow() { return getSidedBoolTweak(GameplayTweak.INVINCIBLE_BOW, GAMEPLAY.invincibleBow, SERVER_GAMEPLAY.invincibleBow); }
        public static boolean disableSweep() { return getSidedBoolTweak(GameplayTweak.DISABLE_SWEEP, GAMEPLAY.disableSweep, SERVER_GAMEPLAY.disableSweep); }
        public static boolean instantBow() { return getSidedBoolTweak(GameplayTweak.INSTANT_BOW, GAMEPLAY.instantBow, SERVER_GAMEPLAY.instantBow); }

        // Bugs
        public static boolean oldLadderGap() { return getSidedBoolTweak(GameplayTweak.LADDER_GAP, GAMEPLAY.oldLadderGap, SERVER_GAMEPLAY.oldLadderGap); }

        // Experience System
        public static TweakType.Corner alternativeProgressCorner() { return getEnum(GameplayTweak.XP_PROGRESS_CORNER, GAMEPLAY.altXpProgressCorner); }
        public static TweakType.Corner alternativeLevelCorner() { return getEnum(GameplayTweak.XP_LEVEL_CORNER, GAMEPLAY.altXpLevelCorner); }
        public static boolean displayAlternativeProgressText() { return getBoolTweak(GameplayTweak.SHOW_XP_PROGRESS, GAMEPLAY.showXpProgressText); }
        public static boolean displayAlternativeLevelText() { return getBoolTweak(GameplayTweak.SHOW_XP_LEVEL, GAMEPLAY.showXpLevelText); }
        public static boolean useDynamicProgressColor() { return getBoolTweak(GameplayTweak.USE_DYNAMIC_PROGRESS_COLOR, GAMEPLAY.useDynamicProgressColor); }
        public static boolean disableExperienceBar() { return getBoolTweak(GameplayTweak.DISABLE_EXP_BAR, GAMEPLAY.disableExperienceBar); }
        public static boolean disableOrbRendering() { return getBoolTweak(GameplayTweak.ORB_RENDERING, GAMEPLAY.disableOrbRendering); }
        public static boolean disableEnchantTable() { return getSidedBoolTweak(GameplayTweak.ENCHANT_TABLE, GAMEPLAY.disableEnchantTable, SERVER_GAMEPLAY.disableEnchantTable); }
        public static boolean disableOrbSpawn() { return getSidedBoolTweak(GameplayTweak.ORB_SPAWN, GAMEPLAY.disableOrbSpawn, SERVER_GAMEPLAY.disableOrbSpawn); }
        public static boolean disableAnvil() { return getSidedBoolTweak(GameplayTweak.ANVIL, GAMEPLAY.disableAnvil, SERVER_GAMEPLAY.disableAnvil); }

        // Game Mechanics
        public static boolean disableSprint() { return getSidedBoolTweak(GameplayTweak.SPRINT, GAMEPLAY.disableSprint, SERVER_GAMEPLAY.disableSprint); }
        public static boolean infiniteBurn() { return getSidedBoolTweak(GameplayTweak.INFINITE_BURN, GAMEPLAY.infiniteBurn, SERVER_GAMEPLAY.infiniteBurn); }
        public static boolean disableSwim() { return getSidedBoolTweak(GameplayTweak.SWIM, GAMEPLAY.disableSwim, SERVER_GAMEPLAY.disableSwim); }
        public static boolean instantAir() { return getSidedBoolTweak(GameplayTweak.INSTANT_AIR, GAMEPLAY.instantAir, SERVER_GAMEPLAY.instantAir); }
        public static boolean oldFire() { return getSidedBoolTweak(GameplayTweak.FIRE_SPREAD, GAMEPLAY.oldFire, SERVER_GAMEPLAY.oldFire); }

        // Hunger System
        public static TweakType.Corner alternativeSaturationCorner() { return getEnum(GameplayTweak.HUNGER_SATURATION_CORNER, GAMEPLAY.altHungerSaturationCorner); }
        public static TweakType.Corner alternativeFoodCorner() { return getEnum(GameplayTweak.HUNGER_FOOD_CORNER, GAMEPLAY.altHungerFoodCorner); }
        public static boolean displayAlternativeSatText() { return getBoolTweak(GameplayTweak.SHOW_HUNGER_SATURATION, GAMEPLAY.showHungerSaturationText); }
        public static boolean displayAlternativeFoodText() { return getBoolTweak(GameplayTweak.SHOW_HUNGER_FOOD, GAMEPLAY.showHungerFoodText); }
        public static boolean useDynamicFoodColor() { return getBoolTweak(GameplayTweak.USE_DYNAMIC_FOOD_COLOR, GAMEPLAY.useDynamicFoodColor); }
        public static boolean useDynamicSatColor() { return getBoolTweak(GameplayTweak.USE_DYNAMIC_SATURATION_COLOR, GAMEPLAY.useDynamicSaturationColor); }
        public static boolean disableHungerBar() { return getBoolTweak(GameplayTweak.DISABLE_HUNGER_BAR, GAMEPLAY.disableHungerBar); }
        public static boolean oldFoodStacking() { return getSidedBoolTweak(GameplayTweak.FOOD_STACKING, GAMEPLAY.oldFoodStacking, SERVER_GAMEPLAY.oldFoodStacking); }
        public static boolean disableHunger() { return getSidedBoolTweak(GameplayTweak.HUNGER, GAMEPLAY.disableHunger, SERVER_GAMEPLAY.disableHunger); }
        public static boolean instantEat() { return getSidedBoolTweak(GameplayTweak.INSTANT_EAT, GAMEPLAY.instantEat, SERVER_GAMEPLAY.instantEat); }

        /* String Tweaks */

        public static String getAlternativeSaturationText(String saturation) { return parseColor(GAMEPLAY.altHungerSaturationText, saturation); }
        public static String getAlternativeProgressText(String progress) { return parseColor(GAMEPLAY.altXpProgressText, progress); }
        public static String getAlternativeLevelText(String level) { return parseColor(GAMEPLAY.altXpLevelText, level); }
        public static String getAlternativeFoodText(String food) { return parseColor(GAMEPLAY.altHungerFoodText, food); }
    }

    /* Animation Tweaks */

    public static class Animation
    {
        // Arm Animations
        public static float getArmSwayIntensity()
        {
            float mirror = shouldMirrorArmSway() ? -1.0F : 1.0F;
            return isTweakOn(AnimationTweak.ARM_SWAY_INTENSITY) ? (((float) ANIMATION.armSwayIntensity) * mirror / 100.0F) : 1.0F;
        }

        public static boolean oldSwing() { return getBoolTweak(AnimationTweak.ITEM_SWING, ANIMATION.oldSwing); }
        public static boolean oldArmSway() { return getBoolTweak(AnimationTweak.ARM_SWAY, ANIMATION.oldArmSway); }
        public static boolean oldSwingDropping() { return getBoolTweak(AnimationTweak.SWING_DROP, ANIMATION.oldSwingDropping); }
        public static boolean oldInterruptSwing() { return getBoolTweak(AnimationTweak.SWING_INTERRUPT, ANIMATION.oldSwingInterrupt); }
        public static boolean shouldMirrorArmSway() { return getBoolTweak(AnimationTweak.ARM_SWAY_MIRROR, ANIMATION.armSwayMirror); }

        // Item Animations
        public static boolean oldToolExplosion() { return getBoolTweak(AnimationTweak.TOOL_EXPLODE, ANIMATION.oldToolExplosion); }
        public static boolean oldItemCooldown() { return getBoolTweak(AnimationTweak.COOLDOWN, ANIMATION.oldItemCooldown); }
        public static boolean oldItemReequip() { return getBoolTweak(AnimationTweak.REEQUIP, ANIMATION.oldItemReequip); }

        // Mob Animations
        public static boolean oldGhastCharging() { return getBoolTweak(AnimationTweak.GHAST_CHARGING, ANIMATION.oldGhastCharging); }
        public static boolean oldSkeletonArms() { return getBoolTweak(AnimationTweak.SKELETON_ARMS, ANIMATION.oldSkeletonArms); }
        public static boolean oldZombieArms() { return getBoolTweak(AnimationTweak.ZOMBIE_ARMS, ANIMATION.oldZombieArms); }

        // Player Animations
        public static boolean disablePlayerTopple() { return getBoolTweak(AnimationTweak.DEATH_TOPPLE, ANIMATION.disableDeathTopple); }
        public static boolean oldBackwardsWalking() { return getBoolTweak(AnimationTweak.BACKWARD_WALK, ANIMATION.oldBackwardWalking); }
        public static boolean oldVerticalBobbing() { return getBoolTweak(AnimationTweak.BOB_VERTICAL, ANIMATION.oldVerticalBobbing); }
        public static boolean oldCollideBobbing() { return getBoolTweak(AnimationTweak.COLLIDE_BOB, ANIMATION.oldCollideBobbing); }
        public static boolean oldCreativeCrouch() { return getSidedBoolTweak(AnimationTweak.CREATIVE_CROUCH, ANIMATION.oldCreativeCrouch, SERVER_ANIMATION.oldCreativeCrouch); }
        public static boolean oldDirectionTilt() { return getSidedBoolTweak(AnimationTweak.DIRECTIONAL_DAMAGE, ANIMATION.oldDirectionalDamage, SERVER_ANIMATION.oldDirectionalDamage); }
        public static boolean oldRandomTilt() { return getBoolTweak(AnimationTweak.RANDOM_DAMAGE, ANIMATION.oldRandomDamage); }
        public static boolean oldSneaking() { return getBoolTweak(AnimationTweak.SNEAK_SMOOTH, ANIMATION.oldSneaking); }

        // Animation Constants
        public static float getSneakHeight() { return 1.41F; }
    }
}
