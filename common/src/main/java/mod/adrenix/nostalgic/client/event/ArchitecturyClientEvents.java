package mod.adrenix.nostalgic.client.event;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientScreenInputEvent;
import dev.architectury.event.events.common.PlayerEvent;
import mod.adrenix.nostalgic.client.config.gui.toast.NostalgicToast;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

/**
 * This class contains a registration helper method that will be used by the client initializers in Fabric and Forge.
 * The events used in this class are provided by the Architectury mod.
 */

public abstract class ArchitecturyClientEvents
{
    /**
     * Registers Architectury events. This is used when there is not a Fabric related event to a Forge event. In this
     * instance, using the wrapper events provided by Architectury resolves this problem.
     */
    public static void register()
    {
        PlayerEvent.CHANGE_DIMENSION.register((player, oldDim, newDim) -> ClientEventHelper.onChangeDimension());
        ClientScreenInputEvent.KEY_PRESSED_POST.register(ArchitecturyClientEvents::onSettingsKeyPressed);
        ClientGuiEvent.RENDER_POST.register(NostalgicToast::onPostRender);
    }

    /**
     * Shortcut key that, when pressed, opens the mod's settings screen when at a title screen.
     */
    private static EventResult onSettingsKeyPressed(Minecraft minecraft, Screen screen, int keyCode, int scanCode, int modifiers)
    {
        ClientEventHelper.gotoSettingsOnMatchedKey(minecraft, screen, keyCode, scanCode);
        return EventResult.pass();
    }
}
