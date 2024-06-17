package mod.adrenix.nostalgic.client.gui.screen.config.widget.tag;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.tweak.factory.TweakBinding;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.function.ForEachWithPrevious;
import net.minecraft.client.gui.GuiGraphics;

import java.util.LinkedHashSet;

public class TagWidget extends DynamicWidget<TagBuilder, TagWidget>
{
    /* Builder */

    /**
     * Start the creation process for a new {@link TagWidget} instance.
     *
     * @param tweak The {@link Tweak} for this tag to collect data from.
     * @return A {@link TagBuilder} instance.
     */
    public static TagBuilder create(Tweak<?> tweak)
    {
        return new TagBuilder(tweak);
    }

    /* Fields */

    final LinkedHashSet<Tag> tags = new LinkedHashSet<>();
    final Tweak<?> tweak;
    final int padding = 2;

    /* Constructor */

    TagWidget(TagBuilder builder)
    {
        super(builder);

        this.tweak = builder.tweak;

        if (this.tweak.isNew() && ModTweak.DISPLAY_NEW_TAGS.fromCache())
            this.addTag(TagType.NEW);

        if (this.tweak.isClient())
            this.addTag(TagType.CLIENT);

        if (this.tweak.isServer())
            this.addTag(TagType.SERVER);

        if (this.tweak.isDynamic())
            this.addTag(TagType.DYNAMIC);

        if (this.tweak instanceof TweakBinding)
            this.addTag(TagType.SYNC);

        if (this.tweak.isResourceReloadRequired())
            this.addTag(TagType.RELOAD);

        if (this.tweak.isWarningTag())
            this.addTag(TagType.WARNING);

        if (this.tweak.isAlertTag())
            this.addTag(TagType.ALERT);

        if (this.tweak.isNotSSO())
            this.addTag(TagType.NO_SSO);

        this.setSize();
    }

    /* Methods */

    /**
     * Add a tag to the tags cache.
     *
     * @param tag A {@link TagType} enumeration.
     */
    private void addTag(TagType tag)
    {
        this.tags.add(new Tag(this, tag));
    }

    /**
     * Set the width and height of this widget based on the current tags.
     */
    private void setSize()
    {
        this.getBuilder().width(this.tags.stream().mapToInt(Tag::getWidth).sum() + (this.padding * this.tags.size()));
        this.getBuilder().height(GuiUtil.textHeight() + 2);

        ForEachWithPrevious.create(this.tags).forEach(Tag::setFromPrev).run();
    }

    /**
     * @return The tweak associated with this tag widget instance.
     */
    public Tweak<?> getTweak()
    {
        return this.tweak;
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

        if (this.tweak.isAlertTag() && this.tags.stream().noneMatch(Tag::isAlertTag))
        {
            this.addTag(TagType.ALERT);
            this.setSize();
        }
        else if (!this.tweak.isAlertTag() && this.tags.stream().anyMatch(Tag::isAlertTag))
        {
            this.tags.removeIf(Tag::isAlertTag);
            this.setSize();
        }

        if (ModTweak.DISPLAY_TAG_TOOLTIPS.fromCache())
            this.tags.forEach(tag -> tag.setTooltip(mouseX, mouseY));

        RenderUtil.beginBatching();

        this.tags.forEach(tag -> tag.render(graphics));
        this.renderDebug(graphics);

        RenderUtil.endBatching();
    }
}
