package mod.adrenix.nostalgic.mixin.common.world.entity;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.mixin.duck.IGhastAttack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobMixin extends LivingEntity implements IGhastAttack
{
    /* Dummy Constructor */

    private MobMixin(EntityType<? extends LivingEntity> entity, World leve)
    {
        super(entity, leve);
    }

    /* Unique Fields */

    @Unique private int NT$attackCounter = 0;

    /* Overrides */

    @Override
    public int NT$getAttackCounter()
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
        if (ModConfig.Animation.oldGhastCharging() && this.getType() == EntityType.GHAST)
        {
            GhastEntity ghast = (GhastEntity) this.getType().downcast(this);
            if (ghast != null && ghast.isShooting() && ghast.isAlive())
                this.NT$attackCounter++;
            else if (ghast != null)
                this.NT$attackCounter = 0;
        }
    }
}
