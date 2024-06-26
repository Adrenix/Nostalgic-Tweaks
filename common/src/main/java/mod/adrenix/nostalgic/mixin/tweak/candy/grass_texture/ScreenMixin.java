package mod.adrenix.nostalgic.mixin.tweak.candy.grass_texture;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin
{
    /* Unique */

    @Unique private GraphicsStatus nt$oldGraphicsMode = GraphicsStatus.FANCY;

    /* Injections */

    /**
     * Tracks the current graphics mode.
     */
    @SuppressWarnings("ConstantValue") // Some mods load a screen so early that the game doesn't have options yet
    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    private void nt_grass_texture$onConstructScreen(CallbackInfo callback)
    {
        if (Minecraft.getInstance().options == null)
            return;

        this.nt$oldGraphicsMode = Minecraft.getInstance().options.graphicsMode().get();
    }

    /**
     * Reloads the game's textures if the graphics mode changed and the old fast grass tweak is enabled.
     */
    @Inject(
        method = "removed",
        at = @At("HEAD")
    )
    private void nt_grass_texture$onRemovedScreen(CallbackInfo callback)
    {
        if (!CandyTweak.OLD_FAST_GRASS_TEXTURE.get())
            return;

        if (Minecraft.getInstance().options.graphicsMode().get() != this.nt$oldGraphicsMode)
            Minecraft.getInstance().delayTextureReload();
    }
}
