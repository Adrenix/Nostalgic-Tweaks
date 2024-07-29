package mod.adrenix.nostalgic.client.gui.screen.config.widget.list.controller;

import mod.adrenix.nostalgic.client.gui.widget.button.ButtonBuilder;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.tweak.factory.TweakFlag;
import mod.adrenix.nostalgic.util.common.data.NullableHolder;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class BooleanController
{
    /* Fields */

    private final NullableHolder<Controller> controller;
    private final TweakFlag tweak;

    /* Constructor */

    /**
     * Create a new boolean controller instance.
     *
     * @param controller The originating controller.
     * @param tweak      The boolean tweak this controller manages.
     */
    public BooleanController(Controller controller, TweakFlag tweak)
    {
        this.controller = NullableHolder.create(controller);
        this.tweak = tweak;
    }

    /**
     * Create a new boolean controller instance.
     *
     * @param tweak The boolean tweak this controller manages.
     */
    public BooleanController(TweakFlag tweak)
    {
        this.controller = NullableHolder.empty();
        this.tweak = tweak;
    }

    /* Methods */

    /**
     * @return Create a new button wrapper instance that will manage a boolean tweak.
     */
    public ButtonWidget getWidget()
    {
        return this.getBuilder().leftOf(this.controller.map(Controller::getLeftOf).orElse(null), 1).build();
    }

    /**
     * @return Get the {@link ButtonBuilder} for this boolean controller.
     */
    public ButtonBuilder getBuilder()
    {
        return ButtonWidget.create()
            .title(this::getTitle)
            .onPress(this::toggle)
            .disableIf(this.tweak::isNetworkLocked)
            .width(Controller.BUTTON_WIDTH);
    }

    /**
     * @return The dynamic title for this widget.
     */
    private Component getTitle()
    {
        MutableComponent text = this.tweak.fromCache() ? Lang.TweakRow.ENABLED.get() : Lang.TweakRow.DISABLED.get();
        MutableComponent left = Component.literal("[").withStyle(ChatFormatting.GRAY);
        MutableComponent right = Component.literal("] ").withStyle(ChatFormatting.GRAY);
        MutableComponent symbol = this.tweak.fromCache() ? Component.literal("✔")
            .withStyle(ChatFormatting.GREEN) : Component.literal("❌")
            .withStyle(ChatFormatting.RED, ChatFormatting.BOLD);

        return Component.empty()
            .copy()
            .append(left)
            .append(symbol)
            .append(right)
            .append(text.withStyle(ChatFormatting.RESET));
    }

    /**
     * Toggles the boolean cache for the tweak assigned to this controller.
     */
    private void toggle()
    {
        this.tweak.setCacheValue(!this.tweak.fromCache());
    }
}
