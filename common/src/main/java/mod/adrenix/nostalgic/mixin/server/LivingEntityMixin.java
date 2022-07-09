package mod.adrenix.nostalgic.mixin.server;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * All mixins within this class are injected into the server.
 * Do not class load any vanilla client code here.
 * @see mod.adrenix.nostalgic.mixin.client.world.entity.LivingEntityMixin
 * @see mod.adrenix.nostalgic.mixin.server.LivingEntityMixin
 */

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    /**
     * Prevents connected player entities from sprinting if the tweak is enabled.
     * Controlled by the old sprint tweak.
     */
    @ModifyVariable(method = "setSprinting", at = @At("HEAD"), argsOnly = true)
    private boolean NT$onSetServerSprinting(boolean vanilla)
    {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Player player && ModConfig.Gameplay.disableSprint())
        {
            boolean isOverride = player.isCreative() || player.isSpectator();
            if (isOverride)
                return vanilla;
            return false;
        }
        else
            return vanilla;
    }
}
