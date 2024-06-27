package mod.adrenix.nostalgic.mixin.util.candy;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import mod.adrenix.nostalgic.client.gui.screen.home.overlay.supporter.SupporterOverlay;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.client.animate.Animate;
import mod.adrenix.nostalgic.util.client.animate.Animation;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.array.CycleIndex;
import mod.adrenix.nostalgic.util.common.asset.TextureLocation;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.data.IntegerHolder;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public abstract class SupporterRenderer
{
    /* Badges */

    private static final RenderType AUTHOR_VISIBLE_SHADER = RenderType.create("nt_author_shader", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 1536, false, true, RenderType.CompositeState.builder()
        .setShaderState(RenderStateShard.RENDERTYPE_TEXT_SHADER)
        .setTextureState(new RenderStateShard.TextureStateShard(TextureLocation.NT_LOGO_64.getLocation(), false, false))
        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
        .setLightmapState(RenderStateShard.LIGHTMAP)
        .createCompositeState(false));

    private static final RenderType AUTHOR_SEE_THROUGH_SHADER = RenderType.create("nt_author_see_through_shader", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 1536, false, true, RenderType.CompositeState.builder()
        .setShaderState(RenderStateShard.RENDERTYPE_TEXT_SEE_THROUGH_SHADER)
        .setTextureState(new RenderStateShard.TextureStateShard(TextureLocation.NT_LOGO_64.getLocation(), false, false))
        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
        .setLightmapState(RenderStateShard.LIGHTMAP)
        .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
        .setWriteMaskState(RenderStateShard.COLOR_WRITE)
        .createCompositeState(false));

    private static final RenderType SUPPORTER_VISIBLE_SHADER = RenderType.create("nt_supporter_shader", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 1536, false, true, RenderType.CompositeState.builder()
        .setShaderState(RenderStateShard.RENDERTYPE_TEXT_SHADER)
        .setTextureState(new RenderStateShard.TextureStateShard(TextureLocation.NT_SUPPORTER_64.getLocation(), false, false))
        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
        .setLightmapState(RenderStateShard.LIGHTMAP)
        .createCompositeState(false));

    private static final RenderType SUPPORTER_SEE_THROUGH_SHADER = RenderType.create("nt_supporter_see_through_shader", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 1536, false, true, RenderType.CompositeState.builder()
        .setShaderState(RenderStateShard.RENDERTYPE_TEXT_SEE_THROUGH_SHADER)
        .setTextureState(new RenderStateShard.TextureStateShard(TextureLocation.NT_SUPPORTER_64.getLocation(), false, false))
        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
        .setLightmapState(RenderStateShard.LIGHTMAP)
        .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
        .setWriteMaskState(RenderStateShard.COLOR_WRITE)
        .createCompositeState(false));

    private enum Badge
    {
        AUTHOR(AUTHOR_SEE_THROUGH_SHADER, AUTHOR_VISIBLE_SHADER),
        SUPPORTER(SUPPORTER_SEE_THROUGH_SHADER, SUPPORTER_VISIBLE_SHADER);

        private final RenderType seeThrough;
        private final RenderType visible;

        Badge(RenderType seeThrough, RenderType visible)
        {
            this.seeThrough = seeThrough;
            this.visible = visible;
        }

        private RenderType getSeeThroughShader()
        {
            return this.seeThrough;
        }

        private RenderType getVisibleShader()
        {
            return this.visible;
        }
    }

    /* Animations */

    private static final Animation ADRENIX_COLOR = Animate.linear(2L, TimeUnit.SECONDS);
    private static final Animation RAINBOW_COLOR = Animate.linear(2L, TimeUnit.SECONDS);
    private static final Animation CAPTAIN_MOVER = Animate.linear(100L, TimeUnit.MILLISECONDS);
    private static final CycleIndex CAPTAIN_CYCLE = new CycleIndex("Captain_3".split(""), true);

    /**
     * Check if the given display name is a mod supporter. If the supporter tag tweak is disabled, then no supporter
     * effects will be rendered.
     *
     * @param displayName The display name {@link Component} instance to check.
     * @return Whether the given name is a mod supporter.
     */
    public static boolean isSupporter(Component displayName)
    {
        if (!CandyTweak.SUPPORTER_TAGS.get())
            return false;

        for (String name : SupporterOverlay.getNames().keySet())
        {
            if (name.equals(displayName.getString()))
                return true;
        }

        return false;
    }

    /**
     * Check if the given display name is <b color=red>not</b> a mod supporter.
     *
     * @param displayName The display name {@link Component} instance to check.
     * @return Whether the given name is not a mod supporter.
     */
    public static boolean isNotSupporter(Component displayName)
    {
        return !isSupporter(displayName);
    }

    /**
     * Draw text that conforms to name tag standards.
     *
     * @param endX            The {@link IntegerHolder} that stores the ending x-coordinate.
     * @param text            The text to draw.
     * @param x               Where to start drawing on the x-axis.
     * @param y               Where to start drawing on the y-axis.
     * @param matrix          The position {@link Matrix4f} instance to use.
     * @param bufferSource    The {@link MultiBufferSource} instance to use.
     * @param textColor       The text's color.
     * @param backgroundColor The text's background color.
     * @param packedLight     The packed light coordinates.
     */
    private static void drawText(IntegerHolder endX, String text, float x, float y, Matrix4f matrix, MultiBufferSource bufferSource, int textColor, int backgroundColor, int packedLight)
    {
        Font font = GuiUtil.font();

        font.drawInBatch(text, x, y, textColor, false, matrix, bufferSource, Font.DisplayMode.SEE_THROUGH, backgroundColor, packedLight);
        int lastX = font.drawInBatch(text, x, y, textColor, false, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);

        endX.set(lastX);
    }

    /**
     * Draw a logo badge to the screen.
     *
     * @param badge        The {@link Badge} to get image shaders from.
     * @param bufferSource The {@link MultiBufferSource} to get buffer data from.
     * @param poseStack    The current {@link PoseStack} instance.
     * @param packedLight  The packed light coordinates.
     * @param endX         The x-coordinate of where the name tag ends.
     */
    private static void drawBadge(Badge badge, MultiBufferSource bufferSource, PoseStack poseStack, int packedLight, int endX)
    {
        poseStack.pushPose();
        poseStack.translate(endX + 3.0F, -2.5F, 0.0F);
        poseStack.scale(0.2F, 0.2F, 0.2F);

        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer seeThrough = bufferSource.getBuffer(badge.getSeeThroughShader());

        seeThrough.addVertex(matrix, 0, 64, 0).setColor(255, 255, 255, 255).setUv(0.0F, 1.0F).setLight(packedLight);
        seeThrough.addVertex(matrix, 64, 64, 0).setColor(255, 255, 255, 255).setUv(1.0F, 1.0F).setLight(packedLight);
        seeThrough.addVertex(matrix, 64, 0, 0).setColor(255, 255, 255, 255).setUv(1.0F, 0.0F).setLight(packedLight);
        seeThrough.addVertex(matrix, 0, 0, 0).setColor(255, 255, 255, 255).setUv(0.0F, 0.0F).setLight(packedLight);

        VertexConsumer visible = bufferSource.getBuffer(badge.getVisibleShader());

        visible.addVertex(matrix, 0, 64, 0).setColor(255, 255, 255, 255).setUv(0.0F, 1.0F).setLight(packedLight);
        visible.addVertex(matrix, 64, 64, 0).setColor(255, 255, 255, 255).setUv(1.0F, 1.0F).setLight(packedLight);
        visible.addVertex(matrix, 64, 0, 0).setColor(255, 255, 255, 255).setUv(1.0F, 0.0F).setLight(packedLight);
        visible.addVertex(matrix, 0, 0, 0).setColor(255, 255, 255, 255).setUv(0.0F, 0.0F).setLight(packedLight);

        poseStack.popPose();
    }

    /**
     * Render supporter visual effects for their name tag.
     *
     * @param displayName     The {@link Component} display name.
     * @param x               Where the text renders on the x-axis.
     * @param y               Where the text renders on the y-axis.
     * @param poseStack       The current {@link PoseStack} instance.
     * @param bufferSource    The {@link MultiBufferSource} instance.
     * @param backgroundColor The background color to apply to the text.
     * @param packedLight     The packed light coordinates.
     * @return The ending x-coordinate of where the last character was rendered.
     */
    public static int render(Component displayName, float x, float y, PoseStack poseStack, MultiBufferSource bufferSource, int backgroundColor, int packedLight)
    {
        String name = displayName.getString();
        Matrix4f fontMatrix = poseStack.last().pose();
        IntegerHolder endX = IntegerHolder.create((int) x);
        int transparent = Color.TRANSPARENT.get();
        int translucent = Color.BLACK.fromAlpha(0.016F).get();

        switch (name)
        {
            case "Adrenix" ->
            {
                ADRENIX_COLOR.playOrRewind();

                int color = Color.HSBtoRGB(360.F, 1.0F, Mth.lerp((float) ADRENIX_COLOR.getValue(), 0.4F, 1.0F));
                drawText(endX, name, x, y, fontMatrix, bufferSource, color, backgroundColor, packedLight);
                drawBadge(Badge.AUTHOR, bufferSource, poseStack, packedLight, endX.get());
            }
            case "PoeticRainbow" ->
            {
                if (RAINBOW_COLOR.isFinished())
                {
                    RAINBOW_COLOR.reset();
                    RAINBOW_COLOR.tick();
                }

                RAINBOW_COLOR.play();
                drawText(IntegerHolder.create((int) x), name, x, y, fontMatrix, bufferSource, translucent, backgroundColor, packedLight);

                CollectionUtil.forLoop(Arrays.stream(name.split("")), (letter, index) -> {
                    float offset = index / (float) name.length();
                    float degrees = 360.0F * (1.0F - (float) RAINBOW_COLOR.getValue() + offset);
                    float hsb = MathUtil.normalizeInRange(degrees, 0.0F, 360.0F);
                    int color = Color.HSBtoRGB(hsb / 360.0F, 1.0F, 1.0F);

                    drawText(endX, letter, endX.get(), y, fontMatrix, bufferSource, color, transparent, packedLight);
                });

                drawBadge(Badge.SUPPORTER, bufferSource, poseStack, packedLight, endX.get());
            }
            case "Captain_3" ->
            {
                if (CAPTAIN_MOVER.isFinished())
                {
                    CAPTAIN_MOVER.reset();
                    CAPTAIN_CYCLE.cycle();
                }

                CAPTAIN_MOVER.play();
                drawText(IntegerHolder.create((int) x), name, x, y, fontMatrix, bufferSource, translucent, backgroundColor, packedLight);

                CollectionUtil.forLoop(Arrays.stream(name.split("")), (letter, index) -> {
                    int color = SupporterOverlay.getNames().getOrDefault(name, Color.WHITE).get();
                    int jumpY = (int) y + CAPTAIN_CYCLE.get() == index ? -1 : 0;

                    drawText(endX, letter, endX.get(), jumpY, fontMatrix, bufferSource, color, transparent, packedLight);
                });

                drawBadge(Badge.SUPPORTER, bufferSource, poseStack, packedLight, endX.get());
            }
            default ->
            {
                Color color = SupporterOverlay.getNames().getOrDefault(name, Color.WHITE);

                drawText(endX, name, x, y, fontMatrix, bufferSource, color.get(), backgroundColor, packedLight);
                drawBadge(Badge.SUPPORTER, bufferSource, poseStack, packedLight, endX.get());
            }
        }

        return endX.get();
    }
}
