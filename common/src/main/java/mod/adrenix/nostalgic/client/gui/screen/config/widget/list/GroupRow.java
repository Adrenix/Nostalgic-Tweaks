package mod.adrenix.nostalgic.client.gui.screen.config.widget.list;

import mod.adrenix.nostalgic.client.gui.widget.blank.BlankWidget;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonRenderer;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconWidget;
import mod.adrenix.nostalgic.client.gui.widget.list.RowList;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.LinkedHashSet;

public class GroupRow extends ConfigRow<GroupRowMaker, GroupRow>
{
    /* Builders */

    /**
     * Create a new {@link GroupRow}.
     *
     * @param container The {@link Container} instance this group will read.
     * @param rowList   The {@link RowList} instance.
     * @return A new {@link GroupRowMaker} instance.
     */
    public static GroupRowMaker create(Container container, RowList rowList)
    {
        return new GroupRowMaker(container, rowList);
    }

    /**
     * Create a new {@link GroupRow} linked to another group.
     *
     * @param container The {@link Container} instance this group row is in.
     * @param group     The {@link GroupRow} that is to be linked.
     * @return A new {@link GroupRowMaker} instance.
     */
    public static GroupRowMaker create(Container container, GroupRow group)
    {
        return new GroupRowMaker(container, group);
    }

    /* Fields */

    private final Container container;
    private final LinkedHashSet<ConfigRow<?, ?>> children;
    private boolean collapsed;

    /* Constructors */

    GroupRow(GroupRowMaker maker)
    {
        super(maker);

        this.children = new LinkedHashSet<>();
        this.parent = maker.parent;
        this.container = maker.container;
        this.collapsed = true;

        Color color = this.container.getColor();

        ButtonWidget toggle = ButtonWidget.create()
            .title(() -> this.collapsed ? Component.literal("+") : Component.literal("-"))
            .backgroundRenderer(ButtonRenderer.EMPTY)
            .color(color, color.brighter())
            .padding(3)
            .cannotFocus()
            .useTextWidth()
            .noClickSound()
            .build(this::addWidget);

        IconWidget icon = IconWidget.create(this.container.getIcon())
            .posY(() -> Math.round(MathUtil.center(toggle.getY(), 16, toggle.getHeight())))
            .rightOf(toggle, 6)
            .size(16)
            .build(this::addWidget);

        BlankWidget area = BlankWidget.create()
            .pos(this::getX, this::getY)
            .height(this.getDefaultRowHeight())
            .width(this::getWidth)
            .build(this::addWidget);

        TextWidget.create(Component.literal(this.container.toString()))
            .pressArea(area)
            .onPress(this::toggle, color.brighter())
            .posY(toggle::getTextY)
            .color(color)
            .rightOf(icon, 6)
            .extendWidthToEnd(this, 0)
            .highlightIf(ModTweak.DISPLAY_ROW_HIGHLIGHT_FADE::fromCache)
            .highlighter(this.highlighter)
            .build(this::addWidget);

        this.getBuilder()
            .indent(this.container.getIndentForGroupRow())
            .heightOverflowMargin(0)
            .highlightColor(color)
            .postRenderer(this::renderBox);
    }

    /* Methods */

    /**
     * @return The container associated with this group.
     */
    public Container getContainer()
    {
        return this.container;
    }

    /**
     * @return The rows that are subscribed to this group row.
     */
    public LinkedHashSet<ConfigRow<?, ?>> getChildren()
    {
        return this.children;
    }

    /**
     * @return Whether this group row is currently collapsed.
     */
    public boolean isCollapsed()
    {
        return this.collapsed;
    }

    /**
     * @return Whether this group row is currently expanded.
     */
    public boolean isExpanded()
    {
        return !this.isCollapsed();
    }

    /**
     * Expand this group row so that its children rows are added to the row list.
     */
    public void expand()
    {
        this.collapsed = true;

        this.toggle();
    }

    /**
     * Jump to this group row in the row list. If this group is collapsed, then it will be expanded.
     */
    public void jumpToMe()
    {
        if (this.isCollapsed())
            this.expand();

        this.focusOnFirst();
        this.rowList.setSmoothScrollOn(this);
    }

    /**
     * Add a row to this group.
     *
     * @param row A {@link ConfigRow} instance.
     */
    private void addRow(ConfigRow<?, ?> row)
    {
        this.rowList.addBelowRow(row, CollectionUtil.last(this.children).orElse(this));
        this.children.add(row);
    }

    /**
     * Adds a tweak row to this row's children cache and adds the tweak row to the list.
     *
     * @param tweak A tweak instance.
     */
    private void addTweakRow(Tweak<?> tweak)
    {
        if (RowProvider.get().test(tweak))
            this.addRow(TweakRow.create(tweak, this).build());
    }

    /**
     * Adds a group row to this row's children cache and adds the group row to the list.
     *
     * @param group A group ({@link Container}) instance.
     */
    private void addGroupRow(Container group)
    {
        if (group.getDeepTweaks().stream().noneMatch(RowProvider.get()::test))
            return;

        this.addRow(create(group, this).build());
    }

    /**
     * Removes all rows (including embedded group row children rows) from the row list.
     */
    private void removeChildren()
    {
        CollectionUtil.fromCast(this.children, GroupRow.class).forEach(GroupRow::removeChildren);

        this.children.forEach(this.rowList::removeRow);
        this.children.clear();
    }

    /**
     * Change whether this group row has its children rows visible. This will change the rows within the parent row list
     * instance.
     */
    public void toggle()
    {
        if (this.collapsed)
        {
            if (this.container.getDescription().isPresent())
                this.addRow(DescriptionRow.create(this.container, this.rowList).build());

            this.container.getChildren().forEach(this::addGroupRow);
            this.container.getTweaks().forEach(this::addTweakRow);
        }
        else
            this.removeChildren();

        this.collapsed = !this.collapsed;
    }

    /**
     * Handler method for rendering the box outline for a group row when it is tabbed.
     *
     * @param row         A {@link GroupRow} instance.
     * @param graphics    A {@link GuiGraphics} instance.
     * @param mouseX      The x-coordinate of the mouse cursor.
     * @param mouseY      The y-coordinate of the mouse cursor.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    private void renderBox(GroupRow row, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if (row.isWidgetFocused())
            RenderUtil.outline(graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), Color.FRENCH_SKY_BLUE);

        this.renderTree(row, graphics, mouseX, mouseY, partialTick);
    }
}
