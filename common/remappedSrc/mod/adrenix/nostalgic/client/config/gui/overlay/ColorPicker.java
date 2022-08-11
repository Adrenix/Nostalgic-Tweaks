package mod.adrenix.nostalgic.client.config.gui.overlay;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mod.adrenix.nostalgic.client.config.gui.widget.slider.ColorSlider;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.util.NostalgicLang;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import mod.adrenix.nostalgic.util.client.ModClientUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import java.util.List;

/**
 * This class provides a color picker overlay which assists color fields in the configuration screen.
 */

public class ColorPicker extends Overlay
{
    /* Singleton Constructor */

    public static final int OVERLAY_WIDTH = 176;
    public static final int OVERLAY_HEIGHT = 125;

    private ColorPicker() { super(OVERLAY_WIDTH, OVERLAY_HEIGHT); }

    /* Register Overlay */

    public static final ColorPicker OVERLAY = new ColorPicker();
    static { Overlay.register(OVERLAY); }

    /* Constants */

    private static final int U_CLOSE_OFF = 176;
    private static final int V_CLOSE_OFF = 9;
    private static final int U_CLOSE_ON = 176;
    private static final int V_CLOSE_ON = 0;

    private static final int U_HINT_OFF = 176;
    private static final int V_HINT_OFF = 27;
    private static final int U_HINT_ON = 176;
    private static final int V_HINT_ON = 18;
    private static final int HINT_SQUARE = 9;

    /* Fields */

    private TweakClientCache<String> cache;
    private boolean hint = false;
    private int r;
    private int g;
    private int b;
    private int a;

    /* Widgets Override */

    @Override
    public void generateWidgets()
    {
        int dy = 24;
        int x = (int) this.x + 38;
        int y = (int) this.y + 20;
        int w = 125;
        int h = 20;

        widgets.clear();
        widgets.add(new ColorSlider(this::setRed, this::getRed, ColorSlider.Type.R, x, y, w, h));
        widgets.add(new ColorSlider(this::setGreen, this::getGreen, ColorSlider.Type.G, x, y + dy, w, h));
        widgets.add(new ColorSlider(this::setBlue, this::getBlue, ColorSlider.Type.B, x, y + dy * 2, w, h));
        widgets.add(new ColorSlider(this::setAlpha, this::getAlpha, ColorSlider.Type.A, x, y + dy * 3, w, h));
    }

    private void setRed(int red) { this.r = red; }
    private void setGreen(int green) { this.g = green; }
    private void setBlue(int blue) { this.b = blue; }
    private void setAlpha(int alpha) { this.a = alpha; }

    private int getRed() { return this.r; }
    private int getGreen() { return this.g; }
    private int getBlue() { return this.b; }
    private int getAlpha() { return this.a; }

    private int getHintX() { return (int) this.x + 150; }
    private int getHintY() { return (int) this.y + 4; }

    /* Methods */

    public void open(TweakClientCache<String> hex)
    {
        Overlay.start(ColorPicker.OVERLAY);

        MinecraftClient minecraft = MinecraftClient.getInstance();
        Screen screen = minecraft.currentScreen;
        if (screen == null)
            return;

        this.isJustOpened = true;
        this.cache = hex;
        this.hint = false;
        this.x = (screen.width / 2.0D) - (this.width / 2.0D);
        this.y = (screen.height / 2.0D) - (this.height / 2.0D);

        int[] rgba = NostalgicUtil.Text.toHexRGBA(this.cache.getCurrent());
        this.r = rgba[0];
        this.g = rgba[1];
        this.b = rgba[2];
        this.a = rgba[3];

        this.generateWidgets();
    }

    /* Overrides */

    @Override
    public void onResize()
    {
        TweakClientCache<String> current = this.cache;
        this.onClose();
        this.open(current);
    }

    @Override
    public boolean onClick(double mouseX, double mouseY, int button)
    {
        if (NostalgicUtil.Numbers.isWithinBox(mouseX, mouseY, getHintX(), getHintY(), HINT_SQUARE, HINT_SQUARE))
            this.hint = !this.hint;
        return super.onClick(mouseX, mouseY, button);
    }

    private static void drawBorder(BufferBuilder buffer, Matrix4f matrix, int leftX, int topY, int color)
    {
        ModClientUtil.Render.fill(buffer, matrix, leftX, leftX + 1, topY, topY + 19, color);
        ModClientUtil.Render.fill(buffer, matrix, leftX, leftX + 125, topY, topY + 1, color);
        ModClientUtil.Render.fill(buffer, matrix, leftX + 125, leftX + 126, topY, topY + 20, color);
        ModClientUtil.Render.fill(buffer, matrix, leftX, leftX + 125, topY + 19, topY + 20, color);
    }

    @Override
    public void onRender(MatrixStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        Screen screen = minecraft.currentScreen;
        if (screen == null || !this.isOpen())
            return;

        this.cache.setCurrent(NostalgicUtil.Text.toHexString(new int[] {this.r, this.g, this.b, this.a}));

        int startX = (int) this.x;
        int startY = (int) this.y;
        int closeX = startX + 160;
        int closeY = startY + 4;
        int hintX = this.getHintX();
        int hintY = this.getHintY();

        boolean isOverHint = NostalgicUtil.Numbers.isWithinBox(mouseX, mouseY, hintX, hintY, HINT_SQUARE, HINT_SQUARE);
        this.isOverClose = NostalgicUtil.Numbers.isWithinBox(mouseX, mouseY, closeX, closeY, CLOSE_WIDTH, CLOSE_HEIGHT);

        Tessellator tesselator = Tessellator.getInstance();
        BufferBuilder buffer = tesselator.getBuffer();
        Matrix4f matrix = poseStack.peek().getPositionMatrix();

        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
        RenderSystem.setShaderTexture(0, NostalgicUtil.Resource.COLOR_PICKER);

        drawTexture(poseStack, startX, startY, 0, 0, this.width, this.height);
        drawTexture(poseStack, closeX, closeY, this.isOverClose ? U_CLOSE_ON : U_CLOSE_OFF, this.isOverClose ? V_CLOSE_ON : V_CLOSE_OFF, CLOSE_WIDTH, CLOSE_HEIGHT);
        drawTexture(poseStack, hintX, hintY, isOverHint ? U_HINT_ON : U_HINT_OFF, isOverHint ? V_HINT_ON : V_HINT_OFF, HINT_SQUARE, HINT_SQUARE);

        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        int leftX = startX + 14;
        int topY = startY + 21;

        int[] rgba = NostalgicUtil.Text.toHexRGBA(this.cache.getCurrent());
        int r = NostalgicUtil.Text.toHexInt("#" + (rgba[0] < 16 ? "0" : "") + Integer.toHexString(rgba[0]) + "0000FF");
        int g = NostalgicUtil.Text.toHexInt("#00" + (rgba[1] < 16 ? "0" : "") + Integer.toHexString(rgba[1]) + "00FF");
        int b = NostalgicUtil.Text.toHexInt("#0000" + (rgba[2] < 16 ? "0" : "") + Integer.toHexString(rgba[2]) + "FF");
        int a = NostalgicUtil.Text.toHexInt(this.cache.getCurrent());

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        ModClientUtil.Render.fill(buffer, matrix, leftX, leftX + 18, topY, topY + 18, r);
        ModClientUtil.Render.fill(buffer, matrix, leftX, leftX + 18, topY + 24, topY + 24 + 18, g);
        ModClientUtil.Render.fill(buffer, matrix, leftX, leftX + 18, topY + 48, topY + 48 + 18, b);
        ModClientUtil.Render.fill(buffer, matrix, leftX, leftX + 18, topY + 72, topY + 72 + 18, a);
        tesselator.draw();

        // Render widgets
        MatrixStack sliders = new MatrixStack();
        sliders.peek().getPositionMatrix().addToLastColumn(new Vec3f(0.0F, 0.0F, 1.0F));

        for (ClickableWidget widget : this.widgets)
            widget.render(sliders, mouseX, mouseY, partialTick);

        // Render borders around color sliders
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        leftX = startX + 38;
        topY -= 1;

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        drawBorder(buffer, matrix, leftX, topY, r);
        drawBorder(buffer, matrix, leftX, topY + 24, g);
        drawBorder(buffer, matrix, leftX, topY + 48, b);
        drawBorder(buffer, matrix, leftX, topY + 72, a);
        tesselator.draw();

        // Text needs to be rendered last since it will interfere with alpha rendering
        int color = this.isMouseOverTitle(mouseX, mouseY) && !this.isOverClose && !isOverHint ? 0xFFF65B : 0xFFFFFF;
        drawString(Text.translatable(NostalgicLang.Gui.GUI_OVERLAY_COLOR), startX + 19, startY + 5, color);

        // Render dragging and tooltip hints
        boolean isOverIcon = NostalgicUtil.Numbers.isWithinBox(mouseX, mouseY, this.x + 7, this.y + 3, 8, 9);
        if (isOverIcon)
        {
            List<Text> tooltip = NostalgicUtil.Wrap.tooltip(Text.translatable(NostalgicLang.Gui.GUI_OVERLAY_DRAG_TIP), 36);
            screen.renderTooltip(poseStack, tooltip, mouseX, mouseY);
        }

        if (isOverHint && this.hint)
        {
            List<Text> tooltip = NostalgicUtil.Wrap.tooltip(Text.translatable(NostalgicLang.Gui.GUI_OVERLAY_COLOR_HINT), 36);
            screen.renderTooltip(poseStack, tooltip, mouseX, mouseY);
        }

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}
