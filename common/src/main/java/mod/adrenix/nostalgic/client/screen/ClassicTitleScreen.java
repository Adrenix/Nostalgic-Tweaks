package mod.adrenix.nostalgic.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import mod.adrenix.nostalgic.client.config.DefaultConfig;
import mod.adrenix.nostalgic.client.config.MixinConfig;
import mod.adrenix.nostalgic.client.config.gui.screen.SettingsScreen;
import mod.adrenix.nostalgic.mixin.widen.IMixinOptionsScreen;
import mod.adrenix.nostalgic.mixin.widen.IMixinScreen;
import mod.adrenix.nostalgic.mixin.widen.IMixinTitleScreen;
import mod.adrenix.nostalgic.util.KeyUtil;
import mod.adrenix.nostalgic.util.NostalgicLang;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Blocks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClassicTitleScreen extends TitleScreen
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

    private final float updateCounter;
    private long updateScreenDelay;
    private LogoEffectRandomizer[][] logoEffects;

    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final Random RANDOM = new Random();
    private static final ResourceLocation OVERLAY = new ResourceLocation("textures/gui/title/background/panorama_overlay.png");
    private final PanoramaRenderer panorama = new PanoramaRenderer(TitleScreen.CUBE_MAP);
    private final KeyMapping options = KeyUtil.find(NostalgicLang.Key.OPEN_CONFIG);

    private final List<Widget> alpha = new ArrayList<>();
    private final List<Widget> beta = new ArrayList<>();

    /* Constructor */

    public ClassicTitleScreen()
    {
        this.updateCounter = this.getRand().nextFloat();
        this.updateScreenDelay = 0L;
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

        this.createAlphaOptions(x, y, rowHeight);
        this.createBetaOptions(x, y, rowHeight);

        super.init();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (this.minecraft == null)
            return false;
        else if (keyCode == GLFW.GLFW_KEY_M)
            this.minecraft.setScreen(new ClassicTitleScreen());
        else if (this.options != null && this.options.matches(keyCode, scanCode))
            this.minecraft.setScreen(new SettingsScreen(this, false));
        return true;
    }

    @Override
    public void tick()
    {
        if (this.logoEffects != null)
            for (LogoEffectRandomizer[] logoEffect : this.logoEffects)
                for (LogoEffectRandomizer logoEffectRandomizer : logoEffect)
                    logoEffectRandomizer.run();
        super.tick();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        if (MixinConfig.Candy.oldTitleBackground())
            this.renderDirtBackground(0);
        else
        {
            this.panorama.render(partialTick, 1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, OVERLAY);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            TitleScreen.blit(poseStack, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
        }

        if (this.updateScreenDelay == 0L)
            this.updateScreenDelay = Util.getMillis();

        if (this.minecraft == null || !ClassicTitleScreen.isGameReady && Util.getMillis() - this.updateScreenDelay < 1200)
            return;

        if (MixinConfig.Candy.oldAlphaLogo())
            this.renderClassicLogo(partialTick);
        else
        {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, NostalgicUtil.Resource.MINECRAFT_LOGO);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            int width = this.width / 2 - 137;
            int height = 30;

            if (MixinConfig.Candy.oldLogoOutline())
            {
                this.blit(poseStack, width, height, 0, 0, 155, 44);
                this.blit(poseStack, width + 155, height, 0, 45, 155, 44);
            }
            else
            {
                this.blitOutlineBlack(width, height, (x, y) -> {
                    this.blit(poseStack, x, y, 0, 0, 155, 44);
                    this.blit(poseStack, x + 155, y, 0, 45, 155, 44);
                });
            }
        }

        DefaultConfig.VERSION layout = MixinConfig.Candy.getButtonLayout();
        ClassicTitleScreen.isGameReady = true;
        IMixinTitleScreen accessor = (IMixinTitleScreen) this;
        IMixinScreen screen = (IMixinScreen) this;
        int color = Mth.ceil(255.0F) << 24;

        if (accessor.getSplash() != null)
        {
            poseStack.pushPose();
            poseStack.translate((float) this.width / 2 + 90, 70.0, 0.0);
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(-20.0F));

            float scale = 1.8F - Mth.abs(Mth.sin((float) (Util.getMillis() % 1000L) / 1000.0F * ((float) Math.PI * 2)) * 0.1F);
            scale = scale * 100.0F / (float) (this.font.width(accessor.getSplash()) + 32);
            poseStack.scale(scale, scale, scale);

            TitleScreen.drawCenteredString(poseStack, this.font, accessor.getSplash(), 0, -8, 0xFFFF00 | color);

            poseStack.popPose();
        }

        String minecraft = MixinConfig.Candy.getVersionText();
        Component copyright = switch (layout)
        {
            case MODERN -> COPYRIGHT_TEXT;
            case ALPHA -> new TranslatableComponent(NostalgicLang.Gui.CANDY_TITLE_COPYRIGHT_ALPHA);
            case BETA -> new TranslatableComponent(NostalgicLang.Gui.CANDY_TITLE_COPYRIGHT_BETA);
        };

        if (Minecraft.checkModStatus().shouldReportAsModified() && !MixinConfig.Candy.removeTitleModLoaderText())
            minecraft = minecraft + "/" + this.minecraft.getVersionType() + I18n.get("menu.modded");

        int versionColor = MixinConfig.Candy.oldTitleBackground() && !minecraft.contains("§") ? 5263440 : 0xFFFFFF;
        int height = MixinConfig.Candy.titleBottomLeftText() ? this.height - 10 : 2;

        TitleScreen.drawString(poseStack, this.font, minecraft, 2, height, versionColor);
        TitleScreen.drawString(poseStack, this.font, copyright, this.width - this.font.width(copyright) - 2, this.height - 10, 0xFFFFFF);

        setVisibility(this.alpha, layout == DefaultConfig.VERSION.ALPHA);
        setVisibility(this.beta, layout == DefaultConfig.VERSION.BETA);
        setVisibility(screen.getRenderables(), layout == DefaultConfig.VERSION.MODERN);

        switch (layout)
        {
            case MODERN ->
            {
                for (GuiEventListener child : this.children())
                {
                    if (child instanceof AbstractWidget)
                        ((AbstractWidget) child).setAlpha(1.0F);
                }

                for (Widget widget : screen.getRenderables())
                    widget.render(poseStack, mouseX, mouseY, partialTick);

                if (accessor.getRealmsNotificationsEnabled())
                    accessor.getRealmsNotificationsScreen().render(poseStack, mouseX, mouseY, partialTick);
            }
            case ALPHA -> this.alpha.forEach(widget -> widget.render(poseStack, mouseX, mouseY, partialTick));
            case BETA -> this.beta.forEach(widget -> widget.render(poseStack, mouseX, mouseY, partialTick));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        return switch (MixinConfig.Candy.getButtonLayout())
        {
            case ALPHA -> getClicked(this.alpha, mouseX, mouseY, button);
            case BETA -> getClicked(this.beta, mouseX, mouseY, button);
            case MODERN -> super.mouseClicked(mouseX, mouseY, button);
        };
    }

    /* Methods */

    private void setVisibility(List<Widget> widgets, boolean visible)
    {
        for (Widget widget : widgets)
        {
            if (widget instanceof AbstractWidget)
                ((AbstractWidget) widget).visible = visible;
        }
    }

    private boolean getClicked(List<Widget> widgets, double mouseX, double mouseY, int button)
    {
        boolean isClicked = false;

        for (Widget widget : widgets)
        {
            if (widget instanceof AbstractWidget)
                isClicked = ((AbstractWidget) widget).mouseClicked(mouseX, mouseY, button);
            if (isClicked)
                break;
        }

        return isClicked;
    }

    private void onSingleplayer(Button ignored)
    {
        if (this.minecraft != null)
            this.minecraft.setScreen(new SelectWorldScreen(this));
    }

    private void onMultiplayer(Button ignored)
    {
        if (this.minecraft != null)
            this.minecraft.setScreen(new JoinMultiplayerScreen(this));
    }

    private void onOptions(Button ignored)
    {
        if (this.minecraft != null)
            this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
    }

    private void onMods(Button ignored)
    {
        if (this.minecraft != null)
        {
            IMixinOptionsScreen accessor = (IMixinOptionsScreen) new OptionsScreen(this, this.minecraft.options);
            this.minecraft.setScreen(new PackSelectionScreen(this, this.minecraft.getResourcePackRepository(), accessor::invokeUpdatePackList, this.minecraft.getResourcePackDirectory(), new TranslatableComponent("resourcePack.title")));
        }
    }

    private void createAlphaOptions(int x, int y, int rowHeight)
    {
        // Singleplayer
        this.alpha.add(new Button(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, new TranslatableComponent(NostalgicLang.Vanilla.MENU_SINGLEPLAYER), this::onSingleplayer));

        // Multiplayer
        this.alpha.add(new Button(x, y + rowHeight, BUTTON_WIDTH, BUTTON_HEIGHT, new TranslatableComponent(NostalgicLang.Vanilla.MENU_MULTIPLAYER), this::onMultiplayer));

        // Tutorial
        Button tutorial = new Button(x, y + rowHeight * 2, BUTTON_WIDTH, BUTTON_HEIGHT, TextComponent.EMPTY, (button) -> {});
        tutorial.active = false;
        tutorial.setMessage(new TranslatableComponent(NostalgicLang.Gui.CANDY_TITLE_TUTORIAL).withStyle(ChatFormatting.GRAY));

        this.alpha.add(tutorial);

        // Options
        this.alpha.add(new Button(x, y + rowHeight * 4 - 12, BUTTON_WIDTH, BUTTON_HEIGHT, new TranslatableComponent(NostalgicLang.Vanilla.MENU_OPTIONS), this::onOptions));
    }

    private void createBetaOptions(int x, int y, int rowHeight)
    {
        // Singleplayer
        this.beta.add(new Button(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, new TranslatableComponent(NostalgicLang.Vanilla.MENU_SINGLEPLAYER), this::onSingleplayer));

        // Multiplayer
        this.beta.add(new Button(x, y + rowHeight, BUTTON_WIDTH, BUTTON_HEIGHT, new TranslatableComponent(NostalgicLang.Vanilla.MENU_MULTIPLAYER), this::onMultiplayer));

        // Mods & Texture Packs
        this.beta.add(new Button(x, y + rowHeight * 2, BUTTON_WIDTH, BUTTON_HEIGHT, new TranslatableComponent(NostalgicLang.Gui.CANDY_TITLE_MODS), this::onMods));

        // Options
        this.beta.add(new Button(x, y + rowHeight * 3, BUTTON_WIDTH, BUTTON_HEIGHT, new TranslatableComponent(NostalgicLang.Vanilla.MENU_OPTIONS), this::onOptions));
    }

    private void renderClassicLogo(float partialTick)
    {
        if (this.minecraft == null) return;
        if (this.logoEffects == null)
        {
            this.logoEffects = new LogoEffectRandomizer[MINECRAFT[0].length()][MINECRAFT.length];
            for (int horizontal = 0; horizontal < this.logoEffects.length; horizontal++)
                for (int vertical = 0; vertical < this.logoEffects[horizontal].length; vertical++)
                    logoEffects[horizontal][vertical] = new LogoEffectRandomizer(this, horizontal, vertical);
        }

        RenderSystem.enableDepthTest();

        Window window = this.minecraft.getWindow();
        int scaleHeight = (int) (120 * window.getGuiScale());

        RenderSystem.setProjectionMatrix(Matrix4f.perspective(70, (float) window.getWidth() / (float) scaleHeight, 0.05F, 100F));
        RenderSystem.viewport(0, window.getHeight() - scaleHeight, window.getWidth(), scaleHeight);

        PoseStack model = RenderSystem.getModelViewStack();
        model.translate(0.0, 0.0, 2001.0);

        RenderSystem.applyModelViewMatrix();
        RenderSystem.disableCull();
        RenderSystem.depthMask(true);

        for (int row = 0; row < 3; row++)
        {
            model.pushPose();
            model.translate(0.4F, 0.6F, -13F);

            if (row == 0)
            {
                RenderSystem.clear(256, Minecraft.ON_OSX);
                model.translate(0.0F, -0.4F, 0.0F);
                model.scale(0.98F, 1.0F, 1.0F);
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(770, 771);
            }

            if (row == 1)
            {
                RenderSystem.disableBlend();
                RenderSystem.clear(256, Minecraft.ON_OSX);
            }

            if (row == 2)
            {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(768, 1);
            }

            model.scale(1.0F, -1.0F, 1.0F);
            model.mulPose(Vector3f.XP.rotationDegrees(15.0F));
            model.scale(0.89F, 1.0F, 0.4F);
            model.translate((float) (-MINECRAFT[0].length()) * 0.5F, (float) (-MINECRAFT.length) * 0.5F, 0.0F);

            if (row == 0)
            {
                RenderSystem.setShader(GameRenderer::getRendertypeCutoutShader);
                RenderSystem.setShaderTexture(0, NostalgicUtil.Resource.BLACK_RESOURCE);
            }
            else
            {
                RenderSystem.setShader(GameRenderer::getBlockShader);
                RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
            }

            for (int horizontal = 0; horizontal < MINECRAFT.length; horizontal++)
            {
                for (int vertical = 0; vertical < MINECRAFT[horizontal].length(); vertical++)
                {
                    char symbol = MINECRAFT[horizontal].charAt(vertical);
                    if (horizontal == 2 && ((double) this.updateCounter < 0.0001D))
                        symbol = MINECRAFT[horizontal].charAt(vertical == 20 ? vertical - 1 : (vertical == 16 ? vertical + 1 : vertical));

                    if (symbol == ' ')
                        continue;

                    model.pushPose();

                    LogoEffectRandomizer logo = logoEffects[vertical][horizontal];

                    float depth = (float) (logo.vertical + (logo.horizontal - logo.vertical) * (double) partialTick);
                    float scale = 1.0F;
                    float alpha = 1.0F;

                    if (row == 0)
                    {
                        scale = depth * 0.04F + 1.0F;
                        alpha = 1.0F / scale;
                        depth = 0.0F;
                    }

                    model.translate(vertical, horizontal, depth);
                    model.scale(scale, scale, scale);
                    renderBlock(model, row, alpha);
                    model.popPose();
                }
            }

            model.popPose();
        }

        RenderSystem.disableBlend();
        RenderSystem.setProjectionMatrix(Matrix4f.orthographic(0F, (float) ((double) window.getWidth() / window.getGuiScale()), 0F, (float) ((double) window.getHeight() / window.getGuiScale()), 1000F, 3000F));
        RenderSystem.viewport(0, 0, window.getWidth(), window.getHeight());
        model.setIdentity();
        model.translate(0, 0, -2000);
        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableCull();
    }

    private Random getRand()
    {
        return RANDOM;
    }

    private static int getColorFromRGBA(float red, float green, float blue, float alpha)
    {
        return (int) (alpha * 255.0F) << 24 | (int) (red * 255.0F) << 16 | (int) (green * 255.0F) << 8 | (int) (blue * 255.0F);
    }

    private static int getColorFromBrightness(float brightness, float alpha)
    {
        return getColorFromRGBA(brightness, brightness, brightness, alpha);
    }

    private void renderQuad(PoseStack.Pose pose, BufferBuilder bufferbuilder, BakedQuad quad, float brightness, float alpha)
    {
        int combinedLight = getColorFromBrightness(brightness, alpha);
        int[] vertices = quad.getVertices();
        Vec3i vec = quad.getDirection().getNormal();
        Vector3f vec3f = new Vector3f(vec.getX(), vec.getY(), vec.getZ());
        Matrix4f matrix = pose.pose();
        vec3f.transform(pose.normal());

        try (MemoryStack memoryStack = MemoryStack.stackPush())
        {
            ByteBuffer byteBuffer = memoryStack.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
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

                bufferbuilder.vertex(vec4f.x(), vec4f.y(), vec4f.z(), 1.0F, 1.0F, 1.0F, alpha, byteBuffer.getFloat(16), byteBuffer.getFloat(20), OverlayTexture.NO_OVERLAY, combinedLight, vec3f.x(), vec3f.y(), vec3f.z());
            }
        }
    }

    private void renderBlock(PoseStack modelView, int row, float alpha)
    {
        modelView.translate(-0.5F, -0.5F, -0.5F);
        BakedModel model = this.itemRenderer.getItemModelShaper().getItemModel(Blocks.STONE.asItem().getDefaultInstance());

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);

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

            int color = getColorFromBrightness(brightness, alpha);

            for (BakedQuad quad : model.getQuads(null, direction, RANDOM))
            {
                if (row == 0)
                    renderQuad(modelView.last(), bufferbuilder, quad, brightness, alpha);
                else
                    bufferbuilder.putBulkData(modelView.last(), quad, brightness, brightness, brightness, color, OverlayTexture.NO_OVERLAY);
            }
        }

        tesselator.end();
        modelView.translate(0.5F, 0.5F, 0.5F);
    }

    /* Logo Effect Randomizer */

    private static class LogoEffectRandomizer
    {
        public double horizontal;
        public double vertical;
        public double depth;

        public LogoEffectRandomizer(ClassicTitleScreen screen, int horizontal, int vertical)
        {
            this.horizontal = this.vertical = (double) (10 + vertical) + screen.getRand().nextDouble() * 32D + (double) horizontal;
        }

        public void run()
        {
            this.vertical = this.horizontal;

            if (this.horizontal > 0.0D)
                this.depth -= 0.59D;

            this.horizontal += this.depth;
            this.depth *= 0.9D;

            if (this.horizontal < 0.0D)
            {
                this.horizontal = 0.0D;
                this.depth = 0.0D;
            }
        }
    }
}
