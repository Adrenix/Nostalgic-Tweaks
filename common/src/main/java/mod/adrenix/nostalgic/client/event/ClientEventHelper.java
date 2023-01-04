package mod.adrenix.nostalgic.client.event;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.client.screen.NostalgicLoadingScreen;
import mod.adrenix.nostalgic.client.screen.NostalgicProgressScreen;
import mod.adrenix.nostalgic.client.screen.NostalgicTitleScreen;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import mod.adrenix.nostalgic.util.client.FogUtil;
import mod.adrenix.nostalgic.util.client.WorldClientUtil;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.*;
import net.minecraft.network.chat.Component;

/**
 * This helper class provides instructions for various client events. These methods are used by both mod loader event
 * subscriptions. Any unique instructions are handled by their respective mod loader helpers.
 */

public abstract class ClientEventHelper
{
    /* Client Helpers */

    /**
     * This method provides instructions for the mod to perform after a player disconnects from a level.
     * Connection instructions are handled by the server. The client will wait for a connection verification packet
     * from the server. If one is not received, then the mod must assume the server does not have the mod installed.
     */
    public static void disconnect()
    {
        if (NostalgicTweaks.isClient())
        {
            // Reset static fog utility trackers
            FogUtil.Void.reset();

            // Reset client world utility caches
            WorldClientUtil.resetLightingCache();
            WorldClientUtil.resetWorldInterpolationCache();

            // Reset network verification and server cache
            NostalgicTweaks.setNetworkVerification(false);

            TweakServerCache.all().forEach((id, tweak) ->
            {
                if (tweak.isDynamic())
                    tweak.setValue(TweakClientCache.all().get(id).getValue());
            });
        }


    }

    /**
     * This method provides instructions for the mod to perform after a player changes dimensions. Some utility caches
     * need to reset when this event occurs.
     */
    public static void onChangeDimension()
    {
        // Resets world interpolation animation caches
        WorldClientUtil.resetWorldInterpolationCache();
    }

    /*
       Screen Event Helpers

       The following interface and methods provides utility for the client's screen management.
       Some vanilla screens will be redirected to modded ones depending on tweak settings.
     */

    /**
     * This interface is used to set the game screen to a new screen.
     */
    public interface SetScreen
    {
        /**
         * Each mod loader has a different way of setting new game screens. As long as the mod loader has a method that
         * accepts a screen and returns nothing, then this interface can be used.
         *
         * @param screen The screen to set.
         */
        void set(Screen screen);
    }

    /**
     * Checks if the current screen is of a loading type screen.
     * @param screen The screen to check.
     * @return Whether the given screen is a loading screen.
     */
    private static boolean isLoadingScreen(Screen screen)
    {
        return screen.getClass() == NostalgicProgressScreen.class ||
            screen.getClass() == ProgressScreen.class ||
            screen.getClass() == ReceivingLevelScreen.class
        ;
    }

    /**
     * Redirects the vanilla title screen to a classic style title screen.
     * @param screen A vanilla screen.
     * @param setScreen A function that accepts a new screen.
     */
    public static void classicTitleScreen(Screen screen, SetScreen setScreen)
    {
        if (screen == null)
            return;

        if (screen.getClass() == TitleScreen.class)
        {
            if (ModConfig.Candy.overrideTitleScreen())
                setScreen.set(new NostalgicTitleScreen());
            else
                NostalgicTitleScreen.isGameReady = true;
        }
        else if (!ModConfig.Candy.overrideTitleScreen() && screen.getClass() == NostalgicTitleScreen.class)
            setScreen.set(new TitleScreen());
    }

    /**
     * Redirects the vanilla level loading screen to a classic style loading screen.
     * @param screen A vanilla screen.
     * @param setScreen A function that accepts a new screen.
     */
    public static void classicProgressScreen(Screen screen, SetScreen setScreen)
    {
        Minecraft minecraft = Minecraft.getInstance();

        if (screen == null || !ModConfig.Candy.oldLoadingScreens())
            return;

        if (screen.getClass() == LevelLoadingScreen.class)
        {
            Component title = Component.translatable(LangUtil.Gui.LEVEL_LOADING);
            Component subtitle = Component.translatable(LangUtil.Gui.LEVEL_BUILDING);

            setScreen.set(new NostalgicLoadingScreen(minecraft.getProgressListener(), title, subtitle));
        }

        if (ClientEventHelper.isLoadingScreen(screen))
        {
            if (screen.getClass() == NostalgicProgressScreen.class && !((NostalgicProgressScreen) screen).isTicking())
                ((NostalgicProgressScreen) screen).load();
            else
            {
                NostalgicProgressScreen progressScreen;

                if (screen.getClass() == ProgressScreen.class)
                {
                    progressScreen = new NostalgicProgressScreen((ProgressScreen) screen);
                    progressScreen.load();
                }
                else if (screen.getClass() == ReceivingLevelScreen.class)
                {
                    progressScreen = new NostalgicProgressScreen(new ProgressScreen(true));
                    progressScreen.setHeader(Component.translatable(LangUtil.Gui.LEVEL_LOADING));
                    progressScreen.setStage(Component.translatable(LangUtil.Gui.LEVEL_SIMULATE));
                    progressScreen.setPauseTicking(NostalgicProgressScreen.NO_PAUSES);
                    progressScreen.setRenderProgressBar(false);
                    progressScreen.load();
                }
            }
        }
    }
}
