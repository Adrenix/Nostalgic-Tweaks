package mod.adrenix.nostalgic.client.config.gui.widget.button;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.gui.widget.PermissionLock;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.Optional;

/**
 * This control button cycles between different values defined in an enumeration class that is connected to a tweak.
 * Since this control can be used to toggle server tweaks, the permission lock interface has been attached.
 *
 * @param <E> The enumeration type connected to this controller.
 */

public class CycleButton<E extends Enum<E>> extends ControlButton implements PermissionLock
{
    /* Fields */

    private final TweakClientCache<E> tweak;
    private final Class<E> values;

    /* Constructor */

    /**
     * Create a new cycle button controller for a tweak.
     * @param tweak A tweak client cache entry.
     * @param values An enumeration class.
     * @param onPress Instructions to perform when clicked.
     */
    public CycleButton(TweakClientCache<E> tweak, Class<E> values, OnPress onPress)
    {
        super(Component.empty(), onPress);

        this.tweak = tweak;
        this.values = values;
    }

    /* Methods */

    /**
     * This method toggles the value connected to the tweak cache. Values can go in two directions, forwards and
     * backwards. When the shift key is held down, every click will cycle this controller backwards. Otherwise, the
     * cycle will always move forwards.
     */
    public void toggle()
    {
        E[] enums = this.values.getEnumConstants();

        if (enums.length == 0)
        {
            String name = enums.getClass().getName();
            NostalgicTweaks.LOGGER.warn("Tried to toggle %s an empty enumeration list. This shouldn't happen!", name);

            return;
        }

        Optional<E> firstSearch = Arrays.stream(enums).findFirst();
        E firstConstant = firstSearch.orElse(this.tweak.getValue());
        E nextConstant = firstConstant;
        E lastConstant = enums[enums.length - 1];
        E currentConstant = this.tweak.getValue();

        if (Screen.hasShiftDown())
        {
            if (firstConstant == currentConstant)
            {
                this.tweak.setValue(lastConstant);
                return;
            }

            E previousConstant = currentConstant;

            for (E next : enums)
            {
                if (next == currentConstant)
                {
                    this.tweak.setValue(previousConstant);
                    return;
                }

                previousConstant = next;
            }
        }
        else
        {
            if (lastConstant == currentConstant)
            {
                this.tweak.setValue(firstConstant);
                return;
            }

            boolean isCurrent = false;

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

            this.tweak.setValue(nextConstant);
        }
    }

    /**
     * This message will be dependent on the current enumeration value.
     * @return The message component associated with this controller.
     */
    @Override
    public Component getMessage() { return Component.literal(this.tweak.getValue().toString()); }
}
