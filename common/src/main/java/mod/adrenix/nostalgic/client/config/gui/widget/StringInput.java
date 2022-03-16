package mod.adrenix.nostalgic.client.config.gui.widget;

import mod.adrenix.nostalgic.client.config.reflect.ConfigReflect;
import mod.adrenix.nostalgic.client.config.reflect.EntryCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.TextComponent;

public class StringInput
{
    public final EditBox input;
    protected final EntryCache<String> cache;

    public StringInput(EntryCache<String> cache)
    {
        this.cache = cache;
        this.input = new EditBox(
            Minecraft.getInstance().font,
            ConfigRowList.getControlStartX(),
            0,
            ConfigRowList.CONTROL_BUTTON_WIDTH - 2,
            ConfigRowList.BUTTON_HEIGHT - 2,
            TextComponent.EMPTY
        );

        this.input.setMaxLength(100);
        this.input.setBordered(true);
        this.input.setVisible(true);
        this.input.setTextColor(0xFFFFFF);
        this.input.setValue(this.cache.getCurrent());
        this.input.setResponder(this::setInput);
    }

    public AbstractWidget getWidget() { return this.input; }

    public void setInput(String input)
    {
        String cached = ConfigReflect.getCurrent(this.cache.getGroup(), this.cache.getEntryKey());
        if (cached.equals(input))
            this.cache.setCurrent(cached);
        else
            this.cache.setCurrent(input);
    }
}
