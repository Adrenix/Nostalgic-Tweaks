package mod.adrenix.nostalgic.mixin.tweak.gameplay.mob_ai;

import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixin
{
    /**
     * Update speed attributes as needed based on tweak context.
     */
    @SuppressWarnings("unchecked")
    @Inject(
        method = "serverAiStep",
        at = @At("HEAD")
    )
    private void nt_mob_ai$onServerAiStep(CallbackInfo callback)
    {
        Mob mob = (Mob) (Object) this;
        AttributeInstance speed = mob.getAttribute(Attributes.MOVEMENT_SPEED);

        if (speed == null)
            return;

        double emptyValue = DefaultAttributes.getSupplier((EntityType<? extends LivingEntity>) mob.getType())
            .getBaseValue(Attributes.MOVEMENT_SPEED);

        speed.setBaseValue(GameplayTweak.OLD_MOB_MOVEMENT_SPEED.get().valueFrom(mob, emptyValue));
    }
}
