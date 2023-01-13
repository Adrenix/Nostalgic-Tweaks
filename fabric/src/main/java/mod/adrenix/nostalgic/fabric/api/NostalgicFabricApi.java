package mod.adrenix.nostalgic.fabric.api;

/**
 * Used as an entrypoint to allow for integration with Nostalgic Tweaks API events.
 * Using an interface approach allows mod authors to not depend on Nostalgic Tweaks during runtime.
 */

public interface NostalgicFabricApi
{
    /**
     * Called during Fabric's {@link net.fabricmc.api.ClientModInitializer} to allow the implementer to register events
     * with the Nostalgic Tweak's API.
     *
     * For a list of mod events see the following package:
     * @see mod.adrenix.nostalgic.fabric.api.event
     */
    void registerEvents();
}
