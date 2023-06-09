package mod.adrenix.nostalgic.client.config.gui.widget.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.gui.overlay.ManageItemOverlay;
import mod.adrenix.nostalgic.client.config.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigWidgets;
import mod.adrenix.nostalgic.client.config.gui.screen.list.ListScreen;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.tweak.GuiTweak;
import mod.adrenix.nostalgic.util.client.ItemClientUtil;
import mod.adrenix.nostalgic.util.client.RenderUtil;
import mod.adrenix.nostalgic.util.common.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

/**
 * This widget is used by config list screens. When the mouse hovers an item button, animations will take place as well
 * as a tooltip with item information and a notice that clicking the item will open a new overlay window with further
 * options.
 */

public class ItemButton extends Button
{
    /* Widget Constants */

    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;

    /* Fields */

    public ListScreen screen;
    public ItemStack itemStack;

    /**
     * This field tracks whether this button is being by an overlay.
     * Different rendering logic is needed when this button is used by the item manager overlay.
     */
    private boolean isForOverlay;

    /* Constructor */

    /**
     * Create a new hover item button instance.
     * Since this button is used exclusively by row lists, a starting y-position is omitted from the constructor.
     *
     * @param screen An abstract list screen instance.
     * @param itemStack The item stack that is being used by this button.
     * @param startX The starting x-position of this button.
     */
    public ItemButton(ListScreen screen, ItemStack itemStack, int startX)
    {
        super(startX, 0, WIDTH, HEIGHT, Component.empty(), (ignored) -> {}, DEFAULT_NARRATION);

        this.screen = screen;
        this.itemStack = itemStack;
        this.isForOverlay = false;
    }

    /**
     * Shortcut for setting the focused flag to <code>true</code>. The invoker instance is returned.
     * @return The item button instance that invoked this method.
     */
    public ItemButton forOverlay()
    {
        this.isForOverlay = true;
        return this;
    }

    /* Methods */

    /**
     * Handler method for when the mouse clicks on an item button.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     */
    @Override
    public void onClick(double mouseX, double mouseY)
    {
        if (!this.isForOverlay)
            new ManageItemOverlay(this.itemStack);
    }

    /**
     * Handler method for when the mouse clicks n an item button widget.
     * This will return false to prevent a clicking sound from playing when this widget is for an item manager overlay.
     *
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @param button The mouse button that was clicked.
     * @return Whether this method handled the mouse click event.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (this.isForOverlay)
            return false;

        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * Handler method for rendering a tooltip for this item button.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     */
    public void renderToolTip(GuiGraphics graphics, int mouseX, int mouseY)
    {
        graphics.renderTooltip(Minecraft.getInstance().font, this.itemStack, mouseX, mouseY);
    }

    /**
     * Handler method for rendering an item button.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        // Overlay activation

        this.active = !Overlay.isOpened();

        // If the manage item overlay is open and uses this button, then the y position needs aligned to the overlay.

        if (Overlay.getVisible() instanceof ManageItemOverlay itemOverlay && this.isForOverlay)
        {
            int dy = ItemClientUtil.isModelFlat(this.itemStack) ? 2 : 1;

            this.setY(itemOverlay.getOverlayStartY() + dy);
            this.active = true;
            this.visible = true;
        }

        // Item button rendering

        PoseStack viewStack = RenderSystem.getModelViewStack();
        boolean isMouseOver = this.isMouseOver(mouseX, mouseY) && ConfigWidgets.isInsideRowList(mouseY);

        int startX = this.getX() + 2;
        int startY = this.getY() + 1;
        int color = ColorUtil.toHexInt((String) TweakClientCache.get(GuiTweak.ROW_HIGHLIGHT_COLOR).getValue());

        if (this.itemStack.getItem() instanceof BlockItem)
            startY = this.getY() + 2;

        viewStack.pushPose();

        if (this.isForOverlay)
            viewStack.translate(0.0F, 0.0F, 100.0F);

        if (isMouseOver && !this.isForOverlay)
        {
            viewStack.translate(0.0F, -0.6F, 0.0F);
            RenderUtil.fill(graphics, this.getX(), this.getX() + this.width, this.getY(), this.getY() + this.height, color);
        }

        RenderSystem.applyModelViewMatrix();

        graphics.renderItem(this.itemStack, startX, startY);

        viewStack.popPose();
        RenderSystem.applyModelViewMatrix();

        if (isMouseOver)
        {
            if (!this.isForOverlay)
                this.screen.renderLast.add(() -> this.renderToolTip(graphics, mouseX, mouseY));
            else
                this.screen.renderOverlayTooltips.add(this::renderToolTip);
        }
    }
}