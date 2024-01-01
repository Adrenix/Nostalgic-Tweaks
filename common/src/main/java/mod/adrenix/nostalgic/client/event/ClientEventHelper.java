package mod.adrenix.nostalgic.client.event;

import mod.adrenix.nostalgic.client.gui.screen.home.HomeScreen;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;

/**
 * This helper class provides instructions for various client events. Any unique instructions are handled by their
 * respective mod loader helpers.
 */
public abstract class ClientEventHelper
{
    /**
     * This method provides instructions for the mod to perform after the settings screen mapping key is pressed.
     */
    public static void gotoSettingsIfPossible(Minecraft minecraft)
    {
        if (minecraft.screen != null && ClassUtil.isNotInstanceOf(minecraft.screen, TitleScreen.class))
            return;

        minecraft.setScreen(new HomeScreen(minecraft.screen, true));
    }

    /**
     * This method cycles the game's render distance after the toggle fog mapping key is pressed. The player must be in
     * a level for a cycle to take effect.
     */
    public static void cycleRenderDistance(Minecraft minecraft)
    {
        if (minecraft.level != null)
        {
            int distance = minecraft.options.renderDistance().get();

            if (distance >= 16)
                minecraft.options.renderDistance().set(8);
            else if (distance >= 8)
                minecraft.options.renderDistance().set(4);
            else if (distance >= 4)
                minecraft.options.renderDistance().set(2);
            else if (distance >= 2)
                minecraft.options.renderDistance().set(16);

            minecraft.levelRenderer.needsUpdate();
            minecraft.options.save();
        }
    }
}
