package mod.adrenix.nostalgic.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;

/**
 * Do <b color=red>not</b> class load any mod related classes here. Doing so will cause "applied too early" ASM errors
 * during the mixin application process. This mod tracker utility is used by mixin plugins.
 */
public final class TrackerPlatform
{
    private TrackerPlatform()
    {
    }

    /**
     * The Architectury platform {@code isModLoaded} utility uses NeoForge's {@code ModList} to check if a mod is
     * loaded. The {@code ModList} is not available when mixin plugins are applied; however, the FML Loader
     * {@code getLoadingModList#getModFileById} method is available when mixin plugins are applied, which is what our
     * NeoForge {@link TrackerPlatform} implementation uses.
     *
     * @param id The mod id to check.
     * @return Whether the given mod id will be loaded at some point by a mod loader.
     */
    @ExpectPlatform
    public static boolean isModLoaded(String id)
    {
        throw new AssertionError();
    }
}
