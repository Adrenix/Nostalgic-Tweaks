package mod.adrenix.nostalgic.config.cache;

/**
 * This enumeration is used by the client-side config user interface. Each cache value can be set to either
 * {@code LOCAL} or {@code NETWORK}. Only server or dynamically sided values will make use of this utility.
 *
 * <ul>
 *   <li>{@code LOCAL} - This is for a value that is stored locally, either on the client or server.</li><br>
 *   <li>{@code NETWORK} - This is for a value that is goes back and forth over the network. This is only managed by the
 *   client.</li>
 * </ul>
 */
public enum CacheMode
{
    LOCAL,
    NETWORK
}
