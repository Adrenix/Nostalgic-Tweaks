package mod.adrenix.nostalgic.tweak.enums;

import mod.adrenix.nostalgic.util.common.lang.Translation;
import mod.adrenix.nostalgic.util.common.lang.Lang;

/**
 * The graphical user interface background enumeration is used by tweaks that change the background of transparent game
 * screens, such as the inventory screen.
 */
public enum GuiBackground implements EnumTweak
{
    SOLID_BLACK(Lang.Enum.BACKGROUND_SOLID_BLACK),
    SOLID_BLUE(Lang.Enum.BACKGROUND_SOLID_BLUE),
    GRADIENT_BLUE(Lang.Enum.BACKGROUND_GRADIENT_BLUE);

    private final Translation title;

    GuiBackground(Translation title)
    {
        this.title = title;
    }

    @Override
    public Translation getTitle()
    {
        return this.title;
    }
}
