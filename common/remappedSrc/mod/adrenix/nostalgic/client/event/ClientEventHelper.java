package mod.adrenix.nostalgic.client.event;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.client.screen.NostalgicLoadingScreen;
import mod.adrenix.nostalgic.client.screen.NostalgicProgressScreen;
import mod.adrenix.nostalgic.client.screen.NostalgicTitleScreen;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screens.*;
import net.minecraft.text.Text;

public abstract class ClientEventHelper
{
    /* Client Network Helpers */

    public static void disconnect()
    {
        if (NostalgicTweaks.isClient())
        {
            NostalgicTweaks.setNetworkVerification(false);
            TweakServerCache.all().forEach((id, tweak) -> {
                if (tweak.isDynamic())
                    tweak.setValue(TweakClientCache.all().get(id).getCurrent());
            });
        }
    }

    /* Screen Event Helpers */

    public interface ISetScreen { void set(Screen screen); }

    private static boolean isLoadingScreen(Screen screen)
    {
        return screen.getClass() == NostalgicProgressScreen.class ||
            screen.getClass() == ProgressScreen.class ||
            screen.getClass() == DownloadingTerrainScreen.class
        ;
    }

    public static void renderClassicTitle(Screen screen, ISetScreen setScreen)
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

    public static void renderClassicProgress(Screen screen, ISetScreen setScreen)
    {
        MinecraftClient minecraft = MinecraftClient.getInstance();

        if (screen == null || !ModConfig.Candy.oldLoadingScreens())
            return;

        if (screen.getClass() == LevelLoadingScreen.class)
        {
            Text title = Text.translatable(NostalgicLang.Gui.LEVEL_LOADING);
            Text subtitle = Text.translatable(NostalgicLang.Gui.LEVEL_BUILDING);
            setScreen.set(new NostalgicLoadingScreen(minecraft.getWorldGenerationProgressTracker(), title, subtitle));
        }

        if (ClientEventHelper.isLoadingScreen(screen))
        {
            if (screen.getClass() == NostalgicProgressScreen.class && !((NostalgicProgressScreen) screen).isTicking())
                ((NostalgicProgressScreen) screen).renderProgress();
            else
            {
                NostalgicProgressScreen progressScreen;
                if (screen.getClass() == ProgressScreen.class)
                {
                    progressScreen = new NostalgicProgressScreen((ProgressScreen) screen);
                    progressScreen.renderProgress();
                }
                else if (screen.getClass() == DownloadingTerrainScreen.class)
                {
                    progressScreen = new NostalgicProgressScreen(new ProgressScreen(true));
                    progressScreen.setHeader(Text.translatable(NostalgicLang.Gui.LEVEL_LOADING));
                    progressScreen.setStage(Text.translatable(NostalgicLang.Gui.LEVEL_SIMULATE));
                    progressScreen.setPauseTicking(NostalgicProgressScreen.NO_PAUSES);
                    progressScreen.setRenderProgressBar(false);
                    progressScreen.renderProgress();
                }
            }
        }
    }
}
