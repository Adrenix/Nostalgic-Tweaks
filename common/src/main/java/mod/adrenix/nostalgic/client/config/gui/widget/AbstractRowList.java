package mod.adrenix.nostalgic.client.config.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mod.adrenix.nostalgic.mixin.widen.IMixinAbstractSelectionList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;

import java.util.Objects;

public abstract class AbstractRowList<R extends ContainerObjectSelectionList.Entry<R>> extends ContainerObjectSelectionList<R>
{
    public AbstractRowList(Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight)
    {
        super(minecraft, width, height, y0, y1, itemHeight);
        this.centerListVertically = false;
    }

    /* Overrides */

    @Override public int getRowWidth() { return this.width; }
    @Override protected int getScrollbarPosition() { return this.width - 4; }
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        if (this.minecraft.level == null && this.minecraft.screen != null)
            this.minecraft.screen.renderDirtBackground(0);
        else
            this.fillGradient(poseStack, 0, 0, this.width, this.height, -1072689136, -804253680);

        int scrollbarPosition = this.getScrollbarPosition();
        int scrollbarPositionOffset = scrollbarPosition + 6;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        int startX = this.getRowLeft();
        int startY = this.y0 + 4 - (int) this.getScrollAmount();

        this.fillGradient(poseStack, this.x0, this.y0, this.x1, this.y1, 0x68000000, 0x68000000);
        this.renderList(poseStack, startX, startY, mouseX, mouseY, partialTick);

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, BACKGROUND_LOCATION);
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(519);

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        builder.vertex(this.x0, this.y0, -100.0D).uv(0.0F, (float) this.y0 / 32.0F).color(64, 64, 64, 255).endVertex();
        builder.vertex((this.x0 + this.width), this.y0, -100.0D).uv((float) this.width / 32.0F, (float) this.y0 / 32.0F).color(64, 64, 64, 255).endVertex();
        builder.vertex((this.x0 + this.width), 0.0D, -100.0D).uv((float) this.width / 32.0F, 0.0F).color(64, 64, 64, 255).endVertex();
        builder.vertex(this.x0, 0.0D, -100.0D).uv(0.0F, 0.0F).color(64, 64, 64, 255).endVertex();
        builder.vertex(this.x0, this.height, -100.0D).uv(0.0F, (float) this.height / 32.0F).color(64, 64, 64, 255).endVertex();
        builder.vertex((this.x0 + this.width), this.height, -100.0D).uv((float) this.width / 32.0F, (float) this.height / 32.0F).color(64, 64, 64, 255).endVertex();
        builder.vertex((this.x0 + this.width), this.y1, -100.0D).uv((float) this.width / 32.0F, (float) this.y1 / 32.0F).color(64, 64, 64, 255).endVertex();
        builder.vertex(this.x0, this.y1, -100.0D).uv(0.0F, (float) this.y1 / 32.0F).color(64, 64, 64, 255).endVertex();
        tesselator.end();

        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        builder.vertex(this.x0, (this.y0 + 4), 0.0D).color(0, 0, 0, 0).endVertex();
        builder.vertex(this.x1, (this.y0 + 4), 0.0D).color(0, 0, 0, 0).endVertex();
        builder.vertex(this.x1, this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
        builder.vertex(this.x0, this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
        builder.vertex(this.x0, this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
        builder.vertex(this.x1, this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
        builder.vertex(this.x1, (this.y1 - 4), 0.0D).color(0, 0, 0, 0).endVertex();
        builder.vertex(this.x0, (this.y1 - 4), 0.0D).color(0, 0, 0, 0).endVertex();
        tesselator.end();

        int maxScroll = this.getMaxScroll();
        if (maxScroll > 0)
        {
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            int heightOffset = (int) ((float) ((this.y1 - this.y0) * (this.y1 - this.y0)) / (float) this.getMaxPosition());
            heightOffset = Mth.clamp(heightOffset, 32, (this.y1 - this.y0 - 8));
            int scrollOffset = (int) this.getScrollAmount() * (this.y1 - this.y0 - heightOffset) / maxScroll + this.y0;

            if (scrollOffset < this.y0)
                scrollOffset = this.y0;

            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            builder.vertex(scrollbarPosition, this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
            builder.vertex(scrollbarPositionOffset, this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
            builder.vertex(scrollbarPositionOffset, this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
            builder.vertex(scrollbarPosition, this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
            builder.vertex(scrollbarPosition, (scrollOffset + heightOffset), 0.0D).color(128, 128, 128, 255).endVertex();
            builder.vertex(scrollbarPositionOffset, (scrollOffset + heightOffset), 0.0D).color(128, 128, 128, 255).endVertex();
            builder.vertex(scrollbarPositionOffset, scrollOffset, 0.0D).color(128, 128, 128, 255).endVertex();
            builder.vertex(scrollbarPosition, scrollOffset, 0.0D).color(128, 128, 128, 255).endVertex();
            builder.vertex(scrollbarPosition, (scrollOffset + heightOffset - 1), 0.0D).color(192, 192, 192, 255).endVertex();
            builder.vertex((scrollbarPositionOffset - 1), (scrollOffset + heightOffset - 1), 0.0D).color(192, 192, 192, 255).endVertex();
            builder.vertex((scrollbarPositionOffset - 1), scrollOffset, 0.0D).color(192, 192, 192, 255).endVertex();
            builder.vertex(scrollbarPosition, scrollOffset, 0.0D).color(192, 192, 192, 255).endVertex();
            tesselator.end();
        }

        this.renderDecorations(poseStack, mouseX, mouseY);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    private int getRowBottom(int index)
    {
        return this.getRowTop(index) + this.itemHeight;
    }

    protected void renderList(PoseStack poseStack, int x, int y, int mouseX, int mouseY, float partialTick)
    {
        int itemCount = this.getItemCount();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();

        for (int i = 0; i < itemCount; i++)
        {
            AbstractSelectionList.Entry<?> entry = this.getEntry(i);
            int top = this.getRowTop(i);
            int bottom = this.getRowBottom(i);
            if (bottom < this.y0 || top > this.y1)
            {
                boolean isTextWidget = false;
                if (entry instanceof ConfigRowList.Row)
                {
                    for (AbstractWidget widget : ((ConfigRowList.Row) entry).children)
                    {
                        if (widget instanceof TextGroup.TextRow)
                            if (((TextGroup.TextRow) widget).isFirst())
                                isTextWidget = true;
                    }
                }

                if (!isTextWidget)
                    continue;
            }

            int left;
            int startY = y + i * this.itemHeight + this.headerHeight;
            int height = this.itemHeight - 4;
            int width = this.getRowWidth();

            if (((IMixinAbstractSelectionList) this).NT$getRenderSelection() && this.isSelectedItem(i))
            {
                left = this.x0 + this.width / 2 - width / 2;
                int startX = this.x0 + this.width / 2 + width / 2;
                float brightness = this.isFocused() ? 1.0F : 0.5F;

                RenderSystem.disableTexture();
                RenderSystem.setShader(GameRenderer::getPositionShader);
                RenderSystem.setShaderColor(brightness, brightness, brightness, 1.0F);

                builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
                builder.vertex(left, startY + height + 2, 0.0).endVertex();
                builder.vertex(startX, startY + height + 2, 0.0).endVertex();
                builder.vertex(startX, startY - 2, 0.0).endVertex();
                builder.vertex(left, startY - 2, 0.0).endVertex();
                tesselator.end();

                RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
                builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
                builder.vertex(left + 1, startY + height + 1, 0.0).endVertex();
                builder.vertex(startX - 1, startY + height + 1, 0.0).endVertex();
                builder.vertex(startX - 1, startY - 1, 0.0).endVertex();
                builder.vertex(left + 1, startY - 1, 0.0).endVertex();
                tesselator.end();

                RenderSystem.enableTexture();
            }

            left = this.getRowLeft();
            entry.render(poseStack, i, top, left, width, height, mouseX, mouseY, Objects.equals(this.getHovered(), entry), partialTick);
        }
    }
}