package mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.impl;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.grid.Grid;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.client.Minecraft;

public class ItemInfoOverlay
{
    /* Fields */

    private final Overlay overlay;

    /* Constructor */

    public ItemInfoOverlay(String resourceId)
    {
        int padding = 2;

        this.overlay = Overlay.create(Lang.Listing.ITEM_INFO)
            .icon(Icons.SMALL_INFO)
            .resizeWidthUsingPercentage(0.7D, 250)
            .resizeHeightForWidgets()
            .padding(padding)
            .build();

        TextWidget info = TextWidget.create("Resource Identifier:\n\n- " + resourceId)
            .extendWidthToScreenEnd(0)
            .build(this.overlay::addWidget);

        ButtonWidget copy = ButtonWidget.create(Lang.Button.COPY)
            .icon(Icons.COPY)
            .skipFocusOnClick()
            .onPress(() -> Minecraft.getInstance().keyboardHandler.setClipboard(resourceId))
            .build(this.overlay::addWidget);

        ButtonWidget done = ButtonWidget.create(Lang.Vanilla.GUI_DONE)
            .icon(Icons.GREEN_CHECK)
            .onPress(this.overlay::close)
            .build(this.overlay::addWidget);

        SeparatorWidget separator = SeparatorWidget.create(Color.WHITE)
            .height(1)
            .below(info, padding)
            .extendWidthToScreenEnd(0)
            .build(this.overlay::addWidget);

        Grid.create(this.overlay, 2)
            .extendWidthToScreenEnd(0)
            .below(separator, padding)
            .columnSpacing(padding)
            .addCells(copy, done)
            .build(this.overlay::addWidget);
    }

    /* Methods */

    /**
     * Open a new item info overlay.
     */
    public void open()
    {
        this.overlay.open();
    }
}
