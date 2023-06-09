package mod.adrenix.nostalgic.client.config.gui.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.ClientConfigCache;
import mod.adrenix.nostalgic.client.config.gui.screen.GearSpinner;
import mod.adrenix.nostalgic.common.NostalgicConnection;
import mod.adrenix.nostalgic.util.client.RenderUtil;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import mod.adrenix.nostalgic.util.common.LangUtil;
import mod.adrenix.nostalgic.util.common.TextureLocation;
import mod.adrenix.nostalgic.util.common.TimeWatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import java.util.List;

public class NostalgicToast implements Toast
{
    /* Singleton References */

    private final static NostalgicToast WELCOME = new NostalgicToast(ToastId.WELCOME);
    private final static NostalgicToast HANDSHAKE = new NostalgicToast(ToastId.HANDSHAKE);
    private final static NostalgicToast TWEAK_C2S = new NostalgicToast(ToastId.TWEAK_C2S);
    private final static NostalgicToast TWEAK_S2C = new NostalgicToast(ToastId.TWEAK_S2C);

    /**
     * Get a nostalgic toast singleton reference based on the given toast identifier.
     * @param id A toast identifier enumeration value.
     * @return A nostalgic toast singleton instance.
     */
    public static NostalgicToast getInstance(ToastId id)
    {
        return switch (id)
        {
            case WELCOME -> WELCOME;
            case HANDSHAKE -> HANDSHAKE;
            case TWEAK_C2S -> TWEAK_C2S;
            case TWEAK_S2C -> TWEAK_S2C;
        };
    }

    /* Ticking */

    /**
     * Ticking instructions that run at the end of each screen render cycle. This will check if certain conditions are
     * met so that toasts are properly opened and/or closed.
     */
    @SuppressWarnings("unused") // Unused parameters are needed for functional shortcut
    public static void onPostRender(Screen screen, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if (WELCOME.isClosed() && screen instanceof TitleScreen && !ClientConfigCache.getGui().interactedWithConfig)
            WELCOME.open();

        if (WELCOME.isOpened() && ClassUtil.isNotInstanceOf(screen, TitleScreen.class))
            WELCOME.close();
    }

    /* Fields */

    private TimeWatcher timer = null;
    private final Font font = Minecraft.getInstance().font;
    private final ToastId id;
    private int width;
    private Component title;
    private List<FormattedCharSequence> messageLines;
    private boolean isVisible = false;

    /* Constructor */

    /**
     * Create a new nostalgic toast instance.
     * @param id A toast identifier enumeration value.
     */
    private NostalgicToast(ToastId id)
    {
        this.id = id;
        this.setup();
    }

    /* Methods */

    /**
     * Sets the toast's title and message body.
     */
    private void setup()
    {
        this.title = switch (this.id)
        {
            case WELCOME -> Component.translatable(LangUtil.Gui.TOAST_WELCOME_TITLE);
            case HANDSHAKE -> Component.translatable(LangUtil.Gui.TOAST_HANDSHAKE_TITLE);
            case TWEAK_C2S -> Component.translatable(LangUtil.Gui.TOAST_TWEAK_C2S_TITLE);
            case TWEAK_S2C -> Component.translatable(LangUtil.Gui.TOAST_TWEAK_S2C_TITLE);
        };

        NostalgicConnection connection = NostalgicTweaks.getConnection().orElseGet(NostalgicConnection::disconnected);

        String client = ChatFormatting.GOLD + NostalgicTweaks.getTinyVersion();
        String server = ChatFormatting.GOLD + connection.getVersion();
        String loader = ChatFormatting.LIGHT_PURPLE + connection.getLoader();

        Component message = switch (this.id)
        {
            case WELCOME -> Component.translatable(LangUtil.Gui.TOAST_WELCOME_MESSAGE, client);
            case HANDSHAKE -> Component.translatable(LangUtil.Gui.TOAST_HANDSHAKE_MESSAGE, server, loader);
            case TWEAK_C2S -> Component.translatable(LangUtil.Gui.TOAST_TWEAK_C2S_MESSAGE);
            case TWEAK_S2C -> Component.translatable(LangUtil.Gui.TOAST_TWEAK_S2C_MESSAGE);
        };

        this.messageLines = this.font.split(message, 182);
        this.width = 24 + this.messageLines.stream().mapToInt(this.font::width).max().orElse(182);

        if (this.messageLines.size() == 1)
            this.width = Math.max(42 + this.font.width(this.title), 24 + this.font.width(this.messageLines.get(0)));
    }

    /**
     * Change the visibility of the toast.
     * @param visible Whether the toast is visible.
     */
    private void setVisible(boolean visible)
    {
        this.isVisible = visible;
        this.setup();
    }

    /**
     * Open this toast.
     * This will add the toast to the game's toast system if it isn't there already.
     */
    public void open()
    {
        this.setVisible(true);
        Minecraft.getInstance().getToasts().addToast(this);
    }

    /**
     * Close this toast.
     * This will remove the toast from the game's toast system if it wasn't already closed.
     */
    public void close() { this.setVisible(false); }

    /**
     * @return Whether this toast is opened.
     */
    public boolean isOpened() { return this.isVisible; }

    /**
     * @return Whether this toast is closed.
     */
    public boolean isClosed() { return !this.isOpened(); }

    /**
     * Set a timer for this toast instance.
     * This will override any previous timer.
     *
     * @param timer A new time watcher instance.
     * @return The toast instance so that additional instructions can be chained.
     */
    public NostalgicToast setTimer(TimeWatcher timer)
    {
        this.timer = timer;
        this.timer.skip();

        return this;
    }

    /**
     * Used by the game's internal toast system so that it can check if this toast is already open or not.
     * @return A toast identifier enumeration value.
     */
    @Override
    public Object getToken() { return this.id; }

    /**
     * Used by the renderer and internal toast system.
     * @return Gets the maximum width of this toast instance.
     */
    @Override
    public int width() { return this.width; }

    /**
     * Used by the internal toast system.
     * @return Gets the maximum height of this toast instance.
     */
    @Override
    public int height() { return 37 + (this.messageLines.size() * 12); }

    /**
     * Used by the renderer.
     * @return Gets the maximum drawing height for the toast.
     */
    private int drawHeight() { return this.height(); }

    /**
     * Render the toast.
     * @param graphics The current GuiGraphics object.
     * @param toast A toast component instance.
     * @param timeSinceLastVisible The time in milliseconds.
     * @return A visibility enumeration instance that indicates whether the toast is visible or not.
     */
    @Override
    public Visibility render(GuiGraphics graphics, ToastComponent toast, long timeSinceLastVisible)
    {
        RenderSystem.setShaderTexture(0, TextureLocation.TOASTS);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        graphics.blit(TextureLocation.TOASTS, 0, 0, 0, 0, 8, 8);
        graphics.blit(TextureLocation.TOASTS, 0, this.drawHeight() - 8, 0, 9, 8, 8);
        graphics.blit(TextureLocation.TOASTS, this.width() - 8, 0, 9, 0, 8, 8);
        graphics.blit(TextureLocation.TOASTS, this.width() - 8, this.drawHeight() - 8, 9, 9, 8, 8);

        for (int x = 8; x < this.width() - 8; x++)
        {
            graphics.blit(TextureLocation.TOASTS, x, 0, 8, 0, 1, 8);
            graphics.blit(TextureLocation.TOASTS, x, this.drawHeight() - 8, 8, 9, 1, 8);
        }

        for (int y = 0; y < this.drawHeight() - 16; y++)
        {
            graphics.blit(TextureLocation.TOASTS, 0, 8 + y, 0, 8, 8, 1);
            graphics.blit(TextureLocation.TOASTS, this.width() - 8, 8 + y, 9, 8, 8, 1);
        }

        RenderUtil.fill(graphics, 8, this.width() - 8, 8, this.drawHeight() - 8, 0xAF000000);
        GearSpinner.getInstance().render(graphics, 16.0F, 10, 10);

        PoseStack poseStack = graphics.pose();

        poseStack.pushPose();
        poseStack.translate(23.0F, 23.0F, 1.0);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-20.0F));

        String splash = "N.T";
        float scale = 1.8F - Mth.abs(Mth.sin((float) (Util.getMillis() % 1000L) / 1000.0F * ((float) Math.PI * 2)) * 0.1F);
        scale = scale * 5.0F / (float) (this.font.width(splash));

        poseStack.scale(scale, scale, scale);
        graphics.drawCenteredString(this.font, splash, 1, -6, 0xFFFF00);
        poseStack.popPose();

        graphics.drawString(this.font, this.title, 30, 14, 0xFFFF00, true);

        for (int i = 0; i < this.messageLines.size(); i++)
            graphics.drawString(this.font, this.messageLines.get(i), 12, 30 + i * 12, 0xFFFFFF, true);

        if (this.timer != null && this.timer.isReady())
            this.close();

        return this.isVisible ? Visibility.SHOW : Visibility.HIDE;
    }
}
