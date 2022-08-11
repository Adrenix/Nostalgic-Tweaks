package mod.adrenix.nostalgic.client.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.client.config.gui.screen.SettingsScreen;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;
import mod.adrenix.nostalgic.mixin.widen.IMixinScreen;
import mod.adrenix.nostalgic.mixin.widen.IMixinTitleScreen;
import mod.adrenix.nostalgic.util.client.KeyUtil;
import mod.adrenix.nostalgic.util.NostalgicLang;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import mod.adrenix.nostalgic.util.client.ModClientUtil;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class NostalgicTitleScreen extends TitleScreen
{
    /* Public Fields */

    public static boolean isGameReady = false;

    /* Private Fields */

    private static final String[] MINECRAFT = {
        " *   * * *   * *** *** *** *** *** ***",
        " ** ** * **  * *   *   * * * * *    * ",
        " * * * * * * * **  *   **  *** **   * ",
        " *   * * *  ** *   *   * * * * *    * ",
        " *   * * *   * *** *** * * * * *    * "
    };

    private final boolean isEasterEgged;
    private long updateScreenDelay;
    private LogoEffectRandomizer[][] logoEffects;

    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final Identifier OVERLAY = new Identifier("textures/gui/title/background/panorama_overlay.png");
    private final RotatingCubeMapRenderer panorama = new RotatingCubeMapRenderer(TitleScreen.PANORAMA_CUBE_MAP);
    private final KeyBinding optionsKey = KeyUtil.find(NostalgicLang.Key.OPEN_CONFIG);
    private final RandomSource random = RandomSource.create();

    private final List<Drawable> alpha = new ArrayList<>();
    private final List<Drawable> beta = new ArrayList<>();
    private final List<Drawable> release = new ArrayList<>();

    /* Constructor */

    public NostalgicTitleScreen()
    {
        this.isEasterEgged = random.nextFloat() < 1.0E-4;
        this.updateScreenDelay = 0L;

        if (this.isEasterEgged)
            MINECRAFT[2] = " * * * * * * * *   **  **  *** **   * ";
        else
            MINECRAFT[2] = " * * * * * * * **  *   **  *** **   * ";
    }

    /* Overrides */

    @Override
    protected void init()
    {
        int x = this.width / 2 - 100;
        int y = this.height / 4 + 48;
        int rowHeight = 24;

        this.alpha.clear();
        this.beta.clear();
        this.release.clear();

        this.createAlphaOptions(x, y, rowHeight);
        this.createBetaOptions(x, y, rowHeight);
        this.createReleaseOptions(x, y, rowHeight);

        List<Drawable> widgets = switch (ModConfig.Candy.getButtonLayout())
        {
            case ALPHA -> this.alpha;
            case BETA -> this.beta;
            default -> this.release;
        };

        if (ModConfig.Candy.getButtonLayout() != TweakVersion.TitleLayout.MODERN)
            widgets.forEach((widget) -> super.addDrawableChild((ClickableWidget) widget));

        super.init();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (this.client == null)
            return false;
        else if (keyCode == GLFW.GLFW_KEY_M)
            this.client.setScreen(new NostalgicTitleScreen());
        else if (this.optionsKey != null && this.optionsKey.matchesKey(keyCode, scanCode))
            this.client.setScreen(new SettingsScreen(this, true));
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        if (ModConfig.Candy.oldTitleBackground())
            this.renderBackgroundTexture(0);
        else
        {
            this.panorama.render(partialTick, 1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, OVERLAY);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            TitleScreen.drawTexture(poseStack, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
        }

        if (this.updateScreenDelay == 0L)
            this.updateScreenDelay = Util.getMeasuringTimeMs();

        boolean isModern = ModConfig.Candy.getLoadingOverlay() == TweakVersion.Overlay.MODERN;
        boolean isDelayed = !NostalgicTitleScreen.isGameReady && Util.getMeasuringTimeMs() - this.updateScreenDelay < 1200;
        if (this.client == null || (isModern && isDelayed))
            return;

        if (ModConfig.Candy.oldAlphaLogo())
            this.renderClassicLogo(partialTick);
        else
        {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, NostalgicUtil.Resource.MINECRAFT_LOGO);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            int width = this.width / 2 - 137;
            int height = 30;

            if (ModConfig.Candy.oldLogoOutline())
            {
                if (this.isEasterEgged)
                {
                    this.drawTexture(poseStack, width, height, 0, 0, 99, 44);
                    this.drawTexture(poseStack, width + 99, height, 129, 0, 27, 44);
                    this.drawTexture(poseStack, width + 99 + 26, height, 126, 0, 3, 44);
                    this.drawTexture(poseStack, width + 99 + 26 + 3, height, 99, 0, 26, 44);
                    this.drawTexture(poseStack, width + 155, height, 0, 45, 155, 44);
                }
                else
                {
                    this.drawTexture(poseStack, width, height, 0, 0, 155, 44);
                    this.drawTexture(poseStack, width + 155, height, 0, 45, 155, 44);
                }
            }
            else
            {
                if (this.isEasterEgged)
                {
                    this.drawWithOutline(width, height, (x, y) -> {
                        this.drawTexture(poseStack, x, y, 0, 0, 99, 44);
                        this.drawTexture(poseStack, x + 99, y, 129, 0, 27, 44);
                        this.drawTexture(poseStack, x + 99 + 26, y, 126, 0, 3, 44);
                        this.drawTexture(poseStack, x + 99 + 26 + 3, y, 99, 0, 26, 44);
                        this.drawTexture(poseStack, x + 155, y, 0, 45, 155, 44);
                    });
                }
                else
                {
                    this.drawWithOutline(width, height, (x, y) -> {
                        this.drawTexture(poseStack, x, y, 0, 0, 155, 44);
                        this.drawTexture(poseStack, x + 155, y, 0, 45, 155, 44);
                    });
                }
            }
        }

        TweakVersion.TitleLayout layout = ModConfig.Candy.getButtonLayout();
        NostalgicTitleScreen.isGameReady = true;
        IMixinTitleScreen accessor = (IMixinTitleScreen) this;
        IMixinScreen screen = (IMixinScreen) this;
        int color = MathHelper.ceil(255.0F) << 24;

        if (accessor.NT$getSplash() != null)
        {
            poseStack.push();
            poseStack.translate((float) this.width / 2 + 90, 70.0, 0.0);
            poseStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-20.0F));

            float scale = 1.8F - MathHelper.abs(MathHelper.sin((float) (Util.getMeasuringTimeMs() % 1000L) / 1000.0F * ((float) Math.PI * 2)) * 0.1F);
            scale = scale * 100.0F / (float) (this.textRenderer.getWidth(accessor.NT$getSplash()) + 32);
            poseStack.scale(scale, scale, scale);

            TitleScreen.drawCenteredText(poseStack, this.textRenderer, accessor.NT$getSplash(), 0, -8, 0xFFFF00 | color);

            poseStack.pop();
        }

        String minecraft = ModConfig.Candy.getVersionText();
        Text copyright = switch (layout)
        {
            case ALPHA -> Component.translatable(NostalgicLang.Gui.CANDY_TITLE_COPYRIGHT_ALPHA);
            case BETA -> Component.translatable(NostalgicLang.Gui.CANDY_TITLE_COPYRIGHT_BETA);
            default -> COPYRIGHT_TEXT;
        };

        if (MinecraftClient.getModStatus().isModded() && !ModConfig.Candy.removeTitleModLoaderText())
            minecraft = minecraft + "/" + this.client.getVersionType() + I18n.translate("menu.modded");

        int versionColor = ModConfig.Candy.oldTitleBackground() && !minecraft.contains("§") ? 5263440 : 0xFFFFFF;
        int height = ModConfig.Candy.titleBottomLeftText() ? this.height - 10 : 2;

        TitleScreen.drawStringWithShadow(poseStack, this.textRenderer, minecraft, 2, height, versionColor);
        TitleScreen.drawTextWithShadow(poseStack, this.textRenderer, copyright, this.width - this.textRenderer.getWidth(copyright) - 2, this.height - 10, 0xFFFFFF);

        boolean isRelease = layout == TweakVersion.TitleLayout.RELEASE_TEXTURE_PACK || layout == TweakVersion.TitleLayout.RELEASE_NO_TEXTURE_PACK;

        setLayoutVisibility(screen.NT$getRenderables(), layout == TweakVersion.TitleLayout.MODERN);
        setLayoutVisibility(this.alpha, layout == TweakVersion.TitleLayout.ALPHA);
        setLayoutVisibility(this.beta, layout == TweakVersion.TitleLayout.BETA);
        setLayoutVisibility(this.release, isRelease);

        switch (layout)
        {
            case MODERN ->
            {
                for (Element child : this.children())
                {
                    if (child instanceof ClickableWidget)
                        ((ClickableWidget) child).setAlpha(1.0F);
                }

                this.setButtonVisibility();

                for (Drawable widget : screen.NT$getRenderables())
                    widget.render(poseStack, mouseX, mouseY, partialTick);

                if (accessor.NT$getRealmsNotificationsEnabled())
                    accessor.NT$getRealmsNotificationsScreen().render(poseStack, mouseX, mouseY, partialTick);
            }
            case ALPHA -> this.alpha.forEach(widget -> widget.render(poseStack, mouseX, mouseY, partialTick));
            case BETA -> this.beta.forEach(widget -> widget.render(poseStack, mouseX, mouseY, partialTick));
            default -> this.release.forEach(widget -> widget.render(poseStack, mouseX, mouseY, partialTick));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        return switch (ModConfig.Candy.getButtonLayout())
        {
            case ALPHA -> getClicked(this.alpha, mouseX, mouseY, button);
            case BETA -> getClicked(this.beta, mouseX, mouseY, button);
            case RELEASE_TEXTURE_PACK, RELEASE_NO_TEXTURE_PACK -> getClicked(this.release, mouseX, mouseY, button);
            case MODERN -> super.mouseClicked(mouseX, mouseY, button);
        };
    }

    /**
     * Methods
     */

    /* Button Layout */

    private void setButtonVisibility()
    {
        IMixinScreen screen = (IMixinScreen) this;
        for (Drawable widget : screen.NT$getRenderables())
        {
            if (widget instanceof TexturedButtonWidget && ((TexturedButtonWidget) widget).x == this.width / 2 - 124)
                ((TexturedButtonWidget) widget).visible = !ModConfig.Candy.removeLanguageButton();
            else if (widget instanceof TexturedButtonWidget && ((TexturedButtonWidget) widget).x == this.width / 2 + 104)
                ((TexturedButtonWidget) widget).visible = !ModConfig.Candy.removeAccessibilityButton();
            else if (widget instanceof ButtonWidget button)
            {
                boolean isRealms = button.getMessage().getString().equals(Text.translatable("menu.online").getString());
                boolean isRemovable = ModConfig.Candy.removeRealmsButton();
                ((ButtonWidget) widget).visible = !isRealms || !isRemovable;
            }
        }
    }

    private void setLayoutVisibility(List<Drawable> widgets, boolean visible)
    {
        for (Drawable widget : widgets)
        {
            if (widget instanceof ClickableWidget)
                ((ClickableWidget) widget).visible = visible;
        }
    }

    private boolean getClicked(List<Drawable> widgets, double mouseX, double mouseY, int button)
    {
        boolean isClicked = false;

        for (Drawable widget : widgets)
        {
            if (widget instanceof ClickableWidget)
                isClicked = ((ClickableWidget) widget).mouseClicked(mouseX, mouseY, button);

            if (isClicked)
                break;
        }

        return isClicked;
    }

    private void onSingleplayer(ButtonWidget ignored)
    {
        if (this.client != null)
            this.client.setScreen(new SelectWorldScreen(this));
    }

    private void onMultiplayer(ButtonWidget ignored)
    {
        if (this.client != null)
            this.client.setScreen(new MultiplayerScreen(this));
    }

    private void onOptions(ButtonWidget ignored)
    {
        if (this.client != null)
            this.client.setScreen(new OptionsScreen(this, this.client.options));
    }

    private void updatePackList(ResourcePackManager repository)
    {
        if (this.client == null)
            return;

        GameOptions options = this.client.options;
        ImmutableList<String> before = ImmutableList.copyOf(options.resourcePacks);

        options.resourcePacks.clear();
        options.incompatibleResourcePacks.clear();

        for (ResourcePackProfile pack : repository.getEnabledProfiles())
        {
            if (pack.isPinned())
                continue;

            options.resourcePacks.add(pack.getName());

            if (pack.getCompatibility().isCompatible())
                continue;

            options.incompatibleResourcePacks.add(pack.getName());
        }

        options.write();

        ImmutableList<String> after = ImmutableList.copyOf(options.resourcePacks);
        if (!after.equals(before))
            this.client.reloadResources();
    }

    private void onMods(ButtonWidget ignored)
    {
        if (this.client != null && ModClientUtil.Gui.modScreen != null)
            this.client.setScreen(ModClientUtil.Gui.modScreen.apply(this.client.currentScreen));
    }

    private void onResources(ButtonWidget ignored)
    {
        if (this.client != null)
            this.client.setScreen(new PackScreen(this, this.client.getResourcePackManager(), this::updatePackList, this.client.getResourcePackDir(), Text.translatable("resourcePack.title")));
    }

    private void createAlphaOptions(int x, int y, int rowHeight)
    {
        int row = 1;

        // Singleplayer
        this.alpha.add(new ButtonWidget(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, Text.translatable(NostalgicLang.Vanilla.MENU_SINGLEPLAYER), this::onSingleplayer));

        // Multiplayer
        this.alpha.add(new ButtonWidget(x, y + rowHeight, BUTTON_WIDTH, BUTTON_HEIGHT, Text.translatable(NostalgicLang.Vanilla.MENU_MULTIPLAYER), this::onMultiplayer));

        // Mods
        if (ModConfig.Candy.includeModsOnTitle() && ModClientUtil.Gui.modScreen != null)
            this.alpha.add(new ButtonWidget(x, y + rowHeight * ++row, BUTTON_WIDTH, BUTTON_HEIGHT, Text.translatable(NostalgicLang.Gui.CANDY_TITLE_MODS), this::onMods));

        // Tutorial
        ButtonWidget tutorial = new ButtonWidget(x, y + rowHeight * ++row, BUTTON_WIDTH, BUTTON_HEIGHT, Text.empty(), (button) -> {});
        tutorial.active = false;
        tutorial.setMessage(Text.translatable(NostalgicLang.Gui.CANDY_TITLE_TUTORIAL).withStyle(Formatting.GRAY));

        this.alpha.add(tutorial);

        // Options
        this.alpha.add(new ButtonWidget(x, y + rowHeight * (++row + 1) - 12, BUTTON_WIDTH, BUTTON_HEIGHT, Text.translatable(NostalgicLang.Vanilla.MENU_OPTIONS), this::onOptions));
    }

    private void createBetaOptions(int x, int y, int rowHeight)
    {
        int row = 1;

        // Singleplayer
        this.beta.add(new ButtonWidget(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, Text.translatable(NostalgicLang.Vanilla.MENU_SINGLEPLAYER), this::onSingleplayer));

        // Multiplayer
        this.beta.add(new ButtonWidget(x, y + rowHeight, BUTTON_WIDTH, BUTTON_HEIGHT, Text.translatable(NostalgicLang.Vanilla.MENU_MULTIPLAYER), this::onMultiplayer));

        // Mods & Texture Packs
        boolean isMods = ModConfig.Candy.includeModsOnTitle() && ModClientUtil.Gui.modScreen != null;
        if (isMods)
            this.beta.add(new ButtonWidget(x, y + rowHeight * ++row, BUTTON_WIDTH, BUTTON_HEIGHT, Text.translatable(NostalgicLang.Gui.CANDY_TITLE_MODS), this::onMods));

        this.beta.add(new ButtonWidget(x, y + rowHeight * ++row, BUTTON_WIDTH, BUTTON_HEIGHT, Text.translatable(isMods ? NostalgicLang.Gui.CANDY_TITLE_TEXTURE_PACK : NostalgicLang.Gui.CANDY_TITLE_MODS_TEXTURE), this::onResources));

        // Options
        this.beta.add(new ButtonWidget(x, y + rowHeight * ++row, BUTTON_WIDTH, BUTTON_HEIGHT, Text.translatable(NostalgicLang.Vanilla.MENU_OPTIONS), this::onOptions));
    }

    private void createReleaseOptions(int x, int y, int rowHeight)
    {
        int row = 1;

        // Singleplayer
        this.release.add(new ButtonWidget(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, Text.translatable(NostalgicLang.Vanilla.MENU_SINGLEPLAYER), this::onSingleplayer));

        // Multiplayer
        this.release.add(new ButtonWidget(x, y + rowHeight, BUTTON_WIDTH, BUTTON_HEIGHT, Text.translatable(NostalgicLang.Vanilla.MENU_MULTIPLAYER), this::onMultiplayer));

        // Mods
        boolean isMods = ModConfig.Candy.includeModsOnTitle() && ModClientUtil.Gui.modScreen != null;
        if (isMods)
            this.release.add(new ButtonWidget(x, y + rowHeight * ++row, BUTTON_WIDTH, BUTTON_HEIGHT, Text.translatable(NostalgicLang.Gui.CANDY_TITLE_MODS), this::onMods));

        // Texture Packs
        if (ModConfig.Candy.getButtonLayout() == TweakVersion.TitleLayout.RELEASE_TEXTURE_PACK)
            this.release.add(new ButtonWidget(x, y + rowHeight * ++row, BUTTON_WIDTH, BUTTON_HEIGHT, Text.translatable(NostalgicLang.Gui.CANDY_TITLE_TEXTURE_PACK), this::onResources));

        int lastRow = (this.height / 4 + 48) + 72 + 12;
        if (this.release.size() == 4)
            lastRow += 24;

        // Language
        if (this.client != null && !ModConfig.Candy.removeLanguageButton())
            this.release.add(new TexturedButtonWidget(this.width / 2 - 124, lastRow, 20, 20, 0, 106, 20, ButtonWidget.WIDGETS_TEXTURE, 256, 256, button -> this.minecraft.setScreen(new LanguageSelectScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())), Text.translatable("narrator.button.language")));

        // Options
        this.release.add(new ButtonWidget(this.width / 2 - 100, lastRow, 98, 20, Text.translatable(NostalgicLang.Vanilla.MENU_OPTIONS), button -> this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options))));

        // Quit
        this.release.add(new ButtonWidget(this.width / 2 + 2, lastRow, 98, 20, Text.translatable(NostalgicLang.Vanilla.MENU_QUIT), button -> this.minecraft.stop()));
    }

    /* Classic Logo */

    private void renderClassicLogo(float partialTick)
    {
        if (this.client == null)
            return;

        if (this.logoEffects == null)
        {
            this.logoEffects = new LogoEffectRandomizer[MINECRAFT[0].length()][MINECRAFT.length];

            for (int x = 0; x < this.logoEffects.length; x++)
                for (int y = 0; y < this.logoEffects[x].length; y++)
                    logoEffects[x][y] = new LogoEffectRandomizer(x, y);
        }

        for (LogoEffectRandomizer[] logoEffect : this.logoEffects)
            for (LogoEffectRandomizer logoEffectRandomizer : logoEffect)
                logoEffectRandomizer.update(partialTick);

        Window window = this.client.getWindow();
        int scaleHeight = (int) (120 * window.getScaleFactor());

        RenderSystem.setProjectionMatrix(Matrix4f.viewboxMatrix(70.0D, window.getFramebufferWidth() / (float) scaleHeight, 0.05F, 100.0F));
        RenderSystem.viewport(0, window.getFramebufferHeight() - scaleHeight, window.getFramebufferWidth(), scaleHeight);

        MatrixStack model = RenderSystem.getModelViewStack();
        model.translate(-0.05F, 1.0F, 1987.0F);
        model.scale(1.59F, 1.59F, 1.59F);

        BakedModel stone = this.itemRenderer.getModels().getModel(Blocks.STONE.asItem());

        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.depthMask(true);

        for (int pass = 0; pass < 3; pass++)
        {
            model.push();

            if (pass == 0)
            {
                RenderSystem.clear(256, MinecraftClient.IS_SYSTEM_MAC);
                model.translate(0.0F, -0.4F, 0.0F);
                model.scale(0.98F, 1.0F, 1.0F);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
            }

            if (pass == 1)
            {
                RenderSystem.disableBlend();
                RenderSystem.clear(256, MinecraftClient.IS_SYSTEM_MAC);
            }

            if (pass == 2)
            {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(768, 1);
            }

            model.scale(1.0F, -1.0F, 1.0F);
            model.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(15.0F));
            model.scale(0.89F, 1.0F, 0.4F);
            model.translate((float) (-MINECRAFT[0].length()) * 0.5F, (float) (-MINECRAFT.length) * 0.5F, 0.0F);

            if (pass == 0)
            {
                RenderSystem.setShader(GameRenderer::getRenderTypeCutoutShader);
                RenderSystem.setShaderTexture(0, NostalgicUtil.Resource.BLACK_RESOURCE);
            }
            else
            {
                RenderSystem.setShader(GameRenderer::getBlockShader);
                RenderSystem.setShaderTexture(0, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
            }

            for (int y = 0; y < MINECRAFT.length; y++)
            {
                for (int x = 0; x < MINECRAFT[y].length(); x++)
                {
                    if (MINECRAFT[y].charAt(x) == ' ')
                        continue;

                    model.push();

                    float z = logoEffects[x][y].position;
                    float scale = 1.0F;
                    float alpha = 1.0F;

                    if (pass == 0)
                    {
                        scale = z * 0.04F + 1.0F;
                        alpha = 1.0F / scale;
                        z = 0.0F;
                    }

                    model.translate(x, y, z);
                    model.scale(scale, scale, scale);
                    renderBlock(model, stone, pass, alpha);
                    model.pop();
                }
            }

            model.pop();
        }

        RenderSystem.disableBlend();
        RenderSystem.setProjectionMatrix(Matrix4f.projectionMatrix(0.0F, (float) window.getScaledWidth(), 0.0F, (float) window.getScaledHeight(), 1000.0F, 3000.0F));
        RenderSystem.viewport(0, 0, window.getFramebufferWidth(), window.getFramebufferHeight());
        model.loadIdentity();
        model.translate(0, 0, -2000);
        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableCull();
    }

    private int getColorFromRGBA(float red, float green, float blue, float alpha)
    {
        return (int) (alpha * 255.0F) << 24 | (int) (red * 255.0F) << 16 | (int) (green * 255.0F) << 8 | (int) (blue * 255.0F);
    }

    private int getColorFromBrightness(float brightness, float alpha)
    {
        return this.getColorFromRGBA(brightness, brightness, brightness, alpha);
    }

    private void renderQuad(MatrixStack.Entry modelPose, BufferBuilder builder, BakedQuad quad, float brightness, float alpha)
    {
        int combinedLight = this.getColorFromBrightness(brightness, alpha);
        int[] vertices = quad.getVertexData();
        Vec3i vec = quad.getFace().getVector();
        Vec3f vec3f = new Vec3f(vec.getX(), vec.getY(), vec.getZ());
        Matrix4f matrix = modelPose.getPositionMatrix();
        vec3f.transform(modelPose.getNormalMatrix());

        try (MemoryStack memoryStack = MemoryStack.stackPush())
        {
            ByteBuffer byteBuffer = memoryStack.malloc(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.getVertexSize());
            IntBuffer intBuffer = byteBuffer.asIntBuffer();

            for (int i = 0; i < vertices.length / 8; i++)
            {
                intBuffer.clear();
                intBuffer.put(vertices, i * 8, 8);
                float x = byteBuffer.getFloat(0);
                float y = byteBuffer.getFloat(4);
                float z = byteBuffer.getFloat(8);

                Vector4f vec4f = new Vector4f(x, y, z, 1.0F);
                vec4f.transform(matrix);

                builder.vertex(vec4f.getX(), vec4f.getY(), vec4f.getZ(), 1.0F, 1.0F, 1.0F, alpha, byteBuffer.getFloat(16), byteBuffer.getFloat(20), OverlayTexture.DEFAULT_UV, combinedLight, vec3f.getX(), vec3f.getY(), vec3f.getZ());
            }
        }
    }

    private void renderBlock(MatrixStack modelView, BakedModel stone, int pass, float alpha)
    {
        Tessellator tesselator = Tessellator.getInstance();
        BufferBuilder builder = tesselator.getBuffer();
        builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);

        for (Direction direction : Direction.values())
        {
            float brightness = switch (direction)
            {
                case DOWN -> 1.0F;
                case UP -> 0.5F;
                case NORTH -> 0.0F;
                case SOUTH -> 0.8F;
                case WEST, EAST -> 0.6F;
            };

            int color = this.getColorFromBrightness(brightness, alpha);

            for (BakedQuad quad : stone.getQuads(null, direction, random))
            {
                if (pass == 0)
                    renderQuad(modelView.peek(), builder, quad, brightness, alpha);
                else
                    builder.quad(modelView.peek(), quad, brightness, brightness, brightness, color, OverlayTexture.DEFAULT_UV);
            }
        }

        tesselator.draw();
    }

    /* Logo Effect Randomizer */

    private static class LogoEffectRandomizer
    {
        public float position;
        public float speed;

        public LogoEffectRandomizer(int x, int y)
        {
            this.position = (10 + y) + RandomSource.create().nextFloat() * 32.0F + x;
        }

        public void update(float partialTick)
        {
            if (this.position > 0.0F)
                this.speed -= 0.4F;

            this.position += this.speed * partialTick;
            this.speed *= 0.9F;

            if (this.position < 0.0F)
            {
                this.position = 0.0F;
                this.speed = 0.0F;
            }
        }
    }
}
