package mod.adrenix.nostalgic.client.config.gui.overlay;

import mod.adrenix.nostalgic.util.NostalgicUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Overlays are not screens nor widgets, instead they render on top of screens and use widgets within the overlay.
 */

public abstract class Overlay extends DrawableHelper implements IScreenOverlay
{
    /**
     * Singleton Registration
     */

    private static final Set<Overlay> OVERLAYS = new HashSet<>();
    public static void register(Overlay overlay) { OVERLAYS.add(overlay); }

    /**
     * GUI Helpers
     */

    @Nullable
    private static Overlay getOverlay()
    {
        for (Object obj : OVERLAYS.toArray())
            if (obj instanceof Overlay overlay && overlay.isOpen())
                return overlay;
        return null;
    }

    protected static void start(Overlay starting)
    {
        Overlay overlay = getOverlay();
        if (overlay != null && overlay.isOpen())
            overlay.onClose();
        starting.isOverlayOpen = true;
    }

    public static boolean isOpened()
    {
        Overlay overlay = getOverlay();
        return overlay != null;
    }

    public static void resize()
    {
        Overlay overlay = getOverlay();
        if (overlay != null)
            overlay.onResize();
    }

    public static void onRelease(double mouseX, double mouseY, int button)
    {
        Overlay overlay = getOverlay();
        if (overlay != null)
            overlay.onMouseReleased(mouseX, mouseY, button);
    }

    public static boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        Overlay overlay = getOverlay();
        if (overlay != null && keyCode == GLFW.GLFW_KEY_ESCAPE)
        {
            overlay.onClose();
            return true;
        }

        return overlay != null && overlay.onKeyPressed(keyCode, scanCode, modifiers);
    }

    public static boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        Overlay overlay = getOverlay();
        return overlay != null && overlay.onClick(mouseX, mouseY, button);
    }

    public static boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        Overlay overlay = getOverlay();
        return overlay != null && overlay.onDrag(mouseX, mouseY, button, dragX, dragY);
    }

    public static boolean mouseScrolled(double mouseX, double mouseY, double delta)
    {
        Overlay overlay = getOverlay();
        return overlay != null && overlay.onMouseScrolled(mouseX, mouseY, delta);
    }

    public static void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        Overlay overlay = getOverlay();
        if (overlay != null)
            overlay.onRender(poseStack, mouseX, mouseY, partialTick);
    }

    public static boolean isOverTitle(double mouseX, double mouseY)
    {
        Overlay overlay = getOverlay();
        if (overlay != null)
            return overlay.isMouseOverTitle(mouseX, mouseY);
        return false;
    }

    /**
     * Utility Methods
     */

    protected static void playClickSound()
    {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    // A custom draw string method is needed for overlays since text shadows will collide due to shadows being on the same z-translation.
    protected static void drawString(Text component, int x, int y, int color)
    {
        MatrixStack overlay = new MatrixStack();
        overlay.peek().getPositionMatrix().addToLastColumn(new Vec3f(0.0F, 0.0F, 0.03F));
        drawTextWithShadow(overlay, MinecraftClient.getInstance().textRenderer, component, x, y, color);
    }

    protected static void drawString(Text component, int x, int y) { drawString(component, x, y, 0xFFFFFF); }

    /**
     * Overlay Extension & Interface Overrides
     */

    protected final ArrayList<ClickableWidget> widgets = new ArrayList<>();
    protected boolean isOverlayOpen = false;
    protected boolean isJustOpened = false;
    protected boolean isOverClose = false;
    protected double x;
    protected double y;
    protected int width;
    protected int height;

    protected static final int LEFT_CLICK = GLFW.GLFW_MOUSE_BUTTON_LEFT;
    protected static final int CLOSE_WIDTH = 9;
    protected static final int CLOSE_HEIGHT = 9;

    protected Overlay(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    protected boolean isMouseOverTitle(double mouseX, double mouseY)
    {
        return NostalgicUtil.Numbers.isWithinBox(mouseX, mouseY, this.x, this.y, this.width, 15);
    }

    @Override
    public void generateWidgets() { }

    @Override
    public void onResize() { }

    @Override
    public void onClose() { this.isOverlayOpen = false; }

    @Override
    public boolean isOpen() { return this.isOverlayOpen; }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double delta)
    {
        // Send scroll to widgets
        this.widgets.forEach((widget) -> {
            if (NostalgicUtil.Numbers.isWithinBox(mouseX, mouseY, widget.x, widget.y, widget.getWidth(), widget.getHeight()))
                widget.mouseScrolled(mouseX, mouseY, delta);
        });

        return true;
    }

    @Override
    public boolean onDrag(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        // Send drag to widgets
        this.widgets.forEach((widget) -> {
            if (NostalgicUtil.Numbers.isWithinBox(mouseX, mouseY, widget.x, widget.y, widget.getWidth(), widget.getHeight()))
                widget.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        });

        // Handle overlay
        if (button == LEFT_CLICK && this.isMouseOverTitle(mouseX, mouseY))
        {
            this.x += dragX;
            this.y += dragY;
            this.generateWidgets();

            return true;
        }

        return false;
    }

    @Override
    public boolean onClick(double mouseX, double mouseY, int button)
    {
        // Send click to widgets
        this.widgets.forEach((widget) -> widget.mouseClicked(mouseX, mouseY, button));

        // Handle overlay
        if (button != LEFT_CLICK)
            return false;

        boolean isClickIn = NostalgicUtil.Numbers.isWithinBox(mouseX, mouseY, this.x, this.y, this.width, this.height);

        if (!isClickIn)
            this.onClose();

        if (this.isMouseOverTitle(mouseX, mouseY))
            Overlay.playClickSound();

        if (this.isOverClose)
            this.onClose();

        return false;
    }

    @Override
    public boolean onKeyPressed(int keyCode, int scanCode, int modifiers)
    {
        // Send key press to widgets
        this.widgets.forEach((widget) -> widget.keyPressed(keyCode, scanCode, modifiers));

        // Handle overlay
        return false;
    }

    @Override
    public void onMouseReleased(double mouseX, double mouseY, int button)
    {
        // Don't send the mouse release event to overlays if the window just opened
        if (this.isJustOpened)
        {
            this.isJustOpened = false;
            return;
        }

        // Send mouse release to widgets
        this.widgets.forEach((widget) -> {
            if (NostalgicUtil.Numbers.isWithinBox(mouseX, mouseY, widget.x, widget.y, widget.getWidth(), widget.getHeight()))
                widget.mouseReleased(mouseX, mouseY, button);
        });
    }
}
