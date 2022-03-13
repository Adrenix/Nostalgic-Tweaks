package mod.adrenix.nostalgic.client.config.gui.widget;

import mod.adrenix.nostalgic.client.config.reflect.EntryCache;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class BooleanButton extends Button
{
    protected final EntryCache<Boolean> cache;

    public BooleanButton(EntryCache<Boolean> cache, OnPress onPress)
    {
        super(ConfigRowList.getControlStartX(), 0, ConfigRowList.CONTROL_BUTTON_WIDTH, ConfigRowList.BUTTON_HEIGHT, TextComponent.EMPTY, onPress);
        this.cache = cache;
    }

    @Override
    public Component getMessage()
    {
        return new TranslatableComponent(this.cache.getCurrent() ? NostalgicLang.Cloth.YES : NostalgicLang.Cloth.NO);
    }
}
