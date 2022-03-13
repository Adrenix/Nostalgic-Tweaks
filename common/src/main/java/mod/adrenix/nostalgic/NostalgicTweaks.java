package mod.adrenix.nostalgic;

import mod.adrenix.nostalgic.client.config.CommonRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NostalgicTweaks
{
    /* Mod ID & Logger */

    public static final String MOD_ID = "nostalgic_tweaks";
    public static final Logger LOGGER = LogManager.getLogger();

    /* Side & Environment States */

    public enum Side { CLIENT, SERVER }
    public enum Environment { FABRIC, FORGE }

    private static Side side;
    private static Environment environment;

    private static void setSide(Side _side) { side = _side; }
    private static void setEnvironment(Environment _env) { environment = _env; }

    /* State Checkers */

    public static boolean isClient() { return side == Side.CLIENT; }
    public static boolean isServer() { return side == Side.SERVER; }

    public static boolean isFabric() { return environment == Environment.FABRIC; }
    public static boolean isForge() { return environment == Environment.FORGE; }

    /* Sided Initialization */

    public static void initServer(Environment environment)
    {
        NostalgicTweaks.LOGGER.warn("Nostalgic Tweaks server support is still in-development");
        NostalgicTweaks.LOGGER.warn("No changes will be made to the server");

        NostalgicTweaks.setSide(Side.SERVER);
        NostalgicTweaks.setEnvironment(environment);
        NostalgicTweaks.LOGGER.info(String.format("Loading mod in [%s] server environment", environment.toString()));
    }

    public static void initClient(Environment environment)
    {
        CommonRegistry.initializeConfiguration();

        NostalgicTweaks.setSide(Side.CLIENT);
        NostalgicTweaks.setEnvironment(environment);
        NostalgicTweaks.LOGGER.info(String.format("Loading mod in [%s] client environment", environment.toString()));
    }
}
