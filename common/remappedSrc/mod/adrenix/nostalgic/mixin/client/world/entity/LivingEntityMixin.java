package mod.adrenix.nostalgic.mixin.client.world.entity;

import mod.adrenix.nostalgic.client.config.SwingConfig;
import mod.adrenix.nostalgic.mixin.duck.ICameraPitch;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.common.SoundUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
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
public abstract class LivingEntityMixin extends Entity implements ICameraPitch
{
    /* Dummy Constructor */

    private LivingEntityMixin(EntityType<?> entityType, World level)
    {
        super(entityType, level);
    }

    /* Camera Pitching Ducking */

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
        AbstractClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (SwingConfig.isOverridingSpeeds() || player == null)
            return;
        else if (this.getType() == EntityType.PLAYER)
        {
            PlayerEntity entity = (PlayerEntity) this.getType().downcast(this);
            if (entity == null || !entity.isMainPlayer())
                return;
        }

        int mod = SwingConfig.getSwingSpeed(player);

        if (SwingConfig.isSpeedGlobal())
            callback.setReturnValue(SwingConfig.getSwingSpeed());
        else if (SwingConfig.isOverridingHaste() && player.hasStatusEffect(StatusEffects.HASTE))
            callback.setReturnValue(SwingConfig.getHasteSpeed());
        else if (SwingConfig.isOverridingFatigue() && player.hasStatusEffect(StatusEffects.MINING_FATIGUE))
            callback.setReturnValue(SwingConfig.getFatigueSpeed());
        else if (StatusEffectUtil.hasHaste(player))
            callback.setReturnValue(mod - (1 + StatusEffectUtil.getHasteAmplifier(player)));
        else
        {
            callback.setReturnValue
            (
                player.hasStatusEffect(StatusEffects.MINING_FATIGUE) ?
                    mod + (1 + Objects.requireNonNull(player.getStatusEffect(StatusEffects.MINING_FATIGUE)).getAmplifier()) * 2 :
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
        if (ModConfig.Animation.oldToolExplosion())
            callback.cancel();
    }

    /**
     * Updates the previous camera pitching.
     */
    @Inject(method = "baseTick", at = @At(value = "FIELD", ordinal = 0, target = "Lnet/minecraft/world/entity/LivingEntity;hurtTime:I"))
    private void NT$onBaseTick(CallbackInfo callback)
    {
        this.NT$setPrevCameraPitch(this.NT$getCameraPitch());
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
            PlayerEntity entity = (PlayerEntity) this.getType().downcast(this);
            boolean isInvalidEntity = entity == null || !entity.isMainPlayer();
            boolean isOverride = entity != null && (entity.isCreative() || entity.isSpectator());

            if (isInvalidEntity || isOverride)
                return vanilla;

            if (entity.isSubmergedInWater() && ModConfig.Gameplay.disableSwim())
                return false;
            else if (!entity.isSubmergedInWater() && ModConfig.Gameplay.disableSprint())
                return false;
        }

        return vanilla;
    }
}