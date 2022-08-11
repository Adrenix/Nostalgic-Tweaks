package mod.adrenix.nostalgic.mixin.common.server;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public abstract class ServerLevelMixin
{
    /**
     * Prevents the "Tried to add entity minecraft:experience_orb, but it was marked as removed already" console spam.
     * Controlled by the old experience tweak.
     */
    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    private void NT$onAddEntity(Entity entity, CallbackInfoReturnable<Boolean> callback)
    {
        if (ModConfig.Gameplay.disableOrbSpawn() && entity instanceof ExperienceOrbEntity)
            callback.setReturnValue(false);
    }
}
