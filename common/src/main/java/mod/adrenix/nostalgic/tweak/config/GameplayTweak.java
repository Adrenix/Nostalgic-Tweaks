package mod.adrenix.nostalgic.tweak.config;

import mod.adrenix.nostalgic.tweak.TweakAlert;
import mod.adrenix.nostalgic.tweak.container.group.GameplayGroup;
import mod.adrenix.nostalgic.tweak.enums.Corner;
import mod.adrenix.nostalgic.tweak.factory.*;
import mod.adrenix.nostalgic.tweak.gui.SliderType;
import mod.adrenix.nostalgic.tweak.listing.ItemMap;
import mod.adrenix.nostalgic.tweak.listing.ItemRule;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.world.ItemCommonUtil;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Items;

import java.util.LinkedHashMap;

// @formatter:off
public interface GameplayTweak
{
    // Bugs

    TweakFlag OLD_LADDER_GAP = TweakFlag.server(true, GameplayGroup.BUGS).newForUpdate().build();
    TweakFlag OLD_SQUID_MILKING = TweakFlag.server(true, GameplayGroup.BUGS).newForUpdate().build();

    // Mob AI

    TweakFlag DISABLE_ANIMAL_PANIC = TweakFlag.server(true, GameplayGroup.MOB_AI).newForUpdate().build();

    // Mob Spawning

    TweakNumber<Integer> ANIMAL_SPAWN_CAP = TweakNumber.server(25, GameplayGroup.MOB_ANIMAL_SPAWN)
        .slider(Lang.Slider.CAP, 0, 100)
        .whenDisabled(MobCategory.CREATURE.getMaxInstancesPerChunk())
        .newForUpdate()
        .build();

    TweakFlag OLD_ANIMAL_SPAWNING = TweakFlag.server(false, GameplayGroup.MOB_ANIMAL_SPAWN).newForUpdate().build();

    // Sheep

    TweakFlag DISABLE_SHEEP_EAT_GRASS = TweakFlag.server(false, GameplayGroup.MOB_ANIMAL_SHEEP).newForUpdate().build();
    TweakFlag OLD_SHEEP_PUNCHING = TweakFlag.server(false, GameplayGroup.MOB_ANIMAL_SHEEP).newForUpdate().build();
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

    // Combat

    TweakFlag OLD_DAMAGE_VALUES = TweakFlag.server(true, GameplayGroup.COMBAT).newForUpdate().build();
    TweakFlag DISABLE_COOLDOWN = TweakFlag.server(true, GameplayGroup.COMBAT).newForUpdate().build();
    TweakFlag DISABLE_MISS_TIMER = TweakFlag.server(true, GameplayGroup.COMBAT).newForUpdate().build();
    TweakFlag DISABLE_CRITICAL_HIT = TweakFlag.server(true, GameplayGroup.COMBAT).newForUpdate().build();
    TweakFlag DISABLE_SWEEP = TweakFlag.server(true, GameplayGroup.COMBAT).newForUpdate().build();

    // Combat Bow

    TweakNumber<Integer> ARROW_SPEED = TweakNumber.server(70, GameplayGroup.COMBAT_BOW).newForUpdate().slider(0, 100, SliderType.INTENSITY).build();
    TweakFlag INSTANT_BOW = TweakFlag.server(true, GameplayGroup.COMBAT_BOW).newForUpdate().build();
    TweakFlag INVINCIBLE_BOW = TweakFlag.server(true, GameplayGroup.COMBAT_BOW).newForUpdate().build();

    // Experience Bar

    TweakFlag DISABLE_EXPERIENCE_BAR = TweakFlag.client(true, GameplayGroup.EXPERIENCE_BAR).newForUpdate().build();

    // Alternative Experience Text

    TweakFlag SHOW_XP_LEVEL_TEXT = TweakFlag.client(false, GameplayGroup.EXPERIENCE_BAR_ALT_LEVEL).newForUpdate().build();
    TweakFlag SHOW_XP_LEVEL_IN_CREATIVE = TweakFlag.client(false, GameplayGroup.EXPERIENCE_BAR_ALT_LEVEL).newForUpdate().build();
    TweakEnum<Corner> ALT_XP_LEVEL_CORNER = TweakEnum.client(Corner.TOP_LEFT, GameplayGroup.EXPERIENCE_BAR_ALT_LEVEL).newForUpdate().load().build();
    TweakText ALT_XP_LEVEL_TEXT = TweakText.client("Level: %a%v", GameplayGroup.EXPERIENCE_BAR_ALT_LEVEL).newForUpdate().load().build();

    // Alternative Progress Text

    TweakFlag SHOW_XP_PROGRESS_TEXT = TweakFlag.client(false, GameplayGroup.EXPERIENCE_BAR_ALT_PROGRESS).newForUpdate().build();
    TweakFlag SHOW_XP_PROGRESS_IN_CREATIVE = TweakFlag.client(false, GameplayGroup.EXPERIENCE_BAR_ALT_PROGRESS).newForUpdate().build();
    TweakFlag USE_DYNAMIC_PROGRESS_COLOR = TweakFlag.client(true, GameplayGroup.EXPERIENCE_BAR_ALT_PROGRESS).newForUpdate().load().build();
    TweakEnum<Corner> ALT_XP_PROGRESS_CORNER = TweakEnum.client(Corner.TOP_LEFT, GameplayGroup.EXPERIENCE_BAR_ALT_PROGRESS).newForUpdate().load().build();
    TweakText ALT_XP_PROGRESS_TEXT = TweakText.client("Experience: %v%", GameplayGroup.EXPERIENCE_BAR_ALT_PROGRESS).newForUpdate().load().build();

    // Experience Orb

    TweakFlag DISABLE_ORB_SPAWN = TweakFlag.server(true, GameplayGroup.EXPERIENCE_ORB).newForUpdate().build();
    TweakFlag DISABLE_ORB_RENDERING = TweakFlag.client(false, GameplayGroup.EXPERIENCE_ORB).newForUpdate().build();

    // Experience Blocks

    TweakFlag DISABLE_ANVIL = TweakFlag.server(false, GameplayGroup.EXPERIENCE_BLOCK).newForUpdate().build();
    TweakFlag DISABLE_ENCHANT_TABLE = TweakFlag.server(false, GameplayGroup.EXPERIENCE_BLOCK).newForUpdate().build();

    // Player Mechanics

    TweakFlag DISABLE_SPRINT = TweakFlag.dynamic(true, GameplayGroup.MECHANICS_PLAYER).newForUpdate().build();
    TweakFlag LEFT_CLICK_DOOR = TweakFlag.server(true, GameplayGroup.MECHANICS_PLAYER).newForUpdate().build();
    TweakFlag LEFT_CLICK_LEVER = TweakFlag.server(false, GameplayGroup.MECHANICS_PLAYER).newForUpdate().build();
    TweakFlag LEFT_CLICK_BUTTON = TweakFlag.server(false, GameplayGroup.MECHANICS_PLAYER).newForUpdate().build();

    // Farming Mechanics

    TweakFlag INSTANT_BONEMEAL = TweakFlag.server(true, GameplayGroup.MECHANICS_FARMING).newForUpdate().build();
    TweakFlag TILLED_GRASS_SEEDS = TweakFlag.server(true, GameplayGroup.MECHANICS_FARMING).newForUpdate().build();

    // Fire Mechanics

    TweakFlag OLD_FIRE = TweakFlag.server(false, GameplayGroup.MECHANICS_FIRE).newForUpdate().warningTag().build();
    TweakFlag INFINITE_BURN = TweakFlag.server(false, GameplayGroup.MECHANICS_FIRE).newForUpdate().build();

    // Swimming Mechanics

    TweakFlag INSTANT_AIR = TweakFlag.server(true, GameplayGroup.MECHANICS_SWIMMING).newForUpdate().build();
    TweakFlag DISABLE_SWIM = TweakFlag.dynamic(true, GameplayGroup.MECHANICS_SWIMMING).newForUpdate().build();

    // Minecart Mechanics

    TweakFlag CART_BOOSTING = TweakFlag.server(false, GameplayGroup.MECHANICS_CART).newForUpdate().build();

    // Block Mechanics

    TweakFlag DISABLE_BED_BOUNCE = TweakFlag.server(true, GameplayGroup.MECHANICS_BLOCK).newForUpdate().build();

    // Hunger Bar

    TweakFlag DISABLE_HUNGER_BAR = TweakFlag.client(true, GameplayGroup.HUNGER_BAR).newForUpdate().build();

    // Alternative Food Text

    TweakFlag SHOW_HUNGER_FOOD_TEXT = TweakFlag.client(false, GameplayGroup.HUNGER_BAR_ALT_FOOD).newForUpdate().build();
    TweakFlag USE_DYNAMIC_FOOD_COLOR = TweakFlag.client(true, GameplayGroup.HUNGER_BAR_ALT_FOOD).newForUpdate().load().build();
    TweakEnum<Corner> ALT_HUNGER_FOOD_CORNER = TweakEnum.client(Corner.TOP_LEFT, GameplayGroup.HUNGER_BAR_ALT_FOOD).newForUpdate().load().build();
    TweakText ALT_HUNGER_FOOD_TEXT = TweakText.client("Food: %v", GameplayGroup.HUNGER_BAR_ALT_FOOD).newForUpdate().load().build();

    // Alternative Saturation Text

    TweakFlag SHOW_HUNGER_SATURATION_TEXT = TweakFlag.client(false, GameplayGroup.HUNGER_BAR_ALT_SATURATION).newForUpdate().build();
    TweakFlag USE_DYNAMIC_SATURATION_COLOR = TweakFlag.client(true, GameplayGroup.HUNGER_BAR_ALT_SATURATION).newForUpdate().load().build();
    TweakEnum<Corner> ALT_HUNGER_SATURATION_CORNER = TweakEnum.client(Corner.TOP_LEFT, GameplayGroup.HUNGER_BAR_ALT_SATURATION).newForUpdate().load().build();
    TweakText ALT_HUNGER_SATURATION_TEXT = TweakText.client("Saturation: %v%", GameplayGroup.HUNGER_BAR_ALT_SATURATION).newForUpdate().load().build();

    // Food

    /**
     * Generates an old-style food to health restoration listing.
     */
    private static ItemMap<Integer> defaultFoodHealth()
    {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();

        map.put(ItemCommonUtil.getResourceKey(Items.ROTTEN_FLESH), 0);
        map.put(ItemCommonUtil.getResourceKey(Items.SPIDER_EYE), 0);
        map.put(ItemCommonUtil.getResourceKey(Items.CARROT), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.MELON_SLICE), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.CHORUS_FRUIT), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.SWEET_BERRIES), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.GLOW_BERRIES), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.MUSHROOM_STEW), 10);
        map.put(ItemCommonUtil.getResourceKey(Items.BEETROOT_SOUP), 10);
        map.put(ItemCommonUtil.getResourceKey(Items.RABBIT_STEW), 10);
        map.put(ItemCommonUtil.getResourceKey(Items.SUSPICIOUS_STEW), 10);
        map.put(ItemCommonUtil.getResourceKey(Items.GOLDEN_APPLE), 20);
        map.put(ItemCommonUtil.getResourceKey(Items.ENCHANTED_GOLDEN_APPLE), 20);

        return new ItemMap<>(10).startWith(map).rules(ItemRule.ONLY_EDIBLES);
    }

    /**
     * Generates an old-style food stacking listing.
     */
    private static ItemMap<Integer> defaultFoodStacks()
    {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();

        map.put(ItemCommonUtil.getResourceKey(Items.COOKIE), 8);
        map.put(ItemCommonUtil.getResourceKey(Items.BEETROOT), 8);
        map.put(ItemCommonUtil.getResourceKey(Items.CARROT), 8);
        map.put(ItemCommonUtil.getResourceKey(Items.CHORUS_FRUIT), 8);
        map.put(ItemCommonUtil.getResourceKey(Items.DRIED_KELP), 8);
        map.put(ItemCommonUtil.getResourceKey(Items.MELON_SLICE), 8);
        map.put(ItemCommonUtil.getResourceKey(Items.POTATO), 8);
        map.put(ItemCommonUtil.getResourceKey(Items.POISONOUS_POTATO), 8);
        map.put(ItemCommonUtil.getResourceKey(Items.SWEET_BERRIES), 8);
        map.put(ItemCommonUtil.getResourceKey(Items.GLOW_BERRIES), 8);
        map.put(ItemCommonUtil.getResourceKey(Items.APPLE), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.BAKED_POTATO), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.BEEF), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.BEETROOT_SOUP), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.BREAD), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.CHICKEN), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.COD), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.COOKED_BEEF), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.COOKED_CHICKEN), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.COOKED_COD), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.COOKED_MUTTON), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.COOKED_PORKCHOP), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.COOKED_RABBIT), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.COOKED_SALMON), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.ENCHANTED_GOLDEN_APPLE), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.GOLDEN_APPLE), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.GOLDEN_CARROT), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.HONEY_BOTTLE), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.MUSHROOM_STEW), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.MUTTON), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.PORKCHOP), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.PUFFERFISH), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.PUMPKIN_PIE), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.RABBIT), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.RABBIT_STEW), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.SALMON), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.SUSPICIOUS_STEW), 1);
        map.put(ItemCommonUtil.getResourceKey(Items.TROPICAL_FISH), 1);

        return new ItemMap<>(1).startWith(map).rules(ItemRule.ONLY_EDIBLES);
    }

    /**
     * Controls whether food consumption is immediate or delayed.
     */
    TweakFlag INSTANT_EAT = TweakFlag.server(true, GameplayGroup.HUNGER_FOOD).newForUpdate().build();

    /**
     * Controls whether the hunger system is used. When disabled, only consuming food restores health.
     */
    TweakFlag DISABLE_HUNGER = TweakFlag.server(true, GameplayGroup.HUNGER_FOOD).newForUpdate().build();

    /**
     * This listing provides the number of health points a food item restores when consumed.
     */
    TweakItemMap<Integer> CUSTOM_FOOD_HEALTH = TweakItemMap.server(defaultFoodHealth(), GameplayGroup.HUNGER_FOOD)
        .newForUpdate()
        .icon(Icons.HEART)
        .slider(0, 20, SliderType.HEARTS)
        .alert(TweakAlert.FOOD_HEALTH_CONFLICT)
        .load()
        .build();

    /**
     * Controls whether the custom food item stacking map is used.
     */
    TweakFlag OLD_FOOD_STACKING = TweakFlag.server(false, GameplayGroup.HUNGER_FOOD).newForUpdate().build();

    /**
     * This listing provides the maximum stack size of a food item.
     */
    TweakItemMap<Integer> CUSTOM_FOOD_STACKING = TweakItemMap.server(defaultFoodStacks(), GameplayGroup.HUNGER_FOOD)
        .newForUpdate()
        .slider(Lang.Slider.STACK, 1, 64)
        .alert(TweakAlert.FOOD_STACKING_CONFLICT)
        .load()
        .build();

    /**
     * This listing provides the maximum stack size of any item.
     */
    TweakItemMap<Integer> CUSTOM_ITEM_STACKING = TweakItemMap.server(new ItemMap<>(64), GameplayGroup.MECHANICS_ITEMS)
        .newForUpdate()
        .slider(Lang.Slider.STACK, 1, 64)
        .build();
}
