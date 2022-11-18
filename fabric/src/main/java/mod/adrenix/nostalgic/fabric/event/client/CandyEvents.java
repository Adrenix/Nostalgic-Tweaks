package mod.adrenix.nostalgic.fabric.event.client;

import mod.adrenix.nostalgic.client.event.ClientEventHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;

/**
 * Fabric eye candy related event instructions and registration.
 * Registration is invoked by the client event handler.
 */

public abstract class CandyEvents
{
    /**
     * Registers candy related Fabric events.
     */
    public static void register()
    {
        // Screen Events

        onSetScreen();
    }

    /* Screen Events */

    /**
     * Changes specific vanilla screens so that the mod's screen appears instead.
     * Any further screen redirects are handled by the mod screens.
     */
    public static void onSetScreen()
    {
        Minecraft minecraft = Minecraft.getInstance();

        ScreenEvents.AFTER_INIT.register
        (
            (client, screen, scaledWidth, scaledHeight) ->
            {
                ClientEventHelper.classicTitleScreen(screen, minecraft::setScreen);
                ClientEventHelper.classicProgressScreen(screen, minecraft::setScreen);
            }
        );
    }
}
