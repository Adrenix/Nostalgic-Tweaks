package mod.adrenix.nostalgic.client.gui.screen.config.overlay.manage;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonBuilder;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.network.packet.backup.BackupObject;
import mod.adrenix.nostalgic.util.client.gui.DrawText;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public enum ManageSection
{
    MENU_OPTIONS(Icons.MECHANICAL_TOOLS, Lang.Button.MENU_OPTIONS, new GroupMenuOptions()),
    IMPORT_AND_EXPORT(Icons.SAVE_FLOPPY, Lang.Button.IMPORT_AND_EXPORT, new GroupImportExport()),
    CREATE_BACKUP(Icons.ADD, Lang.Button.CREATE_BACKUP, new GroupCreateBackup()),
    VIEW_BACKUPS(Icons.BOOK_OPEN, Lang.Button.VIEW_BACKUPS, new GroupViewBackups()),
    CONFIG_PRESETS(Icons.COPY, Lang.Button.CONFIG_PRESETS, new GroupConfigPresets()),
    SERVER_OPERATIONS(Icons.SERVER, Lang.Button.SERVER_OPERATIONS, new GroupServerOps()),
    TOGGLE_ALL_TWEAKS(Icons.LIGHTNING, Lang.Button.TOGGLE_ALL_TWEAKS, new GroupToggleAll()),
    HELP(Icons.TOOLTIP, Lang.Button.HELP, new GroupHelp());

    /* Fields */

    @Nullable private Overlay overlay;
    private final ManageGroup manager;
    private final TextureIcon icon;
    private final Translation title;
    private boolean active;

    /* Constructor */

    ManageSection(TextureIcon icon, Translation title, ManageGroup manager)
    {
        this.icon = icon;
        this.title = title;
        this.manager = manager;
        this.overlay = null;
    }

    /* Static */

    /**
     * @return A {@link Stream} of {@link ManageSection} values.
     */
    public static Stream<ManageSection> stream()
    {
        return Arrays.stream(ManageSection.values());
    }

    /**
     * If no section is active, then {@link ManageSection#MENU_OPTIONS} is activated and returned.
     *
     * @return The current section being view in the config manager overlay.
     */
    public static ManageSection getActive()
    {
        return ManageSection.stream().filter(ManageSection::isActive).findFirst().orElse(ManageSection.MENU_OPTIONS);
    }

    /**
     * @return The largest button width built by all sections.
     */
    public static int getLargestWidth()
    {
        List<ButtonWidget> buttons = ManageSection.stream()
            .map(ManageSection::button)
            .map(ButtonBuilder::useTextWidth)
            .map(ButtonBuilder::build)
            .toList();

        return buttons.stream().mapToInt(ButtonWidget::getWidth).max().orElse(0);
    }

    /**
     * Populate the view backups section's server backup objects.
     *
     * @param backups A {@link Set} of {@link BackupObject}.
     */
    public static void setServerBackups(Set<BackupObject> backups)
    {
        if (ManageSection.VIEW_BACKUPS.getManager() instanceof GroupViewBackups view)
            view.setServerBackups(backups);
    }

    /**
     * Inform the view backups section that server backup object retrieval failed.
     */
    public static void setReceiveFailed()
    {
        if (ManageSection.VIEW_BACKUPS.getManager() instanceof GroupViewBackups view)
            view.setReceiveFailed();
    }

    /* Methods */

    /**
     * @return The {@link ManageGroup} widget manager for this section of widgets.
     */
    ManageGroup getManager()
    {
        return this.manager;
    }

    /**
     * Set the current overlay instance for each section manager.
     *
     * @param overlay An {@link Overlay} instance.
     */
    public void setOverlay(@Nullable Overlay overlay)
    {
        this.overlay = overlay;
    }

    /**
     * Deactivates the currently active section.
     */
    public void activate()
    {
        ManageSection.stream().map(ManageSection::getManager).forEach(ManageGroup::setInvisible);
        ManageSection.getActive().active = false;

        this.manager.setVisible();
        this.active = true;

        if (this.overlay != null)
            this.overlay.resetScrollAmount();
    }

    /**
     * @return Whether this section is currently active.
     */
    public boolean isActive()
    {
        return this.active;
    }

    /**
     * @return A {@link ButtonBuilder} instance for this section.
     */
    public ButtonBuilder button()
    {
        return ButtonWidget.create(this.title)
            .icon(this.icon)
            .disabledIcon(this.icon)
            .disableIf(this::isActive)
            .onPress(this::activate)
            .renderer(this::render);
    }

    /**
     * Helper method for rendering buttons that change overlay widgets.
     *
     * @param button      The section {@link ButtonWidget} instance.
     * @param graphics    The current {@link GuiGraphics}.
     * @param mouseX      The mouse x-coordinate.
     * @param mouseY      The mouse y-coordinate.
     * @param partialTick The normalized progress made between two ticks [0.0F-1.0F].
     */
    private void render(ButtonWidget button, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if (button.isHoveredOrFocused() || button.isInactive())
        {
            Color barColor = button.isInactive() ? Color.fromFormatting(ChatFormatting.GOLD) : Color.AZURE_WHITE;
            Color fillColor = button.isInactive() ? Color.fromFormatting(ChatFormatting.GOLD) : Color.CADET_GRAY;

            if (button.isHoveredOrFocused() && button.isInactive())
            {
                barColor = Color.RIPE_MANGO;
                fillColor = Color.RIPE_MANGO;
            }

            fillColor = fillColor.fromAlpha(0.2F);

            RenderUtil.fill(graphics, button.getX(), button.getY(), button.getEndX(), button.getEndY(), fillColor.get());
            RenderUtil.fill(graphics, button.getX(), button.getY(), button.getX() + 2, button.getEndY(), barColor.get());
        }

        button.getIconManager().apply(icon -> icon.pos(button.getX() + 6, button.getIconY()));
        button.getIconManager().render(graphics, mouseX, mouseY, partialTick);

        DrawText.begin(graphics, button.getTitle())
            .pos(button.getIconManager().get().getEndX() + 4, button.getTextY())
            .color(button.isHoveredOrFocused() ? Color.fromFormatting(ChatFormatting.YELLOW) : Color.WHITE)
            .draw();
    }
}
