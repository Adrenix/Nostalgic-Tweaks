package mod.adrenix.nostalgic.client.gui.screen.config.widget.list.controller;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonTemplate;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.tweak.enums.EnumTweak;
import mod.adrenix.nostalgic.tweak.factory.TweakEnum;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.data.NullableHolder;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.network.chat.Component;

import java.util.stream.Stream;

public class EnumController
{
    /* Fields */

    private final TweakEnum<?> tweak;
    private final ButtonWidget widget;
    private final Overlay overlay;
    private final int padding = 6;

    /* Constructor */

    /**
     * Create a new enumeration controller instance. The tweak's value must be an instance of an enum class and
     * implement the {@link EnumTweak} interface.
     *
     * @param controller The originating controller.
     * @param tweak      The enum tweak this controller manages.
     */
    public EnumController(Controller controller, TweakEnum<?> tweak)
    {
        this.tweak = tweak;

        this.widget = ButtonWidget.create()
            .width(Controller.BUTTON_WIDTH)
            .leftOf(controller.getLeftOf(), 1)
            .title(this::getTitle)
            .onPress(this::open)
            .disableIf(this.tweak::isNetworkLocked)
            .build();

        this.overlay = Overlay.create()
            .size(Controller.ENUM_WIDTH, Controller.ENUM_HEIGHT)
            .setX(this.widget::getX)
            .padding(this.padding)
            .aboveOrBelow(this.widget, 1)
            .backgroundColor(new Color(0, 0, 0, 235))
            .shadowColor(new Color(0, 0, 0, 80))
            .scissorPadding(3)
            .resizeHeightForWidgets()
            .unmovable()
            .borderless()
            .build();

        this.build();
    }

    /* Methods */

    /**
     * @return Create a new button instance that will manage an enumeration tweak.
     */
    public ButtonWidget getWidget()
    {
        return this.widget;
    }

    /**
     * Opens an overlay that allows the user to pick different enumeration values.
     */
    private void open()
    {
        this.overlay.open();
    }

    /**
     * @return The title for the widget button controller.
     */
    private Component getTitle()
    {
        return this.tweak.fromCache().getTitle().get();
    }

    /**
     * Create widgets from an enum value.
     *
     * @param value     The enum value.
     * @param separator A {@link NullableHolder} that contains a {@link SeparatorWidget}.
     */
    private void fromEnum(Enum<?> value, NullableHolder<SeparatorWidget> separator)
    {
        Component title = ClassUtil.cast(value, EnumTweak.class)
            .map(EnumTweak::getTitle)
            .orElse(Lang.literal(value.name()))
            .get();

        ButtonWidget checkbox = ButtonTemplate.checkbox(title, () -> this.tweak.fromCache().equals(value))
            .onPress(() -> this.tweak.setCacheValue(value))
            .iconTextPadding(this.padding)
            .build(this.overlay::addWidget);

        if (separator.isEmpty())
            checkbox.getBuilder().pos(0, 0);
        else
            checkbox.getBuilder().below(separator.get(), this.padding);

        TextWidget description = TextWidget.create(this.tweak.getEnumDescription(value))
            .posX(checkbox::getX)
            .below(checkbox, this.padding)
            .extendWidthToScreenEnd(this.padding)
            .build();

        if (this.tweak.isEnumDescribed(value))
            this.overlay.addWidget(description);

        separator.set(SeparatorWidget.create(Color.GRAY)
            .extendWidthToScreenEnd(this.padding)
            .build(this.overlay::addWidget));

        if (this.tweak.isEnumDescribed(value))
        {
            separator.map(SeparatorWidget::getBuilder)
                .ifPresent(builder -> builder.below(description, this.padding).posX(description::getX));
        }
        else
        {
            separator.map(SeparatorWidget::getBuilder)
                .ifPresent(builder -> builder.below(checkbox, this.padding).posX(checkbox::getX));
        }
    }

    /**
     * Creates and adds the widgets that will be assigned to the enumeration list overlay.
     */
    private void build()
    {
        NullableHolder<SeparatorWidget> holder = NullableHolder.empty();

        Stream.of(this.tweak.fromCache().getDeclaringClass().getEnumConstants())
            .forEach(value -> this.fromEnum(value, holder));

        this.overlay.removeWidget(holder.get());
    }
}
