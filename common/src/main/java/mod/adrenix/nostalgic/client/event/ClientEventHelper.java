package mod.adrenix.nostalgic.client.event;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.MixinConfig;
import mod.adrenix.nostalgic.client.screen.ClassicLoadingScreen;
import mod.adrenix.nostalgic.client.screen.ClassicProgressScreen;
import mod.adrenix.nostalgic.client.screen.ClassicTitleScreen;
import mod.adrenix.nostalgic.common.config.reflect.StatusType;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.*;
import net.minecraft.network.chat.Component;

public abstract class ClientEventHelper
{
    /* Client Network Helpers */

    public static void disconnect()
    {
        if (NostalgicTweaks.isClient())
        {
            NostalgicTweaks.setNetworkVerification(false);
            TweakServerCache.all().forEach((id, tweak) -> tweak.setStatus(StatusType.FAIL));
        }
    }

    /* Screen Event Helpers */

    public interface ISetScreen { void set(Screen screen); }

    private static boolean isLoadingScreen(Screen screen)
    {
        return screen.getClass() == ClassicProgressScreen.class ||
            screen.getClass() == ProgressScreen.class ||
            screen.getClass() == ReceivingLevelScreen.class
        ;
    }

    public static void renderClassicTitle(Screen screen, ISetScreen setScreen)
    {
        if (screen == null)
            return;

        if (screen.getClass() == TitleScreen.class)
        {
            if (MixinConfig.Candy.overrideTitleScreen())
                setScreen.set(new ClassicTitleScreen());
            else
                ClassicTitleScreen.isGameReady = true;
        }
        else if (!MixinConfig.Candy.overrideTitleScreen() && screen.getClass() == ClassicTitleScreen.class)
            setScreen.set(new TitleScreen());
    }

    public static void renderClassicProgress(Screen screen, ISetScreen setScreen)
    {
        Minecraft minecraft = Minecraft.getInstance();

        if (screen == null || !MixinConfig.Candy.oldLoadingScreens())
            return;

        if (screen.getClass() == LevelLoadingScreen.class)
        {
            Component title = Component.translatable(NostalgicLang.Gui.LEVEL_LOADING);
            Component subtitle = Component.translatable(NostalgicLang.Gui.LEVEL_BUILDING);
            setScreen.set(new ClassicLoadingScreen(minecraft.getProgressListener(), title, subtitle));
        }

        if (ClientEventHelper.isLoadingScreen(screen))
        {
            if (screen.getClass() == ClassicProgressScreen.class && !((ClassicProgressScreen) screen).isTicking())
                ((ClassicProgressScreen) screen).renderProgress();
            else
            {
                ClassicProgressScreen progressScreen;
                if (screen.getClass() == ProgressScreen.class)
                {
                    progressScreen = new ClassicProgressScreen((ProgressScreen) screen);
                    progressScreen.renderProgress();
                }
                else if (screen.getClass() == ReceivingLevelScreen.class)
                {
                    progressScreen = new ClassicProgressScreen(new ProgressScreen(true));
                    progressScreen.setHeader(Component.translatable(NostalgicLang.Gui.LEVEL_LOADING));
                    progressScreen.setStage(Component.translatable(NostalgicLang.Gui.LEVEL_SIMULATE));
                    progressScreen.setPauseTicking(ClassicProgressScreen.NO_PAUSES);
                    progressScreen.setRenderProgressBar(false);
                    progressScreen.renderProgress();
                }
            }
        }
    }
}
