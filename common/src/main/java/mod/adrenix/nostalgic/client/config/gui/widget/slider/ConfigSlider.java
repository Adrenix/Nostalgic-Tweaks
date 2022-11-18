package mod.adrenix.nostalgic.client.config.gui.widget.slider;

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import mod.adrenix.nostalgic.client.config.annotation.TweakClient;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.PermissionLock;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;

/**
 * This type of slider widget is used exclusively by the configuration screen.
 * Since some tweaks may control server settings, this widget is permission locked.
 */

public class ConfigSlider extends GenericSlider implements PermissionLock
{
    /* Fields */

    /**
     * The tweak client cache integer instance for this slider.
     * This cache is necessary to retrieve metadata during construction and is used for rendering logic.
     */
    private final TweakClientCache<Integer> tweak;

    /* Constructor */

    /**
     * Create a new configuration slider instance.
     * @param tweak The client integer tweak cache reference.
     */
    public ConfigSlider(TweakClientCache<Integer> tweak)
    {
        super(tweak, null, ConfigRowList.getControlStartX(), 0, ConfigRowList.BUTTON_WIDTH, ConfigRowList.BUTTON_HEIGHT);

        this.tweak = tweak;
        ConfigEntry.BoundedDiscrete bounds = this.tweak.getMetadata(ConfigEntry.BoundedDiscrete.class);

        if (bounds != null)
        {
            this.setMinimum((int) bounds.min());
            this.setMaximum((int) bounds.max());
        }

        TweakClient.Gui.SliderType sliderType = this.tweak.getMetadata(TweakClient.Gui.SliderType.class);

        if (sliderType != null)
            this.setSlider(sliderType.slider());
    }

    /* Methods */

    /**
     * Handler method that provides logic for widget rendering.
     * @param poseStack The current pose stack.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick A change in game frame time.
     */
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        if (!this.tweak.isResettable() && !this.isHovered)
            this.setValue(this.tweak.getValue());

        this.updateMessage();

        super.render(poseStack, mouseX, mouseY, partialTick);
    }
}
