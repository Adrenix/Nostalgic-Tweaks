package mod.adrenix.nostalgic.fabric.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * This interface handles events using the Fabric event factory.
 * @param <EventClass> The class to create an "array-backed" event instance.
 */

public interface EventHandler<EventClass>
{
    /**
     * Creates an "array-backed" event instance.
     * @param <T> An event class.
     * @return A Fabric event factory.
     */
    static <T> Event<EventHandler<T>> createArrayBacked()
    {
        return EventFactory.createArrayBacked(EventHandler.class, listeners -> event ->
        {
            for (EventHandler<T> listener : listeners)
                listener.interact(event);
        });
    }

    /**
     * Emits the event out to listeners so that they can process the event and make changes as needed.
     * @param event An event class instance.
     */
    void interact(EventClass event);
}
