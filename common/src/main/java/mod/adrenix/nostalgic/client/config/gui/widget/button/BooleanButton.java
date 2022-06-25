package mod.adrenix.nostalgic.client.config.gui.widget.button;

import mod.adrenix.nostalgic.client.config.gui.widget.ConfigRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.IPermissionWidget;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class BooleanButton extends Button implements IPermissionWidget
{
    protected final TweakClientCache<Boolean> cache;

    public BooleanButton(TweakClientCache<Boolean> cache, OnPress onPress)
    {
        super(ConfigRowList.getControlStartX(), 0, ConfigRowList.CONTROL_BUTTON_WIDTH, ConfigRowList.BUTTON_HEIGHT, Component.empty(), onPress);
        this.cache = cache;
    }

    @Override
    public Component getMessage()
    {
        return Component.translatable(this.cache.getCurrent() ? NostalgicLang.Cloth.YES : NostalgicLang.Cloth.NO);
    }
}
