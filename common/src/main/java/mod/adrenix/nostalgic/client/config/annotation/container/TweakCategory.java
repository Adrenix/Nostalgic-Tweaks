package mod.adrenix.nostalgic.client.config.annotation.container;

import mod.adrenix.nostalgic.common.config.reflect.TweakGroup;
import mod.adrenix.nostalgic.util.common.LangUtil;

/**
 * This enumeration defines category containers for config tweak groups.
 * The tweak group is available in each enumeration value.
 */

public enum TweakCategory
{
    // Sound Categories

    AMBIENT_SOUND(TweakGroup.SOUND, LangUtil.Gui.SOUND_CATEGORY_AMBIENT),
    BLOCK_SOUND(TweakGroup.SOUND, LangUtil.Gui.SOUND_CATEGORY_BLOCK),
    DAMAGE_SOUND(TweakGroup.SOUND, LangUtil.Gui.SOUND_CATEGORY_DAMAGE),
    EXPERIENCE_SOUND(TweakGroup.SOUND, LangUtil.Gui.SOUND_CATEGORY_EXPERIENCE),
    MOB_SOUND(TweakGroup.SOUND, LangUtil.Gui.SOUND_CATEGORY_MOB),

    // Candy Categories

    BLOCK_CANDY(TweakGroup.CANDY, LangUtil.Gui.CANDY_CATEGORY_BLOCK),
    INTERFACE_CANDY(TweakGroup.CANDY, LangUtil.Gui.CANDY_CATEGORY_GUI),
    ITEM_CANDY(TweakGroup.CANDY, LangUtil.Gui.CANDY_CATEGORY_ITEM),
    LIGHTING_CANDY(TweakGroup.CANDY, LangUtil.Gui.CANDY_CATEGORY_LIGHTING),
    PARTICLE_CANDY(TweakGroup.CANDY, LangUtil.Gui.CANDY_CATEGORY_PARTICLE),
    WORLD_CANDY(TweakGroup.CANDY, LangUtil.Gui.CANDY_CATEGORY_WORLD),

    // Gameplay Categories

    BUG_GAMEPLAY(TweakGroup.GAMEPLAY, LangUtil.Gui.GAMEPLAY_CATEGORY_BUG),
    COMBAT_GAMEPLAY(TweakGroup.GAMEPLAY, LangUtil.Gui.GAMEPLAY_CATEGORY_COMBAT),
    EXPERIENCE_GAMEPLAY(TweakGroup.GAMEPLAY, LangUtil.Gui.GAMEPLAY_CATEGORY_EXPERIENCE),
    MECHANICS_GAMEPLAY(TweakGroup.GAMEPLAY, LangUtil.Gui.GAMEPLAY_CATEGORY_MECHANICS),
    HUNGER_GAMEPLAY(TweakGroup.GAMEPLAY, LangUtil.Gui.GAMEPLAY_CATEGORY_HUNGER),
    MOB_GAMEPLAY(TweakGroup.GAMEPLAY, LangUtil.Gui.GAMEPLAY_CATEGORY_MOB),

    // Animation Categories

    ARM_ANIMATION(TweakGroup.ANIMATION, LangUtil.Gui.ANIMATION_CATEGORY_ARM),
    ITEM_ANIMATION(TweakGroup.ANIMATION, LangUtil.Gui.ANIMATION_CATEGORY_ITEM),
    MOB_ANIMATION(TweakGroup.ANIMATION, LangUtil.Gui.ANIMATION_CATEGORY_MOB),
    PLAYER_ANIMATION(TweakGroup.ANIMATION, LangUtil.Gui.ANIMATION_CATEGORY_PLAYER),

    // Swing Categories

    ITEM_SWING(TweakGroup.SWING, LangUtil.Gui.SWING_CATEGORY_ITEM),
    POTION_SWING(TweakGroup.SWING, LangUtil.Gui.SWING_CATEGORY_POTION);

    /*
       Enumeration Class

       Fields, constructor, and methods.
     */

    /* Fields */

    private final String langKey;
    private final TweakGroup tweakGroup;

    /* Constructor */

    /**
     * Create a new category.
     *
     * @param tweakGroup The group type associated with this category.
     * @param langKey    The language file key associated with this category.
     */
    TweakCategory(TweakGroup tweakGroup, String langKey)
    {
        this.tweakGroup = tweakGroup;
        this.langKey = langKey;
    }

    /* Methods */

    /**
     * Get the group type associated with this category.
     * @return A group type enumeration value.
     */
    public TweakGroup getGroup() { return this.tweakGroup; }

    /**
     * Get the language key associated with this group.
     * @return A language file key.
     */
    public String getLangKey() { return this.langKey; }
}
