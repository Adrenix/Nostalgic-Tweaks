package mod.adrenix.nostalgic.mixin.client.world.entity;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin
{
    /**
     * Prevents the spawning of sprinting particles beneath the player.
     * Controlled by the disable sprinting particles tweak.
     */
    @Inject
    (
        cancellable = true,
        method = "spawnSprintParticle",
        at = @At
        (
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
        )
    )
    private void NT$onSpawnSprintParticle(CallbackInfo callback)
    {
        if (ModConfig.Candy.disableSprintingParticles())
            callback.cancel();
    }
}
