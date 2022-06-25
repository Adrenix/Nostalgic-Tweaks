package mod.adrenix.nostalgic.server.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.DefaultConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;

/**
 * The fields in this config need to stay in sync with the fields in the client config.
 * Any updates in that class or this class will require an update in both config classes.
 *
 * @see mod.adrenix.nostalgic.client.config.ClientConfig
 */

@Config(name = NostalgicTweaks.MOD_ID + "-server")
public class ServerConfig implements ConfigData
{
    public EyeCandy eyeCandy = new EyeCandy();
    public static class EyeCandy
    {
        public TweakVersion.Hotbar oldCreativeHotbar = DefaultConfig.Candy.OLD_CREATIVE_HOTBAR;
        public boolean oldChestVoxel = DefaultConfig.Candy.OLD_CHEST_VOXEL;
        public boolean oldItemMerging = DefaultConfig.Candy.OLD_ITEM_MERGING;
        public boolean oldWaterLighting = DefaultConfig.Candy.OLD_WATER_LIGHTING;
        public boolean oldSquareBorder = DefaultConfig.Candy.OLD_SQUARE_BORDER;
    }
}
