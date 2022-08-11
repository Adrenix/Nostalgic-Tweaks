package mod.adrenix.nostalgic.client.config.gui.widget.button;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import java.util.HashMap;
import java.util.Map;

public class GroupButton extends ButtonWidget
{
    /* Expansion Cache */

    private static final Map<Enum<?>, Boolean> EXPANDED = new HashMap<>();

    /* Fields */

    private final Enum<?> id;
    private final ConfigRowList.CategoryRow row;
    private final Text title;
    private boolean lastSubcategory;
    private boolean highlighted;

    /* Constructor */

    public GroupButton(ConfigRowList.CategoryRow row, Enum<?> id, Text title, ConfigRowList.CatType catType)
    {
        super(ConfigRowList.CategoryRow.getIndent(catType), 0, 0, 0, Text.empty(), (ignored) -> {});

        this.id = id;
        this.row = row;
        this.title = title;
        this.width = 18;
        this.height = 16;

        EXPANDED.putIfAbsent(id, false);
    }

    /* Utility Methods */

    public void collapse()
    {
        if (this.isExpanded())
        {
            this.toggle();
            this.row.collapse();
        }
    }

    public Text getTitle() { return this.title; }

    public void setLastSubcategory(boolean state) { this.lastSubcategory = state; }

    public void setHighlight(boolean state) { this.highlighted = state; }

    public boolean isLastSubcategory() { return this.lastSubcategory; }

    public boolean isExpanded() { return EXPANDED.get(this.id); }

    public static void collapseAll() { EXPANDED.forEach((id, state) -> EXPANDED.put(id, false)); }

    /* Private Utility */

    private void toggle() { EXPANDED.put(this.id, !EXPANDED.get(this.id)); }

    /* Widget Overrides */

    public void silentPress()
    {
        if (this.isExpanded())
            this.row.collapse();
        else
            this.row.expand();

        this.toggle();
    }

    @Override
    public void onPress()
    {
        this.silentPress();
        super.onPress();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (ConfigScreen.isEnter(keyCode) && this.isFocused() && this.isNarratable())
        {
            this.silentPress();
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            return true;
        }

        return false;
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        RenderSystem.setShaderTexture(0, NostalgicUtil.Resource.WIDGETS_LOCATION);
        MinecraftClient minecraft = MinecraftClient.getInstance();
        Screen screen = minecraft.currentScreen;
        if (screen == null) return;

        boolean expanded = this.isExpanded();
        if (expanded && !this.row.isExpanded())
            row.expand();

        int uOffset = 33;
        int vOffset = 0;
        int uWidth = 12;
        int vHeight = 18;
        int blitX = this.x;
        int blitY = this.y;
        int color = this.highlighted ? 0xFFAA00 : 0xFFFFFF;
        boolean isMouseOver = this.isMouseOver(expanded ? mouseX + 4 : mouseX, expanded ? mouseY - 4 : mouseY) || this.isFocused();

        if (isMouseOver)
        {
            uOffset = expanded ? 47 : 33;
            vOffset = 23;
        }
        else if (expanded)
            uOffset = 47;

        if (expanded)
        {
            uWidth = 18;
            vHeight = 12;
            blitX = this.x - 4;
            blitY = this.y + 4;
        }

        this.width = 20 + minecraft.textRenderer.getWidth(this.title);
        screen.drawTexture(poseStack, blitX, blitY, uOffset, vOffset, uWidth, vHeight);
        Screen.drawTextWithShadow(poseStack, minecraft.textRenderer, this.title, this.x + 20, this.y + 5, isMouseOver ? 0xFFD800 : color);
    }
}
