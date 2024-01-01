package mod.adrenix.nostalgic.client.gui.screen.config.widget.list.controller;

import mod.adrenix.nostalgic.client.ClientKeyMapping;
import mod.adrenix.nostalgic.client.gui.widget.button.AbstractButtonMaker;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.ActiveBuilder;
import mod.adrenix.nostalgic.client.gui.widget.keybinding.KeybindingWidget;
import mod.adrenix.nostalgic.tweak.factory.TweakBinding;

public class KeybindingController
{
    /* Fields */

    private final Controller controller;
    private final TweakBinding tweak;

    /* Constructor */

    /**
     * Create a new keybinding controller instance.
     *
     * @param controller The originating controller.
     * @param tweak      The keybinding tweak this controller manages.
     */
    public KeybindingController(Controller controller, TweakBinding tweak)
    {
        this.controller = controller;
        this.tweak = tweak;
    }

    /* Methods */

    /**
     * @return Create a new key widget button instance that will manage a key binding tweak.
     */
    public KeybindingWidget getWidget()
    {
        KeybindingWidget widget = KeybindingWidget.create(this.tweak.getKeybindingId())
            .leftOf(this.controller.getLeftOf(), 1)
            .width(Controller.BUTTON_WIDTH)
            .build();

        if (this.controller.getLeftOf().getBuilder() instanceof ActiveBuilder<?, ?> builder)
            builder.disableIf(ClientKeyMapping.getFromId(this.tweak.getKeybindingId())::isDefault);

        if (this.controller.getLeftOf().getBuilder() instanceof AbstractButtonMaker<?, ?> builder)
            builder.onPress(widget::reset);

        return widget;
    }
}
