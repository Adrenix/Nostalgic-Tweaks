package mod.adrenix.nostalgic.client.gui.screen.packs;

import mod.adrenix.nostalgic.client.gui.screen.EnhancedScreen;
import mod.adrenix.nostalgic.client.gui.screen.home.Panorama;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

public class PacksListScreen extends EnhancedScreen<PacksListScreen, PacksListWidgets>
{
    /* Fields */

    private PacksListWidgets listWidgets;

    /* Constructor */

    public PacksListScreen(@Nullable Screen parentScreen)
    {
        super(PacksListWidgets::new, parentScreen, Lang.Enum.SCREEN_PACKS.get());
    }

    /* Methods */

    /**
     * {@inheritDoc}
     */
    @Override
    public PacksListScreen self()
    {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PacksListWidgets getWidgetManager()
    {
        return this.listWidgets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWidgetManager(PacksListWidgets widgetManager)
    {
        this.listWidgets = widgetManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        this.renderBlurredBackground();

        if (this.minecraft.level == null)
            Panorama.render(graphics);
        else
            graphics.fillGradient(0, 0, this.width, this.height, 0xC0101010, 0xD0101010);

        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
