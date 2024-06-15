package mod.adrenix.nostalgic.client.gui.screen.config.widget.tag;

import mod.adrenix.nostalgic.client.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;

public enum TagType
{
    NEW(Lang.Tag.NEW, Color.GOLDEN_GATE_BRIDGE, Color.FRENCH_PUCE),
    SYNC(Lang.Tag.SYNC, Color.GREEN_APPLE, Color.MUGHAL_GREEN),
    CLIENT(Lang.Tag.CLIENT, Color.METALLIC_BLUE, Color.SPACE_CADET),
    SERVER(Lang.Tag.SERVER, Color.BRASS, Color.DARK_BROWN),
    DYNAMIC(Lang.Tag.DYNAMIC, Color.PURPLE_PLUM, Color.AMERICAN_PURPLE),
    RELOAD(Lang.Tag.RELOAD, Color.COPPER_RED, Color.LIVER_BROWN),
    WARNING(Lang.Tag.WARNING.withStyle(ChatFormatting.GRAY), Color.SCHOOL_BUS, Color.OLIVE_BLACK),
    ALERT(Lang.Tag.ALERT.withStyle(ChatFormatting.GRAY), Color.SCHOOL_BUS, Color.OLIVE_BLACK),
    NO_SSO(Lang.Tag.NO_SSO.withStyle(ChatFormatting.GOLD), Color.QUICK_SILVER, Color.OLIVE_BLACK);

    /* Fields */

    private final MutableComponent title;
    private final Color border;
    private final Color background;

    /* Constructors */

    TagType(MutableComponent title, Color border, Color background)
    {
        this.title = title;
        this.border = border;
        this.background = background;
    }

    TagType(Translation title, Color border, Color background)
    {
        this(title.get(), border, background);
    }

    /* Methods */

    /**
     * @return The config screen's interval timer state for widget animations.
     */
    private boolean isFlashing()
    {
        return GuiUtil.getScreenAs(ConfigScreen.class).stream().anyMatch(ConfigScreen::getTimerState);
    }

    /**
     * @return The {@link MutableComponent} title for the enumeration.
     */
    public MutableComponent getTitle()
    {
        if (this.equals(WARNING) || this.equals(ALERT))
            return this.title.copy().withStyle(this.isFlashing() ? ChatFormatting.RED : ChatFormatting.GRAY);

        return this.title;
    }

    /**
     * @return The border {@link Color} for this enumeration.
     */
    public Color getBorderColor()
    {
        return this.border;
    }

    /**
     * @return The background {@link Color} for this enumeration.
     */
    public Color getBackgroundColor()
    {
        return this.background;
    }
}
