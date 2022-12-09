package mod.adrenix.nostalgic.client.config.gui.overlay.template;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import mod.adrenix.nostalgic.client.config.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.config.gui.overlay.OverlayFlag;
import mod.adrenix.nostalgic.util.client.RenderUtil;
import mod.adrenix.nostalgic.util.common.LangUtil;
import mod.adrenix.nostalgic.util.common.MathUtil;
import mod.adrenix.nostalgic.util.common.ModUtil;
import mod.adrenix.nostalgic.util.common.TextUtil;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Base class for generic overlays. A generic overlay is one that displays simple information and/or provides simple
 * abstract widgets. More specialized overlays will require their own implementations.
 */

public abstract class GenericOverlay extends Overlay implements GenericRendering
{
    /* Rendering Constants */

    public static final int U_TOP_LEFT_CORNER = 0;
    public static final int V_TOP_LEFT_CORNER = 0;
    public static final int W_TOP_LEFT_CORNER = 16;
    public static final int H_TOP_LEFT_CORNER = 15;

    public static final int U_TOP_RIGHT_CORNER = 19;
    public static final int V_TOP_RIGHT_CORNER = 0;
    public static final int W_TOP_RIGHT_CORNER = 16;
    public static final int H_TOP_RIGHT_CORNER = 15;

    public static final int U_TOP_BAR = 17;
    public static final int V_TOP_BAR = 0;
    public static final int H_TOP_BAR = 15;

    public static final int U_LEFT_BAR = 0;
    public static final int V_LEFT_BAR = 16;
    public static final int W_LEFT_BAR = 8;

    public static final int U_RIGHT_BAR = 27;
    public static final int V_RIGHT_BAR = 16;
    public static final int W_RIGHT_BAR = 8;

    public static final int U_BOTTOM_LEFT_CORNER = 0;
    public static final int V_BOTTOM_LEFT_CORNER = 18;
    public static final int W_BOTTOM_LEFT_CORNER = 16;
    public static final int H_BOTTOM_LEFT_CORNER = 8;

    public static final int U_BOTTOM_BAR = 17;
    public static final int V_BOTTOM_BAR = 18;
    public static final int H_BOTTOM_BAR = 8;

    public static final int U_BOTTOM_RIGHT_CORNER = 19;
    public static final int V_BOTTOM_RIGHT_CORNER = 18;
    public static final int W_BOTTOM_RIGHT_CORNER = 16;
    public static final int H_BOTTOM_RIGHT_CORNER = 8;

    public static final int U_CLOSE_DISABLED = 35;
    public static final int V_CLOSE_DISABLED = 18;
    public static final int U_CLOSE_OFF = 35;
    public static final int V_CLOSE_OFF = 9;
    public static final int U_CLOSE_ON = 35;
    public static final int V_CLOSE_ON = 0;

    public static final int U_HINT_OFF = 44;
    public static final int V_HINT_OFF = 9;
    public static final int U_HINT_ON = 44;
    public static final int V_HINT_ON = 0;

    public static final int HINT_SQUARE = 9;

    /* Rendering Utility */

    public int getOverlayStartX() { return (int) this.x + 10; }
    public int getOverlayStartY() { return (int) this.y + H_TOP_RIGHT_CORNER; }
    public int getOverlayHeight() { return this.height - H_TOP_RIGHT_CORNER - H_TOP_LEFT_CORNER; }
    public int getDrawWidth() { return this.width - W_TOP_RIGHT_CORNER - W_TOP_LEFT_CORNER; }

    /* Generic Fields */

    public final Component title;
    public int background = 0xC6000000;

    /* Constructor */

    /**
     * Start a new generic overlay window instance.
     * @param width The starting width of the overlay.
     * @param height The starting height of the overlay.
     * @param flags Any overlay flags that need defined.
     */
    public GenericOverlay(Component title, int width, int height, OverlayFlag ...flags)
    {
        super(width, height, flags);

        this.title = title;
    }

    /* Setters */

    /**
     * Change the color background for this generic overlay window instance.
     * @param color A color integer in ARGB format.
     */
    public void setBackground(int color) { this.background = color; }

    /* Overridable Methods */

    /**
     * Handler method for rendering a tooltip when the mouse is over the generic overlay icon in the top-left corner.
     * @param poseStack The current pose stack.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     */
    public void renderTooltipIcon(PoseStack poseStack, int mouseX, int mouseY)
    {
        boolean isOverIcon = MathUtil.isWithinBox(mouseX, mouseY, this.x + 7, this.y + 3, 8, 9);

        if (isOverIcon)
        {
            poseStack.pushPose();
            poseStack.translate(0.0D, 0.0D, 450.0D);

            List<Component> tooltip = TextUtil.Wrap.tooltip(Component.translatable(LangUtil.Gui.OVERLAY_DRAG_TIP), 36);
            this.screen.renderComponentTooltip(poseStack, tooltip, mouseX, mouseY);

            poseStack.popPose();
        }
    }

    /**
     * Handler method for rendering a tooltip when the mouse is over the generic overlay icon in the top-left corner.
     * @param poseStack The current pose stack.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     */
    public void renderTooltipHint(PoseStack poseStack, int mouseX, int mouseY)
    {
        int startX = (int) this.x + W_TOP_LEFT_CORNER + this.getDrawWidth() - 10;
        int startY = (int) this.y + 4;

        boolean isOverHint = MathUtil.isWithinBox(mouseX, mouseY, startX, startY, HINT_SQUARE, HINT_SQUARE);

        if (isOverHint && this.hint)
        {
            poseStack.pushPose();
            poseStack.translate(0.0D, 0.0D, 450.0D);

            List<Component> tooltip = TextUtil.Wrap.tooltip(Component.translatable(LangUtil.Gui.OVERLAY_LIST_HINT), 36);
            this.screen.renderComponentTooltip(poseStack, tooltip, mouseX, mouseY);

            poseStack.popPose();
        }
    }

    /* Widget Overrides */

    /**
     * Sets up overlay fields based on current game window properties.
     * Overlays can override this and add additional field definitions as needed.
     */
    @Override
    public void init()
    {
        this.x = (this.screen.width / 2.0D) - (this.width / 2.0D);
        this.y = (this.screen.height / 2.0D) - (this.height / 2.0D);
    }

    /**
     * Handler method for overlay rendering.
     * @param poseStack The current pose stack.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    @Override
    public void onRender(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        if (!this.isOpen())
            return;

        // Perform any pre-rendering instructions
        this.onPreRender(poseStack, mouseX, mouseY, partialTick);

        // Shift pose stack z-translation
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, Z_OFFSET);

        // Render overlay background
        this.renderBackground(poseStack);

        // Perform any main-rendering instructions
        this.onMainRender(poseStack, mouseX, mouseY, partialTick);

        // Render overlay textured border
        this.renderBorder(poseStack);

        // Render the close button
        this.renderCloseButton(poseStack, mouseX, mouseY);

        // Finish rendering z-offset
        poseStack.popPose();

        // Text needs to be rendered last since it will interfere with alpha rendering
        int color = this.isMouseOverTitle(mouseX, mouseY) && !this.isOverClose ? 0xFFF65B : 0xFFFFFF;

        drawString(this.title, (int) this.x + 19, (int) this.y + 5, color);

        // Render icon tooltip hint
        this.renderTooltipIcon(poseStack, mouseX, mouseY);

        // Perform any post-rendering instructions
        this.onPostRender(poseStack, mouseX, mouseY, partialTick);
    }

    /* Rendering Helpers */

    /**
     * Render the close button for the overlay. If the overlay is locked, then button cannot be interacted with.
     * @param poseStack The current pose stack.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     */
    public void renderCloseButton(PoseStack poseStack, int mouseX, int mouseY)
    {
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, ModUtil.Resource.CATEGORY_LIST);

        int closeX = (int) this.x + W_TOP_LEFT_CORNER + this.getDrawWidth();
        int closeY = (int) this.y + 4;
        this.isOverClose = MathUtil.isWithinBox(mouseX, mouseY, closeX, closeY, Overlay.CLOSE_WIDTH, Overlay.CLOSE_HEIGHT);

        this.blit
        (
            poseStack,
            closeX,
            closeY,
            this.locked ? U_CLOSE_DISABLED : this.isOverClose ? U_CLOSE_ON : U_CLOSE_OFF,
            this.locked ? V_CLOSE_DISABLED : this.isOverClose ? V_CLOSE_ON : V_CLOSE_OFF,
            Overlay.CLOSE_WIDTH,
            Overlay.CLOSE_HEIGHT
        );

        RenderSystem.disableTexture();
    }

    /**
     * Render the hint button for the overlay. This button will be to the left of the close button.
     * @param poseStack The current pose stack.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     */
    public void renderHintButton(PoseStack poseStack, int mouseX, int mouseY)
    {
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, ModUtil.Resource.CATEGORY_LIST);

        int hintX = (int) this.x + W_TOP_LEFT_CORNER + this.getDrawWidth() - 10;
        int hintY = (int) this.y + 4;
        boolean isOverHint = MathUtil.isWithinBox(mouseX, mouseY, hintX, hintY, HINT_SQUARE, HINT_SQUARE);

        this.blit
        (
            poseStack,
            hintX,
            hintY,
            isOverHint ? U_HINT_ON : U_HINT_OFF,
            isOverHint ? V_HINT_ON : V_HINT_OFF,
            HINT_SQUARE,
            HINT_SQUARE
        );

        RenderSystem.disableTexture();
    }

    /**
     * Render the border of an overlay using category list textures.
     * @param poseStack The current pose stack.
     */
    public void renderBorder(PoseStack poseStack)
    {
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, ModUtil.Resource.CATEGORY_LIST);

        int startX = (int) this.x;
        int startY = (int) this.y;
        int drawWidth = this.getDrawWidth();
        int drawHeight = this.getOverlayHeight();

        // Draw top left
        this.blit(poseStack, startX, startY, U_TOP_LEFT_CORNER, V_TOP_LEFT_CORNER, W_TOP_LEFT_CORNER, H_TOP_LEFT_CORNER);

        // Draw top width
        for (int i = 0; i < drawWidth; i++)
            this.blit(poseStack, i + startX + W_TOP_LEFT_CORNER, startY, U_TOP_BAR, V_TOP_BAR, 1, H_TOP_BAR);

        // Draw top right
        this.blit(poseStack, startX + W_TOP_LEFT_CORNER + drawWidth, startY, U_TOP_RIGHT_CORNER, V_TOP_RIGHT_CORNER, W_TOP_RIGHT_CORNER, H_TOP_RIGHT_CORNER);

        // Draw sidebars
        for (int i = 0; i < drawHeight; i++)
        {
            this.blit(poseStack, startX, i + startY + H_TOP_LEFT_CORNER, U_LEFT_BAR, V_LEFT_BAR, W_LEFT_BAR, 1);
            this.blit(poseStack, startX + W_TOP_LEFT_CORNER + drawWidth + 8, i + startY + H_TOP_RIGHT_CORNER, U_RIGHT_BAR, V_RIGHT_BAR, W_RIGHT_BAR, 1);
        }

        // Draw bottom left
        this.blit(poseStack, startX, startY + H_TOP_LEFT_CORNER + drawHeight, U_BOTTOM_LEFT_CORNER, V_BOTTOM_LEFT_CORNER, W_BOTTOM_LEFT_CORNER, H_BOTTOM_LEFT_CORNER);

        // Draw bottom width
        for (int i = 0; i < drawWidth; i++)
            this.blit(poseStack, i + startX + W_BOTTOM_LEFT_CORNER, startY + H_TOP_LEFT_CORNER + drawHeight, U_BOTTOM_BAR, V_BOTTOM_BAR, 1, H_BOTTOM_BAR);

        // Draw bottom right
        this.blit(poseStack, startX + W_BOTTOM_LEFT_CORNER + drawWidth, startY + H_TOP_RIGHT_CORNER + drawHeight, U_BOTTOM_RIGHT_CORNER, V_BOTTOM_RIGHT_CORNER, W_BOTTOM_RIGHT_CORNER, H_BOTTOM_RIGHT_CORNER);

        RenderSystem.disableTexture();
    }

    /**
     * Render a background behind an overlay window.
     * @param poseStack The current pose stack.
     */
    public void renderBackground(PoseStack poseStack)
    {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        Matrix4f matrix = poseStack.last().pose();

        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        float leftX = (float) this.x + 6;
        float rightX = (float) this.x + this.width - 4;
        float topY = (float) this.y + 14;
        float bottomY = (float) this.y + height - 10;

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        RenderUtil.fill(buffer, matrix, leftX, rightX, topY, bottomY, this.background);
        tesselator.end();

        RenderSystem.disableBlend();
    }
}
