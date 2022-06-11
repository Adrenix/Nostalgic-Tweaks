package mod.adrenix.nostalgic.client.config.gui.widget.button;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.gui.widget.ConfigRowList;
import mod.adrenix.nostalgic.client.config.reflect.TweakCache;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.util.Arrays;
import java.util.Optional;

public class CycleButton<E extends Enum<E>> extends Button
{
    private final TweakCache<E> cache;
    private final Class<E> values;

    public CycleButton(TweakCache<E> cache, Class<E> values, OnPress onPress)
    {
        super(ConfigRowList.getControlStartX(), 0, ConfigRowList.CONTROL_BUTTON_WIDTH, ConfigRowList.BUTTON_HEIGHT, TextComponent.EMPTY, onPress);
        this.cache = cache;
        this.values = values;
    }

    public void toggle()
    {
        E[] enums = this.values.getEnumConstants();
        if (enums.length == 0)
        {
            NostalgicTweaks.LOGGER.warn("Tried to toggle an empty enumeration list. This shouldn't happen!");
            return;
        }

        Optional<E> firstSearch = Arrays.stream(enums).findFirst();
        E firstConstant = firstSearch.orElse(this.cache.getCurrent());
        E nextConstant = firstConstant;
        E currentConstant = this.cache.getCurrent();
        boolean isCurrent = false;

        if (enums[enums.length - 1] == currentConstant)
        {
            this.cache.setCurrent(firstConstant);
            return;
        }

        for (E next : enums)
        {
            if (isCurrent)
            {
                nextConstant = next;
                break;
            }

            if (next == currentConstant)
                isCurrent = true;
        }

        this.cache.setCurrent(nextConstant);
    }

    @Override
    public Component getMessage()
    {
        return new TextComponent(this.cache.getCurrent().toString());
    }
}
