package mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.overlay.types.state.SwitchGroup;
import mod.adrenix.nostalgic.tweak.listing.ItemListing;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.world.ItemCommonUtil;
import net.minecraft.network.chat.Component;

public class ManageItemOverlay
{
    /* Fields */

    private final ItemListing<?, ?> listing;
    private final String resourceKey;
    private final Overlay overlay;

    /* Constructor */

    public ManageItemOverlay(ItemListing<?, ?> listing, String resourceKey)
    {
        int padding = 2;

        this.listing = listing;
        this.resourceKey = resourceKey;

        this.overlay = Overlay.create(Lang.Button.MANAGE)
            .icon(Icons.SMALL_TOOLS)
            .resizeUsingPercentage(0.5D)
            .resizeHeightForWidgets()
            .padding(padding)
            .build();

        Component wildcardHead = Lang.Listing.WILDCARD_TITLE.get();
        Component wildcardInfo = Lang.Listing.WILDCARD_MESSAGE.get(ItemCommonUtil.getLocalizedItem(resourceKey));

        SwitchGroup.Widgets wildcardSwitch = SwitchGroup.create(this.overlay, wildcardHead, wildcardInfo, this::isWildcard, this::setWildcard)
            .getWidgets();

        wildcardSwitch.header().getBuilder().extendWidthToScreenEnd(0);
        wildcardSwitch.description().getBuilder().extendWidthToScreenEnd(0);
        wildcardSwitch.subscribeTo(this.overlay);
    }

    /* Methods */

    /**
     * Open overlay to manage an item resource key.
     */
    public void open()
    {
        this.overlay.open();
    }

    /**
     * @return Whether the resource key is a wildcard.
     */
    private boolean isWildcard()
    {
        return this.listing.containsWildcard(this.resourceKey);
    }

    /**
     * Change whether the resource key is a wildcard.
     *
     * @param state The new wildcard state.
     */
    private void setWildcard(boolean state)
    {
        if (state)
            this.listing.addWildcard(this.resourceKey);
        else
            this.listing.removeWildcard(this.resourceKey);
    }
}
