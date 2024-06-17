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

    public boolean serverSideOnly = ModTweak.SERVER_SIDE_ONLY.register("serverSideOnly");
    public boolean serverLogging = ModTweak.SERVER_LOGGING.register("serverLogging");
    public boolean serverDebugMode = ModTweak.SERVER_DEBUG.register("serverDebugMode");

    public static class Mod
    {
        // Config Management

        public int numberOfBackups = ModTweak.NUMBER_OF_BACKUPS.register("numberOfBackups");
    }

    public Mod mod = new Mod();

    public static class EyeCandy
    {
        public Hotbar oldCreativeHotbar = CandyTweak.OLD_CREATIVE_HOTBAR.register("oldCreativeHotbar");
        public int itemMergeLimit = CandyTweak.ITEM_MERGE_LIMIT.register("itemMergeLimit");
        public boolean oldItemMerging = CandyTweak.OLD_ITEM_MERGING.register("oldItemMerging");
        public boolean oldSquareBorder = CandyTweak.OLD_SQUARE_BORDER.register("oldSquareBorder");
        public boolean oldClassicEngine = CandyTweak.OLD_CLASSIC_ENGINE.register("oldClassicEngine");
        public boolean applyChestVoxel = CandyTweak.APPLY_CHEST_VOXEL.register("applyChestVoxel");
        public boolean debugEntityId = CandyTweak.DEBUG_ENTITY_ID.register("debugEntityId");
    }

    public EyeCandy eyeCandy = new EyeCandy();

    public static class Gameplay
    {
        // Bugs

        public boolean oldLadderGap = GameplayTweak.OLD_LADDER_GAP.register("oldLadderGap");
        public boolean oldSquidMilking = GameplayTweak.OLD_SQUID_MILKING.register("oldSquidMilking");

        // Mob AI

        public boolean disableAnimalPanic = GameplayTweak.DISABLE_ANIMAL_PANIC.register("disableAnimalPanic");
        public boolean disableMonsterAvoidSun = GameplayTweak.DISABLE_MONSTER_AVOID_SUN.register("disableMonsterAvoidSun");

        // Monsters

        public boolean disableMonsterItemPickup = GameplayTweak.DISABLE_MONSTER_ITEM_PICKUP.register("disableMonsterItemPickup");

        // Monster Spawning

        public boolean disableBabyZombieSpawn = GameplayTweak.DISABLE_BABY_ZOMBIE_SPAWN.register("disableBabyZombieSpawn");
        public boolean disableBabyPiglinSpawn = GameplayTweak.DISABLE_BABY_PIGLIN_SPAWN.register("disableBabyPiglinSpawn");
        public boolean disableMonsterItemSpawn = GameplayTweak.DISABLE_MONSTER_ITEM_SPAWN.register("disableMonsterItemSpawn");
        public boolean disableMonsterArmorSpawn = GameplayTweak.DISABLE_MONSTER_ARMOR_SPAWN.register("disableMonsterArmorSpawn");
        public boolean disableMonsterEnchantSpawn = GameplayTweak.DISABLE_MONSTER_ENCHANT_SPAWN.register("disableMonsterEnchantSpawn");
        public boolean piglinOnlyGoldSwordSpawn = GameplayTweak.PIGLIN_ONLY_GOLD_SWORD_SPAWN.register("piglinOnlyGoldSwordSpawn");
        public int monsterSpawnCap = GameplayTweak.MONSTER_SPAWN_CAP.register("monsterSpawnCap");

        // Animal Spawning

        public int animalSpawnCap = GameplayTweak.ANIMAL_SPAWN_CAP.register("animalSpawnCap");
        public boolean oldAnimalSpawning = GameplayTweak.OLD_ANIMAL_SPAWNING.register("oldAnimalSpawning");

        // Sheep

        public boolean disableSheepEatGrass = GameplayTweak.DISABLE_SHEEP_EAT_GRASS.register("disableSheepEatGrass");
        public boolean randomSheepWoolRegen = GameplayTweak.RANDOM_SHEEP_WOOL_REGEN.register("randomSheepWoolRegen");
        public boolean oldSheepPunching = GameplayTweak.OLD_SHEEP_PUNCHING.register("oldSheepPunching");
        public boolean oneWoolPunch = GameplayTweak.ONE_WOOL_PUNCH.register("oneWoolPunch");

        // Mob Drops

        public boolean oldZombiePigmenDrops = GameplayTweak.OLD_ZOMBIE_PIGMEN_DROPS.register("oldZombiePigmenDrops");
        public boolean oldSkeletonDrops = GameplayTweak.OLD_SKELETON_DROPS.register("oldSkeletonDrops");
        public boolean oldChickenDrops = GameplayTweak.OLD_CHICKEN_DROPS.register("oldChickenDrops");
        public boolean oldZombieDrops = GameplayTweak.OLD_ZOMBIE_DROPS.register("oldZombieDrops");
        public boolean oldSpiderDrops = GameplayTweak.OLD_SPIDER_DROPS.register("oldSpiderDrops");
        public boolean oldSheepDrops = GameplayTweak.OLD_SHEEP_DROPS.register("oldSheepDrops");
        public boolean oldCowDrops = GameplayTweak.OLD_COW_DROPS.register("oldCowDrops");
        public boolean oldPigDrops = GameplayTweak.OLD_PIG_DROPS.register("oldPigDrops");

        public boolean oldStyleZombieVillagerDrops = GameplayTweak.OLD_STYLE_ZOMBIE_VILLAGER_DROPS.register("oldStyleZombieVillagerDrops");
        public boolean oldStyleCaveSpiderDrops = GameplayTweak.OLD_STYLE_CAVE_SPIDER_DROPS.register("oldStyleCaveSpiderDrops");
        public boolean oldStyleMooshroomDrops = GameplayTweak.OLD_STYLE_MOOSHROOM_DROPS.register("oldStyleMooshroomDrops");
        public boolean oldStyleDrownedDrops = GameplayTweak.OLD_STYLE_DROWNED_DROPS.register("oldStyleDrownedDrops");
        public boolean oldStyleRabbitDrops = GameplayTweak.OLD_STYLE_RABBIT_DROPS.register("oldStyleRabbitDrops");
        public boolean oldStyleStrayDrops = GameplayTweak.OLD_STYLE_STRAY_DROPS.register("oldStyleStrayDrops");
        public boolean oldStyleHuskDrops = GameplayTweak.OLD_STYLE_HUSK_DROPS.register("oldStyleHuskDrops");

        // Combat Blocking

        public boolean oldSwordBlocking = GameplayTweak.OLD_SWORD_BLOCKING.register("oldSwordBlocking");
        public boolean attackWhileSwordBlocking = GameplayTweak.ATTACK_WHILE_SWORD_BLOCKING.register("attackWhileSwordBlocking");
        public int swordBlockDamageReduction = GameplayTweak.SWORD_BLOCK_DAMAGE_REDUCTION.register("swordBlockDamageReduction");

        // Combat

        public boolean oldDamageValues = GameplayTweak.OLD_DAMAGE_VALUES.register("oldDamageValues");
        public boolean disableCooldown = GameplayTweak.DISABLE_COOLDOWN.register("disableCooldown");
        public boolean disableMissTimer = GameplayTweak.DISABLE_MISS_TIMER.register("disableMissTimer");
        public boolean disableCriticalHit = GameplayTweak.DISABLE_CRITICAL_HIT.register("disableCriticalHit");
        public boolean disableSweep = GameplayTweak.DISABLE_SWEEP.register("disableSweep");

        // Combat Bow

        public int arrowSpeed = GameplayTweak.ARROW_SPEED.register("arrowSpeed");
        public boolean instantBow = GameplayTweak.INSTANT_BOW.register("instantBow");
        public boolean invincibleBow = GameplayTweak.INVINCIBLE_BOW.register("invincibleBow");

        // Experience

        public boolean disableOrbSpawn = GameplayTweak.DISABLE_ORB_SPAWN.register("disableOrbSpawn");
        public boolean disableAnvil = GameplayTweak.DISABLE_ANVIL.register("disableAnvil");
        public boolean disableEnchantTable = GameplayTweak.DISABLE_ENCHANT_TABLE.register("disableEnchantTable");

        // Player Mechanics

        public boolean oldNightmares = GameplayTweak.OLD_NIGHTMARES.register("oldNightmares");
        public boolean disableSprint = GameplayTweak.DISABLE_SPRINT.register("disableSprint");
        public boolean leftClickDoor = GameplayTweak.LEFT_CLICK_DOOR.register("leftClickDoor");
        public boolean leftClickLever = GameplayTweak.LEFT_CLICK_LEVER.register("leftClickLever");
        public boolean leftClickButton = GameplayTweak.LEFT_CLICK_BUTTON.register("leftClickButton");

        // Farming Mechanics

        public boolean instantBonemeal = GameplayTweak.INSTANT_BONEMEAL.register("instantBonemeal");
        public boolean tilledGrassSeeds = GameplayTweak.TILLED_GRASS_SEEDS.register("tilledGrassSeeds");

        // Fire Mechanics

        public boolean oldFire = GameplayTweak.OLD_FIRE.register("oldFire");
        public boolean infiniteBurn = GameplayTweak.INFINITE_BURN.register("infiniteBurn");

        // Swimming Mechanics

        public boolean instantAir = GameplayTweak.INSTANT_AIR.register("instantAir");
        public boolean disableSwim = GameplayTweak.DISABLE_SWIM.register("disableSwim");

        // Minecart Mechanics

        public boolean cartBoosting = GameplayTweak.CART_BOOSTING.register("cartBoosting");

        // Boat Mechanics

        public boolean oldBoatWaterLift = GameplayTweak.OLD_BOAT_WATER_LIFT.register("oldBoatWaterLift");
        public boolean oldBoatDrops = GameplayTweak.OLD_BOAT_DROPS.register("oldBoatDrops");

        // Block Mechanics

        public boolean punchTntIgnition = GameplayTweak.PUNCH_TNT_IGNITION.register("punchTntIgnition");
        public boolean disableBedBounce = GameplayTweak.DISABLE_BED_BOUNCE.register("disableBedBounce");
        public boolean alwaysOpenChest = GameplayTweak.ALWAYS_OPEN_CHEST.register("alwaysOpenChest");

        // Food

        public boolean disableHunger = GameplayTweak.DISABLE_HUNGER.register("disableHunger");
        public boolean instantEat = GameplayTweak.INSTANT_EAT.register("instantEat");
        public boolean oldFoodStacking = GameplayTweak.OLD_FOOD_STACKING.register("oldFoodStacking");
        public boolean preventHungerEffect = GameplayTweak.PREVENT_HUNGER_EFFECT.register("preventHungerEffect");

        public ItemMap<Integer> customFoodHealth = GameplayTweak.CUSTOM_FOOD_HEALTH.register("customFoodHealth");
        public ItemMap<Integer> customFoodStacking = GameplayTweak.CUSTOM_FOOD_STACKING.register("customFoodStacking");
        public ItemMap<Integer> customItemStacking = GameplayTweak.CUSTOM_ITEM_STACKING.register("customItemStacking");
    }

    public Gameplay gameplay = new Gameplay();

    public static class Animation
    {
        // Player

        public boolean oldCreativeCrouch = AnimationTweak.OLD_CREATIVE_CROUCH.register("oldCreativeCrouch");
    }

    public Animation animation = new Animation();
}
