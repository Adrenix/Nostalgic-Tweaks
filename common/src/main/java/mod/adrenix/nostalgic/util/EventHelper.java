package mod.adrenix.nostalgic.util;

import mod.adrenix.nostalgic.client.config.ModConfig;
import mod.adrenix.nostalgic.client.screen.ClassicLoadingScreen;
import mod.adrenix.nostalgic.client.screen.ClassicProgressScreen;
import mod.adrenix.nostalgic.client.screen.ClassicTitleScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public abstract class EventHelper
{
    /* Interfaces */

    public interface ISetScreen { void set(Screen screen); }

    /* Private Helpers */

    private static boolean isLoadingScreen(Screen screen)
    {
        return screen.getClass() == ClassicProgressScreen.class ||
            screen.getClass() == ProgressScreen.class ||
            screen.getClass() == ReceivingLevelScreen.class
        ;
    }

    /* Public Helpers */

    public static void renderClassicTitle(Screen screen, ISetScreen setScreen)
    {
        if (screen == null)
            return;

        if (screen.getClass() == TitleScreen.class)
        {
            if (ModConfig.Candy.overrideTitleScreen())
                setScreen.set(new ClassicTitleScreen());
            else
                ClassicTitleScreen.isGameReady = true;
        }
        else if (!ModConfig.Candy.overrideTitleScreen() && screen.getClass() == ClassicTitleScreen.class)
            setScreen.set(new TitleScreen());
    }

    public static void renderClassicProgress(Screen screen, ISetScreen setScreen)
    {
        Minecraft minecraft = Minecraft.getInstance();

        if (screen == null || !ModConfig.Candy.oldLoadingScreens())
            return;

        if (screen.getClass() == LevelLoadingScreen.class)
        {
            Component title = new TranslatableComponent(NostalgicLang.Gui.LEVEL_LOADING);
            Component subtitle = new TranslatableComponent(NostalgicLang.Gui.LEVEL_BUILDING);
            setScreen.set(new ClassicLoadingScreen(minecraft.getProgressListener(), title, subtitle));
        }

        if (EventHelper.isLoadingScreen(screen))
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
                    progressScreen.setHeader(new TranslatableComponent(NostalgicLang.Gui.LEVEL_LOADING));
                    progressScreen.setStage(new TranslatableComponent(NostalgicLang.Gui.LEVEL_SIMULATE));
                    progressScreen.setPauseTicking(ClassicProgressScreen.NO_PAUSES);
                    progressScreen.setRenderProgressBar(false);
                    progressScreen.renderProgress();
                }
            }
        }
    }
}
