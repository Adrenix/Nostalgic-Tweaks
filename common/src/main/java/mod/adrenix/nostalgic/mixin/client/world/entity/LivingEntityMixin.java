package mod.adrenix.nostalgic.mixin.client.world.entity;

import mod.adrenix.nostalgic.client.config.SwingConfig;
import mod.adrenix.nostalgic.mixin.duck.CameraPitching;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.AnimationUtil;
import mod.adrenix.nostalgic.util.common.SoundUtil;
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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

/**
 * All mixins within this class are injected into the client.
 * Do not allow this mixin to be applied to a server since we are class loading client only code here.
 * @see mod.adrenix.nostalgic.mixin.common.world.entity.LivingEntityMixin
 * @see mod.adrenix.nostalgic.mixin.server.LivingEntityMixin
 */

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements CameraPitching
{
    /* Dummy Constructor */

    @Shadow public abstract void setSprinting(boolean sprinting);

    private LivingEntityMixin(EntityType<?> entityType, Level level) { super(entityType, level); }

    /* Camera Pitching Implementation */

    @Unique private float NT$cameraPitch = 0.0F;
    @Unique public float NT$prevCameraPitch = 0.0F;

    @Override public void NT$setCameraPitch(float cameraPitch) { this.NT$cameraPitch = cameraPitch; }
    @Override public void NT$setPrevCameraPitch(float prevCameraPitch) { this.NT$prevCameraPitch = prevCameraPitch; }

    @Override public float NT$getCameraPitch() { return NT$cameraPitch; }
    @Override public float NT$getPrevCameraPitch() { return NT$prevCameraPitch; }

    /* Injections */

    /**
     * Controls how fast the swinging animation is.
     * Modified by numerous swing speed parameters controlled within the config.
     */
    @Inject(method = "getCurrentSwingDuration", at = @At(value = "HEAD"), cancellable = true)
    private void NT$onGetCurrentSwingDuration(CallbackInfoReturnable<Integer> callback)
    {
        AbstractClientPlayer player = Minecraft.getInstance().player;

        if (SwingConfig.isOverridingSpeeds() || player == null)
            return;
        else if (this.getType() == EntityType.PLAYER)
        {
            Player entity = (Player) this.getType().tryCast(this);

            if (entity == null || !entity.isLocalPlayer())
                return;
        }

        if (ModConfig.Animation.oldClassicSwing())
        {
            switch (AnimationUtil.swingType)
            {
                case LEFT_CLICK -> callback.setReturnValue(7);
                case RIGHT_CLICK -> callback.setReturnValue(3);
            }

            return;
        }

        int speed = SwingConfig.getSwingSpeed(player);

        if (SwingConfig.isSpeedGlobal())
            callback.setReturnValue(SwingConfig.getSwingSpeed());
        else if (SwingConfig.isOverridingHaste() && player.hasEffect(MobEffects.DIG_SPEED))
            callback.setReturnValue(SwingConfig.getHasteSpeed());
        else if (SwingConfig.isOverridingFatigue() && player.hasEffect(MobEffects.DIG_SLOWDOWN))
            callback.setReturnValue(SwingConfig.getFatigueSpeed());
        else if (MobEffectUtil.hasDigSpeed(player))
            callback.setReturnValue(speed - (1 + MobEffectUtil.getDigSpeedAmplification(player)));
        else
        {
            callback.setReturnValue
            (
                player.hasEffect(MobEffects.DIG_SLOWDOWN) ?
                    speed + (1 + Objects.requireNonNull(player.getEffect(MobEffects.DIG_SLOWDOWN)).getAmplifier()) * 2 :
                    speed
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
        if (ModConfig.Animation.oldToolExplosion())
            callback.cancel();
    }

    /**
     * Updates the previous camera pitching.
     */
    @Inject(method = "baseTick", at = @At(value = "FIELD", ordinal = 0, target = "Lnet/minecraft/world/entity/LivingEntity;hurtTime:I"))
    private void NT$onBaseTickHurtTime(CallbackInfo callback)
    {
        this.NT$setPrevCameraPitch(this.NT$getCameraPitch());
    }

    /**
     * Prevents players from continuing sprinting out of water if disabled swimming is off but disabled sprinting is on.
     */
    @Inject(method = "baseTick", at = @At("HEAD"))
    private void NT$onBaseTickStart(CallbackInfo callback)
    {
        if (ModConfig.Gameplay.disableSprint() && !ModConfig.Gameplay.disableSwim() && this.getType() == EntityType.PLAYER)
        {
            Player entity = (Player) this.getType().tryCast(this);
            boolean isInvalidEntity = entity == null || !entity.isLocalPlayer();
            boolean isOverride = entity != null && (entity.isCreative() || entity.isSpectator());

            if (this.isSprinting() && !this.isUnderWater() && !isInvalidEntity && !isOverride)
                this.setSprinting(false);
        }
    }

    /**
     * Redirects the vanilla falling sounds to a blank sound.
     * Controlled by the old fall sounds tweak.
     */
    @Inject(method = "getFallDamageSound", at = @At(value = "HEAD"), cancellable = true)
    private void NT$onGetFallDamageSound(int height, CallbackInfoReturnable<SoundEvent> callback)
    {
        if (ModConfig.Sound.oldFall())
            callback.setReturnValue(SoundUtil.Event.BLANK.get());
    }

    /**
     * Client Mixed - Server Controlled
     *
     * Prevents the ability for the client player to sprint or the ability to 'sprint swim'.
     *
     * Although this tweak is being mixed into the client, a server running N.T will control when these gameplay
     * elements should be active.
     */
    @ModifyVariable(method = "setSprinting", at = @At("HEAD"), argsOnly = true)
    private boolean NT$onSetSprinting(boolean vanilla)
    {
        if (!ModConfig.Gameplay.disableSprint() && !ModConfig.Gameplay.disableSwim())
            return vanilla;
        else if (this.getType() == EntityType.PLAYER)
        {
            Player entity = (Player) this.getType().tryCast(this);
            boolean isInvalidEntity = entity == null || !entity.isLocalPlayer();
            boolean isOverride = entity != null && (entity.isCreative() || entity.isSpectator());

            if (isInvalidEntity || isOverride)
                return vanilla;

            if (entity.isUnderWater() && ModConfig.Gameplay.disableSwim())
                return false;
            else if (!entity.isUnderWater() && ModConfig.Gameplay.disableSprint())
                return false;
        }

        return vanilla;
    }
}