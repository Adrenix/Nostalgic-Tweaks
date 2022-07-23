package mod.adrenix.nostalgic.client.config.gui.widget.slider;

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import mod.adrenix.nostalgic.client.config.annotation.TweakClient;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.IPermissionWidget;
import mod.adrenix.nostalgic.common.config.reflect.CommonReflect;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;

public class ConfigSlider extends GenericSlider implements IPermissionWidget
{
    protected final TweakClientCache<Integer> cache;

    public ConfigSlider(TweakClientCache<Integer> cache)
    {
        super(
            cache::setCurrent,
            cache::getCurrent,
            null,
            ConfigRowList.getControlStartX(),
            0,
            ConfigRowList.BUTTON_WIDTH,
            ConfigRowList.BUTTON_HEIGHT
        );

        this.cache = cache;

        ConfigEntry.BoundedDiscrete bounds = CommonReflect.getAnnotation(cache, ConfigEntry.BoundedDiscrete.class);

        if (bounds != null)
        {
            this.setMinimum((int) bounds.min());
            this.setMaximum((int) bounds.max());
        }

        TweakClient.Gui.SliderType sliderType = CommonReflect.getAnnotation(cache, TweakClient.Gui.SliderType.class);

        if (sliderType != null)
            this.setSlider(sliderType.slider());
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        if (!this.cache.isResettable() && !this.isHovered)
            this.setValue(this.cache.getCurrent());

        this.updateMessage();
        super.render(poseStack, mouseX, mouseY, partialTick);
    }
}
