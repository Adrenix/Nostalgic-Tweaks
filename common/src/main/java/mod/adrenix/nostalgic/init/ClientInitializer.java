package mod.adrenix.nostalgic.init;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.ClientKeyMapping;
import mod.adrenix.nostalgic.client.ClientSound;
import mod.adrenix.nostalgic.client.gui.MouseManager;
import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.screen.EnhancedScreen;
import mod.adrenix.nostalgic.client.gui.screen.home.HomeSplash;
import mod.adrenix.nostalgic.client.gui.screen.home.Panorama;
import mod.adrenix.nostalgic.client.gui.toast.ModToast;
import mod.adrenix.nostalgic.client.gui.tooltip.Tooltip;
import mod.adrenix.nostalgic.util.client.ClientTimer;
import mod.adrenix.nostalgic.util.client.animate.Animator;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
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

        // Registers the mod's custom splashes
        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, HomeSplash.getInstance());

        ClientGuiEvent.RENDER_PRE.register(ClientInitializer::setMousePosition);
        ClientGuiEvent.RENDER_POST.register(ClientInitializer::renderModGraphics);
        ClientTickEvent.CLIENT_PRE.register(ClientInitializer::onTick);
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(ClientInitializer::setModConnection);
    }

    /**
     * Sets the mouse position in {@link MouseManager}.
     *
     * @param screen      The current {@link Screen}.
     * @param graphics    The {@link GuiGraphics} instance.
     * @param mouseX      The x-coordinate of the mouse.
     * @param mouseY      The y-coordinate of the mouse.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     * @return The {@link EventResult}.
     */
    private static EventResult setMousePosition(Screen screen, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if (screen instanceof MouseManager manager)
            manager.setMousePosition(mouseX, mouseY);

        return EventResult.pass();
    }

    /**
     * Renders any extra graphics provided by the mod onto the screen.
     *
     * @param screen      The current {@link Screen}.
     * @param graphics    The {@link GuiGraphics} instance.
     * @param mouseX      The x-coordinate of the mouse.
     * @param mouseY      The y-coordinate of the mouse.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    private static void renderModGraphics(Screen screen, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        Tooltip.render(screen, graphics);
        ModToast.update(screen);

        if (screen instanceof EnhancedScreen<?, ?> || screen instanceof Overlay)
            GuiUtil.renderDebug(graphics);
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
