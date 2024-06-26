package mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.add;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.widget.input.GenericInput;
import mod.adrenix.nostalgic.client.gui.widget.input.suggestion.InputSuggester;
import mod.adrenix.nostalgic.client.gui.widget.list.Row;
import mod.adrenix.nostalgic.client.gui.widget.list.RowList;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.lang.Lang;

public class SuggestionOverlay
{
    /* Fields */

    final Overlay overlay;
    final RowList rowList;
    final GenericInput input;
    final InputSuggester<GenericInput> suggester;

    /* Constructor */

    public SuggestionOverlay(GenericInput input)
    {
        this.input = input;
        this.suggester = input.getSuggester();

        if (suggester == null)
            throw new NullPointerException("Cannot open suggestion overlay since the input suggester is null");

        this.overlay = Overlay.create()
            .size(input.getWidth(), 180)
            .setX(input::getX)
            .addListener(input)
            .aboveOrBelow(input, 0)
            .onClose(this::close)
            .backgroundColor(new Color(0, 0, 0, 235))
            .unmovable()
            .shadowless()
            .borderless()
            .build();

        this.rowList = RowList.create()
            .emptyMessage(Lang.Listing.EMPTY_SUGGESTIONS)
            .defaultRowHeight(GuiUtil.textHeight())
            .extendWidthToScreenEnd(0)
            .extendHeightToScreenEnd(0)
            .build(this.overlay::addWidget);

        input.addListener(this::generateSuggestions);

        this.generateSuggestions(input.getInput());
    }

    /* Methods */

    /**
     * Open a new suggestion list overlay.
     */
    public void open()
    {
        this.overlay.open();
        this.rowList.focusOnFirst();
    }

    /**
     * Close the overlay and remove the input listener.
     */
    public void close()
    {
        this.input.removeListener(this::generateSuggestions);
    }

    /**
     * Generate new suggestions based on the given input.
     *
     * @param input The new user input.
     */
    private void generateSuggestions(String input)
    {
        this.rowList.clear();
        this.suggester.getDatabase().findValues(input).stream().limit(25).forEach(this::createRow);
    }

    /**
     * Create a new suggestion row for the row list.
     *
     * @param suggestion The suggestion.
     */
    private void createRow(String suggestion)
    {
        Row row = Row.create(this.rowList).build();

        Runnable onPress = () -> {
            this.overlay.close();
            this.input.setInput(suggestion);
        };

        TextWidget.create(suggestion)
            .onPress(onPress, Color.LEMON_YELLOW)
            .disableUnderline()
            .noClickSound()
            .extendWidthToEnd(row, 0)
            .build(row::addWidget);

        this.rowList.addBottomRow(row);
    }
}
