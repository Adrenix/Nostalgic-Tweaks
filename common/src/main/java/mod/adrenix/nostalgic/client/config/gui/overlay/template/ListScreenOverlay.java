package mod.adrenix.nostalgic.client.config.gui.overlay.template;

import mod.adrenix.nostalgic.client.config.gui.overlay.OverlayFlag;
import mod.adrenix.nostalgic.client.config.gui.screen.list.ListScreen;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;

/**
 * Any overlays that extend this overlay template must be used while an {@link ListScreen} is active. This
 * template uses the generic overlay template.
 */

public abstract class ListScreenOverlay <W extends AbstractWidgetProvider> extends GenericOverlay
{
    /* Fields */

    protected W widgetProvider;
    protected ListScreen listScreen;
    protected int heightPadding = 46;

    /* Constructor */

    /**
     * Create a new list screen overlay window instance.
     * @param title The component title for this overlay.
     * @param width The width for this overlay.
     * @param height The height for this overlay.
     * @param flags Any overlay flags that need defined.
     */
    public ListScreenOverlay
    (
        Component title,
        int width,
        int height,
        OverlayFlag ...flags
    )
    {
        super(title, width, height, flags);
    }

    /* Abstract Methods */

    /**
     * This method, when called, defines the widget provider for the overlay.
     * Usually, happens during the initialization phase, but can be called sooner if needed.
     */
    protected abstract void createWidgetProvider();

    /* Utility Methods */

    /**
     * Change the height padding between the last widget and the overlay border.
     * @param padding The amount of padding to have.
     */
    public void setHeightPadding(int padding) { this.heightPadding = padding; }

    /* Override Methods */

    /**
     * Initialize the list screen overlay window.
     */
    @Override
    public void init()
    {
        Minecraft minecraft = Minecraft.getInstance();
        ListScreen screen = (ListScreen) minecraft.screen;

        if (screen == null || ClassUtil.isNotInstanceOf(screen, ListScreen.class))
            return;

        // Setup generic overlay x, y position initialization
        super.init();

        // Define list screen overlay fields
        this.listScreen = screen;

        // Create and generate overlay widgets
        this.createWidgetProvider();
        this.generateWidgets();

        // Update overlay width based on title and widgets
        this.width = Math.max(this.width, Minecraft.getInstance().font.width(this.title) + 44);

        for (Renderable child : this.widgets)
        {
            if (child instanceof AbstractWidget widget)
                this.width = Math.max(this.width, widget.getWidth() + 2);
        }

        // Update x, y position since overlay width might have changed
        super.init();

        // Redefine widgets since overlay width might have changed
        this.generateWidgets();

        // Update overlay height based on widgets
        int combinedHeight = 0;

        for (Renderable child : this.widgets)
        {
            if (child instanceof AbstractWidget widget)
                combinedHeight += widget.getHeight();
        }

        this.height = combinedHeight + this.heightPadding;

        // Update x, y position since overlay height might have changed
        super.init();

        // Redefine widgets since overlay height might have changed
        this.generateWidgets();
    }

    /**
     * Defines the widgets that are used by this overlay.
     * Any existing widgets are cleared when this is invoked.
     */
    @Override
    public void generateWidgets()
    {
        this.widgets.clear();
        this.widgetProvider.generate();
    }

    /**
     * Handler method for main overlay rendering.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    @Override
    public void onMainRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        this.widgetProvider.render(graphics, mouseX, mouseY, partialTick);
    }
}
