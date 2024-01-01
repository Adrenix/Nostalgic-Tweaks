package mod.adrenix.nostalgic.tweak.enums;

import mod.adrenix.nostalgic.util.common.lang.Translation;
import mod.adrenix.nostalgic.util.common.lang.Lang;

/**
 * The recipe book enumeration is used by tweaks that change the position of the recipe book button in the inventory or
 * crafting screen.
 */
public enum RecipeBook implements EnumTweak
{
    MODERN(Generic.MODERN.getTitle()),
    DISABLED(Lang.Enum.RECIPE_BOOK_DISABLED),
    LARGE(Lang.Enum.RECIPE_BOOK_LARGE),
    SMALL(Lang.Enum.RECIPE_BOOK_SMALL);

    private final Translation title;

    RecipeBook(Translation title)
    {
        this.title = title;
    }

    @Override
    public Translation getTitle()
    {
        return this.title;
    }
}
