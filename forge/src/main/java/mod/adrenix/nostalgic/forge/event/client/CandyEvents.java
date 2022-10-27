package mod.adrenix.nostalgic.forge.event.client;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.event.ClientEventHelper;
import mod.adrenix.nostalgic.util.client.FogUtil;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.ViewportEvent;

public abstract class CandyEvents
{
    // Old Title Screen
    public static void classicTitleScreen(ScreenEvent.Opening event)
    {
        ClientEventHelper.renderClassicTitle(event.getScreen(), event::setNewScreen);
    }

    // Old Loading Screens
    public static void classicLoadingScreens(ScreenEvent.Opening event)
    {
        ClientEventHelper.renderClassicProgress(event.getScreen(), event::setNewScreen);
    }

    // Fog Rendering
    public static void oldFogRendering(ViewportEvent.RenderFog event)
    {
        if (NostalgicTweaks.OPTIFINE.get())
            return;

        if (FogUtil.isOverworld(event.getCamera()))
            FogUtil.setupFog(event.getCamera(), event.getMode());
        else if (FogUtil.isNether(event.getCamera()))
            FogUtil.setupNetherFog(event.getCamera(), event.getMode());
    }
}
