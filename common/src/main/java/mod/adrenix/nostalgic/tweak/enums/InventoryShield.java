package mod.adrenix.nostalgic.tweak.enums;

import mod.adrenix.nostalgic.util.common.lang.Translation;
import mod.adrenix.nostalgic.util.common.lang.Lang;

/**
 * The inventory shield enumeration is used by tweaks that change the position of the off-hand slot in the user's
 * inventory screen.
 */
public enum InventoryShield implements EnumTweak
{
    MODERN(Generic.MODERN.getTitle()),
    INVISIBLE(Lang.Enum.INVENTORY_SHIELD_INVISIBLE),
    MIDDLE_RIGHT(Lang.Enum.INVENTORY_SHIELD_MIDDLE_RIGHT),
    BOTTOM_LEFT(Lang.Enum.INVENTORY_SHIELD_BOTTOM_LEFT);

    private final Translation title;

    InventoryShield(Translation title)
    {
        this.title = title;
    }

    @Override
    public Translation getTitle()
    {
        return this.title;
    }
}
