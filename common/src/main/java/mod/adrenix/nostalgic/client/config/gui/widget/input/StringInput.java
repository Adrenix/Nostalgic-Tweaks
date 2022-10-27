package mod.adrenix.nostalgic.client.config.gui.widget.input;

import mod.adrenix.nostalgic.client.config.gui.widget.IPermissionWidget;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.reflect.ClientReflect;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class StringInput implements IPermissionWidget
{
    /* Fields */

    private final EditBox input;
    private final TweakClientCache<String> cache;

    /* Constructor */

    public StringInput(TweakClientCache<String> cache)
    {
        this.cache = cache;
        this.input = new EditBox
        (
            Minecraft.getInstance().font,
            ConfigRowList.getControlStartX(),
            0,
            ConfigRowList.BUTTON_WIDTH - 2,
            ConfigRowList.BUTTON_HEIGHT - 2,
            Component.empty()
        );

        this.input.setMaxLength(100);
        this.input.setBordered(true);
        this.input.setVisible(true);
        this.input.setTextColor(0xFFFFFF);
        this.input.setValue(this.cache.getCurrent());
        this.input.setResponder(this::setInput);
    }

    /* Methods */

    public AbstractWidget getWidget() { return this.input; }

    public void setInput(String input)
    {
        String cached = ClientReflect.getCurrent(this.cache.getGroup(), this.cache.getKey());
        if (cached.equals(input))
            this.cache.setCurrent(cached);
        else
            this.cache.setCurrent(input);
    }
}
