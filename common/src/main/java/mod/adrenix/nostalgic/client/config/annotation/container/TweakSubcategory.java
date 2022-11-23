package mod.adrenix.nostalgic.client.config.annotation.container;

import mod.adrenix.nostalgic.util.common.LangUtil;

/**
 * This enumeration defines subcategory containers for category containers.
 * The category and tweak group is available in each enumeration value.
 */

public enum TweakSubcategory
{
    /*
       IMPORTANT:

       To prevent issues, subcategory names should not match any category names.
       There will be GUI rendering issues if this occurs.

       The best way to prevent this is to prefix the category name to the subcategory name.
     */

    /*
       Sound Candy
     */

    // Block Sound Subcategories

    BLOCK_CHEST_SOUND(TweakCategory.BLOCK_SOUND, LangUtil.Gui.SOUND_SUBCATEGORY_CHEST),

    /*
       Eye Candy
     */

    // Block Candy Subcategories

    BLOCK_CHEST_CANDY(TweakCategory.BLOCK_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_CHEST),
    BLOCK_TORCH_CANDY(TweakCategory.BLOCK_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_TORCH),

    // Interface Candy Subcategories

    INTERFACE_GUI_CANDY(TweakCategory.INTERFACE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_GUI),
    INTERFACE_CHAT_CANDY(TweakCategory.INTERFACE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_CHAT),
    INTERFACE_CRAFTING_CANDY(TweakCategory.INTERFACE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_CRAFTING),
    INTERFACE_DEBUG_CANDY(TweakCategory.INTERFACE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_DEBUG),
    INTERFACE_FURNACE_CANDY(TweakCategory.INTERFACE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_FURNACE),
    INTERFACE_INVENTORY_CANDY(TweakCategory.INTERFACE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_INVENTORY),
    INTERFACE_LOADING_CANDY(TweakCategory.INTERFACE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_LOADING),
    INTERFACE_PAUSE_CANDY(TweakCategory.INTERFACE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_PAUSE),
    INTERFACE_TITLE_CANDY(TweakCategory.INTERFACE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_TITLE),
    INTERFACE_TOOLTIP_CANDY(TweakCategory.INTERFACE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_TOOLTIP),
    INTERFACE_VERSION_CANDY(TweakCategory.INTERFACE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_VERSION),

    // Item Candy Subcategories

    FLAT_ITEM_CANDY(TweakCategory.ITEM_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_FLAT),
    DISPLAY_ITEM_CANDY(TweakCategory.ITEM_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_ITEM),
    MERGING_ITEM_CANDY(TweakCategory.ITEM_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_MERGE),

    // Lighting Candy Subcategories

    LIGHTING_BLOCK_CANDY(TweakCategory.LIGHTING_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_BLOCK_LIGHT),
    LIGHTING_WORLD_CANDY(TweakCategory.LIGHTING_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_WORLD_LIGHT),

    // Particle Candy Subcategories

    PARTICLE_ATTACK_CANDY(TweakCategory.PARTICLE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_ATTACK),
    PARTICLE_BLOCK_CANDY(TweakCategory.PARTICLE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_BLOCK_PARTICLES),
    PARTICLE_EXPLOSION_CANDY(TweakCategory.PARTICLE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_EXPLOSION),
    PARTICLE_PLAYER_CANDY(TweakCategory.PARTICLE_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_PLAYER),

    // World Candy Subcategories

    WORLD_FOG_CANDY(TweakCategory.WORLD_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_FOG),
    WORLD_SKY_CANDY(TweakCategory.WORLD_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_SKY),
    WORLD_VOID_CANDY(TweakCategory.WORLD_CANDY, LangUtil.Gui.CANDY_SUBCATEGORY_VOID),

    /*
       Gameplay
     */

    // Combat System Subcategories

    COMBAT_BOW_GAMEPLAY(TweakCategory.COMBAT_GAMEPLAY, LangUtil.Gui.GAMEPLAY_SUBCATEGORY_BOW),

    // Experience System Subcategories

    EXPERIENCE_BAR_GAMEPLAY(TweakCategory.EXPERIENCE_GAMEPLAY, LangUtil.Gui.GAMEPLAY_SUBCATEGORY_EXPERIENCE_BAR),
    EXPERIENCE_ORB_GAMEPLAY(TweakCategory.EXPERIENCE_GAMEPLAY, LangUtil.Gui.GAMEPLAY_SUBCATEGORY_EXPERIENCE_ORB),
    EXPERIENCE_BLOCK_GAMEPLAY(TweakCategory.EXPERIENCE_GAMEPLAY, LangUtil.Gui.GAMEPLAY_SUBCATEGORY_EXPERIENCE_BLOCK),

    // Game Mechanics Subcategories

    MECHANICS_FIRE_GAMEPLAY(TweakCategory.MECHANICS_GAMEPLAY, LangUtil.Gui.GAMEPLAY_SUBCATEGORY_FIRE),
    MECHANICS_PLAYER_GAMEPLAY(TweakCategory.MECHANICS_GAMEPLAY, LangUtil.Gui.GAMEPLAY_SUBCATEGORY_PLAYER),
    MECHANICS_FARMING_GAMEPLAY(TweakCategory.MECHANICS_GAMEPLAY, LangUtil.Gui.GAMEPLAY_SUBCATEGORY_FARMING),
    MECHANICS_SWIMMING_GAMEPLAY(TweakCategory.MECHANICS_GAMEPLAY, LangUtil.Gui.GAMEPLAY_SUBCATEGORY_SWIMMING),

    // Hunger System Subcategories

    HUNGER_BAR_GAMEPLAY(TweakCategory.HUNGER_GAMEPLAY, LangUtil.Gui.GAMEPLAY_SUBCATEGORY_HUNGER_BAR),
    HUNGER_FOOD_GAMEPLAY(TweakCategory.HUNGER_GAMEPLAY, LangUtil.Gui.GAMEPLAY_SUBCATEGORY_FOOD),

    // Mob System Subcategories

    MOB_AI_GAMEPLAY(TweakCategory.MOB_GAMEPLAY, LangUtil.Gui.GAMEPLAY_SUBCATEGORY_AI),
    MOB_ANIMAL_GAMEPLAY(TweakCategory.MOB_GAMEPLAY, LangUtil.Gui.GAMEPLAY_SUBCATEGORY_ANIMAL),
    MOB_DROPS_GAMEPLAY(TweakCategory.MOB_GAMEPLAY, LangUtil.Gui.GAMEPLAY_SUBCATEGORY_DROPS);

    /*
       Enumeration Class

       Fields, constructor, and methods.
     */

    /* Fields */

    private final String langKey;
    private final TweakCategory category;

    /* Constructor */

    /**
     * Create a new subcategory.
     *
     * @param category The category associated with this subcategory.
     * @param langKey  The language file key associated with this subcategory.
     */
    TweakSubcategory(TweakCategory category, String langKey)
    {
        this.category = category;
        this.langKey = langKey;
    }

    /* Methods */

    /**
     * Get the category associated with this subcategory.
     * @return A category enumeration value.
     */
    public TweakCategory getCategory() { return this.category; }

    /**
     * Get the language key associated with this category.
     * @return A language file key.
     */
    public String getLangKey() { return this.langKey; }
}
