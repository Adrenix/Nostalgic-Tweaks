package mod.adrenix.nostalgic.mixin.server;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * All mixins within this class are injected into the server.
 * Do not class load any vanilla client code here.
 * @see mod.adrenix.nostalgic.mixin.client.world.entity.LivingEntityMixin
 * @see mod.adrenix.nostalgic.mixin.common.world.entity.LivingEntityMixin
 */

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    /**
     * Prevents players from continuing sprinting out of water if disabled swimming is off but disabled sprinting is on.
     */
    @Inject(method = "baseTick", at = @At("HEAD"))
    private void NT$onBaseTick(CallbackInfo callback)
    {
       LivingEntity entity = (LivingEntity) (Object) this;

       if (entity instanceof Player player && ModConfig.Gameplay.disableSprint() && !ModConfig.Gameplay.disableSwim())
       {
           boolean isOverride = player.isCreative() || player.isSpectator();

           if (player.isSprinting() && !player.isUnderWater() && !isOverride)
               player.setSprinting(false);
       }
    }

    /**
     * Prevents connected player entities from sprinting or 'swim sprinting' if either tweak is enabled.
     * Controlled by the disabled sprint tweak and the disabled swim tweak.
     */
    @ModifyVariable(method = "setSprinting", at = @At("HEAD"), argsOnly = true)
    private boolean NT$onSetServerSprinting(boolean vanilla)
    {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity instanceof Player player && (ModConfig.Gameplay.disableSprint() || ModConfig.Gameplay.disableSwim()))
        {
            boolean isOverride = player.isCreative() || player.isSpectator();

            if (isOverride)
                return vanilla;

            if (player.isUnderWater() && ModConfig.Gameplay.disableSwim())
                return false;
            else if (!player.isUnderWater() && ModConfig.Gameplay.disableSprint())
                return false;
        }

        return vanilla;
    }
}
