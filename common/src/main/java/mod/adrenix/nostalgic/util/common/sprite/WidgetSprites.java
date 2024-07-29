package mod.adrenix.nostalgic.util.common.sprite;

/**
 * Simple backport of the 1.20.2+ widget sprite utility. This class needs to be server-safe since common utilities will
 * class-load this.
 *
 * @param enabled         The {@link GuiSprite} to use when the widget is enabled and not focused.
 * @param disabled        The {@link GuiSprite} to use when the widget is disabled and not focused.
 * @param enabledFocused  The {@link GuiSprite} to use when the widget is enabled and focused.
 * @param disabledFocused The {@link GuiSprite} to use when the widget is disabled and focused.
 */
public record WidgetSprites(GuiSprite enabled, GuiSprite disabled, GuiSprite enabledFocused, GuiSprite disabledFocused)
{
    /**
     * Define a widget that does not have a texture for a focused state.
     *
     * @param enabled  The {@link GuiSprite} to use when the widget is enabled.
     * @param disabled The {@link GuiSprite} to use when the widget is disabled.
     */
    public WidgetSprites(GuiSprite enabled, GuiSprite disabled)
    {
        this(enabled, enabled, disabled, disabled);
    }

    /**
     * Get a sprite to use based on widget context.
     *
     * @param enabled Whether the widget is enabled.
     * @param focused Whether the widget is focused.
     * @return A {@link GuiSprite} instance to use.
     */
    public GuiSprite get(boolean enabled, boolean focused)
    {
        if (enabled)
            return focused ? this.enabledFocused : this.enabled;
        else
            return focused ? this.disabledFocused : this.disabled;
    }
}
