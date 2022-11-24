package mod.adrenix.nostalgic.client.config.gui.widget.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.util.common.LangUtil;
import mod.adrenix.nostalgic.util.common.ModUtil;
import mod.adrenix.nostalgic.util.common.TextUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * An extension of the overlap button and is used by the search tab in the config screen. Each state button will be
 * associated with a {@link SearchWidget} enumeration value.
 */

public class StateButton extends OverlapButton
{
    /* Fields */

    /**
     * This field must be defined during construction.
     * Different search widgets require unique rendering and logic.
     */
    private final SearchWidget widget;

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
    private static Component getText(SearchWidget widget)
    {
        return switch (widget)
        {
            case TAG -> Component.literal("@");
            case CLEAR -> Component.literal("\u274c").withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.BOLD);
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
    public StateButton(SearchWidget widget, int startX, int startY, boolean defaultState, OnPress onPress)
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
    public StateButton(SearchWidget widget, int startX, int startY, OnPress onPress)
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
    private List<Component> getTooltip()
    {
        List<Component> tooltip = new ArrayList<>();

        Component shift = Component.translatable(LangUtil.Gui.STATE_SHIFT).withStyle(ChatFormatting.GRAY);
        Component title = switch (this.widget)
        {
            case TAG -> Component.translatable(LangUtil.Gui.STATE_TAG).withStyle(ChatFormatting.GREEN);
            case CLEAR -> Component.translatable(LangUtil.Gui.STATE_CLEAR).withStyle(ChatFormatting.RED);
            case FUZZY -> Component.translatable(LangUtil.Gui.STATE_FUZZY).withStyle(ChatFormatting.GOLD);
            case BUBBLE -> Component.translatable(LangUtil.Gui.STATE_BUBBLE).withStyle(ChatFormatting.AQUA);
        };

        List<Component> wrap = switch (this.widget)
        {
            case TAG -> TextUtil.Wrap.tooltip(Component.translatable(LangUtil.Gui.STATE_TAG_TOOLTIP), 35);
            case CLEAR -> TextUtil.Wrap.tooltip(Component.translatable(LangUtil.Gui.STATE_CLEAR_TOOLTIP), 40);
            case FUZZY -> TextUtil.Wrap.tooltip(Component.translatable(LangUtil.Gui.STATE_FUZZY_TOOLTIP), 35);
            case BUBBLE -> TextUtil.Wrap.tooltip(Component.translatable(LangUtil.Gui.STATE_BUBBLE_TOOLTIP), 35);
        };

        tooltip.add(title);

        if (Screen.hasShiftDown())
            tooltip.addAll(wrap);
        else
            tooltip.add(shift);

        return tooltip;
    }

    /**
     * Render a tooltip component.
     * @param poseStack The current pose stack.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     */
    private void showTooltip(PoseStack poseStack, int mouseX, int mouseY)
    {
        this.screen.renderComponentTooltip(poseStack, this.getTooltip(), mouseX, mouseY);
    }

    /* Overrides */

    /**
     * Handler method for adding extra rendering instructions when the button is rendered.
     * @param poseStack The current pose stack.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        super.renderButton(poseStack, mouseX, mouseY, partialTick);
        RenderSystem.setShaderTexture(0, ModUtil.Resource.WIDGETS_LOCATION);

        int uOffset = this.state ? 0 : 20;

        switch (this.widget)
        {
            case BUBBLE -> this.screen.blit(poseStack, this.x, this.y, uOffset, 123, this.width, this.height);
            case FUZZY -> this.screen.blit(poseStack, this.x, this.y, uOffset, 143, this.width, this.height);
        }
    }

    /**
     * Handler method for when a tooltip is rendered.
     * @param poseStack The current pose stack.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     */
    @Override
    public void renderToolTip(PoseStack poseStack, int mouseX, int mouseY)
    {
        if (this.shouldRenderToolTip(mouseX, mouseY))
            this.screen.renderLast.add(() -> this.showTooltip(poseStack, mouseX, mouseY));
    }

    /**
     * Handler method for when this button is pressed.
     */
    @Override
    public void onPress()
    {
        this.state = !this.state;
        super.onPress();
    }
}
