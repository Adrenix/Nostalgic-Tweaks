package mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.add;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.grid.Grid;
import mod.adrenix.nostalgic.client.gui.widget.input.GenericInput;
import mod.adrenix.nostalgic.client.gui.widget.input.suggestion.*;
import mod.adrenix.nostalgic.tweak.listing.ListingSuggestion;
import mod.adrenix.nostalgic.util.client.KeyboardUtil;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class StringOverlay
{
    /* Fields */

    private final Predicate<String> containsKey;
    private final Consumer<String> onFinish;
    private final Runnable onEmpty;
    private final String startWith;
    final ButtonWidget suggestion;
    final ButtonWidget cancel;
    final ButtonWidget done;
    final GenericInput input;
    final Overlay overlay;

    /* Constructors */

    /**
     * Create a new {@link StringOverlay} with an input box that starts with the given input.
     */
    public StringOverlay(Supplier<ListingSuggestion> suggestion, Predicate<String> containsKey, Runnable onEmpty, Consumer<String> onFinish, @Nullable String startWith)
    {
        int padding = 2;

        this.containsKey = containsKey;
        this.onEmpty = onEmpty;
        this.onFinish = onFinish;
        this.startWith = startWith;

        this.overlay = Overlay.create(Lang.Listing.ADD)
            .icon(Icons.SMALL_ADD)
            .runOnKeyPressed(this::onKeyPressed)
            .resizeWidthUsingPercentage(0.6D, 215)
            .resizeHeightForWidgets()
            .padding(padding)
            .build();

        Function<GenericInput, ? extends InputSuggester<GenericInput>> suggester = switch (suggestion.get())
        {
            case CREATURE -> CreatureSuggester::new;
            case PARTICLE -> ParticleSuggester::new;
            case SOUND -> SoundSuggester::new;
            case MOB -> MobSuggester::new;
            case NONE -> null;
        };

        this.input = GenericInput.create()
            .suggester(suggester)
            .startWith(startWith == null ? "" : startWith)
            .whenEmpty(Lang.Input.TYPE)
            .background(Color.BLACK, Color.INK_BLACK)
            .extendWidthToScreenEnd(0)
            .border(this::getUnfocusedColor, this::getFocusedColor)
            .tooltip(this::getTooltip, 45)
            .build(this.overlay::addWidget);

        if (suggester != null)
        {
            this.suggestion = ButtonWidget.create(Lang.Button.SUGGESTIONS)
                .icon(Icons.SMALL_INFO)
                .hoverIcon(Icons.SMALL_INFO_HOVER)
                .tooltip(Lang.Listing.OPEN_SUGGESTIONS, 35, 1, TimeUnit.SECONDS)
                .extendWidthToScreenEnd(0)
                .onPress(() -> new SuggestionOverlay(this.input).open())
                .below(this.input, padding)
                .build(this.overlay::addWidget);
        }
        else
            this.suggestion = null;

        this.cancel = ButtonWidget.create(Lang.Vanilla.GUI_CANCEL)
            .icon(Icons.RED_X)
            .onPress(this.overlay::close)
            .build();

        this.done = ButtonWidget.create(Lang.Vanilla.GUI_DONE)
            .onPress(this::close)
            .disableIf(this::isInputAdded)
            .icon(Icons.GREEN_CHECK)
            .build();

        Grid.create(this.overlay, 2)
            .extendWidthToScreenEnd(0)
            .columnSpacing(padding)
            .below(this.suggestion == null ? this.input : this.suggestion, padding)
            .addCells(this.cancel, this.done)
            .build(this.overlay::addWidget);

        this.overlay.setFocused(this.input);
        this.input.moveCursorToEnd(false);
    }

    /**
     * Create a new {@link StringOverlay} with an empty input box.
     */
    public StringOverlay(Supplier<ListingSuggestion> suggestion, Predicate<String> containsKey, Runnable onEmpty, Consumer<String> onFinish)
    {
        this(suggestion, containsKey, onEmpty, onFinish, null);
    }

    /* Methods */

    /**
     * Open the add new string value overlay.
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
        String input = this.input.getInput();

        if (this.isInputAdded() || input.isBlank())
            this.onEmpty.run();
        else
            this.onFinish.accept(input);

        this.overlay.close();
    }

    /**
     * @return Whether the current input is already added to the string listing.
     */
    private boolean isInputAdded()
    {
        String input = this.input.getInput();

        return !input.equals(this.startWith) && this.containsKey.test(input);
    }

    /**
     * @return The unfocused color of the input box.
     */
    private int getUnfocusedColor()
    {
        if (this.input == null)
            return 0;

        return this.isInputAdded() ? Color.RED.get() : Color.GRAY.get();
    }

    /**
     * @return The focused color of the input box.
     */
    private int getFocusedColor()
    {
        if (this.input == null)
            return 0;

        return this.isInputAdded() ? Color.RED.get() : Color.WHITE.get();
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
        if (KeyboardUtil.isReturnOrEnter(keyCode) && this.input.isFocused())
        {
            this.close();
            return true;
        }

        if (KeyboardUtil.isSearching(keyCode) && this.suggestion != null)
        {
            this.suggestion.onPress();
            return true;
        }

        return false;
    }

    /**
     * @return The {@link Component} tooltip to display when the input is already in the list.
     */
    private Component getTooltip()
    {
        return this.isInputAdded() ? Lang.Listing.INPUT_COPIED.get() : Component.empty();
    }
}
