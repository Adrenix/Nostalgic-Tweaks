package mod.adrenix.nostalgic.mixin.common.world.entity.ai;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimalPanic.class)
public abstract class AnimalPanicMixin
{
    /**
     * Prevents allays, frogs, and goats from panicking.
     * Controlled by the disable animal panic tweak.
     */
    @Inject
    (
        cancellable = true,
        at = @At("HEAD"),
        method = "start(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;J)V"
    )
    private void NT$onStart(ServerLevel level, LivingEntity entity, long gameTime, CallbackInfo callback)
    {
        if (ModConfig.Gameplay.disableAnimalPanic())
            callback.cancel();
    }
}
