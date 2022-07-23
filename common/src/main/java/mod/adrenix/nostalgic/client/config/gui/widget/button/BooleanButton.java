package mod.adrenix.nostalgic.client.config.gui.widget.button;

import mod.adrenix.nostalgic.client.config.gui.widget.IPermissionWidget;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.network.chat.Component;

public class BooleanButton extends ControlButton implements IPermissionWidget
{
    protected final TweakClientCache<Boolean> cache;

    public BooleanButton(TweakClientCache<Boolean> cache, OnPress onPress)
    {
        super(Component.empty(), onPress);
        this.cache = cache;
    }

    @Override
    public Component getMessage()
    {
        return Component.translatable(this.cache.getCurrent() ? NostalgicLang.Cloth.YES : NostalgicLang.Cloth.NO);
    }
}
