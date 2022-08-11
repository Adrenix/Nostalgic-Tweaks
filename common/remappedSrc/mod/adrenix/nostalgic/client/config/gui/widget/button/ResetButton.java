package mod.adrenix.nostalgic.client.config.gui.widget.button;

import mod.adrenix.nostalgic.client.config.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.config.gui.widget.input.ColorInput;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class ResetButton extends ButtonWidget
{
    protected static final Text TITLE = Text.translatable(NostalgicLang.Cloth.RESET);
    protected final ClickableWidget anchor;
    @Nullable protected final TweakClientCache<?> cache;

    public ResetButton(@Nullable TweakClientCache<?> cache, ClickableWidget anchor)
    {
        super(
            0,
            0,
            getResetWidth(),
            ConfigRowList.BUTTON_HEIGHT,
            TITLE,
            (button) -> {
                if (cache != null)
                {
                    cache.reset();

                    if (anchor instanceof TextFieldWidget input && cache.getCurrent() instanceof String value)
                        input.setText(value);
                    else if (anchor instanceof ColorInput color && cache.getCurrent() instanceof String value)
                        ((TextFieldWidget) color.getWidget()).setText(value);
                }
                else if (anchor instanceof KeyBindButton key)
                    key.reset();
            }
        );

        this.cache = cache;
        this.anchor = anchor;
        setStartX();
    }

    public static int getResetWidth() { return MinecraftClient.getInstance().textRenderer.getWidth(TITLE.getString()) + 8; }

    private void setStartX() { this.x = anchor.x + anchor.getWidth() + ConfigRowList.ROW_WIDGET_GAP; }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        setStartX();

        if (this.cache != null)
            this.active = this.cache.isResettable();
        else if (this.anchor instanceof KeyBindButton key)
            this.active = key.isResettable();

        if (Overlay.isOpened())
            this.active = false;

        super.render(poseStack, mouseX, mouseY, partialTick);
    }
}
