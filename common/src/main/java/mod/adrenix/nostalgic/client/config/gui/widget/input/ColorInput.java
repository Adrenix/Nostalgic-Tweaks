package mod.adrenix.nostalgic.client.config.gui.widget.input;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import mod.adrenix.nostalgic.client.config.gui.overlay.ColorPicker;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.reflect.ClientReflect;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.util.common.LangUtil;
import mod.adrenix.nostalgic.util.common.ModUtil;
import mod.adrenix.nostalgic.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

public class ColorInput extends AbstractWidget
{
    /* Fields */

    private final EditBox input;
    private final TweakClientCache<String> cache;

    /* Constructor */

    public ColorInput(TweakClientCache<String> cache)
    {
        // Extends abstract widget so rendering can be overridden
        super(ConfigRowList.getControlStartX(), 0, ConfigRowList.BUTTON_WIDTH, ConfigRowList.BUTTON_HEIGHT, Component.empty());
        this.visible = false;

        // Hex cache and hex input edit box
        this.cache = cache;
        this.input = new EditBox
        (
            Minecraft.getInstance().font,
            0,
            0,
            ConfigRowList.BUTTON_WIDTH - 21,
            ConfigRowList.BUTTON_HEIGHT - 2,
            Component.empty()
        );

        this.input.setMaxLength(9);
        this.input.setBordered(true);
        this.input.setVisible(true);
        this.input.setTextColor(0xFFFFFF);
        this.input.setValue(this.validate(this.cache.getCurrent()));
        this.input.setFilter(this::filter);
        this.input.setResponder(this::update);
    }

    /* Methods */

    public AbstractWidget getWidget() { return this.input; }

    public String validate(String input)
    {
        input = input.replaceAll("[^a-fA-F\\d]", "");
        return "#" + input;
    }

    public boolean filter(String input)
    {
        if (input.equals("#"))
            return true;
        else
        {
            if (!input.startsWith("#"))
                input = "#" + input;
            return input.matches("^#[a-fA-F\\d]+$");
        }
    }

    public void update(String input)
    {
        if (!input.startsWith("#"))
            input = "#" + input;

        String cached = ClientReflect.getCurrent(this.cache.getGroup(), this.cache.getKey());
        if (cached.equals(input))
            this.cache.setCurrent(cached);
        else
            this.cache.setCurrent(input);
    }

    /* Overrides */

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (ModUtil.Numbers.isWithinBox(mouseX, mouseY, this.x, this.y, 20, 20))
        {
            ColorPicker.OVERLAY.open(this.cache);
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        Screen screen = Minecraft.getInstance().screen;
        if (screen == null)
            return;

        int color = ModUtil.Text.toHexInt(this.cache.getCurrent());
        int border = this.input.isFocused() ? 0xFFFFFFFF : 0xFFA0A0A0;

        float leftX = this.x;
        float rightX = leftX + 20;
        float topY = this.y;
        float bottomY = topY + 20;

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        Matrix4f matrix = poseStack.last().pose();

        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        // Bordering is used so users can see alpha transparency
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        RenderUtil.fill(buffer, matrix, leftX, rightX, topY, topY + 1, border);
        RenderUtil.fill(buffer, matrix, leftX, rightX, bottomY - 1, bottomY, border);
        RenderUtil.fill(buffer, matrix, leftX, leftX + 1, topY, bottomY, border);
        RenderUtil.fill(buffer, matrix, leftX + 1, rightX - 1, topY + 1, bottomY - 1, color);
        tesselator.end();

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();

        if (ModUtil.Numbers.isWithinBox(mouseX, mouseY, this.x, this.y, 20, 20) && screen instanceof ConfigScreen configScreen)
            configScreen.renderLast.add(() -> screen.renderTooltip(poseStack, Component.translatable(LangUtil.Gui.GUI_OVERLAY_INPUT_TIP), mouseX, mouseY));

        this.input.x = this.x + 21;
        this.input.y = this.y + 1;

        if (!this.input.getValue().equals(this.cache.getCurrent()))
            this.input.setValue(this.cache.getCurrent());
    }

    @Override public void updateNarration(NarrationElementOutput narrationElementOutput) { }
}
