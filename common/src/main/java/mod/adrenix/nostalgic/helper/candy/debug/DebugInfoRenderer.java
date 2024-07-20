package mod.adrenix.nostalgic.helper.candy.debug;

import com.google.common.base.Strings;
import mod.adrenix.nostalgic.mixin.access.DebugScreenOverlayAccess;
import mod.adrenix.nostalgic.mixin.access.LevelRendererAccess;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.enums.Generic;
import mod.adrenix.nostalgic.util.client.GameUtil;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.color.HexUtil;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import mod.adrenix.nostalgic.util.common.text.TextUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;

class DebugInfoRenderer
{
    /* Fields */

    final LevelRenderer levelRenderer;
    final ClientLevel level;
    final Minecraft minecraft;
    final GuiGraphics graphics;
    final HitResult blockHit;
    final HitResult liquidHit;
    final Entity player;
    final BlockPos playerPos;
    final Function<Map.Entry<Property<?>, Comparable<?>>, String> getPropertyValue;
    final Function<Holder<Biome>, String> printBiome;
    final ArrayList<String> left;
    final ArrayList<String> right;
    final boolean isReducedInfo;
    final boolean renderFpsCharts;
    final boolean renderNetworkCharts;

    /* Constructor */

    DebugInfoRenderer(DebugScreenOverlay overlay, GuiGraphics graphics)
    {
        this.graphics = graphics;
        this.minecraft = Minecraft.getInstance();
        this.player = this.minecraft.cameraEntity;
        this.level = this.minecraft.level;
        this.levelRenderer = this.minecraft.levelRenderer;

        assert this.level != null : "No level was present when rendering debug information";
        assert this.player != null : "No player was present when rendering debug information";

        this.playerPos = this.player.blockPosition();
        this.isReducedInfo = this.minecraft.showOnlyReducedInfo();
        this.renderFpsCharts = ((DebugScreenOverlayAccess) overlay).nt$showFpsChart();
        this.renderNetworkCharts = ((DebugScreenOverlayAccess) overlay).nt$showNetworkChart();
        this.blockHit = ((DebugScreenOverlayAccess) overlay).nt$getBlockHitResult();
        this.liquidHit = ((DebugScreenOverlayAccess) overlay).nt$getLiquidHitResult();
        this.getPropertyValue = ((DebugScreenOverlayAccess) overlay)::nt$getPropertyValueString;
        this.printBiome = DebugScreenOverlayAccess::nt$printBiome;
        this.left = new ArrayList<>();
        this.right = new ArrayList<>();
    }

    /* Methods */

    /**
     * Render debug information.
     */
    public void render()
    {
        this.setGameInformation();

        if (!this.isReducedInfo)
            this.setExtraGameInformation();

        for (int i = 0; i < this.left.size(); i++)
        {
            String text = this.left.get(i);

            if (Strings.isNullOrEmpty(text))
                continue;

            this.drawBackground(text, i, true);
            this.drawText(text, 2, 2 + GuiUtil.textHeight() * i, true);
        }

        int width = this.minecraft.getWindow().getGuiScaledWidth();

        for (int i = 0; i < this.right.size(); i++)
        {
            String text = this.right.get(i);

            int x = width - 2 - GuiUtil.font().width(text);
            int y = 2 + GuiUtil.textHeight() * i;

            this.drawBackground(text, i, false);
            this.drawText(text, x, y, false);
        }
    }

    /**
     * Draws debug text using defined tweak settings.
     *
     * @param text     The text to draw.
     * @param x        The x-coordinate of where to draw.
     * @param y        The y-coordinate of where to draw.
     * @param leftSide Whether the text is on the left side of the debug screen.
     */
    void drawText(String text, int x, int y, boolean leftSide)
    {
        int color;
        boolean dropShadow;

        if (leftSide)
        {
            if (CandyTweak.SHOW_DEBUG_LEFT_TEXT_COLOR.get())
                color = HexUtil.parseInt(CandyTweak.DEBUG_LEFT_TEXT_COLOR.get());
            else
                color = Color.WHITE.get();

            dropShadow = CandyTweak.SHOW_DEBUG_LEFT_TEXT_SHADOW.get();
        }
        else
        {
            if (CandyTweak.SHOW_DEBUG_RIGHT_TEXT_COLOR.get())
                color = HexUtil.parseInt(CandyTweak.DEBUG_RIGHT_TEXT_COLOR.get());
            else
                color = Color.NOSTALGIC_GRAY.get();

            dropShadow = CandyTweak.SHOW_DEBUG_RIGHT_TEXT_SHADOW.get();
        }

        this.graphics.drawString(GuiUtil.font(), text, x, y, color, dropShadow);
    }

    /**
     * Draws a background color behind a debug line.
     *
     * @param text     The text to draw.
     * @param index    The line index to calculate the y-offset.
     * @param leftSide Whether the text is on the left side of the debug screen.
     */
    void drawBackground(String text, int index, boolean leftSide)
    {
        boolean isLeftOff = leftSide && !CandyTweak.SHOW_DEBUG_LEFT_BACKGROUND.get();
        boolean isRightOff = !leftSide && !CandyTweak.SHOW_DEBUG_RIGHT_BACKGROUND.get();

        if (isLeftOff || isRightOff)
            return;

        String hex = leftSide ? CandyTweak.DEBUG_LEFT_BACKGROUND_COLOR.get() : CandyTweak.DEBUG_RIGHT_BACKGROUND_COLOR.get();
        int color = HexUtil.parseInt(hex);
        int scaledWidth = GuiUtil.getGuiWidth();
        int fontWidth = GuiUtil.font().width(text);
        int fontHeight = GuiUtil.textHeight();

        int minX = leftSide ? 1 : (scaledWidth - 2 - fontWidth) - 1;
        int maxX = leftSide ? 2 + fontWidth + 1 : (scaledWidth - 2 - fontWidth) + fontWidth + 1;
        int minY = (2 + fontHeight * index) - 1;
        int maxY = (2 + fontHeight * index) + fontHeight - 1;

        this.graphics.fill(minX, minY, maxX, maxY, color);
    }

    /**
     * Adds debug information to the left/right side string lists.
     */
    void setGameInformation()
    {
        int width = this.minecraft.getWindow().getGuiScaledWidth();
        int chunkUpdates = this.levelRenderer.getSectionRenderDispatcher().getToUpload();
        int renderedEntities = ((LevelRendererAccess) this.levelRenderer).nt$getRenderedEntities();
        int culledEntities = ((LevelRendererAccess) this.levelRenderer).nt$getCulledEntities();

        String overlay = CandyTweak.OLD_OVERLAY_TEXT.parse(GameUtil.getVersion());
        String title = overlay.isEmpty() ? "Minecraft " + GameUtil.getVersion() : overlay;
        String fps = String.format(" (%s fps, %s chunk updates)", this.minecraft.getFps(), chunkUpdates);
        String sections = String.format("C: %d/%d. F: 0, O: 0, E: 0", this.levelRenderer.countRenderedSections(), (long) this.levelRenderer.getTotalSections());
        String entities = String.format("E: %s/%s. B: %s, I: 0", renderedEntities, this.level.getEntityCount(), culledEntities);
        String particles = String.format("P: %s. T: All: %s", this.minecraft.particleEngine.countParticles(), this.level.getEntityCount());
        String overflow = String.format(" (%s fps)", this.minecraft.getFps());

        long max = Runtime.getRuntime().maxMemory();
        long total = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        long used = total - free;

        String memory = String.format("Used memory: %2d%% (%03dMB) of %03dMB", used * 100L / max, MathUtil.bytesToMegabytes(used), MathUtil.bytesToMegabytes(max));
        String allocated = String.format("Allocated memory: %2d%% (%03dMB)", total * 100L / max, MathUtil.bytesToMegabytes(total));

        if (GuiUtil.font().width(title + fps) + 2 > width - 2 - GuiUtil.font().width(memory))
            fps = overflow;

        this.left.add(title + fps);
        this.left.add(sections);
        this.left.add(entities);
        this.left.add(particles);

        this.right.add(memory);
        this.right.add(allocated);

        if (CandyTweak.SHOW_DEBUG_GPU_USAGE.get())
        {
            String gpu = TextUtil.extract(this.minecraft.fpsString, "GPU:.+");
            String usage = TextUtil.extract(gpu, "\\d+");

            if (!usage.isEmpty())
                this.right.add("GPU usage: " + TextUtil.getPercentColorHigh(Integer.parseInt(usage)) + "%");
        }

        if (CandyTweak.OLD_DEBUG.get() == Generic.BETA)
        {
            this.left.add(String.format("ChunkCache: %d", this.level.getChunkSource().getLoadedChunksCount()));

            if (!this.isReducedInfo)
            {
                this.left.add("");
                this.left.add(String.format("X: %f", this.player.getX()));
                this.left.add(String.format("Y: %f", this.player.getY()));
                this.left.add(String.format("Z: %f", this.player.getZ()));
            }
        }
    }

    /**
     * Adds extra debug information if needed.
     */
    void setExtraGameInformation()
    {
        this.left.add("");

        if (CandyTweak.SHOW_DEBUG_FACING_DATA.get())
        {
            Direction direction = this.player.getDirection();
            String facing = switch (direction)
            {
                case NORTH -> "Towards negative Z";
                case SOUTH -> "Towards positive Z";
                case WEST -> "Towards negative X";
                case EAST -> "Towards positive X";
                default -> "Invalid";
            };

            this.left.add(String.format("Facing: %s (%s) (%.1f / %.1f)", direction, facing, Mth.wrapDegrees(this.player.getYRot()), Mth.wrapDegrees(this.player.getXRot())));
        }

        if (CandyTweak.SHOW_DEBUG_LIGHT_DATA.get())
        {
            int skyLight = this.level.getBrightness(LightLayer.SKY, this.playerPos);
            int blockLight = this.level.getBrightness(LightLayer.BLOCK, this.playerPos);
            int brightness = this.level.getChunkSource().getLightEngine().getRawBrightness(this.playerPos, 0);

            this.left.add(String.format("Light: %d (%d sky, %d block)", brightness, skyLight, blockLight));
        }

        Entity targetEntity = this.minecraft.crosshairPickEntity;
        boolean isTargeted = CandyTweak.SHOW_DEBUG_TARGET_DATA.get();
        boolean isValidHeight = this.playerPos.getY() >= this.level.getMinBuildHeight() && this.playerPos.getY() < this.level.getMaxBuildHeight();

        if (CandyTweak.SHOW_DEBUG_BIOME_DATA.get() && isValidHeight)
            this.left.add(String.format("Biome: %s", this.printBiome.apply(this.level.getBiome(this.playerPos))));

        if (isTargeted && this.blockHit.getType() == HitResult.Type.BLOCK)
        {
            BlockPos targetPos = ((BlockHitResult) this.blockHit).getBlockPos();
            BlockState targetState = this.level.getBlockState(targetPos);

            this.right.add("");
            this.right.add(String.format(ChatFormatting.UNDERLINE + "Targeted Block: %s, %s, %s", targetPos.getX(), targetPos.getY(), targetPos.getZ()));
            this.right.add(String.valueOf(BuiltInRegistries.BLOCK.getKey(targetState.getBlock())));

            for (Map.Entry<Property<?>, Comparable<?>> entry : targetState.getValues().entrySet())
                this.right.add(this.getPropertyValue.apply(entry));

            targetState.getTags().map(tagKey -> "#" + tagKey.location()).forEach(this.right::add);
        }

        if (isTargeted && this.liquidHit.getType() == HitResult.Type.BLOCK)
        {
            BlockPos targetPos = ((BlockHitResult) this.liquidHit).getBlockPos();
            FluidState targetState = this.level.getFluidState(targetPos);

            this.right.add("");
            this.right.add(String.format(ChatFormatting.UNDERLINE + "Targeted Fluid: %s, %s, %s", targetPos.getX(), targetPos.getY(), targetPos.getZ()));
            this.right.add(String.valueOf(BuiltInRegistries.FLUID.getKey(targetState.getType())));

            for (Map.Entry<Property<?>, Comparable<?>> entry : targetState.getValues().entrySet())
                this.right.add(this.getPropertyValue.apply(entry));

            targetState.getTags().map(tagKey -> "#" + tagKey.location()).forEach(this.right::add);
        }

        if (isTargeted && targetEntity != null)
        {
            this.right.add("");
            this.right.add(ChatFormatting.UNDERLINE + "Targeted Entity:");
            this.right.add(String.valueOf(BuiltInRegistries.ENTITY_TYPE.getKey(targetEntity.getType())));
        }
    }
}
