package mod.adrenix.nostalgic.mixin.tweak.gameplay.mechanics_swim;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity
{
    /* Fake Constructor */

    private LivingEntityMixin(EntityType<?> entityType, Level level)
    {
        super(entityType, level);
    }

    /* Injections */

    /**
     * Immediately refills the player's air supply when they go above water.
     */
    @ModifyReturnValue(
        method = "increaseAirSupply",
        at = @At("RETURN")
    )
    private int nt_mechanics_swim$modifyIncreaseAirSupply(int currentAir)
    {
        if (this.getType() == EntityType.PLAYER)
            return GameplayTweak.INSTANT_AIR.get() ? this.getMaxAirSupply() : currentAir;

        return currentAir;
    }
}
