package mod.adrenix.nostalgic.server;

/**
 * This class contains a registration helper method that will be used by the server initializers in Fabric and Forge.
 * The Architectury mod provides the events used in this class.
 */
public abstract class ArchServerEvents
{
    /**
     * Registers Architectury events. This is used when there is not a Fabric-related event to a Forge event or if code
     * is common between the two mod loaders.
     */
    public static void register()
    {
    }
}
