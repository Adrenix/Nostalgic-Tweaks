package mod.adrenix.nostalgic.client.config.gui.overlay;

import mod.adrenix.nostalgic.client.config.gui.overlay.template.AbstractWidgetProvider;
import mod.adrenix.nostalgic.client.config.gui.overlay.template.ListScreenOverlay;
import mod.adrenix.nostalgic.client.config.gui.screen.SettingsScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.text.TextAlign;
import mod.adrenix.nostalgic.client.config.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.Set;

/**
 * This class provides options to the user that allows them to enable or disable all default entries associated with a
 * list screen. When a default entry is considered "disabled" its value will be ignored when queried by an item entry
 * retrieval function. If there is an item that is both a default entry category and a saved entry category, then the
 * value kept in the saved entry category will be used.
 */

public class DefaultEntriesOverlay extends ListScreenOverlay<DefaultEntriesOverlay.WidgetProvider>
{
    /* Static Fields */

    public static final int OVERLAY_WIDTH = 262;
    public static final int OVERLAY_HEIGHT = 138;

    /* Constructor & Initialize */

    public DefaultEntriesOverlay()
    {
        super(Component.translatable(LangUtil.Gui.OVERLAY_DEFAULTS), OVERLAY_WIDTH, OVERLAY_HEIGHT);

        this.setBackground(0xFF303030);
        this.init();
    }

    /* List Overlay Overrides */

    @Override
    protected void createWidgetProvider() { this.widgetProvider = new WidgetProvider(); }

    /* Widget Provider */

    /**
     * This class is responsible for creating and adding widgets to the overlay.
     * There will be text explaining what the user is about to manage and will be followed by two action buttons.
     */
    protected class WidgetProvider extends AbstractWidgetProvider
    {
        /* Widgets */

        public TextWidget headerText;
        public TextWidget messageText;
        public Button enableAll;
        public Button disableAll;

        /* Methods */

        /**
         * Create and add widgets to the manage defaults overlay window.
         */
        @Override
        public void generate()
        {
            this.headerText = this.createHeaderText();
            this.messageText = this.createMessageText();
            this.enableAll = this.createEnableAll();
            this.disableAll = this.createDisableAll();

            DefaultEntriesOverlay.this.widgets.add(this.headerText);
            DefaultEntriesOverlay.this.widgets.add(this.messageText);
            DefaultEntriesOverlay.this.widgets.add(this.enableAll);
            DefaultEntriesOverlay.this.widgets.add(this.disableAll);

            this.children = Set.of(headerText, messageText, enableAll, disableAll);
        }

        /**
         * @return The starting x-position for widgets in this overlay.
         */
        private int getStartX() { return DefaultEntriesOverlay.this.getOverlayStartX() + 2; }

        /**
         * @return The starting y-position for widgets in this overlay.
         */
        private int getStartY() { return DefaultEntriesOverlay.this.getOverlayStartY() + 4; }

        /**
         * @return The width for buttons.
         */
        private int getButtonWidth() { return DefaultEntriesOverlay.this.getDrawWidth() - 80; }

        /**
         * @return The starting x-position for buttons in this overlay.
         */
        private int getButtonStartX()
        {
            int startX = DefaultEntriesOverlay.this.getOverlayStartX();
            int width = DefaultEntriesOverlay.this.getDrawWidth();

            return startX + (width / 2) - (this.getButtonWidth() / 2) + 5;
        }

        /**
         * Create the message header text for this overlay.
         * @return A text widget instance.
         */
        private TextWidget createHeaderText()
        {
            String translation = Component.translatable(LangUtil.Gui.OVERLAY_DEFAULTS_HEADER).getString();
            Component header = Component.literal(ChatFormatting.UNDERLINE + translation);

            return new TextWidget
            (
                header,
                TextAlign.CENTER,
                this.getStartX() + 2,
                this.getStartY(),
                DefaultEntriesOverlay.this.getDrawWidth()
            );
        }

        /**
         * Create the message text for this overlay.
         * @return A text widget instance.
         */
        private TextWidget createMessageText()
        {
            return new TextWidget
            (
                Component.translatable(LangUtil.Gui.OVERLAY_DEFAULTS_MESSAGE),
                TextAlign.LEFT,
                this.getStartX(),
                this.headerText.getBottomY(),
                DefaultEntriesOverlay.this.getDrawWidth()
            );
        }

        /**
         * Create a button that, when clicked, closes the overlay and enables all default entries.
         * @return A button widget instance.
         */
        private Button createEnableAll()
        {
            return new Button
            (
                this.getButtonStartX(),
                this.messageText.getBottomY() + 24,
                this.getButtonWidth(),
                SettingsScreen.BUTTON_HEIGHT,
                Component.translatable(LangUtil.Gui.BUTTON_ENABLE_ALL),
                (button) ->
                {
                    Overlay.close();
                    DefaultEntriesOverlay.this.listScreen.enableAllDefaults();
                }
            );
        }

        /**
         * Create a button that, when clicked, closes the overlay and disables all default entries.
         * @return A button widget instance.
         */
        private Button createDisableAll()
        {
            return new Button
            (
                this.getButtonStartX(),
                this.messageText.getBottomY() + 2,
                this.getButtonWidth(),
                SettingsScreen.BUTTON_HEIGHT,
                Component.translatable(LangUtil.Gui.BUTTON_DISABLE_ALL),
                (button) ->
                {
                    Overlay.close();
                    DefaultEntriesOverlay.this.listScreen.disableAllDefaults();
                }
            );
        }
    }
}
