package mod.adrenix.nostalgic.client.gui.screen.config.overlay.manage;

import com.google.common.base.Splitter;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.overlay.types.info.MessageOverlay;
import mod.adrenix.nostalgic.client.gui.overlay.types.info.MessageType;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonTemplate;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.grid.Grid;
import mod.adrenix.nostalgic.client.gui.widget.list.Row;
import mod.adrenix.nostalgic.client.gui.widget.list.RowList;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.data.Holder;
import mod.adrenix.nostalgic.util.common.io.PathUtil;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.text.TextUtil;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public abstract class FileInspector
{
    /**
     * Create and open a new overlay window that displays the contents of a config file.
     *
     * @param path     A {@link Supplier} that provides the filesystem {@link Path}.
     * @param filename A {@link Supplier} that provides the filename.
     */
    public static void open(Supplier<Path> path, Supplier<String> filename)
    {
        Holder<String> content = Holder.create("");

        if (Files.exists(path.get()))
        {
            try
            {
                content.set(new String(Files.readAllBytes(path.get())));
            }
            catch (IOException exception)
            {
                NostalgicTweaks.LOGGER.error("[I/O Error] Could not read file (%s)\n%s", filename.get(), exception);

                MessageOverlay.create(MessageType.RED_WARNING, Lang.Error.IO_TITLE, Lang.Error.INSPECT_FILE)
                    .addButton(ButtonTemplate.openFolder(PathUtil.getLogsPath()))
                    .setResizePercentage(0.65D)
                    .build()
                    .open();
            }
        }
        else
        {
            NostalgicTweaks.LOGGER.error("[I/O Error] File does not exist (%s)\n%s", filename.get());

            MessageOverlay.create(MessageType.RED_WARNING, Lang.Error.IO_TITLE, Lang.Error.NONEXISTENT_FILE)
                .addButton(ButtonTemplate.openFolder(PathUtil.getLogsPath()))
                .setResizePercentage(0.65D)
                .build()
                .open();
        }

        Overlay overlay = Overlay.create(Lang.literal(filename.get()))
            .resizeHeightUsingPercentage(0.95D)
            .resizeWidthUsingPercentage(0.75D)
            .build();

        Grid grid = Grid.create(overlay, 2)
            .extendWidthToScreenEnd(1)
            .fromScreenEndY(1)
            .columnSpacing(1)
            .build(overlay::addWidget);

        SeparatorWidget separator = SeparatorWidget.create(Color.WHITE)
            .extendWidthToScreenEnd(1)
            .above(grid, 1)
            .height(1)
            .build(overlay::addWidget);

        RowList rowList = RowList.create()
            .defaultRowHeight(GuiUtil.textHeight())
            .extendHeightTo(separator, 0)
            .extendWidthToScreenEnd(0)
            .verticalMargin(2)
            .build(overlay::addWidget);

        for (String line : Splitter.onPattern("\n").splitToList(TextUtil.colorJson(content.get())))
        {
            Row row = Row.create(rowList).build();

            TextWidget.create(line).extendWidthToEnd(row, 0).build(row::addWidget);
            rowList.addBottomRow(row);
        }

        ButtonWidget.create(Lang.Button.COPY)
            .icon(Icons.COPY)
            .tooltip(Lang.Button.COPY, 40, 700L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.COPY, 40)
            .onPress(() -> Minecraft.getInstance().keyboardHandler.setClipboard(content.get()))
            .below(separator, 1)
            .build(grid::addCell);

        ButtonWidget.create(Lang.Vanilla.GUI_DONE)
            .icon(Icons.GREEN_CHECK)
            .onPress(() -> GuiUtil.getScreenAs(Overlay.class).ifPresent(Overlay::close))
            .build(grid::addCell);

        overlay.open();
    }
}
