package mod.adrenix.nostalgic.client.gui.widget.keybinding;

import com.mojang.blaze3d.platform.InputConstants;
import mod.adrenix.nostalgic.client.gui.widget.button.AbstractButton;
import mod.adrenix.nostalgic.tweak.gui.KeybindingId;
import mod.adrenix.nostalgic.util.client.KeyboardUtil;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class KeybindingWidget extends AbstractButton<KeybindingBuilder, KeybindingWidget>
{
    /* Builder */

    /**
     * Create a new {@link KeybindingWidget}.
     *
     * @param id A {@link KeybindingId} enumeration that points to a {@link KeyMapping}.
     * @return A new {@link KeybindingBuilder} instance.
     */
    public static KeybindingBuilder create(KeybindingId id)
    {
        return new KeybindingBuilder(id);
    }

    /* Fields */

    private final KeyMapping mapping;

    /* Constructor */

    protected KeybindingWidget(KeybindingBuilder builder, Consumer<KeybindingWidget> onPress)
    {
        super(builder, onPress);

        this.mapping = builder.mapping;

        builder.title(this::setTitle);
    }

    /* Methods */

    /**
     * Reset the key mapping associated with this controller.
     */
    public void reset()
    {
        Minecraft.getInstance().options.setKey(this.mapping, this.mapping.getDefaultKey());
        KeyMapping.resetMapping();

        this.setFocused(false);
    }

    /**
     * Change the key associated with this controller's key mapping.
     *
     * @param keyCode  The key code that was pressed.
     * @param scanCode A key scan code.
     */
    protected void setKey(int keyCode, int scanCode)
    {
        if (KeyboardUtil.isEsc(keyCode))
            Minecraft.getInstance().options.setKey(this.mapping, InputConstants.UNKNOWN);
        else
            Minecraft.getInstance().options.setKey(this.mapping, InputConstants.getKey(keyCode, scanCode));

        KeyMapping.resetMapping();

        this.setFocused(false);
    }

    /**
     * Change the button title based on focus context.
     *
     * @return A {@link Component}.
     */
    protected Component setTitle()
    {
        if (this.isFocused())
        {
            return Component.literal("> ")
                .append(this.mapping.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.YELLOW))
                .append(" <")
                .withStyle(ChatFormatting.YELLOW);
        }
        else if (this.mapping.isUnbound())
            return Lang.Binding.UNBOUND.withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC);
        else if (KeyboardUtil.isMappingConflict(this.mapping))
            return this.mapping.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.RED);

        return this.mapping.getTranslatedKeyMessage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (this.isFocused() && this.isActive() && modifiers == 0)
        {
            this.setKey(keyCode, scanCode);
            this.setFocused(false);

            return true;
        }

        return false;
    }
}
