package mod.adrenix.nostalgic;

import com.google.common.base.Suppliers;
import dev.architectury.networking.NetworkChannel;
import mod.adrenix.nostalgic.client.config.ClientConfigCache;
import mod.adrenix.nostalgic.network.PacketRegistry;
import mod.adrenix.nostalgic.server.config.ServerConfigCache;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import mod.adrenix.nostalgic.util.common.log.LogColor;
import mod.adrenix.nostalgic.util.common.log.ModLogger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nullable;
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
     * Check if the logger is in debug mode.
     * @return Whether the internal mod logger is in debugging mode.
     */
    public static boolean isDebugging() { return LOGGER.isDebugMode(); }

    /* Other Mod Tracking */

    /**
     * This field tracks whether Sodium is installed. This is a Fabric only mod, so it only requires a mod list check
     * to set this field properly.
     */
    public static boolean isSodiumInstalled = false;

    /**
     * This field tracks whether Mod Menu is installed. This is a Fabric only mod, so it only requires a mod list check
     * to set this field properly.
     */
    public static boolean isModMenuInstalled = false;

    /**
     * This field tracks whether Optifine is installed. This is a Forge mod, but has a Fabric relative known as Opti-
     * Fabric. A supplier is needed since a hacky class check is required to determine if Optifine is present. This
     * hacky check is needed because Optifine is a closed-source mod.
     */
    public static final Supplier<Boolean> OPTIFINE = Suppliers.memoize(ClassUtil::isOptifinePresent);

    /* Networking */

    /**
     * This is the mod's network channel. Any network subscriptions or registration to mod loaders is handled by the
     * Architectury mod.
     */
    public static final NetworkChannel NETWORK = NetworkChannel.create(new ResourceLocation(MOD_ID, "network"));

    /**
     * This is the mod's network current protocol version. If there are any new or removed packets, then a change in
     * this version is required.
     */
    public static final String PROTOCOL = "1.1";

    /**
     * This field indicates if the client is connected to a level with Nostalgic Tweaks installed. This field will
     * always be true in singleplayer. Otherwise, it will only be true in multiplayer if the mod is connected to a
     * server with the mod installed. This field will be <code>false</code> when not playing on a level.
     */
    private static boolean isNetworkSupported = false;

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
    @Nullable public static MinecraftServer getServer() { return NostalgicTweaks.server; }

    /* Side & Environment States */

    /**
     * This enumeration is a universal side checker that is used in common mod code.
     * Each mod loader has their own version of this.
     */

    public enum Side { CLIENT, SERVER }

    /**
     * This enumeration is a universal mod loader checker this is used in common mod code.
     * Some unique instructions will need executed depending on which mod loader is being used.
     */

    public enum Environment { FABRIC, FORGE }

    /**
     * This field indicates which logical side the mod is running on.
     * This will either be client or server and should never change during runtime.
     */
    private static Side side;

    /**
     * This field indicates which mod loader environment the mod is running on.
     * This will either be Forge or Fabric and should never change during runtime.
     */
    private static Environment environment;

    /**
     * Set the logical side the mod is running on.
     * @param side A side enumeration type value.
     */
    private static void setSide(Side side) { NostalgicTweaks.side = side; }

    /**
     * Set the mod loader environment the mod is running on.
     * @param environment An environment enumeration type value.
     */
    private static void setEnvironment(Environment environment) { NostalgicTweaks.environment = environment; }

    /* State Checkers */

    /**
     * This field tracks whether the mod is currently running in a development environment. Each mod loader has a unique
     * way of checking whether the mod is in development mode.
     */
    private static boolean isDevEnv = false;

    /**
     * Set whether the mod is in a development environment.
     * @param state Whether the mod is in a development environment.
     */
    public static void setDevelopmentEnvironment(boolean state)
    {
        NostalgicTweaks.LOGGER.setDebug(state);
        NostalgicTweaks.isDevEnv = state;
    }

    /**
     * Getter method for checking if the mod is running in a development.
     * @return Whether the mod is running in a development environment.
     */
    public static boolean isDevelopmentEnvironment() { return NostalgicTweaks.isDevEnv; }

    /**
     * Getter method for checking if the mod is running on the logical client.
     * @return Whether the mod is running on the client.
     */
    public static boolean isClient() { return NostalgicTweaks.side == Side.CLIENT; }

    /**
     * Getter method for checking if the mod is running on the logical server.
     * @return Whether the mod is running on the server.
     */
    public static boolean isServer() { return NostalgicTweaks.side == Side.SERVER; }

    /**
     * Getter method for checking if the mod is running on Fabric.
     * @return Whether the mod is loaded by a Fabric loader.
     */
    public static boolean isFabric() { return NostalgicTweaks.environment == Environment.FABRIC; }

    /**
     * Getter method for checking if the mod is running on Forge.
     * @return Whether the mod is loaded by a Forge loader.
     */
    public static boolean isForge() { return NostalgicTweaks.environment == Environment.FORGE; }

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
     * @param environment The mod loader environment initializing the mod.
     */
    public static void initServer(Environment environment)
    {
        ServerConfigCache.initialize();

        NostalgicTweaks.LOGGER.warn("Nostalgic Tweaks server support is still in-development");
        NostalgicTweaks.LOGGER.warn("Please report any problems you encounter");

        NostalgicTweaks.setSide(Side.SERVER);
        NostalgicTweaks.setEnvironment(environment);
        NostalgicTweaks.LOGGER.info("Loading mod in [%s] server environment", LogColor.apply(LogColor.LIGHT_PURPLE, environment.toString()));
        PacketRegistry.initialize();
    }

    /**
     * Instructions for when the mod is initialized by a logical client.
     * @param environment The mod loader environment initializing the mod.
     */
    public static void initClient(Environment environment)
    {
        ClientConfigCache.initialize();

        NostalgicTweaks.setSide(Side.CLIENT);
        NostalgicTweaks.setEnvironment(environment);
        NostalgicTweaks.LOGGER.info("Loading mod in [%s] client environment", LogColor.apply(LogColor.LIGHT_PURPLE, environment.toString()));
        PacketRegistry.initialize();

        if (OPTIFINE.get())
            NostalgicTweaks.LOGGER.warn("Optifine is installed - some tweaks may not work as intended");
    }
}
