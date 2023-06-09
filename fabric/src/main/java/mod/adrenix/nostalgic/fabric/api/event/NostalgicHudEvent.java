package mod.adrenix.nostalgic.fabric.api.event;

import mod.adrenix.nostalgic.api.ClientEventFactory;
import mod.adrenix.nostalgic.api.event.HudEvent;
import mod.adrenix.nostalgic.fabric.api.EventHandler;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Use this event to set or get the current starting x/y positions of overlays that are rendered on the in-game HUD.
 * You can also cancel the event which will prevent Nostalgic Tweaks from making changes.
 *
 * @see HudEvent For usable event methods and other methods not visible in this class.
 */

public class NostalgicHudEvent implements HudEvent
{
    /* Implementation */

    @Override
    public GuiGraphics getGraphics() { return this.graphics; }

    @Override
    public void setX(int x) { this.x = x; }

    @Override
    public void setY(int y) { this.y = y; }

    @Override
    public int getX() { return this.x; }

    @Override
    public int getY() { return this.y; }

    @Override
    public int getIconIndex() { return this.index; }

    @Override
    public void setCanceled(boolean cancel) { this.isCanceled = cancel; }

    @Override
    public boolean isCanceled() { return this.isCanceled; }

    @Override
    public void emit() { throw new AssertionError("Nostalgic-Fabric[HudEvent]: Parent does not emit"); }

    /* Factory Registration */

    public static void register()
    {
        ClientEventFactory.RENDER_HEART.register(RenderHeart::new);
        ClientEventFactory.RENDER_FOOD.register(RenderFood::new);
        ClientEventFactory.RENDER_ARMOR.register(RenderArmor::new);
        ClientEventFactory.RENDER_BUBBLE.register(RenderBubble::new);
    }

    /**
     * Emitted before the mod renders a heart icon on the HUD.
     * If canceled, will prevent rendering of the heart icon.
     */
    public static class RenderHeart extends NostalgicHudEvent implements HudEvent
    {
        /**
         * Unique to this event.
         * A zero based index for the amount of heart rows that are being rendered.
         */
        private final int rowIndex;

        /**
         * Create a new Fabric render hearts event.
         * @param x Where the heart icons start on the x-axis.
         * @param y Where the heart icons start on the y-axis.
         * @param index The current icon index (zero based) for the row.
         * @param rowIndex The current row index (zero based) for hearts.
         * @param graphics The current GuiGraphics object.
         */
        public RenderHeart(int x, int y, int index, int rowIndex, GuiGraphics graphics)
        {
            super(x, y, index, graphics);

            this.rowIndex = rowIndex;
        }

        /**
         * This method is unique to this HUD event.
         * @return Get a zero based index for the amount of heart rows that are currently being rendered.
         */
        public int getRowIndex() { return this.rowIndex; }

        /**
         * Fabric event emitter.
         */
        @Override
        public void emit() { EVENT.invoker().interact(this); }

        /**
         * Fabric event handler instance.
         */
        public static final Event<EventHandler<RenderHeart>> EVENT = EventHandler.createArrayBacked();
    }

    /**
     * Emitted before the mod renders a food icon on the HUD.
     * If canceled, will prevent rendering of the food icon.
     */
    public static class RenderFood extends NostalgicHudEvent implements HudEvent
    {
        /**
         * Create a new Fabric render food event.
         * @param x Where the food icons start on the x-axis.
         * @param y Where the food icons start on the y-axis.
         * @param index The current icon index (zero based) for the row.
         * @param graphics The current GuiGraphics object.
         */
        public RenderFood(int x, int y, int index, GuiGraphics graphics) { super(x, y, index, graphics); }

        /**
         * Fabric event emitter.
         */
        @Override
        public void emit() { EVENT.invoker().interact(this); }

        /**
         * Fabric event handler instance.
         */
        public static final Event<EventHandler<RenderFood>> EVENT = EventHandler.createArrayBacked();
    }

    /**
     * Emitted before the mod renders an armor icon on the HUD.
     * If canceled, will prevent rendering of the armor icon.
     */
    public static class RenderArmor extends NostalgicHudEvent implements HudEvent
    {
        /**
         * Create a new Fabric render armor event.
         * @param x Where the armor icons start on the x-axis.
         * @param y Where the armor icons start on the y-axis.
         * @param index The current icon index (zero based) for the row.
         * @param graphics The current GuiGraphics object.
         */
        public RenderArmor(int x, int y, int index, GuiGraphics graphics) { super(x, y, index, graphics); }

        /**
         * Fabric event emitter.
         */
        @Override
        public void emit() { EVENT.invoker().interact(this); }

        /**
         * Fabric event handler instance.
         */
        public static final Event<EventHandler<RenderArmor>> EVENT = EventHandler.createArrayBacked();
    }

    /**
     * Emitted before the mod renders an air bubble icon on the HUD.
     * If canceled, will prevent rendering of the air bubble icon.
     */
    public static class RenderBubble extends NostalgicHudEvent implements HudEvent
    {
        /**
         * Create a new Fabric render air bubble event.
         * @param x Where the air bubble icons start on the x-axis.
         * @param y Where the air bubble icons start on the y-axis.
         * @param index The current icon index (zero based) for the row.
         * @param graphics The current GuiGraphics object.
         */
        public RenderBubble(int x, int y, int index, GuiGraphics graphics) { super(x, y, index, graphics); }

        /**
         * Fabric event emitter.
         */
        @Override
        public void emit() { EVENT.invoker().interact(this); }

        /**
         * Fabric event handler instance.
         */
        public static final Event<EventHandler<RenderBubble>> EVENT = EventHandler.createArrayBacked();
    }

    /* Common Event Structure */

    private final GuiGraphics graphics;
    private final int index;
    private int x;
    private int y;
    private boolean isCanceled;

    private NostalgicHudEvent(int x, int y, int index, GuiGraphics graphics)
    {
        this.x = x;
        this.y = y;
        this.index = index;
        this.graphics = graphics;
    }
}
