package mod.adrenix.nostalgic.forge.event;

import mod.adrenix.nostalgic.client.event.ClientEventHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.ScreenOpenEvent;

public abstract class ForgeCandyEvents
{
    // Old Title Screen
    public static void classicTitleScreen(ScreenOpenEvent event)
    {
        ClientEventHelper.renderClassicTitle(event.getScreen(), event::setScreen);
    }

    // Old Loading Screens
    public static void classicLoadingScreens(ScreenOpenEvent event)
    {
        ClientEventHelper.renderClassicProgress(event.getScreen(), event::setScreen);
    }

    // Fog Rendering
    public static void oldFogRendering(EntityViewRenderEvent.RenderFogEvent event)
    {
        // TODO: Uncomment this when forge fixes this event and remove temporary mixin
        /*
        The fog mode is currently not passed to the RenderFogEvent in 1.19.
        This seems like an accidental oversight when porting happened from 1.18 to 1.19.
        Update this to remove temporary mixin when this issue gets fixed for Forge

        if (MixinUtil.Fog.isOverworld(event.getCamera()))
            MixinUtil.Fog.setupFog(event.getCamera(), event.getMode());
        else if (MixinUtil.Fog.isNether(event.getCamera()))
            MixinUtil.Fog.setupNetherFog(event.getCamera(), event.getMode());
         */
    }
}
