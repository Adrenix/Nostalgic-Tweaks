package mod.adrenix.nostalgic.platform.fabric;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Do <b color=red>not</b> class load any mod related classes here. Doing so will cause "applied too early" ASM errors
 * during the mixin application process. This mod tracker utility is used by mixin plugins.
 */
public class TrackerPlatformImpl
{
    public static boolean isModLoaded(String id)
    {
        return FabricLoader.getInstance().isModLoaded(id);
    }
}
