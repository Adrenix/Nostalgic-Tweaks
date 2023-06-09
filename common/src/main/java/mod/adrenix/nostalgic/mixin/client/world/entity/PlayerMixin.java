package mod.adrenix.nostalgic.mixin.client.world.entity;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.mixin.duck.SlotTracker;
import mod.adrenix.nostalgic.util.common.SoundUtil;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
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
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements SlotTracker
{
    /* Dummy Constructor */

    private PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) { super(entityType, level); }

    /* Shadows */

    @Shadow private ItemStack lastItemInMainHand;

    /* Slot Tracker Implementation */

    @Unique public int NT$lastSlot = -1;
    @Unique public boolean NT$reequip = false;

    @Override public void NT$setLastSlot(int slot) { this.NT$lastSlot = slot; }
    @Override public void NT$setReequip(boolean state) { this.NT$reequip = state; }

    @Override public int NT$getLastSlot() { return NT$lastSlot; }
    @Override public boolean NT$getReequip() { return NT$reequip; }
    @Override public ItemStack NT$getLastItem() { return this.lastItemInMainHand; }

    /* Mixin Injections */

    /**
     * Brings back the old "oof" sounds while also disabling the other custom hurt sounds.
     * Controlled by the old hurt sound tweak.
     */
    @Inject(method = "getHurtSound", at = @At(value = "HEAD"), cancellable = true)
    private void NT$onGetHurtSound(DamageSource damageSource, CallbackInfoReturnable<SoundEvent> callback)
    {
        if (ModConfig.Sound.oldDamage())
            callback.setReturnValue(SoundUtil.Event.PLAYER_HURT.get());
    }

    /**
     * Updates the camera pitching when the player moves up and down.
     */
    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setSpeed(F)V"))
    private void NT$onAiStep(CallbackInfo callback)
    {
        double deltaY = this.getDeltaMovement().y;
        float rotation = (float) (Math.atan(-deltaY * 0.20000000298023224D) * 15.0D);
        float current = this.NT$getCameraPitch();

        // Fixes weird bug that occurs when standing on a slime block.
        boolean isGrounded = deltaY < -0.07 && deltaY > -0.08;

        if (isGrounded || this.onGround() || this.getHealth() <= 0.0F)
            rotation = 0.0F;

        this.NT$setCameraPitch(current + (rotation - current) * 0.8F);
    }

    /**
     * Disables the bobbing animation when the player provides input for movement but cannot move further.
     * Controlled by the bob on collision tweak.
     */
    @ModifyArg(method = "aiStep", index = 1, at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(FF)F"))
    private float NT$onAiBobbing(float min, float current)
    {
        if (!ModConfig.Animation.oldCollideBobbing())
            return current;
        else if (this.walkDist == this.walkDistO)
            return 0.0F;

        return current;
    }

    /**
     * Prevents the swinging animation when dropping an item from the hand or within an inventory screen.
     * Controlled by the swing drop tweak.
     */
    @Redirect
    (
        method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
        at = @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;swing(Lnet/minecraft/world/InteractionHand;)V"
        )
    )
    private void NT$itemDroppingProxy(Player player, InteractionHand hand)
    {
        if (ModConfig.Animation.oldSwingDropping())
            return;

        player.swing(InteractionHand.MAIN_HAND);
    }

    /**
     * Prevents the xp level up sound from playing.
     * Controlled by the disabled xp level up sound tweak.
     */
    @ModifyArg(method = "giveExperienceLevels", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private SoundEvent NT$gainLevelSoundProxy(SoundEvent vanilla)
    {
        return ModConfig.Sound.disableXpLevel() ? SoundUtil.Event.BLANK.get() : vanilla;
    }
}