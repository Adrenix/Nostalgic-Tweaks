package mod.adrenix.nostalgic;

import com.google.common.base.Suppliers;
import dev.architectury.networking.NetworkChannel;
import dev.architectury.platform.Platform;
import mod.adrenix.nostalgic.client.config.ClientConfigCache;
import mod.adrenix.nostalgic.common.NostalgicConnection;
import mod.adrenix.nostalgic.common.config.DefaultConfig;
import mod.adrenix.nostalgic.network.PacketRegistry;
import mod.adrenix.nostalgic.server.config.ServerConfigCache;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.common.TextUtil;
import mod.adrenix.nostalgic.util.common.log.LogColor;
import mod.adrenix.nostalgic.util.common.log.ModLogger;
import net.fabricmc.api.EnvType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * This is the mod's main class. It is responsible for defining important fields and trackers such as identifiers, what
 * mods are installed, network logic, sided state checks, and sided initialization.
 */

public class NostalgicTweaks
{
    /* Mod ID & Logger */

    /**
     * This is the mod's unique identifier. This should never change. If a change is required, then it is important that
     * mod developers using our API are properly informed of the change.
     */
    public static final String MOD_ID = "nostalgic_tweaks";

    /**
     * This is the mod's display name. This can change, but should not be required since it closely resembles the mod's
     * unique identifier.
     */
    public static final String MOD_NAME = "Nostalgic Tweaks";

    /**
     * This is a unique logger instance. It will change the output visible in the debugging console and in a player's
     * runtime console.
     */
    public static final ModLogger LOGGER = new ModLogger(MOD_NAME);

    /**
     * Uses Architectury to get the mod's current version. Getting the mod's version is mod loader platform dependant.
     * A memoized supplier is used here since the version will never change during runtime.
     */
    public static final Supplier<String> VERSION = Suppliers.memoize(Platform.getMod(MOD_ID)::getVersion);

    /**
     * This will give a version number where all additional information attached to a version string is removed.
     * @return A tiny version number formatted as '#.#.#'.
     */
    public static String getTinyVersion() { return TextUtil.extract(VERSION.get(), "(\\d\\.\\d\\.\\d)"); }

    /**
     * This will give the beta version number. If the build is not in beta, then an empty string is returned.
     * @return A beta version number formatted as 'Beta-#' or 'Beta-#.#'.
     */
    public static String getBetaVersion() { return TextUtil.extract(VERSION.get(), "Beta-\\d+(?:\\.\\d+)?"); }

    /**
     * This will follow the same filename format that is used on the mod's approved distribution sites.
     * @return A version number with a prefixed title case mod loader name and Minecraft version.
     */
    public static String getFullVersion()
    {
        return String.format("%s-%s-%s", TextUtil.toTitleCase(getLoader()), Platform.getMinecraftVersion(), VERSION.get());
    }

    /**
     * This will follow the same filename format as above but without the mod loader name.
     * @return A version number with a prefixed Minecraft version.
     */
    public static String getShortVersion()
    {
        return String.format("%s-%s", Platform.getMinecraftVersion(), VERSION.get());
    }

    /**
     * Check if the logger is in debug mode.
     * @return Whether the internal mod logger is in debugging mode.
     */
    public static boolean isDebugging() { return LOGGER.isDebugMode(); }

    /**
     * Flag that indicates if the event API is being tested.
     */
    private static final boolean isEventTesting = false;

    /**
     * @return Whether event tests should be registered by the mod.
     */
    public static boolean isEventTesting() { return Platform.isDevelopmentEnvironment() && isEventTesting; }

    /* Networking */

    /**
     * This is the mod's network channel. Any network subscriptions or registration to mod loaders is handled by the
     * Architectury mod.
     */
    public static final NetworkChannel NETWORK = NetworkChannel.create(new ResourceLocation(MOD_ID, "network"));

    /**
     * This is the mod's network current protocol version. If there are any changes made to the mod's network code that
     * will cause communication issues with older versions of the mod, then this value needs bumped up. Typically,
     * changes made to network packets and/or changes in tweak data serialization will require a bump.
     */
    public static final String PROTOCOL = "1.3";

    /**
     * Shortcut method for supplying the network's protocol version.
     * @return The mod's network protocol version.
     */
    public static String getProtocol() { return PROTOCOL; }

    /**
     * This field indicates if the client is connected to a level with Nostalgic Tweaks installed. This field will
     * always be true in singleplayer. Otherwise, it will only be true in multiplayer if the mod is connected to a
     * server with the mod installed. This field will be <code>false</code> when not playing on a level.
     */
    private static boolean isNetworkSupported = false;

    /**
     * This field stores the current connection state with a server running Nostalgic Tweaks. Once a handshake is
     * performed between the server and the client, data from the Nostalgic Tweaks mod running on the server is sent to
     * the client.
     */
    private static NostalgicConnection connection = null;

    /**
     * Change the server connection data that is stored on the client. The server has no need to invoke this method.
     * If the user disconnects from a server running Nostalgic Tweaks, this value should be reset back to null.
     *
     * @param data A nostalgic connection instance.
     */
    public static void setConnection(@Nullable NostalgicConnection data) { NostalgicTweaks.connection = data; }

    /**
     * Get the current connection data between a server running Nostalgic Tweaks and the client. If the user is playing
     * in singleplayer, there will be data stored here since the session could possibly move to LAN. It is up to the
     * invoker to check for a singleplayer only world. If the user is not playing on a server running Nostalgic Tweaks,
     * then no data will be stored.
     *
     * @return An optional nostalgic connection instance.
     */
    public static Optional<NostalgicConnection> getConnection()
    {
        return Optional.ofNullable(NostalgicTweaks.connection);
    }

    /* Logical Server */

    /**
     * This field tracks the game's logical server if the mod is running in server-side mode.
     * Otherwise, the client should never use this field.
     */
    private static MinecraftServer server;

    /**
     * Set the logical server instance for the mod.
     * @param server A minecraft server instance.
     */
    public static void setServer(MinecraftServer server) { NostalgicTweaks.server = server; }

    /**
     * A getter method for retrieving the logical server instance. This should only be used by the server and not used
     * by the client.
     *
     * @return A minecraft server instance.
     */
    @CheckReturnValue
    public static MinecraftServer getServer() { return NostalgicTweaks.server; }

    /* State Checkers */

    /**
     * Getter method for checking if the mod is running on the logical client.
     * @return Whether the mod is running on the client.
     */
    public static boolean isClient() { return Platform.getEnv() == EnvType.CLIENT; }

    /**
     * Getter method for checking if the mod is running on the logical server.
     * @return Whether the mod is running on the server.
     */
    public static boolean isServer() { return Platform.getEnv() == EnvType.SERVER; }

    /**
     * Getter method for checking if the mod is running on Fabric.
     * @return Whether the mod is loaded by a Fabric loader.
     */
    public static boolean isFabric() { return Platform.isFabric(); }

    /**
     * Getter method for checking if the mod is running on Forge.
     * @return Whether the mod is loaded by a Forge loader.
     */
    public static boolean isForge() { return Platform.isForge(); }

    /**
     * Gets a string representation of the current mod loader the user is using.
     * @return The mod loader name in caps.
     */
    public static String getLoader() { return isForge() ? "FORGE" : "FABRIC"; }

    /**
     * This checks if the network is verified. This will always be <code>true</code> when the mod is running on the
     * logical server since it is impossible to be unverified. This may not always be true on the client since the user
     * may connect to a multiplayer server without the mod installed.
     *
     * @return Whether the network connection is verified by a multiplayer server or internal server.
     */
    public static boolean isNetworkVerified()
    {
        if (NostalgicTweaks.isServer())
            return true;

        return NostalgicTweaks.isNetworkSupported;
    }

    /**
     * This sets the state of network connection verification. This should only be changed during level disconnection or
     * during level connection. Never change this while connected and playing on a level.
     *
     * @param verified A network verification state.
     */
    public static void setNetworkVerification(boolean verified)
    {
        NostalgicTweaks.LOGGER.debug("Setting network verification to: %s", verified);
        NostalgicTweaks.isNetworkSupported = verified;
    }

    /* Sided Initialization */

    /**
     * Instructions for when the mod is initialized by a logical server.
     */
    public static void initServer()
    {
        DefaultConfig.initialize();
        ServerConfigCache.initialize();
        PacketRegistry.initialize();

        NostalgicTweaks.LOGGER.warn("Nostalgic Tweaks server support is still in-development");
        NostalgicTweaks.LOGGER.warn("Please report any problems you encounter");
        NostalgicTweaks.LOGGER.info("Loading mod in [%s] server environment", LogColor.apply(LogColor.LIGHT_PURPLE, getLoader()));
    }

    /**
     * Instructions for when the mod is initialized by a logical client.
     */
    public static void initClient()
    {
        DefaultConfig.initialize();
        ClientConfigCache.initialize();
        PacketRegistry.initialize();

        NostalgicTweaks.LOGGER.info("Loading mod in [%s] client environment", LogColor.apply(LogColor.LIGHT_PURPLE, getLoader()));

        if (ModTracker.OPTIFINE.isInstalled())
            NostalgicTweaks.LOGGER.warn("Optifine is installed - some tweaks may not work as intended");
    }
}
