package mod.adrenix.nostalgic.client.config.gui.widget.input;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mod.adrenix.nostalgic.client.config.gui.overlay.ColorPicker;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.reflect.ClientReflect;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.util.NostalgicLang;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import mod.adrenix.nostalgic.util.client.ModClientUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;

public class ColorInput extends ClickableWidget
{
    /* Fields */

    private final TextFieldWidget input;
    private final TweakClientCache<String> cache;

    /* Constructor */

    public ColorInput(TweakClientCache<String> cache)
    {
        // Extends abstract widget so rendering can be overridden
        super(ConfigRowList.getControlStartX(), 0, ConfigRowList.BUTTON_WIDTH, ConfigRowList.BUTTON_HEIGHT, Text.empty());
        this.visible = false;

        // Hex cache and hex input edit box
        this.cache = cache;
        this.input = new TextFieldWidget
        (
            MinecraftClient.getInstance().textRenderer,
            0,
            0,
            ConfigRowList.BUTTON_WIDTH - 21,
            ConfigRowList.BUTTON_HEIGHT - 2,
            Text.empty()
        );

        this.input.setMaxLength(9);
        this.input.setDrawsBackground(true);
        this.input.setVisible(true);
        this.input.setEditableColor(0xFFFFFF);
        this.input.setText(this.validate(this.cache.getCurrent()));
        this.input.setTextPredicate(this::filter);
        this.input.setChangedListener(this::update);
    }

    /* Methods */

    public ClickableWidget getWidget() { return this.input; }

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
        if (NostalgicUtil.Numbers.isWithinBox(mouseX, mouseY, this.x, this.y, 20, 20))
        {
            ColorPicker.OVERLAY.open(this.cache);
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (screen == null)
            return;

        int color = NostalgicUtil.Text.toHexInt(this.cache.getCurrent());
        int border = this.input.isFocused() ? 0xFFFFFFFF : 0xFFA0A0A0;

        float leftX = this.x;
        float rightX = leftX + 20;
        float topY = this.y;
        float bottomY = topY + 20;

        Tessellator tesselator = Tessellator.getInstance();
        BufferBuilder buffer = tesselator.getBuffer();
        Matrix4f matrix = poseStack.peek().getPositionMatrix();

        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        // Bordering is used so users can see alpha transparency
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        ModClientUtil.Render.fill(buffer, matrix, leftX, rightX, topY, topY + 1, border);
        ModClientUtil.Render.fill(buffer, matrix, leftX, rightX, bottomY - 1, bottomY, border);
        ModClientUtil.Render.fill(buffer, matrix, leftX, leftX + 1, topY, bottomY, border);
        ModClientUtil.Render.fill(buffer, matrix, leftX + 1, rightX - 1, topY + 1, bottomY - 1, color);
        tesselator.draw();

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();

        if (NostalgicUtil.Numbers.isWithinBox(mouseX, mouseY, this.x, this.y, 20, 20) && screen instanceof ConfigScreen configScreen)
            configScreen.renderLast.add(() -> screen.renderTooltip(poseStack, Text.translatable(NostalgicLang.Gui.GUI_OVERLAY_INPUT_TIP), mouseX, mouseY));

        this.input.x = this.x + 21;
        this.input.y = this.y + 1;

        if (!this.input.getText().equals(this.cache.getCurrent()))
            this.input.setText(this.cache.getCurrent());
    }

    @Override public void appendNarrations(NarrationMessageBuilder narrationElementOutput) { }
}
