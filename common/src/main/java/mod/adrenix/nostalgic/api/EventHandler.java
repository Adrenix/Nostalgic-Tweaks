package mod.adrenix.nostalgic.api;

/**
 * Mod loader events will define how events are emitted and handled.
 */

public interface EventHandler
{
    /**
     * Emits the event instance using the provided consumer instructions to all listeners.
     */
    void emit();

    /**
     * Sets the cancel state of the event.
     * @param cancel The new canceled value.
     */
    void setCanceled(boolean cancel);

    /**
     * Check if this event was marked as canceled by a listener.
     * @return Whether this event is canceled.
     */
    boolean isCanceled();
}
