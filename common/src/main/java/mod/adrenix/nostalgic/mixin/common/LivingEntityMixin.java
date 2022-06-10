package mod.adrenix.nostalgic.mixin.common;

import mod.adrenix.nostalgic.mixin.duck.ICameraPitch;
import mod.adrenix.nostalgic.client.config.MixinConfig;
import mod.adrenix.nostalgic.util.SoundUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

/**
 * All mixins within this class are currently controlled by the client.
 */

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ICameraPitch
{
    /* Dummy Constructor */

    private LivingEntityMixin(EntityType<?> entityType, Level level)
    {
        super(entityType, level);
    }

    /* Camera Pitching Ducking */

    @Unique private float NT$cameraPitch = 0.0F;
    @Unique public float NT$prevCameraPitch = 0.0F;

    @Override public void setCameraPitch(float cameraPitch) { this.NT$cameraPitch = cameraPitch; }
    @Override public void setPrevCameraPitch(float prevCameraPitch) { this.NT$prevCameraPitch = prevCameraPitch; }

    @Override public float getCameraPitch() { return NT$cameraPitch; }
    @Override public float getPrevCameraPitch() { return NT$prevCameraPitch; }

    /* Mixin Injections */

    /**
     * Controls how fast the swinging animation is.
     * Modified by numerous swing speed parameters controlled within the config.
     */
    @Inject(method = "getCurrentSwingDuration", at = @At(value = "HEAD"), cancellable = true)
    private void NT$onGetCurrentSwingDuration(CallbackInfoReturnable<Integer> callback)
    {
        AbstractClientPlayer player = Minecraft.getInstance().player;

        if (MixinConfig.Swing.isOverridingSpeeds() || player == null)
            return;
        else if (this.getType() == EntityType.PLAYER)
        {
            Player entity = (Player) this.getType().tryCast(this);
            if (entity == null || !entity.isLocalPlayer())
                return;
        }

        int mod = MixinConfig.Swing.getSwingSpeed(player);

        if (MixinConfig.Swing.isSpeedGlobal())
            callback.setReturnValue(MixinConfig.Swing.getSwingSpeed());
        else if (MixinConfig.Swing.isOverridingHaste() && player.hasEffect(MobEffects.DIG_SPEED))
            callback.setReturnValue(MixinConfig.Swing.getHasteSpeed());
        else if (MixinConfig.Swing.isOverridingFatigue() && player.hasEffect(MobEffects.DIG_SLOWDOWN))
            callback.setReturnValue(MixinConfig.Swing.getFatigueSpeed());
        else if (MobEffectUtil.hasDigSpeed(player))
            callback.setReturnValue(mod - (1 + MobEffectUtil.getDigSpeedAmplification(player)));
        else
        {
            callback.setReturnValue(
                player.hasEffect(MobEffects.DIG_SLOWDOWN) ?
                    mod + (1 + Objects.requireNonNull(player.getEffect(MobEffects.DIG_SLOWDOWN)).getAmplifier()) * 2 :
                    mod
            );
        }
    }

    /**
     * Prevents the breaking animation and breaking sound when a tool runs out of durability.
     * Controlled by the tool disintegration tweak.
     */
    @Inject(method = "breakItem", at = @At(value = "HEAD"), cancellable = true)
    private void NT$onBreakItem(ItemStack itemStack, CallbackInfo callback)
    {
        if (MixinConfig.Animation.oldToolExplosion())
            callback.cancel();
    }

    /**
     * Updates the previous camera pitching.
     */
    @Inject(method = "baseTick", at = @At(value = "FIELD", ordinal = 0, target = "Lnet/minecraft/world/entity/LivingEntity;hurtTime:I"))
    private void NT$onBaseTick(CallbackInfo callback)
    {
        this.setPrevCameraPitch(this.getCameraPitch());
    }

    /**
     * Redirects the vanilla falling sounds to a blank sound.
     * Controlled by the old fall sounds tweak.
     */
    @Inject(method = "getFallDamageSound", at = @At(value = "HEAD"), cancellable = true)
    private void NT$onGetFallDamageSound(int height, CallbackInfoReturnable<SoundEvent> callback)
    {
        if (MixinConfig.Sound.oldFall())
            callback.setReturnValue(SoundUtil.Event.BLANK.get());
    }
}