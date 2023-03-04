package mod.adrenix.nostalgic.forge.event.client;

import mod.adrenix.nostalgic.client.event.ClientEventHelper;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.client.FogUtil;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.ScreenOpenEvent;

/**
 * Helper class that defines instructions for various eye candy related events.
 */

public abstract class CandyEvents
{
    /**
     * Changes specific vanilla screens so that the mod's old style screens appear instead.
     * Any further screen redirects are handled by the mod screens.
     */
    public static void classicTitleScreens(ScreenOpenEvent event)
    {
        ClientEventHelper.classicTitleScreen(event.getScreen(), event::setScreen);
        ClientEventHelper.classicProgressScreen(event.getScreen(), event::setScreen);
    }

    /**
     * Performs various fog manipulation instructions which are dependent various tweaks.
     * The overworld, nether, and caves has unique fog that is handled by this mod.
     */
    public static void renderOldFog(EntityViewRenderEvent.RenderFogEvent event)
    {
        if (ModTracker.OPTIFINE.isInstalled())
            return;

        if (FogUtil.isOverworld(event.getCamera()))
            FogUtil.setupFog(event.getCamera(), event.getMode());
        else if (FogUtil.isNether(event.getCamera()))
            FogUtil.setupNetherFog(event.getCamera(), event.getMode());
    }
}
