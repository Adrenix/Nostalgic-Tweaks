package mod.adrenix.nostalgic.client.gui.widget.keybinding;

import mod.adrenix.nostalgic.client.ClientKeyMapping;
import mod.adrenix.nostalgic.client.gui.widget.button.AbstractButtonMaker;
import mod.adrenix.nostalgic.tweak.gui.KeybindingId;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;

public class KeybindingBuilder extends AbstractButtonMaker<KeybindingBuilder, KeybindingWidget>
{
    /* Fields */

    final KeyMapping mapping;

    /* Constructor */

    public KeybindingBuilder(KeybindingId id)
    {
        super(Component.empty());

        this.mapping = ClientKeyMapping.getFromId(id);
    }

    /* Methods */

    @Override
    public KeybindingBuilder self()
    {
        return this;
    }

    @Override
    protected KeybindingWidget construct()
    {
        return new KeybindingWidget(this, this::onPress);
    }
}
