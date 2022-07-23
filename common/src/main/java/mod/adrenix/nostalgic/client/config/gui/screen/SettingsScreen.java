package mod.adrenix.nostalgic.client.config.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Vector3f;
import me.shedaniel.autoconfig.AutoConfig;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.client.config.ClientConfigCache;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.util.NostalgicLang;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import mod.adrenix.nostalgic.util.client.NetClientUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SettingsScreen extends Screen
{
    /* Widget Constants */

    protected static final int BUTTON_HEIGHT = 20;
    protected static final int DONE_BUTTON_TOP_OFFSET = 26;
    protected static final int LARGE_WIDTH = 204;
    protected static final int SMALL_WIDTH = 98;

    /* Instance Fields */

    protected final Minecraft minecraft;
    private final Screen parent;
    private final ArrayList<Button> buttons = new ArrayList<>();
    private boolean isRedirected;

    /* Constructors */

    public SettingsScreen(Screen parent, Component title, boolean isRedirected)
    {
        super(title);
        this.parent = parent;
        this.minecraft = Minecraft.getInstance();
        this.isRedirected = isRedirected;
    }

    public SettingsScreen(Screen parent, boolean isRedirected)
    {
        this(parent, Component.translatable(NostalgicLang.Gui.SETTINGS_TITLE), isRedirected);
    }

    /* Button Helpers */

    private void addButton(Component title, Button.OnPress onPress)
    {
        this.buttons.add(new Button(0, 0, SMALL_WIDTH, BUTTON_HEIGHT, title, onPress));
    }

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

    private Button.OnPress setLink(String url)
    {
        return (button -> this.minecraft.setScreen(new ConfirmLinkScreen((accepted) ->
        {
            if (accepted)
            {
                try { Util.getPlatform().openUrl(new URL(url)); }
                catch (MalformedURLException e) { e.printStackTrace(); }
            }
            this.minecraft.setScreen(this);
        }, url, true)));
    }

    /* Init & Closing */

    @Override
    protected void init()
    {
        // Clear Button List on Init
        this.buttons.clear();

        // General Settings
        this.addButton(Component.translatable(NostalgicLang.Cloth.CONFIG_TITLE), (button) ->
            this.minecraft.setScreen(new ConfigScreen(this))
        );

        // Config Presets (WIP)
        Button preset = new Button(0, 0, SMALL_WIDTH, BUTTON_HEIGHT, Component.translatable(NostalgicLang.Gui.SETTINGS_PRESETS), (button) -> {});
        preset.active = false;
        this.buttons.add(preset);

        // Support
        this.addButton(Component.translatable(NostalgicLang.Gui.SETTINGS_SUPPORT), this.setLink(NostalgicUtil.Link.KO_FI));

        // Discord
        this.addButton(Component.translatable(NostalgicLang.Gui.SETTINGS_DISCORD), this.setLink(NostalgicUtil.Link.DISCORD));

        // Golden Days Button
        this.addButton(Component.translatable(NostalgicLang.Gui.SETTINGS_GOLDEN_DAYS), this.setLink(NostalgicUtil.Link.GOLDEN_DAYS));

        // Done Button
        this.addButton(Component.translatable(NostalgicLang.Vanilla.GUI_DONE), (button) -> this.onClose());

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
                case CUSTOM_SWING_MENU -> this.minecraft.setScreen(new CustomizeScreen(this));
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (Screen.hasShiftDown() && Screen.hasControlDown() && keyCode == GLFW.GLFW_KEY_D)
            NostalgicTweaks.LOGGER.setDebug(!NostalgicTweaks.isDebugging());

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose()
    {
        AutoConfig.getConfigHolder(ClientConfig.class).save();
        this.minecraft.setScreen(parent);
    }

    /* Rendering */

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float ticks)
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

        super.render(poseStack, mouseX, mouseY, ticks);
    }

    protected void renderScreenTitle(PoseStack poseStack, int height)
    {
        drawCenteredString(poseStack, this.font, this.title.getString(), this.width / 2, height, 0xFFFFFF);
    }

    private String getColored(Object value)
    {
        if (value instanceof Boolean)
            return String.format("%s%s", (Boolean) value ? "§2" : "§4", value);
        return String.format("§f%s", value);
    }

    private void renderDebug(PoseStack poseStack)
    {
        drawCenteredString(poseStack, this.font, "Debug Mode (Ctrl + Shift + D)", this.width / 2, 5, 0xFFFF00);
        drawString(poseStack, this.font, String.format("LAN: %s", getColored(NetClientUtil.isLocalHost())), 2, height - 70, 0xFFFFFF);
        drawString(poseStack, this.font, String.format("Verified: %s", getColored(NostalgicTweaks.isNetworkVerified())), 2, height - 60, 0xFFFFFF);
        drawString(poseStack, this.font, String.format("Operator: %s", getColored(NetClientUtil.isPlayerOp())), 2, height - 50, 0xFFFFFF);
        drawString(poseStack, this.font, String.format("Connection: %s", getColored(NetClientUtil.isConnected())), 2, height - 40, 0xFFFFFF);
        drawString(poseStack, this.font, String.format("Multiplayer: %s", getColored(NetClientUtil.isMultiplayer())), 2, height - 30, 0xFFFFFF);
        drawString(poseStack, this.font, String.format("Integration: %s", getColored(NetClientUtil.getIntegratedServer())), 2, height - 20, 0xFFFFFF);
        drawString(poseStack, this.font, String.format("Singleplayer: %s", getColored(NetClientUtil.isSingleplayer())), 2, height - 10, 0xFFFFFF);
    }

    private void renderLogo(PoseStack poseStack)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, NostalgicUtil.Resource.GEAR_LOGO);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        SettingsScreen.blit(poseStack, this.width / 2 - 16, this.height / 4 - 24, 0, 0, 32, 32, 32, 32);

        poseStack.pushPose();
        poseStack.translate((float) this.width / 2 + 10, (float) this.height / 4 + 5, 0.0);
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(-20.0F));

        String splash = "N.T";
        float scale = 1.8F - Mth.abs(Mth.sin((float) (Util.getMillis() % 1000L) / 1000.0F * ((float) Math.PI * 2)) * 0.1F);
        scale = scale * 13.0F / (float) (this.font.width(splash));
        poseStack.scale(scale, scale, scale);

        SettingsScreen.drawCenteredString(poseStack, this.font, splash, 0, -8, 0xFFFF00 | Mth.ceil(255.0F) << 24);

        poseStack.popPose();
    }
}