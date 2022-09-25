package mod.adrenix.nostalgic;

import com.google.common.base.Suppliers;
import dev.architectury.networking.NetworkChannel;
import mod.adrenix.nostalgic.client.config.ClientConfigCache;
import mod.adrenix.nostalgic.network.PacketRegistry;
import mod.adrenix.nostalgic.server.config.ServerConfigCache;
import mod.adrenix.nostalgic.util.common.log.LogColor;
import mod.adrenix.nostalgic.util.common.log.ModLogger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class NostalgicTweaks
{
    /* Mod ID & Logger */

    public static final String MOD_ID = "nostalgic_tweaks";
    public static final String MOD_NAME = "Nostalgic Tweaks";
    public static final ModLogger LOGGER = new ModLogger(MOD_NAME);
    public static boolean isDebugging() { return LOGGER.isDebugMode(); }

    /* Other Mod Tracking */

    public static boolean isSodiumInstalled = false;
    public static boolean isModMenuInstalled = false;

    public static final Supplier<Boolean> OPTIFINE = Suppliers.memoize(() ->
    {
        try
        {
            Class.forName("net.optifine.Config");
            return true;
        }
        catch (ClassNotFoundException ignored)
        {
            return false;
        }
    });

    /* Networking */

    public static final NetworkChannel NETWORK = NetworkChannel.create(new ResourceLocation(MOD_ID, "network"));
    public static final String PROTOCOL = "1.1";

    private static MinecraftServer server;
    private static boolean isNetworkSupported = false;

    @Nullable
    public static MinecraftServer getServer() { return server; }
    public static void setServer(MinecraftServer _server) { server = _server; }

    /* Side & Environment States */

    public enum Side { CLIENT, SERVER }
    public enum Environment { FABRIC, FORGE }

    private static Side side;
    private static Environment environment;

    private static void setSide(Side _side) { side = _side; }
    private static void setEnvironment(Environment _env) { environment = _env; }

    private static boolean isDevEnv = false;

    /* State Checkers */

    public static void setDevelopmentEnvironment(boolean state)
    {
        NostalgicTweaks.LOGGER.setDebug(state);
        isDevEnv = state;
    }

    public static boolean isDevelopmentEnvironment() { return isDevEnv; }

    public static boolean isClient() { return side == Side.CLIENT; }
    public static boolean isServer() { return side == Side.SERVER; }

    public static boolean isFabric() { return environment == Environment.FABRIC; }
    public static boolean isForge() { return environment == Environment.FORGE; }

    public static boolean isNetworkVerified()
    {
        if (NostalgicTweaks.isServer())
            return true;
        return isNetworkSupported;
    }

    public static void setNetworkVerification(boolean verified)
    {
        NostalgicTweaks.LOGGER.debug(String.format("Setting network verification to: %s", verified));
        isNetworkSupported = verified;
    }

    /* Sided Initialization */

    public static void initServer(Environment environment)
    {
        ServerConfigCache.initializeConfiguration();

        NostalgicTweaks.LOGGER.warn("Nostalgic Tweaks server support is still in-development");
        NostalgicTweaks.LOGGER.warn("Please report any problems you encounter");

        NostalgicTweaks.setSide(Side.SERVER);
        NostalgicTweaks.setEnvironment(environment);
        NostalgicTweaks.LOGGER.info(String.format("Loading mod in [%s] server environment", LogColor.apply(LogColor.LIGHT_PURPLE, environment.toString())));
        PacketRegistry.init();
    }

    public static void initClient(Environment environment)
    {
        ClientConfigCache.initializeConfiguration();

        NostalgicTweaks.setSide(Side.CLIENT);
        NostalgicTweaks.setEnvironment(environment);
        NostalgicTweaks.LOGGER.info(String.format("Loading mod in [%s] client environment", LogColor.apply(LogColor.LIGHT_PURPLE, environment.toString())));
        PacketRegistry.init();

        if (OPTIFINE.get())
            NostalgicTweaks.LOGGER.warn("Optifine is installed - some tweaks may not work as intended");
    }
}
