package mod.adrenix.nostalgic.client.config.gui.overlay;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mod.adrenix.nostalgic.client.config.gui.widget.slider.ColorSlider;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.util.client.RenderUtil;
import mod.adrenix.nostalgic.util.common.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

/**
 * This class provides a color picker overlay which assists color fields in the configuration screen.
 * All overlay windows will have all sliders accessible. Hex management must be handled separately.
 */

public class ColorPickerOverlay extends Overlay
{
    /* Static Fields */

    public static final int OVERLAY_WIDTH = 176;
    public static final int OVERLAY_HEIGHT = 125;

    /* Constructor & Initialize */

    /**
     * Start a new color picker overlay window instance.
     * @param hex A client tweak cache with a string value that stores a hex value.
     */
    public ColorPickerOverlay(TweakClientCache<String> hex)
    {
        super(OVERLAY_WIDTH, OVERLAY_HEIGHT);

        this.tweak = hex;
        this.init();
    }

    /**
     * Sets up overlay fields based on current game window properties.
     */
    @Override
    public void init()
    {
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;

        if (screen == null)
            return;

        this.isJustOpened = true;
        this.hint = false;
        this.x = (screen.width / 2.0D) - (this.width / 2.0D);
        this.y = (screen.height / 2.0D) - (this.height / 2.0D);

        int[] rgba = ColorUtil.toHexRGBA(this.tweak.getValue());
        this.r = rgba[0];
        this.g = rgba[1];
        this.b = rgba[2];
        this.a = rgba[3];

        this.generateWidgets();
    }

    /* Rendering Constants */

    private static final int U_CLOSE_OFF = 176;
    private static final int V_CLOSE_OFF = 9;
    private static final int U_CLOSE_ON = 176;
    private static final int V_CLOSE_ON = 0;

    private static final int U_HINT_OFF = 176;
    private static final int V_HINT_OFF = 27;
    private static final int U_HINT_ON = 176;
    private static final int V_HINT_ON = 18;
    private static final int HINT_SQUARE = 9;

    /* Overlay Fields */

    private final TweakClientCache<String> tweak;
    private boolean hint = false;
    private int r;
    private int g;
    private int b;
    private int a;

    /* Field Setters & Getters */

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

    /* Overlay Overrides */

    /**
     * Defines the widgets that are used by this overlay.
     * Any existing widgets are cleared when this is invoked.
     */
    @Override
    public void generateWidgets()
    {
        int x = (int) this.x + 38;
        int y = (int) this.y + 20;
        int w = 125;
        int h = 20;
        int dy = 24;

        this.widgets.clear();
        this.widgets.add(new ColorSlider(this::setRed, this::getRed, ColorSlider.Type.R, x, y, w, h));
        this.widgets.add(new ColorSlider(this::setGreen, this::getGreen, ColorSlider.Type.G, x, y + dy, w, h));
        this.widgets.add(new ColorSlider(this::setBlue, this::getBlue, ColorSlider.Type.B, x, y + dy * 2, w, h));
        this.widgets.add(new ColorSlider(this::setAlpha, this::getAlpha, ColorSlider.Type.A, x, y + dy * 3, w, h));
    }

    /**
     * Handler method for when the mouse is clicked.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param button The mouse button that was clicked.
     * @return Whether this method handled the click event.
     */
    @Override
    public boolean onClick(double mouseX, double mouseY, int button)
    {
        if (MathUtil.isWithinBox(mouseX, mouseY, getHintX(), getHintY(), HINT_SQUARE, HINT_SQUARE))
            this.hint = !this.hint;

        return super.onClick(mouseX, mouseY, button);
    }

    /**
     * Draws a colored border around a slider widget.
     * @param buffer A buffer builder instance.
     * @param matrix A position matrix.
     * @param startX The starting x-position for rendering.
     * @param startY The starting y-position for rendering.
     * @param color A color integer for the border.
     */
    private static void drawBorder(BufferBuilder buffer, Matrix4f matrix, int startX, int startY, int color)
    {
        RenderUtil.fill(buffer, matrix, startX, startX + 1, startY, startY + 19, color);
        RenderUtil.fill(buffer, matrix, startX, startX + 125, startY, startY + 1, color);
        RenderUtil.fill(buffer, matrix, startX + 125, startX + 126, startY, startY + 20, color);
        RenderUtil.fill(buffer, matrix, startX, startX + 125, startY + 19, startY + 20, color);
    }

    /**
     * Handler method for overlay rendering.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick A change in game frame time.
     */
    @Override
    public void onRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;

        if (screen == null || !this.isOpen())
            return;

        this.tweak.setValue(ColorUtil.toHexString(new int[] {this.r, this.g, this.b, this.a}));

        int startX = (int) this.x;
        int startY = (int) this.y;
        int closeX = startX + 160;
        int closeY = startY + 4;
        int hintX = this.getHintX();
        int hintY = this.getHintY();

        boolean isOverHint = MathUtil.isWithinBox(mouseX, mouseY, hintX, hintY, HINT_SQUARE, HINT_SQUARE);
        this.isOverClose = MathUtil.isWithinBox(mouseX, mouseY, closeX, closeY, CLOSE_WIDTH, CLOSE_HEIGHT);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        Matrix4f matrix = graphics.pose().last().pose();

        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        RenderSystem.setShaderTexture(0, TextureLocation.COLOR_PICKER);

        graphics.blit(TextureLocation.COLOR_PICKER, startX, startY, 0, 0, this.width, this.height);
        graphics.blit(TextureLocation.COLOR_PICKER, closeX, closeY, this.isOverClose ? U_CLOSE_ON : U_CLOSE_OFF, this.isOverClose ? V_CLOSE_ON : V_CLOSE_OFF, CLOSE_WIDTH, CLOSE_HEIGHT);
        graphics.blit(TextureLocation.COLOR_PICKER, hintX, hintY, isOverHint ? U_HINT_ON : U_HINT_OFF, isOverHint ? V_HINT_ON : V_HINT_OFF, HINT_SQUARE, HINT_SQUARE);

        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        int leftX = startX + 14;
        int topY = startY + 21;

        int[] rgba = ColorUtil.toHexRGBA(this.tweak.getValue());
        int r = ColorUtil.toHexInt("#" + (rgba[0] < 16 ? "0" : "") + Integer.toHexString(rgba[0]) + "0000FF");
        int g = ColorUtil.toHexInt("#00" + (rgba[1] < 16 ? "0" : "") + Integer.toHexString(rgba[1]) + "00FF");
        int b = ColorUtil.toHexInt("#0000" + (rgba[2] < 16 ? "0" : "") + Integer.toHexString(rgba[2]) + "FF");
        int a = ColorUtil.toHexInt(this.tweak.getValue());

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        RenderUtil.fill(buffer, matrix, leftX, leftX + 18, topY, topY + 18, r);
        RenderUtil.fill(buffer, matrix, leftX, leftX + 18, topY + 24, topY + 24 + 18, g);
        RenderUtil.fill(buffer, matrix, leftX, leftX + 18, topY + 48, topY + 48 + 18, b);
        RenderUtil.fill(buffer, matrix, leftX, leftX + 18, topY + 72, topY + 72 + 18, a);
        tesselator.end();

        // Render widgets
        PoseStack sliders = new PoseStack();
        sliders.last().pose().translate(new Vector3f(0.0F, 0.0F, 1.0F));

        for (AbstractWidget widget : this.widgets)
            widget.render(graphics, mouseX, mouseY, partialTick);

        // Render borders around color sliders
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        leftX = startX + 38;
        topY -= 1;

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        drawBorder(buffer, matrix, leftX, topY, r);
        drawBorder(buffer, matrix, leftX, topY + 24, g);
        drawBorder(buffer, matrix, leftX, topY + 48, b);
        drawBorder(buffer, matrix, leftX, topY + 72, a);
        tesselator.end();

        // Text needs to be rendered last since it will interfere with alpha rendering
        int color = this.isMouseOverTitle(mouseX, mouseY) && !this.isOverClose && !isOverHint ? 0xFFF65B : 0xFFFFFF;
        graphics.drawString(minecraft.font, Component.translatable(LangUtil.Gui.OVERLAY_COLOR), startX + 19, startY + 5, color);

        // Render dragging and tooltip hints
        boolean isOverIcon = MathUtil.isWithinBox(mouseX, mouseY, this.x + 7, this.y + 3, 8, 9);

        PoseStack poseStack = graphics.pose();

        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, 500.0D);

        if (isOverIcon)
        {
            List<Component> tooltip = TextUtil.Wrap.tooltip(Component.translatable(LangUtil.Gui.OVERLAY_DRAG_TIP), 36);
            graphics.renderComponentTooltip(minecraft.font, tooltip, mouseX, mouseY);
        }

        if (isOverHint && this.hint)
        {
            List<Component> tooltip = TextUtil.Wrap.tooltip(Component.translatable(LangUtil.Gui.OVERLAY_COLOR_HINT), 36);
            graphics.renderComponentTooltip(minecraft.font, tooltip, mouseX, mouseY);
        }

        poseStack.popPose();
        RenderSystem.disableBlend();
    }
}
