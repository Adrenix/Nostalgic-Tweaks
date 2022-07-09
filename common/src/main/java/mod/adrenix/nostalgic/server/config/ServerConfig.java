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
 *
 * Note any new configuration groups added here must be updated in the config server reflection.
 * @see mod.adrenix.nostalgic.server.config.reflect.ServerReflect
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

    public Gameplay gameplay = new Gameplay();
    public static class Gameplay
    {
        // Combat System
        public int arrowSpeed = DefaultConfig.Gameplay.ARROW_SPEED;
        public boolean instantBow = DefaultConfig.Gameplay.INSTANT_BOW;
        public boolean invincibleBow = DefaultConfig.Gameplay.INVINCIBLE_BOW;
        public boolean disableCooldown = DefaultConfig.Gameplay.DISABLE_COOLDOWN;
        public boolean disableSweep = DefaultConfig.Gameplay.DISABLE_SWEEP;

        // Experience System
        public boolean disableOrbSpawn = DefaultConfig.Gameplay.DISABLE_ORB_SPAWN;
        public boolean disableAnvil = DefaultConfig.Gameplay.DISABLE_ANVIL;
        public boolean disableEnchantTable = DefaultConfig.Gameplay.DISABLE_ENCHANT_TABLE;

        // Game Mechanics
        public boolean oldFire = DefaultConfig.Gameplay.OLD_FIRE;
        public boolean infiniteBurn = DefaultConfig.Gameplay.INFINITE_BURN;
        public boolean disableSprint = DefaultConfig.Gameplay.DISABLE_SPRINT;

        // Hunger System
        public boolean disableHunger = DefaultConfig.Gameplay.DISABLE_HUNGER;
        public boolean instantEat = DefaultConfig.Gameplay.INSTANT_EAT;
        public boolean oldFoodStacking = DefaultConfig.Gameplay.OLD_FOOD_STACKING;
    }
}
