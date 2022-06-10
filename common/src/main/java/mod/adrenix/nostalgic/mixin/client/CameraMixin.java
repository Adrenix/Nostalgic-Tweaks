package mod.adrenix.nostalgic.mixin.client;

import mod.adrenix.nostalgic.client.config.MixinConfig;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin
{
    /* Shadows */

    @Shadow private Entity entity;
    @Shadow private float eyeHeightOld;
    @Shadow private float eyeHeight;

    /**
     * Disables the smooth sneaking.
     * Controlled by the old sneaking tweak.
     */
    @Inject(method = "tick", at = @At(value = "HEAD"), cancellable = true)
    private void NT$onTick(CallbackInfo callback)
    {
        if (!MixinConfig.Animation.oldSneaking())
            return;

        if (this.entity != null)
        {
            this.eyeHeightOld = this.eyeHeight;
            this.eyeHeight = this.entity.getEyeHeight();
        }

        callback.cancel();
    }
}