package mod.adrenix.nostalgic.common.config.tweak;

/**
 * Interface used in conjunction with enumerations that allows any enumeration to define what the default disabled value
 * should be. The disabled value should be a type for when the mod is put into a "disabled" state.
 *
 * @param <E> The enumeration type.
 */

public interface IDisableTweak<E extends Enum<E>>
{
    E getDisabled();
}
