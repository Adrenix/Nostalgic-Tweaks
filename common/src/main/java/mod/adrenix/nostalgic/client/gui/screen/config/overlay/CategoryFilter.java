package mod.adrenix.nostalgic.client.gui.screen.config.overlay;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.widget.blank.BlankWidget;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonTemplate;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.grid.Grid;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.color.Gradient;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;
import mod.adrenix.nostalgic.util.common.data.NullableHolder;
import mod.adrenix.nostalgic.util.common.function.BooleanConsumer;
import mod.adrenix.nostalgic.util.common.lang.Lang;

import java.util.LinkedHashMap;

public class CategoryFilter
{
    /* Fields */

    private final Runnable onPress;
    private final Overlay overlay;
    private final LinkedHashMap<Container, FlagHolder> toggles = new LinkedHashMap<>();

    /* Constructor */

    /**
     * Create a new category filter overlay instance.
     *
     * @param aboveOrBelow A {@link DynamicWidget} to set the filter overlay above/below.
     * @param onPress      A {@link Runnable} to run when a checkbox changes state.
     */
    public CategoryFilter(DynamicWidget<?, ?> aboveOrBelow, Runnable onPress)
    {
        this.onPress = onPress;
        this.overlay = Overlay.create()
            .setWidth(170)
            .setHeight(220)
            .padding(6)
            .pos(() -> aboveOrBelow.getX() + 1, () -> aboveOrBelow.getY() - 2)
            .aboveOrBelow(aboveOrBelow, 2)
            .outlineColor(Color.WHITE)
            .gradientBackground(Gradient.vertical(Color.RICH_BLACK.fromAlpha(230), Color.DARK_BLUE.fromAlpha(230)))
            .shadowColor(Color.BLACK.fromAlpha(0.2D))
            .scissorPadding(3)
            .resizeHeightForWidgets()
            .unmovable()
            .borderless()
            .build();

        for (Container category : Container.CATEGORIES)
            this.toggles.put(category, FlagHolder.on());

        TextWidget description = TextWidget.create(Lang.Info.FILTER_BY_CATEGORY)
            .extendWidthToScreenEnd(0)
            .build(this.overlay::addWidget);

        NullableHolder<DynamicWidget<?, ?>> previous = NullableHolder.create(SeparatorWidget.create(Color.WHITE)
            .height(1)
            .below(description, 4)
            .extendWidthToScreenEnd(0)
            .build(this.overlay::addWidget));

        this.toggles.forEach((container, checkbox) -> {
            ButtonWidget toggle = ButtonTemplate.toggle(checkbox::get, this.set(checkbox))
                .skipFocusOnClick()
                .below(previous.get(), 4)
                .build(this.overlay::addWidget);

            BlankWidget spacer = BlankWidget.create()
                .width(4)
                .rightOf(toggle, 0)
                .height(GuiUtil.textHeight())
                .onPress(toggle::onPress)
                .build(this.overlay::addWidget);

            TextWidget text = TextWidget.create(container.toString())
                .icon(container.getIcon())
                .color(container.getColor())
                .brightenIconOnHover(1.2F)
                .skipFocusOnClick()
                .cannotFocus()
                .useTextWidth()
                .rightOf(spacer, 0)
                .hoverOrFocusSync(toggle, spacer)
                .hoverOrFocusColor(container.getColor().brighten(0.35D))
                .onPress(toggle::onPress)
                .build(this.overlay::addWidget);

            toggle.getBuilder().hoverOrFocusSync(text, spacer);
            previous.set(text);
        });

        SeparatorWidget separator = SeparatorWidget.create(Color.WHITE)
            .height(1)
            .extendWidthToScreenEnd(0)
            .below(previous.get(), 4)
            .build(this.overlay::addWidget);

        Grid grid = Grid.create(this.overlay, 2)
            .below(separator, 4)
            .extendWidthToScreenEnd(0)
            .build(this.overlay::addWidget);

        ButtonWidget.create(Lang.Button.SHOW_ALL).skipFocusOnClick().icon(Icons.UNDO).onPress(() -> {
            this.toggles.values().forEach(FlagHolder::enable);
            this.onPress.run();
        }).build(grid::addCell);

        ButtonWidget.create(Lang.Button.FILTER_ALL).skipFocusOnClick().icon(Icons.RED_REDO).onPress(() -> {
            this.toggles.values().forEach(FlagHolder::disable);
            this.onPress.run();
        }).build(grid::addCell);
    }

    /**
     * Creates a {@link BooleanConsumer} and runs the {@link Runnable} assigned to this utility for when a toggle is
     * changed.
     *
     * @param flag A {@link FlagHolder} instance.
     * @return A {@link BooleanConsumer} instance.
     */
    private BooleanConsumer set(FlagHolder flag)
    {
        return state -> {
            flag.set(state);
            this.onPress.run();
        };
    }

    /**
     * Open the category filter overlay.
     */
    public void open()
    {
        this.overlay.open();
    }

    /**
     * Check if the given category is filtered.
     *
     * @param category The category {@link Container} to check.
     * @return Whether the category is filtered.
     */
    public boolean test(Container category)
    {
        return this.toggles.getOrDefault(category, FlagHolder.off()).get();
    }
}
