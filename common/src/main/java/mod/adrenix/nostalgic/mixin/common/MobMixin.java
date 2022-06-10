package mod.adrenix.nostalgic.mixin.common;

import mod.adrenix.nostalgic.client.config.MixinConfig;
import mod.adrenix.nostalgic.mixin.duck.IGhastAttack;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity implements IGhastAttack
{
    /* Dummy Constructor */

    private MobMixin(EntityType<? extends LivingEntity> entity, Level leve)
    {
        super(entity, leve);
    }

    /* Unique Fields */

    @Unique private int NT$attackCounter = 0;

    /* Overrides */

    @Override
    public int getAttackCounter()
    {
        return this.NT$attackCounter;
    }

    /**
     * Brings back the old ghast charging animation that was removed when singleplayer was turned into an internal server.
     * Since the old charging code was removed in 1.8, a unique property will need to be included to track ghast charging.
     *
     * Controlled by the old ghast charging tweak.
     */
    @Inject(method = "tick", at = @At("TAIL"))
    protected void NT$onTick(CallbackInfo callback)
    {
        if (MixinConfig.Animation.oldGhastCharging() && this.getType() == EntityType.GHAST)
        {
            Ghast ghast = (Ghast) this.getType().tryCast(this);
            if (ghast != null && ghast.isCharging() && ghast.isAlive())
                this.NT$attackCounter++;
            else if (ghast != null)
                this.NT$attackCounter = 0;
        }
    }
}
