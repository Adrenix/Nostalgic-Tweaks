package mod.adrenix.nostalgic.platform.neoforge;

import net.neoforged.fml.loading.FMLLoader;

/**
 * Do <b color=red>not</b> class load any mod related classes here. Doing so will cause "applied too early" ASM errors
 * during the mixin application process. This mod tracker utility is used by mixin plugins.
 */
public class TrackerPlatformImpl
{
    public static boolean isModLoaded(String id)
    {
        return FMLLoader.getLoadingModList().getModFileById(id) != null;
    }
}
