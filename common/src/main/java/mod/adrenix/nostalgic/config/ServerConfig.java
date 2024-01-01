package mod.adrenix.nostalgic.config;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.config.factory.Config;
import mod.adrenix.nostalgic.config.factory.ConfigMeta;
import mod.adrenix.nostalgic.config.factory.LoaderException;
import mod.adrenix.nostalgic.tweak.TweakValidator;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.tweak.enums.Hotbar;
import mod.adrenix.nostalgic.tweak.listing.ItemMap;

/**
 * <b color=red>IMPORTANT</b>
 * <p>
 * Any update to a field name in this class that is associated with a client/dynamic tweak must update its counterpart
 * field name in the client config structure class as well.
 *
 * @see mod.adrenix.nostalgic.config.ClientConfig
 */

// This class only serves as a structure definition for Gson
@SuppressWarnings("unused")
@Config(filename = NostalgicTweaks.MOD_ID + "-server")
public class ServerConfig implements ConfigMeta
{
    /* Config Metadata */

    @Override
    public void validate() throws LoaderException
    {
        new TweakValidator(true).scan(ServerConfig.class);
    }

    public boolean serverSideOnly = ModTweak.SERVER_SIDE_ONLY.fromJson("serverSideOnly");
    public boolean serverLogging = ModTweak.SERVER_LOGGING.fromJson("serverLogging");
    public boolean serverDebugMode = ModTweak.SERVER_DEBUG.fromJson("serverDebugMode");

    public static class Mod
    {
        // Config Management

        public int numberOfBackups = ModTweak.NUMBER_OF_BACKUPS.fromJson("numberOfBackups");
    }

    public Mod mod = new Mod();

    public static class EyeCandy
    {
        public Hotbar oldCreativeHotbar = CandyTweak.OLD_CREATIVE_HOTBAR.fromJson("oldCreativeHotbar");
        public int itemMergeLimit = CandyTweak.ITEM_MERGE_LIMIT.fromJson("itemMergeLimit");
        public boolean oldChestVoxel = CandyTweak.OLD_CHEST_VOXEL.fromJson("oldChestVoxel");
        public boolean oldItemMerging = CandyTweak.OLD_ITEM_MERGING.fromJson("oldItemMerging");
        public boolean oldSquareBorder = CandyTweak.OLD_SQUARE_BORDER.fromJson("oldSquareBorder");
        public boolean oldClassicLighting = CandyTweak.OLD_CLASSIC_LIGHTING.fromJson("oldClassicLighting");
        public boolean debugEntityId = CandyTweak.DEBUG_ENTITY_ID.fromJson("debugEntityId");
    }

    public EyeCandy eyeCandy = new EyeCandy();

    public static class Gameplay
    {
        // Bugs

        public boolean oldLadderGap = GameplayTweak.OLD_LADDER_GAP.fromJson("oldLadderGap");
        public boolean oldSquidMilking = GameplayTweak.OLD_SQUID_MILKING.fromJson("oldSquidMilking");

        // Mobs

        public int animalSpawnCap = GameplayTweak.ANIMAL_SPAWN_CAP.fromJson("animalSpawnCap");
        public boolean disableAnimalPanic = GameplayTweak.DISABLE_ANIMAL_PANIC.fromJson("disableAnimalPanic");
        public boolean disableSheepEatGrass = GameplayTweak.DISABLE_SHEEP_EAT_GRASS.fromJson("disableSheepEatGrass");
        public boolean oldAnimalSpawning = GameplayTweak.OLD_ANIMAL_SPAWNING.fromJson("oldAnimalSpawning");
        public boolean oldSheepPunching = GameplayTweak.OLD_SHEEP_PUNCHING.fromJson("oldSheepPunching");
        public boolean oneWoolPunch = GameplayTweak.ONE_WOOL_PUNCH.fromJson("oneWoolPunch");

        // Mob Drops

        public boolean oldZombiePigmenDrops = GameplayTweak.OLD_ZOMBIE_PIGMEN_DROPS.fromJson("oldZombiePigmenDrops");
        public boolean oldSkeletonDrops = GameplayTweak.OLD_SKELETON_DROPS.fromJson("oldSkeletonDrops");
        public boolean oldChickenDrops = GameplayTweak.OLD_CHICKEN_DROPS.fromJson("oldChickenDrops");
        public boolean oldZombieDrops = GameplayTweak.OLD_ZOMBIE_DROPS.fromJson("oldZombieDrops");
        public boolean oldSpiderDrops = GameplayTweak.OLD_SPIDER_DROPS.fromJson("oldSpiderDrops");
        public boolean oldSheepDrops = GameplayTweak.OLD_SHEEP_DROPS.fromJson("oldSheepDrops");
        public boolean oldCowDrops = GameplayTweak.OLD_COW_DROPS.fromJson("oldCowDrops");
        public boolean oldPigDrops = GameplayTweak.OLD_PIG_DROPS.fromJson("oldPigDrops");

        public boolean oldStyleZombieVillagerDrops = GameplayTweak.OLD_STYLE_ZOMBIE_VILLAGER_DROPS.fromJson("oldStyleZombieVillagerDrops");
        public boolean oldStyleCaveSpiderDrops = GameplayTweak.OLD_STYLE_CAVE_SPIDER_DROPS.fromJson("oldStyleCaveSpiderDrops");
        public boolean oldStyleMooshroomDrops = GameplayTweak.OLD_STYLE_MOOSHROOM_DROPS.fromJson("oldStyleMooshroomDrops");
        public boolean oldStyleDrownedDrops = GameplayTweak.OLD_STYLE_DROWNED_DROPS.fromJson("oldStyleDrownedDrops");
        public boolean oldStyleRabbitDrops = GameplayTweak.OLD_STYLE_RABBIT_DROPS.fromJson("oldStyleRabbitDrops");
        public boolean oldStyleStrayDrops = GameplayTweak.OLD_STYLE_STRAY_DROPS.fromJson("oldStyleStrayDrops");
        public boolean oldStyleHuskDrops = GameplayTweak.OLD_STYLE_HUSK_DROPS.fromJson("oldStyleHuskDrops");

        // Combat

        public int arrowSpeed = GameplayTweak.ARROW_SPEED.fromJson("arrowSpeed");
        public boolean instantBow = GameplayTweak.INSTANT_BOW.fromJson("instantBow");
        public boolean invincibleBow = GameplayTweak.INVINCIBLE_BOW.fromJson("invincibleBow");
        public boolean disableCooldown = GameplayTweak.DISABLE_COOLDOWN.fromJson("disableCooldown");
        public boolean disableMissTimer = GameplayTweak.DISABLE_MISS_TIMER.fromJson("disableMissTimer");
        public boolean disableCriticalHit = GameplayTweak.DISABLE_CRITICAL_HIT.fromJson("disableCriticalHit");
        public boolean disableSweep = GameplayTweak.DISABLE_SWEEP.fromJson("disableSweep");
        public boolean oldDamageValues = GameplayTweak.OLD_DAMAGE_VALUES.fromJson("oldDamageValues");

        // Experience

        public boolean disableOrbSpawn = GameplayTweak.DISABLE_ORB_SPAWN.fromJson("disableOrbSpawn");
        public boolean disableAnvil = GameplayTweak.DISABLE_ANVIL.fromJson("disableAnvil");
        public boolean disableEnchantTable = GameplayTweak.DISABLE_ENCHANT_TABLE.fromJson("disableEnchantTable");

        // Mechanics

        public boolean oldFire = GameplayTweak.OLD_FIRE.fromJson("oldFire");
        public boolean instantAir = GameplayTweak.INSTANT_AIR.fromJson("instantAir");
        public boolean infiniteBurn = GameplayTweak.INFINITE_BURN.fromJson("infiniteBurn");
        public boolean leftClickDoor = GameplayTweak.LEFT_CLICK_DOOR.fromJson("leftClickDoor");
        public boolean leftClickLever = GameplayTweak.LEFT_CLICK_LEVER.fromJson("leftClickLever");
        public boolean leftClickButton = GameplayTweak.LEFT_CLICK_BUTTON.fromJson("leftClickButton");
        public boolean instantBonemeal = GameplayTweak.INSTANT_BONEMEAL.fromJson("instantBonemeal");
        public boolean tilledGrassSeeds = GameplayTweak.TILLED_GRASS_SEEDS.fromJson("tilledGrassSeeds");
        public boolean disableBedBounce = GameplayTweak.DISABLE_BED_BOUNCE.fromJson("disableBedBounce");
        public boolean disableSprint = GameplayTweak.DISABLE_SPRINT.fromJson("disableSprint");
        public boolean disableSwim = GameplayTweak.DISABLE_SWIM.fromJson("disableSwim");
        public boolean cartBoosting = GameplayTweak.CART_BOOSTING.fromJson("cartBoosting");

        // Food

        public boolean disableHunger = GameplayTweak.DISABLE_HUNGER.fromJson("disableHunger");
        public boolean instantEat = GameplayTweak.INSTANT_EAT.fromJson("instantEat");
        public boolean oldFoodStacking = GameplayTweak.OLD_FOOD_STACKING.fromJson("oldFoodStacking");

        public ItemMap<Integer> customFoodHealth = GameplayTweak.CUSTOM_FOOD_HEALTH.fromJson("customFoodHealth");
        public ItemMap<Integer> customFoodStacking = GameplayTweak.CUSTOM_FOOD_STACKING.fromJson("customFoodStacking");
        public ItemMap<Integer> customItemStacking = GameplayTweak.CUSTOM_ITEM_STACKING.fromJson("customItemStacking");
    }

    public Gameplay gameplay = new Gameplay();

    public static class Animation
    {
        // Player

        public boolean oldCreativeCrouch = AnimationTweak.OLD_CREATIVE_CROUCH.fromJson("oldCreativeCrouch");
        public boolean oldDirectionalDamage = AnimationTweak.OLD_DIRECTIONAL_DAMAGE.fromJson("oldDirectionalDamage");
    }

    public Animation animation = new Animation();
}
