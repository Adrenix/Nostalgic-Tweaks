package mod.adrenix.nostalgic.client.config.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.reflect.EntryCache;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.TranslatableComponent;

public class ResetButton extends Button
{
    protected static final TranslatableComponent TITLE = new TranslatableComponent(NostalgicLang.Cloth.RESET);
    protected final EntryCache<?> cache;
    protected final AbstractWidget anchor;

    public ResetButton(EntryCache<?> cache, AbstractWidget anchor)
    {
        super(
            0,
            0,
            getResetWidth(),
            ConfigRowList.BUTTON_HEIGHT,
            TITLE,
            (button) -> {
                cache.reset();
                if (anchor instanceof EditBox && cache.getCurrent() instanceof String)
                    ((EditBox) anchor).setValue((String) cache.getCurrent());
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
        this.active = this.cache.isResettable();
        super.render(poseStack, mouseX, mouseY, partialTick);
    }
}
