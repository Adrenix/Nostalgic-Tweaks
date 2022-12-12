package mod.adrenix.nostalgic.client.config.gui.overlay;

import mod.adrenix.nostalgic.client.config.gui.overlay.template.AbstractWidgetProvider;
import mod.adrenix.nostalgic.client.config.gui.overlay.template.ListScreenOverlay;
import mod.adrenix.nostalgic.client.config.gui.screen.SettingsScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.text.TextAlign;
import mod.adrenix.nostalgic.client.config.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.util.common.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.Set;

/**
 * This class provides confirmation options to the user when the "Nuke" button is pressed on an abstract list screen.
 * Once confirmed, the entire saved entries list will be cleared. This action can be undone if the user wishes.
 */

public class NukeListOverlay extends ListScreenOverlay<NukeListOverlay.WidgetProvider>
{
    /* Static Fields */

    public static final int OVERLAY_WIDTH = 176;
    public static final int OVERLAY_HEIGHT = 138;

    /* Constructor & Initialize */

    /**
     * Start a new nuke list overlay window instance.
     */
    public NukeListOverlay()
    {
        super(Component.translatable(LangUtil.Gui.OVERLAY_NUKE), OVERLAY_WIDTH, OVERLAY_HEIGHT);

        this.setBackground(0xFF303030);
        this.init();
    }

    /* List Overlay Overrides */

    @Override
    protected void createWidgetProvider() { this.widgetProvider = new WidgetProvider(); }

    /* Widget Provider */

    /**
     * This class is responsible for creating and adding widgets to the overlay.
     * There will be text explaining what the user is about to do and will be followed by two action buttons.
     */
    protected class WidgetProvider extends AbstractWidgetProvider
    {
        /* Widgets */

        public TextWidget headerText;
        public TextWidget messageText;
        public Button confirmYes;
        public Button confirmNo;

        /* Methods */

        /**
         * Create and add widgets to the nuke confirmation overlay window.
         */
        public void generate()
        {
            this.headerText = this.createHeaderText();
            this.messageText = this.createMessageText();
            this.confirmYes = this.createConfirmYes();
            this.confirmNo = this.createConfirmNo();

            NukeListOverlay.this.widgets.add(this.headerText);
            NukeListOverlay.this.widgets.add(this.messageText);
            NukeListOverlay.this.widgets.add(this.confirmYes);
            NukeListOverlay.this.widgets.add(this.confirmNo);

            this.children = Set.of(headerText, messageText, confirmYes, confirmNo);
        }

        /**
         * @return The starting x-position for widgets in this overlay.
         */
        private int getStartX() { return NukeListOverlay.this.getOverlayStartX() + 2; }

        /**
         * @return The starting y-position for widgets in this overlay.
         */
        private int getStartY() { return NukeListOverlay.this.getOverlayStartY() + 4; }

        /**
         * @return The width for confirmation buttons.
         */
        private int getButtonWidth() { return NukeListOverlay.this.getDrawWidth() - 20; }

        /**
         * @return The starting x-position for buttons in this overlay.
         */
        private int getButtonStartX()
        {
            int startX = NukeListOverlay.this.getOverlayStartX();
            int width = NukeListOverlay.this.getDrawWidth();

            return startX + (width / 2) - (this.getButtonWidth() / 2) + 5;
        }

        /**
         * Create the warning header text for the nuke overlay window.
         * @return A text widget instance.
         */
        private TextWidget createHeaderText()
        {
            String header = Component.translatable(LangUtil.Gui.OVERLAY_NUKE_HEADER).getString();

            return new TextWidget
            (
                Component.literal(ChatFormatting.DARK_RED + header),
                TextAlign.CENTER,
                this.getStartX() + 2,
                this.getStartY(),
                NukeListOverlay.this.getDrawWidth()
            );
        }

        /**
         * Create the warning message text for the nuke overlay window.
         * @return A text widget instance.
         */
        private TextWidget createMessageText()
        {
            return new TextWidget
            (
                Component.translatable(LangUtil.Gui.OVERLAY_NUKE_MESSAGE),
                TextAlign.LEFT,
                this.getStartX(),
                this.headerText.getBottomY(),
                NukeListOverlay.this.getDrawWidth()
            );
        }

        /**
         * Create a button that, when clicked, closes the overlay and clears the saved entries list.
         * @return A button widget instance.
         */
        private Button createConfirmYes()
        {
            return new Button
            (
                this.getButtonStartX(),
                this.messageText.getBottomY() + 2,
                this.getButtonWidth(),
                SettingsScreen.BUTTON_HEIGHT,
                Component.translatable(LangUtil.Gui.BUTTON_YES),
                (button) ->
                {
                    Overlay.close();
                    NukeListOverlay.this.listScreen.clearAllSaved();
                }
            );
        }

        /**
         * Create a button that, when clicked, closes the overlay and does nothing else.
         * @return A button widget instance.
         */
        private Button createConfirmNo()
        {
            return new Button
            (
                this.getButtonStartX(),
                this.messageText.getBottomY() + 24,
                this.getButtonWidth(),
                SettingsScreen.BUTTON_HEIGHT,
                Component.translatable(LangUtil.Gui.BUTTON_NO),
                (button) -> Overlay.close()
            );
        }
    }
}
