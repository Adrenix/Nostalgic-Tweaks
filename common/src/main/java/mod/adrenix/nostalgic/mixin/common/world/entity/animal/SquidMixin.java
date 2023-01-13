package mod.adrenix.nostalgic.mixin.common.world.entity.animal;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.entity.animal.Squid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Squid.class)
public abstract class SquidMixin
{
    /* Shadows */

    @Shadow protected abstract void spawnInk();

    /* Injections */

    /**
     * Prevents a squid from spawning ink.
     * Controlled by the disable animal panic tweak.
     */
    @Redirect(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Squid;spawnInk()V"))
    private void NT$onHurt(Squid squid)
    {
        if (!ModConfig.Gameplay.disableAnimalPanic())
            this.spawnInk();
    }
}