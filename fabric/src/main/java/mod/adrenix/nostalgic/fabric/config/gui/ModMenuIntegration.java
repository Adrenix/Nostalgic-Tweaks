package mod.adrenix.nostalgic.fabric.config.gui;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import mod.adrenix.nostalgic.client.config.gui.screen.SettingsScreen;
import mod.adrenix.nostalgic.util.client.GuiUtil;
import net.minecraft.client.Minecraft;

/**
 * This class implements the Mod Menu API for Fabric. A functional interface is cached so that mod menu screen can be
 * accessed from the nostalgic title screen and the nostalgic pause screen.
 */

public class ModMenuIntegration implements ModMenuApi
{
    static { GuiUtil.modScreen = ModMenuApi::createModsScreen; }

    /**
     * Override the mod config screen factory so that it points to the mod's built-in configuration graphical user
     * interface.
     *
     * @return A config screen factory that builds a new settings screen instance.
     */
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return parent -> new SettingsScreen(Minecraft.getInstance().screen, false);
    }
}
