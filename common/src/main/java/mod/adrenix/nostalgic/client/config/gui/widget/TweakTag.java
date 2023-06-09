package mod.adrenix.nostalgic.client.config.gui.widget;

import mod.adrenix.nostalgic.client.config.annotation.TweakGui;
import mod.adrenix.nostalgic.client.config.annotation.TweakReload;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigWidgets;
import mod.adrenix.nostalgic.client.config.gui.widget.button.StatusButton;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.annotation.TweakData;
import mod.adrenix.nostalgic.common.config.tweak.GuiTweak;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.common.LangUtil;
import mod.adrenix.nostalgic.util.common.TextUtil;
import mod.adrenix.nostalgic.util.common.TextureLocation;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * There are multiple tags that can be displayed next to a tweak's display name in a configuration row.
 * A row can have a variety of different tags and will always be visible on the screen.
 */

public class TweakTag extends AbstractWidget
{
    /* Horizontal Coordinate Offsets */

    public static final int U_NEW_OFFSET = 66;
    public static final int U_CLIENT_OFFSET = 69;
    public static final int U_SERVER_OFFSET = 72;
    public static final int U_DYNAMIC_OFFSET = 81;
    public static final int U_RELOAD_OFFSET = 75;
    public static final int U_RESTART_OFFSET = 78;
    public static final int U_KEY_OFFSET = 81;
    public static final int U_SYNC_OFFSET = 84;
    public static final int U_WARNING_OFFSET = 87;
    public static final int V_GLOBAL_OFFSET = 0;
    public static final int U_GLOBAL_WIDTH = 1;
    public static final int V_GLOBAL_HEIGHT = 11;
    public static final int TAG_MARGIN = 5;

    /* Widget Fields */

    private String title;
    private boolean render = true;
    private boolean widthChanged = false;
    private final TweakClientCache<?> tweak;
    private final AbstractWidget controller;
    private final boolean isTooltip;

    /* Constructor */

    /**
     * Create a new tweak tag instance.
     * @param tweak A tweak from the client cache.
     * @param controller A controller widget associated with a config row list row.
     * @param isTooltip Whether this tag has a tooltip associated with it.
     */
    public TweakTag(TweakClientCache<?> tweak, AbstractWidget controller, boolean isTooltip)
    {
        super(0, 0, 0, 0, Component.empty());

        this.tweak = tweak;
        this.controller = controller;
        this.isTooltip = isTooltip;
        this.title = Component.translatable(this.tweak.getLangKey()).getString();
    }

    /* Helper Methods */

    /**
     * @return Get the title of this tag.
     */
    public String getTitle() { return this.title; }

    /**
     * Set the title of this tag.
     * @param title The new title of this tag.
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * Control whether this tag should render.
     * @param state A flag that dictates tag rendering.
     */
    public void setRender(boolean state) { this.render = state; }

    /**
     * @return Checks if the tag width has changed.
     */
    public boolean isWidthChanged() { return this.widthChanged; }

    /**
     * Resets the title back to the original translation and acknowledges the width change.
     */
    public void resetTag()
    {
        this.widthChanged = false;
        this.title = Component.translatable(this.tweak.getLangKey()).getString();
    }

    /* Rendering Static Helpers */

    /**
     * Get the width of a tag.
     * @param tag The title of a tag.
     * @param startX Where the title of this tag is starting.
     * @return The ending x-position that includes the starting x-position and the text-width of the tag title.
     */
    private static int getTagWidth(Component tag, int startX)
    {
        return startX + U_GLOBAL_WIDTH + Minecraft.getInstance().font.width(tag) + TAG_MARGIN;
    }

    /**
     * Draws a tag to the screen.
     * @param graphics The current GuiGraphics object.
     * @param location The ResourceLocation to the resource to render.
     * @param x The x-position of where the tag should be drawn.
     * @param y The y-position of where the tag should be drawn.
     * @param uOffset The horizontal texture coordinate offset.
     * @param vOffset The vertical texture coordinate offset.
     * @param render Whether the tag should be rendered.
     */
    private static void draw(GuiGraphics graphics, ResourceLocation location, int x, int y, int uOffset, int vOffset, boolean render)
    {
        if (render)
            graphics.blit(location, x, y, uOffset, vOffset, U_GLOBAL_WIDTH, V_GLOBAL_HEIGHT);
    }

    /**
     * Renders a complete tag to the screen.
     * @param graphics The current GuiGraphics object.
     * @param tag The tag to render.
     * @param startX The x-position of where the tag should be drawn.
     * @param startY The y-position of where the tag should be drawn.
     * @param uOffset The horizontal texture coordinate offset.
     * @param render Whether the tag should be rendered.
     * @return An x-position of where the next tag should start rendering. This includes the defined tag margin.
     */
    public static int renderTag(GuiGraphics graphics, Component tag, int startX, int startY, int uOffset, boolean render)
    {
        Font font = Minecraft.getInstance().font;

        int tagWidth = font.width(tag);
        int endX = getTagWidth(tag, startX);

        TweakTag.draw(graphics, TextureLocation.WIDGETS, startX, startY, uOffset, V_GLOBAL_OFFSET, render);

        for (int i = 0; i < tagWidth + TAG_MARGIN; i++)
            TweakTag.draw(graphics, TextureLocation.WIDGETS, startX + U_GLOBAL_WIDTH + i, startY, uOffset + 1, 0, render);

        TweakTag.draw(graphics, TextureLocation.WIDGETS, endX, startY, uOffset, V_GLOBAL_OFFSET, render);

        if (render)
            graphics.drawString(font, tag, startX + 4, startY + 2, 0xFFFFFF);

        return endX + TAG_MARGIN;
    }

    /**
     * An override method of {@link TweakTag#renderTag(GuiGraphics, Component, int, int, int, boolean)} that does
     * not require a rendering state.
     *
     * @param graphics The current GuiGraphics object.
     * @param tag The tag to render.
     * @param startX The x-position of where the tag should be drawn.
     * @param startY The y-position of where the tag should be drawn.
     * @param uOffset The horizontal texture coordinate offset.
     * @return An x-position of where the next tag should start rendering. This includes the defined tag margin.
     */
    public static int renderTag(GuiGraphics graphics, Component tag, int startX, int startY, int uOffset)
    {
        return renderTag(graphics, tag, startX, startY, uOffset, true);
    }

    /**
     * Render a tooltip on the screen if the mouse is over a specific position.
     * @param screen The current screen.
     * @param graphics The current GuiGraphics object.
     * @param title The title component of a tag.
     * @param tooltip The tooltip component to display.
     * @param startX Where the tooltip box starts rendering on the x-axis.
     * @param startY Where the tooltip box starts rendering on the y-axis.
     * @param mouseX Where the mouse currently sits on the x-axis.
     * @param mouseY Where the mouse currently sits on the y-axis.
     */
    public static void renderTooltip(Screen screen, GuiGraphics graphics, Component title, Component tooltip, int startX, int startY, int mouseX, int mouseY)
    {
        int endX = getTagWidth(title, startX);
        boolean isMouseOver = (mouseX >= startX && mouseX <= endX) && (mouseY >= startY && mouseY <= startY + V_GLOBAL_HEIGHT);

        // Prevents tooltip rendering when the row list has not assigned a y-position yet
        boolean isInitialized = startY != 4;
        boolean isWithinList = ConfigWidgets.isInsideRowList(mouseY);

        if (isMouseOver && isWithinList && isInitialized && screen instanceof ConfigScreen configScreen)
            configScreen.renderLast.add(() ->
                graphics.renderComponentTooltip(Minecraft.getInstance().font, TextUtil.Wrap.tooltip(tooltip, 38), mouseX, mouseY));
    }

    /**
     * An override method that instructs the screen renderer what to show.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @param partialTick The change in frame time.
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;

        if (screen == null)
            return;

        StatusButton.update();

        TweakGui.New newTag = this.tweak.getMetadata(TweakGui.New.class);
        TweakData.Client clientTag = this.tweak.getMetadata(TweakData.Client.class);
        TweakData.Server serverTag = this.tweak.getMetadata(TweakData.Server.class);
        TweakData.Dynamic dynamicTag = this.tweak.getMetadata(TweakData.Dynamic.class);
        TweakData.Conflict conflictTag = this.tweak.getMetadata(TweakData.Conflict.class);
        TweakGui.Alert alertTag = this.tweak.getMetadata(TweakGui.Alert.class);
        TweakGui.Sodium sodiumTag = this.tweak.getMetadata(TweakGui.Sodium.class);
        TweakGui.Restart restartTag = this.tweak.getMetadata(TweakGui.Restart.class);
        TweakGui.Warning warningTag = this.tweak.getMetadata(TweakGui.Warning.class);
        TweakGui.Optifine optifineTag = this.tweak.getMetadata(TweakGui.Optifine.class);
        TweakReload.Resources reloadTag = this.tweak.getMetadata(TweakReload.Resources.class);

        Component optifineTitle = Component.literal("Optifine");
        Component sodiumTitle = Component.literal("Sodium");

        ChatFormatting flashColor = StatusButton.isFlashOff() ? ChatFormatting.GRAY : ChatFormatting.RED;

        Component title = Component.literal(this.title);
        Component newTitle = Component.translatable(LangUtil.Gui.TAG_NEW);
        Component clientTitle = Component.translatable(LangUtil.Gui.TAG_CLIENT);
        Component serverTitle = Component.translatable(LangUtil.Gui.TAG_SERVER);
        Component dynamicTitle = Component.translatable(LangUtil.Gui.TAG_DYNAMIC);
        Component reloadTitle = Component.translatable(LangUtil.Gui.TAG_RELOAD).withStyle(ChatFormatting.ITALIC);
        Component restartTitle = Component.translatable(LangUtil.Gui.TAG_RESTART).withStyle(ChatFormatting.ITALIC);
        Component warningTitle = Component.translatable(LangUtil.Gui.TAG_WARNING).withStyle(flashColor);
        Component alertTitle = Component.translatable(LangUtil.Gui.TAG_ALERT).withStyle(flashColor);

        Component newTooltip = Component.translatable(LangUtil.Gui.TAG_NEW_TOOLTIP);
        Component clientTooltip = Component.translatable(LangUtil.Gui.TAG_CLIENT_TOOLTIP);
        Component serverTooltip = Component.translatable(LangUtil.Gui.TAG_SERVER_TOOLTIP);
        Component dynamicTooltip = Component.translatable(LangUtil.Gui.TAG_DYNAMIC_TOOLTIP);
        Component reloadTooltip = Component.translatable(LangUtil.Gui.TAG_RELOAD_TOOLTIP);
        Component restartTooltip = Component.translatable(LangUtil.Gui.TAG_RESTART_TOOLTIP);
        Component sodiumTooltip = Component.translatable(this.tweak.getSodiumKey());
        Component optifineTooltip = Component.translatable(this.tweak.getOptifineKey());
        Component warningTooltip = Component.translatable(this.tweak.getWarningKey());

        boolean isNewRenderable = (Boolean) TweakClientCache.get(GuiTweak.DISPLAY_NEW_TAGS).getValue();
        boolean isSidedRenderable = (Boolean) TweakClientCache.get(GuiTweak.DISPLAY_SIDED_TAGS).getValue();
        boolean isTooltipRenderable = (Boolean) TweakClientCache.get(GuiTweak.DISPLAY_TAG_TOOLTIPS).getValue();

        int startX = ConfigRowList.getStartX() + minecraft.font.width(title) + (isTooltip ? 20 : 4);
        int startY = this.controller.getY() + 4;
        int lastX = startX;

        if (newTag != null && isNewRenderable)
        {
            if (isTooltipRenderable)
                renderTooltip(screen, graphics, newTitle, newTooltip, lastX, startY, mouseX, mouseY);

            lastX = renderTag(graphics, newTitle, lastX, startY, U_NEW_OFFSET, this.render);
        }

        if (clientTag != null && isSidedRenderable)
        {
            if (isTooltipRenderable)
                renderTooltip(screen, graphics, clientTitle, clientTooltip, lastX, startY, mouseX, mouseY);

            lastX = renderTag(graphics, clientTitle, lastX, startY, U_CLIENT_OFFSET, this.render);
        }

        if (serverTag != null && isSidedRenderable)
        {
            if (isTooltipRenderable)
                renderTooltip(screen, graphics, serverTitle, serverTooltip, lastX, startY, mouseX, mouseY);

            lastX = renderTag(graphics, serverTitle, lastX, startY, U_SERVER_OFFSET, this.render);
        }

        if (dynamicTag != null && isSidedRenderable)
        {
            if (isTooltipRenderable)
                renderTooltip(screen, graphics, dynamicTitle, dynamicTooltip, lastX, startY, mouseX, mouseY);

            lastX = renderTag(graphics, dynamicTitle, lastX, startY, U_DYNAMIC_OFFSET, this.render);
        }

        if (reloadTag != null)
        {
            renderTooltip(screen, graphics, reloadTitle, reloadTooltip, lastX, startY, mouseX, mouseY);
            lastX = renderTag(graphics, reloadTitle, lastX, startY, U_RELOAD_OFFSET, this.render);
        }

        if (restartTag != null)
        {
            renderTooltip(screen, graphics, restartTitle, restartTooltip, lastX, startY, mouseX, mouseY);
            lastX = renderTag(graphics, restartTitle, lastX, startY, U_RESTART_OFFSET, this.render);
        }

        if (alertTag != null && alertTag.condition().active())
        {
            Component tooltip = Component.translatable(alertTag.langKey());
            renderTooltip(screen, graphics, alertTitle, tooltip, lastX, startY, mouseX, mouseY);
            lastX = renderTag(graphics, alertTitle, lastX, startY, U_WARNING_OFFSET, this.render);
        }

        if (conflictTag != null && this.tweak.isConflict())
        {
            Component tooltip = Component.translatable(this.tweak.getConflictKey());
            renderTooltip(screen, graphics, alertTitle, tooltip, lastX, startY, mouseX, mouseY);
            lastX = renderTag(graphics, alertTitle, lastX, startY, U_WARNING_OFFSET, this.render);
        }

        if (warningTag != null)
        {
            renderTooltip(screen, graphics, warningTitle, warningTooltip, lastX, startY, mouseX, mouseY);
            lastX = renderTag(graphics, warningTitle, lastX, startY, U_WARNING_OFFSET, this.render);
        }

        if (sodiumTag != null && ModTracker.SODIUM.isInstalled())
        {
            if (sodiumTag.incompatible())
                sodiumTooltip = Component.translatable(LangUtil.Gui.TAG_SODIUM_TOOLTIP);

            renderTooltip(screen, graphics, sodiumTitle, sodiumTooltip, lastX, startY, mouseX, mouseY);
            lastX = renderTag(graphics, sodiumTitle, lastX, startY, U_RESTART_OFFSET, this.render);
        }

        if (optifineTag != null && ModTracker.OPTIFINE.isInstalled())
        {
            if (optifineTag.incompatible())
                optifineTooltip = Component.translatable(LangUtil.Gui.TAG_OPTIFINE_TOOLTIP);

            renderTooltip(screen, graphics, optifineTitle, optifineTooltip, lastX, startY, mouseX, mouseY);
            lastX = renderTag(graphics, optifineTitle, lastX, startY, U_RESTART_OFFSET, this.render);
        }

        int previousWidth = this.width;

        this.setX(startX);
        this.setWidth(lastX - startX);

        if (previousWidth != this.width)
            this.widthChanged = true;
    }

    /* Required Widget Overrides */

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) { }
}
