package mod.adrenix.nostalgic.api;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.api.event.HudEvent;
import mod.adrenix.nostalgic.util.common.function.QuadFunction;
import mod.adrenix.nostalgic.util.common.function.QuintFunction;

/**
 * An abstract client API class that houses event classes which keep track of event factories defined in each mod
 * loader implementation. All branches of the source code can use this utility to create and emit events regardless of
 * mod loader.
 *
 * This utility uses vanilla client-side code.
 */

public abstract class ClientEventFactory
{
    /*
        HUD Events

        These events provide support for changing how the mod renders icon to the in-game heads up display. Each mod
        loader implementation will have the same structure found in the HudEvent interface.
    */

    public static final RenderHeart RENDER_HEART = new RenderHeart();
    public static final RenderFood RENDER_FOOD = new RenderFood();
    public static final RenderArmor RENDER_ARMOR = new RenderArmor();
    public static final RenderBubble RENDER_BUBBLE = new RenderBubble();

    /**
     * Hud event for when heart icons are rendered by the mod.
     * @see HudEvent
     */
    public static class RenderHeart
    {
        /**
         * An event class constructor pointer. This is defined by the mod loader implementation.
         */
        private QuintFunction<Integer, Integer, Integer, Integer, PoseStack, HudEvent> factory;

        /**
         * Register the event class constructor pointer.
         * @param factory The constructor for the event.
         */
        public void register(QuintFunction<Integer, Integer, Integer, Integer, PoseStack, HudEvent> factory)
        {
            this.factory = factory;
        }

        /**
         * Creates a new event class instance.
         * @param x Where the icons start on the x-axis.
         * @param y Where the icons start on the y-axis.
         * @param index The current icon index (zero based) for the row.
         * @param rowIndex The current row index (zero based) for hearts.
         * @param poseStack The current pose stack.
         * @return A new hud event instance.
         */
        public HudEvent create(int x, int y, int index, int rowIndex, PoseStack poseStack)
        {
            return this.factory.apply(x, y, index, rowIndex, poseStack);
        }
    }

    /**
     * Factory template for {@link HudEvent} events.
     */
    private static class RenderHudEvent
    {
        /**
         * An event class constructor pointer. This is defined by the mod loader implementation.
         */
        private QuadFunction<Integer, Integer, Integer, PoseStack, HudEvent> factory;

        /**
         * Register the event class constructor pointer.
         * @param factory The constructor for the event.
         */
        public void register(QuadFunction<Integer, Integer, Integer, PoseStack, HudEvent> factory)
        {
            this.factory = factory;
        }

        /**
         * Creates a new event class instance.
         * @param x Where the icons start on the x-axis.
         * @param y Where the icons start on the y-axis.
         * @param index The current icon index (zero based) for the row.
         * @param poseStack The current pose stack.
         * @return A new hud event instance.
         */
        public HudEvent create(int x, int y, int index, PoseStack poseStack)
        {
            return this.factory.apply(x, y, index, poseStack);
        }
    }

    /**
     * Hud event for when food icons are rendered by the mod.
     * @see HudEvent
     */
    public static class RenderFood extends RenderHudEvent {}

    /**
     * Hud event for when armor icons are rendered by the mod.
     * @see HudEvent
     */
    public static class RenderArmor extends RenderHudEvent {}

    /**
     * Hud event for when air bubble icons are rendered by the mod.
     * @see HudEvent
     */
    public static class RenderBubble extends RenderHudEvent {}
}
