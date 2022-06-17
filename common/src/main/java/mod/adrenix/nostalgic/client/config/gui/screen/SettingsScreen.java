package mod.adrenix.nostalgic.client.config.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Vector3f;
import me.shedaniel.autoconfig.AutoConfig;
import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.client.config.CommonRegistry;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.util.NostalgicLang;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SettingsScreen extends Screen
{
    /* Screen Option Enum */

    public enum OptionScreen
    {
        MAIN_MENU(NostalgicLang.Gui.GENERAL_CONFIG_SCREEN_MAIN),
        SETTINGS_MENU(NostalgicLang.Gui.GENERAL_CONFIG_SCREEN_SETTINGS),
        CUSTOM_SWING_MENU(NostalgicLang.Gui.GENERAL_CONFIG_SCREEN_CUSTOM);

        OptionScreen(String langKey) { this.langKey = langKey; }

        private final String langKey;
        public String getLangKey() { return this.langKey; }

        public static Component getTranslation(OptionScreen screen) { return new TranslatableComponent(screen.getLangKey()); }

        @Override
        public String toString() { return NostalgicUtil.Text.toTitleCase(super.toString()); }
    }

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

    public SettingsScreen(Screen parent, TranslatableComponent title, boolean isRedirected)
    {
        super(title);
        this.parent = parent;
        this.minecraft = Minecraft.getInstance();
        this.isRedirected = isRedirected;
    }

    public SettingsScreen(Screen parent, boolean isRedirected)
    {
        this(parent, new TranslatableComponent(NostalgicLang.Gui.SETTINGS_TITLE), isRedirected);
    }

    /* Button Helpers */

    private void addButton(TranslatableComponent title, Button.OnPress onPress)
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
        this.addButton(new TranslatableComponent(NostalgicLang.Cloth.CONFIG_TITLE), (button) ->
            this.minecraft.setScreen(new ConfigScreen(this))
        );

        // Custom Swing Speeds
        this.addButton(new TranslatableComponent(NostalgicLang.Gui.CUSTOMIZE), (button) ->
            this.minecraft.setScreen(new CustomizeScreen(this))
        );

        // Support
        this.addButton(new TranslatableComponent(NostalgicLang.Gui.SETTINGS_SUPPORT), this.setLink(NostalgicUtil.Link.KO_FI));

        // Discord
        this.addButton(new TranslatableComponent(NostalgicLang.Gui.SETTINGS_DISCORD), this.setLink(NostalgicUtil.Link.DISCORD));

        // Golden Days Button
        this.addButton(new TranslatableComponent(NostalgicLang.Gui.SETTINGS_GOLDEN_DAYS), this.setLink(NostalgicUtil.Link.GOLDEN_DAYS));

        // Done Button
        this.addButton(new TranslatableComponent(NostalgicLang.Vanilla.GUI_DONE), (button) -> this.onClose());

        // Setup Buttons
        this.setupButtons();

        // Add Buttons as Widgets
        for (Button button : this.buttons)
            this.addRenderableWidget(button);

        // Jump Screen
        if (this.isRedirected)
        {
            this.isRedirected = false;
            switch (CommonRegistry.getGui().defaultScreen)
            {
                case SETTINGS_MENU -> this.minecraft.setScreen(new ConfigScreen(this));
                case CUSTOM_SWING_MENU -> this.minecraft.setScreen(new CustomizeScreen(this));
            }
        }
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

        super.render(poseStack, mouseX, mouseY, ticks);
    }

    protected void renderScreenTitle(PoseStack poseStack, int height)
    {
        drawCenteredString(poseStack, this.font, this.title.getString(), this.width / 2, height, 0xFFFFFF);
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