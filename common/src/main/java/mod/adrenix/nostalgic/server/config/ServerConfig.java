package mod.adrenix.nostalgic.server.config;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.DefaultConfig;
import mod.adrenix.nostalgic.common.config.ValidateConfig;
import mod.adrenix.nostalgic.common.config.annotation.TweakData;
import mod.adrenix.nostalgic.common.config.auto.ConfigData;
import mod.adrenix.nostalgic.common.config.auto.annotation.Config;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    /* Config Validation */

    @Override public void validatePostLoad() throws ValidationException { ValidateConfig.scan(this); }

    /* SSO Mode */

    public boolean serverSideOnlyMode = false;

    /* Constants */

    @TweakData.Ignore private static final int HEALTH_MIN = DefaultConfig.Gameplay.HEALTH_MIN;
    @TweakData.Ignore private static final int HEALTH_MAX = DefaultConfig.Gameplay.HEALTH_MAX;
    @TweakData.Ignore private static final int HEALTH_RESET = DefaultConfig.Gameplay.HEALTH_RESET;

    @TweakData.Ignore private static final int ITEM_MIN = DefaultConfig.Gameplay.ITEM_STACK_MIN;
    @TweakData.Ignore private static final int ITEM_MAX = DefaultConfig.Gameplay.ITEM_STACK_MAX;
    @TweakData.Ignore private static final int ITEM_RESET = DefaultConfig.Gameplay.ITEM_STACK_RESET;
    @TweakData.Ignore private static final int FOOD_RESET = DefaultConfig.Gameplay.FOOD_STACK_RESET;

    /* Server Config */

    public EyeCandy eyeCandy = new EyeCandy();
    public static class EyeCandy
    {
        @TweakData.BoundedSlider(min = 1, max = 64, reset = DefaultConfig.Candy.ITEM_MERGE_LIMIT)
        public int itemMergeLimit = DefaultConfig.Candy.ITEM_MERGE_LIMIT;

        public TweakVersion.Hotbar oldCreativeHotbar = DefaultConfig.Candy.OLD_CREATIVE_HOTBAR;
        public boolean oldChestVoxel = DefaultConfig.Candy.OLD_CHEST_VOXEL;
        public boolean oldItemMerging = DefaultConfig.Candy.OLD_ITEM_MERGING;
        public boolean oldSquareBorder = DefaultConfig.Candy.OLD_SQUARE_BORDER;
        public boolean oldClassicLighting = DefaultConfig.Candy.OLD_CLASSIC_LIGHTING;
        public boolean debugEntityId = DefaultConfig.Candy.DEBUG_ENTITY_ID;
    }

    public Gameplay gameplay = new Gameplay();
    public static class Gameplay
    {
        // Bugs
        public boolean oldLadderGap = DefaultConfig.Gameplay.OLD_LADDER_GAP;
        public boolean oldSquidMilking = DefaultConfig.Gameplay.OLD_SQUID_MILKING;

        // Mob System
        @TweakData.BoundedSlider(min = 0, max = 100, reset = DefaultConfig.Gameplay.ANIMAL_SPAWN_CAP)
        public int animalSpawnCap = DefaultConfig.Gameplay.ANIMAL_SPAWN_CAP;

        public boolean disableAnimalPanic = DefaultConfig.Gameplay.DISABLE_ANIMAL_PANIC;
        public boolean disableSheepEatGrass = DefaultConfig.Gameplay.DISABLE_SHEEP_EAT_GRASS;
        public boolean oldAnimalSpawning = DefaultConfig.Gameplay.OLD_ANIMAL_SPAWNING;
        public boolean oldSheepPunching = DefaultConfig.Gameplay.OLD_SHEEP_PUNCHING;
        public boolean oneWoolPunch = DefaultConfig.Gameplay.ONE_WOOL_PUNCH;

        // Mob Drops
        public boolean oldZombiePigmenDrops = DefaultConfig.Gameplay.OLD_ZOMBIE_PIGMEN_DROPS;
        public boolean oldSkeletonDrops = DefaultConfig.Gameplay.OLD_SKELETON_DROPS;
        public boolean oldChickenDrops = DefaultConfig.Gameplay.OLD_CHICKEN_DROPS;
        public boolean oldZombieDrops = DefaultConfig.Gameplay.OLD_ZOMBIE_DROPS;
        public boolean oldSpiderDrops = DefaultConfig.Gameplay.OLD_SPIDER_DROPS;
        public boolean oldSheepDrops = DefaultConfig.Gameplay.OLD_SHEEP_DROPS;
        public boolean oldCowDrops = DefaultConfig.Gameplay.OLD_COW_DROPS;
        public boolean oldPigDrops = DefaultConfig.Gameplay.OLD_PIG_DROPS;

        public boolean oldStyleZombieVillagerDrops = DefaultConfig.Gameplay.OLD_STYLE_ZOMBIE_VILLAGER_DROPS;
        public boolean oldStyleCaveSpiderDrops = DefaultConfig.Gameplay.OLD_STYLE_CAVE_SPIDER_DROPS;
        public boolean oldStyleMooshroomDrops = DefaultConfig.Gameplay.OLD_STYLE_MOOSHROOM_DROPS;
        public boolean oldStyleDrownedDrops = DefaultConfig.Gameplay.OLD_STYLE_DROWNED_DROPS;
        public boolean oldStyleRabbitDrops = DefaultConfig.Gameplay.OLD_STYLE_RABBIT_DROPS;
        public boolean oldStyleStrayDrops = DefaultConfig.Gameplay.OLD_STYLE_STRAY_DROPS;
        public boolean oldStyleHuskDrops = DefaultConfig.Gameplay.OLD_STYLE_HUSK_DROPS;

        // Combat System
        @TweakData.BoundedSlider(min = 0, max = 100, reset = DefaultConfig.Gameplay.ARROW_SPEED)
        public int arrowSpeed = DefaultConfig.Gameplay.ARROW_SPEED;

        public boolean instantBow = DefaultConfig.Gameplay.INSTANT_BOW;
        public boolean invincibleBow = DefaultConfig.Gameplay.INVINCIBLE_BOW;
        public boolean disableCooldown = DefaultConfig.Gameplay.DISABLE_COOLDOWN;
        public boolean disableMissTimer = DefaultConfig.Gameplay.DISABLE_MISS_TIMER;
        public boolean disableCriticalHit = DefaultConfig.Gameplay.DISABLE_CRITICAL_HIT;
        public boolean disableSweep = DefaultConfig.Gameplay.DISABLE_SWEEP;
        public boolean oldDamageValues = DefaultConfig.Gameplay.OLD_DAMAGE_VALUES;

        // Experience System
        public boolean disableOrbSpawn = DefaultConfig.Gameplay.DISABLE_ORB_SPAWN;
        public boolean disableAnvil = DefaultConfig.Gameplay.DISABLE_ANVIL;
        public boolean disableEnchantTable = DefaultConfig.Gameplay.DISABLE_ENCHANT_TABLE;

        // Game Mechanics
        public boolean oldFire = DefaultConfig.Gameplay.OLD_FIRE;
        public boolean instantAir = DefaultConfig.Gameplay.INSTANT_AIR;
        public boolean infiniteBurn = DefaultConfig.Gameplay.INFINITE_BURN;
        public boolean leftClickDoor = DefaultConfig.Gameplay.LEFT_CLICK_DOOR;
        public boolean leftClickLever = DefaultConfig.Gameplay.LEFT_CLICK_LEVER;
        public boolean leftClickButton = DefaultConfig.Gameplay.LEFT_CLICK_BUTTON;
        public boolean instantBonemeal = DefaultConfig.Gameplay.INSTANT_BONE_MEAL;
        public boolean tilledGrassSeeds = DefaultConfig.Gameplay.TILLED_GRASS_SEEDS;
        public boolean disableBedBounce = DefaultConfig.Gameplay.DISABLE_BED_BOUNCE;
        public boolean disableSprint = DefaultConfig.Gameplay.DISABLE_SPRINT;
        public boolean disableSwim = DefaultConfig.Gameplay.DISABLE_SWIM;
        public boolean cartBoosting = DefaultConfig.Gameplay.CART_BOOSTING;

        // Hunger System
        public boolean disableHunger = DefaultConfig.Gameplay.DISABLE_HUNGER;
        public boolean instantEat = DefaultConfig.Gameplay.INSTANT_EAT;
        public boolean oldFoodStacking = DefaultConfig.Gameplay.OLD_FOOD_STACKING;

        @TweakData.BoundedSlider(min = HEALTH_MIN, max = HEALTH_MAX, reset = HEALTH_RESET)
        public Map<String, Integer> customFoodHealth = new HashMap<>();

        @TweakData.BoundedSlider(min = ITEM_MIN, max = ITEM_MAX, reset = FOOD_RESET)
        public Map<String, Integer> customFoodStacking = new HashMap<>();

        public Set<String> disabledFoodHealth = new HashSet<>();
        public Set<String> disabledFoodStacking = new HashSet<>();

        // Item Stacking
        @TweakData.BoundedSlider(min = ITEM_MIN, max = ITEM_MAX, reset = ITEM_RESET)
        public Map<String, Integer> customItemStacking = new HashMap<>();
    }

    public Animation animation = new Animation();
    public static class Animation
    {
        // Player Animations
        public boolean oldCreativeCrouch = DefaultConfig.Animation.OLD_CREATIVE_CROUCH;
    }
}
