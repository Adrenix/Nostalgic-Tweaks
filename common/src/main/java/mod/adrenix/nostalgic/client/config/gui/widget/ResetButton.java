package mod.adrenix.nostalgic.client.config.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.reflect.EntryCache;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.TranslatableComponent;
import org.jetbrains.annotations.Nullable;

public class ResetButton extends Button
{
    protected static final TranslatableComponent TITLE = new TranslatableComponent(NostalgicLang.Cloth.RESET);
    @Nullable
    protected final EntryCache<?> cache;
    protected final AbstractWidget anchor;

    public ResetButton(@Nullable EntryCache<?> cache, AbstractWidget anchor)
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
                    if (anchor instanceof EditBox && cache.getCurrent() instanceof String)
                        ((EditBox) anchor).setValue((String) cache.getCurrent());
                }
                else if (anchor instanceof KeyBindButton)
                    ((KeyBindButton) anchor).reset();
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
        else if (this.anchor instanceof KeyBindButton)
            this.active = ((KeyBindButton) this.anchor).isResettable();

        super.render(poseStack, mouseX, mouseY, partialTick);
    }
}
