package mod.adrenix.nostalgic.tweak.factory;

import mod.adrenix.nostalgic.tweak.TweakEnv;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.tweak.gui.SliderType;
import mod.adrenix.nostalgic.tweak.gui.TweakSlider;
import mod.adrenix.nostalgic.tweak.listing.ListingMap;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public abstract class TweakMap<V, L extends ListingMap<V, L>> extends TweakListing<V, L>
{
    /* Fields */

    private final Builder<?, V, L> builder;

    /* Constructor */

    TweakMap(TweakMap.Builder<?, V, L> builder)
    {
        super(builder);

        this.builder = builder;
    }

    /* Methods */

    /**
     * If this map uses a number for its entries. Then a slider can be defined that will be used in the automatic
     * listing user interface.
     *
     * @return This map's slider, if it is defined.
     */
    public Optional<TweakSlider> getSlider()
    {
        if (this.builder.slider != null)
            return Optional.of(this.builder.slider.build());

        return Optional.empty();
    }

    /* Casting */

    /**
     * Check if the given tweak is an instance of a {@link TweakMap}.
     *
     * @param tweak A that at least extends {@link TweakListing}.
     * @return An {@link Optional} {@link TweakMap}.
     */
    public static Optional<TweakMap<?, ?>> cast(TweakListing<?, ?> tweak)
    {
        if (tweak instanceof TweakMap<?, ?> map)
            return Optional.of(map);

        return Optional.empty();
    }

    /* Builder */

    public abstract static class Builder<B extends Builder<B, W, U>, W, U extends ListingMap<W, U>>
        extends TweakListing.Builder<B, W, U> implements TweakSlider.Factory<B>
    {
        /* Static */

        private static final String NON_NUMBER_MAP = "Cannot define slider data for non-number map";

        /* Fields */

        @Nullable final TweakSlider.Builder slider;

        /* Constructor */

        Builder(U defaultList, TweakEnv env, Container container)
        {
            super(defaultList, env, container);

            if (Number.class.isAssignableFrom(this.defaultList.genericType()))
                this.slider = TweakSlider.create(null);
            else
                this.slider = null;
        }

        /* Methods */

        @Override
        public B range(Number min, Number max)
        {
            if (this.slider == null)
                throw new RuntimeException(NON_NUMBER_MAP);

            this.slider.range(min, max);

            return this.self();
        }

        @Override
        public B interval(Number interval)
        {
            if (this.slider == null)
                throw new RuntimeException(NON_NUMBER_MAP);

            this.slider.interval(interval);

            return this.self();
        }

        @Override
        public B roundTo(int place)
        {
            if (this.slider == null)
                throw new RuntimeException(NON_NUMBER_MAP);

            this.slider.roundTo(place);

            return this.self();
        }

        @Override
        public B formatter(Function<Number, String> formatter)
        {
            if (this.slider == null)
                throw new RuntimeException(NON_NUMBER_MAP);

            this.slider.formatter(formatter);

            return this.self();
        }

        @Override
        public B langKey(Translation langKey)
        {
            if (this.slider == null)
                throw new RuntimeException(NON_NUMBER_MAP);

            this.slider.langKey(langKey);

            return this.self();
        }

        @Override
        public B suffix(String suffix)
        {
            if (this.slider == null)
                throw new RuntimeException(NON_NUMBER_MAP);

            this.slider.suffix(suffix);

            return this.self();
        }

        @Override
        public B type(SliderType type)
        {
            if (this.slider == null)
                throw new RuntimeException(NON_NUMBER_MAP);

            this.slider.type(type);

            return this.self();
        }
    }
}
