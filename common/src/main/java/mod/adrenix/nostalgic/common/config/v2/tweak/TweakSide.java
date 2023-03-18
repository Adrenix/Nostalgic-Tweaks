package mod.adrenix.nostalgic.common.config.v2.tweak;

/**
 * When set to CLIENT: Instructs the mod that this tweak is controlled by the client.
 * When set to SERVER: Instructs the mod that this tweak is controlled by the server.
 *
 * When set to DYNAMIC: Instructs the mod that this tweak is controlled by both the client and server. Only servers with
 * Nostalgic Tweaks installed will override the tweak.
 */

public enum TweakSide
{
    CLIENT,
    SERVER,
    DYNAMIC
}
