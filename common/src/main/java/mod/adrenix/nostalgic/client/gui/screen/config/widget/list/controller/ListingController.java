package mod.adrenix.nostalgic.client.gui.screen.config.widget.list.controller;

import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.ListingOverlay;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.tweak.factory.TweakListing;
import mod.adrenix.nostalgic.tweak.listing.Listing;
import mod.adrenix.nostalgic.util.common.lang.Lang;

import java.util.function.Function;

public class ListingController
{
    /* Fields */

    private final ButtonWidget widget;

    /* Constructor */

    /**
     * Create a new listing tweak controller instance.
     *
     * @param controller The originating controller.
     * @param tweak      The listing tweak this controller manages.
     * @param overlay    A {@link Function} that accepts the tweak and provides a new {@link ListingOverlay}.
     */
    public <V, L extends Listing<V, L>> ListingController(Controller controller, TweakListing<V, L> tweak, Function<TweakListing<V, L>, ListingOverlay<V, L>> overlay)
    {
        this.widget = ButtonWidget.create(Lang.Button.EDIT_LIST)
            .onPress(() -> overlay.apply(tweak).open())
            .disableIf(tweak::isNetworkLocked)
            .leftOf(controller.getLeftOf(), 1)
            .width(Controller.BUTTON_WIDTH)
            .build();
    }

    /* Methods */

    /**
     * @return Create a new button wrapper instance that will manage a listing tweak.
     */
    public ButtonWidget getWidget()
    {
        return this.widget;
    }
}
