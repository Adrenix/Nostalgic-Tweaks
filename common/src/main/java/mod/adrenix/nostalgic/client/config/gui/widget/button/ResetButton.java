package mod.adrenix.nostalgic.client.config.gui.widget.button;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.config.gui.widget.input.ColorInput;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class ResetButton extends Button
{
    protected static final Component TITLE = Component.translatable(LangUtil.Cloth.RESET);
    protected final AbstractWidget anchor;
    @Nullable protected final TweakClientCache<?> cache;

    public ResetButton(@Nullable TweakClientCache<?> cache, AbstractWidget anchor)
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

                    if (anchor instanceof EditBox input && cache.getCurrent() instanceof String value)
                        input.setValue(value);
                    else if (anchor instanceof ColorInput color && cache.getCurrent() instanceof String value)
                        ((EditBox) color.getWidget()).setValue(value);
                }
                else if (anchor instanceof KeyBindButton key)
                    key.reset();
            }
        );

        this.cache = cache;
        this.anchor = anchor;
        setStartX();
    }

    public static int getResetWidth() { return Minecraft.getInstance().font.width(TITLE.getString()) + 8; }

    private void setStartX() { this.x = anchor.x + anchor.getWidth() + ConfigRowList.ROW_WIDGET_GAP; }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
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
