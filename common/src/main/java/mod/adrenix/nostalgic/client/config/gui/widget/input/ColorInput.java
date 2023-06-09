package mod.adrenix.nostalgic.client.config.gui.widget.input;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import mod.adrenix.nostalgic.client.config.gui.overlay.ColorPickerOverlay;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.reflect.ClientReflect;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.util.client.RenderUtil;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import mod.adrenix.nostalgic.util.common.ColorUtil;
import mod.adrenix.nostalgic.util.common.LangUtil;
import mod.adrenix.nostalgic.util.common.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;

/**
 * A color input widget provides an extra layer of logic over an input box widget. These color input boxes comes with
 * hex validation logic, filtering logic, and hex pasting logic.
 */

public class ColorInput extends AbstractWidget
{
    /* Fields */

    private final EditBox input;
    private final TweakClientCache<String> tweak;

    /* Constructor */

    /**
     * Create a new color input widget instance.
     * @param tweak A tweak that uses a hex string as its value.
     */
    public ColorInput(TweakClientCache<String> tweak)
    {
        // Extends abstract widget so rendering can be overridden
        super(ConfigRowList.getControlStartX(), 0, ConfigRowList.BUTTON_WIDTH, ConfigRowList.BUTTON_HEIGHT, Component.empty());

        // This widget is not visible since it uses an edit box internally
        this.visible = false;

        // Hex cache and hex input edit box
        this.tweak = tweak;
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
        this.input.setValue(this.clean(this.tweak.getValue()));
        this.input.setFilter(this::filter);
        this.input.setResponder(this::update);
    }

    /* Methods */

    /**
     * Get the internally used edit box widget. The {@link ColorInput} class is not a widget that can be interacted with.
     * Use this method when you need set a property of the input box or need a widget instance to add.
     *
     * @return The edit box widget instance for this widget.
     */
    public AbstractWidget getWidget() { return this.input; }

    /**
     * Cleans the input string so that it appears as a valid hex string.
     *
     * This method removes non A-F 0-9 characters. Therefore, the length of the hex output is not guaranteed to be
     * three or six characters long.
     *
     * @param input The string to clean and prefix a pound sign to.
     * @return A cleaned string as hex.
     */
    public String clean(String input)
    {
        input = input.replaceAll("[^a-fA-F\\d]", "");
        return ("#" + input).trim().toUpperCase();
    }

    /**
     * Determines if the provided input should be considered a valid hex code. This method does not check if the input
     * is three or six characters long. Any string length is accepted as long as the string contains A-F 0-9 characters.
     * The pound sign can be omitted or included within the input.
     *
     * @param input The string to check.
     * @return Whether the given input is a valid hex code.
     */
    public boolean filter(String input)
    {
        if (input.equals("#"))
            return true;
        else
        {
            input = input.trim().toUpperCase();

            if (!input.startsWith("#"))
                input = "#" + input;

            return input.matches("^#[a-fA-F\\d]+$");
        }
    }

    /**
     * Update the tweak cache with current input.
     * @param input A valid hex code to set the tweak cache to.
     */
    public void update(String input)
    {
        if (!input.startsWith("#"))
            input = "#" + input;

        String cached = ClientReflect.getCurrent(this.tweak.getGroup(), this.tweak.getKey());

        if (cached.equals(input))
            this.tweak.setValue(cached.trim().toUpperCase());
        else
            this.tweak.setValue(input.trim().toUpperCase());
    }

    /* Overrides */

    /**
     * Handler method for when the mouse clicks on a color input widget.
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @param button The mouse button that was clicked.
     * @return Whether this method handled the mouse click event.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (MathUtil.isWithinBox(mouseX, mouseY, this.getX(), this.getY(), 20, 20))
        {
            new ColorPickerOverlay(this.tweak);
            this.playDownSound(Minecraft.getInstance().getSoundManager());

            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * Handler method for rendering a color input widget.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @param partialTick The change in frame time.
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        int color = ColorUtil.toHexInt(this.tweak.getValue());
        int border = this.input.isFocused() ? 0xFFFFFFFF : 0xFFA0A0A0;

        float leftX = this.getX();
        float rightX = leftX + 20;
        float topY = this.getY();
        float bottomY = topY + 20;

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        Matrix4f matrix = graphics.pose().last().pose();

        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        // Bordering is used so users can see alpha transparency
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        RenderUtil.fill(buffer, matrix, leftX, rightX, topY, topY + 1, border);
        RenderUtil.fill(buffer, matrix, leftX, rightX, bottomY - 1, bottomY, border);
        RenderUtil.fill(buffer, matrix, leftX, leftX + 1, topY, bottomY, border);
        RenderUtil.fill(buffer, matrix, leftX + 1, rightX - 1, topY + 1, bottomY - 1, color);
        tesselator.end();

        RenderSystem.disableBlend();

        if (ClassUtil.isNotInstanceOf(Minecraft.getInstance().screen, ConfigScreen.class))
            return;

        ConfigScreen screen = (ConfigScreen) Minecraft.getInstance().screen;

        if (MathUtil.isWithinBox(mouseX, mouseY, this.getX(), this.getY(), 20, 20))
            screen.renderLast.add(() -> graphics.renderTooltip(Minecraft.getInstance().font, Component.translatable(LangUtil.Gui.OVERLAY_INPUT_TIP), mouseX, mouseY));

        this.input.setX(this.getX() + 21);
        this.input.setY(this.getY() + 1);

        if (!this.input.getValue().equals(this.tweak.getValue()))
            this.input.setValue(this.tweak.getValue());
    }

    /* Required Widget Overrides */

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}
