package mod.adrenix.nostalgic.mixin.tweak.gameplay.mob_ai;

import mod.adrenix.nostalgic.helper.gameplay.MobAiHelper;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Creeper.class)
public abstract class CreeperMixin
{
    /**
     * Makes a creeper strafe around the player when the creeper begins to swell.
     */
    @Inject(
        method = "tick",
        at = @At("HEAD")
    )
    private void nt_mob_ai$onCreeperTick(CallbackInfo callback)
    {
        if (!GameplayTweak.OLD_CREEPER_STRAFE_ON_SWELL.get())
            return;

        ClassUtil.cast(this, Creeper.class).ifPresent(creeper -> {
            if (creeper.isAlive() && creeper.getSwellDir() > 0 && creeper.getTarget() != null)
                MobAiHelper.strafeAroundTarget(creeper, creeper.getTarget());
        });
    }
}
