package mod.adrenix.nostalgic.client.gui.widget.dynamic;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.util.common.data.CacheHolder;
import net.minecraft.client.gui.screens.Screen;

import java.util.function.Supplier;

public class WidgetCache
{
    /* Static */

    /**
     * Create a new layout cache from a dynamic widget.
     *
     * @param widget A {@link DynamicWidget} instance.
     * @return A new {@link WidgetCache} instance.
     */
    public static WidgetCache from(DynamicWidget<?, ?> widget)
    {
        return new WidgetCache(widget);
    }

    /* Fields */

    public final CacheHolder<Integer> x;
    public final CacheHolder<Integer> y;
    public final CacheHolder<Integer> width;
    public final CacheHolder<Integer> height;
    public final CacheHolder<Boolean> active;
    public final CacheHolder<Boolean> visible;
    public final ScreenCache screen;

    /* Constructor */

    private WidgetCache(DynamicWidget<?, ?> widget)
    {
        this.x = CacheHolder.create(widget::getX);
        this.y = CacheHolder.create(widget::getY);
        this.width = CacheHolder.create(widget::getWidth);
        this.height = CacheHolder.create(widget::getHeight);
        this.active = CacheHolder.create(widget::isActive);
        this.visible = CacheHolder.create(widget::isVisible);
        this.screen = new ScreenCache(widget);
    }

    /* Methods */

    public void update()
    {
        this.x.update();
        this.y.update();
        this.width.update();
        this.height.update();
        this.active.update();
        this.visible.update();
        this.screen.update();
    }

    public boolean isAnyExpired()
    {
        boolean isWidgetExpired = CacheHolder.isAnyExpired(this.x, this.y, this.width, this.height, this.active, this.visible);
        boolean isScreenExpired = CacheHolder.isAnyExpired(this.screen.x, this.screen.y, this.screen.width, this.screen.height);

        return isWidgetExpired || isScreenExpired;
    }

    public CacheHolder<Integer> getX()
    {
        return this.x;
    }

    public CacheHolder<Integer> getY()
    {
        return this.y;
    }

    public CacheHolder<Integer> getWidth()
    {
        return this.width;
    }

    public CacheHolder<Integer> getHeight()
    {
        return this.height;
    }

    public CacheHolder<Boolean> getActive()
    {
        return this.active;
    }

    public CacheHolder<Boolean> getVisible()
    {
        return this.visible;
    }

    public ScreenCache getScreen()
    {
        return this.screen;
    }

    public static class ScreenCache
    {
        public final CacheHolder<Integer> x;
        public final CacheHolder<Integer> y;
        public final CacheHolder<Integer> width;
        public final CacheHolder<Integer> height;
        public final Supplier<Screen> screen;

        private ScreenCache(DynamicWidget<?, ?> widget)
        {
            this.width = CacheHolder.create(widget::getScreenWidth);
            this.height = CacheHolder.create(widget::getScreenHeight);
            this.screen = widget::getScreen;

            this.x = CacheHolder.create(() -> {
                if (widget.getScreen() instanceof Overlay overlay)
                    return overlay.getInsideX();

                return 0;
            });

            this.y = CacheHolder.create(() -> {
                if (widget.getScreen() instanceof Overlay overlay)
                    return overlay.getInsideY();

                return 0;
            });
        }

        public void update()
        {
            this.x.update();
            this.y.update();
            this.width.update();
            this.height.update();
        }

        public CacheHolder<Integer> getX()
        {
            return this.x;
        }

        public CacheHolder<Integer> getY()
        {
            return this.y;
        }

        public CacheHolder<Integer> getWidth()
        {
            return this.width;
        }

        public CacheHolder<Integer> getHeight()
        {
            return this.height;
        }
    }
}
