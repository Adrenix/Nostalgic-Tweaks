package mod.adrenix.nostalgic.common.config.v2.server;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.ValidateConfig;
import mod.adrenix.nostalgic.common.config.auto.ConfigData;
import mod.adrenix.nostalgic.common.config.auto.annotation.Config;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;
import mod.adrenix.nostalgic.common.config.v2.tweak.AnimationTweak;
import mod.adrenix.nostalgic.common.config.v2.tweak.CandyTweak;
import mod.adrenix.nostalgic.common.config.v2.tweak.GameplayTweak;

import java.util.LinkedHashMap;

/**
 * IMPORTANT:
 *
 * Any update to a field name in this class that is associated with a client/dynamic tweak also needs updated in the
 * client config class as well.
 *
 * @see mod.adrenix.nostalgic.client.config.ClientConfig
 */

@SuppressWarnings("unused") // This class only serves as a structure definition for the config JSON.
@Config(name = "v2" + NostalgicTweaks.MOD_ID + "-server")
public class ServerConfig implements ConfigData
{
    /* Config Validation */

    @Override
    public void validatePostLoad() throws ValidationException
    {
        ValidateConfig.scan(this);
    }

    /* Server Config */

    public static class EyeCandy
    {
        public TweakVersion.Hotbar oldCreativeHotbar = CandyTweak.OLD_CREATIVE_HOTBAR.setAndGet("oldCreativeHotbar");
        public int itemMergeLimit = CandyTweak.ITEM_MERGE_LIMIT.setAndGet("itemMergeLimit");
        public boolean oldChestVoxel = CandyTweak.OLD_CHEST_VOXEL.setAndGet("oldChestVoxel");
        public boolean oldItemMerging = CandyTweak.OLD_ITEM_MERGING.setAndGet("oldItemMerging");
        public boolean oldSquareBorder = CandyTweak.OLD_SQUARE_BORDER.setAndGet("oldSquareBorder");
        public boolean oldClassicLighting = CandyTweak.OLD_CLASSIC_LIGHTING.setAndGet("oldClassicLighting");
        public boolean debugEntityId = CandyTweak.DEBUG_ENTITY_ID.setAndGet("debugEntityId");
    }

    public EyeCandy eyeCandy = new EyeCandy();

    public static class Gameplay
    {
        // Bugs

        public boolean oldLadderGap = GameplayTweak.OLD_LADDER_GAP.setAndGet("oldLadderGap");
        public boolean oldSquidMilking = GameplayTweak.OLD_SQUID_MILKING.setAndGet("oldSquidMilking");

        // Mobs

        public int animalSpawnCap = GameplayTweak.ANIMAL_SPAWN_CAP.setAndGet("animalSpawnCap");
        public boolean disableAnimalPanic = GameplayTweak.DISABLE_ANIMAL_PANIC.setAndGet("disableAnimalPanic");
        public boolean disableSheepEatGrass = GameplayTweak.DISABLE_SHEEP_EAT_GRASS.setAndGet("disableSheepEatGrass");
        public boolean oldAnimalSpawning = GameplayTweak.OLD_ANIMAL_SPAWNING.setAndGet("oldAnimalSpawning");
        public boolean oldSheepPunching = GameplayTweak.OLD_SHEEP_PUNCHING.setAndGet("oldSheepPunching");
        public boolean oneWoolPunch = GameplayTweak.ONE_WOOL_PUNCH.setAndGet("oneWoolPunch");

        // Mob Drops

        public boolean oldZombiePigmenDrops = GameplayTweak.OLD_ZOMBIE_PIGMEN_DROPS.setAndGet("oldZombiePigmenDrops");
        public boolean oldSkeletonDrops = GameplayTweak.OLD_SKELETON_DROPS.setAndGet("oldSkeletonDrops");
        public boolean oldChickenDrops = GameplayTweak.OLD_CHICKEN_DROPS.setAndGet("oldChickenDrops");
        public boolean oldZombieDrops = GameplayTweak.OLD_ZOMBIE_DROPS.setAndGet("oldZombieDrops");
        public boolean oldSpiderDrops = GameplayTweak.OLD_SPIDER_DROPS.setAndGet("oldSpiderDrops");
        public boolean oldSheepDrops = GameplayTweak.OLD_SHEEP_DROPS.setAndGet("oldSheepDrops");
        public boolean oldCowDrops = GameplayTweak.OLD_COW_DROPS.setAndGet("oldCowDrops");
        public boolean oldPigDrops = GameplayTweak.OLD_PIG_DROPS.setAndGet("oldPigDrops");

        public boolean oldStyleZombieVillagerDrops = GameplayTweak.OLD_STYLE_ZOMBIE_VILLAGER_DROPS.setAndGet("oldStyleZombieVillagerDrops");
        public boolean oldStyleCaveSpiderDrops = GameplayTweak.OLD_STYLE_CAVE_SPIDER_DROPS.setAndGet("oldStyleCaveSpiderDrops");
        public boolean oldStyleMooshroomDrops = GameplayTweak.OLD_STYLE_MOOSHROOM_DROPS.setAndGet("oldStyleMooshroomDrops");
        public boolean oldStyleDrownedDrops = GameplayTweak.OLD_STYLE_DROWNED_DROPS.setAndGet("oldStyleDrownedDrops");
        public boolean oldStyleRabbitDrops = GameplayTweak.OLD_STYLE_RABBIT_DROPS.setAndGet("oldStyleRabbitDrops");
        public boolean oldStyleStrayDrops = GameplayTweak.OLD_STYLE_STRAY_DROPS.setAndGet("oldStyleStrayDrops");
        public boolean oldStyleHuskDrops = GameplayTweak.OLD_STYLE_HUSK_DROPS.setAndGet("oldStyleHuskDrops");

        // Combat

        public int arrowSpeed = GameplayTweak.ARROW_SPEED.setAndGet("arrowSpeed");
        public boolean instantBow = GameplayTweak.INSTANT_BOW.setAndGet("instantBow");
        public boolean invincibleBow = GameplayTweak.INVINCIBLE_BOW.setAndGet("invincibleBow");
        public boolean disableCooldown = GameplayTweak.DISABLE_COOLDOWN.setAndGet("disableCooldown");
        public boolean disableMissTimer = GameplayTweak.DISABLE_MISS_TIMER.setAndGet("disableMissTimer");
        public boolean disableCriticalHit = GameplayTweak.DISABLE_CRITICAL_HIT.setAndGet("disableCriticalHit");
        public boolean disableSweep = GameplayTweak.DISABLE_SWEEP.setAndGet("disableSweep");
        public boolean oldDamageValues = GameplayTweak.OLD_DAMAGE_VALUES.setAndGet("oldDamageValues");

        // Experience

        public boolean disableOrbSpawn = GameplayTweak.DISABLE_ORB_SPAWN.setAndGet("disableOrbSpawn");
        public boolean disableAnvil = GameplayTweak.DISABLE_ANVIL.setAndGet("disableAnvil");
        public boolean disableEnchantTable = GameplayTweak.DISABLE_ENCHANT_TABLE.setAndGet("disableEnchantTable");

        // Mechanics

        public boolean oldFire = GameplayTweak.OLD_FIRE.setAndGet("oldFire");
        public boolean instantAir = GameplayTweak.INSTANT_AIR.setAndGet("instantAir");
        public boolean infiniteBurn = GameplayTweak.INFINITE_BURN.setAndGet("infiniteBurn");
        public boolean leftClickDoor = GameplayTweak.LEFT_CLICK_DOOR.setAndGet("leftClickDoor");
        public boolean leftClickLever = GameplayTweak.LEFT_CLICK_LEVER.setAndGet("leftClickLever");
        public boolean leftClickButton = GameplayTweak.LEFT_CLICK_BUTTON.setAndGet("leftClickButton");
        public boolean instantBonemeal = GameplayTweak.INSTANT_BONEMEAL.setAndGet("instantBonemeal");
        public boolean tilledGrassSeeds = GameplayTweak.TILLED_GRASS_SEEDS.setAndGet("tilledGrassSeeds");
        public boolean disableBedBounce = GameplayTweak.DISABLE_BED_BOUNCE.setAndGet("disableBedBounce");
        public boolean disableSprint = GameplayTweak.DISABLE_SPRINT.setAndGet("disableSprint");
        public boolean disableSwim = GameplayTweak.DISABLE_SWIM.setAndGet("disableSwim");
        public boolean cartBoosting = GameplayTweak.CART_BOOSTING.setAndGet("cartBoosting");

        // Food

        public boolean disableHunger = GameplayTweak.DISABLE_HUNGER.setAndGet("disableHunger");
        public boolean instantEat = GameplayTweak.INSTANT_EAT.setAndGet("instantEat");
        public boolean oldFoodStacking = GameplayTweak.OLD_FOOD_STACKING.setAndGet("oldFoodStacking");

        public LinkedHashMap<String, Integer> customFoodHealth = GameplayTweak.CUSTOM_FOOD_HEALTH.setAndGet("customFoodHealth");
        public LinkedHashMap<String, Integer> customFoodStacking = GameplayTweak.CUSTOM_FOOD_STACKING.setAndGet("customFoodStacking");
        public LinkedHashMap<String, Integer> customItemStacking = GameplayTweak.CUSTOM_ITEM_STACKING.setAndGet("customItemStacking");
    }

    public Gameplay gameplay = new Gameplay();

    public static class Animation
    {
        public boolean oldCreativeCrouch = AnimationTweak.OLD_CREATIVE_CROUCH.setAndGet("oldCreativeCrouch");
        public boolean oldDirectionalDamage = AnimationTweak.OLD_DIRECTIONAL_DAMAGE.setAndGet("oldDirectionalDamage");
    }

    public Animation animation = new Animation();
}
