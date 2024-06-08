package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccess
{
    @Accessor("run")
    float nt$getRun();

    @Accessor("oRun")
    float nt$getOldRun();

    @Accessor("animStep")
    float nt$getAnimStep();

    @Accessor("animStepO")
    float nt$getOldAnimStep();

    @Accessor("attackStrengthTicker")
    void nt$setAttackStrengthTicker(int attackStrengthTicker);
}
