package mod.adrenix.nostalgic.init;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.ClientKeyMapping;
import mod.adrenix.nostalgic.client.ClientSound;
import mod.adrenix.nostalgic.client.gui.screen.home.HomeSplash;
import mod.adrenix.nostalgic.client.gui.screen.home.Panorama;
import mod.adrenix.nostalgic.init.listener.client.GuiListener;
import mod.adrenix.nostalgic.util.client.ClientTimer;
import mod.adrenix.nostalgic.util.client.animate.Animator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.packs.PackType;

abstract class ClientInitializer
{
    /**
     * Registers client events.
     */
    static void register()
    {
        ClientKeyMapping.register();
        ClientSound.register();
        GuiListener.register();

        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, HomeSplash.getInstance());

        for (Panorama panorama : Panorama.values())
            ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, panorama);

        ClientTickEvent.CLIENT_PRE.register(ClientInitializer::onTick);
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(ClientInitializer::setModConnection);
    }

    /**
     * Removes the verification of the mod connection when the player leaves a level with the mod installed.
     *
     * @param player The {@link LocalPlayer} instance.
     */
    private static void setModConnection(LocalPlayer player)
    {
        NostalgicTweaks.setNetworkVerification(false);
        NostalgicTweaks.setConnection(null);
    }

    /**
     * Instructions to perform every tick.
     *
     * @param minecraft The {@link Minecraft} singleton instance.
     */
    private static void onTick(Minecraft minecraft)
    {
        ClientTimer.getInstance().onTick();
        Animator.onTick();
        Panorama.onTick();
    }
}
