package mod.adrenix.nostalgic.client.config.gui.overlay;

import mod.adrenix.nostalgic.client.config.gui.overlay.template.AbstractWidgetProvider;
import mod.adrenix.nostalgic.client.config.gui.overlay.template.ListScreenOverlay;
import mod.adrenix.nostalgic.client.config.gui.widget.text.TextAlign;
import mod.adrenix.nostalgic.client.config.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.common.config.DefaultConfig;
import mod.adrenix.nostalgic.util.common.ComponentBackport;
import mod.adrenix.nostalgic.util.common.LangUtil;

import java.util.Set;

/**
 * This class provides information about swing speed values associated with different eras of the game. Information
 * about photosensitivity is also included. Additionally, this text can also be found in the swing tab introduction
 * paragraph.
 */

public class SpeedOverlay extends ListScreenOverlay<SpeedOverlay.WidgetProvider>
{
    /* Static Fields */

    public static final int OVERLAY_WIDTH = 140;
    public static final int OVERLAY_HEIGHT = 140;

    /* Constructor & Initialize */

    /**
     * Start a new speed overlay information window instance.
     */
    public SpeedOverlay()
    {
        super(ComponentBackport.translatable(LangUtil.Gui.OVERLAY_SWING), OVERLAY_WIDTH, OVERLAY_HEIGHT);

        this.setHeightPadding(34);
        this.setBackground(0xFF303030);
        this.init();
    }

    /* List Overlay Overrides */

    @Override
    protected void createWidgetProvider() { this.widgetProvider = new WidgetProvider(); }

    /* Widget Provider */

    protected class WidgetProvider extends AbstractWidgetProvider
    {
        /* Widgets */

        public TextWidget oldSpeedText;
        public TextWidget modernSpeedText;
        public TextWidget photosensitiveText;

        /* Methods */

        /**
         * Create and add widgets to the speed information overlay window.
         */
        @Override
        public void generate()
        {
            this.oldSpeedText = this.createOldSpeedText();
            this.modernSpeedText = this.createModernSpeedText();
            this.photosensitiveText = this.createPhotosensitiveText();

            SpeedOverlay.this.widgets.add(this.oldSpeedText);
            SpeedOverlay.this.widgets.add(this.modernSpeedText);
            SpeedOverlay.this.widgets.add(this.photosensitiveText);

            this.children = Set.of(oldSpeedText, modernSpeedText, photosensitiveText);
        }

        /**
         * @return The starting x-position for widgets in this overlay.
         */
        private int getStartX() { return SpeedOverlay.this.getOverlayStartX() + 2; }

        /**
         * @return The starting y-position for widgets in this overlay.
         */
        private int getStartY() { return SpeedOverlay.this.getOverlayStartY() + 4; }

        /**
         * Creates a text line with old swing speed information.
         * @return A text widget instance.
         */
        private TextWidget createOldSpeedText()
        {
            return new TextWidget
            (
                ComponentBackport.translatable(LangUtil.Gui.OVERLAY_SWING_OLD, DefaultConfig.Swing.OLD_SPEED),
                TextAlign.LEFT,
                this.getStartX(),
                this.getStartY(),
                SpeedOverlay.this.getDrawWidth()
            );
        }

        /**
         * Creates a text line with modern swing speed information.
         * @return A text widget instance.
         */
        private TextWidget createModernSpeedText()
        {
            return new TextWidget
            (
                ComponentBackport.translatable(LangUtil.Gui.OVERLAY_SWING_MODERN, DefaultConfig.Swing.NEW_SPEED),
                TextAlign.LEFT,
                this.getStartX(),
                this.oldSpeedText.getBottomY(),
                SpeedOverlay.this.getDrawWidth()
            );
        }

        /**
         * Creates a text line with photosensitive swing speed information.
         * @return A text widget instance.
         */
        private TextWidget createPhotosensitiveText()
        {
            return new TextWidget
            (
                ComponentBackport.translatable(LangUtil.Gui.OVERLAY_SWING_PHOTOSENSITIVE, DefaultConfig.Swing.PHOTOSENSITIVE),
                TextAlign.LEFT,
                this.getStartX(),
                this.modernSpeedText.getBottomY(),
                SpeedOverlay.this.getDrawWidth()
            );
        }
    }
}
