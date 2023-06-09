package mod.adrenix.nostalgic.client.config.gui.widget.button;

import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigWidgets;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.util.client.RunUtil;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import mod.adrenix.nostalgic.util.common.MathUtil;
import mod.adrenix.nostalgic.util.common.TextUtil;
import mod.adrenix.nostalgic.util.common.TextureLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * The tooltip button is a transparent button (no dimensions) that simply renders a speech bubble icon that when hovered
 * renders a helpful tooltip displaying more information about the tweak being configured.
 */

public class TooltipButton extends Button
{
    /* Fields */

    private final TweakClientCache<?> tweak;
    private final AbstractWidget controller;
    private String title;

    /* Constructor */

    /**
     * Create a new tooltip button instance.
     * @param tweak A tweak client cache instance.
     * @param controller A neighboring widget controller.
     */
    public TooltipButton(TweakClientCache<?> tweak, AbstractWidget controller)
    {
        super(0, 0, 0, 0, Component.empty(), RunUtil::nothing, DEFAULT_NARRATION);

        this.tweak = tweak;
        this.controller = controller;
        this.title = Component.translatable(this.tweak.getLangKey()).getString();
    }

    /* Methods */

    /**
     * Change the title of this tooltip button.
     * The purpose of including a title helps with rendering logic when determining the width of this widget.
     * @param title An unused title for display, but used for calculating the width of this widget.
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * Handler method for tooltip button rendering.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        List<Component> tooltip = TextUtil.Wrap.tooltip(Component.translatable(this.tweak.getTooltipKey()), 38);
        Minecraft minecraft = Minecraft.getInstance();

        if (ClassUtil.isNotInstanceOf(minecraft.screen, ConfigScreen.class))
            return;

        ConfigScreen screen = (ConfigScreen) minecraft.screen;

        int startX = ConfigRowList.getStartX() + minecraft.font.width(this.title) + 4;
        int startY = this.controller.getY() + 4;
        int uWidth = 12;
        int vHeight = 14;

        graphics.blit(TextureLocation.WIDGETS, startX, startY, 0, 0, uWidth, vHeight);

        boolean isOverBubble = MathUtil.isWithinBox(mouseX, mouseY, startX, startY, uWidth, vHeight);
        boolean isWithinList = ConfigWidgets.isInsideRowList(mouseY);

        if (isOverBubble && isWithinList)
            screen.renderLast.add(() -> graphics.renderComponentTooltip(Minecraft.getInstance().font, tooltip, mouseX, mouseY));
    }
}
