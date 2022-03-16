package mod.adrenix.nostalgic.fabric.event;

import mod.adrenix.nostalgic.util.EventHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;

public abstract class FabricCandyEvents
{
    public static void register()
    {
        // Screen Events

        onTitleScreen();
        onLoadScreen();

        // ...
    }

    /* Screen Events */

    public static void onTitleScreen()
    {
        Minecraft minecraft = Minecraft.getInstance();
        ScreenEvents.BEFORE_INIT.register(
            (client, screen, scaledWidth, scaledHeight) -> EventHelper.renderClassicTitle(screen, minecraft::setScreen)
        );
    }

    public static void onLoadScreen()
    {
        Minecraft minecraft = Minecraft.getInstance();
        ScreenEvents.BEFORE_INIT.register(
            (client, screen, scaledWidth, scaledHeight) -> EventHelper.renderClassicProgress(screen, minecraft::setScreen)
        );
    }
}
