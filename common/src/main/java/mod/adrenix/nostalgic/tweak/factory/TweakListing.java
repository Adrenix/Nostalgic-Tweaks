package mod.adrenix.nostalgic.tweak.factory;

import mod.adrenix.nostalgic.config.cache.CacheHolder;
import mod.adrenix.nostalgic.tweak.TweakEnv;
import mod.adrenix.nostalgic.tweak.TweakValidator;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.tweak.listing.Listing;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;

import java.util.Optional;

public abstract class TweakListing<V, T extends Listing<V, T>> extends Tweak<T>
{
    /* Fields */

    private final TweakListing.Builder<?, V, T> builder;
    private final CacheHolder<T> cacheHolder;
    private final T emptyList;
    private final T diskList;
    private final T defaultList;
    private final T receivedList;

    /* Constructor */

    TweakListing(TweakListing.Builder<?, V, T> builder)
    {
        super(builder);

        this.builder = builder;
        this.defaultList = builder.defaultList;
        this.receivedList = builder.defaultList.create();
        this.diskList = builder.defaultList.create();
        this.emptyList = builder.defaultList.create();
        this.cacheHolder = CacheHolder.with(this.defaultList.create(), this.defaultList.create(), this::getCacheMode);

        this.emptyList.clear();
    }

    /* Methods */

    @Override
    public T get()
    {
        if (this.diskList.isDisabled())
            return this.emptyList;

        return super.get();
    }

    @Override
    public T fromDisk()
    {
        return this.diskList;
    }

    @Override
    public T fromServer()
    {
        return this.receivedList;
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
        {
            this.fromLocal().clear();
            this.fromLocal().copy(value);
        }
        else
        {
            this.fromNetwork().clear();
            this.fromNetwork().copy(value);
        }
    }

    @Override
    public void setCacheToDefault()
    {
        this.fromCache().clear();
        this.fromCache().copy(this.defaultList);
    }

    @Override
    public boolean isCacheDefault()
    {
        return this.fromCache().matches(this.defaultList);
    }

    @Override
    public void setCacheDisabled()
    {
        this.fromCache().setDisabled(true);
    }

    @Override
    public boolean isCacheDisabled()
    {
        return this.isIgnored() || this.fromCache().isDisabled();
    }

    @Override
    public void applyCurrentCache()
    {
        switch (this.getCacheMode())
        {
            case LOCAL -> this.fromLocal().applyCache();
            case NETWORK -> this.fromNetwork().applyCache();
        }

        super.applyCurrentCache();
    }

    @Override
    public void applyCacheAndSend()
    {
        this.fromLocal().applyCache();
        this.fromNetwork().applyCache();

        super.applyCacheAndSend();
    }

    @Override
    public void undoCache()
    {
        if (this.isLocalMode())
        {
            this.fromLocal().clear();
            this.fromLocal().copy(this.fromDisk());
        }
        else
        {
            this.fromNetwork().clear();
            this.fromNetwork().copy(this.fromServer());
        }
    }

    @Override
    public boolean isNetworkSavable()
    {
        return this.isNetworkAvailable() && !this.fromServer().matches(this.fromNetwork());
    }

    @Override
    public boolean isLocalSavable()
    {
        return !this.fromDisk().matches(this.fromLocal());
    }

    @Override
    public void sync()
    {
        this.fromLocal().clear();
        this.fromLocal().copy(this.fromDisk());

        this.fromNetwork().clear();
        this.fromNetwork().copy(this.fromServer());
    }

    @Override
    public void setDisk(T value)
    {
        this.diskList.clear();
        this.diskList.copy(value);

        this.applyReflection(this.diskList);
    }

    @Override
    public void setReceived(T value)
    {
        this.receivedList.clear();
        this.receivedList.copy(value);

        this.fromNetwork().clear();
        this.fromNetwork().copy(value);
    }

    @Override
    public T getDefault()
    {
        return this.defaultList;
    }

    @Override
    public T getDisabled()
    {
        if (this.isIgnored())
            return this.fromDisk();

        return this.emptyList;
    }

    @Override
    public void setDisabled(T value)
    {
    }

    @Override
    public boolean hasChanged(T receivedValue)
    {
        return !this.fromDisk().matches(receivedValue);
    }

    @Override
    public boolean validate(TweakValidator validator)
    {
        return this.diskList.validate(validator, this);
    }

    @PublicAPI
    public Optional<TextureIcon> getIcon()
    {
        return Optional.ofNullable(this.builder.icon);
    }

    @Override
    protected String getTypeName()
    {
        return this.fromDisk().debugString();
    }

    /* Builder */

    public abstract static class Builder<B extends Builder<B, W, U>, W, U extends Listing<W, U>> extends TweakBuilder<B>
    {
        /* Fields */

        final U defaultList;
        TextureIcon icon;

        /* Constructor */

        Builder(U defaultList, TweakEnv env, Container container)
        {
            super(env, container);

            this.defaultList = defaultList;
        }

        /* Methods */

        /**
         * Assign this listing a tweak a {@link TextureIcon} that will appear in the listing overlay on the client.
         *
         * @param icon A {@link TextureIcon} instance.
         */
        @PublicAPI
        public B icon(TextureIcon icon)
        {
            this.icon = icon;
            return this.self();
        }
    }
}
