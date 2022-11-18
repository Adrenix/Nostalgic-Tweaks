package mod.adrenix.nostalgic.forge.event.client;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.event.ClientEventHelper;
import mod.adrenix.nostalgic.util.client.FogUtil;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.ViewportEvent;

/**
 * Helper class that defines instructions for various eye candy related events.
 */

public abstract class CandyEvents
{
    /**
     * Changes specific vanilla screens so that the mod's old style screens appear instead.
     * Any further screen redirects are handled by the mod screens.
     */
    public static void classicTitleScreens(ScreenEvent.Opening event)
    {
        ClientEventHelper.classicTitleScreen(event.getScreen(), event::setNewScreen);
        ClientEventHelper.classicProgressScreen(event.getScreen(), event::setNewScreen);
    }

    /**
     * Performs various fog manipulation instructions which are dependent various tweaks.
     * The overworld, nether, and caves has unique fog that is handled by this mod.
     */
    public static void renderOldFog(ViewportEvent.RenderFog event)
    {
        if (NostalgicTweaks.OPTIFINE.get())
            return;

        if (FogUtil.isOverworld(event.getCamera()))
            FogUtil.setupFog(event.getCamera(), event.getMode());
        else if (FogUtil.isNether(event.getCamera()))
            FogUtil.setupNetherFog(event.getCamera(), event.getMode());
    }
}
