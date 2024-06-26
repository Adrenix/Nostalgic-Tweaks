package mod.adrenix.nostalgic.tweak.config;

import mod.adrenix.nostalgic.tweak.TweakAlert;
import mod.adrenix.nostalgic.tweak.container.group.GameplayGroup;
import mod.adrenix.nostalgic.tweak.factory.TweakFlag;
import mod.adrenix.nostalgic.tweak.factory.TweakItemMap;
import mod.adrenix.nostalgic.tweak.factory.TweakNumber;
import mod.adrenix.nostalgic.tweak.gui.SliderType;
import mod.adrenix.nostalgic.tweak.listing.DefaultListing;
import mod.adrenix.nostalgic.tweak.listing.ItemMap;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.world.entity.MobCategory;

// @formatter:off
public interface GameplayTweak
{
    // Bugs

    TweakFlag OLD_LADDER_GAP = TweakFlag.server(true, GameplayGroup.BUGS).newForUpdate().noSSO().build();
    TweakFlag OLD_SQUID_MILKING = TweakFlag.server(true, GameplayGroup.BUGS).newForUpdate().build();

    // Mob AI

    TweakFlag DISABLE_ANIMAL_PANIC = TweakFlag.server(true, GameplayGroup.MOB_AI).newForUpdate().build();
    TweakFlag DISABLE_MONSTER_AVOID_SUN = TweakFlag.server(true, GameplayGroup.MOB_AI).newForUpdate().build();

    // Monsters

    TweakFlag DISABLE_MONSTER_ITEM_PICKUP = TweakFlag.server(false, GameplayGroup.MOB_MONSTER).newForUpdate().build();

    // Monster Spawning

    TweakFlag DISABLE_BABY_ZOMBIE_SPAWN = TweakFlag.server(false, GameplayGroup.MOB_MONSTER_SPAWN).newForUpdate().build();
    TweakFlag DISABLE_BABY_PIGLIN_SPAWN = TweakFlag.server(false, GameplayGroup.MOB_MONSTER_SPAWN).newForUpdate().build();
    TweakFlag DISABLE_MONSTER_ITEM_SPAWN = TweakFlag.server(false, GameplayGroup.MOB_MONSTER_SPAWN).newForUpdate().build();
    TweakFlag DISABLE_MONSTER_ARMOR_SPAWN = TweakFlag.server(false, GameplayGroup.MOB_MONSTER_SPAWN).newForUpdate().build();
    TweakFlag DISABLE_MONSTER_ENCHANT_SPAWN = TweakFlag.server(false, GameplayGroup.MOB_MONSTER_SPAWN).newForUpdate().build();
    TweakFlag PIGLIN_ONLY_GOLD_SWORD_SPAWN = TweakFlag.server(false, GameplayGroup.MOB_MONSTER_SPAWN).newForUpdate().build();
    TweakNumber<Integer> MONSTER_SPAWN_CAP = TweakNumber.server(90, GameplayGroup.MOB_MONSTER_SPAWN)
        .slider(Lang.Slider.CAP, 0, 100)
        .whenDisabled(MobCategory.MONSTER.getMaxInstancesPerChunk())
        .newForUpdate()
        .build();

    // Animal Spawning

    TweakFlag OLD_ANIMAL_SPAWNING = TweakFlag.server(false, GameplayGroup.MOB_ANIMAL_SPAWN).newForUpdate().warningTag().build();
    TweakNumber<Integer> ANIMAL_SPAWN_CAP = TweakNumber.server(25, GameplayGroup.MOB_ANIMAL_SPAWN)
        .slider(Lang.Slider.CAP, 0, 100)
        .whenDisabled(MobCategory.CREATURE.getMaxInstancesPerChunk())
        .newForUpdate()
        .build();

    // Sheep

    TweakFlag DISABLE_SHEEP_EAT_GRASS = TweakFlag.server(true, GameplayGroup.MOB_ANIMAL_SHEEP).newForUpdate().build();
    TweakFlag RANDOM_SHEEP_WOOL_REGEN = TweakFlag.server(true, GameplayGroup.MOB_ANIMAL_SHEEP).newForUpdate().build();
    TweakFlag OLD_SHEEP_PUNCHING = TweakFlag.server(true, GameplayGroup.MOB_ANIMAL_SHEEP).newForUpdate().build();
    TweakFlag ONE_WOOL_PUNCH = TweakFlag.server(false, GameplayGroup.MOB_ANIMAL_SHEEP).newForUpdate().build();

    // Classic Mob Drops

    TweakFlag OLD_ZOMBIE_PIGMEN_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_CLASSIC).newForUpdate().build();
    TweakFlag OLD_SKELETON_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_CLASSIC).newForUpdate().build();
    TweakFlag OLD_CHICKEN_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_CLASSIC).newForUpdate().build();
    TweakFlag OLD_ZOMBIE_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_CLASSIC).newForUpdate().build();
    TweakFlag OLD_SPIDER_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_CLASSIC).newForUpdate().build();
    TweakFlag OLD_SHEEP_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_CLASSIC).newForUpdate().build();
    TweakFlag OLD_COW_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_CLASSIC).newForUpdate().build();
    TweakFlag OLD_PIG_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_CLASSIC).newForUpdate().build();

    // Modern Mob Drops

    TweakFlag OLD_STYLE_ZOMBIE_VILLAGER_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_MODERN).newForUpdate().build();
    TweakFlag OLD_STYLE_CAVE_SPIDER_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_MODERN).newForUpdate().build();
    TweakFlag OLD_STYLE_MOOSHROOM_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_MODERN).newForUpdate().build();
    TweakFlag OLD_STYLE_DROWNED_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_MODERN).newForUpdate().build();
    TweakFlag OLD_STYLE_RABBIT_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_MODERN).newForUpdate().build();
    TweakFlag OLD_STYLE_STRAY_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_MODERN).newForUpdate().build();
    TweakFlag OLD_STYLE_HUSK_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_MODERN).newForUpdate().build();

    // Combat Blocking

    TweakFlag BLOCK_WITH_SWORD_ON_SHIELD = TweakFlag.client(true, GameplayGroup.COMBAT_BLOCKING).newForUpdate().build();
    TweakFlag OLD_SWORD_BLOCKING = TweakFlag.server(true, GameplayGroup.COMBAT_BLOCKING).noSSO().newForUpdate().build();
    TweakFlag ATTACK_WHILE_SWORD_BLOCKING = TweakFlag.server(true, GameplayGroup.COMBAT_BLOCKING).newForUpdate().build();
    TweakNumber<Integer> SWORD_BLOCK_DAMAGE_REDUCTION = TweakNumber.server(50, GameplayGroup.COMBAT_BLOCKING).newForUpdate().slider(Lang.Slider.PERCENTAGE, 0, 100, "%").build();

    // Combat

    TweakFlag OLD_DAMAGE_VALUES = TweakFlag.server(false, GameplayGroup.COMBAT).newForUpdate().build();
    TweakFlag DISABLE_COOLDOWN = TweakFlag.server(false, GameplayGroup.COMBAT).newForUpdate().build();
    TweakFlag DISABLE_MISS_TIMER = TweakFlag.server(false, GameplayGroup.COMBAT).newForUpdate().build();
    TweakFlag DISABLE_CRITICAL_HIT = TweakFlag.server(false, GameplayGroup.COMBAT).newForUpdate().build();
    TweakFlag DISABLE_SWEEP = TweakFlag.server(false, GameplayGroup.COMBAT).newForUpdate().build();

    // Combat Bow

    TweakNumber<Integer> ARROW_SPEED = TweakNumber.server(70, GameplayGroup.COMBAT_BOW).newForUpdate().slider(0, 100, SliderType.INTENSITY).build();
    TweakFlag INSTANT_BOW = TweakFlag.server(false, GameplayGroup.COMBAT_BOW).newForUpdate().noSSO().build();
    TweakFlag INVINCIBLE_BOW = TweakFlag.server(false, GameplayGroup.COMBAT_BOW).newForUpdate().build();

    // Experience Orb

    TweakFlag DISABLE_ORB_SPAWN = TweakFlag.server(false, GameplayGroup.EXPERIENCE_ORB).newForUpdate().build();
    TweakFlag DISABLE_ORB_RENDERING = TweakFlag.client(false, GameplayGroup.EXPERIENCE_ORB).newForUpdate().build();

    // Experience Blocks

    TweakFlag DISABLE_ANVIL = TweakFlag.server(false, GameplayGroup.EXPERIENCE_BLOCK).newForUpdate().build();
    TweakFlag DISABLE_ENCHANT_TABLE = TweakFlag.server(false, GameplayGroup.EXPERIENCE_BLOCK).newForUpdate().build();

    // Player Mechanics

    TweakFlag OLD_NIGHTMARES = TweakFlag.server(true, GameplayGroup.MECHANICS_PLAYER).newForUpdate().build();
    TweakFlag DISABLE_SPRINT = TweakFlag.dynamic(false, GameplayGroup.MECHANICS_PLAYER).newForUpdate().noSSO().build();
    TweakFlag LEFT_CLICK_DOOR = TweakFlag.server(false, GameplayGroup.MECHANICS_PLAYER).newForUpdate().build();
    TweakFlag LEFT_CLICK_LEVER = TweakFlag.server(false, GameplayGroup.MECHANICS_PLAYER).newForUpdate().build();
    TweakFlag LEFT_CLICK_BUTTON = TweakFlag.server(false, GameplayGroup.MECHANICS_PLAYER).newForUpdate().build();

    // Farming Mechanics

    TweakFlag INSTANT_BONEMEAL = TweakFlag.server(true, GameplayGroup.MECHANICS_FARMING).newForUpdate().build();
    TweakFlag TILLED_GRASS_SEEDS = TweakFlag.server(true, GameplayGroup.MECHANICS_FARMING).newForUpdate().build();

    // Fire Mechanics

    TweakFlag OLD_FIRE = TweakFlag.server(false, GameplayGroup.MECHANICS_FIRE).newForUpdate().warningTag().build();
    TweakFlag INFINITE_BURN = TweakFlag.server(false, GameplayGroup.MECHANICS_FIRE).newForUpdate().build();

    // Swimming Mechanics

    TweakFlag DISABLE_SWIM = TweakFlag.dynamic(false, GameplayGroup.MECHANICS_SWIMMING).newForUpdate().noSSO().build();
    TweakFlag INSTANT_AIR = TweakFlag.server(true, GameplayGroup.MECHANICS_SWIMMING).newForUpdate().build();

    // Minecart Mechanics

    TweakFlag CART_BOOSTING = TweakFlag.server(false, GameplayGroup.MECHANICS_CART).newForUpdate().build();

    // Boat Mechanics

    TweakFlag DISABLE_BOAT_BUSY_HANDS = TweakFlag.server(true, GameplayGroup.MECHANICS_BOAT).newForUpdate().build();
    TweakFlag OLD_BOAT_WATER_LIFT = TweakFlag.server(true, GameplayGroup.MECHANICS_BOAT).newForUpdate().build();
    TweakFlag OLD_BOAT_DROPS = TweakFlag.server(false, GameplayGroup.MECHANICS_BOAT).newForUpdate().build();

    // Block Mechanics

    TweakFlag PUNCH_TNT_IGNITION = TweakFlag.server(false, GameplayGroup.MECHANICS_BLOCK_TNT).newForUpdate().build();
    TweakFlag DISABLE_BED_BOUNCE = TweakFlag.server(true, GameplayGroup.MECHANICS_BLOCK_BED).newForUpdate().build();
    TweakFlag ALWAYS_OPEN_CHEST = TweakFlag.server(true, GameplayGroup.MECHANICS_BLOCK_CHEST).newForUpdate().build();

    // Item Mechanics

    TweakItemMap<Integer> CUSTOM_ITEM_STACKING = TweakItemMap.server(new ItemMap<>(64), GameplayGroup.MECHANICS_ITEMS)
        .newForUpdate()
        .ignoreNetworkCheck()
        .warningTag()
        .slider(Lang.Slider.STACK, 1, 64)
        .build();

    // Food Health

    TweakFlag DISABLE_HUNGER = TweakFlag.server(false, GameplayGroup.HUNGER).newForUpdate().build();
    TweakFlag INSTANT_EAT = TweakFlag.server(false, GameplayGroup.HUNGER_FOOD).newForUpdate().build();
    TweakFlag PREVENT_HUNGER_EFFECT = TweakFlag.server(false, GameplayGroup.HUNGER_FOOD).newForUpdate().build();
    TweakItemMap<Integer> CUSTOM_FOOD_HEALTH = TweakItemMap.server(DefaultListing.foodHealth(), GameplayGroup.HUNGER_FOOD)
        .newForUpdate()
        .icon(Icons.HEART)
        .slider(0, 20, SliderType.HEARTS)
        .alert(TweakAlert.FOOD_HEALTH_CONFLICT)
        .build();

    // Food Stacking

    TweakFlag OLD_FOOD_STACKING = TweakFlag.server(false, GameplayGroup.HUNGER_FOOD).newForUpdate().ignoreNetworkCheck().build();
    TweakItemMap<Integer> CUSTOM_FOOD_STACKING = TweakItemMap.server(DefaultListing.foodStacks(), GameplayGroup.HUNGER_FOOD)
        .newForUpdate()
        .ignoreNetworkCheck()
        .warningTag()
        .slider(Lang.Slider.STACK, 1, 64)
        .alert(TweakAlert.FOOD_STACKING_CONFLICT)
        .build();
}
