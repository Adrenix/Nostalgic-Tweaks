package mod.adrenix.nostalgic.client.config.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Vector3f;
import me.shedaniel.autoconfig.AutoConfig;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.client.config.ClientConfigCache;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.common.config.tweak.TweakType;
import mod.adrenix.nostalgic.util.client.GuiUtil;
import mod.adrenix.nostalgic.util.client.LinkUtil;
import mod.adrenix.nostalgic.util.common.LangUtil;
import mod.adrenix.nostalgic.util.common.ModUtil;
import mod.adrenix.nostalgic.util.client.NetUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
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

    protected static final int BUTTON_HEIGHT = 20;
    protected static final int DONE_BUTTON_TOP_OFFSET = 26;
    protected static final int LARGE_WIDTH = 204;
    protected static final int SMALL_WIDTH = 98;

    /* Fields */

    protected final Minecraft minecraft;
    private final Screen parent;
    private final ArrayList<Button> buttons = new ArrayList<>();
    private boolean isRedirected;

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
        this.buttons.add(new Button(0, 0, SMALL_WIDTH, BUTTON_HEIGHT, title, onPress));
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
        int startY = this.height / 4 + 24 - 8;
        int lastX = this.width / 2 + 4;
        boolean isNextLarge = false;
        Button prevButton = this.buttons.get(0);

        for (Button button : this.buttons)
        {
            button.x = (i % 2 == 0) ? lastX : startX;

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
                    button.x = startX;
                    prevButton.x = startX;
                    prevButton.setWidth(LARGE_WIDTH);
                }
                else
                    isNextLarge = true;

                button.setWidth(LARGE_WIDTH);
            }

            button.y = startY + (row * 24);
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
            Component.translatable(LangUtil.Cloth.CONFIG_TITLE), (button) ->
                this.minecraft.setScreen(new ConfigScreen(this))
        );

        // Config Presets (WIP)
        Button preset = new Button(0, 0, SMALL_WIDTH, BUTTON_HEIGHT, Component.translatable(LangUtil.Gui.SETTINGS_PRESETS), (button) -> {});
        preset.active = false;

        this.buttons.add(preset);

        // Support
        this.addButton(Component.translatable(LangUtil.Gui.SETTINGS_SUPPORT), LinkUtil.onPress(LinkUtil.KO_FI));

        // Discord
        this.addButton(Component.translatable(LangUtil.Gui.SETTINGS_DISCORD), LinkUtil.onPress(LinkUtil.DISCORD));

        // Golden Days Button
        this.addButton(Component.translatable(LangUtil.Gui.SETTINGS_GOLDEN_DAYS), LinkUtil.onPress(LinkUtil.GOLDEN_DAYS));

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
                case CUSTOM_SWING_MENU -> this.minecraft.setScreen(new SwingScreen(this));
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
     * Handler for when the screen closes.
     * All configuration values are saved to disk when this screen closes.
     */
    @Override
    public void onClose()
    {
        AutoConfig.getConfigHolder(ClientConfig.class).save();
        this.minecraft.setScreen(parent);
    }

    /* Rendering */

    /**
     * Renders the home settings screen.
     * @param poseStack The current pose stack.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        if (this.minecraft.level != null)
            this.fillGradient(poseStack, 0, 0, this.width, this.height, -1072689136, -804253680);
        else
            this.renderDirtBackground(0);

        this.renderScreenTitle(poseStack, this.height / 4 - 42);
        this.renderLogo(poseStack);

        if (NostalgicTweaks.isDebugging())
            this.renderDebug(poseStack);
        else
        {
            Component hint = Component.literal("Debug (Ctrl + Shift + D)").withStyle(ChatFormatting.DARK_GRAY);
            drawString(poseStack, this.font, hint, 2, this.height - 10, 0xFFFFFF);
        }

        super.render(poseStack, mouseX, mouseY, partialTick);
    }

    /**
     * Renders the screen's title while being centered.
     * @param poseStack The current pose stack.
     * @param height The height of the screen.
     */
    protected void renderScreenTitle(PoseStack poseStack, int height)
    {
        drawCenteredString(poseStack, this.font, this.title.getString(), this.width / 2, height, 0xFFFFFF);
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
     * @param poseStack The current pose stack.
     */
    private void renderDebug(PoseStack poseStack)
    {
        GuiUtil.CornerManager manager = new GuiUtil.CornerManager();

        drawCenteredString(poseStack, this.font, "Debug Mode (Ctrl + Shift + D)", this.width / 2, 5, 0xFFFF00);

        GuiUtil.drawText(poseStack, String.format("Loader: §d%s", NostalgicTweaks.isForge() ? "Forge" : "Fabric"), TweakType.Corner.TOP_LEFT, manager);
        GuiUtil.drawText(poseStack, String.format("Protocol: §b%s", NostalgicTweaks.PROTOCOL), TweakType.Corner.TOP_LEFT, manager);

        GuiUtil.drawText(poseStack, String.format("Singleplayer: %s", getColored(NetUtil.isSingleplayer())), TweakType.Corner.BOTTOM_LEFT, manager);
        GuiUtil.drawText(poseStack, String.format("Multiplayer: %s", getColored(NetUtil.isMultiplayer())), TweakType.Corner.BOTTOM_LEFT, manager);
        GuiUtil.drawText(poseStack, String.format("Connected: %s", getColored(NetUtil.isConnected())), TweakType.Corner.BOTTOM_LEFT, manager);
        GuiUtil.drawText(poseStack, String.format("Operator: %s", getColored(NetUtil.isPlayerOp())), TweakType.Corner.BOTTOM_LEFT, manager);
        GuiUtil.drawText(poseStack, String.format("Verified: %s", getColored(NostalgicTweaks.isNetworkVerified())), TweakType.Corner.BOTTOM_LEFT, manager);
        GuiUtil.drawText(poseStack, String.format("LAN: %s", getColored(NetUtil.isLocalHost())), TweakType.Corner.BOTTOM_LEFT, manager);

        GuiUtil.drawText(poseStack, String.format("Integration: %s", getColored(NetUtil.getIntegratedServer())), TweakType.Corner.BOTTOM_RIGHT, manager);
        GuiUtil.drawText(poseStack, String.format("Optifine: %s", getColored(NostalgicTweaks.OPTIFINE.get())), TweakType.Corner.TOP_RIGHT, manager);

        if (NostalgicTweaks.isFabric())
            GuiUtil.drawText(poseStack, String.format("Sodium: %s", getColored(NostalgicTweaks.isSodiumInstalled)), TweakType.Corner.TOP_RIGHT, manager);
    }

    /**
     * Renders the mod's logo to the home settings screen.
     * @param poseStack The current pose stack.
     */
    private void renderLogo(PoseStack poseStack)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, ModUtil.Resource.GEAR_LOGO);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        SettingsScreen.blit(poseStack, this.width / 2 - 16, this.height / 4 - 24, 0, 0, 32, 32, 32, 32);

        poseStack.pushPose();
        poseStack.translate((float) this.width / 2 + 10, (float) this.height / 4 + 5, 0.0);
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(-20.0F));

        String splash = "N.T";
        float scale = 1.8F - Mth.abs(Mth.sin((float) (Util.getMillis() % 1000L) / 1000.0F * ((float) Math.PI * 2)) * 0.1F);
        scale = scale * 13.0F / (float) (this.font.width(splash));

        poseStack.scale(scale, scale, scale);
        SettingsScreen.drawCenteredString(poseStack, this.font, splash, 0, -8, 0xFFFFFF00);
        poseStack.popPose();
    }
}