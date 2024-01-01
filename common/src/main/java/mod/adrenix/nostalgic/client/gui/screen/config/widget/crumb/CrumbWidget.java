package mod.adrenix.nostalgic.client.gui.screen.config.widget.crumb;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicField;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicFunction;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.WidgetCache;
import mod.adrenix.nostalgic.client.gui.widget.list.AbstractRow;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.data.CacheHolder;
import mod.adrenix.nostalgic.util.common.function.ForEachWithPrevious;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

public class CrumbWidget extends DynamicWidget<CrumbBuilder, CrumbWidget>
{
    /* Builder */

    /**
     * Start the creation process for a new {@link CrumbWidget} instance.
     *
     * @param tweak The {@link Tweak} for this {@link Crumb} to obtain data from.
     * @return A crumb builder instance.
     */
    public static CrumbBuilder create(Tweak<?> tweak)
    {
        return new CrumbBuilder(tweak);
    }

    /* Static */

    static final MutableComponent SLASH = Component.literal(" / ").withStyle(ChatFormatting.WHITE);

    /* Fields */

    final int crumbMargin = 3;
    final LinkedHashSet<Crumb> crumbs = new LinkedHashSet<>();
    @Nullable final AbstractRow<?, ?> row;

    /* Constructor */

    CrumbWidget(CrumbBuilder builder)
    {
        super(builder);

        builder.tweak.getContainer()
            .getGroupSetFromCategory()
            .forEach(container -> this.crumbs.add(new Crumb(this, container)));

        this.row = builder.row;

        class Sync implements DynamicFunction<CrumbBuilder, CrumbWidget>
        {
            @Override
            public void apply(CrumbWidget widget, CrumbBuilder builder)
            {
                ForEachWithPrevious.create(widget.crumbs).forEach(Crumb::setFromPrev).run();
                CollectionUtil.last(widget.crumbs).ifPresent(Crumb::setAsLast);

                widget.setWidth(widget.getWidthFromCrumbs());
                widget.setHeight(widget.getHeightFromCrumbs());
            }

            @Override
            public boolean isReapplyNeeded(CrumbWidget widget, CrumbBuilder builder, WidgetCache cache)
            {
                if (widget.row != null)
                    return CacheHolder.isAnyExpired(widget.row.cache.x, widget.row.cache.width);

                return false;
            }

            @Override
            public List<DynamicField> getManaging(CrumbBuilder builder)
            {
                return List.of(DynamicField.WIDTH, DynamicField.HEIGHT);
            }
        }

        this.getBuilder().addFunction(new Sync());
    }

    /* Methods */

    /**
     * @return The height for each line of crumbs.
     */
    int getLineHeight()
    {
        return GuiUtil.textHeight() + this.crumbMargin;
    }

    /**
     * Get the height for this widget based on the crumb with the greatest line height.
     */
    private int getHeightFromCrumbs()
    {
        int maxLineHeight = this.crumbs.stream().mapToInt(Crumb::getLineHeight).max().orElse(0);

        return Math.max(this.getLineHeight(), maxLineHeight) - this.crumbMargin;
    }

    /**
     * Get the width for this widget based on the line index with the greatest crumb text width.
     */
    private int getWidthFromCrumbs()
    {
        return this.crumbs.stream().mapToInt(Crumb::getMaxWidthFromCrumbLine).max().orElse(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (this.isInvalidClick(mouseX, mouseY, button))
            return false;

        Optional<Crumb> clicked = this.crumbs.stream().filter(crumb -> crumb.isMouseOver(mouseX, mouseY)).findFirst();

        if (clicked.isPresent())
        {
            clicked.get().onClick();
            GuiUtil.playClick();

            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.render(graphics, mouseX, mouseY, partialTick);

        if (this.isInvisible())
            return;

        RenderUtil.beginBatching();

        this.crumbs.forEach(crumb -> crumb.render(graphics, mouseX, mouseY, partialTick));
        this.renderDebug(graphics);

        RenderUtil.endBatching();
    }
}
