package mod.adrenix.nostalgic.mixin.common.world.entity;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.mixin.duck.IGhastAttack;
import net.minecraft.world.damagesource.DamageSource;
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

    private MobMixin(EntityType<? extends LivingEntity> entity, Level leve) { super(entity, leve); }

    /* Unique Fields */

    @Unique private int NT$attackCounter = 0;

    /* Overrides */

    @Override
    public int NT$getAttackCounter()
    {
        return this.NT$attackCounter;
    }

    /* Injections */

    /**
     * Brings back the old ghast charging animation that was removed when singleplayer was turned into an internal server.
     * Since the old charging code was removed in 1.8, a unique property will need to be included to track ghast charging.
     *
     * Controlled by the old ghast charging tweak.
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void NT$onTick(CallbackInfo callback)
    {
        if (ModConfig.Animation.oldGhastCharging() && this.getType() == EntityType.GHAST)
        {
            Ghast ghast = (Ghast) this.getType().tryCast(this);

            if (ghast != null && ghast.isCharging() && ghast.isAlive())
                this.NT$attackCounter++;
            else if (ghast != null)
                this.NT$attackCounter = 0;
        }
    }

    /**
     * Prevents bows being dropped by skeletons.
     * Controlled by old skeleton drops tweaks.
     */
    @Inject(method = "dropCustomDeathLoot", at = @At("HEAD"), cancellable = true)
    private void NT$onDropCustomDeathLoot(DamageSource damageSource, int looting, boolean hitByPlayer, CallbackInfo callback)
    {
        boolean isSkeleton = ModConfig.Gameplay.oldSkeletonDrops() && this.getType() == EntityType.SKELETON;
        boolean isStray = ModConfig.Gameplay.oldStrayDrops() && this.getType() == EntityType.STRAY;

        if (isSkeleton || isStray)
            callback.cancel();
    }
}
