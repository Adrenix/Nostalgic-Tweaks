package mod.adrenix.nostalgic.fabric.config.gui;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.gui.screen.SettingsScreen;
import mod.adrenix.nostalgic.util.client.GuiUtil;
import net.minecraft.client.Minecraft;

public class ModMenuIntegration implements ModMenuApi
{
    /* Mod Tracking */
    static
    {
        NostalgicTweaks.isModMenuInstalled = true;
        GuiUtil.modScreen = ModMenuApi::createModsScreen;
    }

    /* Menu Implementation */
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return parent -> new SettingsScreen(Minecraft.getInstance().screen, false);
    }
}
