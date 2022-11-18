package mod.adrenix.nostalgic.common.config.tweak;

/**
 * Interface used in conjunction with enumerations that allows any enumeration to define what the default disabled value
 * should be. The disabled value should provide a value for when the mod is put into a "disabled" state.
 *
 * @param <E> The enumeration type.
 */

public interface DisabledTweak<E extends Enum<E>>
{
    /**
     * Any enumeration that inherits this interface indicates that a specific value should be used when the mod is put
     * into a "disabled" state.
     *
     * @return A value to use when the mod is put into a "disabled" state.
     */
    E getDisabledValue();
}
