package mod.adrenix.nostalgic.client.gui.screen.config.widget.list.controller;

import mod.adrenix.nostalgic.client.ClientKeyMapping;
import mod.adrenix.nostalgic.client.gui.widget.keybinding.KeybindingWidget;
import mod.adrenix.nostalgic.tweak.factory.TweakBinding;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;

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
        KeybindingWidget widget = KeybindingWidget.create(this.tweak)
            .leftOf(this.controller.getLeftOf(), 1)
            .width(Controller.BUTTON_WIDTH)
            .build();

        this.controller.getLayout().getModern().getBuilder().disableIf(BooleanSupplier.ALWAYS);
        this.controller.getLayout().getSave().getBuilder().disableIf(BooleanSupplier.ALWAYS);
        this.controller.getLayout().getUndo().getBuilder().disableIf(BooleanSupplier.ALWAYS);
        this.controller.getLayout().getReset().getBuilder().onPress(widget::reset);
        this.controller.getLayout()
            .getReset()
            .getBuilder()
            .disableIf(ClientKeyMapping.getFromId(this.tweak.getKeybindingId())::isDefault);

        return widget;
    }
}
