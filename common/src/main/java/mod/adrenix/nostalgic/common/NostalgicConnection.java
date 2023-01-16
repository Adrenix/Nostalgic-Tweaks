package mod.adrenix.nostalgic.common;

import com.google.gson.Gson;
import mod.adrenix.nostalgic.NostalgicTweaks;

@SuppressWarnings("FieldMayBeFinal") // The GSON deserializer cannot modify fields that are final
public class NostalgicConnection
{
    /**
     * This Gson instance is used to convert a JSON string into a readable data class.
     * @see NostalgicConnection#deserialize(String)
     */
    private static final Gson GSON = new Gson();

    /* Utility */

    public static NostalgicConnection disconnected() { return new NostalgicConnection("n/a", "n/a", "n/a"); }

    /* Fields */

    private String loader;
    private String version;
    private String protocol;

    /* Constructor */

    /**
     * Create a new connection instance for handshake transmission.
     * @param loader The server mod loader.
     * @param version The mod version on the server.
     * @param protocol The mod protocol on the server.
     */
    public NostalgicConnection(String loader, String version, String protocol)
    {
        this.loader = loader;
        this.version = version;
        this.protocol = protocol;
    }

    /* Methods */

    /**
     * @return Get the mod loader the server is using.
     */
    public String getLoader() { return this.loader; }

    /**
     * @return Get the mod version the server is using.
     */
    public String getVersion() { return this.version; }

    /**
     * @return Get the mod network protocol the server is using.
     */
    public String getProtocol() { return this.protocol; }

    /**
     * Convert this class into a serialized JSON string.
     *
     * @return A JSON string.
     */
    public String serialize() { return GSON.toJson(this); }

    /**
     * Deserialize a JSON string into a readable {@link NostalgicConnection} class and store it in the mod's network
     * connection cache.
     *
     * @param json A JSON string.
     */
    public static void deserialize(String json)
    {
        NostalgicTweaks.setConnection(GSON.fromJson(json, NostalgicConnection.class));
    }
}
