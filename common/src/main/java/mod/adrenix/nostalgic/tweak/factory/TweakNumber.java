package mod.adrenix.nostalgic.tweak.factory;

import mod.adrenix.nostalgic.network.packet.tweak.ClientboundTweakNumber;
import mod.adrenix.nostalgic.network.packet.tweak.ServerboundTweakNumber;
import mod.adrenix.nostalgic.network.packet.tweak.TweakPacket;
import mod.adrenix.nostalgic.tweak.TweakEnv;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.tweak.gui.SliderType;
import mod.adrenix.nostalgic.tweak.gui.TweakSlider;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class TweakNumber<T extends Number> extends TweakValue<T>
{
    /* Factories */

    /**
     * Build a new {@link TweakNumber} instance that is only available for the client. Reference the {@code see also}
     * link for more information about client tweaks.
     *
     * @param defaultNumber The tweak's default value.
     * @param container     The tweak's {@link Container}, either a {@code category} or {@code group}.
     * @param <N>           The number class type of the default value.
     * @return A new {@link TweakNumber.Builder} instance.
     * @see TweakEnv#CLIENT
     */
    public static <N extends Number> TweakNumber.Builder<N> client(N defaultNumber, Container container)
    {
        return new Builder<>(defaultNumber, TweakEnv.CLIENT, container);
    }

    /**
     * Build a new {@link TweakNumber} instance that is available for both the client and server. Reference the
     * {@code see also} link for more information about server tweaks.
     *
     * @param defaultNumber The tweak's default value.
     * @param container     The tweak's {@link Container}, either a {@code category} or {@code group}.
     * @param <N>           The number class type of the default value.
     * @return A new {@link TweakNumber.Builder} instance.
     * @see TweakEnv#SERVER
     */
    public static <N extends Number> TweakNumber.Builder<N> server(N defaultNumber, Container container)
    {
        return new Builder<>(defaultNumber, TweakEnv.SERVER, container);
    }

    /**
     * Build a new {@link TweakNumber} instance that is dynamic. Reference the {@code see also} link for more
     * information about dynamic tweaks.
     *
     * @param defaultNumber The tweak's default value.
     * @param container     The tweak's {@link Container}, either a {@code category} or {@code group}.
     * @param <N>           The number class type of the default value.
     * @return A new {@link TweakNumber.Builder} instance.
     * @see TweakEnv#DYNAMIC
     */
    public static <N extends Number> TweakNumber.Builder<N> dynamic(N defaultNumber, Container container)
    {
        return new Builder<>(defaultNumber, TweakEnv.DYNAMIC, container);
    }

    /* Fields */

    private final TweakNumber.Builder<T> builder;

    /* Constructor */

    TweakNumber(TweakNumber.Builder<T> builder)
    {
        super(builder);

        this.builder = builder;
    }

    /* Methods */

    @Override
    public void setCacheValue(Number number)
    {
        if (this.isLocalMode())
            this.setLocal(number);
        else
            this.setNetwork(number);
    }

    @Override
    public void setLocal(Number value)
    {
        this.getCacheHolder().setLocal(value);
    }

    @Override
    public void setNetwork(Number value)
    {
        this.getCacheHolder().setNetwork(value);
    }

    @Override
    public @Nullable TweakPacket getClientboundPacket()
    {
        return new ClientboundTweakNumber(this);
    }

    @Override
    public @Nullable TweakPacket getServerboundPacket()
    {
        return new ServerboundTweakNumber(this);
    }

    /**
     * @return This tweak's slider, if it is defined.
     */
    public TweakSlider getSlider()
    {
        return this.builder.slider.build();
    }

    /* Builder */

    public static class Builder<U extends Number> extends TweakValue.Builder<U, Builder<U>>
        implements TweakSlider.Factory<Builder<U>>
    {
        /* Fields */

        final TweakSlider.Builder slider;

        /* Constructor */

        Builder(U defaultValue, TweakEnv env, Container container)
        {
            super(defaultValue, env, container);

            this.slider = TweakSlider.create(defaultValue);
        }

        /* Methods */

        @Override
        Builder<U> self()
        {
            return this;
        }

        @Override
        public Builder<U> range(Number min, Number max)
        {
            this.slider.range(min, max);
            return this;
        }

        @Override
        public Builder<U> interval(Number interval)
        {
            this.slider.interval(interval);
            return this;
        }

        @Override
        public Builder<U> roundTo(int place)
        {
            this.slider.roundTo(place);
            return this;
        }

        @Override
        public Builder<U> formatter(Function<Number, String> formatter)
        {
            this.slider.formatter(formatter);
            return this;
        }

        @Override
        public Builder<U> langKey(Translation langKey)
        {
            this.slider.langKey(langKey);
            return this;
        }

        @Override
        public Builder<U> suffix(String suffix)
        {
            this.slider.suffix(suffix);
            return this;
        }

        @Override
        public Builder<U> type(SliderType type)
        {
            this.slider.type(type);
            return this;
        }

        /**
         * Finalize the building process.
         *
         * @return A new {@link TweakNumber} instance.
         */
        public TweakNumber<U> build()
        {
            return new TweakNumber<>(this);
        }
    }
}
