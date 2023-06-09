package mod.adrenix.nostalgic.common.config;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.client.config.ClientConfigCache;
import mod.adrenix.nostalgic.common.config.list.ConfigList;
import mod.adrenix.nostalgic.common.config.list.ListMap;
import mod.adrenix.nostalgic.common.config.list.ListSet;
import mod.adrenix.nostalgic.common.config.reflect.TweakStatus;
import mod.adrenix.nostalgic.common.config.tweak.*;
import mod.adrenix.nostalgic.network.packet.PacketS2CTweakUpdate;
import mod.adrenix.nostalgic.server.config.ServerConfig;
import mod.adrenix.nostalgic.server.config.ServerConfigCache;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.client.NetUtil;
import mod.adrenix.nostalgic.util.common.PacketUtil;
import net.minecraft.SharedConstants;
import net.minecraft.world.entity.MobCategory;

/**
 * This utility class acts as the interface for parts of the mod that need to know the state of tweaks.
 * This is used by both the client and server, so it is a requirement to keep vanilla client code out.
 */

public abstract class ModConfig
{
    /* Server Config Caches */

    private static final ServerConfig.EyeCandy SERVER_CANDY = ServerConfigCache.getCandy();
    private static final ServerConfig.Gameplay SERVER_GAMEPLAY = ServerConfigCache.getGameplay();
    private static final ServerConfig.Animation SERVER_ANIMATION = ServerConfigCache.getAnimation();

    /* Client Config Caches */

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
    public static boolean isTweakOn(Tweak tweak)
    {
        // Code is querying this tweak - load it
        loadTweak(tweak);

        // If the tweak is conflicting with another mod then block it here
        if (tweak.getCommonCache().isConflict())
            return false;

        // The server does not need to use a universal enabled/disable state
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
    private static boolean isTweakExempt(Tweak tweak)
    {
        return CONFIG.isModEnabled && (tweak.equals(CandyTweak.CREATIVE_HOTBAR) || tweak.equals(CandyTweak.SQUARE_BORDER) || tweak.equals(GameplayTweak.CART_BOOSTING));
    }

    /**
     * Loads a tweak by updating its status within the all tweak caches.
     * If the server is loading a tweak, then an update packet needs to be sent to all connected players.
     *
     * @param tweak The tweak to load.
     */
    private static void loadTweak(Tweak tweak)
    {
        TweakServerCache<?> cache = TweakServerCache.get(tweak);

        if (!tweak.isLoaded() || (cache != null && cache.getStatus().equals(TweakStatus.FAIL)))
        {
            tweak.setEnabled();

            // Server cache status syncing
            if (cache != null)
            {
                cache.setStatus(TweakStatus.LOADED);

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
    private static <T> T getSidedTweak(Tweak tweak, T client, T server)
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
    private static boolean getBoolTweak(Tweak tweak, boolean client)
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
    private static boolean getSidedBoolTweak(Tweak tweak, boolean client, boolean server)
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
    private static <E extends Enum<E> & DisabledTweak<E>> E getEnum(Tweak tweak, E client)
    {
        return !isTweakOn(tweak) ? client.getDisabledValue() : client;
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
    private static <E extends Enum<E> & DisabledTweak<E>> E getSidedEnum(Tweak tweak, E client, E server)
    {
        return NostalgicTweaks.isClient() ? getEnum(tweak, client) : server;
    }

    /**
     * Get a sided list map using the given tweak.
     * @param tweak The tweak to get a list map from.
     * @param <V> The value of map entries.
     * @return A list map with the given value type and associated with the given tweak.
     */
    @SuppressWarnings("unchecked") // List value types are assured from their connected tweaks
    private static <V> ListMap<V> getListMap(Tweak tweak)
    {
        loadTweak(tweak);

        if (NostalgicTweaks.isServer())
            return (ListMap<V>) ConfigList.getMapFromTweak(tweak.getServerCache());

        return (ListMap<V>) ConfigList.getMapFromTweak(tweak.getClientCache());
    }

    /**
     * Get a sided list set using the given tweak.
     * @param tweak The tweak to get a list set from.
     * @return A list set with associated with the given tweak.
     */
    private static ListSet getListSet(Tweak tweak)
    {
        loadTweak(tweak);

        if (NostalgicTweaks.isServer())
            return ConfigList.getSetFromTweak(tweak.getServerCache());

        return ConfigList.getSetFromTweak(tweak.getClientCache());
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
        public static boolean disableGlowSquidAmbience() { return getBoolTweak(SoundTweak.DISABLE_GLOW_SQUID_AMBIENCE, SOUND.disableGlowSquidAmbience); }
        public static boolean disableGlowSquidOther() { return getBoolTweak(SoundTweak.DISABLE_GLOW_SQUID_OTHER, SOUND.disableGlowSquidOther); }
        public static boolean disableNetherAmbience() { return getBoolTweak(SoundTweak.DISABLE_NETHER_AMBIENCE, SOUND.disableNetherAmbience); }
        public static boolean disableWaterAmbience() { return getBoolTweak(SoundTweak.DISABLE_WATER_AMBIENCE, SOUND.disableWaterAmbience); }
        public static boolean disableLavaAmbience() { return getBoolTweak(SoundTweak.DISABLE_LAVA_AMBIENCE, SOUND.disableLavaAmbience); }
        public static boolean disableGenericSwim() { return getBoolTweak(SoundTweak.DISABLE_GENERIC_SWIM, SOUND.disableGenericSwim); }
        public static boolean ignoreModdedStep() { return getBoolTweak(SoundTweak.IGNORE_MODDED_STEP, SOUND.ignoreModdedStep); }
        public static boolean disableFishDeath() { return getBoolTweak(SoundTweak.DISABLE_FISH_DEATH, SOUND.disableFishDeath); }
        public static boolean disableFishSwim() { return getBoolTweak(SoundTweak.DISABLE_FISH_SWIM, SOUND.disableFishSwim); }
        public static boolean disableFishHurt() { return getBoolTweak(SoundTweak.DISABLE_FISH_HURT, SOUND.disableFishHurt); }
        public static boolean disableXpPickup() { return getBoolTweak(SoundTweak.DISABLE_PICKUP, SOUND.disableXpPickup); }
        public static boolean disableFurnace() { return getBoolTweak(SoundTweak.DISABLE_FURNACE, SOUND.disableFurnace); }
        public static boolean disableXpLevel() { return getBoolTweak(SoundTweak.DISABLE_LEVEL, SOUND.disableXpLevel); }
        public static boolean disableLavaPop() { return getBoolTweak(SoundTweak.DISABLE_LAVA_POP, SOUND.disableLavaPop); }
        public static boolean disableGrowth() { return getBoolTweak(SoundTweak.DISABLE_GROWTH, SOUND.disableGrowth); }
        public static boolean disableChest() { return getBoolTweak(SoundTweak.DISABLE_CHEST, SOUND.disableChest); }
        public static boolean disableEnderChest() { return getBoolTweak(SoundTweak.DISABLE_ENDER_CHEST, SOUND.disableEnderChest); }
        public static boolean disableTrappedChest() { return getBoolTweak(SoundTweak.DISABLE_TRAPPED_CHEST, SOUND.disableTrappedChest); }
        public static boolean disableSquid() { return getBoolTweak(SoundTweak.DISABLE_SQUID, SOUND.disableSquid); }
        public static boolean disableDoor() { return getBoolTweak(SoundTweak.DISABLE_DOOR, SOUND.disableDoorPlace); }
        public static boolean disableBed() { return getBoolTweak(SoundTweak.DISABLE_BED, SOUND.disableBedPlace); }
        public static boolean oldAttack() { return getBoolTweak(SoundTweak.OLD_ATTACK, SOUND.oldAttack); }
        public static boolean oldDamage() { return getBoolTweak(SoundTweak.OLD_HURT, SOUND.oldHurt); }
        public static boolean oldChest() { return getBoolTweak(SoundTweak.OLD_CHEST, SOUND.oldChest); }
        public static boolean oldFall() { return getBoolTweak(SoundTweak.OLD_FALL, SOUND.oldFall); }
        public static boolean oldStep() { return getBoolTweak(SoundTweak.OLD_STEP, SOUND.oldStep); }
        public static boolean oldBed() { return getBoolTweak(SoundTweak.OLD_BED, SOUND.oldBed); }
        public static boolean oldXp() { return getBoolTweak(SoundTweak.OLD_XP, SOUND.oldXp); }
    }

    /* Eye Candy Tweaks */

    public static class Candy
    {
        /* Boolean Tweaks */

        // Block Candy
        public static TweakVersion.MissingTexture oldMissingTexture() { return getEnum(CandyTweak.OLD_MISSING_TEXTURE, CANDY.oldMissingTexture); }
        public static boolean fixAmbientOcclusion() { return getBoolTweak(CandyTweak.FIX_AO, CANDY.fixAmbientOcclusion); }
        public static boolean disableFlowerOffset() { return getBoolTweak(CandyTweak.DISABLE_FLOWER_OFFSET, CANDY.disableFlowerOffset); }
        public static boolean disableAllOffset() { return getBoolTweak(CandyTweak.DISABLE_ALL_OFFSET, CANDY.disableAllOffset); }
        public static boolean oldTrappedChest() { return getBoolTweak(CandyTweak.TRAPPED_CHEST, CANDY.oldTrappedChest); }
        public static boolean oldEnderChest() { return getBoolTweak(CandyTweak.ENDER_CHEST, CANDY.oldEnderChest); }
        public static boolean oldChestVoxel() { return getSidedBoolTweak(CandyTweak.CHEST_VOXEL, CANDY.oldChestVoxel, SERVER_CANDY.oldChestVoxel); }
        public static boolean oldChest() { return getBoolTweak(CandyTweak.CHEST, CANDY.oldChest); }

        // Block Candy - Torches
        private static boolean getModelState(Tweak tweak, boolean client)
        {
            return !ModTracker.SODIUM.isInstalled() && getBoolTweak(tweak, client);
        }

        public static boolean oldTorchBrightness() { return getBoolTweak(CandyTweak.TORCH_BRIGHTNESS, CANDY.oldTorchBrightness); }
        public static boolean oldRedstoneTorchModel() { return getModelState(CandyTweak.REDSTONE_TORCH_MODEL, CANDY.oldRedstoneTorchModel); }
        public static boolean oldSoulTorchModel() { return getModelState(CandyTweak.SOUL_TORCH_MODEL, CANDY.oldSoulTorchModel); }
        public static boolean oldTorchModel() { return getModelState(CandyTweak.TORCH_MODEL, CANDY.oldTorchModel); }

        // Block Candy - Outlines
        public static boolean oldStairOutline() { return getBoolTweak(CandyTweak.OLD_STAIR_OUTLINE, CANDY.oldStairOutline); }
        public static boolean oldFenceOutline() { return getBoolTweak(CandyTweak.OLD_FENCE_OUTLINE, CANDY.oldFenceOutline); }
        public static boolean oldSlabOutline() { return getBoolTweak(CandyTweak.OLD_SLAB_OUTLINE, CANDY.oldSlabOutline); }
        public static boolean oldWallOutline() { return getBoolTweak(CandyTweak.OLD_WALL_OUTLINE, CANDY.oldWallOutline); }
        public static ListSet getFullOutlines() { return getListSet(CandyTweak.FULL_BLOCK_OUTLINE); }

        // Interface - Generic & Title Candy
        public static TweakType.Corner oldOverlayCorner() { return getEnum(CandyTweak.VERSION_CORNER, CANDY.oldOverlayCorner); }
        public static boolean oldPlainSelectedItemName() { return getBoolTweak(CandyTweak.PLAIN_SELECTED_ITEM_NAME, CANDY.oldPlainSelectedItemName); }
        public static boolean oldNoSelectedItemName() { return getBoolTweak(CandyTweak.NO_SELECTED_ITEM_NAME, CANDY.oldNoSelectedItemName); }
        public static boolean oldDurabilityColors() { return getBoolTweak(CandyTweak.DURABILITY_COLORS, CANDY.oldDurabilityColors); }
        public static boolean includeModsOnPause() { return getBoolTweak(CandyTweak.PAUSE_MODS, CANDY.includeModsOnPause); }
        public static boolean oldVersionOverlay() { return getBoolTweak(CandyTweak.VERSION_OVERLAY, CANDY.oldVersionOverlay); }
        public static boolean oldLoadingScreens() { return getBoolTweak(CandyTweak.LOADING_SCREENS, CANDY.oldLoadingScreens); }
        public static boolean removeLoadingBar() { return getBoolTweak(CandyTweak.REMOVE_LOADING_BAR, CANDY.removeLoadingBar); }
        public static boolean oldButtonHover() { return getBoolTweak(CandyTweak.BUTTON_HOVER, CANDY.oldButtonHover); }

        // Interface - Chat Candy
        public static int getChatOffset() { return CANDY.chatOffset; }
        public static boolean disableSignatureBoxes() { return getBoolTweak(CandyTweak.SIGNATURE_BOXES, CANDY.disableSignatureBoxes); }
        public static boolean oldChatInput() { return getBoolTweak(CandyTweak.CHAT_INPUT, CANDY.oldChatInput); }
        public static boolean oldChatBox() { return getBoolTweak(CandyTweak.CHAT_BOX, CANDY.oldChatBox); }

        // Interface - Window Title
        public static String getWindowTitle() { return CANDY.windowTitleText; }
        public static boolean enableWindowTitle() { return getBoolTweak(CandyTweak.ENABLE_WINDOW_TITLE, CANDY.enableWindowTitle); }
        public static boolean matchVersionOverlay() { return getBoolTweak(CandyTweak.MATCH_VERSION_OVERLAY, CANDY.matchVersionOverlay); }

        // Interface - Screen Candy
        public static TweakType.InventoryShield getInventoryShield() { return getEnum(CandyTweak.INVENTORY_SHIELD, CANDY.inventoryShield); }
        public static TweakType.GuiBackground oldGuiBackground() { return getEnum(CandyTweak.OLD_GUI_BACKGROUND, CANDY.oldGuiBackground); }
        public static TweakType.RecipeBook getInventoryBook() { return getEnum(CandyTweak.INVENTORY_BOOK, CANDY.inventoryBook); }
        public static TweakType.RecipeBook getCraftingBook() { return getEnum(CandyTweak.CRAFTING_RECIPE, CANDY.craftingBook); }
        public static TweakType.RecipeBook getFurnaceBook() { return getEnum(CandyTweak.FURNACE_RECIPE, CANDY.furnaceBook); }
        public static TweakType.DebugChart getDebugChart() { return getEnum(CandyTweak.DEBUG_FPS_CHART, CANDY.fpsChart); }
        public static String customTopGradient() { return CANDY.customTopGradient; }
        public static String customBottomGradient() { return CANDY.customBottomGradient; }
        public static String debugBackgroundColor() { return CANDY.debugBackgroundColor; }
        public static boolean removeExtraPauseButtons() { return getBoolTweak(CandyTweak.PAUSE_REMOVE_EXTRA, CANDY.removeExtraPauseButtons); }
        public static boolean showDebugTextShadow() { return getBoolTweak(CandyTweak.DEBUG_SHOW_SHADOW, CANDY.showDebugTextShadow); }
        public static boolean showDebugBackground() { return getBoolTweak(CandyTweak.DEBUG_SHOW_COLOR, CANDY.showDebugBackground); }
        public static boolean showDebugTargetData() { return getBoolTweak(CandyTweak.DEBUG_TARGETED, CANDY.showDebugTargetData); }
        public static boolean showDebugFacingData() { return getBoolTweak(CandyTweak.DEBUG_FACING, CANDY.showDebugFacingData); }
        public static boolean showDebugLightData() { return getBoolTweak(CandyTweak.DEBUG_LIGHT, CANDY.showDebugLightData); }
        public static boolean showDebugBiomeData() { return getBoolTweak(CandyTweak.DEBUG_BIOME, CANDY.showDebugBiomeData); }
        public static boolean showDebugGpuUsage() { return getBoolTweak(CandyTweak.DEBUG_GPU, CANDY.showDebugGpuUsage); }
        public static boolean customGuiBackground() { return getBoolTweak(CandyTweak.CUSTOM_GUI_BACKGROUND, CANDY.customGuiBackground); }
        public static boolean disableEmptyShield() { return getBoolTweak(CandyTweak.DISABLE_EMPTY_SHIELD, CANDY.disableEmptyShieldTexture); }
        public static boolean disableEmptyArmor() { return getBoolTweak(CandyTweak.DISABLE_EMPTY_ARMOR, CANDY.disableEmptyArmorTexture); }
        public static boolean invertPlayerLight() { return getBoolTweak(CandyTweak.INVERTED_PLAYER_LIGHTING, CANDY.invertedPlayerLighting); }
        public static boolean oldCraftingScreen() { return getBoolTweak(CandyTweak.CRAFTING_SCREEN, CANDY.oldCraftingScreen); }
        public static boolean invertBlockLight() { return getBoolTweak(CandyTweak.INVERTED_BLOCK_LIGHTING, CANDY.invertedBlockLighting); }
        public static boolean oldPieBackground() { return getBoolTweak(CandyTweak.OLD_PIE_BACKGROUND, CANDY.oldPieChartBackground); }
        public static boolean oldFurnaceScreen() { return getBoolTweak(CandyTweak.FURNACE_SCREEN, CANDY.oldFurnaceScreen); }
        public static boolean displayPieChart() { return getBoolTweak(CandyTweak.DEBUG_PIE_CHART, CANDY.showDebugPieChart); }
        public static boolean displayTpsChart() { return getBoolTweak(CandyTweak.DEBUG_TPS_CHART, CANDY.showDebugTpsChart); }
        public static boolean oldAnvilScreen() { return getBoolTweak(CandyTweak.ANVIL_SCREEN, CANDY.oldAnvilScreen); }
        public static boolean debugEntityId() { return getSidedBoolTweak(CandyTweak.DEBUG_ENTITY_ID, CANDY.debugEntityId, SERVER_CANDY.debugEntityId); }
        public static boolean oldInventory() { return getBoolTweak(CandyTweak.OLD_INVENTORY, CANDY.oldInventory); }

        // Interface - Tooltip Candy
        public static boolean addDyeTip() { return getBoolTweak(CandyTweak.DYE_TIP, CANDY.showDyeTip); }
        public static boolean oldTooltips() { return getBoolTweak(CandyTweak.TOOLTIP_BOXES, CANDY.oldTooltipBoxes); }
        public static boolean addModifiersTip() { return getBoolTweak(CandyTweak.MODIFIERS_TIP, CANDY.showModifiersTip); }
        public static boolean addEnchantmentTip() { return getBoolTweak(CandyTweak.ENCHANTMENT_TIP, CANDY.showEnchantmentTip); }
        public static boolean oldNoItemTooltips() { return getBoolTweak(CandyTweak.NO_ITEM_TOOLTIPS, CANDY.oldNoItemTooltips); }

        // Item Candy
        public static boolean fixItemModelGaps() { return getBoolTweak(CandyTweak.FIX_ITEM_MODEL_GAP, CANDY.fixItemModelGap); }
        public static boolean oldDamageArmorTint() { return getBoolTweak(CandyTweak.DAMAGE_ARMOR_TINT, CANDY.oldDamageArmorTint); }
        public static boolean oldFlatEnchantment() { return getBoolTweak(CandyTweak.FLAT_ENCHANTED_ITEMS, CANDY.old2dEnchantedItems) && oldFloatingItems(); }
        public static boolean oldFlatRendering() { return getBoolTweak(CandyTweak.FLAT_RENDERING, CANDY.old2dRendering); }
        public static boolean oldFloatingItems() { return getBoolTweak(CandyTweak.FLAT_ITEMS, CANDY.old2dItems); }
        public static boolean oldFlatThrowing() { return getBoolTweak(CandyTweak.FLAT_THROW_ITEMS, CANDY.old2dThrownItems); }
        public static boolean oldItemHolding() { return getBoolTweak(CandyTweak.ITEM_HOLDING, CANDY.oldItemHolding); }
        public static boolean oldItemMerging() { return getSidedBoolTweak(CandyTweak.ITEM_MERGING, CANDY.oldItemMerging, SERVER_CANDY.oldItemMerging); }
        public static boolean oldFlatFrames() { return getBoolTweak(CandyTweak.FLAT_FRAMES, CANDY.old2dFrames); }
        public static boolean oldFlatColors() { return getBoolTweak(CandyTweak.FLAT_COLORS, CANDY.old2dColors); }

        public static ListSet getIgnoredItemHoldings() { return getListSet(CandyTweak.IGNORED_ITEM_HOLDING); }

        // Lighting Candy
        public static boolean disableLightFlicker() { return getBoolTweak(CandyTweak.LIGHT_FLICKER, CANDY.disableLightFlicker); }
        public static boolean disableBrightness() { return getBoolTweak(CandyTweak.DISABLE_BRIGHTNESS, CANDY.disableBrightness); }
        public static boolean fixChunkBorderLag() { return getBoolTweak(CandyTweak.FIX_CHUNK_BORDER_LAG, CANDY.fixChunkBorderLag); }
        public static boolean oldLightRendering() { return getBoolTweak(CandyTweak.LIGHT_RENDERING, CANDY.oldLightRendering); }
        public static boolean oldSmoothLighting() { return getBoolTweak(CandyTweak.SMOOTH_LIGHTING, CANDY.oldSmoothLighting); }
        public static boolean oldNetherLighting() { return getBoolTweak(CandyTweak.NETHER_LIGHTING, CANDY.oldNetherLighting); }
        public static boolean oldLeavesLighting() { return getBoolTweak(CandyTweak.LEAVES_LIGHTING, CANDY.oldLeavesLighting); }
        public static boolean oldWaterLighting() { return getBoolTweak(CandyTweak.WATER_LIGHTING, CANDY.oldWaterLighting); }
        public static boolean oldClassicLight() { return getSidedBoolTweak(CandyTweak.CLASSIC_LIGHTING, CANDY.oldClassicLighting, SERVER_CANDY.oldClassicLighting); }
        public static boolean oldLightColor() { return getBoolTweak(CandyTweak.LIGHT_COLOR, CANDY.oldLightColor); }

        // Particle Candy
        public static boolean disableModelDestructionParticles() { return getBoolTweak(CandyTweak.NO_MODEL_DESTRUCTION_PARTICLES, CANDY.disableModelDestructionParticles); }
        public static boolean unoptimizedExplosionParticles() { return getBoolTweak(CandyTweak.UNOPTIMIZED_EXPLOSION_PARTICLES, CANDY.unoptimizedExplosionParticles); }
        public static boolean disableUnderwaterParticles() { return getBoolTweak(CandyTweak.NO_UNDERWATER_PARTICLES, CANDY.disableUnderwaterParticles); }
        public static boolean oldMixedExplosionParticles() { return getBoolTweak(CandyTweak.MIXED_EXPLOSION_PARTICLES, CANDY.oldMixedExplosionParticles); }
        public static boolean oldNoCriticalHitParticles() { return getBoolTweak(CandyTweak.NO_CRIT_PARTICLES, CANDY.oldNoCritParticles); }
        public static boolean disableSprintingParticles() { return getBoolTweak(CandyTweak.NO_SPRINTING_PARTICLES, CANDY.disableSprintingParticles); }
        public static boolean oldNoEnchantHitParticles() { return getBoolTweak(CandyTweak.NO_MAGIC_HIT_PARTICLES, CANDY.oldNoMagicHitParticles); }
        public static boolean disableFallingParticles() { return getBoolTweak(CandyTweak.NO_FALLING_PARTICLES, CANDY.disableFallingParticles); }
        public static boolean disableGrowthParticles() { return getBoolTweak(CandyTweak.NO_GROWTH_PARTICLES, CANDY.disableGrowthParticles); }
        public static boolean disableNetherParticles() { return getBoolTweak(CandyTweak.NO_NETHER_PARTICLES, CANDY.disableNetherParticles); }
        public static boolean disableLeverParticles() { return getBoolTweak(CandyTweak.NO_LEVER_PARTICLES, CANDY.disableLeverParticles); }
        public static boolean oldExplosionParticles() { return getBoolTweak(CandyTweak.EXPLOSION_PARTICLES, CANDY.oldExplosionParticles); }
        public static boolean disableLavaParticles() { return getBoolTweak(CandyTweak.NO_LAVA_PARTICLES, CANDY.disableLavaParticles); }
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
        public static boolean oldAlphaLogo() { return getBoolTweak(CandyTweak.ALPHA_LOGO, CANDY.oldAlphaLogo); }
        public static boolean uncapTitleFPS() { return getBoolTweak(CandyTweak.UNCAP_TITLE_FPS, CANDY.uncapTitleFPS); }

        // World Candy
        public static boolean disableSunriseSunsetColor() { return getBoolTweak(CandyTweak.DISABLE_SUNRISE_SUNSET_COLOR, CANDY.disableSunriseSunsetColors); }
        public static boolean oldSunriseSunsetFog() { return getBoolTweak(CandyTweak.SUNRISE_SUNSET_FOG, CANDY.oldSunriseSunsetFog); }
        public static boolean oldBlueVoidOverride() { return getBoolTweak(CandyTweak.BLUE_VOID_OVERRIDE, CANDY.oldBlueVoidOverride); }
        public static boolean oldDynamicSkyColor() { return getBoolTweak(CandyTweak.DYNAMIC_SKY_COLOR, CANDY.oldDynamicSkyColor); }
        public static boolean oldDynamicFogColor() { return getBoolTweak(CandyTweak.DYNAMIC_FOG_COLOR, CANDY.oldDynamicFogColor); }
        public static boolean smoothWaterDensity() { return getBoolTweak(CandyTweak.SMOOTH_WATER_DENSITY, CANDY.smoothWaterDensity); }
        public static boolean oldWaterFogDensity() { return getBoolTweak(CandyTweak.WATER_FOG_DENSITY, CANDY.oldWaterFogDensity); }
        public static boolean oldDarkVoidHeight() { return getBoolTweak(CandyTweak.DARK_VOID_HEIGHT, CANDY.oldDarkVoidHeight); }
        public static boolean oldSunriseAtNorth() { return getBoolTweak(CandyTweak.SUNRISE_AT_NORTH, CANDY.oldSunriseAtNorth); }
        public static boolean disableHorizonFog() { return getBoolTweak(CandyTweak.DISABLE_HORIZON_FOG, CANDY.disableHorizonFog); }
        public static boolean smoothWaterColor() { return getBoolTweak(CandyTweak.SMOOTH_WATER_COLOR, CANDY.smoothWaterColor); }
        public static boolean oldWaterFogColor() { return getBoolTweak(CandyTweak.WATER_FOG_COLOR, CANDY.oldWaterFogColor); }
        public static boolean oldSquareBorder() { return getSidedBoolTweak(CandyTweak.SQUARE_BORDER, CANDY.oldSquareBorder, SERVER_CANDY.oldSquareBorder); }
        public static boolean oldNetherFog() { return getBoolTweak(CandyTweak.NETHER_FOG, CANDY.oldNetherFog); }
        public static boolean oldNetherSky() { return getBoolTweak(CandyTweak.NETHER_SKY, CANDY.oldNetherSky); }
        public static boolean oldNameTags() { return getBoolTweak(CandyTweak.NAME_TAGS, CANDY.oldNameTags); }
        public static boolean oldDarkFog() { return getBoolTweak(CandyTweak.DARK_FOG, CANDY.oldDarkFog); }

        // Custom Fog
        public static String getTerrainFogColor() { return CANDY.customTerrainFogColor; }
        public static String getNetherFogColor() { return CANDY.customNetherFogColor; }
        public static boolean isTerrainFogCustom() { return getBoolTweak(CandyTweak.CUSTOM_TERRAIN_FOG, CANDY.customTerrainFog); }
        public static boolean isNetherFogCustom() { return getBoolTweak(CandyTweak.CUSTOM_NETHER_FOG, CANDY.customNetherFog); }

        // Custom Sky
        public static String getWorldSkyColor() { return CANDY.customWorldSkyColor; }
        public static String getNetherSkyColor() { return CANDY.customNetherSkyColor; }
        public static boolean isWorldSkyCustom() { return getBoolTweak(CandyTweak.CUSTOM_WORLD_SKY, CANDY.customWorldSky); }
        public static boolean isNetherSkyCustom() { return getBoolTweak(CandyTweak.CUSTOM_NETHER_SKY, CANDY.customNetherSky); }

        // Custom Void
        public static String getVoidSkyColor() { return CANDY.customVoidSkyColor; }
        public static boolean isVoidSkyCustom() { return getBoolTweak(CandyTweak.CUSTOM_VOID_SKY, CANDY.customVoidSky); }

        // Void Fog
        public static String getVoidFogColor() { return CANDY.voidFogColor; }
        public static boolean disableVoidFog() { return getBoolTweak(CandyTweak.DISABLE_VOID_FOG, CANDY.disableVoidFog); }
        public static boolean creativeVoidFog() { return getBoolTweak(CandyTweak.CREATIVE_VOID_FOG, CANDY.creativeVoidFog); }
        public static boolean creativeVoidParticles() { return getBoolTweak(CandyTweak.CREATIVE_VOID_PARTICLE, CANDY.creativeVoidParticles); }
        public static boolean shouldLightRemoveVoidFog() { return getBoolTweak(CandyTweak.LIGHT_REMOVES_VOID_FOG, CANDY.lightRemovesVoidFog); }

        /* Version Tweaks */

        public static TweakVersion.WorldFog getWorldFog() { return getEnum(CandyTweak.WORLD_FOG, CANDY.oldWorldFog); }
        public static TweakVersion.TitleLayout getButtonLayout() { return getEnum(CandyTweak.TITLE_BUTTON_LAYOUT, CANDY.oldButtonLayout); }
        public static TweakVersion.PauseLayout getPauseLayout() { return getEnum(CandyTweak.PAUSE_LAYOUT, CANDY.oldPauseMenu); }
        public static TweakVersion.Overlay getLoadingOverlay() { return getEnum(CandyTweak.LOADING_OVERLAY, CANDY.oldLoadingOverlay); }
        public static TweakVersion.Generic getDebugScreen() { return getEnum(CandyTweak.DEBUG_SCREEN, CANDY.oldDebug); }
        public static TweakVersion.Generic getBlueVoid() { return getEnum(CandyTweak.BLUE_VOID, CANDY.oldBlueVoid); }
        public static TweakVersion.Generic getStars() { return getEnum(CandyTweak.STARS, CANDY.oldStars); }
        public static TweakVersion.Hotbar getHotbar() { return getSidedEnum(CandyTweak.CREATIVE_HOTBAR, CANDY.oldCreativeHotbar, SERVER_CANDY.oldCreativeHotbar); }
        public static TweakVersion.FogColor getUniversalFog() { return getEnum(CandyTweak.UNIVERSAL_FOG_COLOR, CANDY.universalFogColor); }
        public static TweakVersion.SkyColor getUniversalSky() { return getEnum(CandyTweak.UNIVERSAL_SKY_COLOR, CANDY.universalSkyColor); }

        /* String Tweaks */

        private static final String MINECRAFT_VERSION = SharedConstants.getCurrentVersion().getName();

        public static String getOverlayText() { return parseColor(CANDY.oldOverlayText, MINECRAFT_VERSION); }
        public static String getVersionText() { return parseColor(CANDY.titleVersionText, MINECRAFT_VERSION); }

        /* Integer Tweaks */

        public static int getVoidParticleDensity() { return isTweakOn(CandyTweak.VOID_PARTICLE_DENSITY) ? CANDY.voidParticleDensity : DefaultConfig.Candy.VOID_PARTICLE_DENSITY; }
        public static int getVoidParticleRadius() { return isTweakOn(CandyTweak.VOID_PARTICLE_RADIUS) ? CANDY.voidParticleRadius : DefaultConfig.Candy.VOID_PARTICLE_RADIUS; }
        public static int getVoidParticleStart() { return isTweakOn(CandyTweak.VOID_PARTICLE_START) ? CANDY.voidParticleStart + 64 : DefaultConfig.Candy.VOID_PARTICLE_START + 64; }
        public static int getVoidFogEncroach() { return isTweakOn(CandyTweak.VOID_FOG_ENCROACH) ? CANDY.voidFogEncroach : DefaultConfig.Candy.VOID_FOG_ENCROACH; }
        public static int getMaxBlockLight() { return isTweakOn(CandyTweak.MAX_BLOCK_LIGHT) ? CANDY.maxBlockLight : DefaultConfig.Candy.MAX_BLOCK_LIGHT; }
        public static int getVoidFogStart() { return isTweakOn(CandyTweak.VOID_FOG_START) ? CANDY.voidFogStart + 64 : DefaultConfig.Candy.VOID_FOG_START + 64; }
        public static int getCloudHeight() { return isTweakOn(CandyTweak.CLOUD_HEIGHT) ? CANDY.oldCloudHeight : DefaultConfig.Candy.DISABLED_CLOUD_HEIGHT; }
        public static int getItemMergeLimit() { return isTweakOn(CandyTweak.ITEM_MERGE_LIMIT) ? getSidedTweak(CandyTweak.ITEM_MERGE_LIMIT, CANDY.itemMergeLimit, SERVER_CANDY.itemMergeLimit) : 1; }
    }

    /* Gameplay Tweaks */

    public static class Gameplay
    {
        // Combat System
        public static int instantBowSpeed()
        {
            return isTweakOn(GameplayTweak.ARROW_SPEED) ? getSidedTweak(GameplayTweak.ARROW_SPEED, GAMEPLAY.arrowSpeed, SERVER_GAMEPLAY.arrowSpeed) : 0;
        }

        public static boolean disableCriticalHit() { return getSidedBoolTweak(GameplayTweak.DISABLE_CRITICAL_HIT, GAMEPLAY.disableCriticalHit, SERVER_GAMEPLAY.disableCriticalHit); }
        public static boolean oldDamageValues() { return getSidedBoolTweak(GameplayTweak.DAMAGE_VALUES, GAMEPLAY.oldDamageValues, SERVER_GAMEPLAY.oldDamageValues); }
        public static boolean disableMissTime() { return getSidedBoolTweak(GameplayTweak.DISABLE_MISS_TIMER, GAMEPLAY.disableMissTimer, SERVER_GAMEPLAY.disableMissTimer); }
        public static boolean disableCooldown() { return getSidedBoolTweak(GameplayTweak.DISABLE_COOLDOWN, GAMEPLAY.disableCooldown, SERVER_GAMEPLAY.disableCooldown); }
        public static boolean invincibleBow() { return getSidedBoolTweak(GameplayTweak.INVINCIBLE_BOW, GAMEPLAY.invincibleBow, SERVER_GAMEPLAY.invincibleBow); }
        public static boolean disableSweep() { return getSidedBoolTweak(GameplayTweak.DISABLE_SWEEP, GAMEPLAY.disableSweep, SERVER_GAMEPLAY.disableSweep); }
        public static boolean instantBow() { return getSidedBoolTweak(GameplayTweak.INSTANT_BOW, GAMEPLAY.instantBow, SERVER_GAMEPLAY.instantBow); }

        // Bugs
        public static boolean oldLadderGap() { return getSidedBoolTweak(GameplayTweak.LADDER_GAP, GAMEPLAY.oldLadderGap, SERVER_GAMEPLAY.oldLadderGap); }
        public static boolean oldSquidMilk() { return getSidedBoolTweak(GameplayTweak.SQUID_MILK, GAMEPLAY.oldSquidMilking, SERVER_GAMEPLAY.oldSquidMilking); }

        // Mob System
        public static int getMonsterSpawnCap()
        {
            return isTweakOn(GameplayTweak.MONSTER_CAP) ? getSidedTweak(GameplayTweak.MONSTER_CAP, GAMEPLAY.monsterSpawnCap, SERVER_GAMEPLAY.monsterSpawnCap) : MobCategory.MONSTER.getMaxInstancesPerChunk();
        }

        public static int getAnimalSpawnCap()
        {
            return isTweakOn(GameplayTweak.ANIMAL_CAP) ? getSidedTweak(GameplayTweak.ANIMAL_CAP, GAMEPLAY.animalSpawnCap, SERVER_GAMEPLAY.animalSpawnCap) : MobCategory.CREATURE.getMaxInstancesPerChunk();
        }

        public static boolean disableSheepEatGrass() { return getSidedBoolTweak(GameplayTweak.SHEEP_EAT_GRASS, GAMEPLAY.disableSheepEatGrass, SERVER_GAMEPLAY.disableSheepEatGrass); }
        public static boolean disableAnimalPanic() { return getSidedBoolTweak(GameplayTweak.ANIMAL_PANIC, GAMEPLAY.disableAnimalPanic, SERVER_GAMEPLAY.disableAnimalPanic); }
        public static boolean oldAnimalSpawning() { return getSidedBoolTweak(GameplayTweak.ANIMAL_SPAWNING, GAMEPLAY.oldAnimalSpawning, SERVER_GAMEPLAY.oldAnimalSpawning); }
        public static boolean oldSheepPunching() { return getSidedBoolTweak(GameplayTweak.SHEEP_PUNCHING, GAMEPLAY.oldSheepPunching, SERVER_GAMEPLAY.oldSheepPunching); }
        public static boolean oneWoolPunch() { return getSidedBoolTweak(GameplayTweak.ONE_WOOL_PUNCH, GAMEPLAY.oneWoolPunch, SERVER_GAMEPLAY.oneWoolPunch); }

        // Mob Drops
        public static boolean oldZombiePigmenDrops() { return getSidedBoolTweak(GameplayTweak.ZOMBIE_PIGMEN_DROPS, GAMEPLAY.oldZombiePigmenDrops, SERVER_GAMEPLAY.oldZombiePigmenDrops); }
        public static boolean oldSkeletonDrops() { return getSidedBoolTweak(GameplayTweak.SKELETON_DROPS, GAMEPLAY.oldSkeletonDrops, SERVER_GAMEPLAY.oldSkeletonDrops); }
        public static boolean oldChickenDrops() { return getSidedBoolTweak(GameplayTweak.CHICKEN_DROPS, GAMEPLAY.oldChickenDrops, SERVER_GAMEPLAY.oldChickenDrops); }
        public static boolean oldZombieDrops() { return getSidedBoolTweak(GameplayTweak.ZOMBIE_DROPS, GAMEPLAY.oldZombieDrops, SERVER_GAMEPLAY.oldZombieDrops); }
        public static boolean oldSpiderDrops() { return getSidedBoolTweak(GameplayTweak.SPIDER_DROPS, GAMEPLAY.oldSpiderDrops, SERVER_GAMEPLAY.oldSpiderDrops); }
        public static boolean oldSheepDrops() { return getSidedBoolTweak(GameplayTweak.SHEEP_DROPS, GAMEPLAY.oldSheepDrops, SERVER_GAMEPLAY.oldSheepDrops); }
        public static boolean oldCowDrops() { return getSidedBoolTweak(GameplayTweak.COW_DROPS, GAMEPLAY.oldCowDrops, SERVER_GAMEPLAY.oldCowDrops); }
        public static boolean oldPigDrops() { return getSidedBoolTweak(GameplayTweak.PIG_DROPS, GAMEPLAY.oldPigDrops, SERVER_GAMEPLAY.oldPigDrops); }

        public static boolean oldZombieVillagerDrops() { return getSidedBoolTweak(GameplayTweak.ZOMBIE_VILLAGER_DROPS, GAMEPLAY.oldStyleZombieVillagerDrops, SERVER_GAMEPLAY.oldStyleZombieVillagerDrops); }
        public static boolean oldCaveSpiderDrops() { return getSidedBoolTweak(GameplayTweak.CAVE_SPIDER_DROPS, GAMEPLAY.oldStyleCaveSpiderDrops, SERVER_GAMEPLAY.oldStyleCaveSpiderDrops); }
        public static boolean oldMooshroomDrops() { return getSidedBoolTweak(GameplayTweak.MOOSHROOM_DROPS, GAMEPLAY.oldStyleMooshroomDrops, SERVER_GAMEPLAY.oldStyleMooshroomDrops); }
        public static boolean oldDrownedDrops() { return getSidedBoolTweak(GameplayTweak.DROWNED_DROPS, GAMEPLAY.oldStyleDrownedDrops, SERVER_GAMEPLAY.oldStyleDrownedDrops); }
        public static boolean oldRabbitDrops() { return getSidedBoolTweak(GameplayTweak.RABBIT_DROPS, GAMEPLAY.oldStyleRabbitDrops, SERVER_GAMEPLAY.oldStyleRabbitDrops); }
        public static boolean oldStrayDrops() { return getSidedBoolTweak(GameplayTweak.STRAY_DROPS, GAMEPLAY.oldStyleStrayDrops, SERVER_GAMEPLAY.oldStyleStrayDrops); }
        public static boolean oldHuskDrops() { return getSidedBoolTweak(GameplayTweak.HUSK_DROPS, GAMEPLAY.oldStyleHuskDrops, SERVER_GAMEPLAY.oldStyleHuskDrops); }

        // Experience System
        public static TweakType.Corner alternativeProgressCorner() { return getEnum(GameplayTweak.XP_PROGRESS_CORNER, GAMEPLAY.altXpProgressCorner); }
        public static TweakType.Corner alternativeLevelCorner() { return getEnum(GameplayTweak.XP_LEVEL_CORNER, GAMEPLAY.altXpLevelCorner); }
        public static boolean displayAlternativeLevelCreative() { return getBoolTweak(GameplayTweak.SHOW_XP_LEVEL_CREATIVE, GAMEPLAY.showXpLevelInCreative); }
        public static boolean displayAlternativeProgressCreative() { return getBoolTweak(GameplayTweak.SHOW_XP_PROGRESS_CREATIVE, GAMEPLAY.showXpProgressInCreative); }
        public static boolean displayAlternativeProgressText() { return getBoolTweak(GameplayTweak.SHOW_XP_PROGRESS, GAMEPLAY.showXpProgressText); }
        public static boolean displayAlternativeLevelText() { return getBoolTweak(GameplayTweak.SHOW_XP_LEVEL, GAMEPLAY.showXpLevelText); }
        public static boolean useDynamicProgressColor() { return getBoolTweak(GameplayTweak.USE_DYNAMIC_PROGRESS_COLOR, GAMEPLAY.useDynamicProgressColor); }
        public static boolean disableExperienceBar() { return getBoolTweak(GameplayTweak.DISABLE_EXP_BAR, GAMEPLAY.disableExperienceBar); }
        public static boolean disableOrbRendering() { return getBoolTweak(GameplayTweak.ORB_RENDERING, GAMEPLAY.disableOrbRendering); }
        public static boolean disableEnchantTable() { return getSidedBoolTweak(GameplayTweak.ENCHANT_TABLE, GAMEPLAY.disableEnchantTable, SERVER_GAMEPLAY.disableEnchantTable); }
        public static boolean disableOrbSpawn() { return getSidedBoolTweak(GameplayTweak.ORB_SPAWN, GAMEPLAY.disableOrbSpawn, SERVER_GAMEPLAY.disableOrbSpawn); }
        public static boolean disableAnvil() { return getSidedBoolTweak(GameplayTweak.ANVIL, GAMEPLAY.disableAnvil, SERVER_GAMEPLAY.disableAnvil); }

        // Game Mechanics
        public static boolean cartBoosting() { return getSidedBoolTweak(GameplayTweak.CART_BOOSTING, GAMEPLAY.cartBoosting, SERVER_GAMEPLAY.cartBoosting); }
        public static boolean disableBedBounce() { return getSidedBoolTweak(GameplayTweak.BED_BOUNCE, GAMEPLAY.disableBedBounce, SERVER_GAMEPLAY.disableBedBounce); }
        public static boolean tilledGrassSeeds() { return getSidedBoolTweak(GameplayTweak.TILLED_GRASS_SEEDS, GAMEPLAY.tilledGrassSeeds, SERVER_GAMEPLAY.tilledGrassSeeds); }
        public static boolean instantBonemeal() { return getSidedBoolTweak(GameplayTweak.INSTANT_BONE_MEAL, GAMEPLAY.instantBonemeal, SERVER_GAMEPLAY.instantBonemeal); }
        public static boolean leftClickButton() { return getSidedBoolTweak(GameplayTweak.LEFT_CLICK_BUTTON, GAMEPLAY.leftClickButton, SERVER_GAMEPLAY.leftClickButton); }
        public static boolean leftClickLever() { return getSidedBoolTweak(GameplayTweak.LEFT_CLICK_LEVER, GAMEPLAY.leftClickLever, SERVER_GAMEPLAY.leftClickLever); }
        public static boolean leftClickDoor() { return getSidedBoolTweak(GameplayTweak.LEFT_CLICK_DOOR, GAMEPLAY.leftClickDoor, SERVER_GAMEPLAY.leftClickDoor); }
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

        public static ListMap<Integer> getFoodHealth() { return getListMap(GameplayTweak.CUSTOM_FOOD_HEALTH); }
        public static ListMap<Integer> getFoodStacking() { return getListMap(GameplayTweak.CUSTOM_FOOD_STACKING); }
        public static ListMap<Integer> getItemStacking() { return getListMap(GameplayTweak.CUSTOM_ITEM_STACKING); }

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
        public static boolean oldClassicSwing() { return getBoolTweak(AnimationTweak.CLASSIC_SWING, ANIMATION.oldClassicSwing); }
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
        public static boolean oldRandomTilt() { return getBoolTweak(AnimationTweak.RANDOM_DAMAGE, ANIMATION.oldRandomDamage); }
        public static boolean oldSneaking() { return getBoolTweak(AnimationTweak.SNEAK_SMOOTH, ANIMATION.oldSneaking); }

        // Animation Constants
        public static float getSneakHeight() { return 1.41F; }
    }
}
