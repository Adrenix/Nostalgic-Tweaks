package mod.adrenix.nostalgic.client.event;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import mod.adrenix.nostalgic.client.ClientKeyMapping;
import mod.adrenix.nostalgic.client.gui.MouseManager;
import mod.adrenix.nostalgic.client.gui.screen.home.HomeSplash;
import mod.adrenix.nostalgic.client.gui.screen.home.Panorama;
import mod.adrenix.nostalgic.client.gui.toast.ModToast;
import mod.adrenix.nostalgic.client.gui.tooltip.Tooltip;
import mod.adrenix.nostalgic.util.client.ClientTimer;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.animate.AnimationImpl;
import net.minecraft.server.packs.PackType;

/**
 * This class contains a registration helper method that will be used by the client initializers in Fabric and Forge.
 * The Architectury mod provides the events used in this class.
 */
public abstract class ArchClientEvents
{
    /**
     * Registers Architectury events. This is used when there is not a Fabric-related event to a Forge event or if code
     * is common between the two mod loaders.
     */
    public static void register()
    {
        // Registers the mod's keyboard mappings
        ClientKeyMapping.register();

        // Registers the mod's custom splashes
        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, HomeSplash.getInstance());

        // Handles the mod's mouse position tracker before every gui render pass
        ClientGuiEvent.RENDER_PRE.register((screen, graphics, mouseX, mouseY, delta) -> {
            if (screen instanceof MouseManager manager)
                manager.setMousePosition(mouseX, mouseY);

            return EventResult.pass();
        });

        // Handles the mod's toasts, tooltips, and debug info every gui render pass
        ClientGuiEvent.RENDER_POST.register((screen, graphics, mouseX, mouseY, delta) -> {
            Tooltip.render(screen, graphics);
            ModToast.update(screen);
            GuiUtil.renderDebug(graphics);
        });

        // Handles mod instructions that need run every tick
        ClientTickEvent.CLIENT_PRE.register(minecraft -> {
            ClientTimer.getInstance().onTick();
            AnimationImpl.onTick();
            Panorama.onTick();
        });
    }
}
