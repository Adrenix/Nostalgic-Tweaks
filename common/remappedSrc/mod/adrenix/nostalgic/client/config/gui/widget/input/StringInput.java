package mod.adrenix.nostalgic.client.config.gui.widget.input;

import mod.adrenix.nostalgic.client.config.gui.widget.IPermissionWidget;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.reflect.ClientReflect;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class StringInput implements IPermissionWidget
{
    /* Fields */

    private final TextFieldWidget input;
    private final TweakClientCache<String> cache;

    /* Constructor */

    public StringInput(TweakClientCache<String> cache)
    {
        this.cache = cache;
        this.input = new TextFieldWidget
        (
            MinecraftClient.getInstance().textRenderer,
            ConfigRowList.getControlStartX(),
            0,
            ConfigRowList.BUTTON_WIDTH - 2,
            ConfigRowList.BUTTON_HEIGHT - 2,
            Text.empty()
        );

        this.input.setMaxLength(100);
        this.input.setDrawsBackground(true);
        this.input.setVisible(true);
        this.input.setEditableColor(0xFFFFFF);
        this.input.setText(this.cache.getCurrent());
        this.input.setChangedListener(this::setInput);
    }

    /* Methods */

    public ClickableWidget getWidget() { return this.input; }

    public void setInput(String input)
    {
        String cached = ClientReflect.getCurrent(this.cache.getGroup(), this.cache.getKey());
        if (cached.equals(input))
            this.cache.setCurrent(cached);
        else
            this.cache.setCurrent(input);
    }
}
