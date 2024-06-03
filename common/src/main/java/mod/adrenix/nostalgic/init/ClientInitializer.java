package mod.adrenix.nostalgic.init;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.AfterConfigSave;
import mod.adrenix.nostalgic.client.ClientKeyMapping;
import mod.adrenix.nostalgic.client.ClientSound;
import mod.adrenix.nostalgic.client.gui.screen.home.HomeSplash;
import mod.adrenix.nostalgic.client.gui.screen.home.Panorama;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.title.NostalgicLogoText;
import mod.adrenix.nostalgic.init.listener.client.GuiListener;
import mod.adrenix.nostalgic.init.listener.client.TooltipListener;
import mod.adrenix.nostalgic.mixin.util.candy.ChestMixinHelper;
import mod.adrenix.nostalgic.mixin.util.candy.MipmapMixinHelper;
import mod.adrenix.nostalgic.mixin.util.candy.hud.HudMixinHelper;
import mod.adrenix.nostalgic.mixin.util.candy.lighting.LightingMixinHelper;
import mod.adrenix.nostalgic.mixin.util.candy.lighting.LightmapMixinHelper;
import mod.adrenix.nostalgic.mixin.util.candy.world.fog.OverworldFogRenderer;
import mod.adrenix.nostalgic.mixin.util.candy.world.fog.VoidFogRenderer;
import mod.adrenix.nostalgic.mixin.util.candy.world.fog.WaterFogRenderer;
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
        TooltipListener.register();

        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, HomeSplash.getInstance());
        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, NostalgicLogoText.getInstance());

        for (Panorama panorama : Panorama.values())
            ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, panorama);

        ClientTickEvent.CLIENT_PRE.register(ClientInitializer::onTick);
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(ClientInitializer::onPlayerQuit);

        AfterConfigSave.addInstruction(ChestMixinHelper::runAfterSave);
        AfterConfigSave.addInstruction(LightingMixinHelper::runAfterSave);
        AfterConfigSave.addInstruction(MipmapMixinHelper::runAfterSave);
        AfterConfigSave.addInstruction(HudMixinHelper::runAfterSave);

        MipmapMixinHelper.init();
    }

    /**
     * Removes the verification of the mod connection when the player leaves a world with the mod installed and performs
     * other needed tasks when the player leaves the world.
     *
     * @param player The {@link LocalPlayer} instance.
     */
    private static void onPlayerQuit(LocalPlayer player)
    {
        NostalgicTweaks.setNetworkVerification(false);
        NostalgicTweaks.setConnection(null);

        LightingMixinHelper.resetLightingCache();
        LightmapMixinHelper.resetLightingCache();

        OverworldFogRenderer.reset();
        WaterFogRenderer.reset();
        VoidFogRenderer.reset();
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

        LightingMixinHelper.onTick();
    }
}
