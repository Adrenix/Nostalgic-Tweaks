package mod.adrenix.nostalgic.common.config.v2.container.group;

import mod.adrenix.nostalgic.common.config.v2.container.TweakCategory;
import mod.adrenix.nostalgic.common.config.v2.container.TweakContainer;

public abstract class GameplayGroup
{
    // Bugs

    public static final TweakContainer BUGS = TweakContainer.group(TweakCategory.GAMEPLAY, "bugs");

    // Combat

    public static final TweakContainer COMBAT = TweakContainer.group(TweakCategory.GAMEPLAY, "combat");
    public static final TweakContainer COMBAT_BOW = TweakContainer.group(COMBAT, "combat_bow");

    // Experience

    public static final TweakContainer EXPERIENCE = TweakContainer.group(TweakCategory.GAMEPLAY, "experience");
    public static final TweakContainer EXPERIENCE_BAR = TweakContainer.group(EXPERIENCE, "experience_bar");
    public static final TweakContainer EXPERIENCE_BAR_ALT_LEVEL = TweakContainer.group(EXPERIENCE_BAR, "experience_bar_alt_level");
    public static final TweakContainer EXPERIENCE_BAR_ALT_PROGRESS = TweakContainer.group(EXPERIENCE_BAR, "experience_bar_alt_progress");
    public static final TweakContainer EXPERIENCE_ORB = TweakContainer.group(EXPERIENCE, "experience_orb");
    public static final TweakContainer EXPERIENCE_BLOCK = TweakContainer.group(EXPERIENCE, "experience_block");

    // Mechanics

    public static final TweakContainer MECHANICS = TweakContainer.group(TweakCategory.GAMEPLAY, "mechanics");
    public static final TweakContainer MECHANICS_FIRE = TweakContainer.group(MECHANICS, "mechanics_fire");
    public static final TweakContainer MECHANICS_CART = TweakContainer.group(MECHANICS, "mechanics_cart");
    public static final TweakContainer MECHANICS_BLOCK = TweakContainer.group(MECHANICS, "mechanics_block");
    public static final TweakContainer MECHANICS_PLAYER = TweakContainer.group(MECHANICS, "mechanics_player");
    public static final TweakContainer MECHANICS_FARMING = TweakContainer.group(MECHANICS, "mechanics_farming");
    public static final TweakContainer MECHANICS_SWIMMING = TweakContainer.group(MECHANICS, "mechanics_swimming");
    public static final TweakContainer MECHANICS_GAMEPLAY = TweakContainer.group(MECHANICS, "mechanics_gameplay");

    // Hunger

    public static final TweakContainer HUNGER = TweakContainer.group(TweakCategory.GAMEPLAY, "hunger");
    public static final TweakContainer HUNGER_BAR = TweakContainer.group(HUNGER, "hunger_bar");
    public static final TweakContainer HUNGER_BAR_ALT_FOOD = TweakContainer.group(HUNGER_BAR, "hunger_bar_alt_food");
    public static final TweakContainer HUNGER_BAR_ALT_SATURATION = TweakContainer.group(HUNGER_BAR, "hunger_bar_alt_saturation");
    public static final TweakContainer HUNGER_FOOD = TweakContainer.group(HUNGER, "hunger_food");

    // Mob

    public static final TweakContainer MOB = TweakContainer.group(TweakCategory.GAMEPLAY, "mob");
    public static final TweakContainer MOB_AI = TweakContainer.group(MOB, "mob_ai");
    public static final TweakContainer MOB_ANIMAL = TweakContainer.group(MOB, "mob_animal");
    public static final TweakContainer MOB_ANIMAL_SPAWN = TweakContainer.group(MOB_ANIMAL, "mob_animal_spawn");
    public static final TweakContainer MOB_ANIMAL_SHEEP = TweakContainer.group(MOB_ANIMAL, "mob_animal_sheep");
    public static final TweakContainer MOB_DROPS = TweakContainer.group(MOB, "mob_drops");
    public static final TweakContainer MOB_DROPS_CLASSIC = TweakContainer.group(MOB_DROPS, "mob_drops_classic");
    public static final TweakContainer MOB_DROPS_MODERN = TweakContainer.group(MOB_DROPS, "mob_drops_modern");
}
