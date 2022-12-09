package mod.adrenix.nostalgic.client.config.gui.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.client.config.gui.overlay.template.AbstractWidgetProvider;
import mod.adrenix.nostalgic.client.config.gui.overlay.template.ListScreenOverlay;
import mod.adrenix.nostalgic.client.config.gui.screen.SettingsScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.text.TextAlign;
import mod.adrenix.nostalgic.client.config.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.common.config.auto.AutoConfig;
import mod.adrenix.nostalgic.util.common.*;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.Set;

/**
 * This class prevents users from configuring a list that requires operator permissions. This window will provide
 * information as to what happened and provide two exit options. The first option will save changes locally to the
 * client, and the second option will allow the user to exit without saving. If the user regains operator permissions
 * while this overlay is open, the text will update and the "X" button will be activated.
 */

public class LostPermissionOverlay extends ListScreenOverlay<LostPermissionOverlay.WidgetProvider>
{
    /* Static Fields */

    public static final int OVERLAY_WIDTH = 250;
    public static final int OVERLAY_HEIGHT = 98;

    /* Overlay Fields */

    private static final int TIME_OUT = 3;
    private int countdown = TIME_OUT;
    private long timeSinceLast = 0L;

    /* Constructor & Initialize */

    /**
     * Start a new lost permission overlay window instance.
     */
    public LostPermissionOverlay()
    {
        super
        (
            Component.translatable(LangUtil.Gui.OVERLAY_PERMISSION),
            OVERLAY_WIDTH,
            OVERLAY_HEIGHT,
            OverlayFlag.LOCKED
        );

        this.setHeightPadding(42);
        this.setBackground(0xFF303030);
        this.init();
    }

    /* List Overlay Overrides */

    @Override
    protected void createWidgetProvider() { this.widgetProvider = new WidgetProvider(); }

    /* Widget Provider */

    /**
     * This class is responsible for creating and adding widgets to the overlay.
     * There will be text explaining what happened and will be followed by two action buttons.
     */
    protected class WidgetProvider extends AbstractWidgetProvider
    {
        /* Widgets */

        public TextWidget headerText;
        public TextWidget messageText;
        public Button exitWithoutSaving;
        public Button exitSaveLocally;

        /* Methods */

        /**
         * Create and add widgets to the lost permissions overlay window.
         */
        public void generate()
        {
            this.headerText = this.createHeaderText();
            this.messageText = this.createMessageText();
            this.exitWithoutSaving = this.createWithoutSaving();
            this.exitSaveLocally = this.createSaveLocally();

            LostPermissionOverlay.this.widgets.add(this.headerText);
            LostPermissionOverlay.this.widgets.add(this.messageText);
            LostPermissionOverlay.this.widgets.add(this.exitWithoutSaving);
            LostPermissionOverlay.this.widgets.add(this.exitSaveLocally);

            this.children = Set.of(headerText, messageText, exitWithoutSaving, exitSaveLocally);
        }

        /**
         * @return The starting x-position for widgets in this overlay.
         */
        private int getStartX() { return LostPermissionOverlay.this.getOverlayStartX() + 2; }

        /**
         * @return The starting y-position for widgets in this overlay.
         */
        private int getStartY() { return LostPermissionOverlay.this.getOverlayStartY() + 4; }

        /**
         * @return The width for confirmation buttons.
         */
        private int getButtonWidth() { return LostPermissionOverlay.this.getDrawWidth() - 20; }

        /**
         * @return The starting x-position for buttons in this overlay.
         */
        private int getButtonStartX()
        {
            int windowWidth = LostPermissionOverlay.this.getDrawWidth() / 2;
            int buttonWidth = this.getButtonWidth() / 2;

            return LostPermissionOverlay.this.getOverlayStartX() + windowWidth - buttonWidth + 5;
        }

        /**
         * Create the lost permission header text for this overlay.
         * @return A text widget instance.
         */
        private TextWidget createHeaderText()
        {
            String header = Component.translatable(LangUtil.Gui.OVERLAY_PERMISSION_HEADER).getString();

            return new TextWidget
            (
                Component.literal(ChatFormatting.DARK_RED + header),
                TextAlign.CENTER,
                this.getStartX() + 2,
                this.getStartY(),
                LostPermissionOverlay.this.getDrawWidth()
            );
        }

        /**
         * Create the lost permission message text for this overlay.
         * @return A text widget instance.
         */
        private TextWidget createMessageText()
        {
            return new TextWidget
            (
                Component.translatable(LangUtil.Gui.OVERLAY_PERMISSION_MESSAGE),
                TextAlign.LEFT,
                this.getStartX(),
                this.headerText.getBottomY() + 2,
                LostPermissionOverlay.this.getDrawWidth()
            );
        }

        /**
         * Create button, that when clicked, closes the overlay and closes the list screen without saving.
         * @return A button widget instance.
         */
        private Button createWithoutSaving()
        {
            return new Button
            (
                this.getButtonStartX(),
                this.messageText.getBottomY(),
                this.getButtonWidth(),
                SettingsScreen.BUTTON_HEIGHT,
                Component.translatable(LangUtil.Gui.BUTTON_EXIT_NO_SAVE),
                (button) ->
                {
                    Overlay.close();
                    LostPermissionOverlay.this.listScreen.closeWithoutSaving();
                }
            );
        }

        /**
         * Create a button, that when clicked, closes the overlay and closes the list screen while saving changes
         * locally to the client config.
         *
         * @return A button widget instance.
         */
        private Button createSaveLocally()
        {
            return new Button
            (
                this.getButtonStartX(),
                this.messageText.getBottomY() + 22,
                this.getButtonWidth(),
                SettingsScreen.BUTTON_HEIGHT,
                Component.translatable(LangUtil.Gui.BUTTON_EXIT_LOCAL_SAVE),
                (button) ->
                {
                    Overlay.close();
                    AutoConfig.getConfigHolder(ClientConfig.class).save();
                }
            );
        }
    }

    /* Overlay Overrides */

    /**
     * Handler method for overlay main rendering.
     * @param poseStack The current pose stack.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick A change in game frame time.
     */
    @Override
    public void onMainRender(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        // Widget management
        if (this.timeSinceLast == 0L)
            this.timeSinceLast = Util.getMillis();

        if (Util.getMillis() - this.timeSinceLast >= 1000L)
        {
            this.timeSinceLast = Util.getMillis();
            this.countdown = Mth.clamp(--this.countdown, 0, TIME_OUT);
        }

        this.widgetProvider.exitWithoutSaving.active = this.countdown == 0;
        this.widgetProvider.exitSaveLocally.active = this.countdown == 0;

        super.onMainRender(poseStack, mouseX, mouseY, partialTick);
    }
}
