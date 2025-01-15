package mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.overlay.types.info.MessageOverlay;
import mod.adrenix.nostalgic.client.gui.overlay.types.info.MessageType;
import mod.adrenix.nostalgic.util.common.lang.Lang;

public class InvalidTypeOverlay
{
    /* Fields */

    private final Overlay overlay;

    /* Constructor */

    public InvalidTypeOverlay()
    {
        this.overlay = MessageOverlay.create(MessageType.ERROR, Lang.Listing.INVALID_TYPE, Lang.Listing.INVALID_MESSAGE)
            .build();
    }

    /* Methods */

    /**
     * Open the invalid value type overlay.
     */
    public void open()
    {
        this.overlay.open();
    }
}
