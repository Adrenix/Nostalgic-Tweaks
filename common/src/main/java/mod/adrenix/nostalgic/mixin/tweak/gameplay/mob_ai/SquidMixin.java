package mod.adrenix.nostalgic.mixin.tweak.gameplay.mob_ai;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.entity.animal.Squid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Squid.class)
public abstract class SquidMixin
{
    /**
     * Prevents a squid from spawning ink.
     */
    @WrapWithCondition(
        method = "hurtServer",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/animal/Squid;spawnInk()V"
        )
    )
    private boolean nt_mob_ai$shouldSquidSpawnInk(Squid squid)
    {
        return !GameplayTweak.DISABLE_ANIMAL_PANIC.get();
    }
}
