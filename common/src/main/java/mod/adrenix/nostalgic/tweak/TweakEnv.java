package mod.adrenix.nostalgic.tweak;

/**
 * Each tweak must be assigned an environment. The value retrieved from a tweak will change based on its environment and
 * the current game state.
 *
 * <ul>
 *     <li>{@code CLIENT} - Instructs the mod that this tweak is controlled by the client.</li><br>
 *     <li>{@code SERVER} - Instructs the mod that this tweak is controlled by the server.</li><br>
 *     <li>{@code DYNAMIC} - Instructs the mod that this tweak is controlled by both the client and server. Only servers
 *     with this mod installed will override the tweak.</li>
 * </ul>
 */
public enum TweakEnv
{
    /**
     * Client tweaks are usually only aesthetic and will not give the player an unfair advantage when connected to a
     * server without the mod installed. These tweaks will work everywhere regardless of the game's current state.
     * Additionally, only the client can control the state of the tweak.
     */
    CLIENT,

    /**
     * Server tweaks usually alter gameplay mechanics in some way. Some server tweaks could give players an unfair
     * advantage when connected to a server without the mod installed. This is why these tweaks will only work in
     * singleplayer or when the player is connected to a server with the mod installed. If the client is connected to a
     * server without the mod installed, then the tweak will be put into a disabled state.
     */
    SERVER,

    /**
     * Dynamic tweaks are available for both the client and server. These tweaks will work everywhere regardless of the
     * game's current state; however, dynamic tweaks can have their state overridden by a server with the mod
     * installed.
     * <p>
     * Dynamic tweaks may alter gameplay mechanics, but not in a way that would give the player an unfair advantage when
     * connected to a server without the mod installed. A good example of this is the disable sprinting tweak. The
     * player only limits themselves when this tweak is enabled. However, consider the player is connected to a server
     * with the mod installed, and the server has sprinting disabled. It is important that the server is able to control
     * the client's ability to sprint, or else the player could have an unfair advantage against other players.
     */
    DYNAMIC
}
