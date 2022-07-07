package mod.adrenix.nostalgic.client.config.gui.widget.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.util.NostalgicLang;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class StateButton extends OverlapButton
{
    /* Fields */

    private final StateType type;
    private boolean state;

    /* Private Static Helpers */

    private static Component getText(StateType type)
    {
        return switch (type)
        {
            case TAG -> Component.literal("@");
            case CLEAR -> Component.literal("\u274c").withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.BOLD);
            default -> Component.empty();
        };
    }

    /* Constructors */

    public StateButton(ConfigScreen screen, StateType type, int startX, int startY, boolean defaultState, OnPress onPress)
    {
        super(screen, startX, startY, 20, 20, getText(type), onPress);
        this.type = type;
        this.state = defaultState;
    }

    public StateButton(ConfigScreen screen, StateType type, int startX, int startY, OnPress onPress)
    {
        this(screen, type, startX, startY, true, onPress);
    }

    /* Getters */

    public boolean getState() { return this.state; }

    /* Tooltips */

    private List<Component> getTooltip(boolean isShiftDown)
    {
        List<Component> tooltip = new ArrayList<>();

        Component shift = Component.translatable(NostalgicLang.Gui.STATE_SHIFT).withStyle(ChatFormatting.GRAY);
        Component title = switch (this.type)
        {
            case TAG -> Component.translatable(NostalgicLang.Gui.STATE_TAG).withStyle(ChatFormatting.GREEN);
            case CLEAR -> Component.translatable(NostalgicLang.Gui.STATE_CLEAR).withStyle(ChatFormatting.RED);
            case FUZZY -> Component.translatable(NostalgicLang.Gui.STATE_FUZZY).withStyle(ChatFormatting.GOLD);
            case BUBBLE -> Component.translatable(NostalgicLang.Gui.STATE_BUBBLE).withStyle(ChatFormatting.AQUA);
        };

        List<Component> wrap = switch (this.type)
        {
            case TAG -> NostalgicUtil.Wrap.tooltip(Component.translatable(NostalgicLang.Gui.STATE_TAG_TOOLTIP), 35);
            case CLEAR -> NostalgicUtil.Wrap.tooltip(Component.translatable(NostalgicLang.Gui.STATE_CLEAR_TOOLTIP), 40);
            case FUZZY -> NostalgicUtil.Wrap.tooltip(Component.translatable(NostalgicLang.Gui.STATE_FUZZY_TOOLTIP), 35);
            case BUBBLE -> NostalgicUtil.Wrap.tooltip(Component.translatable(NostalgicLang.Gui.STATE_BUBBLE_TOOLTIP), 35);
        };

        tooltip.add(title);

        if (isShiftDown)
            tooltip.addAll(wrap);
        else
            tooltip.add(shift);

        return tooltip;
    }

    /* Overrides */

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        super.renderButton(poseStack, mouseX, mouseY, partialTick);
        RenderSystem.setShaderTexture(0, NostalgicUtil.Resource.WIDGETS_LOCATION);

        int uOffset = this.state ? 0 : 20;

        switch (this.type)
        {
            case BUBBLE -> screen.blit(poseStack, this.x, this.y, uOffset, 123, this.width, this.height);
            case FUZZY -> screen.blit(poseStack, this.x, this.y, uOffset, 143, this.width, this.height);
        }
    }

    @Override
    public void renderToolTip(PoseStack poseStack, int mouseX, int mouseY)
    {
        if (this.shouldRenderToolTip(mouseX, mouseY))
        {
            this.screen.renderLast.add(() ->
                screen.renderComponentTooltip(poseStack, this.getTooltip(Screen.hasShiftDown()), mouseX, mouseY)
            );
        }
    }

    @Override
    public void onPress()
    {
        this.state = !this.state;
        super.onPress();
    }
}
