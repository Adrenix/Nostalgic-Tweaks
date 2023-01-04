package mod.adrenix.nostalgic.fabric.event.client;

import mod.adrenix.nostalgic.client.event.ClientEventHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

/**
 * Fabric eye candy related event instructions and registration.
 * Registration is invoked by the client event handler.
 */

public abstract class CandyEvents
{
    /**
     * Registers candy related Fabric events.
     */
    public static void register() { ScreenEvents.AFTER_INIT.register(CandyEvents::setScreen); }

    /**
     * Changes specific vanilla screens so that the mod's screen appears instead.
     * Any further screen redirects are handled by the mod screens.
     */
    private static void setScreen(Minecraft minecraft, Screen screen, int width, int height)
    {
        ClientEventHelper.classicTitleScreen(screen, minecraft::setScreen);
        ClientEventHelper.classicProgressScreen(screen, minecraft::setScreen);
    }
}
