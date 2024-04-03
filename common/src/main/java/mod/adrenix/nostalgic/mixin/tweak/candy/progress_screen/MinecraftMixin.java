package mod.adrenix.nostalgic.mixin.tweak.candy.progress_screen;

import mod.adrenix.nostalgic.client.gui.screen.vanilla.progress.NostalgicProgressScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    /* Shadows */

    @Shadow @Nullable public ClientLevel level;
    @Shadow @Nullable public Screen screen;

    /* Injections */

    /**
     * Sets the progress screen's dimension trackers with the correct level context.
     */
    @Inject(
        method = "setLevel",
        at = @At("HEAD")
    )
    private void nt_progress_screen$onSetLevel(ClientLevel level, CallbackInfo callback)
    {
        if (this.level != null)
            NostalgicProgressScreen.PREVIOUS_DIMENSION.set(this.level.dimension());

        NostalgicProgressScreen.CURRENT_DIMENSION.set(level.dimension());
    }

    /**
     * Clears the progress screen's dimension trackers when the player disconnects from a world.
     */
    @Inject(
        method = "disconnect()V",
        at = @At("TAIL")
    )
    private void nt_progress_screen$onClearLevel(CallbackInfo callback)
    {
        NostalgicProgressScreen.PREVIOUS_DIMENSION.clear();
        NostalgicProgressScreen.CURRENT_DIMENSION.clear();
    }
}
