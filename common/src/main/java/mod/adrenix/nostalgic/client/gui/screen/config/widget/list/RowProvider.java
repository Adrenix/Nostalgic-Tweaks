package mod.adrenix.nostalgic.client.gui.screen.config.widget.list;

import mod.adrenix.nostalgic.tweak.FavoriteTweak;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.function.Predicate;

/**
 * These enumerations define the providers that produce rows for the row list widget.
 *
 * <ul>
 *   <li>{@code DEFAULT} - The default provider is being used to display rows.</li>
 *   <li>{@code ALL} - The view all provider is being used to show all rows.</li>
 *   <li>{@code FAVORITE} - The favorite button is being used to display rows.</li>
 * </ul>
 */
public enum RowProvider
{
    DEFAULT(Tweak::isNotIgnored, Lang.EMPTY, Lang.EMPTY),
    ALL(Tweak::isNotIgnored, Lang.Tooltip.ALL, Lang.Tooltip.ALL_DISABLED),
    FAVORITE(FavoriteTweak::isPresent, Lang.Tooltip.FAVORITE, Lang.Tooltip.FAVORITE_DISABLED);

    /* Fields */

    private final Predicate<Tweak<?>> predicate;
    private final Translation enabledKey;
    private final Translation disabledKey;
    private boolean providing;

    /* Constructor */

    RowProvider(Predicate<Tweak<?>> predicate, Translation enabledKey, Translation disabledKey)
    {
        this.predicate = predicate;
        this.enabledKey = enabledKey;
        this.disabledKey = disabledKey;
        this.providing = false;
    }

    /* Static */

    /**
     * @return The current row provider, if none is providing, then {@link RowProvider#DEFAULT} is returned.
     */
    public static RowProvider get()
    {
        return Arrays.stream(RowProvider.values())
            .filter(RowProvider::isProviding)
            .findFirst()
            .orElse(RowProvider.DEFAULT);
    }

    /**
     * Disables all row providers.
     */
    private static void reset()
    {
        Arrays.stream(RowProvider.values()).forEach(provider -> provider.providing = false);
    }

    /* Methods */

    /**
     * Change the list state of the config screen.
     */
    public void use()
    {
        RowProvider.reset();

        this.providing = true;
    }

    /**
     * Change the list state of the config screen and then run the given runnable.
     *
     * @param runnable A {@link Runnable} instance.
     */
    public void useAndThen(Runnable runnable)
    {
        this.use();
        runnable.run();
    }

    /**
     * Start using this row provider and return its predicate.
     *
     * @return A {@link Predicate} that tests a {@link Tweak}.
     */
    public Predicate<Tweak<?>> useAndGetPredicate()
    {
        this.use();

        return this.predicate;
    }

    /**
     * Perform a predicate test on the given tweak.
     *
     * @param tweak A {@link Tweak} instance.
     * @return Whether the given tweak passed the predicate test.
     */
    public boolean test(Tweak<?> tweak)
    {
        return this.predicate.test(tweak);
    }

    /**
     * @return Whether this provider is providing.
     */
    public boolean isProviding()
    {
        return this.providing;
    }

    /**
     * @return A {@link Component} information tooltip that may be associated with this state.
     */
    public Component getInfoTooltip()
    {
        return this.isProviding() ? this.disabledKey.get() : this.enabledKey.get();
    }
}
