package mod.adrenix.nostalgic.client.config.gui.widget;

import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigWidgets;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.util.common.MathUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;

/**
 * This widget renders clickable text that will jump from the search tab to a tab where the category, subcategory,
 * embed, or tweak resides.
 */
public class SearchCrumbs extends AbstractWidget
{
    /**
     * This enumeration instructs the click handler on how to get a new spot.
     * Different types require a different order of jumping.
     */
    private enum JumpType { GROUP, CATEGORY, SUBCATEGORY, EMBEDDED }

    /**
     * This record distinguishes individual "crumbs" in a searching crumb line of text.
     * @param startX Where this crumb starts on the x-axis.
     * @param tweak The tweak this crumb is associated with.
     * @param text The translated text of this crumb's name.
     * @param jump The type of config row list container jump associated with this crumb.
     */
    private record Crumb(int startX, TweakClientCache<?> tweak, MutableComponent text, JumpType jump)
    {
        public int getEndX()
        {
            Font font = Minecraft.getInstance().font;
            return this.startX + font.width(text) + font.width(Component.literal("/"));
        }
    }

    /* Fields */

    private final ArrayList<Crumb> crumbs = new ArrayList<>();
    private final MutableComponent slashText = Component.literal("/").withStyle(ChatFormatting.WHITE);

    // This integer is used by the config row list renderer
    public final int startX;

    /* Constructor */

    /**
     * Create a new bread crumb trail for a tweak in the search configuration tab.
     * @param tweak The tweak associated with this crumb trail.
     */
    public SearchCrumbs(TweakClientCache<?> tweak)
    {
        super(ConfigRowList.TEXT_START, 0, 0, 0, Component.empty());

        this.startX = ConfigRowList.TEXT_START;

        String groupKey = tweak.getGroup().getLangKey();
        Crumb groupCrumb = new Crumb(startX, tweak, Component.translatable(groupKey).withStyle(ChatFormatting.GOLD), JumpType.GROUP);

        this.crumbs.add(groupCrumb);

        if (tweak.getCategory() != null)
        {
            String catKey = tweak.getCategory().container().getLangKey();
            MutableComponent catText = Component.translatable(catKey).withStyle(ChatFormatting.YELLOW);
            Crumb catCrumb = new Crumb(groupCrumb.getEndX(), tweak, catText, JumpType.CATEGORY);

            this.crumbs.add(catCrumb);
        }

        if (tweak.getSubcategory() != null)
        {
            String catKey = tweak.getSubcategory().container().getCategory().getLangKey();
            String subKey = tweak.getSubcategory().container().getLangKey();

            MutableComponent catText = Component.translatable(catKey).withStyle(ChatFormatting.YELLOW);
            MutableComponent subText = Component.translatable(subKey).withStyle(ChatFormatting.GREEN);

            Crumb catCrumb = new Crumb(groupCrumb.getEndX(), tweak, catText, JumpType.CATEGORY);
            Crumb subCrumb = new Crumb(catCrumb.getEndX(), tweak, subText, JumpType.SUBCATEGORY);

            this.crumbs.add(catCrumb);
            this.crumbs.add(subCrumb);
        }

        if (tweak.getEmbed() != null)
        {
            String catKey = tweak.getEmbed().container().getSubcategory().getCategory().getLangKey();
            String subKey = tweak.getEmbed().container().getSubcategory().getLangKey();
            String embKey = tweak.getEmbed().container().getLangKey();

            MutableComponent catText = Component.translatable(catKey).withStyle(ChatFormatting.YELLOW);
            MutableComponent subText = Component.translatable(subKey).withStyle(ChatFormatting.GREEN);
            MutableComponent embText = Component.translatable(embKey).withStyle(ChatFormatting.AQUA);

            Crumb catCrumb = new Crumb(groupCrumb.getEndX(), tweak, catText, JumpType.CATEGORY);
            Crumb subCrumb = new Crumb(catCrumb.getEndX(), tweak, subText, JumpType.SUBCATEGORY);
            Crumb embCrumb = new Crumb(subCrumb.getEndX(), tweak, embText, JumpType.EMBEDDED);

            this.crumbs.add(catCrumb);
            this.crumbs.add(subCrumb);
            this.crumbs.add(embCrumb);
        }
    }

    /**
     * Draws a crumb onto the current screen using the crumb's rendering information.
     * @param graphics The current GuiGraphics object.
     * @param crumb A crumb instance.
     * @param isUnderlined Whether the text rendering should have an underline to it.
     */
    private void drawCrumb(GuiGraphics graphics, Crumb crumb, boolean isUnderlined)
    {
        MutableComponent crumbText = isUnderlined ? crumb.text().copy().withStyle(ChatFormatting.UNDERLINE) : crumb.text();
        graphics.drawString(Minecraft.getInstance().font, crumbText, crumb.startX(), this.getY(), 0xFFFFFF);
    }

    /**
     * Draws a slash in front of a crumb. This is used to separate individual crumbs.
     * @param graphics The current GuiGraphics object.
     * @param crumb A crumb instance.
     */
    private void drawSlash(GuiGraphics graphics, Crumb crumb)
    {
        Font font = Minecraft.getInstance().font;
        int startX = crumb.getEndX() - font.width(Component.literal("/"));

        graphics.drawString(font, this.slashText, startX, this.getY(), 0xFFFFFF);
    }

    /**
     * Checks if the mouse is over a crumb.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param crumb A crumb instance.
     * @return Whether the mouse is over the given crumb.
     */
    private boolean isOverCrumb(double mouseX, double mouseY, Crumb crumb)
    {
        // This prevents clicking when the crumbs haven't received a y-position from the row list yet.
        if (this.getY() == 0 || ConfigWidgets.isOutsideRowList(mouseY))
            return false;

        Font font = Minecraft.getInstance().font;
        int width = font.width(crumb.text());
        int height = font.lineHeight;

        return MathUtil.isWithinBox(mouseX, mouseY, crumb.startX(), this.getY(), width, height);
    }

    /**
     * Handler method for when the mouse is clicked.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param button The mouse button that was clicked.
     * @return Whether this method handled the click event.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (button != 0)
            return false;

        Minecraft minecraft = Minecraft.getInstance();

        for (Crumb crumb : this.crumbs)
        {
            if (isOverCrumb(mouseX, mouseY, crumb) && minecraft.screen instanceof ConfigScreen configScreen)
            {
                configScreen.setTabFromGroupKey(crumb.tweak().getGroup().getLangKey());

                /*
                   Each tweak has different metadata attached to it. A tweak will always be assigned a configuration
                   group tab. Some tweaks may not have any further organization attached to it. Depending on the
                   metadata defined in the client config, crumb jumping behavior will need to change based on the
                   desired jump type and the metadata associated with the tweak.

                   If the tweak only has category metadata, then the only possible jump type after a group is the
                   category itself.

                   If the tweak has subcategory metadata, then two possible jump types needs handled.
                   If the tweak has embedded metadata, then three possible jump types needs handled.
                 */

                if (crumb.tweak().getCategory() != null && crumb.jump() == JumpType.CATEGORY)
                    configScreen.setScrollOnContainer(crumb.tweak().getCategory().container());

                if (crumb.tweak().getSubcategory() != null)
                {
                    if (crumb.jump() == JumpType.CATEGORY)
                        configScreen.setScrollOnContainer(crumb.tweak().getSubcategory().container().getCategory());
                    else if (crumb.jump() == JumpType.SUBCATEGORY)
                        configScreen.setScrollOnContainer(crumb.tweak().getSubcategory().container());
                }

                if (crumb.tweak().getEmbed() != null)
                {
                    if (crumb.jump() == JumpType.CATEGORY)
                        configScreen.setScrollOnContainer(crumb.tweak().getEmbed().container().getSubcategory().getCategory());
                    else if (crumb.jump() == JumpType.SUBCATEGORY)
                        configScreen.setScrollOnContainer(crumb.tweak().getEmbed().container().getSubcategory());
                    else if (crumb.jump() == JumpType.EMBEDDED)
                        configScreen.setScrollOnContainer(crumb.tweak().getEmbed().container());
                }

                this.playDownSound(minecraft.getSoundManager());

                return true;
            }
        }

        return false;
    }

    /**
     * Renders a crumb onto the screen.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick A change in frame time.
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if (this.crumbs.size() == 1)
        {
            Crumb crumb = this.crumbs.get(0);
            drawCrumb(graphics, crumb, this.isOverCrumb(mouseX, mouseY, crumb));
        }
        else
        {
            for (int i = 0; i < this.crumbs.size(); i++)
            {
                Crumb crumb = this.crumbs.get(i);

                if (i != this.crumbs.size() - 1)
                    drawSlash(graphics, crumb);

                drawCrumb(graphics, crumb, this.isOverCrumb(mouseX, mouseY, crumb));
            }
        }
    }

    /* Required Widget Overrides */

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) { }
}
