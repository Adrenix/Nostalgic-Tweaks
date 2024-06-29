package mod.adrenix.nostalgic.mixin.tweak.gameplay.animal_spawn;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.entity.AgeableMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AgeableMob.class)
public abstract class AgeableMobMixin
{
    /**
     * Prevents baby animals from spawning naturally.
     */
    @ModifyExpressionValue(
        method = "finalizeSpawn",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/AgeableMob$AgeableMobGroupData;isShouldSpawnBaby()Z"
        )
    )
    private boolean nt_animal_spawn$modifyShouldSpawnBaby(boolean shouldSpawnBaby)
    {
        return !GameplayTweak.DISABLE_BABY_ANIMAL_SPAWNING.get() && shouldSpawnBaby;
    }
}
