package mod.adrenix.nostalgic.client.config.gui.widget.button;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.client.config.gui.screen.list.ListScreen;
import mod.adrenix.nostalgic.util.client.ItemClientUtil;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import mod.adrenix.nostalgic.util.common.LangUtil;
import mod.adrenix.nostalgic.util.common.TextUtil;
import mod.adrenix.nostalgic.util.common.TextureLocation;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

/**
 * An extension of the overlap button and is used by the search tab in the config screen. Each state button will be
 * associated with a {@link StateWidget} enumeration value.
 */

public class StateButton extends OverlapButton
{
    /* Fields */

    /**
     * This field must be defined during construction.
     * Different search widgets require unique rendering and logic.
     */
    private final StateWidget widget;

    /**
     * This field determines whether this state button is on/off.
     */
    private boolean state;

    /* Private Static Helpers */

    /**
     * Gets a button title component based on the provide search widget type.
     * @param widget A search widget enumeration value.
     * @return A button title component.
     */
    private static Component getText(StateWidget widget)
    {
        return switch (widget)
        {
            case TAG -> Component.literal("#");
            case SWING -> Component.literal("?").withStyle(ChatFormatting.BOLD);
            case CLEAR -> Component.literal("\u274c").withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.BOLD);
            case LIGHTNING -> Component.literal("\u26a1").withStyle(ChatFormatting.YELLOW).withStyle(ChatFormatting.BOLD);
            default -> Component.empty();
        };
    }

    /* Constructors */

    /**
     * Create a state button with predefined default state.
     * @param widget A search widget enumeration value.
     * @param startX The starting x-position.
     * @param startY The starting y-position.
     * @param defaultState The default state of this button.
     * @param onPress Instructions for when the button is pressed.
     */
    public StateButton(StateWidget widget, int startX, int startY, boolean defaultState, OnPress onPress)
    {
        super(startX, startY, 20, 20, getText(widget), onPress);

        this.widget = widget;
        this.state = defaultState;
    }

    /**
     * Create a state button that is defaults to being true.
     * @param widget A search widget enumeration value.
     * @param startX The starting x-position.
     * @param startY The starting y-position.
     * @param onPress Instructions for when the button is pressed.
     */
    public StateButton(StateWidget widget, int startX, int startY, OnPress onPress)
    {
        this(widget, startX, startY, true, onPress);
    }

    /* Getters */

    /**
     * Determine whether this button is on/off.
     * @return The current state of this button.
     */
    public boolean getState() { return this.state; }

    /* Tooltips */

    /**
     * Get a list of tooltip components that is dependent on whether the shift key is held down.
     * @return A list of components that should be used in a tooltip.
     */
    private List<Component> getTooltipComponents()
    {
        List<Component> tooltip = new ArrayList<>();

        Component hide = Component.translatable(LangUtil.Gui.STATE_HIDE).withStyle(ChatFormatting.GRAY);
        Component shift = Component.translatable(LangUtil.Gui.STATE_SHIFT).withStyle(ChatFormatting.GRAY);

        Component title = switch (this.widget)
        {
            case TAG -> Component.translatable(LangUtil.Gui.STATE_TAG).withStyle(ChatFormatting.GREEN);
            case NUKE -> Component.translatable(LangUtil.Gui.STATE_NUKE).withStyle(ChatFormatting.RED);
            case SWING -> Component.translatable(LangUtil.Gui.STATE_SWING).withStyle(ChatFormatting.BLUE);
            case CLEAR -> Component.translatable(LangUtil.Gui.STATE_CLEAR).withStyle(ChatFormatting.RED);
            case FUZZY -> Component.translatable(LangUtil.Gui.STATE_FUZZY).withStyle(ChatFormatting.GOLD);
            case BUBBLE -> Component.translatable(LangUtil.Gui.STATE_BUBBLE).withStyle(ChatFormatting.AQUA);
            case FILTER -> Component.translatable(LangUtil.Gui.STATE_FILTER).withStyle(ChatFormatting.GOLD);
            case LIGHTNING -> Component.translatable(LangUtil.Gui.STATE_LIGHTNING).withStyle(ChatFormatting.YELLOW);
        };

        List<Component> wrap = switch (this.widget)
        {
            case TAG -> TextUtil.Wrap.tooltip(Component.translatable(LangUtil.Gui.STATE_TAG_TOOLTIP), 35);
            case NUKE -> TextUtil.Wrap.tooltip(Component.translatable(LangUtil.Gui.STATE_NUKE_TOOLTIP), 35);
            case SWING -> TextUtil.Wrap.tooltip(Component.translatable(LangUtil.Gui.STATE_SWING_TOOLTIP), 30);
            case CLEAR -> TextUtil.Wrap.tooltip(Component.translatable(LangUtil.Gui.STATE_CLEAR_TOOLTIP), 40);
            case FUZZY -> TextUtil.Wrap.tooltip(Component.translatable(LangUtil.Gui.STATE_FUZZY_TOOLTIP), 35);
            case BUBBLE -> TextUtil.Wrap.tooltip(Component.translatable(LangUtil.Gui.STATE_BUBBLE_TOOLTIP), 35);
            case FILTER -> TextUtil.Wrap.tooltip(Component.translatable(LangUtil.Gui.STATE_FILTER_TOOLTIP), 40);
            case LIGHTNING -> TextUtil.Wrap.tooltip(Component.translatable(LangUtil.Gui.STATE_LIGHTNING_TOOLTIP), 35);
        };

        tooltip.add(title);

        if (Screen.hasShiftDown())
            tooltip.addAll(wrap);
        else
        {
            tooltip.add(shift);
            tooltip.add(hide);
        }

        return tooltip;
    }

    /**
     * Render a tooltip component.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     */
    private void showTooltip(GuiGraphics graphics, int mouseX, int mouseY)
    {
        graphics.renderComponentTooltip(Minecraft.getInstance().font, this.getTooltipComponents(), mouseX, mouseY);
    }

    /* Overrides */

    /**
     * Handler method for adding extra rendering instructions when the button is rendered.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.renderWidget(graphics, mouseX, mouseY, partialTick);

        RenderSystem.setShaderTexture(0, TextureLocation.WIDGETS);

        int uOffset = this.state ? 0 : 20;
        int blockX = this.getX() + 2;
        int blockY = this.getY() + 2;

        switch (this.widget)
        {
            case NUKE -> ItemClientUtil.renderGuiItem(new ItemStack(Items.TNT), blockX, blockY, 0.85F, -0.5F);
            case BUBBLE -> graphics.blit(TextureLocation.WIDGETS, this.getX(), this.getY(), uOffset, 123, this.width, this.height);
            case FUZZY, FILTER -> graphics.blit(TextureLocation.WIDGETS, this.getX(), this.getY(), uOffset, 143, this.width, this.height);
        }

        this.renderToolTip(graphics, mouseX, mouseY);
    }

    /**
     * Handler method for when a tooltip is rendered.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     */
    public void renderToolTip(GuiGraphics graphics, int mouseX, int mouseY)
    {
        if (this.shouldRenderToolTip(mouseX, mouseY) && this.isMouseOver(mouseX, mouseY) && !Screen.hasControlDown())
            this.screen.renderLast.add(() -> this.showTooltip(graphics, mouseX, mouseY));
    }

    /**
     * Handler method for when this button is pressed.
     */
    @Override
    public void onPress()
    {
        if (ClassUtil.isNotInstanceOf(this.screen, ListScreen.class))
            this.state = !this.state;

        super.onPress();
    }
}
