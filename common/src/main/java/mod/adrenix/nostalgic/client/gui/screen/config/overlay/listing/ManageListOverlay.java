package mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.overlay.types.state.SwitchGroup;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;

public class ManageListOverlay
{
    /* Fields */

    private final ListingOverlay<?, ?> listingOverlay;
    private final Overlay overlay;

    /* Constructor */

    public ManageListOverlay(ListingOverlay<?, ?> listingOverlay)
    {
        int padding = 2;

        this.listingOverlay = listingOverlay;
        this.overlay = Overlay.create(Lang.Button.MANAGE)
            .icon(Icons.SMALL_TOOLS)
            .resizeUsingPercentage(0.5D)
            .resizeHeightForWidgets()
            .padding(padding)
            .build();

        Translation disabledHead = Lang.Listing.DISABLED_TITLE;
        Translation disabledInfo = Lang.Listing.DISABLED_MESSAGE;

        SwitchGroup.Widgets disabledSwitch = SwitchGroup.create(this.overlay, disabledHead, disabledInfo, this::isDisabled, this::setDisabled)
            .getWidgets();

        disabledSwitch.header().getBuilder().extendWidthToScreenEnd(0);
        disabledSwitch.description().getBuilder().extendWidthToScreenEnd(0);
        disabledSwitch.subscribeTo(this.overlay);

        SeparatorWidget separator = SeparatorWidget.create(Color.SILVER_CHALICE)
            .height(1)
            .below(disabledSwitch.description(), padding)
            .extendWidthToScreenEnd(0)
            .build(this.overlay::addWidget);

        ButtonWidget.create(Lang.Vanilla.GUI_DONE)
            .icon(Icons.GREEN_CHECK)
            .below(separator, padding)
            .widthOfScreen(0.6F)
            .centerInScreenX()
            .onPress(this.overlay::close)
            .build(this.overlay::addWidget);
    }

    /* Methods */

    /**
     * Open overlay that manages this listing.
     */
    public void open()
    {
        this.overlay.open();
    }

    /**
     * @return Whether the listing is currently disabled.
     */
    private boolean isDisabled()
    {
        return this.listingOverlay.getListing().isDisabled();
    }

    /**
     * Change the listing's disabled state and save that change to disk.
     *
     * @param state The new disabled state.
     */
    private void setDisabled(boolean state)
    {
        this.listingOverlay.getListing().setDisabled(state);
    }
}
