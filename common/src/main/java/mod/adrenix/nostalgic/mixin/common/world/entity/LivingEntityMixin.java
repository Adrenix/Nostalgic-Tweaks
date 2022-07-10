package mod.adrenix.nostalgic.mixin.common.world.entity;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * All mixins within this class are injected into both client and server.
 * Do not class load any vanilla client code here.
 * @see mod.adrenix.nostalgic.mixin.client.world.entity.LivingEntityMixin
 * @see mod.adrenix.nostalgic.mixin.common.world.entity.LivingEntityMixin
 */

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity
{
    /* Dummy Constructor */

    private LivingEntityMixin(EntityType<?> entity, Level level)
    {
        super(entity, level);
    }

    /* Injections */

    /**
     * Prevents living entities from playing the food consumption sound.
     * Controlled by the old hunger tweak.
     */
    @Redirect(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private void NT$onEat(Level instance, Player player, double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch)
    {
        if (ModConfig.Gameplay.instantEat())
            return;
        instance.playSound(player, x, y, z, sound, category, volume, pitch);
    }

    /**
     * Prevents the spawning of food consumption particles and eating sounds.
     * Controlled by the old hunger tweak.
     */
    @Inject(method = "triggerItemUseEffects", at = @At("HEAD"), cancellable = true)
    private void NT$onAddEatEffect(ItemStack itemStack, int count, CallbackInfo callback)
    {
        if (ModConfig.Gameplay.instantEat() && itemStack.getUseAnimation() == UseAnim.EAT)
            callback.cancel();
    }

    /**
     * Brings back the old backwards walking animation.
     * Controlled by the old backwards walk tweak.
     */
    @ModifyConstant
    (
        method = "tick",
        slice = @Slice(to = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;attackAnim:F")),
        constant = @Constant(floatValue = 180.0F)
    )
    private float NT$onBackwardsRotation(float vanilla)
    {
        return ModConfig.Animation.oldBackwardsWalking() ? 0.0F : vanilla;
    }

    /**
     * Immediately refills the player's air supply when they go above water.
     * Controlled by the instant air tweak.
     */
    @Inject(method = "increaseAirSupply", at = @At("HEAD"), cancellable = true)
    private void NT$onIncreaseAirSupply(int currentAir, CallbackInfoReturnable<Integer> callback)
    {
        if (ModConfig.Gameplay.instantAir())
            callback.setReturnValue(this.getMaxAirSupply());
    }

    /**
     * Makes zombie flesh not as overpowered by swapping the hunger effect with poison.
     * Controlled by the old hunger tweak.
     */
    @Inject
    (
        cancellable = true,
        method = "addEatEffect",
        at = @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/world/food/FoodProperties;getEffects()Ljava/util/List;"
        )
    )
    private void NT$onAddEatEffect(ItemStack itemStack, Level level, LivingEntity entity, CallbackInfo callback)
    {
        if (!ModConfig.Gameplay.disableHunger())
            return;

        Item item = itemStack.getItem();
        boolean isRotten = item.equals(Items.ROTTEN_FLESH);
        boolean isGolden = item.equals(Items.GOLDEN_APPLE);
        boolean isEffectOverride = isRotten || isGolden;

        if (isRotten)
            entity.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));

        if (isEffectOverride)
            callback.cancel();
    }
}
