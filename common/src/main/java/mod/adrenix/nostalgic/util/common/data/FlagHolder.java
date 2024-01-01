package mod.adrenix.nostalgic.util.common.data;

import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.function.ForEachWithPrevious;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

public class FlagHolder extends Holder<Boolean>
{
    /* Static */

    /**
     * @return A new {@link FlagHolder} instance where the default flag is {@code true}.
     * @see #off()
     * @see #FlagHolder(boolean)
     */
    public static FlagHolder on()
    {
        return new FlagHolder(true);
    }

    /**
     * @return A new {@link FlagHolder} instance where the default flag is {@code false}.
     * @see #on()
     * @see #FlagHolder(boolean)
     */
    public static FlagHolder off()
    {
        return new FlagHolder(false);
    }

    /**
     * Link flags together to form a radio group.
     *
     * @param flags A varargs of {@link FlagHolder} that will be linked together and will be considered part of a radio
     *              group. Different logic will occur in {@link #toggle()} when a flag is part of a radio group.
     */
    public static void radio(FlagHolder... flags)
    {
        List<FlagHolder> radios = Arrays.asList(flags);

        for (FlagHolder holder : flags)
            holder.radios.addAll(radios);
    }

    /* Fields */

    private final LinkedHashSet<FlagHolder> radios;

    /* Constructor */

    /**
     * Reference {@code see also}.
     *
     * @param flag The default flag to create a {@link Holder} reference for.
     * @see FlagHolder#on()
     * @see FlagHolder#off()
     */
    public FlagHolder(boolean flag)
    {
        super(flag);

        this.radios = new LinkedHashSet<>();
    }

    /* Methods */

    /**
     * Toggle the flag stored in this holder.
     */
    public void toggle()
    {
        if (this.radios.isEmpty())
            this.set(!this.get());
        else
        {
            boolean state = this.get();

            if (this.radios.stream().filter(FlagHolder::get).count() > 1)
                this.radios.forEach(FlagHolder::disable);
            else
                CollectionUtil.last(this.radios).ifPresent(FlagHolder::disable);

            NullableHolder<FlagHolder> disable = NullableHolder.empty();
            NullableHolder<FlagHolder> enable = NullableHolder.empty();

            ForEachWithPrevious.create(this.radios).forEach((prev, next) -> {
                if (prev.get() && !prev.equals(this))
                {
                    disable.set(prev);
                    enable.set(next);
                }
            }).run();

            if (disable.isEmpty() || enable.isEmpty())
                this.set(!state);
            else
            {
                disable.ifPresent(FlagHolder::disable);
                enable.ifPresent(FlagHolder::enable);
            }
        }
    }

    /**
     * Enable this flag which will set the holder value to {@code true}.
     */
    public void enable()
    {
        this.set(true);
    }

    /**
     * Disable this flag which will set the holder value to {@code false}.
     */
    public void disable()
    {
        this.set(false);
    }
}
