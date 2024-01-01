package mod.adrenix.nostalgic.util.client.link;

/**
 * This utility stores URLs that are used by the mod.
 */
public interface LinkLocation
{
    String LICENSE = "https://github.com/Adrenix/Nostalgic-Tweaks/blob/main/LICENSE";
    String DISCORD = "https://discord.gg/jWdfVh3";
    String KOFI = "https://ko-fi.com/adrenix";
    String GOLDEN_DAYS = "https://github.com/PoeticRainbow/golden-days/releases";
    String DOWNLOAD = "https://modrinth.com/mod/nostalgic-tweaks/versions";
    String SUPPORTERS = "https://raw.githubusercontent.com/Adrenix/Nostalgic-Tweaks/data/supporters-v2.json";

    static String getSupporterFace(String uuid)
    {
        return String.format("https://crafthead.net/avatar/%s/8", uuid);
    }
}
