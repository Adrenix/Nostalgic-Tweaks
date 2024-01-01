package mod.adrenix.nostalgic.client.gui.screen.config.overlay;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.grid.Grid;
import mod.adrenix.nostalgic.client.gui.widget.group.Group;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconWidget;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.config.cache.CacheMode;
import mod.adrenix.nostalgic.tweak.StatusContext;
import mod.adrenix.nostalgic.tweak.TweakContext;
import mod.adrenix.nostalgic.tweak.TweakStatus;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.util.client.network.NetUtil;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.math.DynamicRectangle;
import mod.adrenix.nostalgic.util.common.text.TextUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.ToIntFunction;

public class StatusOverlay
{
    /**
     * This enumeration defines which view the overlay will display. The {@code STATUS} view will show the current
     * status of the given tweak. The {@code HELP} view will display information about what each icon indicates and how
     * the mod defines that status.
     */
    private enum Viewing
    {
        STATUS,
        HELP
    }

    /* Fields */

    private Viewing viewing = Viewing.STATUS;
    private final ButtonWidget statusButton;
    private final ButtonWidget helpButton;
    private final Group clientGroup;
    private final Group serverGroup;
    private final Grid header;
    private final Grid helpGrid;
    private final Grid statusGrid;
    private final Overlay overlay;
    private final Tweak<?> tweak;
    private final int padding;

    /* Constructor */

    public StatusOverlay(Tweak<?> tweak)
    {
        this.tweak = tweak;
        this.padding = 2;

        this.overlay = Overlay.create(Lang.Status.TITLE.get(this.tweak.getTranslation().getString()))
            .icon(Icons.SMALL_TRAFFIC_LIGHT)
            .resizeUsingPercentage(0.85D)
            .padding(this.padding)
            .build();

        this.header = Grid.create(this.overlay, 2)
            .anchor()
            .columnSpacing(0)
            .extendWidthToScreenEnd(0)
            .build(this.overlay::addWidget);

        this.statusButton = ButtonWidget.create(Lang.Button.STATUS)
            .icon(this.tweak.getStatus().getIcon())
            .onPress(this::setStatusView)
            .backgroundRenderer(this::renderHeader)
            .build(this.header::addCell);

        this.helpButton = ButtonWidget.create(Lang.Button.HELP)
            .icon(Icons.TOOLTIP)
            .hoverIcon(Icons.TOOLTIP_HOVER)
            .onPress(this::setHelpView)
            .backgroundRenderer(this::renderHeader)
            .build(this.header::addCell);

        this.statusGrid = Grid.create(this.overlay, 2)
            .below(this.header, this.padding)
            .spacing(2)
            .forceRelativeY()
            .alignRowHeights()
            .extendWidthToScreenEnd(0)
            .extendHeightToScreenEnd(0)
            .build(this.overlay::addWidget);

        this.helpGrid = Grid.create(this.overlay, () -> this.overlay.getWidth() > 400 ? 3 : 2)
            .below(this.header, this.padding)
            .spacing(this.padding)
            .forceRelativeY()
            .alignRowHeights()
            .extendWidthToScreenEnd(0)
            .extendHeightToScreenEnd(0)
            .build(this.overlay::addWidget);

        this.clientGroup = Group.create(this.overlay)
            .icon(Icons.CLIENT)
            .title(Lang.Tag.CLIENT)
            .border(Color.MANTIS_GREEN)
            .build(this.statusGrid::addCell);

        this.serverGroup = Group.create(this.overlay)
            .icon(Icons.SERVER)
            .title(Lang.Tag.SERVER)
            .border(Color.SHADOW_BLUE)
            .build(this.statusGrid::addCell);

        this.init();
    }

    /* Methods */

    /**
     * Open a new status overlay for the assigned tweak.
     */
    public void open()
    {
        this.setStatusView();

        this.overlay.addProjectedWidgets(this.statusButton, this.helpButton);
        this.overlay.setCustomScissor(this.getCustomScissor());
        this.overlay.open();
    }

    /**
     * Creates widgets using enumeration values from {@link TweakContext} and {@link TweakStatus}.
     */
    private void init()
    {
        this.createStatus(TweakContext.from(this.tweak, CacheMode.LOCAL), this.clientGroup);
        this.createStatus(TweakContext.from(this.tweak, CacheMode.NETWORK), this.serverGroup);

        for (TweakStatus status : TweakStatus.values())
            this.createContext(status);

        for (TweakContext context : TweakContext.values())
            this.createContext(context);
    }

    /**
     * @return A {@link DynamicRectangle} that defines the overlay's custom scissoring bounds.
     */
    private DynamicRectangle<Overlay> getCustomScissor()
    {
        ToIntFunction<Overlay> startY = overlay -> this.header.getEndY() + overlay.getPadding();

        return new DynamicRectangle<>(this.overlay::getScissorX, startY, this.overlay::getScissorEndX, this.overlay::getScissorEndY);
    }

    /**
     * Create widgets for a status group.
     *
     * @param tweakStatus A {@link StatusContext} for a tweak.
     * @param group       The {@link Group} to add widgets to.
     */
    private void createStatus(StatusContext tweakStatus, Group group)
    {
        boolean isClientOnly = this.tweak.isClient() && group.equals(this.serverGroup);
        boolean isDisconnected = this.tweak.isMultiplayerLike() && group.equals(this.serverGroup) && !NetUtil.isConnected();
        boolean isOff = isClientOnly || isDisconnected;

        IconWidget statusSignal = IconWidget.create(isOff ? Icons.TRAFFIC_LIGHT_OFF : tweakStatus.getIcon())
            .posX(this.padding * -1)
            .build(group::addWidget);

        MutableComponent statusTitle = tweakStatus.getTitle()
            .withStyle(tweakStatus.getColor(), ChatFormatting.UNDERLINE);

        if (isClientOnly)
            statusTitle = Lang.Status.CLIENT_ONLY.withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
        else if (isDisconnected)
            statusTitle = Lang.Status.NO_CONNECTION.withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);

        TextWidget statusText = TextWidget.create(statusTitle)
            .rightOf(statusSignal, this.padding + 1)
            .centerInWidgetY(statusSignal)
            .extendWidthToEnd(group, group.getInsidePaddingX())
            .build(group::addWidget);

        TextWidget.create(isOff ? Lang.EMPTY : tweakStatus.getInfo())
            .belowAll(this.padding * 3, statusSignal, statusText)
            .width(group::getInsideWidth)
            .build(group::addWidget);
    }

    /**
     * Create widgets for a context group.
     *
     * @param context A {@link StatusContext} instance.
     */
    private void createContext(StatusContext context)
    {
        Group group = Group.create(this.overlay)
            .icon(context.getIcon())
            .border(Color.fromFormatting(context.getColor()))
            .title(TextUtil.toTitleCase(context.toString()))
            .build(this.helpGrid::addCell);

        TextWidget.create(context.getInfo()).width(group::getInsideWidth).build(group::addWidget);
    }

    /**
     * Hide, show, and update widgets to get ready for the tweak's status view.
     */
    private void setStatusView()
    {
        this.viewing = Viewing.STATUS;

        this.helpGrid.setInvisible();
        this.statusGrid.setVisible();
    }

    /**
     * Hide, show, and update widgets to get ready for the status help view.
     */
    private void setHelpView()
    {
        this.viewing = Viewing.HELP;

        this.statusGrid.setInvisible();
        this.helpGrid.setVisible();
    }

    /**
     * @return Whether the overlay is viewing the tweak's status.
     */
    private boolean isViewingStatus()
    {
        return this.viewing == Viewing.STATUS;
    }

    /**
     * @return Whether the overlay is viewing the status help page.
     */
    private boolean isViewingHelp()
    {
        return this.viewing == Viewing.HELP;
    }

    /**
     * Handler for rendering header buttons.
     *
     * @param button      The {@link ButtonWidget} instance being rendered.
     * @param graphics    The {@link GuiGraphics} instance.
     * @param mouseX      The x-coordinate of the mouse.
     * @param mouseY      The y-coordinate of the mouse.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    private void renderHeader(ButtonWidget button, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        boolean isActive = false;

        if (this.statusButton == button)
            isActive = this.isViewingStatus();
        else if (this.helpButton == button)
            isActive = this.isViewingHelp();

        Color bar = isActive ? Color.fromFormatting(ChatFormatting.GOLD) : Color.AZURE_WHITE;
        Color fill = bar.fromAlpha(0.2F);

        if (button.isHoveredOrFocused() && isActive)
        {
            bar = Color.RIPE_MANGO;
            fill = bar.fromAlpha(0.2F);
        }

        float startX = button.getX();
        float endX = button.getEndX();
        float barStartY = button.getEndY() - 1;
        float barEndY = button.getEndY();
        float fillStartY = button.getY();

        if (isActive || button.isHoveredOrFocused())
            RenderUtil.fill(graphics, startX, endX, fillStartY, barStartY, fill);

        RenderUtil.fill(graphics, startX, endX, barStartY, barEndY, bar);
    }
}
