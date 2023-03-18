package mod.adrenix.nostalgic.common.config.v2.tweak;

import mod.adrenix.nostalgic.common.config.tweak.TweakType;
import mod.adrenix.nostalgic.common.config.v2.container.group.GameplayGroup;
import mod.adrenix.nostalgic.common.config.v2.gui.SliderType;
import mod.adrenix.nostalgic.common.config.v2.gui.TweakSlider;
import mod.adrenix.nostalgic.util.common.ItemCommonUtil;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Items;

import java.util.LinkedHashMap;

public abstract class GameplayTweak
{
    // Bugs

    public static final Tweak<Boolean> OLD_LADDER_GAP = Tweak.builder(true, TweakSide.SERVER, GameplayGroup.BUGS).newForUpdate().build();
    public static final Tweak<Boolean> OLD_SQUID_MILKING = Tweak.builder(true, TweakSide.SERVER, GameplayGroup.BUGS).newForUpdate().build();

    // Mob AI

    public static final Tweak<Boolean> DISABLE_ANIMAL_PANIC = Tweak.builder(true, TweakSide.SERVER, GameplayGroup.MOB_AI).newForUpdate().build();

    // Mob Spawning

    public static final Tweak<Integer> ANIMAL_SPAWN_CAP = Tweak.builder(25, TweakSide.SERVER, GameplayGroup.MOB_ANIMAL_SPAWN)
        .newForUpdate()
        .whenDisabled(MobCategory.CREATURE.getMaxInstancesPerChunk())
        .slider(TweakSlider.builder(25, 0, 100, 1).langKey(LangUtil.Gui.SLIDER_CAP).build())
        .build()
    ;

    public static final Tweak<Boolean> OLD_ANIMAL_SPAWNING = Tweak.builder(false, TweakSide.SERVER, GameplayGroup.MOB_ANIMAL_SPAWN).newForUpdate().build();

    // Sheep

    public static final Tweak<Boolean> DISABLE_SHEEP_EAT_GRASS = Tweak.builder(false, TweakSide.SERVER, GameplayGroup.MOB_ANIMAL_SHEEP).newForUpdate().build();
    public static final Tweak<Boolean> OLD_SHEEP_PUNCHING = Tweak.builder(false, TweakSide.SERVER, GameplayGroup.MOB_ANIMAL_SHEEP).newForUpdate().build();
    public static final Tweak<Boolean> ONE_WOOL_PUNCH = Tweak.builder(false, TweakSide.SERVER, GameplayGroup.MOB_ANIMAL_SHEEP).newForUpdate().build();

    // Classic Mob Drops

    public static final Tweak<Boolean> OLD_ZOMBIE_PIGMEN_DROPS = Tweak.builder(false, TweakSide.SERVER, GameplayGroup.MOB_DROPS_CLASSIC).newForUpdate().build();
    public static final Tweak<Boolean> OLD_SKELETON_DROPS = Tweak.builder(false, TweakSide.SERVER, GameplayGroup.MOB_DROPS_CLASSIC).newForUpdate().build();
    public static final Tweak<Boolean> OLD_CHICKEN_DROPS = Tweak.builder(false, TweakSide.SERVER, GameplayGroup.MOB_DROPS_CLASSIC).newForUpdate().build();
    public static final Tweak<Boolean> OLD_ZOMBIE_DROPS = Tweak.builder(false, TweakSide.SERVER, GameplayGroup.MOB_DROPS_CLASSIC).newForUpdate().build();
    public static final Tweak<Boolean> OLD_SPIDER_DROPS = Tweak.builder(false, TweakSide.SERVER, GameplayGroup.MOB_DROPS_CLASSIC).newForUpdate().build();
    public static final Tweak<Boolean> OLD_SHEEP_DROPS = Tweak.builder(false, TweakSide.SERVER, GameplayGroup.MOB_DROPS_CLASSIC).newForUpdate().build();
    public static final Tweak<Boolean> OLD_COW_DROPS = Tweak.builder(false, TweakSide.SERVER, GameplayGroup.MOB_DROPS_CLASSIC).newForUpdate().build();
    public static final Tweak<Boolean> OLD_PIG_DROPS = Tweak.builder(false, TweakSide.SERVER, GameplayGroup.MOB_DROPS_CLASSIC).newForUpdate().build();

    // Modern Mob Drops

    public static final Tweak<Boolean> OLD_STYLE_ZOMBIE_VILLAGER_DROPS = Tweak.builder(false, TweakSide.SERVER, GameplayGroup.MOB_DROPS_MODERN).newForUpdate().build();
    public static final Tweak<Boolean> OLD_STYLE_CAVE_SPIDER_DROPS = Tweak.builder(false, TweakSide.SERVER, GameplayGroup.MOB_DROPS_MODERN).newForUpdate().build();
    public static final Tweak<Boolean> OLD_STYLE_MOOSHROOM_DROPS = Tweak.builder(false, TweakSide.SERVER, GameplayGroup.MOB_DROPS_MODERN).newForUpdate().build();
    public static final Tweak<Boolean> OLD_STYLE_DROWNED_DROPS = Tweak.builder(false, TweakSide.SERVER, GameplayGroup.MOB_DROPS_MODERN).newForUpdate().build();
    public static final Tweak<Boolean> OLD_STYLE_RABBIT_DROPS = Tweak.builder(false, TweakSide.SERVER, GameplayGroup.MOB_DROPS_MODERN).newForUpdate().build();
    public static final Tweak<Boolean> OLD_STYLE_STRAY_DROPS = Tweak.builder(false, TweakSide.SERVER, GameplayGroup.MOB_DROPS_MODERN).newForUpdate().build();
    public static final Tweak<Boolean> OLD_STYLE_HUSK_DROPS = Tweak.builder(false, TweakSide.SERVER, GameplayGroup.MOB_DROPS_MODERN).newForUpdate().build();

    // Combat

    public static final Tweak<Boolean> OLD_DAMAGE_VALUES = Tweak.builder(true, TweakSide.SERVER, GameplayGroup.COMBAT).newForUpdate().top().build();
    public static final Tweak<Boolean> DISABLE_COOLDOWN = Tweak.builder(true, TweakSide.SERVER, GameplayGroup.COMBAT).newForUpdate().build();
    public static final Tweak<Boolean> DISABLE_MISS_TIMER = Tweak.builder(true, TweakSide.SERVER, GameplayGroup.COMBAT).newForUpdate().build();
    public static final Tweak<Boolean> DISABLE_CRITICAL_HIT = Tweak.builder(true, TweakSide.SERVER, GameplayGroup.COMBAT).newForUpdate().build();
    public static final Tweak<Boolean> DISABLE_SWEEP = Tweak.builder(true, TweakSide.SERVER, GameplayGroup.COMBAT).newForUpdate().build();

    // Combat Bow

    public static final Tweak<Integer> ARROW_SPEED = Tweak.builder(70, TweakSide.SERVER, GameplayGroup.COMBAT_BOW).newForUpdate().top().slider(TweakSlider.builder(70, 0, 100, 1).type(SliderType.INTENSITY).build()).build();
    public static final Tweak<Boolean> INSTANT_BOW = Tweak.builder(true, TweakSide.SERVER, GameplayGroup.COMBAT_BOW).newForUpdate().top().build();
    public static final Tweak<Boolean> INVINCIBLE_BOW = Tweak.builder(true, TweakSide.SERVER, GameplayGroup.COMBAT_BOW).newForUpdate().top().build();

    // Experience Bar

    public static final Tweak<Boolean> DISABLE_EXPERIENCE_BAR = Tweak.builder(true, TweakSide.CLIENT, GameplayGroup.EXPERIENCE_BAR).newForUpdate().build();

    // Alternative Experience Text

    public static final Tweak<Boolean> SHOW_XP_LEVEL_TEXT = Tweak.builder(false, TweakSide.CLIENT, GameplayGroup.EXPERIENCE_BAR_ALT_LEVEL).newForUpdate().top().build();
    public static final Tweak<Boolean> SHOW_XP_LEVEL_IN_CREATIVE = Tweak.builder(false, TweakSide.CLIENT, GameplayGroup.EXPERIENCE_BAR_ALT_LEVEL).newForUpdate().top().build();
    public static final Tweak<TweakType.Corner> ALT_XP_LEVEL_CORNER = Tweak.builder(TweakType.Corner.TOP_LEFT, TweakSide.CLIENT, GameplayGroup.EXPERIENCE_BAR_ALT_LEVEL).newForUpdate().top().load().build();
    public static final Tweak<String> ALT_XP_LEVEL_TEXT = Tweak.builder("Level: %a%v", TweakSide.CLIENT, GameplayGroup.EXPERIENCE_BAR_ALT_LEVEL).newForUpdate().top().load().build();

    // Alternative Progress Text

    public static final Tweak<Boolean> SHOW_XP_PROGRESS_TEXT = Tweak.builder(false, TweakSide.CLIENT, GameplayGroup.EXPERIENCE_BAR_ALT_PROGRESS).newForUpdate().top().build();
    public static final Tweak<Boolean> SHOW_XP_PROGRESS_IN_CREATIVE = Tweak.builder(false, TweakSide.CLIENT, GameplayGroup.EXPERIENCE_BAR_ALT_PROGRESS).newForUpdate().top().build();
    public static final Tweak<Boolean> USE_DYNAMIC_PROGRESS_COLOR = Tweak.builder(true, TweakSide.CLIENT, GameplayGroup.EXPERIENCE_BAR_ALT_PROGRESS).newForUpdate().top().load().build();
    public static final Tweak<TweakType.Corner> ALT_XP_PROGRESS_CORNER = Tweak.builder(TweakType.Corner.TOP_LEFT, TweakSide.CLIENT, GameplayGroup.EXPERIENCE_BAR_ALT_PROGRESS).newForUpdate().top().load().build();
    public static final Tweak<String> ALT_XP_PROGRESS_TEXT = Tweak.builder("Experience: %v%", TweakSide.CLIENT, GameplayGroup.EXPERIENCE_BAR_ALT_PROGRESS).newForUpdate().top().load().build();

    // Experience Orb

    public static final Tweak<Boolean> DISABLE_ORB_SPAWN = Tweak.builder(true, TweakSide.SERVER, GameplayGroup.EXPERIENCE_ORB).newForUpdate().top().build();
    public static final Tweak<Boolean> DISABLE_ORB_RENDERING = Tweak.builder(false, TweakSide.CLIENT, GameplayGroup.EXPERIENCE_ORB).newForUpdate().top().build();

    // Experience Blocks

    public static final Tweak<Boolean> DISABLE_ANVIL = Tweak.builder(false, TweakSide.SERVER, GameplayGroup.EXPERIENCE_BLOCK).newForUpdate().build();
    public static final Tweak<Boolean> DISABLE_ENCHANT_TABLE = Tweak.builder(false, TweakSide.SERVER, GameplayGroup.EXPERIENCE_BLOCK).newForUpdate().build();

    // Player Mechanics

    public static final Tweak<Boolean> DISABLE_SPRINT = Tweak.builder(true, TweakSide.DYNAMIC, GameplayGroup.MECHANICS_PLAYER).newForUpdate().build();
    public static final Tweak<Boolean> LEFT_CLICK_DOOR = Tweak.builder(true, TweakSide.SERVER, GameplayGroup.MECHANICS_PLAYER).newForUpdate().build();
    public static final Tweak<Boolean> LEFT_CLICK_LEVER = Tweak.builder(false, TweakSide.SERVER, GameplayGroup.MECHANICS_PLAYER).newForUpdate().build();
    public static final Tweak<Boolean> LEFT_CLICK_BUTTON = Tweak.builder(false, TweakSide.SERVER, GameplayGroup.MECHANICS_PLAYER).newForUpdate().build();

    // Farming Mechanics

    public static final Tweak<Boolean> INSTANT_BONEMEAL = Tweak.builder(true, TweakSide.SERVER, GameplayGroup.MECHANICS_FARMING).newForUpdate().build();
    public static final Tweak<Boolean> TILLED_GRASS_SEEDS = Tweak.builder(true, TweakSide.SERVER, GameplayGroup.MECHANICS_FARMING).newForUpdate().build();

    // Fire Mechanics

    public static final Tweak<Boolean> OLD_FIRE = Tweak.builder(false, TweakSide.SERVER, GameplayGroup.MECHANICS_FIRE).newForUpdate().top().warningTag().build();
    public static final Tweak<Boolean> INFINITE_BURN = Tweak.builder(false, TweakSide.SERVER, GameplayGroup.MECHANICS_FIRE).newForUpdate().top().build();

    // Swimming Mechanics

    public static final Tweak<Boolean> INSTANT_AIR = Tweak.builder(true, TweakSide.SERVER, GameplayGroup.MECHANICS_SWIMMING).newForUpdate().build();
    public static final Tweak<Boolean> DISABLE_SWIM = Tweak.builder(true, TweakSide.DYNAMIC, GameplayGroup.MECHANICS_SWIMMING).newForUpdate().build();

    // Minecart Mechanics

    public static final Tweak<Boolean> CART_BOOSTING = Tweak.builder(false, TweakSide.SERVER, GameplayGroup.MECHANICS_CART).newForUpdate().build();

    // Block Mechanics

    public static final Tweak<Boolean> DISABLE_BED_BOUNCE = Tweak.builder(true, TweakSide.SERVER, GameplayGroup.MECHANICS_BLOCK).newForUpdate().build();

    // Hunger Bar

    public static final Tweak<Boolean> DISABLE_HUNGER_BAR = Tweak.builder(true, TweakSide.CLIENT, GameplayGroup.HUNGER_BAR).newForUpdate().build();

    // Alternative Food Text

    public static final Tweak<Boolean> SHOW_HUNGER_FOOD_TEXT = Tweak.builder(false, TweakSide.CLIENT, GameplayGroup.HUNGER_BAR_ALT_FOOD).newForUpdate().top().build();
    public static final Tweak<Boolean> USE_DYNAMIC_FOOD_COLOR = Tweak.builder(true, TweakSide.CLIENT, GameplayGroup.HUNGER_BAR_ALT_FOOD).newForUpdate().top().load().build();
    public static final Tweak<TweakType.Corner> ALT_HUNGER_FOOD_CORNER = Tweak.builder(TweakType.Corner.TOP_LEFT, TweakSide.CLIENT, GameplayGroup.HUNGER_BAR_ALT_FOOD).newForUpdate().top().load().build();
    public static final Tweak<String> ALT_HUNGER_FOOD_TEXT = Tweak.builder("Food: %v", TweakSide.CLIENT, GameplayGroup.HUNGER_BAR_ALT_FOOD).newForUpdate().top().load().build();

    // Alternative Saturation Text

    public static final Tweak<Boolean> SHOW_HUNGER_SATURATION_TEXT = Tweak.builder(false, TweakSide.CLIENT, GameplayGroup.HUNGER_BAR_ALT_SATURATION).newForUpdate().top().build();
    public static final Tweak<Boolean> USE_DYNAMIC_SATURATION_COLOR = Tweak.builder(true, TweakSide.CLIENT, GameplayGroup.HUNGER_BAR_ALT_SATURATION).newForUpdate().top().load().build();
    public static final Tweak<TweakType.Corner> ALT_HUNGER_SATURATION_CORNER = Tweak.builder(TweakType.Corner.TOP_LEFT, TweakSide.CLIENT, GameplayGroup.HUNGER_BAR_ALT_SATURATION).top().load().build();
    public static final Tweak<String> ALT_HUNGER_SATURATION_TEXT = Tweak.builder("Saturation: %v%", TweakSide.CLIENT, GameplayGroup.HUNGER_BAR_ALT_SATURATION).newForUpdate().top().load().build();

    // Food

    public static final Tweak<Boolean> INSTANT_EAT = Tweak.builder(true, TweakSide.SERVER, GameplayGroup.HUNGER_FOOD).newForUpdate().top().build();
    public static final Tweak<Boolean> DISABLE_HUNGER = Tweak.builder(true, TweakSide.SERVER, GameplayGroup.HUNGER_FOOD).newForUpdate().top().build();

    private static LinkedHashMap<String, Integer> getDefaultFoodHealth()
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

        return map;
    }

    public static final Tweak<LinkedHashMap<String, Integer>> CUSTOM_FOOD_HEALTH = Tweak.builder(getDefaultFoodHealth(), TweakSide.SERVER, GameplayGroup.HUNGER_FOOD)
        .newForUpdate()
        .top()
        .slider(TweakSlider.builder(10, 0, 20, 1).type(SliderType.HEARTS).build())
        .alert(TweakAlert::isCustomFoodHealthConflict)
        .load()
        .build()
    ;

    public static final Tweak<Boolean> OLD_FOOD_STACKING = Tweak.builder(false, TweakSide.SERVER, GameplayGroup.HUNGER_FOOD).newForUpdate().top().build();

    private static LinkedHashMap<String, Integer> getDefaultFoodStacks()
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

        return map;
    }

    public static final Tweak<LinkedHashMap<String, Integer>> CUSTOM_FOOD_STACKING = Tweak.builder(getDefaultFoodStacks(), TweakSide.SERVER, GameplayGroup.HUNGER_FOOD)
        .newForUpdate()
        .top()
        .slider(TweakSlider.builder(1, 1, 64, 1).langKey(LangUtil.Gui.SLIDER_STACK).build())
        .alert(TweakAlert::isCustomFoodStackingConflict)
        .load()
        .build()
    ;

    public static final Tweak<LinkedHashMap<String, Integer>> CUSTOM_ITEM_STACKING = Tweak.builder(new LinkedHashMap<String, Integer>(), TweakSide.SERVER, GameplayGroup.MECHANICS_GAMEPLAY)
        .newForUpdate()
        .slider(TweakSlider.builder(64, 1, 64, 1).langKey(LangUtil.Gui.SLIDER_STACK).build())
        .build()
    ;
}
