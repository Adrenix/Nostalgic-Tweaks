package mod.adrenix.nostalgic.client.config.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.annotation.TweakGui;
import mod.adrenix.nostalgic.client.config.annotation.TweakReload;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigWidgets;
import mod.adrenix.nostalgic.client.config.gui.widget.button.StatusButton;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.common.config.annotation.TweakData;
import mod.adrenix.nostalgic.common.config.tweak.GuiTweak;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.common.ComponentBackport;
import mod.adrenix.nostalgic.util.common.LangUtil;
import mod.adrenix.nostalgic.util.common.TextUtil;
import mod.adrenix.nostalgic.util.common.TextureLocation;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

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
        super(0, 0, 0, 0, ComponentBackport.empty());

        this.tweak = tweak;
        this.controller = controller;
        this.isTooltip = isTooltip;
        this.title = ComponentBackport.translatable(this.tweak.getLangKey()).getString();
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
        this.title = ComponentBackport.translatable(this.tweak.getLangKey()).getString();
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
     * @param screen The current screen.
     * @param poseStack The current pose stack.
     * @param x The x-position of where the tag should be drawn.
     * @param y The y-position of where the tag should be drawn.
     * @param uOffset The horizontal texture coordinate offset.
     * @param vOffset The vertical texture coordinate offset.
     * @param render Whether the tag should be rendered.
     */
    private static void draw(Screen screen, PoseStack poseStack, int x, int y, int uOffset, int vOffset, boolean render)
    {
        if (render)
            screen.blit(poseStack, x, y, uOffset, vOffset, U_GLOBAL_WIDTH, V_GLOBAL_HEIGHT);
    }

    /**
     * Renders a complete tag to the screen.
     * @param screen The current screen.
     * @param poseStack The current pose stack.
     * @param tag The tag to render.
     * @param startX The x-position of where the tag should be drawn.
     * @param startY The y-position of where the tag should be drawn.
     * @param uOffset The horizontal texture coordinate offset.
     * @param render Whether the tag should be rendered.
     * @return An x-position of where the next tag should start rendering. This includes the defined tag margin.
     */
    public static int renderTag(Screen screen, PoseStack poseStack, Component tag, int startX, int startY, int uOffset, boolean render)
    {
        RenderSystem.setShaderTexture(0, TextureLocation.WIDGETS);
        Font font = Minecraft.getInstance().font;

        int tagWidth = font.width(tag);
        int endX = getTagWidth(tag, startX);

        TweakTag.draw(screen, poseStack, startX, startY, uOffset, V_GLOBAL_OFFSET, render);

        for (int i = 0; i < tagWidth + TAG_MARGIN; i++)
            TweakTag.draw(screen, poseStack, startX + U_GLOBAL_WIDTH + i, startY, uOffset + 1, 0, render);

        TweakTag.draw(screen, poseStack, endX, startY, uOffset, V_GLOBAL_OFFSET, render);

        if (render)
            font.draw(poseStack, tag, startX + 4, startY + 2, 0xFFFFFF);

        return endX + TAG_MARGIN;
    }

    /**
     * An override method of {@link TweakTag#renderTag(Screen, PoseStack, Component, int, int, int, boolean)} that does
     * not require a rendering state.
     *
     * @param screen The current screen.
     * @param poseStack The current pose stack.
     * @param tag The tag to render.
     * @param startX The x-position of where the tag should be drawn.
     * @param startY The y-position of where the tag should be drawn.
     * @param uOffset The horizontal texture coordinate offset.
     * @return An x-position of where the next tag should start rendering. This includes the defined tag margin.
     */
    public static int renderTag(Screen screen, PoseStack poseStack, Component tag, int startX, int startY, int uOffset)
    {
        return renderTag(screen, poseStack, tag, startX, startY, uOffset, true);
    }

    /**
     * Render a tooltip on the screen if the mouse is over a specific position.
     * @param screen The current screen.
     * @param poseStack The current pose stack.
     * @param title The title component of a tag.
     * @param tooltip The tooltip component to display.
     * @param startX Where the tooltip box starts rendering on the x-axis.
     * @param startY Where the tooltip box starts rendering on the y-axis.
     * @param mouseX Where the mouse currently sits on the x-axis.
     * @param mouseY Where the mouse currently sits on the y-axis.
     */
    public static void renderTooltip(Screen screen, PoseStack poseStack, Component title, Component tooltip, int startX, int startY, int mouseX, int mouseY)
    {
        int endX = getTagWidth(title, startX);
        boolean isMouseOver = (mouseX >= startX && mouseX <= endX) && (mouseY >= startY && mouseY <= startY + V_GLOBAL_HEIGHT);

        // Prevents tooltip rendering when the row list has not assigned a y-position yet
        boolean isInitialized = startY != 4;
        boolean isWithinList = ConfigWidgets.isInsideRowList(mouseY);

        if (isMouseOver && isWithinList && isInitialized && screen instanceof ConfigScreen configScreen)
            configScreen.renderLast.add(() ->
                screen.renderComponentTooltip(poseStack, TextUtil.Wrap.tooltip(tooltip, 38), mouseX, mouseY));
    }

    /**
     * An override method that instructs the screen renderer what to show.
     * @param poseStack The current pose stack.
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @param partialTick The change in frame time.
     */
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
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

        Component optifineTitle = ComponentBackport.literal("Optifine");
        Component sodiumTitle = ComponentBackport.literal("Sodium");

        ChatFormatting flashColor = StatusButton.isFlashOff() ? ChatFormatting.GRAY : ChatFormatting.RED;

        Component title = ComponentBackport.literal(this.title);
        Component newTitle = ComponentBackport.translatable(LangUtil.Gui.TAG_NEW);
        Component clientTitle = ComponentBackport.translatable(LangUtil.Gui.TAG_CLIENT);
        Component serverTitle = ComponentBackport.translatable(LangUtil.Gui.TAG_SERVER);
        Component dynamicTitle = ComponentBackport.translatable(LangUtil.Gui.TAG_DYNAMIC);
        Component reloadTitle = ComponentBackport.translatable(LangUtil.Gui.TAG_RELOAD).withStyle(ChatFormatting.ITALIC);
        Component restartTitle = ComponentBackport.translatable(LangUtil.Gui.TAG_RESTART).withStyle(ChatFormatting.ITALIC);
        Component warningTitle = ComponentBackport.translatable(LangUtil.Gui.TAG_WARNING).withStyle(flashColor);
        Component alertTitle = ComponentBackport.translatable(LangUtil.Gui.TAG_ALERT).withStyle(flashColor);

        Component newTooltip = ComponentBackport.translatable(LangUtil.Gui.TAG_NEW_TOOLTIP);
        Component clientTooltip = ComponentBackport.translatable(LangUtil.Gui.TAG_CLIENT_TOOLTIP);
        Component serverTooltip = ComponentBackport.translatable(LangUtil.Gui.TAG_SERVER_TOOLTIP);
        Component dynamicTooltip = ComponentBackport.translatable(LangUtil.Gui.TAG_DYNAMIC_TOOLTIP);
        Component reloadTooltip = ComponentBackport.translatable(LangUtil.Gui.TAG_RELOAD_TOOLTIP);
        Component restartTooltip = ComponentBackport.translatable(LangUtil.Gui.TAG_RESTART_TOOLTIP);
        Component sodiumTooltip = ComponentBackport.translatable(this.tweak.getSodiumKey());
        Component optifineTooltip = ComponentBackport.translatable(this.tweak.getOptifineKey());
        Component warningTooltip = ComponentBackport.translatable(this.tweak.getWarningKey());

        boolean isNewRenderable = (Boolean) TweakClientCache.get(GuiTweak.DISPLAY_NEW_TAGS).getValue();
        boolean isSidedRenderable = (Boolean) TweakClientCache.get(GuiTweak.DISPLAY_SIDED_TAGS).getValue();
        boolean isTooltipRenderable = (Boolean) TweakClientCache.get(GuiTweak.DISPLAY_TAG_TOOLTIPS).getValue();

        int startX = ConfigRowList.getStartX() + minecraft.font.width(title) + (isTooltip ? 20 : 4);
        int startY = this.controller.y + 4;
        int lastX = startX;

        if (newTag != null && isNewRenderable)
        {
            if (isTooltipRenderable)
                renderTooltip(screen, poseStack, newTitle, newTooltip, lastX, startY, mouseX, mouseY);

            lastX = renderTag(screen, poseStack, newTitle, lastX, startY, U_NEW_OFFSET, this.render);
        }

        if (clientTag != null && isSidedRenderable)
        {
            if (isTooltipRenderable)
                renderTooltip(screen, poseStack, clientTitle, clientTooltip, lastX, startY, mouseX, mouseY);

            lastX = renderTag(screen, poseStack, clientTitle, lastX, startY, U_CLIENT_OFFSET, this.render);
        }

        if (serverTag != null && isSidedRenderable)
        {
            if (isTooltipRenderable)
                renderTooltip(screen, poseStack, serverTitle, serverTooltip, lastX, startY, mouseX, mouseY);

            lastX = renderTag(screen, poseStack, serverTitle, lastX, startY, U_SERVER_OFFSET, this.render);
        }

        if (dynamicTag != null && isSidedRenderable)
        {
            if (isTooltipRenderable)
                renderTooltip(screen, poseStack, dynamicTitle, dynamicTooltip, lastX, startY, mouseX, mouseY);

            lastX = renderTag(screen, poseStack, dynamicTitle, lastX, startY, U_DYNAMIC_OFFSET, this.render);
        }

        if (reloadTag != null)
        {
            renderTooltip(screen, poseStack, reloadTitle, reloadTooltip, lastX, startY, mouseX, mouseY);
            lastX = renderTag(screen, poseStack, reloadTitle, lastX, startY, U_RELOAD_OFFSET, this.render);
        }

        if (restartTag != null)
        {
            renderTooltip(screen, poseStack, restartTitle, restartTooltip, lastX, startY, mouseX, mouseY);
            lastX = renderTag(screen, poseStack, restartTitle, lastX, startY, U_RESTART_OFFSET, this.render);
        }

        if (alertTag != null && alertTag.condition().active())
        {
            Component tooltip = ComponentBackport.translatable(alertTag.langKey());
            renderTooltip(screen, poseStack, alertTitle, tooltip, lastX, startY, mouseX, mouseY);
            lastX = renderTag(screen, poseStack, alertTitle, lastX, startY, U_WARNING_OFFSET, this.render);
        }

        if (conflictTag != null && this.tweak.isConflict())
        {
            Component tooltip = ComponentBackport.translatable(this.tweak.getConflictKey());
            renderTooltip(screen, poseStack, alertTitle, tooltip, lastX, startY, mouseX, mouseY);
            lastX = renderTag(screen, poseStack, alertTitle, lastX, startY, U_WARNING_OFFSET, this.render);
        }

        if (warningTag != null)
        {
            renderTooltip(screen, poseStack, warningTitle, warningTooltip, lastX, startY, mouseX, mouseY);
            lastX = renderTag(screen, poseStack, warningTitle, lastX, startY, U_WARNING_OFFSET, this.render);
        }

        if (sodiumTag != null && ModTracker.SODIUM.isInstalled())
        {
            if (sodiumTag.incompatible())
                sodiumTooltip = ComponentBackport.translatable(LangUtil.Gui.TAG_SODIUM_TOOLTIP);

            renderTooltip(screen, poseStack, sodiumTitle, sodiumTooltip, lastX, startY, mouseX, mouseY);
            lastX = renderTag(screen, poseStack, sodiumTitle, lastX, startY, U_RESTART_OFFSET, this.render);
        }

        if (optifineTag != null && ModTracker.OPTIFINE.isInstalled())
        {
            if (optifineTag.incompatible())
                optifineTooltip = ComponentBackport.translatable(LangUtil.Gui.TAG_OPTIFINE_TOOLTIP);

            renderTooltip(screen, poseStack, optifineTitle, optifineTooltip, lastX, startY, mouseX, mouseY);
            lastX = renderTag(screen, poseStack, optifineTitle, lastX, startY, U_RESTART_OFFSET, this.render);
        }

        int previousWidth = this.width;
        this.x = startX;
        this.setWidth(lastX - startX);

        if (previousWidth != this.width)
            this.widthChanged = true;
    }

    /* Required Widget Overrides */

    @Override public void updateNarration(NarrationElementOutput narrationElementOutput) { }
}
