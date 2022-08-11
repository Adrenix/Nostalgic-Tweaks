package mod.adrenix.nostalgic.client.config.gui.widget.button;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.util.NostalgicLang;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.util.ArrayList;
import java.util.List;

public class StateButton extends OverlapButton
{
    /* Fields */

    private final StateType type;
    private boolean state;

    /* Private Static Helpers */

    private static Text getText(StateType type)
    {
        return switch (type)
        {
            case TAG -> Component.literal("@");
            case CLEAR -> Component.literal("\u274c").withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.BOLD);
            default -> Component.empty();
        };
    }

    /* Constructors */

    public StateButton(ConfigScreen screen, StateType type, int startX, int startY, boolean defaultState, PressAction onPress)
    {
        super(screen, startX, startY, 20, 20, getText(type), onPress);
        this.type = type;
        this.state = defaultState;
    }

    public StateButton(ConfigScreen screen, StateType type, int startX, int startY, PressAction onPress)
    {
        this(screen, type, startX, startY, true, onPress);
    }

    /* Getters */

    public boolean getState() { return this.state; }

    /* Tooltips */

    private List<Text> getTooltip(boolean isShiftDown)
    {
        List<Text> tooltip = new ArrayList<>();

        Text shift = Text.translatable(NostalgicLang.Gui.STATE_SHIFT).withStyle(Formatting.GRAY);
        Text title = switch (this.type)
        {
            case TAG -> Component.translatable(NostalgicLang.Gui.STATE_TAG).withStyle(ChatFormatting.GREEN);
            case CLEAR -> Component.translatable(NostalgicLang.Gui.STATE_CLEAR).withStyle(ChatFormatting.RED);
            case FUZZY -> Component.translatable(NostalgicLang.Gui.STATE_FUZZY).withStyle(ChatFormatting.GOLD);
            case BUBBLE -> Component.translatable(NostalgicLang.Gui.STATE_BUBBLE).withStyle(ChatFormatting.AQUA);
        };

        List<Text> wrap = switch (this.type)
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
    public void renderButton(MatrixStack poseStack, int mouseX, int mouseY, float partialTick)
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
    public void renderTooltip(MatrixStack poseStack, int mouseX, int mouseY)
    {
        if (this.shouldRenderToolTip(mouseX, mouseY))
        {
            this.screen.renderLast.add(() ->
                screen.renderTooltip(poseStack, this.getTooltip(Screen.hasShiftDown()), mouseX, mouseY)
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
