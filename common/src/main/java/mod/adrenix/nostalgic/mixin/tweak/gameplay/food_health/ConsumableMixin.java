package mod.adrenix.nostalgic.mixin.tweak.gameplay.food_health;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Consumable.class)
public abstract class ConsumableMixin
{
    /* Shadows */

    @Shadow
    public abstract ItemStack onConsume(Level level, LivingEntity entity, ItemStack itemStack);

    /* Injections */

    /**
     * Prevents the player from starting the use of an item that is edible.
     */
    @WrapWithCondition(
        method = "startConsuming",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;startUsingItem(Lnet/minecraft/world/InteractionHand;)V"
        )
    )
    private boolean nt_food_health$shouldStartUsingItem(LivingEntity entity, InteractionHand hand, @Local(argsOnly = true) ItemStack itemStack)
    {
        if (GameplayTweak.INSTANT_EAT.get())
        {
            this.onConsume(entity.level(), entity, itemStack);

            return false;
        }

        return true;
    }

    /**
     * Prevents the generic food consumption sound when instantaneous eating is enabled.
     */
    @WrapWithCondition(
        method = "emitParticlesAndSounds",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"
        )
    )
    private boolean nt_food_health$shouldPlayConsumedSound(LivingEntity entity, SoundEvent soundEvent, float volume, float pitch)
    {
        return !GameplayTweak.INSTANT_EAT.get();
    }

    /**
     * Prevents consumption particles from spawning when instantaneous eating is enabled.
     */
    @WrapWithCondition(
        method = "emitParticlesAndSounds",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;spawnItemParticles(Lnet/minecraft/world/item/ItemStack;I)V"
        )
    )
    private boolean nt_food_health$shouldSpawnConsumedParticles(LivingEntity entity, ItemStack itemStack, int amount)
    {
        return !GameplayTweak.INSTANT_EAT.get();
    }
}
