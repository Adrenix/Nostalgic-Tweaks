package mod.adrenix.nostalgic;

import dev.architectury.injectables.annotations.ExpectPlatform;

/**
 * Do <b color=red>not</b> class load any mod related classes here. Doing so will cause "applied too early" ASM errors
 * during the mixin application process. This mod tracker utility is used by mixin plugins.
 */
public final class ModFinderPlatform
{
    private ModFinderPlatform()
    {
    }

    /**
     * The Architectury platform {@code isModLoaded} utility uses NeoForge's {@code ModList} to check if a mod is
     * loaded. The {@code ModList} is not available when mixin plugins are applied; however, the FML Loader
     * {@code getLoadingModList#getModFileById} method is available when mixin plugins are applied, which is what our
     * NeoForge {@link ModFinderPlatform} implementation uses.
     *
     * @param id A mod's unique identifier.
     * @return Whether the given mod identifier will be loaded at some point by a mod loader.
     */
    @ExpectPlatform
    public static boolean isInstalled(String id)
    {
        throw new AssertionError();
    }
}
