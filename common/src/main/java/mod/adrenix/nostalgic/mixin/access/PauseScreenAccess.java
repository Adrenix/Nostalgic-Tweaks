package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.client.gui.screens.PauseScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PauseScreen.class)
public interface PauseScreenAccess
{
    @Invoker("onDisconnect")
    void nt$onDisconnect();
}
