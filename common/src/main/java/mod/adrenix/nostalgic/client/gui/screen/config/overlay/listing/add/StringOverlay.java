package mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.add;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.input.GenericInput;
import mod.adrenix.nostalgic.tweak.listing.Listing;
import mod.adrenix.nostalgic.util.client.KeyboardUtil;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class StringOverlay<V, L extends Listing<V, L>>
{
    /* Fields */

    private final Listing<V, L> listing;
    private final Consumer<String> onStringAdd;
    private final Runnable onEmptyAdd;
    final ButtonWidget done;
    final GenericInput input;
    final Overlay overlay;

    /* Constructor */

    public StringOverlay(Listing<V, L> listing, Runnable onEmptyAdd, Consumer<String> onStringAdd)
    {
        int padding = 2;

        this.listing = listing;
        this.onEmptyAdd = onEmptyAdd;
        this.onStringAdd = onStringAdd;

        this.overlay = Overlay.create(Lang.Listing.ADD)
            .onClose(this::close)
            .runOnKeyPressed(this::onKeyPressed)
            .resizeUsingPercentage(0.4D, 180)
            .resizeHeightForWidgets()
            .padding(padding)
            .build();

        this.input = GenericInput.create()
            .whenEmpty(Lang.Input.TYPE)
            .background(Color.BLACK, Color.INK_BLACK)
            .border(this::getUnfocusedColor, this::getFocusedColor)
            .tooltip(this::getTooltip, 45)
            .extendWidthToScreenEnd(padding)
            .build(this.overlay::addWidget);

        this.done = ButtonWidget.create(Lang.Vanilla.GUI_DONE)
            .onPress(this.overlay::close)
            .below(this.input, padding)
            .icon(Icons.GREEN_CHECK)
            .extendWidthToScreenEnd(padding)
            .build(this.overlay::addWidget);

        this.overlay.setFocused(this.input);
    }

    /* Methods */

    /**
     * Open the add new value to list overlay.
     */
    public void open()
    {
        this.overlay.open();
    }

    /**
     * Instructions to perform when the overlay closes.
     */
    private void close()
    {
        if (this.input.getInput().isEmpty() || this.input.getInput().isBlank())
            this.onEmptyAdd.run();
        else
            this.onStringAdd.accept(this.input.getInput());
    }

    /**
     * @return The unfocused color of the input box.
     */
    private int getUnfocusedColor()
    {
        if (this.input == null)
            return 0;

        return this.listing.containsKey(this.input.getInput()) ? Color.RED.get() : Color.GRAY.get();
    }

    /**
     * @return The focused color of the input box.
     */
    private int getFocusedColor()
    {
        if (this.input == null)
            return 0;

        return this.listing.containsKey(this.input.getInput()) ? Color.RED.get() : Color.WHITE.get();
    }

    /**
     * Handles custom input from the keyboard for the overlay.
     *
     * @param overlay   The {@link Overlay} instance.
     * @param keyCode   The key code that was pressed.
     * @param scanCode  A key scan code.
     * @param modifiers Key code modifiers.
     * @return Whether this handled the key press event.
     */
    private boolean onKeyPressed(Overlay overlay, int keyCode, int scanCode, int modifiers)
    {
        if (KeyboardUtil.isOnlyEnter(keyCode))
        {
            this.overlay.close();
            return true;
        }

        return false;
    }

    /**
     * @return The {@link Component} tooltip to display when the input is already in the list.
     */
    private Component getTooltip()
    {
        if (!this.listing.containsKey(this.input.getInput()))
            return Component.empty();

        return Lang.Listing.ALREADY_ADDED.get(this.input.getInput());
    }
}
