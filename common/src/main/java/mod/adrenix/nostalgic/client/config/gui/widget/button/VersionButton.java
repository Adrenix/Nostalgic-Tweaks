package mod.adrenix.nostalgic.client.config.gui.widget.button;

import mod.adrenix.nostalgic.client.config.DefaultConfig;
import mod.adrenix.nostalgic.client.config.gui.widget.ConfigRowList;
import mod.adrenix.nostalgic.client.config.reflect.TweakCache;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class VersionButton extends Button
{
    protected final TweakCache<DefaultConfig.VERSION> cache;

    public VersionButton(TweakCache<DefaultConfig.VERSION> cache, OnPress onPress)
    {
        super(ConfigRowList.getControlStartX(), 0, ConfigRowList.CONTROL_BUTTON_WIDTH, ConfigRowList.BUTTON_HEIGHT, TextComponent.EMPTY, onPress);
        this.cache = cache;
    }

    public void toggle()
    {
        DefaultConfig.VERSION next = switch (this.cache.getCurrent()) {
            case MODERN -> DefaultConfig.VERSION.ALPHA;
            case ALPHA -> DefaultConfig.VERSION.BETA;
            case BETA -> DefaultConfig.VERSION.MODERN;
        };

        this.cache.setCurrent(next);
    }

    @Override
    public Component getMessage()
    {
        String langKey = switch (this.cache.getCurrent()) {
            case MODERN -> NostalgicLang.Gui.SETTINGS_MODERN;
            case ALPHA -> NostalgicLang.Gui.SETTINGS_ALPHA;
            case BETA -> NostalgicLang.Gui.SETTINGS_BETA;
        };

        return new TranslatableComponent(langKey);
    }
}
