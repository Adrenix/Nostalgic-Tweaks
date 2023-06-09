package mod.adrenix.nostalgic.client.config.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.ClientConfigCache;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.gui.toast.NostalgicToast;
import mod.adrenix.nostalgic.client.config.gui.toast.ToastId;
import mod.adrenix.nostalgic.common.NostalgicConnection;
import mod.adrenix.nostalgic.common.config.tweak.TweakType;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.client.GuiUtil;
import mod.adrenix.nostalgic.util.client.LinkUtil;
import mod.adrenix.nostalgic.util.client.NetUtil;
import mod.adrenix.nostalgic.util.client.RunUtil;
import mod.adrenix.nostalgic.util.common.LangUtil;
import mod.adrenix.nostalgic.util.common.LinkLocation;
import mod.adrenix.nostalgic.util.common.MathUtil;
import mod.adrenix.nostalgic.util.common.TextureLocation;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

/**
 * The settings screen is the "home page" for the mod's configuration graphical user interface.
 *
 * Most of the user's time will be spent in the {@link ConfigScreen} since that is where tweaks and menu settings can
 * be changed.
 */

public class SettingsScreen extends Screen
{
    /* Widget Constants */

    public static final int BUTTON_HEIGHT = 20;
    public static final int DONE_BUTTON_TOP_OFFSET = 26;
    public static final int LARGE_WIDTH = 204;
    public static final int SMALL_WIDTH = 98;

    /* Fields */

    protected final Minecraft minecraft;
    private final Screen parent;
    private final ArrayList<Button> buttons = new ArrayList<>();
    private DonatorBanner banner;
    private boolean isRedirected;
    private boolean isMouseOverSupportToggle = false;

    /* Constructors */

    /**
     * This constructor requires a parent screen, a component title, and whether the screen should redirect.
     * @param parent A parent screen, if there was one.
     * @param title The screen's display title.
     * @param isRedirected Whether this screen should be immediately redirected.
     */
    public SettingsScreen(Screen parent, Component title, boolean isRedirected)
    {
        super(title);

        this.parent = parent;
        this.minecraft = Minecraft.getInstance();
        this.isRedirected = isRedirected;
        this.banner = new DonatorBanner();

        if (!ClientConfigCache.getGui().interactedWithConfig)
        {
            ClientConfigCache.getGui().interactedWithConfig = true;
            ClientConfigCache.save();

            NostalgicToast.getInstance(ToastId.WELCOME).close();
        }
    }

    /**
     * This constructor requires only a parent screen and whether the screen should redirect.
     * @param parent A parent screen, if there was one.
     * @param isRedirected Whether this screen should be immediately redirected.
     */
    public SettingsScreen(Screen parent, boolean isRedirected)
    {
        this(parent, Component.translatable(LangUtil.Gui.SETTINGS_TITLE), isRedirected);
    }

    /* Button Helpers */

    /**
     * Add a new button to the setting screen.
     * @param title The title of the button.
     * @param onPress A runnable that is invoked when the button is pressed.
     */
    private void addButton(Component title, Button.OnPress onPress)
    {
        this.buttons.add(Button.builder(title, onPress).size(SMALL_WIDTH, BUTTON_HEIGHT).build());
    }

    /**
     * Repositions, resizes, and aligns buttons based on whether the title text is too large for a small button.
     * There is no logic for handling when the game window is too small.
     */
    private void setupButtons()
    {
        int i = 1;
        int row = 0;
        int startX = this.width / 2 - 102;
        int startY = this.height / 4 + 54 - 8;
        int lastX = this.width / 2 + 4;
        boolean isNextLarge = false;
        Button prevButton = this.buttons.get(0);

        for (Button button : this.buttons)
        {
            button.setX((i % 2 == 0) ? lastX : startX);

            boolean isFontWide = this.minecraft.font.width(button.getMessage()) > SMALL_WIDTH;
            boolean isLastButton = button.equals(this.buttons.get(this.buttons.size() - 1));

            if (isNextLarge || isFontWide || isLastButton)
            {
                if (isNextLarge)
                {
                    isNextLarge = false;
                    if (i % 2 == 0)
                        row--;
                }

                if (i % 2 == 0)
                {
                    row++;
                    button.setX(startX);
                    prevButton.setX(startX);
                    prevButton.setWidth(LARGE_WIDTH);
                }
                else
                    isNextLarge = true;

                button.setWidth(LARGE_WIDTH);
            }

            button.setY(startY + (row * 24));
            prevButton = button;

            if (i % 2 == 0 || button.getWidth() == LARGE_WIDTH)
                row++;

            i++;
        }
    }

    /* Screen Initialization & Screen Closing */

    /**
     * Initializes the home settings screen by defining the screen's widgets and redirecting the screen if that option
     * is enabled by the user.
     */
    @Override
    protected void init()
    {
        // Clear Button List on Init
        this.buttons.clear();

        // General Settings
        this.addButton
        (
            Component.translatable(LangUtil.Config.CONFIG_TITLE), (button) ->
                this.minecraft.setScreen(new ConfigScreen(this))
        );

        // Config Presets (WIP)
        Button preset = Button.builder(Component.translatable(LangUtil.Gui.SETTINGS_PRESETS), RunUtil::nothing)
            .size(SMALL_WIDTH, BUTTON_HEIGHT)
            .build()
        ;

        preset.active = false;

        this.buttons.add(preset);

        // Support
        this.addButton(Component.translatable(LangUtil.Gui.SETTINGS_SUPPORT), LinkUtil.onPress(LinkLocation.KO_FI));

        // Discord
        this.addButton(Component.translatable(LangUtil.Gui.SETTINGS_DISCORD), LinkUtil.onPress(LinkLocation.DISCORD));

        // Golden Days Button
        this.addButton(Component.translatable(LangUtil.Gui.SETTINGS_GOLDEN_DAYS), LinkUtil.onPress(LinkLocation.GOLDEN_DAYS));

        // Done Button
        this.addButton(Component.translatable(LangUtil.Vanilla.GUI_DONE), (button) -> this.onClose());

        // Setup Buttons
        this.setupButtons();

        // Add Buttons as Widgets
        for (Button button : this.buttons)
            this.addRenderableWidget(button);

        // Jump Screen
        if (this.isRedirected)
        {
            this.isRedirected = false;

            switch (ClientConfigCache.getGui().defaultScreen)
            {
                case SETTINGS_MENU -> this.minecraft.setScreen(new ConfigScreen(this));
                case PRESETS_MENU -> this.minecraft.setScreen(this);
            }
        }
    }

    /**
     * Handles a screen key pressing event.
     * @param keyCode A key code.
     * @param scanCode A key scancode.
     * @param modifiers Any held key modifiers.
     * @return Whether this method handled the event.
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (Screen.hasShiftDown() && Screen.hasControlDown() && keyCode == GLFW.GLFW_KEY_D)
            NostalgicTweaks.LOGGER.setDebug(!NostalgicTweaks.isDebugging());

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * Handles a mouse click event.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param button The mouse button that was clicked.
     * @return Whether something was clicked.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        boolean isClicked = super.mouseClicked(mouseX, mouseY, button);

        if (this.isMouseOverSupportToggle)
        {
            DonatorBanner.toggle();

            ClientConfigCache.getGui().displayDonatorBanner = DonatorBanner.isOpen();
            ClientConfigCache.save();

            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));

            return true;
        }

        return isClicked;
    }

    /**
     * Handler for when the screen closes.
     * All configuration values are saved to disk when this screen closes.
     */
    @Override
    public void onClose() { this.minecraft.setScreen(parent); }

    /**
     * Handler for when the screen resizes.
     * @param minecraft The Minecraft instance.
     * @param width The new screen width.
     * @param height The new screen height.
     */
    @Override
    public void resize(Minecraft minecraft, int width, int height)
    {
        super.resize(minecraft, width, height);

        this.banner = new DonatorBanner();
    }

    /* Rendering */

    /**
     * Renders the home settings screen.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if (this.minecraft.level != null)
            graphics.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        else
            this.renderDirtBackground(graphics);

        this.renderLogo(graphics);

        if (NostalgicTweaks.isDebugging())
            this.renderDebug(graphics);
        else
        {
            this.banner.render(graphics, partialTick);

            if (DonatorBanner.isOpen())
            {
                int height = DonatorBanner.getHeight();
                graphics.fillGradient(0, height - 5, this.width, height - 2, 0, 0x8F000000);
                graphics.fillGradient(0, 0, this.width, 3, 0x8F000000, 0);
            }

            this.renderSupportToggle(graphics, mouseX, mouseY);

            Component hint = Component.literal("Debug (Ctrl + Shift + D)").withStyle(ChatFormatting.DARK_GRAY);
            graphics.drawString(this.font, hint, 2, this.height - 10, 0xFFFFFF);
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    /**
     * Renders the toggle "button" text that controls support banner opening.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     */
    private void renderSupportToggle(GuiGraphics graphics, int mouseX, int mouseY)
    {
        MutableComponent button = DonatorBanner.isOpen() ?
            Component.literal("\u274c").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.RED) :
            Component.literal("\u2764").withStyle(ChatFormatting.RED)
        ;

        boolean isOpen = DonatorBanner.isOpen();
        int textHeight = this.font.lineHeight;
        int textWidth = this.font.width(button);
        int startX = this.width - textWidth - 2;
        int startY = isOpen ? DonatorBanner.getHeight() : 2;
        boolean isOver = MathUtil.isWithinBox(mouseX, mouseY, startX, startY, textWidth, textHeight);
        int color = isOver ? 0xFFFFA0 : 0xFFFFFF;
        this.isMouseOverSupportToggle = isOver;

        if (isOver)
            button.withStyle(ChatFormatting.DARK_RED);

        graphics.drawString(this.font, button, startX, startY, color, true);
    }

    /**
     * Changes the color of a string based on the given value.
     * @param value An Object value.
     * @return The default string or a colored string.
     */
    private String getColored(Object value)
    {
        if (value instanceof Boolean)
            return String.format("%s%s", (Boolean) value ? "§2" : "§4", value);

        return String.format("§f%s", value);
    }

    /**
     * Renders debug information when the user has the feature enabled.
     * @param graphics The current GuiGraphics object.
     */
    private void renderDebug(GuiGraphics graphics)
    {
        GuiUtil.CornerManager manager = new GuiUtil.CornerManager();
        NostalgicConnection connection = NostalgicTweaks.getConnection().orElseGet(NostalgicConnection::disconnected);

        graphics.drawCenteredString(this.font, "Debug Mode (Ctrl + Shift + D)", this.width / 2, 2, 0xFFFF00);

        GuiUtil.drawText(graphics, String.format("Loader: §d%s", NostalgicTweaks.isForge() ? "Forge" : "Fabric"), TweakType.Corner.TOP_LEFT, manager);
        GuiUtil.drawText(graphics, String.format("Version: §e%s", NostalgicTweaks.getShortVersion()), TweakType.Corner.TOP_LEFT, manager);
        GuiUtil.drawText(graphics, String.format("Protocol: §b%s", NostalgicTweaks.PROTOCOL), TweakType.Corner.TOP_LEFT, manager);

        GuiUtil.drawText(graphics, String.format("Singleplayer: %s", getColored(NetUtil.isSingleplayer())), TweakType.Corner.BOTTOM_LEFT, manager);
        GuiUtil.drawText(graphics, String.format("Multiplayer: %s", getColored(NetUtil.isMultiplayer())), TweakType.Corner.BOTTOM_LEFT, manager);
        GuiUtil.drawText(graphics, String.format("Connected: %s", getColored(NetUtil.isConnected())), TweakType.Corner.BOTTOM_LEFT, manager);
        GuiUtil.drawText(graphics, String.format("Operator: %s", getColored(NetUtil.isPlayerOp())), TweakType.Corner.BOTTOM_LEFT, manager);
        GuiUtil.drawText(graphics, String.format("Verified: %s", getColored(NostalgicTweaks.isNetworkVerified())), TweakType.Corner.BOTTOM_LEFT, manager);
        GuiUtil.drawText(graphics, String.format("LAN: %s", getColored(NetUtil.isLocalHost())), TweakType.Corner.BOTTOM_LEFT, manager);

        GuiUtil.drawText(graphics, String.format("Server Protocol: §b%s", connection.getProtocol()), TweakType.Corner.BOTTOM_RIGHT, manager);
        GuiUtil.drawText(graphics, String.format("Server Version: §e%s", connection.getVersion()), TweakType.Corner.BOTTOM_RIGHT, manager);
        GuiUtil.drawText(graphics, String.format("Server Loader: §d%s", connection.getLoader()), TweakType.Corner.BOTTOM_RIGHT, manager);
        GuiUtil.drawText(graphics, String.format("Server: %s", getColored(NostalgicTweaks.getConnection().isPresent())), TweakType.Corner.BOTTOM_RIGHT, manager);

        GuiUtil.drawText(graphics, String.format("Flywheel: %s", getColored(ModTracker.FLYWHEEL.isInstalled())), TweakType.Corner.TOP_RIGHT, manager);
        GuiUtil.drawText(graphics, String.format("Optifine: %s", getColored(ModTracker.OPTIFINE.isInstalled())), TweakType.Corner.TOP_RIGHT, manager);

        if (NostalgicTweaks.isFabric())
            GuiUtil.drawText(graphics, String.format("Sodium: %s", getColored(ModTracker.SODIUM.isInstalled())), TweakType.Corner.TOP_RIGHT, manager);
    }

    /**
     * Renders the mod's logo to the home settings screen.
     * @param graphics The current GuiGraphics object.
     */
    private void renderLogo(GuiGraphics graphics)
    {
        RenderSystem.enableBlend();

        int gearX = this.width / 2 - 23;
        int gearY = (this.height / 4 + 1);
        int titleX = this.width / 2 - 129;
        int titleY = (this.height / 4 - 25);

        GearSpinner.getInstance().render(graphics, 44.279F, gearX, gearY);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TextureLocation.NOSTALGIC_TWEAKS);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        float titleScale = 0.03325F;

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(titleX, titleY, 1.0D);
        poseStack.scale(titleScale, titleScale, titleScale);
        graphics.blit(TextureLocation.NOSTALGIC_TWEAKS, 0, 0, 0, 0, 7808, 742, 7808, 742);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate((float) this.width / 2 + 10, (float) this.height / 4 + 35, 1.0);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-20.0F));

        String splash = "v" + NostalgicTweaks.getTinyVersion();
        float scale = 1.8F - Mth.abs(Mth.sin((float) (Util.getMillis() % 1000L) / 1000.0F * ((float) Math.PI * 2)) * 0.1F);
        scale = scale * 17.0F / (float) (this.font.width(splash));

        poseStack.scale(scale, scale, scale);
        graphics.drawCenteredString(this.font, splash, 1, -6, 0xFFFF00);
        poseStack.popPose();

        String beta = NostalgicTweaks.getBetaVersion();

        if (beta.isEmpty())
            return;

        float betaScale = (int) this.minecraft.getWindow().getGuiScale() % 2 == 0 ? 0.5F : 0.6F;

        poseStack.pushPose();
        poseStack.translate(gearX + 46, gearY + 34, 1.0D);
        poseStack.scale(betaScale, betaScale, betaScale);
        graphics.drawString(this.font, beta, 0, 0, 0xFFFF00, true);
        poseStack.popPose();

        RenderSystem.disableBlend();
    }
}