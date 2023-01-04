package mod.adrenix.nostalgic.client.config.annotation.container;

import mod.adrenix.nostalgic.util.common.LangUtil;

/**
 * This enumeration defines embed containers for subcategory containers.
 * The subcategory, category, and tweak group is available in each enumeration value.
 */

public enum TweakEmbed
{
    /*
       IMPORTANT:

       To prevent issues, embedded subcategory names should not match any category or subcategory names.
       There will be GUI rendering issues if this occurs.

       The best way to prevent this is to follow a naming scheme for embedded subcategories. The naming scheme should
       be SUBCATEGORY_EMBED_GROUP.

       For example, the name for the button tweaks embed in the title candy subcategory would be: TITLE_BUTTON_CANDY.
     */

    // Title Screen (Eye Candy) - Embedded Subcategories

    TITLE_BUTTON_CANDY(TweakSubcategory.INTERFACE_TITLE_CANDY, LangUtil.Gui.CANDY_EMBED_TITLE_BUTTON),
    TITLE_LOGO_CANDY(TweakSubcategory.INTERFACE_TITLE_CANDY, LangUtil.Gui.CANDY_EMBED_TITLE_LOGO),
    TITLE_TEXT_CANDY(TweakSubcategory.INTERFACE_TITLE_CANDY, LangUtil.Gui.CANDY_EMBED_TITLE_TEXT),

    // Interface Tooltips (Eye Candy) - Embedded Subcategories

    DEBUG_COLOR_CANDY(TweakSubcategory.INTERFACE_DEBUG_CANDY, LangUtil.Gui.CANDY_EMBED_DEBUG_COLOR),
    DEBUG_EXTRA_CANDY(TweakSubcategory.INTERFACE_DEBUG_CANDY, LangUtil.Gui.CANDY_EMBED_DEBUG_EXTRA),
    DEBUG_CHART_CANDY(TweakSubcategory.INTERFACE_DEBUG_CANDY, LangUtil.Gui.CANDY_EMBED_DEBUG_CHART),
    TOOLTIP_PARTS_CANDY(TweakSubcategory.INTERFACE_TOOLTIP_CANDY, LangUtil.Gui.CANDY_EMBED_TOOLTIP_PARTS),

    // World (Eye Candy) - Embedded Subcategories

    CUSTOM_SKY_CANDY(TweakSubcategory.WORLD_SKY_CANDY, LangUtil.Gui.CANDY_EMBED_SKY_CUSTOM),
    CUSTOM_FOG_CANDY(TweakSubcategory.WORLD_FOG_CANDY, LangUtil.Gui.CANDY_EMBED_CUSTOM_FOG),
    WATER_FOG_CANDY(TweakSubcategory.WORLD_FOG_CANDY, LangUtil.Gui.CANDY_EMBED_WATER_FOG),
    SHADER_LIGHT(TweakSubcategory.LIGHTING_WORLD_CANDY, LangUtil.Gui.CANDY_EMBED_SHADER_LIGHT),

    // Void (Eye Candy) - Embedded Subcategories

    VOID_FOG_CANDY(TweakSubcategory.WORLD_VOID_CANDY, LangUtil.Gui.CANDY_EMBED_VOID_FOG),
    VOID_SKY_CANDY(TweakSubcategory.WORLD_VOID_CANDY, LangUtil.Gui.CANDY_EMBED_VOID_SKY),

    // Experience Bar (Gameplay) - Embedded Subcategories

    ALT_XP_LEVEL_GAMEPLAY(TweakSubcategory.EXPERIENCE_BAR_GAMEPLAY, LangUtil.Gui.GAMEPLAY_EMBED_XP_LEVEL),
    ALT_XP_PROGRESS_GAMEPLAY(TweakSubcategory.EXPERIENCE_BAR_GAMEPLAY, LangUtil.Gui.GAMEPLAY_EMBED_XP_PROGRESS),

    // Hunger Bar (Gameplay) - Embedded Subcategories

    ALT_HUNGER_FOOD_GAMEPLAY(TweakSubcategory.HUNGER_BAR_GAMEPLAY, LangUtil.Gui.GAMEPLAY_EMBED_HUNGER_FOOD),
    ALT_HUNGER_SATURATION_GAMEPLAY(TweakSubcategory.HUNGER_BAR_GAMEPLAY, LangUtil.Gui.GAMEPLAY_EMBED_HUNGER_SATURATION),

    // Animals (Mob System) - Embedded Subcategories

    CLASSIC_MOB_DROPS(TweakSubcategory.MOB_DROPS_GAMEPLAY, LangUtil.Gui.GAMEPLAY_EMBED_DROP_CLASSIC),
    MODERN_MOB_DROPS(TweakSubcategory.MOB_DROPS_GAMEPLAY, LangUtil.Gui.GAMEPLAY_EMBED_DROP_MODERN),
    ANIMAL_MOB_SPAWN(TweakSubcategory.MOB_ANIMAL_GAMEPLAY, LangUtil.Gui.GAMEPLAY_EMBED_ANIMAL_SPAWN),
    ANIMAL_MOB_SHEEP(TweakSubcategory.MOB_ANIMAL_GAMEPLAY, LangUtil.Gui.GAMEPLAY_EMBED_ANIMAL_SHEEP);

    /*
       Enumeration Class

       Fields, constructor, and methods.
     */

    /* Fields */

    private final String langKey;
    private final TweakSubcategory subcategory;

    /* Constructor */

    /**
     * Create a new embedded container within a subcategory.
     *
     * @param subcategory The subcategory associated with this embedded subcategory.
     * @param langKey     The language file key associated with this embedded subcategory.
     */
    TweakEmbed(TweakSubcategory subcategory, String langKey)
    {
        this.subcategory = subcategory;
        this.langKey = langKey;
    }

    /* Methods */

    /**
     * Get the subcategory associated with this embedded subcategory.
     * @return A subcategory enumeration value.
     */
    public TweakSubcategory getSubcategory() { return this.subcategory; }

    /**
     * Get the language key associated with this embedded subcategory.
     * @return A language file key.
     */
    public String getLangKey() { return this.langKey; }
}
