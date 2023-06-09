package mod.adrenix.nostalgic.client.config.gui.widget.button;

import mod.adrenix.nostalgic.client.config.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.config.gui.screen.list.ListScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.input.ColorInput;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The reset button is the last button to the right of all tweak cache rows within a config row list row.
 * Depending on the type of tweak that is cached, different resetting logic is required.
 *
 * When used for custom list item entries, the reset button can turn into an undo button.
 */

public class ResetButton extends Button
{
    /* Fields */

    private static final int START_X = 0;
    private static final int START_Y = 0;
    private static final Component RESET_TITLE = Component.translatable(LangUtil.Gui.BUTTON_RESET);
    private static final Component UNDO_TITLE = Component.translatable(LangUtil.Gui.BUTTON_UNDO);
    private final Supplier<Boolean> isEntryChanged;
    private final AbstractWidget controller;

    private final TweakClientCache<?> tweak;

    /* Constructor Helpers */

    /**
     * Changes the width of the reset button based on the translation font width of this button title.
     * @return A reset button width.
     */
    private static int getResetWidth()
    {
        Font font = Minecraft.getInstance().font;
        int resetWidth = font.width(ResetButton.RESET_TITLE.getString());
        int undoWidth = font.width(ResetButton.UNDO_TITLE.getString());

        return Math.max(resetWidth, undoWidth) + 8;
    }

    /**
     * Get the title of the reset button based on whether the current list entry is changed.
     * @param isChanged A supplier that determines whether the entry has changed.
     * @return A component title for the reset button.
     */
    private static Component getResetTitle(Supplier<Boolean> isChanged)
    {
        return isChanged.get() ? ResetButton.UNDO_TITLE : ResetButton.RESET_TITLE;
    }

    /**
     * Resets the tweak client cache based on the neighboring widget controller.
     * @param cache A nullable tweak client cache instance.
     * @param controller A neighboring widget controller.
     */
    private static void resetTweak(@Nullable TweakClientCache<?> cache, AbstractWidget controller)
    {
        if (cache != null)
        {
            cache.reset();

            if (controller instanceof EditBox input && cache.getValue() instanceof String value)
                input.setValue(value);
            else if (controller instanceof ColorInput color && cache.getValue() instanceof String value)
                ((EditBox) color.getWidget()).setValue(value);
        }
        else if (controller instanceof KeyBindButton key)
            key.reset();
    }

    /* Constructors */

    /**
     * Create a new reset button instance.
     * @param tweak A nullable tweak client cache instance.
     * @param controller The controller widget used in config row list row this reset button is attached to.
     */
    public ResetButton(@Nullable TweakClientCache<?> tweak, AbstractWidget controller)
    {
        super
        (
            ResetButton.START_X,
            ResetButton.START_Y,
            ResetButton.getResetWidth(),
            ConfigRowList.BUTTON_HEIGHT,
            ResetButton.RESET_TITLE,
            (button) -> ResetButton.resetTweak(tweak, controller),
            DEFAULT_NARRATION
        );

        this.tweak = tweak;
        this.controller = controller;
        this.isEntryChanged = () -> false;

        this.updateX();
    }

    /**
     * Create a new reset button instance for a list entry.
     * @param controller The controller widget for this entry.
     * @param isChanged A supplier that checks if the entry has changed.
     * @param onReset A consumer that accepts the abstract widget controller and performs reset instructions.
     */
    public ResetButton(AbstractWidget controller, Supplier<Boolean> isChanged, Consumer<AbstractWidget> onReset)
    {
        super
        (
            ResetButton.START_X,
            ResetButton.START_Y,
            ResetButton.getResetWidth(),
            ConfigRowList.BUTTON_HEIGHT,
            ResetButton.getResetTitle(isChanged),
            (button) -> onReset.accept(controller),
            DEFAULT_NARRATION
        );

        this.tweak = null;
        this.controller = controller;
        this.isEntryChanged = isChanged;

        this.updateX();
    }

    /* Methods */

    /**
     * @return Get this button's widget controller. This is the reset button's neighbor to the left.
     */
    public AbstractWidget getController() { return this.controller; }

    /**
     * Update the starting x-position based on controller position and standard row widget gap.
     */
    private void updateX()
    {
        this.setX(this.controller.getX() + this.controller.getWidth() + ConfigRowList.ROW_WIDGET_GAP);
    }

    /**
     * Handler method for reset button rendering.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        this.setMessage(ResetButton.getResetTitle(this.isEntryChanged));
        this.updateX();

        if (this.tweak != null)
            this.active = this.tweak.isResettable();
        else if (this.controller instanceof KeyBindButton key)
            this.active = key.isResettable();

        boolean isAutoGenerated = this.tweak != null && this.tweak.getList() != null;
        boolean isNotListScreen = ClassUtil.isNotInstanceOf(Minecraft.getInstance().screen, ListScreen.class);
        boolean isDisabled = isAutoGenerated && isNotListScreen;

        if (Overlay.isOpened() || isDisabled)
            this.active = false;

        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
