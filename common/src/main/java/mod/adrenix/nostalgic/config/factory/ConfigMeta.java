package mod.adrenix.nostalgic.config.factory;

import mod.adrenix.nostalgic.NostalgicTweaks;

public interface ConfigMeta
{
    /* Methods */

    /**
     * Validate values loaded from the config file on disk.
     *
     * @throws LoaderException When invalid values are serialized.
     */
    default void validate() throws LoaderException
    {
        NostalgicTweaks.LOGGER.info("[Config Meta] No special validator instructions were found - skipping");
    }
}
