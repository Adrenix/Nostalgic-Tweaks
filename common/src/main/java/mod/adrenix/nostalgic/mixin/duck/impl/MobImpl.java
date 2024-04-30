package mod.adrenix.nostalgic.mixin.duck.impl;

import mod.adrenix.nostalgic.mixin.duck.GhastCounter;
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
public abstract class MobImpl extends LivingEntity implements GhastCounter
{
    /* Fake Constructor */

    private MobImpl(EntityType<? extends LivingEntity> entityType, Level level)
    {
        super(entityType, level);
    }

    /* Ghast Counter */

    @Unique private int nt$attackCounter = 0;

    @Override
    public int nt$getAttackCounter()
    {
        return this.nt$attackCounter;
    }

    @Inject(
        method = "tick",
        at = @At("TAIL")
    )
    private void nt_ghast_counter$onTick(CallbackInfo callback)
    {
        if (this.getType() != EntityType.GHAST)
            return;

        Ghast ghast = (Ghast) this.getType().tryCast(this);

        if (ghast != null && ghast.isCharging() && ghast.isAlive())
            this.nt$attackCounter++;
        else if (ghast != null)
            this.nt$attackCounter = 0;
    }
}
