package mod.adrenix.nostalgic.forge.mixin.tweak.candy.debug_screen;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Gui.class)
public interface ForgeGuiAccess
{
    @Accessor("debugScreen")
    DebugScreenOverlay nt$getDebugScreen();
}
