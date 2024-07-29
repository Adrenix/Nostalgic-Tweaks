package mod.adrenix.nostalgic.forge.mixin.flywheel.candy.world_lighting;

import mod.adrenix.nostalgic.forge.mixin.util.FlywheelForgeHelper;
import mod.adrenix.nostalgic.util.ModTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin
{
    /* Shadows */

    @Shadow @Nullable private ClientLevel level;

    /* Injections */

    /**
     * Forces a light update on all light listeners when the round-robin chunk relighting is enabled and the level
     * relight flag is active.
     */
    @Inject(
        method = "compileChunks",
        at = @At("HEAD")
    )
    private void nt_forge_flywheel_world_lighting$onCompileChunks(CallbackInfo callback)
    {
        if (!ModTracker.SODIUM.isInstalled())
            FlywheelForgeHelper.sendLightUpdate(this.level);
    }
}
