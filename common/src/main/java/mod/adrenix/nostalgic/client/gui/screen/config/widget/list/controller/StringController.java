package mod.adrenix.nostalgic.client.gui.screen.config.widget.list.controller;

import mod.adrenix.nostalgic.client.gui.overlay.types.color.ColorPicker;
import mod.adrenix.nostalgic.client.gui.widget.input.*;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.tweak.factory.TweakColor;
import mod.adrenix.nostalgic.tweak.factory.TweakText;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.color.HexUtil;
import org.jetbrains.annotations.Nullable;

public class StringController
{
    /* Fields */

    private final Controller controller;
    @Nullable private final TweakText text;
    @Nullable private final TweakColor color;

    /* Constructor */

    /**
     * Create a new string controller instance for a text tweak.
     *
     * @param controller The originating controller.
     * @param text       The text tweak this controller will manage.
     */
    public StringController(Controller controller, @Nullable TweakText text)
    {
        this.controller = controller;
        this.text = text;
        this.color = null;
    }

    /**
     * Create a new string controller instance for a color tweak.
     *
     * @param controller The originating controller.
     * @param color      The color tweak this controller will manage.
     */
    public StringController(Controller controller, @Nullable TweakColor color)
    {
        this.controller = controller;
        this.color = color;
        this.text = null;
    }

    /* Methods */

    /**
     * Get the string tweak this string controller is managing.
     *
     * @return A string {@link Tweak} instance.
     */
    private Tweak<String> getTweak()
    {
        if (this.text != null)
            return this.text;

        return this.color;
    }

    /**
     * Updates the colored tweak's cache value when the color picker overlay is closed.
     *
     * @param picker A {@link ColorPicker} instance.
     */
    private void onPickerClose(ColorPicker picker)
    {
        if (this.color == null)
            return;

        int[] storage = this.color.isOpaque() ? new int[3] : new int[4];
        this.color.setCacheValue(HexUtil.parseString(picker.getColor().getIntComponents(storage)));
    }

    /**
     * @return Create a new input widget instance that will manage a string tweak.
     */
    public AbstractInput<?, ?> getWidget()
    {
        AbstractInputMaker<?, ?> builder = GenericInput.create().maxLength(100);

        if (this.color != null)
        {
            builder = ColorInput.create(new Color(HexUtil.parseInt(this.color.get()))).colorPicker(this::onPickerClose);

            if (this.color.isOpaque())
                ((ColorInputBuilder) builder).opaque();
        }

        return builder.onInput(this.getTweak()::setCacheValue)
            .onSync(this.getTweak()::fromCache)
            .startWith(this.getTweak().fromCache())
            .disableIf(this.getTweak()::isNetworkLocked)
            .leftOf(this.controller.getLeftOf(), 1)
            .width(this.color == null ? Controller.BUTTON_WIDTH : Controller.BUTTON_WIDTH - 21)
            .background(Color.BLACK)
            .border(Color.WHITE)
            .hover(Color.FRENCH_SKY_BLUE, Color.BLACK)
            .build();
    }
}
