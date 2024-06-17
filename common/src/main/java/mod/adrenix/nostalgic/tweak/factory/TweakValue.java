package mod.adrenix.nostalgic.tweak.factory;

import mod.adrenix.nostalgic.config.cache.CacheHolder;
import mod.adrenix.nostalgic.tweak.TweakEnv;
import mod.adrenix.nostalgic.tweak.TweakValidator;
import mod.adrenix.nostalgic.tweak.container.Container;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class TweakValue<T> extends Tweak<T>
{
    /* Fields */

    private final CacheHolder<T> cacheHolder;
    private final T defaultValue;
    private T diskValue;
    private T receivedValue;
    private @Nullable T disabledValue;

    /* Constructor */

    TweakValue(Builder<T, ?> builder)
    {
        super(builder);

        this.diskValue = builder.defaultValue;
        this.defaultValue = builder.defaultValue;
        this.receivedValue = builder.defaultValue;
        this.disabledValue = builder.disabledValue;
        this.cacheHolder = CacheHolder.from(builder.defaultValue, this::getCacheMode);
    }

    /* Methods */

    @Override
    public T fromDisk()
    {
        return this.diskValue;
    }

    @Override
    public T fromServer()
    {
        return this.receivedValue;
    }

    @Override
    public CacheHolder<T> getCacheHolder()
    {
        return this.cacheHolder;
    }

    @Override
    public void setCacheValue(T value)
    {
        if (this.isLocalMode())
            this.setLocal(value);
        else
            this.setNetwork(value);
    }

    @Override
    public void setCacheToDefault()
    {
        this.setCacheValue(this.defaultValue);
    }

    @Override
    public boolean isCacheDefault()
    {
        return this.fromCache().equals(this.defaultValue);
    }

    @Override
    public void undoCache()
    {
        switch (this.getCacheMode())
        {
            case LOCAL -> this.setLocal(this.fromDisk());
            case NETWORK -> this.setNetwork(this.fromServer());
        }
    }

    @Override
    public void sync()
    {
        this.setLocal(this.fromDisk());
        this.setNetwork(this.fromServer());
    }

    @Override
    public void setDisk(T value)
    {
        this.diskValue = value;
        this.applyReflection(value);
    }

    @Override
    public void setReceived(T value)
    {
        this.receivedValue = value;
        this.setNetwork(value);
    }

    @Override
    public T getDefault()
    {
        return this.defaultValue;
    }

    @Override
    public void setDisabled(@Nullable T value)
    {
        this.disabledValue = value;
    }

    @Override
    public T getDisabled()
    {
        if (this.isIgnored())
            return this.fromDisk();

        if (this.disabledValue != null)
            return this.disabledValue;

        Optional<Tweak<Boolean>> tweak = this.generic(Boolean.class);

        if (tweak.stream().anyMatch(Tweak::getDefault))
            tweak.ifPresent(meta -> meta.setDisabled(false));

        if (this.disabledValue != null)
            return this.disabledValue;

        return this.getDefault();
    }

    @Override
    public boolean hasChanged(T receivedValue)
    {
        return !this.fromDisk().equals(receivedValue);
    }

    @Override
    public boolean validate(TweakValidator validator)
    {
        return true;
    }

    @Override
    protected String getTypeName()
    {
        return this.defaultValue.getClass().getSimpleName();
    }

    /* Builder */

    public abstract static class Builder<U, B extends Builder<U, B>> extends TweakBuilder<B>
    {
        /* Fields */

        final U defaultValue;
        U disabledValue = null;

        /* Constructor */

        Builder(U defaultValue, TweakEnv env, Container container)
        {
            super(env, container);

            this.defaultValue = defaultValue;
        }

        /* Methods */

        /**
         * Reference {@code see also}.
         *
         * @param value The value to use when the tweak is set into a "disabled" state.
         * @see TweakValue#getDisabled()
         */
        public B whenDisabled(U value)
        {
            this.disabledValue = value;
            return this.self();
        }
    }
}
